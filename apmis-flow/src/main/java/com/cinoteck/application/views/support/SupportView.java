package com.cinoteck.application.views.support;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.cinoteck.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Support")
@Route(value = "support", layout = MainLayout.class)
public class SupportView extends VerticalLayout {

	public SupportView() {
		 Div aboutView = new Div();
	       
	        aboutView.setId("aboutView");
	        aboutView.getStyle().set("height", "100%");
	        aboutView.getStyle().set("padding-left", "90px");
	        aboutView.getStyle().set("padding-right", "90px");

	        Div apmisImageContainer = new Div();
	        aboutView.setId("apmisImageContainer");
	        apmisImageContainer.getStyle().set("height", "140px");
	        apmisImageContainer.getStyle().set("display", "flex");
	        apmisImageContainer.getStyle().set("justify-content", "center");
	       
	        Image img = new Image("images/apmislogo.png", "APMIS-LOGO");
	        img.getStyle().set("max-height", "-webkit-fill-available");

	        apmisImageContainer.add(img);

	        Div aboutText = new Div();
//			aboutText.getStyle().set("height", "121px");
	        Paragraph text = new Paragraph("The Afghanistan Polio Management Information System (APMIS) is an online data system that simplifies and improves the use and management of polio immunization-related data. APMIS facilitates field data entry, immunization data storage, data visualization, and real-time monitoring of polio immunization activities in Afghanistan.  Using this system will assist in evaluating immunization campaign activities and identifying program challenges.");
	        text.getStyle().set("color", "green");
	        text.getStyle().set("font-size", "20px");
	        text.getStyle().set("text-align", "justify");
	        text.getStyle().set("padding-top", "30px");

	        aboutText.add(text);


			Div feedbackFormFields = new Div();
	      //  Binder<Feedback> binder = new Binder<>(Feedback.class);

	        TextField firstName = new TextField("First name");
	        firstName.getStyle().set("color", "green");
	       
	     //   binder.forField(firstName).bind(Feedback::getFirstName, Feedback::setFirstName);

	        TextField lastName = new TextField("Last name");
	        lastName.getStyle().set("color", "green");
	       
	     //   binder.forField(lastName).bind(Feedback::getLastName, Feedback::setLastName);

	        TextField email = new TextField("Email");
	        email.getStyle().set("color", "green");
	     //   binder.forField(email).bind(Feedback::getEmailAddress, Feedback::setEmailAddress);

	        TextArea message = new TextArea();
	        message.getStyle().set("color", "green");
	        message.getStyle().set("height", "370px");
	     //   binder.forField(message).bind(Feedback::getFeedback, Feedback::setFeedback);

	        Button sendFeedback = new Button("Send", new Icon("vaadin", "location-arrow-circle-o"));
	        sendFeedback.getStyle().set("color", "white");
	        sendFeedback.getStyle().set("background", "#0D6938");
	        sendFeedback.getStyle().set("width", "10%");
	        sendFeedback.getStyle().set("border-radius", "8px");

	        sendFeedback.addClickListener(click -> System.out.println("kkcvakvckavsckvackvakscvkasvckasvckasvckhavsckhavsckhv"));
	     //   sendFeedback.setSuffixComponent(new Icon("vaadin", "building"));


	        message.setWidthFull();
	        message.setLabel("Feedback");

	        FormLayout feedbackForm = new FormLayout();
	        feedbackForm.add(firstName, lastName, email, message);
	        feedbackForm.setResponsiveSteps(
	                // Use one column by default
	                new ResponsiveStep("0", 1),
	                // Use two columns, if layout's width exceeds 500px
	                new ResponsiveStep("500px", 2));
	        // Stretch the username field over 2 columns
	        feedbackForm.setColspan(email, 2);
	        feedbackForm.setColspan(message, 2);
	        feedbackForm.setColspan(sendFeedback, 0);

			feedbackFormFields.add(feedbackForm);

	        Paragraph versionNum = new Paragraph("Version Number : APMIS 5.0.0");
	        versionNum.getStyle().set("font-size", "12px");

			aboutView.add(aboutText, feedbackForm, sendFeedback, versionNum);
	        add(aboutView);
	}

}
