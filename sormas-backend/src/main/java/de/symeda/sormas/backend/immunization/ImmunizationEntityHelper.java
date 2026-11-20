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

package de.symeda.sormas.backend.immunization;

import java.util.Date;

import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.vaccination.Vaccination;

public final class ImmunizationEntityHelper {

	private ImmunizationEntityHelper() {

	}

	public static Date getDateForComparison(Immunization immunization, boolean withReportDate) {
		Date periodEnd = getPeriodEnd(immunization);
		if (periodEnd != null) {
			return periodEnd;
		}
		Date periodStart = getPeriodStart(immunization);
		if (periodStart != null) {
			return periodStart;
		}
		return withReportDate ? immunization.getReportDate() : immunization.getCreationDate();
	}

	/**
	 * Start and end date were superseded by the validity period (valid from / valid until). Legacy immunizations that have a start or
	 * end date keep using them; all others use the validity period.
	 */
	public static boolean hasLegacyPeriod(Immunization immunization) {
		return immunization.getStartDate() != null || immunization.getEndDate() != null;
	}

	/**
	 * @return the start date for legacy immunizations, otherwise valid from. See {@link #hasLegacyPeriod(Immunization)}.
	 */
	public static Date getPeriodStart(Immunization immunization) {
		return hasLegacyPeriod(immunization) ? immunization.getStartDate() : immunization.getValidFrom();
	}

	/**
	 * @return the end date for legacy immunizations, otherwise valid until. See {@link #hasLegacyPeriod(Immunization)}.
	 */
	public static Date getPeriodEnd(Immunization immunization) {
		return hasLegacyPeriod(immunization) ? immunization.getEndDate() : immunization.getValidUntil();
	}

	public static Date getVaccinationDateForComparison(Vaccination vaccination) {
		return vaccination.getVaccinationDate() != null ? vaccination.getVaccinationDate() : vaccination.getReportDate();
	}

}
