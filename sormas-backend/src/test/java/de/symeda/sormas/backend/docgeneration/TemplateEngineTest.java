package de.symeda.sormas.backend.docgeneration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.person.PersonDto;
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
	public void genericTestCasesDocxTest() throws IOException, XDocReportException, ClassNotFoundException, ParseException {
		genericTestCases(getTestCaseRunnerDocx());
	}

	@Test
	public void genericTestCasesTxtTest() throws IOException, XDocReportException, ClassNotFoundException, ParseException {
		genericTestCases(getTestCaseRunnerTxt());
	}

	private void genericTestCases(TestCaseRunner testCaseRunner) throws IOException, XDocReportException, ParseException, ClassNotFoundException {
		File testCasesDir = new File(testCaseRunner.getTestCasesDirPath());
		File[] testCasesDocx = testCasesDir.listFiles((d, name) -> name.endsWith(testCaseRunner.getTestCaseExtension()));

		for (File testCaseDocx : testCasesDocx) {
			String testcaseBasename = FilenameUtils.getBaseName(testCaseDocx.getName());
			File testcaseProperties = new File(testCaseRunner.getTestCasesDirPath() + File.separator + testcaseBasename + ".properties");
			File testcaseVariables = new File(testCaseRunner.getTestCasesDirPath() + File.separator + testcaseBasename + ".vars");
			File testcaseCmpText = new File(testCaseRunner.getTestCasesDirPath() + File.separator + testcaseBasename + ".cmp");

			if (testcaseProperties.exists() || testcaseVariables.exists()) {
				Set<String> variables = testCaseRunner.extractTemplateVariables(testCaseDocx);

				Properties variablesExpected = new Properties();
				variablesExpected.load(new FileInputStream(testcaseVariables.exists() ? testcaseVariables : testcaseProperties));

				assertEquals(
					testcaseBasename + ": extracted [" + String.join(", ", variables) + "]",
					variablesExpected.stringPropertyNames().size(),
					variables.size());
				for (String key : variablesExpected.stringPropertyNames()) {
					assertTrue(
						testcaseBasename + ": Variable " + key + " not extracted. Found: [" + String.join(", ", variables) + "]",
						variables.contains(key));
				}
				if (testcaseProperties.exists() && testcaseCmpText.exists()) {
					Properties properties = new Properties();
					properties.load(new FileInputStream(testcaseProperties));
					deserializeObjects(properties);
					String docxText = testCaseRunner.getGeneratedText(testCaseDocx, properties);

					String expected = getComparisonText(testcaseCmpText);
					assertEquals(testcaseBasename + ": generated text does not match.", expected, docxText);
				}
			}
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
				return getClass().getResource("/docgeneration/testcasesDocx").getPath();
			}

			@Override
			public String getTestCaseExtension() {
				return ".docx";
			}

			@Override
			public Set<String> extractTemplateVariables(File testCase) throws IOException, XDocReportException {
				return templateEngine.extractTemplateVariablesDocx(testCase);
			}

			@Override
			public String getGeneratedText(File testCase, Properties properties) throws IOException, XDocReportException {
				InputStream generatedFile = new ByteArrayInputStream(templateEngine.generateDocumentDocx(properties, testCase));

				XWPFDocument generatedDocument = new XWPFDocument(generatedFile);
				XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(generatedDocument);
				return xwpfWordExtractor.getText();
			}
		};
	}

	private TestCaseRunner getTestCaseRunnerTxt() {
		return new TestCaseRunner() {

			@Override
			public String getTestCasesDirPath() {
				return getClass().getResource("/docgeneration/testcasesTxt").getPath();
			}

			@Override
			public String getTestCaseExtension() {
				return ".txt";
			}

			@Override
			public Set<String> extractTemplateVariables(File testCase) throws IOException, ParseException {
				return templateEngine.extractTemplateVariablesTxt(testCase);
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

	@Ignore("Only for creation of new test cases")
	@Test
	public void serializeObjects() throws JsonProcessingException {
		// To create test cases with Object properties, use the following:
		PersonDto personDto = new PersonDto();
		personDto.setFirstName("Guy");
		personDto.setLastName("Debord");
		writeJsonProperty(personDto);

		// For collections, use arrays:
		String[] stringArray = {
			"a",
			"b",
			"c" };
		writeJsonProperty(stringArray);
	}

	private void writeJsonProperty(Object object) throws JsonProcessingException {
		String canonicalClassName = object.getClass().getCanonicalName().replaceFirst("(.*)\\[]", "[L$1;");
		String json = new ObjectMapper().writeValueAsString(object);
		System.out.println("(" + canonicalClassName + ") " + json);
	}
}
