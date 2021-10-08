package de.symeda.sormas.ui.utils;

import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.ui.ComboBox;

public class ComboBoxHelper {

	/**
	 * Create a default V7 combobox which uses FilteringMode.CONTAINS
	 */
	public static ComboBox createComboBoxV7() {
		ComboBox cb = new ComboBox();
		cb.setFilteringMode(FilteringMode.CONTAINS);
		return cb;
	}
}
