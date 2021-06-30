/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.utils;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.exceptions.CsvValidationException;
import com.opencsv.validators.LineValidator;

/**
 * CSV Line validator which fails whenever a line which starts with {@link #DEFAULT_COMMENT_LINE_PREFIX} is found.
 * Can be used to ignore commented CSV file lines.
 *
 * @author Alex Vidrean
 * @since 14-Sep-20
 */
public class CSVCommentLineValidator implements LineValidator {

	public static final String DEFAULT_COMMENT_LINE_PREFIX = "##";

	public static final String ERROR_MESSAGE = "comment";

	@Override
	public boolean isValid(String line) {
		return StringUtils.isBlank(line) || !StringUtils.startsWith(line, DEFAULT_COMMENT_LINE_PREFIX);
	}

	@Override
	public void validate(String line) throws CsvValidationException {
		if (!isValid(line)) {
			throw new CsvValidationException(ERROR_MESSAGE);
		}
	}
}
