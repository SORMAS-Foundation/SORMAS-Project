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
 * Declares which {@link ResultValueType}(s) a {@link PathogenTestType} method produces, driving the
 * result fields shown on the pathogen-test form. Read at runtime by
 * {@link PathogenTestType#getResultValueTypes(PathogenTestType)}.
 *
 * <p>An explicit empty array ({@code @ResultValueTypeRel({})}) means the method has <em>no</em> result
 * value type of its own — the qualitative selector and every quantitative field are hidden, and the
 * stored result is coerced to {@link PathogenTestResultType#NOT_APPLICABLE}. Use this only when the
 * method's real result is captured by a dedicated component elsewhere (e.g. Antibiotic Susceptibility's
 * drug-susceptibility grid). It is <em>not</em> a placeholder for "to be filled in later" — leave the
 * annotation off entirely if you want the method to fall back to the default qualitative behaviour.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ResultValueTypeRel {

	ResultValueType[] value();
}
