package de.symeda.sormas.ui.location;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.StringToAngularLocationConverter;

@SuppressWarnings("serial")
public class LocationEditForm extends AbstractEditForm<LocationDto> {

	private static final String HTML_LAYOUT = 
    		LayoutUtil.divs(
    				LayoutUtil.fluidRow(
    						LayoutUtil.loc(LocationDto.ADDRESS), 
    						LayoutUtil.divs(
    								LayoutUtil.fluidRowLocs(LocationDto.REGION, LocationDto.DISTRICT),
    								LayoutUtil.fluidRowLocs(LocationDto.COMMUNITY, LocationDto.CITY))),
    				LayoutUtil.fluidRow(
    						LayoutUtil.loc(LocationDto.DETAILS), 
    						LayoutUtil.fluidRowLocs(LocationDto.LATITUDE, LocationDto.LONGITUDE, LocationDto.LAT_LON_ACCURACY))
    			);

    public LocationEditForm(UserRight editOrCreateUserRight) {
    	super(LocationDto.class, LocationDto.I18N_PREFIX, editOrCreateUserRight);
    }
    
    public void setFieldsRequirement(boolean required, String... fieldIds) {
    	setRequired(required, fieldIds);
    }

    @Override
	protected void addFields() {
    	addField(LocationDto.ADDRESS, TextArea.class).setRows(2);
    	addField(LocationDto.DETAILS, TextField.class);
    	addField(LocationDto.CITY, TextField.class);
    	
    	addField(LocationDto.LATITUDE, TextField.class).setConverter(new StringToAngularLocationConverter());
    	addField(LocationDto.LONGITUDE, TextField.class).setConverter(new StringToAngularLocationConverter());
    	addField(LocationDto.LAT_LON_ACCURACY, TextField.class);

    	ComboBox region = addField(LocationDto.REGION, ComboBox.class);
    	ComboBox district = addField(LocationDto.DISTRICT, ComboBox.class);
    	ComboBox community = addField(LocationDto.COMMUNITY, ComboBox.class);
    	
    	region.addValueChangeListener(e -> {
    		RegionReferenceDto regionDto = (RegionReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()) : null);
       	});
    	district.addValueChangeListener(e -> {
    		FieldHelper.removeItems(community);
    		DistrictReferenceDto districtDto = (DistrictReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(community, districtDto != null ? FacadeProvider.getCommunityFacade().getAllByDistrict(districtDto.getUuid()) : null);
    	});
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
    }

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
