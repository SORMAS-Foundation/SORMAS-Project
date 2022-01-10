package de.symeda.sormas.backend.sormastosormas.entities.labmessage;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.sormastosormas.labmessage.SormasToSormasLabMessageDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.PreviewNotImplementedDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.ValidationDirection;

@Stateless
@LocalBean
public class SormasToSormasLabMessageDtoValidator
	extends SormasToSormasDtoValidator<LabMessageDto, SormasToSormasLabMessageDto, PreviewNotImplementedDto> {

	public SormasToSormasLabMessageDtoValidator() {
	}

	@Inject
	public SormasToSormasLabMessageDtoValidator(InfrastructureValidator infraValidator) {
		super(infraValidator);
	}

	@Override
	public ValidationErrors validate(SormasToSormasLabMessageDto sharedData, ValidationDirection direction) {
		return new ValidationErrors();
	}

	@Override
	public ValidationErrors validatePreview(PreviewNotImplementedDto previewNotImplementedDto, ValidationDirection direction) {
		throw new RuntimeException("LabMessage preview not yet implemented");
	}
}
