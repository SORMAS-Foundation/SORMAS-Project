package de.symeda.sormas.ui.surveypages;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.survey.SurveyCriteria;
import de.symeda.sormas.api.survey.SurveyIndexDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class SurveyGrid extends FilteredGrid<SurveyIndexDto, SurveyCriteria> {

	public SurveyGrid(SurveyCriteria criteria, ViewConfiguration viewConfiguration) {
		super(SurveyIndexDto.class);
		setSizeFull();

		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode()) {
			setCriteria(criteria);
			setEagerDataProvider(FacadeProvider.getSurveyFacade()::getIndexList);
		} else {
			setLazyDataProvider(FacadeProvider.getSurveyFacade()::getIndexList, FacadeProvider.getSurveyFacade()::count);
			setCriteria(criteria);
		}

		initlColumns(criteria);

		addItemClickListener(
			new ShowDetailsListener<>(SurveyIndexDto.UUID, e -> ControllerProvider.getSurveyController().navigateToSurvey(e.getUuid())));
	}

	protected void initlColumns(SurveyCriteria criteria) {

		setColumns(SurveyIndexDto.UUID, SurveyIndexDto.DISEASE, SurveyIndexDto.SURVEY_NAME);

		((Column<SurveyIndexDto, String>) getColumn(SurveyIndexDto.UUID)).setRenderer(new UuidRenderer());

	}

	private void setLazyDataProvider() {
		setLazyDataProvider(FacadeProvider.getSurveyFacade()::getIndexList, FacadeProvider.getSurveyFacade()::count);
	}

	public void reload() {
		getDataProvider().refreshAll();
	}
}
