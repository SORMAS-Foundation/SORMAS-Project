package de.symeda.sormas.ui.immunization.components.filter;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.immunization.ImmunizationCriteria;

public class FilterFormLayout extends VerticalLayout {

	private final ImmunizationFilterForm filterForm;

	public FilterFormLayout() {
		setSpacing(false);
		setMargin(false);
		setWidth(100, Unit.PERCENTAGE);

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

	public boolean hasFilter() {
		return filterForm.hasFilter();
	}
}
