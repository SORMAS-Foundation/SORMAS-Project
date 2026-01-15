/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.surveillance.SurveillanceDashboardView;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.user.UserSettingsForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Responsive navigation menu presenting a list of available views to the user.
 */
public class Menu extends CssLayout {

	private static final String VALO_MENUITEMS = "valo-menuitems";
	private static final String VALO_MENU_TOGGLE = "valo-menu-toggle";
	private static final String VALO_MENU_VISIBLE = "valo-menu-visible";
	private static final String BACKGROUND_COLOR_CONFIG_KEY = "MENU_BACKGROUND_COLOR";
	private static final String DEFAULT_BACKGROUND_COLOR_NAME = "default";
	private static final String COLOR_BACKGROUND_STYLE_NAME = "color-background";

	private final Navigator navigator;
	private final Map<String, Button> viewButtons = new HashMap<>();

	private final CssLayout menuItemsLayout;
	private final CssLayout menuPart;

	public Menu(Navigator navigator) {

		this.navigator = navigator;
		setPrimaryStyleName(ValoTheme.MENU_ROOT);

		defineCustomColorBackgroundIfSystemConfigured();

		menuPart = new CssLayout();
		menuPart.addStyleName(ValoTheme.MENU_PART);

		// header of the menu
		VerticalLayout topContainer = new VerticalLayout();
		topContainer.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		topContainer.addStyleName(ValoTheme.MENU_TITLE);
		HorizontalLayout top = new HorizontalLayout();
		top.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		top.setSpacing(true);

		Label title = new Label(FacadeProvider.getConfigFacade().getSormasInstanceName());
		title.setSizeUndefined();

		Image image;
		if (FacadeProvider.getConfigFacade().isCustomBranding()
			&& StringUtils.isNotBlank(FacadeProvider.getConfigFacade().getCustomBrandingLogoPath())) {
			Path logoPath = Paths.get(FacadeProvider.getConfigFacade().getCustomBrandingLogoPath());
			image = new Image(null, new FileResource(logoPath.toFile()));
		} else {
			image = new Image(null, new ThemeResource("img/sormas-logo.png"));
		}
		CssStyles.style(image, ValoTheme.MENU_LOGO, ValoTheme.BUTTON_LINK);
		top.addComponent(image);
		top.addComponent(title);
		topContainer.addLayoutClickListener(listener -> SormasUI.get().getNavigator().navigateTo(SurveillanceDashboardView.VIEW_NAME));
		topContainer.addComponent(top);
		// TODO: new code to format

		final HorizontalLayout environmentTitleContainer = new HorizontalLayout();
		environmentTitleContainer.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		environmentTitleContainer.setSpacing(false);
		Label systemConfigurationLabel = new Label("QA");
		systemConfigurationLabel.setSizeUndefined();
		CssStyles.style(systemConfigurationLabel, CssStyles.LABEL_UNDERLINE);
		environmentTitleContainer.addComponent(systemConfigurationLabel);
		environmentTitleContainer.setStyleName("environment-name");
		topContainer.addComponent(environmentTitleContainer);

		menuPart.addComponent(topContainer);

		// button for toggling the visibility of the menu when on a small screen
		final Button showMenu = ButtonHelper.createIconButton(Captions.menu, VaadinIcons.MENU, event -> {
			if (menuPart.getStyleName().contains(VALO_MENU_VISIBLE)) {
				menuPart.removeStyleName(VALO_MENU_VISIBLE);
			} else {
				menuPart.addStyleName(VALO_MENU_VISIBLE);
			}
		}, ValoTheme.BUTTON_PRIMARY, VALO_MENU_TOGGLE);

		menuPart.addComponent(showMenu);

		// container for the navigation buttons, which are added by addView()
		menuItemsLayout = new CssLayout();
		menuItemsLayout.setPrimaryStyleName(VALO_MENUITEMS);
		menuPart.addComponent(menuItemsLayout);

		// settings menu item
		MenuBar settingsMenu = new MenuBar();
		settingsMenu.setId(Captions.actionSettings);
		settingsMenu.addItem(I18nProperties.getCaption(Captions.actionSettings), VaadinIcons.COG, (Command) selectedItem -> showSettingsPopup());

		settingsMenu.addStyleNames("user-menu", "settings-menu");
		menuPart.addComponent(settingsMenu);

		// logout menu item
		MenuBar logoutMenu = new MenuBar();
		logoutMenu.setId(Captions.actionLogout);
		logoutMenu.addItem(
			I18nProperties.getCaption(Captions.actionLogout) + " (" + UiUtil.getUserName() + ")",
			VaadinIcons.SIGN_OUT,
			(Command) selectedItem -> LoginHelper.logout());

		logoutMenu.addStyleNames("user-menu", "logout-menu");
		menuPart.addComponent(logoutMenu);

		addComponent(menuPart);
	}

	private void defineCustomColorBackgroundIfSystemConfigured() {
		String backgroundColor = FacadeProvider.getSystemConfigurationValueFacade().getValue(BACKGROUND_COLOR_CONFIG_KEY);

		if (StringUtils.isBlank(backgroundColor) || DEFAULT_BACKGROUND_COLOR_NAME.equals(backgroundColor)) {
			// no need to configure anything as we can keep the default color.
			return;
		}

		addStyleName(COLOR_BACKGROUND_STYLE_NAME);

		String actualColorBackgroundColor = determineActualColor(backgroundColor);

		Page.Styles styles = Page.getCurrent().getStyles();
		// specifying !important as inline CSS has lower precedence
		styles.add(
				".valo-menu-color-background {\n" + "  background-color: " + actualColorBackgroundColor + " !important;\n"
						+ "  background-image: -webkit-linear-gradient(right, " + actualColorBackgroundColor + " 0%, #EEFA5F" + actualColorBackgroundColor
						+ " 8px) !important;\n" + "  background-image: linear-gradient(to left, " + actualColorBackgroundColor + " 0%, "
						+ actualColorBackgroundColor + " 8px) !important;" + "  }");
	}

	private static @NotNull String determineActualColor(String backgroundColor) {
		String actualColorBackgroundColor;
		switch (backgroundColor) {
		case "green":
			actualColorBackgroundColor = "#108548";
			break;
		case "red":
			actualColorBackgroundColor = "#dd2b0e";
			break;
		case "indigo":
			actualColorBackgroundColor = "#7b58cf";
			break;
		case "gray":
			actualColorBackgroundColor = "#737278";
			break;
		default:
			actualColorBackgroundColor = backgroundColor;
		}
		return actualColorBackgroundColor;
	}

	private void showSettingsPopup() {

		Window window = VaadinUiUtil.createPopupWindow();
		window.setCaption(I18nProperties.getString(Strings.headingUserSettings));
		window.setModal(true);

		CommitDiscardWrapperComponent<UserSettingsForm> component = ControllerProvider.getUserController().getUserSettingsComponent(window::close);

		window.setContent(component);
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Register a pre-created view instance in the navigation menu and in the
	 * {@link Navigator}.
	 *
	 * @see Navigator#addView(String, View)
	 *
	 * @param view
	 *            view instance to register
	 * @param name
	 *            view name
	 * @param caption
	 *            view caption in the menu
	 * @param icon
	 *            view icon in the menu
	 */
	public void addView(View view, final String name, String caption, Resource icon) {

		navigator.addView(name, view);
		createViewButton(name, caption, icon);
	}

	/**
	 * Register a view in the navigation menu and in the {@link Navigator} based
	 * on a view class.
	 *
	 * @see Navigator#addView(String, Class)
	 *
	 * @param viewClass
	 *            class of the views to create
	 * @param name
	 *            view name
	 * @param caption
	 *            view caption in the menu
	 * @param icon
	 *            view icon in the menu
	 */
	public void addView(Class<? extends View> viewClass, final String name, String caption, Resource icon) {

		navigator.addView(name, viewClass);
		createViewButton(name, caption, icon);
	}

	private void createViewButton(final String name, String caption, Resource icon) {

		Button button = ButtonHelper.createIconButtonWithCaption(name, caption, icon, event -> navigator.navigateTo(name));
		button.setPrimaryStyleName(ValoTheme.MENU_ITEM);

		menuItemsLayout.addComponent(button);
		viewButtons.put(name, button);
	}

	/**
	 * Highlights a view navigation button as the currently active view in the
	 * menu. This method does not perform the actual navigation.
	 *
	 * @param viewName
	 *            the name of the view to show as active
	 */
	public void setActiveView(String viewName) {

		for (Button button : viewButtons.values()) {
			button.removeStyleName("selected");
		}

		Button selected = viewButtons.get(viewName);
		if (selected == null && viewName.contains("/")) {
			// might be a sub-view
			viewName = viewName.substring(0, viewName.indexOf('/'));
			selected = viewButtons.get(viewName);
		}

		if (selected != null) {
			selected.addStyleName("selected");
		}

		menuPart.removeStyleName(VALO_MENU_VISIBLE);
	}
}
