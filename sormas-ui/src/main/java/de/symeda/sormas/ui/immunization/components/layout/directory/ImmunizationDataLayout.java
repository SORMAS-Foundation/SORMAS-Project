package de.symeda.sormas.ui.immunization.components.layout.directory;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.ui.immunization.components.grid.ImmunizationGrid;

public class ImmunizationDataLayout extends VerticalLayout {

	private final ImmunizationGrid grid;

	public ImmunizationDataLayout(ImmunizationCriteria criteria) {
		grid = new ImmunizationGrid(criteria);
		addComponent(grid);

		setMargin(false);
		setSpacing(false);
		setSizeFull();
		setExpandRatio(grid, 1);
	}

	public ImmunizationGrid getGrid() {
		return grid;
	}

	public void refreshGrid() {
		grid.reload();
	}
}
