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

package de.symeda.sormas.ui.dashboard.climate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.opencsv.CSVWriter;
import com.vaadin.server.StreamResource;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateCaseCountSource;
import de.symeda.sormas.api.climate.ClimateCaseWeekDto;
import de.symeda.sormas.api.climate.ClimateDashboardExportDto;
import de.symeda.sormas.api.climate.ClimateObservationExportDto;
import de.symeda.sormas.api.climate.ClimateRangeDto;
import de.symeda.sormas.api.climate.ClimateWeekDto;
import de.symeda.sormas.api.climate.ClimateWeekValueDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.ui.climate.ClimateCaptions;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;

/**
 * The Climate dashboard's CSV (decision 128): one row per region or district and epi week, the cases of each chosen
 * disease in a group of columns, then the week's climate; never shifted by the lag.
 */
final class ClimateDashboardExport {

	static final String I18N_PREFIX = "ClimateDashboardExport";

	private ClimateDashboardExport() {
	}

	static StreamResource resource(Supplier<List<ClimateDashboardExportDto>> rows, Supplier<List<Disease>> diseases) {
		return new StreamResource(() -> {
			try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
				try (CSVWriter writer = CSVUtils
					.createCSVWriter(new OutputStreamWriter(bytes, StandardCharsets.UTF_8.name()), FacadeProvider.getConfigFacade().getCsvSeparator())) {
					List<Disease> exported = diseases.get();
					writer.writeNext(header(exported).toArray(new String[0]));
					for (ClimateDashboardExportDto row : rows.get()) {
						writer.writeNext(line(row, exported).toArray(new String[0]));
					}
					writer.flush();
				}
				return new ByteArrayInputStream(bytes.toByteArray());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}, DownloadUtil.createFileNameWithCurrentDate(ExportEntityName.CLIMATE_DASHBOARD, ".csv"));
	}

	static List<String> header(List<Disease> diseases) {

		List<String> header = new ArrayList<>();
		for (String field : new String[] {
			"region",
			"district",
			"year",
			"epiWeek",
			"population" }) {
			header.add(caption(field));
		}
		for (Disease disease : diseases) {
			// a disease without case surveillance has its lab-confirmed aggregate reports in the first column (decision 147)
			String countFormat = FacadeProvider.getDiseaseConfigurationFacade().isCaseSurveillanceEnabled(disease)
				? Strings.climateExportCases
				: Strings.climateExportLabConfirmed;
			for (String format : new String[] {
				countFormat,
				Strings.climateExportReportDateCases,
				Strings.climateExportIncidence,
				Strings.climateExportUsualP10,
				Strings.climateExportUsualP90,
				Strings.climateExportAboveUsual }) {
				header.add(String.format(I18nProperties.getString(format), disease.toString()));
			}
		}
		for (String field : new String[] {
			"rainfall",
			"rainfallP10",
			"rainfallP90",
			"rainfallLastYear",
			"rainfallUnusual",
			"rainfallDays",
			"temperature",
			"temperatureMin",
			"temperatureMax",
			"temperatureP10",
			"temperatureP90",
			"temperatureLastYear",
			"temperatureUnusual",
			"temperatureDays",
			"humidity",
			"humidityMeasure",
			"humidityP10",
			"humidityP90",
			"humidityLastYear",
			"humidityUnusual",
			"humidityDays" }) {
			header.add(caption(field));
		}
		header.add(I18nProperties.getPrefixCaption(ClimateObservationExportDto.I18N_PREFIX, "credit"));
		return header;
	}

	static List<String> line(ClimateDashboardExportDto row, List<Disease> diseases) {

		List<String> line = new ArrayList<>();
		line.add(row.getRegion().getCaption());
		line.add(row.getDistrict() != null ? row.getDistrict().getCaption() : "");
		line.add(String.valueOf(row.getClimate().getWeek().getYear()));
		line.add(String.valueOf(row.getClimate().getWeek().getWeek()));
		line.add(row.getPopulation() != null ? String.valueOf(row.getPopulation()) : "");

		for (Disease disease : diseases) {
			ClimateCaseWeekDto week = row.getCases().get(disease);
			if (week == null) {
				for (int i = 0; i < 6; i++) {
					line.add("");
				}
				continue;
			}
			// a week without an aggregate report has no count, not 0; aggregate reports have no report-date fallback (decision 147)
			boolean cases = week.isReported() && row.getCountSource(disease) != ClimateCaseCountSource.AGGREGATE_LAB_CONFIRMED;
			line.add(week.isReported() ? String.valueOf(week.getCount()) : "");
			line.add(cases ? String.valueOf(week.getReportDateCount()) : "");
			line.add(number(week.getIncidence()));
			ClimateRangeDto range = week.getUsualRange();
			line.add(range != null ? number(range.getP10()) : "");
			line.add(range != null ? number(range.getP90()) : "");
			line.add(range != null && week.isReported() ? yesNo(week.isAboveUsualRange()) : "");
		}

		ClimateWeekDto climate = row.getClimate();
		addVariable(line, climate.getRainfall(), false);
		line.add(number(climate.getTemperature().getValue()));
		line.add(number(climate.getTemperatureMin()));
		line.add(number(climate.getTemperatureMax()));
		addVariable(line, climate.getTemperature(), true);
		line.add(number(climate.getHumidity().getValue()));
		line.add(climate.getHumidityMeasure() != null ? climate.getHumidityMeasure().toString() : "");
		addVariable(line, climate.getHumidity(), true);
		line.add(row.isOpenWeatherData() ? ClimateObservationExportDto.OPENWEATHER_CREDIT : "");
		return line;
	}

	/**
	 * Adds the value (unless already added), the normal range, last year's value, the unusual flag and the days with data
	 * (for a region: the districts with a complete week).
	 */
	private static void addVariable(List<String> line, ClimateWeekValueDto value, boolean valueAdded) {
		if (!valueAdded) {
			line.add(number(value.getValue()));
		}
		line.add(value.getNormal() != null ? number(value.getNormal().getP10()) : "");
		line.add(value.getNormal() != null ? number(value.getNormal().getP90()) : "");
		line.add(number(value.getLastYear()));
		line.add(value.getNormal() != null ? yesNo(value.isUnusual()) : "");
		Integer days = value.getDays() != null ? value.getDays() : value.getDistricts();
		line.add(days != null ? String.valueOf(days) : "");
	}

	private static String caption(String field) {
		return I18nProperties.getPrefixCaption(I18N_PREFIX, field);
	}

	private static String number(Double value) {
		return ClimateCaptions.number(value);
	}

	private static String yesNo(boolean value) {
		return I18nProperties.getString(value ? Strings.yes : Strings.no);
	}
}
