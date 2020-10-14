package de.symeda.sormas.api.i18n;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class I18nConstantsUpdatedTest {

	/**
	 * Checks whether the by {@link I18nConstantGenerator} generated Constants classes are in sync with the properties files.
	 */
	@Test
	public void testConstantsAreUpdated() throws IOException {

		List<I18nConstantGenerator> config = I18nConstantGenerator.buildConfig();
		List<String> invalid = new ArrayList<>();
		for (I18nConstantGenerator generator : config) {
			StringWriter writer = new StringWriter();
			generator.writeI18nConstantClass(writer, "\n");

			if (!isValidContent(generator, writer.toString())) {
				invalid.add(generator.getOutputClassFilePath());
			}
		}
		if (!invalid.isEmpty()) {
			fail(String.format("%d Constants file(s) outdated: %s", invalid.size(), invalid.toString()));
		}
	}

	private boolean isValidContent(I18nConstantGenerator generator, String expectedContent) throws IOException {

		try (BufferedReader reader =
			new BufferedReader(new InputStreamReader(new FileInputStream(new File(generator.getOutputClassFilePath())), StandardCharsets.UTF_8))) {

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}

			String fileContent = sb.toString();

			// For debugging to check where the difference is if needed
//			assertEquals(generator.getOutputClassName(), expectedContent, fileContent);
			return expectedContent.equals(fileContent);
		}
	}
}
