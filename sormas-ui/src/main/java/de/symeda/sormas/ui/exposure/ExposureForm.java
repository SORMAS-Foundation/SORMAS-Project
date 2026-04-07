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
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
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
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.AnimalContactType;
import de.symeda.sormas.api.exposure.AnimalLocation;
import de.symeda.sormas.api.exposure.ExposureCategory;
import de.symeda.sormas.api.exposure.ExposureContactFactor;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureProtectiveMeasure;
import de.symeda.sormas.api.exposure.ExposureSetting;
import de.symeda.sormas.api.exposure.ExposureSubSetting;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.exposure.GatheringType;
import de.symeda.sormas.api.exposure.HabitationType;
import de.symeda.sormas.api.exposure.SwimmingLocation;
import de.symeda.sormas.api.exposure.TravelAccommodation;
import de.symeda.sormas.api.exposure.TypeOfAnimal;
import de.symeda.sormas.api.exposure.TypeOfChildcareFacility;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ComboBoxWithPlaceholder;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class ExposureForm extends AbstractEditForm<ExposureDto> {

	private static final long serialVersionUID = 8262753698264714832L;

	private static final String LOC_EXPOSURES_HEADING = "locExposuresHeading";
	private static final String LOC_EXPOSURE_DETAILS_HEADING = "locExposureDetailsHeading";
	private static final String LOC_LOCATION_HEADING = "locLocationHeading";
	private static final String LOC_ANIMAL_CONTACT_DETAILS_HEADING = "locAnimalContactDetailsHeading";
	private static final String LOC_BURIAL_DETAILS_HEADING = "locBurialDetailsHeading";
	private static final String LOC_CONCLUSION_HEADING = "locConclusionHeading";

	//@formatter:off
	private static final String UUID_REPORTING_USER = fluidRowLocs(ExposureDto.UUID, ExposureDto.REPORTING_USER);
	private static final String OTHER_STANDARD_FIELDS =
			fluidRowLocs(ExposureDto.START_DATE, ExposureDto.END_DATE) +
			loc(LOC_EXPOSURES_HEADING) +
			fluidRowLocs(ExposureDto.EXPOSURE_CATEGORY, null, null) +
			fluidRow(
					fluidColumn(4, 0, locs(
							ExposureDto.EXPOSURE_SETTING,
							ExposureDto.SUB_SETTINGS,
							ExposureDto.ANIMAL_CATEGORY,
							ExposureDto.FOMITE_TRANSMISSION_LOCATION
					)),
					fluidColumn(4, 0, locs(
							ExposureDto.CONTACT_FACTORS
					)),
					fluidColumn(4, 0, locs(
							ExposureDto.PROTECTIVE_MEASURES
					))
			) +
			fluidRow(
					fluidColumn(4, 0, locs(
							ExposureDto.EXPOSURE_SETTING_DETAILS,
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
			loc(ExposureDto.DESCRIPTION) +
			fluidRow(
					fluidColumnLoc(6, 0, ExposureDto.EXPOSURE_TYPE),
					fluidColumn(6, 0, locs(
							ExposureDto.EXPOSURE_TYPE_DETAILS,
							ExposureDto.GATHERING_TYPE,
							ExposureDto.HABITATION_TYPE,
							ExposureDto.TYPE_OF_ANIMAL,
							ExposureDto.TYPE_OF_CHILDCARE_FACILITY
					))
			) +
			fluidRow(
					fluidColumn(12, 0, locs(
							ExposureDto.GATHERING_DETAILS,
							ExposureDto.HABITATION_DETAILS,
							ExposureDto.TYPE_OF_ANIMAL_DETAILS,
							ExposureDto.CHILDCARE_FACILITY_DETAILS
					))
			) +
			loc(LOC_EXPOSURE_DETAILS_HEADING) +
			loc(ExposureDto.EXPOSURE_ROLE) +
			loc(ExposureDto.RISK_AREA) +
			loc(ExposureDto.LARGE_ATTENDANCE_NUMBER) +
			loc(ExposureDto.INDOORS) +
			loc(ExposureDto.OUTDOORS) +
			loc(ExposureDto.WEARING_MASK) +
			loc(ExposureDto.WEARING_PPE) +
			loc(ExposureDto.OTHER_PROTECTIVE_MEASURES) +
			loc(ExposureDto.PROTECTIVE_MEASURES_DETAILS) +
			loc(ExposureDto.SHORT_DISTANCE) +
			loc(ExposureDto.LONG_FACE_TO_FACE_CONTACT) +
			loc(ExposureDto.ANIMAL_MARKET) +
			loc(ExposureDto.PERCUTANEOUS) +
			loc(ExposureDto.CONTACT_TO_BODY_FLUIDS) +
			loc(ExposureDto.HANDLING_SAMPLES) +
			loc(ExposureDto.EATING_RAW_ANIMAL_PRODUCTS) +
			loc(ExposureDto.HANDLING_ANIMALS) +
			fluidRowLocs(ExposureDto.TRAVEL_ACCOMMODATION, ExposureDto.TRAVEL_ACCOMMODATION_TYPE) +
			fluidRowLocs(ExposureDto.DOMESTIC_SWIMMING, ExposureDto.INTERNATIONAL_SWIMMING) +
			fluidRowLocs(ExposureDto.RAW_FOOD_CONTACT, ExposureDto.RAW_FOOD_CONTACT_TEXT) +
			fluidRowLocs(ExposureDto.SWIMMING_LOCATION, ExposureDto.SWIMMING_LOCATION_TYPE) +
			fluidRow(fluidColumnLoc(6,0,ExposureDto.SEXUAL_EXPOSURE_TEXT)) +
			fluidRow(fluidColumnLoc(6,0,ExposureDto.SYMPTOMATIC_INDIVIDUAL_TEXT)) +
			loc(ExposureDto.CONTACT_TO_CASE) +
			loc(LOC_ANIMAL_CONTACT_DETAILS_HEADING) +
			loc(ExposureDto.ANIMAL_CONDITION) +
			fluidRowLocs(ExposureDto.ANIMAL_CONTACT_TYPE, ExposureDto.ANIMAL_CONTACT_TYPE_DETAILS) +
			fluidRowLocs(ExposureDto.ANIMAL_LOCATION, ExposureDto.ANIMAL_LOCATION_TEXT) +
			loc(ExposureDto.ANIMAL_VACCINATED) +
			loc(LOC_BURIAL_DETAILS_HEADING) +
			loc(ExposureDto.PHYSICAL_CONTACT_DURING_PREPARATION) +
			loc(ExposureDto.PHYSICAL_CONTACT_WITH_BODY) +
			fluidRowLocs(ExposureDto.DECEASED_PERSON_NAME, ExposureDto.DECEASED_PERSON_RELATION) +

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
			loc(ExposureDto.LOCATION);
	//@formatter:on

	private final Class<? extends EntityDto> epiDataParentClass;
	private final List<ContactReferenceDto> sourceContacts;

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
	private NullableOptionGroup animalConditionField;
	private NullableOptionGroup animalCategoryField;
	private TextField animalCategoryDetailsField;
	private NullableOptionGroup fomiteTransmissionLocationField;

	public ExposureForm(
		boolean create,
		Class<? extends EntityDto> epiDataParentClass,
		List<ContactReferenceDto> sourceContacts,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		UiFieldAccessCheckers fieldAccessCheckers,
		Disease disease) {
		super(ExposureDto.class, ExposureDto.I18N_PREFIX, false, fieldVisibilityCheckers, fieldAccessCheckers);

		setWidth(960, Unit.PIXELS);

		this.sourceContacts = sourceContacts;
		this.epiDataParentClass = epiDataParentClass;
		this.disease = disease;

		if (create) {
			hideValidationUntilNextCommit();
		}

		addFields();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {
		addHeadingsAndInfoTexts();
		addBasicFields();

		addField(ExposureDto.DESCRIPTION, TextArea.class).setRows(5);

		locationForm = addField(ExposureDto.LOCATION, LocationEditForm.class);
		locationForm.setCaption(null);
		addField(ExposureDto.CONNECTION_NUMBER, TextField.class);
		getField(ExposureDto.MEANS_OF_TRANSPORT).addValueChangeListener(e -> {
			if (e.getProperty().getValue() == MeansOfTransport.PLANE) {
				getField(ExposureDto.CONNECTION_NUMBER).setCaption(I18nProperties.getCaption(Captions.exposureFlightNumber));
			} else {
				getField(ExposureDto.CONNECTION_NUMBER)
					.setCaption(I18nProperties.getPrefixCaption(ExposureDto.I18N_PREFIX, ExposureDto.CONNECTION_NUMBER));
			}
		});

		if (epiDataParentClass == CaseDataDto.class) {
			addField(ExposureDto.CONTACT_TO_CASE, ComboBox.class);
		}

		setUpVisibilityDependencies();

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		setUpRequirements();
		setReadOnly(true, ExposureDto.UUID, ExposureDto.REPORTING_USER);
	}

	private void addHeadingsAndInfoTexts() {
		getContent().addComponent(new Label(h3(I18nProperties.getString(Strings.headingExposures)), ContentMode.HTML), LOC_EXPOSURES_HEADING);

		getContent()
			.addComponent(new Label(h3(I18nProperties.getString(Strings.headingExposureDetails)), ContentMode.HTML), LOC_EXPOSURE_DETAILS_HEADING);

		getContent().addComponent(
			new Label(h3(I18nProperties.getPrefixCaption(ExposureDto.I18N_PREFIX, ExposureDto.LOCATION)), ContentMode.HTML),
			LOC_LOCATION_HEADING);

		getContent().addComponent(
			new Label(h3(I18nProperties.getString(Strings.headingAnimalContactDetails)), ContentMode.HTML),
			LOC_ANIMAL_CONTACT_DETAILS_HEADING);

		getContent()
			.addComponent(new Label(h3(I18nProperties.getString(Strings.headingBurialDetails)), ContentMode.HTML), LOC_BURIAL_DETAILS_HEADING);

		getContent().addComponent(new Label(h3(I18nProperties.getString(Strings.headingEpiConclusion)), ContentMode.HTML), LOC_CONCLUSION_HEADING);
	}

	private void addBasicFields() {
		addFields(ExposureDto.UUID, ExposureDto.REPORTING_USER, ExposureDto.PROBABLE_INFECTION_ENVIRONMENT);

		DateTimeField startDate = addField(ExposureDto.START_DATE, DateTimeField.class);
		DateTimeField endDate = addField(ExposureDto.END_DATE, DateTimeField.class);

		DateComparisonValidator.addStartEndValidators(startDate, endDate, false);

		List<ExposureCategory> categories = Arrays.asList(ExposureCategory.values());
		categoryField = addField(ExposureDto.EXPOSURE_CATEGORY, ComboBox.class);
		categoryField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		categoryField.setRequired(true);
		FieldHelper.updateItems(categoryField, categories);

		settingField = addField(ExposureDto.EXPOSURE_SETTING, ComboBox.class);
		settingField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		settingField.setRequired(true);

		settingDetailsField = addField(ExposureDto.EXPOSURE_SETTING_DETAILS, TextField.class);
		settingDetailsField.setVisible(false);

		subSettingsField = addField(ExposureDto.SUB_SETTINGS, OptionGroup.class);
		subSettingsField.setMultiSelect(true);
		CssStyles.style(subSettingsField, CssStyles.CAPTION_ON_TOP);

		subSettingsDetailsField = addField(ExposureDto.EXPOSURE_SUB_SETTING_DETAILS, TextField.class);
		subSettingsDetailsField.setVisible(false);

		//animalConditionField = addField(ExposureDto.ANIMAL_CONDITION, NullableOptionGroup.class);
		animalCategoryField = addField(ExposureDto.ANIMAL_CATEGORY, NullableOptionGroup.class);

		contactFactorsField = addField(ExposureDto.CONTACT_FACTORS, OptionGroup.class);
		contactFactorsField.setMultiSelect(true);
		CssStyles.style(contactFactorsField, CssStyles.CAPTION_ON_TOP);

		contactFactorDetailsField = addField(ExposureDto.CONTACT_FACTOR_DETAILS, TextField.class);
		contactFactorDetailsField.setVisible(false);

		protectiveMeasuresField = addField(ExposureDto.PROTECTIVE_MEASURES, OptionGroup.class);
		protectiveMeasuresField.setMultiSelect(true);
		CssStyles.style(protectiveMeasuresField, CssStyles.CAPTION_ON_TOP);

		protectiveMeasureDetailsField = addField(ExposureDto.PROTECTIVE_MEASURE_DETAILS, TextField.class);
		protectiveMeasureDetailsField.setVisible(false);

		categoryField.addValueChangeListener(e -> {
			ExposureCategory selectedCategory = (ExposureCategory) e.getProperty().getValue();
			updateSettingFieldItems(selectedCategory);

			// Also update subSettings when category changes (setting will be null/cleared)
			updateSubSettingsFieldItems(selectedCategory, (ExposureSetting) settingField.getValue());

			// Update contact factors and protective measures when category changes
			updateContactFactorsFieldItems(selectedCategory, (ExposureSetting) settingField.getValue());
			updateProtectiveMeasuresFieldItems(selectedCategory, (ExposureSetting) settingField.getValue());
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

		addFields(
			ExposureDto.EXPOSURE_TYPE,
			ExposureDto.EXPOSURE_TYPE_DETAILS,
			ExposureDto.GATHERING_TYPE,
			ExposureDto.HABITATION_TYPE,
			ExposureDto.TYPE_OF_ANIMAL,
			ExposureDto.TYPE_OF_CHILDCARE_FACILITY,
			ExposureDto.GATHERING_DETAILS,
			ExposureDto.HABITATION_DETAILS,
			ExposureDto.TYPE_OF_ANIMAL_DETAILS,
			ExposureDto.CHILDCARE_FACILITY_DETAILS,
			ExposureDto.PHYSICAL_CONTACT_DURING_PREPARATION,
			ExposureDto.PHYSICAL_CONTACT_WITH_BODY,
			ExposureDto.DECEASED_PERSON_NAME,
			ExposureDto.DECEASED_PERSON_RELATION,
			ExposureDto.PROTECTIVE_MEASURES_DETAILS,
			ExposureDto.ANIMAL_CONDITION,
			ExposureDto.ANIMAL_CONTACT_TYPE,
			ExposureDto.ANIMAL_CONTACT_TYPE_DETAILS,
			ExposureDto.TYPE_OF_PLACE,
			ExposureDto.TYPE_OF_PLACE_DETAILS,
			ExposureDto.MEANS_OF_TRANSPORT,
			ExposureDto.MEANS_OF_TRANSPORT_DETAILS,
			ExposureDto.SEAT_NUMBER,
			ExposureDto.EXPOSURE_ROLE,
			ExposureDto.TRAVEL_ACCOMMODATION,
			ExposureDto.TRAVEL_ACCOMMODATION_TYPE,
			ExposureDto.DOMESTIC_SWIMMING,
			ExposureDto.INTERNATIONAL_SWIMMING,
			ExposureDto.SWIMMING_LOCATION,
			ExposureDto.SWIMMING_LOCATION_TYPE,
			ExposureDto.RAW_FOOD_CONTACT,
			ExposureDto.RAW_FOOD_CONTACT_TEXT,
			ExposureDto.SYMPTOMATIC_INDIVIDUAL_TEXT,
			ExposureDto.ANIMAL_LOCATION,
			ExposureDto.ANIMAL_LOCATION_TEXT,
			ExposureDto.SEXUAL_EXPOSURE_TEXT,
			ExposureDto.WORK_ENVIRONMENT);

		addFieldsWithCss(
			NullableOptionGroup.class,
			Arrays.asList(
				ExposureDto.LARGE_ATTENDANCE_NUMBER,
				ExposureDto.INDOORS,
				ExposureDto.OUTDOORS,
				ExposureDto.WEARING_MASK,
				ExposureDto.WEARING_PPE,
				ExposureDto.OTHER_PROTECTIVE_MEASURES,
				ExposureDto.SHORT_DISTANCE,
				ExposureDto.LONG_FACE_TO_FACE_CONTACT,
				ExposureDto.ANIMAL_MARKET,
				ExposureDto.PERCUTANEOUS,
				ExposureDto.CONTACT_TO_BODY_FLUIDS,
				ExposureDto.HANDLING_SAMPLES,
				ExposureDto.EATING_RAW_ANIMAL_PRODUCTS,
				ExposureDto.HANDLING_ANIMALS,
				ExposureDto.ANIMAL_VACCINATED,
				ExposureDto.RISK_AREA),
			ValoTheme.OPTIONGROUP_HORIZONTAL,
			CssStyles.OPTIONGROUP_CAPTION_INLINE);
		// Changing the distance and contact label for IMI
		if (Disease.INVASIVE_MENINGOCOCCAL_INFECTION.equals(disease)) {
			getField(ExposureDto.SHORT_DISTANCE).setCaption(I18nProperties.getCaption(Captions.Exposure_imi_shortDistance));
			getField(ExposureDto.LONG_FACE_TO_FACE_CONTACT).setCaption(I18nProperties.getCaption(Captions.Exposure_imi_longFaceToFaceContact));
		}
	}

	private void setUpVisibilityDependencies() {
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.EXPOSURE_TYPE_DETAILS, ExposureDto.EXPOSURE_TYPE, ExposureType.OTHER, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.GATHERING_TYPE, ExposureDto.EXPOSURE_TYPE, ExposureType.GATHERING, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.HABITATION_TYPE, ExposureDto.EXPOSURE_TYPE, ExposureType.HABITATION, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.TYPE_OF_ANIMAL, ExposureDto.EXPOSURE_TYPE, ExposureType.ANIMAL_CONTACT, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			List.of(ExposureDto.DOMESTIC_SWIMMING, ExposureDto.INTERNATIONAL_SWIMMING),
			ExposureDto.EXPOSURE_TYPE,
			ExposureType.RECREATIONAL_WATER,
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ExposureDto.TYPE_OF_CHILDCARE_FACILITY,
			ExposureDto.EXPOSURE_TYPE,
			ExposureType.CHILDCARE_FACILITY,
			true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.GATHERING_DETAILS, ExposureDto.GATHERING_TYPE, GatheringType.OTHER, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.HABITATION_DETAILS, ExposureDto.HABITATION_TYPE, HabitationType.OTHER, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.TYPE_OF_ANIMAL_DETAILS, ExposureDto.TYPE_OF_ANIMAL, TypeOfAnimal.OTHER, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ExposureDto.CHILDCARE_FACILITY_DETAILS,
			ExposureDto.TYPE_OF_CHILDCARE_FACILITY,
			TypeOfChildcareFacility.OTHER,
			true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.LARGE_ATTENDANCE_NUMBER, ExposureDto.EXPOSURE_TYPE, ExposureType.GATHERING, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(
				ExposureDto.PHYSICAL_CONTACT_DURING_PREPARATION,
				ExposureDto.PHYSICAL_CONTACT_WITH_BODY,
				ExposureDto.DECEASED_PERSON_NAME,
				ExposureDto.DECEASED_PERSON_RELATION),
			ExposureDto.EXPOSURE_TYPE,
			ExposureType.BURIAL,
			true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), ExposureDto.PROTECTIVE_MEASURES_DETAILS, ExposureDto.OTHER_PROTECTIVE_MEASURES, YesNoUnknown.YES, true);
		// Animal-contact-related fields are not relevant for Giardiasis and Cryptosporidiosis
		if (!List.of(Disease.GIARDIASIS, Disease.CRYPTOSPORIDIOSIS).contains(disease)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(ExposureDto.ANIMAL_CONDITION, ExposureDto.ANIMAL_VACCINATED, ExposureDto.ANIMAL_CONTACT_TYPE),
				ExposureDto.EXPOSURE_TYPE,
				ExposureType.ANIMAL_CONTACT,
				true);
		}
		FieldHelper
			.setVisibleWhen(getFieldGroup(), ExposureDto.ANIMAL_CONTACT_TYPE_DETAILS, ExposureDto.ANIMAL_CONTACT_TYPE, AnimalContactType.OTHER, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.ANIMAL_LOCATION_TEXT, ExposureDto.ANIMAL_LOCATION, AnimalLocation.OTHER, true);
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

		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.TRAVEL_ACCOMMODATION, ExposureDto.EXPOSURE_TYPE, ExposureType.TRAVEL, true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ExposureDto.TRAVEL_ACCOMMODATION_TYPE,
			ExposureDto.TRAVEL_ACCOMMODATION,
			TravelAccommodation.OTHER,
			true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.SWIMMING_LOCATION, ExposureDto.INTERNATIONAL_SWIMMING, YesNoUnknown.YES, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.SWIMMING_LOCATION_TYPE, ExposureDto.SWIMMING_LOCATION, SwimmingLocation.OTHER, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.ANIMAL_LOCATION, ExposureDto.EXPOSURE_TYPE, ExposureType.ANIMAL_CONTACT, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.SEXUAL_EXPOSURE_TEXT, ExposureDto.EXPOSURE_TYPE, ExposureType.SEXUAL_CONTACT, true);
		if (Disease.CRYPTOSPORIDIOSIS == disease) {
			FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.RAW_FOOD_CONTACT, ExposureDto.EXPOSURE_TYPE, ExposureType.ANIMAL_CONTACT, true);
			FieldHelper.setVisibleWhen(getFieldGroup(), ExposureDto.RAW_FOOD_CONTACT_TEXT, ExposureDto.RAW_FOOD_CONTACT, YesNoUnknown.YES, true);
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				ExposureDto.SYMPTOMATIC_INDIVIDUAL_TEXT,
				ExposureDto.EXPOSURE_TYPE,
				ExposureType.SYMPTOMATIC_CONTACT,
				true);
		}

		getContent().getComponent(LOC_ANIMAL_CONTACT_DETAILS_HEADING).setVisible(false);
		getContent().getComponent(LOC_BURIAL_DETAILS_HEADING).setVisible(false);
		getField(ExposureDto.EXPOSURE_TYPE).addValueChangeListener(e -> {
			ExposureType selectedExposureType = (ExposureType) e.getProperty().getValue();
			if (selectedExposureType != null) {
				getContent().getComponent(LOC_ANIMAL_CONTACT_DETAILS_HEADING).setVisible(selectedExposureType == ExposureType.ANIMAL_CONTACT);
				// Exposure details heading is hidden if an exposure type is Animal Contact or Other (there are no relevant fields)
				getContent().getComponent(LOC_EXPOSURE_DETAILS_HEADING)
					.setVisible(!List.of(ExposureType.ANIMAL_CONTACT, ExposureType.OTHER).contains(selectedExposureType));
				getContent().getComponent(LOC_BURIAL_DETAILS_HEADING).setVisible(selectedExposureType == ExposureType.BURIAL);
			}
		});
		getContent().getComponent(LOC_CONCLUSION_HEADING).setVisible(List.of(Disease.GIARDIASIS, Disease.CRYPTOSPORIDIOSIS).contains(disease));
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

	@Override
	public void setValue(ExposureDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		if (epiDataParentClass == CaseDataDto.class) {
			ComboBox cbContactToCase = getField(ExposureDto.CONTACT_TO_CASE);
			if (sourceContacts != null && !cbContactToCase.isReadOnly()) {
				cbContactToCase.addItems(sourceContacts);
			}
			cbContactToCase.getItemIds()
				.stream()
				.filter(i -> !(i instanceof ComboBoxWithPlaceholder.PlaceholderReferenceDto))
				.forEach(i -> cbContactToCase.setItemCaption(i, ((ContactReferenceDto) i).getCaptionAlwaysWithUuid()));
		}

		if (newFieldValue != null) {
			ExposureCategory category = newFieldValue.getExposureCategory();
			ExposureSetting setting = newFieldValue.getExposureSetting();

			updateSettingFieldItems(category);
			updateSubSettingsFieldItems(category, setting);
			updateContactFactorsFieldItems(category, setting);
			updateProtectiveMeasuresFieldItems(category, setting);

			// Initialize visibility for details fields
			settingDetailsField.setVisible(setting == ExposureSetting.OTHER);

			Set<ExposureSubSetting> subSettings = newFieldValue.getSubSettings();
			subSettingsDetailsField.setVisible(subSettings != null && subSettings.contains(ExposureSubSetting.OTHER));

			Set<ExposureContactFactor> contactFactors = newFieldValue.getContactFactors();
			contactFactorDetailsField.setVisible(contactFactors != null && contactFactors.contains(ExposureContactFactor.OTHER));

			Set<ExposureProtectiveMeasure> protectiveMeasures = newFieldValue.getProtectiveMeasures();
			protectiveMeasureDetailsField.setVisible(protectiveMeasures != null && protectiveMeasures.contains(ExposureProtectiveMeasure.OTHER));
		}

		// HACK: Binding to the fields will call field listeners that may clear/modify the values of other fields.
		// this hopefully resets everything to its correct value
		locationForm.discard();
	}

	@Override
	protected String createHtmlLayout() {
		//@formatter:off
		String HTML_LAYOUT = UUID_REPORTING_USER;
		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY) && epiDataParentClass == CaseDataDto.class) {
			HTML_LAYOUT += fluidRowLocs(ExposureDto.PROBABLE_INFECTION_ENVIRONMENT) +
			(FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled()
				? VaadinIcons.INFO_CIRCLE.getHtml() + " " + (I18nProperties.getString(Strings.infoCheckProbableInfectionEnvironment)) + "<p>   </p>" : "<p>   </p>");
		}
		//@formatter:on
		return HTML_LAYOUT + OTHER_STANDARD_FIELDS;
	}

}
