package com.cinoteck.application.views;

import com.cinoteck.application.components.appnav.AppNav;
import com.cinoteck.application.components.appnav.AppNavItem;
import com.cinoteck.application.views.about.AboutView;
//import com.cinoteck.application.views.admin.AdminView;
import com.cinoteck.application.views.admin.TestView1;
import com.cinoteck.application.views.campaign.CampaignView;
import com.cinoteck.application.views.campaigndata.CampaignDataView;
import com.cinoteck.application.views.configurations.ConfigurationsView;
import com.cinoteck.application.views.dashboard.DashboardView;
import com.cinoteck.application.views.myaccount.MyAccountView;
import com.cinoteck.application.views.test.TestPageView;
//import com.cinoteck.application.views.user.UserView;
//import com.cinoteck.application.views.test.TestView;
import com.cinoteck.application.views.user.UserView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.internal.UIInternals.JavaScriptInvocation;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.nio.file.attribute.UserDefinedFileAttributeView;

/**
 * The main view is a top-level placeholder for other views.
 */

@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://code.jquery.com/jquery-3.6.3.min.js")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
public class MainLayout extends AppLayout {

	private H2 viewTitle;

	public MainLayout() {
		setPrimarySection(Section.DRAWER);
		addDrawerContent();
		addHeaderContent();
//		getUI().get().getPage().executeJs("alert(\"thursday\");");

	}



	private void addHeaderContent() {
		DrawerToggle toggle = new DrawerToggle();
		toggle.getElement().setAttribute("aria-label", "Menu toggle");

		viewTitle = new H2();
		viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

		addToNavbar(true, toggle, viewTitle);
	}

	private void addDrawerContent() {
//		H1 appName = new H1("APMIS-UI-UPGRADE");
//		appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
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
		nav.addItem(new AppNavItem("Campaign", CampaignDataView.class, "la la-clipboard", "navitem").addItem(
				(new AppNavItem("Campaigns", CampaignView.class, "la la-clipboard", "navitem")),
				(new AppNavItem("Campaign  Data", CampaignDataView.class, "la la-clipboard", "navitem"))));
//		nav.addItem(new AppNavItem("Admin", AdminView.class, "la la-user-circle", "navitem").addItem(
//				(new AppNavItem("Configurations", ConfigurationsView.class, "la la-cogs", "navitem")),
//				(new AppNavItem("User ", UserView.class, "la la-user-cog", "navitem"))));

		nav.addItem(new AppNavItem("My Account", MyAccountView.class, "la la-user", "navitem"));

		nav.addItem(new AppNavItem("About", AboutView.class, "la la-info-circle", "navitem"));
		nav.addItem(new AppNavItem("Test", TestPageView.class, "la la-info-circle", "navitem"));

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