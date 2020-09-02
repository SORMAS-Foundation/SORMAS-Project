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

import de.symeda.sormas.api.HasUuid;

/**
 * Interface to build the entity listener for JPA entities.
 * 
 * @author Oliver Milke
 * @since 14.04.2016
 */
public interface AuditListener {

	/**
	 * To be used for
	 * <ul>
	 * <li>PrePersist</li>
	 * <li>PreUpdate</li>
	 * </ul>
	 * Performs the comparison of the entity with its original state.
	 * 
	 * @param o
	 *            The entity to be audited.
	 */
	void prePersist(HasUuid o);

	/**
	 * To be used for
	 * <ul>
	 * <li>PostLoad</li>
	 * </ul>
	 * Saves the original state of an entity for later comparison.
	 * 
	 * @param o
	 *            The entity to be audited.
	 */
	void postLoad(HasUuid o);

	/**
	 * To be used for
	 * <ul>
	 * <li>PreRemove</li>
	 * </ul>
	 * Logs the deleting of an entity.
	 * 
	 * @param o
	 *            The entity to be audited.
	 */
	void preRemove(HasUuid o);
}
