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

	private static final long serialVersionUID = -7617896760817891457L;

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

	public void addViewButton(String id, Button.ClickListener viewClickListener) {
		addViewButton(id, viewClickListener, VaadinIcons.EYE);
	}

	public void addViewButton(String id, Button.ClickListener viewClickListener, VaadinIcons icon) {
		Button viewButton =
				ButtonHelper.createIconButtonWithCaption(id, null, icon, viewClickListener, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
		addComponent(viewButton);
		setComponentAlignment(viewButton, Alignment.TOP_RIGHT);
		setExpandRatio(viewButton, 0);
		viewButton.setEnabled(true);
	}

	@Override
	public void setEnabled(boolean enabled) {
		mainLayout.setEnabled(enabled);
	}

	public void addActionButton(String id, Button.ClickListener actionClickListener, boolean isEditEntry) {
		Button actionButton = ButtonHelper.createIconButtonWithCaption(
			isEditEntry ? "edit" + id : "view" + id,
			null,
			isEditEntry ? VaadinIcons.PENCIL : VaadinIcons.EYE,
			actionClickListener,
			ValoTheme.BUTTON_LINK,
			CssStyles.BUTTON_COMPACT);
		addComponent(actionButton);
		setComponentAlignment(actionButton, Alignment.TOP_RIGHT);
		setExpandRatio(actionButton, 0);
	}

	public void addDeleteButton(String id, Button.ClickListener actionClickListener) {
		Button actionButton = ButtonHelper.createIconButtonWithCaption(
			"delete" + id,
			null,
			VaadinIcons.TRASH,
			actionClickListener,
			ValoTheme.BUTTON_LINK,
			CssStyles.BUTTON_COMPACT);
		addComponent(actionButton);
		setComponentAlignment(actionButton, Alignment.TOP_RIGHT);
		setExpandRatio(actionButton, 0);
	}

	public void setActive() {
		mainLayout.addStyleName(CssStyles.ACTIVE_SIDE_COMPONENT_ELEMENT);
	}
}
