package de.symeda.sormas.backend.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.backend.common.AbstractAdoService;
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
	
	public static <T extends AbstractDomainObject> T fromReferenceDto(ReferenceDto dto, AbstractAdoService<T> service) {
		if (dto != null) {
			return service.getByUuid(dto.getUuid());
		} else {
			return null;
		}
	}
	
	public static final String getApproximateAge(Date birthDate, Date deathDate) {
		if (birthDate == null)
			return null;
		
		LocalDate toDate = deathDate==null?LocalDate.now():deathDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate birthdate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Period period = Period.between(birthdate, toDate);
		
		if(period.getYears()<1) {
			return period.getMonths() + " Months";
		}
		else {
			return period.getYears() + " Years";
		}
	}
	
	public static final String getApproximateAge(Date birthDate) {
		return getApproximateAge(birthDate, null);
	}
}
