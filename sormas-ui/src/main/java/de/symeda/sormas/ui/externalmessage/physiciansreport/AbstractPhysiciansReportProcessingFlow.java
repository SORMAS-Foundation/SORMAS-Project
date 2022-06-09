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
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.externalmessage.processing.AbstractProcessingFlow;
import de.symeda.sormas.ui.externalmessage.processing.PersonAndPickOrCreateEntryResult;
import de.symeda.sormas.ui.externalmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.ui.externalmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.ui.externalmessage.processing.flow.ProcessingResultStatus;

public abstract class AbstractPhysiciansReportProcessingFlow extends AbstractProcessingFlow {

	public AbstractPhysiciansReportProcessingFlow(UserDto user) {
		super(user);
	}

	public CompletionStage<ProcessingResult<CaseDataDto>> run(ExternalMessageDto externalMessage) {

		//@formatter:off
		return doInitialChecks(externalMessage)
			.then((ignored) -> pickOrCreatePerson(externalMessage))
			.then((p) -> pickOrCreateEntry(p.getData(), externalMessage))
			.thenSwitch()
				.when(PersonAndPickOrCreateEntryResult::isNewCase, (f, e) -> f
						.then((ignored) -> createCase(e.getPerson(), externalMessage)).then((c) ->
								convertSamePersonContactsAndEventParticipants(c.getData()).thenCompose((ignored) -> CompletableFuture.completedFuture(c)))
						.then((c) -> updateCase(c.getData(), externalMessage)))
				.when(PersonAndPickOrCreateEntryResult::isSelectedCase, (f, e) -> f.then((ignored) -> updateCase(e.getCaze(), externalMessage)))
				.then(s -> ProcessingResult.continueWith(s.getData()).asCompletedFuture())
			.then(currentResult -> ProcessingResult.of(ProcessingResultStatus.DONE, currentResult.getData()).asCompletedFuture())
			.getResult();
		//@formatter:on
	}

	private CompletionStage<ProcessingResult<PersonAndPickOrCreateEntryResult>> pickOrCreateEntry(
		PersonDto person,
		ExternalMessageDto externalMessage) {

		PersonReferenceDto personRef = person.toReference();
		List<CaseSelectionDto> similarCases = getSimilarCases(personRef, externalMessage);

		HandlerCallback<PickOrCreateEntryResult> callback = new HandlerCallback<>();

		handlePickOrCreateEntry(similarCases, externalMessage, callback);

		return callback.futureResult.thenCompose(p -> {
			if (p.getStatus().isCanceled()) {
				return ProcessingResult.<PersonAndPickOrCreateEntryResult> withStatus(p.getStatus()).asCompletedFuture();
			}

			return ProcessingResult.of(p.getStatus(), new PersonAndPickOrCreateEntryResult(person, p.getData())).asCompletedFuture();
		});
	}

	protected abstract void handlePickOrCreateEntry(
		List<CaseSelectionDto> similarCases,
		ExternalMessageDto externalMessage,
		HandlerCallback<PickOrCreateEntryResult> callback);

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
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(selectedCase.getUuid());;

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
