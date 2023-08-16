package de.symeda.sormas.api.environment;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.SensitiveData;

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
}
