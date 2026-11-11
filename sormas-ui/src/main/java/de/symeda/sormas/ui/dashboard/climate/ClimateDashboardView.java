/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.dashboard.climate;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.IgnoreCancelDownloader;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.utils.ButtonHelper;

/**
 * The Climate dashboard (decision 118): climate-sensitive diseases against the weekly climate of a region or district.
 * Read-only; the header has only the dashboard switcher (decision 139).
 */
public class ClimateDashboardView extends AbstractDashboardView {

	private static final long serialVersionUID = 4162059339720528112L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/climate";

	private final ClimateDashboardLayout layout;

	public ClimateDashboardView() {

		super(VIEW_NAME);

		dashboardSwitcher.setValue(DashboardType.CLIMATE);
		dashboardSwitcher.addValueChangeListener(this::navigateToDashboardView);

		dashboardLayout.setHeightUndefined();
		layout = new ClimateDashboardLayout(UserProvider.getCurrent().getUser(), UserProvider.getCurrent().getJurisdictionLevel());
		dashboardLayout.addComponent(layout);

		if (UiUtil.permitted(FeatureType.CLIMATE_MANAGEMENT, UserRight.CLIMATE_EXPORT)) {
			// the data behind the applied filters, never shifted by the lag (decision 128); a link under the chart, right-aligned, shown with it (decisions 139 and 142)
			Button export = ButtonHelper.createIconButton(Captions.climateDashboardExport, VaadinIcons.TABLE, null, ValoTheme.BUTTON_LINK);
			export.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
			new IgnoreCancelDownloader(ClimateDashboardExport.resource(layout::getExportRows, layout::selectedDiseases)).extend(export);
			layout.addBelowChart(export);
		}
	}

	public static boolean permitted() {
		return UiUtil.permitted(FeatureType.CLIMATE_MANAGEMENT, UserRight.DASHBOARD_CLIMATE_VIEW);
	}

	@Override
	public void refreshDashboard() {
		layout.refresh();
	}

	ClimateDashboardLayout getLayout() {
		return layout;
	}
}
