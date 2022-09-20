/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.sormastosormas.entities;

import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;

import java.io.Serializable;

import javax.validation.Valid;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;

@AuditedClass
public abstract class SormasToSormasEntityDto<T extends SormasToSormasShareableDto> implements Serializable {

	private static final long serialVersionUID = -1142043326721172412L;

	@Valid
	@AuditInclude
	private T entity;

	protected SormasToSormasEntityDto() {
	}

	protected SormasToSormasEntityDto(T entity) {
		this.entity = entity;
	}

	public T getEntity() {
		return entity;
	}
}
