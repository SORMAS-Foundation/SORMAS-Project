package de.symeda.sormas.backend.infrastructure;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.PointOfEntryType;
import de.symeda.sormas.backend.common.InfrastructureAdo;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

@Entity
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

	@Column(length = COLUMN_LENGTH_DEFAULT)
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

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}

	@Override
	public String toString() {
		return InfrastructureHelper.buildPointOfEntryString(getUuid(), name);
	}

}
