package de.symeda.sormas.ui.utils.components.sidecomponent;

import java.util.Arrays;
import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.event.SideComponentCreateEvent;
import de.symeda.sormas.ui.utils.components.sidecomponent.event.SideComponentCreateEventListener;
import de.symeda.sormas.ui.utils.components.sidecomponent.event.SideComponentEditEvent;
import de.symeda.sormas.ui.utils.components.sidecomponent.event.SideComponentEditEventListener;

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

	protected void addCreateButton(String caption, UserRight userRight, Consumer<Button.ClickEvent> clickListener) {
		UserProvider currentUser = UserProvider.getCurrent();
		if (currentUser != null && currentUser.hasUserRight(userRight) && clickListener != null) {
			Button createButton = ButtonHelper.createButton(caption);
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(clickListener::accept);
			addCreateButton(createButton);
		}
	}

	protected void addCreateButton(String caption, UserRight... userRights) {
		if (userHasRight(userRights)) {
			Button createButton = ButtonHelper.createButton(caption);
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(e -> fireEvent(new SideComponentCreateEvent(this)));
			addCreateButton(createButton);
		}
	}

	public Registration addSideComponentCreateEventListener(SideComponentCreateEventListener sideComponentCreateEventListener) {
		return addListener(
			SideComponentCreateEvent.class,
			sideComponentCreateEventListener,
			SideComponentCreateEventListener.ON_SIDE_COMPONENT_CREATE_METHOD);
	}

	public Registration addSideComponentEditEventListener(SideComponentEditEventListener sideComponentEditEventListener) {
		return addListener(
			SideComponentEditEvent.class,
			sideComponentEditEventListener,
			SideComponentEditEventListener.ON_SIDE_COMPONENT_EDIT_METHOD);
	}

	private boolean userHasRight(UserRight... userRights) {
		UserProvider currentUser = UserProvider.getCurrent();
		return Arrays.stream(userRights).anyMatch(currentUser::hasUserRight);
	}
}
