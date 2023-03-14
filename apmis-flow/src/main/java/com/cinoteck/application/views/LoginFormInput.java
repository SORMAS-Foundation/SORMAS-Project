package com.cinoteck.application.views;

import com.nimbusds.jose.shaded.ow2asm.Label;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.NavigationEvent;

public class LoginFormInput extends VerticalLayout {

	private TextField username;
	private PasswordField password;
	private Button loginButton;

	public LoginFormInput() {
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		// Create the UI components
		username = new TextField();
		username.setPlaceholder("Username");
		username.getStyle().set("width", "100%");
		username.getStyle().set("font-weight", "200");

		password = new PasswordField();
		password.setRevealButtonVisible(false);
		password.getStyle().set("width", "100%");
		password.setPlaceholder("Password");
		password.getStyle().set("font-weight", "200");

		loginButton = new Button("Sign In", new Icon(VaadinIcon.ARROW_RIGHT));
		loginButton.getStyle().set("width", "100%");
		loginButton.getStyle().set("background", "white");
		loginButton.getStyle().set("color", "green");

		// Setting the login button action
		loginButton.addClickListener(event -> {
			String usernameValue = username.getValue();
			String passwordValue = password.getValue();
			if (isValidCredentials(usernameValue, passwordValue)) {
				Notification.show("Login successful!");
				loginButton.getUI().ifPresent(ui -> ui.navigate("dashboard"));

			} else {
				Notification.show("Invalid username or password");
			}
		});

		VerticalLayout fieldsLayout = new VerticalLayout(username, password, loginButton);
		fieldsLayout.setAlignItems(Alignment.CENTER);
		add(fieldsLayout);

	}

	private boolean isValidCredentials(String username, String password) {
		// Add your authentication logic here
		return username.equals("admin@example.com") && password.equals("admin");
	}

}
