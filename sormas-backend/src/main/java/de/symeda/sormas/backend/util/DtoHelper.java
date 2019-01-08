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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.util;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.EntityDtoTooOldException;
import de.symeda.sormas.backend.common.AbstractDomainObject;

public final class DtoHelper {
	
	/**
	 * some inaccuracy is ok, because of the rest conversion
	 */
	public static final int CHANGE_DATE_TOLERANCE_MS = 1000;
	
	public static void validateDto(EntityDto dto, AbstractDomainObject entity) {
		if (entity.getChangeDate() != null 
				&& (dto.getChangeDate() == null || dto.getChangeDate().getTime() + CHANGE_DATE_TOLERANCE_MS < entity.getChangeDate().getTime())) {
			throw new EntityDtoTooOldException(dto.getUuid(), dto.getClass());
		}
	}

	public static void fillDto(EntityDto dto, AbstractDomainObject entity) {
		dto.setCreationDate(entity.getCreationDate());
		dto.setChangeDate(entity.getChangeDate());
		dto.setUuid(entity.getUuid());
	}
	
}
