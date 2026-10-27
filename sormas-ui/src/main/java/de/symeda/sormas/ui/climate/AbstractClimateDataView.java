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

package de.symeda.sormas.ui.climate;

import java.util.Arrays;
import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateSummaryDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Configuration › Climate data: one menu entry with two tabs, Observations and Backfill, each its own view. Both show the
 * read-only climate settings and call figures at the top.
 */
@SuppressWarnings("java:S110") // suppress sonar too many parents warning
public abstract class AbstractClimateDataView extends AbstractConfigurationView {

	private static final long serialVersionUID = -6131779003127547520L;

	protected final VerticalLayout contentLayout;
	private final ClimateSummaryStrip summaryStrip;
	private SubMenu menu;
	private ClimateSummaryDto summary;

	protected AbstractClimateDataView(String viewName) {

		super(viewName);

		contentLayout = new VerticalLayout();
		contentLayout.setMargin(true);
		contentLayout.setSpacing(false);
		contentLayout.setSizeFull();
		contentLayout.setStyleName("crud-main-layout");
		contentLayout.addComponent(createTabBar(viewName));

		summaryStrip = new ClimateSummaryStrip();
		contentLayout.addComponent(summaryStrip);

		addComponent(contentLayout);
	}

	private static HorizontalLayout createTabBar(String viewName) {

		HorizontalLayout tabBar = new HorizontalLayout();
		tabBar.setSpacing(true);
		tabBar.setMargin(false);
		tabBar.addStyleName(CssStyles.VSPACE_3);

		Button observations = ButtonHelper.createButton(
			Captions.climateTabObservations,
			e -> SormasUI.get().getNavigator().navigateTo(ClimateObservationsView.VIEW_NAME),
			ValoTheme.BUTTON_BORDERLESS,
			CssStyles.BUTTON_FILTER);
		Button backfill = ButtonHelper.createButton(
			Captions.climateTabBackfill,
			e -> SormasUI.get().getNavigator().navigateTo(ClimateJobsView.VIEW_NAME),
			ValoTheme.BUTTON_BORDERLESS,
			CssStyles.BUTTON_FILTER);
		List<Button> tabs = Arrays.asList(observations, backfill);
		CssStyles.styleSectionFilterButton(ClimateJobsView.VIEW_NAME.equals(viewName) ? backfill : observations, tabs);
		tabBar.addComponents(observations, backfill);
		return tabBar;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		refreshSummary();
	}

	/**
	 * Reads the settings and call figures again, after something that changes them (a fetch or backfill started).
	 */
	protected void refreshSummary() {
		summary = FacadeProvider.getClimateJobFacade().getSummary();
		summaryStrip.update(summary);
	}

	/**
	 * @return the settings and call figures read when the view was entered
	 */
	protected ClimateSummaryDto getSummary() {
		return summary;
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		super.refreshMenu(menu, params);
		this.menu = menu;
	}

	@Override
	public void selectInMenu() {
		// both tabs belong to the one Climate data entry
		if (menu != null) {
			menu.setActiveView(ClimateObservationsView.VIEW_NAME);
		}
	}
}
