package de.symeda.sormas.ui.dashboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.DashboardCase;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.event.DashboardEvent;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.DashboardSample;
import de.symeda.sormas.api.sample.DashboardTestResult;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.task.DashboardTask;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.ui.dashboard.SvgCircleElement.SvgCircleElementPart;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class StatisticsComponent extends VerticalLayout {

	private final DashboardDataProvider dashboardDataProvider;
	private HorizontalLayout subComponentsLayout;
	private StatisticsSubComponent myTasksComponent;
	private StatisticsSubComponent newCasesComponent;
	private StatisticsSubComponent newEventsComponent;
	private StatisticsSubComponent newTestResultsComponent;
	private Button showMoreButton;
	private Button showLessButton;

	private Disease previousDisease;
	private Disease currentDisease;

	// "My Tasks" elements
	private StatisticsCountElement taskPriorityHigh;
	private StatisticsCountElement taskPriorityNormal;
	private StatisticsCountElement taskPriorityLow;
	private StatisticsCountElement taskStatusPending;
	private StatisticsCountElement taskStatusDone;
	private StatisticsPercentageElement taskStatusPendingPercentage;
	private StatisticsPercentageElement taskStatusDonePercentage;
	private StatisticsPercentageElement taskStatusRemovedPercentage;
	private StatisticsPercentageElement taskStatusNotExecutablePercentage;
	private SvgCircleElement taskStatusCircleGraph;

	// "New Cases" elements
	private StatisticsCountElement caseClassificationConfirmed;
	private StatisticsCountElement caseClassificationProbable;
	private StatisticsCountElement caseClassificationSuspect;
	private StatisticsCountElement caseClassificationNotACase;
	private StatisticsCountElement caseClassificationNotYetClassified;
	private StatisticsGrowthElement caseInvestigationStatusDone;
	private StatisticsGrowthElement caseInvestigationStatusDiscarded;
	private StatisticsGrowthElement caseFatalities;
	private Label caseFatalityRateLabel;
	private Label caseFatalityRateCaption;
	
	// "New Events" elements
	private StatisticsCountElement eventStatusConfirmed;
	private StatisticsCountElement eventStatusPossible;
	private StatisticsCountElement eventStatusNotAnEvent;	
	private SvgCircleElement eventTypeRumorCircleGraph;
	private SvgCircleElement eventTypeOutbreakCircleGraph;
	private StatisticsPercentageElement eventStatusConfirmedPercentage;
	private StatisticsPercentageElement eventStatusPossiblePercentage;
	private StatisticsPercentageElement eventStatusNotAnEventPercentage;

	// "New Test Results" elements
	private StatisticsCountElement testResultPositive;
	private StatisticsCountElement testResultNegative;
	private StatisticsCountElement testResultPending;
	private StatisticsCountElement testResultIndeterminate;
	private SvgCircleElement testResultShippedCircleGraph;
	private SvgCircleElement testResultReceivedCircleGraph;
	private StatisticsPercentageElement testResultPositivePercentage;
	private StatisticsPercentageElement testResultNegativePercentage;
	private StatisticsPercentageElement testResultPendingPercentage;
	private StatisticsPercentageElement testResultIndeterminatePercentage;

	public StatisticsComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
		this.setWidth(100, Unit.PERCENTAGE);
		this.setMargin(true);

		subComponentsLayout = new HorizontalLayout();
		subComponentsLayout.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(subComponentsLayout, CssStyles.VSPACE_NONE);
		addSeparatorLabel();
		addMyTasksComponent();
		addSeparatorLabel();
		addNewCasesComponent();
		addSeparatorLabel();
		addNewEventsComponent();
		addSeparatorLabel();
		addNewTestResultsComponent();
		addSeparatorLabel();
		addComponent(subComponentsLayout);
		if (Disease.values().length > 6) {
			addShowMoreAndLessButtons();
		}
	}
	
	private void addSeparatorLabel() {
		Label separator = new Label();
		separator.setHeight(100, Unit.PERCENTAGE);
		separator.setWidthUndefined();
		CssStyles.style(separator, CssStyles.SEPARATOR_VERTICAL_BROAD);
		subComponentsLayout.addComponent(separator);
		subComponentsLayout.setExpandRatio(separator, 0);
	}

	public void updateStatistics(Disease disease) {
		previousDisease = currentDisease;
		currentDisease = disease;

		if (currentDisease != null) {
			showMoreButton.setVisible(false);
			showLessButton.setVisible(false);
		} else if (!showLessButton.isVisible() && !showMoreButton.isVisible()) {
			if (!showMoreButton.isVisible() && !showLessButton.isVisible()) {
				showMoreButton.setVisible(true);
			}
		}

		updateMyTasksComponent();
		updateNewCasesComponent(currentDisease == null ? calculateAmountOfDisplayedDiseases() : 0);
		updateNewEventsComponent(currentDisease == null ? calculateAmountOfDisplayedDiseases() : 0);
		updateNewTestResultsComponent(currentDisease == null ? calculateAmountOfDisplayedDiseases() : 0);
	}

	private void addShowMoreAndLessButtons() {
		showMoreButton = new Button("Show All Diseases", FontAwesome.CHEVRON_DOWN);
		CssStyles.style(showMoreButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE);
		showLessButton = new Button("Show First 6 Diseases", FontAwesome.CHEVRON_UP);
		CssStyles.style(showLessButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE);

		showMoreButton.addClickListener(e -> {
			showMoreButton.setVisible(false);
			showLessButton.setVisible(true);
			updateStatistics(currentDisease);
		});

		showLessButton.addClickListener(e -> {
			showLessButton.setVisible(false);
			showMoreButton.setVisible(true);
			updateStatistics(currentDisease);
		});

		addComponent(showMoreButton);
		addComponent(showLessButton);
		setComponentAlignment(showMoreButton, Alignment.MIDDLE_CENTER);
		setComponentAlignment(showLessButton, Alignment.MIDDLE_CENTER);
		showLessButton.setVisible(false);
	}

	private void addMyTasksComponent() {
		myTasksComponent = new StatisticsSubComponent();

		// Header
		myTasksComponent.addHeader("My Tasks", null);

		// Count layout
		myTasksComponent.addCountLayout();
		taskPriorityHigh = new StatisticsCountElement("High", CssStyles.LABEL_BAR_TOP_CRITICAL);
		myTasksComponent.addComponentToCountLayout(taskPriorityHigh);
		taskPriorityNormal = new StatisticsCountElement("Normal", CssStyles.LABEL_BAR_TOP_NEUTRAL);
		myTasksComponent.addComponentToCountLayout(taskPriorityNormal);
		taskPriorityLow = new StatisticsCountElement("Low", CssStyles.LABEL_BAR_TOP_MINOR);
		myTasksComponent.addComponentToCountLayout(taskPriorityLow);

		Label separator = new Label();
		separator.setHeight(100, Unit.PERCENTAGE);
		CssStyles.style(separator, CssStyles.SEPARATOR_VERTICAL);
		myTasksComponent.addComponentToCountLayout(separator);

		taskStatusPending = new StatisticsCountElement("Pending", CssStyles.LABEL_BAR_TOP_IMPORTANT);
		myTasksComponent.addComponentToCountLayout(taskStatusPending);
		taskStatusDone = new StatisticsCountElement("Done", CssStyles.LABEL_BAR_TOP_POSITIVE);
		myTasksComponent.addComponentToCountLayout(taskStatusDone);

		// Content
		myTasksComponent.addTwoColumnsContent(false, 60);
		taskStatusPendingPercentage = new StatisticsPercentageElement("Pending", CssStyles.SVG_FILL_IMPORTANT);
		myTasksComponent.addComponentToLeftContentColumn(taskStatusPendingPercentage);
		taskStatusDonePercentage = new StatisticsPercentageElement("Done", CssStyles.SVG_FILL_POSITIVE);
		myTasksComponent.addComponentToLeftContentColumn(taskStatusDonePercentage);
		taskStatusRemovedPercentage = new StatisticsPercentageElement("Removed", CssStyles.SVG_FILL_CRITICAL);
		myTasksComponent.addComponentToLeftContentColumn(taskStatusRemovedPercentage);
		taskStatusNotExecutablePercentage = new StatisticsPercentageElement("Not Executable", CssStyles.SVG_FILL_MINOR);
		myTasksComponent.addComponentToLeftContentColumn(taskStatusNotExecutablePercentage);

		taskStatusCircleGraph = new SvgCircleElement(false);
		myTasksComponent.addComponentToRightContentColumn(taskStatusCircleGraph);

		subComponentsLayout.addComponent(myTasksComponent);
		subComponentsLayout.setExpandRatio(myTasksComponent, 25);
	}

	private void updateMyTasksComponent() {
		List<DashboardTask> dashboardTasks = dashboardDataProvider.getTasks();
		List<DashboardTask> dashboardPendingTasks = dashboardDataProvider.getPendingTasks();

		int pendingTasksCount = dashboardPendingTasks.size();		
		myTasksComponent.updateCountLabel(pendingTasksCount);

		int highPriorityCount = (int) dashboardPendingTasks.stream().filter(t -> t.getPriority() == TaskPriority.HIGH).count();
		taskPriorityHigh.updateCountLabel(highPriorityCount);
		int normalPriorityCount = (int) dashboardPendingTasks.stream().filter(t -> t.getPriority() == TaskPriority.NORMAL).count();
		taskPriorityNormal.updateCountLabel(normalPriorityCount);
		int lowPriorityCount = (int) dashboardPendingTasks.stream().filter(t -> t.getPriority() == TaskPriority.LOW).count();
		taskPriorityLow.updateCountLabel(lowPriorityCount);

		int doneCount = (int) dashboardTasks.stream().filter(t -> t.getTaskStatus() == TaskStatus.DONE).count();
		int removedCount = (int) dashboardTasks.stream().filter(t -> t.getTaskStatus() == TaskStatus.REMOVED).count();
		int notExecutableCount = (int) dashboardTasks.stream().filter(t -> t.getTaskStatus() == TaskStatus.NOT_EXECUTABLE).count();
		int totalCount = pendingTasksCount + doneCount + removedCount + notExecutableCount;
		int pendingPercentage = totalCount == 0 ? 0 : 
			new BigDecimal(pendingTasksCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalCount), RoundingMode.HALF_UP).intValue();
		int donePercentage = totalCount == 0 ? 0 :
			new BigDecimal(doneCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalCount), RoundingMode.HALF_UP).intValue();
		int removedPercentage = totalCount == 0 ? 0 :
			new BigDecimal(removedCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalCount), RoundingMode.HALF_UP).intValue();
		int notExecutablePercentage = totalCount == 0 ? 0 :
			new BigDecimal(notExecutableCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalCount), RoundingMode.HALF_UP).intValue();

		taskStatusPending.updateCountLabel(pendingTasksCount);
		taskStatusDone.updateCountLabel(doneCount);

		taskStatusPendingPercentage.updatePercentageValue(pendingPercentage);
		taskStatusDonePercentage.updatePercentageValue(donePercentage);
		taskStatusRemovedPercentage.updatePercentageValue(removedPercentage);
		taskStatusNotExecutablePercentage.updatePercentageValue(notExecutablePercentage);

		SvgCircleElementPart pendingPart = taskStatusCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_IMPORTANT, pendingPercentage);
		SvgCircleElementPart donePart = taskStatusCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_POSITIVE, donePercentage);
		SvgCircleElementPart removedPart = taskStatusCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_CRITICAL, removedPercentage);
		SvgCircleElementPart notExecutablePart = taskStatusCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_MINOR, notExecutablePercentage);
		taskStatusCircleGraph.updateSvg(pendingPart, donePart, removedPart, notExecutablePart);
	}

	private void addNewCasesComponent() {
		newCasesComponent = new StatisticsSubComponent();

		// Header
		newCasesComponent.addHeader("New Cases", null);

		// Count layout
		newCasesComponent.addCountLayout();
		caseClassificationConfirmed = new StatisticsCountElement("Confirmed", CssStyles.LABEL_BAR_TOP_CRITICAL);
		newCasesComponent.addComponentToCountLayout(caseClassificationConfirmed);
		caseClassificationProbable = new StatisticsCountElement("Probable", CssStyles.LABEL_BAR_TOP_IMPORTANT);
		newCasesComponent.addComponentToCountLayout(caseClassificationProbable);
		caseClassificationSuspect = new StatisticsCountElement("Suspect", CssStyles.LABEL_BAR_TOP_RELEVANT);
		newCasesComponent.addComponentToCountLayout(caseClassificationSuspect);
		caseClassificationNotACase = new StatisticsCountElement("Not A Case", CssStyles.LABEL_BAR_TOP_POSITIVE);
		newCasesComponent.addComponentToCountLayout(caseClassificationNotACase);
		caseClassificationNotYetClassified = new StatisticsCountElement("Not Yet Classified", CssStyles.LABEL_BAR_TOP_MINOR);
		newCasesComponent.addComponentToCountLayout(caseClassificationNotYetClassified);

		// Content
		newCasesComponent.addContent();
		caseInvestigationStatusDone = new StatisticsGrowthElement("Investigated", CssStyles.COLOR_SECONDARY, Alignment.MIDDLE_LEFT);
		caseInvestigationStatusDiscarded = new StatisticsGrowthElement("Discarded", CssStyles.COLOR_SECONDARY, Alignment.MIDDLE_LEFT);
		caseFatalities = new StatisticsGrowthElement("Fatalities", CssStyles.COLOR_CRITICAL, Alignment.MIDDLE_RIGHT);
		caseFatalityRateLabel = new Label();
		CssStyles.style(caseFatalityRateLabel, CssStyles.SIZE_LARGE, CssStyles.COLOR_PRIMARY, CssStyles.TEXT_UPPERCASE, CssStyles.TEXT_BOLD);
		caseFatalityRateCaption = new Label("Case Fatality Rate");
		CssStyles.style(caseFatalityRateCaption, CssStyles.SIZE_MEDIUM, CssStyles.COLOR_CRITICAL, CssStyles.TEXT_UPPERCASE, CssStyles.TEXT_BOLD);

		subComponentsLayout.addComponent(newCasesComponent);
		subComponentsLayout.setExpandRatio(newCasesComponent, 25);
	}

	private void updateNewCasesComponent(int amountOfDisplayedDiseases) {
		List<DashboardCase> dashboardCases = dashboardDataProvider.getCases();
		List<DashboardCase> previousDashboardCases = dashboardDataProvider.getPreviousCases();

		int newCasesCount = dashboardCases.size();
		newCasesComponent.updateCountLabel(newCasesCount);

		int confirmedCasesCount = (int) dashboardCases.stream().filter(c -> c.getCaseClassification() == CaseClassification.CONFIRMED).count();
		caseClassificationConfirmed.updateCountLabel(confirmedCasesCount);
		int probableCasesCount = (int) dashboardCases.stream().filter(c -> c.getCaseClassification() == CaseClassification.PROBABLE).count();
		caseClassificationProbable.updateCountLabel(probableCasesCount);
		int suspectCasesCount = (int) dashboardCases.stream().filter(c -> c.getCaseClassification() == CaseClassification.SUSPECT).count();
		caseClassificationSuspect.updateCountLabel(suspectCasesCount);
		int notACaseCasesCount = (int) dashboardCases.stream().filter(c -> c.getCaseClassification() == CaseClassification.NO_CASE).count();
		caseClassificationNotACase.updateCountLabel(notACaseCasesCount);
		int notYetClassifiedCasesCount = (int) dashboardCases.stream().filter(c -> c.getCaseClassification() == CaseClassification.NOT_CLASSIFIED).count();
		caseClassificationNotYetClassified.updateCountLabel(notYetClassifiedCasesCount);

		// Remove and re-create content layout if the disease filter has been applied or set to null
		if ((currentDisease == null && previousDisease != null) || (previousDisease == null && currentDisease != null)) {
			newCasesComponent.removeAllComponentsFromContent();
			newCasesComponent.addComponentToContent(caseInvestigationStatusDone);
			newCasesComponent.addComponentToContent(caseInvestigationStatusDiscarded);
			Label separator = new Label();
			separator.setWidth(100, Unit.PERCENTAGE);
			separator.setHeightUndefined();
			CssStyles.style(separator, CssStyles.SEPARATOR_HORIZONTAL, CssStyles.VSPACE_4);
			newCasesComponent.addComponentToContent(separator);
			HorizontalLayout fatalityLayout = new HorizontalLayout();
			fatalityLayout.setWidth(100, Unit.PERCENTAGE);
			VerticalLayout fatalityRateLayout = new VerticalLayout();
			fatalityRateLayout.addComponent(caseFatalityRateLabel);
			fatalityRateLayout.addComponent(caseFatalityRateCaption);
			fatalityLayout.addComponent(fatalityRateLayout);
			fatalityLayout.addComponent(caseFatalities);
			newCasesComponent.addComponentToContent(fatalityLayout);
		}

		if (currentDisease == null) {
			// Remove all children of the content layout
			newCasesComponent.removeAllComponentsFromContent();

			// Create a map with all diseases as keys and their respective case counts as values
			Map<Disease, Integer> diseaseMap = new TreeMap<Disease, Integer>();
			for (Disease disease : Disease.values()) {
				diseaseMap.put(disease, (int) dashboardCases.stream().filter(c -> c.getDisease() == disease).count());
			}

			// Create a list from this map that sorts the entries by case counts
			List<Map.Entry<Disease, Integer>> sortedDiseaseList = createSortedDiseaseList(diseaseMap);

			// Create a new StatisticsDiseaseElement for every disease, automatically sorting them by case count
			for (int i = 0; i < amountOfDisplayedDiseases; i++) {
				Map.Entry<Disease, Integer> mapEntry = sortedDiseaseList.get(i);
				int previousDiseaseCount = (int) previousDashboardCases.stream().filter(c -> c.getDisease() == mapEntry.getKey()).count();
				StatisticsDiseaseElement diseaseElement = new StatisticsDiseaseElement(mapEntry.getKey().toString(), mapEntry.getValue(), previousDiseaseCount);
				newCasesComponent.addComponentToContent(diseaseElement);
			}
		} else {
			int investigatedCasesCount = (int) dashboardCases.stream().filter(c -> c.getInvestigationStatus() == InvestigationStatus.DONE).count();
			int previousInvestigatedCasesCount = (int) previousDashboardCases.stream().filter(c -> c.getInvestigationStatus() == InvestigationStatus.DONE).count();
			int discardedCasesCount = (int) dashboardCases.stream().filter(c -> c.getInvestigationStatus() == InvestigationStatus.DISCARDED).count();
			int previousDiscardedCasesCount = (int) previousDashboardCases.stream().filter(c -> c.getInvestigationStatus() == InvestigationStatus.DISCARDED).count();
			int fatalitiesCount = (int) dashboardCases.stream().filter(c -> c.getCasePersonCondition() == PresentCondition.DEAD).count();
			int previousFatalitiesCount = (int) previousDashboardCases.stream().filter(c -> c.getCasePersonCondition() == PresentCondition.DEAD).count();
			
			float investigatedCasesGrowth = investigatedCasesCount == 0 ? -previousInvestigatedCasesCount * 100 : 
				previousInvestigatedCasesCount == 0 ? investigatedCasesCount * 100 :
				new BigDecimal(investigatedCasesCount).subtract(new BigDecimal(previousInvestigatedCasesCount)).divide(new BigDecimal(investigatedCasesCount), 1, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).floatValue();
			float discardedCasesGrowth = discardedCasesCount == 0 ? -previousDiscardedCasesCount * 100 : 
				previousDiscardedCasesCount == 0 ? discardedCasesCount * 100 :
				new BigDecimal(discardedCasesCount).subtract(new BigDecimal(previousDiscardedCasesCount)).divide(new BigDecimal(discardedCasesCount), 1, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).floatValue();
			float fatalitiesGrowth = fatalitiesCount == 0 ? -previousFatalitiesCount * 100 : 
				previousFatalitiesCount == 0 ? fatalitiesCount * 100 :
				new BigDecimal(fatalitiesCount).subtract(new BigDecimal(previousFatalitiesCount)).divide(new BigDecimal(fatalitiesCount), 1, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).floatValue();
			float fatalityRate = fatalitiesCount == 0 ? 0 : newCasesCount == 0 ? 0 :
				new BigDecimal(fatalitiesCount).multiply(new BigDecimal(100)).divide(new BigDecimal(newCasesCount), 1, RoundingMode.HALF_UP).floatValue();
			
			caseInvestigationStatusDone.update(investigatedCasesCount, investigatedCasesGrowth, true);
			caseInvestigationStatusDiscarded.update(discardedCasesCount, discardedCasesGrowth, false);
			caseFatalities.update(fatalitiesCount, fatalitiesGrowth, false);
			caseFatalityRateLabel.setValue("CFR " + (fatalityRate % 1.0 != 0 ? String.format("%s", Float.toString(fatalityRate)) + "%" : String.format("%.0f", fatalityRate) + "%"));
		}
	}

	private void addNewEventsComponent() {
		newEventsComponent = new StatisticsSubComponent();

		// Header
		newEventsComponent.addHeader("New Events", null);

		// Count layout
		newEventsComponent.addCountLayout();
		eventStatusConfirmed = new StatisticsCountElement("Confirmed", CssStyles.LABEL_BAR_TOP_CRITICAL);
		newEventsComponent.addComponentToCountLayout(eventStatusConfirmed);
		eventStatusPossible = new StatisticsCountElement("Possible", CssStyles.LABEL_BAR_TOP_IMPORTANT);
		newEventsComponent.addComponentToCountLayout(eventStatusPossible);
		eventStatusNotAnEvent = new StatisticsCountElement("Not An Event", CssStyles.LABEL_BAR_TOP_POSITIVE);
		newEventsComponent.addComponentToCountLayout(eventStatusNotAnEvent);

		// Content
		newEventsComponent.addContent();
		eventTypeRumorCircleGraph = new SvgCircleElement(true);
		eventTypeOutbreakCircleGraph = new SvgCircleElement(true);
		eventStatusConfirmedPercentage = new StatisticsPercentageElement("Confirmed", CssStyles.SVG_FILL_CRITICAL);
		eventStatusPossiblePercentage = new StatisticsPercentageElement("Possible", CssStyles.SVG_FILL_IMPORTANT);
		eventStatusNotAnEventPercentage = new StatisticsPercentageElement("Not An Event", CssStyles.SVG_FILL_MINOR);

		subComponentsLayout.addComponent(newEventsComponent);
		subComponentsLayout.setExpandRatio(newEventsComponent, 25);
	}

	private void updateNewEventsComponent(int amountOfDisplayedDiseases) {
		List<DashboardEvent> dashboardEvents = dashboardDataProvider.getEvents();
		List<DashboardEvent> previousDashboardEvents = dashboardDataProvider.getPreviousEvents();

		int newEventsCount = dashboardEvents.size();
		newEventsComponent.updateCountLabel(newEventsCount);

		int confirmedEventsCount = (int) dashboardEvents.stream().filter(e -> e.getEventStatus() == EventStatus.CONFIRMED).count();
		eventStatusConfirmed.updateCountLabel(confirmedEventsCount);
		int possibleEventsCount = (int) dashboardEvents.stream().filter(e -> e.getEventStatus() == EventStatus.POSSIBLE).count();
		eventStatusPossible.updateCountLabel(possibleEventsCount);
		int notAnEventEventsCount = (int) dashboardEvents.stream().filter(e -> e.getEventStatus() == EventStatus.NO_EVENT).count();
		eventStatusNotAnEvent.updateCountLabel(notAnEventEventsCount);

		// Remove and re-create content layout if the disease filter has been applied or set to null
		if ((currentDisease == null && previousDisease != null) || (previousDisease == null && currentDisease != null)) {
			newEventsComponent.removeContent();
			if (currentDisease != null) {
				newEventsComponent.addTwoColumnsContent(true, 20);
				newEventsComponent.addComponentToLeftContentColumn(eventTypeRumorCircleGraph);
				Label rumorCircleLabel = new Label("Rumor");
				rumorCircleLabel.setWidth(100, Unit.PERCENTAGE);
				CssStyles.style(rumorCircleLabel, CssStyles.COLOR_SECONDARY, CssStyles.SIZE_MEDIUM, CssStyles.TEXT_BOLD, CssStyles.TEXT_UPPERCASE, CssStyles.VSPACE_3, CssStyles.ALIGN_CENTER);
				newEventsComponent.addComponentToLeftContentColumn(rumorCircleLabel);
				newEventsComponent.addComponentToLeftContentColumn(eventTypeOutbreakCircleGraph);
				Label outbreakCircleLabel = new Label("Outbreak");
				outbreakCircleLabel.setWidth(100, Unit.PERCENTAGE);
				CssStyles.style(outbreakCircleLabel, CssStyles.COLOR_SECONDARY, CssStyles.SIZE_MEDIUM, CssStyles.TEXT_BOLD, CssStyles.TEXT_UPPERCASE, CssStyles.VSPACE_3, CssStyles.ALIGN_CENTER);
				newEventsComponent.addComponentToLeftContentColumn(outbreakCircleLabel);
				newEventsComponent.addComponentToRightContentColumn(eventStatusConfirmedPercentage);
				newEventsComponent.addComponentToRightContentColumn(eventStatusPossiblePercentage);
				newEventsComponent.addComponentToRightContentColumn(eventStatusNotAnEventPercentage);
			} else {
				newEventsComponent.addContent();
			}
		}

		if (currentDisease == null) {
			// Remove all children of the content layout
			newEventsComponent.removeAllComponentsFromContent();
	
			// Create a map with all diseases as keys and their respective event counts as values
			Map<Disease, Integer> diseaseMap = new TreeMap<Disease, Integer>();
			for (Disease disease : Disease.values()) {
				diseaseMap.put(disease, (int) dashboardEvents.stream().filter(e -> e.getDisease() == disease).count());
			}
	
			// Create a list from this map that sorts the entries by event counts
			List<Map.Entry<Disease, Integer>> sortedDiseaseList = createSortedDiseaseList(diseaseMap);
	
			// Create a new StatisticsDiseaseElement for every disease, automatically sorting them by event count
			for (int i = 0; i < amountOfDisplayedDiseases; i++) {
				Map.Entry<Disease, Integer> mapEntry = sortedDiseaseList.get(i);
				int previousDiseaseCount = (int) previousDashboardEvents.stream().filter(e -> e.getDisease() == mapEntry.getKey()).count();
				StatisticsDiseaseElement diseaseElement = new StatisticsDiseaseElement(mapEntry.getKey().toString(), mapEntry.getValue(), previousDiseaseCount);
				newEventsComponent.addComponentToContent(diseaseElement);
			}
		} else {
			int rumorsCount = (int) dashboardEvents.stream().filter(e -> e.getEventType() == EventType.RUMOR).count();
			int outbreaksCount = (int) dashboardEvents.stream().filter(e -> e.getEventType() == EventType.OUTBREAK).count();
			int totalTypeCount = rumorsCount + outbreaksCount;
			int rumorsPercentage = totalTypeCount == 0 ? 0 : 
				new BigDecimal(rumorsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalTypeCount), RoundingMode.HALF_UP).intValue();
			int outbreaksPercentage = totalTypeCount == 0 ? 0 :
				new BigDecimal(outbreaksCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalTypeCount), RoundingMode.HALF_UP).intValue();
			int totalStatusCount = confirmedEventsCount + possibleEventsCount + notAnEventEventsCount;
			int confirmedEventsPercentage = totalStatusCount == 0 ? 0 :
				new BigDecimal(confirmedEventsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalStatusCount), RoundingMode.HALF_UP).intValue();
			int possibleEventsPercentage = totalStatusCount == 0 ? 0 :
				new BigDecimal(possibleEventsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalStatusCount), RoundingMode.HALF_UP).intValue();
			int notAnEventEventsPercentage = totalStatusCount == 0 ? 0 :
				new BigDecimal(notAnEventEventsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalStatusCount), RoundingMode.HALF_UP).intValue();
			
			SvgCircleElementPart rumorsPart = eventTypeRumorCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_IMPORTANT, rumorsPercentage);
			SvgCircleElementPart rumorsBackgroundPart = eventTypeRumorCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_BACKGROUND, 100 - rumorsPercentage);
			eventTypeRumorCircleGraph.updateSvg(rumorsPercentage, rumorsPart, rumorsBackgroundPart);
			SvgCircleElementPart outbreaksPart = eventTypeOutbreakCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_CRITICAL, outbreaksPercentage);
			SvgCircleElementPart outbreaksBackgroundPart = eventTypeOutbreakCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_BACKGROUND, 100 - outbreaksPercentage);
			eventTypeOutbreakCircleGraph.updateSvg(outbreaksPercentage, outbreaksPart, outbreaksBackgroundPart);
			eventStatusConfirmedPercentage.updatePercentageValue(confirmedEventsPercentage);
			eventStatusPossiblePercentage.updatePercentageValue(possibleEventsPercentage);
			eventStatusNotAnEventPercentage.updatePercentageValue(notAnEventEventsPercentage);
		}
	}

	private void addNewTestResultsComponent() {
		newTestResultsComponent = new StatisticsSubComponent();

		// Header
		newTestResultsComponent.addHeader("New Test Results", null);

		// Count layout
		newTestResultsComponent.addCountLayout();
		testResultPositive = new StatisticsCountElement("Positive", CssStyles.LABEL_BAR_TOP_CRITICAL);
		newTestResultsComponent.addComponentToCountLayout(testResultPositive);
		testResultNegative = new StatisticsCountElement("Negative", CssStyles.LABEL_BAR_TOP_POSITIVE);
		newTestResultsComponent.addComponentToCountLayout(testResultNegative);
		testResultPending = new StatisticsCountElement("Pending", CssStyles.LABEL_BAR_TOP_IMPORTANT);
		newTestResultsComponent.addComponentToCountLayout(testResultPending);
		testResultIndeterminate = new StatisticsCountElement("Indeterminate", CssStyles.LABEL_BAR_TOP_MINOR);
		newTestResultsComponent.addComponentToCountLayout(testResultIndeterminate);

		// Content
		newTestResultsComponent.addContent();
		testResultShippedCircleGraph = new SvgCircleElement(true);
		testResultReceivedCircleGraph = new SvgCircleElement(true);
		testResultPositivePercentage = new StatisticsPercentageElement("Positive", CssStyles.SVG_FILL_CRITICAL);
		testResultNegativePercentage = new StatisticsPercentageElement("Negative", CssStyles.SVG_FILL_POSITIVE);
		testResultPendingPercentage = new StatisticsPercentageElement("Pending", CssStyles.SVG_FILL_IMPORTANT);
		testResultIndeterminatePercentage = new StatisticsPercentageElement("Indeterminate", CssStyles.SVG_FILL_MINOR);

		subComponentsLayout.addComponent(newTestResultsComponent);
		subComponentsLayout.setExpandRatio(newTestResultsComponent, 25);
	}

	private void updateNewTestResultsComponent(int amountOfDisplayedDiseases) {
		List<DashboardTestResult> dashboardTestResults = dashboardDataProvider.getTestResults();
		List<DashboardTestResult> previousDashboardTestResults = dashboardDataProvider.getPreviousTestResults();

		int newTestResultsCount = dashboardTestResults.size();
		newTestResultsComponent.updateCountLabel(newTestResultsCount);

		int positiveTestResultsCount = (int) dashboardTestResults.stream().filter(r -> r.getTestResult() == SampleTestResultType.POSITIVE).count();
		testResultPositive.updateCountLabel(positiveTestResultsCount);
		int negativeTestResultsCount = (int) dashboardTestResults.stream().filter(r -> r.getTestResult() == SampleTestResultType.NEGATIVE).count();
		testResultNegative.updateCountLabel(negativeTestResultsCount);
		int pendingTestResultsCount = (int) dashboardTestResults.stream().filter(r -> r.getTestResult() == SampleTestResultType.PENDING).count();
		testResultPending.updateCountLabel(pendingTestResultsCount);
		int indeterminateTestResultsCount = (int) dashboardTestResults.stream().filter(r -> r.getTestResult() == SampleTestResultType.INDETERMINATE).count();
		testResultIndeterminate.updateCountLabel(indeterminateTestResultsCount);

		if ((currentDisease == null && previousDisease != null) || (previousDisease == null && currentDisease != null)) {
			newTestResultsComponent.removeContent();
			if (currentDisease != null) {
				newTestResultsComponent.addTwoColumnsContent(true, 20);
				newTestResultsComponent.addComponentToLeftContentColumn(testResultShippedCircleGraph);
				Label shippedCircleLabel = new Label("Shipped");
				shippedCircleLabel.setWidth(100, Unit.PERCENTAGE);
				CssStyles.style(shippedCircleLabel, CssStyles.COLOR_SECONDARY, CssStyles.SIZE_MEDIUM, CssStyles.TEXT_BOLD, CssStyles.TEXT_UPPERCASE, CssStyles.VSPACE_3, CssStyles.ALIGN_CENTER);
				newTestResultsComponent.addComponentToLeftContentColumn(shippedCircleLabel);
				newTestResultsComponent.addComponentToLeftContentColumn(testResultReceivedCircleGraph);
				Label receivedCircleLabel = new Label("Received");
				receivedCircleLabel.setWidth(100, Unit.PERCENTAGE);
				CssStyles.style(receivedCircleLabel, CssStyles.COLOR_SECONDARY, CssStyles.SIZE_MEDIUM, CssStyles.TEXT_BOLD, CssStyles.TEXT_UPPERCASE, CssStyles.VSPACE_3, CssStyles.ALIGN_CENTER);
				newTestResultsComponent.addComponentToLeftContentColumn(receivedCircleLabel);
				newTestResultsComponent.addComponentToRightContentColumn(testResultPositivePercentage);
				newTestResultsComponent.addComponentToRightContentColumn(testResultNegativePercentage);
				newTestResultsComponent.addComponentToRightContentColumn(testResultPendingPercentage);
				newTestResultsComponent.addComponentToRightContentColumn(testResultIndeterminatePercentage);
			} else {
				newEventsComponent.addContent();
			}
		}
		
		if (currentDisease == null) {
			// Remove all children of the content layout
			newTestResultsComponent.removeAllComponentsFromContent();
	
			// Create a map with all diseases as keys and their respective positive test result counts as values
			Map<Disease, Integer> diseaseMap = new TreeMap<Disease, Integer>();
			for (Disease disease : Disease.values()) {
				diseaseMap.put(disease, (int) dashboardTestResults.stream().filter(r -> r.getTestResult() == SampleTestResultType.POSITIVE && r.getDisease() == disease).count());
			}
	
			// Create a list from this map that sorts the entries by test result counts
			List<Map.Entry<Disease, Integer>> sortedDiseaseList = createSortedDiseaseList(diseaseMap);
	
			// Create a new StatisticsDiseaseElement for every disease, automatically sorting them by test result count
			for (int i = 0; i < amountOfDisplayedDiseases; i++) {
				Map.Entry<Disease, Integer> mapEntry = sortedDiseaseList.get(i);
				int previousDiseaseCount = (int) previousDashboardTestResults.stream().filter(r -> r.getDisease() == mapEntry.getKey()).count();
				StatisticsDiseaseElement diseaseElement = new StatisticsDiseaseElement(mapEntry.getKey().toString(), mapEntry.getValue(), previousDiseaseCount);
				newTestResultsComponent.addComponentToContent(diseaseElement);
			}
		} else {
			List<DashboardSample> dashboardSamples = dashboardDataProvider.getSamples();
			int newSamplesCount = dashboardSamples.size();
			
			int shippedCount = (int) dashboardSamples.stream().filter(s -> s.isShipped()).count();
			int receivedCount = (int) dashboardSamples.stream().filter(s -> s.isReceived()).count();
			int shippedPercentage = newSamplesCount == 0 ? 0 :
				new BigDecimal(shippedCount).multiply(new BigDecimal(100)).divide(new BigDecimal(newSamplesCount), RoundingMode.HALF_UP).intValue();
			int receivedPercentage = newSamplesCount == 0 ? 0 :
				new BigDecimal(receivedCount).multiply(new BigDecimal(100)).divide(new BigDecimal(newSamplesCount), RoundingMode.HALF_UP).intValue();
			int positiveTestResultsPercentage = newTestResultsCount == 0 ? 0 :
				new BigDecimal(positiveTestResultsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(newTestResultsCount), RoundingMode.HALF_UP).intValue();
			int negativeTestResultsPercentage = newTestResultsCount == 0 ? 0 :
				new BigDecimal(negativeTestResultsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(newTestResultsCount), RoundingMode.HALF_UP).intValue();
			int pendingTestResultsPercentage = newTestResultsCount == 0 ? 0 :
				new BigDecimal(pendingTestResultsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(newTestResultsCount), RoundingMode.HALF_UP).intValue();
			int indeterminateTestResultsPercentage = newTestResultsCount == 0 ? 0 :
				new BigDecimal(indeterminateTestResultsCount).multiply(new BigDecimal(100)).divide(new BigDecimal(newTestResultsCount), RoundingMode.HALF_UP).intValue();
			
			SvgCircleElementPart shippedPart = testResultShippedCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_PRIMARY, shippedPercentage);
			SvgCircleElementPart shippedBackgroundPart = testResultShippedCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_BACKGROUND, 100 - shippedPercentage);
			testResultShippedCircleGraph.updateSvg(shippedPercentage, shippedPart, shippedBackgroundPart);
			SvgCircleElementPart receivedPart = testResultReceivedCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_PRIMARY, receivedPercentage);
			SvgCircleElementPart receivedBackgroundPart = testResultReceivedCircleGraph.new SvgCircleElementPart(CssStyles.SVG_STROKE_BACKGROUND, 100 - receivedPercentage);
			testResultReceivedCircleGraph.updateSvg(receivedPercentage, receivedPart, receivedBackgroundPart);
			testResultPositivePercentage.updatePercentageValue(positiveTestResultsPercentage);
			testResultNegativePercentage.updatePercentageValue(negativeTestResultsPercentage);
			testResultPendingPercentage.updatePercentageValue(pendingTestResultsPercentage);
			testResultIndeterminatePercentage.updatePercentageValue(indeterminateTestResultsPercentage);
		}
	}

	private List<Map.Entry<Disease, Integer>> createSortedDiseaseList(Map<Disease, Integer> diseaseMap) {
		List<Map.Entry<Disease, Integer>> sortedDiseaseList = new ArrayList<>(diseaseMap.entrySet());
		Collections.sort(sortedDiseaseList, new Comparator<Map.Entry<Disease, Integer>>() {
			public int compare(Map.Entry<Disease, Integer> e1, Map.Entry<Disease, Integer> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		return sortedDiseaseList;
	}

	private int calculateAmountOfDisplayedDiseases() {
		return showMoreButton.isVisible() ? 6 : showLessButton.isVisible() ? Disease.values().length : 0;
	}

}
