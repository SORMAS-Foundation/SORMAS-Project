package de.symeda.sormas.backend.patch.customizablefield;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.externalmessage.survey.PatchField;

/**
 * Defines a tuple between {@link CustomizableFieldContext} and the group index.
 * In short customizable fields for some elements, per example: {@link CustomizableFieldContext#EXPOSURE} requires an element to be created
 * during patching.
 */
public class CustomizableContextIndexKey {

	@NotNull
	private CustomizableFieldContext context;

	/**
	 * Linked to {@link PatchField#getGroupIndex()}
	 */
	@Nullable
	private Integer groupIndex;

	public CustomizableFieldContext getContext() {
		return context;
	}

	public CustomizableContextIndexKey setContext(CustomizableFieldContext context) {
		this.context = context;
		return this;
	}

	@Nullable
	public Integer getGroupIndex() {
		return groupIndex;
	}

	public CustomizableContextIndexKey setGroupIndex(@Nullable Integer groupIndex) {
		this.groupIndex = groupIndex;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		CustomizableContextIndexKey that = (CustomizableContextIndexKey) o;
		return context == that.context && Objects.equals(groupIndex, that.groupIndex);
	}

	@Override
	public int hashCode() {
		return Objects.hash(context, groupIndex);
	}

	@Override
	public String toString() {
		return "CustomizableContextIndexKey{" + "context=" + context + ", groupIndex=" + groupIndex + '}';
	}
}
