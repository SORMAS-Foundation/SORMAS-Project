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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.reports;

import java.util.Date;
import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.utils.AbstractView;
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

		if (UserRole.isNational(CurrentUser.getCurrent().getUserRoles())) {
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
		filterLayout.setSpacing(true);
		filterLayout.addStyleName(CssStyles.VSPACE_3);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		EpiWeek prevEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
		int year = prevEpiWeek.getYear();
		int week = prevEpiWeek.getWeek();

		yearFilter = new ComboBox();
		yearFilter.setWidth(200, Unit.PIXELS);
		yearFilter.addItems(DateHelper.getYearsToNow());
		yearFilter.select(year);
		yearFilter.setCaption("Year");
		yearFilter.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		yearFilter.addValueChangeListener(e -> {
			updateEpiWeeks((int) e.getProperty().getValue(), (int) epiWeekFilter.getValue());
			reloadGrid();
		});
		filterLayout.addComponent(yearFilter);

		epiWeekFilter = new ComboBox();
		epiWeekFilter.setWidth(200, Unit.PIXELS);
		updateEpiWeeks(year, week);
		epiWeekFilter.setCaption("Epi Week");
		epiWeekFilter.addValueChangeListener(e -> {
			reloadGrid();
		});
		filterLayout.addComponent(epiWeekFilter);

		Button lastWeekButton = new Button("Last week");
		lastWeekButton.addStyleName(CssStyles.FORCE_CAPTION);
		lastWeekButton.addClickListener(e -> {
			EpiWeek epiWeek = DateHelper.getPreviousEpiWeek(new Date());
			yearFilter.select(epiWeek.getYear());
			epiWeekFilter.select(epiWeek.getWeek());
		});
		filterLayout.addComponent(lastWeekButton);

		// this week will not have any reports
//		Button thisWeekButton = new Button("This week");
//		thisWeekButton.addStyleName(CssStyles.FORCE_CAPTION);
//		thisWeekButton.addClickListener(e -> {
//			EpiWeek epiWeek = DateHelper.getEpiWeek(new Date());
//			yearFilter.select(epiWeek.getYear());
//			epiWeekFilter.select(epiWeek.getWeek());
//		});
//		filterLayout.addComponent(thisWeekButton);
		
		Label infoLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setDescription("<b>Number of officer/informant reports</b> is the total number of reports that were submitted "
					+ "by the officers/informants associated with the displayed region or officer this week.<br/><br/>"
					+ "<b>Percentage</b> is the percentage of officers/informants that submitted their report for the "
					+ "respective week.<br/><br/>"
					+ "<b>Number of officers/informants zero reports</b> is the amount of zero reports, i.e. submitted reports "
					+ "with no cases. These are included in the total number of reports.<br/><br/>"
					+ "<b>Officer/Informant report submission</b> is either the date the report has been submitted at or "
					+ "a hint that no report has been submitted for this week yet.");
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
			((WeeklyReportOfficersGrid) grid).reload(CurrentUser.getCurrent().getUser().getRegion(),
					(int) yearFilter.getValue(), (int) epiWeekFilter.getValue());
		}
	}
}
