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

import de.symeda.sormas.api.utils.DataHelper;
import org.apache.commons.lang3.StringUtils;
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

			String diff = getDifference(generator, writer.toString());
			if (!DataHelper.isNullOrEmpty(diff)) {
				invalid.add(generator.getOutputClassFilePath() + " expected: " + diff);
			}
		}
		if (!invalid.isEmpty()) {
			fail(String.format("%d Constants file(s) outdated: %s", invalid.size(), invalid.toString()));
		}
	}

	private String getDifference(I18nConstantGenerator generator, String expectedContent) throws IOException {

		try (BufferedReader reader =
			new BufferedReader(new InputStreamReader(new FileInputStream(new File(generator.getOutputClassFilePath())), StandardCharsets.UTF_8))) {

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}

			String fileContent = sb.toString();

			boolean equal = expectedContent.equals(fileContent);
			if (!equal) {
				return StringUtils.difference(fileContent, expectedContent);
			}
			return null;
		}
	}
}
