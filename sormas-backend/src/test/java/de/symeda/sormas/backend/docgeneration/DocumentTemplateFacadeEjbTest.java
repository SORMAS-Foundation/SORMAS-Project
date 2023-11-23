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

import static de.symeda.sormas.api.docgeneneration.DocumentWorkflow.CASE_EMAIL;
import static de.symeda.sormas.api.docgeneneration.DocumentWorkflow.QUARANTINE_ORDER_CASE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateFacade;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class DocumentTemplateFacadeEjbTest extends AbstractDocGenerationTest {

	private DocumentTemplateFacade documentTemplateFacade;

	@BeforeEach
	public void setup() throws URISyntaxException {
		documentTemplateFacade = getDocumentTemplateFacade();
		reset();
	}

	@Test
	public void writeAndDeleteTemplateTest() throws IOException, URISyntaxException, DocumentTemplateException {
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
		} catch (DocumentTemplateException e) {
			assertEquals("Wrong file type", e.getMessage());
		}
		try {
			documentTemplateFacade.writeDocumentTemplate(QUARANTINE_ORDER_CASE, "../TemplateFileToBeValidated.docx", new byte[0]);
			fail("Invalid file extension not recognized.");
		} catch (DocumentTemplateException e) {
			assertEquals("Illegal file name: ../TemplateFileToBeValidated.docx", e.getMessage());
		}
		try {
			documentTemplateFacade.writeDocumentTemplate(QUARANTINE_ORDER_CASE, "TemplateFileToBeValidated.docx", new byte[0]);
			fail("Invalid docx file not recognized.");
		} catch (DocumentTemplateException e) {
			assertEquals("The template file is corrupt.", e.getMessage());
		}
		try {
			byte[] document = IOUtils.toByteArray(getClass().getResourceAsStream("/docgeneration/quarantine/FaultyTemplate.docx"));
			documentTemplateFacade.writeDocumentTemplate(QUARANTINE_ORDER_CASE, "TemplateFileToBeValidated.docx", document);
			fail("Syntax error not recognized.");
		} catch (DocumentTemplateException e) {
			assertEquals("Error processing template.", e.getMessage());
		}
	}

	@Test
	public void testEmailTemplateValidation() throws DocumentTemplateException {
		assertThrows(
			ValidationRuntimeException.class,
			() -> documentTemplateFacade
				.writeDocumentTemplate(CASE_EMAIL, "CaseEmailTemplate.txt", "Email template without subject".getBytes(StandardCharsets.UTF_8)));

		assertThrows(
			ValidationRuntimeException.class,
			() -> documentTemplateFacade.writeDocumentTemplate(
				CASE_EMAIL,
				"CaseEmailTemplate.txt",
				"Email template without subject\nSecond line".getBytes(StandardCharsets.UTF_8)));
		assertThrows(
			ValidationRuntimeException.class,
			() -> documentTemplateFacade.writeDocumentTemplate(
				CASE_EMAIL,
				"CaseEmailTemplate.txt",
				"#\nEmail template without subject\nSecond line".getBytes(StandardCharsets.UTF_8)));
		assertThrows(
			ValidationRuntimeException.class,
			() -> documentTemplateFacade.writeDocumentTemplate(
				CASE_EMAIL,
				"CaseEmailTemplate.txt",
				"# \nEmail template without subject\nSecond line".getBytes(StandardCharsets.UTF_8)));
		assertThrows(
			ValidationRuntimeException.class,
			() -> documentTemplateFacade.writeDocumentTemplate(
				CASE_EMAIL,
				"CaseEmailTemplate.txt",
				"*Subject\nEmail template without subject\nSecond line".getBytes(StandardCharsets.UTF_8)));
	}

	@Test
	public void readTemplateTest() throws DocumentTemplateException {
		byte[] template = documentTemplateFacade.getDocumentTemplate(QUARANTINE_ORDER_CASE, "Quarantine.docx");
		assertEquals(12731, template.length);
	}
}
