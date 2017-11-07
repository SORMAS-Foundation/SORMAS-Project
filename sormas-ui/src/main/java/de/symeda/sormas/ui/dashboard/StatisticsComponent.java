package de.symeda.sormas.ui.dashboard;

import java.util.List;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDashboardDto;
import de.symeda.sormas.api.event.EventDashboardDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.sample.TestResultDashboardDto;
import de.symeda.sormas.api.task.TaskDashboardDto;
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
		myTasksComponent.addHeader("My Tasks", new Image(null, new ThemeResource("img/dashboard-icon-placeholder.png")));
		
		// Overview
		myTasksComponent.addOverview();
		taskPriorityHigh = new StatisticsOverviewElement("High", "border-red");
		myTasksComponent.addComponentToOverview(taskPriorityHigh);
		taskPriorityNormal = new StatisticsOverviewElement("Normal", "border-light-blue");
		myTasksComponent.addComponentToOverview(taskPriorityNormal);
		taskPriorityLow = new StatisticsOverviewElement("Low", "border-green");
		myTasksComponent.addComponentToOverview(taskPriorityLow);
		
		updateMyTasksComponent();
		addComponent(myTasksComponent);
	}
	
	private void updateMyTasksComponent() {
		dashboardView.updateDateLabel(myTasksComponent.getDateLabel());
		
		List<TaskDashboardDto> taskDashboardDtos;
		if (dashboardView.getDateFilterOption() == DateFilterOption.DATE) {
			taskDashboardDtos = FacadeProvider.getTaskFacade().getAllPending(dashboardView.getFromDate(), dashboardView.getToDate(), LoginHelper.getCurrentUserAsReference().getUuid());
		} else {
			taskDashboardDtos = FacadeProvider.getTaskFacade().getAllPending(DateHelper.getEpiWeekStart(dashboardView.getFromWeek()), DateHelper.getEpiWeekEnd(dashboardView.getToWeek()), LoginHelper.getCurrentUserAsReference().getUuid());
		}
		
		int myTasksCount = taskDashboardDtos.size();		
		myTasksComponent.updateCountLabel(myTasksCount);
		
		int highPriorityCount = (int) taskDashboardDtos.stream().filter(t -> t.getPriority() == TaskPriority.HIGH).count();
		taskPriorityHigh.updateCountLabel(highPriorityCount);
		int normalPriorityCount = (int) taskDashboardDtos.stream().filter(t -> t.getPriority() == TaskPriority.NORMAL).count();
		taskPriorityNormal.updateCountLabel(normalPriorityCount);
		int lowPriorityCount = (int) taskDashboardDtos.stream().filter(t -> t.getPriority() == TaskPriority.LOW).count();
		taskPriorityLow.updateCountLabel(lowPriorityCount);
	}
	
	private void addNewCasesComponent() {
		newCasesComponent = new StatisticsSubComponent();
		newCasesComponent.addStyleName("statistics-sub-component");
		
		// Header
		newCasesComponent.addHeader("New Cases", new Image(null, new ThemeResource("img/dashboard-icon-placeholder.png")));
		
		// Overview
		newCasesComponent.addOverview();
		caseClassificationConfirmed = new StatisticsOverviewElement("Confirmed", "border-red");
		newCasesComponent.addComponentToOverview(caseClassificationConfirmed);
		caseClassificationProbable = new StatisticsOverviewElement("Probable", "border-orange");
		newCasesComponent.addComponentToOverview(caseClassificationProbable);
		caseClassificationSuspect = new StatisticsOverviewElement("Suspect", "border-yellow");
		newCasesComponent.addComponentToOverview(caseClassificationSuspect);
//		caseClassificationNotACase = new StatisticsOverviewElement("Not A Case", "border-green");
//		newCasesComponent.addComponentToOverview(caseClassificationNotACase);
		caseClassificationNotYetClassified = new StatisticsOverviewElement("Not Yet Classified", "border-grey");
		newCasesComponent.addComponentToOverview(caseClassificationNotYetClassified);
		
		updateNewCasesComponent();
		addComponent(newCasesComponent);
	}
	
	private void updateNewCasesComponent() {
		dashboardView.updateDateLabel(newCasesComponent.getDateLabel());
		
		List<CaseDashboardDto> caseDashboardDtos;
		if (dashboardView.getDateFilterOption() == DateFilterOption.DATE) {
			caseDashboardDtos = FacadeProvider.getCaseFacade().getNewCasesBetween(dashboardView.getFromDate(), dashboardView.getToDate(), LoginHelper.getCurrentUser().getUuid());
		} else {
			caseDashboardDtos = FacadeProvider.getCaseFacade().getNewCasesBetween(DateHelper.getEpiWeekStart(dashboardView.getFromWeek()), DateHelper.getEpiWeekEnd(dashboardView.getToWeek()), LoginHelper.getCurrentUserAsReference().getUuid());
		}
		
		int newCasesCount = caseDashboardDtos.size();
		newCasesComponent.updateCountLabel(newCasesCount);
		
		int confirmedCasesCount = (int) caseDashboardDtos.stream().filter(c -> c.getCaseClassification() == CaseClassification.CONFIRMED).count();
		caseClassificationConfirmed.updateCountLabel(confirmedCasesCount);
		int probableCasesCount = (int) caseDashboardDtos.stream().filter(c -> c.getCaseClassification() == CaseClassification.PROBABLE).count();
		caseClassificationProbable.updateCountLabel(probableCasesCount);
		int suspectCasesCount = (int) caseDashboardDtos.stream().filter(c -> c.getCaseClassification() == CaseClassification.SUSPECT).count();
		caseClassificationSuspect.updateCountLabel(suspectCasesCount);
//		int notACaseCasesCount = (int) caseDashboardDtos.stream().filter(c -> c.getCaseClassification() == CaseClassification.NO_CASE).count();
//		caseClassificationNotACase.updateCountLabel(notACaseCasesCount);
		int notYetClassifiedCasesCount = (int) caseDashboardDtos.stream().filter(c -> c.getCaseClassification() == CaseClassification.NOT_CLASSIFIED).count();
		caseClassificationNotYetClassified.updateCountLabel(notYetClassifiedCasesCount);
	}

	private void addNewEventsComponent() {
		newEventsComponent = new StatisticsSubComponent();
		newEventsComponent.addStyleName("statistics-sub-component");
		
		// Header
		newEventsComponent.addHeader("New Events", new Image(null, new ThemeResource("img/dashboard-icon-placeholder.png")));
		
		// Overview
		newEventsComponent.addOverview();
		eventStatusConfirmed = new StatisticsOverviewElement("Confirmed", "border-red");
		newEventsComponent.addComponentToOverview(eventStatusConfirmed);
		eventStatusPossible = new StatisticsOverviewElement("Possible", "border-orange");
		newEventsComponent.addComponentToOverview(eventStatusPossible);
		eventStatusNotAnEvent = new StatisticsOverviewElement("Not An Event", "border-green");
		newEventsComponent.addComponentToOverview(eventStatusNotAnEvent);
		
		updateNewEventsComponent();
		addComponent(newEventsComponent);
	}
	
	private void updateNewEventsComponent() {
		dashboardView.updateDateLabel(newEventsComponent.getDateLabel());
		
		List<EventDashboardDto> eventDashboardDtos;
		if (dashboardView.getDateFilterOption() == DateFilterOption.DATE) {
			eventDashboardDtos = FacadeProvider.getEventFacade().getNewEventsBetween(dashboardView.getFromDate(), dashboardView.getToDate(), LoginHelper.getCurrentUser().getUuid());
		} else {
			eventDashboardDtos = FacadeProvider.getEventFacade().getNewEventsBetween(DateHelper.getEpiWeekStart(dashboardView.getFromWeek()), DateHelper.getEpiWeekEnd(dashboardView.getToWeek()), LoginHelper.getCurrentUserAsReference().getUuid());
		}
		
		int newEventsCount = eventDashboardDtos.size();
		newEventsComponent.updateCountLabel(newEventsCount);
		
		int confirmedEventsCount = (int) eventDashboardDtos.stream().filter(e -> e.getEventStatus() == EventStatus.CONFIRMED).count();
		eventStatusConfirmed.updateCountLabel(confirmedEventsCount);
		int possibleEventsCount = (int) eventDashboardDtos.stream().filter(e -> e.getEventStatus() == EventStatus.POSSIBLE).count();
		eventStatusPossible.updateCountLabel(possibleEventsCount);
		int notAnEventEventsCount = (int) eventDashboardDtos.stream().filter(e -> e.getEventStatus() == EventStatus.NO_EVENT).count();
		eventStatusNotAnEvent.updateCountLabel(notAnEventEventsCount);
	}
	
	private void addNewTestResultsComponent() {
		newTestResultsComponent = new StatisticsSubComponent();
		newTestResultsComponent.addStyleName("statistics-sub-component");
		newTestResultsComponent.addStyleName("statistics-sub-component-right");
		
		// Header
		newTestResultsComponent.addHeader("New Test Results", new Image(null, new ThemeResource("img/dashboard-icon-placeholder.png")));
		
		// Overview
		newTestResultsComponent.addOverview();
		testResultPositive = new StatisticsOverviewElement("Positive", "border-red");
		newTestResultsComponent.addComponentToOverview(testResultPositive);
		testResultNegative = new StatisticsOverviewElement("Negative", "border-green");
		newTestResultsComponent.addComponentToOverview(testResultNegative);
		testResultPending = new StatisticsOverviewElement("Pending", "border-orange");
		newTestResultsComponent.addComponentToOverview(testResultPending);
		testResultIndeterminate = new StatisticsOverviewElement("Indeterminate", "border-grey");
		newTestResultsComponent.addComponentToOverview(testResultIndeterminate);
		
		updateNewTestResultsComponent();
		addComponent(newTestResultsComponent);
	}
	
	private void updateNewTestResultsComponent() {
		dashboardView.updateDateLabel(newTestResultsComponent.getDateLabel());
		
		List<TestResultDashboardDto> testResultDashboardDtos;
		if (dashboardView.getDateFilterOption() == DateFilterOption.DATE) {
			testResultDashboardDtos = FacadeProvider.getSampleTestFacade().getNewTestResultsBetween(dashboardView.getFromDate(), dashboardView.getToDate(), LoginHelper.getCurrentUser().getUuid());
		} else {
			testResultDashboardDtos = FacadeProvider.getSampleTestFacade().getNewTestResultsBetween(DateHelper.getEpiWeekStart(dashboardView.getFromWeek()), DateHelper.getEpiWeekEnd(dashboardView.getToWeek()), LoginHelper.getCurrentUserAsReference().getUuid());
		}
		
		int newTestResultsCount = testResultDashboardDtos.size();
		newTestResultsComponent.updateCountLabel(newTestResultsCount);
		
		int positiveTestResultsCount = (int) testResultDashboardDtos.stream().filter(r -> r.getTestResult() == SampleTestResultType.POSITIVE).count();
		testResultPositive.updateCountLabel(positiveTestResultsCount);
		int negativeTestResultsCount = (int) testResultDashboardDtos.stream().filter(r -> r.getTestResult() == SampleTestResultType.NEGATIVE).count();
		testResultNegative.updateCountLabel(negativeTestResultsCount);
		int pendingTestResultsCount = (int) testResultDashboardDtos.stream().filter(r -> r.getTestResult() == SampleTestResultType.PENDING).count();
		testResultPending.updateCountLabel(pendingTestResultsCount);
		int indeterminateTestResultsCount = (int) testResultDashboardDtos.stream().filter(r -> r.getTestResult() == SampleTestResultType.INDETERMINATE).count();
		testResultIndeterminate.updateCountLabel(indeterminateTestResultsCount);
	}
	
}
