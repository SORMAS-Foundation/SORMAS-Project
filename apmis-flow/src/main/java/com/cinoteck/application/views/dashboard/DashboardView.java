package com.cinoteck.application.views.dashboard;

import java.util.LinkedHashMap;
import java.util.Map;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.cinoteck.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

@PageTitle("Campaign Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)

@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
public class DashboardView extends VerticalLayout implements RouterLayout{
	private Map<Tab, Component> tabComponentMap = new LinkedHashMap<>();

	private Tabs createTabs() {
		tabComponentMap.put(new Tab("Campaign Summary"), new CampaignSummaryGridView());
		tabComponentMap.put(new Tab("Admin Coverage By Day"), new AdminCovByDayGridView());
		tabComponentMap.put(new Tab("Admin Coverage: Doses"), new AdminCovByDosesGridView());
		tabComponentMap.put(new Tab("Coverage Summary"), new CampaignSummaryGridView());
		return new Tabs(tabComponentMap.keySet().toArray(new Tab[] {}));

	}

	public DashboardView() {
		setSpacing(true);


		Select<String> campaign = new Select<>();
		campaign.setLabel("Campaign");
		campaign.setId("jgcjgcjgcj");
		campaign.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
				"Price: low to high");
		campaign.setValue("Most recent first");


		Select<String> region = new Select<>();
		region.setLabel("Region");
		region.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
				"Price: low to high");
		region.setValue("");

		Select<String> province = new Select<>();
		province.setLabel("Province");
		province.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
				"Price: low to high");
		province.setValue("");

		Select<String> district = new Select<>();
		district.setLabel("District");
		district.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
				"Price: low to high");
		district.setValue("");

		Select<String> groupby = new Select<>();
		groupby.setLabel("Group By");
		groupby.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
				"Price: low to high");
		groupby.setValue("");

		Select<String> campaignPhase = new Select<>();
		campaignPhase.setLabel("Campaign Phase");
		campaignPhase.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
				"Price: low to high");
		campaignPhase.setValue("");

		HorizontalLayout selectFilterLayout = new HorizontalLayout(campaign, region, province, district, campaignPhase);
		Accordion filterAccordion =  new Accordion();
		filterAccordion.add("Filters", selectFilterLayout);

		Tab details = new Tab("National Overview");
		details.setId("tabsheetBBorder");
		details.getStyle().set("border-radius", "15px 0px  0px 15px");
		details.getStyle().set("border", "1px solid #e3b28a");

		Tab payment = new Tab("Admin Coverage");
		details.getStyle().set("border", "1px solid #e3b28a");


		Tab shipping = new Tab("ICM Coverage");
		shipping.getStyle().set("border-radius", "0px 15px 15px 0px");
		shipping.getStyle().set("border", "1px solid #e3b28a");

		Tabs overviewTabs = new Tabs(details, payment, shipping);


		Tabs tabs = createTabs();
		tabs.setId("overviewTab");
		tabs.getStyle().set("background", "#434343");
		tabs.getStyle().set("width", "100%");


		Div contentContainer = new Div();
		contentContainer.setWidthFull();

		contentContainer.setId("tabsSheet");
//        add(contentContainer);
		tabs.addSelectedChangeListener(e -> {
			contentContainer.removeAll();
			contentContainer.add(tabComponentMap.get(e.getSelectedTab()));


		});
		// Set initial content
		contentContainer.add(tabComponentMap.get(tabs.getSelectedTab())

		);


		add(filterAccordion, overviewTabs,tabs, contentContainer);
		setSizeFull();

		setDefaultHorizontalComponentAlignment(Alignment.START);
		getStyle().set("text-align", "center");
////		final Grid<Movie> grid = new Grid<Movie>();
////		final TextField filterr = new TextField();
////		final UserRepository repo;
////		final Button addNewBtnn = new Button();
//		// final UserEditor editor
//		Accordion accordion = new Accordion();
//
//		Select<String> campaign = new Select<>();
//		campaign.setLabel("Campaign");
//		campaign.setId("jgcjgcjgcj");
//		campaign.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
//				"Price: low to high");
//		campaign.setValue("Most recent first");
//
//
//		Select<String> region = new Select<>();
//		region.setLabel("Region");
//		region.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
//				"Price: low to high");
//		region.setValue("");
//
//		Select<String> province = new Select<>();
//		province.setLabel("Province");
//		province.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
//				"Price: low to high");
//		province.setValue("");
//
//		Select<String> district = new Select<>();
//		district.setLabel("District");
//		district.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
//				"Price: low to high");
//		district.setValue("");
//
//		Select<String> groupby = new Select<>();
//		groupby.setLabel("Group By");
//		groupby.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
//				"Price: low to high");
//		groupby.setValue("");
//
//		Select<String> campaignPhase = new Select<>();
//		campaignPhase.setLabel("Campaign Phase");
//		campaignPhase.setItems("Most recent first", "Rating: high to low", "Rating: low to high", "Price: high to low",
//				"Price: low to high");
//		campaignPhase.setValue("");
//
//		HorizontalLayout filterLayout = new HorizontalLayout(campaign, region, province, district, campaignPhase);
//		accordion.add("Filters", filterLayout );
//
//		Button nOverview = new Button("National Overview");
//		nOverview.setClassName("natOverview");
//		Button adminCoverage = new Button("Admin Coverage");
//		adminCoverage.setClassName("adminCoverage");
//		Button icmCoverage = new Button("ICM Coverage");
//		icmCoverage.setClassName("icmCoverage");
//
//		HorizontalLayout overviewLayout = new HorizontalLayout(nOverview, adminCoverage, icmCoverage);
//
//
//		Tabs tabs = createTabs();
//		tabs.getStyle().set("background", "#434343");
//		tabs.getStyle().set("width", "100%");
//		tabs.getChildren().toArray();
//
//		Div contentContainer = new Div();
//		contentContainer.setWidthFull();
//
//		contentContainer.setId("tabsSheet");
//		add(contentContainer);
//
//		tabs.addSelectedChangeListener(e -> {
//			contentContainer.removeAll();
//			contentContainer.add(tabComponentMap.get(e.getSelectedTab()));
//
//		});
//		// Set initial content
//		contentContainer.add(tabComponentMap.get(tabs.getSelectedTab())
//
//		);
//		String tabSlected = tabs.getChildren().toString();
//		System.out.println(tabSlected + "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
//
//
//
////		Tabs tabs = new Tabs(campaignSummary, adminCoverageByDay, adminCoverageDoses, coverageSummary);
//
//		VerticalLayout layoutDiv = new VerticalLayout();
//		Div shola = new Div();
//		VerticalLayout shola2 = new VerticalLayout();
//		shola.setClassName("Sholadiv");
//		VerticalLayout accordionChild = new VerticalLayout( );
//
//
//
//		shola.add(accordion);
//		shola2.setClassName("Sholadiv2");
//		shola2.add(overviewLayout);
//		shola2.add(tabs);
//		layoutDiv.add(shola, shola2);
//		layoutDiv.setClassName("emma");
//
//
//		add(layoutDiv,contentContainer);
//
//		setSizeFull();
//		setDefaultHorizontalComponentAlignment(Alignment.START);
	}


	
}
