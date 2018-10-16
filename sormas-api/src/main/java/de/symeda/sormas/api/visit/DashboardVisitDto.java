package de.symeda.sormas.api.visit;

import java.io.Serializable;

public class DashboardVisitDto implements Serializable {

	private static final long serialVersionUID = 3659266276391189213L;

	public static final String I18N_PREFIX = "Visit";
	
	private VisitStatus visitStatus;
	
	public DashboardVisitDto(VisitStatus visitStatus) {
		this.visitStatus = visitStatus;
	}

	public VisitStatus getVisitStatus() {
		return visitStatus;
	}

	public void setVisitStatus(VisitStatus visitStatus) {
		this.visitStatus = visitStatus;
	}
	
}
