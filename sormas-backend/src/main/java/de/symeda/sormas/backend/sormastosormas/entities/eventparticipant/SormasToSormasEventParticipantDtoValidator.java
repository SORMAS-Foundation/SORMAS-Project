package de.symeda.sormas.backend.sormastosormas.entities.eventparticipant;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventParticipantPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.validation.Sormas2SormasDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class SormasToSormasEventParticipantDtoValidator
	extends
	SormasToSormasDtoValidator<EventParticipantDto, SormasToSormasEventParticipantDto, SormasToSormasEventParticipantPreview> {

	@EJB
	private Sormas2SormasDataValidator dataValidator;

	@Override
	public ValidationErrors validateIncoming(SormasToSormasEventParticipantDto sharedData) {
		return dataValidator.validateEventParticipant(sharedData.getEntity());
	}

	@Override
	public ValidationErrors validateIncomingPreview(SormasToSormasEventParticipantPreview preview) {
		return dataValidator.validatePersonPreview(preview.getPerson());
	}

	@Override
	public ValidationErrors validateOutgoing(SormasToSormasEventParticipantDto sharedData) {
		return null;
	}

	@Override
	public ValidationErrors validateOutgoingPreview(SormasToSormasEventParticipantPreview preview) {
		return null;
	}
}
