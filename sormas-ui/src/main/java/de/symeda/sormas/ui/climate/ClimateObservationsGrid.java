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

import java.util.function.Consumer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateObservationCriteria;
import de.symeda.sormas.api.climate.ClimateObservationIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;

/**
 * The observations on the Climate data screen: one row per location and day, with the place, the values and their
 * source.
 */
public class ClimateObservationsGrid extends FilteredGrid<ClimateObservationIndexDto, ClimateObservationCriteria> {

	private static final long serialVersionUID = 6390148257081125740L;

	public ClimateObservationsGrid(ClimateObservationCriteria criteria, Consumer<ClimateObservationIndexDto> editHandler) {

		super(ClimateObservationIndexDto.class);
		setSizeFull();
		setLazyDataProvider(FacadeProvider.getClimateObservationFacade()::getIndexList, FacadeProvider.getClimateObservationFacade()::count);
		setCriteria(criteria);

		addColumn(ClimateCaptions::place).setId(ClimateObservationIndexDto.PLACE)
			.setSortable(false)
			.setDescriptionGenerator(
				o -> o.isShared()
					? String.format(I18nProperties.getString(Strings.climateSharedBy), String.join(", ", ClimateCaptions.placeNames(o)))
					: null);

		removeColumn(ClimateObservationIndexDto.OBSERVATION_DATE);
		addColumn(o -> DateFormatHelper.formatDate(o.getObservationDate())).setId(ClimateObservationIndexDto.OBSERVATION_DATE)
			.setSortProperty(ClimateObservationIndexDto.OBSERVATION_DATE);

		for (String value : new String[] {
			ClimateObservationIndexDto.TEMPERATURE_MIN,
			ClimateObservationIndexDto.TEMPERATURE_MEAN,
			ClimateObservationIndexDto.TEMPERATURE_MAX,
			ClimateObservationIndexDto.RAINFALL }) {
			removeColumn(value);
		}
		addColumn(o -> ClimateCaptions.number(o.getTemperatureMin())).setId(ClimateObservationIndexDto.TEMPERATURE_MIN)
			.setSortProperty(ClimateObservationIndexDto.TEMPERATURE_MIN);
		addColumn(o -> ClimateCaptions.number(o.getTemperatureMean())).setId(ClimateObservationIndexDto.TEMPERATURE_MEAN)
			.setSortProperty(ClimateObservationIndexDto.TEMPERATURE_MEAN);
		addColumn(o -> ClimateCaptions.number(o.getTemperatureMax())).setId(ClimateObservationIndexDto.TEMPERATURE_MAX)
			.setSortProperty(ClimateObservationIndexDto.TEMPERATURE_MAX);
		addColumn(o -> ClimateCaptions.number(o.getRainfall())).setId(ClimateObservationIndexDto.RAINFALL)
			.setSortProperty(ClimateObservationIndexDto.RAINFALL);

		removeColumn(ClimateObservationIndexDto.RELATIVE_HUMIDITY);
		addColumn(ClimateCaptions::humidity).setId(ClimateObservationIndexDto.RELATIVE_HUMIDITY)
			.setSortProperty(ClimateObservationIndexDto.RELATIVE_HUMIDITY);

		setColumns(
			ClimateObservationIndexDto.PLACE,
			ClimateObservationIndexDto.OBSERVATION_DATE,
			ClimateObservationIndexDto.TEMPERATURE_MIN,
			ClimateObservationIndexDto.TEMPERATURE_MEAN,
			ClimateObservationIndexDto.TEMPERATURE_MAX,
			ClimateObservationIndexDto.RAINFALL,
			ClimateObservationIndexDto.RELATIVE_HUMIDITY,
			ClimateObservationIndexDto.SOURCE,
			ClimateObservationIndexDto.SOURCE_DETAIL);

		for (Column<ClimateObservationIndexDto, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(ClimateObservationIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}

		if (editHandler != null) {
			addEditColumn(editHandler);
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}
}
