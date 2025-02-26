package de.symeda.sormas.ui.survey;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.survey.SurveyIndexDto;
import de.symeda.sormas.api.survey.SurveyTokenCriteria;
import de.symeda.sormas.api.survey.SurveyTokenIndexDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CaseUuidRenderer;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class SurveyTokenGrid extends FilteredGrid<SurveyTokenIndexDto, SurveyTokenCriteria> {

	public SurveyTokenGrid(SurveyTokenCriteria criteria, ViewConfiguration viewConfiguration) {
		super(SurveyTokenIndexDto.class);
		setSizeFull();

		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode()) {
			setCriteria(criteria);
			setEagerDataProvider(FacadeProvider.getSurveyTokenFacade()::getIndexList);
		} else {
			setLazyDataProvider(FacadeProvider.getSurveyTokenFacade()::getIndexList, FacadeProvider.getSurveyTokenFacade()::count);
			setCriteria(criteria);
		}

		initColumns();

		addItemClickListener(
			new ShowDetailsListener<>(
				SurveyTokenIndexDto.ASSIGNED_CASE_UUID,
				false,
				e -> ControllerProvider.getCaseController().navigateToCase(e.getAssignedCaseUuid())));

		addItemClickListener(
			new ShowDetailsListener<>(SurveyIndexDto.UUID, e -> ControllerProvider.getSurveyTokenController().navigateToSurveyToken(e.getUuid())));
	}

	protected void initColumns() {

		setColumns(
			SurveyTokenIndexDto.UUID,
			SurveyTokenIndexDto.TOKEN,
			SurveyTokenIndexDto.ASSIGNED_CASE_UUID,
			SurveyTokenIndexDto.ASSIGNEMENT_DATE,
			SurveyTokenIndexDto.RESPONSE_RECEIVED,
			SurveyTokenIndexDto.RESPONSE_RECEIVED_DATE);

		for (Column<SurveyTokenIndexDto, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(SurveyTokenIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
			column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(getBeanType(), column.getId()));
		}

		((Column<SurveyTokenIndexDto, String>) getColumn(SurveyTokenIndexDto.ASSIGNED_CASE_UUID)).setRenderer(new CaseUuidRenderer(uuid -> false));

		((Column<SurveyTokenIndexDto, String>) getColumn(SurveyTokenIndexDto.UUID)).setRenderer(new UuidRenderer());
	}

	public void reload() {
		getDataProvider().refreshAll();
	}
}
