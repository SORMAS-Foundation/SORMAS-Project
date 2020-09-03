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
 * Created by Orson on 07/12/2017.
 */

public class NotImplementedException extends UnsupportedOperationException {

	private static final long serialVersionUID = 20131021L;
	private final String code;

	public NotImplementedException(final String message) {
		this(message, (String) null);
	}

	public NotImplementedException(final Throwable cause) {
		this(cause, null);
	}

	public NotImplementedException(final String message, final Throwable cause) {
		this(message, cause, null);
	}

	public NotImplementedException(final String message, final String code) {
		super(message);
		this.code = code;
	}

	public NotImplementedException(final Throwable cause, final String code) {
		super(cause);
		this.code = code;
	}

	public NotImplementedException(final String message, final Throwable cause, final String code) {
		super(message, cause);
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}
}
