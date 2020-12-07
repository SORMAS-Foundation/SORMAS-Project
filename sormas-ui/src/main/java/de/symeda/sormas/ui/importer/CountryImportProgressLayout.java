package de.symeda.sormas.ui.importer;

import com.vaadin.ui.UI;

public class CountryImportProgressLayout extends ImportProgressLayout {

	private static final long serialVersionUID = 550057797894417947L;

	public CountryImportProgressLayout(int totalCount, UI currentUI, Runnable cancelCallback) {
		super(totalCount, currentUI, cancelCallback, true, false);
	}

}
