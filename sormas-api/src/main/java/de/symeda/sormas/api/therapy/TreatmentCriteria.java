package de.symeda.sormas.api.therapy;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class TreatmentCriteria extends BaseCriteria implements Cloneable {

	private static final long serialVersionUID = -2194414949727913664L;

	private TherapyReferenceDto therapy;
	private TreatmentType treatmentType;
	private String textFilter;

	@Override
	public TreatmentCriteria clone() {
		try {
			return (TreatmentCriteria) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@IgnoreForUrl
	public TherapyReferenceDto getTherapy() {
		return therapy;
	}

	public TreatmentCriteria therapy(TherapyReferenceDto therapy) {
		this.therapy = therapy;
		return this;
	}

	public TreatmentType getTreatmentType() {
		return treatmentType;
	}

	public TreatmentCriteria treatmentType(TreatmentType treatmentType) {
		this.treatmentType = treatmentType;
		return this;
	}

	@IgnoreForUrl
	public String getTextFilter() {
		return textFilter;
	}

	public TreatmentCriteria textFilter(String textFilter) {
		this.textFilter = textFilter;
		return this;
	}
}
