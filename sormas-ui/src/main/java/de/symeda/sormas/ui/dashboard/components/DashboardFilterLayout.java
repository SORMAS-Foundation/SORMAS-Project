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
package de.symeda.sormas.ui.dashboard.components;

import static de.symeda.sormas.ui.utils.AbstractFilterForm.FILTER_ITEM_STYLE;
import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.NewDateFilterType;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DashboardType;
import org.apache.commons.lang3.ArrayUtils;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.dashboard.AbstractDashboardDataProvider;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;

@SuppressWarnings("serial")
public class DashboardFilterLayout<P extends AbstractDashboardDataProvider> extends HorizontalLayout {

	public static final String DATE_FILTER = "dateFilter";
	public static final String REGION_FILTER = "regionFilter";
	public static final String DISTRICT_FILTER = "districtFilter";
	private static final String RESET_AND_APPLY_BUTTONS = "resetAndApplyButtons";
	public static final String CASE_CLASSIFICATION_FILTER = "caseClassificationFilter";

	protected AbstractDashboardView dashboardView;
	protected P dashboardDataProvider;
	private CustomLayout customLayout;

	// Filters
	private ComboBox regionFilter;
	private ComboBox districtFilter;
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
	private Button resetButton;
	private Button applyButton;

	DateFilterType currentDateFilterType;

	private HorizontalLayout customDateFilterLayout;
	private ComboBox diseaseFilter;

	private Runnable dateFilterChangeCallback;
	private Consumer<Boolean> diseaseFilterChangeCallback;
	private Label infoLabel;

	private ComboBox caseClassificationFilter;

	public DashboardFilterLayout(AbstractDashboardView dashboardView, P dashboardDataProvider) {
		this.dashboardView = dashboardView;
		this.dashboardDataProvider = dashboardDataProvider;
		this.regionFilter = new ComboBox();
		this.districtFilter = new ComboBox();
		this.diseaseFilter = new ComboBox();
		dateFilterButtons = new HashSet<>();
		dateComparisonButtons = new HashSet<>();
		this.caseClassificationFilter = ComboBoxHelper.createComboBoxV7();

		setSpacing(true);
		setSizeUndefined();
		setMargin(new MarginInfo(true, true, false, true));

		//createDateFilters();
		createDateFiltersNew(dashboardDataProvider);

//		createRegionAndDistrictFilter();
		if (dashboardDataProvider.getDashboardType() == DashboardType.SURVEILLANCE) {
			createRegionAndDistrictFilter();
		}
		if (dashboardDataProvider.getDashboardType() == DashboardType.CONTACTS) {
			createRegionAndDistrictFilter();
			createDiseaseFilter();
		}
	}

	public DashboardFilterLayout(AbstractDashboardView dashboardView, P dashboardDataProvider, String[] templateContent) {
		this.dashboardView = dashboardView;
		this.dashboardDataProvider = dashboardDataProvider;
		this.regionFilter = ComboBoxHelper.createComboBoxV7();
		this.districtFilter = ComboBoxHelper.createComboBoxV7();
		dateFilterButtons = new HashSet<>();
		dateComparisonButtons = new HashSet<>();
		this.caseClassificationFilter = ComboBoxHelper.createComboBoxV7();

		setSpacing(true);
		setSizeUndefined();
		setMargin(new MarginInfo(true, true, false, true));

		String[] templateLocs = new String[] {
			DATE_FILTER,
			RESET_AND_APPLY_BUTTONS };
		templateLocs = ArrayUtils.insert(1, templateLocs, templateContent);

		customLayout = new CustomLayout();
		customLayout.setTemplateContents(filterLocs(templateLocs));

		//createDateFiltersNew(dashboardDataProvider);

		addComponent(customLayout);
		populateLayout();
		if(currentDateFilterType!=null) {
			String dateFilterType = currentDateFilterType.name();
			dashboardDataProvider.setDateFilterType(NewDateFilterType.valueOf(dateFilterType));
		}

		if (dashboardDataProvider.getDashboardType() == DashboardType.SURVEILLANCE) {
			createRegionAndDistrictFilter();
		}
		if (dashboardDataProvider.getDashboardType() == DashboardType.CONTACTS) {
			createRegionAndDistrictFilter();
			createDiseaseFilter();
		}
	}

	public void populateLayout() {
		createDateFilters();
		createResetAndApplyButtons();
	};


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

	protected void createRegionAndDistrictFilter() {
		createRegionFilter(null);
		createDistrictFilter(null);
	}

	//Case Classification filter
	public void createCaseClassificationFilter() {
		caseClassificationFilter.setWidth(200, Unit.PIXELS);
		caseClassificationFilter.setInputPrompt(I18nProperties.getPrefixCaption(CaseIndexDto.I18N_PREFIX, ContactIndexDto.CASE_CLASSIFICATION));
		caseClassificationFilter.addItems((Object[]) CaseClassification.values());
		caseClassificationFilter.addValueChangeListener(e -> {
			dashboardDataProvider.setCaseClassification((CaseClassification) caseClassificationFilter.getValue());
		});
		addCustomComponent(caseClassificationFilter, CASE_CLASSIFICATION_FILTER);
		dashboardDataProvider.setCaseClassification((CaseClassification) caseClassificationFilter.getValue());
	}

	protected void createRegionFilter(String description) {
		if (UserProvider.getCurrent().getUser().getRegion() == null && UiUtil.disabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			regionFilter.setWidth(200, Unit.PIXELS);
			regionFilter.setInputPrompt(I18nProperties.getString(Strings.promptRegion));
			regionFilter.setDescription(description);
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
			regionFilter.addValueChangeListener(e -> {
				dashboardDataProvider.setRegion((RegionReferenceDto) regionFilter.getValue());
			});
			// save height
			// regionFilter.setCaption(I18nProperties.getString(Strings.entityRegion));
			addCustomComponent(regionFilter, REGION_FILTER);
			dashboardDataProvider.setRegion((RegionReferenceDto) regionFilter.getValue());
		}
	}

	protected void createDistrictFilter(String description) {
		if (UserProvider.getCurrent().getUser().getRegion() != null
			&& UserProvider.getCurrent().getUser().getDistrict() == null
			&& UiUtil.disabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			districtFilter.setWidth(200, Unit.PIXELS);
			districtFilter.setInputPrompt(I18nProperties.getString(Strings.promptDistrict));
			districtFilter.setDescription(description);
			districtFilter
				.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(UserProvider.getCurrent().getUser().getRegion().getUuid()));
			districtFilter.addValueChangeListener(e -> dashboardDataProvider.setDistrict((DistrictReferenceDto) districtFilter.getValue()));
			// save height
			//districtFilter.setCaption(I18nProperties.getString(Strings.entityDistrict));
			addCustomComponent(districtFilter, DISTRICT_FILTER);
			dashboardDataProvider.setDistrict((DistrictReferenceDto) districtFilter.getValue());
		}
	}

	public void createResetAndApplyButtons() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		Button.ClickListener resetListener = e -> dashboardView.navigateTo(null);
		resetButton = ButtonHelper.createButton(Captions.actionResetFilters, resetListener, CssStyles.BUTTON_FILTER_LIGHT);
		buttonLayout.addComponent(resetButton);
		Button.ClickListener applyListener = e -> dashboardView.refreshDashboard();
		applyButton = ButtonHelper.createButton(Captions.actionApplyFilters, applyListener, CssStyles.BUTTON_FILTER_LIGHT);
		applyButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		applyButton.addClickListener(e -> {
			if (getDateFilterChangeCallback() != null) {
				getDateFilterChangeCallback().run();
			}
		});
		buttonLayout.addComponent(applyButton);
		addCustomComponent(buttonLayout, RESET_AND_APPLY_BUTTONS);
	}

	public void createDateFilters() {
		HorizontalLayout dateFilterLayout = new HorizontalLayout();
		dateFilterLayout.setSpacing(true);
		CssStyles.style(dateFilterLayout, CssStyles.VSPACE_3);
		addCustomComponent(dateFilterLayout, DATE_FILTER);

		btnCurrentPeriod = ButtonHelper.createIconPopupButton(
			"currentPeriod",
			null,
			new VerticalLayout(createDateFilterButtonsLayout(), createCustomDateFilterLayout()),
			CssStyles.BUTTON_FILTER,
			CssStyles.BUTTON_FILTER_LIGHT);

		Label lblComparedTo = new Label(I18nProperties.getCaption(Captions.dashboardComparedTo));
		CssStyles.style(lblComparedTo, CssStyles.VSPACE_TOP_4, CssStyles.LABEL_BOLD);

		btnComparisonPeriod = ButtonHelper.createIconPopupButton(
			"comparisonPeriod",
			null,
			createDateComparisonButtonsLayout(),
			ValoTheme.BUTTON_BORDERLESS,
			CssStyles.BUTTON_FILTER,
			CssStyles.BUTTON_FILTER_LIGHT);

		dateFilterLayout.addComponents(btnCurrentPeriod, lblComparedTo, btnComparisonPeriod);


//		infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
//		infoLabel.setSizeUndefined();
//		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);
//		addComponent(infoLabel);
//		setComponentAlignment(infoLabel, Alignment.TOP_RIGHT);

		// Set initial date filter
		CssStyles.style(btnThisWeek, CssStyles.BUTTON_FILTER_DARK);
		CssStyles.removeStyles(btnThisWeek, CssStyles.BUTTON_FILTER_LIGHT);
		CssStyles.style(btnPeriodBefore, CssStyles.BUTTON_FILTER_DARK);
		CssStyles.removeStyles(btnPeriodBefore, CssStyles.BUTTON_FILTER_LIGHT);
		activeComparisonButton = btnPeriodBefore;
		currentDateFilterType = DateFilterType.THIS_WEEK;
		setDateFilter(DateHelper.getStartOfWeek(new Date()), new Date());
		updateComparisonButtons(NewDateFilterType.THIS_WEEK, DateHelper.getStartOfWeek(new Date()), new Date(), false);
		btnCurrentPeriod.setCaption(btnThisWeek.getCaption());

	}


	private void createDateFiltersNew(P dashboardDataProvider) {
		HorizontalLayout dateFilterLayout = new HorizontalLayout();
		dateFilterLayout.setSpacing(true);
		CssStyles.style(dateFilterLayout, CssStyles.VSPACE_3);
		addComponent(dateFilterLayout);

		btnCurrentPeriod = ButtonHelper.createIconPopupButton(
				"currentPeriod",
				null,
				new VerticalLayout(createDateFilterButtonsLayout(), createCustomDateFilterLayout()),
				CssStyles.BUTTON_FILTER,
				CssStyles.BUTTON_FILTER_LIGHT);

		Label lblComparedTo = new Label(I18nProperties.getCaption(Captions.dashboardComparedTo));
		CssStyles.style(lblComparedTo, CssStyles.VSPACE_TOP_4, CssStyles.LABEL_BOLD);

		btnComparisonPeriod = ButtonHelper.createIconPopupButton(
				"comparisonPeriod",
				null,
				createDateComparisonButtonsLayout(),
				ValoTheme.BUTTON_BORDERLESS,
				CssStyles.BUTTON_FILTER,
				CssStyles.BUTTON_FILTER_LIGHT);

		dateFilterLayout.addComponents(btnCurrentPeriod, lblComparedTo, btnComparisonPeriod);

//		infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
//		infoLabel.setSizeUndefined();
//		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);
//		addComponent(infoLabel);
//		setComponentAlignment(infoLabel, Alignment.TOP_RIGHT);

		// Set initial date filter
		CssStyles.style(btnThisWeek, CssStyles.BUTTON_FILTER_DARK);
		CssStyles.removeStyles(btnThisWeek, CssStyles.BUTTON_FILTER_LIGHT);
		CssStyles.style(btnPeriodBefore, CssStyles.BUTTON_FILTER_DARK);
		CssStyles.removeStyles(btnPeriodBefore, CssStyles.BUTTON_FILTER_LIGHT);
		activeComparisonButton = btnPeriodBefore;



		setDateFilter(dashboardDataProvider.getFromDate(), dashboardDataProvider.getToDate());


		//setDateFilter(DateHelper.getStartOfWeek(new Date()), new Date());

		//updateComparisonButtons(dashboardDataProvider.getDateFilterType(), dashboardDataProvider.getFromDate(), dashboardDataProvider.getToDate(), false);

		updateComparisonButtons(NewDateFilterType.THIS_WEEK, DateHelper.getStartOfWeek(new Date()), new Date(), false);
		btnCurrentPeriod.setCaption(btnThisWeek.getCaption());
	}
	private HorizontalLayout createDateFilterButtonsLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);

		// Date filters
		btnShowCustomPeriod =
			createAndAddDateFilterButton(Captions.dashboardCustom, I18nProperties.getCaption(Captions.dashboardCustom), dateFilterButtons);

		btnToday = createAndAddDateFilterButton(
			Captions.dashboardToday,
			String.format(I18nProperties.getCaption(Captions.dashboardToday), DateFormatHelper.formatDate(new Date())),
			dateFilterButtons);
		btnToday.addClickListener(e -> {
			currentDateFilterType = DateFilterType.TODAY;
			Date now = new Date();
			Date from = DateHelper.getStartOfDay(now);
			Date to = now;
			setDateFilter(from, to);
			btnCurrentPeriod.setCaption(btnToday.getCaption());
			updateComparisonButtons(NewDateFilterType.TODAY, from, to, false);
			if (DashboardType.DISEASE.equals(dashboardDataProvider.getDashboardType()))
				dashboardView.refreshDiseaseData();
			else
				dashboardView.refreshDashboard();
		});

		btnYesterday = createAndAddDateFilterButton(
			Captions.dashboardYesterday,
			String
				.format(I18nProperties.getCaption(Captions.dashboardYesterday), DateFormatHelper.formatDate(DateHelper.subtractDays(new Date(), 1))),
			dateFilterButtons);
		btnYesterday.addClickListener(e -> {
			currentDateFilterType = DateFilterType.YESTERDAY;
			Date now = new Date();
			Date from = DateHelper.getStartOfDay(DateHelper.subtractDays(now, 1));
			Date to = DateHelper.getEndOfDay(DateHelper.subtractDays(now, 1));
			setDateFilter(from, to);
			btnCurrentPeriod.setCaption(btnYesterday.getCaption());
			updateComparisonButtons(NewDateFilterType.YESTERDAY, from, to, false);
			if (DashboardType.DISEASE.equals(dashboardDataProvider.getDashboardType()))
				dashboardView.refreshDiseaseData();
			else
				dashboardView.refreshDashboard();
		});

		btnThisWeek = createAndAddDateFilterButton(
			Captions.dashboardThisWeek,
			String.format(
				I18nProperties.getCaption(Captions.dashboardThisWeek),
				DateHelper.getEpiWeek(new Date()).toString(new Date(), I18nProperties.getUserLanguage())),
			dateFilterButtons);
		btnThisWeek.addClickListener(e -> {
			currentDateFilterType = DateFilterType.THIS_WEEK;
			Date now = new Date();
			Date from = DateHelper.getStartOfWeek(now);
			Date to = now;
			setDateFilter(from, to);
			btnCurrentPeriod.setCaption(btnThisWeek.getCaption());
			updateComparisonButtons(NewDateFilterType.THIS_WEEK, from, to, false);
			if (DashboardType.DISEASE.equals(dashboardDataProvider.getDashboardType()))
				dashboardView.refreshDiseaseData();
			else
				dashboardView.refreshDashboard();
		});

		btnLastWeek = createAndAddDateFilterButton(
			Captions.dashboardLastWeek,
			String.format(
				I18nProperties.getCaption(Captions.dashboardLastWeek),
				DateHelper.getPreviousEpiWeek(new Date()).toString(I18nProperties.getUserLanguage())),
			dateFilterButtons);
		btnLastWeek.addClickListener(e -> {
			currentDateFilterType = DateFilterType.LAST_WEEK;
			Date now = new Date();
			Date from = DateHelper.getStartOfWeek(DateHelper.subtractWeeks(now, 1));
			Date to = DateHelper.getEndOfWeek(DateHelper.subtractWeeks(now, 1));
			setDateFilter(from, to);
			btnCurrentPeriod.setCaption(btnLastWeek.getCaption());
			updateComparisonButtons(NewDateFilterType.LAST_WEEK, from, to, false);
			if (DashboardType.DISEASE.equals(dashboardDataProvider.getDashboardType()))
				dashboardView.refreshDiseaseData();
			else
				dashboardView.refreshDashboard();
		});

		btnThisYear = createAndAddDateFilterButton(
			Captions.dashboardThisYear,
			String.format(
				I18nProperties.getCaption(Captions.dashboardThisYear),
				DateFormatHelper.buildPeriodString(DateHelper.getStartOfYear(new Date()), new Date())),
			dateFilterButtons);
		btnThisYear.addClickListener(e -> {
			currentDateFilterType = DateFilterType.THIS_YEAR;
			Date now = new Date();
			Date from = DateHelper.getStartOfYear(now);
			Date to = now;
			setDateFilter(from, to);
			btnCurrentPeriod.setCaption(btnThisYear.getCaption());
			updateComparisonButtons(NewDateFilterType.THIS_YEAR, from, to, false);
			if (DashboardType.DISEASE.equals(dashboardDataProvider.getDashboardType()))
				dashboardView.refreshDiseaseData();
			else
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
		Button applyButton =
			ButtonHelper.createButton(Captions.dashboardApplyCustomFilter, null, CssStyles.FORCE_CAPTION, CssStyles.BUTTON_FILTER_LIGHT);

		// Date & Epi Week filter
		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter =
			new EpiWeekAndDateFilterComponent<>(true, true, I18nProperties.getString(Strings.infoCaseDate), null);
		customDateFilterLayout.addComponents(weekAndDateFilter, applyButton);

		// Apply button listener
		applyButton.addClickListener(e -> {
			currentDateFilterType = DateFilterType.CUSTOM;
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
					btnCurrentPeriod.setCaption(DateFormatHelper.buildPeriodString(fromDate, toDate));
					int activePeriodLength = DateHelper.getDaysBetween(fromDate, toDate);
					btnPeriodBefore.setCaption(
						String.format(
							I18nProperties.getCaption(Captions.dashboardPeriodBefore),
							DateFormatHelper.buildPeriodString(
								DateHelper.subtractDays(fromDate, activePeriodLength),
								DateHelper.subtractDays(toDate, activePeriodLength))));
					btnPeriodLastYear.setCaption(
						String.format(
							I18nProperties.getCaption(Captions.dashboardSamePeriodLastYear),
							DateFormatHelper.buildPeriodString(DateHelper.subtractYears(fromDate, 1), DateHelper.subtractYears(toDate, 1))));
				} else {
					btnCurrentPeriod.setCaption(fromWeek.toShortString() + " - " + toWeek.toShortString());
					Date firstEpiWeekStart = DateHelper.getEpiWeekStart(fromWeek);
					Date lastEpiWeekStart = DateHelper.getEpiWeekStart(toWeek);
					int epiWeeksBetween = DateHelper.getWeeksBetween(firstEpiWeekStart, lastEpiWeekStart);
					btnPeriodBefore.setCaption(
						String.format(
							I18nProperties.getCaption(Captions.dashboardPeriodBefore),
							DateHelper.getEpiWeek(DateHelper.subtractWeeks(firstEpiWeekStart, epiWeeksBetween)).toShortString() + " - "
								+ DateHelper.getEpiWeek(DateHelper.subtractWeeks(lastEpiWeekStart, epiWeeksBetween)).toShortString()));
					btnPeriodLastYear.setCaption(
						String.format(
							I18nProperties.getCaption(Captions.dashboardSamePeriodLastYear),
							DateHelper.getEpiWeekYearBefore(fromWeek).toShortString() + " - "
								+ DateHelper.getEpiWeekYearBefore(toWeek).toShortString()));
				}
				updateComparisonButtons(NewDateFilterType.CUSTOM, null, null, true);
				if (DashboardType.DISEASE.equals(dashboardDataProvider.getDashboardType()))
					dashboardView.refreshDiseaseData();
				else
					dashboardView.refreshDashboard();
			} else {
				if (dateFilterOption == DateFilterOption.DATE) {
					new Notification(
						I18nProperties.getString(Strings.headingMissingDateFilter),
						I18nProperties.getString(Strings.messageMissingDateFilter),
						Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				} else {
					new Notification(
						I18nProperties.getString(Strings.headingMissingEpiWeekFilter),
						I18nProperties.getString(Strings.messageMissingEpiWeekFilter),
						Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				}
			}
		});

		return customDateFilterLayout;
	}

	private Component createDateComparisonButtonsLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		btnPeriodBefore = createAndAddDateFilterButton(Captions.dashboardDayBefore, null, dateComparisonButtons);
		btnPeriodBefore.addClickListener(e -> {
			activeComparisonButton = btnPeriodBefore;
			updateComparisonDates();
			btnComparisonPeriod.setCaption(btnPeriodBefore.getCaption());
			if (DashboardType.DISEASE.equals(dashboardDataProvider.getDashboardType()))
				dashboardView.refreshDiseaseData();
			else
				dashboardView.refreshDashboard();
		});

		btnPeriodLastYear = createAndAddDateFilterButton(Captions.dashboardSameDayLastYear, null, dateComparisonButtons);
		btnPeriodLastYear.addClickListener(e -> {
			activeComparisonButton = btnPeriodLastYear;
			updateComparisonDates();
			btnComparisonPeriod.setCaption(btnPeriodLastYear.getCaption());
			if (DashboardType.DISEASE.equals(dashboardDataProvider.getDashboardType()))
				dashboardView.refreshDiseaseData();
			else
				dashboardView.refreshDashboard();
		});

		layout.addComponents(btnPeriodBefore, btnPeriodLastYear);

		return layout;
	}

	private Button createAndAddDateFilterButton(String id, String caption, Set<Button> buttonSet) {
		Button button = ButtonHelper.createButton(id, caption, e -> {
			changeCustomDateFilterPanelStyle(e.getButton(), buttonSet);
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);

		buttonSet.add(button);

		return button;
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

	private void updateComparisonButtons(NewDateFilterType dateFilterType, Date from, Date to, boolean skipChangeButtonCaptions) {
		Language userLanguage = I18nProperties.getUserLanguage();

		if (!skipChangeButtonCaptions) {
			switch (dateFilterType) {
			case TODAY:
			case YESTERDAY:
				btnPeriodBefore.setCaption(
					String.format(
						I18nProperties.getCaption(Captions.dashboardDayBefore),
						DateFormatHelper.formatDate(DateHelper.subtractDays(from, 1))));
				btnPeriodLastYear.setCaption(
					String.format(
						I18nProperties.getCaption(Captions.dashboardSameDayLastYear),
						DateFormatHelper.formatDate(DateHelper.subtractYears(from, 1))));
				break;
			case THIS_WEEK:
				btnPeriodBefore.setCaption(
					String.format(
						I18nProperties.getCaption(Captions.dashboardWeekBefore),
						DateHelper.getPreviousEpiWeek(from).toString(DateHelper.subtractWeeks(to, 1), userLanguage)));
				int daysBetweenEpiWeekStartAndNow =
					DateHelper.getFullDaysBetween(DateHelper.getEpiWeekStart(DateHelper.getEpiWeek(from)), new Date());
				btnPeriodLastYear.setCaption(
					String.format(
						I18nProperties.getCaption(Captions.dashboardSameWeekLastYear),
						DateHelper.getEpiWeekYearBefore(DateHelper.getEpiWeek(from)).toString(daysBetweenEpiWeekStartAndNow, userLanguage)));
				break;
			case LAST_WEEK:
				btnPeriodBefore.setCaption(
					String
						.format(I18nProperties.getCaption(Captions.dashboardWeekBefore), DateHelper.getPreviousEpiWeek(from).toString(userLanguage)));
				btnPeriodLastYear.setCaption(
					String.format(
						I18nProperties.getCaption(Captions.dashboardSameWeekLastYear),
						DateHelper.getEpiWeekYearBefore(DateHelper.getEpiWeek(from)).toString(userLanguage)));
				break;
			case THIS_YEAR:
				btnPeriodLastYear.setCaption(
					String.format(
						I18nProperties.getCaption(Captions.dashboardSamePeriodLastYear),
						DateFormatHelper.buildPeriodString(DateHelper.subtractYears(from, 1), DateHelper.subtractYears(to, 1))));
				break;
			case CUSTOM:
				throw new UnsupportedOperationException(
					"Captions for custom filter should be changed elsewhere to account for the differences between epi weeks and dates.");
			}
		}

		if (dateFilterType == NewDateFilterType.THIS_YEAR) {
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
		if (currentDateFilterType != DateFilterType.THIS_YEAR && activeComparisonButton == btnPeriodBefore) {
			int activePeriodLength = currentDateFilterType == DateFilterType.THIS_WEEK
				? 7
				: DateHelper.getDaysBetween(dashboardDataProvider.getFromDate(), dashboardDataProvider.getToDate());
			dashboardDataProvider.setPreviousFromDate(DateHelper.subtractDays(dashboardDataProvider.getFromDate(), activePeriodLength));
			dashboardDataProvider.setPreviousToDate(DateHelper.subtractDays(dashboardDataProvider.getToDate(), activePeriodLength));
		} else if (currentDateFilterType == DateFilterType.THIS_WEEK || currentDateFilterType == DateFilterType.LAST_WEEK) {
			Date previousEpiWeekStart =
				DateHelper.getEpiWeekStart(DateHelper.getEpiWeekYearBefore(DateHelper.getEpiWeek(dashboardDataProvider.getFromDate())));
			dashboardDataProvider.setPreviousFromDate(previousEpiWeekStart);
			int activePeriodLength = DateHelper.getDaysBetween(dashboardDataProvider.getFromDate(), dashboardDataProvider.getToDate());
			dashboardDataProvider.setPreviousToDate(DateHelper.addDays(previousEpiWeekStart, activePeriodLength - 1));
		} else {
			dashboardDataProvider.setPreviousFromDate(DateHelper.subtractYears(dashboardDataProvider.getFromDate(), 1));
			dashboardDataProvider.setPreviousToDate(DateHelper.subtractYears(dashboardDataProvider.getToDate(), 1));
		}
	}

	public void setInfoLabelText(String text) {
		infoLabel.setDescription(text);
	}

	public void setCriteria(DashboardCriteria criteria) {
		regionFilter.setValue(criteria.getRegion());
		caseClassificationFilter.setValue(criteria.getCaseClassification());
		setDateFilter(criteria.getDateFrom(), criteria.getDateTo());
		btnCurrentPeriod.setCaption(DateFormatHelper.buildPeriodString(criteria.getDateFrom(), criteria.getDateTo()));
		updateComparisonDates();
		updateComparisonButtons(criteria.getDateFilterType(), criteria.getDateFrom(), criteria.getDateTo(), false);
		activeComparisonButton = btnPeriodBefore;
		btnComparisonPeriod.setCaption(activeComparisonButton.getCaption());
		updateCurrentPeriodButtons(criteria);
	}

	private void updateCurrentPeriodButtons(DashboardCriteria criteria) {
		if (criteria.getDateFilterType().equals(NewDateFilterType.TODAY)) {
			btnCurrentPeriod.setCaption(btnToday.getCaption());
		}
		if (criteria.getDateFilterType().equals(NewDateFilterType.YESTERDAY)) {
			btnCurrentPeriod.setCaption(btnYesterday.getCaption());
		}
		if (criteria.getDateFilterType().equals(NewDateFilterType.LAST_WEEK)) {
			btnCurrentPeriod.setCaption(btnLastWeek.getCaption());
		}
		if (criteria.getDateFilterType().equals(NewDateFilterType.THIS_WEEK)) {
			btnCurrentPeriod.setCaption(btnThisWeek.getCaption());
		}
		if (criteria.getDateFilterType().equals(NewDateFilterType.THIS_YEAR)) {
			btnCurrentPeriod.setCaption(btnThisYear.getCaption());
		}
		if (criteria.getDateFilterType().equals(NewDateFilterType.CUSTOM)) {
			btnCurrentPeriod.setCaption(DateFormatHelper.buildPeriodString(criteria.getDateFrom(), criteria.getDateTo()));
		}

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

	protected void addCustomComponent(Component component, String locator) {
		customLayout.addComponent(component, locator);
		component.addStyleName(FILTER_ITEM_STYLE);
	}


	public void reload(ViewChangeListener.ViewChangeEvent event) {
		DashboardCriteria criteria = dashboardDataProvider.getCriteria();
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);

			criteria.fromUrlParams(params);
			updateFilterDates(criteria);
		}
		setCriteria(criteria);

	}

	public void updateFilterDates(DashboardCriteria criteria) {
		NewDateFilterType dateFilterType = criteria.getDateFilterType();
		if (dateFilterType == NewDateFilterType.TODAY) {
			criteria.dateFrom(DateHelper.getStartOfDay(new Date()));
			criteria.dateTo(new Date());
		}
		if (dateFilterType == NewDateFilterType.YESTERDAY) {
			criteria.dateFrom(DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 1)));
			criteria.dateTo(DateHelper.getEndOfDay(DateHelper.subtractDays(new Date(), 1)));
		}
		if (dateFilterType == NewDateFilterType.THIS_WEEK) {
			criteria.dateFrom(DateHelper.getStartOfWeek(new Date()));
			criteria.dateTo(new Date());
		}
		if (dateFilterType == NewDateFilterType.LAST_WEEK) {
			criteria.dateFrom(DateHelper.getStartOfWeek(DateHelper.subtractWeeks(new Date(), 1)));
			criteria.dateTo(DateHelper.getEndOfWeek(DateHelper.subtractWeeks(new Date(), 1)));
		}
		if (dateFilterType == NewDateFilterType.THIS_YEAR) {
			criteria.dateFrom(DateHelper.getStartOfWeek(DateHelper.getStartOfYear(new Date())));
			criteria.dateTo(new Date());
		}
		if (dateFilterType == NewDateFilterType.CUSTOM) {
			criteria.dateFrom(criteria.getDateFrom());
			criteria.dateTo(criteria.getDateTo());
		}



	}
}
