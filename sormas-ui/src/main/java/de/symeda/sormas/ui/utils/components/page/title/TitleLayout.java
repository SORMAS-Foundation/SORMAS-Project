package de.symeda.sormas.ui.utils.components.page.title;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

public class TitleLayout extends VerticalLayout {

	public TitleLayout() {
		addStyleNames(CssStyles.LAYOUT_MINIMAL, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_4);
		setSpacing(false);
	}
}
