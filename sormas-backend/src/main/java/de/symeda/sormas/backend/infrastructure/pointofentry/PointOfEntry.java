package de.symeda.sormas.backend.infrastructure.pointofentry;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryType;
import de.symeda.sormas.backend.infrastructure.InfrastructureAdo;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PointOfEntry extends InfrastructureAdo {

	private static final long serialVersionUID = -6684018483640792433L;

	public static final String TABLE_NAME = "pointofentry";

	public static final String POINT_OF_ENTRY_TYPE = "pointOfEntryType";
	public static final String NAME = "name";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String ACTIVE = "active";
	public static final String EXTERNAL_ID = "externalID";

	private PointOfEntryType pointOfEntryType;
	private String name;
	private Region region;
	private District district;
	private Double latitude;
	private Double longitude;
	private boolean active;
	private String externalID;

	@Enumerated(EnumType.STRING)
	public PointOfEntryType getPointOfEntryType() {
		return pointOfEntryType;
	}

	public void setPointOfEntryType(PointOfEntryType pointOfEntryType) {
		this.pointOfEntryType = pointOfEntryType;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}
}
