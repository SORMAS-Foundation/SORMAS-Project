package de.symeda.sormas.app.backend.location;

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


	private String address;
	private String details;
	private String city;
	
	private Region region;
	private District district;
	private Community community;
	
	private Float latitude;
	private Float longitude;

	
	@Column(length = 255)
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	@Column(length = 255)
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
	@Column(length = 255)
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
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
	
	@Column(columnDefinition = "float8")
	public Float getLatitude() {
		return latitude;
	}
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	
	@Column(columnDefinition = "float8")
	public Float getLongitude() {
		return longitude;
	}
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
}
