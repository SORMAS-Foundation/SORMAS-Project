package de.symeda.sormas.api.patch.partial_retrieval;

import java.util.Objects;

/**
 * Half technical and half user-friendly representation of a field.
 */
public class FieldInfo {

	private String translatedFieldName;
	private Class<?> fieldType;
	private Object fieldValue;

	public String getTranslatedFieldName() {
		return translatedFieldName;
	}

	public FieldInfo setTranslatedFieldName(String translatedFieldName) {
		this.translatedFieldName = translatedFieldName;
		return this;
	}

	public Class<?> getFieldType() {
		return fieldType;
	}

	public FieldInfo setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
		return this;
	}

	public Object getFieldValue() {
		return fieldValue;
	}

	public FieldInfo setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		FieldInfo fieldInfo = (FieldInfo) o;
		return Objects.equals(translatedFieldName, fieldInfo.translatedFieldName)
			&& Objects.equals(fieldType, fieldInfo.fieldType)
			&& Objects.equals(fieldValue, fieldInfo.fieldValue);
	}

	@Override
	public int hashCode() {
		return Objects.hash(translatedFieldName, fieldType, fieldValue);
	}

	@Override
	public String toString() {
		return "FieldInfo{" + "translatedFieldName='" + translatedFieldName + '\'' + ", fieldType=" + fieldType + ", fieldValue=" + fieldValue + '}';
	}
}
