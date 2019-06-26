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
package de.symeda.sormas.ui.login;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.UserRightsException;

/**
 * UI content when the user is not logged in yet.
 */
@SuppressWarnings("serial")
public class LoginScreen extends CssLayout {

	private LoginListener loginListener;

	public LoginScreen(LoginListener loginListener) {
		this.loginListener = loginListener;
		buildUI();
	}

	private void buildUI() {
		addStyleName("login-screen");

		// login form, centered in the available part of the screen
		VerticalLayout loginFormLayout = new VerticalLayout();
		loginFormLayout.addStyleName("login-form");
		loginFormLayout.setMargin(false);
		Component loginForm = buildLoginForm();
		loginFormLayout.addComponent(loginForm);
		loginFormLayout.addComponent(buildLoginDetailsLayout());

		// layout to center login form when there is sufficient screen space
		// - see the theme for how this is made responsive for various screen
		// sizes
		CssLayout loginLayout = new CssLayout();
		loginLayout.setStyleName("login-form-container");
		loginLayout.addComponent(loginFormLayout);
		
		// information text about logging in
		CssLayout loginInformation = buildLoginInformation();
		
		// custom html layout
		CssLayout loginSidebarLayout = buildLoginSidebarLayout();

		addComponent(loginInformation);
		addComponent(loginLayout);
		addComponent(loginSidebarLayout);
	}

	private Component buildLoginForm() {
		LoginForm loginForm = new LoginForm();

		loginForm.addLoginListener(event -> {
			login(event.getLoginParameter("username").trim(), event.getLoginParameter("password"));
		});

		loginForm.setSizeUndefined();

		return loginForm;
	}

	private CssLayout buildLoginInformation() {
		CssLayout loginInformation = new CssLayout();
		loginInformation.setStyleName("login-information");

		VerticalLayout innerLayout = new VerticalLayout();
		innerLayout.setSizeFull();

		Image img = new Image(null, new ThemeResource("img/sormas-logo-big.png"));
		img.setHeight(240, Unit.PIXELS);
		innerLayout.addComponent(img);
		innerLayout.setComponentAlignment(img, Alignment.TOP_CENTER);
		innerLayout.setExpandRatio(img, 0);

		Label loginInfoText = new Label("<h1>SORMAS</h1>"
				+ "<h2 style='color:white'>Surveillance, Outbreak Response Management and Analysis System</h2>"
				+ "<h3 style='color:white; text-transform:uppercase'>&#9679; Disease Prevention<br>&#9679; Disease Detection<br>&#9679; Outbreak Response</h3>",
				ContentMode.HTML);
		loginInfoText.setWidth(100, Unit.PERCENTAGE);
		innerLayout.addComponent(loginInfoText);
		innerLayout.setExpandRatio(loginInfoText, 1);

		Label loginInfoCopyright = new Label("© 2019 SORMAS. All Rights Reserved.");
		loginInfoCopyright.setWidth(100, Unit.PERCENTAGE);
		innerLayout.addComponent(loginInfoCopyright);
		innerLayout.setExpandRatio(loginInfoCopyright, 0);
		innerLayout.setComponentAlignment(loginInfoCopyright, Alignment.BOTTOM_LEFT);

		loginInformation.addComponent(innerLayout);
		return loginInformation;
	}
	
	private CssLayout buildLoginSidebarLayout() {
		CssLayout loginSidebarLayout = new CssLayout();
		loginSidebarLayout.setStyleName("login-sidebar");
		
		Label htmlLabel = new Label();
		htmlLabel.setContentMode(ContentMode.HTML);
		
		Path customHtmlDirectory = Paths.get(FacadeProvider.getConfigFacade().getCustomFilesPath());
		Path filePath = customHtmlDirectory.resolve("loginsidebar.html");
		
		try {
			byte[] encoded = Files.readAllBytes(filePath);
			htmlLabel.setValue(new String(encoded, "UTF-8"));
		} catch (IOException e) {
			htmlLabel.setValue("");
		}
		
		loginSidebarLayout.addComponent(htmlLabel);
		return loginSidebarLayout;
	}
	
	private CssLayout buildLoginDetailsLayout() {
		CssLayout loginDetailsLayout = new CssLayout();
		loginDetailsLayout.setStyleName("login-details");
		
		Label htmlLabel = new Label();
		htmlLabel.setContentMode(ContentMode.HTML);
		
		Path customHtmlDirectory = Paths.get(FacadeProvider.getConfigFacade().getCustomFilesPath());
		Path filePath = customHtmlDirectory.resolve("logindetails.html");

		try {
			byte[] encoded = Files.readAllBytes(filePath);
			htmlLabel.setValue(new String(encoded, "UTF-8"));
		} catch (IOException e) {
			htmlLabel.setValue("");
		}
		
		loginDetailsLayout.addComponent(htmlLabel);
		return loginDetailsLayout;
	}

	private void login(String username, String password) {
		try {
			if (LoginHelper.login(username, password)) {
				loginListener.loginSuccessful();
			} else {
				showNotification(new Notification(I18nProperties.getString(Strings.headingLoginFailed),
						I18nProperties.getString(Strings.messageLoginFailed), Notification.Type.WARNING_MESSAGE));
			}
		} catch (UserRightsException e) {
			showNotification(new Notification(I18nProperties.getString(Strings.headingLoginFailed), e.getMessage(), Notification.Type.WARNING_MESSAGE));
		}
	}

	private void showNotification(Notification notification) {
		// keep the notification visible a little while after moving the
		// mouse, or until clicked
		notification.setDelayMsec(2000);
		notification.show(Page.getCurrent());
	}

	public interface LoginListener extends Serializable {
		void loginSuccessful();
	}
}
