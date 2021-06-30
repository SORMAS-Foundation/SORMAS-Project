/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.api.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.validators.LineValidator;

public final class CSVUtils {

	private CSVUtils() {
		// Hide Utility Class Constructor
	}

	public static CSVReader createCSVReader(Reader reader, char separator, LineValidator ...lineValidators) {
		final CSVReaderBuilder builder = new CSVReaderBuilder(reader).withCSVParser(new CSVParserBuilder().withSeparator(separator).build());
		if (ArrayUtils.isNotEmpty(lineValidators)) {
			for(LineValidator lineValidator: lineValidators) {
				builder.withLineValidator(lineValidator);
			}
		}
		return builder.build();
	}

	public static CSVWriter createCSVWriter(Writer writer, char separator) {
		return new SafeCSVWriter(writer, separator, CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
	}

	/**
	 * Extension of the {@link CSVWriter} which sanitizes each element of the CSV to prevent CSV Injection.
	 * Implementation based on version 5.3 of opencsv.
	 * 
	 * @see <a href="https://owasp.org/www-community/attacks/CSV_Injection">CSV Injection</a>
	 */
	public static class SafeCSVWriter extends CSVWriter {

		private static final String FORMULA_PREFIX = "'";

		private final Pattern formulaPattern = Pattern.compile("(?s)^[+=@-].*", Pattern.MULTILINE);

		public SafeCSVWriter(Writer writer, char separator, char quotechar, char escapechar, String lineEnd) {
			super(writer, separator, quotechar, escapechar, lineEnd);
		}

		@Override
		public void writeNext(String[] nextLine, boolean applyQuotesToAll, Appendable appendable) throws IOException {
			if (nextLine == null) {
				return;
			}

			for (int i = 0; i < nextLine.length; i++) {

				if (i != 0) {
					appendable.append(separator);
				}

				String nextElement = nextLine[i];

				if (nextElement == null) {
					continue;
				}

				Boolean stringContainsSpecialCharacters = stringContainsSpecialCharacters(nextElement);

				appendQuoteCharacterIfNeeded(applyQuotesToAll, appendable, stringContainsSpecialCharacters);

				// begin code change from parent code
				if(formulaPattern.matcher(nextElement).matches()) {
					appendable.append(FORMULA_PREFIX);
				}
				// end

				if (stringContainsSpecialCharacters) {
					processLine(nextElement, appendable);
				} else {
					appendable.append(nextElement);
				}

				appendQuoteCharacterIfNeeded(applyQuotesToAll, appendable, stringContainsSpecialCharacters);
			}

			appendable.append(lineEnd);
			writer.write(appendable.toString());
		}

		// Copied from the parent class since it's access modifier was private
		private void appendQuoteCharacterIfNeeded(boolean applyQuotesToAll, Appendable appendable, Boolean stringContainsSpecialCharacters) throws IOException {
			if ((applyQuotesToAll || stringContainsSpecialCharacters) && quotechar != NO_QUOTE_CHARACTER) {
				appendable.append(quotechar);
			}
		}
	}
}
