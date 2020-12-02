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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;

/**
 * Handles storage of document content itself.
 *
 * <p>
 * The current implementation stores files on the filesystem, in {@link ConfigFacade#getDocumentFilesPath() documents.path}.
 * <p>
 * The computed <i>storage reference</i> is the path of the file, relative to {@code documents.path}.
 */
@Stateless
@LocalBean
public class DocumentStorageService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	public byte[] read(String storageReference) throws IOException {
		return Files.readAllBytes(Paths.get(configFacade.getDocumentFilesPath(), storageReference));
	}

	public String save(Document document, byte[] content) throws IOException {
		Path relativePath = computeRelativePath(document);
		Path filePath = Paths.get(configFacade.getDocumentFilesPath()).resolve(relativePath);
		Files.createDirectories(filePath.getParent());
		Files.write(filePath, content, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
		return relativePath.toString();
	}

	public void delete(String storageReference) {
		Path path = Paths.get(configFacade.getDocumentFilesPath(), storageReference);
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			logger.error("Couldn't delete file {}", path, e);
		}
	}

	@SuppressWarnings("deprecation")
	private Path computeRelativePath(Document document) {
		return Paths.get(
			Integer.toString(1900 + document.getCreationDate().getYear()),
			Integer.toString(1 + document.getCreationDate().getMonth()),
			Integer.toString(document.getCreationDate().getDate()),
			Integer.toString(document.getCreationDate().getHours()),
			document.getUuid());
	}
}
