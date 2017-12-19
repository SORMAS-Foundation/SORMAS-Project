package de.symeda.sormas.api.caze;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRole;

public class CaseCriteria implements Serializable {

	private static final long serialVersionUID = 5114202107622217837L;

	private UserRole reportingUserRole;
	private Disease disease;
	
	public UserRole getReportingUserRole() {
		return reportingUserRole;
	}

	public CaseCriteria reportingUserHasRole(UserRole reportingUserRole) {
		this.reportingUserRole = reportingUserRole;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public CaseCriteria dieasesEquals(Disease disease) {
		this.disease = disease;
		return this;
	}
}
