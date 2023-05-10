package com.cinoteck.application.views.about;


import com.cinoteck.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
public class AboutView extends VerticalLayout {

    public AboutView() {
    	 Div aboutView = new Div();
		aboutView.getStyle().set("height", "100%");
		aboutView.getStyle().set("padding-left", "90px");
		aboutView.getStyle().set("padding-right", "90px");

		Div apmisImageContainer = new Div();
		apmisImageContainer.getStyle().set("height", "140px");
		apmisImageContainer.getStyle().set("display", "flex");
		apmisImageContainer.getStyle().set("justify-content", "center");
		apmisImageContainer.getStyle().set("margin-bottom", "30px");
		apmisImageContainer.getStyle().set("padding-top", "30px");

		Image img = new Image("images/apmislogo.png", "APMIS-LOGO");
		img.getStyle().set("max-height", "-webkit-fill-available");

		apmisImageContainer.add(img);

		Div aboutText = new Div();
		
		Paragraph text = new Paragraph(
				"The Afghanistan Polio Management Information System (APMIS) is an online data system that simplifies and improves the use and management of polio immunization-related data. APMIS facilitates field data entry, immunization data storage, data visualization, and real-time monitoring of polio immunization activities in Afghanistan.  Using this system will assist in evaluating immunization campaign activities and identifying program challenges.");
		text.getStyle().set("color", "green");
		text.getStyle().set("font-size", "20px");
		text.getStyle().set("margin-bottom", "30px");
		text.getStyle().set("text-align", "justify");
		aboutText.add(text);

//		HorizontalLayout guides = new HorizontalLayout();
//		Anchor techguide = new Anchor("https://staging.afghanistan-apmis.com/sormas-ui/VAADIN/themes/sormas/img/APMIS_Technical_Manual.pdf", "Technical Guide");
//		techguide.getStyle().set("text-decoration", "underline !important");
//		techguide.getStyle().set("color", "green !important");
//		Anchor userguide = new Anchor("https://staging.afghanistan-apmis.com/sormas-ui/VAADIN/themes/sormas/img/APMIS_User_Guide.pdf", "User Guide");
//		userguide.getStyle().set("color", "green !important");
//		userguide.getStyle().set("text-decoration", "underline !important");
//
//		guides.setJustifyContentMode(JustifyContentMode.CENTER);
//		// tabs.getStyle().set("background", "#434343");
//
//		guides.add(userguide, techguide);

		aboutView.add(apmisImageContainer, aboutText);
		add(aboutView);
		configureActionButtonVisibility();

//		add(downloadButton);
		
		

	}
	public void configureActionButtonVisibility() {
		Button displayActionButtons =  new Button("Show Action Buttons");
		displayActionButtons.setIcon(new Icon(VaadinIcon.SLIDERS));
		
		
		Button getUserGuide =  new Button("User Guide");
		getUserGuide.setIcon(new Icon(VaadinIcon.NURSE));
		getUserGuide.setVisible(false);
		
		Button getTechnicalGuide =  new Button("Technical Guide");
		getTechnicalGuide.setIcon(new Icon(VaadinIcon.DIPLOMA_SCROLL));
		getTechnicalGuide.setVisible(false);
		
		
		Button getJsonGlossary =  new Button("Export Forms & Diagrams Glossary");
		getJsonGlossary.setIcon(new Icon(VaadinIcon.TABLE));
		getJsonGlossary.setVisible(false);
		
		displayActionButtons.addClickListener(e->{
			if(getUserGuide.isVisible() == false) {
				getUserGuide.setVisible(true);
				getTechnicalGuide.setVisible(true);
				getJsonGlossary.setVisible(true);
				displayActionButtons.setText("Hide Action Buttons");
			}else {
			getUserGuide.setVisible(false);
			getTechnicalGuide.setVisible(false);
			getJsonGlossary.setVisible(false);
			displayActionButtons.setText("Show Action Buttons");
			}
		});
		
		getUserGuide.addClickListener(e->{
			  UI.getCurrent().getPage().open("https://staging.afghanistan-apmis.com/sormas-ui/VAADIN/themes/sormas/img/APMIS_User_Guide.pdf");
		});
		
		getTechnicalGuide.addClickListener(e->{
			  UI.getCurrent().getPage().open("https://staging.afghanistan-apmis.com/sormas-ui/VAADIN/themes/sormas/img/APMIS_Technical_Manual.pdf");
		});
		
		HorizontalLayout buttonsLayout  = new HorizontalLayout();
		buttonsLayout.getStyle().set("padding-left", "90px");
		buttonsLayout.add(displayActionButtons, getUserGuide, getTechnicalGuide, getJsonGlossary);
		add(buttonsLayout);
		
		
	}
}
