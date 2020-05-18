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

import static de.symeda.sormas.ui.utils.LayoutUtil.divs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.Collections;
import java.util.stream.Stream;

import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.MaxLengthValidator;
import de.symeda.sormas.ui.utils.ValidationConstants;
import de.symeda.sormas.ui.utils.ButtonHelper;
import org.apache.commons.lang3.ObjectUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupView;
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
import de.symeda.sormas.ui.map.LeafletMap;
import de.symeda.sormas.ui.map.LeafletMarker;
import de.symeda.sormas.ui.map.MarkerIcon;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.StringToAngularLocationConverter;

public class LocationEditForm extends AbstractEditForm<LocationDto> {

	private static final long serialVersionUID = 1L;


	private static final String GEO_BUTTONS_LOC = "geoButtons";

	private static final String HTML_LAYOUT =
			//XXX #1620 are the divs needed?
			divs(
					fluidRow(
							loc(LocationDto.ADDRESS),
							//XXX #1620 are the divs needed?
							divs(
									fluidRow(
											fluidColumn(6, 0, locs(LocationDto.POSTAL_CODE, LocationDto.AREA_TYPE)),
											fluidColumn(6, 0, loc(LocationDto.CITY)))
							)
					),
					fluidRowLocs(LocationDto.REGION, LocationDto.DISTRICT, LocationDto.COMMUNITY),
					fluidRow(
							loc(LocationDto.DETAILS),
							fluidRow(
									fluidColumnLoc(2, 0, GEO_BUTTONS_LOC),
									fluidColumnLoc(3, 0, LocationDto.LATITUDE),
									fluidColumnLoc(3, 0, LocationDto.LONGITUDE),
									fluidColumnLoc(4, 0, LocationDto.LAT_LON_ACCURACY)))
			);

	private MapPopupView leafletMapPopup;

	public LocationEditForm(UserRight editOrCreateUserRight, FieldVisibilityCheckers fieldVisibilityCheckers) {
		super(LocationDto.class, LocationDto.I18N_PREFIX, editOrCreateUserRight, fieldVisibilityCheckers);

		if (FacadeProvider.getGeocodingFacade().isEnabled() &&
				isVisibleAllowed(LocationDto.LATITUDE) && isVisibleAllowed(LocationDto.LONGITUDE)) {
			getContent().addComponent(createGeoButton(), GEO_BUTTONS_LOC);
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
		TextArea addressField = addField(LocationDto.ADDRESS, TextArea.class);
		addressField.setRows(5);
		addressField.addValidator(new MaxLengthValidator(ValidationConstants.TEXT_FIELD_MAX_LENGTH));

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

		initializeVisibilitiesAndAllowedVisibilities();

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

		Stream.of(LocationDto.LATITUDE, LocationDto.LONGITUDE)
				.map(this::getField)
				.forEach(f -> f.addValueChangeListener(e -> this.updateLeafletMapContent()));

	}

	private HorizontalLayout createGeoButton() {
		HorizontalLayout geoButtonLayout = new HorizontalLayout();
		geoButtonLayout.setMargin(false);
		geoButtonLayout.setSpacing(false);

		Button geocodeButton = new Button(VaadinIcons.MAP_MARKER, e -> triggerGeocoding());
		geocodeButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		geocodeButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		geocodeButton.addStyleName(ValoTheme.BUTTON_LARGE);

		geoButtonLayout.addComponent(geocodeButton);
		geoButtonLayout.setComponentAlignment(geocodeButton, Alignment.BOTTOM_RIGHT);

		leafletMapPopup = new MapPopupView();
		leafletMapPopup.setCaption(" ");
		leafletMapPopup.setEnabled(false);
		leafletMapPopup.setStyleName(ValoTheme.BUTTON_LARGE);
		leafletMapPopup.addStyleName(ValoTheme.BUTTON_ICON_ONLY);

		geoButtonLayout.addComponent(leafletMapPopup);
		geoButtonLayout.setComponentAlignment(leafletMapPopup, Alignment.BOTTOM_RIGHT);

		return geoButtonLayout;
	}

	private void updateLeafletMapContent() {
		if(leafletMapPopup == null){
			return;
		}

		if (areFieldsValid(LocationDto.LATITUDE, LocationDto.LONGITUDE)) {
			Double lat = getConvertedValue(LocationDto.LATITUDE);
			Double lon = getConvertedValue(LocationDto.LONGITUDE);
			GeoLatLon coordinates;
			if (ObjectUtils.allNotNull(lat, lon)) {
				coordinates = new GeoLatLon(lat, lon);
			} else {
				coordinates = null;
			}
			leafletMapPopup.setEnabled(coordinates != null);
			leafletMapPopup.setCoordinates(coordinates);
		} else {
			leafletMapPopup.setEnabled(false);
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

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	private static class MapPopupView extends PopupView {

		//eye-icon styled as button
		private static final String MINNIMIZED_HTML = "<div tabindex=\"0\" role=\"button\" class=\"v-button v-widget icon-only v-button-icon-only borderless v-button-borderless large v-button-large\"><span class=\"v-button-wrap\">" +
				VaadinIcons.EYE.getHtml() +
				"<span class=\"v-button-caption\"></span></span></div>";

		private GeoLatLon coordinates = null;

		public MapPopupView() {
			setContent(new Content() {

				@Override
				public String getMinimizedValueAsHTML() {
					return MINNIMIZED_HTML;
				}

				@Override
				public Component getPopupComponent() {
					return createLeafletMap();
				}
			});
		}

		private LeafletMap createLeafletMap() {

			LeafletMap map = new LeafletMap();
			map.setWidth(420, Unit.PIXELS);
			map.setHeight(420, Unit.PIXELS);
			map.setZoom(12);

			map.setCenter(coordinates);

			LeafletMarker marker = new LeafletMarker();
			marker.setLatLon(coordinates);
			marker.setIcon(MarkerIcon.CASE_UNCLASSIFIED);
			marker.setMarkerCount(1);

			map.addMarkerGroup("cases", Collections.singletonList(marker));

			return map;
		}


		public void setCoordinates(GeoLatLon coordinates) {
			this.coordinates = coordinates;
		}
	}
}
