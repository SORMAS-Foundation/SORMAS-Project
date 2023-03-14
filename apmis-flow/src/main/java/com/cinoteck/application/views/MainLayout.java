package com.cinoteck.application.views;

import com.cinoteck.application.components.appnav.AppNav;
import com.cinoteck.application.components.appnav.AppNavItem;
import com.cinoteck.application.views.about.AboutView;
import com.cinoteck.application.views.campaign.CampaignView;
import com.cinoteck.application.views.campaigndata.CampaignDataView;
import com.cinoteck.application.views.configurations.ConfigurationsView;
import com.cinoteck.application.views.dashboard.DashboardView;
import com.cinoteck.application.views.logout.LogoutView;
import com.cinoteck.application.views.myaccount.MyAccountView;
import com.cinoteck.application.views.reports.ReportView;
//import com.cinoteck.application.views.user.UserView;
//import com.cinoteck.application.views.test.TestView;
import com.cinoteck.application.views.user.UserView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */
//@CssImport(value = "./themes/my-theme/components/vcf-nav-item.css", themeFor = "vcf-nav-item")

@JavaScript("https://code.jquery.com/jquery-3.6.3.min.js")
@StyleSheet("https://cdn.jsdelivr.net/npm/@vaadin/vaadin-lumo-styles@24.0.0/+esm")
@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://code.jquery.com/jquery-3.6.3.min.js")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
public class MainLayout extends AppLayout {

	private H1 viewTitle;

	public MainLayout() {
		setPrimarySection(Section.DRAWER);
		addDrawerContent();
		addHeaderContent();
	}



	private void addHeaderContent() {
		DrawerToggle toggle = new DrawerToggle();
		toggle.getElement().setAttribute("aria-label", "Menu toggle");
		toggle.getStyle().set("color", "white");


		viewTitle = new H1();
		viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
		viewTitle.getStyle().set("padding-left", "40%");
		viewTitle.getStyle().set("color", "green");
		viewTitle.getStyle().set("font-size", "25px");

		addToNavbar(true, toggle, viewTitle);
	}

	private void addDrawerContent() {
		Image imgApmis = new Image("images/apmis_horizontal_logo.png", "APMIS-LOGO");
		imgApmis.setMaxWidth("100%");
		Scroller scroller = new Scroller(createNavigation());

		Header header = new Header(imgApmis);

		addToDrawer(header, scroller, createFooter());
	}

	private AppNav createNavigation() {
		// AppNav is not yet an official component.
		// For documentation, visit https://github.com/vaadin/vcf-nav#readme
		AppNav nav = new AppNav();

		nav.addItem(new AppNavItem("Dashboard", DashboardView.class, "la la-th-large", "navitem"));
		nav.addItem(new AppNavItem("Campaign Data", CampaignDataView.class,"la la-th-large", "navitem"));
		nav.addItem(new AppNavItem("All Campaigns", CampaignView.class, "la la-th-large", "navitem"));
		nav.addItem(new AppNavItem("Configuration", ConfigurationsView.class, "la la-th-large", "navitem"));
		nav.addItem(new AppNavItem("Users", UserView.class, "la la-user-cog", "navitem"));
		nav.addItem(new AppNavItem("Reports", ReportView.class, "la la-user-cog", "navitem"));
		nav.addItem(new AppNavItem("User Profile", MyAccountView.class, "la la-user", "navitem"));
		nav.addItem(new AppNavItem("About", AboutView.class, "la la-info-circle", "navitem"));
		nav.addItem(new AppNavItem("Test", UserView.class, "la la-info-circle", "navitem"));
		nav.addItem(new AppNavItem("Sign Out", LogoutView.class, "la la-info-circle", "navitem"));

		return nav;


	}

	private Footer createFooter() {
		Footer layout = new Footer();

		return layout;
	}

	@Override
	protected void afterNavigation() {
		super.afterNavigation();
		viewTitle.setText(getCurrentPageTitle());
	}

	private String getCurrentPageTitle() {
		PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
		return title == null ? "" : title.value();
	}

//	@Override
	public void configurePage(InitialPageSettings settings) {
		settings.addLink("shortcut icon", "icons/icon.png");
		settings.addFavIcon("icon", "icons/icon.png", "192x192");
	}


}