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

package de.symeda.sormas.api.sormastosormas;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alex Vidrean
 * @since 25-Jun-21
 */
public abstract class SormasToSormasI18nMessageError extends Exception {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public SormasToSormasI18nMessageError(String message) {
		super(message);
	}

	public SormasToSormasI18nMessageError() {
	}

	public abstract String getI18nTag();

	public abstract Object[] getArgs();

	protected abstract String getHumanMessageUnsafe() throws Exception;

	public String getHumanMessage() {
		try {
			return getHumanMessageUnsafe();
		} catch (Exception e) {
			String error =
				String.format("Formatting the human readable message failed. I18nTag: %s, arguments: %s", getI18nTag(), Arrays.toString(getArgs()));
			logger.error(error);
			return error;
		}
	}
}
