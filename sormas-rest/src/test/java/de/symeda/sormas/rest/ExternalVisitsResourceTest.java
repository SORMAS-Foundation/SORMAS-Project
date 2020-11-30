package de.symeda.sormas.rest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExternalVisitsResourceTest {

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void testIfRelevantSwaggerDocumentationIsUnchanged() throws IOException {

		//load released and new swagger docu information
		Map<String, Object> releasedSwaggerDocuMap = loadJson("./src/test/resources/swagger.json");

		Map<String, Object> newSwaggerDocuMap = loadJson("./target/test-classes/swagger.json");

		// Check whether path information is equal in new and released swagger docu
		ArrayList<Object> releasedControllerList = new ArrayList<>();
		extractPathsOfController(releasedSwaggerDocuMap, "External Visits Controller", releasedControllerList);

		ArrayList<Object> newControllerList = new ArrayList<>();
		extractPathsOfController(newSwaggerDocuMap, "External Visits Controller", newControllerList);

		assertEquals(releasedControllerList, newControllerList);

		// Check whether related enum information is equal
		List<String> enumNames = Arrays.asList(
			"JournalPersonDto",
			"PersonSymptomJournalStatusDto",
			"SymptomJournalStatus",
			"ExternalVisitDto",
			"Disease",
			"VisitStatus",
			"SymptomsDto",
			"SymptomState",
			"YesNoUnknown",
			"TemperatureSource");

		for (String name : enumNames) {
			ArrayList<Object> releasedDetailList = new ArrayList<>();
			ArrayList<Object> newDetailList = new ArrayList<>();
			extractDetail(releasedSwaggerDocuMap, name, releasedDetailList);
			extractDetail(newSwaggerDocuMap, name, newDetailList);

			assertEquals("", releasedDetailList, newDetailList);
		}
	}

	/**
	 * 
	 * @param level1
	 *            Nested Map from which to extract the information. It's supposed to be a mapped swagger.json
	 * @param controller
	 *            The name of the controller, e.g. External Visits Controller
	 * @param list
	 *            Extracted information is stored in this list
	 * @return Documentation about any path found for the specified controller (e.g. /visits-external/person/{personUuid} for the External
	 *         Visits Controller). This includes parameter names for that path, but not information about related enums.
	 */
	private static void extractPathsOfController(Map<String, Object> level1, String controller, ArrayList<Object> list) {
		level1.entrySet().forEach(e1 -> {
			String key1 = e1.getKey();
			Object value1 = e1.getValue();
			if (isInnerNode(value1)) {
				Map<String, Object> level2 = innerNode(value1);
				if (hasTag(level2, controller)) {
					list.add(key1);
					list.add(value1);
				}
				extractPathsOfController(level2, controller, list);
			}
		});
	}

	private static boolean hasTag(Map<String, Object> level2, String controller) {
		return level2.values()
			.stream()
			.filter(ExternalVisitsResourceTest::isInnerNode)
			.map(ExternalVisitsResourceTest::innerNode)
			// tags are always represented in the third layer and as ArrayLists
			.map(ExternalVisitsResourceTest::tags)
			.filter(t -> t.contains(controller))
			.findFirst()
			.isPresent();
	}

	@SuppressWarnings("unchecked")
	private static List<Object> tags(Map<String, Object> innerNode) {
		Object tags = innerNode.get("tags");
		if (tags instanceof List) {
			return (List<Object>) tags;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * 
	 * @param topLevelMap
	 *            Nested Map from which to extract the information. It's supposed to be a mapped swagger.json
	 * @param detailName
	 *            The name of the detail, e.g. JournalPersonDto.
	 * @param list
	 *            Extracted information is stored in this list
	 * @return
	 *         Documentation found about the detail. The Map is searched for a key equal to detailName, an it, plus the according value is
	 *         added to the list.
	 */
	private static void extractDetail(Map<String, Object> level1, String detailName, ArrayList<Object> list) {
		level1.entrySet().stream().forEach(e1 -> {
			Object value1 = e1.getValue();
			if (detailName.equals(e1.getKey())) {
				list.add(detailName);
				list.add(value1);
			} else if (isInnerNode(value1)) {
				extractDetail(innerNode(value1), detailName, list);
			}
		});
	}

	private static boolean isInnerNode(Object node) {
		return node instanceof Map;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> innerNode(Object node) {
		return (Map<String, Object>) node;
	}

	private static Map<String, Object> loadJson(String relativePath) throws IOException {

		Path path = Paths.get(System.getProperty("user.dir"), relativePath);

		ObjectMapper objectMapper = new ObjectMapper();

		try (Reader rd = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			return objectMapper.readValue(rd, new TypeReference<Map<String, Object>>() {
			});
		}
	}

}
