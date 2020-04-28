package de.symeda.sormas.api.visit;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.Required;

/**
 * The class ExternalVisitDto.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalVisitDto implements Serializable, Cloneable {

    @Required
    private String contactUuid;
    @Required
    private Date visitDateTime;
    @Required
    private VisitStatus visitStatus;

    private String visitRemarks;

    private SymptomsDto symptoms;

    private Double reportLat;
    private Double reportLon;
    private Float reportLatLonAccuracy;

    public static ExternalVisitDto build(String contactUuid, Date visitDateTime, VisitStatus visitStatus, String visitRemarks, SymptomsDto symptoms, Double reportLat, Double reportLon,
                                         Float reportLatLonAccuracy) {
        final ExternalVisitDto externalVisitDto = new ExternalVisitDto();
        externalVisitDto.setContactUuid(contactUuid);
        externalVisitDto.setVisitDateTime(visitDateTime);
        externalVisitDto.setVisitStatus(visitStatus);
        externalVisitDto.setVisitRemarks(visitRemarks);
        externalVisitDto.setSymptoms(symptoms);
        externalVisitDto.setReportLat(reportLat);
        externalVisitDto.setReportLon(reportLon);
        externalVisitDto.setReportLatLonAccuracy(reportLatLonAccuracy);
        return externalVisitDto;
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

    public String getVisitRemarks() {
        return visitRemarks;
    }

    public void setVisitRemarks(String visitRemarks) {
        this.visitRemarks = visitRemarks;
    }

    public SymptomsDto getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(SymptomsDto symptoms) {
        this.symptoms = symptoms;
    }

    public Double getReportLat() {
        return reportLat;
    }

    public void setReportLat(Double reportLat) {
        this.reportLat = reportLat;
    }

    public Double getReportLon() {
        return reportLon;
    }

    public void setReportLon(Double reportLon) {
        this.reportLon = reportLon;
    }

    public Float getReportLatLonAccuracy() {
        return reportLatLonAccuracy;
    }

    public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
        this.reportLatLonAccuracy = reportLatLonAccuracy;
    }

    public String getContactUuid() {
        return contactUuid;
    }

    public void setContactUuid(String contactUuid) {
        this.contactUuid = contactUuid;
    }
}
