/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.climate;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.climate.ClimateCosts;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.backend.climate.ClimateProviderException.Reason;
import de.symeda.sormas.backend.util.ClientHelper;

/**
 * Daily data from OpenWeather One Call 4.0 ({@code timeline/1day}): up to 10 days per call, starting at 00:00 UTC of the
 * first day wanted.
 * <ul>
 * <li>A record's {@code dt} is 00:00 UTC of the day it describes, the location's own calendar day, so its UTC date is the
 * observation date. {@code timezone_offset} only tells where the location's today begins: records from today on are
 * forecasts and are dropped.</li>
 * <li>Rainfall is {@code rain} + {@code snow}. A record without either was a dry day and gives 0; a missing record stays a
 * gap.</li>
 * <li>Humidity is the 12:00 reading; the mean temperature is the mean of the morning, day, evening and night readings.</li>
 * </ul>
 * Calls are paced, at most one a second. A refusal for going too fast (HTTP 429) is retried once after a minute; a second
 * refusal means the account's daily limit is reached. OpenWeather echoes the API key in the {@code prev} and {@code next}
 * links of every response, so responses are never logged, and messages are cleaned of the key.
 */
public class OpenWeatherProvider implements ClimateDataProvider {

	public static final String DEFAULT_URL = "https://api.openweathermap.org/data/4.0/onecall/timeline/1day";

	static final long MIN_CALL_INTERVAL_MILLIS = 1000;
	static final long TOO_FAST_WAIT_MILLIS = 60_000;
	static final long[] UNAVAILABLE_WAITS_MILLIS = {
		5_000,
		15_000 };

	/**
	 * Waits between calls; replaced in tests.
	 */
	public interface Sleeper {

		void sleep(long millis) throws InterruptedException;
	}

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final String apiKey;
	private final String url;
	private final Clock clock;
	private final Sleeper sleeper;
	private final Client client;
	private long lastCallMillis = Long.MIN_VALUE;

	public OpenWeatherProvider(String apiKey) {
		this(apiKey, DEFAULT_URL, Clock.systemUTC(), Thread::sleep);
	}

	OpenWeatherProvider(String apiKey, String url, Clock clock, Sleeper sleeper) {
		this.apiKey = apiKey;
		this.url = url;
		this.clock = clock;
		this.sleeper = sleeper;
		this.client = ClientHelper.newBuilderWithProxy().connectTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
	}

	@Override
	public ClimateDataSource source() {
		return ClimateDataSource.OPENWEATHER;
	}

	@Override
	public int maxDaysPerCall() {
		return ClimateCosts.OPENWEATHER_DAYS_PER_CALL;
	}

	@Override
	public ClimateFetchResult fetchWindow(double latitude, double longitude, LocalDate firstDay) throws ClimateProviderException {

		long start = firstDay.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
		int calls = 0;
		int unavailableRetries = 0;
		boolean tooFastRetried = false;

		while (true) {
			pace(calls);
			calls++;

			Response response;
			try {
				response = client.target(url)
					.queryParam("lat", latitude)
					.queryParam("lon", longitude)
					.queryParam("start", start)
					.queryParam("units", "metric")
					.queryParam("appid", apiKey)
					.request(MediaType.APPLICATION_JSON)
					.get();
			} catch (ProcessingException e) {
				if (unavailableRetries < UNAVAILABLE_WAITS_MILLIS.length) {
					sleep(UNAVAILABLE_WAITS_MILLIS[unavailableRetries++], calls);
					continue;
				}
				throw new ClimateProviderException(Reason.UNAVAILABLE, "OpenWeather could not be reached: " + clean(e.getMessage()), calls);
			}

			int status;
			String body;
			try {
				status = response.getStatus();
				body = response.readEntity(String.class);
			} catch (ProcessingException e) {
				throw new ClimateProviderException(Reason.UNAVAILABLE, "OpenWeather's response could not be read: " + clean(e.getMessage()), calls);
			} finally {
				response.close();
			}

			if (status == 200) {
				return new ClimateFetchResult(parse(body, firstDay, calls), calls);
			}
			if (status == 429) {
				if (!tooFastRetried) {
					tooFastRetried = true;
					sleep(TOO_FAST_WAIT_MILLIS, calls);
					continue;
				}
				throw new ClimateProviderException(Reason.LIMIT_REACHED, "OpenWeather's daily limit was reached", calls);
			}
			if (status == 401 || status == 403) {
				throw new ClimateProviderException(
					Reason.ACCESS_DENIED,
					"OpenWeather refused the API key (HTTP " + status + "); check the key and the One Call subscription: " + message(body),
					calls);
			}
			if (status >= 500) {
				if (unavailableRetries < UNAVAILABLE_WAITS_MILLIS.length) {
					sleep(UNAVAILABLE_WAITS_MILLIS[unavailableRetries++], calls);
					continue;
				}
				throw new ClimateProviderException(Reason.UNAVAILABLE, "OpenWeather failed (HTTP " + status + "): " + message(body), calls);
			}
			throw new ClimateProviderException(
				Reason.LOCATION_REFUSED,
				"OpenWeather refused the request for " + latitude + ", " + longitude + " (HTTP " + status + "): " + message(body),
				calls);
		}
	}

	@Override
	public void close() {
		client.close();
	}

	List<ClimateDay> parse(String body, LocalDate firstDay, int calls) throws ClimateProviderException {

		JsonNode root;
		try {
			root = MAPPER.readTree(body);
		} catch (IOException e) {
			throw new ClimateProviderException(Reason.UNAVAILABLE, "OpenWeather's response is not valid JSON", calls);
		}

		ZoneOffset locationOffset = ZoneOffset.ofTotalSeconds(root.path("timezone_offset").asInt(0));
		LocalDate locationToday = clock.instant().atOffset(locationOffset).toLocalDate();

		List<ClimateDay> days = new ArrayList<>();
		for (JsonNode record : root.path("data")) {
			if (!record.path("dt").isNumber()) {
				continue;
			}
			LocalDate day = Instant.ofEpochSecond(record.get("dt").asLong()).atOffset(ZoneOffset.UTC).toLocalDate();
			if (day.isBefore(firstDay) || !day.isBefore(locationToday)) {
				continue;
			}

			JsonNode temp = record.path("temp");
			Double humidity = number(record, "humidity");
			days.add(
				new ClimateDay(
					day,
					number(temp, "min"),
					number(temp, "max"),
					mean(number(temp, "morn"), number(temp, "day"), number(temp, "eve"), number(temp, "night")),
					rainfall(record),
					humidity,
					humidity != null ? HumidityMeasure.NOON_READING : null));
		}
		return days;
	}

	/**
	 * Rain plus snow; 0 when the record has neither (a dry day). When either is present but not a plain number, the
	 * rainfall is unknown rather than dry.
	 */
	private static Double rainfall(JsonNode record) {

		JsonNode rain = record.path("rain");
		JsonNode snow = record.path("snow");
		if (rain.isMissingNode() && snow.isMissingNode()) {
			return 0.0;
		}
		if ((!rain.isMissingNode() && !rain.isNumber()) || (!snow.isMissingNode() && !snow.isNumber())) {
			return null;
		}
		return round(rain.asDouble(0) + snow.asDouble(0));
	}

	private static Double mean(Double... values) {

		double sum = 0;
		for (Double value : values) {
			if (value == null) {
				return null;
			}
			sum += value;
		}
		return round(sum / values.length);
	}

	private static Double number(JsonNode node, String field) {
		JsonNode value = node.path(field);
		return value.isNumber() ? value.asDouble() : null;
	}

	private static Double round(double value) {
		return Math.round(value * 100) / 100d;
	}

	private void pace(int calls) throws ClimateProviderException {
		if (lastCallMillis != Long.MIN_VALUE) {
			long wait = lastCallMillis + MIN_CALL_INTERVAL_MILLIS - clock.millis();
			if (wait > 0) {
				sleep(wait, calls);
			}
		}
		lastCallMillis = clock.millis();
	}

	private void sleep(long millis, int calls) throws ClimateProviderException {
		try {
			sleeper.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ClimateProviderException(Reason.UNAVAILABLE, "Interrupted while waiting for OpenWeather", calls);
		}
	}

	/**
	 * @return OpenWeather's own error message from a response body, without the API key
	 */
	private String message(String body) {
		try {
			JsonNode message = MAPPER.readTree(body).path("message");
			return message.isTextual() ? clean(message.asText()) : "";
		} catch (IOException | RuntimeException e) {
			return "";
		}
	}

	private String clean(String text) {
		if (text == null) {
			return "";
		}
		return apiKey == null || apiKey.isEmpty() ? text : text.replace(apiKey, "*****");
	}
}
