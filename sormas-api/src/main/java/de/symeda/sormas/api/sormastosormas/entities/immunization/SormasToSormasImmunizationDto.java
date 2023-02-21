package de.symeda.sormas.api.sormastosormas.entities.immunization;

import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.sormastosormas.entities.SormasToSormasEntityDto;

public class SormasToSormasImmunizationDto extends SormasToSormasEntityDto<ImmunizationDto> {

	public SormasToSormasImmunizationDto() {
	}

	public SormasToSormasImmunizationDto(ImmunizationDto entity) {
		super(entity);
	}
}
