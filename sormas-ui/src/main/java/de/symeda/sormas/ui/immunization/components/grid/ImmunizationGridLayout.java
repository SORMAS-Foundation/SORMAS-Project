package de.symeda.sormas.ui.immunization.components.grid;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.immunization.ImmunizationCriteria;

public class ImmunizationGridLayout extends VerticalLayout {

	public ImmunizationGridLayout(ImmunizationCriteria criteria) {

		ImmunizationGrid grid = new ImmunizationGrid(criteria);
		addComponent(grid);

		setMargin(true);
		setSpacing(false);
		setSizeFull();
		setExpandRatio(grid, 1);
		setStyleName("crud-main-layout");
	}
}
