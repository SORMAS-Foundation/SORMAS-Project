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

package de.symeda.sormas.app.core;

/**
 * Created by Orson on 09/01/2018.
 */

public class ElaboratorNotFoundException extends Exception {

	private Enum enumValue;

	public ElaboratorNotFoundException(Enum e) {
		this(e, null);
	}

	public ElaboratorNotFoundException(Enum e, Throwable cause) {
		this(e, "Elaborator not found for enum: " + e.getClass().getName(), cause);
	}

	public ElaboratorNotFoundException(Enum e, String message, Throwable cause) {
		super(message, cause);

		this.enumValue = e;
	}
}
