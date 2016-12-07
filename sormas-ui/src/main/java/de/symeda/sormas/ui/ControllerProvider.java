package de.symeda.sormas.ui;

import de.symeda.sormas.ui.caze.CaseController;
import de.symeda.sormas.ui.contact.ContactController;
import de.symeda.sormas.ui.person.PersonController;
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
	private final VisitController visitController;
	private final PersonController personController;
	private final UserController userController;
	private final TaskController taskController;

	public ControllerProvider() {
		super();

		caseController = new CaseController();
		contactController = new ContactController();
		visitController = new VisitController();
		personController = new PersonController();
		userController = new UserController();
		taskController = new TaskController();
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
}
