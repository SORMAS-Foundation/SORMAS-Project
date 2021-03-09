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
package de.symeda.sormas.ui.document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentFacade;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.importer.DocumentReceiver;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DocumentListComponent extends VerticalLayout {

	private final DocumentRelatedEntityType relatedEntityType;
	private final ReferenceDto entityRef;
	private final UserRight editRight;

	private final VerticalLayout listLayout;

	public DocumentListComponent(DocumentRelatedEntityType relatedEntityType, ReferenceDto entityRef, UserRight editRight) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		this.relatedEntityType = relatedEntityType;
		this.entityRef = entityRef;
		this.editRight = editRight;

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		listLayout = new VerticalLayout();
		listLayout.setSpacing(true);
		listLayout.setMargin(false);
		addComponent(listLayout);
		reload();

		Label documentsHeader = new Label(I18nProperties.getString(Strings.entityDocuments));
		documentsHeader.addStyleName(CssStyles.H3);
		componentHeader.addComponent(documentsHeader);

		if (((SormasUI) getUI()).getUserProvider().hasUserRight(editRight)) {
			Button uploadButton = buildUploadButton();
			componentHeader.addComponent(uploadButton);
			componentHeader.setComponentAlignment(uploadButton, Alignment.MIDDLE_RIGHT);
		}
	}

	private Button buildUploadButton() {
		VerticalLayout uploadLayout = new VerticalLayout();
		uploadLayout.setSpacing(true);
		uploadLayout.setMargin(true);
		uploadLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
		uploadLayout.setWidth(250, Unit.PIXELS);

		DocumentReceiver receiver = new DocumentReceiver(relatedEntityType, entityRef.getUuid(), this::reload);
		Upload upload = new Upload("", receiver);
		receiver.setUpload(upload);
		upload.setButtonCaption(I18nProperties.getCaption(Captions.importImportData));
		CssStyles.style(upload, CssStyles.VSPACE_2);

		uploadLayout.addComponentsAndExpand(upload);

		return ButtonHelper.createIconPopupButton(Captions.documentUploadDocument, VaadinIcons.PLUS_CIRCLE, uploadLayout, ValoTheme.BUTTON_PRIMARY);
	}

	private void reload() {
		List<DocumentDto> docs = FacadeProvider.getDocumentFacade().getDocumentsRelatedToEntity(relatedEntityType, entityRef.getUuid());
		listLayout.removeAllComponents();
		if (docs.isEmpty()) {
			Label noActionsLabel = new Label(String.format(I18nProperties.getCaption(Captions.documentNoDocuments), relatedEntityType.toString()));
			setSpacing(false);
			listLayout.addComponent(noActionsLabel);
		} else {
			setSpacing(true);
			listLayout.addComponents(docs.stream().map(this::toComponent).toArray(Component[]::new));
		}
	}

	private Component toComponent(DocumentDto document) {
		HorizontalLayout res = new HorizontalLayout();
		res.setSpacing(true);
		res.setMargin(false);
		res.setWidth(100, Unit.PERCENTAGE);

		// TODO: show content-type and/or size?
		Label nameLabel = new Label(DataHelper.toStringNullable(document.getName()));
		res.addComponent(nameLabel);
		res.setExpandRatio(nameLabel, 1);

		Button downloadButton = buildDownloadButton(document);
		res.addComponent(downloadButton);
		res.setExpandRatio(downloadButton, 0);

		if (((SormasUI) getUI()).getUserProvider().hasUserRight(editRight)) {
			Button deleteButton = buildDeleteButton(document);
			res.addComponent(deleteButton);
			res.setExpandRatio(deleteButton, 0);
		}

		return res;
	}

	private Button buildDownloadButton(DocumentDto document) {
		Button viewButton = new Button(VaadinIcons.DOWNLOAD);

		StreamResource streamResource = new StreamResource((StreamResource.StreamSource) () -> {
			DocumentFacade documentFacade = FacadeProvider.getDocumentFacade();
			try {
				return new ByteArrayInputStream(documentFacade.read(document.getUuid()));
			} catch (IOException | IllegalArgumentException e) {
				new Notification(
					String.format(I18nProperties.getString(Strings.errorReadingDocument), document),
					e.getMessage(),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
				return null;
			}
		}, document.getName());
		streamResource.setMIMEType(document.getMimeType());

		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(viewButton);
		fileDownloader.setFileDownloadResource(streamResource);

		return viewButton;
	}

	private Button buildDeleteButton(DocumentDto document) {
		return ButtonHelper.createIconButton(
			"",
			VaadinIcons.TRASH,
			e -> VaadinUiUtil
				.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeleteFile), document.getName()), () -> {
					try {
						FacadeProvider.getDocumentFacade().deleteDocument(document.getUuid());
					} catch (IllegalArgumentException ex) {
						new Notification(
							I18nProperties.getString(Strings.errorDeletingDocument),
							ex.getMessage(),
							Notification.Type.ERROR_MESSAGE,
							false).show(Page.getCurrent());
					}
					reload();
				}));
	}
}
