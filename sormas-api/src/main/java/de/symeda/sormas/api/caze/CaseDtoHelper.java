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

package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class CaseDtoHelper {

	private CaseDtoHelper() {
	}

	public static RegionReferenceDto getRegionWithFallback(CaseDataDto caze) {
		if (caze.getRegion() == null) {
			return caze.getResponsibleRegion();
		}

		return caze.getRegion();
	}

	public static DistrictReferenceDto getDistrictWithFallback(CaseDataDto caze) {
		if (caze.getDistrict() == null) {
			return caze.getResponsibleDistrict();
		}

		return caze.getDistrict();
	}

	public static CommunityReferenceDto getCommunityWithFallback(CaseDataDto caze) {
		if (caze.getRegion() == null) {
			return caze.getResponsibleCommunity();
		}

		return caze.getCommunity();
	}
}
