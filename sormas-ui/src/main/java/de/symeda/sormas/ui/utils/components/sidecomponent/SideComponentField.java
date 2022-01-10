package de.symeda.sormas.ui.utils.components.sidecomponent;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class SideComponentField extends HorizontalLayout {

	private final VerticalLayout mainLayout;

	public SideComponentField() {
		setMargin(false);
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);

		mainLayout = new VerticalLayout();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setMargin(false);
		mainLayout.setSpacing(false);
		addComponent(mainLayout);
		setExpandRatio(mainLayout, 1);
	}

	public void addComponentToField(Component component) {
		mainLayout.addComponent(component);
	}

	public void addEditButton(String id, Button.ClickListener editClickListener) {
		Button editButton = ButtonHelper
			.createIconButtonWithCaption(id, null, VaadinIcons.PENCIL, editClickListener, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);

		addComponent(editButton);
		setComponentAlignment(editButton, Alignment.TOP_RIGHT);
		setExpandRatio(editButton, 0);
	}

	public void setEnabled(boolean enabled) {
		mainLayout.setEnabled(enabled);
	}
}
