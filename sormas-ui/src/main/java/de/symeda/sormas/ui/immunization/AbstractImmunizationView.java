package de.symeda.sormas.ui.immunization;

import de.symeda.sormas.api.immunization.ImmunizationReferenceDto;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractDetailView;

public class AbstractImmunizationView extends AbstractDetailView<ImmunizationReferenceDto> {

	public static final String ROOT_VIEW_NAME = ImmunizationsView.VIEW_NAME;

	protected AbstractImmunizationView(String viewName) {
		super(viewName);
	}

	@Override
	protected ImmunizationReferenceDto getReferenceByUuid(String uuid) {
		return null;
	}

	@Override
	protected String getRootViewName() {
		return null;
	}

	@Override
	protected void initView(String params) {

	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {

	}
}
