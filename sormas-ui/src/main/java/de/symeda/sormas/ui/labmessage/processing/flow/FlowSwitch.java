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

package de.symeda.sormas.ui.labmessage.processing.flow;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class FlowSwitch<T, R> {

	private final CompletionStage<ProcessingResult<T>> currentResult;
	private final CompletionStage<ProcessingResult<R>> switchResult;

	public FlowSwitch(CompletionStage<ProcessingResult<T>> currentResult) {
		this(currentResult, null);
	}

	private FlowSwitch(CompletionStage<ProcessingResult<T>> currentResult, CompletionStage<ProcessingResult<R>> switchResult) {
		this.currentResult = currentResult;
		this.switchResult = switchResult;
	}

	public <RR> FlowSwitch<T, RR> when(Function<T, Boolean> condition, SwitchFlow<T, RR> switchFlow) {

		//noinspection unchecked,rawtypes,rawtypes
		return new FlowSwitch<>(currentResult, currentResult.thenCompose(r -> {
			ProcessingResultStatus status = r.getStatus();
			if (status.isCanceled() || status.isDone()) {
				//noinspection unchecked
				return ProcessingResult.completed(status, (RR)r.getData());
			}

			if (condition.apply(r.getData())) {
				return switchFlow.apply(new FlowThen<>(currentResult), r.getData()).getResult().thenCompose(switchResult -> {
					ProcessingResultStatus switchStatus = switchResult.getStatus();
					if (switchStatus.isCanceled() || switchStatus.isDone()) {
						return ProcessingResult.completed(switchStatus, switchResult.getData());
					}

					return CompletableFuture.completedFuture(switchResult);
				});
			}

			//noinspection rawtypes,unchecked,unchecked
			return (CompletionStage) switchResult;
		}));
	}

	public <RR> FlowThen<RR> then(FlowAction<R, RR> action) {
		return new FlowThen<>(switchResult).then(action);
	}

	public interface SwitchFlow<T, R> {

		FlowThen<R> apply(FlowThen<T> flow, T currentResult);
	}
}
