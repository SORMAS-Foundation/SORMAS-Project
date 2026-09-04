/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.externalemail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;

@ExtendWith(MockitoExtension.class)
public class AttachmentServiceTest {

	/**
	 * Meant to make sure the PDF is not broken after encrypting it.
	 * 
	 * @param tempDir
	 *            handled by JUnit
	 * @throws IOException
	 *             file processing error.
	 */
	@Test
	public void testEncryptPdf(@TempDir Path tempDir) throws IOException {
		// Setup
		AttachmentService attachmentService = new AttachmentService();
		ConfigFacadeEjbLocal configFacade = mock(ConfigFacadeEjbLocal.class);

		// Inject mock configFacade using reflection
		try {
			Field configFacadeField = AttachmentService.class.getDeclaredField("configFacade");
			configFacadeField.setAccessible(true);
			configFacadeField.set(attachmentService, configFacade);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Failed to inject configFacade mock", e);
		}

		// Configure temp directory path
		String tempPath = tempDir.toString();
		when(configFacade.getTempFilesPath()).thenReturn(tempPath);

		// Load test PDF from resources
		ClassLoader classLoader = getClass().getClassLoader();
		File originalPdfFile = new File(classLoader.getResource("pdfs/document-sormas-pdf-encrypt.pdf").getFile());

		// Load original PDF to get metadata for comparison
		PDDocument originalDoc = Loader.loadPDF(originalPdfFile);
		int originalPageCount = originalDoc.getNumberOfPages();
		originalDoc.close();

		// Execute
		File encryptedPdf = attachmentService.encryptPdf(originalPdfFile, "test-password");

		// Assert - check if the encrypted file exists
		assertTrue(encryptedPdf.exists(), "Encrypted PDF file should exist");

		// Verify encrypted PDF is valid by loading it with the password and comparing content
		try {
			PDDocument encryptedDoc = Loader.loadPDF(encryptedPdf, "test-password");
			int encryptedPageCount = encryptedDoc.getNumberOfPages();

			// Assert same number of pages - proves document structure is preserved
			assertEquals(originalPageCount, encryptedPageCount, "Page count should be the same after encryption");

			encryptedDoc.close();
		} catch (IOException e) {
			encryptedPdf.delete();
			throw new AssertionError("Failed to load encrypted PDF with password: " + e.getMessage(), e);
		}

		/*
		 * Set this to true if you want to keep the encrypted file for verification.
		 */
		boolean keepEncryptedFile = true;

		// Keep file only once
		if (keepEncryptedFile) {
			String keepPath = "target/test-output/encrypted-verification.pdf";
			Files.copy(encryptedPdf.toPath(), Paths.get(keepPath), StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Verification file kept at: " + new File(keepPath).getAbsolutePath());
		}

		// Cleanup
		if (encryptedPdf.exists()) {
			encryptedPdf.delete();
		}
	}

	/**
	 * Tests the DOCX to PDF conversion using the converter.
	 *
	 * @param tempDir
	 *            handled by JUnit
	 * @throws Exception
	 *             if conversion fails
	 */
	@Test
	public void testDocxToPdfConversion(@TempDir Path tempDir) throws Exception {
		// Setup
		AttachmentService victim = new AttachmentService();
		ConfigFacadeEjbLocal configFacade = mock(ConfigFacadeEjbLocal.class);

		// Inject mock configFacade using reflection
		try {
			Field configFacadeField = AttachmentService.class.getDeclaredField("configFacade");
			configFacadeField.setAccessible(true);
			configFacadeField.set(victim, configFacade);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Failed to inject configFacade mock", e);
		}

		// Configure temp directory path
		String tempPath = tempDir.toString();
		when(configFacade.getTempFilesPath()).thenReturn(tempPath);

		// Load test DOCX from resources
		ClassLoader classLoader = getClass().getClassLoader();
		File docxFile = new File(classLoader.getResource("docx/ordonnance-test.docx").getFile());

		String password = "password";
		Map<File, String> result = victim.createEncryptedPdfs(Map.of(docxFile, "name-of-result.docx"), password);

		Map.Entry<File, String> fileStringEntry = result.entrySet().stream().findFirst().orElseThrow();

		File convertedPdf = fileStringEntry.getKey();

		// Assert - check if the converted PDF file exists
		assertTrue(convertedPdf.exists(), "Converted PDF file should exist");

		// Verify it's a valid PDF by loading it
		try {
			PDDocument pdfDoc = Loader.loadPDF(convertedPdf, password);
			assertTrue(pdfDoc.getNumberOfPages() > 0, "PDF should have at least one page");
			pdfDoc.close();
		} catch (IOException e) {
			convertedPdf.delete();
			throw new AssertionError("Failed to load converted PDF: " + e.getMessage(), e);
		}

		/*
		 * Set this to true if you want to keep the encrypted file for verification.
		 */
		boolean keepEncryptedFile = true;

		// Keep file only once
		if (keepEncryptedFile) {
			String keepPath = "target/test-output/docx-to-pdf-verification.pdf";
			Files.copy(convertedPdf.toPath(), Paths.get(keepPath), StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Verification file kept at: " + new File(keepPath).getAbsolutePath());
		}

		// Cleanup
		if (convertedPdf.exists()) {
			convertedPdf.delete();
		}
	}


	@Test
	public void testEditPdf(@TempDir Path tempDir) throws IOException {
		// Setup
		AttachmentService attachmentService = new AttachmentService();
		ConfigFacadeEjbLocal configFacade = mock(ConfigFacadeEjbLocal.class);

		// Inject mock configFacade using reflection
		try {
			Field configFacadeField = AttachmentService.class.getDeclaredField("configFacade");
			configFacadeField.setAccessible(true);
			configFacadeField.set(attachmentService, configFacade);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Failed to inject configFacade mock", e);
		}

		// Configure temp directory path
		String tempPath = tempDir.toString();
		when(configFacade.getTempFilesPath()).thenReturn(tempPath);

		// Load test PDF from resources
		ClassLoader classLoader = getClass().getClassLoader();
		File originalPdfFile = new File(classLoader.getResource("pdfs/pdf-to-replace.pdf").getFile());

		// Load original PDF to get metadata for comparison
		PDDocument originalDoc = Loader.loadPDF(originalPdfFile);
		int originalPageCount = originalDoc.getNumberOfPages();
		originalDoc.close();

		// Execute
		File edited = attachmentService.replaceText(originalPdfFile, Map.of(
				"«$person.firstName»", "John",
				"«$person.lastName»", "Doe",
				"«$houseNumber»", "5",
				"«$person.address.street»", "Rue de Paris",
				"«$ postalCode»", "123456",// TODO: warning must be edited in the original file. variable name seems off.
				"«$person.address.city»", "Luxembourg"
		));

		// Assert - check if the encrypted file exists
		assertTrue(edited.exists(), "Encrypted PDF file should exist");

		// Verify encrypted PDF is valid by loading it with the password and comparing content
		try {
			PDDocument encryptedDoc = Loader.loadPDF(edited, "test-password");
			int encryptedPageCount = encryptedDoc.getNumberOfPages();

			// Assert same number of pages - proves document structure is preserved
			assertEquals(originalPageCount, encryptedPageCount, "Page count should be the same after encryption");

			encryptedDoc.close();
		} catch (IOException e) {
			edited.delete();
			throw new AssertionError("Failed to load encrypted PDF with password: " + e.getMessage(), e);
		}

		/*
		 * Set this to true if you want to keep the encrypted file for verification.
		 */
		boolean keepEncryptedFile = true;

		// Keep file only once
		if (keepEncryptedFile) {
			String keepPath = "target/test-output/encrypted-verification.pdf";
			Files.copy(edited.toPath(), Paths.get(keepPath), StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Verification file kept at: " + new File(keepPath).getAbsolutePath());
		}

		// Cleanup
		if (edited.exists()) {
			edited.delete();
		}
	}


}
