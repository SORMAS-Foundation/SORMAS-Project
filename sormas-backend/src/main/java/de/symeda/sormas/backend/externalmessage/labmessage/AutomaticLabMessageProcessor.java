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

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEventResult;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateSampleResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.AbstractLabMessageProcessingFlow;
import de.symeda.sormas.api.externalmessage.processing.labmessage.SampleAndPathogenTests;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.dataprocessing.EntitySelection;
import de.symeda.sormas.api.utils.dataprocessing.HandlerCallback;
import de.symeda.sormas.api.utils.dataprocessing.PickOrCreateEntryResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;
import de.symeda.sormas.api.utils.luxembourg.LuxembourgNationalHealthIdValidator;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class AutomaticLabMessageProcessor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private ConfigFacadeEjbLocal configFacade;
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

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public ProcessingResult<ExternalMessageProcessingResult> processLabMessage(ExternalMessageDto externalMessage)
		throws InterruptedException, ExecutionException {
		return new AutomaticLabMessageProcessingFlow(
			externalMessage,
			userFacade.getCurrentUser(),
			new ExternalMessageMapper(externalMessage, processingFacade),
			processingFacade).run().handle((result, throwable) -> {
				if (throwable != null) {
					em.getTransaction().rollback();
					throw new RuntimeException(throwable);
				} else if (result.getStatus().isCanceled()) {
					em.getTransaction().rollback();
				}

				return result;
			}).toCompletableFuture().get();
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

		/**
		 * Handles person picking/creation with localized logic.
		 * In case of Luxembourg, this method checks for a valid national health ID
		 * and attempts to find an exact matching person.
		 * Other country specific handling could be added to this method in the future.
		 *
		 * @param person
		 *            The {@link PersonDto} to process.
		 * @param callback
		 *            The {@link HandlerCallback} to deliver the result of the
		 *            operation.
		 * @return {@code true} if the person was handled by this method (either found
		 *         or created),
		 *         {@code false} otherwise.
		 */
		protected boolean localizedHandlePickOrCreatePerson(PersonDto person, HandlerCallback<EntitySelection<PersonDto>> callback) {

			if (configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
				String nationalHealthId = person.getNationalHealthId();
				if (StringUtils.isBlank(nationalHealthId)) {
					logger.debug("[MESSAGE PROCESSING] Incoming person's national health ID is blank. Canceling processing.");
					callback.cancel();
					return true;
				}

				if (!LuxembourgNationalHealthIdValidator.isValid(nationalHealthId, null, null, null)) {
					logger.debug("[MESSAGE PROCESSING] Incoming person's national health ID is not valid. Canceling processing.");
					callback.cancel();
					return true;
				}

				final List<PersonDto> matchingPersons = personFacade.getByNationalHealthId(nationalHealthId);

				// Multiple persons matched
				if (matchingPersons.size() > 1) {
					logger
						.debug("[MESSAGE PROCESSING] Multiple persons with the same national health id found in the database. Canceling processing.");
					callback.cancel();
					return true;
				}

				// No persons matched
				if (matchingPersons.isEmpty()) {
					callback.done(new EntitySelection<>(personFacade.save(person), true));
					return true;
				}

				// Exactly one person matched
				callback.done(new EntitySelection<>(matchingPersons.get(0), false));
				return true;
			}

			return false;
		}

		/**
		 * Handles person picking/creation with default logic. This method checks for a
		 * national health ID. If present, it searches for matching persons. If no ID is
		 * found, it checks for similar persons based on name.
		 *
		 * @param person
		 *            The {@link PersonDto} to process.
		 * @param callback
		 *            The {@link HandlerCallback} to deliver the result of the
		 *            operation.
		 */
		protected void defaultHandlePickOrCreatePerson(PersonDto person, HandlerCallback<EntitySelection<PersonDto>> callback) {

			String nationalHealthId = person.getNationalHealthId();
			if (StringUtils.isNotBlank(nationalHealthId)) {
				List<PersonDto> matchingPersons = personFacade.getByNationalHealthId(nationalHealthId);
				if (matchingPersons.isEmpty()) {
					callback.done(new EntitySelection<>(personFacade.save(person), true));
				} else if (matchingPersons.size() == 1 && personDetailsMatch(person, matchingPersons.get(0))) {
					callback.done(new EntitySelection<>(matchingPersons.get(0), false));
				} else {
					logger.debug(
						"[MESSAGE PROCESSING] Multiple persons with the same national health id found in the database, or the one with same id seems to be a different person. Canceling processing.");
					callback.cancel();
				}
			} else {
				PersonSimilarityCriteria similarityCriteria = PersonSimilarityCriteria.forPerson(person, true, false);
				if (personFacade.checkMatchingNameInDatabase(getUser().toReference(), similarityCriteria)) {
					logger.debug("[MESSAGE PROCESSING] Similar persons found in the database. Canceling processing.");
					callback.cancel();
				} else {
					callback.done(new EntitySelection<>(personFacade.save(person), true));
				}
			}
		}

		/**
		 * Handles the process of picking an existing person or creating a new one.
		 * This method first attempts to use localized handling. If that fails, it
		 * defaults to the general handling logic.
		 *
		 * @param person
		 *            The {@link PersonDto} to process.
		 * @param callback
		 *            The {@link HandlerCallback} to deliver the result of the
		 *            operation.
		 */
		@Override
		protected void handlePickOrCreatePerson(PersonDto person, HandlerCallback<EntitySelection<PersonDto>> callback) {

			// try to see if any localized handling is processing it
			if (localizedHandlePickOrCreatePerson(person, callback)) {
				return;
			}

			// no localized handling was do so go with default
			defaultHandlePickOrCreatePerson(person, callback);
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
				// In some cases the resulting case disease needs to change (TUBERCULOSIS message with IGRA tests => LATENT TUBERCULOSIS case)
				Disease disease = externalMessageDto.getDisease();

				// we need to keep track of the diseases that should be automatically assigned samples
				final Set<Disease> automaticSampleAssignmentDiseases = new HashSet<>();
				automaticSampleAssignmentDiseases.add(disease);

				if (getExternalMessageProcessingFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
					if (isLatentTuberculosisMessage(externalMessageDto)) {
						disease = Disease.LATENT_TUBERCULOSIS;
						// original disease was Tuberculosis, so we need to add the newly created disease to the set
						automaticSampleAssignmentDiseases.add(disease);
					}

					// other luxembourg specific settings
				}

				Integer automaticSampleAssignmentThreshold = diseaseConfigurationFacade.getAutomaticSampleAssignmentThreshold(disease);
				if (automaticSampleAssignmentThreshold == null) {
					logger.debug(
						"[MESSAGE PROCESSING] No automatic sample assignment threshold configured for disease {}. Canceling processing.",
						disease);
					callback.cancel();
					return;
				}

				final Date automaticAssignmentSampleDate = externalMessageDto.getSampleReports()
					.stream()
					.map(SampleReportDto::getSampleDateTime)
					.filter(Objects::nonNull)
					.min(Comparator.comparing(Date::getTime))
					.orElse(null);

				final Set<String> similarCaseUuids = similarCases.stream().map(CaseSelectionDto::getUuid).collect(Collectors.toSet());

				final List<String> autoAssignCaseUuids = caseService.getCaseUuidsForAutomaticSampleAssignment(
					similarCaseUuids,
					automaticSampleAssignmentDiseases,
					automaticAssignmentSampleDate,
					automaticSampleAssignmentThreshold);


				CaseSelectionDto caseToAssignTo = null;

				// special case for LATENT_TUBERCULOSIS: if there are more caseUuids and we have tuberculosis cases we need to give priority to the tuberculosis cases
				if (disease == Disease.LATENT_TUBERCULOSIS && autoAssignCaseUuids.size() > 1) {
					// sort the cases by disease giving priority to tuberculosis cases and take the first one (the one with the highest priority and most recent report date)
					caseToAssignTo = similarCases.stream()
						.filter(c -> autoAssignCaseUuids.contains(c.getUuid()))
						.sorted(Comparator.comparing((CaseSelectionDto c) -> c.getDisease() == Disease.TUBERCULOSIS ? 0 : 1)
							.thenComparing((CaseSelectionDto c) -> c.getReportDate(), Comparator.reverseOrder()))
						.findFirst()
						.orElse(null);
				} else {
					caseToAssignTo = similarCases.stream().filter(c -> autoAssignCaseUuids.contains(c.getUuid())).findFirst().orElse(null);
				}

				if (caseToAssignTo == null) {
					logger.debug(
						"[MESSAGE PROCESSING] None of the similar cases {} is usable for automatic sample assignment. Continue with case creation.",
						similarCaseUuids);
					result.setNewCase(true);
				} else {
					boolean overrideWithNewCase = false;

					// we need to check if the existing case is a LATENT_TUBERCULOSIS with only IGRA negative tests
					// if so and the new case has only IGRA positive test, we need to override and create a new case
					if (getExternalMessageProcessingFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)
						&& disease == Disease.LATENT_TUBERCULOSIS
						&& samplesHaveIgraPositiveTest(externalMessageDto.getSampleReports())) {

						// Unfortunately we need to load the case to check it
						final CaseDataDto caze = getExternalMessageProcessingFacade().getCaseDataByUuid(caseToAssignTo.getUuid());

						if (caze.getDisease() == Disease.LATENT_TUBERCULOSIS) {
							// and we need to get the samples as well
							List<SampleDto> similarCaseSamples =
								getExternalMessageProcessingFacade().getSamplesByCaseUuids(Collections.singletonList(caseToAssignTo.getUuid()));

							// if all samples are not positive, we can override
							if (similarCaseSamples.stream().allMatch(s -> s.getPathogenTestResult() != PathogenTestResultType.POSITIVE)) {
								overrideWithNewCase = true;
							}

							// if there was a positive case the caseUuid should be used
						}
					}

					if (overrideWithNewCase) {
						result.setNewCase(true);
					} else {
						result.setCaze(caseToAssignTo);
					}
				}

				callback.done(result);
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

			sample.setSamplePurpose(SamplePurpose.EXTERNAL);

			sampleFacade.saveSample(sample);

			for (PathogenTestDto pathogenTest : pathogenTests) {
				pathogenTest.setTestResultVerified(true);
				pathogenTest.setViaLims(true);

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

		private boolean unsetOrMatches(String personValue, String messageValue) {
			if (personValue == null || messageValue == null) {
				return true;
			}

			Collator collator = Collator.getInstance();
			collator.setStrength(Collator.PRIMARY);

			return collator.compare(normalizatString(personValue), normalizatString(messageValue)) == 0;
		}

		private String normalizatString(String string) {
			return string.replaceAll("[\\s-,;:]", "");
		}
	}
}
