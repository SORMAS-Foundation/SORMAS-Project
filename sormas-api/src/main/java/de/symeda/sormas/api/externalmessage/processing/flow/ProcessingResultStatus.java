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

public enum ProcessingResultStatus {

	CONTINUE(false, false),
	CANCELED(true, false),
	CANCELED_WITH_CORRECTIONS(true, false),
	DONE(false, true);

	private final boolean canceled;
	private final boolean done;

	ProcessingResultStatus(boolean canceled, boolean done) {
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
