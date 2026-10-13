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
package de.symeda.sormas.api.event.sevenoneseven;

import java.io.Serializable;

import de.symeda.sormas.api.audit.AuditedClass;

/**
 * Number of 7-1-7 assessments per timeliness result for one interval, early response action or the whole 7-1-7 target.
 * <p>
 * The share meeting the target only counts evaluable assessments (meeting or not meeting the target). Missing and incomplete data,
 * data errors and not applicable actions are counted separately, so gaps stay visible without distorting the share.
 */
@AuditedClass
public class Event717OutcomeCountsDto implements Serializable {

	private static final long serialVersionUID = -1817396355467201553L;

	private final int withinTarget;
	private final int overTarget;
	private final int missing;
	private final int incomplete;
	private final int dataError;
	private final int notApplicable;

	public Event717OutcomeCountsDto(int withinTarget, int overTarget, int missing, int incomplete, int dataError, int notApplicable) {

		this.withinTarget = withinTarget;
		this.overTarget = overTarget;
		this.missing = missing;
		this.incomplete = incomplete;
		this.dataError = dataError;
		this.notApplicable = notApplicable;
	}

	public int getWithinTarget() {
		return withinTarget;
	}

	public int getOverTarget() {
		return overTarget;
	}

	public int getMissing() {
		return missing;
	}

	public int getIncomplete() {
		return incomplete;
	}

	public int getDataError() {
		return dataError;
	}

	public int getNotApplicable() {
		return notApplicable;
	}

	/**
	 * @return The number of assessments meeting or not meeting the target.
	 */
	public int getEvaluable() {
		return withinTarget + overTarget;
	}

	/**
	 * @return The rounded percentage of evaluable assessments meeting the target, or null if none is evaluable.
	 */
	public Integer getPercentageWithinTarget() {

		int evaluable = getEvaluable();
		return evaluable == 0 ? null : (int) Math.round(withinTarget * 100.0 / evaluable);
	}
}
