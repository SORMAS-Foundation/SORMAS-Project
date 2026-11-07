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

package de.symeda.sormas.ui.climate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.climate.ClimateDailySeriesDto;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateDayDto;
import de.symeda.sormas.api.climate.ClimateRangeDto;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.climate.SiteClimateReadingDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleIndexDto;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;

class EnvironmentClimateChartTest {

	private static final LocalDate TO = LocalDate.of(2026, 11, 3);
	private static final LocalDate FROM = TO.minusDays(89);

	@Test
	void testScriptHasGapsBandsSamplesEventsAndReadings() throws IOException {

		ClimateDailySeriesDto series = series(true);
		EnvironmentClimateChart chart = new EnvironmentClimateChart();
		chart.update(
			series,
			FROM,
			TO,
			at(TO.minusDays(60), 9),
			Arrays.asList(
				sample("W-1", TO.minusDays(20), PathogenTestResultType.POSITIVE),
				sample("W-2", TO.minusDays(20), PathogenTestResultType.NEGATIVE),
				sample("W-3", TO.minusDays(10), PathogenTestResultType.PENDING),
				sample("W-4", TO.minusDays(5), null),
				sample("W-old", FROM.minusDays(5), PathogenTestResultType.POSITIVE)),
			Collections.singletonList(event("Flood <after> 'storm'", TO.minusDays(15))),
			Collections.singletonList(reading(TO.minusDays(20))));

		String script = chart.getScript();
		long gap = ClimateChartScript.x(TO.minusDays(2));
		assertTrue(script.contains("[" + gap + ",null]"), "a missing day is a gap");
		assertTrue(script.contains("type:'arearange'"));
		assertTrue(script.contains("linkedTo:'normal'"));
		assertTrue(script.contains("id:'samples-positive'"));
		assertTrue(script.contains("symbol:'diamond'"));
		assertFalse(script.contains("W-old"), "samples outside the period are left out");
		assertTrue(script.contains("Flood &lt;after&gt; &#39;storm&#39;"), "event titles are escaped");
		assertTrue(script.contains("Report date"));
		assertTrue(script.contains("Point at a day to see its details."), "the hint in the details area");
		for (ClimateIcon icon : ClimateIcon.values()) {
			assertTrue(script.contains("climate-icon " + icon.getStyle()), "each panel name starts with its icon: " + icon);
		}
		assertEquals("Normal range: 2021–2025, from Bagaces district", EnvironmentClimateChart.status(series));

		// written for a render check in a browser with the bundled Highcharts files
		Path out = Paths.get("target", "climate-chart-test.js");
		Files.createDirectories(out.getParent());
		Files.write(out, script.getBytes(StandardCharsets.UTF_8));
	}

	@Test
	void testRowsLeftOutWithoutRightsAndHistoryStatus() {

		ClimateDailySeriesDto series = series(false);
		EnvironmentClimateChart chart = new EnvironmentClimateChart();
		chart.update(series, FROM, TO, null, null, null, Collections.emptyList());

		String script = chart.getScript();
		assertFalse(script.contains("samples-"));
		assertFalse(script.contains("id:'events'"));
		assertFalse(script.contains("plotBands:"), "no lanes");
		assertTrue(EnvironmentClimateChart.status(series).startsWith("Building climate history for normal ranges: "), EnvironmentClimateChart.status(series));
	}

	@Test
	void testEmptyLanesSayNothingInThePeriod() throws IOException {

		EnvironmentClimateChart chart = new EnvironmentClimateChart();
		chart.update(
			series(true),
			FROM,
			TO,
			null,
			Collections.singletonList(sample("W-old", FROM.minusDays(5), PathogenTestResultType.POSITIVE)),
			Collections.emptyList(),
			Collections.emptyList());

		String script = chart.getScript();
		assertTrue(script.contains("text:'Samples'"), script);
		assertTrue(script.contains("No samples in this period"));
		assertTrue(script.contains("text:'Events'"));
		assertTrue(script.contains("No linked events in this period"));

		Path out = Paths.get("target", "climate-chart-empty-test.js");
		Files.createDirectories(out.getParent());
		Files.write(out, script.getBytes(StandardCharsets.UTF_8));
	}

	@Test
	void testRowsLaidOutInPixelsWithRoomForTheirNames() {

		List<EnvironmentClimateChart.Row> rows = Arrays.asList(
			EnvironmentClimateChart.Row.lane("Samples", null),
			EnvironmentClimateChart.Row.lane("Events", null),
			EnvironmentClimateChart.Row.panel("Rain (mm)", 32),
			EnvironmentClimateChart.Row.panel("Temp (°C)", 36),
			EnvironmentClimateChart.Row.panel("Humidity (%)", 32));
		EnvironmentClimateChart.layoutRows(rows);

		int previousBottom = EnvironmentClimateChart.PLOT_TOP;
		for (int i = 0; i < rows.size(); i++) {
			EnvironmentClimateChart.Row row = rows.get(i);
			int space = row.getTop() - previousBottom;
			int expected = i == 0 ? 0 : i < 2 ? EnvironmentClimateChart.LANE_GAP : EnvironmentClimateChart.TITLE_BAND;
			if (i == 2) {
				expected += EnvironmentClimateChart.LANE_GAP;
			}
			assertEquals(expected, space, "space above row " + i);
			previousBottom = row.getTop() + row.getHeight();
		}
		assertEquals(EnvironmentClimateChart.LANE_HEIGHT, rows.get(0).getHeight());
		assertEquals(
			EnvironmentClimateChart.CHART_HEIGHT - EnvironmentClimateChart.MARGIN_BOTTOM,
			previousBottom,
			"the last panel ends at the plot's bottom");
	}

	@Test
	void testDayDetails() {

		ClimateDayDto day = new ClimateDayDto(TO);
		day.setTemperatures(22.0, 26.04, 31.0);
		day.setRainfall(12.0);
		day.setHumidity(80.0, HumidityMeasure.NOON_READING);
		day.setSource(ClimateDataSource.OPENWEATHER);
		day.setRainfallNormal(new ClimateRangeDto(1.0, 4.0, 9.5));

		String details = EnvironmentClimateChart.details(day, null, null, null);
		assertTrue(details.contains("Rainfall 12.0 mm (normal 1.0–9.5 mm)"), details);
		assertTrue(details.contains("Temperature 26.0 °C (min 22.0, max 31.0)"), details);
		assertTrue(details.contains("noon reading"), details);
		assertTrue(details.contains("Source: OpenWeather"), details);
		assertTrue(details.contains(ClimateIcon.RAINFALL.html() + " Rainfall 12.0 mm"), details);
		assertTrue(details.contains(ClimateIcon.HUMIDITY.html() + " Humidity"), details);

		// a site reading's values after their icons (decision 116)
		SiteClimateReadingDto reading = SiteClimateReadingDto.build(null);
		reading.setReadingDateTime(Date.from(TO.atTime(LocalTime.of(10, 15)).atZone(ZoneId.systemDefault()).toInstant()));
		reading.setAirTemperature(7.2);
		reading.setRainfall(0.0);
		reading.setRainfallPeriodHours(2);
		reading.setRelativeHumidity(58.0);
		String withReading = EnvironmentClimateChart.details(day, Collections.singletonList(reading), null, null);
		assertTrue(withReading.contains(ClimateIcon.TEMPERATURE.html() + " 7.2 °C · " + ClimateIcon.RAINFALL.html() + " 0 mm in 2 h · "), withReading);
		assertTrue(withReading.contains(ClimateIcon.HUMIDITY.html() + " 58 %"), withReading);

		ClimateDayDto gap = new ClimateDayDto(TO);
		assertTrue(EnvironmentClimateChart.details(gap, null, null, null).contains("No data"));
	}

	@Test
	void testByDayKeepsThePeriodOnly() {
		Map<Long, List<SiteClimateReadingDto>> byDay = EnvironmentClimateChart
			.byDay(Arrays.asList(reading(TO), reading(TO), reading(FROM.minusDays(1))), SiteClimateReadingDto::getReadingDateTime, FROM, TO);
		assertEquals(1, byDay.size());
		assertEquals(2, byDay.get(ClimateChartScript.x(TO)).size());
	}

	private static ClimateDailySeriesDto series(boolean normals) {

		ClimateDailySeriesDto series = new ClimateDailySeriesDto(
			new EnvironmentReferenceDto("ENV-UUID", "Lake Arenal"),
			new DistrictReferenceDto("DIS-UUID", "Bagaces", null),
			false,
			normals,
			LocalDate.of(2025, 9, 1),
			3,
			2021,
			2025);
		for (LocalDate date = FROM; !date.isAfter(TO); date = date.plusDays(1)) {
			ClimateDayDto day = new ClimateDayDto(date);
			if (!date.equals(TO.minusDays(2))) {
				double wave = Math.sin(date.getDayOfYear() / 9.0);
				day.setTemperatures(22 + wave, 26 + wave, 31 + wave);
				day.setRainfall(Math.max(0, 8 * wave + 2));
				day.setHumidity(78 + 6 * wave, HumidityMeasure.NOON_READING);
				day.setSource(ClimateDataSource.OPENWEATHER);
			}
			if (normals) {
				day.setRainfallNormal(new ClimateRangeDto(0.5, 3, 9));
				day.setTemperatureNormal(new ClimateRangeDto(25, 26, 27));
				day.setHumidityNormal(new ClimateRangeDto(70, 78, 86));
			}
			series.getDays().add(day);
		}
		return series;
	}

	private static EnvironmentSampleIndexDto sample(String fieldSampleId, LocalDate day, PathogenTestResultType result) {
		EnvironmentSampleIndexDto sample = new EnvironmentSampleIndexDto(
			1L,
			fieldSampleId + "-UUID",
			fieldSampleId,
			at(day, 10),
			"Lake Arenal",
			null,
			null,
			null,
			null,
			"Bagaces",
			false,
			null,
			false,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			1L,
			true);
		sample.setLatestPathogenTestResult(result);
		if (result == PathogenTestResultType.POSITIVE) {
			Pathogen vibrio = new Pathogen();
			vibrio.setValue("VIBRIO_CHOLERAE");
			vibrio.setCaption("Vibrio cholerae");
			sample.setPositivePathogenTests(Collections.singletonList(vibrio));
		}
		return sample;
	}

	private static EventIndexDto event(String title, LocalDate start) {
		EventIndexDto event = new EventIndexDto("EVENT-UUID-0001");
		event.setEventTitle(title);
		event.setEventStatus(EventStatus.SIGNAL);
		event.setStartDate(at(start, 0));
		return event;
	}

	private static SiteClimateReadingDto reading(LocalDate day) {
		SiteClimateReadingDto reading = SiteClimateReadingDto.build(new EnvironmentReferenceDto("ENV-UUID", "Lake Arenal"));
		reading.setReadingDateTime(at(day, 14));
		reading.setAirTemperature(28.5);
		reading.setRainfall(6.0);
		reading.setRainfallPeriodHours(6);
		reading.setRelativeHumidity(72.0);
		return reading;
	}

	private static Date at(LocalDate day, int hour) {
		return Date.from(day.atTime(LocalTime.of(hour, 0)).atZone(ZoneId.systemDefault()).toInstant());
	}
}
