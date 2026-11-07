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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.climate.ClimateCaseSeriesDto;
import de.symeda.sormas.api.climate.ClimateCaseWeekDto;
import de.symeda.sormas.api.climate.ClimateUsualRangeMissing;
import de.symeda.sormas.api.climate.ClimateVariable;
import de.symeda.sormas.api.climate.ClimateWeeklySeriesDto;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.ui.climate.ClimateCaptions;
import de.symeda.sormas.ui.dashboard.climate.ClimateDashboardLayout.Measure;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * The Climate dashboard's district table (decision 127): one row per district of the region, with one disease's figures
 * (chosen above the table) and the climate over the period. It counts what happened; it has no alarm colours. A row
 * click shows that district.
 */
public class ClimateDistrictTable extends VerticalLayout {

	private static final long serialVersionUID = -1823502948417615404L;

	static final String DASH = "—";

	/** One district's figures. */
	static final class Row {

		private final DistrictReferenceDto district;
		private final Double value;
		private final Long weeksAbove;
		private final long weeks;
		private final String noUsualRange;
		private final Integer recentAbove;
		private final int recentWeeks;
		private final Double rainfall;
		private final Double temperature;
		private final Double humidity;
		private final HumidityMeasure humidityMeasure;
		private final Long unusualWeeks;
		private final String noNormals;
		private final Double completeness;
		private final String completenessDetail;

		Row(DistrictReferenceDto district, ClimateWeeklySeriesDto climate, ClimateCaseSeriesDto cases, Measure measure, ClimateVariable shading) {

			this.district = district;
			if (cases != null) {
				long count = cases.getWeeks().stream().mapToLong(ClimateCaseWeekDto::getCount).sum();
				value = measure == Measure.INCIDENCE && cases.getPopulation() != null && cases.getPopulation() > 0
					? count * 100_000d / cases.getPopulation()
					: Double.valueOf(count);
				weeks = cases.getWeeks().size();
				boolean hasRange = cases.countWeeksWithUsualRange() > 0;
				weeksAbove = hasRange ? cases.countWeeksAboveUsualRange() : null;
				recentAbove = hasRange && cases.getRecentWeeks() > 0 ? cases.getRecentWeeksAbove() : null;
				recentWeeks = cases.getRecentWeeks();
				noUsualRange = hasRange ? null : reasons(cases);
			} else {
				value = null;
				weeks = 0;
				weeksAbove = null;
				recentAbove = null;
				recentWeeks = 0;
				noUsualRange = null;
			}

			if (climate != null) {
				rainfall = climate.getRainfallTotal();
				temperature = climate.getTemperatureMean();
				humidity = climate.getHumidityMean();
				humidityMeasure = climate.getRangeHumidityMeasure();
				boolean hasNormals = shading != null && climate.hasNormals(shading);
				unusualWeeks = hasNormals ? climate.countUnusualWeeks(shading) : null;
				noNormals = hasNormals ? null : ClimateDashboardChart.status(climate);
				Double lowest = null;
				List<String> shares = new ArrayList<>();
				for (ClimateVariable variable : ClimateVariable.values()) {
					Double share = climate.getCompleteness(variable);
					if (share != null && (lowest == null || share < lowest)) {
						lowest = share;
					}
					shares.add(variable + " " + percent(share));
				}
				completeness = lowest;
				completenessDetail = String.join(" · ", shares);
			} else {
				rainfall = null;
				temperature = null;
				humidity = null;
				humidityMeasure = null;
				unusualWeeks = null;
				noNormals = null;
				completeness = null;
				completenessDetail = null;
			}
		}

		DistrictReferenceDto getDistrict() {
			return district;
		}

		String getName() {
			return district.getCaption();
		}

		Double getValue() {
			return value;
		}

		Long getWeeksAbove() {
			return weeksAbove;
		}

		Integer getRecentAbove() {
			return recentAbove;
		}

		Long getUnusualWeeks() {
			return unusualWeeks;
		}

		Double getCompleteness() {
			return completeness;
		}

		String valueText(Measure measure) {
			if (value == null) {
				return DASH;
			}
			return measure == Measure.INCIDENCE ? ClimateCaptions.oneDecimal(value) : whole(value);
		}

		String weeksAboveText() {
			return weeksAbove == null ? DASH : String.format(I18nProperties.getString(Strings.climateDashboardWeeksOf), weeksAbove, weeks);
		}

		String recentText() {
			return recentAbove == null ? DASH : String.format(I18nProperties.getString(Strings.climateDashboardRecentAbove), recentAbove, recentWeeks);
		}

		String rainfallText() {
			return rainfall == null ? DASH : whole(rainfall) + " mm";
		}

		String temperatureText() {
			return temperature == null ? DASH : ClimateCaptions.oneDecimal(temperature) + " °C";
		}

		String humidityText() {
			if (humidity == null) {
				return DASH;
			}
			String text = whole(humidity) + " %";
			return humidityMeasure == HumidityMeasure.NOON_READING ? text + " " + I18nProperties.getString(Strings.climateDetailNoonReading) : text;
		}

		String unusualText() {
			return unusualWeeks == null ? DASH : String.valueOf(unusualWeeks);
		}

		String completenessText() {
			return percent(completeness);
		}

		/**
		 * @return why there is no usual range, for the hover of "—"
		 */
		String getNoUsualRange() {
			return noUsualRange;
		}

		/**
		 * @return the history status, for the hover of "—" in the unusual weeks column
		 */
		String getNoNormals() {
			return noNormals;
		}

		String getCompletenessDetail() {
			return completenessDetail;
		}

		private static String reasons(ClimateCaseSeriesDto cases) {
			Set<ClimateUsualRangeMissing> reasons = cases.getWeeks()
				.stream()
				.map(ClimateCaseWeekDto::getUsualRangeMissing)
				.filter(Objects::nonNull)
				.collect(Collectors.toCollection(LinkedHashSet::new));
			return reasons.stream().sorted().map(ClimateUsualRangeMissing::toString).collect(Collectors.joining("; "));
		}

		/**
		 * @return the value rounded, with the user's thousands separator ("1,420")
		 */
		private static String whole(double value) {
			return java.text.NumberFormat.getIntegerInstance(I18nProperties.getUserLanguage().getLocale()).format(Math.round(value));
		}

		private static String percent(Double share) {
			return share == null ? DASH : Math.round(share * 100) + "%";
		}
	}

	private final Label heading = new Label();
	private final ComboBox<Disease> diseaseSelect = new ComboBox<>();
	private final Grid<Row> grid = new Grid<>();
	private final Consumer<DistrictReferenceDto> onSelect;

	private Grid.Column<Row, String> valueColumn;
	private Grid.Column<Row, String> weeksAboveColumn;
	private Grid.Column<Row, String> recentColumn;
	private Grid.Column<Row, String> unusualColumn;

	private ClimateWeeklySeriesDto climate;
	private List<ClimateCaseSeriesDto> cases = new ArrayList<>();
	private Measure measure;
	private ClimateVariable shading;
	private boolean updating;

	/**
	 * @param onSelect
	 *            called with the district of a row that is clicked
	 */
	public ClimateDistrictTable(Consumer<DistrictReferenceDto> onSelect) {

		this.onSelect = onSelect;
		setMargin(false);
		setSpacing(false);
		setWidthFull();

		heading.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE);
		diseaseSelect.setCaption(I18nProperties.getCaption(Captions.climateDashboardTableDisease));
		diseaseSelect.setEmptySelectionAllowed(false);
		diseaseSelect.setTextInputAllowed(false);
		diseaseSelect.setWidth(240, Unit.PIXELS);
		diseaseSelect.addValueChangeListener(e -> {
			if (!updating) {
				fill();
			}
		});
		HorizontalLayout header = new HorizontalLayout(heading, diseaseSelect);
		header.setMargin(false);
		header.setComponentAlignment(heading, com.vaadin.ui.Alignment.BOTTOM_LEFT);

		grid.setWidthFull();
		grid.setHeightByRows(5);
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.addStyleName(CssStyles.CLIMATE_GRID);
		text(I18nProperties.getCaption(Captions.climateDashboardDistrictColumn), Row::getName, Comparator.comparing(Row::getName, String.CASE_INSENSITIVE_ORDER));
		valueColumn = text("", row -> row.valueText(measure), Comparator.comparing(Row::getValue, nullsFirst()));
		weeksAboveColumn = text(I18nProperties.getCaption(Captions.climateDashboardWeeksAbove), Row::weeksAboveText, Comparator.comparing(Row::getWeeksAbove, nullsFirst()));
		weeksAboveColumn.setDescriptionGenerator(Row::getNoUsualRange);
		recentColumn = text(I18nProperties.getCaption(Captions.climateDashboardLastWeeks), Row::recentText, Comparator.comparing(Row::getRecentAbove, nullsFirst()));
		recentColumn.setDescriptionGenerator(Row::getNoUsualRange);
		text(I18nProperties.getCaption(Captions.climateRainfallTotal), Row::rainfallText, Comparator.comparing(r -> r.rainfall, nullsFirst()));
		text(I18nProperties.getCaption(Captions.climateTemperatureMean), Row::temperatureText, Comparator.comparing(r -> r.temperature, nullsFirst()));
		text(I18nProperties.getCaption(Captions.climateHumidityMean), Row::humidityText, Comparator.comparing(r -> r.humidity, nullsFirst()));
		unusualColumn = text("", Row::unusualText, Comparator.comparing(Row::getUnusualWeeks, nullsFirst()));
		unusualColumn.setDescriptionGenerator(Row::getNoNormals);
		text(I18nProperties.getCaption(Captions.climateDashboardCompleteness), Row::completenessText, Comparator.comparing(Row::getCompleteness, nullsFirst()))
			.setDescriptionGenerator(Row::getCompletenessDetail);
		grid.addItemClickListener(e -> {
			if (e.getItem() != null) {
				onSelect.accept(e.getItem().getDistrict());
			}
		});

		addComponents(header, grid);
	}

	private Grid.Column<Row, String> text(String caption, com.vaadin.data.ValueProvider<Row, String> value, Comparator<Row> order) {
		Grid.Column<Row, String> column = grid.addColumn(value).setCaption(caption).setSortable(true);
		column.setComparator(order::compare);
		return column;
	}

	private static <T extends Comparable<? super T>> Comparator<T> nullsFirst() {
		return Comparator.nullsFirst(Comparator.<T> naturalOrder());
	}

	/**
	 * @param cases
	 *            the series of the ticked diseases; the dropdown offers these
	 */
	public void update(String regionName, ClimateWeeklySeriesDto climate, List<ClimateCaseSeriesDto> cases, Measure measure, ClimateVariable shading) {

		this.climate = climate;
		this.cases = cases;
		this.measure = measure;
		this.shading = shading;
		heading.setValue(String.format(I18nProperties.getString(Strings.climateDashboardDistricts), regionName));

		updating = true;
		try {
			Disease chosen = diseaseSelect.getValue();
			List<Disease> diseases = cases.stream().map(ClimateCaseSeriesDto::getDisease).collect(Collectors.toList());
			diseaseSelect.setItems(diseases);
			diseaseSelect.setValue(diseases.contains(chosen) ? chosen : diseases.isEmpty() ? null : diseases.get(0));
			diseaseSelect.setVisible(!diseases.isEmpty());
		} finally {
			updating = false;
		}
		fill();
	}

	private void fill() {

		Disease disease = diseaseSelect.getValue();
		ClimateCaseSeriesDto diseaseSeries = cases.stream().filter(s -> s.getDisease() == disease).findFirst().orElse(null);
		Function<DistrictReferenceDto, ClimateCaseSeriesDto> casesOf = district -> diseaseSeries == null
			? null
			: diseaseSeries.getDistrictSeries().stream().filter(s -> district.equals(s.getDistrict())).findFirst().orElse(null);

		List<Row> rows = climate.getDistrictSeries()
			.stream()
			.map(series -> new Row(series.getDistrict(), series, casesOf.apply(series.getDistrict()), measure, shading))
			.collect(Collectors.toList());

		boolean withDisease = diseaseSeries != null;
		valueColumn.setHidden(!withDisease);
		weeksAboveColumn.setHidden(!withDisease);
		recentColumn.setHidden(!withDisease);
		if (withDisease) {
			valueColumn.setCaption(
				String.format(
					I18nProperties.getString(measure == Measure.INCIDENCE ? Strings.climateDashboardColumnIncidence : Strings.climateDashboardColumnCount),
					disease.toString()));
		}
		unusualColumn.setHidden(shading == null);
		if (shading != null) {
			unusualColumn.setCaption(String.format(I18nProperties.getString(Strings.climateDashboardUnusualColumn), shading.toString().toLowerCase()));
		}
		grid.setItems(rows);
		grid.setHeightByRows(Math.max(1, Math.min(rows.size(), 15)));
	}

	ComboBox<Disease> getDiseaseSelect() {
		return diseaseSelect;
	}

	Grid<Row> getGrid() {
		return grid;
	}

	List<Row> getRows() {
		return new ArrayList<>(((com.vaadin.data.provider.ListDataProvider<Row>) grid.getDataProvider()).getItems());
	}

	Label getHeading() {
		return heading;
	}

	Consumer<DistrictReferenceDto> getOnSelect() {
		return onSelect;
	}
}
