package de.symeda.sormas.ui.dashboard;

import java.util.List;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDashboardDto;
import de.symeda.sormas.api.event.EventDashboardDto;
import de.symeda.sormas.api.sample.TestResultDashboardDto;
import de.symeda.sormas.api.task.TaskDashboardDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.login.LoginHelper;

@SuppressWarnings("serial")
public class StatisticsComponent extends HorizontalLayout {
	
	private final DashboardView dashboardView;
	private StatisticsSubComponent myTasksComponent;
	private StatisticsSubComponent newCasesComponent;
	private StatisticsSubComponent newEventsComponent;
	private StatisticsSubComponent newTestResultsComponent;
	
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
		myTasksComponent.addHeader("My Tasks", new Image(null, new ThemeResource("img/dashboard-icon-placeholder.png")));
		updateMyTasksComponent();
		addComponent(myTasksComponent);
	}
	
	private void updateMyTasksComponent() {
		dashboardView.updateDateLabel(myTasksComponent.getDateLabel());
		
		List<TaskDashboardDto> taskDashboardDtos;
		if (dashboardView.getDateFilterOption() == DateFilterOption.DATE) {
			taskDashboardDtos = FacadeProvider.getTaskFacade().getAllWithDueDateBetween(dashboardView.getFromDate(), dashboardView.getToDate(), LoginHelper.getCurrentUserAsReference().getUuid());
		} else {
			taskDashboardDtos = FacadeProvider.getTaskFacade().getAllWithDueDateBetween(DateHelper.getEpiWeekStart(dashboardView.getFromWeek()), DateHelper.getEpiWeekEnd(dashboardView.getToWeek()), LoginHelper.getCurrentUserAsReference().getUuid());
		}
		
		int myTasksCount = taskDashboardDtos.size();		
		myTasksComponent.updateCountLabel(myTasksCount);
	}
	
	private void addNewCasesComponent() {
		newCasesComponent = new StatisticsSubComponent();
		newCasesComponent.addStyleName("statistics-sub-component");
		newCasesComponent.addHeader("New Cases", new Image(null, new ThemeResource("img/dashboard-icon-placeholder.png")));
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
	}

	private void addNewEventsComponent() {
		newEventsComponent = new StatisticsSubComponent();
		newEventsComponent.addStyleName("statistics-sub-component");
		newEventsComponent.addHeader("New Events", new Image(null, new ThemeResource("img/dashboard-icon-placeholder.png")));
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
	}
	
	private void addNewTestResultsComponent() {
		newTestResultsComponent = new StatisticsSubComponent();
		newTestResultsComponent.addStyleName("statistics-sub-component");
		newTestResultsComponent.addStyleName("statistics-sub-component-right");
		newTestResultsComponent.addHeader("New Test Results", new Image(null, new ThemeResource("img/dashboard-icon-placeholder.png")));
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
	}
	
}
