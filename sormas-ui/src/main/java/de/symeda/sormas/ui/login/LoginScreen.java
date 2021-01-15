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

import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.UserRightsException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * UI content when the user is not logged in yet.
 */
@SuppressWarnings("serial")
public class LoginScreen extends CssLayout {

	private static final String UTF_8 = "UTF-8";
	private static final int LOGO_WIDTH = 250;
	private static final int CUSTOM_BRANDING_LOGO_WIDTH = 150;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private LoginListener loginListener;
	private boolean isCustomBranding;

	public LoginScreen(LoginListener loginListener) {
		this.loginListener = loginListener;
		this.isCustomBranding = FacadeProvider.getConfigFacade().isCustomBranding();
		buildUI();
	}

	private void buildUI() {
		addStyleName(CssStyles.LOGINSCREEN_BACK);
		CssLayout layout = new CssLayout();
		layout.addStyleName(CssStyles.LOGINSCREEN);
		addComponent(layout);

		layout.addComponent(buildLoginLayout());

		// custom html layout
		Layout loginSidebarLayout = buildLoginSidebarLayout();
		layout.addComponent(loginSidebarLayout);
	}

	private Component buildLoginLayout() {

		Path customHtmlDirectory = Paths.get(FacadeProvider.getConfigFacade().getCustomFilesPath());
		Path filePath = customHtmlDirectory.resolve("loginmain.html");

		String html;
		try {
			byte[] encoded = Files.readAllBytes(filePath);
			html = new String(encoded, UTF_8);
		} catch (IOException e) {
			html = "";
		}

		Component loginLayout = buildLogin();

		if (html.contains("location=\"login\"")) {
			CustomLayout loginDetailsLayout = new CustomLayout();
			loginDetailsLayout.setStyleName(CssStyles.LOGINDEATILS);
			loginDetailsLayout.setWidth(100, Unit.PERCENTAGE);

			loginDetailsLayout.setTemplateContents(html);
			loginDetailsLayout.addComponent(loginLayout, "login");
			return loginDetailsLayout;
		} else {
			logger.warn("Location for login is missing in loginmain.html");
			return loginLayout;
		}
	}

	private Component buildLogin() {

		// login form, centered in the available part of the screen
		VerticalLayout loginFormLayout = new VerticalLayout();
		CssStyles.style(loginFormLayout, CssStyles.LOGINFORM, CssStyles.LAYOUT_SPACIOUS);
		loginFormLayout.setMargin(true);
		loginFormLayout.setSpacing(false);

		// header of the menu
		final HorizontalLayout titleLayout = new HorizontalLayout();
		titleLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		titleLayout.setSpacing(false);
		Label title = new Label(FacadeProvider.getConfigFacade().getSormasInstanceName());
		CssStyles.style(title, CssStyles.H1, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE, CssStyles.HSPACE_LEFT_3);

		Image image;
		if (isCustomBranding && StringUtils.isNotBlank(FacadeProvider.getConfigFacade().getCustomBrandingLogoPath())) {
			Path logoPath = Paths.get(FacadeProvider.getConfigFacade().getCustomBrandingLogoPath());
			image = new Image(null, new FileResource(logoPath.toFile()));
			image.setWidth(50, Unit.PIXELS);
		} else {
			image = new Image(null, new ThemeResource("img/sormas-logo.png"));
		}

		titleLayout.addComponent(image);
		titleLayout.addComponent(title);
		loginFormLayout.addComponent(titleLayout);

		Label header = new Label(I18nProperties.getCaption(Captions.Login_login));
		CssStyles.style(header, CssStyles.H2);
		loginFormLayout.addComponent(header);

		Component loginForm = buildLoginForm();
		loginFormLayout.addComponent(loginForm);
		loginFormLayout.addComponent(buildLoginDetails());

		// layout to center login form when there is sufficient screen space
		// - see the theme for how this is made responsive for various screen
		// sizes
		CssLayout loginLayout = new CssLayout();
		loginLayout.setStyleName(CssStyles.LOGINFORM_CONTAINER);
		loginLayout.addComponent(loginFormLayout);

		return loginLayout;
	}

	private Component buildLoginForm() {

		LoginForm loginForm = new LoginForm() {

			@Override
			protected TextField createUsernameField() {
				TextField usernameField = new TextField(I18nProperties.getCaption(Captions.Login_username));
				usernameField.focus();
				usernameField.setWidth(100, Unit.PERCENTAGE);
				return usernameField;
			}

			@Override
			protected PasswordField createPasswordField() {
				PasswordField passwordField = new PasswordField(I18nProperties.getCaption(Captions.Login_password));
				passwordField.setWidth(100, Unit.PERCENTAGE);
				return passwordField;
			}

			@Override
			protected Button createLoginButton() {
				Button loginButton = ButtonHelper.createButton(Captions.Login_doLogIn, null, CssStyles.FORCE_CAPTION, ValoTheme.BUTTON_PRIMARY);
				loginButton.setWidth(100, Unit.PERCENTAGE);

				return loginButton;
			}

			@Override
			protected Component createContent(TextField userNameField, PasswordField passwordField, Button loginButton) {
				VerticalLayout contentLayout = (VerticalLayout) super.createContent(userNameField, passwordField, loginButton);
				contentLayout.setMargin(false);
				contentLayout.setSpacing(false);
				return contentLayout;
			}
		};

		loginForm.addLoginListener(event -> {
			login(event.getLoginParameter("username").trim(), event.getLoginParameter("password"));
		});

		loginForm.setWidth(240, Unit.PIXELS);

		return loginForm;
	}

	private CssLayout buildLoginDetails() {

		CssLayout loginDetailsLayout = new CssLayout();
		loginDetailsLayout.setStyleName(CssStyles.LOGINDEATILS);
		loginDetailsLayout.setWidth(100, Unit.PERCENTAGE);

		Label htmlLabel = new Label();
		htmlLabel.setContentMode(ContentMode.HTML);
		htmlLabel.setWidth(100, Unit.PERCENTAGE);

		Path customHtmlDirectory = Paths.get(FacadeProvider.getConfigFacade().getCustomFilesPath());
		Path filePath = customHtmlDirectory.resolve("logindetails.html");

		try {
			byte[] encoded = Files.readAllBytes(filePath);
			htmlLabel.setValue(new String(encoded, UTF_8));
		} catch (IOException e) {
			htmlLabel.setValue("");
		}

		loginDetailsLayout.addComponent(htmlLabel);
		return loginDetailsLayout;
	}

	private Layout buildLoginSidebarLayout() {

		CssLayout loginSidebarLayout = new CssLayout();
		CssStyles.style(loginSidebarLayout, CssStyles.LOGINSIDEBAR);

		VerticalLayout sidebarHeaderLayout = new VerticalLayout();
		CssStyles.style(sidebarHeaderLayout, CssStyles.LAYOUT_SPACIOUS);
		sidebarHeaderLayout.setSizeUndefined();
		sidebarHeaderLayout.setSpacing(false);

		Path customHtmlDirectory = Paths.get(FacadeProvider.getConfigFacade().getCustomFilesPath());

		if (isCustomBranding) {
			Path sidebarHeaderPath = customHtmlDirectory.resolve("loginsidebar-header.html");
			Label customSidebarHeaderLabel = new Label();
			CssStyles.style(sidebarHeaderLayout, CssStyles.LOGINSIDEBAR_CUSTOM_HEADER);
			customSidebarHeaderLabel.setContentMode(ContentMode.HTML);
			try {
				byte[] encoded = Files.readAllBytes(sidebarHeaderPath);
				customSidebarHeaderLabel.setValue(new String(encoded, StandardCharsets.UTF_8));
			} catch (IOException e) {
				customSidebarHeaderLabel.setValue("");
			}

			sidebarHeaderLayout.addComponent(customSidebarHeaderLabel);
		} else {
			Image img = new Image(null, new ThemeResource("img/sormas-logo-big-text.png"));
			img.setWidth(320, Unit.PIXELS);
			sidebarHeaderLayout.addComponent(img);
			sidebarHeaderLayout.setComponentAlignment(img, Alignment.TOP_CENTER);

			Label fullNameText = new Label("Surveillance,<br>Outbreak Response Management<br>and Analysis System<br>", ContentMode.HTML);
			fullNameText.setWidth(320, Unit.PIXELS);
			CssStyles.style(fullNameText, CssStyles.H2, CssStyles.LABEL_PRIMARY, CssStyles.VSPACE_TOP_NONE, CssStyles.ALIGN_CENTER);
			sidebarHeaderLayout.addComponent(fullNameText);
			sidebarHeaderLayout.setComponentAlignment(fullNameText, Alignment.TOP_CENTER);

			Label missionText = new Label(
				"• " + I18nProperties.getCaption(Captions.LoginSidebar_diseasePrevention) + "<br>• "
					+ I18nProperties.getCaption(Captions.LoginSidebar_diseaseDetection) + "<br>• "
					+ I18nProperties.getCaption(Captions.LoginSidebar_outbreakResponse),
				ContentMode.HTML);
			missionText.setWidth(320, Unit.PIXELS);
			CssStyles.style(missionText, CssStyles.H2, CssStyles.VSPACE_TOP_NONE, CssStyles.ALIGN_CENTER);
			sidebarHeaderLayout.addComponent(missionText);
			sidebarHeaderLayout.setComponentAlignment(missionText, Alignment.TOP_CENTER);
		}

		loginSidebarLayout.addComponent(sidebarHeaderLayout);

		Label poweredByLabel = new Label(I18nProperties.getCaption(Captions.LoginSidebar_poweredBy));
		CssStyles.style(poweredByLabel, CssStyles.LOGIN_HEADLINELABEL, CssStyles.H2, CssStyles.VSPACE_NONE);
		loginSidebarLayout.addComponent(poweredByLabel);

		GridLayout poweredByLayout = new GridLayout(isCustomBranding ? 2 : 1, 1);
		CssStyles.style(poweredByLayout, CssStyles.LOGIN_LOGOCONTAINER);
		poweredByLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		poweredByLayout.setWidth(100, Unit.PERCENTAGE);
		poweredByLayout.setSpacing(true);

		if (isCustomBranding) {
			CssStyles.style(poweredByLayout, CssStyles.VSPACE_TOP_3);

			Image imgSormas = new Image(null, new ThemeResource("img/sormas-logo-horizontal.png"));
			imgSormas.setWidth(isCustomBranding ? CUSTOM_BRANDING_LOGO_WIDTH : LOGO_WIDTH, Unit.PIXELS);
			CssStyles.style(imgSormas, CssStyles.VSPACE_2);
			poweredByLayout.addComponent(imgSormas);
		} else {
			CssStyles.style(poweredByLayout, CssStyles.VSPACE_TOP_1);
		}

		Image imgHzi = new Image(null, new ThemeResource("img/hzi-logo.png"));
		imgHzi.setWidth(isCustomBranding ? CUSTOM_BRANDING_LOGO_WIDTH : LOGO_WIDTH, Unit.PIXELS);
		poweredByLayout.addComponent(imgHzi);

		Image imgSymeda = new Image(null, new ThemeResource("img/symeda-logo.png"));
		imgSymeda.setWidth(isCustomBranding ? CUSTOM_BRANDING_LOGO_WIDTH : LOGO_WIDTH, Unit.PIXELS);
		poweredByLayout.addComponent(imgSymeda);

		if (!isCustomBranding) {
			Image imgGiz = new Image(null, new ThemeResource("img/giz-logo.png"));
			imgGiz.setWidth(isCustomBranding ? CUSTOM_BRANDING_LOGO_WIDTH : LOGO_WIDTH, Unit.PIXELS);
			poweredByLayout.addComponent(imgGiz);
		}

		loginSidebarLayout.addComponent(poweredByLayout);

		Label customSidebarLabel = new Label();
		customSidebarLabel.setContentMode(ContentMode.HTML);

		Path sidebarPath = customHtmlDirectory.resolve("loginsidebar.html");

		try {
			byte[] encoded = Files.readAllBytes(sidebarPath);
			customSidebarLabel.setValue(new String(encoded, StandardCharsets.UTF_8));
		} catch (IOException e) {
			customSidebarLabel.setValue("");
		}

		loginSidebarLayout.addComponent(customSidebarLabel);
		return loginSidebarLayout;
	}

	private void login(String username, String password) {
		try {
			if (LoginHelper.login(username, password)) {
				loginListener.loginSuccessful();
			} else {
				showNotification(
					new Notification(
						I18nProperties.getString(Strings.headingLoginFailed),
						I18nProperties.getString(Strings.messageLoginFailed),
						Notification.Type.WARNING_MESSAGE));
			}
		} catch (UserRightsException e) {
			showNotification(
				new Notification(I18nProperties.getString(Strings.headingLoginFailed), e.getMessage(), Notification.Type.WARNING_MESSAGE));
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
