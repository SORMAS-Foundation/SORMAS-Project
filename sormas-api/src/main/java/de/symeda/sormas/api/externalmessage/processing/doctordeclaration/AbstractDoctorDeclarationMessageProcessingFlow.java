/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.api.externalmessage.processing.doctordeclaration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.activityascase.ActivityAsCaseType;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.processing.AbstractMessageProcessingFlowBase;
import de.symeda.sormas.api.externalmessage.processing.EventValidationResult;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEventResult;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TherapyReferenceDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DtoCopyHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
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
		postBuildActivitiesAsCase(caseDto, externalMessageDto);
		postBuildExposure(caseDto, externalMessageDto);
		postBuildHospitalization(caseDto, externalMessageDto);

		caseDto.setInvestigationStatus(InvestigationStatus.PENDING);
		caseDto.setOutcome(CaseOutcome.NO_OUTCOME);
	}

	/**
	 * Sets the symptoms for the case from the external message, if present.
	 *
	 * @param caseDto
	 *            The case data transfer object to update.
	 * @param externalMessageDto
	 *            The external message containing symptom data.
	 */
	protected void postBuildCaseSymptoms(CaseDataDto caseDto, ExternalMessageDto externalMessageDto) {
		if (externalMessageDto.getCaseSymptoms() != null) {
			final SymptomsDto symptomsDto = SymptomsDto.build();
			DtoCopyHelper.copyDtoValues(symptomsDto, externalMessageDto.getCaseSymptoms(), true, "uuid");
			caseDto.setSymptoms(symptomsDto);

			logger.debug("[POST BUILD CASE] Symptoms set for case with UUID: {}", caseDto.getUuid());
		}
	}

	/**
	 * Sets the therapy information for the case from the external message, if present.
	 *
	 * @param caseDto
	 *            The case data transfer object to update.
	 * @param externalMessageDto
	 *            The external message containing therapy data.
	 */
	protected void postBuildCaseTherapy(CaseDataDto caseDto, ExternalMessageDto externalMessageDto) {

		TherapyDto therapyDto = caseDto.getTherapy();

		therapyDto.setTreatmentStarted(externalMessageDto.getTreatmentStarted());
		therapyDto.setTreatmentStartDate(externalMessageDto.getTreatmentStartedDate());
		therapyDto.setTreatmentNotApplicable(Boolean.TRUE.equals(externalMessageDto.getTreatmentNotApplicable()));

		logger.debug("[POST BUILD CASE] Therapy set for case with UUID: {}", caseDto.getUuid());

	}

	/**
	 * Sets the activities as case for the case from the external message, if present.
	 * <p>
	 * This method deserializes the activities as case from a JSON string in the external message
	 * into a list of {@link ActivityAsCaseDto} objects. Each activity is built and copied, and if
	 * the activity type is missing, it is set to {@link ActivityAsCaseType#UNKNOWN}.
	 * If there are activities, the {@code activityAsCaseDetailsKnown} field is set to {@link YesNoUnknown#YES}.
	 * </p>
	 *
	 * @param caseDto
	 *            The case data transfer object to update.
	 * @param externalMessageDto
	 *            The external message containing activities as case data in JSON format.
	 */
	protected void postBuildActivitiesAsCase(CaseDataDto caseDto, ExternalMessageDto externalMessageDto) {

		if (externalMessageDto.getActivitiesAsCase() != null && !externalMessageDto.getActivitiesAsCase().isEmpty()) {
			final ArrayList<ActivityAsCaseDto> activitiesAsCase = new ArrayList<>();

			try {
				ObjectMapper objectMapper = new ObjectMapper();
				List<ActivityAsCaseDto> deserialActivityAsCaseDtos =
					objectMapper.readValue(externalMessageDto.getActivitiesAsCase(), new TypeReference<List<ActivityAsCaseDto>>() {
					});
				for (ActivityAsCaseDto activityAsCaseDto : deserialActivityAsCaseDtos) {
					ActivityAsCaseDto newActivityAsCase = ActivityAsCaseDto.build(activityAsCaseDto.getActivityAsCaseType());
					if (newActivityAsCase.getActivityAsCaseType() == null) {
						newActivityAsCase.setActivityAsCaseType(ActivityAsCaseType.UNKNOWN);
					}
					DtoCopyHelper.copyDtoValues(newActivityAsCase, activityAsCaseDto, true, "uuid");
					activitiesAsCase.add(newActivityAsCase);
				}
			} catch (Exception e) {
				logger.error("[POST BUILD CASE] Error while processing activities as case for case with UUID: {}", caseDto.getUuid(), e);
				return;
			}

			if (!activitiesAsCase.isEmpty()) {
				EpiDataDto epiData = caseDto.getEpiData();
				if (epiData == null) {
					epiData = EpiDataDto.build();
					caseDto.setEpiData(epiData);
				}

				epiData.setActivityAsCaseDetailsKnown(YesNoUnknown.YES);
				epiData.setActivitiesAsCase(activitiesAsCase);
			}

		} else {
			logger.debug("[POST BUILD CASE] No activities to set for case with UUID: {}", caseDto.getUuid());
		}
	}

	/**
	 * Sets the exposure information for the case from the external message, if present.
	 * <p>
	 * This method deserializes the exposures from a JSON string in the external message
	 * into a list of {@link ExposureDto} objects. Each exposure is built and copied, and if
	 * the exposure type is missing, it is set to {@link ExposureType#UNKNOWN}.
	 * If there are exposures, the {@code exposureDetailsKnown} field is set to {@link YesNoUnknown#YES}.
	 * </p>
	 *
	 * @param caseDto
	 *            The case data transfer object to update.
	 * @param externalMessageDto
	 *            The external message containing exposure data in JSON format.
	 */
	protected void postBuildExposure(CaseDataDto caseDto, ExternalMessageDto externalMessageDto) {

		if (externalMessageDto.getExposures() != null && !externalMessageDto.getExposures().isEmpty()) {
			final ArrayList<ExposureDto> exposures = new ArrayList<>();

			try {
				ObjectMapper objectMapper = new ObjectMapper();
				List<ExposureDto> deserialExposureDtos =
					objectMapper.readValue(externalMessageDto.getExposures(), new TypeReference<List<ExposureDto>>() {
					});
				for (ExposureDto exposureDto : deserialExposureDtos) {
					ExposureDto newExposure = ExposureDto.build(exposureDto.getExposureType());
					if (newExposure.getExposureType() == null) {
						newExposure.setExposureType(ExposureType.UNKNOWN);
					}
					DtoCopyHelper.copyDtoValues(newExposure, exposureDto, true, "uuid");
					exposures.add(newExposure);
				}
			} catch (Exception e) {
				logger.error("[POST BUILD CASE] Error while processing exposures for case with UUID: {}", caseDto.getUuid(), e);
				return;
			}

			if (!exposures.isEmpty()) {
				EpiDataDto epiData = caseDto.getEpiData();
				if (epiData == null) {
					epiData = EpiDataDto.build();
					caseDto.setEpiData(epiData);
				}

				epiData.setExposureDetailsKnown(YesNoUnknown.YES);
				epiData.setExposures(exposures);
			}

		} else {
			logger.debug("[POST BUILD CASE] No exposures to set for case with UUID: {}", caseDto.getUuid());
		}
	}

	/**
	 * Sets the hospitalization information for the case from the external message, if present.
	 *
	 * @param caseDto
	 *            The case data transfer object to update.
	 * @param externalMessageDto
	 *            The external message containing hospitalization data.
	 */
	protected void postBuildHospitalization(CaseDataDto caseDto, ExternalMessageDto externalMessageDto) {

		final FacilityReferenceDto hospitalFacilityReference = getHospitalFacilityReference(externalMessageDto);

		// If we do not have a hospital facility reference, we quit early
		if (hospitalFacilityReference == null) {
			logger.warn(
				"[POST BUILD HOSPITALIZATION] No hospital facility found for case with UUID: {}. Hospitalization details will not be set.",
				caseDto.getUuid());
			return;
		}

		HospitalizationDto hospitalizationDto = caseDto.getHospitalization();
		if (hospitalizationDto == null) {
			hospitalizationDto = HospitalizationDto.build();
			caseDto.setHospitalization(hospitalizationDto);
			logger.debug("[POST BUILD HOSPITALIZATION] Hospitalization initialized for case with UUID: {}", caseDto.getUuid());
		}

		final FacilityDto hospitalFacility = getExternalMessageProcessingFacade().getFacilityByUuid(hospitalFacilityReference.getUuid());

		// if for whatever reason the hospital facility is not found, we quit early
		if (hospitalFacility == null) {
			logger.warn(
				"[POST BUILD HOSPITALIZATION] Hospital facility with UUID: {} not found for case with UUID: {}. Hospitalization details will not be set.",
				hospitalFacilityReference.getUuid(),
				caseDto.getUuid());
			return;
		}

		// In case the patient is admitted to a health facility, we need to set the case hospitalization details and quit early
		if (YesNoUnknown.YES.equals(externalMessageDto.getAdmittedToHealthFacility())) {
			hospitalizationDto.setAdmissionDate(externalMessageDto.getHospitalizationAdmissionDate());
			hospitalizationDto.setDischargeDate(externalMessageDto.getHospitalizationDischargeDate());
			hospitalizationDto.setAdmittedToHealthFacility(externalMessageDto.getAdmittedToHealthFacility());

			caseDto.setResponsibleRegion(hospitalFacility.getRegion());
			caseDto.setResponsibleDistrict(hospitalFacility.getDistrict());
			caseDto.setResponsibleCommunity(hospitalFacility.getCommunity());
			caseDto.setHealthFacility(hospitalFacilityReference);
			caseDto.setDepartment(externalMessageDto.getHospitalizationFacilityDepartment());
			caseDto.setFacilityType(FacilityType.HOSPITAL);
			return;
		}

		// the patient is not admitted to a health facility, so we create a previous hospitalization entry
		final PreviousHospitalizationDto previousHospitalization = new PreviousHospitalizationDto();
		previousHospitalization.setUuid(DataHelper.createUuid());

		previousHospitalization.setAdmittedToHealthFacility(externalMessageDto.getAdmittedToHealthFacility());
		previousHospitalization.setAdmissionDate(externalMessageDto.getHospitalizationAdmissionDate());
		previousHospitalization.setDischargeDate(externalMessageDto.getHospitalizationDischargeDate());

		previousHospitalization.setRegion(hospitalFacility.getRegion());
		previousHospitalization.setDistrict(hospitalFacility.getDistrict());
		previousHospitalization.setCommunity(hospitalFacility.getCommunity());
		previousHospitalization.setHealthFacility(hospitalFacilityReference);
		previousHospitalization.setHealthFacilityDepartment(externalMessageDto.getHospitalizationFacilityDepartment());

		if (hospitalizationDto.getPreviousHospitalizations() == null) {
			hospitalizationDto.setPreviousHospitalizations(List.of(previousHospitalization));
			return;
		}
		hospitalizationDto.getPreviousHospitalizations().add(previousHospitalization);
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

	/**
	 * Retrieves the hospital facility reference based on the external message data.
	 * It first tries to find the facility by external ID and then by name containing external ID.
	 *
	 * @param externalMessageDto
	 *            The external message containing hospitalization facility information.
	 * @return The facility reference DTO for the hospital, or null if not found.
	 */
	protected FacilityReferenceDto getHospitalFacilityReference(ExternalMessageDto externalMessageDto) {
		final String hospitalName =
			StringUtils.isNotBlank(externalMessageDto.getHospitalizationFacilityName()) ? externalMessageDto.getHospitalizationFacilityName() : "";

		final String hospitalExternalId = StringUtils.isNotBlank(externalMessageDto.getHospitalizationFacilityExternalId())
			? externalMessageDto.getHospitalizationFacilityExternalId()
			: "";

		FacilityReferenceDto hospitalFacilityReference = StringUtils.isNotBlank(hospitalExternalId)
			? getExternalMessageProcessingFacade().getHospitalFacilityReferenceByExternalId(hospitalExternalId)
			: null;

		// Search for a hospital facility containing the external ID in name if not found by external ID
		if (hospitalFacilityReference == null && StringUtils.isNotBlank(hospitalExternalId)) {
			final Pattern hospitalIdInNamePattern = Pattern.compile(
				"^(\\b" + Pattern.quote(hospitalExternalId) + "\\b)?.*?(\\b" + Pattern.quote(hospitalExternalId) + "\\b).*?$",
				Pattern.CASE_INSENSITIVE);
			hospitalFacilityReference = getExternalMessageProcessingFacade().getHospitalFacilityReferenceNameMatching(hospitalIdInNamePattern)
				.stream()
				.findFirst()
				.orElse(null);
		}

		// Search for a hospital facility containing the given name if not found by external ID
		if (hospitalFacilityReference == null && StringUtils.isNotBlank(hospitalName)) {
			final Pattern hospitalNamePattern = Pattern.compile(Pattern.quote(hospitalName), Pattern.CASE_INSENSITIVE);
			hospitalFacilityReference =
				getExternalMessageProcessingFacade().getHospitalFacilityReferenceNameMatching(hospitalNamePattern).stream().findFirst().orElse(null);
		}

		return hospitalFacilityReference;
	}

}
