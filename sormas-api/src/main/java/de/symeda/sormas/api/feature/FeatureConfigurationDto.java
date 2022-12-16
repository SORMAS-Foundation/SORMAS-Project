package de.symeda.sormas.api.feature;

import java.util.Date;
import java.util.Map;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import io.swagger.v3.oas.annotations.media.Schema;

public class FeatureConfigurationDto extends EntityDto {

	private static final long serialVersionUID = 4027927530101427321L;

	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String DISEASE = "disease";
	public static final String END_DATE = "endDate";
	public static final String ENABLED = "enabled";

	private FeatureType featureType;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Disease disease;
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private Date endDate;
	@Schema(description = "Indicates whether the feature configuration is enabled.")
	private boolean enabled;
	@Schema(description = "Additional properties of a feature; i.e. adjustable settings (on/off or numbered value) for a specific functionality.")
	private Map<FeatureTypeProperty, Object> properties;

	public static FeatureConfigurationDto build() {
		FeatureConfigurationDto config = new FeatureConfigurationDto();
		config.setUuid(DataHelper.createUuid());
		return config;
	}

	public FeatureType getFeatureType() {
		return featureType;
	}

	public void setFeatureType(FeatureType featureType) {
		this.featureType = featureType;
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

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Map<FeatureTypeProperty, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<FeatureTypeProperty, Object> properties) {
		this.properties = properties;
	}
}
