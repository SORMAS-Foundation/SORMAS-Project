package com.cinoteck.application.views.admin;

//import com.cinoteck.application.views.admin.AdminView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;

//@Route(layout = AdminView.class)

public class TestView1 extends VerticalLayout {

	public TestView1() {

		Div chartsContainer = new Div();
		chartsContainer.setClassName("container col-12");
		Div chartsRow = new Div();
		chartsRow.setClassName("row col");
		chartsRow.getStyle().set( "display", "flex");
		chartsRow.getStyle().set("flex-direction" , "row");

		Div chartsRow2 = new Div();
		chartsRow2.setClassName("row col");
		chartsRow2.getStyle().set( "display", "flex");
		chartsRow2.getStyle().set("flex-direction" , "row");

		Div chartsRow3 = new Div();
		chartsRow3.setClassName("row col");
		chartsRow3.getStyle().set( "display", "flex");
		chartsRow3.getStyle().set("flex-direction" , "row");


		Div charts1 = new Div();
		charts1.setClassName("card col-sm-6 col-md-3 col-lg-3");
		Image img = new Image("images/targetedchildren.png", "placeholder plant");
		charts1.setWidth("25%");
		charts1.add(img);

		Div charts2 = new Div();
		charts2.setClassName("card col-sm-6 col-md-3 col-lg-3");
		Image img2 = new Image("images/targetedchildren.png", "placeholder plant");
		charts2.setWidth("25%");
		charts2.add(img2);

		Div charts3 = new Div();
		charts3.setClassName("card col-sm-6 col-md-3 col-lg-3");
		Image img3 = new Image("images/targetedchildren.png", "placeholder plant");
		charts3.setWidth("25%");
		charts3.add(img3);

		Div charts4 = new Div();
		charts4.setClassName("card col-sm-6 col-md-3 col-lg-3");
		Image img4 = new Image("images/targetedchildren.png", "placeholder plant");
		charts4.setWidth("25%");
		charts4.add(img4);

		Div charts5 = new Div();
		charts5.setClassName("card col-sm-6 col-md-3 col-lg-3");
		Image img5 = new Image("images/totalvac.png", "placeholder plant");
		img5.setWidth("100%");
		charts5.setWidth("50%");
		charts5.add(img5);

		Div charts6 = new Div();
		charts6.setClassName("card col-sm-6 col-md-3 col-lg-3");
		Image img6 = new Image("images/totalvac2.png", "placeholder plant");
		img6.setWidth("100%");
		charts6.setWidth("50%");
		charts6.add(img6);

		Div charts7 = new Div();
		charts7.setClassName("card col-sm-6 col-md-3 col-lg-3");
		Image img7 = new Image("images/vacvstarg.png", "placeholder plant");
		img7.setWidth("100%");
		charts7.setWidth("100%");
		charts7.add(img7);

		chartsRow.add(charts1, charts2, charts3, charts4);
		chartsRow2.add(charts5, charts6);
		chartsRow3.add(charts7);

		chartsContainer.add(chartsRow, chartsRow2, chartsRow3);
		chartsContainer.setWidth("100%");

		add(chartsContainer);
	}
}