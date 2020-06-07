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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.login;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasErrorHandler;
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
public class LoginUI extends UI {

	@Override
	public void init(VaadinRequest vaadinRequest) {

		setErrorHandler(SormasErrorHandler.get());
		setLocale(vaadinRequest.getLocale());

		Responsive.makeResponsive(this);

		VaadinSession.getCurrent().setConverterFactory(new SormasDefaultConverterFactory());

		getPage().setTitle("SORMAS");

		setContent(new LoginScreen(new LoginListener() {

			@Override
			public void loginSuccessful() {
				UI.getCurrent()
					.getPage()
					.setLocation(
						VaadinServletService.getCurrentServletRequest().getContextPath() + "#"
							+ DataHelper.toStringNullable(UI.getCurrent().getPage().getUriFragment()));
			}
		}));
	}

	@WebServlet(urlPatterns = {
		"/login/*",
		"/VAADIN/*" }, name = "SormasLoginServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = LoginUI.class, productionMode = true)
	@ServletSecurity(@HttpConstraint)
	public static class SormasLoginServlet extends VaadinServlet {

	}
}
