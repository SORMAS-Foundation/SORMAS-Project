package de.symeda.sormas.api.user;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum JurisdictionLevel {

	NONE(0),
	NATION(1),
	REGION(2),
	DISTRICT(3),
	COMMUNITY(4),
	POINT_OF_ENTRY(4),
	HEALTH_FACILITY(5),
	LABORATORY(5),
	EXTERNAL_LABORATORY(5);

	private int order;

	JurisdictionLevel(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
