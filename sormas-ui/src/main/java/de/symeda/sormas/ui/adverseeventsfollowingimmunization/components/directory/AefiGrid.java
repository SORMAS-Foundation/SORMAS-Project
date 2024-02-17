/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.directory;

import java.util.Date;

import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiCriteria;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiIndexDto;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.immunization.ImmunizationPersonView;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class AefiGrid extends FilteredGrid<AefiIndexDto, AefiCriteria> {

	public AefiGrid(AefiCriteria criteria) {
		super(AefiIndexDto.class);
		setSizeFull();
		setLazyDataProvider();
		setCriteria(criteria);

		Column<AefiIndexDto, String> deleteColumn = addColumn(entry -> {
			if (entry.getDeletionReason() != null) {
				return entry.getDeletionReason() + (entry.getOtherDeletionReason() != null ? ": " + entry.getOtherDeletionReason() : "");
			} else {
				return "-";
			}
		});
		deleteColumn.setId(DELETE_REASON_COLUMN);
		deleteColumn.setSortable(false);
		deleteColumn.setCaption(I18nProperties.getCaption(Captions.deletionReason));

		initColumns();

		addItemClickListener(new ShowDetailsListener<>(AefiIndexDto.UUID, e -> ControllerProvider.getAefiController().navigateToAefi(e.getUuid())));
		addItemClickListener(new ShowDetailsListener<>(AefiIndexDto.IMMUNIZATION_UUID, e -> {
			ControllerProvider.getImmunizationController().navigateToImmunization(e.getImmunizationUuid());
		}));
		addItemClickListener(new ShowDetailsListener<>(AefiIndexDto.PERSON_UUID, e -> {
			ControllerProvider.getImmunizationController().navigateToView(ImmunizationPersonView.VIEW_NAME, e.getImmunizationUuid());
		}));
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	private void initColumns() {
		setColumns(
			AefiIndexDto.UUID,
			AefiIndexDto.IMMUNIZATION_UUID,
			AefiIndexDto.PERSON_UUID,
			AefiIndexDto.PERSON_FIRST_NAME,
			AefiIndexDto.PERSON_LAST_NAME,
			AefiIndexDto.DISEASE,
			AefiIndexDto.AGE_AND_BIRTH_DATE,
			AefiIndexDto.SEX,
			AefiIndexDto.REGION,
			AefiIndexDto.DISTRICT,
			AefiIndexDto.SERIOUS,
			AefiIndexDto.PRIMARY_VACCINE_NAME,
			AefiIndexDto.OUTCOME,
			AefiIndexDto.VACCINATION_DATE,
			AefiIndexDto.REPORT_DATE,
			AefiIndexDto.START_DATE_TIME,
			AefiIndexDto.ADVERSE_EVENTS,
			DELETE_REASON_COLUMN);

		((Column<AefiIndexDto, String>) getColumn(AefiIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<AefiIndexDto, String>) getColumn(AefiIndexDto.IMMUNIZATION_UUID)).setRenderer(new UuidRenderer());
		((Column<AefiIndexDto, String>) getColumn(AefiIndexDto.PERSON_UUID)).setRenderer(new UuidRenderer());

		((Column<AefiIndexDto, AgeAndBirthDateDto>) getColumn(AefiIndexDto.AGE_AND_BIRTH_DATE)).setRenderer(
			value -> value == null
				? ""
				: PersonHelper.getAgeAndBirthdateString(
					value.getAge(),
					value.getAgeType(),
					value.getDateOfBirthDD(),
					value.getDateOfBirthMM(),
					value.getDateOfBirthYYYY()),
			new TextRenderer());

		((Column<AefiIndexDto, Date>) getColumn(AefiIndexDto.VACCINATION_DATE)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<AefiIndexDto, Date>) getColumn(AefiIndexDto.REPORT_DATE)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<AefiIndexDto, Date>) getColumn(AefiIndexDto.START_DATE_TIME)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));

		for (Column<AefiIndexDto, ?> column : getColumns()) {
			column.setCaption(I18nProperties.findPrefixCaptionWithDefault(column.getId(), column.getCaption(), AefiIndexDto.I18N_PREFIX));
			column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(getBeanType(), column.getId()));
		}

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			getColumn(AefiIndexDto.REGION).setHidden(true);
			getColumn(AefiIndexDto.DISTRICT).setHidden(true);
		}
	}

	private void setLazyDataProvider() {

		setLazyDataProvider(FacadeProvider.getAefiFacade()::getIndexList, FacadeProvider.getAefiFacade()::count);
	}
}
