package de.symeda.sormas.api.feature;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class FeatureConfigurationCriteria extends BaseCriteria implements Cloneable {

	private static final long serialVersionUID = 4080680557738276176L;

	public static final String FEATURE_TYPE = "featureType";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String DISEASE = "disease";
	public static final String ENABLED = "enabled";

	private FeatureType[] featureTypes;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Disease disease;
	private Boolean enabled;

	public FeatureType[] getFeatureTypes() {
		return featureTypes;
	}

	public FeatureConfigurationCriteria featureType(FeatureType... featureTypes) {
		this.featureTypes = featureTypes;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public FeatureConfigurationCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public FeatureConfigurationCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public FeatureConfigurationCriteria disease(Disease disease) {
		this.disease = disease;
		return this;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public FeatureConfigurationCriteria enabled(Boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public void setFeatureTypes(FeatureType... featureTypes) {
		this.featureTypes = featureTypes;
	}
}
