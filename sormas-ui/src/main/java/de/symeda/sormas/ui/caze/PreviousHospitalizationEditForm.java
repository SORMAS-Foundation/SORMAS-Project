package de.symeda.sormas.ui.caze;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.PreviousHospitalizationDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class PreviousHospitalizationEditForm extends AbstractEditForm<PreviousHospitalizationDto> {
	
	private static final String FACILITY_REGION = "facilityRegion";
	private static final String FACILITY_DISTRICT = "facilityDistrict";
	private static final String FACILITY_COMMUNITY = "facilityCommunity";
	
    private static final String HTML_LAYOUT = 
			LayoutUtil.fluidRowLocs(PreviousHospitalizationDto.ADMISSION_DATE, PreviousHospitalizationDto.DISCHARGE_DATE)+
			LayoutUtil.fluidRowLocs(FACILITY_REGION, FACILITY_DISTRICT)+
			LayoutUtil.fluidRowLocs(FACILITY_COMMUNITY, PreviousHospitalizationDto.HEALTH_FACILITY)+
			LayoutUtil.fluidRowLocs(PreviousHospitalizationDto.ISOLATED)+
			LayoutUtil.fluidRowLocs(PreviousHospitalizationDto.DESCRIPTION)
			;

    public PreviousHospitalizationEditForm() {
        super(PreviousHospitalizationDto.class, PreviousHospitalizationDto.I18N_PREFIX);

        setWidth(540, Unit.PIXELS);
    }
    
	@Override
	protected void addFields() {

    	addFields(PreviousHospitalizationDto.ADMISSION_DATE,
    			PreviousHospitalizationDto.DISCHARGE_DATE);
    	addField(PreviousHospitalizationDto.ISOLATED, OptionGroup.class);
    	addField(PreviousHospitalizationDto.DESCRIPTION, TextArea.class).setRows(2);

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
    	ComboBox healthFacility = addField(PreviousHospitalizationDto.HEALTH_FACILITY, ComboBox.class);
    	healthFacility.setImmediate(true);

    	
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
    		if(facilityFieldsInitialized || healthFacility.getValue() == null) {
    			healthFacility.removeAllItems();
	    		CommunityReferenceDto communityDto = (CommunityReferenceDto)e.getProperty().getValue();
	    		if(communityDto != null) {
	    			healthFacility.addItems(FacadeProvider.getFacilityFacade().getAllByCommunity(communityDto));
	    		}
    		}
    	});

		facilityRegion.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
		addFieldListeners(PreviousHospitalizationDto.HEALTH_FACILITY, e -> fillFacilityFields());
		
    	setRequired(true,
    			PreviousHospitalizationDto.ADMISSION_DATE, 
    			PreviousHospitalizationDto.DISCHARGE_DATE, 
    			PreviousHospitalizationDto.HEALTH_FACILITY);
    }
	
	private boolean facilityFieldsInitialized = false;

	/**
	 * Taken from PersonEditForm
	 */
	private void fillFacilityFields() {
		if (facilityFieldsInitialized) 
			return;
		
	    ComboBox healthFacility = (ComboBox) getField(PreviousHospitalizationDto.HEALTH_FACILITY);

	    FacilityReferenceDto facilityRef = (FacilityReferenceDto)healthFacility.getValue();
		if (facilityRef == null) 
			return;
		FacilityDto facility = FacadeProvider.getFacilityFacade().getByUuid(facilityRef.getUuid());
		
		ComboBox facilityRegion = (ComboBox) getField(FACILITY_REGION);
	    ComboBox facilityDistrict = (ComboBox) getField(FACILITY_DISTRICT);
	    ComboBox facilityCommunity = (ComboBox) getField(FACILITY_COMMUNITY);
	    facilityRegion.select(facility.getLocation().getRegion());
	    facilityDistrict.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(facility.getLocation().getRegion().getUuid()));
	   	facilityDistrict.select(facility.getLocation().getDistrict());
	   	facilityCommunity.addItems(FacadeProvider.getCommunityFacade().getAllByDistrict(facility.getLocation().getDistrict().getUuid()));
	   	facilityCommunity.select(facility.getLocation().getCommunity());
	   	healthFacility.addItems(FacadeProvider.getFacilityFacade().getAllByCommunity(facility.getLocation().getCommunity()));
	   	
	   	facilityFieldsInitialized = true;
	}

	
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
