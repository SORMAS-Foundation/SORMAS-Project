package de.symeda.sormas.ui.utils;

import com.vaadin.v7.ui.ComboBox;

public class DiseaseComboBox<T> extends ComboBox {
	
	private boolean showNonPrimaryDiseases;

	public boolean isShowNonPrimaryDiseases() {
		return showNonPrimaryDiseases;
	}

	public void setShowNonPrimaryDiseases(boolean showNonPrimaryDiseases) {
		this.showNonPrimaryDiseases = showNonPrimaryDiseases;
	}
	
}
