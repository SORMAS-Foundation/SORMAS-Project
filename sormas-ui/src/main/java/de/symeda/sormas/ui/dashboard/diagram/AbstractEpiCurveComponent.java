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
package de.symeda.sormas.ui.dashboard.diagram;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.highcharts.HighChart;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public abstract class AbstractEpiCurveComponent extends VerticalLayout {

	// Components
	protected final DashboardDataProvider dashboardDataProvider;
	protected final HighChart epiCurveChart;
	protected Label epiCurveLabel;

	// Others
	protected EpiCurveGrouping epiCurveGrouping;
	private boolean showMinimumEntries;
	private Consumer<Boolean> externalExpandListener;

	public AbstractEpiCurveComponent(DashboardDataProvider dashboardDataProvider) {

		this.dashboardDataProvider = dashboardDataProvider;

		setSpacing(false);
		setSizeFull();
		setMargin(true);

		epiCurveChart = new HighChart();
		epiCurveChart.setSizeFull();
		//epiCurveChart.setHeight(0, Unit.PIXELS);
		epiCurveGrouping = EpiCurveGrouping.WEEK;
		showMinimumEntries = true;

		addComponent(createHeader());
		addComponent(epiCurveChart);
		addComponent(createFooter());
		setExpandRatio(epiCurveChart, 1);

		//clearAndFillEpiCurveChart();
	}

	public void setExpandListener(Consumer<Boolean> listener) {
		externalExpandListener = listener;
	}

	private HorizontalLayout createHeader() {

		HorizontalLayout epiCurveHeaderLayout = new HorizontalLayout();
		epiCurveHeaderLayout.setWidth(100, Unit.PERCENTAGE);
		epiCurveHeaderLayout.setSpacing(true);
		CssStyles.style(epiCurveHeaderLayout, CssStyles.VSPACE_4);

		epiCurveLabel = new Label(I18nProperties.getString(Strings.headingEpiCurve));
		epiCurveLabel.setSizeUndefined();
		CssStyles.style(epiCurveLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);

		epiCurveHeaderLayout.addComponent(epiCurveLabel);
		epiCurveHeaderLayout.setComponentAlignment(epiCurveLabel, Alignment.BOTTOM_LEFT);
		epiCurveHeaderLayout.setExpandRatio(epiCurveLabel, 1);

		// "Expand" and "Collapse" buttons
		Button expandEpiCurveButton =
			ButtonHelper.createIconButtonWithCaption("expandEpiCurve", "", VaadinIcons.EXPAND, null, CssStyles.BUTTON_SUBTLE, CssStyles.VSPACE_NONE);
		Button collapseEpiCurveButton = ButtonHelper
			.createIconButtonWithCaption("collapseEpiCurve", "", VaadinIcons.COMPRESS, null, CssStyles.BUTTON_SUBTLE, CssStyles.VSPACE_NONE);

		expandEpiCurveButton.addClickListener(e -> {
			externalExpandListener.accept(true);
			epiCurveHeaderLayout.removeComponent(expandEpiCurveButton);
			epiCurveHeaderLayout.addComponent(collapseEpiCurveButton);
			epiCurveHeaderLayout.setComponentAlignment(collapseEpiCurveButton, Alignment.MIDDLE_RIGHT);
		});

		collapseEpiCurveButton.addClickListener(e -> {
			externalExpandListener.accept(false);
			epiCurveHeaderLayout.removeComponent(collapseEpiCurveButton);
			epiCurveHeaderLayout.addComponent(expandEpiCurveButton);
			epiCurveHeaderLayout.setComponentAlignment(expandEpiCurveButton, Alignment.MIDDLE_RIGHT);
		});

		epiCurveHeaderLayout.addComponent(expandEpiCurveButton);
		epiCurveHeaderLayout.setComponentAlignment(expandEpiCurveButton, Alignment.MIDDLE_RIGHT);

		return epiCurveHeaderLayout;
	}

	private HorizontalLayout createFooter() {

		HorizontalLayout epiCurveFooterLayout = new HorizontalLayout();
		epiCurveFooterLayout.setWidth(100, Unit.PERCENTAGE);
		epiCurveFooterLayout.setSpacing(true);
		CssStyles.style(epiCurveFooterLayout, CssStyles.VSPACE_4);

		// Grouping
		VerticalLayout groupingLayout = new VerticalLayout();
		{
			groupingLayout.setMargin(true);
			groupingLayout.setSizeUndefined();

			// Grouping option group
			OptionGroup groupingSelect = new OptionGroup();
			groupingSelect.setWidth(100, Unit.PERCENTAGE);
			groupingSelect.addItems((Object[]) EpiCurveGrouping.values());
			groupingSelect.setValue(epiCurveGrouping);
			groupingSelect.addValueChangeListener(e -> {
				epiCurveGrouping = (EpiCurveGrouping) e.getProperty().getValue();
				clearAndFillEpiCurveChart();
			});
			groupingLayout.addComponent(groupingSelect);

			// "Always show at least 7 entries" checkbox
			CheckBox minimumEntriesCheckbox = new CheckBox(I18nProperties.getCaption(Captions.dashboardShowMinimumEntries));
			CssStyles.style(minimumEntriesCheckbox, CssStyles.VSPACE_NONE);
			minimumEntriesCheckbox.setValue(showMinimumEntries);
			minimumEntriesCheckbox.addValueChangeListener(e -> {
				showMinimumEntries = (boolean) e.getProperty().getValue();
				clearAndFillEpiCurveChart();
			});
			groupingLayout.addComponent(minimumEntriesCheckbox);
		}

		PopupButton groupingDropdown = ButtonHelper.createPopupButton(Captions.dashboardGrouping, groupingLayout, CssStyles.BUTTON_SUBTLE);

		epiCurveFooterLayout.addComponent(groupingDropdown);
		epiCurveFooterLayout.setComponentAlignment(groupingDropdown, Alignment.MIDDLE_RIGHT);
		epiCurveFooterLayout.setExpandRatio(groupingDropdown, 1);

		// Epi curve mode
		AbstractComponent epiCurveModeSelector = createEpiCurveModeSelector();
		epiCurveFooterLayout.addComponent(epiCurveModeSelector);
		epiCurveFooterLayout.setComponentAlignment(epiCurveModeSelector, Alignment.MIDDLE_RIGHT);
		epiCurveFooterLayout.setExpandRatio(epiCurveModeSelector, 0);

		return epiCurveFooterLayout;
	}

	protected abstract AbstractComponent createEpiCurveModeSelector();

	public abstract void clearAndFillEpiCurveChart();

	/**
	 * Builds a list that contains an object for each day, week or month between the
	 * from and to dates. Additional previous days, weeks or months might be added
	 * when showMinimumEntries is true.
	 */
	protected List<Date> buildListOfFilteredDates() {

		List<Date> filteredDates = new ArrayList<>();
		Date fromDate = DateHelper.getStartOfDay(dashboardDataProvider.getFromDate());
		Date toDate = DateHelper.getEndOfDay(dashboardDataProvider.getToDate());
		Date currentDate;

		if (epiCurveGrouping == EpiCurveGrouping.DAY) {
			if (!showMinimumEntries || DateHelper.getDaysBetween(fromDate, toDate) >= 7) {
				currentDate = fromDate;
			} else {
				currentDate = DateHelper.subtractDays(toDate, 6);
			}
			while (!currentDate.after(toDate)) {
				filteredDates.add(currentDate);
				currentDate = DateHelper.addDays(currentDate, 1);
			}
		} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
			if (!showMinimumEntries || DateHelper.getWeeksBetween(fromDate, toDate) >= 7) {
				currentDate = fromDate;
			} else {
				currentDate = DateHelper.subtractWeeks(toDate, 6);
			}
			while (!currentDate.after(toDate)) {
				filteredDates.add(currentDate);
				currentDate = DateHelper.addWeeks(currentDate, 1);
			}
		} else if (epiCurveGrouping == EpiCurveGrouping.MONTH) {
			if (!showMinimumEntries || DateHelper.getMonthsBetween(fromDate, toDate) >= 7) {
				currentDate = fromDate;
			} else {
				currentDate = DateHelper.subtractMonths(toDate, 6);
			}
			while (!currentDate.after(toDate)) {
				filteredDates.add(currentDate);
				currentDate = DateHelper.addMonths(currentDate, 1);
			}
		}

		return filteredDates;
	}
}
