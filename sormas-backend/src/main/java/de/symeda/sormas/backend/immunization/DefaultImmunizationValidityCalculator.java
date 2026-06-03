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

package de.symeda.sormas.backend.immunization;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.immunization.ImmunizationValidityCalculator;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.utils.UtilDate;

/**
 * Default validity calculator aligned with legacy vaccination relevance behavior.
 * <p>
 * For vaccination-based immunizations, validity starts with the entered immunization date
 * and does not expire.
 * </p>
 */
public class DefaultImmunizationValidityCalculator implements ImmunizationValidityCalculator {

	@Override
	public Date calculateValidFrom(Disease disease, MeansOfImmunization meansOfImmunization, Date immunizationDate, Integer numberOfDoses) {
		if (!MeansOfImmunization.isVaccination(meansOfImmunization) || immunizationDate == null) {
			return null;
		}

		return immunizationDate;
	}

	@Override
	public Date calculateValidUntil(Disease disease, MeansOfImmunization meansOfImmunization, Date validFrom, Integer numberOfDoses) {
		if (!MeansOfImmunization.isVaccination(meansOfImmunization) || validFrom == null) {
			return null;
		}

		return UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL);
	}
}
