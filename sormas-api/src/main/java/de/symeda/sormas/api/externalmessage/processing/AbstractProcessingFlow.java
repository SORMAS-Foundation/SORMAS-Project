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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.processing.flow.FlowThen;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResultStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;

public abstract class AbstractProcessingFlow {

	protected final UserDto user;

	protected final ExternalMessageProcessingFacade processingFacade;

	public AbstractProcessingFlow(UserDto user, ExternalMessageProcessingFacade processingFacade) {
		this.user = user;
		this.processingFacade = processingFacade;
	}

	protected FlowThen<Void> doInitialChecks(ExternalMessageDto externalMessageDto) {
		return new FlowThen<Void>().then(ignored -> checkDisease(externalMessageDto))
			.then(ignored -> checkRelatedForwardedMessages(externalMessageDto));
	}

	private CompletionStage<ProcessingResult<Void>> checkDisease(ExternalMessageDto externalMessageDto) {

		if (externalMessageDto.getDisease() == null) {
			return handleMissingDisease().thenCompose(
				next -> ProcessingResult
					.<Void> withStatus(Boolean.TRUE.equals(next) ? ProcessingResultStatus.CONTINUE : ProcessingResultStatus.CANCELED)
					.asCompletedFuture());
		} else {
			return ProcessingResult.<Void> continueWith(null).asCompletedFuture();
		}
	}

	protected abstract CompletionStage<Boolean> handleMissingDisease();

	private CompletionStage<ProcessingResult<Void>> checkRelatedForwardedMessages(ExternalMessageDto externalMessageDto) {

		if (processingFacade.existsForwardedExternalMessageWith(externalMessageDto.getReportId())) {
			return handleRelatedForwardedMessages().thenCompose(
				next -> ProcessingResult
					.<Void> withStatus(Boolean.TRUE.equals(next) ? ProcessingResultStatus.CONTINUE : ProcessingResultStatus.CANCELED)
					.asCompletedFuture());
		} else {
			return ProcessingResult.<Void> continueWith(null).asCompletedFuture();
		}
	}

	protected abstract CompletionStage<Boolean> handleRelatedForwardedMessages();

	protected CompletionStage<ProcessingResult<PersonDto>> pickOrCreatePerson(ExternalMessageMapper mapper) {

		final PersonDto person = buildPerson(mapper);

		HandlerCallback<PersonDto> callback = new HandlerCallback<>();
		handlePickOrCreatePerson(person, callback);

		return callback.futureResult;
	}

	protected abstract void handlePickOrCreatePerson(PersonDto person, HandlerCallback<PersonDto> callback);

	private PersonDto buildPerson(ExternalMessageMapper mapper) {

		final PersonDto personDto = PersonDto.build();

		mapper.mapToPerson(personDto);
		mapper.mapToLocation(personDto.getAddress());

		return personDto;
	}

	protected List<CaseSelectionDto> getSimilarCases(PersonReferenceDto selectedPerson, ExternalMessageDto externalMessageDto) {

		if (processingFacade.isFeatureDisabled(FeatureType.CASE_SURVEILANCE)
			|| !processingFacade.hasAllUserRights(UserRight.CASE_CREATE, UserRight.CASE_EDIT)) {
			return Collections.emptyList();
		}

		CaseCriteria caseCriteria = new CaseCriteria();
		caseCriteria.person(selectedPerson);
		caseCriteria.disease(externalMessageDto.getDisease());
		CaseSimilarityCriteria caseSimilarityCriteria = new CaseSimilarityCriteria();
		caseSimilarityCriteria.caseCriteria(caseCriteria);
		caseSimilarityCriteria.personUuid(selectedPerson.getUuid());

		return processingFacade.getSimilarCases(caseSimilarityCriteria);
	}

	protected CaseDataDto buildCase(PersonDto person, ExternalMessageDto externalMessageDto) {

		CaseDataDto caseDto = CaseDataDto.build(person.toReference(), externalMessageDto.getDisease());
		caseDto.setDiseaseVariant(externalMessageDto.getDiseaseVariant());
		caseDto.setDiseaseVariantDetails(externalMessageDto.getDiseaseVariantDetails());
		caseDto.setReportingUser(user.toReference());
		caseDto.setReportDate(
			externalMessageDto.getCaseReportDate() != null ? externalMessageDto.getCaseReportDate() : externalMessageDto.getMessageDateTime());

		FacilityReferenceDto personFacility = externalMessageDto.getPersonFacility();
		if (personFacility != null) {
			FacilityDto facility = processingFacade.getFacilityByUuid(personFacility.getUuid());
			FacilityType facilityType = facility.getType();

			caseDto.setResponsibleRegion(facility.getRegion());
			caseDto.setResponsibleDistrict(facility.getDistrict());

			if (facilityType.isAccommodation()) {
				caseDto.setFacilityType(facilityType);
				caseDto.setHealthFacility(personFacility);
			} else {
				caseDto.setHealthFacility(processingFacade.getFacilityReferenceByUuid(FacilityDto.NONE_FACILITY_UUID));
			}
		}

		return caseDto;
	}

	public static class HandlerCallback<T> {

		public final CompletableFuture<ProcessingResult<T>> futureResult;

		public HandlerCallback() {
			this.futureResult = new CompletableFuture<>();
		}

		public void done(T result) {
			futureResult.complete(ProcessingResult.continueWith(result));
		}

		public void cancel() {
			futureResult.complete(ProcessingResult.withStatus(ProcessingResultStatus.CANCELED));
		}
	}

}
