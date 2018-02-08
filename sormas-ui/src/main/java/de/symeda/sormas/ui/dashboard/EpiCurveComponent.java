package de.symeda.sormas.ui.dashboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.highcharts.HighChart;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class EpiCurveComponent extends VerticalLayout {

	// Components
	private final DashboardDataProvider dashboardDataProvider;
	private final HighChart epiCurveChart;

	// UI elements
	private Label epiCurveDateLabel;
	
	// Others
	private EpiCurveMode epiCurveMode;
	private ClickListener externalExpandButtonListener;
	private ClickListener externalCollapseButtonListener;

	public EpiCurveComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
		epiCurveChart = new HighChart();
		epiCurveChart.setSizeFull();
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
		for (Date date : filteredDates) {
			String label = DateHelper.formatShortDate(date);
			newLabels.add(label);
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
			List<DashboardCaseDto> cases = dashboardDataProvider.getCases();
			List<DashboardCaseDto> confirmedCases = cases.stream()
					.filter(c -> c.getCaseClassification() == CaseClassification.CONFIRMED)
					.collect(Collectors.toList());
			List<DashboardCaseDto> probableCases = cases.stream()
					.filter(c -> c.getCaseClassification() == CaseClassification.PROBABLE)
					.collect(Collectors.toList());
			List<DashboardCaseDto> suspectedCases = cases.stream()
					.filter(c -> c.getCaseClassification() == CaseClassification.SUSPECT)
					.collect(Collectors.toList());
			List<DashboardCaseDto> notYetClassifiedCases = cases.stream()
					.filter(c -> c.getCaseClassification() == CaseClassification.NOT_CLASSIFIED)
					.collect(Collectors.toList());
	
			int[] confirmedNumbers = new int[newLabels.size()];
			int[] probableNumbers = new int[newLabels.size()];
			int[] suspectNumbers = new int[newLabels.size()];
			int[] notYetClassifiedNumbers = new int[newLabels.size()];
	
			for (int i = 0; i < filteredDates.size(); i++) {
				Date date = filteredDates.get(i);
				int confirmedCasesAtDate = (int) confirmedCases.stream()
						.filter(c -> c.getOnsetDate() != null ? DateHelper.isSameDay(c.getOnsetDate(), date) : 
							c.getReceptionDate() != null ? DateHelper.isSameDay(c.getReceptionDate(),  date) :
								DateHelper.isSameDay(c.getReportDate(), date))
						.count();
				confirmedNumbers[i] = confirmedCasesAtDate;
				int probableCasesAtDate = (int) probableCases.stream()
						.filter(c -> c.getOnsetDate() != null ? DateHelper.isSameDay(c.getOnsetDate(), date) : 
							c.getReceptionDate() != null ? DateHelper.isSameDay(c.getReceptionDate(),  date) :
								DateHelper.isSameDay(c.getReportDate(), date))
						.count();
				probableNumbers[i] = probableCasesAtDate;
				int suspectCasesAtDate = (int) suspectedCases.stream()
						.filter(c -> c.getOnsetDate() != null ? DateHelper.isSameDay(c.getOnsetDate(), date) : 
							c.getReceptionDate() != null ? DateHelper.isSameDay(c.getReceptionDate(),  date) :
								DateHelper.isSameDay(c.getReportDate(), date))
						.count();
				suspectNumbers[i] = suspectCasesAtDate;
				int notYetClassifiedCasesAtDate = (int) notYetClassifiedCases.stream()
						.filter(c -> c.getOnsetDate() != null ? DateHelper.isSameDay(c.getOnsetDate(), date) : 
							c.getReceptionDate() != null ? DateHelper.isSameDay(c.getReceptionDate(),  date) :
								DateHelper.isSameDay(c.getReportDate(), date))
						.count();
				notYetClassifiedNumbers[i] = notYetClassifiedCasesAtDate;
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
			List<DashboardCaseDto> cases = dashboardDataProvider.getCases();
			List<DashboardCaseDto> aliveCases = cases.stream()
					.filter(c -> c.getCasePersonCondition() == PresentCondition.ALIVE)
					.collect(Collectors.toList());
			List<DashboardCaseDto> deadCases = cases.stream()
					.filter(c -> c.getCasePersonCondition() == PresentCondition.DEAD)
					.collect(Collectors.toList());
			
			int[] aliveNumbers = new int[newLabels.size()];
			int[] deadNumbers = new int[newLabels.size()];
			
			for (int i = 0; i < filteredDates.size(); i++) {
				Date date = filteredDates.get(i);
				int aliveCasesAtDate = (int) aliveCases.stream()
						.filter(c -> c.getOnsetDate() != null ? DateHelper.isSameDay(c.getOnsetDate(), date) : 
							c.getReceptionDate() != null ? DateHelper.isSameDay(c.getReceptionDate(),  date) :
								DateHelper.isSameDay(c.getReportDate(), date))
						.count();
				aliveNumbers[i] = aliveCasesAtDate;
				int deadCasesAtDate = (int) deadCases.stream()
						.filter(c -> c.getOnsetDate() != null ? DateHelper.isSameDay(c.getOnsetDate(), date) : 
							c.getReceptionDate() != null ? DateHelper.isSameDay(c.getReceptionDate(),  date) :
								DateHelper.isSameDay(c.getReportDate(), date))
						.count();
				deadNumbers[i] = deadCasesAtDate;
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

	public void updateDateLabel() {
		if (dashboardDataProvider.getDateFilterOption() == DateFilterOption.EPI_WEEK) {
			EpiWeek fromWeek = dashboardDataProvider.getFromWeek();
			EpiWeek toWeek = dashboardDataProvider.getToWeek();
			if (fromWeek.getWeek() == toWeek.getWeek()) {
				epiCurveDateLabel.setValue("NEW CASES IN EPI WEEK " + fromWeek.getWeek());
			} else {
				epiCurveDateLabel.setValue("NEW CASES BETWEEN EPI WEEK " + fromWeek.getWeek() + " AND " + toWeek.getWeek());
			}
		} else {
			Date fromDate = dashboardDataProvider.getFromDate();
			Date toDate = dashboardDataProvider.getToDate();
			if (DateHelper.isSameDay(fromDate, toDate)) {
				epiCurveDateLabel.setValue("NEW CASES ON " + DateHelper.formatShortDate(fromDate));
			} else {
				epiCurveDateLabel.setValue("NEW CASES BETWEEN " + DateHelper.formatShortDate(fromDate) + 
						" AND " + DateHelper.formatShortDate(toDate));
			}
		}
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

		// Curve and date labels
		VerticalLayout epiCurveLabelLayout = new VerticalLayout();
		{
			epiCurveLabelLayout.setSizeUndefined();
			Label epiCurveLabel = new Label("Epidemiological Curve");
			epiCurveLabel.setSizeUndefined();
			CssStyles.style(epiCurveLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
			epiCurveLabelLayout.addComponent(epiCurveLabel);

			epiCurveDateLabel = new Label();
			epiCurveDateLabel.setSizeUndefined();
			CssStyles.style(epiCurveDateLabel, CssStyles.H4, CssStyles.VSPACE_TOP_NONE);
			updateDateLabel();
			epiCurveLabelLayout.addComponent(epiCurveDateLabel);
		}
		epiCurveHeaderLayout.addComponent(epiCurveLabelLayout);
		epiCurveHeaderLayout.setComponentAlignment(epiCurveLabelLayout, Alignment.BOTTOM_LEFT);
		epiCurveHeaderLayout.setExpandRatio(epiCurveLabelLayout, 1);

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
		Button expandEpiCurveButton = new Button("Expand epi curve", FontAwesome.EXPAND);
		CssStyles.style(expandEpiCurveButton, CssStyles.BUTTON_SUBTLE);
		expandEpiCurveButton.addStyleName(CssStyles.VSPACE_NONE);   
		Button collapseEpiCurveButton = new Button("Collapse epi curve", FontAwesome.COMPRESS);
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
	 * Builds a list that contains an object for each day between the from and to dates
	 * @return
	 */
	private List<Date> buildListOfFilteredDates() {
		List<Date> filteredDates = new ArrayList<>();
		if (dashboardDataProvider.getDateFilterOption() == DateFilterOption.DATE) {
			Date currentDate = new Date(dashboardDataProvider.getFromDate().getTime());
			while (!currentDate.after(dashboardDataProvider.getToDate())) {
				filteredDates.add(currentDate);
				currentDate = DateHelper.addDays(currentDate, 1);
			}
		} else {
			Date currentDate = DateHelper.getEpiWeekStart(dashboardDataProvider.getFromWeek());
			Date targetDate = DateHelper.getEpiWeekEnd(dashboardDataProvider.getToWeek());
			while (!currentDate.after(targetDate)) {
				filteredDates.add(currentDate);
				currentDate = DateHelper.addDays(currentDate, 1);
			}
		}

		return filteredDates;
	}

	public Label getEpiCurveDateLabel() {
		return epiCurveDateLabel;
	}

	public EpiCurveMode getEpiCurveMode() {
		return epiCurveMode;
	}

}
