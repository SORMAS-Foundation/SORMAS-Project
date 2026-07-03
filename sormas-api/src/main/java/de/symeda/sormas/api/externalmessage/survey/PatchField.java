package de.symeda.sormas.api.externalmessage.survey;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * To be able to repeat some groups and make them belong together, the groupIndex was added.
 */
public class PatchField implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	private String field;

	/**
	 * 0-based index of belonging within the repeated occurrence.
	 */
	@Nullable
	private Integer groupIndex;

	public PatchField() {
	}

	@JsonCreator
	public PatchField(String field, @Nullable Integer groupIndex) {
		this.field = field;
		this.groupIndex = groupIndex;
	}

	public static PatchField of(String field, Integer groupIndex) {
		return new PatchField().setField(field).setGroupIndex(groupIndex);
	}

	public static PatchField of(String field) {
		return new PatchField().setField(field);
	}

	public String getField() {
		return field;
	}

	public PatchField setField(String field) {
		this.field = field;
		return this;
	}

	@Nullable
	public Integer getGroupIndex() {
		return groupIndex;
	}

	public PatchField setGroupIndex(@Nullable Integer groupIndex) {
		this.groupIndex = groupIndex;
		return this;
	}

	@JsonIgnore
	public Optional<Integer> getGroupIndexAsOptional() {
		return Optional.ofNullable(groupIndex);
	}

	@JsonValue
	public String toJsonValue() {
		return groupIndex == null ? field : field + "@" + groupIndex;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		PatchField that = (PatchField) o;
		return Objects.equals(field, that.field) && Objects.equals(groupIndex, that.groupIndex);
	}

	@Override
	public int hashCode() {
		return Objects.hash(field, groupIndex);
	}

	@Override
	public String toString() {
		return "PatchField{" + "field='" + field + '\'' + ", groupIndex=" + groupIndex + '}';
	}
}
