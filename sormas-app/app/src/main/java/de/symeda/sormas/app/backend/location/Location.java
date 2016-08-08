package de.symeda.sormas.app.backend.location;

import android.databinding.Bindable;

import com.j256.ormlite.table.DatabaseTable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

@Entity(name=Location.TABLE_NAME)
@DatabaseTable(tableName = Location.TABLE_NAME)
public class Location extends AbstractDomainObject {
	
	private static final long serialVersionUID = 392776645668778670L;

	public static final String TABLE_NAME = "location";

	@Column(length = 255)
	private String address;
	@Column(length = 255)
	private String details;
	@Column(length = 255)
	private String city;

	@ManyToOne(cascade = {})
	private Region region;
	@ManyToOne(cascade = {})
	private District district;
	@ManyToOne(cascade = {})
	private Community community;

	@Column(columnDefinition = "float8")
	private Float latitude;
	@Column(columnDefinition = "float8")
	private Float longitude;

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
	
	public Float getLatitude() {
		return latitude;
	}
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	
	public Float getLongitude() {
		return longitude;
	}
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
}
