package de.symeda.sormas.api.environment;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DependingOnFeatureType;

@DependingOnFeatureType(featureType = FeatureType.ENVIRONMENT_MANAGEMENT)
public class EnvironmentReferenceDto extends ReferenceDto {

	public EnvironmentReferenceDto() {

	}

	public EnvironmentReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public EnvironmentReferenceDto(String uuid, String environmentName) {
		setUuid(uuid);
		setCaption(environmentName);
	}
}
