package de.symeda.sormas.app.visit;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.visit.VisitDto;

public enum VisitEditTabs {
	VISIT_DATA,
	SYMPTOMS
	;
	
	public String toString() {
		return I18nProperties.getFieldCaption(VisitDto.I18N_PREFIX+"."+this.name());
	};
}
