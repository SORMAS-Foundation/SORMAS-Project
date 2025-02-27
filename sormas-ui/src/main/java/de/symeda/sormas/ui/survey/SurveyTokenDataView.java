package de.symeda.sormas.ui.survey;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.survey.SurveyTokenDto;
import de.symeda.sormas.api.survey.SurveyTokenReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;

public class SurveyTokenDataView extends AbstractDetailView<SurveyTokenReferenceDto> {

	public static final String VIEW_NAME = SurveyTokensView.VIEW_NAME + "/data";

	private CommitDiscardWrapperComponent<SurveyTokenDataForm> editComponent;
	private SurveyTokenDto surveyToken;

	public SurveyTokenDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected SurveyTokenReferenceDto getReferenceByUuid(String uuid) {
		final SurveyTokenReferenceDto reference;
		if (FacadeProvider.getSurveyTokenFacade().exists(uuid)) {
			reference = FacadeProvider.getSurveyTokenFacade().getReferenceByUuid(uuid);
		} else {
			reference = null;
		}
		return reference;
	}

	@Override
	protected String getRootViewName() {
		return VIEW_NAME;
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();

		surveyToken = FacadeProvider.getSurveyTokenFacade().getByUuid(getReference().getUuid());

		editComponent =
			ControllerProvider.getSurveyTokenController().getSurveyTokenEditComponent(surveyToken.getUuid(), UserRight.SURVEY_TOKEN_EDIT, true);

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		container.setEnabled(true);

		LayoutWithSidePanel layout = new LayoutWithSidePanel(editComponent);
		container.addComponent(layout);

	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		if (!findReferenceByParams(params)) {
			return;
		}
		menu.removeAllViews();
		menu.addView(SurveyTokenDataView.VIEW_NAME, I18nProperties.getCaption(SurveyTokenDto.I18N_PREFIX), params);

		setMainHeaderComponent(ControllerProvider.getSurveyTokenController().getSurveyTokenViewTitleLayout(params));
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		initOrRedirect(event);
	}
}
