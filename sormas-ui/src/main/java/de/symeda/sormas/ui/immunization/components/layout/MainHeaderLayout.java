package de.symeda.sormas.ui.immunization.components.layout;

import com.vaadin.ui.Label;

import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;

public class MainHeaderLayout extends TitleLayout {

	public MainHeaderLayout(String text) {
		Label immunizationLabel = new Label(text);
		immunizationLabel.addStyleNames(CssStyles.H2, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_PRIMARY);
		addComponent(immunizationLabel);
	}
}
