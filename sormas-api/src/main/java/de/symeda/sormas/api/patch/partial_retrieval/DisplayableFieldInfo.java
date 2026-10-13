package de.symeda.sormas.api.patch.partial_retrieval;

import java.io.Serializable;
import java.util.Objects;

/**
 * Type to display a specific value to the user with its field name and its value.
 */
public class DisplayableFieldInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String translatedFieldName;
	private String translatedFieldValue;

	public String getTranslatedFieldName() {
		return translatedFieldName;
	}

	public DisplayableFieldInfo setTranslatedFieldName(String translatedFieldName) {
		this.translatedFieldName = translatedFieldName;
		return this;
	}

	public String getTranslatedFieldValue() {
		return translatedFieldValue;
	}

	public DisplayableFieldInfo setTranslatedFieldValue(String translatedFieldValue) {
		this.translatedFieldValue = translatedFieldValue;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		DisplayableFieldInfo that = (DisplayableFieldInfo) o;
		return Objects.equals(translatedFieldName, that.translatedFieldName) && Objects.equals(translatedFieldValue, that.translatedFieldValue);
	}

	@Override
	public int hashCode() {
		return Objects.hash(translatedFieldName, translatedFieldValue);
	}

	@Override
	public String toString() {
		return "DisplayableFieldInfo{" + "translatedFieldName='" + translatedFieldName + '\'' + ", translatedFieldValue='" + translatedFieldValue
			+ '\'' + '}';
	}
}
