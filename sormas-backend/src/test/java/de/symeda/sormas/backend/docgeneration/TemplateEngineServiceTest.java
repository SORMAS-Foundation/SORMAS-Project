package de.symeda.sormas.backend.docgeneration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.velocity.runtime.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import fr.opensagres.xdocreport.core.XDocReportException;

public class TemplateEngineServiceTest extends AbstractBeanTest {

	private TemplateEngineService templateEngineService;

	@Before
	public void setup() {
		templateEngineService = getTemplateEngineService();
	}

	@Test
	public void readVariablesFromDocxDocument() throws IOException, XDocReportException, ParseException {
		String filePath = TemplateEngineServiceTest.class.getResource("/DocumentTemplate.docx").getPath();
		Set<String> placeholders = templateEngineService.getPlaceholders(filePath);
		for (String placeholder : placeholders) {
			System.out.println(placeholder);
		}

		assertTrue(placeholders.contains("{name}"));
		assertTrue(placeholders.contains("{quarantine.to}"));
		assertTrue(placeholders.contains("{quarantine.from}"));

		// What to do with this???
		assertTrue(placeholders.contains("{___NoEscapeStylesGenerator.generateAllStyles($___DefaultStyle)}"));
	}

	@Test
	public void processDocxTemplate() throws IOException, XDocReportException {
		String filePath = TemplateEngineServiceTest.class.getResource("/DocumentTemplate.docx").getPath();

		Map<String, String> context = new HashMap<>();
		context.put("name", "Max Mustermann");
		context.put("quarantine.to", "2020/09/03");
		context.put("quarantine.from", "2020/09/17");

		String outFile = templateEngineService.generateDocument(context, filePath, ".");

		XWPFDocument outDocument = new XWPFDocument(new FileInputStream(outFile));
		XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(outDocument);
		String docxText = xwpfWordExtractor.getText();
		assertEquals("Hello World Max Mustermann\n" + "Quarantäne von 2020/09/17 bis 2020/09/03\n", docxText);
	}

	@Test
	public void getTempPath() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, ".");
		System.out.println(templateEngineService.getTempDir());
	}
}
