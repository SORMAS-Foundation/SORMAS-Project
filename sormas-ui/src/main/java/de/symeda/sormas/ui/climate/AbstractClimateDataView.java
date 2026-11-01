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
 * read-only climate settings and call figures; the switcher between the tabs sits in the header.
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

		// first in the header, so it sits left of each tab's own buttons, like the cases view switcher
		addHeaderComponent(createTabSwitcher(viewName));

		summaryStrip = new ClimateSummaryStrip();
		contentLayout.addComponent(summaryStrip);

		addComponent(contentLayout);
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
