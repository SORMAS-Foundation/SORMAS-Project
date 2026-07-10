/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Names of enum constants that were removed and whose data was merged into the annotated constant.
 *
 * <p>
 * Deleting an enum constant is a compile-time change but a runtime hazard: the old name still arrives from
 * a peer that has not upgraded, from an external message, from a REST client, or from a CSV a user exported
 * last year. A database migration cannot reach any of those. Declaring the old names here lets
 * {@link LegacyEnumHelper#resolve} translate them instead of failing, and keeps the merge documented on the
 * constant that absorbed it.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LegacyEnumNames {

	String[] value();
}
