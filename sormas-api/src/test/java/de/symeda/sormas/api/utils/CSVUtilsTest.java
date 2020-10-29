/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.utils;

import com.opencsv.CSVWriter;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * @author Alex Vidrean
 * @since 26-Oct-20
 */
public class CSVUtilsTest {

	@Test
	public void testCSVFormulaInjectionPreventionSingleLine() throws IOException {

		String[] line = new String[] {
			"text",
			"=today()",
			"=1+1",
			"+2-1",
			"-1-1",
			"@sum(1+9)" };

		StringWriter sw = new StringWriter();
		BufferedWriter bw = new BufferedWriter(sw);

		CSVWriter csvWriter = CSVUtils.createCSVWriter(bw, ',');
		csvWriter.writeNext(line);
		csvWriter.flush();

		String string = sw.toString();
		String expectedString = "\"text\",\"'=today()\",\"'=1+1\",\"'+2-1\",\"'-1-1\",\"'@sum(1+9)\"" + CSVWriter.DEFAULT_LINE_END;

		assertEquals(expectedString, string);

	}

	@Test
	public void testCSVFormulaInjectionMultipleLines() throws IOException {

		String[] firstLine = new String[] {
			"1+2",
			"now=today",
			"to-from",
			"test@email.com" };
		String[] secondLine = new String[] {
			"=DATE(1,1,1)",
			"@VALUE(\"2\")",
			"+CONCAT(\"test\",\n\"test2\")",
			"-5*5" };

		StringWriter sw = new StringWriter();
		BufferedWriter bw = new BufferedWriter(sw);

		CSVWriter csvWriter = CSVUtils.createCSVWriter(bw, ',');
		csvWriter.writeNext(firstLine);
		csvWriter.writeNext(secondLine);
		csvWriter.flush();

		String string = sw.toString();
		String expectedString = "\"1+2\",\"now=today\",\"to-from\",\"test@email.com\"" + CSVWriter.DEFAULT_LINE_END
			+ "\"'=DATE(1,1,1)\",\"'@VALUE(\"\"2\"\")\",\"'+CONCAT(\"\"test\"\",\n\"\"test2\"\")\",\"'-5*5\"" + CSVWriter.DEFAULT_LINE_END;

		assertEquals(expectedString, string);
	}

}
