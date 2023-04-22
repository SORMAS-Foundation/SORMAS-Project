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
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
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
        tabComponentMap.put(new Tab("Province"), new ProvinceView());
        tabComponentMap.put(new Tab("District"), new DistrictView());
        tabComponentMap.put(new Tab("Cluster"), new ClusterView());
       
        return new Tabs(tabComponentMap.keySet().toArray(new Tab[] {}));

    }

    public ConfigurationsView() {
        HorizontalLayout campDatFill = new HorizontalLayout();
        campDatFill.setClassName("campDatFill");

        Tabs tabs = createTabss();
        tabs.getStyle().set("background", "#434343");
        tabs.getStyle().set("width", "100%");
        Div contentContainer = new Div();
        contentContainer.setWidthFull();


        tabs.addSelectedChangeListener(e -> {
            contentContainer.removeAll();
            contentContainer.add(tabComponentMap.get(e.getSelectedTab()));
        });
        // Set initial content
        contentContainer.add(tabComponentMap.get(tabs.getSelectedTab()));
        

        HorizontalLayout configActionLayput = new HorizontalLayout();
        configActionLayput.getStyle().set("margin-right", "1em");
        configActionLayput.setMargin(false);
        configActionLayput.setJustifyContentMode(JustifyContentMode.END);
		
        Button importButton = new Button("Import", new Icon(VaadinIcon.DOWNLOAD_ALT));
		importButton.getStyle().set("color", "white");
		importButton.getStyle().set("background", "#0C5830");
		importButton.setVisible(false);
		configActionLayput.add(importButton);
		

		Button exportButton = new Button("Export", new Icon(VaadinIcon.UPLOAD_ALT));
		exportButton.getStyle().set("color", "white");
		exportButton.getStyle().set("background", "#0C5830");
		exportButton.setVisible(false);
		configActionLayput.add(exportButton);
		
		

		Button newEntryButton = new Button("New Entry", new Icon(VaadinIcon.PLUS_CIRCLE_O));
		newEntryButton.getStyle().set("color", "white");
		newEntryButton.getStyle().set("background", "#0C5830");
		newEntryButton.setVisible(false);
		configActionLayput.add(newEntryButton);

		Button bulkEditMode = new Button("Enter Bulk Mode", new Icon(VaadinIcon.CHECK));
		bulkEditMode.getStyle().set("color", "white");
		bulkEditMode.getStyle().set("background", "#0C5830");
		bulkEditMode.setVisible(false);
		configActionLayput.add(bulkEditMode);
		
		Button displayActionButtons = new Button("Show Action Buttons", new Icon(VaadinIcon.SLIDERS));
		displayActionButtons.addClickListener(e->{
			if (bulkEditMode.isVisible() == false) {
				importButton.setVisible(true);
				exportButton.setVisible(true);
				newEntryButton.setVisible(true);
				bulkEditMode.setVisible(true);
				displayActionButtons.setText("Hide Action Buttons");
			}else {
				importButton.setVisible(false);
				exportButton.setVisible(false);
				newEntryButton.setVisible(false);
				bulkEditMode.setVisible(false);
				displayActionButtons.setText("Show Action Buttons");
			}
		});
		configActionLayput.add(displayActionButtons);

	
        campDatFill.add(tabs, configActionLayput);


        add(campDatFill,contentContainer);
    }

}
