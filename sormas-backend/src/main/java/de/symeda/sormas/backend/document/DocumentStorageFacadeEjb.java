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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.document.DocumentStorageFacade;

@Stateless(name = "DocumentStorageFacade")
public class DocumentStorageFacadeEjb implements DocumentStorageFacade {

	@EJB
	private ConfigFacade configFacade;
	@EJB
	private DocumentService documentService;

	@Override
	public byte[] read(String uuid) throws IOException {
		return Files.readAllBytes(computeFilePath(uuid));
	}

	@Override
	public void store(String uuid, byte[] content) throws IOException {
		Path filePath = computeFilePath(uuid);
		Files.createDirectories(filePath.getParent());
		Files.write(filePath, content, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
	}

	@SuppressWarnings("deprecation")
	private Path computeFilePath(String uuid) {
		String basedir = configFacade.getDocumentFilesPath();
		Document document = documentService.getByUuid(uuid);

		return Paths.get(
			basedir,
			Integer.toString(1900 + document.getCreationDate().getYear()),
			Integer.toString(1 + document.getCreationDate().getMonth()),
			Integer.toString(document.getCreationDate().getDate()),
			Integer.toString(document.getCreationDate().getHours()),
			uuid);
	}

	@LocalBean
	@Stateless
	public static class DocumentStorageFacadeEjbLocal extends DocumentStorageFacadeEjb {
	}
}
