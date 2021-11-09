package de.symeda.sormas.backend.sormastosormas.data.validation;

import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasShareable;

public abstract class SormasToSormasDtoValidator<DTO extends SormasToSormasShareableDto, SHARED extends SormasToSormasEntityDto<DTO>, PREVIEW extends PseudonymizableDto> {

	public abstract ValidationErrors validateIncoming(SHARED sharedData);

	public abstract ValidationErrors validateIncomingPreview(PREVIEW preview);

	public abstract ValidationErrors validateOutgoing(SHARED sharedData);

	public abstract ValidationErrors validateOutgoingPreview(PREVIEW preview);
}
