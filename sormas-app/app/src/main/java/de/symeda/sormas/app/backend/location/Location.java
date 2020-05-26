/*
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
 */

package de.symeda.sormas.app.backend.location;

import androidx.databinding.Bindable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.symeda.sormas.api.location.AreaType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;
import de.symeda.sormas.app.backend.common.PseudonymizableDomainObject;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

@Entity(name=Location.TABLE_NAME)
@DatabaseTable(tableName = Location.TABLE_NAME)
@EmbeddedAdo
public class Location extends PseudonymizableDomainObject {

	private static final long serialVersionUID = 392776645668778670L;

	public static final String TABLE_NAME = "location";
	public static final String I18N_PREFIX = "Location";
	public static final String COMMUNITY = "community";

	@Column(length = 255)
	private String address;
	@Column(length = 255)
	private String details;
	@Column(length = 255)
	private String city;
	@Column
	private AreaType areaType;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Region region;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private District district;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Community community;

	@DatabaseField
	private Double latitude;
	@DatabaseField
	private Double longitude;
	@DatabaseField
	private Float latLonAccuracy;

	@Column(length = 255)
	private String postalCode;

	@Bindable
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	@Bindable
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}

	@Bindable
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public AreaType getAreaType() {
		return areaType;
	}
	public void setAreaType(AreaType areaType) {
		this.areaType = areaType;
	}

	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}

	public District getDistrict() {
		return district;
	}
	public void setDistrict(District district) {
		this.district = district;
	}

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

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCompleteString() {

		StringBuilder sb = new StringBuilder();
		if (getAddress() != null && !getAddress().isEmpty()) {
			sb.append(getAddress());
		}
		if ((getCity() != null && !getCity().isEmpty()) || getCommunity() != null || getDistrict() != null
				|| getAreaType() != null) {
			if (getAddress() != null && !getAddress().isEmpty()) {
				sb.append("\n");
			}
			if (getCity() != null && !getCity().isEmpty()) {
				sb.append(getCity());
			} else if (getCommunity() != null) {
				sb.append(getCommunity());
			}
			if (getDistrict() != null) {
				if ((getCity() != null && !getCity().isEmpty()) || getCommunity() != null) {
					sb.append(", ");
				}
				sb.append(getDistrict());
			}
			if (getAreaType() != null) {
				if ((!StringUtils.isEmpty(getCity()) || getCommunity() != null || getDistrict() != null)) {
					sb.append(", ");
				}
				sb.append(getAreaType().toString());
			}
		}

		if (getDetails() != null && !getDetails().isEmpty()) {
			if ((getAddress() != null && !getAddress().isEmpty()) || (getCity() != null && !getCity().isEmpty()) ||
					getCommunity() != null || getDistrict() != null) {
				sb.append("\n");
			}
			sb.append(getDetails());
		}

		String latLonString = getLatLonString();
		if (!DataHelper.isNullOrEmpty(latLonString)) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(latLonString);
		}

		return sb.toString();
	}

	public String getLatLonString() {
		return getLatLonString(getLatitude(), getLongitude(), getLatLonAccuracy());
	}

	public static String getLatLonString(Double latitude, Double longitude, Float latLonAccuracy) {
		if (latitude != null && longitude != null) {

			StringBuilder resultString = new StringBuilder();
			DecimalFormat df = new DecimalFormat("###.#####");

			try {
				String latString = df.format(latitude);
				String lonString = df.format(longitude);
				resultString.append(latString).append(", ").append(lonString);
				if (latLonAccuracy != null) {
					resultString.append(" +-").append(Math.round(latLonAccuracy)).append("m");
				}
				return resultString.toString();
			} catch(IllegalArgumentException convertException) {
				// ignore
			}
		}
		return "";
	}

	@Override
	public String toString() {
		return getCompleteString();
	}

	public boolean isEmptyLocation() {
		return address == null && details == null && city == null && region == null &&
				district == null && community == null;
	}

	public String getGpsLocation() {
		if (latitude == null || longitude == null) {
			return "";
		}

		if (latLonAccuracy != null) {
			return android.location.Location.convert(latitude, android.location.Location.FORMAT_DEGREES)
					+ ", " + android.location.Location.convert(longitude, android.location.Location.FORMAT_DEGREES)
					+ " +-" + Math.round(latLonAccuracy) + "m";
		} else {
			return android.location.Location.convert(latitude, android.location.Location.FORMAT_DEGREES)
					+ ", " + android.location.Location.convert(longitude, android.location.Location.FORMAT_DEGREES);
		}
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public Float getLatLonAccuracy() {
		return latLonAccuracy;
	}

	public void setLatLonAccuracy(Float latLonAccuracy) {
		this.latLonAccuracy = latLonAccuracy;
	}
}
