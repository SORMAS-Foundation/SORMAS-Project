/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.vaccination;

import java.util.function.Consumer;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.Table;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.caze.AbstractTableField;
import de.symeda.sormas.ui.utils.DateFormatHelper;

public class VaccinationsField extends AbstractTableField<VaccinationDto> {

	private Disease disease;
	private final UiFieldAccessCheckers fieldAccessCheckers;

	public VaccinationsField(UiFieldAccessCheckers fieldAccessCheckers) {
		super(fieldAccessCheckers);
		this.fieldAccessCheckers = fieldAccessCheckers;
	}

	@Override
	public Class<VaccinationDto> getEntryType() {
		return VaccinationDto.class;
	}

	@Override
	protected void editEntry(VaccinationDto entry, boolean create, Consumer<VaccinationDto> commitCallback) {

		if (create) {
			if (entry.getUuid() == null) {
				entry.setUuid(DataHelper.createUuid());
			}
			if (entry.getReportingUser() == null) {
				entry.setReportingUser(FacadeProvider.getUserFacade().getCurrentUserAsReference());
			}
		}

		if (create) {
			ControllerProvider.getVaccinationController().create(entry.getImmunization(), disease, fieldAccessCheckers, commitCallback);
		} else {
			ControllerProvider.getVaccinationController()
				.edit(entry, disease, fieldAccessCheckers, false, commitCallback, () -> VaccinationsField.this.removeEntry(entry), true, true);
		}
	}

	@Override
	protected VaccinationDto createEntry() {

		UserDto user = UiUtil.getUser();
		return VaccinationDto.build(user.toReference());
	}

	@Override
	protected Table createTable() {
		Table table = super.createTable();

		table.addGeneratedColumn(VaccinationDto.UUID, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			Label textField = new Label(DataHelper.getShortUuid(((EntityDto) itemId).getUuid()));
			return textField;
		});
		table.addGeneratedColumn(VaccinationDto.VACCINATION_DATE, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			Label textField = new Label(DateFormatHelper.formatDate(((VaccinationDto) itemId).getVaccinationDate()));
			return textField;
		});

		return table;
	}

	@Override
	protected void updateColumns() {
		Table table = getTable();

		table.addGeneratedColumn(Captions.columnVaccineName, (Table.ColumnGenerator) (source, item, columnId) -> {
			VaccinationDto vaccinationDto = (VaccinationDto) item;
			return Vaccine.OTHER.equals(vaccinationDto.getVaccineName()) ? vaccinationDto.getOtherVaccineName() : vaccinationDto.getVaccineName();
		});

		table.addGeneratedColumn(Captions.columnVaccineManufacturer, (Table.ColumnGenerator) (source, item, columnId) -> {
			VaccinationDto vaccinationDto = (VaccinationDto) item;
			return VaccineManufacturer.OTHER.equals(vaccinationDto.getVaccineManufacturer())
				? vaccinationDto.getOtherVaccineManufacturer()
				: vaccinationDto.getVaccineManufacturer();
		});

		table.setVisibleColumns(
			ACTION_COLUMN_ID,
			VaccinationDto.UUID,
			VaccinationDto.VACCINATION_DATE,
			VaccinationDto.VACCINE_NAME,
			VaccinationDto.VACCINE_MANUFACTURER,
			VaccinationDto.VACCINE_TYPE,
			VaccinationDto.VACCINE_DOSE);

		for (Object columnId : table.getVisibleColumns()) {
			if (columnId.equals(ACTION_COLUMN_ID)) {
				table.setColumnHeader(columnId, "&nbsp");
			} else if (columnId.equals(Captions.columnVaccineName)) {
				table.setColumnHeader(columnId, I18nProperties.getCaption(Captions.columnVaccineName));
			} else if (columnId.equals(Captions.columnVaccineManufacturer)) {
				table.setColumnHeader(columnId, I18nProperties.getCaption(Captions.columnVaccineManufacturer));
			} else {
				table.setColumnHeader(columnId, I18nProperties.getPrefixCaption(VaccinationDto.I18N_PREFIX, (String) columnId));
			}
		}
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Override
	protected boolean isEmpty(VaccinationDto entry) {
		return false;
	}

	@Override
	protected boolean isModified(VaccinationDto oldEntry, VaccinationDto newEntry) {
		return false;
	}
}
