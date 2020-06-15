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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.location;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.AreaType;
import de.symeda.sormas.api.location.LocationReferenceDto;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

@Entity
@Audited
public class Location extends AbstractDomainObject {

	private static final long serialVersionUID = 392776645668778670L;

	public static final String TABLE_NAME = "location";

	public static final String ADDRESS = "address";
	public static final String DETAILS = "details";
	public static final String CITY = "city";
	public static final String AREA_TYPE = "areaType";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String POSTAL_CODE = "postalCode";

	private String address;
	private String details;
	private String city;
	private AreaType areaType;

	private Region region;
	private District district;
	private Community community;

	private Double latitude;
	private Double longitude;
	private Float latLonAccuracy;

	private String postalCode;

	@Column(length = COLUMN_LENGTH_BIG)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Enumerated(EnumType.STRING)
	public AreaType getAreaType() {
		return areaType;
	}

	public void setAreaType(AreaType areaType) {
		this.areaType = areaType;
	}

	@ManyToOne(cascade = {})
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@ManyToOne(cascade = {})
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@ManyToOne(cascade = {})
	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Float getLatLonAccuracy() {
		return latLonAccuracy;
	}

	public void setLatLonAccuracy(Float latLonAccuracy) {
		this.latLonAccuracy = latLonAccuracy;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String buildGpsCoordinatesCaption() {
		if (latitude == null && longitude == null) {
			return "";
		} else if (latitude == null || longitude == null) {
			return I18nProperties.getString(Strings.messageIncompleteGpsCoordinates);
		} else if (latLonAccuracy == null) {
			return latitude + ", " + longitude;
		} else {
			return latitude + ", " + longitude + " +-" + Math.round(latLonAccuracy) + "m";
		}
	}

	@Override
	public String toString() {
		return LocationReferenceDto.buildCaption(
			region != null ? region.getName() : null,
			district != null ? district.getName() : null,
			community != null ? community.getName() : null,
			city,
			address);
	}
}
