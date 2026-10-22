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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.backend.climate.ClimateProviderException.Reason;

@WireMockTest
class OpenWeatherProviderTest {

	private static final String PATH = "/data/4.0/onecall/timeline/1day";
	private static final String KEY = "test-key-0123456789";

	/**
	 * Shape of a live response for Bagaces, Costa Rica (UTC-6), 22 October 2026, cut to four records: 10 October 2025
	 * (rain), 11 October (dry: no rain field), 12 October and 13 October. {@code dt} is 00:00 UTC of each day.
	 */
	private static final String BAGACES = "{\"lat\":10.53,\"lon\":-85.25,\"timezone\":\"America/Costa_Rica\",\"timezone_offset\":-21600,\"data\":["
		+ "{\"dt\":1760054400,\"temp\":{\"day\":30.54,\"min\":23.13,\"max\":30.54,\"night\":24.39,\"eve\":23.95,\"morn\":23.13},\"humidity\":72,\"rain\":4.58},"
		+ "{\"dt\":1760140800,\"temp\":{\"day\":27.66,\"min\":22.56,\"max\":27.94,\"night\":23.91,\"eve\":24.21,\"morn\":22.56},\"humidity\":82},"
		+ "{\"dt\":1760227200,\"temp\":{\"day\":27.46,\"min\":23.36,\"max\":27.72,\"night\":23.45,\"eve\":24.89,\"morn\":23.44},\"humidity\":89,\"rain\":7.4,\"snow\":0.1},"
		+ "{\"dt\":1760313600,\"temp\":{\"day\":27.77,\"min\":22.99,\"max\":27.94,\"night\":23.49,\"eve\":25.71,\"morn\":23.23},\"humidity\":74,\"rain\":9.66,\"pop\":1}],"
		+ "\"prev\":\"http://api.openweathermap.org/data/4.0/onecall/timeline/1day?appid=" + KEY + "\"}";

	private static final LocalDate FIRST_DAY = LocalDate.of(2025, 10, 10);

	private final List<Long> sleeps = new ArrayList<>();
	private OpenWeatherProvider provider;
	private String url;

	@BeforeEach
	void setUp(WireMockRuntimeInfo wireMock) {
		url = wireMock.getHttpBaseUrl() + PATH;
		// 13 October 2025, 03:00 UTC: still 12 October at the location, so 12 and 13 October are forecasts there
		provider = providerAt(Instant.parse("2025-10-13T03:00:00Z"));
	}

	@AfterEach
	void tearDown() {
		provider.close();
	}

	@Test
	void testRecordsAreDatedByTheUtcDateOfDtAndForecastsAreDropped() throws ClimateProviderException {

		stubFor(get(urlPathEqualTo(PATH)).willReturn(aResponse().withStatus(200).withBody(BAGACES)));

		ClimateFetchResult result = provider.fetchWindow(10.53, -85.25, FIRST_DAY);

		assertEquals(1, result.getCalls());
		List<ClimateDay> days = result.getDays();
		assertEquals(2, days.size(), "12 and 13 October have not ended at the location yet");

		ClimateDay first = days.get(0);
		assertEquals(FIRST_DAY, first.getDate(), "a UTC-6 location's record is not shifted to the day before");
		assertEquals(23.13, first.getTemperatureMin());
		assertEquals(30.54, first.getTemperatureMax());
		assertEquals(25.5, first.getTemperatureMean(), "mean of morning, day, evening and night, to 2 decimals");
		assertEquals(4.58, first.getRainfall());
		assertEquals(72.0, first.getRelativeHumidity());
		assertEquals(HumidityMeasure.NOON_READING, first.getHumidityMeasure());

		assertEquals(LocalDate.of(2025, 10, 11), days.get(1).getDate());
		assertEquals(0.0, days.get(1).getRainfall(), "a record without rain or snow was a dry day");

		verify(
			getRequestedFor(urlPathEqualTo(PATH)).withQueryParam("start", equalTo("1760054400"))
				.withQueryParam("units", equalTo("metric"))
				.withQueryParam("appid", equalTo(KEY))
				.withQueryParam("lat", equalTo("10.53"))
				.withQueryParam("lon", equalTo("-85.25")));
	}

	@Test
	void testRainAndSnowAreAddedUp() throws ClimateProviderException {

		stubFor(get(urlPathEqualTo(PATH)).willReturn(aResponse().withStatus(200).withBody(BAGACES)));
		provider.close();
		provider = providerAt(Instant.parse("2025-10-20T00:00:00Z"));

		List<ClimateDay> days = provider.fetchWindow(10.53, -85.25, FIRST_DAY).getDays();

		assertEquals(4, days.size());
		assertEquals(7.5, days.get(2).getRainfall());
	}

	@Test
	void testRainThatIsNotANumberIsUnknownNotDry() throws ClimateProviderException {

		String body = "{\"timezone_offset\":3600,\"data\":[{\"dt\":1760054400,\"temp\":{\"min\":20,\"max\":30},\"rain\":{\"1h\":2.5}}]}";
		stubFor(get(urlPathEqualTo(PATH)).willReturn(aResponse().withStatus(200).withBody(body)));

		ClimateDay day = provider.fetchWindow(6.6, 3.35, FIRST_DAY).getDays().get(0);

		assertNull(day.getRainfall());
		assertNull(day.getTemperatureMean(), "the mean needs all four readings");
		assertNull(day.getHumidityMeasure());
	}

	@Test
	void testTooFastIsRetriedOnceAfterAMinute() throws ClimateProviderException {

		stubFor(
			get(urlPathEqualTo(PATH)).inScenario("too fast")
				.whenScenarioStateIs(Scenario.STARTED)
				.willReturn(aResponse().withStatus(429))
				.willSetStateTo("slowed down"));
		stubFor(
			get(urlPathEqualTo(PATH)).inScenario("too fast")
				.whenScenarioStateIs("slowed down")
				.willReturn(aResponse().withStatus(200).withBody(BAGACES)));

		ClimateFetchResult result = provider.fetchWindow(10.53, -85.25, FIRST_DAY);

		assertEquals(2, result.getCalls());
		assertEquals(2, result.getDays().size());
		assertTrue(sleeps.contains(OpenWeatherProvider.TOO_FAST_WAIT_MILLIS));
	}

	@Test
	void testASecondRefusalMeansTheDailyLimitIsReached() {

		stubFor(get(urlPathEqualTo(PATH)).willReturn(aResponse().withStatus(429)));

		ClimateProviderException e = assertThrows(ClimateProviderException.class, () -> provider.fetchWindow(10.53, -85.25, FIRST_DAY));

		assertEquals(Reason.LIMIT_REACHED, e.getReason());
		assertEquals(2, e.getCalls());
	}

	@Test
	void testRefusedKeyStopsWithoutShowingTheKey() {

		stubFor(
			get(urlPathEqualTo(PATH)).willReturn(
				aResponse().withStatus(401).withBody("{\"cod\":401,\"message\":\"Invalid API key " + KEY + ". Please see the FAQ.\"}")));

		ClimateProviderException e = assertThrows(ClimateProviderException.class, () -> provider.fetchWindow(10.53, -85.25, FIRST_DAY));

		assertEquals(Reason.ACCESS_DENIED, e.getReason());
		assertEquals(1, e.getCalls());
		assertTrue(e.getMessage().contains("Invalid API key"));
		assertFalse(e.getMessage().contains(KEY));
	}

	@Test
	void testServerErrorsAreRetriedTwice() {

		stubFor(get(urlPathEqualTo(PATH)).willReturn(aResponse().withStatus(503)));

		ClimateProviderException e = assertThrows(ClimateProviderException.class, () -> provider.fetchWindow(10.53, -85.25, FIRST_DAY));

		assertEquals(Reason.UNAVAILABLE, e.getReason());
		assertEquals(3, e.getCalls());
	}

	@Test
	void testRefusedLocation() {

		stubFor(get(urlPathEqualTo(PATH)).willReturn(aResponse().withStatus(400).withBody("{\"cod\":\"400\",\"message\":\"wrong latitude\"}")));

		ClimateProviderException e = assertThrows(ClimateProviderException.class, () -> provider.fetchWindow(95, 3.35, FIRST_DAY));

		assertEquals(Reason.LOCATION_REFUSED, e.getReason());
		assertTrue(e.getMessage().contains("wrong latitude"));
	}

	@Test
	void testCallsArePaced() throws ClimateProviderException {

		stubFor(get(urlPathEqualTo(PATH)).willReturn(aResponse().withStatus(200).withBody(BAGACES)));

		provider.fetchWindow(10.53, -85.25, FIRST_DAY);
		assertTrue(sleeps.isEmpty(), "the first call does not wait");
		provider.fetchWindow(10.53, -85.25, FIRST_DAY);
		assertEquals(OpenWeatherProvider.MIN_CALL_INTERVAL_MILLIS, sleeps.get(0), "the clock stands still, so the second call waits a full second");
	}

	private OpenWeatherProvider providerAt(Instant now) {
		return new OpenWeatherProvider(KEY, url, Clock.fixed(now, ZoneOffset.UTC), sleeps::add);
	}
}
