package com.cinoteck.application.views.configurations;
//
//import com.cinoteck.application.data.entity.RegionsDataGrid;
//import com.cinoteck.application.data.service.RegionService;

import com.cinoteck.application.views.MainLayout;
import com.cinoteck.application.views.admin.TestView1;
//import com.cinoteck.application.views.admin.TestView2;
import com.cinoteck.application.views.admin.TestView3;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import java.util.LinkedHashMap;
import java.util.Map;

@PageTitle("Configurations")
@Route(value = "configurations", layout = MainLayout.class)
public class ConfigurationsView extends VerticalLayout implements RouterLayout {
    private Map<Tab, Component> tabComponentMap = new LinkedHashMap<>();

    private Tabs createTabss() {
        tabComponentMap.put(new Tab("Regions"), new RegionView());
//        tabComponentMap.put(new Tab("Province"), new TestView2());
        tabComponentMap.put(new Tab("District"), new TestView3());
        tabComponentMap.put(new Tab("Cluster"), new TestView1());
        tabComponentMap.put(new Tab("Population"), new TestView1());
        return new Tabs(tabComponentMap.keySet().toArray(new Tab[] {}));

    }

    public ConfigurationsView() {
        HorizontalLayout campDatFill = new HorizontalLayout();
        campDatFill.setClassName("campDatFill");

        Tabs tabs = createTabss();
        tabs.getStyle().set("background", "#434343");
        Div contentContainer = new Div();
        contentContainer.setWidthFull();


        tabs.addSelectedChangeListener(e -> {
            contentContainer.removeAll();
            contentContainer.add(tabComponentMap.get(e.getSelectedTab()));
        });
        // Set initial content
        contentContainer.add(tabComponentMap.get(tabs.getSelectedTab()));


        Button addNewFormm = new Button("Export");
        Button importButton = new Button("New Entry");
        Button exportButton = new Button("Enter Bulk Mode");

        Div actionLayout = new Div(addNewFormm, importButton, exportButton);
        actionLayout.setClassName("actionLayout");

        campDatFill.add(tabs,actionLayout);

        HorizontalLayout configAction = new HorizontalLayout();
        configAction.setWidthFull();
        configAction.getStyle().set("display", "flex");
        configAction.getStyle().set("justify-content", "flex-end");




        TextField searchField = new TextField();
        searchField.setLabel("Search Campaign");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        Button clear = new Button("Clear");

        Select<String> activeRegions = new Select<>();
        activeRegions.setLabel("Region");
        activeRegions.setItems("Most recent first", "Rating: high to low",
                "Rating: low to high", "Price: high to low",
                "Price: low to high");
        activeRegions.setValue("");

        Div configActionLayout = new Div(searchField, clear, activeRegions);

        configActionLayout.setWidthFull();
        configActionLayout.getStyle().set("display", "flex");
        configActionLayout.getStyle().set("justify-content", "flex-end");
        configActionLayout.getStyle().set("align-items", "flex-end");

        configAction.add(configActionLayout);



        add(campDatFill, configAction, contentContainer);
    }

}
