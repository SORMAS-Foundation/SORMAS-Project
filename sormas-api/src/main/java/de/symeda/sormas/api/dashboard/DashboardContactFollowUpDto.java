/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.dashboard;

import de.symeda.sormas.api.audit.AuditedClass;

import java.io.Serializable;

@AuditedClass
public class DashboardContactFollowUpDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207650L;
	private int followUpContactsCount;
	private int cooperativeContactsCount;
	private int cooperativeContactsPercentage;
	private int uncooperativeContactsCount;
	private int uncooperativeContactsPercentage;
	private int unavailableContactsCount;
	private int unavailableContactsPercentage;
	private int neverVisitedContactsCount;
	private int notVisitedContactsPercentage;

	private int missedVisitsOneDay;
	private int missedVisitsTwoDays;
	private int missedVisitsThreeDays;
	private int missedVisitsGtThreeDays;

	public DashboardContactFollowUpDto(
		int followUpContactsCount,
		int cooperativeContactsCount,
		int cooperativeContactsPercentage,
		int uncooperativeContactsCount,
		int uncooperativeContactsPercentage,
		int unavailableContactsCount,
		int unavailableContactsPercentage,
		int neverVisitedContactsCount,
		int notVisitedContactsPercentage,
		int missedVisitsOneDay,
		int missedVisitsTwoDays,
		int missedVisitsThreeDays,
		int missedVisitsGtThreeDays) {
		this.followUpContactsCount = followUpContactsCount;
		this.cooperativeContactsCount = cooperativeContactsCount;
		this.cooperativeContactsPercentage = cooperativeContactsPercentage;
		this.uncooperativeContactsCount = uncooperativeContactsCount;
		this.uncooperativeContactsPercentage = uncooperativeContactsPercentage;
		this.unavailableContactsCount = unavailableContactsCount;
		this.unavailableContactsPercentage = unavailableContactsPercentage;
		this.neverVisitedContactsCount = neverVisitedContactsCount;
		this.notVisitedContactsPercentage = notVisitedContactsPercentage;
		this.missedVisitsOneDay = missedVisitsOneDay;
		this.missedVisitsTwoDays = missedVisitsTwoDays;
		this.missedVisitsThreeDays = missedVisitsThreeDays;
		this.missedVisitsGtThreeDays = missedVisitsGtThreeDays;
	}

	public int getFollowUpContactsCount() {
		return followUpContactsCount;
	}

	public int getCooperativeContactsCount() {
		return cooperativeContactsCount;
	}

	public int getCooperativeContactsPercentage() {
		return cooperativeContactsPercentage;
	}

	public int getUncooperativeContactsCount() {
		return uncooperativeContactsCount;
	}

	public int getUncooperativeContactsPercentage() {
		return uncooperativeContactsPercentage;
	}

	public int getUnavailableContactsCount() {
		return unavailableContactsCount;
	}

	public int getUnavailableContactsPercentage() {
		return unavailableContactsPercentage;
	}

	public int getNeverVisitedContactsCount() {
		return neverVisitedContactsCount;
	}

	public int getNotVisitedContactsPercentage() {
		return notVisitedContactsPercentage;
	}

	public int getMissedVisitsOneDay() {
		return missedVisitsOneDay;
	}

	public int getMissedVisitsTwoDays() {
		return missedVisitsTwoDays;
	}

	public int getMissedVisitsThreeDays() {
		return missedVisitsThreeDays;
	}

	public int getMissedVisitsGtThreeDays() {
		return missedVisitsGtThreeDays;
	}
}
