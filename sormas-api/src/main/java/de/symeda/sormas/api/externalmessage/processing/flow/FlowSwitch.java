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

package de.symeda.sormas.api.externalmessage.processing.flow;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Class used for building conditional async flow/chain
 * 
 * @param <T>
 *            Data type of current result
 * @param <X>
 *            Data type of the result of switch
 */
public class FlowSwitch<T, X, O> {

	private final CompletionStage<ProcessingResult<T>> currentResult;
	private final FlowAction<T, O> switchOperation;

	private final CompletionStage<ProcessingResult<X>> switchResult;

	private Function<ProcessingResult<T>, CompletionStage<ProcessingResult<O>>> switchOperationMemo;

	public FlowSwitch(CompletionStage<ProcessingResult<T>> currentResult, FlowAction<T, O> switchOperation) {
		this(currentResult, switchOperation, null, null);
	}

	private FlowSwitch(
		CompletionStage<ProcessingResult<T>> currentResult,
		FlowAction<T, O> switchOperation,
		CompletionStage<ProcessingResult<X>> switchResult,
		Function<ProcessingResult<T>, CompletionStage<ProcessingResult<O>>> switchOperationMemo) {
		this.currentResult = currentResult;
		this.switchOperation = switchOperation;
		this.switchResult = switchResult;
		this.switchOperationMemo = switchOperationMemo;

		if (this.switchOperationMemo == null) {
			// cache the result so not the operation is called only once
			AtomicReference<CompletionStage<ProcessingResult<O>>> switchOperationResult = new AtomicReference<>(null);
			this.switchOperationMemo = cr -> {
				if (switchOperationResult.get() == null) {
					switchOperationResult.set(switchOperation.apply(cr));
				}
				return switchOperationResult.get();
			};
		}
	}

	public <Y> FlowSwitch<T, Y, O> when(Predicate<O> condition, SwitchFlow<T, O, Y> switchFlow) {

		//noinspection unchecked,rawtypes,rawtypes
		return new FlowSwitch<>(currentResult, switchOperation, currentResult.thenCompose(cr -> {
			ProcessingResultStatus currentStatus = cr.getStatus();
			if (currentStatus.isCanceled() || currentStatus.isDone()) {
				//noinspection unchecked
				return ProcessingResult.of(currentStatus, (Y) cr.getData()).asCompletedFuture();
			}

			return switchOperationMemo.apply(cr).thenCompose(or -> {

				ProcessingResultStatus switchStatus = or.getStatus();
				if (switchStatus.isCanceled() || switchStatus.isDone()) {
					//noinspection unchecked
					return ProcessingResult.of(switchStatus, (Y) cr.getData()).asCompletedFuture();
				}

				if (condition.test(or.getData())) {
					return switchFlow.apply(new FlowThen<>(currentResult), or.getData(), cr.getData()).getResult().thenCompose(switchFlowResult -> {
						ProcessingResultStatus switchFlowStatus = switchFlowResult.getStatus();
						if (switchFlowStatus.isCanceled() || switchFlowStatus.isDone()) {
							return ProcessingResult.of(switchFlowStatus, switchFlowResult.getData()).asCompletedFuture();
						}

						return CompletableFuture.completedFuture(switchFlowResult);
					});
				}

				//noinspection rawtypes,unchecked
				return (CompletionStage) switchResult;
			});
		}), switchOperationMemo);
	}

	public <Y> FlowThen<Y> then(FlowAction<X, Y> action) {
		return then(action, null);
	}

	public <Y> FlowThen<Y> then(FlowAction<X, Y> action, FlowAction<X, Y> cancelAction) {
		return new FlowThen<>(switchResult).then(action, cancelAction);
	}

	public interface SwitchFlow<T, O, Y> {

		FlowThen<Y> apply(FlowThen<T> flow, O operationResult, T currentResult);
	}
}
