package de.symeda.sormas.backend.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.visit.VisitStatus;

public class DashboardVisit implements Serializable {

	private static final long serialVersionUID = -2178146234937420833L;

	private Long contactId;
	private Boolean symptomatic;
	private VisitStatus visitStatus;
	private Date visitDateTime;

	public DashboardVisit(Long contactId, Boolean symptomatic, VisitStatus visitStatus, Date visitDateTime) {
		this.contactId = contactId;
		this.symptomatic = symptomatic;
		this.visitStatus = visitStatus;
		this.visitDateTime = visitDateTime;
	}

	public Long getContactId() {
		return contactId;
	}

	public Boolean isSymptomatic() {
		return symptomatic;
	}

	public VisitStatus getVisitStatus() {
		return visitStatus;
	}

	public Date getVisitDateTime() {
		return visitDateTime;
	}

	public void setContactId(long contactId) {
		this.contactId = contactId;
	}

	public void setSymptomatic(boolean symptomatic) {
		this.symptomatic = symptomatic;
	}

	public void setVisitStatus(VisitStatus visitStatus) {
		this.visitStatus = visitStatus;
	}

	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}
}
