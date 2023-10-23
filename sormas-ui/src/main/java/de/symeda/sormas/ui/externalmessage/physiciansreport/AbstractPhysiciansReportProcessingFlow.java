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

package de.symeda.sormas.ui.externalmessage.physiciansreport;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.processing.AbstractProcessingFlow;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResultStatus;
import de.symeda.sormas.api.externalmessage.processing.labmessage.PersonAndPickOrCreateEntryResult;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserDto;

public abstract class AbstractPhysiciansReportProcessingFlow extends AbstractProcessingFlow {

	public AbstractPhysiciansReportProcessingFlow(UserDto user, ExternalMessageMapper mapper, ExternalMessageProcessingFacade processingFacade) {
		super(user, mapper, processingFacade);
	}

	public CompletionStage<ProcessingResult<CaseDataDto>> run(ExternalMessageDto externalMessage) {

		//@formatter:off
		return doInitialChecks(externalMessage, new ExternalMessageProcessingResult())
			.then(initialResult -> pickOrCreatePerson(initialResult.getData()))
			.thenSwitch(p -> pickOrCreateEntry(p.getData(), externalMessage))
				.when(PersonAndPickOrCreateEntryResult::isNewCase, (f, e, p) -> f
						.then(ignored -> createCase(e.getPerson(), externalMessage)).then(c ->
								convertSamePersonContactsAndEventParticipants(c.getData()).thenCompose(ignored -> CompletableFuture.completedFuture(c)))
						.then(c -> updateCase(c.getData(), externalMessage)))
				.when(PersonAndPickOrCreateEntryResult::isSelectedCase, (f, e, p) -> f.then(ignored -> updateCase(e.getCaze(), externalMessage)))
				.then(s -> ProcessingResult.continueWith(s.getData()).asCompletedFuture())
			.then(currentResult -> ProcessingResult.of(ProcessingResultStatus.DONE, currentResult.getData()).asCompletedFuture())
			.getResult();
		//@formatter:on
	}

	private CompletionStage<ProcessingResult<PersonAndPickOrCreateEntryResult>> pickOrCreateEntry(
		ExternalMessageProcessingResult previousResult,
		ExternalMessageDto externalMessage) {

		PersonDto person = previousResult.getPerson();
		PersonReferenceDto personRef = person.toReference();
		List<CaseSelectionDto> similarCases = getSimilarCases(personRef, externalMessage);

		AbstractProcessingFlow.HandlerCallback<PickOrCreateEntryResult> callback = new AbstractProcessingFlow.HandlerCallback<>();

		handlePickOrCreateEntry(similarCases, externalMessage, callback);

		return callback.futureResult.thenCompose(p -> {
			if (p.getStatus().isCanceled()) {
				return ProcessingResult.<PersonAndPickOrCreateEntryResult> withStatus(p.getStatus(), null).asCompletedFuture();
			}

			return ProcessingResult.of(p.getStatus(), new PersonAndPickOrCreateEntryResult(person, p.getData())).asCompletedFuture();
		});
	}

	protected abstract void handlePickOrCreateEntry(
		List<CaseSelectionDto> similarCases,
		ExternalMessageDto externalMessage,
		AbstractProcessingFlow.HandlerCallback<PickOrCreateEntryResult> callback);

	private CompletionStage<ProcessingResult<CaseDataDto>> createCase(PersonDto person, ExternalMessageDto externalMessage) {

		CaseDataDto caze = buildCase(person, externalMessage);

		HandlerCallback<CaseDataDto> callback = new HandlerCallback<>();
		handleCreateCase(caze, person, externalMessage, callback);

		return callback.futureResult;
	}

	protected abstract void handleCreateCase(
		CaseDataDto caze,
		PersonDto person,
		ExternalMessageDto externalMessage,
		HandlerCallback<CaseDataDto> callback);

	private CompletionStage<ProcessingResult<CaseDataDto>> updateCase(CaseSelectionDto selectedCase, ExternalMessageDto externalMessage) {
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(selectedCase.getUuid());

		return updateCase(caze, externalMessage);
	}

	private CompletionStage<ProcessingResult<CaseDataDto>> updateCase(CaseDataDto caze, ExternalMessageDto externalMessage) {

		HandlerCallback<CaseDataDto> callback = new HandlerCallback<>();
		handleUpdateCase(caze, externalMessage, callback);

		return callback.futureResult;
	}

	protected abstract void handleUpdateCase(CaseDataDto caze, ExternalMessageDto externalMessage, HandlerCallback<CaseDataDto> callback);

	private CompletionStage<ProcessingResult<Void>> convertSamePersonContactsAndEventParticipants(CaseDataDto caze) {

		HandlerCallback<Void> callback = new HandlerCallback<>();
		handleConvertSamePersonContactsAndEventParticipants(caze, callback);

		return callback.futureResult;
	}

	protected abstract void handleConvertSamePersonContactsAndEventParticipants(CaseDataDto caze, HandlerCallback<Void> callback);
}
