package de.symeda.sormas.ui.user;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Component;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

public class AbstractUserView extends AbstractSubNavigationView<Component> {

	public static final String ROOT_VIEW_NAME = "user";

	protected AbstractUserView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		menu.removeAllViews();

		menu.addView(UsersView.VIEW_NAME, I18nProperties.getPrefixCaption("View", UsersView.VIEW_NAME.replaceAll("/", "."), ""), params);
		menu.addView(UserRolesView.VIEW_NAME, I18nProperties.getPrefixCaption("View", UserRolesView.VIEW_NAME.replaceAll("/", "."), ""), params);
	}

	public static void registerViews(Navigator navigator) {
		navigator.addView(UsersView.VIEW_NAME, UsersView.class);
		navigator.addView(UserRolesView.VIEW_NAME, UserRolesView.class);
		navigator.addView(UserRoleView.VIEW_NAME, UserRoleView.class);
		navigator.addView(UserRoleNotificationsView.VIEW_NAME, UserRoleNotificationsView.class);
	}
}
