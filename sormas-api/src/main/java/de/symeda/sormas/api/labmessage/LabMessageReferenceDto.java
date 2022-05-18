package de.symeda.sormas.api.labmessage;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DependingOnFeatureType;

@DependingOnFeatureType(featureType = FeatureType.LAB_MESSAGES)
public class LabMessageReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -4467135674568277340L;

	public LabMessageReferenceDto() {

	}

	public LabMessageReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}

	public LabMessageReferenceDto(String uuid) {
		setUuid(uuid);
	}
}
