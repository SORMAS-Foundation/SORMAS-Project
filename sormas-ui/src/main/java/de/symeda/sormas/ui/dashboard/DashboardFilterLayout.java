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
import java.util.function.Consumer;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
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
	private PopupButton btnCurrentPeriod;
	private PopupButton btnComparisonPeriod;
	private Set<Button> dateFilterButtons;
	private Set<Button> dateComparisonButtons;

	// Buttons
	private Button btnShowCustomPeriod;
	private Button btnToday;
	private Button btnYesterday;
	private Button btnThisWeek;
	private Button btnLastWeek;
	private Button btnThisYear;
	private Button btnPeriodBefore;
	private Button btnPeriodLastYear;
	private Button activeComparisonButton;
	
	private HorizontalLayout customDateFilterLayout;
	
	private Runnable dateFilterChangeCallback;
	private Consumer<Boolean> diseaseFilterChangeCallback;

	public DashboardFilterLayout(AbstractDashboardView dashboardView, DashboardDataProvider dashboardDataProvider) {
		this.dashboardView = dashboardView;
		this.dashboardDataProvider = dashboardDataProvider;
		this.regionFilter = new ComboBox();
		this.districtFilter = new ComboBox();
		this.diseaseFilter = new ComboBox();
		dateFilterButtons = new HashSet<>();
		dateComparisonButtons = new HashSet<>();

		setSpacing(true);
		setSizeUndefined();
		setMargin(new MarginInfo(true, true, false, true));

		createDateFilters();
		createRegionAndDistrictFilter();
		if (dashboardDataProvider.getDashboardType() == DashboardType.CONTACTS) {
			createDiseaseFilter();
		}
	}

	private void createRegionAndDistrictFilter() {
		// Region filter
		if (UserProvider.getCurrent().getUser().getRegion() == null) {
			regionFilter.setWidth(200, Unit.PIXELS);
			regionFilter.setInputPrompt(I18nProperties.getString(Strings.promptRegion));
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
			regionFilter.addValueChangeListener(e -> {
				dashboardDataProvider.setRegion((RegionReferenceDto) regionFilter.getValue());
				dashboardView.refreshDashboard();
			});
			// save height
			// regionFilter.setCaption(I18nProperties.getString(Strings.entityRegion));
			addComponent(regionFilter);
			dashboardDataProvider.setRegion((RegionReferenceDto) regionFilter.getValue());
		}

		// District filter
		if (UserProvider.getCurrent().getUser().getRegion() != null && UserProvider.getCurrent().getUser().getDistrict() == null) {
			districtFilter.setWidth(200, Unit.PIXELS);
			districtFilter.setInputPrompt(I18nProperties.getString(Strings.promptDistrict));
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(UserProvider.getCurrent().getUser().getRegion().getUuid()));
			districtFilter.addValueChangeListener(e -> {
				dashboardDataProvider.setDistrict((DistrictReferenceDto) districtFilter.getValue());
				dashboardView.refreshDashboard();
			});
			// save height
			//districtFilter.setCaption(I18nProperties.getString(Strings.entityDistrict));
			addComponent(districtFilter);
			dashboardDataProvider.setDistrict((DistrictReferenceDto) districtFilter.getValue());
		}
	}

	private void createDiseaseFilter() {
		diseaseFilter.setWidth(200, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getString(Strings.promptDisease));
		if (dashboardDataProvider.getDashboardType() == DashboardType.CONTACTS) {
			diseaseFilter.addItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseasesWithFollowUp(true, true, true).toArray());
			diseaseFilter.setValue(dashboardDataProvider.getDisease());
		} else {
			diseaseFilter.addItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true).toArray());
		}
		diseaseFilter.addValueChangeListener(e -> {
			if (diseaseFilterChangeCallback != null) {
				diseaseFilterChangeCallback.accept(diseaseFilter.getValue() != null);
			}
			dashboardDataProvider.setDisease((Disease) diseaseFilter.getValue());
			dashboardView.refreshDashboard();
		});
		addComponent(diseaseFilter);
	}

	private void createDateFilters() {
		HorizontalLayout dateFilterLayout = new HorizontalLayout();
		dateFilterLayout.setSpacing(true);
		CssStyles.style(dateFilterLayout, CssStyles.VSPACE_3);
		addComponent(dateFilterLayout);

		btnCurrentPeriod = new PopupButton();
		CssStyles.style(btnCurrentPeriod, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);

		Label lblComparedTo = new Label(I18nProperties.getCaption(Captions.dashboardComparedTo));
		CssStyles.style(lblComparedTo, CssStyles.VSPACE_TOP_4, CssStyles.LABEL_BOLD);

		btnComparisonPeriod = new PopupButton();
		CssStyles.style(btnComparisonPeriod, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);

		btnCurrentPeriod.setContent(new VerticalLayout(createDateFilterButtonsLayout(), createCustomDateFilterLayout()));
		btnComparisonPeriod.setContent(createDateComparisonButtonsLayout());

		dateFilterLayout.addComponents(btnCurrentPeriod, lblComparedTo, btnComparisonPeriod);

		infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);
		addComponent(infoLabel);
		setComponentAlignment(infoLabel, Alignment.TOP_RIGHT);

		// Set initial date filter
		CssStyles.style(btnThisWeek, CssStyles.BUTTON_FILTER_DARK);
		CssStyles.removeStyles(btnThisWeek, CssStyles.BUTTON_FILTER_LIGHT);
		CssStyles.style(btnPeriodBefore, CssStyles.BUTTON_FILTER_DARK);
		CssStyles.removeStyles(btnPeriodBefore, CssStyles.BUTTON_FILTER_LIGHT);
		activeComparisonButton = btnPeriodBefore;
		setDateFilter(DateHelper.getStartOfWeek(new Date()), new Date());
		updateComparisonButtons(DateFilterType.THIS_WEEK, DateHelper.getStartOfWeek(new Date()), new Date(), false);
		btnCurrentPeriod.setCaption(btnThisWeek.getCaption());
	}

	private HorizontalLayout createDateFilterButtonsLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);

		// Date filters
		btnShowCustomPeriod = new Button(I18nProperties.getCaption(Captions.dashboardCustom));
		initializeDateFilterButton(btnShowCustomPeriod, dateFilterButtons);

		btnToday = new Button(String.format(I18nProperties.getCaption(Captions.dashboardToday), DateHelper.formatLocalDate(new Date())));
		initializeDateFilterButton(btnToday, dateFilterButtons);
		btnToday.addClickListener(e -> {
			Date now = new Date();
			Date from = DateHelper.getStartOfDay(now);
			Date to = now;
			setDateFilter(from, to);
			btnCurrentPeriod.setCaption(btnToday.getCaption());
			updateComparisonButtons(DateFilterType.TODAY, from, to, false);
			dashboardView.refreshDashboard();
		});

		btnYesterday = new Button(String.format(I18nProperties.getCaption(Captions.dashboardYesterday), DateHelper.formatLocalDate(DateHelper.subtractDays(new Date(), 1))));
		initializeDateFilterButton(btnYesterday, dateFilterButtons);
		btnYesterday.addClickListener(e -> {
			Date now = new Date();
			Date from = DateHelper.getStartOfDay(DateHelper.subtractDays(now, 1));
			Date to = DateHelper.getEndOfDay(DateHelper.subtractDays(now, 1));
			setDateFilter(from, to);
			btnCurrentPeriod.setCaption(btnYesterday.getCaption());
			updateComparisonButtons(DateFilterType.YESTERDAY, from, to, false);
			dashboardView.refreshDashboard();
		});

		btnThisWeek = new Button(String.format(I18nProperties.getCaption(Captions.dashboardThisWeek), DateHelper.getEpiWeek(new Date()).toString(new Date())));
		initializeDateFilterButton(btnThisWeek, dateFilterButtons);
		btnThisWeek.addClickListener(e -> {
			Date now = new Date();
			Date from = DateHelper.getStartOfWeek(now);
			Date to = now;
			setDateFilter(from, to);
			btnCurrentPeriod.setCaption(btnThisWeek.getCaption());
			updateComparisonButtons(DateFilterType.THIS_WEEK, from, to, false);
			dashboardView.refreshDashboard();
		});

		btnLastWeek = new Button(String.format(I18nProperties.getCaption(Captions.dashboardLastWeek), DateHelper.getPreviousEpiWeek(new Date()).toString()));
		initializeDateFilterButton(btnLastWeek, dateFilterButtons);
		btnLastWeek.addClickListener(e -> {
			Date now = new Date();
			Date from = DateHelper.getStartOfWeek(DateHelper.subtractWeeks(now, 1));
			Date to = DateHelper.getEndOfWeek(DateHelper.subtractWeeks(now, 1));
			setDateFilter(from, to);
			btnCurrentPeriod.setCaption(btnLastWeek.getCaption());
			updateComparisonButtons(DateFilterType.LAST_WEEK, from, to, false);
			dashboardView.refreshDashboard();
		});

		btnThisYear = new Button(String.format(I18nProperties.getCaption(Captions.dashboardThisYear), DateHelper.buildPeriodString(DateHelper.getStartOfYear(new Date()), new Date())));
		initializeDateFilterButton(btnThisYear, dateFilterButtons);
		btnThisYear.addClickListener(e -> {
			Date now = new Date();
			Date from = DateHelper.getStartOfYear(now);
			Date to = now;
			setDateFilter(from, to);
			btnCurrentPeriod.setCaption(btnThisYear.getCaption());
			updateComparisonButtons(DateFilterType.THIS_YEAR, from, to, false);
			dashboardView.refreshDashboard();
		});

		layout.addComponents(btnShowCustomPeriod, btnToday, btnYesterday, btnThisWeek, btnLastWeek, btnThisYear);

		return layout;
	}

	private HorizontalLayout createCustomDateFilterLayout() {
		// Custom filter
		customDateFilterLayout = new HorizontalLayout();
		customDateFilterLayout.setSpacing(true);
		customDateFilterLayout.setVisible(false);

		// 'Apply custom filter' button
		Button applyButton = new Button(I18nProperties.getCaption(Captions.dashboardApplyCustomFilter));
		CssStyles.style(applyButton, CssStyles.FORCE_CAPTION, CssStyles.BUTTON_FILTER_LIGHT);
		applyButton.setEnabled(false);

		// Date & Epi Week filter
		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(applyButton, true, true, I18nProperties.getString(Strings.infoCaseDate));
		customDateFilterLayout.addComponents(weekAndDateFilter, applyButton);

		// Apply button listener
		applyButton.addClickListener(e -> {
			applyButton.setEnabled(false);
			applyButton.removeStyleName(ValoTheme.BUTTON_PRIMARY);
			DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
			Date fromDate = null;
			Date toDate = null;
			EpiWeek fromWeek = null;
			EpiWeek toWeek = null;
			if (dateFilterOption == DateFilterOption.DATE) {
				fromDate = weekAndDateFilter.getDateFromFilter().getValue();
				toDate = weekAndDateFilter.getDateToFilter().getValue();
				setDateFilter(DateHelper.getStartOfDay(fromDate), DateHelper.getEndOfDay(toDate));
			} else {
				fromWeek = (EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue();
				toWeek = (EpiWeek) weekAndDateFilter.getWeekToFilter().getValue();
				setDateFilter(DateHelper.getEpiWeekStart(fromWeek), DateHelper.getEpiWeekEnd(toWeek));
			}

			if ((fromDate != null && toDate != null) || (fromWeek != null && toWeek != null)) {
				if (dateFilterOption == DateFilterOption.DATE) {
					btnCurrentPeriod.setCaption(DateHelper.buildPeriodString(fromDate, toDate));
					int activePeriodLength = DateHelper.getDaysBetween(fromDate, toDate);
					btnPeriodBefore.setCaption(String.format(I18nProperties.getCaption(Captions.dashboardPeriodBefore), DateHelper.buildPeriodString(DateHelper.subtractDays(fromDate, activePeriodLength), DateHelper.subtractDays(toDate, activePeriodLength))));
					btnPeriodLastYear.setCaption(String.format(I18nProperties.getCaption(Captions.dashboardSamePeriodLastYear), DateHelper.buildPeriodString(DateHelper.subtractYears(fromDate, 1), DateHelper.subtractYears(toDate, 1))));
				} else {
					btnCurrentPeriod.setCaption(fromWeek.toShortString() + " - " + toWeek.toShortString());
					Date firstEpiWeekStart = DateHelper.getEpiWeekStart(fromWeek);
					Date lastEpiWeekStart = DateHelper.getEpiWeekStart(toWeek);
					int epiWeeksBetween = DateHelper.getWeeksBetween(firstEpiWeekStart, lastEpiWeekStart);
					btnPeriodBefore.setCaption(String.format(I18nProperties.getCaption(Captions.dashboardPeriodBefore), DateHelper.getEpiWeek(DateHelper.subtractWeeks(firstEpiWeekStart, epiWeeksBetween)).toShortString() + " - " 
							+ DateHelper.getEpiWeek(DateHelper.subtractWeeks(lastEpiWeekStart, epiWeeksBetween)).toShortString()));
					btnPeriodLastYear.setCaption(String.format(I18nProperties.getCaption(Captions.dashboardSamePeriodLastYear), DateHelper.getEpiWeekYearBefore(fromWeek).toShortString() + " - " 
							+ DateHelper.getEpiWeekYearBefore(toWeek).toShortString()));
				}
				updateComparisonButtons(DateFilterType.CUSTOM, null, null, true);
				dashboardView.refreshDashboard();
			} else {
				if (dateFilterOption == DateFilterOption.DATE) {
					new Notification(I18nProperties.getString(Strings.headingMissingDateFilter), 
							I18nProperties.getString(Strings.messageMissingDateFilter), Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				} else {
					new Notification(I18nProperties.getString(Strings.headingMissingEpiWeekFilter), 
							I18nProperties.getString(Strings.messageMissingEpiWeekFilter), Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				}
			}
		});

		return customDateFilterLayout;
	}

	private Component createDateComparisonButtonsLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		btnPeriodBefore = new Button();
		initializeDateFilterButton(btnPeriodBefore, dateComparisonButtons);
		btnPeriodBefore.addClickListener(e -> {
			activeComparisonButton = btnPeriodBefore;
			updateComparisonDates();
			btnComparisonPeriod.setCaption(btnPeriodBefore.getCaption());
			dashboardView.refreshDashboard();
		});

		btnPeriodLastYear = new Button();
		initializeDateFilterButton(btnPeriodLastYear, dateComparisonButtons);
		btnPeriodLastYear.addClickListener(e -> {
			activeComparisonButton = btnPeriodLastYear;
			updateComparisonDates();
			btnComparisonPeriod.setCaption(btnPeriodLastYear.getCaption());
			dashboardView.refreshDashboard();
		});

		layout.addComponents(btnPeriodBefore, btnPeriodLastYear);

		return layout;
	}

	private void initializeDateFilterButton(Button button, Set<Button> buttonSet) {
		button.addClickListener(e -> {
			changeCustomDateFilterPanelStyle(button, buttonSet);
		});
		CssStyles.style(button, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);
		buttonSet.add(button);
	}

	private void changeCustomDateFilterPanelStyle(Button activeFilterButton, Set<Button> buttonSet) {
		if (activeFilterButton != null) {
			CssStyles.style(activeFilterButton, CssStyles.BUTTON_FILTER_DARK);
			CssStyles.removeStyles(activeFilterButton, CssStyles.BUTTON_FILTER_LIGHT);
		}

		buttonSet.forEach(b -> {
			if (activeFilterButton == null || b != activeFilterButton) {
				CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
				CssStyles.removeStyles(b, CssStyles.BUTTON_FILTER_DARK);
			}
		});
		
		if (activeFilterButton == btnShowCustomPeriod)
			customDateFilterLayout.setVisible(true);
		else
			customDateFilterLayout.setVisible(false);
	}

	private void updateComparisonButtons(DateFilterType dateFilterType, Date from, Date to, boolean skipChangeButtonCaptions) {
		if (!skipChangeButtonCaptions) {
			switch (dateFilterType) {
			case TODAY:
			case YESTERDAY:
				btnPeriodBefore.setCaption(String.format(I18nProperties.getCaption(Captions.dashboardDayBefore), DateHelper.formatLocalDate(DateHelper.subtractDays(from, 1))));
				btnPeriodLastYear.setCaption(String.format(I18nProperties.getCaption(Captions.dashboardSameDayLastYear), DateHelper.formatLocalDate(DateHelper.subtractYears(from, 1))));
				break;
			case THIS_WEEK:
				btnPeriodBefore.setCaption(String.format(I18nProperties.getCaption(Captions.dashboardWeekBefore), DateHelper.getPreviousEpiWeek(from).toString(DateHelper.subtractWeeks(to, 1))));
				int daysBetweenEpiWeekStartAndNow = DateHelper.getFullDaysBetween(DateHelper.getEpiWeekStart(DateHelper.getEpiWeek(from)), new Date());
				btnPeriodLastYear.setCaption(String.format(I18nProperties.getCaption(Captions.dashboardSameWeekLastYear), DateHelper.getEpiWeekYearBefore(DateHelper.getEpiWeek(from)).toString(daysBetweenEpiWeekStartAndNow)));
				break;
			case LAST_WEEK:
				btnPeriodBefore.setCaption(String.format(I18nProperties.getCaption(Captions.dashboardWeekBefore), DateHelper.getPreviousEpiWeek(from).toString()));
				btnPeriodLastYear.setCaption(String.format(I18nProperties.getCaption(Captions.dashboardSameWeekLastYear), DateHelper.getEpiWeekYearBefore(DateHelper.getEpiWeek(from)).toString()));
				break;
			case THIS_YEAR:
				btnPeriodLastYear.setCaption(String.format(I18nProperties.getCaption(Captions.dashboardSamePeriodLastYear), DateHelper.buildPeriodString(DateHelper.subtractYears(from, 1), DateHelper.subtractYears(to, 1))));
				break;
			case CUSTOM:
				throw new UnsupportedOperationException("Captions for custom filter should be changed elsewhere to account for the differences between epi weeks and dates.");
			}
		}

		if (dateFilterType == DateFilterType.THIS_YEAR) {
			btnPeriodBefore.setVisible(false);
			activeComparisonButton = btnPeriodLastYear;
			changeCustomDateFilterPanelStyle(btnPeriodLastYear, dateComparisonButtons);
		} else {
			btnPeriodBefore.setVisible(true);
		}

		btnComparisonPeriod.setCaption(activeComparisonButton.getCaption());
	}

	private void setDateFilter(Date from, Date to) {
		dashboardDataProvider.setFromDate(DateHelper.getStartOfDay(from));
		dashboardDataProvider.setToDate(DateHelper.getEndOfDay(to));
		updateComparisonDates();
		if (dateFilterChangeCallback != null) {
			dateFilterChangeCallback.run();
		}
	}

	private void updateComparisonDates() {
		if (activeComparisonButton == btnPeriodBefore) {
			int activePeriodLength = DateHelper.getDaysBetween(dashboardDataProvider.getFromDate(), dashboardDataProvider.getToDate());
			setDateComparisonFilter(DateHelper.subtractDays(dashboardDataProvider.getFromDate(), activePeriodLength), 
					DateHelper.subtractDays(dashboardDataProvider.getToDate(), activePeriodLength));
		} else {
			setDateComparisonFilter(DateHelper.subtractYears(dashboardDataProvider.getFromDate(), 1),
					DateHelper.subtractYears(dashboardDataProvider.getToDate(), 1));
		}
	}

	private void setDateComparisonFilter(Date from, Date to) {
		dashboardDataProvider.setPreviousFromDate(from);
		dashboardDataProvider.setPreviousToDate(to);
	}

	public void setInfoLabelText(String text) {
		infoLabel.setDescription(text);
	}

	private enum DateFilterType {
		TODAY,
		YESTERDAY,
		THIS_WEEK,
		LAST_WEEK,
		THIS_YEAR,
		CUSTOM;
	}

	public Runnable getDateFilterChangeCallback() {
		return dateFilterChangeCallback;
	}

	public void setDateFilterChangeCallback(Runnable dateFilterChangeCallback) {
		this.dateFilterChangeCallback = dateFilterChangeCallback;
	}
	
	public Consumer<Boolean> getDiseaseFilterChangeCallback() {
		return diseaseFilterChangeCallback;
	}
	
	public void setDiseaseFilterChangeCallback(Consumer<Boolean> diseaseFilterChangeCallback) {
		this.diseaseFilterChangeCallback = diseaseFilterChangeCallback;
	}
	
	public boolean hasDiseaseSelected() {
		return diseaseFilter.getValue() != null;
	}

}
