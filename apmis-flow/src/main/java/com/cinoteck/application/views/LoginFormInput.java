package com.cinoteck.application.views;

import com.nimbusds.jose.shaded.ow2asm.Label;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.NavigationEvent;

public class LoginFormInput extends VerticalLayout {

	private TextField username;
	private PasswordField password;
	private Button loginButton;
	private Icon signInIcon;
	private Anchor resetPassword;

	public LoginFormInput() {
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		// Create the UI components
		username = new TextField();
		username.setPlaceholder("Username");
		username.setClassName("loginUsername");
		username.setLabel("Username");
		username.getStyle().set("width", "100%");
		username.getStyle().set("font-weight", "200");
		username.getStyle().set("margin-bottom", "1.5em");

		password = new PasswordField();
		password.setRevealButtonVisible(false);
		password.setClassName("loginUsername");
		password.setLabel("Password");
		password.getStyle().set("width", "100%");
		password.setPlaceholder("Password");
		password.getStyle().set("font-weight", "200");
		password.getStyle().set("margin-bottom", "1.5em");

		signInIcon = new Icon(VaadinIcon.SIGN_OUT_ALT);
		signInIcon.getStyle().set("color", "#0D6938 !important");
		signInIcon.setId("signInIcon");

		loginButton = new Button("Sign In", signInIcon);
		loginButton.getStyle().set("width", "100%");
		loginButton.getStyle().set("margin-top", "20px");
		loginButton.setClassName("loginButton");
		loginButton.getStyle().set("background", "white");
		loginButton.getStyle().set("color", "green");
		loginButton.getStyle().set("margin-bottom", "1em");
		
//		Icon loginIcon  = new Icon(VaadinIcon.SIGN_IN);
//		loginIcon.getStyle().set("color", "#0D6938 !important");
//		loginButton.setIcon(loginIcon);

		// Setting the login button action
		loginButton.addClickListener(event -> {
			String usernameValue = username.getValue();
			String passwordValue = password.getValue();
			if (isValidCredentials(usernameValue, passwordValue)) {
				Notification.show("Login successful!");
				loginButton.getUI().ifPresent(ui -> ui.navigate("dashboard"));

			} else {
				Notification notification = new Notification();
				notification.setPosition(Position.MIDDLE);
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
				Div text = new Div(new Text("Login Failed : Please Check your Username and Password and try again."));

				Button closeButton = new Button(new Icon("lumo", "cross"));
				closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
				closeButton.getElement().setAttribute("aria-label", "Close");
				closeButton.addClickListener(e -> {
					notification.close();
				});

				HorizontalLayout layout = new HorizontalLayout(text, closeButton);
				layout.setAlignItems(Alignment.CENTER);

				notification.add(layout);
				notification.open();
			}
		});
		
		resetPassword = new Anchor("#", "Reset Password");
		resetPassword.getStyle().set("color", "white");
		
		
		VerticalLayout fieldsLayout = new VerticalLayout(username, password, loginButton, resetPassword);
		fieldsLayout.setAlignItems(Alignment.CENTER);
		add(fieldsLayout);

	}

	private boolean isValidCredentials(String username, String password) {
		// Add your authentication logic here
		return username.equals("admin@example.com") && password.equals("admin");
	}

}
