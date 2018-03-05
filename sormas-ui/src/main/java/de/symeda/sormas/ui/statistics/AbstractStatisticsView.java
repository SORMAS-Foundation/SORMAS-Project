package de.symeda.sormas.ui.statistics;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public class AbstractStatisticsView extends AbstractSubNavigationView {

	public final static String I18N_PREFIX = "Statistics";
	
	protected AbstractStatisticsView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubNavigationMenu menu, Label infoLabel, Label infoLabelSub, String params) {
		menu.removeAllViews();
		menu.addView(StatisticsView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(I18N_PREFIX, StatisticsView.VIEW_NAME), params);
		if (LoginHelper.hasUserRight(UserRight.DATABASE_EXPORT_ACCESS)) {
			menu.addView(DatabaseExportView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(I18N_PREFIX, "database-export"), params);
		}
	
		hideInfoLabel();
	}
	
}
