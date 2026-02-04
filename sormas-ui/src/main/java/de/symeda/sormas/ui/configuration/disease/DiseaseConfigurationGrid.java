/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration.disease;

import static de.symeda.sormas.ui.EnumSortUtils.comparingOnEnumField;

import java.util.List;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.disease.DiseaseConfigurationCriteria;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.disease.DiseaseConfigurationIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.FilteredGrid;

public class DiseaseConfigurationGrid extends FilteredGrid<DiseaseConfigurationIndexDto, DiseaseConfigurationCriteria> {

	private static final long serialVersionUID = -1581998195111914436L;

	public DiseaseConfigurationGrid(DiseaseConfigurationCriteria criteria) {

		super(DiseaseConfigurationIndexDto.class);
		setSizeFull();

		setInEagerMode(true);

		setCriteria(criteria);

		setEagerDataProvider();

		setColumns(
			DiseaseConfigurationIndexDto.DISEASE,
			DiseaseConfigurationIndexDto.ACTIVE,
			DiseaseConfigurationIndexDto.PRIMARY_DISEASE,
			DiseaseConfigurationIndexDto.FOLLOW_UP_ENABLED,
			DiseaseConfigurationIndexDto.FOLLOW_UP_DURATION,
			DiseaseConfigurationIndexDto.CASE_SURVEILLANCE_ENABLED,
			DiseaseConfigurationIndexDto.AGGREGATE_REPORTING_ENABLED,
			DiseaseConfigurationIndexDto.CASE_FOLLOW_UP_DURATION,
			DiseaseConfigurationIndexDto.AGE_GROUPS,
			DiseaseConfigurationIndexDto.EVENT_PARTICIPANT_FOLLOW_UP_DURATION,
			DiseaseConfigurationIndexDto.EXTENDED_CLASSIFICATION,
			DiseaseConfigurationIndexDto.EXTENDED_CLASSIFICATION_MULTI,
			DiseaseConfigurationIndexDto.AUTOMATIC_SAMPLE_ASSIGNMENT_THRESHOLD);

		getColumn(DiseaseConfigurationIndexDto.AGE_GROUPS).setSortable(false);

		addEditColumn(e -> ControllerProvider.getDiseaseConfirgurationController().editDiseaseConfiguration(e.getUuid()));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(DiseaseConfigurationDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}

		getColumn(DiseaseConfigurationIndexDto.ACTIVE).setRenderer(new BooleanRenderer());
		getColumn(DiseaseConfigurationIndexDto.PRIMARY_DISEASE).setRenderer(new BooleanRenderer());
		getColumn(DiseaseConfigurationIndexDto.FOLLOW_UP_ENABLED).setRenderer(new BooleanRenderer());
		getColumn(DiseaseConfigurationIndexDto.CASE_SURVEILLANCE_ENABLED).setRenderer(new BooleanRenderer());
		getColumn(DiseaseConfigurationIndexDto.EXTENDED_CLASSIFICATION).setRenderer(new BooleanRenderer());
		getColumn(DiseaseConfigurationIndexDto.EXTENDED_CLASSIFICATION_MULTI).setRenderer(new BooleanRenderer());
		getColumn(DiseaseConfigurationIndexDto.AGGREGATE_REPORTING_ENABLED).setRenderer(new BooleanRenderer());

		getColumn(DiseaseConfigurationIndexDto.DISEASE)
			.setComparator((o1, o2) -> comparingOnEnumField(DiseaseConfigurationIndexDto::getDisease).compare(o1, o2));

	}

	private void setEagerDataProvider() {
		setDataProvider(FacadeProvider.getDiseaseConfigurationFacade().getIndexList(getCriteria(), null, null, List.of()).stream());
	}

	public void reload() {
		setEagerDataProvider();
	}
}
