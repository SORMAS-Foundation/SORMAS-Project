package de.symeda.sormas.rest.externaljournal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
		Map<String, Object> releasedSwaggerDocuMap = loadJson("src/test/resources/external_journal_API.json");

		Map<String, Object> newSwaggerDocuMap = loadJson("target/external_journal_API.json");

		assertThat(newSwaggerDocuMap, equalTo(releasedSwaggerDocuMap));

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
