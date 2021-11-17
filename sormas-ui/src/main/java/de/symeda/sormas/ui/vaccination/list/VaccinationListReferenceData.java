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

package de.symeda.sormas.ui.vaccination.list;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;

public class VaccinationListReferenceData {

	private final RegionReferenceDto region;
	private final DistrictReferenceDto district;
	private final PersonReferenceDto person;
	private final Disease disease;

	public VaccinationListReferenceData(RegionReferenceDto region, DistrictReferenceDto district, PersonReferenceDto person, Disease disease) {
		this.region = region;
		this.district = district;
		this.person = person;
		this.disease = disease;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public Disease getDisease() {
		return disease;
	}
}
