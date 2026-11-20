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
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;

/**
 * Weekly counts of cases near an environment, for its Climate data tab. Only cases the user may see are counted.
 */
@AuditedClass
public class ClimateNearbyCasesDto implements Serializable {

	private static final long serialVersionUID = 5023987148201834786L;

	private final EnvironmentReferenceDto environment;
	private final Disease disease;
	private final double radiusKm;
	private final boolean byCommunityOnly;
	private final List<ClimateNearbyCasesWeekDto> weeks = new ArrayList<>();

	public ClimateNearbyCasesDto(EnvironmentReferenceDto environment, Disease disease, double radiusKm, boolean byCommunityOnly) {
		this.environment = environment;
		this.disease = disease;
		this.radiusKm = radiusKm;
		this.byCommunityOnly = byCommunityOnly;
	}

	public EnvironmentReferenceDto getEnvironment() {
		return environment;
	}

	/**
	 * @return the disease counted, or null for all diseases
	 */
	public Disease getDisease() {
		return disease;
	}

	public double getRadiusKm() {
		return radiusKm;
	}

	/**
	 * @return true when the environment has no usable GPS, so every case is matched by community and the radius is not used
	 */
	public boolean isByCommunityOnly() {
		return byCommunityOnly;
	}

	public List<ClimateNearbyCasesWeekDto> getWeeks() {
		return weeks;
	}

	/**
	 * @return the cases matched by community over all weeks, for the note on cases without GPS
	 */
	public int getCommunityTotal() {
		return weeks.stream().mapToInt(ClimateNearbyCasesWeekDto::getCommunityCount).sum();
	}
}
