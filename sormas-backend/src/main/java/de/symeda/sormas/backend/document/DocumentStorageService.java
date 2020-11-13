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
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;

@Stateless
@LocalBean
public class DocumentStorageService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	public byte[] read(Document document) throws IOException {
		return Files.readAllBytes(computeFilePath(document));
	}

	public void save(Document document, byte[] content) throws IOException {
		Path filePath = computeFilePath(document);
		Files.createDirectories(filePath.getParent());
		Files.write(filePath, content, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
	}

	/**
	 * Delete file on disk when a document failed to be saved in database.
	 */
	public void cleanupUnsavedDocument(@Observes(during = TransactionPhase.AFTER_FAILURE) DocumentSaved event) {
		delete(event.getDocument());
	}

	public void delete(Document document) {
		Path path = computeFilePath(document);
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			logger.error("Couldn't delete file {}", path, e);
		}
	}

	@SuppressWarnings("deprecation")
	private Path computeFilePath(Document document) {
		return Paths.get(
			configFacade.getDocumentFilesPath(),
			Integer.toString(1900 + document.getCreationDate().getYear()),
			Integer.toString(1 + document.getCreationDate().getMonth()),
			Integer.toString(document.getCreationDate().getDate()),
			Integer.toString(document.getCreationDate().getHours()),
			document.getUuid());
	}
}
