package de.symeda.sormas.backend.sormastosormas.entities.sample;

import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.PreviewNotImplementedDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;

import de.symeda.sormas.backend.sormastosormas.data.validation.Sormas2SormasDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class SormasToSormasSampleDtoValidator
	extends SormasToSormasDtoValidator<SampleDto, SormasToSormasSampleDto, PreviewNotImplementedDto> {

	@EJB
	private Sormas2SormasDataValidator dataValidator;

	@Override
	public ValidationErrors validateIncoming(SormasToSormasSampleDto sharedData) {
		ValidationErrors validationErrors = dataValidator.validateSample(sharedData.getEntity());
		sharedData.getPathogenTests().forEach(pathogenTest -> dataValidator.validatePathogenTest(validationErrors, pathogenTest));
		return validationErrors;
	}

	@Override
	public ValidationErrors validateIncomingPreview(PreviewNotImplementedDto previewNotImplementedDto) {
		throw new RuntimeException("Samples preview not yet implemented");
	}

	@Override
	public ValidationErrors validateOutgoing(SormasToSormasSampleDto sharedData) {
		return null;
	}

	@Override
	public ValidationErrors validateOutgoingPreview(PreviewNotImplementedDto previewNotImplementedDto) {
		return null;
	}
}
