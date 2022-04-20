/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.labmessage;

import static java.util.concurrent.CompletableFuture.completedFuture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.SimilarEntriesDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public abstract class LabMessageProcessingFlow {

	private final UserReferenceDto userRef;

	/*
	 * *** checkDisease
	 * checkRelatedForwardedMessages
	 * handleRelatedLabMessages
	 * pickOrCreatePerson
	 * pickOrCreateEntry
	 * createCase
	 * createContact
	 * pickOrCreateEvent
	 * createEventParticipant
	 * createSample(eventParticipant)
	 * pickOrCreateSample(eventParticipant)
	 * ...
	 * createSample
	 * pickOrCreateSample(caze)
	 * editSample
	 * createSample(caze)
	 * createSample(contact)
	 * createSample(eventParticipant)
	 * pickOrCreateSample(contact)
	 * ....
	 * pickOrCreateSample(eventParticipant)
	 * ....
	 */

	public LabMessageProcessingFlow(UserReferenceDto userRef) {
		this.userRef = userRef;
	}

	public ResultStatus run(LabMessageDto labMessage, RelatedLabMessageHandler relatedLabMessageHandler)
		throws ExecutionException, InterruptedException {

		return new FlowThen<Void>().then((ignored) -> checkDisease(labMessage))
			.then((ignored) -> checkRelatedForwardedMessages(labMessage))
			.then((ignored) -> handleRelatedLabMessages(relatedLabMessageHandler, labMessage))
			.then((ignored) -> pickOrCreatePerson(labMessage))
			.then((personResult) -> pickOrCreateEntry(personResult.getData(), labMessage))
			.thenSwitch()
			.when(
				entryResult -> entryResult.getSimilarEntrySelection().isNewCase(),
				(f) -> f.then((personResult) -> createCase(personResult.getData().getPersonDto(), labMessage))
					.then((caseResult) -> createSample(caseResult.getData(), labMessage)))
			.when(
				entryResult -> entryResult.getSimilarEntrySelection().isNewContact(),
				(f) -> f.then((ignored) -> createContact(labMessage)).then((contactResult) -> createSample(contactResult.getData(), labMessage)))
			.then((ignored) -> Result.completedStatus(ResultStatus.DONE))
			.getResult()
			.getStatus();
	}

	private CompletionStage<Result<Void>> checkDisease(LabMessageDto labMessage) {
		if (labMessage.getTestedDisease() == null) {
			return handleMissingDisease().thenCompose(next -> Result.completedStatus(next ? ResultStatus.CONTINUE : ResultStatus.CANCELED));
		} else {
			return Result.completedContinue();
		}
	}

	abstract CompletionStage<Boolean> handleMissingDisease();

	private CompletionStage<Result<Void>> checkRelatedForwardedMessages(LabMessageDto labMessage) {
		if (FacadeProvider.getLabMessageFacade().existsForwardedLabMessageWith(labMessage.getReportId())) {
			return handleRelatedForwardedMessages().thenCompose(next -> Result.completedStatus(next ? ResultStatus.CONTINUE : ResultStatus.CANCELED));
		} else {
			return Result.completedStatus(ResultStatus.CONTINUE);
		}
	}

	protected abstract CompletionStage<Boolean> handleRelatedForwardedMessages();

	private CompletionStage<Result<Void>> handleRelatedLabMessages(RelatedLabMessageHandler relatedLabMessageHandler, LabMessageDto labMessage) {
		return relatedLabMessageHandler.handle(labMessage).thenCompose((result) -> {
			if (result == RelatedLabMessageHandler.HandlerResult.CANCELED) {
				return Result.completedStatus(ResultStatus.CANCELED);
			}

			if (result == RelatedLabMessageHandler.HandlerResult.CANCELED_WITH_UPDATES) {
				return Result.completedStatus(ResultStatus.CANCELED_WITH_CORRECTIONS);
			}

			if (result == RelatedLabMessageHandler.HandlerResult.HANDLED) {
				return Result.completedStatus(ResultStatus.DONE);
			}

			return Result.completedContinue();
		});
	}

	private CompletionStage<Result<PersonDto>> pickOrCreatePerson(LabMessageDto labMessage) {
		return handlePickOrCreatePerson(labMessage)
			.thenCompose(person -> person != null ? Result.completedContinue(person) : Result.completedStatus(ResultStatus.CANCELED));
	}

	protected abstract CompletionStage<PersonDto> handlePickOrCreatePerson(LabMessageDto labMessage);

	private CompletionStage<Result<PersonAndSimilarEntrySelection>> pickOrCreateEntry(PersonDto person, LabMessageDto labMessage) {
		PersonReferenceDto personRef = person.toReference();
		List<CaseSelectionDto> similarCases = getSimilarCases(personRef, labMessage);
		List<SimilarContactDto> similarContacts = getSimilarContacts(personRef, labMessage);
		List<SimilarEventParticipantDto> similarEventParticipants = getSimilarEventParticipants(labMessage, personRef);

		return handlePickOrCreateEntry(labMessage, similarCases, similarContacts, similarEventParticipants)
			.thenCompose(similarEntrySelection -> Result.completedContinue(new PersonAndSimilarEntrySelection(person, similarEntrySelection)));
	}

	protected abstract CompletionStage<SimilarEntriesDto> handlePickOrCreateEntry(
		LabMessageDto labMessageDto,
		List<CaseSelectionDto> similarCases,
		List<SimilarContactDto> similarContacts,
		List<SimilarEventParticipantDto> similarEventParticipants);

	private List<CaseSelectionDto> getSimilarCases(PersonReferenceDto selectedPerson, LabMessageDto labMessage) {
		CaseCriteria caseCriteria = new CaseCriteria();
		caseCriteria.person(selectedPerson);
		caseCriteria.disease(labMessage.getTestedDisease());
		CaseSimilarityCriteria caseSimilarityCriteria = new CaseSimilarityCriteria();
		caseSimilarityCriteria.caseCriteria(caseCriteria);
		caseSimilarityCriteria.personUuid(selectedPerson.getUuid());

		return FacadeProvider.getCaseFacade().getSimilarCases(caseSimilarityCriteria);
	}

	private List<SimilarContactDto> getSimilarContacts(PersonReferenceDto selectedPerson, LabMessageDto labMessage) {
		ContactSimilarityCriteria contactSimilarityCriteria = new ContactSimilarityCriteria();
		contactSimilarityCriteria.setPerson(selectedPerson);
		contactSimilarityCriteria.setDisease(labMessage.getTestedDisease());

		return FacadeProvider.getContactFacade().getMatchingContacts(contactSimilarityCriteria);
	}

	private List<SimilarEventParticipantDto> getSimilarEventParticipants(LabMessageDto labMessage, PersonReferenceDto selectedPerson) {
		EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
		eventParticipantCriteria.setPerson(selectedPerson);
		eventParticipantCriteria.setDisease(labMessage.getTestedDisease());

		return FacadeProvider.getEventParticipantFacade().getMatchingEventParticipants(eventParticipantCriteria);
	}

	private CompletionStage<Result<CaseDataDto>> createCase(PersonDto person, LabMessageDto labMessage) {
		CaseDataDto caze = buildCase(person, labMessage);

		return handleCreateCase(caze, person, labMessage).thenCompose(
			(createdCase) -> createdCase != null ? Result.completedContinue(createdCase) : Result.completedStatus(ResultStatus.CANCELED));
	}

	protected abstract CompletionStage<CaseDataDto> handleCreateCase(CaseDataDto caze, PersonDto person, LabMessageDto labMessage);

	private CaseDataDto buildCase(PersonDto person, LabMessageDto labMessage) {
		CaseDataDto caseDto = CaseDataDto.build(person.toReference(), labMessage.getTestedDisease());
		caseDto.setReportingUser(userRef);
		return caseDto;
	}

	private CompletionStage<Result<SampleDto>> createSample(CaseDataDto caze, LabMessageDto labMessage) {
		SampleDto sample = SampleDto.build(userRef, caze.toReference());
		LabMessageMapper.forLabMessage(labMessage).mapToSample(sample);

		return handleCreateSample(sample, caze.getDisease(), labMessage).thenCompose(Result::completedContinue);
	}

	private CompletionStage<Result<SampleDto>> createSample(ContactDto contact, LabMessageDto labMessage) {
		SampleDto sample = SampleDto.build(userRef, contact.toReference());
		LabMessageMapper.forLabMessage(labMessage).mapToSample(sample);

		return handleCreateSample(sample, contact.getDisease(), labMessage).thenCompose(Result::completedContinue);
	}

	protected abstract CompletionStage<SampleDto> handleCreateSample(SampleDto sample, Disease disease, LabMessageDto labMessage);

	private CompletionStage<Result<ContactDto>> createContact(LabMessageDto labMessageDto) {
		return null;
	}

	enum ResultStatus {

		CONTINUE(false, false),
		CANCELED(true, false),
		CANCELED_WITH_CORRECTIONS(true, false),
		DONE(false, true);

		private final boolean canceled;
		private final boolean done;

		ResultStatus(boolean canceled, boolean done) {
			this.canceled = canceled;
			this.done = done;
		}

		public boolean isCanceled() {
			return canceled;
		}

		public boolean isDone() {
			return done;
		}
	}

	private static class Result<T> {

		private final ResultStatus status;

		private final T data;

		public static <TT> CompletableFuture<Result<TT>> completedStatus(ResultStatus status) {
			return completedFuture(withStatus(status));
		}

		public static CompletableFuture<Result<Void>> completedContinue() {
			return completedFuture(withStatus(ResultStatus.CONTINUE));
		}

		public static <TT> CompletableFuture<Result<TT>> completedContinue(TT data) {
			return completedFuture(continueWith(data));
		}

		public static <TT> Result<TT> withStatus(ResultStatus status) {
			return new Result<>(status, null);
		}

		public static <TT> Result<TT> continueWith(TT data) {
			return new Result<>(ResultStatus.CONTINUE, data);
		}

		private Result(ResultStatus status, T data) {
			this.status = status;
			this.data = data;
		}

		public ResultStatus getStatus() {
			return status;
		}

		public T getData() {
			return data;
		}
	}

	private interface IFlowThen<R> {

		<RR> IFlowThen<RR> then(Function<Result<R>, CompletionStage<Result<RR>>> actionSupplier) throws ExecutionException, InterruptedException;

		<RR> IThenSwitch<R, RR> thenSwitch() throws ExecutionException, InterruptedException;

		Result<R> getResult();
	}

	private interface IThenSwitch<T, R> {

		<RR> IThenSwitch<T, RR> when(Function<T, Boolean> condition, SwitchFlow<T, RR> switchFlow) throws ExecutionException, InterruptedException;

		<RR> IFlowThen<RR> then(Function<Result<R>, CompletionStage<Result<RR>>> actionSupplier) throws ExecutionException, InterruptedException;
	}

	private static class FlowThen<R> implements IFlowThen<R> {

		private final Result<R> currentResult;

		public FlowThen() {
			this(null);
		}

		private FlowThen(Result<R> currentResult) {
			this.currentResult = currentResult;
		}

		public <RR> IFlowThen<RR> then(Function<Result<R>, CompletionStage<Result<RR>>> actionSupplier)
			throws ExecutionException, InterruptedException {
			CompletionStage<Result<RR>> action = actionSupplier.apply(currentResult);

			Result<RR> result = action.toCompletableFuture().get();

			ResultStatus status = result.getStatus();
			if (status.isCanceled() || status.isDone()) {
				return new FlowStopped<>(status);
			}

			return new FlowThen<>(result);
		}

		@Override
		public <RR> IThenSwitch<R, RR> thenSwitch() throws ExecutionException, InterruptedException {
			ResultStatus status = currentResult.getStatus();
			if (status.isCanceled() || status.isDone()) {
				return new ThenSwitchStopped<>(status);
			}

			return new ThenSwitch<>(currentResult);
		}

		public Result<R> getResult() {
			return currentResult;
		}
	}

	private static class ThenSwitch<T, R> implements IThenSwitch<T, R> {

		private final Result<T> currentResult;
		private final Result<R> switchResult;

		public ThenSwitch(Result<T> currentResult) {
			this(currentResult, null);
		}

		private ThenSwitch(Result<T> currentResult, Result<R> switchResult) {
			this.currentResult = currentResult;
			this.switchResult = switchResult;
		}

		@Override
		public <RR> IThenSwitch<T, RR> when(Function<T, Boolean> condition, SwitchFlow<T, RR> switchFlow)
			throws ExecutionException, InterruptedException {
			if (condition.apply(currentResult.getData())) {
				Result<RR> result = switchFlow.apply(new FlowThen<>(currentResult)).getResult();

				ResultStatus status = result.getStatus();
				if (status.isCanceled() || status.isDone()) {
					return new ThenSwitchStopped<>(status);
				}

				return new ThenSwitch<>(currentResult, result);
			}

			return new ThenSwitch(currentResult, switchResult);
		}

		@Override
		public <RR> IFlowThen<RR> then(Function<Result<R>, CompletionStage<Result<RR>>> actionSupplier)
			throws ExecutionException, InterruptedException {
			return new FlowThen<>(switchResult).then(actionSupplier);
		}
	}

	private static class FlowStopped<R> implements IFlowThen<R> {

		private final ResultStatus status;

		public FlowStopped(ResultStatus status) {
			if (!status.isCanceled() && !status.isDone()) {
				throw new IllegalArgumentException("FlowCanceled should be created only with canceled or done status");
			}

			this.status = status;
		}

		@Override
		public <RR> IFlowThen<RR> then(Function<Result<R>, CompletionStage<Result<RR>>> actionSupplier)
			throws ExecutionException, InterruptedException {
			return new FlowStopped<>(status);
		}

		@Override
		public <RR> IThenSwitch<R, RR> thenSwitch() throws ExecutionException, InterruptedException {
			return new ThenSwitchStopped<>(status);
		}

		@Override
		public Result<R> getResult() {
			return Result.withStatus(status);
		}
	}

	private static class ThenSwitchStopped<T, R> implements IThenSwitch<T, R> {

		private final ResultStatus status;

		public ThenSwitchStopped(ResultStatus status) {
			if (!status.isCanceled() && !status.isDone()) {
				throw new IllegalArgumentException("FlowCanceled should be created only with canceled or done status");
			}

			this.status = status;
		}

		@Override
		public <RR> IThenSwitch<T, RR> when(Function<T, Boolean> condition, SwitchFlow<T, RR> switchFlow) {
			return new ThenSwitchStopped<>(status);
		}

		@Override
		public <RR> IFlowThen<RR> then(Function<Result<R>, CompletionStage<Result<RR>>> actionSupplier)
			throws ExecutionException, InterruptedException {
			return new FlowStopped<>(status);
		}
	}

	interface SwitchFlow<T, R> {

		IFlowThen<R> apply(IFlowThen<T> flow) throws ExecutionException, InterruptedException;
	}

	private static final class PersonAndSimilarEntrySelection {

		private final PersonDto personDto;
		private final SimilarEntriesDto similarEntrySelection;

		public PersonAndSimilarEntrySelection(PersonDto personDto, SimilarEntriesDto similarEntrySelection) {
			this.personDto = personDto;
			this.similarEntrySelection = similarEntrySelection;
		}

		public PersonDto getPersonDto() {
			return personDto;
		}

		public SimilarEntriesDto getSimilarEntrySelection() {
			return similarEntrySelection;
		}
	}
}
