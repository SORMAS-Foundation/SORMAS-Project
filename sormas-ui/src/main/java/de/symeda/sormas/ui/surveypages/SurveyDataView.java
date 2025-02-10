package de.symeda.sormas.ui.surveypages;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.samples.HasName;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;

public class SurveyDataView extends AbstractSurveyView implements HasName {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	private CommitDiscardWrapperComponent<SurveyDataForm> editComponent;
	private SurveyDto survey;

	public SurveyDataView() {
		super(VIEW_NAME);
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	protected String getRootViewName() {
		return SurveysView.VIEW_NAME;
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();

		survey = FacadeProvider.getSurveyFacade().getByUuid(getReference().getUuid());
		editComponent =
			ControllerProvider.getSurveyController().getSurveyEditComponent(getReference().getUuid(), UserRight.SURVEY_EDIT, isEditAllowed());

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		container.setEnabled(true);

		LayoutWithSidePanel layout = new LayoutWithSidePanel(editComponent);
		container.addComponent(layout);
	}

}
