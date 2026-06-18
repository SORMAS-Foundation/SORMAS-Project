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

package de.symeda.sormas.backend.systemconfiguration;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.common.QueryJoins;

/**
 * Class to handle joins for SystemConfigurationValue entities.
 */
public class SystemConfigurationValueJoins extends QueryJoins<SystemConfigurationValue> {

	private Join<SystemConfigurationValue, SystemConfigurationCategory> category;

	/**
	 * Constructor to initialize the root for joins.
	 *
	 * @param root
	 *            the root from which joins will be created
	 */
	public SystemConfigurationValueJoins(final From<?, SystemConfigurationValue> root) {
		super(root);
	}

	/**
	 * Get or create a join to the SystemConfigurationCategory entity.
	 *
	 * @return the join to the SystemConfigurationCategory entity
	 */
	public Join<SystemConfigurationValue, SystemConfigurationCategory> getCategory() {
		return getOrCreate(category, SystemConfigurationValue.CATEGORY_FIELD_NAME, JoinType.LEFT, this::setCategory);
	}

	private void setCategory(final Join<SystemConfigurationValue, SystemConfigurationCategory> category) {
		this.category = category;
	}
}
