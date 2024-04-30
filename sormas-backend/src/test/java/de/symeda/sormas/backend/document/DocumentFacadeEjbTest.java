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
package de.symeda.sormas.backend.document;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import javax.persistence.EntityExistsException;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.FileExtensionNotAllowedException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class DocumentFacadeEjbTest extends AbstractBeanTest {

	private byte[] contentAsBytes = new String(
		"%PDF-1.0\n1 0 obj<</Type/Catalog/Pages " + "2 0 R>>endobj 2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj 3 0 obj<</Ty"
			+ "pe/Page/MediaBox[0 0 3 3]>>endobj\nxref\n0 4\n0000000000 65535 f\n000000001"
			+ "0 00000 n\n0000000053 00000 n\n0000000102 00000 n\ntrailer<</Size 4/Root 1 " + "0 R>>\nstartxref\n149\n%EOF")
		.getBytes();

	@Test
	public void testDocumentCreation() throws IOException {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf);
		EventDto event = creator.createEvent(user.toReference());

		DocumentDto document = creator.createDocument(user.toReference(), "Name.pdf", "application/pdf", 42L, event.toReference(), contentAsBytes);

		assertNotNull(getDocumentFacade().getDocumentByUuid(document.getUuid()));

		assertThat(getDocumentFacade().getDocumentsRelatedToEntity(DocumentRelatedEntityType.EVENT, event.getUuid()), hasSize(1));
	}

	@Test
	public void testExistingDocument() throws IOException {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf);
		EventDto event = creator.createEvent(user.toReference());

		DocumentDto document = creator.createDocument(user.toReference(), "Name.pdf", "application/pdf", 42L, event.toReference(), contentAsBytes);

		assertEquals(
			document.getUuid(),
			getDocumentFacade().isExistingDocument(DocumentRelatedEntityType.EVENT, event.getUuid(), document.getName()));
		assertNull(getDocumentFacade().isExistingDocument(DocumentRelatedEntityType.EVENT, event.getUuid(), "Some other name.docx"));
	}

	@Test
	public void testDuplicate() throws IOException {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf);
		EventDto event = creator.createEvent(user.toReference());

		DocumentDto document = creator.createDocument(user.toReference(), "Name.pdf", "application/pdf", 42L, event.toReference(), contentAsBytes);

		assertThrows(
			EntityExistsException.class,
			() -> getDocumentFacade().saveDocument(document, "duplicate".getBytes(StandardCharsets.UTF_8), Collections.emptyList()));
	}

	@Test
	public void testDocumentDeletion() throws IOException {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf);
		EventDto event = creator.createEvent(user.toReference());

		DocumentDto document = creator.createDocument(user.toReference(), "Name.pdf", "application/pdf", 42L, event.toReference(), contentAsBytes);

		assertNotNull(getDocumentFacade().getDocumentByUuid(document.getUuid()));
		assertThat(getDocumentFacade().getDocumentsRelatedToEntity(DocumentRelatedEntityType.EVENT, event.getUuid()), hasSize(1));
		assertThat(
			getDocumentFacade().isExistingDocument(DocumentRelatedEntityType.EVENT, event.getUuid(), document.getName()),
			equalTo(document.getUuid()));

		getDocumentFacade().deleteDocument(document.getUuid(), event.getUuid(), DocumentRelatedEntityType.EVENT);

		Document deleted = getDocumentService().getByUuid(document.getUuid());
		assertNotNull(deleted);
		assertTrue(deleted.isDeleted());

		assertThat(getDocumentFacade().getDocumentsRelatedToEntity(DocumentRelatedEntityType.EVENT, event.getUuid()), empty());
		assertNull(getDocumentFacade().isExistingDocument(DocumentRelatedEntityType.EVENT, event.getUuid(), document.getName()));
	}

	@Test
	public void testDocumentMimeTypeUnknown() throws IOException {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf);
		EventDto event = creator.createEvent(user.toReference());
		assertThrows(
			FileExtensionNotAllowedException.class,
			() -> creator.createDocument(
				user.toReference(),
				"test.json",
				DocumentDto.MIME_TYPE_DEFAULT,
				42L,
				event.toReference(),
				"content".getBytes(StandardCharsets.UTF_8)));
	}
}
