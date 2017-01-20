package de.symeda.sormas.ui.location;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class LocationForm extends AbstractEditForm<LocationDto> {

    private static final String HTML_LAYOUT = 
    		LayoutUtil.div(
    				LayoutUtil.fluidRowLocs(LocationDto.ADDRESS, LocationDto.DETAILS),
    				LayoutUtil.fluidRowLocs(LocationDto.REGION, LocationDto.DISTRICT),
    				LayoutUtil.fluidRowLocs(LocationDto.COMMUNITY, LocationDto.CITY)
    			);

    public LocationForm() {
    	super(LocationDto.class, LocationDto.I18N_PREFIX);
    }
    
    public void setFieldsRequirement(boolean required, String... fieldIds) {
    	setRequired(required, fieldIds);
    }

    @Override
	protected void addFields() {
    	addField(LocationDto.ADDRESS, TextArea.class).setRows(2);;
    	addField(LocationDto.DETAILS, TextArea.class).setRows(2);
    	addField(LocationDto.CITY, TextField.class);
//    	addField(LocationDto.LATITUDE, TextField.class);
//    	addField(LocationDto.LONGITUDE, TextField.class);

    	ComboBox region = addField(LocationDto.REGION, ComboBox.class);
    	ComboBox district = addField(LocationDto.DISTRICT, ComboBox.class);
    	ComboBox community = addField(LocationDto.COMMUNITY, ComboBox.class);
    	
    	region.addValueChangeListener(e -> {
    		district.removeAllItems();
    		RegionReferenceDto regionDto = (RegionReferenceDto)e.getProperty().getValue();
    		if (regionDto != null) {
    			district.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()));
    		}
    	});
    	district.addValueChangeListener(e -> {
    		community.removeAllItems();
    		DistrictReferenceDto districtDto = (DistrictReferenceDto)e.getProperty().getValue();
    		if (districtDto != null) {
    			community.addItems(FacadeProvider.getCommunityFacade().getAllByDistrict(districtDto.getUuid()));
    		}
    	});
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
    }

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
