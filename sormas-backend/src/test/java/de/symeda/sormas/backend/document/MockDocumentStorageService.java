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

import java.nio.charset.StandardCharsets;

import javax.enterprise.inject.Specializes;

@Specializes
public class MockDocumentStorageService extends DocumentStorageService {

	@Override
	public byte[] read(String storageReference) {
		return storageReference.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public String save(Document document, byte[] content) {
		return document.getUuid();
	}

	@Override
	public void delete(String storageReference) {
	}

}
