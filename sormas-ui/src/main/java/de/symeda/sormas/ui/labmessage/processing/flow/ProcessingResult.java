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

package de.symeda.sormas.ui.labmessage.processing.flow;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class ProcessingResult<T> {

	private final ProcessingResultStatus status;

	private final T data;

	public static <TT> CompletableFuture<ProcessingResult<TT>> completedStatus(ProcessingResultStatus status) {
		return completedFuture(withStatus(status));
	}

	public static CompletableFuture<ProcessingResult<Void>> completedContinue() {
		return completedFuture(withStatus(ProcessingResultStatus.CONTINUE));
	}

	public static <TT> CompletableFuture<ProcessingResult<TT>> completedContinue(TT data) {
		return completedFuture(continueWith(data));
	}

	public static <TT> CompletableFuture<ProcessingResult<TT>> completed(ProcessingResultStatus status, TT data) {
		return completedFuture(new ProcessingResult<>(status, data));
	}

	public static <TT> ProcessingResult<TT> withStatus(ProcessingResultStatus status) {
		return new ProcessingResult<>(status, null);
	}

	public static <TT> ProcessingResult<TT> continueWith(TT data) {
		return new ProcessingResult<>(ProcessingResultStatus.CONTINUE, data);
	}

	private ProcessingResult(ProcessingResultStatus status, T data) {
		this.status = status;
		this.data = data;
	}

	public ProcessingResultStatus getStatus() {
		return status;
	}

	public T getData() {
		return data;
	}
}
