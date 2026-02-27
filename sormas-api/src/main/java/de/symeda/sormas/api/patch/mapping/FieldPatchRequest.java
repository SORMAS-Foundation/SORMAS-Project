package de.symeda.sormas.api.patch.mapping;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.patch.DataReplacementStrategy;

/**
 * Patching request for a specific field, target type is known by the {@link FieldCustomMapper}.
 */
public final class FieldPatchRequest {

	@NotNull
	private String fieldName;
	@NotNull
	private Object target;
	@NotNull
	private Object value;
	@NotNull
	private DataReplacementStrategy replacementType;

	@Nullable
	private String origin;

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

	public DataReplacementStrategy getReplacementType() {
		return replacementType;
	}

	public FieldPatchRequest setReplacementType(DataReplacementStrategy replacementType) {
		this.replacementType = replacementType;
		return this;
	}

	@Nullable
	public String getOrigin() {
		return origin;
	}

	public FieldPatchRequest setOrigin(@Nullable String origin) {
		this.origin = origin;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		FieldPatchRequest that = (FieldPatchRequest) o;
		return Objects.equals(fieldName, that.fieldName)
			&& Objects.equals(target, that.target)
			&& Objects.equals(value, that.value)
			&& replacementType == that.replacementType
			&& Objects.equals(origin, that.origin);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldName, target, value, replacementType, origin);
	}

	@Override
	public String toString() {
		return "FieldPatchRequest{" + "fieldName='" + fieldName + '\'' + ", target=" + target + ", value=" + value + ", replacementType="
			+ replacementType + ", origin='" + origin + '\'' + '}';
	}
}
