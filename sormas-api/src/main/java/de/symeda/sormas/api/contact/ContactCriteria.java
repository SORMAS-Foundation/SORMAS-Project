package de.symeda.sormas.api.contact;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.user.UserRole;

public class ContactCriteria implements Serializable {

	private static final long serialVersionUID = 5114202107622217837L;

	private UserRole reportingUserRole;
	private Disease caseDisease;
	private CaseReferenceDto caze;
	
	public UserRole getReportingUserRole() {
		return reportingUserRole;
	}

	public ContactCriteria reportingUserHasRole(UserRole reportingUserRole) {
		this.reportingUserRole = reportingUserRole;
		return this;
	}

	public Disease getCaseDisease() {
		return caseDisease;
	}

	public ContactCriteria caseDieasesEquals(Disease disease) {
		this.caseDisease = disease;
		return this;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public void caseEquals(CaseReferenceDto caze) {
		this.caze = caze;
	}
}
