package de.symeda.sormas.ui.configuration;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;

/**
 * @author Christopher Riedel
 *
 */
public class HealthFacilitiesView extends AbstractFacilitiesView {

	private static final long serialVersionUID = -7708098278141028591L;

	public static final String VIEW_NAME = "configuration/healthFacilities";

	public HealthFacilitiesView() {
		super(VIEW_NAME, false);
		if (LoginHelper.hasUserRight(UserRight.FACILITIES_CREATE)) {
			createButton.setCaption("new health facility");
			createButton.addClickListener(
					e -> ControllerProvider.getFacilityController().create("Create new health facility", false));
		}
	}

}
