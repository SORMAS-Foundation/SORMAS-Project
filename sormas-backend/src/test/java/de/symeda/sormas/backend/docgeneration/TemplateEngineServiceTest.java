package de.symeda.sormas.backend.docgeneration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import fr.opensagres.xdocreport.core.XDocReportException;

public class TemplateEngineServiceTest extends AbstractBeanTest {

	private TemplateEngineService templateEngineService;
	private String testCasesDirPath;

	@Before
	public void setup() {
		templateEngineService = getTemplateEngineService();
		testCasesDirPath = getClass().getResource("/docgeneration/testcases").getPath();
	}

	@Test
	public void genericTestCasesTest() throws IOException, XDocReportException {
		File testCasesDir = new File(testCasesDirPath);
		File[] testcasesDocx = testCasesDir.listFiles((d, name) -> name.endsWith(".docx"));

		for (File testcaseDocx : testcasesDocx) {
			String testcaseBasename = FilenameUtils.getBaseName(testcaseDocx.getName());
			File testcaseProperties = new File(testCasesDirPath + File.separator + testcaseBasename + ".properties");
			File testcaseCmpText = new File(testCasesDirPath + File.separator + testcaseBasename + ".txt");

			if (testcaseProperties.exists()) {
				Set<String> variables = templateEngineService.extractTemplateVariables(new FileInputStream(testcaseDocx));

				Properties properties = new Properties();
				properties.load(new FileInputStream(testcaseProperties));

				assertEquals(properties.stringPropertyNames().size(), variables.size());
				for (String key : properties.stringPropertyNames()) {
					assertTrue(testcaseBasename + ": Property " + key + " not extracted", variables.contains(key));
				}

				if (testcaseCmpText.exists()) {
					InputStream generatedFile = templateEngineService.generateDocument(properties, new FileInputStream(testcaseDocx));

					XWPFDocument generatedDocument = new XWPFDocument(generatedFile);
					XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(generatedDocument);
					String docxText = xwpfWordExtractor.getText();

					StringWriter writer = new StringWriter();
					IOUtils.copy(new FileInputStream(testcaseCmpText), writer, "UTF-8");

					String expected = writer.toString().replaceAll("\\r\\n?", "\n");
					assertEquals(testcaseBasename + ": generated text does not match.", expected, docxText);
				}
			}
		}
	}

	@Test
	public void propertyAccessTest() throws IOException, XDocReportException {
		PersonDto personDto = new PersonDto();
		personDto.setFirstName("Michail");
		personDto.setLastName("Bakunin");

		Properties properties = new Properties();
		properties.put("person", personDto);

		String testcaseDocx = testCasesDirPath + File.separator + "PropertyAccessTest.docx";
		String testcaseCmpText = testCasesDirPath + File.separator + "PropertyAccessTest.txt";

		InputStream generatedFile = templateEngineService.generateDocument(properties, new FileInputStream(testcaseDocx));

		XWPFDocument generatedDocument = new XWPFDocument(generatedFile);
		XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(generatedDocument);
		String docxText = xwpfWordExtractor.getText();

		StringWriter writer = new StringWriter();
		IOUtils.copy(new FileInputStream(testcaseCmpText), writer, "UTF-8");

		String expected = writer.toString().replaceAll("\\r\\n?", "\n");
		assertEquals(expected, docxText);
	}
}
