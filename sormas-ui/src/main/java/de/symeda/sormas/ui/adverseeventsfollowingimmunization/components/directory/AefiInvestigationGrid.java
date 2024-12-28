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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.directory;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationCriteria;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationIndexDto;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class AefiInvestigationGrid extends FilteredGrid<AefiInvestigationIndexDto, AefiInvestigationCriteria> {

	public static final String PRIMARY_VACCINE_COLUMN = "primaryVaccineColumn";

	public AefiInvestigationGrid(AefiInvestigationCriteria criteria) {
		super(AefiInvestigationIndexDto.class);
		setSizeFull();
		setLazyDataProvider();
		setCriteria(criteria);

		Column<AefiInvestigationIndexDto, String> primaryVaccineColumn = addColumn(entry -> {
			if (entry.getPrimaryVaccine() != null) {
				if (entry.getPrimaryVaccine() != Vaccine.OTHER) {
					return entry.getPrimaryVaccine().toString();
				} else {
					return StringUtils.isBlank(entry.getPrimaryVaccineDetails())
						? entry.getPrimaryVaccine().toString()
						: (entry.getPrimaryVaccine() + " (" + entry.getPrimaryVaccineDetails() + ")");
				}
			} else {
				return "-";
			}
		});
		primaryVaccineColumn.setId(PRIMARY_VACCINE_COLUMN);
		primaryVaccineColumn
			.setCaption(I18nProperties.getPrefixCaption(AefiInvestigationIndexDto.I18N_PREFIX, AefiInvestigationIndexDto.PRIMARY_VACCINE_NAME));

		Column<AefiInvestigationIndexDto, String> deleteColumn = addColumn(entry -> {
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

		addItemClickListener(
			new ShowDetailsListener<>(
				AefiInvestigationIndexDto.UUID,
				e -> ControllerProvider.getAefiInvestigationController().navigateToAefiInvestigation(e.getUuid())));
		addItemClickListener(new ShowDetailsListener<>(AefiInvestigationIndexDto.AEFI_REPORT_UUID, e -> {
			ControllerProvider.getAefiController().navigateToAefi(e.getAefiReportUuid());
		}));
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	private void initColumns() {
		setColumns(
			AefiInvestigationIndexDto.UUID,
			AefiInvestigationIndexDto.INVESTIGATION_CASE_ID,
			AefiInvestigationIndexDto.AEFI_REPORT_UUID,
			AefiInvestigationIndexDto.PERSON_FIRST_NAME,
			AefiInvestigationIndexDto.PERSON_LAST_NAME,
			AefiInvestigationIndexDto.DISEASE,
			AefiInvestigationIndexDto.AGE_AND_BIRTH_DATE,
			AefiInvestigationIndexDto.SEX,
			AefiInvestigationIndexDto.REGION,
			AefiInvestigationIndexDto.DISTRICT,
			PRIMARY_VACCINE_COLUMN,
			AefiInvestigationIndexDto.STATUS_ON_DATE_OF_INVESTIGATION,
			AefiInvestigationIndexDto.INVESTIGATION_STATUS,
			AefiInvestigationIndexDto.AEFI_CLASSIFICATION,
			AefiInvestigationIndexDto.REPORT_DATE,
			AefiInvestigationIndexDto.INVESTIGATION_DATE,
			AefiInvestigationIndexDto.INVESTIGATION_STAGE,
			DELETE_REASON_COLUMN);

		((Column<AefiInvestigationIndexDto, String>) getColumn(AefiInvestigationIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<AefiInvestigationIndexDto, String>) getColumn(AefiInvestigationIndexDto.AEFI_REPORT_UUID)).setRenderer(new UuidRenderer());

		((Column<AefiInvestigationIndexDto, AgeAndBirthDateDto>) getColumn(AefiInvestigationIndexDto.AGE_AND_BIRTH_DATE)).setRenderer(
			value -> value == null
				? ""
				: PersonHelper.getAgeAndBirthdateString(
					value.getAge(),
					value.getAgeType(),
					value.getDateOfBirthDD(),
					value.getDateOfBirthMM(),
					value.getDateOfBirthYYYY()),
			new TextRenderer());

		((Column<AefiInvestigationIndexDto, Date>) getColumn(AefiInvestigationIndexDto.REPORT_DATE))
			.setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<AefiInvestigationIndexDto, Date>) getColumn(AefiInvestigationIndexDto.INVESTIGATION_DATE))
			.setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));

		for (Column<AefiInvestigationIndexDto, ?> column : getColumns()) {
			column
				.setCaption(I18nProperties.findPrefixCaptionWithDefault(column.getId(), column.getCaption(), AefiInvestigationIndexDto.I18N_PREFIX));
			column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(getBeanType(), column.getId()));
		}

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			getColumn(AefiInvestigationIndexDto.REGION).setHidden(true);
			getColumn(AefiInvestigationIndexDto.DISTRICT).setHidden(true);
		}
	}

	private void setLazyDataProvider() {

		setLazyDataProvider(FacadeProvider.getAefiInvestigationFacade()::getIndexList, FacadeProvider.getAefiInvestigationFacade()::count);
	}
}
