package de.symeda.sormas.backend.patch.customizablefield;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;

public class CustomizablePatchField {

	@NotNull
	private CustomizableFieldContext context;
	@NotNull
	private String leafFieldName;
	@Nullable
	private Integer groupIndex;

	public CustomizableFieldContext getContext() {
		return context;
	}

	public CustomizablePatchField setContext(CustomizableFieldContext context) {
		this.context = context;
		return this;
	}

	public String getLeafFieldName() {
		return leafFieldName;
	}

	public CustomizablePatchField setLeafFieldName(String leafFieldName) {
		this.leafFieldName = leafFieldName;
		return this;
	}

	public Integer getGroupIndex() {
		return groupIndex;
	}

	public CustomizablePatchField setGroupIndex(Integer groupIndex) {
		this.groupIndex = groupIndex;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		CustomizablePatchField that = (CustomizablePatchField) o;
		return context == that.context && Objects.equals(leafFieldName, that.leafFieldName) && Objects.equals(groupIndex, that.groupIndex);
	}

	@Override
	public int hashCode() {
		return Objects.hash(context, leafFieldName, groupIndex);
	}
}
