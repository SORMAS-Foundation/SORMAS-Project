/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.externalmessage.processing;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.dataprocessing.EntitySelection;
import de.symeda.sormas.api.utils.dataprocessing.HandlerCallback;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus;
import de.symeda.sormas.api.utils.dataprocessing.flow.FlowThen;

public abstract class AbstractProcessingFlow {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final UserDto user;

	private final ExternalMessageMapper mapper;
	private final ExternalMessageProcessingFacade processingFacade;

	public AbstractProcessingFlow(UserDto user, ExternalMessageMapper mapper, ExternalMessageProcessingFacade processingFacade) {
		this.user = user;
		this.mapper = mapper;
		this.processingFacade = processingFacade;
	}

	protected FlowThen<ExternalMessageProcessingResult> doInitialChecks(
		ExternalMessageDto externalMessage,
		ExternalMessageProcessingResult defaultResult) {
		return new FlowThen<ExternalMessageProcessingResult>().then(ignored -> checkDisease(externalMessage, defaultResult))
			.then(ignored -> checkInfraData(externalMessage, defaultResult))
			.then(ignored -> checkRelatedForwardedMessages(externalMessage, defaultResult));
	}

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> checkDisease(
		ExternalMessageDto externalMessageDto,
		ExternalMessageProcessingResult defaultResult) {
		if (externalMessageDto.getDisease() == null) {
			return handleMissingDisease().thenCompose(next -> {
				if (Boolean.TRUE.equals(next)) {
					logger.debug("[MESSAGE PROCESSING] The disease is missing in the external message, but it was decided to continue processing");

					return ProcessingResult.withStatus(ProcessingResultStatus.CONTINUE, defaultResult).asCompletedFuture();
				} else {
					logger.debug("[MESSAGE PROCESSING] The disease is missing in the external message, and it was decided to cancel processing");

					return ProcessingResult.withStatus(ProcessingResultStatus.CANCELED, defaultResult).asCompletedFuture();
				}
			});
		} else {
			return ProcessingResult.continueWith(defaultResult).asCompletedFuture();
		}
	}

	/**
	 * This method is called to check the infrastructure data of the external message.
	 * It can be overridden to implement specific checks.
	 *
	 * @param externalMessageDto
	 *            The external message to check.
	 * @param defaultResult
	 *            The default result to return if the checks pass.
	 * @return A CompletableFuture containing the processing result.
	 */
	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> checkInfraData(
		ExternalMessageDto externalMessageDto,
		ExternalMessageProcessingResult defaultResult) {

		return handleInfraDataChecks().thenCompose(next -> {
			if (Boolean.TRUE.equals(next)) {
				logger.debug("[MESSAGE PROCESSING] The infrastructure data checks passed, continuing processing");
				return ProcessingResult.continueWith(defaultResult).asCompletedFuture();
			} else {
				logger.debug("[MESSAGE PROCESSING] The infrastructure data checks failed, canceling processing");
				return ProcessingResult.withStatus(ProcessingResultStatus.CANCELED, defaultResult).asCompletedFuture();
			}
		});
	}

	/**
	 * This method is called to perform infrastructure data checks.
	 * It can be overridden to implement specific checks.
	 * By default, no checks are performed, and it returns a completed future with true.
	 *
	 * @return A CompletableFuture containing a boolean indicating whether the checks passed.
	 */
	protected CompletionStage<Boolean> handleInfraDataChecks() {
		// No specific infrastructure data checks are performed by default,
		return CompletableFuture.completedFuture(Boolean.TRUE);
	}

	protected abstract CompletionStage<Boolean> handleMissingDisease();

	private CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> checkRelatedForwardedMessages(
		ExternalMessageDto externalMessageDto,
		ExternalMessageProcessingResult defaultResult) {

		if (processingFacade.existsForwardedExternalMessageWith(externalMessageDto.getReportId())) {
			return handleRelatedForwardedMessages().thenCompose(next -> {
				if (Boolean.TRUE.equals(next)) {
					logger.debug("[MESSAGE PROCESSING] A related messages exists that has been forwarded, but it was decided to continue processing");

					return ProcessingResult.withStatus(ProcessingResultStatus.CONTINUE, defaultResult).asCompletedFuture();
				} else {
					logger.debug("[MESSAGE PROCESSING] A related messages exists that has been forwarded, and it was decided to cancel processing");

					return ProcessingResult.withStatus(ProcessingResultStatus.CANCELED, defaultResult).asCompletedFuture();
				}
			});
		} else {
			return ProcessingResult.continueWith(defaultResult).asCompletedFuture();
		}
	}

	protected abstract CompletionStage<Boolean> handleRelatedForwardedMessages();

	protected CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> pickOrCreatePerson(ExternalMessageProcessingResult previousResult) {

		final PersonDto person = buildPerson();

		HandlerCallback<EntitySelection<PersonDto>> callback = new HandlerCallback<>();
		handlePickOrCreatePerson(person, callback);

		return mapHandlerResult(callback, previousResult, personSelection -> {
			logger.debug("[MESSAGE PROCESSING] Continue processing with person: {}", personSelection);

			// requested for #13589
			doPersonUpdates(personSelection);

			return previousResult.withPerson(personSelection.getEntity(), personSelection.isNew());
		});
	}

	protected <T> CompletionStage<ProcessingResult<ExternalMessageProcessingResult>> mapHandlerResult(
		HandlerCallback<T> callback,
		ExternalMessageProcessingResult previousResult,
		Function<T, ExternalMessageProcessingResult> resultMapper) {
		return callback.futureResult.thenCompose(p -> {
			if (p.getStatus().isCanceled()) {
				logger.debug("[MESSAGE PROCESSING] Processing canceled with status {}: {}", p.getStatus(), previousResult);
				return ProcessingResult.withStatus(p.getStatus(), previousResult).asCompletedFuture();
			}

			return ProcessingResult.of(p.getStatus(), resultMapper.apply(p.getData())).asCompletedFuture();
		});
	}

	protected abstract void handlePickOrCreatePerson(PersonDto person, HandlerCallback<EntitySelection<PersonDto>> callback);

	protected abstract void doPersonUpdates(EntitySelection<PersonDto> personSelection);

	private PersonDto buildPerson() {

		final PersonDto personDto = PersonDto.build();

		mapper.mapToPerson(personDto);
		mapper.mapToLocation(personDto.getAddress());

		return personDto;
	}

	protected CaseDataDto buildCase(PersonDto person, ExternalMessageDto externalMessageDto) {

		CaseDataDto caseDto = CaseDataDto.build(person.toReference(), externalMessageDto.getDisease());
		caseDto.setDiseaseVariant(externalMessageDto.getDiseaseVariant());
		caseDto.setDiseaseVariantDetails(externalMessageDto.getDiseaseVariantDetails());
		caseDto.setReportingUser(user.toReference());
		caseDto.setReportDate(
			externalMessageDto.getCaseReportDate() != null ? externalMessageDto.getCaseReportDate() : externalMessageDto.getMessageDateTime());

		if (processingFacade.isFeatureEnabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			caseDto.setResponsibleRegion(processingFacade.getDefaultRegionReference());
			caseDto.setResponsibleDistrict(processingFacade.getDefaultDistrictReference());
			caseDto.setResponsibleCommunity(processingFacade.getDefaultCommunityReference());
		}

		FacilityReferenceDto personFacility = externalMessageDto.getPersonFacility();
		if (personFacility != null) {
			FacilityDto facility = processingFacade.getFacilityByUuid(personFacility.getUuid());
			FacilityType facilityType = facility.getType();

			caseDto.setResponsibleRegion(facility.getRegion());
			caseDto.setResponsibleDistrict(facility.getDistrict());

			if (facilityType != null && facilityType.isAccommodation()) {
				caseDto.setFacilityType(facilityType);
				caseDto.setHealthFacility(personFacility);
			} else {
				caseDto.setHealthFacility(processingFacade.getFacilityReferenceByUuid(FacilityDto.NONE_FACILITY_UUID));
			}
		} else if (!processingFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			caseDto.setHealthFacility(processingFacade.getFacilityReferenceByUuid(FacilityDto.NONE_FACILITY_UUID));
		}

		caseDto.setVaccinationStatus(externalMessageDto.getVaccinationStatus());
		caseDto.getHospitalization().setAdmittedToHealthFacility(externalMessageDto.getAdmittedToHealthFacility());

		postBuildCase(caseDto, externalMessageDto);

		return caseDto;
	}

	/**
	 * This method is called after the case data has been built and allows for additional processing or modifications
	 * to the case data based on the external message.
	 *
	 * @param caseDto
	 *            The case data that has been built.
	 * @param externalMessageDto
	 *            The external message that contains additional information.
	 */
	protected abstract void postBuildCase(CaseDataDto caseDto, ExternalMessageDto externalMessageDto);

	/**
	 * This method is called after the person data has been built
	 * and allows for additional processing or modifications.
	 *
	 * @param personDto
	 * @param externalMessageDto
	 */
	protected abstract void postBuildPerson(PersonDto personDto, ExternalMessageDto externalMessageDto);

	public ExternalMessageProcessingFacade getExternalMessageProcessingFacade() {
		return processingFacade;
	}

	public UserDto getUser() {
		return user;
	}

	public ExternalMessageMapper getMapper() {
		return mapper;
	}

}
