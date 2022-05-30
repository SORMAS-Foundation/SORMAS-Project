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

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.labmessage.LabMessageMapper;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.ui.labmessage.processing.flow.FlowThen;
import de.symeda.sormas.ui.labmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.ui.labmessage.processing.flow.ProcessingResultStatus;

public abstract class AbstractProcessingFlow {

	protected final UserDto user;

	public AbstractProcessingFlow(UserDto user) {
		this.user = user;
	}

	protected FlowThen<Void> doInitialChecks(LabMessageDto labMessage) {
		return new FlowThen<Void>().then(ignored -> checkDisease(labMessage)).then(ignored -> checkRelatedForwardedMessages(labMessage));
	}

	private CompletionStage<ProcessingResult<Void>> checkDisease(LabMessageDto labMessage) {

		if (labMessage.getTestedDisease() == null) {
			return handleMissingDisease().thenCompose(
				next -> ProcessingResult
					.<Void> withStatus(Boolean.TRUE.equals(next) ? ProcessingResultStatus.CONTINUE : ProcessingResultStatus.CANCELED)
					.asCompletedFuture());
		} else {
			return ProcessingResult.<Void> continueWith(null).asCompletedFuture();
		}
	}

	protected abstract CompletionStage<Boolean> handleMissingDisease();

	private CompletionStage<ProcessingResult<Void>> checkRelatedForwardedMessages(LabMessageDto labMessage) {

		if (FacadeProvider.getLabMessageFacade().existsForwardedLabMessageWith(labMessage.getReportId())) {
			return handleRelatedForwardedMessages().thenCompose(
				next -> ProcessingResult
					.<Void> withStatus(Boolean.TRUE.equals(next) ? ProcessingResultStatus.CONTINUE : ProcessingResultStatus.CANCELED)
					.asCompletedFuture());
		} else {
			return ProcessingResult.<Void> continueWith(null).asCompletedFuture();
		}
	}

	protected abstract CompletionStage<Boolean> handleRelatedForwardedMessages();

	protected CompletionStage<ProcessingResult<PersonDto>> pickOrCreatePerson(LabMessageDto labMessage) {

		final PersonDto person = buildPerson(LabMessageMapper.forLabMessage(labMessage));

		AbstractLabMessageProcessingFlow.HandlerCallback<PersonDto> callback = new HandlerCallback<>();
		handlePickOrCreatePerson(person, callback);

		return callback.futureResult;
	}

	protected abstract void handlePickOrCreatePerson(PersonDto person, HandlerCallback<PersonDto> callback);

	private PersonDto buildPerson(LabMessageMapper mapper) {

		final PersonDto personDto = PersonDto.build();

		mapper.mapToPerson(personDto);
		mapper.mapToLocation(personDto.getAddress());

		return personDto;
	}

	protected List<CaseSelectionDto> getSimilarCases(PersonReferenceDto selectedPerson, LabMessageDto labMessage) {

		CaseCriteria caseCriteria = new CaseCriteria();
		caseCriteria.person(selectedPerson);
		caseCriteria.disease(labMessage.getTestedDisease());
		CaseSimilarityCriteria caseSimilarityCriteria = new CaseSimilarityCriteria();
		caseSimilarityCriteria.caseCriteria(caseCriteria);
		caseSimilarityCriteria.personUuid(selectedPerson.getUuid());

		return FacadeProvider.getCaseFacade().getSimilarCases(caseSimilarityCriteria);
	}

	protected CaseDataDto buildCase(PersonDto person, LabMessageDto labMessage) {

		CaseDataDto caseDto = CaseDataDto.build(person.toReference(), labMessage.getTestedDisease());
		caseDto.setReportingUser(user.toReference());
		return caseDto;
	}

	public static class HandlerCallback<T> {

		protected final CompletableFuture<ProcessingResult<T>> futureResult;

		protected HandlerCallback() {
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
