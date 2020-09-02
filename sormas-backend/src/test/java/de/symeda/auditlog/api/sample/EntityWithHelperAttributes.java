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
package de.symeda.auditlog.api.sample;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.HasUuid;

@Audited
public class EntityWithHelperAttributes implements HasUuid {

	private final String uuid;
	private EntityWithIgnoredMethods firstEntity;
	private EntityWithIgnoredMethods secondEntity;
	private EntityWithIgnoredMethods thirdEntity;

	public EntityWithHelperAttributes(String uuid) {
		this.uuid = uuid;
	}

	@Override
	@AuditedIgnore
	public String getUuid() {
		return uuid;
	}

	@OneToOne
	public EntityWithIgnoredMethods getFirstEntity() {
		return firstEntity;
	}

	public void setFirstEntity(EntityWithIgnoredMethods firstEntity) {
		this.firstEntity = firstEntity;
	}

	@ManyToOne
	public EntityWithIgnoredMethods getSecondEntity() {
		return secondEntity;
	}

	public void setSecondEntity(EntityWithIgnoredMethods secondEntity) {
		this.secondEntity = secondEntity;
	}

	@OneToOne(mappedBy = "thirdEntity")
	public EntityWithIgnoredMethods getThirdEntity() {
		return thirdEntity;
	}

	public void setThirdEntity(EntityWithIgnoredMethods thirdEntity) {
		this.thirdEntity = thirdEntity;
	}
}
