package de.symeda.sormas.ui.person;

import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.CauseOfDeath;
import de.symeda.sormas.api.person.DeathPlaceType;
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
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.ViewMode;

public class PersonEditForm extends AbstractEditForm<PersonDto> {

	private static final long serialVersionUID = -1L;
	
	private static final String FACILITY_REGION = "facilityRegion";
	private static final String FACILITY_DISTRICT = "facilityDistrict";
	private static final String FACILITY_COMMUNITY = "facilityCommunity";
	
	private static final String OCCUPATION_HEADER = "occupationHeader";
	private static final String ADDRESS_HEADER = "addressHeader";
    
	private Label occupationHeader = new Label(LayoutUtil.h3("Occupation"), ContentMode.HTML);
	private Label addressHeader = new Label(LayoutUtil.h3("Permanent residence of person"), ContentMode.HTML);
	
	private boolean facilityFieldsInitialized = false;
	private Disease disease;
	private String diseaseDetails;
	private ComboBox causeOfDeathField;
	private ComboBox causeOfDeathDiseaseField;
	private TextField causeOfDeathDetailsField;
	private final ViewMode viewMode;
	
    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3("Person information")+
			LayoutUtil.fluidRowLocs(PersonDto.FIRST_NAME, PersonDto.LAST_NAME) +
			LayoutUtil.fluidRowLocs(PersonDto.NICKNAME, PersonDto.MOTHERS_MAIDEN_NAME) +
			LayoutUtil.fluidRow(
					LayoutUtil.fluidRowLocs(PersonDto.BIRTH_DATE_YYYY, PersonDto.BIRTH_DATE_MM, PersonDto.BIRTH_DATE_DD),
					LayoutUtil.fluidRowLocs(PersonDto.APPROXIMATE_AGE, PersonDto.APPROXIMATE_AGE_TYPE)
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
    				LayoutUtil.fluidRowLocs(FACILITY_REGION, FACILITY_DISTRICT, FACILITY_COMMUNITY, PersonDto.OCCUPATION_FACILITY)
    		) +
    		LayoutUtil.loc(ADDRESS_HEADER) +
			LayoutUtil.fluidRowLocs(PersonDto.ADDRESS)
			;

    public PersonEditForm(Disease disease, String diseaseDetails, UserRight editOrCreateUserRight, ViewMode viewMode) {
    	super(PersonDto.class, PersonDto.I18N_PREFIX, editOrCreateUserRight);
    	this.disease = disease;
    	this.diseaseDetails = diseaseDetails;
    	this.viewMode = viewMode;
    	
    	getContent().addComponent(occupationHeader, OCCUPATION_HEADER);
    	getContent().addComponent(addressHeader, ADDRESS_HEADER);
    	
		addFields();
    }

    @Override
	protected void addFields() {
    	if (disease == null || viewMode == null) {
			return;
		}
    	
		// Add fields
    	
    	addField(PersonDto.FIRST_NAME, TextField.class);
    	addField(PersonDto.LAST_NAME, TextField.class);
    	ComboBox sex = addField(PersonDto.SEX, ComboBox.class);
    	addField(PersonDto.NICKNAME, TextField.class);
    	addField(PersonDto.MOTHERS_MAIDEN_NAME, TextField.class);
    	ComboBox presentCondition = addField(PersonDto.PRESENT_CONDITION, ComboBox.class);
    	ComboBox days = addField(PersonDto.BIRTH_DATE_DD, ComboBox.class);
    	// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
    	days.setNullSelectionAllowed(true);
    	days.setNullSelectionItemId("");
    	days.addItems(DateHelper.getDaysInMonth());
    	ComboBox months = addField(PersonDto.BIRTH_DATE_MM, ComboBox.class);
    	// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
    	months.setNullSelectionAllowed(true);
    	months.setNullSelectionItemId("");
    	months.addItems(DateHelper.getMonthsInYear());
    	months.setPageLength(12);
    	setItemCaptionsForMonths(months);
    	ComboBox years = addField(PersonDto.BIRTH_DATE_YYYY, ComboBox.class);
    	years.setCaption(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, "birthdate"));
    	// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
    	years.setNullSelectionAllowed(true);
    	years.setNullSelectionItemId("");
		years.addItems(DateHelper.getYearsToNow());
		years.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
    	DateField deathDate = addField(PersonDto.DEATH_DATE, DateField.class);
    	addField(PersonDto.APPROXIMATE_AGE, TextField.class);
    	addField(PersonDto.APPROXIMATE_AGE_TYPE, ComboBox.class);
    	AbstractSelect deathPlaceType = addField(PersonDto.DEATH_PLACE_TYPE, ComboBox.class);
    	deathPlaceType.setNullSelectionAllowed(true);
    	TextField deathPlaceDesc = addField(PersonDto.DEATH_PLACE_DESCRIPTION, TextField.class);
    	DateField burialDate = addField(PersonDto.BURIAL_DATE, DateField.class);
    	TextField burialPlaceDesc = addField(PersonDto.BURIAL_PLACE_DESCRIPTION, TextField.class);
    	ComboBox burialConductor = addField(PersonDto.BURIAL_CONDUCTOR, ComboBox.class);
    	addField(PersonDto.ADDRESS, LocationEditForm.class).setCaption(null);
    	addField(PersonDto.PHONE, TextField.class);
    	addField(PersonDto.PHONE_OWNER, TextField.class);
    	addField(PersonDto.OCCUPATION_TYPE, ComboBox.class);
    	addField(PersonDto.OCCUPATION_DETAILS, TextField.class);
    	causeOfDeathField = addField(PersonDto.CAUSE_OF_DEATH, ComboBox.class);
    	causeOfDeathDiseaseField = addField(PersonDto.CAUSE_OF_DEATH_DISEASE, ComboBox.class);
    	causeOfDeathDetailsField = addField(PersonDto.CAUSE_OF_DEATH_DETAILS, TextField.class);
    	ComboBox facilityRegion = new ComboBox();
    	facilityRegion.setCaption(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, FACILITY_REGION));
    	facilityRegion.setImmediate(true);
    	facilityRegion.setWidth(100, Unit.PERCENTAGE);
    	getContent().addComponent(facilityRegion, FACILITY_REGION);
    	ComboBox facilityDistrict = new ComboBox();
    	facilityDistrict.setCaption(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, FACILITY_DISTRICT));
    	facilityDistrict.setImmediate(true);
    	facilityDistrict.setWidth(100, Unit.PERCENTAGE);
    	getContent().addComponent(facilityDistrict, FACILITY_DISTRICT);
    	ComboBox facilityCommunity = new ComboBox();
    	facilityCommunity.setCaption(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, FACILITY_COMMUNITY));
    	facilityCommunity.setImmediate(true);
    	facilityCommunity.setWidth(100, Unit.PERCENTAGE);
    	getContent().addComponent(facilityCommunity, FACILITY_COMMUNITY);
    	ComboBox occupationFacility = addField(PersonDto.OCCUPATION_FACILITY, ComboBox.class);
    	occupationFacility.setImmediate(true);
    		
		// Set requirements that don't need visibility changes and read only status
		
    	setRequired(true, 
    			PersonDto.FIRST_NAME, 
    			PersonDto.LAST_NAME);
    	setVisible(false, 
    			PersonDto.OCCUPATION_DETAILS,
    			PersonDto.OCCUPATION_FACILITY,
    			FACILITY_REGION,
    			FACILITY_DISTRICT,
    			FACILITY_COMMUNITY,
    			PersonDto.DEATH_DATE,
    			PersonDto.DEATH_PLACE_TYPE,
    			PersonDto.DEATH_PLACE_DESCRIPTION,
    			PersonDto.BURIAL_DATE,
    			PersonDto.BURIAL_PLACE_DESCRIPTION,
    			PersonDto.BURIAL_CONDUCTOR,
    			PersonDto.CAUSE_OF_DEATH,
    			PersonDto.CAUSE_OF_DEATH_DETAILS,
    			PersonDto.CAUSE_OF_DEATH_DISEASE);

    	FieldHelper.addSoftRequiredStyle(presentCondition, sex, deathDate, deathPlaceDesc, deathPlaceType, 
    			causeOfDeathField, causeOfDeathDiseaseField, causeOfDeathDetailsField, 
    			burialDate, burialPlaceDesc, burialConductor);

    	// Set initial visibilities
    	
		initializeVisibilitiesAndAllowedVisibilities(disease, viewMode);
		
		if (!getField(PersonDto.OCCUPATION_TYPE).isVisible())
			occupationHeader.setVisible(false);
		if (!getField(PersonDto.ADDRESS).isVisible())
			addressHeader.setVisible(false);

    	// Add listeners
    	
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
		addFieldListeners(PersonDto.OCCUPATION_FACILITY, e -> fillFacilityFields());
    	addFieldListeners(PersonDto.PRESENT_CONDITION, e -> toogleDeathAndBurialFields());
    	
    	causeOfDeathField.addValueChangeListener(e -> {
    		toggleCauseOfDeathFields(true);
    	});
    	
    	causeOfDeathDiseaseField.addValueChangeListener(e -> {
    		toggleCauseOfDeathFields(true);
    	});
    	
    	addValueChangeListener(e -> {
    		fillDeathAndBurialFields(deathPlaceType, deathPlaceDesc, burialPlaceDesc);
    	});
    	
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

    
	private void updateApproximateAge() {
		if (getFieldGroup().getField(PersonDto.BIRTH_DATE_YYYY).getValue() != null) {
			Calendar birthdate = new GregorianCalendar();
			birthdate.set(
					(Integer)getFieldGroup().getField(PersonDto.BIRTH_DATE_YYYY).getValue(), 
					getFieldGroup().getField(PersonDto.BIRTH_DATE_MM).getValue()!=null?(Integer) getFieldGroup().getField(PersonDto.BIRTH_DATE_MM).getValue()-1:0, 
					getFieldGroup().getField(PersonDto.BIRTH_DATE_DD).getValue()!=null?(Integer) getFieldGroup().getField(PersonDto.BIRTH_DATE_DD).getValue():1);
			Pair<Integer, ApproximateAgeType> pair = ApproximateAgeHelper.getApproximateAge(
					(Date) birthdate.getTime(),
					(Date) getFieldGroup().getField(PersonDto.DEATH_DATE).getValue()
					);
			
			TextField approximateAgeField = (TextField)getFieldGroup().getField(PersonDto.APPROXIMATE_AGE);
			approximateAgeField.setReadOnly(false);
			approximateAgeField.setValue(pair.getElement0()!=null?String.valueOf(pair.getElement0()):null);
			approximateAgeField.setReadOnly(true);
			
			AbstractSelect approximateAgeTypeSelect = (AbstractSelect)getFieldGroup().getField(PersonDto.APPROXIMATE_AGE_TYPE);
			approximateAgeTypeSelect.setReadOnly(false);
			approximateAgeTypeSelect.setValue(String.valueOf(pair.getElement1()));
			approximateAgeTypeSelect.setReadOnly(true);
		}
	}
	
	private void toogleOccupationMetaFields() {
		OccupationType type = (OccupationType) ((AbstractSelect)getFieldGroup().getField(PersonDto.OCCUPATION_TYPE)).getValue();
		switch(type) {
			case BUSINESSMAN_WOMAN:
			case TRANSPORTER:
			case OTHER:
				setVisible(false, 
						PersonDto.OCCUPATION_FACILITY,
						FACILITY_REGION,
						FACILITY_DISTRICT,
						FACILITY_COMMUNITY);
				setVisible(true, 
		    			PersonDto.OCCUPATION_DETAILS);
				break;
			case HEALTHCARE_WORKER:
				setVisible(true, 
						PersonDto.OCCUPATION_DETAILS,
						PersonDto.OCCUPATION_FACILITY,
						FACILITY_REGION,
						FACILITY_DISTRICT,
						FACILITY_COMMUNITY);
				break;
			default:
				setVisible(false, 
		    			PersonDto.OCCUPATION_DETAILS,
		    			PersonDto.OCCUPATION_FACILITY,
		    			FACILITY_REGION,
						FACILITY_DISTRICT,
						FACILITY_COMMUNITY);
				break;
		}
	}
	
	private void toogleDeathAndBurialFields() {
//		List<Object> diseaseSpecificFields = Arrays.asList(PersonDto.DEATH_PLACE_TYPE, PersonDto.DEATH_PLACE_DESCRIPTION, PersonDto.BURIAL_DATE,
//				PersonDto.BURIAL_PLACE_DESCRIPTION, PersonDto.BURIAL_CONDUCTOR);
		PresentCondition type = (PresentCondition) ((AbstractSelect)getFieldGroup().getField(PersonDto.PRESENT_CONDITION)).getValue();
		switch (type) {
		case DEAD:
			setVisible(true,
					PersonDto.DEATH_DATE,
					PersonDto.DEATH_PLACE_TYPE,
					PersonDto.DEATH_PLACE_DESCRIPTION);
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
			if (causeOfDeathField.getValue() == null && disease != null) {
				causeOfDeathField.setValue(CauseOfDeath.EPIDEMIC_DISEASE);
			}
			
			if (causeOfDeathField.getValue() == null) {
				causeOfDeathDiseaseField.setVisible(false);
				causeOfDeathDetailsField.setVisible(false);
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
				if (isVisibleAllowed(causeOfDeathDetailsField)) {
					causeOfDeathDetailsField.setVisible(true);
				}
			}
		}
	}
	
	private void fillFacilityFields() {
		if(facilityFieldsInitialized) return;
		FacilityReferenceDto facilityRef = (FacilityReferenceDto) getFieldGroup().getField(PersonDto.OCCUPATION_FACILITY).getValue();
		if(facilityRef == null) return;
		FacilityDto facility = FacadeProvider.getFacilityFacade().getByUuid(facilityRef.getUuid());
		
		ComboBox facilityRegion = (ComboBox) getField(FACILITY_REGION);
	    ComboBox facilityDistrict = (ComboBox) getField(FACILITY_DISTRICT);
	    ComboBox facilityCommunity = (ComboBox) getField(FACILITY_COMMUNITY);
	    ComboBox occupationFacility = (ComboBox) getField(PersonDto.OCCUPATION_FACILITY);
	    facilityRegion.select(facility.getRegion());
	    facilityDistrict.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(facility.getRegion().getUuid()));
	   	facilityDistrict.select(facility.getDistrict());
	   	facilityCommunity.addItems(FacadeProvider.getCommunityFacade().getAllByDistrict(facility.getDistrict().getUuid()));
	   	facilityCommunity.select(facility.getCommunity());
	   	if (facility.getCommunity() != null) {
		   	occupationFacility.addItems(FacadeProvider.getFacilityFacade().getHealthFacilitiesByCommunity(facility.getCommunity(), false));
		   	occupationFacility.select(facility);
	   	} else {
	   		occupationFacility.addItems(FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict(facility.getDistrict(), false));
		   	occupationFacility.select(facility);
	   	}
	   	
	   	facilityFieldsInitialized = true;
	}
	
	private void updateOccupationFieldCaptions() {
		OccupationType type = (OccupationType) ((AbstractSelect)getFieldGroup().getField(PersonDto.OCCUPATION_TYPE)).getValue();
		Field<?> od = getFieldGroup().getField(PersonDto.OCCUPATION_DETAILS);
		switch(type) {
			case BUSINESSMAN_WOMAN:
				od.setCaption(I18nProperties.getFieldCaption(getPropertyI18nPrefix()+".business."+PersonDto.OCCUPATION_DETAILS));
				break;
			case TRANSPORTER:
				od.setCaption(I18nProperties.getFieldCaption(getPropertyI18nPrefix()+".transporter."+PersonDto.OCCUPATION_DETAILS));
				break;
			case OTHER:
				od.setCaption(I18nProperties.getFieldCaption(getPropertyI18nPrefix()+".other."+PersonDto.OCCUPATION_DETAILS));
				break;
			case HEALTHCARE_WORKER:
				od.setCaption(I18nProperties.getFieldCaption(getPropertyI18nPrefix()+".healthcare."+PersonDto.OCCUPATION_DETAILS));
				break;
			default:
				od.setCaption(I18nProperties.getFieldCaption(getPropertyI18nPrefix()+"."+PersonDto.OCCUPATION_DETAILS));
				break;
		
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
