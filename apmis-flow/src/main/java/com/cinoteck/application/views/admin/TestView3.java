package com.cinoteck.application.views.admin;




//import com.cinoteck.application.views.admin.AdminView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
//import com.vaadin.highcharts.HighCharts;

import java.awt.*;

//@Route( layout = AdminView.class)
public class TestView3 extends VerticalLayout {
    public TestView3() {   add("wiwtetrs");}


}
//
//    private Button drawChartButton;
//    private HighCharts hc;
//
//    @Override
//    public void init(){
//        Window mainWindow = new Window("HighCharts Sample Application");
//        Panel highChartsPanel = new Panel("HighCharts");
//
//        drawChartButton = new Button("Draw chart");
//        drawChartButton.addClickListener(new Button("Draw chart")) {
//            private static final long serialVersionUID = 1L;
//
////            @Override
////            public void buttonClick(ClickEvent event) {
////
////                String hcjs = "var chart = new Highcharts.Chart({" +
////                        "chart: { renderTo: 'container' }," +
////                        "xAxis: { categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'] }," +
////                        "series: [{" +
////                        "type: 'line', data: [29.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4], name: 'Temperature' }, {" +
////                        "type: 'column', data: [194.1, 95.6, 54.4, 29.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4], name: 'Rainfall' }]" +
////                        "});";
////
////                hc.drawChart(hcjs);
////            }
////        });
//
//       // highChartsPanel.add(drawChartButton());
////        highChartsPanel.add(getHc());
//a
//        mainWindow.add(highChartsPanel);
////        setMainWindow(mainWindow);
//    }
//
//    private void setMainWindow(Window mainWindow) {
//    }
//
//    private Component getHc() {
//        return null;
//    }
//}
//}