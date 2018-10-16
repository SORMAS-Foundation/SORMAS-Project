package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.visit.VisitStatus;

public class DashboardContactDto implements Serializable {

	private static final long serialVersionUID = -8118109313009645462L;

	public static final String I18N_PREFIX = "Contact";
	
	private String uuid;
	private Date reportDate;
	private boolean symptomatic;
	private VisitStatus lastVisitStatus;
	private Date lastVisitDateTime;
	private ContactStatus contactStatus;
	private ContactClassification contactClassification;
	private FollowUpStatus followUpStatus;
	private Date followUpUntil;
	private Disease disease;
	
	public DashboardContactDto(String uuid, Date reportDate, ContactStatus contactStatus,
			ContactClassification contactClassification, FollowUpStatus followUpStatus,
			Date followUpUntil, Disease disease) {
		this.uuid = uuid;
		this.reportDate = reportDate;
		this.contactStatus = contactStatus;
		this.contactClassification = contactClassification;
		this.followUpStatus = followUpStatus;
		this.followUpUntil = followUpUntil;
		this.disease = disease;
	}

	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public boolean isSymptomatic() {
		return symptomatic;
	}

	public void setSymptomatic(boolean symptomatic) {
		this.symptomatic = symptomatic;
	}

	public VisitStatus getLastVisitStatus() {
		return lastVisitStatus;
	}

	public void setLastVisitStatus(VisitStatus lastVisitStatus) {
		this.lastVisitStatus = lastVisitStatus;
	}

	public Date getLastVisitDateTime() {
		return lastVisitDateTime;
	}

	public void setLastVisitDateTime(Date lastVisitDateTime) {
		this.lastVisitDateTime = lastVisitDateTime;
	}

	public ContactStatus getContactStatus() {
		return contactStatus;
	}

	public void setContactStatus(ContactStatus contactStatus) {
		this.contactStatus = contactStatus;
	}

	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}

	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	
}
