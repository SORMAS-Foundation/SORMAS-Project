/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

package de.symeda.sormas.api.epipulse;

import java.util.Date;

/**
 * One acquired vaccination course from the export aggregate.
 *
 * <p>
 * Contains only fields needed for vaccination status and last-dose derivation.
 */
public class EpipulseImmunizationCheckDto {

	private Long id;
	private Date validFrom;
	private Date validUntil;
	private Integer numberOfDoses;

	/**
	 * Database id of the course, used to match linked vaccination doses.
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/** When the course starts protecting the patient. */
	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	/** When that protection expires. Lifelong protection is stored as a date in the year 9999. */
	public Date getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Date validUntil) {
		this.validUntil = validUntil;
	}

	/**
	 * Number of doses in this acquired course.
	 *
	 * <p>
	 * This is the recorded dose count, not a planned target.
	 *
	 * @apiNote Keep null-safe handling: an acquired course may still miss dose count after manual
	 *          status edits.
	 */
	public Integer getNumberOfDoses() {
		return numberOfDoses;
	}

	public void setNumberOfDoses(Integer numberOfDoses) {
		this.numberOfDoses = numberOfDoses;
	}
}
