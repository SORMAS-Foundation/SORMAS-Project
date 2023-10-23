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

import java.util.concurrent.CompletionStage;

/**
 * Class used for initializing and building async flow/chain
 * 
 * @param <T>
 */
public class FlowThen<T> {

	private final CompletionStage<ProcessingResult<T>> currentResult;

	public FlowThen() {
		this(ProcessingResult.<T> continueWith(null).asCompletedFuture());
	}

	public FlowThen(CompletionStage<ProcessingResult<T>> currentResult) {
		this.currentResult = currentResult;
	}

	public <R> FlowThen<R> then(FlowAction<T, R> action) {
		return then(action, null);
	}

	public <R> FlowThen<R> then(FlowAction<T, R> action, FlowAction<T, R> cancelAction) {
		//noinspection unchecked
		FlowAction<T, R> cancelActionNullSafe =
			cancelAction != null ? cancelAction : r -> ProcessingResult.of(r.getStatus(), (R) r.getData()).asCompletedFuture();

		return new FlowThen<>(currentResult.thenCompose(r -> {
			ProcessingResultStatus status = r.getStatus();
			if (status.isCanceled()) {
				return cancelActionNullSafe.apply(r);
			} else if (status.isDone()) {
				return ProcessingResult.of(status, (R) r.getData()).asCompletedFuture();
			}

			return action.apply(r);
		}));
	}

	public <R, O> FlowSwitch<T, R, O> thenSwitch(FlowAction<T, O> operation) {
		return new FlowSwitch<>(currentResult, operation);
	}

	public CompletionStage<ProcessingResult<T>> getResult() {
		return currentResult;
	}
}
