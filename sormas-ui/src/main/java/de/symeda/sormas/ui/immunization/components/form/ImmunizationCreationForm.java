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

package de.symeda.sormas.ui.immunization.components.form;

import static de.symeda.sormas.ui.utils.CssStyles.ERROR_COLOR_PRIMARY;
import static de.symeda.sormas.ui.utils.CssStyles.FORCE_CAPTION;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.SOFT_REQUIRED;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.style;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Collections;

import com.vaadin.ui.Button;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.PersonDependentEditForm;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Label;
import com.vaadin.v7.data.validator.EmailValidator;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.InfrastructureFieldsHelper;
import de.symeda.sormas.ui.utils.NumberValidator;
import de.symeda.sormas.ui.utils.PhoneNumberValidator;

public class ImmunizationCreationForm extends PersonDependentEditForm<ImmunizationDto> {

	private static final String OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS = "overwriteImmunizationManagementStatus";
	private static final String RESPONSIBLE_JURISDICTION_HEADING_LOC = "responsibleJurisdictionHeadingLoc";
	private static final String FACILITY_TYPE_GROUP_LOC = "facilityTypeGroupLoc";
	private static final String VACCINATION_HEADING_LOC = "vaccinationHeadingLoc";

	//@formatter:off
	private static final String HTML_LAYOUT = fluidRowLocs(ImmunizationDto.REPORT_DATE, ImmunizationDto.EXTERNAL_ID)
		+ fluidRowLocs(ImmunizationDto.DISEASE, ImmunizationDto.DISEASE_DETAILS)
		+ fluidRowLocs(ImmunizationDto.MEANS_OF_IMMUNIZATION, ImmunizationDto.MEANS_OF_IMMUNIZATION_DETAILS)
		+ fluidRowLocs(OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS)
		+ fluidRowLocs(ImmunizationDto.IMMUNIZATION_MANAGEMENT_STATUS, ImmunizationDto.IMMUNIZATION_STATUS)
		+ fluidRowLocs(RESPONSIBLE_JURISDICTION_HEADING_LOC)
		+ fluidRowLocs(ImmunizationDto.RESPONSIBLE_REGION, ImmunizationDto.RESPONSIBLE_DISTRICT, ImmunizationDto.RESPONSIBLE_COMMUNITY)
		+ fluidRowLocs(FACILITY_TYPE_GROUP_LOC, ImmunizationDto.FACILITY_TYPE)
		+ fluidRowLocs(ImmunizationDto.HEALTH_FACILITY, ImmunizationDto.HEALTH_FACILITY_DETAILS)
		+ fluidRowLocs(ImmunizationDto.START_DATE, ImmunizationDto.END_DATE)
		+ fluidRowLocs(ImmunizationDto.VALID_FROM, ImmunizationDto.VALID_UNTIL)
		+ fluidRowLocs(VACCINATION_HEADING_LOC)
		+ fluidRow(fluidColumnLoc(6, 0, ImmunizationDto.NUMBER_OF_DOSES))
		+ LayoutUtil.fluidRowLocs(6, PersonDto.FIRST_NAME, 4, PersonDto.LAST_NAME, 2, PERSON_SEARCH_LOC)
		+ fluidRow(fluidRowLocs(PersonDto.BIRTH_DATE_YYYY, PersonDto.BIRTH_DATE_MM, PersonDto.BIRTH_DATE_DD),
		fluidRowLocs(PersonDto.SEX))
		+ fluidRowLocs(PersonDto.NATIONAL_HEALTH_ID, PersonDto.PASSPORT_NUMBER)
		+ fluidRowLocs(PersonDto.PRESENT_CONDITION, "")
		+ fluidRowLocs(PersonDto.PHONE, PersonDto.EMAIL_ADDRESS);
	//@formatter:on

	private ComboBox birthDateDay;
	private final PersonReferenceDto personDto;
	private final Disease disease;

	public ImmunizationCreationForm() {
		this(null, null);
	}

	public ImmunizationCreationForm(PersonReferenceDto personDto, Disease disease) {
		super(
			ImmunizationDto.class,
			ImmunizationDto.I18N_PREFIX,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()));
		this.personDto = personDto;
		this.disease = disease;
		setWidth(720, Unit.PIXELS);
		hideValidationUntilNextCommit();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {
		addField(ImmunizationDto.REPORT_DATE, DateField.class);

		TextField externalIdField = addField(ImmunizationDto.EXTERNAL_ID, TextField.class);
		style(externalIdField, ERROR_COLOR_PRIMARY);

		addDiseaseField(ImmunizationDto.DISEASE, false, true);
		addField(ImmunizationDto.DISEASE_DETAILS, TextField.class);

		ComboBox meansOfImmunizationField = addField(ImmunizationDto.MEANS_OF_IMMUNIZATION, ComboBox.class);
		addField(ImmunizationDto.MEANS_OF_IMMUNIZATION_DETAILS, TextField.class);

		CheckBox overwriteImmunizationManagementStatus = addCustomField(OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS, Boolean.class, CheckBox.class);
		overwriteImmunizationManagementStatus.addStyleName(VSPACE_3);

		ComboBox managementStatusField =
			addCustomField(ImmunizationDto.IMMUNIZATION_MANAGEMENT_STATUS, ImmunizationManagementStatus.class, ComboBox.class);
		managementStatusField.setValue(ImmunizationManagementStatus.SCHEDULED);
		managementStatusField.setEnabled(false);
		managementStatusField.setNullSelectionAllowed(false);

		ComboBox immunizationStatusField = addCustomField(ImmunizationDto.IMMUNIZATION_STATUS, ImmunizationStatus.class, ComboBox.class);
		immunizationStatusField.setValue(ImmunizationStatus.PENDING);
		immunizationStatusField.setEnabled(false);

		Label jurisdictionHeadingLabel = new Label(I18nProperties.getString(Strings.headingResponsibleJurisdiction));
		jurisdictionHeadingLabel.addStyleName(H3);
		getContent().addComponent(jurisdictionHeadingLabel, RESPONSIBLE_JURISDICTION_HEADING_LOC);

		ComboBox responsibleRegion = addInfrastructureField(ImmunizationDto.RESPONSIBLE_REGION);
		responsibleRegion.setRequired(true);
		ComboBox responsibleDistrictCombo = addInfrastructureField(ImmunizationDto.RESPONSIBLE_DISTRICT);
		responsibleDistrictCombo.setRequired(true);
		ComboBox responsibleCommunityCombo = addInfrastructureField(ImmunizationDto.RESPONSIBLE_COMMUNITY);
		responsibleCommunityCombo.setNullSelectionAllowed(true);
		responsibleCommunityCombo.addStyleName(SOFT_REQUIRED);

		InfrastructureFieldsHelper.initInfrastructureFields(responsibleRegion, responsibleDistrictCombo, responsibleCommunityCombo);

		ComboBox facilityTypeGroup = ComboBoxHelper.createComboBoxV7();
		facilityTypeGroup.setId("typeGroup");
		facilityTypeGroup.setCaption(I18nProperties.getCaption(Captions.Facility_typeGroup));
		facilityTypeGroup.setWidth(100, Unit.PERCENTAGE);
		facilityTypeGroup.addItems(FacilityTypeGroup.getAccomodationGroups());
		getContent().addComponent(facilityTypeGroup, FACILITY_TYPE_GROUP_LOC);
		ComboBox facilityType = ComboBoxHelper.createComboBoxV7();
		facilityType.setId("type");
		facilityType.setCaption(I18nProperties.getCaption(Captions.facilityType));
		facilityType.setWidth(100, Unit.PERCENTAGE);
		getContent().addComponent(facilityType, ImmunizationDto.FACILITY_TYPE);
		ComboBox facilityCombo = addInfrastructureField(ImmunizationDto.HEALTH_FACILITY);
		facilityCombo.setImmediate(true);
		TextField facilityDetails = addField(ImmunizationDto.HEALTH_FACILITY_DETAILS, TextField.class);
		facilityDetails.setVisible(false);

		DateField startDate = addField(ImmunizationDto.START_DATE, DateField.class);
		DateField endDate = addDateField(ImmunizationDto.END_DATE, DateField.class, -1);
		DateComparisonValidator.addStartEndValidators(startDate, endDate);

		DateField validFrom = addDateField(ImmunizationDto.VALID_FROM, DateField.class, -1);
		DateField validUntil = addDateField(ImmunizationDto.VALID_UNTIL, DateField.class, -1);
		DateComparisonValidator.addStartEndValidators(validFrom, validUntil);

		Field numberOfDosesField = addField(ImmunizationDto.NUMBER_OF_DOSES);
		numberOfDosesField.addValidator(new NumberValidator(I18nProperties.getValidationError(Validations.vaccineDosesFormat), 1, 10, false));
		numberOfDosesField.setVisible(false);

		addCustomField(PersonDto.FIRST_NAME, String.class, TextField.class);
		addCustomField(PersonDto.LAST_NAME, String.class, TextField.class);

		Button searchPersonButton = createPersonSearchButton(PERSON_SEARCH_LOC);
		getContent().addComponent(searchPersonButton, PERSON_SEARCH_LOC);

		addCustomField(PersonDto.NATIONAL_HEALTH_ID, String.class, TextField.class);
		addCustomField(PersonDto.PASSPORT_NUMBER, String.class, TextField.class);

		birthDateDay = addCustomField(PersonDto.BIRTH_DATE_DD, Integer.class, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateDay.setNullSelectionAllowed(true);
		birthDateDay.addStyleName(FORCE_CAPTION);
		birthDateDay.setInputPrompt(I18nProperties.getString(Strings.day));
		ComboBox birthDateMonth = addCustomField(PersonDto.BIRTH_DATE_MM, Integer.class, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateMonth.setNullSelectionAllowed(true);
		birthDateMonth.addItems(DateHelper.getMonthsInYear());
		birthDateMonth.setPageLength(12);
		birthDateMonth.addStyleName(FORCE_CAPTION);
		birthDateMonth.setInputPrompt(I18nProperties.getString(Strings.month));
		setItemCaptionsForMonths(birthDateMonth);
		ComboBox birthDateYear = addCustomField(PersonDto.BIRTH_DATE_YYYY, Integer.class, ComboBox.class);
		birthDateYear.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE));
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateYear.setNullSelectionAllowed(true);
		birthDateYear.addItems(DateHelper.getYearsToNow());
		birthDateYear.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID_TOSTRING);
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

		// Update the list of days according to the selected month and year
		birthDateYear.addValueChangeListener(e -> {
			updateListOfDays((Integer) e.getProperty().getValue(), (Integer) birthDateMonth.getValue());
			birthDateMonth.markAsDirty();
			birthDateDay.markAsDirty();
		});
		birthDateMonth.addValueChangeListener(e -> {
			updateListOfDays((Integer) birthDateYear.getValue(), (Integer) e.getProperty().getValue());
			birthDateYear.markAsDirty();
			birthDateDay.markAsDirty();
		});
		birthDateDay.addValueChangeListener(e -> {
			birthDateYear.markAsDirty();
			birthDateMonth.markAsDirty();
		});

		ComboBox sex = addCustomField(PersonDto.SEX, Sex.class, ComboBox.class);
		sex.setCaption(I18nProperties.getCaption(Captions.Person_sex));
		ComboBox presentCondition = addCustomField(PersonDto.PRESENT_CONDITION, PresentCondition.class, ComboBox.class);
		presentCondition.setCaption(I18nProperties.getCaption(Captions.Person_presentCondition));

		TextField phone = addCustomField(PersonDto.PHONE, String.class, TextField.class);
		phone.setCaption(I18nProperties.getCaption(Captions.Person_phone));
		TextField email = addCustomField(PersonDto.EMAIL_ADDRESS, String.class, TextField.class);
		email.setCaption(I18nProperties.getCaption(Captions.Person_emailAddress));

		phone.addValidator(new PhoneNumberValidator(I18nProperties.getValidationError(Validations.validPhoneNumber, phone.getCaption())));
		email.addValidator(new EmailValidator(I18nProperties.getValidationError(Validations.validEmailAddress, email.getCaption())));

		// Set initial visibilities & accesses
		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(true, ImmunizationDto.REPORT_DATE, ImmunizationDto.MEANS_OF_IMMUNIZATION);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(ImmunizationDto.DISEASE_DETAILS),
			ImmunizationDto.DISEASE,
			Collections.singletonList(Disease.OTHER),
			true);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			ImmunizationDto.DISEASE,
			Collections.singletonList(ImmunizationDto.DISEASE_DETAILS),
			Collections.singletonList(Disease.OTHER));

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(ImmunizationDto.MEANS_OF_IMMUNIZATION_DETAILS),
			ImmunizationDto.MEANS_OF_IMMUNIZATION,
			Collections.singletonList(MeansOfImmunization.OTHER),
			true);

		overwriteImmunizationManagementStatus.addValueChangeListener(e -> {
			boolean selectedValue = (boolean) e.getProperty().getValue();
			if (!selectedValue) {
				managementStatusField.setValue(ImmunizationManagementStatus.SCHEDULED);
			}
			managementStatusField.setEnabled(selectedValue);
		});

		meansOfImmunizationField.addValueChangeListener(e -> {
			MeansOfImmunization meansOfImmunization = (MeansOfImmunization) e.getProperty().getValue();
			if (MeansOfImmunization.RECOVERY.equals(meansOfImmunization) || MeansOfImmunization.OTHER.equals(meansOfImmunization)) {
				managementStatusField.setValue(ImmunizationManagementStatus.COMPLETED);
			} else {
				managementStatusField.setValue(ImmunizationManagementStatus.SCHEDULED);
			}
			boolean isVaccinationVisible =
				MeansOfImmunization.VACCINATION.equals(meansOfImmunization) || MeansOfImmunization.VACCINATION_RECOVERY.equals(meansOfImmunization);
			numberOfDosesField.setVisible(isVaccinationVisible);
			if (!isVaccinationVisible) {
				numberOfDosesField.setValue(null);
			}
		});

		managementStatusField.addValueChangeListener(e -> {
			ImmunizationManagementStatus managementStatusValue = (ImmunizationManagementStatus) e.getProperty().getValue();
			switch (managementStatusValue) {
			case SCHEDULED:
			case ONGOING:
				immunizationStatusField.setValue(ImmunizationStatus.PENDING);
				break;
			case COMPLETED:
				immunizationStatusField.setValue(ImmunizationStatus.ACQUIRED);
				break;
			case CANCELED:
				immunizationStatusField.setValue(ImmunizationStatus.NOT_ACQUIRED);
				break;
			default:
				break;
			}
		});

		responsibleDistrictCombo.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			if (districtDto != null && facilityType.getValue() != null) {
				FieldHelper.updateItems(
					facilityCombo,
					FacadeProvider.getFacilityFacade()
						.getActiveFacilitiesByDistrictAndType(districtDto, (FacilityType) facilityType.getValue(), true, false));
			}
		});

		responsibleCommunityCombo.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
			if (facilityType.getValue() != null) {
				FieldHelper.updateItems(
					facilityCombo,
					communityDto != null
						? FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByCommunityAndType(communityDto, (FacilityType) facilityType.getValue(), true, false)
						: responsibleDistrictCombo.getValue() != null
							? FacadeProvider.getFacilityFacade()
								.getActiveFacilitiesByDistrictAndType(
									(DistrictReferenceDto) responsibleDistrictCombo.getValue(),
									(FacilityType) facilityType.getValue(),
									true,
									false)
							: null);
			}
		});

		facilityTypeGroup.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			FieldHelper.updateEnumData(facilityType, FacilityType.getAccommodationTypes((FacilityTypeGroup) facilityTypeGroup.getValue()));
		});
		facilityType.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			if (facilityType.getValue() != null && responsibleDistrictCombo.getValue() != null) {
				if (responsibleCommunityCombo.getValue() != null) {
					FieldHelper.updateItems(
						facilityCombo,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByCommunityAndType(
								(CommunityReferenceDto) responsibleCommunityCombo.getValue(),
								(FacilityType) facilityType.getValue(),
								true,
								false));
				} else {
					FieldHelper.updateItems(
						facilityCombo,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByDistrictAndType(
								(DistrictReferenceDto) responsibleDistrictCombo.getValue(),
								(FacilityType) facilityType.getValue(),
								true,
								false));
				}
			}
		});

		facilityCombo.addValueChangeListener(e -> {
			updateFacilityFields(facilityCombo, facilityDetails);
			this.getValue().setFacilityType((FacilityType) facilityType.getValue());
		});

		addValueChangeListener(e -> {
			if (disease != null) {
				setVisible(false, ImmunizationDto.DISEASE, ImmunizationDto.DISEASE_DETAILS);
				setReadOnly(false, ImmunizationDto.DISEASE, ImmunizationDto.DISEASE_DETAILS);
			} else {
				setRequired(true, ImmunizationDto.DISEASE);
			}
			if (personDto != null) {
				setVisible(
					false,
					PersonDto.FIRST_NAME,
					PersonDto.LAST_NAME,
					PersonDto.SEX,
					PersonDto.NATIONAL_HEALTH_ID,
					PersonDto.PASSPORT_NUMBER,
					PersonDto.BIRTH_DATE_DD,
					PersonDto.BIRTH_DATE_MM,
					PersonDto.BIRTH_DATE_YYYY,
					PersonDto.PRESENT_CONDITION,
					PersonDto.PHONE,
					PersonDto.EMAIL_ADDRESS);
				setReadOnly(
					false,
					PersonDto.FIRST_NAME,
					PersonDto.LAST_NAME,
					PersonDto.SEX,
					PersonDto.NATIONAL_HEALTH_ID,
					PersonDto.PASSPORT_NUMBER,
					PersonDto.BIRTH_DATE_DD,
					PersonDto.BIRTH_DATE_MM,
					PersonDto.BIRTH_DATE_YYYY,
					PersonDto.PRESENT_CONDITION,
					PersonDto.PHONE,
					PersonDto.EMAIL_ADDRESS);

				searchPersonButton.setVisible(false);
			} else {
				setRequired(true, PersonDto.FIRST_NAME, PersonDto.LAST_NAME, PersonDto.SEX);
			}
		});
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

	private void updateListOfDays(Integer selectedYear, Integer selectedMonth) {

		Integer currentlySelected = (Integer) birthDateDay.getValue();
		birthDateDay.removeAllItems();
		birthDateDay.addItems(DateHelper.getDaysInMonth(selectedMonth, selectedYear));
		if (birthDateDay.containsId(currentlySelected)) {
			birthDateDay.setValue(currentlySelected);
		}
	}

	private void updateFacilityFields(ComboBox cbFacility, TextField tfFacilityDetails) {

		if (cbFacility.getValue() != null) {
			boolean otherHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
			boolean noneHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
			boolean visibleAndRequired = otherHealthFacility || noneHealthFacility;

			tfFacilityDetails.setVisible(visibleAndRequired);
			tfFacilityDetails.setRequired(otherHealthFacility);

			if (otherHealthFacility) {
				tfFacilityDetails.setCaption(I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.HEALTH_FACILITY_DETAILS));
			}
			if (noneHealthFacility) {
				tfFacilityDetails.setCaption(I18nProperties.getCaption(Captions.CaseData_noneHealthFacilityDetails));
			}
			if (!visibleAndRequired) {
				tfFacilityDetails.clear();
			}
		} else {
			tfFacilityDetails.setVisible(false);
			tfFacilityDetails.setRequired(false);
			tfFacilityDetails.clear();
		}
	}

	public PersonDto getPerson() {
		PersonDto person = PersonDto.build();

		person.setFirstName((String) getField(PersonDto.FIRST_NAME).getValue());
		person.setLastName((String) getField(PersonDto.LAST_NAME).getValue());
		person.setBirthdateDD((Integer) getField(PersonDto.BIRTH_DATE_DD).getValue());
		person.setBirthdateMM((Integer) getField(PersonDto.BIRTH_DATE_MM).getValue());
		person.setBirthdateYYYY((Integer) getField(PersonDto.BIRTH_DATE_YYYY).getValue());
		person.setSex((Sex) getField(PersonDto.SEX).getValue());
		person.setPresentCondition((PresentCondition) getField(PersonDto.PRESENT_CONDITION).getValue());

		String phone = (String) getField(PersonDto.PHONE).getValue();
		if (StringUtils.isNotEmpty(phone)) {
			person.setPhone(phone);
		}

		String emailAddress = (String) getField(PersonDto.EMAIL_ADDRESS).getValue();
		if (StringUtils.isNotEmpty(emailAddress)) {
			person.setEmailAddress(emailAddress);
		}

		person.setNationalHealthId((String) getField(PersonDto.NATIONAL_HEALTH_ID).getValue());
		person.setPassportNumber((String) getField(PersonDto.PASSPORT_NUMBER).getValue());

		return person;
	}

	@Override
	public ImmunizationDto getValue() {
		ImmunizationDto immunizationDto = super.getValue();
		immunizationDto
			.setImmunizationManagementStatus((ImmunizationManagementStatus) getField(ImmunizationDto.IMMUNIZATION_MANAGEMENT_STATUS).getValue());
		immunizationDto.setImmunizationStatus((ImmunizationStatus) getField(ImmunizationDto.IMMUNIZATION_STATUS).getValue());
		return immunizationDto;
	}

	@Override
	public void setPerson(PersonDto person) {
		if (person != null) {
			((TextField) getField(PersonDto.FIRST_NAME)).setValue(person.getFirstName());
			((TextField) getField(PersonDto.LAST_NAME)).setValue(person.getLastName());
			((ComboBox) getField(PersonDto.BIRTH_DATE_YYYY)).setValue(person.getBirthdateYYYY());
			((ComboBox) getField(PersonDto.BIRTH_DATE_MM)).setValue(person.getBirthdateMM());
			((ComboBox) getField(PersonDto.BIRTH_DATE_DD)).setValue(person.getBirthdateDD());
			((ComboBox) getField(PersonDto.SEX)).setValue(person.getSex());
			((TextField) getField(PersonDto.NATIONAL_HEALTH_ID)).setValue(person.getNationalHealthId());
			((TextField) getField(PersonDto.PASSPORT_NUMBER)).setValue(person.getPassportNumber());
			((ComboBox) getField(PersonDto.PRESENT_CONDITION)).setValue(person.getPresentCondition());
			((TextField) getField(PersonDto.PHONE)).setValue(person.getPhone());
			((TextField) getField(PersonDto.EMAIL_ADDRESS)).setValue(person.getEmailAddress());
		} else {
			getField(PersonDto.FIRST_NAME).clear();
			getField(PersonDto.LAST_NAME).clear();
			getField(PersonDto.BIRTH_DATE_DD).clear();
			getField(PersonDto.BIRTH_DATE_MM).clear();
			getField(PersonDto.BIRTH_DATE_YYYY).clear();
			getField(PersonDto.SEX).clear();
			getField(PersonDto.NATIONAL_HEALTH_ID).clear();
			getField(PersonDto.PASSPORT_NUMBER).clear();
			getField(PersonDto.PRESENT_CONDITION).clear();
			getField(PersonDto.PHONE).clear();
			getField(PersonDto.EMAIL_ADDRESS).clear();
		}
	}

	@Override
	protected void enablePersonFields(Boolean enable) {
		getField(PersonDto.FIRST_NAME).setEnabled(enable);
		getField(PersonDto.LAST_NAME).setEnabled(enable);
		getField(PersonDto.BIRTH_DATE_DD).setEnabled(enable);
		getField(PersonDto.BIRTH_DATE_MM).setEnabled(enable);
		getField(PersonDto.BIRTH_DATE_YYYY).setEnabled(enable);
		getField(PersonDto.SEX).setEnabled(enable);
		getField(PersonDto.NATIONAL_HEALTH_ID).setEnabled(enable);
		getField(PersonDto.PASSPORT_NUMBER).setEnabled(enable);
		getField(PersonDto.PRESENT_CONDITION).setEnabled(enable);
		getField(PersonDto.PHONE).setEnabled(enable);
		getField(PersonDto.EMAIL_ADDRESS).setEnabled(enable);
	}
}
