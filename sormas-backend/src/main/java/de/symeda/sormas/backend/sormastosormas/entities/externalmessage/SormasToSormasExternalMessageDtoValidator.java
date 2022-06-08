package de.symeda.sormas.backend.sormastosormas.entities.externalmessage;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildExternalMessageValidationGroupName;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.sormastosormas.externalmessage.SormasToSormasExternalMessageDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.PreviewNotImplementedDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.ValidationDirection;

@Stateless
@LocalBean
public class SormasToSormasExternalMessageDtoValidator
	extends SormasToSormasDtoValidator<ExternalMessageDto, SormasToSormasExternalMessageDto, PreviewNotImplementedDto> {

	public SormasToSormasExternalMessageDtoValidator() {
	}

	@Inject
	public SormasToSormasExternalMessageDtoValidator(InfrastructureValidator infraValidator) {
		super(infraValidator);
	}

	@Override
	public ValidationErrors validate(SormasToSormasExternalMessageDto sharedData, ValidationDirection direction) {
		ExternalMessageDto externalMessage = sharedData.getEntity();
		return new ValidationErrors(buildExternalMessageValidationGroupName(externalMessage));
	}

	@Override
	public ValidationErrors validatePreview(PreviewNotImplementedDto previewNotImplementedDto, ValidationDirection direction) {
		throw new RuntimeException("ExternalMessage preview not yet implemented");
	}
}
