package com.cinoteck.application.views.support;


import com.cinoteck.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Support")
@Route(value = "support", layout = MainLayout.class)
public class SupportView extends VerticalLayout {

    public SupportView() {
        Div aboutView = new Div();
        aboutView.getStyle().set("height", "100%");
        aboutView.getStyle().set("padding-left", "90px");
        aboutView.getStyle().set("padding-right", "90px");

        Div apmisImageContainer = new Div();
        apmisImageContainer.getStyle().set("height", "140px");
        apmisImageContainer.getStyle().set("display", "flex");
        apmisImageContainer.getStyle().set("justify-content", "center");

        Image img = new Image("images/apmislogo.png", "APMIS-LOGO");
        img.getStyle().set("max-height", "-webkit-fill-available");

        apmisImageContainer.add(img);

        Div aboutText = new Div();
		aboutText.getStyle().set("height", "121px");
        Paragraph text = new Paragraph("The Afghanistan Polio Management Information System (APMIS) is an online data system that simplifies and improves the use and management of polio immunization-related data. APMIS facilitates field data entry, immunization data storage, data visualization, and real-time monitoring of polio immunization activities in Afghanistan.  Using this system will assist in evaluating immunization campaign activities and identifying program challenges.");
        text.getStyle().set("color", "green");
        aboutText.add(text);

        HorizontalLayout guides = new HorizontalLayout();
        Tab techguide = new Tab("Technical Guide");
        techguide.getStyle().set("color", "green");
        Tab userguide = new Tab("User Guide");
        userguide.getStyle().set("color", "green");

        Tabs tabs = new Tabs(techguide, userguide);
       // tabs.getStyle().set("background", "#434343");

        guides.add(tabs);

		

		aboutView.add(apmisImageContainer, aboutText, guides);
        add(aboutView);

    }

}
