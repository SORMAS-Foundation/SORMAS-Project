/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.selfreport.processing;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DtoCopyHelper;
import de.symeda.sormas.api.utils.dataprocessing.EntitySelection;
import de.symeda.sormas.api.utils.dataprocessing.HandlerCallback;
import de.symeda.sormas.api.utils.dataprocessing.PickOrCreateEntryResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResultStatus;
import de.symeda.sormas.api.utils.dataprocessing.flow.FlowThen;

public abstract class AbstractSelfReportProcessingFlow {

	private static final Logger logger = LoggerFactory.getLogger(AbstractSelfReportProcessingFlow.class);
	private final SelfReportProcessingFacade processingFacade;
	protected final UserDto user;

	public AbstractSelfReportProcessingFlow(SelfReportProcessingFacade selfReportProcessingFacade, UserDto user) {
		this.processingFacade = selfReportProcessingFacade;
		this.user = user;
	}

	public CompletionStage<ProcessingResult<SelfReportProcessingResult>> runCaseFlow(SelfReportDto selfReport) {
		if (selfReport.getType() != SelfReportType.CASE) {
			throw new IllegalArgumentException("SelfReportDto must be of type CASE");
		}

		return new FlowThen<>().then(ignored -> pickOrCreatePerson(selfReport, new SelfReportProcessingResult()))
			.thenSwitch(personResult -> pickOrCreateCase(selfReport, personResult.getData()))
			.when(
				PickOrCreateEntryResult::isNewCase,
				(flow, pickOrCreateResult, ignored) -> flow.then(previousResult -> createCase(selfReport, previousResult.getData())))
			.when(
				PickOrCreateEntryResult::isSelectedCase,
				(flow, pickOrCreateResult, ignored) -> flow.then(previousResult -> pickCase(pickOrCreateResult.getCaze(), previousResult.getData())))
			.then(result -> ProcessingResult.of(ProcessingResultStatus.DONE, result.getData()).asCompletedFuture())
			.getResult()
			.thenCompose(res -> this.handleCaseProcessingDone(res, selfReport));
	}

	public CompletionStage<ProcessingResult<SelfReportProcessingResult>> runContactFlow(SelfReportDto selfReport) {
		if (selfReport.getType() != SelfReportType.CONTACT) {
			throw new IllegalArgumentException("SelfReportDto must be of type CONTACT");
		}

		return new FlowThen<>().then(ignored -> checkAndConfirmReferencedCaseReport(selfReport))
			.then(ignored -> pickOrCreatePerson(selfReport, new SelfReportProcessingResult()))
			.thenSwitch(personResult -> pickOrCreateContact(selfReport, personResult.getData()))
			.when(
				PickOrCreateEntryResult::isNewContact,
				(flow, pickOrCreateResult, ignored) -> flow.then(previousResult -> createContact(selfReport, previousResult.getData())))
			.when(
				PickOrCreateEntryResult::isSelectedContact,
				(flow, pickOrCreateResult, ignored) -> flow
					.then(previousResult -> pickContact(pickOrCreateResult.getContact(), previousResult.getData())))
			.then(result -> ProcessingResult.of(ProcessingResultStatus.DONE, result.getData()).asCompletedFuture())
			.getResult()
			.thenCompose(res -> this.handleContactProcessingDone(res, selfReport));
	}

	private CompletionStage<ProcessingResult<Void>> checkAndConfirmReferencedCaseReport(SelfReportDto selfReport) {
		if (processingFacade.existsReferencedCaseReport(selfReport)) {
			return confirmContinueWithoutProcessingReferencedCaseReport().thenApply(r -> {
				if (Boolean.TRUE.equals(r)) {
					return ProcessingResult.continueWith(null);
				}
				return ProcessingResult.of(ProcessingResultStatus.CANCELED, null);
			});
		}
		return ProcessingResult.<Void> continueWith(null).asCompletedFuture();
	}

	protected abstract CompletionStage<Boolean> confirmContinueWithoutProcessingReferencedCaseReport();

	protected CompletionStage<ProcessingResult<SelfReportProcessingResult>> pickOrCreatePerson(
		SelfReportDto selfReport,
		SelfReportProcessingResult previousResult) {

		final PersonDto person = buildPerson(selfReport);

		HandlerCallback<EntitySelection<PersonDto>> callback = new HandlerCallback<>();
		handlePickOrCreatePerson(person, callback);

		return mapHandlerResult(callback, previousResult, personSelection -> {
			logger.debug("[SELF REPORT PROCESSING] Continue processing with person: {}", personSelection);
			return previousResult.withPerson(personSelection.getEntity(), personSelection.isNew());
		});
	}

	private static PersonDto buildPerson(SelfReportDto selfReport) {
		PersonDto person = PersonDto.build();

		person.setFirstName(selfReport.getFirstName());
		person.setLastName(selfReport.getLastName());
		person.setSex(selfReport.getSex());
		person.setBirthdateDD(selfReport.getBirthdateDD());
		person.setBirthdateMM(selfReport.getBirthdateMM());
		person.setBirthdateYYYY(selfReport.getBirthdateYYYY());
		person.setNationalHealthId(selfReport.getNationalHealthId());
		person.setEmailAddress(selfReport.getEmail());
		person.setPhone(selfReport.getPhoneNumber());
		person.setAddress(DtoCopyHelper.copyDtoValues(LocationDto.build(), selfReport.getAddress(), true));

		return person;
	}

	protected abstract void handlePickOrCreatePerson(PersonDto person, HandlerCallback<EntitySelection<PersonDto>> callback);

	private CompletionStage<ProcessingResult<PickOrCreateEntryResult>> pickOrCreateCase(
		SelfReportDto selfReport,
		SelfReportProcessingResult previousResult) {

		PersonReferenceDto personRef = previousResult.getPerson().getEntity().toReference();
		List<CaseSelectionDto> similarCases = processingFacade.getSimilarCases(personRef, selfReport.getDisease());

		HandlerCallback<PickOrCreateEntryResult> callback = new HandlerCallback<>();

		handlePickOrCreateCase(similarCases, selfReport, callback);

		return callback.futureResult;
	}

	protected abstract void handlePickOrCreateCase(
		List<CaseSelectionDto> similarCases,
		SelfReportDto selfReport,
		HandlerCallback<PickOrCreateEntryResult> callback);

	private CompletionStage<ProcessingResult<PickOrCreateEntryResult>> pickOrCreateContact(
		SelfReportDto selfReport,
		SelfReportProcessingResult previousResult) {

		PersonReferenceDto personRef = previousResult.getPerson().getEntity().toReference();
		List<SimilarContactDto> similarContacts = processingFacade.getSimilarContacts(personRef, selfReport.getDisease());

		HandlerCallback<PickOrCreateEntryResult> callback = new HandlerCallback<>();

		handlePickOrCreateContact(similarContacts, selfReport, callback);

		return callback.futureResult;
	}

	protected abstract void handlePickOrCreateContact(
		List<SimilarContactDto> similarContacts,
		SelfReportDto selfReport,
		HandlerCallback<PickOrCreateEntryResult> callback);

	private CompletionStage<ProcessingResult<SelfReportProcessingResult>> createCase(
		SelfReportDto selfReport,
		SelfReportProcessingResult previousResult) {
		EntitySelection<PersonDto> personSelection = previousResult.getPerson();
		CaseDataDto caze = buildCase(selfReport, previousResult);

		HandlerCallback<CaseDataDto> callback = new HandlerCallback<>();
		handleCreateCase(caze, personSelection.getEntity(), personSelection.isNew(), selfReport, callback);

		return mapHandlerResult(callback, previousResult, cratedCase -> {
			logger.debug("[SELF REPORT PROCESSING] Continue processing with case: {}", cratedCase);
			return previousResult.withCase(cratedCase, true);
		});
	}

	protected abstract void handleCreateCase(
		CaseDataDto caze,
		PersonDto person,
		boolean isNewPerson,
		SelfReportDto selfReport,
		HandlerCallback<CaseDataDto> callback);

	private CompletionStage<ProcessingResult<SelfReportProcessingResult>> createContact(
		SelfReportDto selfReport,
		SelfReportProcessingResult previousResult) {
		EntitySelection<PersonDto> personSelection = previousResult.getPerson();
		ContactDto contact = buildContact(selfReport, previousResult);

		HandlerCallback<ContactDto> callback = new HandlerCallback<>();
		handleCreateContact(contact, personSelection.getEntity(), personSelection.isNew(), selfReport, callback);

		return mapHandlerResult(callback, previousResult, createdContact -> {
			logger.debug("[SELF REPORT PROCESSING] Continue processing with contact: {}", createdContact);
			return previousResult.withContact(createdContact, true);
		});
	}

	protected abstract void handleCreateContact(
		ContactDto contact,
		PersonDto person,
		boolean isNewPerson,
		SelfReportDto selfReport,
		HandlerCallback<ContactDto> callback);

	private CompletionStage<ProcessingResult<SelfReportProcessingResult>> pickCase(
		CaseSelectionDto selectedCase,
		SelfReportProcessingResult previousResult) {
		CaseDataDto caze = processingFacade.getCaseDataByUuid(selectedCase.getUuid());

		return CompletableFuture.completedFuture(ProcessingResult.continueWith(previousResult.withCase(caze, false)));
	}

	private CompletionStage<ProcessingResult<SelfReportProcessingResult>> pickContact(
		SimilarContactDto selectedContact,
		SelfReportProcessingResult previousResult) {
		ContactDto contact = processingFacade.getContactByUuid(selectedContact.getUuid());

		return CompletableFuture.completedFuture(ProcessingResult.continueWith(previousResult.withContact(contact, false)));
	}

	private CaseDataDto buildCase(SelfReportDto selfReport, SelfReportProcessingResult previousResult) {
		CaseDataDto caze = CaseDataDto.build(previousResult.getPerson().getEntity().toReference(), selfReport.getDisease());
		caze.setReportingUser(user.toReference());
		caze.setReportDate(new Date());
		caze.setCaseReferenceNumber(selfReport.getCaseReference());
		caze.setDiseaseDetails(selfReport.getDiseaseDetails());
		caze.setDiseaseVariant(selfReport.getDiseaseVariant());
		caze.setDiseaseVariantDetails(selfReport.getDiseaseVariantDetails());

		if (processingFacade.isFeatureEnabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			caze.setResponsibleRegion(processingFacade.getDefaultRegionReference());
			caze.setResponsibleDistrict(processingFacade.getDefaultDistrictReference());
			caze.setResponsibleCommunity(processingFacade.getDefaultCommunityReference());
		}
		caze.setHealthFacility(processingFacade.getNoneFacility());

		// TODO - quarantine type
		//caze.setQuarantine(QuarantineType.INSTITUTIONELL);
		caze.setQuarantineFrom(selfReport.getIsolationDate());
		caze.getSymptoms().setOnsetDate(selfReport.getDateOfSymptoms());
		// TODO
		// map date of test
		// map workplace, dateWorkplace

		return caze;
	}

	private ContactDto buildContact(SelfReportDto selfReport, SelfReportProcessingResult previousResult) {
		ContactDto contact = ContactDto.build(previousResult.getPerson().getEntity());

		contact.setReportingUser(user.toReference());
		contact.setReportDateTime(new Date());
		contact.setCaseReferenceNumber(selfReport.getCaseReference());
		contact.setDisease(selfReport.getDisease());
		contact.setDiseaseDetails(selfReport.getDiseaseDetails());
		contact.setDiseaseVariant(selfReport.getDiseaseVariant());
		contact.setLastContactDate(selfReport.getContactDate());

		if (processingFacade.isFeatureEnabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			contact.setRegion(processingFacade.getDefaultRegionReference());
			contact.setDistrict(processingFacade.getDefaultDistrictReference());
			contact.setCommunity(processingFacade.getDefaultCommunityReference());
		}

		// TODO - quarantine type
		//contact.setQuarantine(QuarantineType.INSTITUTIONELL);
		contact.setQuarantineFrom(selfReport.getIsolationDate());
		// TODO
		// map workplace, dateWorkplace

		return contact;
	}

	protected <T> CompletionStage<ProcessingResult<SelfReportProcessingResult>> mapHandlerResult(
		HandlerCallback<T> callback,
		SelfReportProcessingResult previousResult,
		Function<T, SelfReportProcessingResult> resultMapper) {
		return callback.futureResult.thenCompose(p -> {
			if (p.getStatus().isCanceled()) {
				logger.debug("[SELF REPORT PROCESSING] Processing canceled with status {}: {}", p.getStatus(), previousResult);
				return ProcessingResult.withStatus(p.getStatus(), previousResult).asCompletedFuture();
			}

			return ProcessingResult.of(p.getStatus(), resultMapper.apply(p.getData())).asCompletedFuture();
		});
	}

	private CompletionStage<ProcessingResult<SelfReportProcessingResult>> handleCaseProcessingDone(
		ProcessingResult<SelfReportProcessingResult> result,
		SelfReportDto selfReport) {
		ProcessingResultStatus status = result.getStatus();

		if (status.isDone()) {
			CaseDataDto caze = result.getData().getCaze().getEntity();
			processingFacade.markSelfReportAsProcessed(selfReport.toReference(), caze.toReference());

			if (processingFacade.existsContactToLinkToCase(caze.getCaseReferenceNumber())) {
				return confirmLinkContactsToCase().thenCompose(r -> {
					if (Boolean.TRUE.equals(r)) {
						processingFacade.linkContactsToCaseByReferenceNumber(caze.toReference());
					}
					return result.asCompletedFuture();
				});
			}
		}

		return result.asCompletedFuture();
	}

	protected abstract CompletionStage<Boolean> confirmLinkContactsToCase();

	private CompletionStage<ProcessingResult<SelfReportProcessingResult>> handleContactProcessingDone(
		ProcessingResult<SelfReportProcessingResult> result,
		SelfReportDto selfReport) {
		ProcessingResultStatus status = result.getStatus();

		if (status.isDone()) {
			ContactDto contact = result.getData().getContact().getEntity();
			processingFacade.markSelfReportAsProcessed(selfReport.toReference(), contact.toReference());

			List<CaseIndexDto> casesWithReferenceNumber = processingFacade.getCasesWithReferenceNumber(selfReport.getCaseReference());
			if (casesWithReferenceNumber.size() == 1) {
				return confirmLinkContactToCaseByReferenceNumber().thenCompose(r -> {
					if (Boolean.TRUE.equals(r)) {
						processingFacade.linkContactToCase(contact.toReference(), casesWithReferenceNumber.get(0).toReference());
					}
					return result.asCompletedFuture();
				});
			}
		}

		return result.asCompletedFuture();
	}

	protected abstract CompletionStage<Boolean> confirmLinkContactToCaseByReferenceNumber();
}
