package de.symeda.sormas.backend.sormastosormas.entities.caze;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.caze.Case;

import de.symeda.sormas.backend.sormastosormas.data.validation.Sormas2SormasDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class SormasToSormasCaseDtoValidator extends SormasToSormasDtoValidator<CaseDataDto, SormasToSormasCaseDto, SormasToSormasCasePreview, Case> {

	@EJB
	private Sormas2SormasDataValidator dataValidator;

	@Override
	public ValidationErrors validateIncoming(SormasToSormasCaseDto sharedData) {
		return dataValidator.validateCaseData(sharedData.getEntity(), sharedData.getPerson());
	}

	@Override
	public ValidationErrors validateIncomingPreview(SormasToSormasCasePreview preview) {
		ValidationErrors validationErrors = dataValidator.validateCasePreview(preview);
		validationErrors.addAll(dataValidator.validatePersonPreview(preview.getPerson()));
		return validationErrors;
	}

	@Override
	public ValidationErrors validateOutgoing(SormasToSormasCaseDto sharedData) {
		return null;
	}

	@Override
	public ValidationErrors validateOutgoingPreview(SormasToSormasCasePreview preview) {
		return null;
	}

}
