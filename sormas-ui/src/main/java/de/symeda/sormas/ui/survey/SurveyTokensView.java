package de.symeda.sormas.ui.survey;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.survey.SurveyTokenCriteria;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class SurveyTokensView extends AbstractSurveyView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/surveytokens";
	private final SurveyTokenCriteria criteria;
	public ViewConfiguration viewConfiguration;

	private SurveyTokenGridComponent gridComponent;

	private DetailSubComponentWrapper gridLayout;

	public SurveyTokensView() {
		super(VIEW_NAME);
		setSizeFull();

		viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);
		viewConfiguration.setInEagerMode(false);

		criteria = ViewModelProviders.of(SurveyTokensView.class).get(SurveyTokenCriteria.class);

		gridComponent = new SurveyTokenGridComponent(criteria, viewConfiguration, () -> navigateTo(criteria, true), () -> {
			ViewModelProviders.of(SurveyTokensView.class).remove(SurveyTokenCriteria.class);
			navigateTo(null, true);
		});
		addComponent(gridComponent);

	}

	@Override
	protected String getRootViewName() {
		return VIEW_NAME;
	}

	@Override
	protected void initView(String params) {
		criteria.setSurvey(getSurveyRef());
		setSubComponent(gridLayout);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
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
