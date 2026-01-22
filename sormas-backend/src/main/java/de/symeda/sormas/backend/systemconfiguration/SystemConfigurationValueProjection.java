package de.symeda.sormas.backend.systemconfiguration;

import java.util.Optional;

public class SystemConfigurationValueProjection {

	private String value;
	private String defaultValue;

	public SystemConfigurationValueProjection() {
	}

	public SystemConfigurationValueProjection(String value, String defaultValue) {
		this.value = value;
		this.defaultValue = defaultValue;
	}

	public Optional<String> getActualValue() {
		return Optional.ofNullable(value).or(() -> Optional.ofNullable(defaultValue));
	}
}
