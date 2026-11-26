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

import de.symeda.sormas.api.immunization.MeansOfImmunization;

/**
 * One immunization record of the patient for the exported disease, from the export aggregate.
 *
 * <p>
 * This field is only read for the {@code VaccinationStatus} outcomes that dated doses cannot
 * decide:
 * <ul>
 * <li>{@code UNKDOSE} - a vaccination immunization with no dated dose, valid from before the
 * reference date</li>
 * <li>{@code NOTVACC} - every vaccination immunization known to postdate the reference date, or an
 * explicit {@link MeansOfImmunization#NOT_IMMUNIZED} record</li>
 * </ul>
 * The dose count is derived from the dose rows, so this record does not carry the immunization's
 * status or planned number of doses.
 */
public class EpipulseImmunizationCheckDto {

	private Long id;
	private MeansOfImmunization meansOfImmunization;
	private Date validFrom;
	private Date validUntil;

	/** Links the doses recorded under this immunization ({@link EpipulseVaccinationCheckDto#getImmunizationId}). */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MeansOfImmunization getMeansOfImmunization() {
		return meansOfImmunization;
	}

	public void setMeansOfImmunization(MeansOfImmunization meansOfImmunization) {
		this.meansOfImmunization = meansOfImmunization;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Date validUntil) {
		this.validUntil = validUntil;
	}
}
