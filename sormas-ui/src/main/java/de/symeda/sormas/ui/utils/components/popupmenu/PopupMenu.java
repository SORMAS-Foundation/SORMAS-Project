package de.symeda.sormas.ui.utils.components.popupmenu;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

public class PopupMenu extends PopupButton {

	private final VerticalLayout menuLayout;

	public PopupMenu(String caption) {
		super(caption);
		setId("more");
		setIcon(VaadinIcons.ELLIPSIS_DOTS_V);
		menuLayout = new VerticalLayout();
		menuLayout.setSpacing(true);
		menuLayout.setMargin(true);
		menuLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
		menuLayout.setWidth(250, Unit.PIXELS);
		setContent(menuLayout);
	}

	public void addMenuEntry(Component menuEntry) {
		menuLayout.addComponent(menuEntry);
	}

	public boolean hasMenuEntries() {
		return menuLayout.getComponentCount() > 0;
	}
}
