package de.symeda.sormas.api.visit;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.Required;

public class VisitDto extends EntityDto {

	private static final long serialVersionUID = -441664767075414789L;

	public static final String I18N_PREFIX = "Visit";
	public static final int ALLOWED_CONTACT_DATE_OFFSET = 30;
	
	public static final String PERSON = "person";
	public static final String DISEASE = "disease";
	public static final String VISIT_DATE_TIME = "visitDateTime";
	public static final String VISIT_USER = "visitUser";
	public static final String VISIT_STATUS = "visitStatus";
	public static final String VISIT_REMARKS = "visitRemarks";
	public static final String SYMPTOMS = "symptoms";
	public static final String REPORT_LAT = "reportLat";
	public static final String REPORT_LON = "reportLon";
	
	@Required
	private PersonReferenceDto person;
	private Disease disease;
	@Required
	private Date visitDateTime;
	@Required
	private UserReferenceDto visitUser;
	@Required
	private VisitStatus visitStatus;
	private String visitRemarks;
	private SymptomsDto symptoms;
	
	private Double reportLat;
	private Double reportLon;
	private Float reportLatLonAccuracy;
	
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
	public PersonReferenceDto getPerson() {
		return person;
	}
	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	public UserReferenceDto getVisitUser() {
		return visitUser;
	}
	public void setVisitUser(UserReferenceDto visitUser) {
		this.visitUser = visitUser;
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
	
	public VisitReferenceDto toReference() {
		return new VisitReferenceDto(getUuid());
	}
	
}
