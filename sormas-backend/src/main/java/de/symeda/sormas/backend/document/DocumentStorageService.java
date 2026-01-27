/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;

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
	@EJB
	private UserService userService;

	public byte[] read(String storageReference) throws IOException {
        return Files.readAllBytes(getFilePath(storageReference));
    }

    public File getFile(String storageReference) {
        return getFilePath(storageReference).toFile();
    }

    @NotNull
    private Path getFilePath(String storageReference) {
        return Paths.get(configFacade.getDocumentFilesPath(), storageReference);
	}

	public String save(Document document, byte[] content) throws IOException {
		Path relativePath = computeRelativePath(document);
		Path filePath = Paths.get(configFacade.getDocumentFilesPath()).resolve(relativePath);
		Files.createDirectories(filePath.getParent());
		Files.write(filePath, content, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
		setDocumentAttributes(document, filePath);
		return relativePath.toString();
	}

	public void delete(String storageReference) {
        Path path = getFilePath(storageReference);
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

	private void setDocumentAttributes(Document document, Path filePath) throws IOException {
		setAttribute(filePath, "Document UUID", document.getUuid());
		setAttribute(filePath, "Author", userService.getCurrentUser().getUserName());
		setAttribute(filePath, "Display Name", document.getName());
		setAttribute(filePath, "Type", document.getMimeType());
		setAttribute(filePath, "Upload Date", DateFormatHelper.formatDate(document.getCreationDate()));
	}

	private void setAttribute(Path path, String attributeKey, String attributeValue) throws IOException {
		try {
			Files.setAttribute(path, "user:" + attributeKey, Charset.defaultCharset().encode(attributeValue));
		} catch (UnsupportedOperationException uoe) {
			// Java 11 does not support user defined file attributes on OS X. https://bugs.openjdk.org/browse/JDK-8030048
			// TODO remove try/catch after upgrade to Java 17
			logger.warn("Could not set document attribute [{}]: {}", attributeKey, uoe.getMessage());
		}
	}
}
