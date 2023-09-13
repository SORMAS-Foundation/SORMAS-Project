package de.symeda.sormas.api.environment;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.SensitiveData;

@DependingOnFeatureType(featureType = FeatureType.ENVIRONMENT_MANAGEMENT)
public class EnvironmentReferenceDto extends ReferenceDto {

	@SensitiveData
	private String environmentName;

	public EnvironmentReferenceDto() {

	}

	public EnvironmentReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public EnvironmentReferenceDto(String uuid, String environmentName) {
		setUuid(uuid);
		this.environmentName = environmentName;
	}

	@Override
	public String getCaption() {
		return environmentName;
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}
}
