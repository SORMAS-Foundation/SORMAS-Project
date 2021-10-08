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

package de.symeda.sormas.backend.share;

import java.util.Date;

import de.symeda.sormas.api.share.ExternalShareStatus;

public class ExternalShareInfoCountAndLatestDate {

	private final String associatedObjectUuid;

	private final Long count;

	private final Date latestDate;

	private final ExternalShareStatus latestStatus;

	public ExternalShareInfoCountAndLatestDate(String associatedObjectUuid, Long count, Date latestDate, ExternalShareStatus latestStatus) {
		this.associatedObjectUuid = associatedObjectUuid;
		this.count = count;
		this.latestDate = latestDate;
		this.latestStatus = latestStatus;
	}

	public String getAssociatedObjectUuid() {
		return associatedObjectUuid;
	}

	public Long getCount() {
		return count;
	}

	public Date getLatestDate() {
		return latestDate;
	}

	public ExternalShareStatus getLatestStatus() {
		return latestStatus;
	}
}
