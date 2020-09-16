package de.symeda.sormas.backend.docgeneration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Before;
import org.junit.Test;

import com.auth0.jwt.internal.org.apache.commons.io.IOUtils;

import de.symeda.sormas.backend.AbstractBeanTest;
import fr.opensagres.xdocreport.core.XDocReportException;

public class TemplateEngineServiceTest extends AbstractBeanTest {

	private TemplateEngineService templateEngineService;

	@Before
	public void setup() {
		templateEngineService = getTemplateEngineService();
	}

	@Test
	public void genericTestCasesTest() throws IOException, XDocReportException {
		String testCasesDirPath = getClass().getResource("/docgeneration/testcases").getPath();
		File testCasesDir = new File(testCasesDirPath);
		File[] testcasesDocx = testCasesDir.listFiles((d, name) -> name.endsWith(".docx"));

		for (File testcaseDocx : testcasesDocx) {
			System.out.println("Processing " + testcaseDocx.getName() + "...");

			String testcaseBasename = FilenameUtils.getBaseName(testcaseDocx.getName());
			File testcaseProperties = new File(testCasesDirPath + File.separator + testcaseBasename + ".properties");
			File testcaseCmpText = new File(testCasesDirPath + File.separator + testcaseBasename + ".txt");

			if (testcaseProperties.exists()) {
				Set<String> variables = templateEngineService.extractTemplateVariables(new FileInputStream(testcaseDocx));

				Properties properties = new Properties();
				properties.load(new FileInputStream(testcaseProperties));

				assertEquals(properties.stringPropertyNames().size(), variables.size());
				for (String key : properties.stringPropertyNames()) {
					assertTrue("Property not extracted: " + key, variables.contains(key));
				}

				System.out.println("  variables extracted.");

				if (testcaseCmpText.exists()) {
					InputStream generatedFile = templateEngineService.generateDocument(properties, new FileInputStream(testcaseDocx));

					XWPFDocument generatedDocument = new XWPFDocument(generatedFile);
					XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(generatedDocument);
					String docxText = xwpfWordExtractor.getText();
					docxText.replaceAll("\\r\\n?", "\n");

					StringWriter writer = new StringWriter();
					IOUtils.copy(new FileInputStream(testcaseCmpText), writer, "UTF-8");

					String expected = writer.toString().replaceAll("\\r\\n?", "\n");
					assertEquals(expected, docxText);
					System.out.println("  document generated.");
				}
			}
		}
	}
}
