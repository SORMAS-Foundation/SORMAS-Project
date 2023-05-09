package com.cinoteck.application.views.dashboard;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.cinoteck.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;

@PageTitle("Campaign Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)

@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
public class DashboardView extends VerticalLayout implements RouterLayout {

	Binder<UserDto> binder = new BeanValidationBinder<>(UserDto.class);
	Binder<CampaignDto> campaignBinder = new BeanValidationBinder<>(CampaignDto.class);

	private Map<Tab, Component> tabComponentMap = new LinkedHashMap<>();
	ComboBox<CampaignReferenceDto> campaign = new ComboBox<>();
	ComboBox<String> campaignPhase = new ComboBox<>();
	ComboBox<AreaReferenceDto> region = new ComboBox<>();
	ComboBox<RegionReferenceDto> province = new ComboBox<>();
	ComboBox<DistrictReferenceDto> district = new ComboBox<>();
	ComboBox<CommunityReferenceDto> cluster = new ComboBox<>();

	List<CampaignReferenceDto> campaigns;
	List<CampaignReferenceDto> campaignPhases;
	List<AreaReferenceDto> regions;
	List<RegionReferenceDto> provinces;
	List<DistrictReferenceDto> districts;
	List<CommunityReferenceDto> communities;

	private Tabs createTabs() {
		tabComponentMap.put(new Tab("Campaign Summary"), new CampaignSummaryGridView());
		tabComponentMap.put(new Tab("Admin Coverage By Day"), new AdminCovByDayGridView());
		tabComponentMap.put(new Tab("Admin Coverage: Doses"), new AdminCovByDosesGridView());
		tabComponentMap.put(new Tab("Coverage Summary"), new AdminCovByDosesGridView());
		return new Tabs(tabComponentMap.keySet().toArray(new Tab[] {}));

	}

	public DashboardView() {
		setSpacing(true);

		campaign.setLabel("Campaign");
		campaign.setId("jgcjgcjgcj");
		campaigns = FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference();
		campaign.setItems(campaigns);

		campaign.getStyle().set("padding-top", "0px");
		campaign.setClassName("col-sm-6, col-xs-6");

		campaignPhase.setLabel("Campaign Phase");
//		campaignPhases = FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference()
		campaignPhase.setItems("Pre-Campaign", "Intra- Campaign", "Post- Campaign");
		campaignPhase.setValue("");
		campaignPhase.getStyle().set("padding-top", "0px");
		campaignPhase.setClassName("col-sm-6, col-xs-6");

		region.setLabel("Region");
		binder.forField(region).bind(UserDto::getArea, UserDto::setArea);
		regions = FacadeProvider.getAreaFacade().getAllActiveAsReference();
		region.setItems(regions);
		region.addValueChangeListener(e -> {
			provinces = FacadeProvider.getRegionFacade().getAllActiveByArea(e.getValue().getUuid());
			province.setItems(provinces);
		});
		region.getStyle().set("padding-top", "0px");
		region.setClassName("col-sm-6, col-xs-6");

		province.setLabel("Province");
		binder.forField(province).bind(UserDto::getRegion, UserDto::setRegion);
		provinces = FacadeProvider.getRegionFacade().getAllActiveAsReference();
		province.setItems(provinces);
		province.addValueChangeListener(e -> {
			districts = FacadeProvider.getDistrictFacade().getAllActiveByRegion(e.getValue().getUuid());
			district.setItems(districts);
		});
		province.getStyle().set("padding-top", "0px");
		province.setClassName("col-sm-6, col-xs-6");

		district.setLabel("District");
		binder.forField(district).bind(UserDto::getDistrict, UserDto::setDistrict);
		districts = FacadeProvider.getDistrictFacade().getAllActiveAsReference();
		district.setItems(districts);
		district.addValueChangeListener(e -> {
			communities = FacadeProvider.getCommunityFacade().getAllActiveByDistrict(e.getValue().getUuid());
			cluster.setItemLabelGenerator(CommunityReferenceDto::getCaption);
			cluster.setItems(communities);
		});
		district.getStyle().set("padding-top", "0px");
		district.setClassName("col-sm-6, col-xs-6");

		cluster.setLabel("Cluster");

//		communities = FacadeProvider.getCommunityFacade().getAllActiveByDistrict(null);
//		cluster.setItems(communities);

		cluster.getStyle().set("padding-top", "0px");
		cluster.setClassName("col-sm-6, col-xs-6");

		Select<String> groupby = new Select<>();
		groupby.setLabel("Group By");
		groupby.setItems("Archived", "Active", "Closed", "Open");
		groupby.setValue("");
		groupby.getStyle().set("padding-top", "0px");
		groupby.setClassName("col-sm-6, col-xs-6");

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
		HorizontalLayout selectFilterLayout = new HorizontalLayout(campaign, campaignPhase, region, province, district,
				cluster);
		selectFilterLayout.setClassName("row pl-3");
		// HorizontalLayout selectFilterLayout2 = new HorizontalLayout(overviewTabs);
		VerticalLayout selectFilterLayoutparent = new VerticalLayout(selectFilterLayout);
		selectFilterLayoutparent.getStyle().set("padding", "0px");
		selectFilterLayoutparent.getStyle().set("margin-left", "12px");
		selectFilterLayoutparent.setVisible(false);

		Button displayFilters = new Button("Show Filters");
		displayFilters.getStyle().set("margin-left", "12px");
		displayFilters.getStyle().set("margin-top", "12px");
		displayFilters.setIcon(new Icon(VaadinIcon.SLIDERS));

		displayFilters.addClickListener(e -> {
			if (!selectFilterLayoutparent.isVisible()) {
				selectFilterLayoutparent.setVisible(true);
				displayFilters.setText("Hide Filters");

			} else {
				selectFilterLayoutparent.setVisible(false);
				displayFilters.setText("Show Filters");
			}

		});

		// filterAccordion.add("Filters", overviewTabs);
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

		add(displayFilters, selectFilterLayoutparent, tabs, contentContainer);
		setSizeFull();
	}
//	private ComboBox campaignFilter;
//	
//	private void createCampaignFilter() {
//		campaignFilter.setRequired(true);
////		campaignFilter.setNullSelectionAllowed(false);
////		campaignFilter.setCaption(I18nProperties.getCaption(Captions.Campaign));
//		
//		//campaignFilter.setWidth(200, Unit.PIXELS);
////		campaignFilter.setInputPrompt(I18nProperties.getString(Strings.promptCampaign));
//		campaignFilter.setItems(FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference().toArray());
//		campaignFilter.addValueChangeListener(e -> {
////			dashboardDataProvider.setCampaign((CampaignReferenceDto) campaignFilter.getValue());
////			dashboardView.refreshDashboard();
//		});
//		add(campaignFilter);
//		
//		createCampaignPhaseFilter();
//
//		final CampaignReferenceDto lastStartedCampaign = dashboardDataProvider.getLastStartedCampaign();
//		if (lastStartedCampaign != null) {
//			//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.  "+lastStartedCampaign);
//			campaignFilter.setValue(lastStartedCampaign);
//			campaignPhaseFilter.setValue(CampaignPhase.INTRA.toString());
//		}
//		dashboardDataProvider.setCampaign((CampaignReferenceDto) campaignFilter.getValue());
//	}

}
