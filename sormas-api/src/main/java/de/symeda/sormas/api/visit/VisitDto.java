package de.symeda.sormas.api.visit;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class VisitDto extends VisitReferenceDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -441664767075414789L;

	public static final String I18N_PREFIX = "Visit";
	
	public static final String PERSON = "person";
	public static final String DISEASE = "disease";
	public static final String VISIT_DATE_TIME = "visitDateTime";
	public static final String VISIT_USER = "visitUser";
	public static final String VISIT_STATUS = "visitStatus";
	public static final String VISIT_REMARKS = "visitRemarks";
	public static final String SYMPTOMS = "symptoms";
	
	private PersonReferenceDto person;
	private Disease disease;
	private Date visitDateTime;
	private UserReferenceDto visitUser;
	private VisitStatus visitStatus;
	private String visitRemarks;
	private SymptomsDto symptoms;
	
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
	
}
