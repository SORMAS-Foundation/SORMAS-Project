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

import java.sql.Timestamp;
import java.util.function.Supplier;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.OutdatedEntityException;
import de.symeda.sormas.api.uuid.MismatchUuidException;
import de.symeda.sormas.backend.common.AbstractDomainObject;

public final class DtoHelper {

	private DtoHelper() {
		// Hide Utility Class Constructor
	}

	/**
	 * some inaccuracy is ok, because of the rest conversion
	 */
	public static final int CHANGE_DATE_TOLERANCE_MS = 1000;

	public static void validateDto(EntityDto dto, AbstractDomainObject entity, boolean checkChangeDate) {

		if (checkChangeDate
			&& entity.getChangeDate() != null
			&& (dto.getChangeDate() == null || dto.getChangeDate().getTime() + CHANGE_DATE_TOLERANCE_MS < entity.getChangeDate().getTime())) {
			throw new OutdatedEntityException(dto.getUuid(), dto.getClass(), dto.getChangeDate(), entity.getChangeDate());
		}
	}

	public static void fillDto(EntityDto dto, AbstractDomainObject entity) {
		dto.setCreationDate(entity.getCreationDate());
		dto.setChangeDate(entity.getChangeDate());
		dto.setUuid(entity.getUuid());
	}

	public static <T extends AbstractDomainObject> T fillOrBuildEntity(EntityDto source, T target, Supplier<T> newEntity, boolean checkChangeDate) {
		return fillOrBuildEntity(source, target, newEntity, checkChangeDate, false);
	}

	/**
	 * @return The target entity or a new entity of target was null
	 */
	public static <T extends AbstractDomainObject> T fillOrBuildEntity(
		EntityDto source,
		T target,
		Supplier<T> newEntity,
		boolean checkChangeDate,
		boolean allowUuidOverwrite) {
		if (target == null) {
			target = newEntity.get();

			String uuid = source.getUuid() != null ? source.getUuid() : DataHelper.createUuid();
			target.setUuid(uuid);

			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		} else {
			if (DataHelper.isNullOrEmpty(target.getUuid())) {
				String uuid = source.getUuid() != null ? source.getUuid() : DataHelper.createUuid();
				target.setUuid(uuid);
			} else if (DataHelper.isNullOrEmpty(source.getUuid())) {
				// target has a uuid. do nothing -> gracefully handle missing uuids of children
			} else if (!target.getUuid().equals(source.getUuid())) {
				if (allowUuidOverwrite) {
					target.setUuid(source.getUuid());
				} else {
					throw new MismatchUuidException(target.getUuid(), target.getClass(), source.getUuid());
				}
			}
		}

		DtoHelper.validateDto(source, target, checkChangeDate);

		return target;
	}
}
