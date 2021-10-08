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
package de.symeda.sormas.ui.document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.vaadin.hene.popupbutton.PopupButton;

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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentFacade;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.importer.DocumentMultiFileUpload;
import de.symeda.sormas.ui.importer.DocumentUploadFinishedHandler;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DocumentListComponent extends VerticalLayout {

	private final DocumentRelatedEntityType relatedEntityType;
	private final ReferenceDto entityRef;
	private final UserRight editRight;
	private final boolean pseudonymized;

	private final VerticalLayout listLayout;

	public DocumentListComponent(DocumentRelatedEntityType relatedEntityType, ReferenceDto entityRef, UserRight editRight, boolean pseudonymized) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		this.relatedEntityType = relatedEntityType;
		this.entityRef = entityRef;
		this.editRight = editRight;
		this.pseudonymized = pseudonymized;

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

		if (UserProvider.getCurrent().hasUserRight(editRight)) {
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

		PopupButton mainButton =
			ButtonHelper.createIconPopupButton(Captions.documentUploadDocument, VaadinIcons.PLUS_CIRCLE, uploadLayout, ValoTheme.BUTTON_PRIMARY);

		boolean multipleUpload = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.DOCUMENTS_MULTI_UPLOAD);

		UploadStateWindow uploadStateWindow = new UploadStateWindow();
		MultiFileUpload multiFileUpload = new DocumentMultiFileUpload(() -> {
			mainButton.setButtonClickTogglesPopupVisibility(false);
			mainButton.setClosePopupOnOutsideClick(false);
		}, new DocumentUploadFinishedHandler(relatedEntityType, entityRef.getUuid(), this::reload), uploadStateWindow, multipleUpload);
		multiFileUpload
			.setUploadButtonCaptions(I18nProperties.getCaption(Captions.importImportData), I18nProperties.getCaption(Captions.importImportData));
		multiFileUpload.setAllUploadFinishedHandler(() -> {
			mainButton.setButtonClickTogglesPopupVisibility(true);
			mainButton.setClosePopupOnOutsideClick(true);
			mainButton.setPopupVisible(false);
		});

		uploadLayout.addComponentsAndExpand(multiFileUpload);

		return mainButton;
	}

	private void reload() {
		List<DocumentDto> docs = Collections.emptyList();
		if (!pseudonymized) {
			docs = FacadeProvider.getDocumentFacade().getDocumentsRelatedToEntity(relatedEntityType, entityRef.getUuid());
		}
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
		nameLabel.addStyleName(CssStyles.LABEL_WHITE_SPACE_NORMAL);
		res.addComponent(nameLabel);
		res.setExpandRatio(nameLabel, 1);

		Button downloadButton = buildDownloadButton(document);
		res.addComponent(downloadButton);
		res.setExpandRatio(downloadButton, 0);

		if (UserProvider.getCurrent().hasUserRight(editRight)) {
			Button deleteButton = buildDeleteButton(document);
			res.addComponent(deleteButton);
			res.setExpandRatio(deleteButton, 0);
		}

		return res;
	}

	private Button buildDownloadButton(DocumentDto document) {
		Button viewButton = ButtonHelper.createIconButton(VaadinIcons.DOWNLOAD);

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
