package de.symeda.sormas.ui.dashboard.statistics;

public enum CountElementStyle {
	
	CRITICAL("critical"),
	IMPORTANT("important"),
	RELEVANT("relevant"),
	NEUTRAL("neutral"),
	POSITIVE("positive"),
	MINOR("minor"),
	PRIMARY("primary");
	
	private String cssClass;
	
	CountElementStyle(String cssClass) {
		this.cssClass = cssClass;
	}
	
	public String getCssClass() {
		return cssClass;
	}

}
