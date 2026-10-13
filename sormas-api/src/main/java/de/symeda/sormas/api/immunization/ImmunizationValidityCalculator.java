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

package de.symeda.sormas.api.immunization;

import java.time.LocalDate;
import java.util.Date;

import de.symeda.sormas.api.Disease;

public interface ImmunizationValidityCalculator {

	LocalDate LIFELONG_VALID_UNTIL = LocalDate.of(9999, 12, 31);

	Date calculateValidFrom(Disease disease, MeansOfImmunization meansOfImmunization, Date immunizationDate, Integer numberOfDoses);

	Date calculateValidUntil(Disease disease, MeansOfImmunization meansOfImmunization, Date validFrom, Integer numberOfDoses);
}
