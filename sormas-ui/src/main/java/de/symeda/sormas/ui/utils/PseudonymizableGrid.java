package de.symeda.sormas.ui.utils;

import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class PseudonymizableGrid extends Grid {

	/**
	 * Set the selection Mode; Using SelectionMode.MULTI prevents the user from selecting pseudonymized Entities
	 */
	@Override
	public SelectionModel setSelectionMode(SelectionMode selectionMode) {
		SelectionModel model = super.setSelectionMode(selectionMode);
		if (selectionMode == SelectionMode.MULTI) {
			addSelectionListener(event -> {
				event.getSelected().forEach(item -> {
					if (item instanceof PseudonymizableIndexDto && ((PseudonymizableIndexDto) item).isPseudonymized()) {
						deselect(item);
					}
				});
			});
		}
		return model;

	}
}
