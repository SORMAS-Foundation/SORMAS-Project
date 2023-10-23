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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult.EntitySelection;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.AbstractLabMessageProcessingFlow;
import de.symeda.sormas.api.externalmessage.processing.labmessage.PickOrCreateEventResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.PickOrCreateSampleResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.SampleAndPathogenTests;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;

@Stateless
@LocalBean
public class AutomaticLabMessageProcessor {
	@EJB
	private UserFacadeEjbLocal userFacade;
	@EJB
	private ExternalMessageProcessingFacadeEjbLocal processingFacade;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private CaseService caseService;
	@EJB
	private SampleFacadeEjbLocal sampleFacade;
	@EJB
	private PathogenTestFacadeEjbLocal pathogenTestFacade;

	public ProcessingResult<ExternalMessageProcessingResult> processLabMessage(ExternalMessageDto externalMessage)
		throws ExecutionException, InterruptedException {
		return new AutomaticLabMessageProcessingFlow(
			externalMessage,
			userFacade.getCurrentUser(),
			new ExternalMessageMapper(externalMessage, processingFacade),
			processingFacade).run().toCompletableFuture().get();
	}

	private class AutomaticLabMessageProcessingFlow extends AbstractLabMessageProcessingFlow {

		public AutomaticLabMessageProcessingFlow(
			ExternalMessageDto externalMessage,
			UserDto user,
			ExternalMessageMapper mapper,
			ExternalMessageProcessingFacade processingFacade) {
			super(externalMessage, user, mapper, processingFacade, null, true);
		}

		@Override
		protected CompletionStage<Boolean> handleMissingDisease() {
			return CompletableFuture.completedFuture(Boolean.FALSE);
		}

		@Override
		protected CompletionStage<Boolean> handleRelatedForwardedMessages() {
			throw new UnsupportedOperationException("Related forwarded messages not supported yet");
		}

		@Override
		protected void handlePickOrCreatePerson(PersonDto person, HandlerCallback<EntitySelection<PersonDto>> callback) {
			String nationalHealthId = person.getNationalHealthId();
			if (nationalHealthId != null) {
				List<PersonDto> matchingPersons = personFacade.getByNationalHealthId(nationalHealthId);
				if (matchingPersons.isEmpty()) {
					callback.done(new EntitySelection<>(personFacade.save(person), true));
				} else if (matchingPersons.size() == 1 && personDetailsMatch(person, matchingPersons.get(0))) {
					callback.done(new EntitySelection<>(matchingPersons.get(0), false));
				} else {
					callback.cancel();
				}
			} else {
				PersonSimilarityCriteria similarityCriteria = PersonSimilarityCriteria.forPerson(person, true);
				if (personFacade.checkMatchingNameInDatabase(user.toReference(), similarityCriteria)) {
					callback.cancel();
				} else {
					callback.done(new EntitySelection<>(personFacade.save(person), true));
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
			if (similarCases.isEmpty()) {
				result.setNewCase(true);
				callback.done(result);
			} else {
				String caseUuid = caseService.getCaseUuidForAutomaticSampleAssignment(
					similarCases.stream().map(CaseSelectionDto::getUuid).collect(Collectors.toSet()),
					similarCases.get(0).getDisease());

				if (caseUuid != null) {
					CaseSelectionDto caseToAsiignTo =
						similarCases.stream().filter(c -> c.getUuid().equals(caseUuid)).findFirst().orElseThrow(IllegalStateException::new);
					result.setCaze(caseToAsiignTo);
					callback.done(result);
				} else {
					callback.cancel();
				}
			}
		}

		@Override
		protected void handleCreateCase(CaseDataDto caze, PersonDto person, ExternalMessageDto labMessage, HandlerCallback<CaseDataDto> callback) {
			callback.done(caseFacade.save(caze));
		}

		@Override
		public CompletionStage<Boolean> handleMultipleSampleConfirmation() {
			return CompletableFuture.completedFuture(Boolean.FALSE);
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

			sample.setSamplePurpose(SamplePurpose.EXTERNAL);
			sample.setSampleMaterial(SampleMaterial.OTHER);
			sample.setSampleMaterialText("Automatically processed");

			sampleFacade.saveSample(sample);

			for (PathogenTestDto pathogenTest : pathogenTests) {
				pathogenTest.setTestResultVerified(true);
				pathogenTest.setTestType(PathogenTestType.OTHER);
				pathogenTest.setTestTypeText("Automatically processed");

				pathogenTestFacade.savePathogenTest(pathogenTest);
			}

			callback.done(new SampleAndPathogenTests(sample, pathogenTests));
		}

		@Override
		protected void handleCreateContact(
			ContactDto contact,
			PersonDto person,
			ExternalMessageDto labMessage,
			HandlerCallback<ContactDto> callback) {
			throw new UnsupportedOperationException("Creating contact not supported yet");
		}

		@Override
		protected void handlePickOrCreateEvent(ExternalMessageDto labMessage, HandlerCallback<PickOrCreateEventResult> callback) {
			throw new UnsupportedOperationException("Pcik or create event supported yet");
		}

		@Override
		protected void handleCreateEvent(EventDto event, HandlerCallback<EventDto> callback) {
			throw new UnsupportedOperationException("Creating event not supported yet");
		}

		@Override
		protected void handleCreateEventParticipant(
			EventParticipantDto eventParticipant,
			EventDto event,
			ExternalMessageDto labMessage,
			HandlerCallback<EventParticipantDto> callback) {
			throw new UnsupportedOperationException("Creating event not supported yet");
		}

		@Override
		protected CompletionStage<Boolean> confirmPickExistingEventParticipant() {
			throw new UnsupportedOperationException("Editing event participant not supported yet");
		}

		@Override
		protected void handlePickOrCreateSample(
			List<SampleDto> similarSamples,
			List<SampleDto> otherSamples,
			ExternalMessageDto labMessage,
			int sampleReportIndex,
			HandlerCallback<PickOrCreateSampleResult> callback) {
			PickOrCreateSampleResult result = new PickOrCreateSampleResult();
			result.setNewSample(true);
			callback.done(result);
		}

		@Override
		protected void handleEditSample(
			SampleDto sample,
			List<PathogenTestDto> newPathogenTests,
			ExternalMessageDto labMessage,
			ExternalMessageMapper mapper,
			boolean lastSample,
			HandlerCallback<SampleAndPathogenTests> callback) {
			throw new UnsupportedOperationException("Sample editing not supported yet");
		}

		@Override
		protected CompletionStage<Void> notifyCorrectionsSaved() {
			throw new UnsupportedOperationException("Corrections not supported yet");
		}

		private void handleException(Runnable action, HandlerCallback callback) {
			try {
				action.run();
			} catch (Exception e) {
				callback.cancel();
			}
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

		private <T> boolean unsetOrMatches(T personValue, T messageValue) {
			if (personValue == null || messageValue == null) {
				return true;
			}

			return personValue.equals(messageValue);
		}
	}
}
