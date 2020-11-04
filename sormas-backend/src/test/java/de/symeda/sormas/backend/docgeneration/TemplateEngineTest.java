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
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Before;
import org.junit.Test;

import com.auth0.jwt.internal.org.apache.commons.io.IOUtils;

import de.symeda.sormas.api.person.PersonDto;
import fr.opensagres.xdocreport.core.XDocReportException;

public class TemplateEngineTest {

	private TemplateEngine templateEngine;
	private String testCasesDocxDirPath;

	@Before
	public void setup() {
		templateEngine = new TemplateEngine();
		testCasesDocxDirPath = getClass().getResource("/docgeneration/testcasesDocx").getPath();
	}

	@Test
	public void genericTestCasesDocxTest() throws IOException, XDocReportException {
		File testCasesDir = new File(testCasesDocxDirPath);
		File[] testcasesDocx = testCasesDir.listFiles((d, name) -> name.endsWith(".docx"));

		for (File testcaseDocx : testcasesDocx) {
			String testcaseBasename = FilenameUtils.getBaseName(testcaseDocx.getName());
			File testcaseProperties = new File(testCasesDocxDirPath + File.separator + testcaseBasename + ".properties");
			File testcaseCmpText = new File(testCasesDocxDirPath + File.separator + testcaseBasename + ".cmp");

			if (testcaseProperties.exists()) {
				Set<String> variables = templateEngine.extractTemplateVariables(new FileInputStream(testcaseDocx));

				Properties properties = new Properties();
				properties.load(new FileInputStream(testcaseProperties));

				assertEquals(properties.stringPropertyNames().size(), variables.size());
				for (String key : properties.stringPropertyNames()) {
					assertTrue(testcaseBasename + ": Property " + key + " not extracted", variables.contains(key));
				}

				if (testcaseCmpText.exists()) {
					InputStream generatedFile = templateEngine.generateDocument(properties, new FileInputStream(testcaseDocx));

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

		String testcaseDocx = testCasesDocxDirPath + File.separator + "PropertyAccessTest.docx";
		String testcaseCmpText = testCasesDocxDirPath + File.separator + "PropertyAccessTest.txt";

		InputStream generatedFile = templateEngine.generateDocument(properties, new FileInputStream(testcaseDocx));

		XWPFDocument generatedDocument = new XWPFDocument(generatedFile);
		XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(generatedDocument);
		String docxText = xwpfWordExtractor.getText();

		StringWriter writer = new StringWriter();
		IOUtils.copy(new FileInputStream(testcaseCmpText), writer, "UTF-8");

		String expected = writer.toString().replaceAll("\\r\\n?", "\n");
		assertEquals(expected, docxText);
	}
}
