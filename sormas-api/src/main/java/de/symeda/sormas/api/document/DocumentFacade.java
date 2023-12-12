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
import java.util.Map;
import java.util.Set;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface DocumentFacade {

	DocumentDto getDocumentByUuid(String uuid);

	DocumentDto saveDocument(@Valid DocumentDto dto, byte[] bytes) throws IOException;

	void deleteDocument(String uuid);

	List<DocumentDto> getDocumentsRelatedToEntity(DocumentRelatedEntityType type, String uuid);

    List<DocumentReferenceDto> getReferencesRelatedToEntity(DocumentRelatedEntityType type, String uuid, Set<String> fileExtensions);

	Map<String, List<DocumentDto>> getDocumentsRelatedToEntities(DocumentCriteria criteria, List<SortProperty> sortProperties);

	String isExistingDocument(DocumentRelatedEntityType type, String uuid, String name);

	byte[] getContent(String uuid) throws IOException;

	void cleanupDeletedDocuments();
}
