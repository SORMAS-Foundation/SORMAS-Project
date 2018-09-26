package de.symeda.sormas.backend.util;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.backend.common.AbstractDomainObject;

public final class DtoHelper {
	
	/**
	 * some inaccuracy is ok, because of the rest conversion
	 */
	public static final int CHANGE_DATE_TOLERANCE_MS = 1000;
	
	public static void validateDto(EntityDto dto, AbstractDomainObject entity) {
		if (entity.getChangeDate() != null 
				&& (dto.getChangeDate() == null || dto.getChangeDate().getTime() + CHANGE_DATE_TOLERANCE_MS < entity.getChangeDate().getTime())) {
			throw new UnsupportedOperationException("Dto is older than existing entity: " + entity.getUuid());
		}
	}

	public static void fillDto(EntityDto dto, AbstractDomainObject entity) {
		dto.setCreationDate(entity.getCreationDate());
		dto.setChangeDate(entity.getChangeDate());
		dto.setUuid(entity.getUuid());
	}
	
}
