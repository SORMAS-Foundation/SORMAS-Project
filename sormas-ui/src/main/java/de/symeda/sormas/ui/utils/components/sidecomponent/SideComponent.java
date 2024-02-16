package de.symeda.sormas.ui.utils.components.sidecomponent;

import java.util.Arrays;
import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
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

public class SideComponent extends VerticalLayout {

	private final HorizontalLayout componentHeader;
	private final Consumer<Runnable> actionCallback;

	protected Button createButton;

	public SideComponent(String heading) {
		this(heading, Runnable::run);
	}

	public SideComponent(String heading, Consumer<Runnable> actionCallback) {
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
		componentHeader.setExpandRatio(headingLabel, 1);

		this.actionCallback = actionCallback;
	}

	protected void addCreateButton(Button button) {
		componentHeader.addComponent(button);
		componentHeader.setComponentAlignment(button, Alignment.MIDDLE_RIGHT);
		componentHeader.setExpandRatio(button, 0);
	}

	protected void addCreateButton(String caption, Runnable callback, UserRight... userRights) {
		if (userRights.length == 0 || userHasRight(userRights)) {
			createButton = ButtonHelper.createButton(caption);
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(e -> actionCallback.accept(callback));
			addCreateButton(createButton);
		}
	}

	private boolean userHasRight(UserRight... userRights) {
		UserProvider currentUser = UserProvider.getCurrent();
		return Arrays.stream(userRights).anyMatch(currentUser::hasUserRight);
	}
}
