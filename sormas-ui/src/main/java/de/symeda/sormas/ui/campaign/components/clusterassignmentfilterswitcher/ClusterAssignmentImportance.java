package de.symeda.sormas.ui.campaign.components.clusterassignmentfilterswitcher;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ClusterAssignmentImportance {
	ASSIGNED,
	UNASSIGNED;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
