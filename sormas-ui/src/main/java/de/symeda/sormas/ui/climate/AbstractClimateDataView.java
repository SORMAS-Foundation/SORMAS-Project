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

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateSummaryDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Configuration › Climate data: one menu entry with two tabs, Observations and Backfill, each its own view. Both show the
 * read-only climate settings and call figures in a side column; the switcher between the tabs sits in the header.
 */
@SuppressWarnings("java:S110") // suppress sonar too many parents warning
public abstract class AbstractClimateDataView extends AbstractConfigurationView {

	private static final long serialVersionUID = -6131779003127547520L;

	private static final String MAIN_LOC = "main";
	private static final String SIDE_LOC = "side";

	/**
	 * Wide screens: filters and lists in 8 of 12 columns, settings and call figures beside them in 4; narrow screens: one
	 * below the other. The row and the main column take the full height, so a list can fill what is left below its filters
	 * and scroll inside itself.
	 */
	private static final String COLUMNS_TEMPLATE = "<div class='row' style='height: 100%'>"
		+ "<div class='col-lg-8 col-xs-12' style='height: 100%' location='" + MAIN_LOC + "'></div>"
		+ "<div class='col-lg-4 col-xs-12' location='" + SIDE_LOC + "'></div></div>";

	/** The filters and lists. */
	protected final VerticalLayout contentLayout;
	/** The settings and call figures, and anything else that describes the data rather than lists it. */
	protected final VerticalLayout sideLayout;
	protected final CustomLayout columns;
	private final ClimateSummaryStrip summaryStrip;
	private SubMenu menu;
	private ClimateSummaryDto summary;

	protected AbstractClimateDataView(String viewName) {

		super(viewName);

		// first in the header, so it sits left of each tab's own buttons, like the cases view switcher
		addHeaderComponent(createTabSwitcher(viewName));

		contentLayout = new VerticalLayout();
		contentLayout.setMargin(true);
		contentLayout.setSpacing(false);
		contentLayout.setSizeFull();
		contentLayout.setStyleName("crud-main-layout");

		summaryStrip = new ClimateSummaryStrip();
		sideLayout = new VerticalLayout(summaryStrip);
		sideLayout.setMargin(true);
		sideLayout.setSpacing(true);
		sideLayout.setWidthFull();

		columns = new CustomLayout();
		columns.setTemplateContents(COLUMNS_TEMPLATE);
		columns.addStyleName(CssStyles.ROOT_COMPONENT);
		columns.setSizeFull();
		columns.addComponent(contentLayout, MAIN_LOC);
		columns.addComponent(sideLayout, SIDE_LOC);

		addComponent(columns);
		setExpandRatio(columns, 1);
	}

	private static OptionGroup createTabSwitcher(String viewName) {

		OptionGroup switcher = new OptionGroup();
		switcher.setId("climateViewSwitcher");
		CssStyles.style(switcher, CssStyles.FORCE_CAPTION, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		switcher.addItem(ClimateObservationsView.VIEW_NAME);
		switcher.setItemCaption(ClimateObservationsView.VIEW_NAME, I18nProperties.getCaption(Captions.climateTabObservations));
		switcher.addItem(ClimateJobsView.VIEW_NAME);
		switcher.setItemCaption(ClimateJobsView.VIEW_NAME, I18nProperties.getCaption(Captions.climateTabBackfill));
		switcher.setValue(viewName);
		switcher.addValueChangeListener(e -> {
			String target = (String) e.getProperty().getValue();
			if (target != null && !target.equals(viewName)) {
				SormasUI.get().getNavigator().navigateTo(target);
			}
		});
		return switcher;
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
