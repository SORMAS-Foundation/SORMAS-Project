package de.symeda.sormas.api.i18n;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Language;

public class I18nPropertiesRequest {

	/**
	 * If not provided will fallback to default language.
	 */
	@Nullable
	private Language language;

	@NotNull
	private ResourceBundleType resourceBundleType;

	@NotNull
	private Class<?> targetType;

	@Nullable
	public Language getLanguage() {
		return language;
	}

	public I18nPropertiesRequest setLanguage(@Nullable Language language) {
		this.language = language;
		return this;
	}

	public ResourceBundleType getResourceBundleType() {
		return resourceBundleType;
	}

	public I18nPropertiesRequest setResourceBundleType(ResourceBundleType resourceBundleType) {
		this.resourceBundleType = resourceBundleType;
		return this;
	}

	@NotNull
	public Class<?> getTargetType() {
		return targetType;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		I18nPropertiesRequest that = (I18nPropertiesRequest) o;
		return language == that.language && resourceBundleType == that.resourceBundleType && Objects.equals(targetType, that.targetType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(language, resourceBundleType, targetType);
	}

	public I18nPropertiesRequest setTargetType(@NotNull Class<?> targetType) {
		this.targetType = targetType;
		return this;
	}

	@Override
	public String toString() {
		return "I18nPropertiesRequest{" + "language=" + language + ", resourceBundleType=" + resourceBundleType + ", prefix='" + targetType + '\''
			+ '}';
	}

	public enum ResourceBundleType {
		CAPTION,
		DESCRIPTION,
		ENUMS,
		VALIDATION,
		STRING,
		COUNTRY,
		CONTINENT,
		SUBCONTINENT
	}
}
