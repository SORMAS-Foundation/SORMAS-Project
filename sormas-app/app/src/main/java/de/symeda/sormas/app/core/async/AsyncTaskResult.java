/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.core.async;

import de.symeda.sormas.app.core.BoolResult;

public class AsyncTaskResult<T> {

	private T result;
	private Exception error;
	private BoolResult resultStatus;

	public T getResult() {
		return result;
	}

	public Exception getError() {
		return error;
	}

	public BoolResult getResultStatus() {
		return resultStatus;
	}

	public AsyncTaskResult(BoolResult resultStatus, T result) {
		super();
		this.result = result;
		this.resultStatus = resultStatus;
	}

	public AsyncTaskResult(Exception error) {
		super();
		this.error = error;
		this.resultStatus = new BoolResult(false, error.getMessage());
	}
}
