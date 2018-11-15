package de.symeda.sormas.api.caze.classification;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

import de.symeda.sormas.api.Disease;

public class DiseaseClassificationCriteriaDto implements Serializable {

	private static final long serialVersionUID = 8800921617332187938L;

	// TODO: Remove this once disease configuration has been implemented
	private static final Date CHANGE_DATE = new GregorianCalendar(2017, 9, 17).getTime();
	
	private Disease disease;
	private ClassificationCriteriaDto suspectCriteria;
	private ClassificationCriteriaDto probableCriteria;
	private ClassificationCriteriaDto confirmedCriteria;
	private Date changeDate;
	
	public DiseaseClassificationCriteriaDto() {
		
	}
	
	public DiseaseClassificationCriteriaDto(Disease disease, ClassificationCriteriaDto suspectCriteria,
			ClassificationCriteriaDto probableCriteria, ClassificationCriteriaDto confirmedCriteria) {
		this.disease = disease;
		this.suspectCriteria = suspectCriteria;
		this.probableCriteria = probableCriteria;
		this.confirmedCriteria = confirmedCriteria;
		this.changeDate = CHANGE_DATE;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public ClassificationCriteriaDto getSuspectCriteria() {
		return suspectCriteria;
	}

	public void setSuspectCriteria(ClassificationCriteriaDto suspectCriteria) {
		this.suspectCriteria = suspectCriteria;
	}

	public ClassificationCriteriaDto getProbableCriteria() {
		return probableCriteria;
	}

	public void setProbableCriteria(ClassificationCriteriaDto probableCriteria) {
		this.probableCriteria = probableCriteria;
	}

	public ClassificationCriteriaDto getConfirmedCriteria() {
		return confirmedCriteria;
	}

	public void setConfirmedCriteria(ClassificationCriteriaDto confirmedCriteria) {
		this.confirmedCriteria = confirmedCriteria;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}
	
	public boolean hasAnyCriteria() {
		return suspectCriteria != null || probableCriteria != null || confirmedCriteria != null;
	}
	
}
