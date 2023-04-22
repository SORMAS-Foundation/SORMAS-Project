package com.cinoteck.application.views.dashboard;

import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(layout = DashboardView.class)
public class CampaignSummaryGridView extends VerticalLayout {

    public CampaignSummaryGridView(){

        Div chartsContainer = new Div();
        chartsContainer.setClassName("container col");
        
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
        charts1.setClassName("card col-sm-12 col-md-6 col-lg-3");
        Image img = new Image("images/missedchildren.png", "placeholder plant");
       // charts1.setWidth("25%");
        charts1.add(img);
        DragSource<Div> box1DragSource = DragSource.create(charts1);
 
        Div charts2 = new Div();
        charts2.setClassName("card col-sm-12 col-md-6 col-lg-3");
        Image img2 = new Image("images/reportedmissed.png", "placeholder plant");
      //  charts2.setWidth("25%");
        charts2.add(img2);
        DragSource<Div> box2DragSource = DragSource.create(charts2);
        box2DragSource.setDraggable(true);

        Div charts3 = new Div();
        charts3.setClassName("card col-sm-12 col-md-6 col-lg-3");
        Image img3 = new Image("images/reportedmissed2.png", "placeholder plant");
      //  charts3.setWidth("25%");
        charts3.add(img3);

        Div charts4 = new Div();
        charts4.setClassName("card col-sm-12 col-md-6 col-lg-3");
        Image img4 = new Image("images/reportedmissed3.png", "placeholder plant");
       // charts4.setWidth("25%");
        charts4.add(img4);

        Div charts5 = new Div();
        charts5.setClassName("card col-sm-12 col-md-12 col-lg-6");
        Image img5 = new Image("images/columnbargraph.png", "placeholder plant");
        img5.setWidth("100%");
        charts5.setWidth("50%");
        charts5.add(img5);

        Div charts6 = new Div();
        charts6.setClassName("card col-sm-12 col-md-12 col-lg-6");
        Image img6 = new Image("images/xolumndonut2.png", "placeholder plant");
        img6.setWidth("100%");
        charts6.setWidth("50%");
        charts6.add(img6);

        Div charts7 = new Div();
        charts7.setClassName("card col-sm-12 col-md-12 col-lg-12");
        Image img7 = new Image("images/vactag2.png", "placeholder plant");
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
