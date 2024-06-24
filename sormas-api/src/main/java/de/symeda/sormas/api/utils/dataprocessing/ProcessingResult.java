/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.utils.dataprocessing;

import static java.util.concurrent.CompletableFuture.completedFuture;

import java.util.concurrent.CompletableFuture;

public class ProcessingResult<T> {

	private final ProcessingResultStatus status;

	private final T data;

	public static <T> ProcessingResult<T> withStatus(ProcessingResultStatus status, T data) {
		return new ProcessingResult<>(status, data);
	}

	public static <T> ProcessingResult<T> continueWith(T data) {
		return new ProcessingResult<>(ProcessingResultStatus.CONTINUE, data);
	}

	public static <T> ProcessingResult<T> of(ProcessingResultStatus status, T data) {
		return new ProcessingResult<>(status, data);
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

	public CompletableFuture<ProcessingResult<T>> asCompletedFuture() {
		return completedFuture(this);
	}
}
