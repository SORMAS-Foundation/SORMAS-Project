package de.symeda.sormas.backend.util;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.backend.common.AbstractDomainObject;

public final class DtoHelper {

	public static ReferenceDto toReferenceDto(AbstractDomainObject entity) {
		if (entity == null) {
			return null;
		}
		ReferenceDto dto = new ReferenceDto();
		dto.setUuid(entity.getUuid());
		dto.setChangeDate(entity.getChangeDate());
		dto.setCaption(entity.toString());
		return dto;
	}
}
