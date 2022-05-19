package de.symeda.sormas.api.sormastosormas.labmessage;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;

public class SormasToSormasLabMessageDto extends SormasToSormasEntityDto<ExternalMessageDto> {

	public SormasToSormasLabMessageDto() {
	}

	public SormasToSormasLabMessageDto(ExternalMessageDto entity) {
		super(entity);
	}
}
