package de.symeda.sormas.ui.immunization;

public class ImmunizationDataView extends AbstractImmunizationView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public ImmunizationDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected String getRootViewName() {
		return ImmunizationsView.VIEW_NAME;
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();
	}
}
