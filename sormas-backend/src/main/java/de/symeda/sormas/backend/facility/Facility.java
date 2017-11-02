package de.symeda.sormas.backend.facility;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

@Entity
public class Facility extends AbstractDomainObject {
	
	private static final long serialVersionUID = 8572137127616417072L;

	public static final String NAME = "name";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String CITY = "city";
	public static final String TYPE = "type";
	
	private String name;
	private Region region;
	private District district;
	private Community community;
	private String city;
	private Double latitude;
	private Double longitude;
	private FacilityType type;
	private boolean publicOwnership;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	@Column(length = 512)
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
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
	
	@Enumerated(EnumType.STRING)
	public FacilityType getType() {
		return type;
	}
	public void setType(FacilityType type) {
		this.type = type;
	}
	
	public boolean isPublicOwnership() {
		return publicOwnership;
	}
	public void setPublicOwnership(boolean publicOwnership) {
		this.publicOwnership = publicOwnership;
	}

	@Override
	public String toString() {
		if (getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
			return I18nProperties.getPrefixFieldCaption(FacilityDto.I18N_PREFIX, FacilityDto.OTHER_FACILITY);
		}
		if (getUuid().equals(FacilityDto.NONE_FACILITY_UUID)) {
			return I18nProperties.getPrefixFieldCaption(FacilityDto.I18N_PREFIX, FacilityDto.NO_FACILITY);
		}
		
		StringBuilder caption = new StringBuilder();
		caption.append(name);

		return caption.toString();
	}
}
