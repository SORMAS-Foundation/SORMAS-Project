package de.symeda.sormas.api.clinicalcourse;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DataHelper;

public class ClinicalVisitDto extends EntityDto {

	private static final long serialVersionUID = -8220449896773019721L;
	
	public static final String I18N_PREFIX = "ClinicalVisit";
	
	public static final String CLINICAL_COURSE = "clinicalCourse";
	public static final String SYMPTOMS = "symptoms";
	public static final String DISEASE = "disease";
	public static final String PERSON = "person";
	public static final String VISIT_DATE_TIME = "visitDateTime";
	public static final String VISIT_REMARKS = "visitRemarks";
	public static final String VISITING_PERSON = "visitingPerson";
	
	private ClinicalCourseReferenceDto clinicalCourse;
	private SymptomsDto symptoms;
	private Disease disease;
	private PersonReferenceDto person;
	private Date visitDateTime;
	private String visitRemarks;
	private String visitingPerson;
	
	public static ClinicalVisitDto buildClinicalVisit(ClinicalCourseReferenceDto clinicalCourse, SymptomsDto symptoms,
			Disease disease, PersonReferenceDto person) {
		ClinicalVisitDto clinicalVisit = new ClinicalVisitDto();
		clinicalVisit.setUuid(DataHelper.createUuid());
		clinicalVisit.setClinicalCourse(clinicalCourse);
		clinicalVisit.setSymptoms(symptoms);
		clinicalVisit.setDisease(disease);
		clinicalVisit.setPerson(person);
		clinicalVisit.setVisitDateTime(new Date());
		
		return clinicalVisit;
	}

	public ClinicalCourseReferenceDto getClinicalCourse() {
		return clinicalCourse;
	}
	public void setClinicalCourse(ClinicalCourseReferenceDto clinicalCourse) {
		this.clinicalCourse = clinicalCourse;
	}

	public SymptomsDto getSymptoms() {
		return symptoms;
	}
	public void setSymptoms(SymptomsDto symptoms) {
		this.symptoms = symptoms;
	}
	
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	
	public PersonReferenceDto getPerson() {
		return person;
	}
	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public Date getVisitDateTime() {
		return visitDateTime;
	}
	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}

	public String getVisitRemarks() {
		return visitRemarks;
	}
	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	public String getVisitingPerson() {
		return visitingPerson;
	}
	public void setVisitingPerson(String visitingPerson) {
		this.visitingPerson = visitingPerson;
	}
	
}
