package de.symeda.sormas.ui.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.highcharts.HighChart;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class EpiCurveComponent extends VerticalLayout {

	// Components
	private final DashboardDataProvider dashboardDataProvider;
	private final HighChart epiCurveChart;

	// Others
	private EpiCurveGrouping epiCurveGrouping;
	private boolean showMinimumEntries;
	private EpiCurveMode epiCurveMode;
	private ClickListener externalExpandButtonListener;
	private ClickListener externalCollapseButtonListener;

	public EpiCurveComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
		epiCurveChart = new HighChart();
		epiCurveChart.setSizeFull();
		epiCurveGrouping = EpiCurveGrouping.WEEK;
		showMinimumEntries = true;
		epiCurveMode = EpiCurveMode.CASE_STATUS;
		this.setMargin(true);

		addComponent(createHeader());
		addComponent(epiCurveChart);
		setExpandRatio(epiCurveChart, 1);

		clearAndFillEpiCurveChart();
	}

	public void clearAndFillEpiCurveChart() {
		StringBuilder hcjs = new StringBuilder();
		hcjs.append(
				"var options = {"
						+ "chart:{ "
						+ " type: 'column', "
						+ " backgroundColor: 'transparent' "
						+ "},"
						+ "credits:{ enabled: false },"
						+ "exporting:{ "
						+ " enabled: true,"
						+ " buttons:{ contextButton:{ theme:{ fill: 'transparent' } } }"
						+ "},"
						+ "title:{ text: '' },"
				);

		// Creates and sets the labels for each day on the x-axis
		List<Date> filteredDates = buildListOfFilteredDates();
		List<String> newLabels = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		for (Date date : filteredDates) {
			if (epiCurveGrouping == EpiCurveGrouping.DAY) {
				String label = DateHelper.formatLocalShortDate(date);
				newLabels.add(label);
			} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
				calendar.setTime(date);
				String label = DateHelper.getEpiWeek(date).toShortString();
				newLabels.add(label);
			} else {
				String label = DateHelper.formatDateWithMonthAbbreviation(date);
				newLabels.add(label);
			}
		}

		hcjs.append("xAxis: { categories: [");
		for (String s : newLabels) {
			if (newLabels.indexOf(s) == newLabels.size() - 1) {
				hcjs.append("'" + s + "']},");
			} else {
				hcjs.append("'" + s + "', ");
			}
		}

		hcjs.append("yAxis: { min: 0, title: { text: 'Number of Cases' }, allowDecimals: false, softMax: 10, "
				+ "stackLabels: { enabled: true, "
				+ "style: {fontWeight: 'normal', textOutline: '0', gridLineColor: '#000000', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray' } } },"
				+ "legend: { verticalAlign: 'top', backgroundColor: 'transparent', align: 'left', "
				+ "borderWidth: 0, shadow: false, margin: 30, padding: 0 },"
				+ "tooltip: { headerFormat: '<b>{point.x}</b><br/>', pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'},"
				+ "plotOptions: { column: { borderWidth: 0, stacking: 'normal', groupPadding: 0, pointPadding: 0, dataLabels: {"
				+ "enabled: true, formatter: function() { if (this.y > 0) return this.y; },"
				+ "color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white' } } },");

		if (epiCurveMode == EpiCurveMode.CASE_STATUS) {
			// Adds the number of confirmed, probable and suspect cases for each day as data
			int[] confirmedNumbers = new int[newLabels.size()];
			int[] probableNumbers = new int[newLabels.size()];
			int[] suspectNumbers = new int[newLabels.size()];
			int[] notYetClassifiedNumbers = new int[newLabels.size()];

			for (int i = 0; i < filteredDates.size(); i++) {
				Date date = filteredDates.get(i);

				CaseCriteria caseCriteria = new CaseCriteria()
						.diseaseEquals(dashboardDataProvider.getDisease())
						.regionEquals(dashboardDataProvider.getRegion())
						.districtEquals(dashboardDataProvider.getDistrict());
				if (epiCurveGrouping == EpiCurveGrouping.DAY) {
					caseCriteria.newCaseDateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date));
				} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
					caseCriteria.newCaseDateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date));
				} else {
					caseCriteria.newCaseDateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date));
				}

				Map<CaseClassification, Long> caseCounts = FacadeProvider.getCaseFacade()
						.getNewCaseCountPerClassification(caseCriteria, LoginHelper.getCurrentUser().getUuid());

				Long confirmedCount = caseCounts.get(CaseClassification.CONFIRMED);
				Long probableCount = caseCounts.get(CaseClassification.PROBABLE);
				Long suspectCount = caseCounts.get(CaseClassification.SUSPECT);
				Long notYetClassifiedCount = caseCounts.get(CaseClassification.NOT_CLASSIFIED);
				confirmedNumbers[i] = confirmedCount != null ? confirmedCount.intValue() : 0;
				probableNumbers[i] = probableCount != null ? probableCount.intValue() : 0;
				suspectNumbers[i] = suspectCount != null ? suspectCount.intValue() : 0;
				notYetClassifiedNumbers[i] = notYetClassifiedCount != null ? notYetClassifiedCount.intValue() : 0;
			}

			hcjs.append("series: [");
			hcjs.append("{ name: 'Confirmed', color: '#B22222', dataLabels: { allowOverlap: false }, data: [");
			for (int i = 0; i < confirmedNumbers.length; i++) {
				if (i == confirmedNumbers.length - 1) {
					hcjs.append(confirmedNumbers[i] + "]},");
				} else {
					hcjs.append(confirmedNumbers[i] + ", ");
				}
			}
			hcjs.append("{ name: 'Probable', color: '#FF4500', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < probableNumbers.length; i++) {
				if (i == probableNumbers.length - 1) {
					hcjs.append(probableNumbers[i] + "]},");
				} else {
					hcjs.append(probableNumbers[i] + ", ");
				}
			}
			hcjs.append("{ name: 'Suspect', color: '#FFD700', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < suspectNumbers.length; i++) {
				if (i == suspectNumbers.length - 1) {
					hcjs.append(suspectNumbers[i] + "]},");
				} else {
					hcjs.append(suspectNumbers[i] + ", ");
				}
			}
			hcjs.append("{name: 'Not Yet Classified', color: '#808080', dataLabels: { allowOverlap: false }, data: [");
			for (int i = 0; i < notYetClassifiedNumbers.length; i++) {
				if (i == notYetClassifiedNumbers.length - 1) {
					hcjs.append(notYetClassifiedNumbers[i] + "]}]};");
				} else {
					hcjs.append(notYetClassifiedNumbers[i] + ", ");
				}
			}
		} else {
			// Adds the number of alive and dead cases for each day as data
			int[] aliveNumbers = new int[newLabels.size()];
			int[] deadNumbers = new int[newLabels.size()];

			for (int i = 0; i < filteredDates.size(); i++) {
				Date date = filteredDates.get(i);

				CaseCriteria caseCriteria = new CaseCriteria()
						.diseaseEquals(dashboardDataProvider.getDisease())
						.regionEquals(dashboardDataProvider.getRegion())
						.districtEquals(dashboardDataProvider.getDistrict());
				if (epiCurveGrouping == EpiCurveGrouping.DAY) {
					caseCriteria.newCaseDateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date));
				} else if (epiCurveGrouping == EpiCurveGrouping.WEEK) {
					caseCriteria.newCaseDateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date));
				} else {
					caseCriteria.newCaseDateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date));
				}

				Map<PresentCondition, Long> caseCounts = FacadeProvider.getCaseFacade()
						.getNewCaseCountPerPersonCondition(caseCriteria, LoginHelper.getCurrentUser().getUuid());

				Long aliveCount = caseCounts.get(PresentCondition.ALIVE);
				Long deadCount = caseCounts.get(PresentCondition.DEAD);
				aliveNumbers[i] = aliveCount != null ? aliveCount.intValue() : 0;
				deadNumbers[i] = deadCount != null ? deadCount.intValue() : 0;
			}

			hcjs.append("series: [");
			hcjs.append("{ name: 'Alive', color: '#32CD32', dataLabels: { allowOverlap: false }, data: [");
			for (int i = 0; i < aliveNumbers.length; i++) {
				if (i == aliveNumbers.length - 1) {
					hcjs.append(aliveNumbers[i] + "]},");
				} else {
					hcjs.append(aliveNumbers[i] + ", ");
				}
			}
			hcjs.append("{ name: 'Dead', color: '#B22222', dataLabels: { allowOverlap: false },  data: [");
			for (int i = 0; i < deadNumbers.length; i++) {
				if (i == deadNumbers.length - 1) {
					hcjs.append(deadNumbers[i] + "]}]};");
				} else {
					hcjs.append(deadNumbers[i] + ", ");
				}
			}
		}

		epiCurveChart.setHcjs(hcjs.toString());	
	}

	public void setExpandListener(ClickListener listener) {
		externalExpandButtonListener = listener;
	}

	public void setCollapseListener(ClickListener listener) {
		externalCollapseButtonListener = listener;
	}

	private HorizontalLayout createHeader() {
		HorizontalLayout epiCurveHeaderLayout = new HorizontalLayout();
		epiCurveHeaderLayout.setWidth(100, Unit.PERCENTAGE);
		epiCurveHeaderLayout.setSpacing(true);
		CssStyles.style(epiCurveHeaderLayout, CssStyles.VSPACE_4);

		Label epiCurveLabel = new Label("Epidemiological Curve");
		epiCurveLabel.setSizeUndefined();
		CssStyles.style(epiCurveLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		
		epiCurveHeaderLayout.addComponent(epiCurveLabel);
		epiCurveHeaderLayout.setComponentAlignment(epiCurveLabel, Alignment.BOTTOM_LEFT);
		epiCurveHeaderLayout.setExpandRatio(epiCurveLabel, 1);

		// Grouping
		PopupButton groupingDropdown = new PopupButton("Grouping");
		CssStyles.style(groupingDropdown, CssStyles.BUTTON_SUBTLE);
		{
			VerticalLayout groupingLayout = new VerticalLayout();
			groupingLayout.setMargin(true);
			groupingLayout.setSizeUndefined();
			groupingDropdown.setContent(groupingLayout);

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
			CheckBox minimumEntriesCheckbox = new CheckBox("Always show at least 7 entries");
			CssStyles.style(minimumEntriesCheckbox, CssStyles.VSPACE_NONE);
			minimumEntriesCheckbox.setValue(showMinimumEntries);
			minimumEntriesCheckbox.addValueChangeListener(e -> {
				showMinimumEntries = (boolean) e.getProperty().getValue();
				clearAndFillEpiCurveChart();
			});
			groupingLayout.addComponent(minimumEntriesCheckbox);

			groupingDropdown.setContent(groupingLayout);
		}
		epiCurveHeaderLayout.addComponent(groupingDropdown);
		epiCurveHeaderLayout.setComponentAlignment(groupingDropdown, Alignment.MIDDLE_RIGHT);

		// Epi curve mode option
		OptionGroup epiCurveModeOptionGroup = new OptionGroup();
		epiCurveModeOptionGroup.setMultiSelect(false);
		CssStyles.style(epiCurveModeOptionGroup, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_SUBTLE);
		epiCurveModeOptionGroup.addItems((Object[]) EpiCurveMode.values());
		epiCurveModeOptionGroup.setValue(epiCurveMode);
		epiCurveModeOptionGroup.addValueChangeListener(e -> {
			epiCurveMode = (EpiCurveMode) e.getProperty().getValue();
			clearAndFillEpiCurveChart();
		});
		epiCurveHeaderLayout.addComponent(epiCurveModeOptionGroup);
		epiCurveHeaderLayout.setComponentAlignment(epiCurveModeOptionGroup, Alignment.MIDDLE_RIGHT);

		// "Expand" and "Collapse" buttons
		Button expandEpiCurveButton = new Button("", FontAwesome.EXPAND);
		CssStyles.style(expandEpiCurveButton, CssStyles.BUTTON_SUBTLE);
		expandEpiCurveButton.addStyleName(CssStyles.VSPACE_NONE);   
		Button collapseEpiCurveButton = new Button("", FontAwesome.COMPRESS);
		CssStyles.style(collapseEpiCurveButton, CssStyles.BUTTON_SUBTLE);
		collapseEpiCurveButton.addStyleName(CssStyles.VSPACE_NONE);

		expandEpiCurveButton.addClickListener(e -> {
			externalExpandButtonListener.buttonClick(e);
			epiCurveHeaderLayout.removeComponent(expandEpiCurveButton);
			epiCurveHeaderLayout.addComponent(collapseEpiCurveButton);
			epiCurveHeaderLayout.setComponentAlignment(collapseEpiCurveButton, Alignment.MIDDLE_RIGHT);
		});
		collapseEpiCurveButton.addClickListener(e -> {
			externalCollapseButtonListener.buttonClick(e);
			epiCurveHeaderLayout.removeComponent(collapseEpiCurveButton);
			epiCurveHeaderLayout.addComponent(expandEpiCurveButton);
			epiCurveHeaderLayout.setComponentAlignment(expandEpiCurveButton, Alignment.MIDDLE_RIGHT);
		});
		epiCurveHeaderLayout.addComponent(expandEpiCurveButton);
		epiCurveHeaderLayout.setComponentAlignment(expandEpiCurveButton, Alignment.MIDDLE_RIGHT);

		return epiCurveHeaderLayout;
	}

	/**
	 * Builds a list that contains an object for each day, week or month between the from and to dates. 
	 * Additional previous days, weeks or months might be added when showMinimumEntries is true.
	 * @return
	 */
	private List<Date> buildListOfFilteredDates() {
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

	public EpiCurveMode getEpiCurveMode() {
		return epiCurveMode;
	}

}
