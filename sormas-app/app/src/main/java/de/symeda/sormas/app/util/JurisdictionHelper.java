package de.symeda.sormas.app.util;

import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseJurisdictionDto;
import de.symeda.sormas.app.backend.caze.ResponsibleJurisdictionDto;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactJurisdictionDto;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventJurisdictionDto;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.event.EventParticipantJurisdictionDto;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleJurisdictionDto;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskJurisdictionDto;
import de.symeda.sormas.app.backend.user.User;

public class JurisdictionHelper {

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

		jurisdiction.setJurisdictionLevel(UserRole.getJurisdictionLevel(user.getUserRoles()));

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

		dto.setResponsibleJurisdiction(createResponsibleJurisdiction(caze));

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
		List<Sample> samples = DatabaseHelper.getSampleDao().queryByCase(caze);
		if (!samples.isEmpty()) {
			dto.setSampleLabUuids(samples.stream().map(sample -> sample.getLab().getUuid()).collect(Collectors.toList()));
		}

		return dto;
	}

	public static ContactJurisdictionDto createContactJurisdictionDto(Contact contact) {
		if (contact == null) {
			return null;
		}
		ContactJurisdictionDto dto = new ContactJurisdictionDto();

		if (contact.getReportingUser() != null) {
			dto.setReportingUserUuid(contact.getReportingUser().getUuid());
		}
		if (contact.getRegion() != null) {
			dto.setRegionUuid(contact.getRegion().getUuid());
		}
		if (contact.getDistrict() != null) {
			dto.setDistrictUuid(contact.getDistrict().getUuid());
		}

		if (contact.getCaseUuid() != null) {
			Case caseOfContact = DatabaseHelper.getCaseDao().queryUuidBasic(contact.getCaseUuid());
			dto.setCaseJurisdiction(JurisdictionHelper.createCaseJurisdictionDto(caseOfContact));
		}

		return dto;
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

	public static EventParticipantJurisdictionDto createEventParticipantJurisdictionDto(EventParticipant eventParticipant) {
		EventParticipantJurisdictionDto jurisdiction = new EventParticipantJurisdictionDto();

		if (eventParticipant.getReportingUser() != null) {
			jurisdiction.setReportingUserUuid(eventParticipant.getReportingUser().getUuid());
		}

		// todo https://github.com/hzi-braunschweig/SORMAS-Project/issues/5903
		// if (eventParticipant.getRegion() != null) {
		// 	jurisdiction.setRegionUuid(eventParticipant.getRegion().getUuid());
		// }
		//
		// if (eventParticipant.getDistrict() != null) {
		// 	jurisdiction.setDistrictUuid(eventParticipant.getDistrict().getUuid());
		// }

		Event event = eventParticipant.getEvent();
		if (event != null) {
			jurisdiction.setEventJurisdictionDto(JurisdictionHelper.createEventJurisdictionDto(event));
		}

		return jurisdiction;
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

		Facility labFacility = sample.getLab();
		if (labFacility != null) {
			jurisdiction.setLabUuid(sample.getLab().getUuid());
		}

		return jurisdiction;
	}

	private static ResponsibleJurisdictionDto createResponsibleJurisdiction(Case caze) {
		Region responsibleRegion = caze.getResponsibleRegion();
		District responsibleDistrict = caze.getResponsibleDistrict();
		Community responsibleCommunity = caze.getResponsibleCommunity();

		if (responsibleRegion == null && responsibleDistrict == null && responsibleCommunity == null) {
			return null;
		}

		ResponsibleJurisdictionDto jurisdiction = new ResponsibleJurisdictionDto();
		if (responsibleRegion != null) {
			jurisdiction.setRegionUuid(responsibleRegion.getUuid());
		}
		if (responsibleDistrict != null) {
			jurisdiction.setDistrictUuid(responsibleDistrict.getUuid());
		}
		if (responsibleCommunity != null) {
			jurisdiction.setCommunityUuid(responsibleCommunity.getUuid());
		}

		return jurisdiction;
	}
}
