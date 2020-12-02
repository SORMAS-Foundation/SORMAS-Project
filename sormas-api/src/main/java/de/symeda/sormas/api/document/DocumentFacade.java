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
package de.symeda.sormas.api.document;

import java.io.IOException;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface DocumentFacade {

	DocumentDto getDocumentByUuid(String uuid);

	DocumentDto saveDocument(DocumentDto dto, byte[] bytes) throws IOException;

	void deleteDocument(String uuid);

	List<DocumentDto> getDocumentsRelatedToEntity(DocumentRelatedEntityType type, String uuid);

	String isExistingDocument(DocumentRelatedEntityType type, String uuid, String name);

	byte[] read(String uuid) throws IOException;

	void cleanupDeletedDocuments();
}
