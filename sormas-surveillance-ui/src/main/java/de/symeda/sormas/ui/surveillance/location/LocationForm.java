package de.symeda.sormas.ui.surveillance.location;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class LocationForm extends AbstractEditForm<LocationDto> {

	private static final long serialVersionUID = -1L;
    
    private static final String HTML_LAYOUT = 
    		LayoutUtil.div(
    				LayoutUtil.fluidRowLocs(LocationDto.ADDRESS, LocationDto.DETAILS),
    				LayoutUtil.fluidRowLocs(LocationDto.REGION, LocationDto.DISTRICT, LocationDto.COMMUNITY),
    				LayoutUtil.fluidRowLocs(LocationDto.CITY, LocationDto.LATITUDE, LocationDto.LONGITUDE)
    			);

    public LocationForm() {
    	super(LocationDto.class, LocationDto.I18N_PREFIX);
    }

    @Override
	protected void addFields() {
    	addField(LocationDto.ADDRESS, TextArea.class).setRows(2);;
    	addField(LocationDto.DETAILS, TextArea.class).setRows(2);
    	addField(LocationDto.CITY, TextField.class);
    	addField(LocationDto.LATITUDE, TextField.class);
    	addField(LocationDto.LONGITUDE, TextField.class);

    	ComboBox region = addField(LocationDto.REGION, ComboBox.class);
    	ComboBox district = addField(LocationDto.DISTRICT, ComboBox.class);
    	ComboBox community = addField(LocationDto.COMMUNITY, ComboBox.class);
    	
    	region.addValueChangeListener(e -> {
    		district.removeAllItems();
    		ReferenceDto regionDto = (ReferenceDto)e.getProperty().getValue();
    		if (regionDto != null) {
    			district.addItems(FacadeProvider.getDistrictFacade().getAllAsReference(regionDto.getUuid()));
    		}
    	});
    	district.addValueChangeListener(e -> {
    		community.removeAllItems();
    		ReferenceDto districtDto = (ReferenceDto)e.getProperty().getValue();
    		if (districtDto != null) {
    			community.addItems(FacadeProvider.getCommunityFacade().getAllAsReference(districtDto.getUuid()));
    		}
    	});
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
    }

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
