package de.symeda.sormas.ui.utils.components.expandablebutton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.ui.utils.ButtonHelper;

public class ExpandableButton extends HorizontalLayout {

	private final Button expandableButton;

	public ExpandableButton(String caption) {
		expandableButton = ButtonHelper.createIconButton(caption, VaadinIcons.PLUS_CIRCLE, null, ValoTheme.BUTTON_PRIMARY);
		addComponent(expandableButton);
	}

	public ExpandableButton expand(Button.ClickListener listener) {
		expandableButton.addClickListener(listener);
		return this;
	}
}
