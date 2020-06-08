/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.configuration.outbreak;

import java.util.HashSet;
import java.util.Set;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class OutbreakRegionConfiguration {

	private Disease disease;
	private int totalDistricts;
	private RegionReferenceDto region;
	private Set<DistrictReferenceDto> affectedDistricts;

	public OutbreakRegionConfiguration(Disease disease, RegionReferenceDto region, int totalDistricts, Set<DistrictReferenceDto> affectedDistricts) {
		this.disease = disease;
		this.region = region;
		this.totalDistricts = totalDistricts;
		if (affectedDistricts != null) {
			this.affectedDistricts = affectedDistricts;
		} else {
			this.affectedDistricts = new HashSet<>();
		}
	}

	public int getTotalDistricts() {
		return totalDistricts;
	}

	public void setTotalDistricts(int totalDistricts) {
		this.totalDistricts = totalDistricts;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public Set<DistrictReferenceDto> getAffectedDistricts() {
		return affectedDistricts;
	}

	public void setAffectedDistricts(Set<DistrictReferenceDto> affectedDistricts) {
		this.affectedDistricts = affectedDistricts;
	}

	@Override
	public String toString() {

		if (affectedDistricts.isEmpty()) {
			return "0";
		} else {
			return affectedDistricts.size() + "/" + totalDistricts;
		}
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}
}
