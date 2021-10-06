package de.symeda.sormas.api.travelentry;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class TravelEntryListCriteria extends BaseCriteria {

	private final PersonReferenceDto personReferenceDto;
	private final CaseReferenceDto caseReferenceDto;

	public static class Builder {

		private PersonReferenceDto personReferenceDto;
		private CaseReferenceDto caseReferenceDto;

		public Builder withPerson(PersonReferenceDto personReferenceDto) {
			this.personReferenceDto = personReferenceDto;
			return this;
		}

		public Builder withCase(CaseReferenceDto caseReferenceDto) {
			this.caseReferenceDto = caseReferenceDto;
			return this;
		}

		public TravelEntryListCriteria build() {
			return new TravelEntryListCriteria(this);
		}
	}

	private TravelEntryListCriteria(Builder builder) {
		this.personReferenceDto = builder.personReferenceDto;
		this.caseReferenceDto = builder.caseReferenceDto;
	}

	public PersonReferenceDto getPersonReferenceDto() {
		return personReferenceDto;
	}

	public CaseReferenceDto getCaseReferenceDto() {
		return caseReferenceDto;
	}
}
