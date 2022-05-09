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

import java.util.concurrent.CompletionStage;

/**
 * Class used for initializing and building async flow/chain
 * 
 * @param <R>
 */
public class FlowThen<R> {

	private final CompletionStage<ProcessingResult<R>> currentResult;

	public FlowThen() {
		this(ProcessingResult.<R> continueWith(null).asCompletedFuture());
	}

	public FlowThen(CompletionStage<ProcessingResult<R>> currentResult) {
		this.currentResult = currentResult;
	}

	public <RR> FlowThen<RR> then(FlowAction<R, RR> action) {
		return new FlowThen<>(currentResult.thenCompose((r) -> {
			ProcessingResultStatus status = r.getStatus();
			if (status.isCanceled() || status.isDone()) {
				//noinspection unchecked
				return ProcessingResult.of(status, (RR) r.getData()).asCompletedFuture();
			}

			return action.apply(r);
		}));
	}

	public <RR> FlowSwitch<R, RR> thenSwitch() {
		return new FlowSwitch<>(currentResult);
	}

	public CompletionStage<ProcessingResult<R>> getResult() {
		return currentResult;
	}
}
