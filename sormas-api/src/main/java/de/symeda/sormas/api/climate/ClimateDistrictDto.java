/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.api.climate;

import java.io.Serializable;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

/**
 * A district with its region, as the Climate data screen lists districts without a climate point.
 */
@AuditedClass
public class ClimateDistrictDto implements Serializable {

	private static final long serialVersionUID = -3517726350958925140L;

	public static final String I18N_PREFIX = "ClimateDistrict";

	public static final String REGION = "region";
	public static final String DISTRICT = "district";

	private RegionReferenceDto region;
	private DistrictReferenceDto district;

	public ClimateDistrictDto(RegionReferenceDto region, DistrictReferenceDto district) {
		this.region = region;
		this.district = district;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}
}
