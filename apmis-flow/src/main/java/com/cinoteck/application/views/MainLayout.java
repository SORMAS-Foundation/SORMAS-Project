package com.cinoteck.application.views;

import com.cinoteck.application.components.appnav.AppNav;
import com.cinoteck.application.components.appnav.AppNavItem;
import com.cinoteck.application.views.about.AboutView;
import com.cinoteck.application.views.campaign.CampaignsView;
import com.cinoteck.application.views.campaigndata.CampaignDataView;
import com.cinoteck.application.views.configurations.ConfigurationsView;
import com.cinoteck.application.views.dashboard.DashboardView;
import com.cinoteck.application.views.logout.LogoutView;
import com.cinoteck.application.views.myaccount.MyAccountView;
import com.cinoteck.application.views.reports.ReportView;
import com.cinoteck.application.views.support.SupportView;
//import com.cinoteck.application.views.user.UserView;
//import com.cinoteck.application.views.test.TestView;
import com.cinoteck.application.views.user.UserView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */
//@CssImport(value = "./themes/my-theme/components/vcf-nav-item.css", themeFor = "vcf-nav-item")
@NpmPackage(value = "lumo-css-framework", version = "^4.0.10")
@NpmPackage(value = "line-awesome", version = "1.3.0")
@NpmPackage(value = "@vaadin-component-factory/vcf-nav", version = "1.0.6")
@JavaScript(value = "https://code.jquery.com/jquery-3.6.4.min.js" )
@StyleSheet("https://cdn.jsdelivr.net/npm/@vaadin/vaadin-lumo-styles@24.0.0/")
@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://code.jquery.com/jquery-3.6.3.min.js")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
@StyleSheet("https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@6.2.1/css/fontawesome.min.css")
@StyleSheet("https://cdnjs.cloudflare.com/ajax/libs/lato-font/3.0.0/css/lato-font.min.css")

@CssImport(value = "/styles/lato-font.css", themeFor = "vaadin-text-field")

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
		viewTitle.setId("pageHeader");

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
		

		nav.addItem(new AppNavItem("Dashboard", DashboardView.class,  VaadinIcon.GRID_BIG_O, "navitem"));
		nav.addItem(new AppNavItem("Campaign Data", CampaignDataView.class,  VaadinIcon.CLIPBOARD , "navitem"));
		nav.addItem(new AppNavItem("All Campaigns", CampaignsView.class, VaadinIcon.CLIPBOARD_TEXT, "navitem"));
		nav.addItem(new AppNavItem("Configuration", ConfigurationsView.class, VaadinIcon.COG_O, "navitem"));
		nav.addItem(new AppNavItem("Users", UserView.class, VaadinIcon.USERS, "navitem"));
		nav.addItem(new AppNavItem("Reports", ReportView.class,VaadinIcon.CHART_LINE, "navitem"));
		nav.addItem(new AppNavItem("User Profile", MyAccountView.class, VaadinIcon.USER_CHECK, "navitem"));
		nav.addItem(new AppNavItem("About", AboutView.class, VaadinIcon.INFO_CIRCLE_O, "navitem"));
		nav.addItem(new AppNavItem("Support", SupportView.class, VaadinIcon.INFO_CIRCLE_O, "navitem"));
//		nav.addItem(new AppNavItem("Test", TestPageView.class, "la la-info-circle", "navitem"));
		nav.addItem(new AppNavItem("Sign Out", LogoutView.class, VaadinIcon.SIGN_OUT_ALT, "navitem"));

		return nav;
		

	}
	
	private Tabs getTabs() {
	    Tabs tabs = new Tabs();
	    tabs.add(createTab(VaadinIcon.COG_O, "Dashboard",DashboardView.class));
	    tabs.add(createTab(VaadinIcon.CLIPBOARD, "Campaign Data", CampaignDataView.class));
	    tabs.add(createTab(VaadinIcon.CLIPBOARD_TEXT, "All Campaigns", CampaignsView.class));
	    tabs.add(createTab(VaadinIcon.COG_O, "Configurations", ConfigurationsView.class));
	    tabs.add(createTab(VaadinIcon.USERS, "Users", UserView.class));
	    tabs.add(createTab(VaadinIcon.CHART, "Report", ReportView.class));
	    tabs.add(createTab(VaadinIcon.USER, "User Profile", MyAccountView.class));
	    tabs.add(createTab(VaadinIcon.INFO_CIRCLE_O, "About", AboutView.class));
	    tabs.add(createTab(VaadinIcon.SIGN_OUT_ALT, "Sign-Out", SupportView.class));
	    tabs.setOrientation(Tabs.Orientation.VERTICAL);
	    tabs.addClassName("tabs");
	    return tabs;
	}

	
	//TODO: Move the styles into CSS classes for a cleaner code 
		private Tab createTab(VaadinIcon viewIcon, String viewName, Class<? extends Component> viewClass) {
		    Icon icon = viewIcon.create();
		    icon.getStyle().set("box-sizing", "border-box")
		        .set("margin-inline-end", "var(--lumo-space-m)")
		        .set("padding", "var(--lumo-space-xs)");

		    RouterLink link = new RouterLink();
		    link.setRoute(viewClass);
		    

		    // Create a VerticalLayout to stack the icon and the Span vertically
		    VerticalLayout verticalLayout = new VerticalLayout(icon, new Span(viewName));
		    verticalLayout.setSpacing(false);
		    verticalLayout.setPadding(false);

		    // Center the elements vertically and horizontally within the VerticalLayout
		    verticalLayout.getStyle().set("display", "flex")
		        .set("flex-direction", "column")
		        .set("align-items", "center")
		        .set("justify-content", "center")
		        .set("color", "white")
		        .set("font-weight", "normal")
		        .set("margin", "8px 0px");

		    link.add(verticalLayout);

		    return new Tab(link);
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