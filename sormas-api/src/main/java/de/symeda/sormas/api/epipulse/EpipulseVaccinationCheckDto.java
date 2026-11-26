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
 * One vaccination dose the patient received, as supplied by the export aggregate.
 *
 * <p>
 * Only doses recorded under an immunization whose means is in
 * {@link EpipulseMapping#VACCINATION_DOSE_MEANS} reach this DTO; the aggregate filters the rest.
 *
 * <p>
 * The immunization id links the dose to its {@link EpipulseImmunizationCheckDto}: a dose with no
 * vaccination date is placed on the newest dated dose of the same immunization.
 */
public class EpipulseVaccinationCheckDto {

	private Long immunizationId;
	private Date vaccinationDate;

	public Long getImmunizationId() {
		return immunizationId;
	}

	public void setImmunizationId(Long immunizationId) {
		this.immunizationId = immunizationId;
	}

	public Date getVaccinationDate() {
		return vaccinationDate;
	}

	public void setVaccinationDate(Date vaccinationDate) {
		this.vaccinationDate = vaccinationDate;
	}
}
