package com.cinoteck.application.views.reports;

import java.util.LinkedHashMap;
import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

@Route(layout = ReportView.class)
public class CompletionAnalysisView extends VerticalLayout implements RouterLayout{
	private Map<Tab, Component> tabComponentMap = new LinkedHashMap<>();

	private Tabs createTabs() {
		tabComponentMap.put(new Tab("ICM Completion"), new CompletionAnalysisGridView());
		
		
		return new Tabs(tabComponentMap.keySet().toArray(new Tab[] {}));

	}
	
	public CompletionAnalysisView() {
		
	}

}
