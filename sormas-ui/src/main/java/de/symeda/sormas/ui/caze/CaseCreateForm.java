package de.symeda.sormas.ui.caze;

import java.util.Arrays;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class CaseCreateForm extends AbstractEditForm<CaseDataDto> {
	
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	public static final String NONE_HEALTH_FACILITY_DETAILS = "noneHealthFacilityDetails";

    private static final String HTML_LAYOUT = 
			LayoutUtil.fluidRowLocs(CaseDataDto.DISEASE) +
			LayoutUtil.fluidRow(LayoutUtil.locs(CaseDataDto.DISEASE_DETAILS, CaseDataDto.PLAGUE_TYPE, CaseDataDto.DENGUE_FEVER_TYPE)) +
			LayoutUtil.fluidRowLocs(FIRST_NAME, LAST_NAME) +
			LayoutUtil.fluidRowLocs(CaseDataDto.REGION, CaseDataDto.DISTRICT) +
			LayoutUtil.fluidRowLocs(CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY) +
			LayoutUtil.fluidRowLocs("", CaseDataDto.HEALTH_FACILITY_DETAILS)
			;

    public CaseCreateForm(UserRight editOrCreateUserRight) {
        super(CaseDataDto.class, CaseDataDto.I18N_PREFIX, editOrCreateUserRight);

        setWidth(540, Unit.PIXELS);		
        
        hideValidationUntilNextCommit();
    }

    @Override
	protected void addFields() {
    	
    	addField(CaseDataDto.DISEASE, ComboBox.class);
    	addField(CaseDataDto.DISEASE_DETAILS, TextField.class);
    	OptionGroup plagueType = addField(CaseDataDto.PLAGUE_TYPE, OptionGroup.class);
    	addField(CaseDataDto.DENGUE_FEVER_TYPE, OptionGroup.class);
    	addCustomField(FIRST_NAME, String.class, TextField.class);
    	addCustomField(LAST_NAME, String.class, TextField.class);
    	ComboBox region = addField(CaseDataDto.REGION, ComboBox.class);
    	ComboBox district = addField(CaseDataDto.DISTRICT, ComboBox.class);
    	ComboBox community = addField(CaseDataDto.COMMUNITY, ComboBox.class);
		community.setNullSelectionAllowed(true);
    	ComboBox facility = addField(CaseDataDto.HEALTH_FACILITY, ComboBox.class);
    	TextField facilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);
    	facilityDetails.setVisible(false);
    	
    	region.addValueChangeListener(e -> {
    		RegionReferenceDto regionDto = (RegionReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()) : null);
       	});
    	district.addValueChangeListener(e -> {
    		if (community.getValue() == null) {
    			FieldHelper.removeItems(facility);
    		}
    		FieldHelper.removeItems(community);
    		DistrictReferenceDto districtDto = (DistrictReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(community, districtDto != null ? FacadeProvider.getCommunityFacade().getAllByDistrict(districtDto.getUuid()) : null);
    		FieldHelper.updateItems(facility, districtDto != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict(districtDto, true) : null);
    	});
    	community.addValueChangeListener(e -> {
    		FieldHelper.removeItems(facility);
    		CommunityReferenceDto communityDto = (CommunityReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(facility, communityDto != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByCommunity(communityDto, true) :
    			district.getValue() != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict((DistrictReferenceDto) district.getValue(), true) :
    				null);
    	});
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());

    	setRequired(true, FIRST_NAME, LAST_NAME, CaseDataDto.DISEASE, CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.HEALTH_FACILITY);
		FieldHelper.addSoftRequiredStyle(plagueType, community, facilityDetails);

		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.DISEASE_DETAILS), CaseDataDto.DISEASE, Arrays.asList(Disease.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), CaseDataDto.DISEASE, Arrays.asList(CaseDataDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.PLAGUE_TYPE), CaseDataDto.DISEASE, Arrays.asList(Disease.PLAGUE), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.DENGUE_FEVER_TYPE), CaseDataDto.DISEASE, Arrays.asList(Disease.DENGUE), true);
		
    	facility.addValueChangeListener(e -> {
    		if (facility.getValue() != null) {
				boolean otherHealthFacility = ((FacilityReferenceDto) facility.getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
				boolean noneHealthFacility = ((FacilityReferenceDto) facility.getValue()).getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
				boolean visibleAndRequired = otherHealthFacility || noneHealthFacility;
				
				facilityDetails.setVisible(visibleAndRequired);
				facilityDetails.setRequired(visibleAndRequired);

				if (otherHealthFacility) {
					facilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
				}
				if (noneHealthFacility) {
					facilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, NONE_HEALTH_FACILITY_DETAILS));
				}
				if (!visibleAndRequired) {
					facilityDetails.clear();
				}
			} else {
				facilityDetails.setVisible(false);
				facilityDetails.setRequired(false);
				facilityDetails.clear();
			}	
		});
    	
    }
    
    public String getPersonFirstName() {
    	return (String)getField(FIRST_NAME).getValue();
    }

    public String getPersonLastName() {
    	return (String)getField(LAST_NAME).getValue();
    }
    
    public void setPerson(PersonDto person) {
    	((TextField) getField(FIRST_NAME)).setValue(person.getFirstName());
    	((TextField) getField(LAST_NAME)).setValue(person.getLastName());
    }
    
    public void setNameReadOnly(boolean readOnly) {
    	getField(FIRST_NAME).setEnabled(!readOnly);
    	getField(LAST_NAME).setEnabled(!readOnly);
    }
    
    public void setDiseaseReadOnly(boolean readOnly) {
    	getField(CaseDataDto.DISEASE).setEnabled(!readOnly);
    }
    
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
