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
package de.symeda.sormas.ui.dashboard;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;

@SuppressWarnings("serial")
public class DashboardFilterLayout extends HorizontalLayout {

	private AbstractDashboardView dashboardView;
	private DashboardDataProvider dashboardDataProvider;	

	private Label infoLabel;

	// Filters
	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private ComboBox diseaseFilter;
	private PopupButton customButton;
	private Set<Button> dateFilterButtons;

	public DashboardFilterLayout(AbstractDashboardView dashboardView, DashboardDataProvider dashboardDataProvider) {
		this.dashboardView = dashboardView;
		this.dashboardDataProvider = dashboardDataProvider;
		this.regionFilter = new ComboBox();
		this.districtFilter = new ComboBox();
		this.diseaseFilter = new ComboBox();
		dateFilterButtons = new HashSet<>();

		setSpacing(true);
		setSizeUndefined();
		setMargin(new MarginInfo(true, true, false, true));

		createRegionAndDistrictFilter();
		//createDiseaseFilter();
		createDateFilters();
	}

	private void createRegionAndDistrictFilter() {
		// Region filter
		if (UserProvider.getCurrent().getUser().getRegion() == null) {
			regionFilter.setWidth(200, Unit.PIXELS);
			regionFilter.setInputPrompt(I18nProperties.getPrefixCaption(AbstractDashboardView.I18N_PREFIX, "region"));
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
			regionFilter.addValueChangeListener(e -> {
				dashboardDataProvider.setRegion((RegionReferenceDto) regionFilter.getValue());
				dashboardView.refreshDashboard();
			});
			regionFilter.setCaption(I18nProperties.getPrefixCaption(AbstractDashboardView.I18N_PREFIX, "region"));
			addComponent(regionFilter);
			dashboardDataProvider.setRegion((RegionReferenceDto) regionFilter.getValue());
		}

		// District filter
		if (UserProvider.getCurrent().getUser().getRegion() != null && UserProvider.getCurrent().getUser().getDistrict() == null) {
			districtFilter.setWidth(200, Unit.PIXELS);
			districtFilter.setInputPrompt(I18nProperties.getPrefixCaption(AbstractDashboardView.I18N_PREFIX, "district"));
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(UserProvider.getCurrent().getUser().getRegion().getUuid()));
			districtFilter.addValueChangeListener(e -> {
				dashboardDataProvider.setDistrict((DistrictReferenceDto) districtFilter.getValue());
				dashboardView.refreshDashboard();
			});
			districtFilter.setCaption(I18nProperties.getPrefixCaption(AbstractDashboardView.I18N_PREFIX, "district"));
			addComponent(districtFilter);
			dashboardDataProvider.setDistrict((DistrictReferenceDto) districtFilter.getValue());
		}
	}

	private void createDiseaseFilter() {
		diseaseFilter.setWidth(200, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getPrefixCaption(AbstractDashboardView.I18N_PREFIX, "disease"));
		if (dashboardDataProvider.getDashboardType() == DashboardType.CONTACTS) {
			diseaseFilter.addItems(DiseaseHelper.getAllDiseasesWithFollowUp());
		} else {
			diseaseFilter.addItems((Object[]) Disease.values());
		}
		diseaseFilter.addValueChangeListener(e -> {
			dashboardDataProvider.setDisease((Disease) diseaseFilter.getValue());
			dashboardView.refreshDashboard();
		});
		diseaseFilter.setCaption(I18nProperties.getPrefixCaption(AbstractDashboardView.I18N_PREFIX, "disease"));
		addComponent(diseaseFilter);
	}

	private void createDateFilters() {
		HorizontalLayout dateFilterLayout = new HorizontalLayout();
		dateFilterLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
		dateFilterLayout.setSpacing(true);
		addComponent(dateFilterLayout);
		Date now = new Date();

		// Date filters
		Button todayButton = new Button("Today");
		initializeDateFilterButton(todayButton);
		todayButton.addClickListener(e -> {
			setDateFilter(DateHelper.getStartOfDay(now), DateHelper.getEndOfDay(now));
			dashboardView.refreshDashboard();
		});

		Button yesterdayButton = new Button("Yesterday");
		initializeDateFilterButton(yesterdayButton);
		yesterdayButton.addClickListener(e -> {
			setDateFilter(DateHelper.getStartOfDay(DateHelper.subtractDays(now, 1)),
					DateHelper.getEndOfDay(DateHelper.subtractDays(now, 1)));
			dashboardView.refreshDashboard();
		});

		Button thisWeekButton = new Button("This week");
		initializeDateFilterButton(thisWeekButton);
		thisWeekButton.addClickListener(e -> {
			setDateFilter(DateHelper.getStartOfWeek(now), DateHelper.getEndOfWeek(now));
			dashboardView.refreshDashboard();
		});
		CssStyles.style(thisWeekButton, CssStyles.BUTTON_FILTER_DARK);
		CssStyles.removeStyles(thisWeekButton, CssStyles.BUTTON_FILTER_LIGHT);

		Button lastWeekButton = new Button("Last week");
		initializeDateFilterButton(lastWeekButton);
		lastWeekButton.addClickListener(e -> {
			setDateFilter(DateHelper.getStartOfWeek(DateHelper.subtractWeeks(now, 1)),
					DateHelper.getEndOfWeek(DateHelper.subtractWeeks(now, 1)));
			dashboardView.refreshDashboard();
		});

		Button thisYearButton = new Button("This year");
		initializeDateFilterButton(thisYearButton);
		thisYearButton.addClickListener(e -> {
			setDateFilter(DateHelper.getStartOfYear(now), DateHelper.getEndOfYear(now));
			dashboardView.refreshDashboard();
		});

		Button lastYearButton = new Button("Last year");
		initializeDateFilterButton(lastYearButton);
		lastYearButton.addClickListener(e -> {
			setDateFilter(DateHelper.getStartOfYear(DateHelper.subtractYears(now, 1)),
					DateHelper.getEndOfYear(DateHelper.subtractYears(now, 1)));
			dashboardView.refreshDashboard();
		});

		customButton = new PopupButton("Custom");
		initializeDateFilterButton(customButton);

		// Custom filter
		HorizontalLayout customDateFilterLayout = new HorizontalLayout();
		customDateFilterLayout.setSpacing(true);
		customDateFilterLayout.setMargin(true);

		// 'Apply custom filter' button
		Button applyButton = new Button("Apply custom filter");
		CssStyles.style(applyButton, CssStyles.FORCE_CAPTION, ValoTheme.BUTTON_PRIMARY);

		// Date & Epi Week filter
		EpiWeekAndDateFilterComponent weekAndDateFilter = new EpiWeekAndDateFilterComponent(applyButton, true, true, false);
		customDateFilterLayout.addComponent(weekAndDateFilter);
		dashboardDataProvider.setDateFilterOption((DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue());
		dashboardDataProvider.setFromDate(DateHelper.getEpiWeekStart((EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue()));
		dashboardDataProvider.setToDate(DateHelper.getEpiWeekEnd((EpiWeek) weekAndDateFilter.getWeekToFilter().getValue()));

		customDateFilterLayout.addComponent(applyButton);

		// Apply button listener
		applyButton.addClickListener(e -> {
			DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
			Date fromDate = null;
			Date toDate = null;
			EpiWeek fromWeek = null;
			EpiWeek toWeek = null;
			dashboardDataProvider.setDateFilterOption(dateFilterOption);
			if (dateFilterOption == DateFilterOption.DATE) {
				fromDate = weekAndDateFilter.getDateFromFilter().getValue();
				dashboardDataProvider.setFromDate(fromDate);
				toDate = weekAndDateFilter.getDateToFilter().getValue();
				dashboardDataProvider.setToDate(toDate);
			} else {
				fromWeek = (EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue();
				dashboardDataProvider.setFromDate(DateHelper.getEpiWeekStart(fromWeek));
				toWeek = (EpiWeek) weekAndDateFilter.getWeekToFilter().getValue();
				dashboardDataProvider.setToDate(DateHelper.getEpiWeekEnd(toWeek));
			}

			if ((fromDate != null && toDate != null) || (fromWeek != null && toWeek != null)) {
				changeDateFilterButtonsStyles(customButton);
				dashboardView.refreshDashboard();
				if (dateFilterOption == DateFilterOption.DATE) {
					customButton.setCaption(DateHelper.formatLocalShortDate(fromDate) + " - " + DateHelper.formatLocalShortDate(toDate));
				} else {
					customButton.setCaption(fromWeek.toShortString() + " - " + toWeek.toShortString());
				}
			} else {
				if (dateFilterOption == DateFilterOption.DATE) {
					new Notification("Missing date filter", "Please fill in both date filter fields", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				} else {
					new Notification("Missing epi week filter", "Please fill in both epi week filter fields", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				}
			}
		});

		customButton.setContent(customDateFilterLayout);

		dateFilterLayout.addComponents(todayButton, yesterdayButton, thisWeekButton, lastWeekButton, thisYearButton, lastYearButton, customButton);

		infoLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);
		addComponent(infoLabel);
		setComponentAlignment(infoLabel, Alignment.MIDDLE_RIGHT);
	}

	private void initializeDateFilterButton(Button button) {
		if (button != customButton) {
			button.addClickListener(e -> {
				changeDateFilterButtonsStyles(button);
			});
		}
		CssStyles.style(button, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT, CssStyles.FORCE_CAPTION);
		dateFilterButtons.add(button);
	}

	private void changeDateFilterButtonsStyles(Button activeFilterButton) {
		CssStyles.style(activeFilterButton, CssStyles.BUTTON_FILTER_DARK);
		CssStyles.removeStyles(activeFilterButton, CssStyles.BUTTON_FILTER_LIGHT);
		if (customButton != activeFilterButton) {
			customButton.setCaption("Custom");
		}

		dateFilterButtons.forEach(b -> {
			if (b != activeFilterButton) {
				CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
				CssStyles.removeStyles(b, CssStyles.BUTTON_FILTER_DARK);
			}
		});
	}

	private void setDateFilter(Date from, Date to) {
		dashboardDataProvider.setDateFilterOption(DateFilterOption.DATE);
		dashboardDataProvider.setFromDate(from);
		dashboardDataProvider.setToDate(to);
	}

	public void setInfoLabelText(String text) {
		infoLabel.setDescription(text);
	}

}
