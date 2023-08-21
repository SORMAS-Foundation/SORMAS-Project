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

package de.symeda.sormas.api.task;

import java.io.Serializable;

public class TaskJurisdictionFlagsDto implements Serializable {

	private Boolean isInJurisdiction;
	private Boolean isCaseInJurisdiction;
	private Boolean isContactInJurisdiction;
	private Boolean isContactCaseInJurisdiction;
	private Boolean isEventInJurisdiction;
	private Boolean isTravelEntryInJurisdiction;
	private Boolean isEnvironmentInJurisdiction;

	public TaskJurisdictionFlagsDto(Object[] flags) {
		this(
			(boolean) flags[1],
			(boolean) flags[2],
			(boolean) flags[3],
			(boolean) flags[4],
			(boolean) flags[5],
			(boolean) flags[6],
			(boolean) flags[7]);
	}

	public TaskJurisdictionFlagsDto(
		boolean isInJurisdiction,
		boolean isCaseInJurisdiction,
		boolean isContactInJurisdiction,
		boolean isContactCaseInJurisdiction,
		boolean isEventInJurisdiction,
		boolean isTravelEntryInJurisdiction,
		boolean isEnvironmentInJurisdiction) {

		this.isInJurisdiction = isInJurisdiction;
		this.isCaseInJurisdiction = isCaseInJurisdiction;
		this.isContactInJurisdiction = isContactInJurisdiction;
		this.isContactCaseInJurisdiction = isContactCaseInJurisdiction;
		this.isEventInJurisdiction = isEventInJurisdiction;
		this.isTravelEntryInJurisdiction = isTravelEntryInJurisdiction;
		this.isEnvironmentInJurisdiction = isEnvironmentInJurisdiction;
	}

	public Boolean getInJurisdiction() {
		return isInJurisdiction;
	}

	public Boolean getCaseInJurisdiction() {
		return isCaseInJurisdiction;
	}

	public Boolean getContactInJurisdiction() {
		return isContactInJurisdiction;
	}

	public Boolean getContactCaseInJurisdiction() {
		return isContactCaseInJurisdiction;
	}

	public Boolean getEventInJurisdiction() {
		return isEventInJurisdiction;
	}

	public Boolean getTravelEntryInJurisdiction() {
		return isTravelEntryInJurisdiction;
	}

	public Boolean getEnvironmentInJurisdiction() {
		return isEnvironmentInJurisdiction;
	}
}
