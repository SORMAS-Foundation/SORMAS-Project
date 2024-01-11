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

package de.symeda.sormas.ui.person;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.LABEL_WHITE_SPACE_NORMAL;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.divsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLocCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.oneOfFourCol;
import static de.symeda.sormas.ui.utils.LayoutUtil.oneOfTwoCol;

import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.EducationType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Salutation;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ApproximateAgeValidator;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.OutbreakFieldVisibilityChecker;
import de.symeda.sormas.ui.utils.ResizableTextAreaWrapper;
import de.symeda.sormas.ui.utils.SormasFieldGroupFieldFactory;
import de.symeda.sormas.ui.utils.ValidationUtils;
import de.symeda.sormas.ui.utils.ViewMode;

public class PersonEditForm extends AbstractEditForm<PersonDto> {

	private static final long serialVersionUID = -1L;

	private static final String PERSON_INFORMATION_HEADING_LOC = "personInformationHeadingLoc";
	private static final String OCCUPATION_HEADER = "occupationHeader";
	private static final String ADDRESS_HEADER = "addressHeader";
	private static final String ADDRESSES_HEADER = "addressesHeader";
	private static final String CONTACT_INFORMATION_HEADER = "contactInformationHeader";
	private static final String EXTERNAL_TOKEN_WARNING_LOC = "externalTokenWarningLoc";
	private static final String GENERAL_COMMENT_LOC = "generalCommentLoc";
	//@formatter:off
    private static final String HTML_LAYOUT =
            loc(PERSON_INFORMATION_HEADING_LOC) +
					fluidRowLocs(PersonDto.UUID, "")+
                    fluidRowLocs(PersonDto.FIRST_NAME, PersonDto.LAST_NAME) +
					fluidRowLocs(PersonDto.SALUTATION, PersonDto.OTHER_SALUTATION) +
                    fluidRow(
                            fluidRowLocs(PersonDto.BIRTH_DATE_YYYY, PersonDto.BIRTH_DATE_MM, PersonDto.BIRTH_DATE_DD),
                            fluidRowLocs(PersonDto.APPROXIMATE_AGE, PersonDto.APPROXIMATE_AGE_TYPE, PersonDto.APPROXIMATE_AGE_REFERENCE_DATE)
                    ) +
                    fluidRowLocs(PersonDto.PLACE_OF_BIRTH_REGION, PersonDto.PLACE_OF_BIRTH_DISTRICT, PersonDto.PLACE_OF_BIRTH_COMMUNITY) +
                    fluidRowLocs(PersonDto.PLACE_OF_BIRTH_FACILITY_TYPE, PersonDto.PLACE_OF_BIRTH_FACILITY, PersonDto.PLACE_OF_BIRTH_FACILITY_DETAILS) +
                    fluidRowLocs(PersonDto.GESTATION_AGE_AT_BIRTH, PersonDto.BIRTH_WEIGHT) +
                    fluidRowLocs(PersonDto.SEX, PersonDto.PRESENT_CONDITION) +
                    fluidRow(
                            oneOfFourCol(PersonDto.DEATH_DATE),
                            oneOfFourCol(PersonDto.CAUSE_OF_DEATH),
                            fluidColumnLocCss(CssStyles.LAYOUT_COL_HIDE_INVSIBLE, 3, 0, PersonDto.CAUSE_OF_DEATH_DISEASE),
                            oneOfFourCol(PersonDto.CAUSE_OF_DEATH_DETAILS)
                    ) +
                    fluidRow(
                            oneOfFourCol(PersonDto.DEATH_PLACE_TYPE),
                            oneOfFourCol(PersonDto.DEATH_PLACE_DESCRIPTION)
                    ) +
                    fluidRow(
                            oneOfFourCol(PersonDto.BURIAL_DATE),
                            oneOfFourCol(PersonDto.BURIAL_CONDUCTOR),
                            oneOfTwoCol(PersonDto.BURIAL_PLACE_DESCRIPTION)
                    ) +
                    fluidRowLocs(PersonDto.PASSPORT_NUMBER, PersonDto.NATIONAL_HEALTH_ID) +
					fluidRowLocs(PersonDto.EXTERNAL_ID, PersonDto.EXTERNAL_TOKEN) +
					fluidRowLocs(PersonDto.INTERNAL_TOKEN, EXTERNAL_TOKEN_WARNING_LOC) +

					fluidRowLocs(PersonDto.HAS_COVID_APP, PersonDto.COVID_CODE_DELIVERED) +

                    loc(OCCUPATION_HEADER) +
                    divsCss(VSPACE_3,
                            fluidRowLocs(PersonDto.OCCUPATION_TYPE, PersonDto.OCCUPATION_DETAILS) +
                            fluidRow(oneOfTwoCol(PersonDto.ARMED_FORCES_RELATION_TYPE)),
                            fluidRowLocs(PersonDto.EDUCATION_TYPE, PersonDto.EDUCATION_DETAILS)
                    ) +

                    loc(ADDRESS_HEADER) +
                    divsCss(VSPACE_3, fluidRowLocs(PersonDto.ADDRESS)) +

					loc(ADDRESSES_HEADER) +
					fluidRowLocs(PersonDto.ADDRESSES) +

                    loc(CONTACT_INFORMATION_HEADER) +
					divsCss(
                            VSPACE_3,
							fluidRowLocs(PersonDto.BIRTH_NAME, "") +
									fluidRowLocs(PersonDto.NICKNAME, PersonDto.MOTHERS_MAIDEN_NAME) +
									fluidRowLocs(PersonDto.MOTHERS_NAME, PersonDto.FATHERS_NAME) +
									fluidRowLocs(PersonDto.NAMES_OF_GUARDIANS) +
                                    fluidRowLocs(PersonDto.BIRTH_COUNTRY, PersonDto.CITIZENSHIP) +
					fluidRowLocs(PersonDto.PERSON_CONTACT_DETAILS)) +
					loc(GENERAL_COMMENT_LOC) + fluidRowLocs(CaseDataDto.ADDITIONAL_DETAILS);
	private final Label occupationHeader = new Label(I18nProperties.getString(Strings.headingPersonOccupation));
	private final Label addressHeader = new Label(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.ADDRESS));
	private final Label addressesHeader = new Label(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.ADDRESSES));
	private final Label contactInformationHeader = new Label(I18nProperties.getString(Strings.headingContactInformation));
	private Label personInformationHeadingLabel;
	private TextField firstNameField;
	private TextField lastNameField;
	private Disease disease;
	private String diseaseDetails;
	private ComboBox causeOfDeathField;
	private ComboBox causeOfDeathDiseaseField;
	private TextField causeOfDeathDetailsField;
	private ComboBox birthDateDay;
	private ComboBox cbPlaceOfBirthFacility;
	private PersonContext personContext;
	private boolean isPseudonymized;
	private LocationEditForm addressForm;
	private PresentConditionChangeListener presentConditionChangeListener;
	private ComboBox occupationTypeField;
	//@formatter:on

	public PersonEditForm(
		PersonContext personContext,
		Disease disease,
		String diseaseDetails,
		ViewMode viewMode,
		boolean isPseudonymized,
		boolean inJurisdiction,
		boolean isEditAllowed) {
		super(
			PersonDto.class,
			PersonDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(disease)
				.add(new OutbreakFieldVisibilityChecker(viewMode))
				.add(new CountryFieldVisibilityChecker(FacadeProvider.getConfigFacade().getCountryLocale())),
			UiFieldAccessCheckers.forDataAccessLevel(UserProvider.getCurrent().getPseudonymizableDataAccessLevel(inJurisdiction), isPseudonymized),
			isEditAllowed);

		this.personContext = personContext;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.isPseudonymized = isPseudonymized;

		CssStyles.style(CssStyles.H3, occupationHeader, addressHeader, addressesHeader, contactInformationHeader);
		getContent().addComponent(occupationHeader, OCCUPATION_HEADER);
		getContent().addComponent(addressHeader, ADDRESS_HEADER);
		getContent().addComponent(addressesHeader, ADDRESSES_HEADER);
		getContent().addComponent(contactInformationHeader, CONTACT_INFORMATION_HEADER);

		addFields();
	}

	public PersonEditForm(
		PersonContext personContext,
		Disease disease,
		String diseaseDetails,
		ViewMode viewMode,
		boolean isPseudonymized,
		boolean inJurisdiction) {
		super(
			PersonDto.class,
			PersonDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(disease)
				.add(new OutbreakFieldVisibilityChecker(viewMode))
				.add(new CountryFieldVisibilityChecker(FacadeProvider.getConfigFacade().getCountryLocale())),
			UiFieldAccessCheckers.forDataAccessLevel(UserProvider.getCurrent().getPseudonymizableDataAccessLevel(inJurisdiction), isPseudonymized));

		this.personContext = personContext;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.isPseudonymized = isPseudonymized;

		CssStyles.style(CssStyles.H3, occupationHeader, addressHeader, addressesHeader, contactInformationHeader);
		getContent().addComponent(occupationHeader, OCCUPATION_HEADER);
		getContent().addComponent(addressHeader, ADDRESS_HEADER);
		getContent().addComponent(addressesHeader, ADDRESSES_HEADER);
		getContent().addComponent(contactInformationHeader, CONTACT_INFORMATION_HEADER);

		addFields();
	}

	public PersonEditForm(boolean isEditAllowed, boolean isPseudonymized, boolean inJurisdiction) {
		super(
			PersonDto.class,
			PersonDto.I18N_PREFIX,
			false,
			new FieldVisibilityCheckers().add(new OutbreakFieldVisibilityChecker(ViewMode.NORMAL))
				.add(new CountryFieldVisibilityChecker(FacadeProvider.getConfigFacade().getCountryLocale())),
			UiFieldAccessCheckers.forDataAccessLevel(UserProvider.getCurrent().getPseudonymizableDataAccessLevel(inJurisdiction), isPseudonymized),
			isEditAllowed);

		CssStyles.style(CssStyles.H3, occupationHeader, addressHeader, addressesHeader, contactInformationHeader);
		getContent().addComponent(occupationHeader, OCCUPATION_HEADER);
		getContent().addComponent(addressHeader, ADDRESS_HEADER);
		getContent().addComponent(addressesHeader, ADDRESSES_HEADER);
		getContent().addComponent(contactInformationHeader, CONTACT_INFORMATION_HEADER);

		addFields();
	}

	@Override
	protected void addFields() {

		personInformationHeadingLabel = new Label(I18nProperties.getString(Strings.headingPersonInformation));
		personInformationHeadingLabel.addStyleName(H3);
		getContent().addComponent(personInformationHeadingLabel, PERSON_INFORMATION_HEADING_LOC);

		addField(PersonDto.UUID).setReadOnly(true);
		firstNameField = addField(PersonDto.FIRST_NAME, TextField.class);
		lastNameField = addField(PersonDto.LAST_NAME, TextField.class);

		addFields(PersonDto.SALUTATION, PersonDto.OTHER_SALUTATION);
		FieldHelper.setVisibleWhen(getFieldGroup(), PersonDto.OTHER_SALUTATION, PersonDto.SALUTATION, Salutation.OTHER, true);

		ComboBox sex = addField(PersonDto.SEX, ComboBox.class);
		addField(PersonDto.BIRTH_NAME, TextField.class);
		addField(PersonDto.NICKNAME, TextField.class);
		addField(PersonDto.MOTHERS_MAIDEN_NAME, TextField.class);
		addFields(PersonDto.MOTHERS_NAME, PersonDto.FATHERS_NAME);
		addFields(PersonDto.NAMES_OF_GUARDIANS);
		ComboBox presentCondition = addField(PersonDto.PRESENT_CONDITION, ComboBox.class);
		birthDateDay = addField(PersonDto.BIRTH_DATE_DD, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateDay.setNullSelectionAllowed(true);
		birthDateDay.setInputPrompt(I18nProperties.getString(Strings.day));
		birthDateDay.setCaption("");
		ComboBox birthDateMonth = addField(PersonDto.BIRTH_DATE_MM, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateMonth.setNullSelectionAllowed(true);
		birthDateMonth.addItems(DateHelper.getMonthsInYear());
		birthDateMonth.setPageLength(12);
		birthDateMonth.setInputPrompt(I18nProperties.getString(Strings.month));
		birthDateMonth.setCaption("");
		DateHelper.getMonthsInYear()
			.forEach(month -> birthDateMonth.setItemCaption(month, de.symeda.sormas.api.Month.values()[month - 1].toString()));
		setItemCaptionsForMonths(birthDateMonth);
		ComboBox birthDateYear = addField(PersonDto.BIRTH_DATE_YYYY, ComboBox.class);
		birthDateYear.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE));
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateYear.setNullSelectionAllowed(true);
		birthDateYear.addItems(DateHelper.getYearsToNow());
		birthDateYear.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		birthDateYear.setInputPrompt(I18nProperties.getString(Strings.year));
		birthDateDay.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) birthDateYear.getValue(), (Integer) birthDateMonth.getValue(), (Integer) e));
		birthDateMonth.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) birthDateYear.getValue(), (Integer) e, (Integer) birthDateDay.getValue()));
		birthDateYear.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) e, (Integer) birthDateMonth.getValue(), (Integer) birthDateDay.getValue()));

		DateField deathDate = addField(PersonDto.DEATH_DATE, DateField.class);
		TextField approximateAgeField = addField(PersonDto.APPROXIMATE_AGE, TextField.class);
		approximateAgeField
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, approximateAgeField.getCaption()));
		ComboBox approximateAgeTypeField = addField(PersonDto.APPROXIMATE_AGE_TYPE, ComboBox.class);
		addField(PersonDto.APPROXIMATE_AGE_REFERENCE_DATE, DateField.class);

		approximateAgeField.addValidator(
			new ApproximateAgeValidator(
				approximateAgeField,
				approximateAgeTypeField,
				I18nProperties.getValidationError(Validations.softApproximateAgeTooHigh)));

		TextField tfGestationAgeAtBirth = addField(PersonDto.GESTATION_AGE_AT_BIRTH, TextField.class);
		tfGestationAgeAtBirth
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfGestationAgeAtBirth.getCaption()));
		TextField tfBirthWeight = addField(PersonDto.BIRTH_WEIGHT, TextField.class);
		tfBirthWeight.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, tfBirthWeight.getCaption()));

		AbstractSelect deathPlaceType = addField(PersonDto.DEATH_PLACE_TYPE, ComboBox.class);
		deathPlaceType.setNullSelectionAllowed(true);
		TextField deathPlaceDesc = addField(PersonDto.DEATH_PLACE_DESCRIPTION, TextField.class);
		DateField burialDate = addField(PersonDto.BURIAL_DATE, DateField.class);
		TextField burialPlaceDesc = addField(PersonDto.BURIAL_PLACE_DESCRIPTION, TextField.class);
		ComboBox burialConductor = addField(PersonDto.BURIAL_CONDUCTOR, ComboBox.class);
		addressForm = addField(PersonDto.ADDRESS, LocationEditForm.class);
		addressForm.setCaption(null);
		addField(PersonDto.ADDRESSES, LocationsField.class).setCaption(null);

		PersonContactDetailsField personContactDetailsField = addField(PersonDto.PERSON_CONTACT_DETAILS, PersonContactDetailsField.class);
		personContactDetailsField.setThisPerson(getValue());
		personContactDetailsField.setCaption(null);
		personContactDetailsField.setPseudonymized(isPseudonymized);

		occupationTypeField = addField(PersonDto.OCCUPATION_TYPE, ComboBox.class);
		TextField occupationTypeDetailsField = addField(PersonDto.OCCUPATION_DETAILS, TextField.class);
		occupationTypeDetailsField.setVisible(false);
		occupationTypeField.addValueChangeListener(o -> {
			OccupationType occupationType = (OccupationType) o.getProperty().getValue();
			occupationTypeDetailsField.setVisible(occupationType != null && occupationType.matchPropertyValue(OccupationType.HAS_DETAILS, true));
		});

		addFields(PersonDto.ARMED_FORCES_RELATION_TYPE, PersonDto.EDUCATION_TYPE, PersonDto.EDUCATION_DETAILS);

		List<CountryReferenceDto> countries = FacadeProvider.getCountryFacade().getAllActiveAsReference();
		addInfrastructureField(PersonDto.BIRTH_COUNTRY).addItems(countries);
		addInfrastructureField(PersonDto.CITIZENSHIP).addItems(countries);

		addFields(PersonDto.PASSPORT_NUMBER, PersonDto.NATIONAL_HEALTH_ID);
		Field externalId = addField(PersonDto.EXTERNAL_ID);
		if (FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled()) {
			externalId.setEnabled(false);
		}
		TextField externalTokenField = addField(PersonDto.EXTERNAL_TOKEN);
		Label externalTokenWarningLabel = new Label(I18nProperties.getString(Strings.messagePersonExternalTokenWarning));
		externalTokenWarningLabel.addStyleNames(VSPACE_3, LABEL_WHITE_SPACE_NORMAL);
		getContent().addComponent(externalTokenWarningLabel, EXTERNAL_TOKEN_WARNING_LOC);
		addField(PersonDto.INTERNAL_TOKEN);

		addField(PersonDto.HAS_COVID_APP).addStyleName(CssStyles.FORCE_CAPTION_CHECKBOX);
		addField(PersonDto.COVID_CODE_DELIVERED).addStyleName(CssStyles.FORCE_CAPTION_CHECKBOX);

		if (personContext != PersonContext.CASE) {
			setVisible(false, PersonDto.HAS_COVID_APP, PersonDto.COVID_CODE_DELIVERED);
		}

		ComboBox cbPlaceOfBirthRegion = addInfrastructureField(PersonDto.PLACE_OF_BIRTH_REGION);
		ComboBox cbPlaceOfBirthDistrict = addInfrastructureField(PersonDto.PLACE_OF_BIRTH_DISTRICT);
		ComboBox cbPlaceOfBirthCommunity = addInfrastructureField(PersonDto.PLACE_OF_BIRTH_COMMUNITY);
		ComboBox placeOfBirthFacilityType = addField(PersonDto.PLACE_OF_BIRTH_FACILITY_TYPE);
		FieldHelper.removeItems(placeOfBirthFacilityType);
		placeOfBirthFacilityType.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID);
		placeOfBirthFacilityType.addItems(FacilityType.getPlaceOfBirthTypes());

		cbPlaceOfBirthFacility = addInfrastructureField(PersonDto.PLACE_OF_BIRTH_FACILITY);
		TextField tfPlaceOfBirthFacilityDetails = addField(PersonDto.PLACE_OF_BIRTH_FACILITY_DETAILS, TextField.class);

		causeOfDeathField = addField(PersonDto.CAUSE_OF_DEATH, ComboBox.class);
		causeOfDeathDiseaseField = addDiseaseField(PersonDto.CAUSE_OF_DEATH_DISEASE, true);
		causeOfDeathDetailsField = addField(PersonDto.CAUSE_OF_DEATH_DETAILS, TextField.class);

		// Set requirements that don't need visibility changes and read only status

		setReadOnly(true, PersonDto.APPROXIMATE_AGE_REFERENCE_DATE);
		setRequired(true, PersonDto.FIRST_NAME, PersonDto.LAST_NAME, PersonDto.SEX);
		setVisible(
			false,
			PersonDto.DEATH_DATE,
			PersonDto.DEATH_PLACE_TYPE,
			PersonDto.DEATH_PLACE_DESCRIPTION,
			PersonDto.BURIAL_DATE,
			PersonDto.BURIAL_PLACE_DESCRIPTION,
			PersonDto.BURIAL_CONDUCTOR,
			PersonDto.CAUSE_OF_DEATH,
			PersonDto.CAUSE_OF_DEATH_DETAILS,
			PersonDto.CAUSE_OF_DEATH_DISEASE);

		FieldHelper.setVisibleWhen(getFieldGroup(), PersonDto.EDUCATION_DETAILS, PersonDto.EDUCATION_TYPE, Arrays.asList(EducationType.OTHER), true);

		FieldHelper.addSoftRequiredStyle(
			presentCondition,
			sex,
			deathDate,
			deathPlaceDesc,
			deathPlaceType,
			causeOfDeathField,
			causeOfDeathDiseaseField,
			causeOfDeathDetailsField,
			burialDate,
			burialPlaceDesc,
			burialConductor);

		// Set initial visibilities

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		if (!getField(PersonDto.OCCUPATION_TYPE).isVisible()
			&& !getField(PersonDto.ARMED_FORCES_RELATION_TYPE).isVisible()
			&& !getField(PersonDto.EDUCATION_TYPE).isVisible())
			occupationHeader.setVisible(false);
		if (!getField(PersonDto.ADDRESS).isVisible())
			addressHeader.setVisible(false);
		if (!getField(PersonDto.ADDRESSES).isVisible())
			addressesHeader.setVisible(false);

		// Add listeners

		FieldHelper.setRequiredWhenNotNull(getFieldGroup(), PersonDto.APPROXIMATE_AGE, PersonDto.APPROXIMATE_AGE_TYPE);
		addFieldListeners(PersonDto.APPROXIMATE_AGE, e -> {
			@SuppressWarnings("unchecked")
			Field<ApproximateAgeType> ageTypeField = (Field<ApproximateAgeType>) getField(PersonDto.APPROXIMATE_AGE_TYPE);
			if (!ageTypeField.isReadOnly()) {
				if (e.getProperty().getValue() == null) {
					ageTypeField.clear();
				} else {
					if (ageTypeField.isEmpty()) {
						ageTypeField.setValue(ApproximateAgeType.YEARS);
					}
				}
			}
		});

		addFieldListeners(PersonDto.BIRTH_DATE_DD, e -> {
			updateApproximateAge();
			updateReadyOnlyApproximateAge();
		});

		addFieldListeners(PersonDto.BIRTH_DATE_MM, e -> {
			updateApproximateAge();
			updateReadyOnlyApproximateAge();
		});

		addFieldListeners(PersonDto.BIRTH_DATE_YYYY, e -> {
			updateApproximateAge();
			updateReadyOnlyApproximateAge();
		});

		addFieldListeners(PersonDto.DEATH_DATE, e -> updateApproximateAge());

		addListenersToInfrastructureFields(
			cbPlaceOfBirthRegion,
			cbPlaceOfBirthDistrict,
			cbPlaceOfBirthCommunity,
			placeOfBirthFacilityType,
			cbPlaceOfBirthFacility,
			tfPlaceOfBirthFacilityDetails,
			true);
		cbPlaceOfBirthRegion.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());

		this.presentConditionChangeListener = new PresentConditionChangeListener();
		addFieldListeners(PersonDto.PRESENT_CONDITION, presentConditionChangeListener);

		causeOfDeathField.addValueChangeListener(e -> {
			boolean causeOfDeathVisible = presentCondition.getValue() != PresentCondition.ALIVE
				&& presentCondition.getValue() != PresentCondition.UNKNOWN
				&& presentCondition.getValue() != null;
			toggleCauseOfDeathFields(causeOfDeathVisible);
		});

		causeOfDeathDiseaseField.addValueChangeListener(e -> {
			boolean causeOfDeathVisible = presentCondition.getValue() != PresentCondition.ALIVE
				&& presentCondition.getValue() != PresentCondition.UNKNOWN
				&& presentCondition.getValue() != null;
			toggleCauseOfDeathFields(causeOfDeathVisible);
		});

		addValueChangeListener(e -> fillDeathAndBurialFields(deathPlaceType, deathPlaceDesc, burialPlaceDesc));

		deathDate.addValidator(
			new DateComparisonValidator(
				deathDate,
				this::calcBirthDateValue,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, deathDate.getCaption(), birthDateYear.getCaption())));
		deathDate.addValidator(
			new DateComparisonValidator(
				deathDate,
				burialDate,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, deathDate.getCaption(), burialDate.getCaption())));
		deathDate.addValueChangeListener(value -> {
			deathDate.setValidationVisible(!deathDate.isValid());
			burialDate.setValidationVisible(!burialDate.isValid());
		});
		burialDate.addValidator(
			new DateComparisonValidator(
				burialDate,
				this::calcBirthDateValue,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, burialDate.getCaption(), birthDateYear.getCaption())));
		burialDate.addValidator(
			new DateComparisonValidator(
				burialDate,
				deathDate,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, burialDate.getCaption(), deathDate.getCaption())));
		burialDate.addValueChangeListener(b -> {
			deathDate.setValidationVisible(!deathDate.isValid());
			burialDate.setValidationVisible(!burialDate.isValid());
		});

		// Update the list of days according to the selected month and year
		birthDateYear.addValueChangeListener(e -> {
			updateListOfDays((Integer) e.getProperty().getValue(), (Integer) birthDateMonth.getValue());
			birthDateMonth.markAsDirty();
			birthDateDay.markAsDirty();
			deathDate.setValidationVisible(!deathDate.isValid());
			burialDate.setValidationVisible(!burialDate.isValid());
		});
		birthDateMonth.addValueChangeListener(e -> {
			updateListOfDays((Integer) birthDateYear.getValue(), (Integer) e.getProperty().getValue());
			birthDateYear.markAsDirty();
			birthDateDay.markAsDirty();
			deathDate.setValidationVisible(!deathDate.isValid());
			burialDate.setValidationVisible(!burialDate.isValid());
		});
		birthDateDay.addValueChangeListener(e -> {
			birthDateYear.markAsDirty();
			birthDateMonth.markAsDirty();
			deathDate.setValidationVisible(!deathDate.isValid());
			burialDate.setValidationVisible(!burialDate.isValid());
		});

		addValueChangeListener((e) -> {
			ValidationUtils.initComponentErrorValidator(
				externalTokenField,
				getValue().getExternalToken(),
				Validations.duplicateExternalToken,
				externalTokenWarningLabel,
				(externalToken) -> FacadeProvider.getPersonFacade().doesExternalTokenExist(externalToken, getValue().getUuid()));

			personContactDetailsField.setThisPerson((PersonDto) e.getProperty().getValue());
		});

		Label generalCommentLabel = new Label(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.ADDITIONAL_DETAILS));
		generalCommentLabel.addStyleName(H3);
		getContent().addComponent(generalCommentLabel, GENERAL_COMMENT_LOC);

		TextArea additionalDetails = addField(PersonDto.ADDITIONAL_DETAILS, TextArea.class, new ResizableTextAreaWrapper<>(false));
		additionalDetails.setRows(6);
		additionalDetails.setDescription(
			I18nProperties.getPrefixDescription(PersonDto.I18N_PREFIX, PersonDto.ADDITIONAL_DETAILS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));
		CssStyles.style(additionalDetails, CssStyles.CAPTION_HIDDEN);
	}

	@Override
	public void setValue(PersonDto newFieldValue) {
		super.setValue(newFieldValue);
		initializePresentConditionField();

		FieldHelper.updateItems(
			occupationTypeField,
			FacadeProvider.getCustomizableEnumFacade()
				.getEnumValues(
					CustomizableEnumType.OCCUPATION_TYPE,
					Optional.ofNullable(getValue().getOccupationType()).map(CustomizableEnum::getValue).orElse(null),
					null));

		// HACK: Binding to the fields will call field listeners that may clear/modify the values of other fields.
		// this hopefully resets everything to its correct value
		addressForm.discard();
	}

	private void addListenersToInfrastructureFields(
		ComboBox regionField,
		ComboBox districtField,
		ComboBox communityField,
		ComboBox typeField,
		ComboBox facilityField,
		TextField detailsField,
		boolean allowNoneFacility) {

		regionField.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(districtField, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});

		districtField.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(
				communityField,
				districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
			updateFacilities(facilityField, typeField, communityField, districtField, allowNoneFacility);

		});

		communityField.addValueChangeListener(e -> updateFacilities(facilityField, typeField, communityField, districtField, allowNoneFacility));
		typeField.addValueChangeListener(e -> updateFacilities(facilityField, typeField, communityField, districtField, allowNoneFacility));
		FieldHelper.updateItems(
			facilityField,
			Collections.singletonList(FacadeProvider.getFacilityFacade().getReferenceByUuid(FacilityDto.NONE_FACILITY_UUID)));

		facilityField.addValueChangeListener(e -> {
			updateFacilityDetailsVisibility(detailsField, (FacilityReferenceDto) e.getProperty().getValue());
		});
		// Set initial visibility
		updateFacilityDetailsVisibility(detailsField, (FacilityReferenceDto) facilityField.getValue());
	}

	private void updateFacilities(
		ComboBox facilityField,
		ComboBox typeField,
		ComboBox communityField,
		ComboBox districtField,
		boolean allowNoneFacility) {
		if (typeField.getValue() != null) {
			FieldHelper.updateItems(
				facilityField,
				communityField.getValue() != null
					? FacadeProvider.getFacilityFacade()
						.getActiveFacilitiesByCommunityAndType(
							(CommunityReferenceDto) communityField.getValue(),
							(FacilityType) typeField.getValue(),
							true,
							false)
					: districtField.getValue() != null
						? FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByDistrictAndType(
								(DistrictReferenceDto) districtField.getValue(),
								(FacilityType) typeField.getValue(),
								true,
								false)
						: null);
		} else {
			if (allowNoneFacility) {
				// "home or other place" as fallback
				FieldHelper.updateItems(
					facilityField,
					Collections.singletonList(FacadeProvider.getFacilityFacade().getReferenceByUuid(FacilityDto.NONE_FACILITY_UUID)));
			} else {
				FieldHelper.removeItems(facilityField);
			}
		}

	}

	private void updateListOfDays(Integer selectedYear, Integer selectedMonth) {
		if (!birthDateDay.isReadOnly()) {
			Integer currentlySelected = (Integer) birthDateDay.getValue();
			birthDateDay.removeAllItems();
			birthDateDay.addItems(DateHelper.getDaysInMonth(selectedMonth, selectedYear));
			if (birthDateDay.containsId(currentlySelected)) {
				birthDateDay.setValue(currentlySelected);
			}
		}
	}

	private void initializePresentConditionField() {
		PresentCondition presentCondition = getValue().getPresentCondition();
		ComboBox presentConditionField = getField(PersonDto.PRESENT_CONDITION);

		if (this.disease != null || FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease() != null) {
			Disease disease = this.disease != null ? this.disease : FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease();
			FieldVisibilityCheckers fieldVisibilityCheckers = FieldVisibilityCheckers.withDisease(disease);
			List<PresentCondition> validValues = Arrays.stream(PresentCondition.values())
				.filter(c -> fieldVisibilityCheckers.isVisible(PresentCondition.class, c.name()))
				.collect(Collectors.toList());
			PresentCondition currentValue = (PresentCondition) presentConditionField.getValue();
			if (currentValue != null && !validValues.contains(currentValue)) {
				validValues.add(currentValue);
			}
			presentConditionField.removeValueChangeListener(presentConditionChangeListener);
			FieldHelper.updateEnumData(presentConditionField, validValues);
			presentConditionField.addValueChangeListener(presentConditionChangeListener);
		}

		/*
		 * It may happen that the person currently has a present condition that usually shall not be shows for the form's disease.
		 * In that case, the present condition is added as selectable item here.
		 */
		if (presentCondition != null && presentConditionField.getItem(presentCondition) == null) {
			Item currentItem = presentConditionField.addItem(presentCondition);
			currentItem.getItemProperty(SormasFieldGroupFieldFactory.CAPTION_PROPERTY_ID).setValue(presentCondition.toString());
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	private void updateReadyOnlyApproximateAge() {
		boolean readonly = false;
		if (getFieldGroup().getField(PersonDto.BIRTH_DATE_YYYY).getValue() != null) {
			readonly = true;
		}

		getFieldGroup().getField(PersonDto.APPROXIMATE_AGE).setReadOnly(readonly);
		getFieldGroup().getField(PersonDto.APPROXIMATE_AGE_TYPE).setReadOnly(readonly);
	}

	private Date calcBirthDateValue() {
		if (getFieldGroup().getField(PersonDto.BIRTH_DATE_YYYY).getValue() != null) {
			Calendar birthDateCalendar = new GregorianCalendar();
			birthDateCalendar.set(
				(Integer) getFieldGroup().getField(PersonDto.BIRTH_DATE_YYYY).getValue(),
				getFieldGroup().getField(PersonDto.BIRTH_DATE_MM).getValue() != null
					? (Integer) getFieldGroup().getField(PersonDto.BIRTH_DATE_MM).getValue() - 1
					: 0,
				getFieldGroup().getField(PersonDto.BIRTH_DATE_DD).getValue() != null
					? (Integer) getFieldGroup().getField(PersonDto.BIRTH_DATE_DD).getValue()
					: 1);
			return birthDateCalendar.getTime();
		}
		return null;
	}

	private void updateApproximateAge() {
		String approximateAge = null;
		ApproximateAgeType approximateAgeType = null;

		Date birthDate = calcBirthDateValue();
		if (birthDate != null) {
			Pair<Integer, ApproximateAgeType> pair =
				ApproximateAgeHelper.getApproximateAge(birthDate, (Date) getFieldGroup().getField(PersonDto.DEATH_DATE).getValue());
			if (pair.getElement0() != null) {
				approximateAge = String.valueOf(pair.getElement0());
			}
			approximateAgeType = pair.getElement1();
		}

		TextField approximateAgeField = (TextField) getFieldGroup().getField(PersonDto.APPROXIMATE_AGE);
		approximateAgeField.setReadOnly(false);
		approximateAgeField.setValue(approximateAge);
		approximateAgeField.setReadOnly(true);

		AbstractSelect approximateAgeTypeSelect = (AbstractSelect) getFieldGroup().getField(PersonDto.APPROXIMATE_AGE_TYPE);
		approximateAgeTypeSelect.setReadOnly(false);
		approximateAgeTypeSelect.setValue(approximateAgeType);
		approximateAgeTypeSelect.setReadOnly(true);
	}

	private void updateFacilityDetailsVisibility(TextField detailsField, FacilityReferenceDto facility) {
		if (facility == null) {
			detailsField.setVisible(false);
			detailsField.clear();
			return;
		}

		boolean otherFacility = facility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
		boolean noneFacility = facility.getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
		boolean visibleAndRequired = otherFacility || noneFacility;

		detailsField.setVisible(visibleAndRequired);

		if (otherFacility) {
			detailsField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
		}
		if (noneFacility) {
			detailsField.setCaption(I18nProperties.getCaption(Captions.CaseData_noneHealthFacilityDetails));
		}
		if (!visibleAndRequired) {
			detailsField.clear();
		}
	}

	private void toggleCauseOfDeathFields(boolean causeOfDeathVisible) {
		if (!causeOfDeathVisible) {
			causeOfDeathField.setVisible(false);
			causeOfDeathDiseaseField.setVisible(false);
			causeOfDeathDetailsField.setVisible(false);

			causeOfDeathField.setValue(null);
			causeOfDeathDiseaseField.setValue(null);
			causeOfDeathDetailsField.setValue(null);
			getField(PersonDto.DEATH_PLACE_TYPE).setValue(null);
			getField(PersonDto.DEATH_PLACE_DESCRIPTION).setValue(null);

		} else {
			if (isVisibleAllowed(causeOfDeathField)) {
				causeOfDeathField.setVisible(true);
			}

			if (causeOfDeathField.getValue() == null) {
				causeOfDeathDiseaseField.setVisible(false);
				causeOfDeathDetailsField.setVisible(false);
				causeOfDeathDiseaseField.setValue(null);
				causeOfDeathDetailsField.setValue(null);
			} else if (causeOfDeathField.getValue() == CauseOfDeath.EPIDEMIC_DISEASE) {
				if (isVisibleAllowed(causeOfDeathDiseaseField)) {
					causeOfDeathDiseaseField.setVisible(true);
				}
				if (causeOfDeathDiseaseField.getValue() == Disease.OTHER) {
					if (isVisibleAllowed(causeOfDeathDetailsField)) {
						causeOfDeathDetailsField.setVisible(true);
					}
				} else {
					causeOfDeathDetailsField.setVisible(false);
				}
				if (causeOfDeathDiseaseField.getValue() == null) {
					causeOfDeathDiseaseField.setValue(disease);
				}
				if (disease == Disease.OTHER) {
					causeOfDeathDetailsField.setValue(diseaseDetails);
				}
			} else {
				causeOfDeathDiseaseField.setVisible(false);
				causeOfDeathDiseaseField.setValue(null);
				if (isVisibleAllowed(causeOfDeathDetailsField)) {
					causeOfDeathDetailsField.setVisible(true);
				}
			}
		}
	}

	private void setItemCaptionsForMonths(AbstractSelect months) {
		months.setItemCaption(1, I18nProperties.getEnumCaption(Month.JANUARY));
		months.setItemCaption(2, I18nProperties.getEnumCaption(Month.FEBRUARY));
		months.setItemCaption(3, I18nProperties.getEnumCaption(Month.MARCH));
		months.setItemCaption(4, I18nProperties.getEnumCaption(Month.APRIL));
		months.setItemCaption(5, I18nProperties.getEnumCaption(Month.MAY));
		months.setItemCaption(6, I18nProperties.getEnumCaption(Month.JUNE));
		months.setItemCaption(7, I18nProperties.getEnumCaption(Month.JULY));
		months.setItemCaption(8, I18nProperties.getEnumCaption(Month.AUGUST));
		months.setItemCaption(9, I18nProperties.getEnumCaption(Month.SEPTEMBER));
		months.setItemCaption(10, I18nProperties.getEnumCaption(Month.OCTOBER));
		months.setItemCaption(11, I18nProperties.getEnumCaption(Month.NOVEMBER));
		months.setItemCaption(12, I18nProperties.getEnumCaption(Month.DECEMBER));
	}

	private void fillDeathAndBurialFields(AbstractSelect deathPlaceType, TextField deathPlaceDesc, TextField burialPlaceDesc) {

		if (deathPlaceType.isVisible() && deathPlaceType.getValue() == null) {
			if (deathPlaceDesc.isVisible() && StringUtils.isBlank(deathPlaceDesc.getValue())) {
				deathPlaceDesc.setValue(getValue().getAddress().buildCaption());
			}
		}

		if (burialPlaceDesc.isVisible() && StringUtils.isBlank(burialPlaceDesc.getValue())) {
			burialPlaceDesc.setValue(getValue().getAddress().buildCaption());
		}
	}

	public Field getFirstNameField() {
		return firstNameField;
	}

	public Field getLastNameField() {
		return lastNameField;
	}

	@Override
	protected <F extends Field> F addFieldToLayout(CustomLayout layout, String propertyId, F field) {
		field.addValueChangeListener(e -> fireValueChange(false));

		return super.addFieldToLayout(layout, propertyId, field);
	}

	@Override
	public void setHeading(String heading) {
		personInformationHeadingLabel.setValue(heading);
	}

	private class PresentConditionChangeListener implements ValueChangeListener {

		@Override
		public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
			PresentCondition type = (PresentCondition) ((AbstractSelect) getFieldGroup().getField(PersonDto.PRESENT_CONDITION)).getValue();
			if (type == null) {
				setVisible(
					false,
					PersonDto.DEATH_DATE,
					PersonDto.DEATH_PLACE_TYPE,
					PersonDto.DEATH_PLACE_DESCRIPTION,
					PersonDto.BURIAL_DATE,
					PersonDto.BURIAL_PLACE_DESCRIPTION,
					PersonDto.BURIAL_CONDUCTOR);
				getField(PersonDto.DEATH_DATE).setValue(null);
				getField(PersonDto.BURIAL_DATE).setValue(null);
				getField(PersonDto.BURIAL_PLACE_DESCRIPTION).setValue(null);
				getField(PersonDto.BURIAL_CONDUCTOR).setValue(null);
				toggleCauseOfDeathFields(false);
			} else {
				switch (type) {
				case DEAD:
					setVisible(true, PersonDto.DEATH_DATE, PersonDto.DEATH_PLACE_TYPE, PersonDto.DEATH_PLACE_DESCRIPTION);
					causeOfDeathField.setValue(CauseOfDeath.EPIDEMIC_DISEASE);
					toggleCauseOfDeathFields(true);
					setVisible(false, PersonDto.BURIAL_DATE, PersonDto.BURIAL_PLACE_DESCRIPTION, PersonDto.BURIAL_CONDUCTOR);

					getField(PersonDto.BURIAL_DATE).setValue(null);
					getField(PersonDto.BURIAL_PLACE_DESCRIPTION).setValue(null);
					getField(PersonDto.BURIAL_CONDUCTOR).setValue(null);

					break;
				case BURIED:
					setVisible(true, PersonDto.DEATH_DATE, PersonDto.DEATH_PLACE_TYPE, PersonDto.DEATH_PLACE_DESCRIPTION);
					causeOfDeathField.setValue(CauseOfDeath.EPIDEMIC_DISEASE);
					//@formatter:off
						setVisible(fieldVisibilityCheckers.isVisible(PersonDto.class, PersonDto.BURIAL_DATE), PersonDto.BURIAL_DATE);
						setVisible(fieldVisibilityCheckers.isVisible(PersonDto.class, PersonDto.BURIAL_PLACE_DESCRIPTION), PersonDto.BURIAL_PLACE_DESCRIPTION);
						setVisible(fieldVisibilityCheckers.isVisible(PersonDto.class, PersonDto.BURIAL_CONDUCTOR), PersonDto.BURIAL_CONDUCTOR);
						//@formatter:on
					toggleCauseOfDeathFields(true);
					break;
				default:
					setVisible(
						false,
						PersonDto.DEATH_DATE,
						PersonDto.DEATH_PLACE_TYPE,
						PersonDto.DEATH_PLACE_DESCRIPTION,
						PersonDto.BURIAL_DATE,
						PersonDto.BURIAL_PLACE_DESCRIPTION,
						PersonDto.BURIAL_CONDUCTOR);
					getField(PersonDto.DEATH_DATE).setValue(null);
					getField(PersonDto.BURIAL_DATE).setValue(null);
					getField(PersonDto.BURIAL_PLACE_DESCRIPTION).setValue(null);
					getField(PersonDto.BURIAL_CONDUCTOR).setValue(null);
					toggleCauseOfDeathFields(false);
					break;
				}
			}

			fillDeathAndBurialFields(
				getField(PersonDto.DEATH_PLACE_TYPE),
				getField(PersonDto.DEATH_PLACE_DESCRIPTION),
				getField(PersonDto.BURIAL_PLACE_DESCRIPTION));
		}
	}
}
