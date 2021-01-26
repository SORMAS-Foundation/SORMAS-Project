package de.symeda.sormas.backend.sample;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.sample.SampleJurisdictionDto;
import de.symeda.sormas.api.utils.jurisdiction.SampleJurisdictionHelper;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantJurisdictionChecker;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless(name = "SampleJurisdictionChecker")
@LocalBean
public class SampleJurisdictionChecker {

	@EJB
	private UserService userService;
	@EJB
	private EventParticipantJurisdictionChecker eventParticipantJurisdictionChecker;
	@EJB
	private EventParticipantService eventParticipantService;

	public boolean isInJurisdictionOrOwned(Sample sample) {

		return isInJurisdictionOrOwned(JurisdictionHelper.createSampleJurisdictionDto(sample));
	}

	public boolean isInJurisdictionOrOwned(SampleJurisdictionDto sampleJurisdiction) {

		User user = userService.getCurrentUser();

		if (sampleJurisdiction.getEventParticipantJurisdiction() != null) {
			EventParticipant sampleEventParticipant =
				eventParticipantService.getByUuid(sampleJurisdiction.getEventParticipantJurisdiction().getEventParticipantUuid());
			return eventParticipantJurisdictionChecker.isInJurisdiction(sampleEventParticipant);
		}

		return SampleJurisdictionHelper
			.isInJurisdictionOrOwned(user.getJurisdictionLevel(), JurisdictionHelper.createUserJurisdiction(user), sampleJurisdiction);
	}

	public boolean isPseudonymized(Sample sample) {

		if (sample.getAssociatedEventParticipant()!=null){
			eventParticipantJurisdictionChecker.isPseudonymized(sample.getAssociatedEventParticipant());
		}

		return false;


	}
}
