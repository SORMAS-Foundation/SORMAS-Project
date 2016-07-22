package de.symeda.sormas.backend.util;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;

public final class DtoHelper {

	public static ReferenceDto toReferenceDto(AbstractDomainObject entity) {
		if (entity == null) {
			return null;
		}
		ReferenceDto dto = new ReferenceDto();
		dto.setCreationDate(entity.getCreationDate());
		dto.setChangeDate(entity.getChangeDate());
		dto.setUuid(entity.getUuid());
		dto.setCaption(entity.toString());
		return dto;
	}
	
	public static <T extends AbstractDomainObject> T fromReferenceDto(ReferenceDto dto, AbstractAdoService<T> service) {
		if (dto != null) {
			return service.getByUuid(dto.getUuid());
		} else {
			return null;
		}
	}
}
