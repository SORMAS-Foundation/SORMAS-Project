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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import java.util.List;

public class VaccinationListCriteria extends BaseCriteria {

	private static final long serialVersionUID = 577972890587599470L;

	private final PersonReferenceDto personReferenceDto;
	private final List<PersonReferenceDto> personReferences;
	private final Disease disease;

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

		public VaccinationListCriteria.Builder withDisease(Disease disease) {
			this.disease = disease;
			return this;
		}

		public VaccinationListCriteria build() {
			return new VaccinationListCriteria(this);
		}
	}

	private VaccinationListCriteria(VaccinationListCriteria.Builder builder) {
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
}
