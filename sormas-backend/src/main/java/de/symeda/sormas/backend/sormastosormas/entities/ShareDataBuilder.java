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

package de.symeda.sormas.backend.sormastosormas.entities;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.immunization.SormasToSormasImmunizationDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventParticipantPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventPreview;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.entities.caze.CaseShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.entities.contact.ContactShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.entities.event.EventShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.entities.eventparticipant.EventParticipantShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.entities.immunization.ImmunizationShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.entities.sample.SampleShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareRequestInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.sharerequest.ShareRequestPreviews;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class ShareDataBuilder {

	@EJB
	private CaseShareDataBuilder caseShareDataBuilder;
	@EJB
	private ContactShareDataBuilder contactShareDataBuilder;
	@EJB
	private SampleShareDataBuilder sampleShareDataBuilder;
	@EJB
	private EventShareDataBuilder eventShareDataBuilder;
	@EJB
	private EventParticipantShareDataBuilder eventParticipantShareDataBuilder;
	@EJB
	private ImmunizationShareDataBuilder immunizationShareDataBuilder;
	@EJB
	private ShareDataBuilderHelper shareDataBuilderHelper;

	public SormasToSormasDto buildShareDataForRequest(ShareRequestInfo requestInfo, User user) throws SormasToSormasException {
		SormasToSormasOriginInfoDto originInfo = shareDataBuilderHelper.createSormasToSormasOriginInfo(user, requestInfo);

		return buildShareData(requestInfo.getShares(), originInfo, requestInfo);
	}

	public SormasToSormasDto buildShareData(
		List<SormasToSormasShareInfo> shares,
		SormasToSormasOriginInfoDto originInfo,
		ShareRequestInfo requestInfo)
		throws SormasToSormasException {

		List<SormasToSormasCaseDto> cases = new ArrayList<>();
		List<SormasToSormasContactDto> contacts = new ArrayList<>();
		List<SormasToSormasSampleDto> samples = new ArrayList<>();
		List<SormasToSormasEventDto> events = new ArrayList<>();
		List<SormasToSormasEventParticipantDto> eventParticipants = new ArrayList<>();
		List<SormasToSormasImmunizationDto> immunizations = new ArrayList<>();

		List<ValidationErrors> validationErrors = new ArrayList<>();

		shares.forEach(s -> {
			if (s.getCaze() != null) {
				try {
					cases.add(caseShareDataBuilder.buildShareData(s.getCaze(), requestInfo));
				} catch (SormasToSormasValidationException e) {
					validationErrors.addAll(e.getErrors());
				}
			}

			if (s.getContact() != null) {
				try {
					contacts.add(contactShareDataBuilder.buildShareData(s.getContact(), requestInfo));
				} catch (SormasToSormasValidationException e) {
					validationErrors.addAll(e.getErrors());
				}

			}
			if (s.getSample() != null) {
				try {
					samples.add(sampleShareDataBuilder.buildShareData(s.getSample(), requestInfo));
				} catch (SormasToSormasValidationException e) {
					validationErrors.addAll(e.getErrors());
				}
			}

			if (s.getEvent() != null) {
				try {
					events.add(eventShareDataBuilder.buildShareData(s.getEvent(), requestInfo));
				} catch (SormasToSormasValidationException e) {
					validationErrors.addAll(e.getErrors());
				}
			}

			if (s.getEventParticipant() != null) {
				try {
					eventParticipants.add(eventParticipantShareDataBuilder.buildShareData(s.getEventParticipant(), requestInfo));
				} catch (SormasToSormasValidationException e) {
					validationErrors.addAll(e.getErrors());
				}
			}

			if (s.getImmunization() != null) {
				try {
					immunizations.add(immunizationShareDataBuilder.buildShareData(s.getImmunization(), requestInfo));
				} catch (SormasToSormasValidationException e) {
					validationErrors.addAll(e.getErrors());
				}
			}
		});

		if (!validationErrors.isEmpty()) {
			throw SormasToSormasException.fromStringProperty(validationErrors, Strings.errorSormasToSormasShare);
		}

		SormasToSormasDto dto = new SormasToSormasDto();
		dto.setOriginInfo(originInfo);
		dto.setCases(cases);
		dto.setContacts(contacts);
		dto.setSamples(samples);
		dto.setEvents(events);
		dto.setEventParticipants(eventParticipants);
		dto.setImmunizations(immunizations);

		return dto;
	}

	public ShareRequestPreviews buildShareDataPreview(ShareRequestInfo requestInfo) throws SormasToSormasException {
		List<SormasToSormasCasePreview> cases = new ArrayList<>();
		List<SormasToSormasContactPreview> contacts = new ArrayList<>();
		List<SormasToSormasEventPreview> events = new ArrayList<>();
		List<SormasToSormasEventParticipantPreview> eventParticipants = new ArrayList<>();

		List<ValidationErrors> validationErrors = new ArrayList<>();

		requestInfo.getShares().forEach(s -> {
			if (s.getCaze() != null) {
				try {
					cases.add(caseShareDataBuilder.buildShareDataPreview(s.getCaze(), requestInfo));
				} catch (SormasToSormasValidationException e) {
					validationErrors.addAll(e.getErrors());
				}
			}

			if (s.getContact() != null) {
				try {
					contacts.add(contactShareDataBuilder.buildShareDataPreview(s.getContact(), requestInfo));
				} catch (SormasToSormasValidationException e) {
					validationErrors.addAll(e.getErrors());
				}
			}

			if (s.getEvent() != null) {
				try {
					events.add(eventShareDataBuilder.buildShareDataPreview(s.getEvent(), requestInfo));
				} catch (SormasToSormasValidationException e) {
					validationErrors.addAll(e.getErrors());
				}
			}

			if (s.getEventParticipant() != null) {
				try {
					eventParticipants.add(eventParticipantShareDataBuilder.buildShareDataPreview(s.getEventParticipant(), requestInfo));
				} catch (SormasToSormasValidationException e) {
					validationErrors.addAll(e.getErrors());
				}
			}
		});

		if (!validationErrors.isEmpty()) {
			throw SormasToSormasException.fromStringProperty(validationErrors, Strings.errorSormasToSormasShare);
		}

		return new ShareRequestPreviews(cases, contacts, events, eventParticipants);
	}
}
