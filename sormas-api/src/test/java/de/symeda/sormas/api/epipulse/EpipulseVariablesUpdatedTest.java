/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.api.epipulse;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.epipulse.EpipulseVariableGenerator.EpipulseMetadata;

/**
 * Checks that the classes {@link EpipulseVariableGenerator} writes still say what the EpiPulse
 * metadata says.
 * 
 */
public class EpipulseVariablesUpdatedTest {

	@Test
	@DisplayName("the generated EpiPulse metadata classes match the metadata CSV")
	public void testGeneratedClassesAreUpdated() throws IOException {

		EpipulseMetadata metadata = EpipulseVariableGenerator.readMetadata();

		List<String> outdated = new ArrayList<>();
		for (EpipulseVariableGenerator generator : EpipulseVariableGenerator.buildConfig()) {

			StringWriter writer = new StringWriter();
			generator.writeClass(writer, "\n", metadata);

			String actual = new String(Files.readAllBytes(Paths.get(generator.getOutputClassFilePath())), StandardCharsets.UTF_8);
			if (!writer.toString().equals(normaliseLineEndings(actual))) {
				outdated.add(generator.getOutputClassFilePath() + " (" + firstDifference(normaliseLineEndings(actual), writer.toString()) + ")");
			}
		}

		if (!outdated.isEmpty()) {
			fail(
				String.format(
					"%d generated class(es) no longer match the EpiPulse metadata: %s. Rerun EpipulseVariableGenerator from the sormas-api module directory.",
					outdated.size(),
					outdated));
		}
	}

	private static String normaliseLineEndings(String content) {
		return content.replace("\r\n", "\n");
	}

	private static String firstDifference(String actual, String expected) {

		String[] actualLines = actual.split("\n", -1);
		String[] expectedLines = expected.split("\n", -1);

		for (int i = 0; i < Math.min(actualLines.length, expectedLines.length); i++) {
			if (!actualLines[i].equals(expectedLines[i])) {
				return String.format("line %d is '%s', expected '%s'", i + 1, actualLines[i], expectedLines[i]);
			}
		}

		return String.format("the file has %d lines, expected %d", actualLines.length, expectedLines.length);
	}
}
