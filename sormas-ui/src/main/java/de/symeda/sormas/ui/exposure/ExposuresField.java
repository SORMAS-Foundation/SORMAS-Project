/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.exposure;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.exposure.GatheringType;
import de.symeda.sormas.api.exposure.HabitationType;
import de.symeda.sormas.api.exposure.TypeOfAnimal;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.LocationHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.caze.AbstractTableField;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessCellStyleGenerator;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class ExposuresField extends AbstractTableField<ExposureDto> {

	private static final String COLUMN_EXPOSURE_TYPE = ExposureDto.EXPOSURE_TYPE;
	private static final String COLUMN_TYPE_OF_PLACE = ExposureDto.TYPE_OF_PLACE;
	private static final String COLUMN_DATE = Captions.date;
	private static final String COLUMN_ADDRESS = Captions.address;
	private static final String COLUMN_DESCRIPTION = ExposureDto.DESCRIPTION;
	private static final String COLUMN_SOURCE_CASE_NAME = Captions.exposureSourceCaseName;

	private final FieldVisibilityCheckers fieldVisibilityCheckers;
	private Supplier<List<ContactReferenceDto>> getSourceContactsCallback;
	private Class<? extends EntityDto> epiDataParentClass;
	private boolean isPseudonymized;
	private boolean isEditAllowed;

	public ExposuresField(FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers, boolean isEditAllowed) {

		super(fieldAccessCheckers, isEditAllowed);
		this.fieldVisibilityCheckers = fieldVisibilityCheckers;
		this.isEditAllowed = isEditAllowed;
	}

	@Override
	protected void updateColumns() {
		Table table = getTable();

		addGeneratedColumns(table);

		if (epiDataParentClass == CaseDataDto.class) {
			table.setVisibleColumns(
				ACTION_COLUMN_ID,
				COLUMN_EXPOSURE_TYPE,
				ExposureDto.EXPOSURE_ROLE,
				COLUMN_TYPE_OF_PLACE,
				COLUMN_DATE,
				COLUMN_ADDRESS,
				COLUMN_DESCRIPTION,
				COLUMN_SOURCE_CASE_NAME);
		} else {
			table.setVisibleColumns(
				ACTION_COLUMN_ID,
				COLUMN_EXPOSURE_TYPE,
				ExposureDto.EXPOSURE_ROLE,
				COLUMN_TYPE_OF_PLACE,
				COLUMN_DATE,
				COLUMN_ADDRESS,
				COLUMN_DESCRIPTION);
		}
		table.setCellStyleGenerator(
			FieldAccessCellStyleGenerator.withFieldAccessCheckers(ExposureDto.class, UiFieldAccessCheckers.forSensitiveData(isPseudonymized)));

		for (Object columnId : table.getVisibleColumns()) {
			if (columnId.equals(ACTION_COLUMN_ID)) {
				table.setColumnHeader(columnId, "&nbsp");
			} else {
				table.setColumnHeader(columnId, I18nProperties.getPrefixCaption(ExposureDto.I18N_PREFIX, (String) columnId));
			}
		}
	}

	private void addGeneratedColumns(Table table) {
		table.addGeneratedColumn(COLUMN_EXPOSURE_TYPE, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;
			String exposureString =
				ExposureType.OTHER != exposure.getExposureType() ? exposure.getExposureType().toString() : exposure.getExposureTypeDetails();

			// if possible, always display "lowest-level" activity type (e.g. show type of gathering instead of just "gathering")
			if (exposure.getExposureType() == ExposureType.GATHERING && exposure.getGatheringType() != null) {
				exposureString += " - " + (exposure.getGatheringType() != GatheringType.OTHER
					? exposure.getGatheringType().toString()
					: (StringUtils.isNotEmpty(exposure.getGatheringDetails()) ? exposure.getGatheringDetails() : GatheringType.OTHER.toString()));
			}

			if (exposure.getExposureType() == ExposureType.HABITATION && exposure.getHabitationType() != null) {
				exposureString += " - " + (exposure.getHabitationType() != HabitationType.OTHER
					? exposure.getHabitationType().toString()
					: (StringUtils.isNotEmpty(exposure.getHabitationDetails()) ? exposure.getHabitationDetails() : HabitationType.OTHER.toString()));
			}

			if (exposure.getExposureType() == ExposureType.ANIMAL_CONTACT && exposure.getTypeOfAnimal() != null) {
				exposureString += " (" + (exposure.getTypeOfAnimal() != TypeOfAnimal.OTHER
					? exposure.getTypeOfAnimal().toString()
					: (exposure.getTypeOfAnimalDetails() != null ? exposure.getTypeOfAnimalDetails() : TypeOfAnimal.OTHER.toString())) + ")";
			}

			if (exposure.getRiskArea() == YesNoUnknown.YES || exposure.isProbableInfectionEnvironment())
				exposureString = "<b>" + exposureString + "</b>";

			if (exposure.getRiskArea() == YesNoUnknown.YES) {
				exposureString = VaadinIcons.INFO_CIRCLE.getHtml() + " " + exposureString;
			}

			if (exposure.isProbableInfectionEnvironment()) {
				exposureString = VaadinIcons.CHECK_CIRCLE.getHtml() + " " + exposureString;
			}

			Label exposureTypeLabel = new Label(exposureString, ContentMode.HTML);

			if (exposure.getRiskArea() == YesNoUnknown.YES) {
				exposureTypeLabel.setDescription(I18nProperties.getString(Strings.infoExposuresRiskAreaHint) + " ");
			}

			if (exposure.isProbableInfectionEnvironment()) {
				exposureTypeLabel
					.setDescription(exposureTypeLabel.getDescription() + I18nProperties.getString(Strings.infoExposuresInfectionEnvironmentHint));
			}

			return exposureTypeLabel;
		});

		table.addGeneratedColumn(COLUMN_TYPE_OF_PLACE, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;
			String typeOfPlaceString;

			if (exposure.getTypeOfPlace() == null) {
				return "";
			} else if (exposure.getTypeOfPlace() == TypeOfPlace.FACILITY && exposure.getLocation().getFacilityType() != null) {
				typeOfPlaceString = exposure.getLocation().getFacilityType().toString();

				if (StringUtils.isNotEmpty(exposure.getLocation().getFacilityDetails())) {
					typeOfPlaceString += " - " + exposure.getLocation().getFacilityDetails();
				} else if (exposure.getLocation().getFacility() != null) {
					typeOfPlaceString += " - " + exposure.getLocation().getFacility().buildCaption();
				}
			} else if (exposure.getTypeOfPlace() == TypeOfPlace.MEANS_OF_TRANSPORT) {
				typeOfPlaceString = exposure.getMeansOfTransport() == null
					? TypeOfPlace.MEANS_OF_TRANSPORT.toString()
					: (exposure.getMeansOfTransport() != MeansOfTransport.OTHER
						? exposure.getMeansOfTransport().toString()
						: ((StringUtils.isNotEmpty(exposure.getMeansOfTransportDetails()))
							? exposure.getMeansOfTransportDetails()
							: TypeOfPlace.MEANS_OF_TRANSPORT.toString()));
			} else {
				typeOfPlaceString = exposure.getTypeOfPlace() != TypeOfPlace.OTHER
					? exposure.getTypeOfPlace().toString()
					: (StringUtils.isNotEmpty(exposure.getTypeOfPlaceDetails()) ? exposure.getTypeOfPlaceDetails() : TypeOfPlace.OTHER.toString());
			}

			return typeOfPlaceString;
		});

		table.addGeneratedColumn(COLUMN_DATE, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;
			return DateFormatHelper.buildPeriodDateTimeString(exposure.getStartDate(), exposure.getEndDate());
		});

		table.addGeneratedColumn(
			COLUMN_ADDRESS,
			(Table.ColumnGenerator) (source, itemId, columnId) -> LocationHelper.buildLocationString(((ExposureDto) itemId).getLocation()));

		table.addGeneratedColumn(COLUMN_DESCRIPTION, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;

			Label descriptionLabel = new Label(StringUtils.abbreviate(exposure.getDescription(), 75));
			if (StringUtils.isNotBlank(exposure.getDescription())) {
				descriptionLabel.setDescription(exposure.getDescription());
			}

			return descriptionLabel;
		});

		table.addGeneratedColumn(COLUMN_SOURCE_CASE_NAME, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;
			ContactReferenceDto contactToCase = exposure.getContactToCase();
			return !isPseudonymized
				? DataHelper.toStringNullable(contactToCase != null ? getContactCaseName(contactToCase) : null)
				: I18nProperties.getCaption(Captions.inaccessibleValue);
		});
	}

	private static String getContactCaseName(ContactReferenceDto contactToCase) {
		return contactToCase.getCaze() != null ? contactToCase.getCaze().buildNameCaption() : null;
	}

	@Override
	protected boolean isEmpty(ExposureDto entry) {
		return false;
	}

	@Override
	protected boolean isModified(ExposureDto oldEntry, ExposureDto newEntry) {
		return isModifiedObject(oldEntry.getExposureType(), newEntry.getExposureType())
			|| isModifiedObject(oldEntry.getExposureTypeDetails(), newEntry.getExposureTypeDetails())
			|| isModifiedObject(oldEntry.getExposureRole(), newEntry.getExposureRole())
			|| isModifiedObject(oldEntry.getTypeOfPlace(), newEntry.getTypeOfPlace())
			|| isModifiedObject(oldEntry.getTypeOfPlaceDetails(), newEntry.getTypeOfPlaceDetails())
			|| isModifiedObject(oldEntry.getStartDate(), newEntry.getStartDate())
			|| isModifiedObject(oldEntry.getEndDate(), newEntry.getEndDate())
			|| isModifiedObject(oldEntry.isProbableInfectionEnvironment(), newEntry.isProbableInfectionEnvironment())
			|| isModifiedObject(oldEntry.getRiskArea(), newEntry.getRiskArea())
			|| isModifiedObject(oldEntry.getLocation(), newEntry.getLocation())
			|| isModifiedObject(oldEntry.getDescription(), newEntry.getDescription())
			|| isModifiedObject(oldEntry.getContactToCase(), newEntry.getContactToCase());
	}

	@Override
	public Class<ExposureDto> getEntryType() {
		return ExposureDto.class;
	}

	@Override
	protected void editEntry(ExposureDto entry, boolean create, Consumer<ExposureDto> commitCallback) {
		if (create) {
			entry.setUuid(DataHelper.createUuid());
		}

		ExposureForm exposureForm = new ExposureForm(
			create,
			epiDataParentClass,
			getSourceContactsCallback != null ? getSourceContactsCallback.get() : null,
			fieldVisibilityCheckers,
			fieldAccessCheckers);
		exposureForm.setValue(entry);

		final CommitDiscardWrapperComponent<ExposureForm> component =
			new CommitDiscardWrapperComponent<>(exposureForm, UiUtil.permitted(UserRight.CASE_EDIT) && isEditAllowed, exposureForm.getFieldGroup());
		component.getCommitButton().setCaption(I18nProperties.getString(Strings.done));

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.entityExposure));
		popupWindow.setHeight(90, Unit.PERCENTAGE);

		if (isEditAllowed) {
			component.addCommitListener(() -> {
				if (entry.isProbableInfectionEnvironment()) {
					for (ExposureDto exposure : getValue()) {
						if (exposure.isProbableInfectionEnvironment() && !exposure.getUuid().equals(entry.getUuid())) {
							showMultipleInfectionEnvironmentsPopup(entry);
							break;
						}
					}
				}
			});

			component.addCommitListener(() -> {
				if (!exposureForm.getFieldGroup().isModified()) {
					commitCallback.accept(exposureForm.getValue());

					updateAddButtonVisibility(getValue().size());
				}
			});

			if (!create) {
				component.addDeleteListener(() -> {
					popupWindow.close();
					ExposuresField.this.removeEntry(entry);

					updateAddButtonVisibility(getValue().size());
				}, I18nProperties.getCaption(ExposureDto.I18N_PREFIX));
			}
		} else {
			component.getCommitButton().setVisible(false);
			component.getDiscardButton().setVisible(false);
		}
	}

	@Override
	protected ExposureDto createEntry() {
		UserDto user = UiUtil.getUser();
		ExposureDto exposure = ExposureDto.build(null);
		exposure.getLocation().setRegion(user.getRegion());
		exposure.getLocation().setDistrict(user.getDistrict());
		exposure.getLocation().setCommunity(user.getCommunity());
		exposure.setReportingUser(user.toReference());
		return exposure;
	}

	@Override
	public void setPropertyDataSource(Property newDataSource) {
		super.setPropertyDataSource(newDataSource);

		if (getValue() != null) {
			updateAddButtonVisibility(getValue().size());
		}
	}

	public void setGetSourceContactsCallback(Supplier<List<ContactReferenceDto>> callback) {
		getSourceContactsCallback = callback;
	}

	public void setEpiDataParentClass(Class<? extends EntityDto> epiDataParentClass) {
		this.epiDataParentClass = epiDataParentClass;
	}

	public void setPseudonymized(boolean isPseudonymized) {
		this.isPseudonymized = isPseudonymized;
	}

	private void updateAddButtonVisibility(int exposureCount) {
		if (isReadOnly() || epiDataParentClass == ContactDto.class && exposureCount > 0 || !isEditAllowed) {
			getAddButton().setVisible(false);
		} else {
			getAddButton().setVisible(true);
		}
	}

	private void showMultipleInfectionEnvironmentsPopup(ExposureDto entry) {
		VerticalLayout warningLayout = VaadinUiUtil.createWarningLayout();
		Window popupWindow = VaadinUiUtil.showPopupWindow(warningLayout);
		com.vaadin.ui.Label infoLabel = new com.vaadin.ui.Label(I18nProperties.getValidationError(Validations.caseMultipleInfectionEnvironments));
		CssStyles.style(infoLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_WHITE_SPACE_NORMAL);
		warningLayout.addComponent(infoLabel);
		ConfirmationComponent yesNo = VaadinUiUtil.buildYesNoConfirmationComponent();
		yesNo.getConfirmButton().addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				popupWindow.close();
				for (ExposureDto exposure : getValue()) {
					if (exposure.isProbableInfectionEnvironment() && !exposure.getUuid().equals(entry.getUuid())) {
						exposure.setProbableInfectionEnvironment(false);
					}
				}
				getTable().refreshRowCache();
			}
		});

		yesNo.getCancelButton().addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				popupWindow.close();
				entry.setProbableInfectionEnvironment(false);
				getTable().refreshRowCache();
			}
		});

		warningLayout.addComponent(yesNo);
		popupWindow.setWidth(800, Sizeable.Unit.PIXELS);
		popupWindow.setClosable(false);
	}
}
