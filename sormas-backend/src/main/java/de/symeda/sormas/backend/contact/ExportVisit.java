package de.symeda.sormas.backend.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.symptoms.Symptoms;

public class ExportVisit implements Serializable {
	
	private static final long serialVersionUID = -9164461680231010705L;
	
	private Long contactId;
	private Date visitDateTime;
	private VisitStatus visitStatus;
	private Symptoms symptoms;

	public ExportVisit(Long contactId, Date visitDateTime, VisitStatus visitStatus, Symptoms symptoms) {
		this.contactId = contactId;
		this.visitDateTime = visitDateTime;
		this.visitStatus = visitStatus;
		this.symptoms = symptoms;
	}

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public Date getVisitDateTime() {
		return visitDateTime;
	}

	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}

	public Symptoms getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(Symptoms symptoms) {
		this.symptoms = symptoms;
	}

	public VisitStatus getVisitStatus() {
		return visitStatus;
	}

	public void setVisitStatus(VisitStatus visitStatus) {
		this.visitStatus = visitStatus;
	}
	
}