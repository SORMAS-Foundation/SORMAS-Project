package de.symeda.sormas.api.therapy;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class PrescriptionCriteria extends BaseCriteria implements Cloneable {

	private static final long serialVersionUID = -9045206141614519189L;

	private TherapyDto therapy;
	private TreatmentType prescriptionType;
	private String textFilter;
	
	@Override
	public PrescriptionCriteria clone() {
		try {
			return (PrescriptionCriteria) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	@IgnoreForUrl
	public TherapyDto getTherapy() {
		return therapy;
	}
	
	public PrescriptionCriteria therapy(TherapyDto therapy) {
		this.therapy = therapy;
		return this;
	}
	
	public TreatmentType getPrescriptionType() {
		return prescriptionType;
	}
	
	public PrescriptionCriteria prescriptionType(TreatmentType prescriptionType) {
		this.prescriptionType = prescriptionType;
		return this;
	}

	@IgnoreForUrl
	public String getTextFilter() {
		return textFilter;
	}
	
	public PrescriptionCriteria textFilter(String textFilter) {
		this.textFilter = textFilter;
		return this;
	}
	
}
