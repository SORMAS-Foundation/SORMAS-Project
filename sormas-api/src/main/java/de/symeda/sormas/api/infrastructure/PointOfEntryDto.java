package de.symeda.sormas.api.infrastructure;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class PointOfEntryDto extends EntityDto {

	private static final long serialVersionUID = 4124483408068181854L;
	
	public static final String I18N_PREFIX = "PointOfEntry";
	public static final String OTHER_AIRPORT_UUID = "SORMAS-CONSTID-OTHERS-AIRPORTX";
	public static final String OTHER_SEAPORT_UUID = "SORMAS-CONSTID-OTHERS-SEAPORTX";
	public static final String OTHER_GROUND_CROSSING_UUID = "SORMAS-CONSTIG-OTHERS-GROUNDCR";
	public static final String OTHER_POE_UUID = "SORMAS-CONSTID-OTHERS-OTHERPOE";
	public static final String OTHER_AIRPORT = "OTHER_AIRPORT";
	public static final String OTHER_SEAPORT = "OTHER_SEAPORT";
	public static final String OTHER_GROUND_CROSSING = "OTHER_GROUND_CROSSING";
	public static final String OTHER_POE = "OTHER_POE";
	
	public static final String POINT_OF_ENTRY_TYPE = "pointOfEntryType";
	public static final String NAME = "name";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String ACTIVE = "active";
	
	private PointOfEntryType pointOfEntryType;
	private String name;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Double latitude;
	private Double longitude;
	private boolean active;
	
	public PointOfEntryType getPointOfEntryType() {
		return pointOfEntryType;
	}
	public void setPointOfEntryType(PointOfEntryType pointOfEntryType) {
		this.pointOfEntryType = pointOfEntryType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public RegionReferenceDto getRegion() {
		return region;
	}
	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}
	public DistrictReferenceDto getDistrict() {
		return district;
	}
	public void setDistrict(DistrictReferenceDto district) {
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
	
	@Override
	public String toString() {
		return InfrastructureHelper.buildPointOfEntryString(getUuid(), name);
	}
	
}
