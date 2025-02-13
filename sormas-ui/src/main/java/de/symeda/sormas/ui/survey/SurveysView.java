package de.symeda.sormas.ui.survey;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.survey.SurveyCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class SurveysView extends AbstractView {

	public static final String VIEW_NAME = "surveys";

	private final SurveyCriteria criteria;
	private final ViewConfiguration viewConfiguration;
	private SurveyGridComponent gridComponent;

	public SurveysView() {
		super(VIEW_NAME);

		setSizeFull();

		viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);
		criteria = ViewModelProviders.of(SurveysView.class).get(SurveyCriteria.class);

		gridComponent = new SurveyGridComponent(criteria, viewConfiguration, () -> navigateTo(criteria, true), () -> navigateTo(null, true));
		addComponent(gridComponent);

		if (UiUtil.permitted(UserRight.SURVEY_CREATE)) {
			final Button btnNewSurvey = ButtonHelper
				.createIconButton(Captions.surveyNewSurvey, VaadinIcons.PLUS_CIRCLE, e -> ControllerProvider.getSurveyController().create());
			addHeaderComponent(btnNewSurvey);
		}
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		String params = event.getParameters().trim();
		setApplyingCriteria(false);
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		setApplyingCriteria(true);
		gridComponent.updateFilterComponents(criteria);
		setApplyingCriteria(false);
	}
}
