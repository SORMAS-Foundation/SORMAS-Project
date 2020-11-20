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

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.persistence.EntityExistsException;

import org.junit.Test;

import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class DocumentFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testDocumentCreation() throws IOException {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf);
		EventDto event = creator.createEvent(user.toReference());

		DocumentDto document = creator
			.createDocument(user.toReference(), "Name.pdf", "application/pdf", 42L, event.toReference(), "content".getBytes(StandardCharsets.UTF_8));

		assertNotNull(getDocumentFacade().getDocumentByUuid(document.getUuid()));

		assertThat(getDocumentFacade().getDocumentsRelatedToEntity(DocumentRelatedEntityType.EVENT, event.getUuid()), hasSize(1));
	}

	@Test
	public void testExistingDocument() throws IOException {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf);
		EventDto event = creator.createEvent(user.toReference());

		DocumentDto document = creator
			.createDocument(user.toReference(), "Name.pdf", "application/pdf", 42L, event.toReference(), "content".getBytes(StandardCharsets.UTF_8));

		assertEquals(
			document.getUuid(),
			getDocumentFacade().isExistingDocument(DocumentRelatedEntityType.EVENT, event.getUuid(), document.getName()));
		assertNull(getDocumentFacade().isExistingDocument(DocumentRelatedEntityType.EVENT, event.getUuid(), "Some other name.docx"));
	}

	@Test(expected = EntityExistsException.class)
	public void testDuplicate() throws IOException {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf);
		EventDto event = creator.createEvent(user.toReference());

		DocumentDto document = creator
			.createDocument(user.toReference(), "Name.pdf", "application/pdf", 42L, event.toReference(), "content".getBytes(StandardCharsets.UTF_8));

		getDocumentFacade().saveDocument(document, "duplicate".getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testDocumentDeletion() throws IOException {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf);
		EventDto event = creator.createEvent(user.toReference());

		DocumentDto document = creator
			.createDocument(user.toReference(), "Name.pdf", "application/pdf", 42L, event.toReference(), "content".getBytes(StandardCharsets.UTF_8));

		assumeNotNull(getDocumentFacade().getDocumentByUuid(document.getUuid()));
		assumeThat(getDocumentFacade().getDocumentsRelatedToEntity(DocumentRelatedEntityType.EVENT, event.getUuid()), hasSize(1));
		assumeThat(
			getDocumentFacade().isExistingDocument(DocumentRelatedEntityType.EVENT, event.getUuid(), document.getName()),
			equalTo(document.getUuid()));

		getDocumentFacade().deleteDocument(document.getUuid());

		Document deleted = getDocumentService().getByUuid(document.getUuid());
		assertNotNull(deleted);
		assertTrue(deleted.isDeleted());

		assertThat(getDocumentFacade().getDocumentsRelatedToEntity(DocumentRelatedEntityType.EVENT, event.getUuid()), empty());
		assertNull(getDocumentFacade().isExistingDocument(DocumentRelatedEntityType.EVENT, event.getUuid(), document.getName()));
	}
}
