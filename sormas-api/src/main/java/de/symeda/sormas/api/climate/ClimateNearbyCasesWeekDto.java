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

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.utils.EpiWeek;

/**
 * The cases near an environment in one epi week: those placed within the radius (by their probable infection environment,
 * else their home address), and those without a point that were matched by community, with their UUIDs so the week's
 * cases can be opened.
 */
@AuditedClass
public class ClimateNearbyCasesWeekDto implements Serializable {

	private static final long serialVersionUID = -4870362130935107246L;

	private final EpiWeek week;
	private final List<String> caseUuids = new ArrayList<>();
	private final List<String> exposureCaseUuids = new ArrayList<>();
	private final List<String> communityCaseUuids = new ArrayList<>();

	public ClimateNearbyCasesWeekDto(EpiWeek week) {
		this.week = week;
	}

	public EpiWeek getWeek() {
		return week;
	}

	/**
	 * @return the cases whose point lies within the radius
	 */
	public int getCount() {
		return caseUuids.size();
	}

	/**
	 * @return the cases without a point counted because their community is the environment's (all of them for an
	 *         environment without usable GPS)
	 */
	public int getCommunityCount() {
		return communityCaseUuids.size();
	}

	/**
	 * @return the cases of {@link #getCount()} placed by an exposure marked as the probable infection environment
	 */
	public int getExposureCount() {
		return exposureCaseUuids.size();
	}

	/**
	 * @return the cases of {@link #getCount()} placed by their home address
	 */
	public int getHomeCount() {
		return caseUuids.size() - exposureCaseUuids.size();
	}

	public List<String> getCaseUuids() {
		return caseUuids;
	}

	/**
	 * @return the UUIDs of the cases placed by their probable infection environment, a subset of {@link #getCaseUuids()}
	 */
	public List<String> getExposureCaseUuids() {
		return exposureCaseUuids;
	}

	public List<String> getCommunityCaseUuids() {
		return communityCaseUuids;
	}
}
