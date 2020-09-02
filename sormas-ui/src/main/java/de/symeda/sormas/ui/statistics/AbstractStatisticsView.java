/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.statistics;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public class AbstractStatisticsView extends AbstractSubNavigationView {

	public static final String ROOT_VIEW_NAME = "statistics";

	protected AbstractStatisticsView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubMenu menu, Label infoLabel, Label infoLabelSub, String params) {
		menu.removeAllViews();
		menu.addView(StatisticsView.VIEW_NAME, I18nProperties.getCaption(Captions.statisticsStatistics), params);
		if (UserProvider.getCurrent().hasUserRight(UserRight.DATABASE_EXPORT_ACCESS)) {
			menu.addView(DatabaseExportView.VIEW_NAME, I18nProperties.getCaption(Captions.statisticsDatabaseExport), params);
		}

		hideInfoLabel();
	}
}
