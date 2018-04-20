package de.symeda.sormas.ui.statistics;

import com.vaadin.navigator.Navigator;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.login.LoginHelper;

public class StatisticsController {
	
	public StatisticsController() {
		
	}
	
	public void registerViews(Navigator navigator) {
		navigator.addView(StatisticsView.VIEW_NAME, StatisticsView.class);
		navigator.addView(BasicStatisticsView.VIEW_NAME, BasicStatisticsView.class);
		if (LoginHelper.hasUserRight(UserRight.DATABASE_EXPORT_ACCESS)) {
			navigator.addView(DatabaseExportView.VIEW_NAME, DatabaseExportView.class);
		}
	}

}
