package de.symeda.sormas.api.feature;

import static de.symeda.sormas.api.HasUuid.UUID_REGEX;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_UUID_MAX;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_UUID_MIN;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Validations;

public class FeatureConfigurationIndexDto implements Serializable {

	private static final long serialVersionUID = -8033830301352311580L;

	public static final String I18N_PREFIX = "FeatureConfiguration";

	public static final String REGION_NAME = "regionName";
	public static final String DISTRICT_NAME = "districtName";
	public static final String ENABLED = "enabled";
	public static final String END_DATE = "endDate";

	@Pattern(regexp = UUID_REGEX, message = Validations.patternNotMatching)
	@Size(min = CHARACTER_LIMIT_UUID_MIN, max = CHARACTER_LIMIT_UUID_MAX, message = Validations.textSizeNotInRange)
	private String uuid;
	@Pattern(regexp = UUID_REGEX, message = Validations.patternNotMatching)
	@Size(min = CHARACTER_LIMIT_UUID_MIN, max = CHARACTER_LIMIT_UUID_MAX, message = Validations.textSizeNotInRange)
	private String regionUuid;
	private String regionName;
	@Pattern(regexp = UUID_REGEX, message = Validations.patternNotMatching)
	@Size(min = CHARACTER_LIMIT_UUID_MIN, max = CHARACTER_LIMIT_UUID_MAX, message = Validations.textSizeNotInRange)
	private String districtUuid;
	private String districtName;
	private Disease disease;
	private boolean enabled;
	private Date endDate;

	public FeatureConfigurationIndexDto(
		String uuid,
		String regionUuid,
		String regionName,
		String districtUuid,
		String districtName,
		Disease disease,
		Boolean enabled,
		Date endDate) {

		this.uuid = uuid;
		this.regionUuid = regionUuid;
		this.regionName = regionName;
		this.districtUuid = districtUuid;
		this.districtName = districtName;
		this.disease = disease;
		this.enabled = enabled != null ? enabled : false;
		this.endDate = endDate;
	}

	public FeatureConfigurationIndexDto() {
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

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
