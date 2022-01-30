package de.symeda.sormas.ui.utils.components.automaticdeletion;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

public class AutomaticDeletionLabel extends Label {

	public AutomaticDeletionLabel() {
		String infoText = String.format(I18nProperties.getString(Strings.infoAutomaticDeletion), "10/11/2031");
		setValue(VaadinIcons.INFO_CIRCLE.getHtml() + " " + infoText);
		setContentMode(ContentMode.HTML);
		addStyleNames(CssStyles.VSPACE_TOP_4);
	}
}
