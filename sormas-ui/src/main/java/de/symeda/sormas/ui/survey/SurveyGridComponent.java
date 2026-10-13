package de.symeda.sormas.ui.survey;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.survey.SurveyCriteria;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class SurveyGridComponent extends VerticalLayout {

	private SurveyFilterForm filterForm;
	private SurveyGrid grid;

	public SurveyGridComponent(
		SurveyCriteria criteria,
		ViewConfiguration viewConfiguration,
		Runnable filterChangeHandler,
		Runnable filterResetHandler) {
		setSizeFull();
		setSpacing(false);

		grid = new SurveyGrid(criteria, viewConfiguration);

		filterForm = new SurveyFilterForm();
		filterForm.addApplyHandler(e -> filterChangeHandler.run());
		filterForm.addResetHandler(e -> filterResetHandler.run());

		addComponents(filterForm, grid);
		setExpandRatio(grid, 1);
	}

	public void reload() {
		grid.reload();
	}

	public void updateFilterComponents(SurveyCriteria criteria) {
		filterForm.setValue(criteria);
	}

}
