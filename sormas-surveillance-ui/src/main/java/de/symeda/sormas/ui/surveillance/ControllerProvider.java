package de.symeda.sormas.ui.surveillance;

import de.symeda.sormas.ui.surveillance.caze.CaseController;
import de.symeda.sormas.ui.utils.BaseControllerProvider;

/**
 * @author Stefan Szczesny
 */
public class ControllerProvider extends BaseControllerProvider {

	private final CaseController caseController;

	public ControllerProvider() {
		super();

		caseController = new CaseController();
	}

	protected static ControllerProvider get() {
		return (ControllerProvider) BaseControllerProvider.get();
	}

	public static CaseController getCaseController() {
		return get().caseController;
	}

}
