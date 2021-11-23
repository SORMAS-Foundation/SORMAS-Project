package de.symeda.sormas.ui.immunization.components.fields.pickorcreate;

import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.ui.utils.CssStyles;

public class ImmunizationPickOrCreateOption extends OptionGroup {

	protected ImmunizationPickOrCreateOption(String itemId, String itemCaption) {
		addItem(itemId);
		setItemCaption(itemId, itemCaption);
		CssStyles.style(this, CssStyles.VSPACE_NONE);
	}
}
