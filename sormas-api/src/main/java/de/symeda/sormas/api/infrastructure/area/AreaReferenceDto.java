package de.symeda.sormas.api.infrastructure.area;

import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DependingOnFeatureType;

@DependingOnFeatureType(featureType = FeatureType.INFRASTRUCTURE_TYPE_AREA)
public class AreaReferenceDto extends InfrastructureDataReferenceDto {

	private static final long serialVersionUID = -6241927331721175673L;

	public AreaReferenceDto() {

	}

	public AreaReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public AreaReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}

	public AreaReferenceDto(String uuid, String caption, String externalId) {
		super(uuid, caption, externalId);
	}

}
