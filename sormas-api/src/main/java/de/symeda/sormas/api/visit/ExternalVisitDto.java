package de.symeda.sormas.api.visit;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.Required;

import java.io.Serializable;
import java.util.Date;

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

    public ExternalVisitDto() {
    }

    public ExternalVisitDto(String contactUuid, Date visitDateTime, VisitStatus visitStatus, String visitRemarks, SymptomsDto symptoms, Double reportLat, Double reportLon,
      Float reportLatLonAccuracy) {
        this.contactUuid = contactUuid;
        this.visitDateTime = visitDateTime;
        this.visitStatus = visitStatus;
        this.visitRemarks = visitRemarks;
        this.symptoms = symptoms;
        this.reportLat = reportLat;
        this.reportLon = reportLon;
        this.reportLatLonAccuracy = reportLatLonAccuracy;
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
