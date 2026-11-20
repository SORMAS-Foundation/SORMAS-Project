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

import java.util.Date;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

/**
 * Filters for the climate job list on the Climate data screen.
 */
@AuditedClass(includeAllFields = true)
public class ClimateJobCriteria extends BaseCriteria {

	private static final long serialVersionUID = 4466903163862711027L;

	private ClimateJobType jobType;
	private ClimateJobStatus status;
	private Boolean onlyRelinksWithSkippedDays;
	private Date createdFrom;

	@IgnoreForUrl
	public ClimateJobType getJobType() {
		return jobType;
	}

	public ClimateJobCriteria jobType(ClimateJobType jobType) {
		this.jobType = jobType;
		return this;
	}

	@IgnoreForUrl
	public ClimateJobStatus getStatus() {
		return status;
	}

	public ClimateJobCriteria status(ClimateJobStatus status) {
		this.status = status;
		return this;
	}

	/**
	 * @return true to list only district relinks that left days uncopied because the cell already had data
	 */
	@IgnoreForUrl
	public Boolean getOnlyRelinksWithSkippedDays() {
		return onlyRelinksWithSkippedDays;
	}

	public ClimateJobCriteria onlyRelinksWithSkippedDays(Boolean onlyRelinksWithSkippedDays) {
		this.onlyRelinksWithSkippedDays = onlyRelinksWithSkippedDays;
		return this;
	}

	/**
	 * @return the earliest creation day listed; open jobs are listed whatever their age
	 */
	@IgnoreForUrl
	public Date getCreatedFrom() {
		return createdFrom;
	}

	public ClimateJobCriteria createdFrom(Date createdFrom) {
		this.createdFrom = createdFrom;
		return this;
	}
}
