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

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldGroup;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldVisibilityContext;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.epidata.AnimalCondition;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.AnimalCategory;
import de.symeda.sormas.api.exposure.AnimalLocation;
import de.symeda.sormas.api.exposure.ExposureCategory;
import de.symeda.sormas.api.exposure.ExposureContactFactor;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureProtectiveMeasure;
import de.symeda.sormas.api.exposure.ExposureSetting;
import de.symeda.sormas.api.exposure.ExposureSubSetting;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.exposure.FomiteTransmissionLocation;
import de.symeda.sormas.api.exposure.ProphylaxisAdherence;
import de.symeda.sormas.api.exposure.SexualContact;
import de.symeda.sormas.api.exposure.TravelPurpose;
import de.symeda.sormas.api.exposure.TypeOfAnimal;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.form.FormSectionAccordion;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.components.CustomizableFieldsGroup;

public class ExposureForm extends AbstractEditForm<ExposureDto> {

	private static final long serialVersionUID = 8262753698264714832L;

	public static final String MAIN_ACCORDION_LOC = "mainAccordionLoc";
	private static final String LOC_CUSTOMIZABLE_FIELDS_EXPOSURE_DETAILS = CustomizableFieldGroup.EXPOSURE_DETAILS.getKey();
	private static final String LOC_CUSTOMIZABLE_FIELDS_EXPOSURES_GENERAL = CustomizableFieldGroup.EXPOSURES_GENERAL.getKey();
	private static final String LOC_CUSTOMIZABLE_FIELDS_LOCATION_GENERAL = CustomizableFieldGroup.LOCATION_GENERAL.getKey();
	private static final String LOC_EXPOSURES_HEADING = "locExposuresHeading";
	private static final String LOC_LOCATION_HEADING = "locLocationHeading";
	private static final String LOC_CONCLUSION_HEADING = "locConclusionHeading";

	public static final String MAIN_ACCORDION_LAYOUT = fluidRowLocs(MAIN_ACCORDION_LOC);

	private static final String UUID_REPORTING_USER = fluidRowLocs(ExposureDto.UUID, ExposureDto.REPORTING_USER);
	private static final String DEPRECATED_COMBOBOX_ITEM_STYLE = "deprecated-select-item";
	private static final String DEPRECATED_COMBOBOX_VALUE_STYLE = "deprecated-select-value";

	//@formatter:off
	private static final String GENERAL_DETAILS_LAYOUT =
			fluidRowLocs(ExposureDto.START_DATE, ExposureDto.END_DATE, "", "") +
					fluidRowLocs(ExposureDto.EXPOSURE_TYPE, ExposureDto.EXPOSURE_TYPE_DETAILS) +
					loc(ExposureDto.DESCRIPTION);

	private static final String EXPOSURE_DETAILS_LAYOUT =
			loc(LOC_CUSTOMIZABLE_FIELDS_EXPOSURE_DETAILS) +
					loc(LOC_EXPOSURES_HEADING) +
					fluidRowLocs(ExposureDto.EXPOSURE_CATEGORY, ExposureDto.EXPOSURE_SETTING, ExposureDto.EXPOSURE_SETTING_DETAILS) +
					fluidRow(
							fluidColumn(4, 0, locs(
									ExposureDto.SUB_SETTINGS,
									ExposureDto.SHOPPING_FOR_FOOD_DETAILS,
									ExposureDto.CONDITION_OF_ANIMAL,
									ExposureDto.ANIMAL_CATEGORY,
									ExposureDto.TYPE_OF_ANIMAL,
									ExposureDto.ANIMAL_LOCATION,
									ExposureDto.FOMITE_TRANSMISSION_LOCATION,
									ExposureDto.SEXUAL_CONTACT
							)),
							fluidColumn(4, 0, locs(
									ExposureDto.CONTACT_FACTORS
							)),
							fluidColumn(4, 0, locs(
									ExposureDto.PROTECTIVE_MEASURES
							))
					) +
					fluidRow(fluidColumn(4, 0, locs(ExposureDto.PROPHYLAXIS_ADHERENCE))) +
					fluidRow(fluidColumn(4, 0, locs(ExposureDto.PROPHYLAXIS_ADHERENCE_DETAILS))) +
					fluidRow(fluidColumn(4, 0, locs(ExposureDto.TRAVEL_PURPOSE))) +
					fluidRow(fluidColumn(4, 0, locs(ExposureDto.TRAVEL_PURPOSE_DETAILS))) +
					fluidRow(
							fluidColumn(4, 0, locs(
									ExposureDto.EXPOSURE_SUB_SETTING_DETAILS,
											ExposureDto.ANIMAL_CATEGORY_DETAILS,
											ExposureDto.ANIMAL_LOCATION_TEXT
							)),
							fluidColumn(4, 0, locs(
									ExposureDto.CONTACT_FACTOR_DETAILS
							)),
							fluidColumn(4, 0, locs(
									ExposureDto.PROTECTIVE_MEASURE_DETAILS
							))
					) +
					loc(LOC_CUSTOMIZABLE_FIELDS_EXPOSURES_GENERAL);

	private static final String LOCATION_DETAILS_LAYOUT =
			loc(LOC_LOCATION_HEADING) +
			fluidRow(
					fluidColumn(6, 0, locs(ExposureDto.TYPE_OF_PLACE)),
					fluidColumn(6, 0, locs(
							ExposureDto.TYPE_OF_PLACE_DETAILS,
							ExposureDto.MEANS_OF_TRANSPORT,
							ExposureDto.WORK_ENVIRONMENT
					))
			) +
			loc(ExposureDto.MEANS_OF_TRANSPORT_DETAILS) +
			fluidRowLocs(ExposureDto.CONNECTION_NUMBER, ExposureDto.SEAT_NUMBER) +
			loc(ExposureDto.LOCATION)+
			loc(LOC_CUSTOMIZABLE_FIELDS_LOCATION_GENERAL);
	//@formatter:on

	private final Class<? extends EntityDto> epiDataParentClass;
	private final List<ContactReferenceDto> sourceContacts;

	private CustomLayout generalDetailsLayout;
	private CustomLayout exposureDetailsLayout;
	private CustomLayout locationDetailsLayout;

	private Label exposuresHeading;
	private Label locationHeading;
	private Label conclusionHeading;

	private LocationEditForm locationForm;
	private Disease disease;

	private ComboBox exposureTypeField;

	private ComboBox categoryField;
	private ComboBox settingField;
	private TextField settingDetailsField;
	private OptionGroup subSettingsField;
	private TextField subSettingsDetailsField;
	private OptionGroup contactFactorsField;
	private TextField contactFactorDetailsField;
	private OptionGroup protectiveMeasuresField;
	private TextField protectiveMeasureDetailsField;
	private NullableOptionGroup conditionOfAnimalField;
	private NullableOptionGroup animalCategoryField;
	private ComboBox typeOfAnimalField;
	private TextField animalCategoryDetailsField;
	private NullableOptionGroup animalLocationField;
	private TextField animalLocationDetailsField;
	private NullableOptionGroup fomiteTransmissionLocationField;
	private ComboBox prophylaxisAdherenceField;
	private ComboBox travelPurposeField;
	private TextField prophylaxisAdherenceDetailsField;
	private TextField travelPurposeDetailsField;
	private NullableOptionGroup sexualContactField;

	private CustomizableFieldsGroup exposureDetailsPanel;
	private CustomizableFieldsGroup exposuresGeneralPanel;
	private CustomizableFieldsGroup locationGeneralPanel;
	private boolean updatingCategoryFieldItems;
	private boolean updatingSettingFieldItems;

	public ExposureForm(
		boolean create,
		Class<? extends EntityDto> epiDataParentClass,
		List<ContactReferenceDto> sourceContacts,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		UiFieldAccessCheckers<?> fieldAccessCheckers,
		Disease disease,
		List<CustomizableFieldMetadataDto> customizableFieldsMetadata,
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> customizableFieldsValues) {
		super(ExposureDto.class, ExposureDto.I18N_PREFIX, false, fieldVisibilityCheckers, fieldAccessCheckers);

		setWidth(960, Unit.PIXELS);

		this.sourceContacts = sourceContacts;
		this.epiDataParentClass = epiDataParentClass;
		this.disease = disease;

		setCustomizableFieldsMetadata(customizableFieldsMetadata);
		setCustomizableFieldsValues(customizableFieldsValues);

		if (create) {
			hideValidationUntilNextCommit();
		}

		addFields();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {

		FormSectionAccordion accordion = new FormSectionAccordion();

		generalDetailsLayout = new CustomLayout();
		generalDetailsLayout.setTemplateContents(GENERAL_DETAILS_LAYOUT);

		exposureDetailsLayout = new CustomLayout();
		exposureDetailsLayout.setTemplateContents(EXPOSURE_DETAILS_LAYOUT);

		locationDetailsLayout = new CustomLayout();
		locationDetailsLayout.setTemplateContents(LOCATION_DETAILS_LAYOUT);

		addHeadingsAndInfoTexts();

		exposureDetailsPanel = new CustomizableFieldsGroup(CustomizableFieldGroup.EXPOSURE_DETAILS);
		exposureDetailsPanel.setVisibilityContext(new CustomizableFieldVisibilityContext().withDisease(disease));
		exposureDetailsPanel.setFieldsMetadata(getCustomizableFieldsMetadata());
		exposureDetailsPanel.setFieldsValues(getCustomizableFieldsValues());
		exposureDetailsPanel.updateFieldsDisplay();
		exposureDetailsLayout.addComponent(exposureDetailsPanel, LOC_CUSTOMIZABLE_FIELDS_EXPOSURE_DETAILS);

		addBasicFields();

		exposuresGeneralPanel = new CustomizableFieldsGroup(CustomizableFieldGroup.EXPOSURES_GENERAL);
		exposuresGeneralPanel.setVisibilityContext(new CustomizableFieldVisibilityContext().withDisease(disease));
		exposuresGeneralPanel.setFieldsMetadata(getCustomizableFieldsMetadata());
		exposuresGeneralPanel.setFieldsValues(getCustomizableFieldsValues());
		exposuresGeneralPanel.updateFieldsDisplay();
		exposureDetailsLayout.addComponent(exposuresGeneralPanel, LOC_CUSTOMIZABLE_FIELDS_EXPOSURES_GENERAL);

		locationForm = addField(locationDetailsLayout, ExposureDto.LOCATION, LocationEditForm.class);
		locationForm.setCaption(null);
		addField(locationDetailsLayout, ExposureDto.CONNECTION_NUMBER, TextField.class);

		locationGeneralPanel = new CustomizableFieldsGroup(CustomizableFieldGroup.LOCATION_GENERAL);
		locationGeneralPanel.setVisibilityContext(new CustomizableFieldVisibilityContext().withDisease(disease));
		locationGeneralPanel.setFieldsMetadata(getCustomizableFieldsMetadata());
		locationGeneralPanel.setFieldsValues(getCustomizableFieldsValues());
		locationGeneralPanel.updateFieldsDisplay();
		locationDetailsLayout.addComponent(locationGeneralPanel, LOC_CUSTOMIZABLE_FIELDS_LOCATION_GENERAL);
		getField(ExposureDto.MEANS_OF_TRANSPORT).addValueChangeListener(e -> {
			if (e.getProperty().getValue() == MeansOfTransport.PLANE) {
				getField(ExposureDto.CONNECTION_NUMBER).setCaption(I18nProperties.getCaption(Captions.exposureFlightNumber));
			} else {
				getField(ExposureDto.CONNECTION_NUMBER)
					.setCaption(I18nProperties.getPrefixCaption(ExposureDto.I18N_PREFIX, ExposureDto.CONNECTION_NUMBER));
			}
		});

		accordion.addFormSectionPanel(Captions.titleExposuresGeneralSection, true, generalDetailsLayout);
		accordion.addFormSectionPanel(Captions.titleExposuresSection, false, exposureDetailsLayout);
		accordion.addFormSectionPanel(Captions.titleExposureLocationSection, false, locationDetailsLayout);

		getContent().addComponent(accordion, MAIN_ACCORDION_LOC);

		setUpVisibilityDependencies();

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		setUpRequirements();

		setReadOnly(true, ExposureDto.UUID, ExposureDto.REPORTING_USER);
	}

	private void addHeadingsAndInfoTexts() {

		exposuresHeading = new Label(h3(I18nProperties.getString(Strings.headingExposures)), ContentMode.HTML);
		exposureDetailsLayout.addComponent(exposuresHeading, LOC_EXPOSURES_HEADING);

		locationHeading = new Label(h3(I18nProperties.getPrefixCaption(ExposureDto.I18N_PREFIX, ExposureDto.LOCATION)), ContentMode.HTML);
		locationDetailsLayout.addComponent(locationHeading, LOC_LOCATION_HEADING);

		conclusionHeading = new Label(h3(I18nProperties.getString(Strings.headingEpiConclusion)), ContentMode.HTML);
		getContent().addComponent(conclusionHeading, LOC_CONCLUSION_HEADING);
	}

	private void addBasicFields() {
		addFields(ExposureDto.UUID, ExposureDto.REPORTING_USER, ExposureDto.PROBABLE_INFECTION_ENVIRONMENT);

		DateField startDate = addField(generalDetailsLayout, ExposureDto.START_DATE, DateField.class);
		DateField endDate = addField(generalDetailsLayout, ExposureDto.END_DATE, DateField.class);

		DateComparisonValidator.addStartEndValidators(startDate, endDate, false);

		exposureTypeField = addField(generalDetailsLayout, ExposureDto.EXPOSURE_TYPE, ComboBox.class);
		exposureTypeField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);

		addField(generalDetailsLayout, ExposureDto.EXPOSURE_TYPE_DETAILS, TextField.class);
		addField(generalDetailsLayout, ExposureDto.DESCRIPTION, TextArea.class).setRows(5);

		categoryField = addField(exposureDetailsLayout, ExposureDto.EXPOSURE_CATEGORY, ComboBox.class);
		categoryField.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
		categoryField.setItemStyleGenerator((source, item) -> {
			if (item instanceof ExposureCategory && ((ExposureCategory) item).isDeprecated()) {
				return DEPRECATED_COMBOBOX_ITEM_STYLE;
			}
			return null;
		});

		settingField = addField(exposureDetailsLayout, ExposureDto.EXPOSURE_SETTING, ComboBox.class);
		settingField.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
		settingField.setItemStyleGenerator((source, item) -> {
			if (item instanceof ExposureSetting && ((ExposureSetting) item).isDeprecated()) {
				return DEPRECATED_COMBOBOX_ITEM_STYLE;
			}
			return null;
		});

		settingDetailsField = addField(exposureDetailsLayout, ExposureDto.EXPOSURE_SETTING_DETAILS, TextField.class);
		settingDetailsField.setVisible(false);

		subSettingsField = addField(exposureDetailsLayout, ExposureDto.SUB_SETTINGS, OptionGroup.class);
		subSettingsField.setMultiSelect(true);
		subSettingsField.setHtmlContentAllowed(true);
		CssStyles.style(subSettingsField, CssStyles.CAPTION_ON_TOP);

		subSettingsDetailsField = addField(exposureDetailsLayout, ExposureDto.EXPOSURE_SUB_SETTING_DETAILS, TextField.class);
		subSettingsDetailsField.setVisible(false);

		TextField shoppingForFoodDetailsField = addField(exposureDetailsLayout, ExposureDto.SHOPPING_FOR_FOOD_DETAILS, TextField.class);
		shoppingForFoodDetailsField.setVisible(false);

		conditionOfAnimalField = addField(exposureDetailsLayout, ExposureDto.CONDITION_OF_ANIMAL, NullableOptionGroup.class);
		conditionOfAnimalField.setVisible(false);

		animalCategoryField = addField(exposureDetailsLayout, ExposureDto.ANIMAL_CATEGORY, NullableOptionGroup.class);
		animalCategoryField.setVisible(false);

		typeOfAnimalField = addField(exposureDetailsLayout, ExposureDto.TYPE_OF_ANIMAL, ComboBox.class);
		typeOfAnimalField.setVisible(false);

		animalCategoryDetailsField = addField(exposureDetailsLayout, ExposureDto.ANIMAL_CATEGORY_DETAILS, TextField.class);
		animalCategoryDetailsField.setVisible(false);

		animalLocationField = addField(exposureDetailsLayout, ExposureDto.ANIMAL_LOCATION, NullableOptionGroup.class);
		animalLocationField.setVisible(false);

		animalLocationDetailsField = addField(exposureDetailsLayout, ExposureDto.ANIMAL_LOCATION_TEXT, TextField.class);
		animalLocationDetailsField.setVisible(false);

		fomiteTransmissionLocationField = addField(exposureDetailsLayout, ExposureDto.FOMITE_TRANSMISSION_LOCATION, NullableOptionGroup.class);
		fomiteTransmissionLocationField.setVisible(false);

		contactFactorsField = addField(exposureDetailsLayout, ExposureDto.CONTACT_FACTORS, OptionGroup.class);
		contactFactorsField.setMultiSelect(true);
		contactFactorsField.setHtmlContentAllowed(true);
		CssStyles.style(contactFactorsField, CssStyles.CAPTION_ON_TOP);

		contactFactorDetailsField = addField(exposureDetailsLayout, ExposureDto.CONTACT_FACTOR_DETAILS, TextField.class);
		contactFactorDetailsField.setVisible(false);

		protectiveMeasuresField = addField(exposureDetailsLayout, ExposureDto.PROTECTIVE_MEASURES, OptionGroup.class);
		protectiveMeasuresField.setMultiSelect(true);
		protectiveMeasuresField.setHtmlContentAllowed(true);
		CssStyles.style(protectiveMeasuresField, CssStyles.CAPTION_ON_TOP);

		protectiveMeasureDetailsField = addField(exposureDetailsLayout, ExposureDto.PROTECTIVE_MEASURE_DETAILS, TextField.class);
		protectiveMeasureDetailsField.setVisible(false);

		prophylaxisAdherenceField = addField(exposureDetailsLayout, ExposureDto.PROPHYLAXIS_ADHERENCE, ComboBox.class);
		prophylaxisAdherenceField.setVisible(false);
		prophylaxisAdherenceDetailsField = addField(exposureDetailsLayout, ExposureDto.PROPHYLAXIS_ADHERENCE_DETAILS, TextField.class);
		travelPurposeField = addField(exposureDetailsLayout, ExposureDto.TRAVEL_PURPOSE, ComboBox.class);
		travelPurposeField.setVisible(false);
		travelPurposeDetailsField = addField(exposureDetailsLayout, ExposureDto.TRAVEL_PURPOSE_DETAILS, TextField.class);
		sexualContactField = addField(exposureDetailsLayout, ExposureDto.SEXUAL_CONTACT, NullableOptionGroup.class);
		sexualContactField.setVisible(false);

		categoryField.addValueChangeListener(e -> {
			if (updatingCategoryFieldItems) {
				return;
			}

			ExposureCategory selectedCategory = (ExposureCategory) e.getProperty().getValue();
			refreshCategoryFieldItems(selectedCategory);
			updateSettingFieldItems(selectedCategory);

			// Also update subSettings when category changes (setting will be null/cleared)
			updateSubSettingsFieldItems(selectedCategory, (ExposureSetting) settingField.getValue());

			// Update contact factors and protective measures when category changes
			updateContactFactorsFieldItems(selectedCategory, (ExposureSetting) settingField.getValue());
			updateProtectiveMeasuresFieldItems(selectedCategory, (ExposureSetting) settingField.getValue());

			// Update animal contact fields based on category
			updateAnimalContactFields(selectedCategory);

			// Update fomite transmission field based on category
			updateFomiteTransmissionField(selectedCategory);

			updateSexualContactVisibility(selectedCategory, getSelectedSubSettings());
			refreshSettingFieldItems(selectedCategory, (ExposureSetting) settingField.getValue());
		});

		settingField.addValueChangeListener(e -> {
			if (updatingSettingFieldItems) {
				return;
			}

			ExposureSetting selectedSetting = (ExposureSetting) e.getProperty().getValue();

			// 1. Show/hide settingDetailsField if setting is OTHER
			settingDetailsField.setVisible(selectedSetting == ExposureSetting.OTHER);

			// 2. Update subSettings based on category and setting
			ExposureCategory selectedCategory = (ExposureCategory) categoryField.getValue();
			updateSubSettingsFieldItems(selectedCategory, selectedSetting);

			// 3. Update contact factors and protective measures based on category and setting
			updateContactFactorsFieldItems(selectedCategory, selectedSetting);
			updateProtectiveMeasuresFieldItems(selectedCategory, selectedSetting);

			updateSexualContactVisibility(selectedCategory, getSelectedSubSettings());
			refreshSettingFieldItems(selectedCategory, selectedSetting);
		});

		subSettingsField.addValueChangeListener(e -> {
			// 3. Show/hide subSettingsDetailsField if subSettings contains OTHER
			@SuppressWarnings("unchecked")
			Set<ExposureSubSetting> selectedSubSettings = (Set<ExposureSubSetting>) e.getProperty().getValue();
			boolean containsOther = selectedSubSettings != null && selectedSubSettings.contains(ExposureSubSetting.OTHER);
			subSettingsDetailsField.setVisible(containsOther);
			// FIXME: Replace legacy TRAVELED_ABROAD dependency with a non-deprecated travel signal once taxonomy migration is complete.
			// prophylaxis is allowed only for Malaria abroad travelers, not all diseases
			boolean hasTravelledAbroad = selectedSubSettings != null && selectedSubSettings.contains(ExposureSubSetting.TRAVELED_ABROAD);
			// FIXME: Prophylaxis/travel-purpose visibility still depends on TRAVELED_ABROAD to preserve backwards compatibility.
			setVisibleClear(hasTravelledAbroad && disease == Disease.MALARIA, ExposureDto.PROPHYLAXIS_ADHERENCE);

			// Travel purpose is visible to aboard travelers of Malaria and Dengue
			setVisibleClear(hasTravelledAbroad && (disease == Disease.MALARIA || disease == Disease.DENGUE), ExposureDto.TRAVEL_PURPOSE);
			updateSexualContactVisibility((ExposureCategory) categoryField.getValue(), selectedSubSettings);

			// Salmonellosis: shopping-for-food details follows sub-setting selection.
			// Disease-gated so non-SAL exposures with FOOD_BORNE category don't see it.
			// FIXME: handle shopping for food after rework
			boolean isSalmonellosis = disease == Disease.SALMONELLOSIS;
			boolean showShoppingForFood =
				isSalmonellosis && selectedSubSettings != null && selectedSubSettings.contains(ExposureSubSetting.SHOPPING_FOR_FOOD);
			shoppingForFoodDetailsField.setVisible(showShoppingForFood);
			applyDeprecatedOptionGroupPolicy(subSettingsField, selectedSubSettings, ExposureSubSetting::isDeprecated);
		});

		contactFactorsField.addValueChangeListener(e -> {
			@SuppressWarnings("unchecked")
			Set<ExposureContactFactor> selectedContactFactors = (Set<ExposureContactFactor>) e.getProperty().getValue();
			boolean containsOther = selectedContactFactors != null && selectedContactFactors.contains(ExposureContactFactor.OTHER);
			contactFactorDetailsField.setVisible(containsOther);
			applyDeprecatedOptionGroupPolicy(contactFactorsField, selectedContactFactors, ExposureContactFactor::isDeprecated);
		});

		protectiveMeasuresField.addValueChangeListener(e -> {
			@SuppressWarnings("unchecked")
			Set<ExposureProtectiveMeasure> selectedProtectiveMeasures = (Set<ExposureProtectiveMeasure>) e.getProperty().getValue();
			boolean containsOther = selectedProtectiveMeasures != null && selectedProtectiveMeasures.contains(ExposureProtectiveMeasure.OTHER);
			protectiveMeasureDetailsField.setVisible(containsOther);
			applyDeprecatedOptionGroupPolicy(protectiveMeasuresField, selectedProtectiveMeasures, ExposureProtectiveMeasure::isDeprecated);
		});

		conditionOfAnimalField.addValueChangeListener(e -> {
			Object value = e.getProperty().getValue();
			boolean hasValue = value != null;

			// Show/hide and set required for animalCategoryField
			animalCategoryField.setVisible(hasValue);
			animalCategoryField.setRequired(hasValue);

			// Clear dependent fields when conditionOfAnimal becomes null
			if (!hasValue) {
				animalCategoryField.setValue(null);

				typeOfAnimalField.setValue(null);
				typeOfAnimalField.setVisible(false);
				animalCategoryDetailsField.setValue(null);
				animalCategoryDetailsField.setVisible(false);
			}
		});

		animalCategoryField.addValueChangeListener(e -> {
			Object value = e.getProperty().getValue();
			boolean hasValue = value != null;

			// Show/hide typeOfAnimal, animalCategoryDetailsField
			typeOfAnimalField.setVisible(hasValue);
			animalCategoryDetailsField.setVisible(hasValue);

			// Clear details field when animalCategory becomes null
			if (!hasValue) {
				typeOfAnimalField.setValue(null);
				animalCategoryDetailsField.setValue(null);
			}
		});

		animalLocationField.addValueChangeListener(e -> {
			Object value = e.getProperty().getValue();
			boolean showDetails = value == AnimalLocation.OTHER;
			animalLocationDetailsField.setVisible(showDetails);
			if (!showDetails) {
				animalLocationDetailsField.setValue(null);
			}
		});

		addField(locationDetailsLayout, ExposureDto.TYPE_OF_PLACE, ComboBox.class);
		addField(locationDetailsLayout, ExposureDto.TYPE_OF_PLACE_DETAILS, TextField.class);

		addField(locationDetailsLayout, ExposureDto.MEANS_OF_TRANSPORT, ComboBox.class);
		addField(locationDetailsLayout, ExposureDto.MEANS_OF_TRANSPORT_DETAILS, TextField.class);

		addField(locationDetailsLayout, ExposureDto.SEAT_NUMBER, TextField.class);

		addField(locationDetailsLayout, ExposureDto.WORK_ENVIRONMENT, ComboBox.class);
	}

	private void setUpVisibilityDependencies() {
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.EXPOSURE_TYPE_DETAILS, ExposureDto.EXPOSURE_TYPE, ExposureType.OTHER, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.TYPE_OF_PLACE_DETAILS, ExposureDto.TYPE_OF_PLACE, TypeOfPlace.OTHER, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(ExposureDto.MEANS_OF_TRANSPORT, ExposureDto.CONNECTION_NUMBER),
			ExposureDto.TYPE_OF_PLACE,
			TypeOfPlace.MEANS_OF_TRANSPORT,
			true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), ExposureDto.MEANS_OF_TRANSPORT_DETAILS, ExposureDto.MEANS_OF_TRANSPORT, MeansOfTransport.OTHER, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ExposureDto.SEAT_NUMBER,
			ExposureDto.MEANS_OF_TRANSPORT,
			Arrays.asList(MeansOfTransport.PLANE, MeansOfTransport.TRAIN, MeansOfTransport.OTHER),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ExposureDto.WORK_ENVIRONMENT,
			locationForm.getFacilityTypeGroup(),
			Collections.singletonList(FacilityTypeGroup.WORKING_PLACE),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ExposureDto.PROPHYLAXIS_ADHERENCE_DETAILS,
			ExposureDto.PROPHYLAXIS_ADHERENCE,
			ProphylaxisAdherence.OTHER,
			true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.TRAVEL_PURPOSE_DETAILS, ExposureDto.TRAVEL_PURPOSE, TravelPurpose.OTHER, true);
		conclusionHeading.setVisible(List.of(Disease.GIARDIASIS, Disease.CRYPTOSPORIDIOSIS).contains(disease));
		locationForm.setFacilityFieldsVisible(getField(ExposureDto.TYPE_OF_PLACE).getValue() == TypeOfPlace.FACILITY, true);
		getField(ExposureDto.TYPE_OF_PLACE)
			.addValueChangeListener(e -> locationForm.setFacilityFieldsVisible(e.getProperty().getValue() == TypeOfPlace.FACILITY, true));
		locationForm.setContinentFieldsVisibility();
	}

	private void setUpRequirements() {
		setRequired(true, ExposureDto.EXPOSURE_TYPE);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			ExposureDto.EXPOSURE_TYPE,
			Collections.singletonList(ExposureDto.EXPOSURE_TYPE_DETAILS),
			Collections.singletonList(ExposureType.OTHER));
	}

	private void updateSettingFieldItems(ExposureCategory category) {
		updateSettingFieldItems(category, null);
	}

	private void updateSettingFieldItems(ExposureCategory category, ExposureSetting currentSetting) {
		updatingSettingFieldItems = true;
		try {
			List<ExposureSetting> settings = new ArrayList<>(ExposureSetting.getValues(category, false, disease));
			addIfMissing(settings, currentSetting);
			FieldHelper.updateItems(settingField, settings);

			// if the disease is Malaria or Dengue and the category is VECTOR_BORNE, preselect MOSQUITO_BORNE as setting (since it's the only valid option in this case)
			boolean isVectorBorneAutoSetting =
				Stream.of(Disease.MALARIA, Disease.DENGUE).anyMatch(d -> d == disease) && category == ExposureCategory.VECTOR_BORNE;
			// Sexually transmitted infections: Direct contact defaults to Person to person, but remains editable (other settings are available)
			boolean isSexuallyTransmittedInfectionDirectContact =
				(disease == Disease.SYPHILIS || disease == Disease.GONOCOCCAL_INFECTION) && category == ExposureCategory.DIRECT_CONTACT;

			// FIXME - address the auto selection based on the new exposure values
			ExposureSetting defaultSetting;
			if (isVectorBorneAutoSetting) {
				defaultSetting = ExposureSetting.MOSQUITO_BORNE;
			} else if (isSexuallyTransmittedInfectionDirectContact) {
				defaultSetting = ExposureSetting.PERSON_TO_PERSON;
			} else {
				defaultSetting = null;
			}

			ExposureSetting valueToSet = currentSetting != null && settings.contains(currentSetting) ? currentSetting : defaultSetting;
			settingField.setValue(valueToSet);
			settingField.setEnabled(true);

			settingDetailsField.setValue(null);
			settingDetailsField.setVisible(false);
			applyDeprecatedSettingPolicy(valueToSet);

			if (category == null || category.hasNoSetting()) {
				settingField.setVisible(false);
				settingField.setRequired(false);
			} else {
				settingField.setVisible(true);
				settingField.setRequired(true);
			}
		} finally {
			updatingSettingFieldItems = false;
		}
	}

	private void updateSubSettingsFieldItems(ExposureCategory category, ExposureSetting setting) {
		updateSubSettingsFieldItems(category, setting, null);
	}

	private void updateSubSettingsFieldItems(ExposureCategory category, ExposureSetting setting, Set<ExposureSubSetting> currentSubSettings) {
		List<ExposureSubSetting> subSettings;

		// For categories that have no setting but do have subsettings (e.g., FOOD_BORNE),
		// get values based only on category.
		if (category != null && category.hasNoSetting()) {
			subSettings = ExposureSubSetting.getValuesForCategoryOnly(category, false, disease);
		} else {
			subSettings = ExposureSubSetting.getValues(category, setting, false, disease);
		}
		addAllIfMissing(subSettings, currentSubSettings);

		FieldHelper.updateItems(subSettingsField, subSettings);
		applyDeprecatedOptionGroupPolicy(subSettingsField, currentSubSettings, ExposureSubSetting::isDeprecated);

		if (currentSubSettings == null || currentSubSettings.isEmpty()) {
			subSettingsField.setValue(null);
		} else {
			Set<ExposureSubSetting> selectedSubSettings =
				currentSubSettings.stream().filter(subSettings::contains).collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
			subSettingsField.setValue(selectedSubSettings);
		}

		// Clear the dependent details field; it is restored by caller when needed.
		subSettingsDetailsField.setValue(null);
		subSettingsDetailsField.setVisible(false);

		// Hide subSettings field if no options available
		subSettingsField.setVisible(!subSettings.isEmpty());
	}

	private void updateContactFactorsFieldItems(ExposureCategory category, ExposureSetting setting) {
		updateContactFactorsFieldItems(category, setting, null);
	}

	private void updateContactFactorsFieldItems(
		ExposureCategory category,
		ExposureSetting setting,
		Set<ExposureContactFactor> currentContactFactors) {
		List<ExposureContactFactor> contactFactors = new ArrayList<>(ExposureContactFactor.getValues(category, setting, false, disease));
		addAllIfMissing(contactFactors, currentContactFactors);
		FieldHelper.updateItems(contactFactorsField, contactFactors);
		applyDeprecatedOptionGroupPolicy(contactFactorsField, currentContactFactors, ExposureContactFactor::isDeprecated);

		if (currentContactFactors == null || currentContactFactors.isEmpty()) {
			contactFactorsField.setValue(null);
		} else {
			Set<ExposureContactFactor> selectedContactFactors =
				currentContactFactors.stream().filter(contactFactors::contains).collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
			contactFactorsField.setValue(selectedContactFactors);
		}

		// Clear the dependent details field; it is restored by caller when needed.
		contactFactorDetailsField.setValue(null);
		contactFactorDetailsField.setVisible(false);

		// Hide contactFactors field if no options available
		contactFactorsField.setVisible(!contactFactors.isEmpty());
	}

	private void updateProtectiveMeasuresFieldItems(ExposureCategory category, ExposureSetting setting) {
		updateProtectiveMeasuresFieldItems(category, setting, null);
	}

	private void updateProtectiveMeasuresFieldItems(
		ExposureCategory category,
		ExposureSetting setting,
		Set<ExposureProtectiveMeasure> currentProtectiveMeasures) {
		List<ExposureProtectiveMeasure> protectiveMeasures = new ArrayList<>(ExposureProtectiveMeasure.getValues(category, setting, false, disease));
		addAllIfMissing(protectiveMeasures, currentProtectiveMeasures);
		FieldHelper.updateItems(protectiveMeasuresField, protectiveMeasures);
		applyDeprecatedOptionGroupPolicy(protectiveMeasuresField, currentProtectiveMeasures, ExposureProtectiveMeasure::isDeprecated);

		if (currentProtectiveMeasures == null || currentProtectiveMeasures.isEmpty()) {
			protectiveMeasuresField.setValue(null);
		} else {
			Set<ExposureProtectiveMeasure> selectedProtectiveMeasures = currentProtectiveMeasures.stream()
				.filter(protectiveMeasures::contains)
				.collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
			protectiveMeasuresField.setValue(selectedProtectiveMeasures);
		}

		// Clear the dependent details field; it is restored by caller when needed.
		protectiveMeasureDetailsField.setValue(null);
		protectiveMeasureDetailsField.setVisible(false);

		// Hide protectiveMeasures field if no options available
		protectiveMeasuresField.setVisible(!protectiveMeasures.isEmpty());
	}

	@SuppressWarnings("unchecked")
	private Set<ExposureSubSetting> getSelectedSubSettings() {
		return (Set<ExposureSubSetting>) subSettingsField.getValue();
	}

	private void updateSexualContactVisibility(ExposureCategory category, Set<ExposureSubSetting> selectedSubSettings) {
		boolean isLegacySexualActivity = selectedSubSettings != null && selectedSubSettings.contains(ExposureSubSetting.SEXUAL_ACTIVITY);
		boolean isSexualCategory = category == ExposureCategory.SEXUAL;
		setVisibleClear(isSexualCategory || isLegacySexualActivity, ExposureDto.SEXUAL_CONTACT);
	}

	private void refreshCategoryFieldItems(ExposureCategory selectedCategory) {
		updatingCategoryFieldItems = true;
		try {
			FieldHelper.updateItems(categoryField, buildExposureCategoryItems(selectedCategory));
			applyDeprecatedCategoryPolicy(selectedCategory);
		} finally {
			updatingCategoryFieldItems = false;
		}
	}

	private void refreshSettingFieldItems(ExposureCategory category, ExposureSetting selectedSetting) {
		updatingSettingFieldItems = true;
		try {
			List<ExposureSetting> settings = new ArrayList<>(ExposureSetting.getValues(category, false, disease));
			addIfMissing(settings, selectedSetting);
			FieldHelper.updateItems(settingField, settings);
			applyDeprecatedSettingPolicy(selectedSetting);
		} finally {
			updatingSettingFieldItems = false;
		}
	}

	private void applyDeprecatedCategoryPolicy(ExposureCategory selectedCategory) {
		applyDeprecatedSingleSelectPolicy(categoryField, selectedCategory, ExposureCategory::isDeprecated);
	}

	private void applyDeprecatedSettingPolicy(ExposureSetting selectedSetting) {
		applyDeprecatedSingleSelectPolicy(settingField, selectedSetting, ExposureSetting::isDeprecated);
	}

	private <T> void applyDeprecatedSingleSelectPolicy(ComboBox field, T selectedValue, Predicate<T> deprecatedChecker) {
		boolean hasDeprecatedItem = false;
		boolean hasDeprecatedSelectedValue = selectedValue != null && deprecatedChecker.test(selectedValue);
		List<Object> itemIds = new ArrayList<>(field.getItemIds());
		for (Object itemId : itemIds) {
			@SuppressWarnings("unchecked")
			T item = (T) itemId;
			boolean deprecated = deprecatedChecker.test(item);
			if (deprecated && !item.equals(selectedValue)) {
				field.removeItem(item);
				continue;
			}
			field.setItemCaption(item, String.valueOf(item));
			hasDeprecatedItem = hasDeprecatedItem || deprecated;
		}
		field.setDescription(hasDeprecatedItem ? I18nProperties.getString(Strings.deprecatedValueRemovalOnly) : null);
		if (hasDeprecatedSelectedValue) {
			field.addStyleName(DEPRECATED_COMBOBOX_VALUE_STYLE);
		} else {
			field.removeStyleName(DEPRECATED_COMBOBOX_VALUE_STYLE);
		}
	}

	private <T> void applyDeprecatedOptionGroupPolicy(OptionGroup field, Set<T> selectedValues, Predicate<T> deprecatedChecker) {
		Set<T> selected = selectedValues != null ? selectedValues : Collections.emptySet();
		List<Object> itemIds = new ArrayList<>(field.getItemIds());
		for (Object itemId : itemIds) {
			@SuppressWarnings("unchecked")
			T item = (T) itemId;
			boolean deprecated = deprecatedChecker.test(item);
			if (deprecated && !selected.contains(item)) {
				field.removeItem(item);
				continue;
			}
			field.setItemCaption(item, formatDeprecatedCaption(item, deprecated));
		}
	}

	private static String formatDeprecatedCaption(Object item, boolean deprecated) {
		String escapedCaption = escapeHtml(String.valueOf(item));
		if (!deprecated) {
			return escapedCaption;
		}

		return "<span style=\"text-decoration: line-through;\" title=\""
			+ escapeHtmlAttribute(I18nProperties.getString(Strings.deprecatedValueRemovalOnly)) + "\">" + escapedCaption + "</span>";
	}

	private static String escapeHtml(String value) {
		if (value == null) {
			return "";
		}

		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	private static String escapeHtmlAttribute(String value) {
		return escapeHtml(value).replace("\"", "&quot;").replace("'", "&#39;");
	}

	private static <T> void addIfMissing(List<T> items, T value) {
		if (value != null && !items.contains(value)) {
			items.add(value);
		}
	}

	private static <T> void addAllIfMissing(List<T> items, Set<T> values) {
		if (values == null || values.isEmpty()) {
			return;
		}
		values.stream().filter(v -> !items.contains(v)).forEach(items::add);
	}

	private void updateAnimalContactFields(ExposureCategory category) {
		boolean isAnimalContact = category == ExposureCategory.ANIMAL_CONTACT;

		// Show/hide and set required for conditionOfAnimalField
		conditionOfAnimalField.setVisible(isAnimalContact);
		conditionOfAnimalField.setRequired(isAnimalContact);
		animalLocationField.setVisible(isAnimalContact);
		animalLocationField.setRequired(isAnimalContact);
		if (!isAnimalContact) {
			animalLocationDetailsField.setVisible(false);
		}

		// Clear all animal contact related fields when category is not ANIMAL_CONTACT
		if (!isAnimalContact) {
			conditionOfAnimalField.setValue(null);
			animalCategoryField.setValue(null);
			animalCategoryField.setVisible(false);
			animalCategoryField.setRequired(false);

			typeOfAnimalField.setValue(null);
			typeOfAnimalField.setVisible(false);
			animalCategoryDetailsField.setValue(null);
			animalCategoryDetailsField.setVisible(false);
			animalLocationField.setValue(null);
			animalLocationDetailsField.setValue(null);
		}
	}

	private void updateFomiteTransmissionField(ExposureCategory category) {
		boolean isFomiteTransmission = category == ExposureCategory.FOMITE_TRANSMISSION;

		// Show/hide and set required for fomiteTransmissionLocationField
		fomiteTransmissionLocationField.setVisible(isFomiteTransmission);
		fomiteTransmissionLocationField.setRequired(isFomiteTransmission);

		// Clear field when category is not FOMITE_TRANSMISSION
		if (!isFomiteTransmission) {
			fomiteTransmissionLocationField.setValue(null);
		}
	}

	@Override
	public void setValue(ExposureDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		populateExposureTypes(newFieldValue);
		populateExposureCategories(newFieldValue);

		if (newFieldValue != null) {
			ExposureCategory category = newFieldValue.getExposureCategory();
			ExposureSetting setting = newFieldValue.getExposureSetting();

			// Store ALL the original values before updating field items (which clear values)
			String settingDetails = newFieldValue.getExposureSettingDetails();
			Set<ExposureSubSetting> subSettings = newFieldValue.getSubSettings();
			String subSettingDetails = newFieldValue.getExposureSubSettingDetails();
			Set<ExposureContactFactor> contactFactors = newFieldValue.getContactFactors();
			String contactFactorDetails = newFieldValue.getContactFactorDetails();
			Set<ExposureProtectiveMeasure> protectiveMeasures = newFieldValue.getProtectiveMeasures();
			String protectiveMeasureDetails = newFieldValue.getProtectiveMeasureDetails();
			AnimalCondition conditionOfAnimal = newFieldValue.getConditionOfAnimal();
			AnimalCategory animalCategory = newFieldValue.getAnimalCategory();
			TypeOfAnimal typeOfAnimal = newFieldValue.getTypeOfAnimal();
			String animalCategoryDetails = newFieldValue.getAnimalCategoryDetails();
			AnimalLocation animalLocation = newFieldValue.getAnimalLocation();
			String animalLocationDetails = newFieldValue.getAnimalLocationText();
			FomiteTransmissionLocation fomiteTransmissionLocation = newFieldValue.getFomiteTransmissionLocation();
			ProphylaxisAdherence prophylaxisAdherence = newFieldValue.getProphylaxisAdherence();
			String prophylaxisAdherenceDetails = newFieldValue.getProphylaxisAdherenceDetails();
			TravelPurpose travelPurpose = newFieldValue.getTravelPurpose();
			String travelPurposeDetails = newFieldValue.getTravelPurposeDetails();
			SexualContact sexualContact = newFieldValue.getSexualContact();

			// Update field items (these methods clear the field values)
			updateSettingFieldItems(category, setting);
			updateSubSettingsFieldItems(category, setting, subSettings);
			updateContactFactorsFieldItems(category, setting, contactFactors);
			updateProtectiveMeasuresFieldItems(category, setting, protectiveMeasures);

			// Restore setting field value and visibility
			if (setting != null) {
				settingField.setValue(setting);
			}
			settingDetailsField.setVisible(setting == ExposureSetting.OTHER);
			if (settingDetails != null) {
				settingDetailsField.setValue(settingDetails);
			}

			// Restore subSettings field value and visibility
			if (subSettings != null && !subSettings.isEmpty()) {
				subSettingsField.setValue(subSettings);
			}
			subSettingsDetailsField.setVisible(subSettings != null && subSettings.contains(ExposureSubSetting.OTHER));
			if (subSettingDetails != null) {
				subSettingsDetailsField.setValue(subSettingDetails);
			}

			// FIXME: Keep legacy TRAVELED_ABROAD gate for loaded records until travel/prophylaxis trigger is moved to new taxonomy.
			boolean hasTravelledAbroad = subSettings != null && subSettings.contains(ExposureSubSetting.TRAVELED_ABROAD);

			boolean isMalariaCaseTraveled = subSettings != null && disease == Disease.MALARIA && hasTravelledAbroad;
			prophylaxisAdherenceField.setVisible(isMalariaCaseTraveled);
			// If the Malaria-effected person traveled abroad, show the prophylaxis adherence and travel purpose fields
			if (isMalariaCaseTraveled) {
				if (prophylaxisAdherence != null) {
					prophylaxisAdherenceField.setValue(prophylaxisAdherence);
				}
				prophylaxisAdherenceDetailsField.setVisible(prophylaxisAdherence == ProphylaxisAdherence.OTHER);
				if (prophylaxisAdherenceDetails != null) {
					prophylaxisAdherenceDetailsField.setValue(prophylaxisAdherenceDetails);
				}
			} else {
				prophylaxisAdherenceField.setValue(null);
				prophylaxisAdherenceDetailsField.setValue(null);
				prophylaxisAdherenceDetailsField.setVisible(false);
			}
			// Travel purpose is visible to abroad travelers of Malaria and Dengue
			boolean isTravelPurposeVisible = hasTravelledAbroad && (disease == Disease.MALARIA || disease == Disease.DENGUE);
			travelPurposeField.setVisible(isTravelPurposeVisible);
			if (isTravelPurposeVisible) {
				travelPurposeField.setValue(travelPurpose);
				travelPurposeDetailsField.setVisible(travelPurpose == TravelPurpose.OTHER);
				if (travelPurposeDetails != null) {
					travelPurposeDetailsField.setValue(travelPurposeDetails);
				}
			} else {
				travelPurposeField.setValue(null);
				travelPurposeDetailsField.setValue(null);
				travelPurposeDetailsField.setVisible(false);
			}

			boolean isSexualActivity = subSettings != null && subSettings.contains(ExposureSubSetting.SEXUAL_ACTIVITY);
			boolean isSexualCategory = category == ExposureCategory.SEXUAL;
			boolean showSexualContact = isSexualCategory || isSexualActivity;
			sexualContactField.setVisible(showSexualContact);
			if (showSexualContact) {
				sexualContactField.setValue(sexualContact);
			} else {
				sexualContactField.setValue(null);
			}

			// Restore contactFactors field value and visibility
			if (contactFactors != null && !contactFactors.isEmpty()) {
				contactFactorsField.setValue(contactFactors);
			}
			contactFactorDetailsField.setVisible(contactFactors != null && contactFactors.contains(ExposureContactFactor.OTHER));
			if (contactFactorDetails != null) {
				contactFactorDetailsField.setValue(contactFactorDetails);
			}

			// Restore protectiveMeasures field value and visibility
			if (protectiveMeasures != null && !protectiveMeasures.isEmpty()) {
				protectiveMeasuresField.setValue(protectiveMeasures);
			}
			protectiveMeasureDetailsField.setVisible(protectiveMeasures != null && protectiveMeasures.contains(ExposureProtectiveMeasure.OTHER));
			if (protectiveMeasureDetails != null) {
				protectiveMeasureDetailsField.setValue(protectiveMeasureDetails);
			}

			// Initialize animal contact fields visibility and restore values
			boolean isAnimalContact = category == ExposureCategory.ANIMAL_CONTACT;
			conditionOfAnimalField.setVisible(isAnimalContact);
			conditionOfAnimalField.setRequired(isAnimalContact);
			if (isAnimalContact && conditionOfAnimal != null) {
				conditionOfAnimalField.setValue(conditionOfAnimal);
			}

			boolean hasConditionOfAnimal = conditionOfAnimal != null;
			animalCategoryField.setVisible(isAnimalContact && hasConditionOfAnimal);
			animalCategoryField.setRequired(isAnimalContact && hasConditionOfAnimal);
			if (isAnimalContact && hasConditionOfAnimal && animalCategory != null) {
				animalCategoryField.setValue(animalCategory);
			}

			boolean hasAnimalCategory = animalCategory != null;

			typeOfAnimalField.setVisible(isAnimalContact && hasConditionOfAnimal && hasAnimalCategory);
			if (isAnimalContact && hasConditionOfAnimal && hasAnimalCategory && typeOfAnimal != null) {
				typeOfAnimalField.setValue(typeOfAnimal);
			}

			animalCategoryDetailsField.setVisible(isAnimalContact && hasConditionOfAnimal && hasAnimalCategory);
			if (isAnimalContact && hasConditionOfAnimal && hasAnimalCategory && animalCategoryDetails != null) {
				animalCategoryDetailsField.setValue(animalCategoryDetails);
			}

			animalLocationField.setVisible(isAnimalContact);
			animalLocationField.setRequired(isAnimalContact);
			if (isAnimalContact && animalLocation != null) {
				animalLocationField.setValue(animalLocation);
			}

			boolean showAnimalLocationDetails = isAnimalContact && animalLocation == AnimalLocation.OTHER;
			animalLocationDetailsField.setVisible(showAnimalLocationDetails);
			if (showAnimalLocationDetails && animalLocationDetails != null) {
				animalLocationDetailsField.setValue(animalLocationDetails);
			} else {
				animalLocationDetailsField.setValue(null);
			}

			// Initialize fomite transmission field visibility and restore value
			boolean isFomiteTransmission = category == ExposureCategory.FOMITE_TRANSMISSION;
			fomiteTransmissionLocationField.setVisible(isFomiteTransmission);
			fomiteTransmissionLocationField.setRequired(isFomiteTransmission);
			if (isFomiteTransmission && fomiteTransmissionLocation != null) {
				fomiteTransmissionLocationField.setValue(fomiteTransmissionLocation);
			}
		}

		// HACK: Binding to the fields will call field listeners that may clear/modify the values of other fields.
		// this hopefully resets everything to its correct value
		locationForm.discard();
	}

	private void populateExposureTypes(ExposureDto exposure) {
		// Get disease configuration
		DiseaseConfigurationDto diseaseConfig = null;
		if (disease != null) {
			diseaseConfig = FacadeProvider.getDiseaseConfigurationFacade().getDiseaseConfiguration(disease);
		}

		Set<ExposureCategory> diseaseCategories = diseaseConfig != null && diseaseConfig.getExposureCategories() != null
			? new HashSet<>(diseaseConfig.getExposureCategories())
			: Collections.emptySet();

		// defaults (+ types matching the disease's configured categories, if any)
		List<ExposureType> filteredTypes = ExposureType.getValues(diseaseCategories);

		// Preserve existing record's value even if it is no longer in the filtered set (legacy data)
		Set<ExposureType> finalTypes = new LinkedHashSet<>(filteredTypes);
		if (exposure != null && exposure.getExposureType() != null) {
			finalTypes.add(exposure.getExposureType());
		}

		FieldHelper.updateItems(exposureTypeField, new ArrayList<>(finalTypes));
	}

	private void populateExposureCategories(ExposureDto exposure) {
		ExposureCategory selectedCategory = exposure != null ? exposure.getExposureCategory() : null;
		refreshCategoryFieldItems(selectedCategory);
	}

	private List<ExposureCategory> buildExposureCategoryItems(ExposureCategory selectedCategory) {
		Set<ExposureCategory> categories = new LinkedHashSet<>();

		DiseaseConfigurationDto diseaseConfig = null;
		if (disease != null) {
			diseaseConfig = FacadeProvider.getDiseaseConfigurationFacade().getDiseaseConfiguration(disease);
		}

		if (diseaseConfig != null && diseaseConfig.getExposureCategories() != null && !diseaseConfig.getExposureCategories().isEmpty()) {
			for (ExposureCategory category : ExposureCategory.values()) {
				if (diseaseConfig.getExposureCategories().contains(category) && !category.isDeprecated()) {
					categories.add(category);
				}
			}
		} else {
			categories.addAll(ExposureCategory.getNonDeprecatedValues());
		}

		if (selectedCategory != null) {
			categories.add(selectedCategory);
		}

		return new ArrayList<>(categories);
	}

	public Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> collectCurrentFieldValues() {
		Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> result = new HashMap<>();
		for (CustomizableFieldsGroup panel : new CustomizableFieldsGroup[] {
			exposureDetailsPanel,
			exposuresGeneralPanel,
			locationGeneralPanel }) {
			if (panel != null) {
				panel.getFieldsValues().forEach((metadata, valueDto) -> {
					if (valueDto != null) {
						result.put(metadata, valueDto);
					}
				});
			}
		}
		return result;
	}

	@Override
	protected String createHtmlLayout() {
		//@formatter:off
		String HTML_LAYOUT = UUID_REPORTING_USER + MAIN_ACCORDION_LAYOUT;
		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY) && epiDataParentClass == CaseDataDto.class) {
			HTML_LAYOUT += fluidRowLocs(ExposureDto.PROBABLE_INFECTION_ENVIRONMENT) +
			(FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled()
				? VaadinIcons.INFO_CIRCLE.getHtml() + " " + (I18nProperties.getString(Strings.infoCheckProbableInfectionEnvironment)) + "<p>   </p>" : "<p>   </p>");
		}
		//@formatter:on
		return HTML_LAYOUT;
	}

}
