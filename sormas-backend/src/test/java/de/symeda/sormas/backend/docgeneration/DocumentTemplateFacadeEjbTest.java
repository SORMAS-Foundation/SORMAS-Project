package de.symeda.sormas.backend.docgeneration;

import static de.symeda.sormas.api.docgeneneration.DocumentWorkflow.QUARANTINE_ORDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.docgeneneration.DocumentTemplateFacade;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class DocumentTemplateFacadeEjbTest extends AbstractDocGenerationTest {

	private DocumentTemplateFacade documentTemplateFacade;

	@Before
	public void setup() throws URISyntaxException {
		documentTemplateFacade = getDocumentTemplateFacade();
		reset();
	}

	@Test
	public void writeAndDeleteTemplateTest() throws IOException, URISyntaxException {
		String testDirectory = "target" + File.separator + "doctest";
		byte[] document = IOUtils.toByteArray(getClass().getResourceAsStream("/docgeneration/quarantine/Quarantine.docx"));
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, testDirectory);
		documentTemplateFacade.writeDocumentTemplate(QUARANTINE_ORDER, "TemplateFileToBeDeleted.docx", document);
		assertTrue(documentTemplateFacade.getAvailableTemplates(QUARANTINE_ORDER).contains("TemplateFileToBeDeleted.docx"));
		assertTrue(documentTemplateFacade.deleteDocumentTemplate(QUARANTINE_ORDER, "TemplateFileToBeDeleted.docx"));
		assertFalse(documentTemplateFacade.getAvailableTemplates(QUARANTINE_ORDER).contains("TemplateFileToBeDeleted.docx"));
		FileUtils.deleteDirectory(new File(testDirectory));
		resetCustomPath();
	}

	@Test
	public void isExistingTemplateTest() {
		assertTrue(documentTemplateFacade.isExistingTemplate(QUARANTINE_ORDER, "Quarantine.docx"));
		assertFalse(documentTemplateFacade.isExistingTemplate(QUARANTINE_ORDER, "ThisTemplateDoesNotExist.docx"));
	}

	@Test
	public void validateTemplateTest() throws IOException {
		try {
			documentTemplateFacade.writeDocumentTemplate(QUARANTINE_ORDER, "TemplateFileToBeValidated.txt", new byte[0]);
			fail("Invalid file extension not recognized.");
		} catch (IllegalArgumentException e) {
			assertEquals("Wrong file type", e.getMessage());
		}
		try {
			documentTemplateFacade.writeDocumentTemplate(QUARANTINE_ORDER, "../TemplateFileToBeValidated.docx", new byte[0]);
			fail("Invalid file extension not recognized.");
		} catch (IllegalArgumentException e) {
			assertEquals("Illegal file name: ../TemplateFileToBeValidated.docx", e.getMessage());
		}
		try {
			documentTemplateFacade.writeDocumentTemplate(QUARANTINE_ORDER, "TemplateFileToBeValidated.docx", new byte[0]);
			fail("Invalid docx file not recognized.");
		} catch (IllegalArgumentException e) {
			assertEquals("InputStream is not a zip.", e.getMessage());
		}
		try {
			byte[] document = IOUtils.toByteArray(getClass().getResourceAsStream("/docgeneration/quarantine/FaultyTemplate.docx"));
			documentTemplateFacade.writeDocumentTemplate(QUARANTINE_ORDER, "TemplateFileToBeValidated.docx", document);
			fail("Syntax error not recognized.");
		} catch (IllegalArgumentException e) {
			String message =
				"org.apache.velocity.runtime.parser.TemplateParseException: Encountered \"].</w:t></w:r></w:p><w:p><w:pPr><w:pStyle w:val=\\\"Normal\\\"/><w:bidi w:val=\\\"0\\\"/><w:ind w:right=\\\"3117\\\" w:hanging=\\\"0\\\"/><w:jc w:val=\\\"both\\\"/><w:rPr><w:rFonts w:ascii=\\\"DejaVu Sans\\\" w:hAnsi=\\\"DejaVu Sans\\\"/><w:sz w:val=\\\"21\\\"/><w:szCs w:val=\\\"21\\\"/></w:rPr></w:pPr><w:r><w:rPr><w:b w:val=\\\"false\\\"/><w:bCs w:val=\\\"false\\\"/></w:rPr></w:r></w:p><w:p><w:pPr><w:pStyle w:val=\\\"Normal\\\"/><w:bidi w:val=\\\"0\\\"/><w:ind w:right=\\\"3117\\\" w:hanging=\\\"0\\\"/><w:jc w:val=\\\"both\\\"/><w:rPr><w:b w:val=\\\"false\\\"/><w:b w:val=\\\"false\\\"/><w:bCs w:val=\\\"false\\\"/></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:ascii=\\\"DejaVu Sans\\\" w:hAnsi=\\\"DejaVu Sans\\\"/><w:b w:val=\\\"false\\\"/><w:bCs w:val=\\\"false\\\"/><w:sz w:val=\\\"21\\\"/><w:szCs w:val=\\\"21\\\"/></w:rPr><w:t>Processing of this template should fail.</w:t></w:r></w:p><w:sectPr><w:type w:val=\\\"nextPage\\\"/><w:pgSz w:w=\\\"11906\\\" w:h=\\\"16838\\\"/><w:pgMar w:left=\\\"1134\\\" w:right=\\\"1134\\\" w:header=\\\"0\\\" w:top=\\\"1134\\\" w:footer=\\\"0\\\" w:bottom=\\\"1134\\\" w:gutter=\\\"0\\\"/><w:pgNumType w:fmt=\\\"decimal\\\"/><w:formProt w:val=\\\"false\\\"/><w:textDirection w:val=\\\"lrTb\\\"/><w:docGrid w:type=\\\"default\\\" w:linePitch=\\\"100\\\" w:charSpace=\\\"0\\\"/></w:sectPr></w:body></w:document>\" at word/document.xml[line 1, column 1240]\n"
					+ "Was expecting one of:\n" //
					+ "    \"[\" ...\n" //
					+ "    \"}\" ...\n    ";
			assertEquals(message, e.getMessage().replaceAll("\\r\\n?", "\n"));
		}
	}

	@Test
	public void readTemplateTest() throws IOException {
		byte[] template = documentTemplateFacade.getDocumentTemplate(QUARANTINE_ORDER, "Quarantine.docx");
		assertEquals(12300, template.length);
	}
}
