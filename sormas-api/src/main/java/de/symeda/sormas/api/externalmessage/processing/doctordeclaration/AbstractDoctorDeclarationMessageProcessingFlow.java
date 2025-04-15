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

package de.symeda.sormas.api.externalmessage.processing.labmessage;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.processing.AbstractMessageProcessingFlowBase;
import de.symeda.sormas.api.externalmessage.processing.EventValidationResult;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEventResult;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TherapyReferenceDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DtoCopyHelper;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;
import de.symeda.sormas.api.utils.dataprocessing.flow.FlowThen;

/**
 * Abstract class defining the flow of processing a lab message allowing to
 * choose between multiple options like create or select a
 * case/contact/event participant and then create or update a sample with
 * pathogen tests
 * The flow is coded in the `run` method.
 */
public abstract class AbstractDoctorDeclarationMessageProcessingFlow extends AbstractMessageProcessingFlowBase {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public AbstractDoctorDeclarationMessageProcessingFlow(
		ExternalMessageDto externalMessage,
		UserDto user,
		ExternalMessageMapper mapper,
		ExternalMessageProcessingFacade processingFacade) {
		super(user, externalMessage, mapper, processingFacade);
	}

	/**
	 * Handles the flow for creating or selecting an event participant.
	 *
	 * @param flow
	 *            The current flow of processing.
	 * @return The updated flow after processing the event participant.
	 */
	protected FlowThen<ExternalMessageProcessingResult> doCreateEventParticipantFlow(FlowThen<ExternalMessageProcessingResult> flow) {

		//@formatter:off
		return flow.thenSwitch(p -> pickOrCreateEvent())
				.when(PickOrCreateEventResult::isNewEvent, (f, p, r) -> {
					FlowThen<ExternalMessageProcessingResult> eventFlow = f.then(ignored -> createEvent(r));
					return eventFlow.then(pp -> createEventParticipant(pp.getData().getEvent(), r.getPerson(), pp.getData()));
				})
				.when(PickOrCreateEventResult::isEventSelected, (f, p, r) -> f
					.thenSwitch(e -> validateSelectedEvent(p.getEvent(), e.getData().getPerson()))
						.when(EventValidationResult::isEventSelected, (vf, v, vr) -> {
							FlowThen<ExternalMessageProcessingResult> eventFlow = vf.then(e -> {
								ExternalMessageProcessingResult withEvent = e.getData().withSelectedEvent(v.getEvent());

								logger.debug("[MESSAGE PROCESSING] Continue processing with event: {}", withEvent);
								
								return ProcessingResult.continueWith(withEvent).asCompletedFuture();
							});
							return eventFlow.then(pp -> createEventParticipant(pp.getData().getEvent(), r.getPerson(), pp.getData()));
						})
						.when(EventValidationResult::isEventParticipantSelected, (vf, v, vr) -> {
							EventDto event = getExternalMessageProcessingFacade().getEventByUuid(p.getEvent().getUuid());
							EventParticipantDto eventParticipant = getExternalMessageProcessingFacade().getEventParticipantByUuid(v.getEventParticipant().getUuid());

							FlowThen<ExternalMessageProcessingResult> eventParticipantFlow = vf.then(ignored -> {
								ExternalMessageProcessingResult withEventParticipant = vr.withSelectedEvent(event).withSelectedEventParticipant(eventParticipant);
								logger.debug("[MESSAGE PROCESSING] Continue processing with event participant: {}", withEventParticipant);
								
								return ProcessingResult
										.continueWith(withEventParticipant)
										.asCompletedFuture();
							});

							return eventParticipantFlow;
						})
						.when(EventValidationResult::isEventSelectionCanceled, (vf, v, vr) -> {
							logger.debug("[MESSAGE PROCESSING] Event selection discarded");
							return vf.then(ignored -> doCreateEventParticipantFlow(vf).getResult());
						})
					.then(ProcessingResult::asCompletedFuture))
			.then(ProcessingResult::asCompletedFuture);
		//@formatter:on
	}

	/**
	 * Handles the flow when a case is selected.
	 *
	 * @param caseSelection
	 *            The selected case.
	 * @param flow
	 *            The current flow of processing.
	 * @return The updated flow after processing the selected case.
	 */
	protected FlowThen<ExternalMessageProcessingResult> doCaseSelectedFlow(
		CaseSelectionDto caseSelection,
		FlowThen<ExternalMessageProcessingResult> flow) {

		CaseDataDto caze = getExternalMessageProcessingFacade().getCaseDataByUuid(caseSelection.getUuid());

		return flow.then(previousResult -> {
			ExternalMessageProcessingResult withCase = previousResult.getData().withSelectedCase(caze);

			logger.debug("[MESSAGE PROCESSING] Continue processing with case: {}", withCase);

			return ProcessingResult.continueWith(withCase).asCompletedFuture();
		});
	}

	/**
	 * Handles the flow when a contact is selected.
	 *
	 * @param contactSelection
	 *            The selected contact.
	 * @param flow
	 *            The current flow of processing.
	 * @return The updated flow after processing the selected contact.
	 */
	protected FlowThen<ExternalMessageProcessingResult> doContactSelectedFlow(
		SimilarContactDto contactSelection,
		FlowThen<ExternalMessageProcessingResult> flow) {

		ContactDto contact = getExternalMessageProcessingFacade().getContactByUuid(contactSelection.getUuid());

		return flow.then(previousResult -> {
			ExternalMessageProcessingResult withContact = previousResult.getData().withSelectedContact(contact);

			logger.debug("[MESSAGE PROCESSING] Continue processing with contact: {}", withContact);

			return ProcessingResult.continueWith(withContact).asCompletedFuture();
		});
	}

	/**
	 * Handles the flow when an event participant is selected.
	 *
	 * @param eventParticipantSelection
	 *            The selected event participant.
	 * @param flow
	 *            The current flow of processing.
	 * @return The updated flow after processing the selected event participant.
	 */
	protected FlowThen<ExternalMessageProcessingResult> doEventParticipantSelectedFlow(
		SimilarEventParticipantDto eventParticipantSelection,
		FlowThen<ExternalMessageProcessingResult> flow) {

		EventParticipantDto eventParticipant = getExternalMessageProcessingFacade().getEventParticipantByUuid(eventParticipantSelection.getUuid());
		EventDto event = getExternalMessageProcessingFacade().getEventByUuid(eventParticipant.getEvent().getUuid());

		return flow.then(previousResult -> {
			ExternalMessageProcessingResult withEventParticipant =
				previousResult.getData().withSelectedEvent(event).withSelectedEventParticipant(eventParticipant);

			logger.debug("[MESSAGE PROCESSING] Continue processing with event participant: {}", withEventParticipant);

			return ProcessingResult.continueWith(withEventParticipant).asCompletedFuture();
		});

	}

	/**
	 * Marks the external message as processed and updates its status.
	 *
	 * @param externalMessage
	 *            The external message to mark as processed.
	 * @param result
	 *            The processing result.
	 * @param surveillanceReport
	 *            The associated surveillance report, if any.
	 */
	protected void markExternalMessageAsProcessed(
		ExternalMessageDto externalMessage,
		ProcessingResult<ExternalMessageProcessingResult> result,
		SurveillanceReportDto surveillanceReport) {

		if (surveillanceReport != null) {
			externalMessage.setSurveillanceReport(surveillanceReport.toReference());
		}
		externalMessage.setStatus(ExternalMessageStatus.PROCESSED);
		externalMessage.setChangeDate(new Date());
		getExternalMessageProcessingFacade().saveExternalMessage(externalMessage);
	}

	/**
	 * Custom logic to execute after building a case.
	 *
	 * @param caseDto
	 *            The case data transfer object.
	 * @param externalMessageDto
	 *            The external message data transfer object.
	 */
	@Override
	protected void postBuildCase(CaseDataDto caseDto, ExternalMessageDto externalMessageDto) {

		logger.debug("[POST BUILD CASE] Processing case with UUID: {}", caseDto.getUuid());

		postBuildCaseSymptoms(caseDto, externalMessageDto);
		postBuildCaseTherapy(caseDto, externalMessageDto);

		caseDto.setInvestigationStatus(InvestigationStatus.PENDING);
		caseDto.setOutcome(CaseOutcome.NO_OUTCOME);
	}

	protected void postBuildCaseSymptoms(CaseDataDto caseDto, ExternalMessageDto externalMessageDto) {
		if (externalMessageDto.getCaseSymptoms() != null) {
			final SymptomsDto symptomsDto = SymptomsDto.build();
			DtoCopyHelper.copyDtoValues(symptomsDto, externalMessageDto.getCaseSymptoms(), true, "uuid");
			caseDto.setSymptoms(symptomsDto);

			logger.debug("[POST BUILD CASE] Symptoms set for case with UUID: {}", caseDto.getUuid());
		}
	}

	protected void postBuildCaseTherapy(CaseDataDto caseDto, ExternalMessageDto externalMessageDto) {

		TherapyDto therapyDto = caseDto.getTherapy();

		TreatmentDto treatmentDto = new TreatmentDto();
		treatmentDto.setTherapy(new TherapyReferenceDto(therapyDto.getUuid()));
		treatmentDto.setTreatmentDateTime(externalMessageDto.getTreatmentStartedDate());

		logger.debug("[POST BUILD CASE] Therapy set for case with UUID: {}", caseDto.getUuid());

	}

	/**
	 * Custom logic to execute after building a person.
	 *
	 * @param personDto
	 *            The person data transfer object.
	 * @param externalMessageDto
	 *            The external message data transfer object.
	 */
	@Override
	protected void postBuildPerson(PersonDto personDto, ExternalMessageDto externalMessageDto) {

		logger.debug("[POST BUILD PERSON] Processing person with UUID: {}", personDto.getUuid());

		if (externalMessageDto.getPersonGuardianFirstName() != null && externalMessageDto.getPersonGuardianLastName() != null) {
			personDto.setNamesOfGuardians(externalMessageDto.getPersonGuardianFirstName() + " " + externalMessageDto.getPersonGuardianLastName());

			logger.debug(
				"[POST BUILD PERSON] Guardian names set for person with UUID: {} - Guardian: {} {}",
				personDto.getUuid(),
				externalMessageDto.getPersonGuardianFirstName(),
				externalMessageDto.getPersonGuardianLastName());
		}
	}

}
