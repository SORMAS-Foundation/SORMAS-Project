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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.velocity.runtime.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.opensagres.xdocreport.core.XDocReportException;

public class TemplateEngineTest {

	private static final Pattern OBJECT_PROPERTY_PATTERN = Pattern.compile("^ *[(] *([\\[]?[A-Za-z0-9.]+;?) *[)] *([{\\[].*[}\\]]) *$");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	private TemplateEngine templateEngine;

	@Before
	public void setup() {
		templateEngine = new TemplateEngine();
	}

	@Test
	public void genericTestCasesDocxTest() throws IOException, XDocReportException, ClassNotFoundException, ParseException, URISyntaxException {
		genericTestCases(getTestCaseRunnerDocx());
	}

	@Test
	public void genericTestCasesTxtTest() throws IOException, XDocReportException, ClassNotFoundException, ParseException, URISyntaxException {
		genericTestCases(getTestCaseRunnerTxt());
	}

	private void genericTestCases(TestCaseRunner testCaseRunner)
		throws IOException, XDocReportException, ParseException, ClassNotFoundException, URISyntaxException {
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
					testcaseBasename + ": extracted [" + String.join(", ", variables) + "]",
					variablesExpected.stringPropertyNames().size(),
					variables.size());
				for (String key : variablesExpected.stringPropertyNames()) {
					assertTrue(
						testcaseBasename + ": Variable " + key + " not extracted. Found: [" + String.join(", ", variables) + "]",
						variables.contains(key));
				}

				if (testcaseProperties != null && testcaseCmpText != null) {
					Properties properties = new Properties();
					properties.load(new FileInputStream(new File(testcaseProperties.toURI())));
					deserializeObjects(properties);
					String testCaseText = testCaseRunner.getGeneratedText(testCase, properties);

					String expected = getComparisonText(new File(testcaseCmpText.toURI()));
					assertEquals(testcaseBasename + ": generated text does not match.", expected, testCaseText);
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

		Set<String> extractTemplateVariables(File testCase) throws IOException, XDocReportException, ParseException;

		String getGeneratedText(File testCase, Properties properties) throws IOException, XDocReportException;
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
			public Set<String> extractTemplateVariables(File testCase) throws IOException, XDocReportException {
				return templateEngine.extractTemplateVariablesDocx(testCase).getVariables();
			}

			@Override
			public String getGeneratedText(File testCase, Properties properties) throws IOException, XDocReportException {
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
			public Set<String> extractTemplateVariables(File testCase) throws IOException, ParseException {
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
		return writer.toString().replaceAll("\\r\\n?", "\n");
	}
}
