package de.symeda.sormas.ui.survey;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.api.survey.SurveyReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractDetailView;

public abstract class AbstractSurveyView extends AbstractDetailView<SurveyReferenceDto> {

	public static final String ROOT_VIEW_NAME = SurveysView.VIEW_NAME;
	private static final long serialVersionUID = 5429099481597698053L;

	protected AbstractSurveyView(String viewName) {
		super(viewName);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	protected SurveyReferenceDto getReferenceByUuid(String uuid) {
		final SurveyReferenceDto reference;
		if (FacadeProvider.getSurveyFacade().exists(uuid)) {
			reference = FacadeProvider.getSurveyFacade().getReferenceByUuid(uuid);
		} else {
			reference = null;
		}
		return reference;
	}

	protected boolean isEditAllowed() {
		return FacadeProvider.getSurveyFacade().isEditAllowed(getReference().getUuid());
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		if (!findReferenceByParams(params)) {
			return;
		}
		menu.removeAllViews();
		menu.addView(SurveysView.VIEW_NAME, I18nProperties.getCaption(Captions.surveySurveyList));
		menu.addView(SurveyDataView.VIEW_NAME, I18nProperties.getCaption(SurveyDto.I18N_PREFIX), params);

		setMainHeaderComponent(ControllerProvider.getSurveyController().getSurveyViewTitleLayout(getReference().getUuid()));
	}
}
