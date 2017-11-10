package de.symeda.sormas.ui.dashboard;

import java.util.List;

import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.DashboardCase;
import de.symeda.sormas.api.event.DashboardEvent;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.sample.DashboardTestResult;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.task.DashboardTask;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.login.LoginHelper;

@SuppressWarnings("serial")
public class StatisticsComponent extends HorizontalLayout {
	
	private final DashboardView dashboardView;
	private StatisticsSubComponent myTasksComponent;
	private StatisticsSubComponent newCasesComponent;
	private StatisticsSubComponent newEventsComponent;
	private StatisticsSubComponent newTestResultsComponent;
	
	// "My Tasks" elements
	private StatisticsOverviewElement taskPriorityHigh;
	private StatisticsOverviewElement taskPriorityNormal;
	private StatisticsOverviewElement taskPriorityLow;
	
	// "New Cases" elements
	private StatisticsOverviewElement caseClassificationConfirmed;
	private StatisticsOverviewElement caseClassificationProbable;
	private StatisticsOverviewElement caseClassificationSuspect;
	private StatisticsOverviewElement caseClassificationNotACase;
	private StatisticsOverviewElement caseClassificationNotYetClassified;
	
	// "New Events" elements
	private StatisticsOverviewElement eventStatusConfirmed;
	private StatisticsOverviewElement eventStatusPossible;
	private StatisticsOverviewElement eventStatusNotAnEvent;	
	
	// "New Test Results" elements
	private StatisticsOverviewElement testResultPositive;
	private StatisticsOverviewElement testResultNegative;
	private StatisticsOverviewElement testResultPending;
	private StatisticsOverviewElement testResultIndeterminate;
	
	public StatisticsComponent(DashboardView dashboardView) {
		this.dashboardView = dashboardView;
		this.setWidth(100, Unit.PERCENTAGE);
		this.setMargin(true);

		addMyTasksComponent();
		addNewCasesComponent();
		addNewEventsComponent();
		addNewTestResultsComponent();
	}
	
	public void updateStatistics() {
		updateMyTasksComponent();
		updateNewCasesComponent();
		updateNewEventsComponent();
		updateNewTestResultsComponent();
	}
	
	private void addMyTasksComponent() {
		myTasksComponent = new StatisticsSubComponent();
		myTasksComponent.addStyleName("statistics-sub-component");
		
		// Header
		myTasksComponent.addHeader("My Tasks", null);
		
		// Overview
		myTasksComponent.addOverview();
		taskPriorityHigh = new StatisticsOverviewElement("High", "border-critical");
		myTasksComponent.addComponentToOverview(taskPriorityHigh);
		taskPriorityNormal = new StatisticsOverviewElement("Normal", "border-neutral");
		myTasksComponent.addComponentToOverview(taskPriorityNormal);
		taskPriorityLow = new StatisticsOverviewElement("Low", "border-positive");
		myTasksComponent.addComponentToOverview(taskPriorityLow);
		
		updateMyTasksComponent();
		addComponent(myTasksComponent);
	}
	
	private void updateMyTasksComponent() {
		dashboardView.updateDateLabel(myTasksComponent.getDateLabel());
		
		List<DashboardTask> dashboardTasks;
		if (dashboardView.getDateFilterOption() == DateFilterOption.DATE) {
			dashboardTasks = FacadeProvider.getTaskFacade().getAllPending(dashboardView.getFromDate(), dashboardView.getToDate(), LoginHelper.getCurrentUserAsReference().getUuid());
		} else {
			dashboardTasks = FacadeProvider.getTaskFacade().getAllPending(DateHelper.getEpiWeekStart(dashboardView.getFromWeek()), DateHelper.getEpiWeekEnd(dashboardView.getToWeek()), LoginHelper.getCurrentUserAsReference().getUuid());
		}
		
		int myTasksCount = dashboardTasks.size();		
		myTasksComponent.updateCountLabel(myTasksCount);
		
		int highPriorityCount = (int) dashboardTasks.stream().filter(t -> t.getPriority() == TaskPriority.HIGH).count();
		taskPriorityHigh.updateCountLabel(highPriorityCount);
		int normalPriorityCount = (int) dashboardTasks.stream().filter(t -> t.getPriority() == TaskPriority.NORMAL).count();
		taskPriorityNormal.updateCountLabel(normalPriorityCount);
		int lowPriorityCount = (int) dashboardTasks.stream().filter(t -> t.getPriority() == TaskPriority.LOW).count();
		taskPriorityLow.updateCountLabel(lowPriorityCount);
	}
	
	private void addNewCasesComponent() {
		newCasesComponent = new StatisticsSubComponent();
		newCasesComponent.addStyleName("statistics-sub-component");
		
		// Header
		newCasesComponent.addHeader("New Cases", null);
		
		// Overview
		newCasesComponent.addOverview();
		caseClassificationConfirmed = new StatisticsOverviewElement("Confirmed", "border-critical");
		newCasesComponent.addComponentToOverview(caseClassificationConfirmed);
		caseClassificationProbable = new StatisticsOverviewElement("Probable", "border-important");
		newCasesComponent.addComponentToOverview(caseClassificationProbable);
		caseClassificationSuspect = new StatisticsOverviewElement("Suspect", "border-relevant");
		newCasesComponent.addComponentToOverview(caseClassificationSuspect);
		caseClassificationNotACase = new StatisticsOverviewElement("Not A Case", "border-positive");
		newCasesComponent.addComponentToOverview(caseClassificationNotACase);
		caseClassificationNotYetClassified = new StatisticsOverviewElement("Not Yet Classified", "border-wayne");
		newCasesComponent.addComponentToOverview(caseClassificationNotYetClassified);
		
		updateNewCasesComponent();
		addComponent(newCasesComponent);
	}
	
	private void updateNewCasesComponent() {
		dashboardView.updateDateLabel(newCasesComponent.getDateLabel());
		
		List<DashboardCase> dashboardCases = dashboardView.getCases();
		
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
	}

	private void addNewEventsComponent() {
		newEventsComponent = new StatisticsSubComponent();
		newEventsComponent.addStyleName("statistics-sub-component");
		
		// Header
		newEventsComponent.addHeader("New Events", null);
		
		// Overview
		newEventsComponent.addOverview();
		eventStatusConfirmed = new StatisticsOverviewElement("Confirmed", "border-critical");
		newEventsComponent.addComponentToOverview(eventStatusConfirmed);
		eventStatusPossible = new StatisticsOverviewElement("Possible", "border-important");
		newEventsComponent.addComponentToOverview(eventStatusPossible);
		eventStatusNotAnEvent = new StatisticsOverviewElement("Not An Event", "border-positive");
		newEventsComponent.addComponentToOverview(eventStatusNotAnEvent);
		
		updateNewEventsComponent();
		addComponent(newEventsComponent);
	}
	
	private void updateNewEventsComponent() {
		dashboardView.updateDateLabel(newEventsComponent.getDateLabel());
		
		List<DashboardEvent> dashboardEvents;
		if (dashboardView.getDateFilterOption() == DateFilterOption.DATE) {
			dashboardEvents = FacadeProvider.getEventFacade().getNewEventsForDashboard(dashboardView.getDistrict(), dashboardView.getDisease(), dashboardView.getFromDate(), dashboardView.getToDate(), LoginHelper.getCurrentUser().getUuid());
		} else {
			dashboardEvents = FacadeProvider.getEventFacade().getNewEventsForDashboard(dashboardView.getDistrict(), dashboardView.getDisease(), DateHelper.getEpiWeekStart(dashboardView.getFromWeek()), DateHelper.getEpiWeekEnd(dashboardView.getToWeek()), LoginHelper.getCurrentUserAsReference().getUuid());
		}
		
		int newEventsCount = dashboardEvents.size();
		newEventsComponent.updateCountLabel(newEventsCount);
		
		int confirmedEventsCount = (int) dashboardEvents.stream().filter(e -> e.getEventStatus() == EventStatus.CONFIRMED).count();
		eventStatusConfirmed.updateCountLabel(confirmedEventsCount);
		int possibleEventsCount = (int) dashboardEvents.stream().filter(e -> e.getEventStatus() == EventStatus.POSSIBLE).count();
		eventStatusPossible.updateCountLabel(possibleEventsCount);
		int notAnEventEventsCount = (int) dashboardEvents.stream().filter(e -> e.getEventStatus() == EventStatus.NO_EVENT).count();
		eventStatusNotAnEvent.updateCountLabel(notAnEventEventsCount);
	}
	
	private void addNewTestResultsComponent() {
		newTestResultsComponent = new StatisticsSubComponent();
		newTestResultsComponent.addStyleName("statistics-sub-component");
		newTestResultsComponent.addStyleName("statistics-sub-component-right");
		
		// Header
		newTestResultsComponent.addHeader("New Test Results", null);
		
		// Overview
		newTestResultsComponent.addOverview();
		testResultPositive = new StatisticsOverviewElement("Positive", "border-critical");
		newTestResultsComponent.addComponentToOverview(testResultPositive);
		testResultNegative = new StatisticsOverviewElement("Negative", "border-positive");
		newTestResultsComponent.addComponentToOverview(testResultNegative);
		testResultPending = new StatisticsOverviewElement("Pending", "border-important");
		newTestResultsComponent.addComponentToOverview(testResultPending);
		testResultIndeterminate = new StatisticsOverviewElement("Indeterminate", "border-wayne");
		newTestResultsComponent.addComponentToOverview(testResultIndeterminate);
		
		updateNewTestResultsComponent();
		addComponent(newTestResultsComponent);
	}
	
	private void updateNewTestResultsComponent() {
		dashboardView.updateDateLabel(newTestResultsComponent.getDateLabel());
		
		List<DashboardTestResult> dashboardTestResults;
		if (dashboardView.getDateFilterOption() == DateFilterOption.DATE) {
			dashboardTestResults = FacadeProvider.getSampleTestFacade().getNewTestResultsForDashboard(dashboardView.getDistrict(), dashboardView.getDisease(), dashboardView.getFromDate(), dashboardView.getToDate(), LoginHelper.getCurrentUser().getUuid());
		} else {
			dashboardTestResults = FacadeProvider.getSampleTestFacade().getNewTestResultsForDashboard(dashboardView.getDistrict(), dashboardView.getDisease(), DateHelper.getEpiWeekStart(dashboardView.getFromWeek()), DateHelper.getEpiWeekEnd(dashboardView.getToWeek()), LoginHelper.getCurrentUserAsReference().getUuid());
		}
		
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
	}
	
}
