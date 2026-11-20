package de.symeda.sormas.api.externalpersondata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.AbstractUnitTest;

class ExternalPersonDataForDisplayDtoTest extends AbstractUnitTest {

	private final ExternalPersonDataForDisplayDto victim = new ExternalPersonDataForDisplayDto();

	@Test
	void summaryUsesExternalPersonDataPropertyAndRoundTrips() throws Exception {
		// PREPARE
		ExternalPersonDataSummaryDto summary = new ExternalPersonDataSummaryDto();
		summary.setFirstName("Jane");
		victim.setSummary(summary);
		victim.setHtml("<p>Jane</p>");
		ObjectMapper mapper = new ObjectMapper();

		// EXECUTE
		JsonNode json = mapper.valueToTree(victim);
		ExternalPersonDataForDisplayDto restored = mapper.treeToValue(json, ExternalPersonDataForDisplayDto.class);

		// CHECK
		assertTrue(json.has("summary"));
		assertEquals("Jane", json.get("summary").get("firstName").asText());
		assertEquals(victim, restored);
	}
}
