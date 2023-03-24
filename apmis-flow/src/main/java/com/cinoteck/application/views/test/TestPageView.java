package com.cinoteck.application.views.test;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.cinoteck.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Test Page")
@Route(value = "test", layout = MainLayout.class)

public class TestPageView extends VerticalLayout {

	public TestPageView() {
		DatePicker date = new DatePicker("Pick A Date");
		Button button = new Button("Click Me");

		HorizontalLayout layout = new HorizontalLayout(date, button);
		layout.setDefaultVerticalComponentAlignment(Alignment.END);

		add(layout);

		button.addClickListener(click -> add(new Paragraph("Clicked" + date.getValue())));
	}
}
