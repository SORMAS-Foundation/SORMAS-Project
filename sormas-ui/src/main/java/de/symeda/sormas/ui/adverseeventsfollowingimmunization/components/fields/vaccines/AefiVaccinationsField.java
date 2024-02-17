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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.fields.vaccines;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.caze.AbstractTableField;

public class AefiVaccinationsField extends AbstractTableField<VaccinationDto> {

	private AefiDto aefiDto;
	private VaccinationDto primarySuspectVaccination;
	private final UiFieldAccessCheckers fieldAccessCheckers;

	public AefiVaccinationsField(UiFieldAccessCheckers fieldAccessCheckers) {
		super(fieldAccessCheckers);
		this.fieldAccessCheckers = fieldAccessCheckers;

		updateAddButtonCaption();
	}

	@Override
	public Class<VaccinationDto> getEntryType() {
		return VaccinationDto.class;
	}

	@Override
	protected void editEntry(VaccinationDto entry, boolean create, Consumer<VaccinationDto> commitCallback) {

		if (create) {
			ControllerProvider.getAefiController().selectPrimarySuspectVaccination(aefiDto, this::selectPrimarySuspectVaccination);
		}
	}

	public void updateAddButtonCaption() {
		getAddButton().setCaption(I18nProperties.getCaption(Captions.actionAefiSelectPrimarySuspectVaccination));
	}

	/*
	 * @Override
	 * protected VaccinationDto createEntry() {
	 * UserDto user = UserProvider.getCurrent().getUser();
	 * return VaccinationDto.build(user.toReference());
	 * }
	 */

	/*
	 * @Override
	 * protected Table createTable() {
	 * Table table = super.createTable();
	 * table.addGeneratedColumn(VaccinationDto.UUID, (Table.ColumnGenerator) (source, itemId, columnId) -> {
	 * Label textField = new Label(DataHelper.getShortUuid(((EntityDto) itemId).getUuid()));
	 * return textField;
	 * });
	 * table.addGeneratedColumn(VaccinationDto.VACCINATION_DATE, (Table.ColumnGenerator) (source, itemId, columnId) -> {
	 * Label textField = new Label(DateFormatHelper.formatDate(((VaccinationDto) itemId).getVaccinationDate()));
	 * return textField;
	 * });
	 * return table;
	 * }
	 */

	@Override
	protected void updateColumns() {
		Table table = getTable();

		table.addGeneratedColumn(Captions.aefiVaccinationsPrimaryVaccine, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			VaccinationDto vaccinationDto = (VaccinationDto) itemId;
			return new Label(
				primarySuspectVaccination != null && StringUtils.equals(vaccinationDto.getUuid(), primarySuspectVaccination.getUuid())
					? VaadinIcons.CHECK_CIRCLE.getHtml()
					: "",
				ContentMode.HTML);
		});

		table.addGeneratedColumn(Captions.aefiVaccinationsVaccineDetails, (Table.ColumnGenerator) (source, item, columnId) -> {
			VaccinationDto vaccinationDto = (VaccinationDto) item;

			StringBuilder vaccineDetailsBuilder = new StringBuilder();
			vaccineDetailsBuilder.append(vaccinationDto.getVaccineManufacturer());
			vaccineDetailsBuilder.append(", ")
				.append(
					Vaccine.OTHER.equals(vaccinationDto.getVaccineName()) ? vaccinationDto.getOtherVaccineName() : vaccinationDto.getVaccineName());

			if (vaccinationDto.getVaccinationDate() != null) {
				vaccineDetailsBuilder.append(", ").append(DateFormatHelper.formatDate(vaccinationDto.getVaccinationDate()));
			}

			if (!StringUtils.isBlank(vaccinationDto.getVaccineDose())) {
				vaccineDetailsBuilder.append(", Dose ").append(vaccinationDto.getVaccineDose());
			}

			return vaccineDetailsBuilder.toString();
		});

		table.addGeneratedColumn(Captions.aefiVaccinationsDiluentBatchLotNumber, (Table.ColumnGenerator) (source, item, columnId) -> {
			VaccinationDto vaccinationDto = (VaccinationDto) item;
			return "-";
		});

		table.addGeneratedColumn(Captions.aefiVaccinationsDiluentExpiryDate, (Table.ColumnGenerator) (source, item, columnId) -> {
			VaccinationDto vaccinationDto = (VaccinationDto) item;
			return "-";
		});

		table.addGeneratedColumn(Captions.aefiVaccinationsDiluentTimeOfReconstitution, (Table.ColumnGenerator) (source, item, columnId) -> {
			VaccinationDto vaccinationDto = (VaccinationDto) item;
			return "-";
		});

		table.setVisibleColumns(
			Captions.aefiVaccinationsPrimaryVaccine,
			Captions.aefiVaccinationsVaccineDetails,
			Captions.aefiVaccinationsDiluentBatchLotNumber,
			Captions.aefiVaccinationsDiluentExpiryDate,
			Captions.aefiVaccinationsDiluentTimeOfReconstitution);

		for (Object columnId : table.getVisibleColumns()) {
			if (columnId.equals(ACTION_COLUMN_ID)) {
				table.setColumnHeader(columnId, "&nbsp");
			} else if (columnId.equals(Captions.aefiVaccinationsPrimaryVaccine)) {
				table.setColumnHeader(columnId, I18nProperties.getCaption(Captions.aefiVaccinationsPrimaryVaccine));
			} else if (columnId.equals(Captions.aefiVaccinationsVaccineDetails)) {
				table.setColumnHeader(columnId, I18nProperties.getCaption(Captions.aefiVaccinationsVaccineDetails));
			} else if (columnId.equals(Captions.aefiVaccinationsDiluentBatchLotNumber)) {
				table.setColumnHeader(columnId, I18nProperties.getCaption(Captions.aefiVaccinationsDiluentBatchLotNumber));
			} else if (columnId.equals(Captions.aefiVaccinationsDiluentExpiryDate)) {
				table.setColumnHeader(columnId, I18nProperties.getCaption(Captions.aefiVaccinationsDiluentExpiryDate));
			} else if (columnId.equals(Captions.aefiVaccinationsDiluentTimeOfReconstitution)) {
				table.setColumnHeader(columnId, I18nProperties.getCaption(Captions.aefiVaccinationsDiluentTimeOfReconstitution));
			} else {
				table.setColumnHeader(columnId, I18nProperties.getPrefixCaption(VaccinationDto.I18N_PREFIX, (String) columnId));
			}
		}
	}

	public void refreshTable() {
		Table table = getTable();

		BeanItemContainer<VaccinationDto> container = getContainer();
		if (container == null) {
			return;
		}

		container.removeAllItems();
		container.addAll(aefiDto.getVaccinations());
		table.refreshRowCache();
	}

	public void setAefiDto(AefiDto aefiDto) {
		this.aefiDto = aefiDto;
	}

	public void selectPrimarySuspectVaccination(VaccinationDto vaccinationDto) {
		primarySuspectVaccination = vaccinationDto;
		refreshTable();
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
