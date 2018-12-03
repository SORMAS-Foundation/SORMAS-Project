/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui;

import java.util.Properties;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.ui.CurrentUser.HasCurrentUser;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.login.LoginScreen;
import de.symeda.sormas.ui.login.LoginScreen.LoginListener;
import de.symeda.sormas.ui.utils.SormasDefaultConverterFactory;

/**
 * Main UI class of the application that shows either the login screen or the
 * main view of the application depending on whether a user is signed in.
 *
 * The @Viewport annotation configures the viewport meta tags appropriately on
 * mobile devices. Instead of device based scaling (default), using responsive
 * layouts.
 */
@SuppressWarnings("serial")
@Viewport("user-scalable=no,initial-scale=1.0")
@Theme("sormas")
@Widgetset("de.symeda.sormas.SormasWidgetset")
public class SormasUI extends UI implements HasCurrentUser {

	private final CurrentUser currentUser = new CurrentUser();

	@Override
	public void init(VaadinRequest vaadinRequest) {

		setErrorHandler(SormasErrorHandler.get());
		setLocale(vaadinRequest.getLocale());

		Responsive.makeResponsive(this);

		VaadinSession.getCurrent().setConverterFactory(new SormasDefaultConverterFactory());

		getPage().setTitle("SORMAS");

		// XXX
		// LoginHelper.login("SunkSesa", "Sunkanmi");

		if (!LoginHelper.isUserSignedIn()) {

			setContent(new LoginScreen(new LoginListener() {
				@Override
				public void loginSuccessful() {
					initMainScreen();
					// open view
					getNavigator().navigateTo(getNavigator().getState());
				}
			}));

		} else {
			initMainScreen();
		}
	}

	protected void initMainScreen() {
		addStyleName(ValoTheme.UI_WITH_MENU);
		setContent(new MainScreen(SormasUI.this));
	}

	public static SormasUI get() {
		return (SormasUI) UI.getCurrent();
	}

	@Override
	public CurrentUser getCurrentUser() {
		return currentUser;
	}

	@WebServlet(urlPatterns = "/*", name = "SormasUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = SormasUI.class, productionMode = false) // , resourceCacheTime = 0)
	public static class SormasUIServlet extends VaadinServlet {

		// private static final String VAADIN_RESOURCES = "/sormas-widgetset";

		@Override
		protected DeploymentConfiguration createDeploymentConfiguration(Properties initParameters) {

			// initParameters.setProperty(Constants.PARAMETER_VAADIN_RESOURCES,
			// VAADIN_RESOURCES);

			return super.createDeploymentConfiguration(initParameters);
		}
	}
}
