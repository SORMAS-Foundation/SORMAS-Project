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

package de.symeda.sormas.app.component;

/**
 * Created by Orson on 02/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class InvalidValueException extends Exception {

	private Object value;

	public InvalidValueException(Object e) {
		this(e, (Throwable) null);
	}

	public InvalidValueException(Object e, String message) {
		this(e, message, null);
	}

	public InvalidValueException(Object e, Throwable cause) {
		this(e, "Field value is not valid: " + e.getClass().getName(), cause);
	}

	public InvalidValueException(Object e, String message, Throwable cause) {
		super(message, cause);

		this.value = e;
	}
}
