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
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventParticipantPreview;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareRequestInfo;
import de.symeda.sormas.backend.util.Pseudonymizer;

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
	public SormasToSormasEventParticipantDto doBuildShareData(EventParticipant data, ShareRequestInfo requestInfo) {
		Pseudonymizer pseudonymizer =
			dataBuilderHelper.createPseudonymizer(requestInfo.isPseudonymizedPersonalData(), requestInfo.isPseudonymizedSensitiveData());

		EventParticipantDto eventParticipantDto = eventParticipantFacade.convertToDto(data, pseudonymizer);

		eventParticipantDto.setReportingUser(null);
		eventParticipantDto.setSormasToSormasOriginInfo(null);
		dataBuilderHelper.clearIgnoredProperties(eventParticipantDto.getPerson());

		dataBuilderHelper.pseudonymiePerson(
			eventParticipantDto.getPerson(),
			requestInfo.isPseudonymizedPersonalData(),
			requestInfo.isPseudonymizedSensitiveData());

		return new SormasToSormasEventParticipantDto(eventParticipantDto);
	}

	@Override
	public SormasToSormasEventParticipantPreview doBuildShareDataPreview(EventParticipant eventParticipant, ShareRequestInfo requestInfo) {
		Pseudonymizer pseudonymizer =
			dataBuilderHelper.createPseudonymizer(requestInfo.isPseudonymizedPersonalData(), requestInfo.isPseudonymizedSensitiveData());

		SormasToSormasEventParticipantPreview preview = new SormasToSormasEventParticipantPreview();

		preview.setUuid(eventParticipant.getUuid());
		preview.setPerson(dataBuilderHelper.getPersonPreview(eventParticipant.getPerson()));
		preview.setEvent(EventFacadeEjb.toReferenceDto(eventParticipant.getEvent()));

		pseudonymizer.pseudonymizeDto(SormasToSormasEventParticipantPreview.class, preview, false, null);

		return preview;

	}
}
