/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.samples;

import java.util.Date;

import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleCriteria;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleIndexDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleMaterial;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.ReloadableGrid;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class EnvironmentSampleGrid extends ReloadableGrid<EnvironmentSampleIndexDto, EnvironmentSampleCriteria> {

	private static final long serialVersionUID = -8118341883217832133L;

	public EnvironmentSampleGrid(EnvironmentSampleCriteria criteria) {
		super(EnvironmentSampleIndexDto.class);

		setSizeFull();

		SamplesViewConfiguration viewConfiguration = ViewModelProviders.of(SamplesView.class).get(SamplesViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		removeColumn(EnvironmentSampleIndexDto.SAMPLE_MATERIAL);
		Column<EnvironmentSampleIndexDto, String> sampleMaterialColumn = addColumn(entry -> {
			EnvironmentSampleMaterial sampleMaterial = entry.getSampleMaterial();
			if (sampleMaterial == EnvironmentSampleMaterial.OTHER) {
				return entry.getOtherSampleMaterial() != null ? entry.getOtherSampleMaterial() : sampleMaterial.toString();
			} else {
				return sampleMaterial != null ? sampleMaterial.toString() : "";
			}
		});
		sampleMaterialColumn.setId(EnvironmentSampleIndexDto.SAMPLE_MATERIAL);

		Column<EnvironmentSampleIndexDto, String> deleteColumn = addColumn(entry -> {
			if (entry.getDeletionReason() != null) {
				return entry.getDeletionReason() + (entry.getOtherDeletionReason() != null ? ": " + entry.getOtherDeletionReason() : "");
			} else {
				return "-";
			}
		});
		deleteColumn.setId(DELETE_REASON_COLUMN);
		deleteColumn.setSortable(false);
		deleteColumn.setCaption(I18nProperties.getCaption(Captions.deletionReason));

		setColumns(
			EnvironmentSampleIndexDto.UUID,
			EnvironmentSampleIndexDto.FIELD_SAMPLE_ID,
			EnvironmentSampleIndexDto.SAMPLE_DATE_TIME,
			EnvironmentSampleIndexDto.ENVIRONMENT,
			EnvironmentSampleIndexDto.LOCATION,
			EnvironmentSampleIndexDto.DISTRICT,
			EnvironmentSampleIndexDto.DISPATCHED,
			EnvironmentSampleIndexDto.DISPATCH_DATE,
			EnvironmentSampleIndexDto.RECEIVED,
			EnvironmentSampleIndexDto.LABORATORY,
			EnvironmentSampleIndexDto.SAMPLE_MATERIAL,
			EnvironmentSampleIndexDto.POSITIVE_PATHOGEN_TESTS,
			EnvironmentSampleIndexDto.LATEST_PATHOGEN_TEST,
			EnvironmentSampleIndexDto.NUMBER_OF_TESTS,
			DELETE_REASON_COLUMN);

		Language userLanguage = I18nProperties.getUserLanguage();
		((Column<EnvironmentSampleIndexDto, String>) getColumn(EnvironmentSampleIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<EnvironmentSampleIndexDto, Date>) getColumn(EnvironmentSampleIndexDto.SAMPLE_DATE_TIME))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<EnvironmentSampleIndexDto, Boolean>) getColumn(EnvironmentSampleIndexDto.DISPATCHED)).setRenderer(new BooleanRenderer());
		((Column<EnvironmentSampleIndexDto, Date>) getColumn(EnvironmentSampleIndexDto.DISPATCH_DATE))
			.setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<EnvironmentSampleIndexDto, Boolean>) getColumn(EnvironmentSampleIndexDto.RECEIVED)).setRenderer(new BooleanRenderer());
		((Column<EnvironmentSampleIndexDto, String>) getColumn(EnvironmentSampleIndexDto.LABORATORY)).setMaximumWidth(200);

		for (Column<EnvironmentSampleIndexDto, ?> column : getColumns()) {
			if (!DELETE_REASON_COLUMN.equals(column.getId())) {
				column.setCaption(I18nProperties.findPrefixCaption(column.getId(), EnvironmentSampleIndexDto.I18N_PREFIX, LocationDto.I18N_PREFIX));
				column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(getBeanType(), column.getId()));
			}
		}

		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS_CASE_SAMPLES)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}
	}

	@Override
	public void reload() {
		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		if (ViewModelProviders.of(SamplesView.class).get(SamplesViewConfiguration.class).isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {

		setLazyDataProvider(FacadeProvider.getEnvironmentSampleFacade()::getIndexList, FacadeProvider.getEnvironmentSampleFacade()::count);
	}

	public void setEagerDataProvider() {

		setEagerDataProvider(FacadeProvider.getEnvironmentSampleFacade()::getIndexList);
	}
}
