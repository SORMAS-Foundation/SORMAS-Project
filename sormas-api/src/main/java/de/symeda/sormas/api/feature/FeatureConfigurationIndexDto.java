package de.symeda.sormas.api.feature;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;

public class FeatureConfigurationIndexDto implements Serializable {

	private static final long serialVersionUID = -8033830301352311580L;

	public static final String I18N_PREFIX = "FeatureConfiguration";

	public static final String DISTRICT_NAME = "districtName";
	public static final String ACTIVE = "active";
	public static final String END_DATE = "endDate";
	
	private String uuid;
	private String regionUuid;
	private String districtUuid;
	private String districtName;
	private Disease disease;
	private Boolean active;
	private Date endDate;
	
	public FeatureConfigurationIndexDto(String uuid, String regionUuid, String districtUuid, 
			String districtName, Disease disease, Date endDate) {
		this.uuid = uuid;
		this.regionUuid = regionUuid;
		this.districtUuid = districtUuid;
		this.districtName = districtName;
		this.disease = disease;
		this.endDate = endDate;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getRegionUuid() {
		return regionUuid;
	}

	public void setRegionUuid(String regionUuid) {
		this.regionUuid = regionUuid;
	}

	public String getDistrictUuid() {
		return districtUuid;
	}

	public void setDistrictUuid(String districtUuid) {
		this.districtUuid = districtUuid;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public Disease getDisease() {
		return disease;
	}
	
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	
	public Boolean getActive() {
		return active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
