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
import java.util.function.Predicate;

/**
 * Class used for building conditional async flow/chain
 * 
 * @param <T>
 *            Data type of current result
 * @param <X>
 *            Data type of the result of switch
 */
public class FlowSwitch<T, X> {

	private final CompletionStage<ProcessingResult<T>> currentResult;
	private final CompletionStage<ProcessingResult<X>> switchResult;

	public FlowSwitch(CompletionStage<ProcessingResult<T>> currentResult) {
		this(currentResult, null);
	}

	private FlowSwitch(CompletionStage<ProcessingResult<T>> currentResult, CompletionStage<ProcessingResult<X>> switchResult) {
		this.currentResult = currentResult;
		this.switchResult = switchResult;
	}

	public <Y> FlowSwitch<T, Y> when(Predicate<T> condition, SwitchFlow<T, Y> switchFlow) {

		//noinspection unchecked,rawtypes,rawtypes
		return new FlowSwitch<>(currentResult, currentResult.thenCompose(r -> {
			ProcessingResultStatus status = r.getStatus();
			if (status.isCanceled() || status.isDone()) {
				//noinspection unchecked
				return ProcessingResult.of(status, (Y) r.getData()).asCompletedFuture();
			}

			if (condition.test(r.getData())) {
				return switchFlow.apply(new FlowThen<>(currentResult), r.getData()).getResult().thenCompose(switchFlowResult -> {
					ProcessingResultStatus switchStatus = switchFlowResult.getStatus();
					if (switchStatus.isCanceled() || switchStatus.isDone()) {
						return ProcessingResult.of(switchStatus, switchFlowResult.getData()).asCompletedFuture();
					}

					return CompletableFuture.completedFuture(switchFlowResult);
				});
			}

			//noinspection rawtypes,unchecked,unchecked
			return (CompletionStage) switchResult;
		}));
	}

	public <Y> FlowThen<Y> then(FlowAction<X, Y> action) {
		return new FlowThen<>(switchResult).then(action);
	}

	public interface SwitchFlow<T, Y> {

		FlowThen<Y> apply(FlowThen<T> flow, T currentResult);
	}
}
