package de.symeda.sormas.ui.person;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.location.LocationForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class PersonEditForm extends AbstractEditForm<PersonDto> {

	private static final long serialVersionUID = -1L;
	
	private static final String FACILITY_REGION = "facilityRegion";
	private static final String FACILITY_DISTRICT = "facilityDistrict";
	private static final String FACILITY_COMMUNITY = "facilityCommunity";
    
	private boolean facilityFieldsInitialized = false;
	
    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE3, "Person information")+
    		LayoutUtil.div(
    				LayoutUtil.fluidRowCss(
						CssStyles.VSPACE4,
						LayoutUtil.oneOfTwoCol(LayoutUtil.loc(PersonDto.FIRST_NAME)),
						LayoutUtil.oneOfTwoCol(LayoutUtil.loc(PersonDto.LAST_NAME))
					),
    				LayoutUtil.fluidRowCss(
    						CssStyles.VSPACE4, 
    						LayoutUtil.oneOfTwoCol(LayoutUtil.loc(PersonDto.NICKNAME)),
    						LayoutUtil.oneOfTwoCol(LayoutUtil.loc(PersonDto.MOTHERS_MAIDEN_NAME))
    				),
    				LayoutUtil.fluidRowCss(
    						CssStyles.VSPACE4,
    						LayoutUtil.oneOfTwoCol(
    								LayoutUtil.fluidRowCss(null,
    										LayoutUtil.oneOfThreeCol(LayoutUtil.loc(PersonDto.BIRTH_DATE_DD)),
    										LayoutUtil.oneOfThreeCol(LayoutUtil.loc(PersonDto.BIRTH_DATE_MM)),
    										LayoutUtil.oneOfThreeCol(LayoutUtil.loc(PersonDto.BIRTH_DATE_YYYY))
    								)
    								
    						),
    						LayoutUtil.oneOfTwoCol(LayoutUtil.fluidRowLocs(PersonDto.APPROXIMATE_AGE, PersonDto.APPROXIMATE_AGE_TYPE))
    				),
    				LayoutUtil.fluidRowCss(
    						CssStyles.VSPACE4,
    						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(PersonDto.SEX)),
    						LayoutUtil.oneOfThreeCol(LayoutUtil.loc(PersonDto.PRESENT_CONDITION)),
							LayoutUtil.oneOfThreeCol(LayoutUtil.loc(PersonDto.DEATH_DATE))
    				),
    				LayoutUtil.fluidRowCss(
						CssStyles.VSPACE4,
						LayoutUtil.oneOfTwoCol(LayoutUtil.loc(PersonDto.PHONE)),
						LayoutUtil.oneOfTwoCol(LayoutUtil.loc(PersonDto.PHONE_OWNER))
					)) +
    		LayoutUtil.h3(CssStyles.VSPACE3, "Permanent residence of person")+
    		LayoutUtil.div(
    				LayoutUtil.fluidRowLocsCss(CssStyles.VSPACE4, PersonDto.ADDRESS)
				)+
    		LayoutUtil.h3(CssStyles.VSPACE3, "Occupation")+
    		LayoutUtil.div(
    				LayoutUtil.fluidRowCss(
						CssStyles.VSPACE4,
						LayoutUtil.oneOfTwoCol(LayoutUtil.loc(PersonDto.OCCUPATION_TYPE)),
						LayoutUtil.oneOfTwoCol(LayoutUtil.loc(PersonDto.OCCUPATION_DETAILS))
					),
    				LayoutUtil.fluidRowCss(
    					CssStyles.VSPACE4,
    					LayoutUtil.oneOfFourCol(LayoutUtil.loc(FACILITY_REGION)),
    					LayoutUtil.oneOfFourCol(LayoutUtil.loc(FACILITY_DISTRICT)),
    					LayoutUtil.oneOfFourCol(LayoutUtil.loc(FACILITY_COMMUNITY)),
    					LayoutUtil.oneOfFourCol(LayoutUtil.loc(PersonDto.OCCUPATION_FACILITY))
    				)
    		);

    public PersonEditForm() {
    	super(PersonDto.class, PersonDto.I18N_PREFIX);
    }

    @Override
	protected void addFields() {
    	addField(PersonDto.FIRST_NAME, TextField.class);
    	addField(PersonDto.LAST_NAME, TextField.class);
    	addField(PersonDto.SEX, NativeSelect.class);
    	addField(PersonDto.NICKNAME, TextField.class);
    	addField(PersonDto.MOTHERS_MAIDEN_NAME, TextField.class);
    	
    	addField(PersonDto.PRESENT_CONDITION, NativeSelect.class);
    	NativeSelect days = addField(PersonDto.BIRTH_DATE_DD, NativeSelect.class);
    	days.setCaption(I18nProperties.getFieldCaption(PersonDto.I18N_PREFIX, "Date of birth"));
    	// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
    	days.setNullSelectionAllowed(true);
    	days.setNullSelectionItemId("");
    	days.addItems(DateHelper.getDaysInMonth());
    	NativeSelect months = addField(PersonDto.BIRTH_DATE_MM, NativeSelect.class);
    	// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
    	months.setNullSelectionAllowed(true);
    	months.setNullSelectionItemId("");
    	months.addItems(DateHelper.getMonthsInYear());
    	NativeSelect years = addField(PersonDto.BIRTH_DATE_YYYY, NativeSelect.class);
    	// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
    	years.setNullSelectionAllowed(true);
    	years.setNullSelectionItemId("");
		years.addItems(DateHelper.getYearsToNow());
		years.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
    	addField(PersonDto.DEATH_DATE, DateField.class);
    	addField(PersonDto.APPROXIMATE_AGE, TextField.class);
    	addField(PersonDto.APPROXIMATE_AGE_TYPE, NativeSelect.class);
    	
    	addField(PersonDto.ADDRESS, LocationForm.class).setCaption(null);
    	addField(PersonDto.PHONE, TextField.class);
    	addField(PersonDto.PHONE_OWNER, TextField.class);

    	addField(PersonDto.OCCUPATION_TYPE, NativeSelect.class);
    	addField(PersonDto.OCCUPATION_DETAILS, TextField.class);
    	
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
    	
    	setRequired(true, 
    			PersonDto.FIRST_NAME, 
    			PersonDto.LAST_NAME);
    	setVisible(false, 
    			PersonDto.OCCUPATION_DETAILS,
    			PersonDto.OCCUPATION_FACILITY,
    			FACILITY_REGION,
    			FACILITY_DISTRICT,
    			FACILITY_COMMUNITY,
    			PersonDto.DEATH_DATE);
    	
    	// add some listeners 
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
    	
    	addFieldListeners(PersonDto.PRESENT_CONDITION, e -> toogleDeathFields());
    	addFieldListeners(PersonDto.DEATH_DATE, e -> updateApproximateAge());
    	addFieldListeners(PersonDto.OCCUPATION_TYPE, e -> {
    		updateOccupationFieldCaptions();
    		toogleOccupationMetaFields();
    	});
    	
    	facilityRegion.addValueChangeListener(e -> {
    		facilityDistrict.removeAllItems();
    		RegionReferenceDto regionDto = (RegionReferenceDto)e.getProperty().getValue();
    		if(regionDto != null) {
    			facilityDistrict.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()));
    		}
    	});
    	facilityDistrict.addValueChangeListener(e -> {
    		facilityCommunity.removeAllItems();
    		DistrictReferenceDto districtDto = (DistrictReferenceDto)e.getProperty().getValue();
    		if(districtDto != null) {
    			facilityCommunity.addItems(FacadeProvider.getCommunityFacade().getAllByDistrict(districtDto.getUuid()));
    		}
    	});
    	facilityCommunity.addValueChangeListener(e -> {
    		if(facilityFieldsInitialized || occupationFacility.getValue() == null) {
	    		occupationFacility.removeAllItems();
	    		CommunityReferenceDto communityDto = (CommunityReferenceDto)e.getProperty().getValue();
	    		if(communityDto != null) {
	    			occupationFacility.addItems(FacadeProvider.getFacilityFacade().getAllByCommunity(communityDto));
	    		}
    		}
    	});

		facilityRegion.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
		addFieldListeners(PersonDto.OCCUPATION_FACILITY, e -> fillFacilityFields());
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
		
		if (getFieldGroup().getField(PersonDto.BIRTH_DATE_YYYY).getValue() != null && getFieldGroup().getField(PersonDto.BIRTH_DATE_YYYY).getValue() != "") {
			Calendar birthdate = new GregorianCalendar();
			birthdate.set(
					(Integer)getFieldGroup().getField(PersonDto.BIRTH_DATE_YYYY).getValue(), 
					getFieldGroup().getField(PersonDto.BIRTH_DATE_MM).getValue()!=null?(Integer) getFieldGroup().getField(PersonDto.BIRTH_DATE_MM).getValue()-1:0, 
					getFieldGroup().getField(PersonDto.BIRTH_DATE_DD).getValue()!=null?(Integer) getFieldGroup().getField(PersonDto.BIRTH_DATE_DD).getValue():1);
			Pair<Integer, ApproximateAgeType> pair = ApproximateAgeHelper.getApproximateAge(
					(Date) birthdate.getTime(),
					(Date) getFieldGroup().getField(PersonDto.DEATH_DATE).getValue()
					);
			
			TextField textField = (TextField)getFieldGroup().getField(PersonDto.APPROXIMATE_AGE);
			textField.setReadOnly(false);
			textField.setValue(pair.getElement0()!=null?String.valueOf(pair.getElement0()):null);
			textField.setReadOnly(true);
			
			NativeSelect nativeSelect = (NativeSelect)getFieldGroup().getField(PersonDto.APPROXIMATE_AGE_TYPE);
			nativeSelect.setReadOnly(false);
			nativeSelect.setValue(String.valueOf(pair.getElement1()));
			nativeSelect.setReadOnly(true);
		}
	}
	
	private void toogleOccupationMetaFields() {
		OccupationType type = (OccupationType) ((NativeSelect)getFieldGroup().getField(PersonDto.OCCUPATION_TYPE)).getValue();
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
	
	private void toogleDeathFields() {
		PresentCondition type = (PresentCondition) ((NativeSelect)getFieldGroup().getField(PersonDto.PRESENT_CONDITION)).getValue();
		switch (type) {
		case DEAD:
		case BURIED:
			setVisible(true, 
					PersonDto.DEATH_DATE);
			break;

		default:
			setVisible(false, 
					PersonDto.DEATH_DATE);
			break;
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
	    facilityRegion.select(facility.getLocation().getRegion());
	    facilityDistrict.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(facility.getLocation().getRegion().getUuid()));
	   	facilityDistrict.select(facility.getLocation().getDistrict());
	   	facilityCommunity.addItems(FacadeProvider.getCommunityFacade().getAllByDistrict(facility.getLocation().getDistrict().getUuid()));
	   	facilityCommunity.select(facility.getLocation().getCommunity());
	   	occupationFacility.addItems(FacadeProvider.getFacilityFacade().getAllByCommunity(facility.getLocation().getCommunity()));
	   	
	   	facilityFieldsInitialized = true;
	}
	
	private void updateOccupationFieldCaptions() {
		OccupationType type = (OccupationType) ((NativeSelect)getFieldGroup().getField(PersonDto.OCCUPATION_TYPE)).getValue();
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
}
