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

import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.vaadin.shared.ui.ContentMode;
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
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.ViewMode;

public class PersonEditForm extends AbstractEditForm<PersonDto> {

	private static final long serialVersionUID = -1L;

	private static final String OCCUPATION_HEADER = "occupationHeader";
	private static final String ADDRESS_HEADER = "addressHeader";

	private Label occupationHeader = new Label(LayoutUtil.h3(I18nProperties.getString(Strings.headingPersonOccupation)), ContentMode.HTML);
	private Label addressHeader = new Label(LayoutUtil.h3(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.ADDRESS)), ContentMode.HTML);

	private boolean facilityFieldsInitialized = false;

	private Disease disease;
	private String diseaseDetails;
	private ComboBox causeOfDeathField;
	private ComboBox causeOfDeathDiseaseField;
	private TextField causeOfDeathDetailsField;
	private ComboBox occupationFacility;
	private TextField occupationFacilityDetails;
	private final ViewMode viewMode;

	private static final String HTML_LAYOUT = 
			LayoutUtil.h3(I18nProperties.getString(Strings.headingPersonInformation))+
			LayoutUtil.fluidRowLocs(PersonDto.FIRST_NAME, PersonDto.LAST_NAME) +
			LayoutUtil.fluidRowLocs(PersonDto.NICKNAME, PersonDto.MOTHERS_MAIDEN_NAME) +
			LayoutUtil.fluidRow(
					LayoutUtil.fluidRowLocs(PersonDto.BIRTH_DATE_YYYY, PersonDto.BIRTH_DATE_MM, PersonDto.BIRTH_DATE_DD),
					LayoutUtil.fluidRowLocs(PersonDto.APPROXIMATE_AGE, PersonDto.APPROXIMATE_AGE_TYPE, PersonDto.APPROXIMATE_AGE_REFERENCE_DATE)
					) +
			LayoutUtil.fluidRowLocs(PersonDto.SEX, PersonDto.PRESENT_CONDITION) +
			LayoutUtil.fluidRow(
					LayoutUtil.oneOfFourCol(PersonDto.DEATH_DATE),
					LayoutUtil.oneOfFourCol(PersonDto.CAUSE_OF_DEATH),
					LayoutUtil.fluidColumnLocCss(CssStyles.LAYOUT_COL_HIDE_INVSIBLE, 3, 0, PersonDto.CAUSE_OF_DEATH_DISEASE),
					LayoutUtil.oneOfFourCol(PersonDto.CAUSE_OF_DEATH_DETAILS)
					) +
			LayoutUtil.fluidRow(
					LayoutUtil.oneOfFourCol(PersonDto.DEATH_PLACE_TYPE), 
					LayoutUtil.oneOfFourCol(PersonDto.DEATH_PLACE_DESCRIPTION)
					) +
			LayoutUtil.fluidRow(
					LayoutUtil.oneOfFourCol(PersonDto.BURIAL_DATE),
					LayoutUtil.oneOfFourCol(PersonDto.BURIAL_CONDUCTOR),
					LayoutUtil.oneOfTwoCol(PersonDto.BURIAL_PLACE_DESCRIPTION)
					) +
			LayoutUtil.fluidRowLocs(PersonDto.PHONE, PersonDto.PHONE_OWNER) +
			LayoutUtil.loc(OCCUPATION_HEADER) +
			LayoutUtil.divsCss(
					CssStyles.VSPACE_3, 
					LayoutUtil.fluidRowLocs(PersonDto.OCCUPATION_TYPE, PersonDto.OCCUPATION_DETAILS),
					LayoutUtil.fluidRowLocs(PersonDto.OCCUPATION_REGION, PersonDto.OCCUPATION_DISTRICT, PersonDto.OCCUPATION_COMMUNITY, PersonDto.OCCUPATION_FACILITY),
					LayoutUtil.fluidRowLocs("","", PersonDto.OCCUPATION_FACILITY_DETAILS),
					LayoutUtil.fluidRowLocs(PersonDto.EDUCATION_TYPE, PersonDto.EDUCATION_DETAILS)
					) +
			LayoutUtil.loc(ADDRESS_HEADER) +
			LayoutUtil.fluidRowLocs(PersonDto.ADDRESS)
			;

	private boolean initialized = false;


	public PersonEditForm(Disease disease, String diseaseDetails, UserRight editOrCreateUserRight, ViewMode viewMode) {
		super(PersonDto.class, PersonDto.I18N_PREFIX, editOrCreateUserRight);
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.viewMode = viewMode;

		getContent().addComponent(occupationHeader, OCCUPATION_HEADER);
		getContent().addComponent(addressHeader, ADDRESS_HEADER);

		initialized = true;
		addFields();
	}

	@Override
	protected void addFields() {
		if (!initialized) {
			// vars have to be set first
			return;
		}

		addField(PersonDto.FIRST_NAME, TextField.class);
		addField(PersonDto.LAST_NAME, TextField.class);
		ComboBox sex = addField(PersonDto.SEX, ComboBox.class);
		addField(PersonDto.NICKNAME, TextField.class);
		addField(PersonDto.MOTHERS_MAIDEN_NAME, TextField.class);
		ComboBox presentCondition = addField(PersonDto.PRESENT_CONDITION, ComboBox.class);
		ComboBox birthDateDay = addField(PersonDto.BIRTH_DATE_DD, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateDay.setNullSelectionAllowed(true);
		birthDateDay.addItems(DateHelper.getDaysInMonth());
		ComboBox birthDateMonth = addField(PersonDto.BIRTH_DATE_MM, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateMonth.setNullSelectionAllowed(true);
		birthDateMonth.addItems(DateHelper.getMonthsInYear());
		birthDateMonth.setPageLength(12);
		setItemCaptionsForMonths(birthDateMonth);
		ComboBox birthDateYear = addField(PersonDto.BIRTH_DATE_YYYY, ComboBox.class);
		birthDateYear.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE));
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateYear.setNullSelectionAllowed(true);
		birthDateYear.addItems(DateHelper.getYearsToNow());
		birthDateYear.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		DateField deathDate = addField(PersonDto.DEATH_DATE, DateField.class);
		addField(PersonDto.APPROXIMATE_AGE, TextField.class);
		addField(PersonDto.APPROXIMATE_AGE_TYPE, ComboBox.class);
		addField(PersonDto.APPROXIMATE_AGE_REFERENCE_DATE, DateField.class);
		
		AbstractSelect deathPlaceType = addField(PersonDto.DEATH_PLACE_TYPE, ComboBox.class);
		deathPlaceType.setNullSelectionAllowed(true);
		TextField deathPlaceDesc = addField(PersonDto.DEATH_PLACE_DESCRIPTION, TextField.class);
		DateField burialDate = addField(PersonDto.BURIAL_DATE, DateField.class);
		TextField burialPlaceDesc = addField(PersonDto.BURIAL_PLACE_DESCRIPTION, TextField.class);
		ComboBox burialConductor = addField(PersonDto.BURIAL_CONDUCTOR, ComboBox.class);
		addField(PersonDto.ADDRESS, LocationEditForm.class).setCaption(null);
		addField(PersonDto.PHONE, TextField.class);
		addField(PersonDto.PHONE_OWNER, TextField.class);
		
		addFields(PersonDto.OCCUPATION_TYPE, PersonDto.OCCUPATION_DETAILS,
				PersonDto.EDUCATION_TYPE, PersonDto.EDUCATION_DETAILS);

		causeOfDeathField = addField(PersonDto.CAUSE_OF_DEATH, ComboBox.class);
		causeOfDeathDiseaseField = addDiseaseField(PersonDto.CAUSE_OF_DEATH_DISEASE, true, disease);
		causeOfDeathDetailsField = addField(PersonDto.CAUSE_OF_DEATH_DETAILS, TextField.class);
		ComboBox facilityRegion = addField(PersonDto.OCCUPATION_REGION, ComboBox.class);
		facilityRegion.setImmediate(true);
		facilityRegion.setNullSelectionAllowed(true);
		ComboBox facilityDistrict = addField(PersonDto.OCCUPATION_DISTRICT, ComboBox.class);
		facilityDistrict.setImmediate(true);
		facilityDistrict.setNullSelectionAllowed(true);
		ComboBox facilityCommunity = addField(PersonDto.OCCUPATION_COMMUNITY, ComboBox.class);
		facilityCommunity.setImmediate(true);
		facilityCommunity.setNullSelectionAllowed(true);
		occupationFacility = addField(PersonDto.OCCUPATION_FACILITY, ComboBox.class);
		occupationFacility.setImmediate(true);
		occupationFacility.setNullSelectionAllowed(true);
		occupationFacilityDetails = addField(PersonDto.OCCUPATION_FACILITY_DETAILS, TextField.class);

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

		initializeVisibilitiesAndAllowedVisibilities(disease, viewMode);

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

		facilityRegion.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto)e.getProperty().getValue();
			FieldHelper.updateItems(facilityDistrict, regionDto != null ? FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()) : null);
		});

		facilityDistrict.addValueChangeListener(e -> {
			if (facilityCommunity.getValue() == null) {
				FieldHelper.removeItems(occupationFacility);
			}
			FieldHelper.removeItems(facilityCommunity);
			DistrictReferenceDto districtDto = (DistrictReferenceDto)e.getProperty().getValue();
			FieldHelper.updateItems(facilityCommunity, districtDto != null ? FacadeProvider.getCommunityFacade().getAllByDistrict(districtDto.getUuid()) : null);
			FieldHelper.updateItems(occupationFacility, districtDto != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict(districtDto, true) : null);
		});

		facilityCommunity.addValueChangeListener(e -> {
			if(facilityFieldsInitialized || occupationFacility.getValue() == null) {
				FieldHelper.removeItems(occupationFacility);
				CommunityReferenceDto communityDto = (CommunityReferenceDto)e.getProperty().getValue();
				FieldHelper.updateItems(occupationFacility, communityDto != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByCommunity(communityDto, true) :
					facilityDistrict.getValue() != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict((DistrictReferenceDto) facilityDistrict.getValue(), true) :
						null);
			}
		});

		facilityRegion.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
		addFieldListeners(PersonDto.PRESENT_CONDITION, e -> toogleDeathAndBurialFields());

		occupationFacility.addValueChangeListener(e -> {
			updateOccupationFacilityDetailsVisibility((FacilityReferenceDto) e.getProperty().getValue());
		});

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
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	private void updateReadyOnlyApproximateAge() {
		boolean readonly = false;
		if(getFieldGroup().getField(PersonDto.BIRTH_DATE_YYYY).getValue()!=null) {
			readonly = true;
		}

		getFieldGroup().getField(PersonDto.APPROXIMATE_AGE).setReadOnly(readonly);
		getFieldGroup().getField(PersonDto.APPROXIMATE_AGE_TYPE).setReadOnly(readonly);
	}

	private Date calcBirthDateValue() {
		if (getFieldGroup().getField(PersonDto.BIRTH_DATE_YYYY).getValue() != null) {
			Calendar birthDateCalendar = new GregorianCalendar();
			birthDateCalendar.set(
					(Integer)getFieldGroup().getField(PersonDto.BIRTH_DATE_YYYY).getValue(), 
					getFieldGroup().getField(PersonDto.BIRTH_DATE_MM).getValue()!=null?(Integer) getFieldGroup().getField(PersonDto.BIRTH_DATE_MM).getValue()-1:0, 
							getFieldGroup().getField(PersonDto.BIRTH_DATE_DD).getValue()!=null?(Integer) getFieldGroup().getField(PersonDto.BIRTH_DATE_DD).getValue():1);
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

			TextField approximateAgeField = (TextField)getFieldGroup().getField(PersonDto.APPROXIMATE_AGE);
			approximateAgeField.setReadOnly(false);
			approximateAgeField.setValue(pair.getElement0()!=null?String.valueOf(pair.getElement0()):null);
			approximateAgeField.setReadOnly(true);

			AbstractSelect approximateAgeTypeSelect = (AbstractSelect)getFieldGroup().getField(PersonDto.APPROXIMATE_AGE_TYPE);
			approximateAgeTypeSelect.setReadOnly(false);
			approximateAgeTypeSelect.setValue(pair.getElement1());
			approximateAgeTypeSelect.setReadOnly(true);
		}
	}

	private void toogleOccupationMetaFields() {
		OccupationType type = (OccupationType) ((AbstractSelect)getFieldGroup().getField(PersonDto.OCCUPATION_TYPE)).getValue();
		if (type != null) {
			switch(type) {
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
				updateOccupationFacilityDetailsVisibility((FacilityReferenceDto) occupationFacility.getValue());
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

	private void updateOccupationFacilityDetailsVisibility(FacilityReferenceDto facility) {
		if (facility == null) {
			occupationFacilityDetails.setVisible(false);
			occupationFacilityDetails.clear();
			return;
		}

		boolean otherHealthFacility = facility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
		boolean noneHealthFacility = facility.getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
		boolean visibleAndRequired = otherHealthFacility || noneHealthFacility;

		occupationFacilityDetails.setVisible(visibleAndRequired);
		
		if (otherHealthFacility) {
			occupationFacilityDetails.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
		}
		if (noneHealthFacility) {
			occupationFacilityDetails.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.NONE_HEALTH_FACILITY_DETAILS));
		}
		if (!visibleAndRequired) {
			occupationFacilityDetails.clear();
		}
	}

	private void toogleDeathAndBurialFields() {
		//		List<Object> diseaseSpecificFields = Arrays.asList(PersonDto.DEATH_PLACE_TYPE, PersonDto.DEATH_PLACE_DESCRIPTION, PersonDto.BURIAL_DATE,
		//				PersonDto.BURIAL_PLACE_DESCRIPTION, PersonDto.BURIAL_CONDUCTOR);
		PresentCondition type = (PresentCondition) ((AbstractSelect)getFieldGroup().getField(PersonDto.PRESENT_CONDITION)).getValue();
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

		fillDeathAndBurialFields((AbstractSelect)getField(PersonDto.DEATH_PLACE_TYPE), (TextField)getField(PersonDto.DEATH_PLACE_DESCRIPTION), (TextField)getField(PersonDto.BURIAL_PLACE_DESCRIPTION));
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
		OccupationType type = (OccupationType) ((AbstractSelect)getFieldGroup().getField(PersonDto.OCCUPATION_TYPE)).getValue();		
		if (type != null) {
			Field<?> od = getFieldGroup().getField(PersonDto.OCCUPATION_DETAILS);
			switch(type) {
			case BUSINESSMAN_WOMAN:
				od.setCaption(I18nProperties.getCaption(getPropertyI18nPrefix()+".business."+PersonDto.OCCUPATION_DETAILS));
				break;
			case TRANSPORTER:
				od.setCaption(I18nProperties.getCaption(getPropertyI18nPrefix()+".transporter."+PersonDto.OCCUPATION_DETAILS));
				break;
			case OTHER:
				od.setCaption(I18nProperties.getCaption(getPropertyI18nPrefix()+".other."+PersonDto.OCCUPATION_DETAILS));
				break;
			case HEALTHCARE_WORKER:
				od.setCaption(I18nProperties.getCaption(getPropertyI18nPrefix()+".healthcare."+PersonDto.OCCUPATION_DETAILS));
				break;
			default:
				od.setCaption(I18nProperties.getCaption(getPropertyI18nPrefix()+"."+PersonDto.OCCUPATION_DETAILS));
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
			if (deathPlaceDesc.isVisible() && (deathPlaceDesc.getValue() == null || deathPlaceDesc.getValue().isEmpty())) {
				deathPlaceDesc.setValue(getValue().getAddress().toString());
			}
		}

		if (burialPlaceDesc.isVisible() && (burialPlaceDesc.getValue() == null || deathPlaceDesc.getValue().isEmpty())) {
			burialPlaceDesc.setValue(getValue().getAddress().toString());
		}
	}
}
