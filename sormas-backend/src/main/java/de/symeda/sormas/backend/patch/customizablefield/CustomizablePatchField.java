package de.symeda.sormas.backend.patch.customizablefield;

import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;

public class CustomizablePatchField {

	private CustomizableFieldContext context;
	private String leafFieldName;

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
}
