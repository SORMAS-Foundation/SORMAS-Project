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
package de.symeda.sormas.backend.util;

public interface ModelConstants {

	String PERSISTENCE_UNIT_NAME = "SormasPU";
	String PERSISTENCE_UNIT_NAME_AUDITLOG = "auditlogPU";


	/**
	 * <h2>
	 * A query hint to make JPA entities loaded as read-only to avoid caching and dirty checks. Usage:
	 * </h2>
	 *
	 *<p>
	 *  <b>
	 * "List<JpaEntity> resultList = em.createQuery(cq).setHint(ModelConstants.READ_ONLY, true).getResultList();"
	 * Note: This does not have any effect on Multiselect-Queries directly into DTOs.
	 * </b>
	 * </p>
	 */
	String HINT_READ_ONLY = "org.hibernate.readOnly";

}
