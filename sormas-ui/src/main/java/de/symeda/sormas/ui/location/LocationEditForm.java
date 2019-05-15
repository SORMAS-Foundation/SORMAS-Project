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
package de.symeda.sormas.ui.location;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

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
    								LayoutUtil.fluidRowLocs(LocationDto.AREA_TYPE, LocationDto.CITY))),
    				LayoutUtil.fluidRowLocs(
    						LocationDto.REGION, LocationDto.DISTRICT, LocationDto.COMMUNITY),
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
    	addField(LocationDto.AREA_TYPE, ComboBox.class);
    	
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
