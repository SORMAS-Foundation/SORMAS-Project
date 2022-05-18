package de.symeda.sormas.api.therapy;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DependingOnFeatureType;

@DependingOnFeatureType(featureType = FeatureType.CASE_SURVEILANCE)
public class TherapyReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -1467303502817738376L;

	public TherapyReferenceDto() {

	}

	public TherapyReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public TherapyReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
}
