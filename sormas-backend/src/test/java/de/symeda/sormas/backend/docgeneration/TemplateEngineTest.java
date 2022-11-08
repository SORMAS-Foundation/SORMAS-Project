/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.docgeneration;

import static de.symeda.sormas.backend.docgeneration.TemplateTestUtil.cleanLineSeparators;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.velocity.runtime.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import fr.opensagres.xdocreport.core.XDocReportException;

public class TemplateEngineTest {

	private static final Pattern OBJECT_PROPERTY_PATTERN = Pattern.compile("^ *[(] *([\\[]?[A-Za-z0-9.]+;?) *[)] *([{\\[].*[}\\]]) *$");

	private TemplateEngine templateEngine;

	@BeforeEach
	public void setup() {
		templateEngine = new TemplateEngine();
	}

	@Test
	public void genericTestCasesDocxTest()
		throws IOException, XDocReportException, ClassNotFoundException, ParseException, URISyntaxException, DocumentTemplateException {
		genericTestCases(getTestCaseRunnerDocx());
	}

	@Test
	public void genericTestCasesTxtTest()
		throws IOException, XDocReportException, ClassNotFoundException, ParseException, URISyntaxException, DocumentTemplateException {
		genericTestCases(getTestCaseRunnerTxt());
	}

	private void genericTestCases(TestCaseRunner testCaseRunner)
		throws IOException, XDocReportException, ParseException, ClassNotFoundException, URISyntaxException, DocumentTemplateException {
		File testCasesDir = new File(getClass().getResource(testCaseRunner.getTestCasesDirPath()).toURI());
		File[] testCases = testCasesDir.listFiles((d, name) -> name.endsWith(testCaseRunner.getTestCaseExtension()));
		assertNotNull(testCases);

		for (File testCase : testCases) {
			String testcaseBasename = FilenameUtils.getBaseName(testCase.getName());

			URL testcaseProperties = getClass().getResource(testCaseRunner.getTestCasesDirPath() + File.separator + testcaseBasename + ".properties");
			URL testcaseVariables = getClass().getResource(testCaseRunner.getTestCasesDirPath() + File.separator + testcaseBasename + ".vars");
			URL testcaseCmpText = getClass().getResource(testCaseRunner.getTestCasesDirPath() + File.separator + testcaseBasename + ".cmp");

			if (testcaseProperties != null || testcaseVariables != null) {
				Set<String> variables = testCaseRunner.extractTemplateVariables(testCase);

				Properties variablesExpected = new Properties();
				variablesExpected
					.load(new FileInputStream(new File(testcaseVariables != null ? testcaseVariables.toURI() : testcaseProperties.toURI())));

				assertEquals(
					variablesExpected.stringPropertyNames().size(),
					variables.size(),
					testcaseBasename + ": extracted [" + String.join(", ", variables) + "]");
				for (String key : variablesExpected.stringPropertyNames()) {
					assertTrue(
						variables.contains(key),
						testcaseBasename + ": Variable " + key + " not extracted. Found: [" + String.join(", ", variables) + "]");
				}

				if (testcaseProperties != null && testcaseCmpText != null) {
					Properties properties = new Properties();
					properties.load(new FileInputStream(new File(testcaseProperties.toURI())));
					deserializeObjects(properties);
					String testCaseText = cleanLineSeparators(testCaseRunner.getGeneratedText(testCase, properties));

					String expected = getComparisonText(new File(testcaseCmpText.toURI()));
					assertEquals(expected, testCaseText, testcaseBasename + ": generated text does not match.");
				}
			} else {
				System.out.println("No file " + testcaseBasename + ".vars or " + testcaseBasename + ".properties found");
			}
			System.out.println("Testcase completed: " + testcaseBasename + testCaseRunner.getTestCaseExtension());
		}
	}

	private void deserializeObjects(Properties properties) throws ClassNotFoundException, IOException {
		for (String key : properties.stringPropertyNames()) {
			String property = properties.getProperty(key);
			if (property != null) {
				Matcher matcher = OBJECT_PROPERTY_PATTERN.matcher(property);
				if (matcher.matches()) {
					Class<?> clazz = Class.forName(matcher.group(1));
					String json = matcher.group(2);
					Object object = new ObjectMapper().readValue(json, clazz);
					properties.put(key, object);
				}
			}
		}
	}

	private interface TestCaseRunner {

		String getTestCasesDirPath();

		String getTestCaseExtension();

		Set<String> extractTemplateVariables(File testCase) throws IOException, XDocReportException, ParseException, DocumentTemplateException;

		String getGeneratedText(File testCase, Properties properties) throws IOException, XDocReportException, DocumentTemplateException;
	}

	private TestCaseRunner getTestCaseRunnerDocx() {
		return new TestCaseRunner() {

			@Override
			public String getTestCasesDirPath() {
				return "/docgeneration/testcasesDocx";
			}

			@Override
			public String getTestCaseExtension() {
				return ".docx";
			}

			@Override
			public Set<String> extractTemplateVariables(File testCase) throws DocumentTemplateException {
				return templateEngine.extractTemplateVariablesDocx(testCase).getVariables();
			}

			@Override
			public String getGeneratedText(File testCase, Properties properties) throws IOException, DocumentTemplateException {
				InputStream generatedFile = new ByteArrayInputStream(templateEngine.generateDocumentDocx(properties, testCase));
				XWPFDocument generatedDocument = new XWPFDocument(generatedFile);
				XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(generatedDocument);
				String generatedText = xwpfWordExtractor.getText();
				xwpfWordExtractor.close();
				return generatedText;
			}
		};
	}

	private TestCaseRunner getTestCaseRunnerTxt() {
		return new TestCaseRunner() {

			@Override
			public String getTestCasesDirPath() {
				return "/docgeneration/testcasesTxt";
			}

			@Override
			public String getTestCaseExtension() {
				return ".txt";
			}

			@Override
			public Set<String> extractTemplateVariables(File testCase) throws DocumentTemplateException {
				return templateEngine.extractTemplateVariablesTxt(testCase).getVariables();
			}

			@Override
			public String getGeneratedText(File testCase, Properties properties) {
				return templateEngine.generateDocumentTxt(properties, testCase);
			}
		};
	}

	private String getComparisonText(File testcaseCmpText) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(new FileInputStream(testcaseCmpText), writer, "UTF-8");
		return cleanLineSeparators(writer.toString());
	}
}
