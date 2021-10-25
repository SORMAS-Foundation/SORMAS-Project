package de.symeda.sormas.api.sormastosormas.validation;


import de.symeda.sormas.api.utils.SormasToSormasEntityDtoInterface;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

public interface Sormas2SormasEntityValidator< DTO extends SormasToSormasEntityDtoInterface, PREVIEW extends  PseudonymizableDto> {

	ValidationErrors validateInboundEntity(DTO dto);

	ValidationErrors validateInboundPreviewEntity(PREVIEW preview);
}
