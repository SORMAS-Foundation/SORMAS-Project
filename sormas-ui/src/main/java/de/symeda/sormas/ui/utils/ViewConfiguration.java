package de.symeda.sormas.ui.utils;

public class ViewConfiguration {
	
	private ViewMode viewMode;

	public ViewConfiguration(ViewMode viewMode) {
		this.setViewMode(viewMode);
	}

	public ViewMode getViewMode() {
		return viewMode;
	}

	public void setViewMode(ViewMode viewMode) {
		this.viewMode = viewMode;
	}

}
