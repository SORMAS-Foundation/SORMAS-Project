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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.exposure.ExposureCategory;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
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

@SuppressWarnings({
	"java:S110", // suppress sonar too many parents warning
	"java:S2160" // suppress missing equals not relevant for Vaadin components
})
public class ExposuresField extends AbstractTableField<ExposureDto> {

	private static final String COLUMN_EXPOSURE_TYPE = ExposureDto.EXPOSURE_TYPE;
	private static final String COLUMN_EXPOSURE_CATEGORY = ExposureDto.EXPOSURE_CATEGORY;
	private static final String COLUMN_LOCATION_COUNTRY = "locationCountry";
	private static final String COLUMN_DATE = Captions.date;
	private static final String COLUMN_DESCRIPTION = ExposureDto.DESCRIPTION;
	private static final String COLUMN_PROPHYLAXIS_ADHERENCE = ExposureDto.PROPHYLAXIS_ADHERENCE;

	private final FieldVisibilityCheckers fieldVisibilityCheckers;
	private Supplier<List<ContactReferenceDto>> getSourceContactsCallback;
	private Class<? extends EntityDto> epiDataParentClass;
	private boolean isPseudonymized;
	private boolean isEditAllowed;
	private Disease disease;
	private final Map<String, Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto>> pendingCustomizableFieldValues = new HashMap<>();

	public ExposuresField(
		Disease disease,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		UiFieldAccessCheckers<?> fieldAccessCheckers,
		boolean isEditAllowed) {

		super(fieldAccessCheckers, isEditAllowed);
		this.fieldVisibilityCheckers = fieldVisibilityCheckers;
		this.isEditAllowed = isEditAllowed;
		this.disease = disease;
	}

	@Override
	protected void updateColumns() {
		Table table = getTable();

		addGeneratedColumns(table);

		if (epiDataParentClass == CaseDataDto.class) {
			table.setVisibleColumns(
				ACTION_COLUMN_ID,
				COLUMN_EXPOSURE_TYPE,
				COLUMN_LOCATION_COUNTRY,
				COLUMN_DATE,
				COLUMN_EXPOSURE_CATEGORY,
				COLUMN_DESCRIPTION);
		} else {
			table.setVisibleColumns(
				ACTION_COLUMN_ID,
				COLUMN_EXPOSURE_TYPE,
				COLUMN_LOCATION_COUNTRY,
				COLUMN_DATE,
				COLUMN_EXPOSURE_CATEGORY,
				COLUMN_DESCRIPTION);
		}
		table.setCellStyleGenerator(
			FieldAccessCellStyleGenerator.withFieldAccessCheckers(
				ExposureDto.class,
				UiFieldAccessCheckers.forSensitiveData(isPseudonymized, FacadeProvider.getConfigFacade().getCountryLocale())));

		for (Object columnId : table.getVisibleColumns()) {
			if (columnId.equals(ACTION_COLUMN_ID)) {
				table.setColumnHeader(columnId, "&nbsp");
			} else if (COLUMN_LOCATION_COUNTRY.equals(columnId)) {
				table.setColumnHeader(columnId, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.COUNTRY));
			} else {
				table.setColumnHeader(columnId, I18nProperties.getPrefixCaption(ExposureDto.I18N_PREFIX, (String) columnId));
			}
		}

		table.setColumnWidth(COLUMN_DATE, 150);
	}

	private void addGeneratedColumns(Table table) {

		table.addGeneratedColumn(COLUMN_LOCATION_COUNTRY, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;
			LocationDto location = exposure.getLocation();
			if (location == null || location.getCountry() == null) {
				return "";
			}
			return location.getCountry().getCaption();
		});

		table.addGeneratedColumn(COLUMN_DATE, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;
			return DateFormatHelper.buildPeriodString(exposure.getStartDate(), exposure.getEndDate());
		});

		table.addGeneratedColumn(COLUMN_DESCRIPTION, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;

			Label descriptionLabel = new Label(StringUtils.abbreviate(exposure.getDescription(), 75));
			if (StringUtils.isNotBlank(exposure.getDescription())) {
				descriptionLabel.setDescription(exposure.getDescription());
			}

			return descriptionLabel;
		});
		table.addGeneratedColumn(COLUMN_PROPHYLAXIS_ADHERENCE, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			ExposureDto exposure = (ExposureDto) itemId;
			return exposure.getProphylaxisAdherence() != null ? exposure.getProphylaxisAdherence().toString() : "";
		});
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

		List<CustomizableFieldMetadataDto> exposureMetadata =
			FacadeProvider.getCustomizableFieldMetadataFacade().getActiveFieldsForContext(CustomizableFieldContext.EXPOSURE);
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> exposureFieldValues = pendingCustomizableFieldValues.containsKey(entry.getUuid())
			? pendingCustomizableFieldValues.get(entry.getUuid())
			: FacadeProvider.getCustomizableFieldValueFacade().getValuesForEntity(entry.getUuid(), CustomizableFieldContext.EXPOSURE);

		ExposureForm exposureForm = new ExposureForm(
			create,
			epiDataParentClass,
			getSourceContactsCallback != null ? getSourceContactsCallback.get() : null,
			fieldVisibilityCheckers,
			fieldAccessCheckers,
			disease,
			exposureMetadata,
			exposureFieldValues);
		exposureForm.setValue(entry);

		final CommitDiscardWrapperComponent<ExposureForm> component =
			new CommitDiscardWrapperComponent<>(exposureForm, UiUtil.permitted(isEditAllowed, UserRight.CASE_EDIT), exposureForm.getFieldGroup());
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
					pendingCustomizableFieldValues.put(entry.getUuid(), exposureForm.collectCurrentFieldValues());
					commitCallback.accept(exposureForm.getValue());

					updateAddButtonVisibility(getValue().size());
				}
			});

			if (!create) {
				component.addDeleteListener(() -> {
					popupWindow.close();
					ExposuresField.this.removeEntry(entry);
					pendingCustomizableFieldValues.remove(entry.getUuid());

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
		if (disease == Disease.SYPHILIS) {
			// Direct contact is selected by default when creating a new exposure for Syphilis
			exposure.setExposureCategory(ExposureCategory.DIRECT_CONTACT);
		}
		return exposure;
	}

	@Override
	public void setPropertyDataSource(Property newDataSource) {
		super.setPropertyDataSource(newDataSource);

		if (getValue() != null) {
			updateAddButtonVisibility(getValue().size());
		}
	}

	public Map<String, Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto>> collectCustomizableFieldValues() {
		return Collections.unmodifiableMap(pendingCustomizableFieldValues);
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
