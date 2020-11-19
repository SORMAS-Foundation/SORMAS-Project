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
package de.symeda.sormas.ui.importer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import com.vaadin.server.Page;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.Upload;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("deprecation")
public class DocumentReceiver implements com.vaadin.v7.ui.Upload.Receiver, com.vaadin.v7.ui.Upload.SucceededListener {

	private final DocumentRelatedEntityType relatedEntityType;
	private final String relatedEntityUuid;
	private final Runnable callback;
	private File file;

	public DocumentReceiver(DocumentRelatedEntityType relatedEntityType, String relatedEntityUuid, Runnable callback) {
		this.relatedEntityType = relatedEntityType;
		this.relatedEntityUuid = relatedEntityUuid;
		this.callback = callback;
	}

	@Override
	public OutputStream receiveUpload(String fileName, String mimeType) {
		final FileOutputStream fos;
		// Reject empty files
		if (fileName == null || fileName.isEmpty()) {
			file = null;
			new Notification(
				I18nProperties.getString(Strings.headingNoFile),
				I18nProperties.getString(Strings.messageNoDocumentUploadFile),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			// Workaround because returning null here throws an uncatchable UploadException
			return new ByteArrayOutputStream();
		}

		try {
			String newFileName = ImportExportUtils.TEMP_FILE_PREFIX + "_document_upload" + DateHelper.formatDateForExport(new Date()) + "_"
				+ DataHelper.getShortUuid(UserProvider.getCurrent().getUuid());
			file = new File(Paths.get(FacadeProvider.getConfigFacade().getTempFilesPath()).resolve(newFileName).toString());
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			file = null;
			new Notification(
				I18nProperties.getString(Strings.headingImportError),
				I18nProperties.getString(Strings.messageImportError),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			// Workaround because returning null here throws an uncatchable UploadException
			return new ByteArrayOutputStream();
		}

		return fos;
	}

	@Override
	public void uploadSucceeded(com.vaadin.v7.ui.Upload.SucceededEvent succeededEvent) {
		if (file == null) {
			return;
		}

		// Check for duplicate files
		String existing = FacadeProvider.getDocumentFacade().isExistingDocument(relatedEntityType, relatedEntityUuid, succeededEvent.getFilename());
		if (existing != null) {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingFileExists),
				new Label(String.format(I18nProperties.getString(Strings.infoDocumentAlreadyExists), succeededEvent.getFilename())),
				I18nProperties.getCaption(Captions.actionConfirm),
				I18nProperties.getCaption(Captions.actionCancel),
				null,
				ok -> {
					if (ok) {
						FacadeProvider.getDocumentFacade().deleteDocument(existing);
						saveDocument(succeededEvent);
					}
				});
		} else {
			saveDocument(succeededEvent);
		}
	}

	private void saveDocument(Upload.SucceededEvent succeededEvent) {
		try {
			DocumentDto document = DocumentDto.build();
			document.setUploadingUser(UserProvider.getCurrent().getUserReference());
			document.setName(succeededEvent.getFilename());
			document.setMimeType(succeededEvent.getMIMEType());
			document.setSize(succeededEvent.getLength());
			document.setRelatedEntityType(relatedEntityType);
			document.setRelatedEntityUuid(relatedEntityUuid);

			FacadeProvider.getDocumentFacade().saveDocument(document, Files.readAllBytes(file.toPath()));

			callback.run();

			VaadinUiUtil.showSimplePopupWindow(
				I18nProperties.getString(Strings.headingUploadSuccess),
				I18nProperties.getString(Strings.messageUploadSuccessful));
		} catch (IOException | IllegalArgumentException e) {
			new Notification(I18nProperties.getString(Strings.headingImportFailed), e.getMessage(), Notification.Type.ERROR_MESSAGE, false)
				.show(Page.getCurrent());
		}
	}
}
