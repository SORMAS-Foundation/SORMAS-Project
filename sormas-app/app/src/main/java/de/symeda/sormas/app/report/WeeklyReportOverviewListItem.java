/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.report;

import java.util.Date;

import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.user.User;

public class WeeklyReportOverviewListItem {

	private Facility healthFacility;
	private Community community;
	private User user;
	private int numberOfCases;
	private Date reportDate;

	public WeeklyReportOverviewListItem(Facility healthFacility, Community community, User user, int numberOfCases, Date reportDate) {
		this.healthFacility = healthFacility;
		this.community = community;
		this.user = user;
		this.numberOfCases = numberOfCases;
		this.reportDate = reportDate;
	}

	public Facility getHealthFacility() {
		return healthFacility;
	}

	public User getUser() {
		return user;
	}

	public int getNumberOfCases() {
		return numberOfCases;
	}

	public Community getCommunity() {
		return community;
	}

	public Date getReportDate() {
		return reportDate;
	}
}
