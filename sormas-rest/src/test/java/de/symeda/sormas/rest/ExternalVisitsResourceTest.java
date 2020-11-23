package de.symeda.sormas.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ExternalVisitsResourceTest extends TestCase {

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void testIfRelevantSwaggerDocumentationIsUnchanged() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();

		//load released and new swagger docu information
		String releasedSwaggerDocuPath = System.getProperty("user.dir");
		releasedSwaggerDocuPath = releasedSwaggerDocuPath + "/src/test/resources/swagger.json";
		String releasedSwaggerDocu = fileToString(releasedSwaggerDocuPath);
		Map<String, Object> releasedSwaggerDocuMap = objectMapper.readValue(releasedSwaggerDocu, new TypeReference<Map<String, Object>>() {
		});

		String newSwaggerDocuPath = System.getProperty("user.dir");
		newSwaggerDocuPath = newSwaggerDocuPath + "/target/test-classes/swagger.json";
		String newSwaggerDocu = fileToString(newSwaggerDocuPath);
		Map<String, Object> newSwaggerDocuMap = objectMapper.readValue(newSwaggerDocu, new TypeReference<Map<String, Object>>() {
		});

		// Check whether path information is equal in new and released swagger docu
		ArrayList releasedControllerList = new ArrayList();
		extractPathsOfController(releasedSwaggerDocuMap, "External Visits Controller", releasedControllerList);

		ArrayList newControllerList = new ArrayList();
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
		ArrayList releasedDetailList = new ArrayList();
		ArrayList newDetailList = new ArrayList();

		for (String name : enumNames) {
			extractDetail(releasedSwaggerDocuMap, name, releasedDetailList);
			extractDetail(newSwaggerDocuMap, name, newDetailList);
		}
		assertEquals(releasedDetailList, newDetailList);
	}

	/**
	 * 
	 * @param topLevelMap
	 *            Nested Map from which to extract the information. It's supposed to be a mapped swagger.json
	 * @param controller
	 *            The name of the controller, e.g. External Visits Controller
	 * @param list
	 *            Extracted information is stored in this list
	 * @return Documentation about any path found for the specified controller (e.g. /visits-external/person/{personUuid} for the External
	 *         Visits Controller). This includes parameter names for that path, but not information about related enums.
	 */
	private static ArrayList extractPathsOfController(Map topLevelMap, String controller, ArrayList list) {
		for (Object firstLayerKey : topLevelMap.keySet()) {
			Object firstLayerValue = topLevelMap.get(firstLayerKey);
			if (firstLayerValue instanceof Map) {
				Map<String, Object> secondLayerMap = (Map<String, Object>) topLevelMap.get(firstLayerKey);
				for (Object secondLayerKey : secondLayerMap.keySet()) {
					Object secondLayerValue = secondLayerMap.get(secondLayerKey);
					if (secondLayerValue instanceof Map) {
						Map<String, Object> thirdLayerMap = (Map<String, Object>) secondLayerMap.get(secondLayerKey);
						for (Object thirdLayerKey : thirdLayerMap.keySet()) {
							// tags are always represented in the third layer and as ArrayLists
							if ("tags".equals(thirdLayerKey) && thirdLayerMap.get(thirdLayerKey) instanceof ArrayList) {
								ArrayList tags = (ArrayList) thirdLayerMap.get(thirdLayerKey);
								if (tags.contains(controller)) {
									list.add(firstLayerKey);
									list.add(topLevelMap.get(firstLayerKey));
								}
							}
						}
					}
				}
				extractPathsOfController((Map) firstLayerValue, controller, list);
			}
		}
		return list;
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
	private static ArrayList extractDetail(Map topLevelMap, String detailName, ArrayList list) {
		for (Object firstLayerKey : topLevelMap.keySet()) {
			Object firstLayerValue = topLevelMap.get(firstLayerKey);
			if (detailName.equals(firstLayerKey)) {
				list.add(detailName);
				list.add(firstLayerValue);
			} else if (firstLayerValue instanceof Map) {
				extractDetail((Map) firstLayerValue, detailName, list);
			}
		}
		return list;
	}

	private static String fileToString(String filePath) {
		StringBuilder contentBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return contentBuilder.toString();
	}

}
