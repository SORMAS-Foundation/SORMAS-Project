/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.backend.climate;

/**
 * A provider call that did not return data. The message never contains the API key.
 */
public class ClimateProviderException extends Exception {

	private static final long serialVersionUID = -2187364105874429061L;

	public enum Reason {

		/** The account's daily call limit is reached; no further calls succeed until it resets. */
		LIMIT_REACHED,
		/** The key is missing, invalid or has no subscription for the endpoint; no call will succeed. */
		ACCESS_DENIED,
		/** The provider refused this location's request; other locations may still work. */
		LOCATION_REFUSED,
		/** The provider could not be reached or failed, also after retries. */
		UNAVAILABLE
	}

	private final Reason reason;
	private final int calls;

	public ClimateProviderException(Reason reason, String message, int calls) {
		super(message);
		this.reason = reason;
		this.calls = calls;
	}

	public Reason getReason() {
		return reason;
	}

	/**
	 * @return the requests sent before the call gave up, retries included
	 */
	public int getCalls() {
		return calls;
	}
}
