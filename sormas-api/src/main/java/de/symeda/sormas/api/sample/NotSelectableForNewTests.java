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

package de.symeda.sormas.api.sample;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@link PathogenTestType} value as a legacy method that must no longer be offered when
 * adding a <em>new</em> pathogen test, regardless of disease — for example methods that were merged
 * into a broader entry (the old IgM/IgG/IgA serum antibody values, now ELISA) or superseded
 * (e.g. {@code CQ_VALUE_DETECTION}).
 *
 * <p>
 * The constant is intentionally kept rather than deleted: it is still referenced by case
 * classification, external-message mapping and exports, and existing tests recorded with it must
 * continue to display and export unchanged. Only the new-test method picker filters it out (read at
 * runtime by {@link PathogenTestType#isSelectableForNewTests(PathogenTestType)}); the "keep the
 * currently-saved value" behaviour of the form still lets such records be edited.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotSelectableForNewTests {
}
