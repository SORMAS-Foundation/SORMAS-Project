package de.symeda.sormas.ui.configuration;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;

public class HealthFacilitiesView extends AbstractFacilitiesView {

	private static final long serialVersionUID = -7708098278141028591L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/healthFacilities";

	public HealthFacilitiesView() {
		super(VIEW_NAME, false);
		if (LoginHelper.hasUserRight(UserRight.INFRASTRUCTURE_CREATE)) {
			createButton.setCaption("new health facility");
			createButton.addClickListener(
					e -> ControllerProvider.getInfrastructureController().createHealthFacility("Create new health facility", false));
		}
	}

}
