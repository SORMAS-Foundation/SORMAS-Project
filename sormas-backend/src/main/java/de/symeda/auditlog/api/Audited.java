/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.auditlog.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that the class provided with this annotation should be audited.
 * </p>
 * Every instantiated object whose class is marked with the {@link Audited} annotation and all super classes with {@link Audited} will
 * be taken into account.
 * Should the depth of inheritance contain an element that is not marked with {@link Audited}, this element and all its attributes
 * will be skipped.
 * 
 * @author Oliver Milke
 * @since 08.04.2016
 */
@Target({
	ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Audited {

}
