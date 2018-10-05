package de.symeda.sormas.ui.dashboard;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public abstract class AbstractDashboardView extends AbstractView {

	public static final String I18N_PREFIX = "Dashboard";

	public static final String ROOT_VIEW_NAME = "dashboard";

	protected DashboardDataProvider dashboardDataProvider;

	protected VerticalLayout dashboardLayout;
	protected DashboardFilterLayout filterLayout;
	protected AbstractDashboardStatisticsComponent statisticsComponent;
	protected AbstractEpiCurveComponent epiCurveComponent;
	protected DashboardMapComponent mapComponent;
	protected HorizontalLayout epiCurveAndMapLayout;
	private VerticalLayout epiCurveLayout;
	private VerticalLayout mapLayout;

	protected AbstractDashboardView(String viewName, DashboardType dashboardType) {
		super(viewName);	

		addStyleName(DashboardCssStyles.DASHBOARD_SCREEN);

		dashboardDataProvider = new DashboardDataProvider();
		if (dashboardDataProvider.getDashboardType() == null) {
			dashboardDataProvider.setDashboardType(dashboardType);
		}

		OptionGroup dashboardSwitcher = new OptionGroup();
		CssStyles.style(dashboardSwitcher, CssStyles.FORCE_CAPTION, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		if (LoginHelper.hasUserRight(UserRight.DASHBOARD_SURVEILLANCE_ACCESS)) {
			dashboardSwitcher.addItem(DashboardType.SURVEILLANCE);
			dashboardSwitcher.setItemCaption(DashboardType.SURVEILLANCE, I18nProperties.getEnumCaption(DashboardType.SURVEILLANCE));
		}
		if (LoginHelper.hasUserRight(UserRight.DASHBOARD_CONTACT_ACCESS)) {
			dashboardSwitcher.addItem(DashboardType.CONTACTS);
			dashboardSwitcher.setItemCaption(DashboardType.CONTACTS, I18nProperties.getEnumCaption(DashboardType.CONTACTS));
		}
		dashboardSwitcher.setValue(dashboardType);
		dashboardSwitcher.addValueChangeListener(e -> {
			dashboardDataProvider.setDashboardType((DashboardType) e.getProperty().getValue());
			if (e.getProperty().getValue().equals(DashboardType.SURVEILLANCE)) {
				SormasUI.get().getNavigator().navigateTo(DashboardSurveillanceView.VIEW_NAME);
			} else {
				SormasUI.get().getNavigator().navigateTo(DashboardContactsView.VIEW_NAME);
			}
		});
		addHeaderComponent(dashboardSwitcher);
		
		// Hide the dashboard switcher if only one dashboard is accessible to the user
		if (dashboardSwitcher.size() <= 1) {
			dashboardSwitcher.setVisible(false);
		}

		// Dashboard layout
		dashboardLayout = new VerticalLayout();
		dashboardLayout.setSpacing(false);
		dashboardLayout.setSizeFull();
		dashboardLayout.setStyleName("crud-main-layout");

		// Filter bar
		filterLayout = new DashboardFilterLayout(this, dashboardDataProvider);
		dashboardLayout.addComponent(filterLayout);

		addComponent(dashboardLayout);
	}

	protected HorizontalLayout createEpiCurveAndMapLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addStyleName(DashboardCssStyles.CURVE_AND_MAP_LAYOUT);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(false);

		// Epi curve layout
		epiCurveLayout = createEpiCurveLayout();
		layout.addComponent(epiCurveLayout);

		// Map layout	
		mapLayout = createMapLayout();
		layout.addComponent(mapLayout);

		return layout;
	}

	protected VerticalLayout createEpiCurveLayout() {
		if (epiCurveComponent == null) {
			throw new UnsupportedOperationException("EpiCurveComponent needs to be initialized before calling createEpiCurveLayout");
		}

		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeight(400, Unit.PIXELS);

		epiCurveComponent.setSizeFull();

		layout.addComponent(epiCurveComponent);
		layout.setExpandRatio(epiCurveComponent, 1);

		epiCurveComponent.setExpandListener(e -> {
			dashboardLayout.removeComponent(statisticsComponent);
			epiCurveAndMapLayout.removeComponent(mapLayout);
			AbstractDashboardView.this.setHeight(100, Unit.PERCENTAGE);
			epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
			epiCurveLayout.setSizeFull();			
		});

		epiCurveComponent.setCollapseListener(e -> {
			dashboardLayout.addComponent(statisticsComponent, 1);
			epiCurveAndMapLayout.addComponent(mapLayout, 1);
			epiCurveLayout.setHeight(400, Unit.PIXELS);
			AbstractDashboardView.this.setHeightUndefined();
			epiCurveAndMapLayout.setHeightUndefined();
		});

		return layout;
	}

	protected VerticalLayout createMapLayout() {
		if (mapComponent == null) {
			throw new UnsupportedOperationException("MapComponent needs to be initialized before calling createMapLayout");
		}
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeight(400, Unit.PIXELS);

		mapComponent.setSizeFull();

		layout.addComponent(mapComponent);
		layout.setExpandRatio(mapComponent, 1);

		mapComponent.setExpandListener(e -> {
			dashboardLayout.removeComponent(statisticsComponent);
			epiCurveAndMapLayout.removeComponent(epiCurveLayout);
			AbstractDashboardView.this.setHeight(100, Unit.PERCENTAGE);
			epiCurveAndMapLayout.setHeight(100, Unit.PERCENTAGE);
			mapLayout.setSizeFull();
		});

		mapComponent.setCollapseListener(e -> {
			dashboardLayout.addComponent(statisticsComponent, 1);
			epiCurveAndMapLayout.addComponent(epiCurveLayout, 0);
			mapLayout.setHeight(400, Unit.PIXELS);
			AbstractDashboardView.this.setHeightUndefined();
			epiCurveAndMapLayout.setHeightUndefined();
		});

		return layout;
	}

	public void refreshDashboard() {		
		dashboardDataProvider.refreshData();

		// Updates statistics
		statisticsComponent.updateStatistics(dashboardDataProvider.getDisease());

		// Update cases and contacts shown on the map
		mapComponent.refreshMap();

		// Epi curve chart has to be created again due to a canvas resizing issue when simply refreshing the component
		epiCurveComponent.clearAndFillEpiCurveChart();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		refreshDashboard();
	}


}
