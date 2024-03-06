package de.symeda.sormas.ui.document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

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
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DocumentList extends VerticalLayout {

	private final DocumentRelatedEntityType relatedEntityType;
	private final ReferenceDto entityRef;
	private final UserRight editRight;
	private final boolean pseudonymized;
	private final boolean isEditAllowed;
	private final boolean isDeleteAllowed;

	public DocumentList(
		DocumentRelatedEntityType relatedEntityType,
		ReferenceDto entityRef,
		UserRight editRight,
		boolean pseudonymized,
		boolean isEditAllowed,
		boolean isDeleteAllowed) {
		this.relatedEntityType = relatedEntityType;
		this.entityRef = entityRef;
		this.editRight = editRight;
		this.pseudonymized = pseudonymized;
		this.isEditAllowed = isEditAllowed;
		this.isDeleteAllowed = isDeleteAllowed;

		setSpacing(true);
		setMargin(false);
	}

	public void reload() {
		List<DocumentDto> docs = Collections.emptyList();
		if (!pseudonymized) {
			docs = FacadeProvider.getDocumentFacade().getDocumentsRelatedToEntity(relatedEntityType, entityRef.getUuid());
		}
		removeAllComponents();
		if (docs.isEmpty()) {
			Label noActionsLabel = new Label(String.format(I18nProperties.getCaption(Captions.documentNoDocuments), relatedEntityType.toString()));
			setSpacing(false);
			addComponent(noActionsLabel);
		} else {
			setSpacing(true);
			addComponents(docs.stream().map(this::toComponent).toArray(Component[]::new));
		}
	}

	private Component toComponent(DocumentDto document) {
		HorizontalLayout res = new HorizontalLayout();
		res.setSpacing(true);
		res.setMargin(false);
		res.setWidth(100, Unit.PERCENTAGE);

		// TODO: show content-type and/or size?
		Label nameLabel = new Label(DataHelper.toStringNullable(document.getName()));
		nameLabel.addStyleName(CssStyles.LABEL_CAPTION_TRUNCATED);
		res.addComponent(nameLabel);
		res.setExpandRatio(nameLabel, 1);

		Button downloadButton = buildDownloadButton(document);
		res.addComponent(downloadButton);
		res.setExpandRatio(downloadButton, 0);

		if (UiUtil.permitted(editRight, UserRight.DOCUMENT_DELETE) && isDeleteAllowed) {
			Button deleteButton = buildDeleteButton(document);
			res.addComponent(deleteButton);
			res.setExpandRatio(deleteButton, 0);
		} else {
			nameLabel.setEnabled(false);
		}

		return res;
	}

	private Button buildDownloadButton(DocumentDto document) {
		Button viewButton = ButtonHelper.createIconButton(VaadinIcons.DOWNLOAD);

		StreamResource streamResource = new StreamResource((StreamResource.StreamSource) () -> {
			DocumentFacade documentFacade = FacadeProvider.getDocumentFacade();
			try {
				return new ByteArrayInputStream(documentFacade.getContent(document.getUuid()));
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
