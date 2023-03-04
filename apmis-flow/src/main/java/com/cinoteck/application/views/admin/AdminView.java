//package com.cinoteck.application.views.admin;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//import com.cinoteck.application.views.MainLayout;
//import com.vaadin.flow.component.Component;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.combobox.ComboBox;
//import com.vaadin.flow.component.html.Div;
//import com.vaadin.flow.component.html.H1;
//import com.vaadin.flow.component.html.H2;
//import com.vaadin.flow.component.html.Image;
//import com.vaadin.flow.component.html.Paragraph;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.tabs.Tab;
//import com.vaadin.flow.component.tabs.Tabs;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import com.vaadin.flow.router.RouterLayout;
//import com.vaadin.flow.server.RouteRegistry;
//
//@PageTitle("Admin")
//@Route(value = "admin", layout = MainLayout.class)
//public class AdminView extends VerticalLayout implements RouterLayout {
//
//    private Map<Tab, Component> tabComponentMap = new LinkedHashMap<>();
//
//    public AdminView() {
//        Tabs tabs = createTabs();
//        Div contentContainer = new Div();
//        contentContainer.setWidth("100%");
//        add(tabs, contentContainer);
//
//        tabs.addSelectedChangeListener(e -> {
//            contentContainer.removeAll();
//            contentContainer.add(tabComponentMap.get(e.getSelectedTab()));
//        });
//        // Set initial content
//        contentContainer.add(tabComponentMap.get(tabs.getSelectedTab()));
//    }
//
//    private Tabs createTabs() {
//        tabComponentMap.put(new Tab("Show some text"), new TestView1());
//        tabComponentMap.put(new Tab("Show a Combo Box"), new TestView2());
//        tabComponentMap.put(new Tab("Show a button"), new TestView3());
//        return new Tabs(tabComponentMap.keySet().toArray(new Tab[]{}));
//    }
//
//}
