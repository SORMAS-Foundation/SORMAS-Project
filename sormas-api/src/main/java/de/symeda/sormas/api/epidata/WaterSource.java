package de.symeda.sormas.api.epidata;

import de.symeda.sormas.api.I18nProperties;

public enum WaterSource {

	PIPE_NETWORK,
	COMMUNITY_BOREHOLE_WELL,
	PRIVATE_BOREHOLE_WELL,
	STREAM,
	OTHER;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
	
}
