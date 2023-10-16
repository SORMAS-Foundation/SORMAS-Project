/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.externalmessage.labmessage;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.AbstractLabMessageProcessingFlow;
import de.symeda.sormas.api.externalmessage.processing.labmessage.PickOrCreateEventResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.PickOrCreateSampleResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.RelatedSamplesReportsAndPathogenTests;
import de.symeda.sormas.api.externalmessage.processing.labmessage.SampleAndPathogenTests;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.externalmessage.ExternalMessageFacadeEjb.ExternalMessageFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class AutomaticLabMessageProcessingFlow extends AbstractLabMessageProcessingFlow {

	private PersonFacadeEjbLocal personFacade;
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;
	private CaseFacadeEjbLocal caseFacade;
	private CaseService caseService;

	@Inject
	public AutomaticLabMessageProcessingFlow(
		UserFacadeEjbLocal userFacade,
		UserService userService,
		CountryFacadeEjbLocal countryFacade,
		ExternalMessageFacadeEjbLocal externalMessageFacade,
		FeatureConfigurationFacadeEjbLocal featureConfigurationFacade,
		CaseFacadeEjbLocal caseFacade,
		ContactFacadeEjbLocal contactFacade,
		EventFacadeEjbLocal eventFacade,
		EventParticipantFacadeEjbLocal eventParticipantFacade,
		SampleFacadeEjbLocal sampleFacade,
		PathogenTestFacadeEjbLocal pathogenTestFacade,
		FacilityFacadeEjbLocal facilityFacade,
		PersonFacadeEjbLocal personFacade,
		DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade,
		CaseService caseService) {
		super(
			userFacade.getCurrentUser(),
			new ExternalMessageProcessingFacade(
				externalMessageFacade,
				featureConfigurationFacade,
				caseFacade,
				contactFacade,
				eventFacade,
				eventParticipantFacade,
				sampleFacade,
				pathogenTestFacade,
				facilityFacade) {

				@Override
				public boolean hasAllUserRights(UserRight... userRights) {
					return Arrays.stream(userRights).allMatch(userService::hasRight);
				}
			},
			countryFacade.getServerCountry());
		this.personFacade = personFacade;
		this.diseaseConfigurationFacade = diseaseConfigurationFacade;
		this.caseFacade = caseFacade;
		this.caseService = caseService;
	}

	private static <T> boolean unsetOrMatches(T personValue, T messageValue) {
		if (personValue == null || messageValue == null) {
			return true;
		}

		return personValue.equals(messageValue);
	}

	@Override
	protected CompletionStage<Boolean> handleMissingDisease() {
		return CompletableFuture.completedFuture(Boolean.FALSE);
	}

	@Override
	protected CompletionStage<Boolean> handleRelatedForwardedMessages() {
		return CompletableFuture.completedFuture(Boolean.FALSE);
	}

	@Override
	protected void handlePickOrCreatePerson(PersonDto person, HandlerCallback<PersonDto> callback) {
		String nationalHealthId = person.getNationalHealthId();
		if (nationalHealthId != null) {
			List<PersonDto> matchingPersons = personFacade.getByNationalHealthId(nationalHealthId);
			if (matchingPersons.isEmpty()) {
				callback.done(personFacade.save(person));
			} else if (matchingPersons.size() == 1 && personDetailsMatch(person, matchingPersons.get(0))) {
				callback.done(matchingPersons.get(0));
			} else {
				callback.cancel();
			}
		} else {
			PersonSimilarityCriteria similarityCriteria = PersonSimilarityCriteria.forPerson(person, true);
			if (personFacade.checkMatchingNameInDatabase(user.toReference(), similarityCriteria)) {
				callback.cancel();
			} else {
				callback.done(personFacade.save(person));
			}
		}
	}

	@Override
	protected void handlePickOrCreateEntry(
		List<CaseSelectionDto> similarCases,
		List<SimilarContactDto> similarContacts,
		List<SimilarEventParticipantDto> similarEventParticipants,
		ExternalMessageDto externalMessageDto,
		HandlerCallback<PickOrCreateEntryResult> callback) {

		PickOrCreateEntryResult result = new PickOrCreateEntryResult();
		if (similarCases.isEmpty() && similarContacts.isEmpty() && similarEventParticipants.isEmpty()) {
			result.setNewCase(true);
			callback.done(result);
		} else if (!similarCases.isEmpty() && similarContacts.isEmpty() && similarEventParticipants.isEmpty()) {
			CaseSelectionDto firstSimilarCase = similarCases.get(0);

			Integer automaticSampleAssignmentThreshold =
				diseaseConfigurationFacade.getAutomaticSampleAssignmentThreshold(firstSimilarCase.getDisease());
			if (automaticSampleAssignmentThreshold == null) {
				callback.cancel();
				return;
			}

			Map<String, Date> referenceDates = caseService.getReferenceDatesForautomaticSampleAssignment(firstSimilarCase.getUuid());

			result.setCaze(firstSimilarCase);
			callback.done(result);
		} else {
			callback.cancel();
		}
	}

	@Override
	protected void handleCreateCase(CaseDataDto caze, PersonDto person, ExternalMessageDto labMessage, HandlerCallback<CaseDataDto> callback) {
		callback.done(caseFacade.save(caze));
	}

	@Override
	public CompletionStage<Boolean> handleMultipleSampleConfirmation() {
		return CompletableFuture.completedFuture(Boolean.TRUE);
	}

	@Override
	protected void handleCreateSampleAndPathogenTests(
		SampleDto sample,
		List<PathogenTestDto> pathogenTests,
		Disease disease,
		ExternalMessageDto labMessage,
		boolean entityCreated,
		boolean lastSample,
		HandlerCallback<SampleAndPathogenTests> callback) {

	}

	@Override
	protected void handleCreateContact(ContactDto contact, PersonDto person, ExternalMessageDto labMessage, HandlerCallback<ContactDto> callback) {

	}

	@Override
	protected void handlePickOrCreateEvent(ExternalMessageDto labMessage, HandlerCallback<PickOrCreateEventResult> callback) {

	}

	@Override
	protected void handleCreateEvent(EventDto event, HandlerCallback<EventDto> callback) {

	}

	@Override
	protected void handleCreateEventParticipant(
		EventParticipantDto eventParticipant,
		EventDto event,
		ExternalMessageDto labMessage,
		HandlerCallback<EventParticipantDto> callback) {

	}

	@Override
	protected CompletionStage<Boolean> confirmPickExistingEventParticipant() {
		return null;
	}

	@Override
	protected void handlePickOrCreateSample(
		List<SampleDto> similarSamples,
		List<SampleDto> otherSamples,
		ExternalMessageDto labMessage,
		int sampleReportIndex,
		HandlerCallback<PickOrCreateSampleResult> callback) {

	}

	@Override
	protected void handleEditSample(
		SampleDto sample,
		List<PathogenTestDto> newPathogenTests,
		ExternalMessageDto labMessage,
		boolean lastSample,
		HandlerCallback<SampleAndPathogenTests> callback) {

	}

	private boolean personDetailsMatch(PersonDto person1, PersonDto person2) {
		if (unsetOrMatches(person1.getFirstName(), person2.getFirstName())
			&& unsetOrMatches(person1.getLastName(), person2.getLastName())
			&& unsetOrMatches(person1.getBirthdateDD(), person2.getBirthdateDD())
			&& unsetOrMatches(person1.getBirthdateMM(), person2.getBirthdateMM())
			&& unsetOrMatches(person1.getBirthdateYYYY(), person2.getBirthdateYYYY())
			&& unsetOrMatches(person1.getAddress().getStreet(), person2.getAddress().getStreet())
			&& unsetOrMatches(person1.getAddress().getCity(), person2.getAddress().getCity())
			&& unsetOrMatches(person1.getAddress().getPostalCode(), person2.getAddress().getPostalCode())) {
			return true;
		}

		return false;
	}

	public CompletionStage<ProcessingResult<RelatedSamplesReportsAndPathogenTests>> run(ExternalMessageDto externalMessage) {
		return run(externalMessage, null);
	}
}
