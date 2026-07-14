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
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.activityascase.ActivityAsCaseType;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.processing.AbstractMessageProcessingFlowBase;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleSimilarityCriteria;
import de.symeda.sormas.api.symptoms.SymptomsComparisonHelper;
import de.symeda.sormas.api.symptoms.SymptomsDto;
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

	protected AbstractDoctorDeclarationMessageProcessingFlow(
		ExternalMessageDto externalMessage,
		UserDto user,
		ExternalMessageMapper mapper,
		ExternalMessageProcessingFacade processingFacade) {
		super(user, externalMessage, mapper, processingFacade);
	}

	protected FlowThen<ExternalMessageProcessingResult> doPickOrCreateSamplesFlow(
		Consumer<SampleSimilarityCriteria> addSampleSearchCriteria,
		BiFunction<Integer, ExternalMessageProcessingResult, CompletionStage<ProcessingResult<ExternalMessageProcessingResult>>> createSampleAndPathogenTests,
		FlowThen<ExternalMessageProcessingResult> flow) {

		// We use the non null safe sample reports list in case no samples reports were created following DD parsing
		final List<SampleReportDto> sampleReports = getExternalMessage().getSampleReports();

		if (sampleReports == null || sampleReports.isEmpty()) {
			// Just do nothing
			return flow.then(ProcessingResult::asCompletedFuture);
		}

		// The sample report is not null or empty, we need to check if any tests exists or there are just empty reports
		final boolean hasTests = sampleReports.stream().anyMatch(report -> report.getTestReports() != null && !report.getTestReports().isEmpty());
		if (!hasTests) {
			// Just do nothing
			return flow.then(ProcessingResult::asCompletedFuture);
		}

		// Just proceed with the default implementation
		return super.doPickOrCreateSamplesFlow(addSampleSearchCriteria, createSampleAndPathogenTests, flow);
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
	 * Additional logic for updating the surveillance report in case of a doctor declaration.
	 * Updates the date of diagnosis and treatment fields.
	 * 
	 * @param surveillanceReport
	 *            The surveillance report to update.
	 * @param externalMessage
	 *            The external message containing the additional data.
	 * @param caze
	 *            The case containing the additional data.
	 */
	@Override
	protected void updateSurveillanceReportAdditionalData(
		SurveillanceReportDto surveillanceReport,
		ExternalMessageDto externalMessage,
		CaseDataDto caze) {
		super.updateSurveillanceReportAdditionalData(surveillanceReport, externalMessage, caze);

		// Make sure we update the date of diagnosis which is relevant for doctor declarations
		surveillanceReport.setDateOfDiagnosis(externalMessage.getDiagnosticDate());

		// Make sure we update the treatment fields which is relevant for doctor declarations
		surveillanceReport.setTreatmentStarted(externalMessage.getTreatmentStarted());
		surveillanceReport.setTreatmentStartDate(externalMessage.getTreatmentStartedDate());
		surveillanceReport.setTreatmentNotApplicable(Boolean.TRUE.equals(externalMessage.getTreatmentNotApplicable()));
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

		postBuildCaseData(caseDto, externalMessageDto);
		postBuildHealthConditions(caseDto, externalMessageDto);
		postBuildCaseSymptoms(caseDto, externalMessageDto);
		postBuildActivitiesAsCase(caseDto, externalMessageDto);
		postBuildExposure(caseDto, externalMessageDto);
		postBuildHospitalization(caseDto, externalMessageDto);

		caseDto.setInvestigationStatus(InvestigationStatus.PENDING);
		caseDto.setOutcome(CaseOutcome.NO_OUTCOME);
	}

	/**
	 * Sets radiography compatibility and other diagnostic criteria for the case from the external message.
	 *
	 * @param caseDto
	 *            The case data transfer object to update.
	 * @param externalMessageDto
	 *            The external message containing diagnostic data.
	 */
	protected void postBuildCaseData(CaseDataDto caseDto, ExternalMessageDto externalMessageDto) {

		caseDto.setCaseClassification(
			externalMessageDto.getCaseClassification() != null ? externalMessageDto.getCaseClassification() : caseDto.getCaseClassification());
		caseDto.setRadiographyCompatibility(externalMessageDto.getRadiographyCompatibility());
		caseDto.setOtherDiagnosticCriteria(externalMessageDto.getOtherDiagnosticCriteria());

		caseDto.setTreatmentStarted(externalMessageDto.getTreatmentStarted());
		caseDto.setTreatmentStartDate(externalMessageDto.getTreatmentStartedDate());
		caseDto.setTreatmentNotApplicable(Boolean.TRUE.equals(externalMessageDto.getTreatmentNotApplicable()));

	}

	/**
	 * Sets health conditions for the case from the external message.
	 *
	 * @param caseDto
	 *            The case data transfer object to update.
	 * @param externalMessageDto
	 *            The external message containing health condition data.
	 */
	protected void postBuildHealthConditions(CaseDataDto caseDto, ExternalMessageDto externalMessageDto) {
		final HealthConditionsDto healthConditionsDto =
			caseDto.getHealthConditions() != null ? caseDto.getHealthConditions() : HealthConditionsDto.build();

		if (Disease.TUBERCULOSIS.equals(externalMessageDto.getDisease())) {
			postBuildTuberculosisHealthConditions(healthConditionsDto, externalMessageDto);
		} else if (Disease.MALARIA.equals(externalMessageDto.getDisease())) {
			postBuildMalariaHealthConditions(healthConditionsDto, externalMessageDto);
		} else {
			logger.debug("[POST BUILD HEALTH CONDITIONS] Health conditions not set for case. Disease: {}", externalMessageDto.getDisease());
		}

		// we need to set it in case it is newly created
		caseDto.setHealthConditions(healthConditionsDto);
	}

	/**
	 * Sets malaria-specific health conditions for the case from the external message.
	 * 
	 * @param healthConditionsDto
	 * 
	 * @param externalMessageDto
	 */
	private void postBuildMalariaHealthConditions(HealthConditionsDto healthConditionsDto, ExternalMessageDto externalMessageDto) {
		healthConditionsDto.setMalaria(externalMessageDto.getMalaria());
		healthConditionsDto.setMalariaInfectedYear(externalMessageDto.getMalariaInfectedYear());
		logger.debug(
			"[POST BUILD HEALTH CONDITIONS] Malaria health conditions set for case. Malaria: {}, Malaria Infected Year: {}",
			externalMessageDto.getMalaria(),
			externalMessageDto.getMalariaInfectedYear());
	}

	/**
	 * Sets tuberculosis-specific health conditions for the case from the external message.
	 *
	 * @param healthConditionsDto
	 *            The health conditions data transfer object to update.
	 * @param externalMessageDto
	 *            The external message containing tuberculosis health condition data.
	 */
	protected void postBuildTuberculosisHealthConditions(HealthConditionsDto healthConditionsDto, ExternalMessageDto externalMessageDto) {
		healthConditionsDto.setTuberculosis(externalMessageDto.getTuberculosis());
		healthConditionsDto.setHiv(externalMessageDto.getHiv());
		healthConditionsDto.setHivArt(externalMessageDto.getHivArt());
		healthConditionsDto.setTuberculosisInfectionYear(externalMessageDto.getTuberculosisInfectionYear());
		healthConditionsDto.setPreviousTuberculosisTreatment(externalMessageDto.getPreviousTuberculosisTreatment());
		healthConditionsDto.setComplianceWithTreatment(externalMessageDto.getComplianceWithTreatment());

		logger.debug(
			"[POST BUILD HEALTH CONDITIONS] Tuberculosis health conditions set for case. Tuberculosis: {}, HIV: {}, HIV ART: {}, Infection Year: {}, Previous Treatment: {}, Compliance: {}",
			externalMessageDto.getTuberculosis(),
			externalMessageDto.getHiv(),
			externalMessageDto.getHivArt(),
			externalMessageDto.getTuberculosisInfectionYear(),
			externalMessageDto.getPreviousTuberculosisTreatment(),
			externalMessageDto.getComplianceWithTreatment());
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

	@Override
	protected boolean hasCaseSymptomsMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
		boolean symptomsMismatch = SymptomsComparisonHelper.hasCaseSymptomsMismatch(caze.getSymptoms(), externalMessage.getCaseSymptoms());

		if (symptomsMismatch) {
			logger.debug("[MESSAGE PROCESSING] Symptoms mismatch detected for existing case with UUID: {}", caze.getUuid());
		}

		return symptomsMismatch;
	}

	@Override
	protected boolean hasCaseHospitalizationMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
		// Check if there is hospitalization data in the external message
		if (externalMessage.getHospitalizationAdmissionDate() == null
			&& externalMessage.getHospitalizationDischargeDate() == null
			&& externalMessage.getAdmittedToHealthFacility() == null
			&& externalMessage.getHospitalizationFacilityName() == null
			&& externalMessage.getHospitalizationFacilityExternalId() == null) {
			return false;
		}

		// Compare with existing hospitalization data
		HospitalizationDto existingHospitalization = caze.getHospitalization();
		if (existingHospitalization == null) {
			// If there's external hospitalization data but no existing hospitalization, it's a mismatch
			return true;
		}

		// Check for differences in key hospitalization fields
		boolean admissionDateMismatch =
			!Objects.equals(existingHospitalization.getAdmissionDate(), externalMessage.getHospitalizationAdmissionDate());
		boolean dischargeDateMismatch =
			!Objects.equals(existingHospitalization.getDischargeDate(), externalMessage.getHospitalizationDischargeDate());
		boolean admittedMismatch =
			!Objects.equals(existingHospitalization.getAdmittedToHealthFacility(), externalMessage.getAdmittedToHealthFacility());

		boolean hospitalizationFacilityNameMismatch =
			!Objects.equals(
				caze.getHealthFacility() != null ? caze.getHealthFacility().getCaption() : null,
				externalMessage.getHospitalizationFacilityName());
		boolean hospitalizationFacilityExternalIdMismatch =
			!Objects.equals(
				caze.getHealthFacility() != null ? caze.getHealthFacility().getExternalId() : null,
				externalMessage.getHospitalizationFacilityExternalId());

		boolean mismatch = admissionDateMismatch || dischargeDateMismatch || admittedMismatch
			|| hospitalizationFacilityNameMismatch || hospitalizationFacilityExternalIdMismatch;

		if (mismatch) {
			logger.debug("[MESSAGE PROCESSING] Hospitalization mismatch detected for existing case with UUID: {}", caze.getUuid());
		}

		return mismatch;
	}

	@Override
	protected boolean hasCaseExposuresMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
		// Check if there are exposures in the external message
		if (externalMessage.getExposures() == null || externalMessage.getExposures().isEmpty()) {
			return false;
		}

		// Check if the case already has exposures
		EpiDataDto epiData = caze.getEpiData();
		if (epiData == null || epiData.getExposures() == null || epiData.getExposures().isEmpty()) {
			// If there are external exposures but no existing exposures, it's a mismatch
			return true;
		}

		// If both have exposures, consider it a mismatch (user should be informed to review)
		boolean mismatch = true;

		if (mismatch) {
			logger.debug("[MESSAGE PROCESSING] Exposures mismatch detected for existing case with UUID: {}", caze.getUuid());
		}

		return mismatch;
	}

	@Override
	protected boolean hasCaseActivitiesAsCaseMismatch(CaseDataDto caze, ExternalMessageDto externalMessage) {
		// Check if there are activities as case in the external message
		if (externalMessage.getActivitiesAsCase() == null || externalMessage.getActivitiesAsCase().isEmpty()) {
			return false;
		}

		// Check if the case already has activities as case
		EpiDataDto epiData = caze.getEpiData();
		if (epiData == null || epiData.getActivitiesAsCase() == null || epiData.getActivitiesAsCase().isEmpty()) {
			// If there are external activities but no existing activities, it's a mismatch
			return true;
		}

		// If both have activities, consider it a mismatch (user should be informed to review)
		boolean mismatch = true;

		if (mismatch) {
			logger.debug("[MESSAGE PROCESSING] Activities as case mismatch detected for existing case with UUID: {}", caze.getUuid());
		}

		return mismatch;
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
				epiData.setAirportWorker(externalMessageDto.getAirportWorker());
				epiData.setHealthcareProfessional(externalMessageDto.getHealthcareProfessional());
				epiData.setModeOfTransmission(externalMessageDto.getModeOfTransmission());
				epiData.setModeOfTransmissionType(externalMessageDto.getModeOfTransmissionType());
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

		if (externalMessageDto.getHospitalizationFacilityName() == null
			&& externalMessageDto.getHospitalizationFacilityExternalId() == null
			&& externalMessageDto.getHospitalizationFacilityDepartment() == null) {
			logger.info("[POST BUILD HOSPITALIZATION] No hospitalization information found for case with UUID: {}.", caseDto.getUuid());
			return;
		}

		HospitalizationDto hospitalizationDto = caseDto.getHospitalization();
		if (hospitalizationDto == null) {
			hospitalizationDto = HospitalizationDto.build();
			caseDto.setHospitalization(hospitalizationDto);
			logger.debug("[POST BUILD HOSPITALIZATION] Hospitalization initialized for case with UUID: {}", caseDto.getUuid());
		}

		final FacilityDto hospitalFacility =
			hospitalFacilityReference != null ? getExternalMessageProcessingFacade().getFacilityByUuid(hospitalFacilityReference.getUuid()) : null;

		// In case the patient is admitted to a health facility, we need to set the case hospitalization details and quit early
		if (YesNoUnknown.YES.equals(externalMessageDto.getAdmittedToHealthFacility())) {
			hospitalizationDto.setAdmissionDate(externalMessageDto.getHospitalizationAdmissionDate());
			hospitalizationDto.setDischargeDate(externalMessageDto.getHospitalizationDischargeDate());
			hospitalizationDto.setAdmittedToHealthFacility(externalMessageDto.getAdmittedToHealthFacility());

			// we need to do a sanity check on discharge date
			// if the discharge date is before today's date, we need to handle it
			if (hospitalizationDto.getDischargeDate() != null && hospitalizationDto.getDischargeDate().before(new Date())) {
				hospitalizationDto.setCurrentlyHospitalized(YesNoUnknown.NO);
			} else {
				// the date is either null or in the future so we consider the patient currently hospitalized
				hospitalizationDto.setCurrentlyHospitalized(YesNoUnknown.YES);
			}

			caseDto.setDepartment(externalMessageDto.getHospitalizationFacilityDepartment());
			caseDto.setFacilityType(FacilityType.HOSPITAL);

			// if for whatever reason the hospital facility is not found, we quit early
			if (hospitalFacility == null) {
				logger.warn(
					"[POST BUILD HOSPITALIZATION] Hospital facility not found for case with UUID: {}. Hospitalization details will not be set.",
					caseDto.getUuid());
				return;
			}

			// we have a facility, so we set the responsible region, district and community
			caseDto.setResponsibleRegion(hospitalFacility.getRegion());
			caseDto.setResponsibleDistrict(hospitalFacility.getDistrict());
			caseDto.setResponsibleCommunity(hospitalFacility.getCommunity());
			caseDto.setHealthFacility(hospitalFacilityReference);

			return;
		}

		// the patient is not admitted to a health facility, so we create a previous hospitalization entry
		final PreviousHospitalizationDto previousHospitalization = new PreviousHospitalizationDto();
		previousHospitalization.setUuid(DataHelper.createUuid());

		// Here we set the admitted to health facility to yes, because currently he is not admitted to a health facility thus meaning that this is a previous hospitalization
		previousHospitalization.setAdmittedToHealthFacility(YesNoUnknown.YES);
		previousHospitalization.setAdmissionDate(externalMessageDto.getHospitalizationAdmissionDate());
		previousHospitalization.setDischargeDate(externalMessageDto.getHospitalizationDischargeDate());

		if (hospitalFacility != null) {
			previousHospitalization.setRegion(hospitalFacility.getRegion());
			previousHospitalization.setDistrict(hospitalFacility.getDistrict());
			previousHospitalization.setCommunity(hospitalFacility.getCommunity());
			previousHospitalization.setHealthFacility(hospitalFacilityReference);
		}

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
		// No additional actions needed for person data
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
