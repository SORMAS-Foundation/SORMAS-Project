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

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.StringToAngularLocationConverter;

@SuppressWarnings("serial")
public class LocationEditForm extends AbstractEditForm<LocationDto> {

	private static final String GEOCODING_LOC = "geocoding";

	private static final String HTML_LAYOUT = 
			LayoutUtil.divs(
			LayoutUtil.fluidRow(
					LayoutUtil.loc(LocationDto.ADDRESS),
					LayoutUtil.divs(
							LayoutUtil.fluidRow(
									LayoutUtil.fluidColumn(6, 0,
											LayoutUtil.locs(LocationDto.POSTAL_CODE, LocationDto.AREA_TYPE)),
													LayoutUtil.fluidColumn(6, 0, LayoutUtil.loc(LocationDto.CITY))))),
			LayoutUtil.fluidRowLocs(LocationDto.REGION, LocationDto.DISTRICT, LocationDto.COMMUNITY),
			LayoutUtil.fluidRow(LayoutUtil.loc(LocationDto.DETAILS),
					LayoutUtil.fluidRow(
							fluidColumnLoc(2, 0, GEOCODING_LOC), 
							fluidColumnLoc(3, 0, LocationDto.LATITUDE),
							fluidColumnLoc(3, 0, LocationDto.LONGITUDE),
							fluidColumnLoc(4, 0, LocationDto.LAT_LON_ACCURACY))));

	public LocationEditForm(UserRight editOrCreateUserRight) {
		super(LocationDto.class, LocationDto.I18N_PREFIX, editOrCreateUserRight);

		if (FacadeProvider.getGeocodingFacade().isEnabled()) {
			Button geocodeButton = new Button(VaadinIcons.MAP_MARKER, e -> triggerGeocoding());
			geocodeButton.setWidth(100, Unit.PERCENTAGE);
			geocodeButton.setStyleName(ValoTheme.BUTTON_HUGE);
			geocodeButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			getContent().addComponent(geocodeButton, GEOCODING_LOC);
		}
	}

	private void triggerGeocoding() {

		String address = getConvertedValue(LocationDto.ADDRESS);
		String postalCode = getConvertedValue(LocationDto.POSTAL_CODE);
		String city = getConvertedValue(LocationDto.CITY);

		GeoLatLon latLon = FacadeProvider.getGeocodingFacade().getLatLon(address, postalCode, city);

		if (latLon != null) {
			setConvertedValue(LocationDto.LATITUDE, latLon.getLat());
			setConvertedValue(LocationDto.LONGITUDE, latLon.getLon());
		}
	}

	private void setConvertedValue(String propertyId, Object value) {
		((AbstractField<?>) getField(propertyId)).setConvertedValue(value);
	}

	@SuppressWarnings("unchecked")
	private <T> T getConvertedValue(String propertyId) {
		return (T) ((AbstractField<?>) getField(propertyId)).getConvertedValue();
	}

	public void setFieldsRequirement(boolean required, String... fieldIds) {
		setRequired(required, fieldIds);
	}

	@Override
	protected void addFields() {
		addField(LocationDto.ADDRESS, TextArea.class).setRows(5);
		addField(LocationDto.DETAILS, TextField.class);
		addField(LocationDto.CITY, TextField.class);
		addField(LocationDto.POSTAL_CODE, TextField.class);
		ComboBox areaType = addField(LocationDto.AREA_TYPE, ComboBox.class);
		areaType.setDescription(I18nProperties.getDescription(getPropertyI18nPrefix() + "." + LocationDto.AREA_TYPE));

		addField(LocationDto.LATITUDE, TextField.class).setConverter(new StringToAngularLocationConverter());
		addField(LocationDto.LONGITUDE, TextField.class).setConverter(new StringToAngularLocationConverter());
		addField(LocationDto.LAT_LON_ACCURACY, TextField.class);

		ComboBox region = addInfrastructureField(LocationDto.REGION);
		ComboBox district = addInfrastructureField(LocationDto.DISTRICT);
		ComboBox community = addInfrastructureField(LocationDto.COMMUNITY);

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		district.addValueChangeListener(e -> {
			FieldHelper.removeItems(community);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(community, districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
		});
		region.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
