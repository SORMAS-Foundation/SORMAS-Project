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
import java.util.Collection;

import javax.persistence.Embeddable;

import de.symeda.auditlog.api.value.format.CollectionFormatter;
import de.symeda.auditlog.api.value.format.DefaultCollectionFormatter;
import de.symeda.auditlog.api.value.format.ValueFormatter;

/**
 * Determines for a {@link Collection} of an entity that its elements should be checked for changes by the AuditLog.
 * <p/>
 * Not applicable for Collection attributes within an {@link Embeddable}.
 * 
 * @author Oliver Milke
 * @since 11.04.2016
 */
@Target({
	ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditedCollection {

	/**
	 * Determines the Formatter for the elements of this collection.
	 * This String representation is also the foundation for the discovery of a change.
	 * </p>
	 * Possible uses:
	 * <ol>
	 * <li>Default: {@link DefaultCollectionFormatter}.</li>
	 * <li>Other {@link CollectionFormatter} that contains a custom {@link ValueFormatter} to format the Collection elements.
	 * </li>
	 * <li>A {@link ValueFormatter} for Collection formatting. The sorting will be ensured by {@link DefaultCollectionFormatter}.</li>
	 * </ol>
	 */
	Class<? extends ValueFormatter<?>> value() default DefaultCollectionFormatter.class;
}
