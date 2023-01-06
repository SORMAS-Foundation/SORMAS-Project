package de.symeda.sormas.api.externalmessage;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import io.swagger.v3.oas.annotations.media.Schema;

@DependingOnFeatureType(featureType = FeatureType.EXTERNAL_MESSAGES)
@Schema(description = "Corresponding external message.")
public class ExternalMessageReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -4467135674568277340L;

	public ExternalMessageReferenceDto() {

	}

	public ExternalMessageReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}

	public ExternalMessageReferenceDto(String uuid) {
		setUuid(uuid);
	}
}
