package de.symeda.sormas.ui.configuration;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;

public class LaboratoriesView extends AbstractFacilitiesView {

	private static final long serialVersionUID = 7745914668183276666L;

	public static final String VIEW_NAME = "configuration/laboratories";

	public LaboratoriesView() {
		super(VIEW_NAME, true);
		if (LoginHelper.hasUserRight(UserRight.FACILITIES_CREATE)) {
			createButton.setCaption("new laboratory");
			createButton.addClickListener(
					e -> ControllerProvider.getFacilityController().create("Create new laboratory", true));
		}
	}

}
