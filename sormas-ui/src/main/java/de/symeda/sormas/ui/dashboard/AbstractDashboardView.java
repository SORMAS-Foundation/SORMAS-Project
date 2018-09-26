package de.symeda.sormas.ui.dashboard;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
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

	// Layouts and Components
	protected VerticalLayout dashboardLayout;
	protected DashboardFilterLayout filterLayout;
	
	public static final String I18N_PREFIX = "Dashboard";
	
	public static final String ROOT_VIEW_NAME = "dashboard";

	protected DashboardDataProvider dashboardDataProvider;

	protected AbstractDashboardView(String viewName, DashboardType dashboardType) {
		super(viewName);	
		
		addStyleName(DashboardCssStyles.DASHBOARD_SCREEN);

		dashboardDataProvider = new DashboardDataProvider();
		
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
			if (e.getProperty().getValue().equals(DashboardType.SURVEILLANCE)) {
				SormasUI.get().getNavigator().navigateTo(DashboardSurveillanceView.VIEW_NAME);
			} else {
				SormasUI.get().getNavigator().navigateTo(DashboardContactsView.VIEW_NAME);
			}
		});
		addHeaderComponent(dashboardSwitcher);

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
	
	public abstract void refreshDashboard();
	
	@Override
	public void enter(ViewChangeEvent event) {

	}


}
