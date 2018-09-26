package de.symeda.sormas.ui.dashboard;

import com.vaadin.navigator.Navigator;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.login.LoginHelper;

public class DashboardController {

	public DashboardController() {
		
	}

	public void registerViews(Navigator navigator) {
		if (LoginHelper.hasUserRight(UserRight.DASHBOARD_SURVEILLANCE_ACCESS)) {
			navigator.addView(DashboardSurveillanceView.VIEW_NAME, DashboardSurveillanceView.class);
		}
		if (LoginHelper.hasUserRight(UserRight.DASHBOARD_CONTACT_ACCESS)) {
			navigator.addView(DashboardContactsView.VIEW_NAME, DashboardContactsView.class);
		}
	}
	
}
