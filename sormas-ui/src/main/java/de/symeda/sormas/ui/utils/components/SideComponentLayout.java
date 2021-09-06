package de.symeda.sormas.ui.utils.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

public class SideComponentLayout extends VerticalLayout {

	public SideComponentLayout(Component sideComponent) {
		setMargin(false);
		setSpacing(false);
		sideComponent.addStyleName(CssStyles.SIDE_COMPONENT);
		addComponent(sideComponent);
	}
}
