package de.symeda.sormas.ui.immunization.components.layout;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

public class MainHeaderLayout extends VerticalLayout {

	public MainHeaderLayout(String text) {
		addStyleNames(CssStyles.LAYOUT_MINIMAL, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_4);
		setSpacing(false);

		Label immunizationLabel = new Label(text);
		immunizationLabel.addStyleNames(CssStyles.H2, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_PRIMARY);
		addComponent(immunizationLabel);
	}
}
