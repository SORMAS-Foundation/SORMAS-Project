package de.symeda.sormas.ui;

import de.symeda.sormas.ui.caze.CaseController;
import de.symeda.sormas.ui.configuration.infrastructure.InfrastructureController;
import de.symeda.sormas.ui.configuration.outbreak.OutbreakController;
import de.symeda.sormas.ui.contact.ContactController;
import de.symeda.sormas.ui.dashboard.DashboardController;
import de.symeda.sormas.ui.events.EventController;
import de.symeda.sormas.ui.events.EventParticipantsController;
import de.symeda.sormas.ui.person.PersonController;
import de.symeda.sormas.ui.samples.SampleController;
import de.symeda.sormas.ui.samples.SampleTestController;
import de.symeda.sormas.ui.statistics.StatisticsController;
import de.symeda.sormas.ui.task.TaskController;
import de.symeda.sormas.ui.user.UserController;
import de.symeda.sormas.ui.utils.BaseControllerProvider;
import de.symeda.sormas.ui.visit.VisitController;

/**
 * @author Stefan Szczesny
 */
public class ControllerProvider extends BaseControllerProvider {

	private final CaseController caseController;
	private final ContactController contactController;
	private final EventController eventController;
	private final EventParticipantsController eventParticipantController;
	private final InfrastructureController infrastructureController;
	private final VisitController visitController;
	private final PersonController personController;
	private final UserController userController;
	private final TaskController taskController;
	private final SampleController sampleController;
	private final SampleTestController sampleTestController;
	private final OutbreakController outbreakController;
	private final StatisticsController statisticsController;
	private final DashboardController dashboardController;

	public ControllerProvider() {
		super();

		caseController = new CaseController();
		contactController = new ContactController();
		eventController = new EventController();
		eventParticipantController = new EventParticipantsController();
		infrastructureController = new InfrastructureController();
		visitController = new VisitController();
		personController = new PersonController();
		userController = new UserController();
		taskController = new TaskController();
		sampleController = new SampleController();
		sampleTestController = new SampleTestController();
		outbreakController = new OutbreakController();
		statisticsController = new StatisticsController();
		dashboardController = new DashboardController();
	}

	protected static ControllerProvider get() {
		return (ControllerProvider) BaseControllerProvider.get();
	}

	public static CaseController getCaseController() {
		return get().caseController;
	}

	public static ContactController getContactController() {
		return get().contactController;
	}
	
	public static EventController getEventController() {
		return get().eventController;
	}
	
	public static EventParticipantsController getEventParticipantController() {
		return get().eventParticipantController;
	}
	
	public static InfrastructureController getInfrastructureController() {
		return get().infrastructureController;
	}
	
	public static VisitController getVisitController() {
		return get().visitController;
	}
	
	public static PersonController getPersonController() {
		return get().personController;
	}
	
	public static UserController getUserController() {
		return get().userController;
	}

	public static TaskController getTaskController() {
		return get().taskController;
	}
	
	public static SampleController getSampleController() {
		return get().sampleController;
	}
	
	public static SampleTestController getSampleTestController() {
		return get().sampleTestController;
	}
	
	public static OutbreakController getOutbreakController() {
		return get().outbreakController;
	}
	
	public static StatisticsController getStatisticsController() {
		return get().statisticsController;
	}
	
	public static DashboardController getDashboardController() {
		return get().dashboardController;
	}

}
