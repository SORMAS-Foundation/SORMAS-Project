package de.symeda.sormas.backend.sormastosormas.entities.labmessage;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildLabMessageValidationGroupName;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.sormastosormas.labmessage.SormasToSormasLabMessageDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.PreviewNotImplementedDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.ValidationDirection;

@Stateless
@LocalBean
public class SormasToSormasLabMessageDtoValidator
	extends SormasToSormasDtoValidator<ExternalMessageDto, SormasToSormasLabMessageDto, PreviewNotImplementedDto> {

	public SormasToSormasLabMessageDtoValidator() {
	}

	@Inject
	public SormasToSormasLabMessageDtoValidator(InfrastructureValidator infraValidator) {
		super(infraValidator);
	}

	@Override
	public ValidationErrors validate(SormasToSormasLabMessageDto sharedData, ValidationDirection direction) {
		ExternalMessageDto labMessage = sharedData.getEntity();
		return new ValidationErrors(buildLabMessageValidationGroupName(labMessage));
	}

	@Override
	public ValidationErrors validatePreview(PreviewNotImplementedDto previewNotImplementedDto, ValidationDirection direction) {
		throw new RuntimeException("ExternalMessage preview not yet implemented");
	}
}
