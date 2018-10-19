package de.symeda.sormas.api.caze.classification;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

import de.symeda.sormas.api.Disease;

public class DiseaseClassificationCriteria implements Serializable {

	private static final long serialVersionUID = 8800921617332187938L;

	// TODO: Remove this once disease configuration has been implemented
	private static final Date CHANGE_DATE = new GregorianCalendar(2017, 9, 17).getTime();
	
	private Disease disease;
	private ClassificationCriteria suspectCriteria;
	private ClassificationCriteria probableCriteria;
	private ClassificationCriteria confirmedCriteria;
	private Date changeDate;
	
	public DiseaseClassificationCriteria(Disease disease, ClassificationCriteria suspectCriteria,
			ClassificationCriteria probableCriteria, ClassificationCriteria confirmedCriteria) {
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

	public ClassificationCriteria getSuspectCriteria() {
		return suspectCriteria;
	}

	public void setSuspectCriteria(ClassificationCriteria suspectCriteria) {
		this.suspectCriteria = suspectCriteria;
	}

	public ClassificationCriteria getProbableCriteria() {
		return probableCriteria;
	}

	public void setProbableCriteria(ClassificationCriteria probableCriteria) {
		this.probableCriteria = probableCriteria;
	}

	public ClassificationCriteria getConfirmedCriteria() {
		return confirmedCriteria;
	}

	public void setConfirmedCriteria(ClassificationCriteria confirmedCriteria) {
		this.confirmedCriteria = confirmedCriteria;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}
	
}
