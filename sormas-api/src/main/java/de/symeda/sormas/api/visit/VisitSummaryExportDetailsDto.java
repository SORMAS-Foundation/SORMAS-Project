package de.symeda.sormas.api.visit;

import java.io.Serializable;
import java.util.Date;

public class VisitSummaryExportDetailsDto implements Serializable {
	
	private static final long serialVersionUID = -4677902897777543789L;
	
	private Date visitDateTime;
    private VisitStatus visitStatus;
    private String symptoms;

    public VisitSummaryExportDetailsDto(Date visitDateTime, VisitStatus visitStatus, String symptoms) {
        this.visitDateTime = visitDateTime;
        this.visitStatus = visitStatus;
        this.symptoms = symptoms;
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

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
    
}