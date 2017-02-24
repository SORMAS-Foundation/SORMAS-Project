package de.symeda.sormas.app.caze;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;

public enum CaseEditTabs {
	CASE_DATA,
	PATIENT,
	SYMPTOMS,
	CONTACTS,
	TASKS,
	SAMPLES,
	//HOSPITALIZATION,
	;

	public String toString() {
		return I18nProperties.getFieldCaption(CaseDataDto.I18N_PREFIX+"."+this.name());
	};
}
