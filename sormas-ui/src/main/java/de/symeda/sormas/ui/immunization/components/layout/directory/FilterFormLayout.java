package de.symeda.sormas.ui.immunization.components.layout.directory;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.ui.immunization.components.filter.ImmunizationFilterForm;

public class FilterFormLayout extends VerticalLayout {

	private static final float PERCENTAGE_WIDTH = 100;

	private final ImmunizationFilterForm filterForm;

	public FilterFormLayout() {
		setSpacing(false);
		setMargin(false);
		setWidth(PERCENTAGE_WIDTH, Unit.PERCENTAGE);

		filterForm = new ImmunizationFilterForm();
		addComponent(filterForm);
	}

	public ImmunizationCriteria getValue() {
		return filterForm.getValue();
	}

	public void setValue(ImmunizationCriteria criteria) {
		filterForm.setValue(criteria);
	}

	public void addResetHandler(Button.ClickListener listener) {
		filterForm.addResetHandler(listener);
	}

	public void addApplyHandler(Button.ClickListener listener) {
		filterForm.addApplyHandler(listener);
	}
}
