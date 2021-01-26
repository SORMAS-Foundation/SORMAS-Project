/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import static de.symeda.sormas.api.docgeneneration.DocumentWorkflow.QUARANTINE_ORDER_CASE;
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
		documentTemplateFacade.writeDocumentTemplate(QUARANTINE_ORDER_CASE, "TemplateFileToBeDeleted.docx", document);
		assertTrue(documentTemplateFacade.getAvailableTemplates(QUARANTINE_ORDER_CASE).contains("TemplateFileToBeDeleted.docx"));
		assertTrue(documentTemplateFacade.deleteDocumentTemplate(QUARANTINE_ORDER_CASE, "TemplateFileToBeDeleted.docx"));
		assertFalse(documentTemplateFacade.getAvailableTemplates(QUARANTINE_ORDER_CASE).contains("TemplateFileToBeDeleted.docx"));
		FileUtils.deleteDirectory(new File(testDirectory));
		resetCustomPath();
	}

	@Test
	public void isExistingTemplateTest() {
		assertTrue(documentTemplateFacade.isExistingTemplate(QUARANTINE_ORDER_CASE, "Quarantine.docx"));
		assertFalse(documentTemplateFacade.isExistingTemplate(QUARANTINE_ORDER_CASE, "ThisTemplateDoesNotExist.docx"));
	}

	@Test
	public void validateTemplateTest() throws IOException {
		try {
			documentTemplateFacade.writeDocumentTemplate(QUARANTINE_ORDER_CASE, "TemplateFileToBeValidated.txt", new byte[0]);
			fail("Invalid file extension not recognized.");
		} catch (IllegalArgumentException e) {
			assertEquals("Wrong file type", e.getMessage());
		}
		try {
			documentTemplateFacade.writeDocumentTemplate(QUARANTINE_ORDER_CASE, "../TemplateFileToBeValidated.docx", new byte[0]);
			fail("Invalid file extension not recognized.");
		} catch (IllegalArgumentException e) {
			assertEquals("Illegal file name: ../TemplateFileToBeValidated.docx", e.getMessage());
		}
		try {
			documentTemplateFacade.writeDocumentTemplate(QUARANTINE_ORDER_CASE, "TemplateFileToBeValidated.docx", new byte[0]);
			fail("Invalid docx file not recognized.");
		} catch (IllegalArgumentException e) {
			assertEquals("Error reading from the stream (no bytes available)", e.getMessage());
		}
		try {
			byte[] document = IOUtils.toByteArray(getClass().getResourceAsStream("/docgeneration/quarantine/FaultyTemplate.docx"));
			documentTemplateFacade.writeDocumentTemplate(QUARANTINE_ORDER_CASE, "TemplateFileToBeValidated.docx", document);
			fail("Syntax error not recognized.");
		} catch (IllegalArgumentException e) {
			String message =
				"org.apache.velocity.runtime.parser.TemplateParseException: Encountered \"].</w:t>\\n            </w:r>\\n        </w:p>\\n        <w:p>\\n            <w:pPr>\\n                <w:pStyle w:val=\\\"Normal\\\"/>\\n                <w:bidi w:val=\\\"false\\\"/>\\n                <w:ind w:right=\\\"3117\\\" w:hanging=\\\"0\\\"/>\\n                <w:jc w:val=\\\"both\\\"/>\\n                <w:rPr>\\n                    <w:rFonts w:ascii=\\\"DejaVu Sans\\\" w:hAnsi=\\\"DejaVu Sans\\\"/>\\n                    <w:sz w:val=\\\"21\\\"/>\\n                    <w:szCs w:val=\\\"21\\\"/>\\n                </w:rPr>\\n            </w:pPr>\\n            <w:r>\\n                <w:rPr>\\n                    <w:b w:val=\\\"false\\\"/>\\n                    <w:bCs w:val=\\\"false\\\"/>\\n                </w:rPr>\\n            </w:r>\\n        </w:p>\\n        <w:p>\\n            <w:pPr>\\n                <w:pStyle w:val=\\\"Normal\\\"/>\\n                <w:bidi w:val=\\\"false\\\"/>\\n                <w:ind w:right=\\\"3117\\\" w:hanging=\\\"0\\\"/>\\n                <w:jc w:val=\\\"both\\\"/>\\n                <w:rPr>\\n                    <w:b w:val=\\\"false\\\"/>\\n                    <w:bCs w:val=\\\"false\\\"/>\\n                </w:rPr>\\n            </w:pPr>\\n            <w:r>\\n                <w:rPr>\\n                    <w:rFonts w:ascii=\\\"DejaVu Sans\\\" w:hAnsi=\\\"DejaVu Sans\\\"/>\\n                    <w:b w:val=\\\"false\\\"/>\\n                    <w:bCs w:val=\\\"false\\\"/>\\n                    <w:sz w:val=\\\"21\\\"/>\\n                    <w:szCs w:val=\\\"21\\\"/>\\n                </w:rPr>\\n                <w:t>Processing of this template should fail.</w:t>\\n            </w:r>\\n        </w:p>\\n        <w:sectPr>\\n            <w:type w:val=\\\"nextPage\\\"/>\\n            <w:pgSz w:w=\\\"11906\\\" w:h=\\\"16838\\\"/>\\n            <w:pgMar w:top=\\\"1134\\\" w:right=\\\"1134\\\" w:bottom=\\\"1134\\\" w:left=\\\"1134\\\" w:header=\\\"0\\\" w:footer=\\\"0\\\" w:gutter=\\\"0\\\"/>\\n            <w:pgNumType w:fmt=\\\"decimal\\\"/>\\n            <w:formProt w:val=\\\"false\\\"/>\\n            <w:textDirection w:val=\\\"lrTb\\\"/>\\n            <w:docGrid w:type=\\\"default\\\" w:linePitch=\\\"100\\\" w:charSpace=\\\"0\\\"/>\\n        </w:sectPr>\\n    </w:body>\\n</w:document>\" at word/document.xml[line 22, column 59]\n"
					+ "Was expecting one of:\n" //
					+ "    \"[\" ...\n" //
					+ "    \"}\" ...\n    ";
			assertEquals(message, e.getMessage().replaceAll("\\r\\n?", "\n"));
		}
	}

	@Test
	public void readTemplateTest() throws IOException {
		byte[] template = documentTemplateFacade.getDocumentTemplate(QUARANTINE_ORDER_CASE, "Quarantine.docx");
		assertEquals(12683, template.length);
	}
}
