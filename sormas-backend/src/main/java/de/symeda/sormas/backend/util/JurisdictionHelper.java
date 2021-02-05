/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.util;

import javax.ejb.EJB;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.event.EventJurisdictionDto;
import de.symeda.sormas.api.event.EventParticipantJurisdictionDto;
import de.symeda.sormas.api.sample.SampleJurisdictionDto;
import de.symeda.sormas.api.task.TaskJurisdictionDto;
import de.symeda.sormas.api.utils.jurisdiction.UserJurisdiction;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantJurisdictionChecker;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.user.User;

public class JurisdictionHelper {

	@EJB
	private EventParticipantJurisdictionChecker eventParticipantJurisdictionChecker;

	public static UserJurisdiction createUserJurisdiction(User user) {

		UserJurisdiction jurisdiction = new UserJurisdiction();
		jurisdiction.setUuid(user.getUuid());

		if (user.getRegion() != null) {
			jurisdiction.setRegionUuid(user.getRegion().getUuid());
		}
		if (user.getDistrict() != null) {
			jurisdiction.setDistrictUuid(user.getDistrict().getUuid());
		}
		if (user.getCommunity() != null) {
			jurisdiction.setCommunityUuid(user.getCommunity().getUuid());
		}
		if (user.getHealthFacility() != null) {
			jurisdiction.setHealthFacilityUuid(user.getHealthFacility().getUuid());
		}
		if (user.getPointOfEntry() != null) {
			jurisdiction.setPointOfEntryUuid(user.getPointOfEntry().getUuid());
		}

		if (user.getLaboratory() != null) {
			jurisdiction.setLabUuid(user.getLaboratory().getUuid());
		}

		return jurisdiction;
	}

	public static CaseJurisdictionDto createCaseJurisdictionDto(Case caze) {

		if (caze == null) {
			return null;
		}

		CaseJurisdictionDto dto = new CaseJurisdictionDto();

		if (caze.getReportingUser() != null) {
			dto.setReportingUserUuid(caze.getReportingUser().getUuid());
		}
		if (caze.getRegion() != null) {
			dto.setRegionUuid(caze.getRegion().getUuid());
		}
		if (caze.getDistrict() != null) {
			dto.setDistrictUuid(caze.getDistrict().getUuid());
		}
		if (caze.getCommunity() != null) {
			dto.setCommunityUuid(caze.getCommunity().getUuid());
		}
		if (caze.getHealthFacility() != null) {
			dto.setHealthFacilityUuid(caze.getHealthFacility().getUuid());
		}
		if (caze.getPointOfEntry() != null) {
			dto.setPointOfEntryUuid(caze.getPointOfEntry().getUuid());
		}

		return dto;
	}

	public static ContactJurisdictionDto createContactJurisdictionDto(Contact contact) {

		if (contact == null) {
			return null;
		}

		ContactJurisdictionDto jurisdiction = new ContactJurisdictionDto();

		if (contact.getReportingUser() != null) {
			jurisdiction.setReportingUserUuid(contact.getReportingUser().getUuid());
		}

		if (contact.getRegion() != null) {
			jurisdiction.setRegionUuid(contact.getRegion().getUuid());
		}

		if (contact.getDistrict() != null) {
			jurisdiction.setDistrictUuid(contact.getDistrict().getUuid());
		}

		Case caze = contact.getCaze();
		if (caze != null) {
			jurisdiction.setCaseJurisdiction(createCaseJurisdictionDto(caze));
		}

		return jurisdiction;
	}

	public static EventJurisdictionDto createEventJurisdictionDto(Event event) {
		if (event == null) {
			return null;
		}

		Location eventLocation = event.getEventLocation();
		if (eventLocation == null) {
			return null;
		}

		EventJurisdictionDto eventJurisdiction = new EventJurisdictionDto();

		if (event.getReportingUser() != null) {
			eventJurisdiction.setReportingUserUuid(event.getReportingUser().getUuid());
		}

		if (event.getResponsibleUser() != null) {
			eventJurisdiction.setResponsibleUserUuid(event.getResponsibleUser().getUuid());
		}

		if (eventLocation.getRegion() != null) {
			eventJurisdiction.setRegionUuid(eventLocation.getRegion().getUuid());
		}

		if (eventLocation.getDistrict() != null) {
			eventJurisdiction.setDistrictUuid(eventLocation.getDistrict().getUuid());
		}

		if (eventLocation.getCommunity() != null) {
			eventJurisdiction.setCommunityUuid(eventLocation.getCommunity().getUuid());

		}

		return eventJurisdiction;
	}

	public static TaskJurisdictionDto createTaskJurisdictionDto(Task task) {

		if (task == null) {
			return null;
		}

		TaskJurisdictionDto jurisdiction = new TaskJurisdictionDto();

		if (task.getCreatorUser() != null) {
			jurisdiction.setCreatorUserUuid(task.getCreatorUser().getUuid());
		}

		if (task.getAssigneeUser() != null) {
			jurisdiction.setAssigneeUserUuid(task.getAssigneeUser().getUuid());
		}

		Case caze = task.getCaze();
		if (caze != null) {
			jurisdiction.setCaseJurisdiction(createCaseJurisdictionDto(caze));
		}

		Contact contact = task.getContact();
		if (contact != null) {
			jurisdiction.setContactJurisdiction(createContactJurisdictionDto(contact));
		}

		Event event = task.getEvent();
		if (event != null) {
			jurisdiction.setEventJurisdiction(createEventJurisdictionDto(event));
		}

		return jurisdiction;
	}

	public static SampleJurisdictionDto createSampleJurisdictionDto(Sample sample) {

		if (sample == null) {
			return null;
		}

		SampleJurisdictionDto jurisdiction = new SampleJurisdictionDto();

		if (sample.getReportingUser() != null) {
			jurisdiction.setReportingUserUuid(sample.getReportingUser().getUuid());
		}

		Case caze = sample.getAssociatedCase();
		if (caze != null) {
			jurisdiction.setCaseJurisdiction(createCaseJurisdictionDto(caze));
		}

		Contact contact = sample.getAssociatedContact();
		if (contact != null) {
			jurisdiction.setContactJurisdiction(createContactJurisdictionDto(contact));
		}

		EventParticipant eventParticipant = sample.getAssociatedEventParticipant();
		if (eventParticipant != null) {
//			jurisdiction.setEventJurisdiction(createEventJurisdictionDto(eventParticipant.getEvent()));
			jurisdiction.setEventParticipantJurisdiction(createEventParticipantJurisdictionDto(eventParticipant));
		}

		Facility labFacility = sample.getLab();
		if (labFacility != null) {
			jurisdiction.setLabUuid(sample.getLab().getUuid());
		}

		return jurisdiction;
	}

	public static EventParticipantJurisdictionDto createEventParticipantJurisdictionDto(EventParticipant eventParticipant) {

		if (eventParticipant == null) {
			return null;
		}

		EventParticipantJurisdictionDto eventParticipantJurisdiction = new EventParticipantJurisdictionDto();

		eventParticipantJurisdiction.setEventParticipantUuid(eventParticipant.getUuid());

		if (eventParticipant.getReportingUser() != null) {
			eventParticipantJurisdiction.setReportingUserUuid(eventParticipant.getReportingUser().getUuid());
		}

		if (eventParticipant.getRegion() != null) {
			eventParticipantJurisdiction.setRegionUuid(eventParticipant.getRegion().getUuid());
		}

		if (eventParticipant.getDistrict() != null) {
			eventParticipantJurisdiction.setDistrictUuid(eventParticipant.getDistrict().getUuid());
		}

		if (eventParticipant.getEvent() != null) {
			eventParticipantJurisdiction.setEventUuid(eventParticipant.getEvent().getUuid());
		}

		return eventParticipantJurisdiction;
	}
}
