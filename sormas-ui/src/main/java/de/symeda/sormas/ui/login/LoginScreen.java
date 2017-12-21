package de.symeda.sormas.ui.login;

import java.io.Serializable;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
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
        centeringLayout.setComponentAlignment(loginForm,
                Alignment.MIDDLE_CENTER);

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
        loginInformation.addComponent(innerLayout);
                
        Label loginInfoText = new Label(
                "<h1>SORMAS</h1>"
                + "<p class=\"font-size-xlarge\">SORMAS stands for “Surveillance, Outbreak Response Management and Analysis System“</p>"
                + "<ul class=\"font-size-xlarge\">"
                + "<li>Disease Prevention</li>"
                + "<li>Disease Detection</li>"
                + "<li>Outbreak Response</li>"
                + "</ul>",
                ContentMode.HTML);
        innerLayout.addComponent(loginInfoText);
        
        Label loginInfoCopyright = new Label ("© 2017 SORMAS. All Rights Reserved.");
        innerLayout.addComponent(loginInfoCopyright);
        innerLayout.setComponentAlignment(loginInfoCopyright, Alignment.BOTTOM_LEFT);
        
        return loginInformation;
    }

    private void login(String username, String password) {
    	try {
	        if (LoginHelper.login(username, password)) {
	            loginListener.loginSuccessful();
	        } else {
	            showNotification(new Notification("Login failed",
	                    "Please check your username and password and try again.",
	                    Notification.Type.WARNING_MESSAGE));
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
