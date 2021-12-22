package de.symeda.sormas.api.immunization;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class ImmunizationListCriteria extends BaseCriteria {

	private final PersonReferenceDto personReferenceDto;
	private final Disease disease;

	public static class Builder {

		private final PersonReferenceDto personReferenceDto;
		private Disease disease;

		public Builder(PersonReferenceDto personReferenceDto) {
			this.personReferenceDto = personReferenceDto;
		}

		public Builder wihDisease(Disease disease) {
			this.disease = disease;
			return this;
		}

		public ImmunizationListCriteria build() {
			return new ImmunizationListCriteria(this);
		}
	}

	private ImmunizationListCriteria(Builder builder) {
		this.personReferenceDto = builder.personReferenceDto;
		this.disease = builder.disease;
	}

	public PersonReferenceDto getPerson() {
		return personReferenceDto;
	}

	public Disease getDisease() {
		return disease;
	}
}
