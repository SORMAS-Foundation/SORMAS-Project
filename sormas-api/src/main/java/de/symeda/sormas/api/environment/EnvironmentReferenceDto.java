package de.symeda.sormas.api.environment;

import de.symeda.sormas.api.ReferenceDto;

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
