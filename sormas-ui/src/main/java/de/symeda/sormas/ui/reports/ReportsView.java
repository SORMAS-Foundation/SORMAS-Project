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
package de.symeda.sormas.ui.reports;

import java.util.Date;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class ReportsView extends AbstractView {

	private static final long serialVersionUID = -226852255434803180L;

	public static final String VIEW_NAME = "reports";

	private Grid grid;
	private VerticalLayout gridLayout;
	private AbstractSelect yearFilter;
	private AbstractSelect epiWeekFilter;

	public ReportsView() {
		super(VIEW_NAME);

		if (UserRole.isNational(UserProvider.getCurrent().getUserRoles())) {
			grid = new WeeklyReportRegionsGrid();
		} else {
			grid = new WeeklyReportOfficersGrid();
		}

		grid.setHeightMode(HeightMode.UNDEFINED);

		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);

		addComponent(gridLayout);
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.addStyleName(CssStyles.VSPACE_3);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		EpiWeek prevEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
		int year = prevEpiWeek.getYear();
		int week = prevEpiWeek.getWeek();

		yearFilter = new ComboBox();
		yearFilter.setId(Strings.year);
		yearFilter.setWidth(200, Unit.PIXELS);
		yearFilter.setNullSelectionAllowed(false);
		yearFilter.addItems(DateHelper.getYearsToNow());
		yearFilter.select(year);
		yearFilter.setCaption(I18nProperties.getString(Strings.year));
		yearFilter.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		yearFilter.addValueChangeListener(e -> {
			updateEpiWeeks((int) e.getProperty().getValue(), (int) epiWeekFilter.getValue());
			reloadGrid();
		});
		filterLayout.addComponent(yearFilter);

		epiWeekFilter = new ComboBox();
		epiWeekFilter.setId(Strings.epiWeek);
		epiWeekFilter.setWidth(200, Unit.PIXELS);
		epiWeekFilter.setNullSelectionAllowed(false);
		updateEpiWeeks(year, week);
		epiWeekFilter.setCaption(I18nProperties.getString(Strings.epiWeek));
		epiWeekFilter.addValueChangeListener(e -> {
			reloadGrid();
		});
		filterLayout.addComponent(epiWeekFilter);

		Button lastWeekButton = ButtonHelper.createButtonWithCaption(
			Captions.dashboardLastWeek,
			String.format(I18nProperties.getCaption(Captions.dashboardLastWeek), DateHelper.getPreviousEpiWeek(new Date()).toString()),
			e -> {
				EpiWeek epiWeek = DateHelper.getPreviousEpiWeek(new Date());
				yearFilter.select(epiWeek.getYear());
				epiWeekFilter.select(epiWeek.getWeek());
			},
			CssStyles.FORCE_CAPTION);

		filterLayout.addComponent(lastWeekButton);

		Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setDescription(I18nProperties.getString(Strings.infoWeeklyReportsView), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);
		filterLayout.addComponent(infoLabel);
		filterLayout.setComponentAlignment(infoLabel, Alignment.MIDDLE_RIGHT);
		filterLayout.setExpandRatio(infoLabel, 1);

		return filterLayout;
	}

	private void updateEpiWeeks(int year, int week) {
		List<EpiWeek> epiWeekList = DateHelper.createEpiWeekList(year);
		for (EpiWeek epiWeek : epiWeekList) {
			epiWeekFilter.addItem(epiWeek.getWeek());
			epiWeekFilter.setItemCaption(epiWeek.getWeek(), epiWeek.toString());
		}
		epiWeekFilter.select(week);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		reloadGrid();
	}

	private void reloadGrid() {
		if (grid instanceof WeeklyReportRegionsGrid) {
			((WeeklyReportRegionsGrid) grid).reload((int) yearFilter.getValue(), (int) epiWeekFilter.getValue());
		} else {
			((WeeklyReportOfficersGrid) grid)
				.reload(UserProvider.getCurrent().getUser().getRegion(), (int) yearFilter.getValue(), (int) epiWeekFilter.getValue());
		}
	}
}
