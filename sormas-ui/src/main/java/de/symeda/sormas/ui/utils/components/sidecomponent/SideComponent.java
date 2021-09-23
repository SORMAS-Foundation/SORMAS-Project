package de.symeda.sormas.ui.utils.components.sidecomponent;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.utils.CssStyles;

public class SideComponent extends VerticalLayout {

	private final HorizontalLayout componentHeader;

	public SideComponent(String heading) {
		setWidth(100, Sizeable.Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Sizeable.Unit.PERCENTAGE);
		addComponent(componentHeader);

		Label headingLabel = new Label(heading);
		headingLabel.addStyleName(CssStyles.H3);
		componentHeader.addComponent(headingLabel);
	}

	protected void addCreateButton(Button button) {
		componentHeader.addComponent(button);
		componentHeader.setComponentAlignment(button, Alignment.MIDDLE_RIGHT);
	}
}
