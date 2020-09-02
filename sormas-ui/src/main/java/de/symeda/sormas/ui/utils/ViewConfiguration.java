package de.symeda.sormas.ui.utils;

public class ViewConfiguration {

	private ViewMode viewMode;
	private boolean inEagerMode;

	public ViewConfiguration() {

	}

	public ViewConfiguration(ViewMode viewMode) {
		this.setViewMode(viewMode);
	}

	public ViewMode getViewMode() {
		return viewMode;
	}

	public void setViewMode(ViewMode viewMode) {
		this.viewMode = viewMode;
	}

	public boolean isInEagerMode() {
		return inEagerMode;
	}

	public void setInEagerMode(boolean inEagerMode) {
		this.inEagerMode = inEagerMode;
	}
}
