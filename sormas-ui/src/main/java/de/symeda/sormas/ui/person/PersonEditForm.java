/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.person;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.divsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLocCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.oneOfFourCol;
import static de.symeda.sormas.ui.utils.LayoutUtil.oneOfTwoCol;

import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.symeda.sormas.api.utils.fieldaccess.checkers.SensitiveDataFieldAccessChecker;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.DeathPlaceType;
import de.symeda.sormas.api.person.EducationType;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldaccess.checkers.PersonalDataFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ApproximateAgeValidator;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.OutbreakFieldVisibilityChecker;
import de.symeda.sormas.ui.utils.ViewMode;

public class PersonEditForm extends AbstractEditForm<PersonDto> {

	private static final long serialVersionUID = -1L;

	private static final String OCCUPATION_HEADER = "occupationHeader";
	private static final String ADDRESS_HEADER = "addressHeader";
	private static final String CONTACT_INFORMATION_HEADER = "contactInformationHeader";

	private Label occupationHeader = new Label(I18nProperties.getString(Strings.headingPersonOccupation));
	private Label addressHeader = new Label(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.ADDRESS));
	private Label contactInformationHeader = new Label(I18nProperties.getString(Strings.headingContactInformation));

	private Disease disease;
	private String diseaseDetails;
	private ComboBox causeOfDeathField;
	private ComboBox causeOfDeathDiseaseField;
	private TextField causeOfDeathDetailsField;
	private ComboBox occupationFacility;
	private TextField occupationFacilityDetails;
	private final ViewMode viewMode;
	private ComboBox birthDateDay;

	private static final String HTML_LAYOUT =
			h3(I18nProperties.getString(Strings.headingPersonInformation)) +
					fluidRowLocs(PersonDto.FIRST_NAME, PersonDto.LAST_NAME) +
					fluidRow(
							fluidRowLocs(PersonDto.BIRTH_DATE_YYYY, PersonDto.BIRTH_DATE_MM, PersonDto.BIRTH_DATE_DD),
							fluidRowLocs(PersonDto.APPROXIMATE_AGE, PersonDto.APPROXIMATE_AGE_TYPE, PersonDto.APPROXIMATE_AGE_REFERENCE_DATE)
					) +
					fluidRowLocs(PersonDto.PLACE_OF_BIRTH_REGION, PersonDto.PLACE_OF_BIRTH_DISTRICT, PersonDto.PLACE_OF_BIRTH_COMMUNITY) +
					fluidRowLocs(PersonDto.PLACE_OF_BIRTH_FACILITY, PersonDto.PLACE_OF_BIRTH_FACILITY_DETAILS) +
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

					loc(OCCUPATION_HEADER) +
					divsCss(VSPACE_3,
							fluidRowLocs(PersonDto.OCCUPATION_TYPE, PersonDto.OCCUPATION_DETAILS),
							fluidRowLocs(PersonDto.OCCUPATION_REGION, PersonDto.OCCUPATION_DISTRICT, PersonDto.OCCUPATION_COMMUNITY, PersonDto.OCCUPATION_FACILITY),
							fluidRowLocs("", "", PersonDto.OCCUPATION_FACILITY_DETAILS),
							fluidRowLocs(PersonDto.EDUCATION_TYPE, PersonDto.EDUCATION_DETAILS)
					) +

					loc(ADDRESS_HEADER) +
					divsCss(VSPACE_3, fluidRowLocs(PersonDto.ADDRESS)) +

					loc(CONTACT_INFORMATION_HEADER) +
					divsCss(
							VSPACE_3,
							fluidRowLocs(PersonDto.NICKNAME, PersonDto.MOTHERS_MAIDEN_NAME) +
									fluidRowLocs(PersonDto.MOTHERS_NAME, PersonDto.FATHERS_NAME) +
									fluidRowLocs(PersonDto.PHONE, PersonDto.PHONE_OWNER) +
									fluidRowLocs(PersonDto.EMAIL_ADDRESS, "") +
									loc(PersonDto.GENERAL_PRACTITIONER_DETAILS));

	public PersonEditForm(Disease disease, String diseaseDetails, ViewMode viewMode, boolean isInJurisdiction) {
		super(PersonDto.class, PersonDto.I18N_PREFIX, false,
				new FieldVisibilityCheckers()
						.add(new DiseaseFieldVisibilityChecker(disease))
						.add(new OutbreakFieldVisibilityChecker(viewMode)),
				new FieldAccessCheckers()
						.add(new PersonalDataFieldAccessChecker(r -> UserProvider.getCurrent().hasUserRight(r), isInJurisdiction))
						.add(new SensitiveDataFieldAccessChecker(r -> UserProvider.getCurrent().hasUserRight(r), isInJurisdiction))
		);
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.viewMode = viewMode;

		occupationHeader.addStyleName(CssStyles.H3);
		addressHeader.addStyleName(CssStyles.H3);
		contactInformationHeader.addStyleName(CssStyles.H3);
		getContent().addComponent(occupationHeader, OCCUPATION_HEADER);
		getContent().addComponent(addressHeader, ADDRESS_HEADER);
		getContent().addComponent(contactInformationHeader, CONTACT_INFORMATION_HEADER);

		addFields();
	}

	@Override
	protected void addFields() {
		addField(PersonDto.FIRST_NAME, TextField.class);
		addField(PersonDto.LAST_NAME, TextField.class);
		ComboBox sex = addField(PersonDto.SEX, ComboBox.class);
		addField(PersonDto.NICKNAME, TextField.class);
		addField(PersonDto.MOTHERS_MAIDEN_NAME, TextField.class);
		addFields(PersonDto.MOTHERS_NAME, PersonDto.FATHERS_NAME);
		ComboBox presentCondition = addField(PersonDto.PRESENT_CONDITION, ComboBox.class);
		birthDateDay = addField(PersonDto.BIRTH_DATE_DD, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateDay.setNullSelectionAllowed(true);
		birthDateDay.setInputPrompt(I18nProperties.getString(Strings.day));
		ComboBox birthDateMonth = addField(PersonDto.BIRTH_DATE_MM, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateMonth.setNullSelectionAllowed(true);
		birthDateMonth.addItems(DateHelper.getMonthsInYear());
		birthDateMonth.setPageLength(12);
		birthDateMonth.setInputPrompt(I18nProperties.getString(Strings.month));
		setItemCaptionsForMonths(birthDateMonth);
		ComboBox birthDateYear = addField(PersonDto.BIRTH_DATE_YYYY, ComboBox.class);
		birthDateYear.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE));
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateYear.setNullSelectionAllowed(true);
		birthDateYear.addItems(DateHelper.getYearsToNow());
		birthDateYear.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		birthDateYear.setInputPrompt(I18nProperties.getString(Strings.year));
		DateField deathDate = addField(PersonDto.DEATH_DATE, DateField.class);
		TextField approximateAgeField = addField(PersonDto.APPROXIMATE_AGE, TextField.class);
		approximateAgeField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, approximateAgeField.getCaption()));
		ComboBox approximateAgeTypeField = addField(PersonDto.APPROXIMATE_AGE_TYPE, ComboBox.class);
		addField(PersonDto.APPROXIMATE_AGE_REFERENCE_DATE, DateField.class);

		approximateAgeField.addValidator(new ApproximateAgeValidator(approximateAgeField, approximateAgeTypeField, I18nProperties.getValidationError(Validations.softApproximateAgeTooHigh)));

		TextField tfGestationAgeAtBirth = addField(PersonDto.GESTATION_AGE_AT_BIRTH, TextField.class);
		tfGestationAgeAtBirth.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, tfGestationAgeAtBirth.getCaption()));
		TextField tfBirthWeight = addField(PersonDto.BIRTH_WEIGHT, TextField.class);
		tfBirthWeight.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, tfBirthWeight.getCaption()));

		AbstractSelect deathPlaceType = addField(PersonDto.DEATH_PLACE_TYPE, ComboBox.class);
		deathPlaceType.setNullSelectionAllowed(true);
		TextField deathPlaceDesc = addField(PersonDto.DEATH_PLACE_DESCRIPTION, TextField.class);
		DateField burialDate = addField(PersonDto.BURIAL_DATE, DateField.class);
		TextField burialPlaceDesc = addField(PersonDto.BURIAL_PLACE_DESCRIPTION, TextField.class);
		ComboBox burialConductor = addField(PersonDto.BURIAL_CONDUCTOR, ComboBox.class);
		addField(PersonDto.ADDRESS, LocationEditForm.class).setCaption(null);

		addFields(PersonDto.OCCUPATION_TYPE, PersonDto.OCCUPATION_DETAILS,
				PersonDto.EDUCATION_TYPE, PersonDto.EDUCATION_DETAILS,
				PersonDto.PHONE, PersonDto.PHONE_OWNER, PersonDto.EMAIL_ADDRESS,
				PersonDto.PASSPORT_NUMBER, PersonDto.NATIONAL_HEALTH_ID);

		ComboBox cbPlaceOfBirthRegion = addInfrastructureField(PersonDto.PLACE_OF_BIRTH_REGION);
		ComboBox cbPlaceOfBirthDistrict = addInfrastructureField(PersonDto.PLACE_OF_BIRTH_DISTRICT);
		ComboBox cbPlaceOfBirthCommunity = addInfrastructureField(PersonDto.PLACE_OF_BIRTH_COMMUNITY);
		ComboBox cbPlaceOfBirthFacility = addInfrastructureField(PersonDto.PLACE_OF_BIRTH_FACILITY);
		TextField tfPlaceOfBirthFacilityDetails = addField(PersonDto.PLACE_OF_BIRTH_FACILITY_DETAILS, TextField.class);

		causeOfDeathField = addField(PersonDto.CAUSE_OF_DEATH, ComboBox.class);
		causeOfDeathDiseaseField = addDiseaseField(PersonDto.CAUSE_OF_DEATH_DISEASE, true);
		causeOfDeathDetailsField = addField(PersonDto.CAUSE_OF_DEATH_DETAILS, TextField.class);
		ComboBox facilityRegion = addInfrastructureField(PersonDto.OCCUPATION_REGION);
		facilityRegion.setImmediate(true);
		facilityRegion.setNullSelectionAllowed(true);
		ComboBox facilityDistrict = addInfrastructureField(PersonDto.OCCUPATION_DISTRICT);
		facilityDistrict.setImmediate(true);
		facilityDistrict.setNullSelectionAllowed(true);
		ComboBox facilityCommunity = addInfrastructureField(PersonDto.OCCUPATION_COMMUNITY);
		facilityCommunity.setImmediate(true);
		facilityCommunity.setNullSelectionAllowed(true);
		occupationFacility = addInfrastructureField(PersonDto.OCCUPATION_FACILITY);
		occupationFacility.setImmediate(true);
		occupationFacility.setNullSelectionAllowed(true);
		occupationFacilityDetails = addField(PersonDto.OCCUPATION_FACILITY_DETAILS, TextField.class);

		addField(PersonDto.GENERAL_PRACTITIONER_DETAILS, TextField.class);

		// Set requirements that don't need visibility changes and read only status

		setReadOnly(true,
				PersonDto.APPROXIMATE_AGE_REFERENCE_DATE);
		setRequired(true,
				PersonDto.FIRST_NAME,
				PersonDto.LAST_NAME);
		setVisible(false,
				PersonDto.OCCUPATION_DETAILS,
				PersonDto.OCCUPATION_FACILITY,
				PersonDto.OCCUPATION_FACILITY_DETAILS,
				PersonDto.OCCUPATION_REGION,
				PersonDto.OCCUPATION_DISTRICT,
				PersonDto.OCCUPATION_COMMUNITY,
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

		FieldHelper.addSoftRequiredStyle(presentCondition, sex, deathDate, deathPlaceDesc, deathPlaceType,
				causeOfDeathField, causeOfDeathDiseaseField, causeOfDeathDetailsField,
				burialDate, burialPlaceDesc, burialConductor);

		// Set initial visibilities

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		if (!getField(PersonDto.OCCUPATION_TYPE).isVisible()
				&& !getField(PersonDto.EDUCATION_TYPE).isVisible())
			occupationHeader.setVisible(false);
		if (!getField(PersonDto.ADDRESS).isVisible())
			addressHeader.setVisible(false);

		// Add listeners

		FieldHelper.setRequiredWhenNotNull(getFieldGroup(), PersonDto.APPROXIMATE_AGE, PersonDto.APPROXIMATE_AGE_TYPE);
		addFieldListeners(PersonDto.APPROXIMATE_AGE, e -> {
			@SuppressWarnings("unchecked")
			Field<ApproximateAgeType> ageTypeField = (Field<ApproximateAgeType>) getField(PersonDto.APPROXIMATE_AGE_TYPE);
			if (e.getProperty().getValue() == null) {
				ageTypeField.clear();
			} else {
				if (ageTypeField.isEmpty()) {
					ageTypeField.setValue(ApproximateAgeType.YEARS);
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
		addFieldListeners(PersonDto.OCCUPATION_TYPE, e -> {
			updateOccupationFieldCaptions();
			toogleOccupationMetaFields();
		});

		addListenersToInfrastructureFields(cbPlaceOfBirthRegion, cbPlaceOfBirthDistrict, cbPlaceOfBirthCommunity, cbPlaceOfBirthFacility, tfPlaceOfBirthFacilityDetails);
		addListenersToInfrastructureFields(facilityRegion, facilityDistrict, facilityCommunity, occupationFacility, occupationFacilityDetails);
		cbPlaceOfBirthRegion.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		facilityRegion.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

		addFieldListeners(PersonDto.PRESENT_CONDITION, e -> toogleDeathAndBurialFields());

		causeOfDeathField.addValueChangeListener(e -> {
			toggleCauseOfDeathFields(presentCondition.getValue() != PresentCondition.ALIVE &&
					presentCondition.getValue() != null);
		});

		causeOfDeathDiseaseField.addValueChangeListener(e -> {
			toggleCauseOfDeathFields(presentCondition.getValue() != PresentCondition.ALIVE &&
					presentCondition.getValue() != null);
		});

		addValueChangeListener(e -> {
			fillDeathAndBurialFields(deathPlaceType, deathPlaceDesc, burialPlaceDesc);
		});

		deathDate.addValidator(new DateComparisonValidator(deathDate, this::calcBirthDateValue, false, false,
				I18nProperties.getValidationError(Validations.afterDate, deathDate.getCaption(), birthDateYear.getCaption())));
		burialDate.addValidator(new DateComparisonValidator(burialDate, deathDate, false, false,
				I18nProperties.getValidationError(Validations.afterDate, burialDate.getCaption(), deathDate.getCaption())));

		// Update the list of days according to the selected month and year
		birthDateYear.addValueChangeListener(e -> {
			updateListOfDays((Integer) e.getProperty().getValue(), (Integer) birthDateMonth.getValue());
		});
		birthDateMonth.addValueChangeListener(e -> {
			updateListOfDays((Integer) birthDateYear.getValue(), (Integer) e.getProperty().getValue());
		});
	}

	private void addListenersToInfrastructureFields(ComboBox regionField, ComboBox districtField, ComboBox communityField, ComboBox facilityField, TextField detailsField) {
		regionField.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(districtField, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});

		districtField.addValueChangeListener(e -> {
			if (communityField.getValue() == null) {
				FieldHelper.removeItems(facilityField);
			}
			FieldHelper.removeItems(communityField);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(communityField, districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
			FieldHelper.updateItems(facilityField, districtDto != null ? FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByDistrict(districtDto, true) : null);
		});

		communityField.addValueChangeListener(e -> {
			if (occupationFacility.getValue() == null) {
				FieldHelper.removeItems(facilityField);
				CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
				FieldHelper.updateItems(facilityField, communityDto != null ? FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByCommunity(communityDto, true) :
						districtField.getValue() != null ? FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByDistrict((DistrictReferenceDto) districtField.getValue(), true) :
								null);
			}
		});
		facilityField.addValueChangeListener(e -> {
			updateFacilityDetailsVisibility(detailsField, (FacilityReferenceDto) e.getProperty().getValue());
		});
		// Set initial visibility
		updateFacilityDetailsVisibility(detailsField, (FacilityReferenceDto) facilityField.getValue());
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
					getFieldGroup().getField(PersonDto.BIRTH_DATE_MM).getValue() != null ? (Integer) getFieldGroup().getField(PersonDto.BIRTH_DATE_MM).getValue() - 1 : 0,
					getFieldGroup().getField(PersonDto.BIRTH_DATE_DD).getValue() != null ? (Integer) getFieldGroup().getField(PersonDto.BIRTH_DATE_DD).getValue() : 1);
			return birthDateCalendar.getTime();
		}
		return null;
	}

	private void updateApproximateAge() {

		Date birthDate = calcBirthDateValue();

		if (birthDate != null) {
			Pair<Integer, ApproximateAgeType> pair = ApproximateAgeHelper.getApproximateAge(
					birthDate, (Date) getFieldGroup().getField(PersonDto.DEATH_DATE).getValue()
			);

			TextField approximateAgeField = (TextField) getFieldGroup().getField(PersonDto.APPROXIMATE_AGE);
			approximateAgeField.setReadOnly(false);
			approximateAgeField.setValue(pair.getElement0() != null ? String.valueOf(pair.getElement0()) : null);
			approximateAgeField.setReadOnly(true);

			AbstractSelect approximateAgeTypeSelect = (AbstractSelect) getFieldGroup().getField(PersonDto.APPROXIMATE_AGE_TYPE);
			approximateAgeTypeSelect.setReadOnly(false);
			approximateAgeTypeSelect.setValue(pair.getElement1());
			approximateAgeTypeSelect.setReadOnly(true);
		}
	}

	private void toogleOccupationMetaFields() {
		OccupationType type = (OccupationType) ((AbstractSelect) getFieldGroup().getField(PersonDto.OCCUPATION_TYPE)).getValue();
		if (type != null) {
			switch (type) {
				case BUSINESSMAN_WOMAN:
				case TRANSPORTER:
				case OTHER:
					setVisible(false,
							PersonDto.OCCUPATION_FACILITY,
							PersonDto.OCCUPATION_FACILITY_DETAILS,
							PersonDto.OCCUPATION_REGION,
							PersonDto.OCCUPATION_DISTRICT,
							PersonDto.OCCUPATION_COMMUNITY);
					setVisible(true,
							PersonDto.OCCUPATION_DETAILS);
					break;
				case HEALTHCARE_WORKER:
					setVisible(true,
							PersonDto.OCCUPATION_DETAILS,
							PersonDto.OCCUPATION_REGION,
							PersonDto.OCCUPATION_DISTRICT,
							PersonDto.OCCUPATION_COMMUNITY,
							PersonDto.OCCUPATION_FACILITY);
					updateFacilityDetailsVisibility(occupationFacilityDetails, (FacilityReferenceDto) occupationFacility.getValue());
					break;
				default:
					setVisible(false,
							PersonDto.OCCUPATION_DETAILS,
							PersonDto.OCCUPATION_FACILITY,
							PersonDto.OCCUPATION_FACILITY_DETAILS,
							PersonDto.OCCUPATION_REGION,
							PersonDto.OCCUPATION_DISTRICT,
							PersonDto.OCCUPATION_COMMUNITY);
					break;
			}
		} else {
			setVisible(false,
					PersonDto.OCCUPATION_DETAILS,
					PersonDto.OCCUPATION_FACILITY,
					PersonDto.OCCUPATION_FACILITY_DETAILS,
					PersonDto.OCCUPATION_REGION,
					PersonDto.OCCUPATION_DISTRICT,
					PersonDto.OCCUPATION_COMMUNITY);
		}
	}

	private void updateFacilityDetailsVisibility(TextField detailsField, FacilityReferenceDto facility) {
		if (facility == null) {
			detailsField.setVisible(false);
			detailsField.clear();
			return;
		}

		boolean otherHealthFacility = facility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
		boolean noneHealthFacility = facility.getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
		boolean visibleAndRequired = otherHealthFacility || noneHealthFacility;

		detailsField.setVisible(visibleAndRequired);

		if (otherHealthFacility) {
			detailsField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
		}
		if (noneHealthFacility) {
			detailsField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.NONE_HEALTH_FACILITY_DETAILS));
		}
		if (!visibleAndRequired) {
			detailsField.clear();
		}
	}

	private void toogleDeathAndBurialFields() {
		//		List<Object> diseaseSpecificFields = Arrays.asList(PersonDto.DEATH_PLACE_TYPE, PersonDto.DEATH_PLACE_DESCRIPTION, PersonDto.BURIAL_DATE,
		//				PersonDto.BURIAL_PLACE_DESCRIPTION, PersonDto.BURIAL_CONDUCTOR);
		PresentCondition type = (PresentCondition) ((AbstractSelect) getFieldGroup().getField(PersonDto.PRESENT_CONDITION)).getValue();
		if (type == null) {
			setVisible(false,
					PersonDto.DEATH_DATE,
					PersonDto.DEATH_PLACE_TYPE,
					PersonDto.DEATH_PLACE_DESCRIPTION,
					PersonDto.BURIAL_DATE,
					PersonDto.BURIAL_PLACE_DESCRIPTION,
					PersonDto.BURIAL_CONDUCTOR);
			toggleCauseOfDeathFields(false);
		} else {
			switch (type) {
				case DEAD:
					setVisible(true,
							PersonDto.DEATH_DATE,
							PersonDto.DEATH_PLACE_TYPE,
							PersonDto.DEATH_PLACE_DESCRIPTION);
					causeOfDeathField.setValue(CauseOfDeath.EPIDEMIC_DISEASE);
					toggleCauseOfDeathFields(true);
					setVisible(false,
							PersonDto.BURIAL_DATE,
							PersonDto.BURIAL_PLACE_DESCRIPTION,
							PersonDto.BURIAL_CONDUCTOR);
					break;
				case BURIED:
					setVisible(true,
							PersonDto.DEATH_DATE,
							PersonDto.DEATH_PLACE_TYPE,
							PersonDto.DEATH_PLACE_DESCRIPTION,
							PersonDto.BURIAL_DATE,
							PersonDto.BURIAL_PLACE_DESCRIPTION,
							PersonDto.BURIAL_CONDUCTOR);
					causeOfDeathField.setValue(CauseOfDeath.EPIDEMIC_DISEASE);
					toggleCauseOfDeathFields(true);
					break;
				default:
					setVisible(false,
							PersonDto.DEATH_DATE,
							PersonDto.DEATH_PLACE_TYPE,
							PersonDto.DEATH_PLACE_DESCRIPTION,
							PersonDto.BURIAL_DATE,
							PersonDto.BURIAL_PLACE_DESCRIPTION,
							PersonDto.BURIAL_CONDUCTOR);
					toggleCauseOfDeathFields(false);
					break;
			}
		}

		// Make sure that disease specific fields are only shown when required
		//		for (Object propertyId : diseaseSpecificFields) {
		//			boolean visible = DiseasesConfiguration.isDefinedOrMissing(PersonDto.class, (String)propertyId, disease);
		//			if (!visible) {
		//				getFieldGroup().getField(propertyId).setVisible(false);
		//			}
		//		}

		fillDeathAndBurialFields((AbstractSelect) getField(PersonDto.DEATH_PLACE_TYPE), (TextField) getField(PersonDto.DEATH_PLACE_DESCRIPTION), (TextField) getField(PersonDto.BURIAL_PLACE_DESCRIPTION));
	}

	private void toggleCauseOfDeathFields(boolean causeOfDeathVisible) {
		if (!causeOfDeathVisible) {
			causeOfDeathField.setVisible(false);
			causeOfDeathDiseaseField.setVisible(false);
			causeOfDeathDetailsField.setVisible(false);
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

	private void updateOccupationFieldCaptions() {
		OccupationType type = (OccupationType) ((AbstractSelect) getFieldGroup().getField(PersonDto.OCCUPATION_TYPE)).getValue();
		if (type != null) {
			Field<?> od = getFieldGroup().getField(PersonDto.OCCUPATION_DETAILS);
			switch (type) {
				case BUSINESSMAN_WOMAN:
					od.setCaption(I18nProperties.getCaption(getPropertyI18nPrefix() + ".business." + PersonDto.OCCUPATION_DETAILS));
					break;
				case TRANSPORTER:
					od.setCaption(I18nProperties.getCaption(getPropertyI18nPrefix() + ".transporter." + PersonDto.OCCUPATION_DETAILS));
					break;
				case OTHER:
					od.setCaption(I18nProperties.getCaption(getPropertyI18nPrefix() + ".other." + PersonDto.OCCUPATION_DETAILS));
					break;
				case HEALTHCARE_WORKER:
					od.setCaption(I18nProperties.getCaption(getPropertyI18nPrefix() + ".healthcare." + PersonDto.OCCUPATION_DETAILS));
					break;
				default:
					od.setCaption(I18nProperties.getCaption(getPropertyI18nPrefix() + "." + PersonDto.OCCUPATION_DETAILS));
					break;
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
			deathPlaceType.setValue(DeathPlaceType.OTHER);
			if (deathPlaceDesc.isVisible() && StringUtils.isBlank(deathPlaceDesc.getValue())) {
				deathPlaceDesc.setValue(getValue().getAddress().toString());
			}
		}

		if (burialPlaceDesc.isVisible() && StringUtils.isBlank(burialPlaceDesc.getValue())) {
			burialPlaceDesc.setValue(getValue().getAddress().toString());
		}
	}
}
