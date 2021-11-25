package de.symeda.sormas.api.sormastosormas.immunization;

import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;

public class SormasToSormasImmunizationDto extends SormasToSormasEntityDto<ImmunizationDto> {

	public SormasToSormasImmunizationDto() {
	}

	public SormasToSormasImmunizationDto(ImmunizationDto entity) {
		super(entity);
	}
}
