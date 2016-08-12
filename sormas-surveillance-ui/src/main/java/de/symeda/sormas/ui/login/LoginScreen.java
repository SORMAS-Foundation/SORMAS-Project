package de.symeda.sormas.ui.login;

import java.io.Serializable;

import com.ejt.vaadin.loginform.DefaultVerticalLoginForm;
import com.ejt.vaadin.loginform.LoginForm;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

/**
 * UI content when the user is not logged in yet.
 */
@SuppressWarnings("serial")
public class LoginScreen extends CssLayout {

//    private TextField username;
//    private PasswordField password;
//    private Button login;
//    private Button forgotPassword;
    private LoginListener loginListener;

    public LoginScreen(LoginListener loginListener) {
        this.loginListener = loginListener;
        buildUI();
        //username.focus();
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
    	LoginForm loginForm = new DefaultVerticalLoginForm();
    	
    	loginForm.addLoginListener(event -> {
    		login(event.getUserName(), event.getPassword());
    	});

        loginForm.addStyleName("login-form");
        loginForm.setSizeUndefined();
        //loginForm.setMargin(false);

//        loginForm.addComponent(username = new TextField("Username"));
//        username.setWidth(16, Unit.EM);
//        loginForm.addComponent(password = new PasswordField("Password"));
//        password.setWidth(16, Unit.EM);
//        password.setDescription("Write anything");
//        CssLayout buttons = new CssLayout();
//        buttons.setStyleName("buttons");
//        loginForm.addComponent(buttons);

//        buttons.addComponent(login = new Button("Login"));
//        login.setDisableOnClick(true);
//        login.addClickListener(new Button.ClickListener() {
//            @Override
//            public void buttonClick(Button.ClickEvent event) {
//                try {
//                    login();
//                } finally {
//                    login.setEnabled(true);
//                }
//            }
//        });
//        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
//        login.addStyleName(ValoTheme.BUTTON_FRIENDLY);
//
//        buttons.addComponent(forgotPassword = new Button("Forgot password?"));
//        forgotPassword.addClickListener(new Button.ClickListener() {
//            @Override
//            public void buttonClick(Button.ClickEvent event) {
//                showNotification(new Notification("Hint: Try anything"));
//            }
//        });
//        forgotPassword.addStyleName(ValoTheme.BUTTON_LINK);
        return loginForm;
    }

    private CssLayout buildLoginInformation() {
        CssLayout loginInformation = new CssLayout();
        loginInformation.setStyleName("login-information");
        Label loginInfoText = new Label(
                "<h1>Login Information</h1>"
                        + "Log in as &quot;SunkSesa&quot; and password &quot;Sunkanmi&quot;",
                ContentMode.HTML);
        loginInformation.addComponent(loginInfoText);
        return loginInformation;
    }

    private void login(String username, String password) {
        if (LoginHelper.login(username, password)) {
            loginListener.loginSuccessful();
        } else {
            showNotification(new Notification("Login failed",
                    "Please check your username and password and try again.",
                    Notification.Type.HUMANIZED_MESSAGE));
            //username.focus();
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
