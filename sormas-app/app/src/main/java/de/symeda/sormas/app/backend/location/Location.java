package de.symeda.sormas.app.backend.location;

import android.databinding.Bindable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

@Entity(name=Location.TABLE_NAME)
@DatabaseTable(tableName = Location.TABLE_NAME)
@EmbeddedAdo
public class Location extends AbstractDomainObject {
	
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

	public String getCompleteString() {

		StringBuilder sb = new StringBuilder();
		if (getAddress() != null && !getAddress().isEmpty()) {
			sb.append(getAddress());
		}
		if ((getCity() != null && !getCity().isEmpty()) || getCommunity() != null || getDistrict() != null) {
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
		}
		if (getDetails() != null && !getDetails().isEmpty()) {
			if ((getAddress() != null && !getAddress().isEmpty()) || (getCity() != null && !getCity().isEmpty()) ||
					getCommunity() != null || getDistrict() != null) {
				sb.append("\n");
			}
			sb.append(getDetails());
		}

		if (getLatitude() != null && getLongitude() != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append("(").append(android.location.Location.convert(getLatitude(), android.location.Location.FORMAT_DEGREES))
					.append(", ").append(android.location.Location.convert(getLongitude(), android.location.Location.FORMAT_DEGREES)).append(")");
		}

		return sb.toString();
	}

	@Override
	public String toString() {

		String result = getCompleteString();
		if (!result.isEmpty()) {
			return result;
		}
		return super.toString();
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
