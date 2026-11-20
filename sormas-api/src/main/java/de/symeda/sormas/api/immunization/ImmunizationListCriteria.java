package de.symeda.sormas.api.immunization;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class ImmunizationListCriteria extends BaseCriteria {

	private final PersonReferenceDto personReferenceDto;
	private final Disease disease;
	private final String diseaseDetails;
	private final Date referencePeriodFrom;
	private final Date referencePeriodTo;

	public static class Builder {

		private final PersonReferenceDto personReferenceDto;
		private Disease disease;
		private String diseaseDetails;
		private Date referencePeriodFrom;
		private Date referencePeriodTo;

		public Builder(PersonReferenceDto personReferenceDto) {
			this.personReferenceDto = personReferenceDto;
		}

		public Builder withDisease(Disease disease) {
			this.disease = disease;
			return this;
		}

		public Builder withDiseaseDetails(String diseaseDetails) {
			this.diseaseDetails = diseaseDetails;
			return this;
		}

		public Builder withReferencePeriod(Date referencePeriodFrom, Date referencePeriodTo) {
			this.referencePeriodFrom = referencePeriodFrom;
			this.referencePeriodTo = referencePeriodTo;
			return this;
		}

		public ImmunizationListCriteria build() {
			return new ImmunizationListCriteria(this);
		}
	}

	private ImmunizationListCriteria(Builder builder) {
		this.personReferenceDto = builder.personReferenceDto;
		this.disease = builder.disease;
		this.diseaseDetails = builder.diseaseDetails;
		this.referencePeriodFrom = builder.referencePeriodFrom;
		this.referencePeriodTo = builder.referencePeriodTo;
	}

	public PersonReferenceDto getPerson() {
		return personReferenceDto;
	}

	public Disease getDisease() {
		return disease;
	}

	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public Date getReferencePeriodFrom() {
		return referencePeriodFrom;
	}

	public Date getReferencePeriodTo() {
		return referencePeriodTo;
	}
}
