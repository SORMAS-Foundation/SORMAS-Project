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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.ComboBox;
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
import de.symeda.sormas.api.exposure.EatingOutVenue;
import de.symeda.sormas.api.exposure.ExposureCategory;
import de.symeda.sormas.api.exposure.ExposureContactFactor;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureProtectiveMeasure;
import de.symeda.sormas.api.exposure.ExposureSetting;
import de.symeda.sormas.api.exposure.ExposureSubSetting;
import de.symeda.sormas.api.exposure.FomiteTransmissionLocation;
import de.symeda.sormas.api.exposure.ProphylaxisAdherence;
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
import de.symeda.sormas.ui.utils.DateTimeField;
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

	//@formatter:off
	private static final String EXPOSURE_DETAILS_LAYOUT =
			fluidRowLocs(ExposureDto.START_DATE, ExposureDto.END_DATE) +
					loc(LOC_CUSTOMIZABLE_FIELDS_EXPOSURE_DETAILS) +
					loc(LOC_EXPOSURES_HEADING) +
					fluidRowLocs(ExposureDto.EXPOSURE_CATEGORY, ExposureDto.EXPOSURE_SETTING, ExposureDto.EXPOSURE_SETTING_DETAILS) +
					fluidRow(
							fluidColumn(4, 0, locs(
									ExposureDto.SUB_SETTINGS,
									ExposureDto.EATING_OUT_VENUES,
									ExposureDto.EATING_OUT_VENUE_OTHER,
									ExposureDto.SHOPPING_FOR_FOOD_DETAILS,
									ExposureDto.CONDITION_OF_ANIMAL,
									ExposureDto.ANIMAL_CATEGORY,
									ExposureDto.TYPE_OF_ANIMAL,
									ExposureDto.FOMITE_TRANSMISSION_LOCATION
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
									ExposureDto.ANIMAL_CATEGORY_DETAILS
							)),
							fluidColumn(4, 0, locs(
									ExposureDto.CONTACT_FACTOR_DETAILS
							)),
							fluidColumn(4, 0, locs(
									ExposureDto.PROTECTIVE_MEASURE_DETAILS
							))
					) +
					loc(LOC_CUSTOMIZABLE_FIELDS_EXPOSURES_GENERAL) +
					loc(ExposureDto.DESCRIPTION);

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

	private CustomLayout exposureDetailsLayout;
	private CustomLayout locationDetailsLayout;

	private Label exposuresHeading;
	private Label locationHeading;
	private Label conclusionHeading;

	private LocationEditForm locationForm;
	private Disease disease;

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
	private NullableOptionGroup fomiteTransmissionLocationField;
	private ComboBox prophylaxisAdherenceField;
	private ComboBox travelPurposeField;
	private TextField prophylaxisAdherenceDetailsField;
	private TextField travelPurposeDetailsField;

	private CustomizableFieldsGroup exposureDetailsPanel;
	private CustomizableFieldsGroup exposuresGeneralPanel;
	private CustomizableFieldsGroup locationGeneralPanel;

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

		addField(exposureDetailsLayout, ExposureDto.DESCRIPTION, TextArea.class).setRows(5);

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

		accordion.addFormSectionPanel(Captions.titleExposuresSection, true, exposureDetailsLayout);
		accordion.addFormSectionPanel(Captions.titleExposureLocationSection, false, locationDetailsLayout);

		getContent().addComponent(accordion, MAIN_ACCORDION_LOC);

		setUpVisibilityDependencies();

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

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

		DateTimeField startDate = addField(exposureDetailsLayout, ExposureDto.START_DATE, DateTimeField.class);
		DateTimeField endDate = addField(exposureDetailsLayout, ExposureDto.END_DATE, DateTimeField.class);

		DateComparisonValidator.addStartEndValidators(startDate, endDate, false);

		categoryField = addField(exposureDetailsLayout, ExposureDto.EXPOSURE_CATEGORY, ComboBox.class);
		categoryField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		categoryField.setRequired(true);

		settingField = addField(exposureDetailsLayout, ExposureDto.EXPOSURE_SETTING, ComboBox.class);
		settingField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);

		settingDetailsField = addField(exposureDetailsLayout, ExposureDto.EXPOSURE_SETTING_DETAILS, TextField.class);
		settingDetailsField.setVisible(false);

		subSettingsField = addField(exposureDetailsLayout, ExposureDto.SUB_SETTINGS, OptionGroup.class);
		subSettingsField.setMultiSelect(true);
		CssStyles.style(subSettingsField, CssStyles.CAPTION_ON_TOP);

		subSettingsDetailsField = addField(exposureDetailsLayout, ExposureDto.EXPOSURE_SUB_SETTING_DETAILS, TextField.class);
		subSettingsDetailsField.setVisible(false);

		OptionGroup eatingOutVenuesField = addField(exposureDetailsLayout, ExposureDto.EATING_OUT_VENUES, OptionGroup.class);
		eatingOutVenuesField.setMultiSelect(true);
		CssStyles.style(eatingOutVenuesField, CssStyles.CAPTION_ON_TOP);
		FieldHelper.updateItems(eatingOutVenuesField, Arrays.asList(EatingOutVenue.values()));
		eatingOutVenuesField.setVisible(false);

		TextField eatingOutVenueOtherField = addField(exposureDetailsLayout, ExposureDto.EATING_OUT_VENUE_OTHER, TextField.class);
		eatingOutVenueOtherField.setVisible(false);

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

		fomiteTransmissionLocationField = addField(exposureDetailsLayout, ExposureDto.FOMITE_TRANSMISSION_LOCATION, NullableOptionGroup.class);
		fomiteTransmissionLocationField.setVisible(false);

		contactFactorsField = addField(exposureDetailsLayout, ExposureDto.CONTACT_FACTORS, OptionGroup.class);
		contactFactorsField.setMultiSelect(true);
		CssStyles.style(contactFactorsField, CssStyles.CAPTION_ON_TOP);

		contactFactorDetailsField = addField(exposureDetailsLayout, ExposureDto.CONTACT_FACTOR_DETAILS, TextField.class);
		contactFactorDetailsField.setVisible(false);

		protectiveMeasuresField = addField(exposureDetailsLayout, ExposureDto.PROTECTIVE_MEASURES, OptionGroup.class);
		protectiveMeasuresField.setMultiSelect(true);
		CssStyles.style(protectiveMeasuresField, CssStyles.CAPTION_ON_TOP);

		protectiveMeasureDetailsField = addField(exposureDetailsLayout, ExposureDto.PROTECTIVE_MEASURE_DETAILS, TextField.class);
		protectiveMeasureDetailsField.setVisible(false);

		prophylaxisAdherenceField = addField(exposureDetailsLayout, ExposureDto.PROPHYLAXIS_ADHERENCE, ComboBox.class);
		prophylaxisAdherenceField.setVisible(false);
		prophylaxisAdherenceDetailsField = addField(exposureDetailsLayout, ExposureDto.PROPHYLAXIS_ADHERENCE_DETAILS, TextField.class);
		travelPurposeField = addField(exposureDetailsLayout, ExposureDto.TRAVEL_PURPOSE, ComboBox.class);
		travelPurposeField.setVisible(false);
		travelPurposeDetailsField = addField(exposureDetailsLayout, ExposureDto.TRAVEL_PURPOSE_DETAILS, TextField.class);

		categoryField.addValueChangeListener(e -> {
			ExposureCategory selectedCategory = (ExposureCategory) e.getProperty().getValue();
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
		});

		settingField.addValueChangeListener(e -> {
			ExposureSetting selectedSetting = (ExposureSetting) e.getProperty().getValue();

			// 1. Show/hide settingDetailsField if setting is OTHER
			settingDetailsField.setVisible(selectedSetting == ExposureSetting.OTHER);

			// 2. Update subSettings based on category and setting
			ExposureCategory selectedCategory = (ExposureCategory) categoryField.getValue();
			updateSubSettingsFieldItems(selectedCategory, selectedSetting);

			// 3. Update contact factors and protective measures based on category and setting
			updateContactFactorsFieldItems(selectedCategory, selectedSetting);
			updateProtectiveMeasuresFieldItems(selectedCategory, selectedSetting);
		});

		subSettingsField.addValueChangeListener(e -> {
			// 3. Show/hide subSettingsDetailsField if subSettings contains OTHER
			@SuppressWarnings("unchecked")
			Set<ExposureSubSetting> selectedSubSettings = (Set<ExposureSubSetting>) e.getProperty().getValue();
			boolean containsOther = selectedSubSettings != null && selectedSubSettings.contains(ExposureSubSetting.OTHER);
			subSettingsDetailsField.setVisible(containsOther);
			boolean isProphylaxis = selectedSubSettings != null && selectedSubSettings.contains(ExposureSubSetting.TRAVELED_ABROAD);
			setVisibleClear(isProphylaxis, ExposureDto.PROPHYLAXIS_ADHERENCE, ExposureDto.TRAVEL_PURPOSE);

			// Salmonellosis Lu: Eating out venues + shopping-for-food details follow sub-setting selection
			boolean showEatingOutVenues = selectedSubSettings != null && selectedSubSettings.contains(ExposureSubSetting.EATING_OUTSIDE);
			eatingOutVenuesField.setVisible(showEatingOutVenues);
			if (!showEatingOutVenues) {
				eatingOutVenuesField.setValue(null);
				eatingOutVenueOtherField.setVisible(false);
				eatingOutVenueOtherField.setValue(null);
			}
			boolean showShoppingForFood = selectedSubSettings != null && selectedSubSettings.contains(ExposureSubSetting.SHOPPING_FOR_FOOD);
			shoppingForFoodDetailsField.setVisible(showShoppingForFood);
			if (!showShoppingForFood) {
				shoppingForFoodDetailsField.setValue(null);
			}
		});

		eatingOutVenuesField.addValueChangeListener(e -> {
			@SuppressWarnings("unchecked")
			Set<EatingOutVenue> selectedVenues = (Set<EatingOutVenue>) e.getProperty().getValue();
			boolean containsOther = selectedVenues != null && selectedVenues.contains(EatingOutVenue.OTHER);
			eatingOutVenueOtherField.setVisible(eatingOutVenuesField.isVisible() && containsOther);
			if (!containsOther) {
				eatingOutVenueOtherField.setValue(null);
			}
		});

		contactFactorsField.addValueChangeListener(e -> {
			@SuppressWarnings("unchecked")
			Set<ExposureContactFactor> selectedContactFactors = (Set<ExposureContactFactor>) e.getProperty().getValue();
			boolean containsOther = selectedContactFactors != null && selectedContactFactors.contains(ExposureContactFactor.OTHER);
			contactFactorDetailsField.setVisible(containsOther);
		});

		protectiveMeasuresField.addValueChangeListener(e -> {
			@SuppressWarnings("unchecked")
			Set<ExposureProtectiveMeasure> selectedProtectiveMeasures = (Set<ExposureProtectiveMeasure>) e.getProperty().getValue();
			boolean containsOther = selectedProtectiveMeasures != null && selectedProtectiveMeasures.contains(ExposureProtectiveMeasure.OTHER);
			protectiveMeasureDetailsField.setVisible(containsOther);
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

		addField(locationDetailsLayout, ExposureDto.TYPE_OF_PLACE, ComboBox.class);
		addField(locationDetailsLayout, ExposureDto.TYPE_OF_PLACE_DETAILS, TextField.class);

		addField(locationDetailsLayout, ExposureDto.MEANS_OF_TRANSPORT, ComboBox.class);
		addField(locationDetailsLayout, ExposureDto.MEANS_OF_TRANSPORT_DETAILS, TextField.class);

		addField(locationDetailsLayout, ExposureDto.SEAT_NUMBER, TextField.class);

		addField(locationDetailsLayout, ExposureDto.WORK_ENVIRONMENT, ComboBox.class);
	}

	private void setUpVisibilityDependencies() {
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

	private void updateSettingFieldItems(ExposureCategory category) {
		List<ExposureSetting> settings = ExposureSetting.getValues(category);
		FieldHelper.updateItems(settingField, settings);

		// Clear the field and its dependent details field
		settingField.setValue(null);
		settingDetailsField.setValue(null);
		settingDetailsField.setVisible(false);

		if (category != null) {
			if (category.hasNoSetting()) {
				settingField.setVisible(false);
				settingField.setRequired(false);
			} else {
				settingField.setVisible(true);
				settingField.setRequired(true);
			}
		}
	}

	private void updateSubSettingsFieldItems(ExposureCategory category, ExposureSetting setting) {
		List<ExposureSubSetting> subSettings;

		// For categories that have no setting but do have subsettings (e.g., FOOD_BORNE),
		// we need to get subsettings based only on category
		if (category != null && category.hasNoSetting()) {
			subSettings = ExposureSubSetting.getValuesForCategoryOnly(category);
		} else {
			subSettings = ExposureSubSetting.getValues(category, setting);
		}

		FieldHelper.updateItems(subSettingsField, subSettings);

		// Clear the field and its dependent details field
		subSettingsField.setValue(null);
		subSettingsDetailsField.setValue(null);
		subSettingsDetailsField.setVisible(false);

		// Hide subSettings field if no options available
		subSettingsField.setVisible(!subSettings.isEmpty());
	}

	private void updateContactFactorsFieldItems(ExposureCategory category, ExposureSetting setting) {
		List<ExposureContactFactor> contactFactors = ExposureContactFactor.getValues(category, setting);
		FieldHelper.updateItems(contactFactorsField, contactFactors);

		// Clear the field and its dependent details field
		contactFactorsField.setValue(null);
		contactFactorDetailsField.setValue(null);
		contactFactorDetailsField.setVisible(false);

		// Hide contactFactors field if no options available
		contactFactorsField.setVisible(!contactFactors.isEmpty());
	}

	private void updateProtectiveMeasuresFieldItems(ExposureCategory category, ExposureSetting setting) {
		List<ExposureProtectiveMeasure> protectiveMeasures = ExposureProtectiveMeasure.getValues(category, setting);
		FieldHelper.updateItems(protectiveMeasuresField, protectiveMeasures);

		// Clear the field and its dependent details field
		protectiveMeasuresField.setValue(null);
		protectiveMeasureDetailsField.setValue(null);
		protectiveMeasureDetailsField.setVisible(false);

		// Hide protectiveMeasures field if no options available
		protectiveMeasuresField.setVisible(!protectiveMeasures.isEmpty());
	}

	private void updateAnimalContactFields(ExposureCategory category) {
		boolean isAnimalContact = category == ExposureCategory.ANIMAL_CONTACT;

		// Show/hide and set required for conditionOfAnimalField
		conditionOfAnimalField.setVisible(isAnimalContact);
		conditionOfAnimalField.setRequired(isAnimalContact);

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
			FomiteTransmissionLocation fomiteTransmissionLocation = newFieldValue.getFomiteTransmissionLocation();
			ProphylaxisAdherence prophylaxisAdherence = newFieldValue.getProphylaxisAdherence();
			String prophylaxisAdherenceDetails = newFieldValue.getProphylaxisAdherenceDetails();
			TravelPurpose travelPurpose = newFieldValue.getTravelPurpose();
			String travelPurposeDetails = newFieldValue.getTravelPurposeDetails();

			// Update field items (these methods clear the field values)
			updateSettingFieldItems(category);
			updateSubSettingsFieldItems(category, setting);
			updateContactFactorsFieldItems(category, setting);
			updateProtectiveMeasuresFieldItems(category, setting);

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

			boolean hasTraveledAbroad = subSettings != null && subSettings.contains(ExposureSubSetting.TRAVELED_ABROAD);
			prophylaxisAdherenceField.setVisible(hasTraveledAbroad);
			travelPurposeField.setVisible(hasTraveledAbroad);
			// If the person has traveled abroad, show the prophylaxis adherence and travel purpose fields
			if (hasTraveledAbroad) {
				if (prophylaxisAdherence != null) {
					prophylaxisAdherenceField.setValue(prophylaxisAdherence);
				}
				prophylaxisAdherenceDetailsField.setVisible(prophylaxisAdherence == ProphylaxisAdherence.OTHER);
				if (prophylaxisAdherenceDetails != null) {
					prophylaxisAdherenceDetailsField.setValue(prophylaxisAdherenceDetails);
				}

				if (travelPurpose != null) {
					travelPurposeField.setValue(travelPurpose);
				}
				travelPurposeDetailsField.setVisible(travelPurpose == TravelPurpose.OTHER);
				if (travelPurposeDetails != null) {
					travelPurposeDetailsField.setValue(travelPurposeDetails);
				}
			} else {
				prophylaxisAdherenceField.setValue(null);
				prophylaxisAdherenceDetailsField.setValue(null);
				prophylaxisAdherenceDetailsField.setVisible(false);
				travelPurposeField.setValue(null);
				travelPurposeDetailsField.setValue(null);
				travelPurposeDetailsField.setVisible(false);
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

	private void populateExposureCategories(ExposureDto exposure) {
		Set<ExposureCategory> categories;

		// Get disease configuration
		DiseaseConfigurationDto diseaseConfig = null;
		if (disease != null) {
			diseaseConfig = FacadeProvider.getDiseaseConfigurationFacade().getDiseaseConfiguration(disease);
		}

		// Determine which categories to use
		if (diseaseConfig != null && diseaseConfig.getExposureCategories() != null && !diseaseConfig.getExposureCategories().isEmpty()) {
			// Disease has configured categories - use them
			categories = new HashSet<>(diseaseConfig.getExposureCategories());

			// For existing exposure, if its category is not in the configured list, add it
			if (exposure != null && exposure.getExposureCategory() != null) {
				ExposureCategory existingCategory = exposure.getExposureCategory();
				if (!categories.contains(existingCategory)) {
					categories.add(existingCategory);
				}
			}
		} else {
			// No configured categories for this disease - use all categories
			categories = EnumSet.allOf(ExposureCategory.class);
		}

		// Update the category field items
		FieldHelper.updateItems(categoryField, new ArrayList<>(categories));
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
