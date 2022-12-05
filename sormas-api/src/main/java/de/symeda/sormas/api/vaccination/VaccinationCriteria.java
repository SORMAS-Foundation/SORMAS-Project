/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.vaccination;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class VaccinationCriteria extends BaseCriteria {

	private static final long serialVersionUID = 577972890587599470L;

	private final PersonReferenceDto personReferenceDto;
	private final List<PersonReferenceDto> personReferences;
	private final Disease disease;
	private VaccinationAssociationType vaccinationAssociationType;
	private CaseReferenceDto caseReference;
	private ContactReferenceDto contactReference;
	private EventParticipantReferenceDto eventParticipantReference;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;

	public static class Builder {

		private final PersonReferenceDto personReferenceDto;
		private final List<PersonReferenceDto> personReferences;
		private Disease disease;

		public Builder(PersonReferenceDto personReferenceDto) {
			this.personReferenceDto = personReferenceDto;
			this.personReferences = null;
		}

		public Builder(List<PersonReferenceDto> personReferences) {
			this.personReferences = personReferences;
			this.personReferenceDto = null;
		}

		public VaccinationCriteria.Builder withDisease(Disease disease) {
			this.disease = disease;
			return this;
		}

		public VaccinationCriteria build() {
			return new VaccinationCriteria(this);
		}
	}

	private VaccinationCriteria(VaccinationCriteria.Builder builder) {
		this.personReferenceDto = builder.personReferenceDto;
		this.personReferences = builder.personReferences;
		this.disease = builder.disease;
	}

	public PersonReferenceDto getPerson() {
		return personReferenceDto;
	}

	public Disease getDisease() {
		return disease;
	}

	public List<PersonReferenceDto> getPersons() {
		return personReferences;
	}

	public VaccinationAssociationType getVaccinationAssociationType() {
		return vaccinationAssociationType;
	}

	public VaccinationCriteria vaccinationAssociationType(VaccinationAssociationType vaccinationAssociationType) {
		this.vaccinationAssociationType = vaccinationAssociationType;
		return this;
	}

	public CaseReferenceDto getCaseReference() {
		return caseReference;
	}

	public VaccinationCriteria caseReference(CaseReferenceDto caseReference) {
		this.caseReference = caseReference;
		return this;
	}

	public ContactReferenceDto getContactReference() {
		return contactReference;
	}

	public VaccinationCriteria contactReference(ContactReferenceDto contactReference) {
		this.contactReference = contactReference;
		return this;
	}

	public EventParticipantReferenceDto getEventParticipantReference() {
		return eventParticipantReference;
	}

	public VaccinationCriteria eventParticipantReference(EventParticipantReferenceDto eventParticipantReference) {
		this.eventParticipantReference = eventParticipantReference;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public VaccinationCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public VaccinationCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}
}
