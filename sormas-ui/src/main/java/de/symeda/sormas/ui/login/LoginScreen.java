package de.symeda.sormas.ui.login;

import java.io.Serializable;

import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

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
		Component loginForm = buildLoginForm();

		// layout to center login form when there is sufficient screen space
		// - see the theme for how this is made responsive for various screen
		// sizes
		VerticalLayout centeringLayout = new VerticalLayout();
		centeringLayout.setStyleName("centering-layout");
		centeringLayout.addComponent(loginForm);
		centeringLayout.setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);

		// information text about logging in
		CssLayout loginInformation = buildLoginInformation();

		addComponent(centeringLayout);
		addComponent(loginInformation);
	}

	private Component buildLoginForm() {
		LoginForm loginForm = new LoginForm();

		loginForm.addLoginListener(event -> {
			login(event.getLoginParameter("username").trim(), event.getLoginParameter("password"));
		});

		loginForm.addStyleName("login-form");
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
		innerLayout.addComponent(loginInfoText);
		innerLayout.setExpandRatio(loginInfoText, 1);

		Label loginInfoCopyright = new Label("© 2018 SORMAS. All Rights Reserved.");
		innerLayout.addComponent(loginInfoCopyright);
		innerLayout.setExpandRatio(loginInfoCopyright, 0);
		innerLayout.setComponentAlignment(loginInfoCopyright, Alignment.BOTTOM_LEFT);

		loginInformation.addComponent(innerLayout);
		return loginInformation;
	}

	private void login(String username, String password) {
		try {
			if (LoginHelper.login(username, password)) {
				loginListener.loginSuccessful();
			} else {
				showNotification(new Notification("Login failed",
						"Please check your username and password and try again.", Notification.Type.WARNING_MESSAGE));
			}
		} catch (UserRightsException e) {
			showNotification(new Notification("Login failed", e.getMessage(), Notification.Type.WARNING_MESSAGE));
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
