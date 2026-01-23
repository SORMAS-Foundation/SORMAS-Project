package de.symeda.sormas.backend.systemconfiguration;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.minidev.json.annotate.JsonIgnore;

public class SystemConfigurationValueProjection {

	private String value;
	private String defaultValue;

	public SystemConfigurationValueProjection() {
	}

	public SystemConfigurationValueProjection(String value, String defaultValue) {
		this.value = value;
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	@JsonIgnore
	public Optional<String> getActualValue() {
		return Optional.ofNullable(value).or(() -> Optional.ofNullable(defaultValue));
	}

	@JsonProperty("actualValue")
	String getActualValueAsString() {
		return getActualValue().orElse(null);
	}
}
