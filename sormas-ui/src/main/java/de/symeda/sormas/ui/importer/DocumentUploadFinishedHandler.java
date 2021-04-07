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

package de.symeda.sormas.ui.importer;

import java.io.InputStream;

import com.google.common.io.ByteStreams;
import com.vaadin.server.Page;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DocumentUploadFinishedHandler implements UploadFinishedHandler {

	private final DocumentRelatedEntityType relatedEntityType;
	private final String relatedEntityUuid;
	private final Runnable callback;

	public DocumentUploadFinishedHandler(DocumentRelatedEntityType relatedEntityType, String relatedEntityUuid, Runnable callback) {
		this.relatedEntityType = relatedEntityType;
		this.relatedEntityUuid = relatedEntityUuid;
		this.callback = callback;
	}

	@Override
	public void handleFile(InputStream inputStream, String fileName, String mimeType, long length, int filesLeftInQueue) {
		try {
			byte[] bytes = ByteStreams.toByteArray(inputStream);

			String existing = FacadeProvider.getDocumentFacade().isExistingDocument(relatedEntityType, relatedEntityUuid, fileName);
			if (existing != null) {
				VaadinUiUtil.showConfirmationPopup(
					I18nProperties.getString(Strings.headingFileExists),
					new Label(String.format(I18nProperties.getString(Strings.infoDocumentAlreadyExists), fileName)),
					I18nProperties.getCaption(Captions.actionConfirm),
					I18nProperties.getCaption(Captions.actionCancel),
					null,
					ok -> {
						if (ok) {
							FacadeProvider.getDocumentFacade().deleteDocument(existing);
							try {
								saveDocument(fileName, mimeType, length, relatedEntityType, relatedEntityUuid, bytes);
							} catch (Exception e) {
								new Notification(
									I18nProperties.getString(Strings.headingImportError),
									I18nProperties.getString(Strings.messageImportError),
									Notification.Type.ERROR_MESSAGE,
									false).show(Page.getCurrent());
								throw new RuntimeException(e);
							}
							if (filesLeftInQueue == 0) {
								Notification.show(I18nProperties.getString(Strings.headingUploadSuccess), Notification.Type.TRAY_NOTIFICATION);
							}
						}
						if (filesLeftInQueue == 0) {
							if (callback != null) {
								callback.run();
							}
						}
					});
			} else {
				saveDocument(fileName, mimeType, length, relatedEntityType, relatedEntityUuid, bytes);

				if (filesLeftInQueue == 0) {
					Notification.show(I18nProperties.getString(Strings.headingUploadSuccess), Notification.Type.TRAY_NOTIFICATION);
					if (callback != null) {
						callback.run();
					}
				}
			}
		} catch (Exception e) {
			new Notification(
				I18nProperties.getString(Strings.headingImportError),
				I18nProperties.getString(Strings.messageImportError),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			throw new RuntimeException(e);
		}
	}

	private void saveDocument(
		String fileName,
		String mimeType,
		Long length,
		DocumentRelatedEntityType relatedEntityType,
		String relatedEntityUuid,
		byte[] bytes)
		throws Exception {
		DocumentDto document = DocumentDto.build();
		document.setUploadingUser(UserProvider.getCurrent().getUserReference());
		document.setName(fileName);
		document.setMimeType(mimeType);
		document.setSize(length);
		document.setRelatedEntityType(relatedEntityType);
		document.setRelatedEntityUuid(relatedEntityUuid);

		FacadeProvider.getDocumentFacade().saveDocument(document, bytes);
	}
}
