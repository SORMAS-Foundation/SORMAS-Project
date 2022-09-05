package de.symeda.sormas.ui.utils.components.sidecomponent;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

public class SideComponentLayout extends VerticalLayout {

	private SideComponent component;
	public SideComponentLayout(SideComponent sideComponent) {
		setMargin(false);
		setSpacing(false);
		sideComponent.addStyleName(CssStyles.SIDE_COMPONENT);
		addComponent(sideComponent);
		component = sideComponent;
	}

	public SideComponent getComponent() {
		return component;
	}
}
