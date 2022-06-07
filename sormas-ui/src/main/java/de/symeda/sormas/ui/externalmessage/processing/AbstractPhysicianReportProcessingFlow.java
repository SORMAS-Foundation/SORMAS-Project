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

package de.symeda.sormas.ui.labmessage.processing;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.labmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.ui.labmessage.processing.flow.ProcessingResultStatus;

public abstract class AbstractPhysicianReportProcessingFlow extends AbstractProcessingFlow {

	public AbstractPhysicianReportProcessingFlow(UserDto user) {
		super(user);
	}

	public CompletionStage<ProcessingResult<CaseDataDto>> run(LabMessageDto labMessage) {

		//@formatter:off
		return doInitialChecks(labMessage)
			.then((ignored) -> pickOrCreatePerson(labMessage))
			.then((p) -> pickOrCreateEntry(p.getData(), labMessage))
			.thenSwitch()
				.when(PersonAndPickOrCreateEntryResult::isNewCase, (f, e) -> f
						.then((ignored) -> createCase(e.getPerson(), labMessage)).then((c) ->
								convertSamePersonContactsAndEventparticipants(c.getData()).thenCompose((ignored) -> CompletableFuture.completedFuture(c)))
						.then((c) -> updateCase(c.getData(), labMessage)))
				.when(PersonAndPickOrCreateEntryResult::isSelectedCase, (f, e) -> f.then((ignored) -> updateCase(e.getCaze(), labMessage)))
				.then(s -> ProcessingResult.continueWith(s.getData()).asCompletedFuture())
			.then(currentResult -> ProcessingResult.of(ProcessingResultStatus.DONE, currentResult.getData()).asCompletedFuture())
			.getResult();
		//@formatter:on
	}

	private CompletionStage<ProcessingResult<PersonAndPickOrCreateEntryResult>> pickOrCreateEntry(PersonDto person, LabMessageDto labMessage) {

		PersonReferenceDto personRef = person.toReference();
		List<CaseSelectionDto> similarCases = getSimilarCases(personRef, labMessage);

		HandlerCallback<PickOrCreateEntryResult> callback = new HandlerCallback<>();

		handlePickOrCreateEntry(similarCases, labMessage, callback);

		return callback.futureResult.thenCompose(p -> {
			if (p.getStatus().isCanceled()) {
				return ProcessingResult.<PersonAndPickOrCreateEntryResult> withStatus(p.getStatus()).asCompletedFuture();
			}

			return ProcessingResult.of(p.getStatus(), new PersonAndPickOrCreateEntryResult(person, p.getData())).asCompletedFuture();
		});
	}

	protected abstract void handlePickOrCreateEntry(
		List<CaseSelectionDto> similarCases,
		LabMessageDto labMessage,
		HandlerCallback<PickOrCreateEntryResult> callback);

	private CompletionStage<ProcessingResult<CaseDataDto>> createCase(PersonDto person, LabMessageDto labMessage) {

		CaseDataDto caze = buildCase(person, labMessage);

		HandlerCallback<CaseDataDto> callback = new HandlerCallback<>();
		handleCreateCase(caze, person, labMessage, callback);

		return callback.futureResult;
	}

	protected abstract void handleCreateCase(CaseDataDto caze, PersonDto person, LabMessageDto labMessage, HandlerCallback<CaseDataDto> callback);

	private CompletionStage<ProcessingResult<CaseDataDto>> updateCase(CaseSelectionDto selectedCase, LabMessageDto labMessage) {
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(selectedCase.getUuid());;

		return updateCase(caze, labMessage);
	}

	private CompletionStage<ProcessingResult<CaseDataDto>> updateCase(CaseDataDto caze, LabMessageDto labMessage) {

		HandlerCallback<CaseDataDto> callback = new HandlerCallback<>();
		handleUpdateCase(caze, labMessage, callback);

		return callback.futureResult;
	}

	protected abstract void handleUpdateCase(CaseDataDto caze, LabMessageDto labMessage, HandlerCallback<CaseDataDto> callback);

	private CompletionStage<Void> convertSamePersonContactsAndEventparticipants(CaseDataDto caze) {
		CompletableFuture<Void> ret = new CompletableFuture<>();
		ControllerProvider.getCaseController().convertSamePersonContactsAndEventparticipants(caze, () -> {
			ret.complete(null);
		});

		return ret;
	}
}
