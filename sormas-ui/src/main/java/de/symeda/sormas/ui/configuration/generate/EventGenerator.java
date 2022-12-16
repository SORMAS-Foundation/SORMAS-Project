package de.symeda.sormas.ui.configuration.generate;

import de.symeda.sormas.ui.configuration.generate.config.EventGenerationConfig;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.infrastructure.facility.FacilityCriteria;
import de.symeda.sormas.api.infrastructure.facility.FacilityIndexDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;

public class EventGenerator extends EntityGenerator<EventGenerationConfig> {

	private final String[] eventTitles = new String[] {
		"Wedding",
		"Party",
		"Funeral",
		"Concert",
		"Fair",
		"Rallye",
		"Demonstration",
		"Football Match",
		"Tournament",
		"Festival",
		"Carnival" };

	public void generate(Binder<EventGenerationConfig> binder) {
		EventGenerationConfig eventGenerationConfig = binder.getBean();
		boolean valid = true;
		StringBuilder errorMessage = new StringBuilder();

		if (eventGenerationConfig.getEntityCountAsNumber() <= 0) {
			errorMessage.append("You must set a valid value for field 'Number of generated events' in 'Generate Events'").append("<br>");
			valid = false;
		}
		if (eventGenerationConfig.getStartDate() == null) {
			errorMessage.append("You must set a valid value for field 'Earliest Event start date' in 'Generate Events'").append("<br>");
			valid = false;
		}

		if (eventGenerationConfig.getEndDate() == null) {
			errorMessage.append("You must set a valid value for field 'Latest Event start date' in 'Generate Events'").append("<br>");
			valid = false;
		}

		if (eventGenerationConfig.getDistrict() == null) {
			errorMessage.append("You must set a valid value for field 'District of the events' in 'Generate Events'").append("<br>");
			valid = false;
		}

		if (valid) {
			generateEvents(eventGenerationConfig);
		} else {
			throw new ValidationRuntimeException(errorMessage.toString());
		}
	}

	private void generateEvents(EventGenerationConfig eventGenerationConfig) {
		initializeRandomGenerator();

		int minParticipantsPerEvent = eventGenerationConfig.convetToNumber(eventGenerationConfig.getMinParticipantsPerEvent());
		int maxParticipantsPerEvent = eventGenerationConfig.convetToNumber(eventGenerationConfig.getMaxParticipantsPerEvent());
		int percentageOfCases = eventGenerationConfig.convetToNumber(eventGenerationConfig.getPercentageOfCases());
		int minContactsPerParticipant = eventGenerationConfig.convetToNumber(eventGenerationConfig.getMinContactsPerParticipant());
		int maxContactsPerParticipant = eventGenerationConfig.convetToNumber(eventGenerationConfig.getMaxContactsPerParticipant());

		int generatedParticipants = 0;
		int generatedCases = 0;
		int generatedContacts = 0;

		Disease disease = eventGenerationConfig.getDisease();

		float baseOffset = random().nextFloat();
		int daysBetween = (int) ChronoUnit.DAYS.between(eventGenerationConfig.getStartDate(), eventGenerationConfig.getEndDate());

		// this should be adjusted to be much more complex
		FacilityCriteria facilityCriteria = new FacilityCriteria();
		facilityCriteria.region(eventGenerationConfig.getRegion());
		facilityCriteria.district(eventGenerationConfig.getDistrict());
		List<FacilityIndexDto> healthFacilities =
			FacadeProvider.getFacilityFacade().getIndexList(facilityCriteria, 0, (int) (maxContactsPerParticipant * percentageOfCases / 100), null);

		// Filter list, so that only health facilities meant for accomodation are selected
		healthFacilities.removeIf(el -> (!el.getType().isAccommodation()));

		long dt = System.nanoTime();

		for (int i = 0; i < eventGenerationConfig.getEntityCountAsNumber(); i++) {
			LocalDateTime referenceDateTime;

			EventDto event = EventDto.build();

			// disease
			if (disease != null) {
				event.setDisease(disease); // reset
				if (event.getDisease() == Disease.OTHER) {
					event.setDiseaseDetails("RD " + (random().nextInt(20) + 1));
				}
				referenceDateTime = getReferenceDateTime(
					i,
					eventGenerationConfig.getEntityCountAsNumber(),
					baseOffset,
					disease,
					eventGenerationConfig.getStartDate(),
					daysBetween);
				fieldVisibilityCheckers =
					FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale());
			} else {
				referenceDateTime = getReferenceDateTime(
					i,
					eventGenerationConfig.getEntityCountAsNumber(),
					baseOffset,
					Disease.OTHER,
					eventGenerationConfig.getStartDate(),
					daysBetween);
				fieldVisibilityCheckers =
					FieldVisibilityCheckers.withDisease(Disease.OTHER).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale());
			}

			// title
			event.setEventTitle(random(eventTitles));

			// description
			event.setEventDesc("Event generated using DevMode on " + LocalDate.now());

			// report
			UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
			event.setReportingUser(userReference);
			event.setReportDateTime(Date.from(referenceDateTime.atZone(ZoneId.systemDefault()).toInstant()));

			// region & district
			event.getEventLocation().setRegion(eventGenerationConfig.getRegion());
			event.getEventLocation().setDistrict(eventGenerationConfig.getDistrict());

			// status
			event.setEventStatus(EventStatus.EVENT);

			FacadeProvider.getEventFacade().save(event);

			// EventParticipants
			int numParticipants = randomInt(minParticipantsPerEvent, maxParticipantsPerEvent);
			for (int j = 0; j < numParticipants; j++) {
				EventParticipantDto eventParticipant = EventParticipantDto.build(event.toReference(), UserProvider.getCurrent().getUserReference());
				// person
				// instead of creating new persons everytime, it would be nice if some persons came of the original database
				PersonDto person = PersonDto.build();
				fillEntity(person, referenceDateTime);
				person.setSymptomJournalStatus(null);
				setPersonName(person);
				FacadeProvider.getPersonFacade().save(person);
				eventParticipant.setPerson(person);
				eventParticipant.setInvolvementDescription("Participant");

				if (disease != null) {
					// generate cases for some participants
					if (randomPercent(percentageOfCases) && !healthFacilities.isEmpty()) {
						CaseDataDto caze = CaseDataDto.buildFromEventParticipant(eventParticipant, person, event.getDisease());
						fillEntity(caze, referenceDateTime);
						caze.setDisease(event.getDisease());
						caze.setReportingUser(UserProvider.getCurrent().getUserReference());
						caze.setReportDate(Date.from(referenceDateTime.atZone(ZoneId.systemDefault()).toInstant()));
						caze.setCaseOrigin(CaseOrigin.IN_COUNTRY);
						caze.setResponsibleRegion(eventGenerationConfig.getRegion());
						caze.setResponsibleDistrict(eventGenerationConfig.getDistrict());
						FacilityIndexDto facility = random(healthFacilities);
						caze.setHealthFacility(facility.toReference());
						caze.setFacilityType(facility.getType());
						caze.setAdditionalDetails("Case generated using DevMode on " + LocalDate.now());
						FacadeProvider.getCaseFacade().save(caze);
						eventParticipant.setResultingCase(caze.toReference());
						generatedCases++;
					}

					// generate contacts for some participants
					List<CaseReferenceDto> cases = FacadeProvider.getCaseFacade()
						.getRandomCaseReferences(
							new CaseCriteria().region(eventGenerationConfig.getRegion())
								.district(eventGenerationConfig.getDistrict())
								.disease(event.getDisease()),
							numParticipants * 2,
							random());
					int numContacts = randomInt(minContactsPerParticipant, maxContactsPerParticipant);
					for (int k = 0; (k < numContacts && (cases != null)); k++) {
						ContactDto contact = ContactDto.build(eventParticipant);
						contact.setDisease(event.getDisease());
						contact.setCaze(random(cases));
						contact.setReportingUser(UserProvider.getCurrent().getUserReference());
						contact.setReportDateTime(Date.from(referenceDateTime.atZone(ZoneId.systemDefault()).toInstant()));
						contact.setDescription("Contact generated using DevMode on " + LocalDate.now());
						FacadeProvider.getContactFacade().save(contact);
						generatedContacts++;
					}
				}

				FacadeProvider.getEventParticipantFacade().save(eventParticipant);
				generatedParticipants++;
			}
		}

		dt = System.nanoTime() - dt;
		long perCase = dt / eventGenerationConfig.getEntityCountAsNumber();
		String msg = String.format(
			"Generating %d events with a total of %d participants (%d contacts, %d cases) took %.2f  s (%.1f ms per event)",
			eventGenerationConfig.getEntityCountAsNumber(),
			generatedParticipants,
			generatedContacts,
			generatedCases,
			(double) dt / 1_000_000_000,
			(double) perCase / 1_000_000);
		logger.info(msg);
		Notification.show("", msg, Notification.Type.TRAY_NOTIFICATION);
	}
}
