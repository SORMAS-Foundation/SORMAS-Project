/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.sormastosormas.entities.eventparticipant;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.sormastosormas.entities.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasEventParticipantPreview;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.SormasToSormasPseudonymizer;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;

@Stateless
@LocalBean
public class EventParticipantShareDataBuilder
	extends
	ShareDataBuilder<EventParticipantDto, EventParticipant, SormasToSormasEventParticipantDto, SormasToSormasEventParticipantPreview, SormasToSormasEventParticipantDtoValidator> {

	@EJB
	private ShareDataBuilderHelper dataBuilderHelper;
	@EJB
	private EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal eventParticipantFacade;

	public EventParticipantShareDataBuilder() {
	}

	@Inject
	protected EventParticipantShareDataBuilder(SormasToSormasEventParticipantDtoValidator validator) {
		super(validator);
	}

	@Override
	public SormasToSormasEventParticipantDto doBuildShareData(
		EventParticipant eventParticipant,
		ShareRequestInfo requestInfo,
		boolean ownerShipHandedOver) {
		SormasToSormasPseudonymizer pseudonymizer = dataBuilderHelper.createPseudonymizer(requestInfo);

		EventParticipantDto eventParticipantDto = getDto(eventParticipant, pseudonymizer);
		dataBuilderHelper.pseudonymizePerson(eventParticipantDto.getPerson(), requestInfo);

		return new SormasToSormasEventParticipantDto(eventParticipantDto);
	}

	@Override
	protected EventParticipantDto getDto(EventParticipant eventParticipant, SormasToSormasPseudonymizer pseudonymizer) {

		EventParticipantDto eventParticipantDto = eventParticipantFacade.toPseudonymizedDto(eventParticipant, pseudonymizer.getPseudonymizer());
		// reporting user is not set to null here as it would not pass the validation
		// the receiver appears to set it to SORMAS2SORMAS Client anyway
		eventParticipantDto.setSormasToSormasOriginInfo(null);
		dataBuilderHelper.clearIgnoredProperties(eventParticipantDto.getPerson());

		return eventParticipantDto;
	}

	@Override
	public void doBusinessValidation(SormasToSormasEventParticipantDto sormasToSormasEventParticipantDto) throws ValidationRuntimeException {
		eventParticipantFacade.validate(sormasToSormasEventParticipantDto.getEntity());
	}

	@Override
	public SormasToSormasEventParticipantPreview doBuildShareDataPreview(EventParticipant eventParticipant, ShareRequestInfo requestInfo) {
		SormasToSormasPseudonymizer pseudonymizer = dataBuilderHelper.createPseudonymizer(requestInfo);

		return getEventParticipantPreview(eventParticipant, pseudonymizer);
	}

	public SormasToSormasEventParticipantPreview getEventParticipantPreview(
		EventParticipant eventParticipant,
		SormasToSormasPseudonymizer pseudonymizer) {
		SormasToSormasEventParticipantPreview preview = new SormasToSormasEventParticipantPreview();

		preview.setUuid(eventParticipant.getUuid());
		preview.setPerson(dataBuilderHelper.getPersonPreview(eventParticipant.getPerson()));
		preview.setEvent(EventFacadeEjb.toReferenceDto(eventParticipant.getEvent()));

		pseudonymizer.<SormasToSormasEventParticipantPreview> getPseudonymizer()
			.pseudonymizeDto(SormasToSormasEventParticipantPreview.class, preview, false, null);

		return preview;
	}
}
