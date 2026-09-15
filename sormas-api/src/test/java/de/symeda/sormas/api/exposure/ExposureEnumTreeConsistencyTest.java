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

package de.symeda.sormas.api.exposure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

class ExposureEnumTreeConsistencyTest {

	private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	@Test
	void exposureEnumTreeShouldMatchSnapshot() throws IOException {
		Map<String, Object> expectedTree = ExposureEnumTreeSnapshotSupport.buildTreeFromEnums();
		JsonNode expectedNode = MAPPER.valueToTree(expectedTree);

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(ExposureEnumTreeSnapshotSupport.SNAPSHOT_CLASSPATH);
		assertNotNull(inputStream, "Missing snapshot resource: " + ExposureEnumTreeSnapshotSupport.SNAPSHOT_CLASSPATH);
		JsonNode snapshotNode;
		try (InputStream closableInputStream = inputStream) {
			snapshotNode = MAPPER.readTree(closableInputStream);
		}

		assertEquals(
			expectedNode,
			snapshotNode,
			() -> "Exposure enum tree snapshot is out of date.\n" + "If enum relations were changed intentionally, regenerate "
				+ ExposureEnumTreeSnapshotSupport.SNAPSHOT_CLASSPATH + " from the current enums.\n\nExpected:\n" + toPrettyJson(expectedNode)
				+ "\n\nActual:\n" + toPrettyJson(snapshotNode));
	}

	private static String toPrettyJson(JsonNode node) {
		try {
			return MAPPER.writeValueAsString(node);
		} catch (IOException e) {
			return String.valueOf(node);
		}
	}
}
