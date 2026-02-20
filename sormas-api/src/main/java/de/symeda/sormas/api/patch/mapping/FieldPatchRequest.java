package de.symeda.sormas.api.patch.mapping;

import java.util.Objects;

import de.symeda.sormas.api.patch.DataReplacementType;

public final class FieldPatchRequest {

	private String fieldName;
	private Object target;
	private Object value;
	private DataReplacementType replacementType;

	public String getFieldName() {
		return fieldName;
	}

	public FieldPatchRequest setFieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	public Object getTarget() {
		return target;
	}

	public FieldPatchRequest setTarget(Object target) {
		this.target = target;
		return this;
	}

	public Object getValue() {
		return value;
	}

	public FieldPatchRequest setValue(Object value) {
		this.value = value;
		return this;
	}

	public DataReplacementType getReplacementType() {
		return replacementType;
	}

	public FieldPatchRequest setReplacementType(DataReplacementType replacementType) {
		this.replacementType = replacementType;
		return this;
	}

	@Override
	public String toString() {
		return "FieldPatchRequest{" + "fieldName='" + fieldName + '\'' + ", target=" + target + ", value=" + value + ", replacementType="
			+ replacementType + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		FieldPatchRequest that = (FieldPatchRequest) o;
		return Objects.equals(fieldName, that.fieldName)
			&& Objects.equals(target, that.target)
			&& Objects.equals(value, that.value)
			&& replacementType == that.replacementType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldName, target, value, replacementType);
	}
}
