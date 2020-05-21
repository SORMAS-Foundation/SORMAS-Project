package de.symeda.sormas.backend.visit;

import java.util.Date;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.symptoms.Symptoms;

public class VisitSummaryExportDetails {
	
	private Long contactId;
	private Date visitDateTime;
    private VisitStatus visitStatus;
    private Symptoms symptoms;

    public VisitSummaryExportDetails(Long contactId, Date visitDateTime, VisitStatus visitStatus, Symptoms symptoms) {
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

    public VisitStatus getVisitStatus() {
        return visitStatus;
    }

    public void setVisitStatus(VisitStatus visitStatus) {
        this.visitStatus = visitStatus;
    }

    public Symptoms getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(Symptoms symptoms) {
        this.symptoms = symptoms;
    }
    
}
