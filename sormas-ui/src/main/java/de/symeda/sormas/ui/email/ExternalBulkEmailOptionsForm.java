/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.ui.email;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.vaadin.hene.popupbutton.PopupButton;

import com.google.common.io.ByteStreams;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Upload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.docgeneneration.AttachementReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentRelatedEntitiesDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.externalemail.ExternalEmailOptionsWithAttachmentsDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.importer.DocumentMultiFileUpload;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.MultiSelectFiles;

public class ExternalBulkEmailOptionsForm extends AbstractEditForm<ExternalEmailOptionsWithAttachmentsDto> {

	private static final String UPLOAD_LOC = "uploadLoc";
	private static final int MAX_ATTACHMENT_NUMBER = 5;
	private static final String HTML_LAYOUT = fluidRowLocs(ExternalEmailOptionsWithAttachmentsDto.TEMPLATE_NAME)
		+ fluidRowLocs(UPLOAD_LOC)
		+ fluidRowLocs(ExternalEmailOptionsWithAttachmentsDto.ATTACHED_DOCUMENTS);

	private final DocumentWorkflow documentWorkflow;
	private final DocumentRelatedEntityType documentRelatedEntityType;
	public MultiSelectFiles<AttachementReferenceDto> attachedDocumentsField;

	protected Upload upload;
	private PopupButton mainButton;

	protected ExternalBulkEmailOptionsForm(DocumentWorkflow documentWorkflow, DocumentRelatedEntityType documentRelatedEntityType) {
		super(ExternalEmailOptionsWithAttachmentsDto.class, ExternalEmailOptionsWithAttachmentsDto.I18N_PREFIX, false);
		this.documentWorkflow = documentWorkflow;
		this.documentRelatedEntityType = documentRelatedEntityType;
		this.attachedDocumentsField = new MultiSelectFiles<>();

		addFields();
		hideValidationUntilNextCommit();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		ComboBox templateCombo = addField(ExternalEmailOptionsWithAttachmentsDto.TEMPLATE_NAME, ComboBox.class);
		templateCombo.setRequired(true);
		List<String> templateNames = FacadeProvider.getExternalEmailFacade().getTemplateNames(documentWorkflow);
		FieldHelper.updateItems(templateCombo, templateNames);

		if (Arrays.asList(DocumentWorkflow.CASE_EMAIL, DocumentWorkflow.CONTACT_EMAIL, DocumentWorkflow.TRAVEL_ENTRY_EMAIL)
			.contains(documentWorkflow)) {
			getContent().addComponent(buildUploadButton(), UPLOAD_LOC);
		}

		if (documentRelatedEntityType != null) {
			attachedDocumentsField = addField(ExternalEmailOptionsWithAttachmentsDto.ATTACHED_DOCUMENTS, MultiSelectFiles.class);
			attachedDocumentsField.setDeleteAttachmentCallBack(SormasUI::refreshView);
		}
	}

	private Button buildUploadButton() {
		VerticalLayout uploadLayout = new VerticalLayout();
		uploadLayout.setSpacing(true);
		uploadLayout.setMargin(true);
		uploadLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);

		mainButton =
			ButtonHelper.createIconPopupButton(Captions.documentUploadDocument, VaadinIcons.PLUS_CIRCLE, uploadLayout, ValoTheme.BUTTON_PRIMARY);

		boolean multipleUpload = UiUtil.enabled(FeatureType.DOCUMENTS_MULTI_UPLOAD);
		AtomicInteger noOfUploadFiles = new AtomicInteger();
		final boolean[] limitFiveInfoPopUpShown = {
			false };

		UploadStateWindow uploadStateWindow = new UploadStateWindow();
		MultiFileUpload multiFileUpload = new DocumentMultiFileUpload(() -> {
			mainButton.setButtonClickTogglesPopupVisibility(false);
			mainButton.setClosePopupOnOutsideClick(false);
		}, (inputStream, fileName, mimeType, length, filesLeftInQueue) -> {
			int attachedFilesBeforeNextAdd = attachedDocumentsField.getSelectedItemsWithCaption().size();
			noOfUploadFiles.set(attachedFilesBeforeNextAdd);
			noOfUploadFiles.getAndIncrement();

			List<String> acceptedFileExtensions = Arrays.asList(".docx", ".pdf", ".jpg", ".png", ".gif");

			String fileExtension = FacadeProvider.getDocumentFacade().getFileExtension(fileName);

			if (acceptedFileExtensions.contains(fileExtension)) {
				if (noOfUploadFiles.get() <= MAX_ATTACHMENT_NUMBER) {
					DocumentDto document = DocumentDto.build();
					document.setName(fileName);
					document.setMimeType(mimeType);
					document.setSize(length);
					document.setUploadingUser(FacadeProvider.getUserFacade().getCurrentUser().toReference());
					DocumentRelatedEntitiesDto documentRelatedEntitiesDto = new DocumentRelatedEntitiesDto();
					documentRelatedEntitiesDto.setRelatedEntityType(documentRelatedEntityType);

					attachedDocumentsField.addSelectedItemWithCaption(new AttachementReferenceDto(document, getContent(inputStream)), fileName);
				} else {
					if (noOfUploadFiles.get() == 6 && !limitFiveInfoPopUpShown[0]) {
						limitFiveInfoPopUpShown[0] = true;
						VaadinUiUtil.showSimplePopupWindow(
							I18nProperties.getString(Strings.headingBulkEmailMaxAttachedFiles),
							I18nProperties.getString(Strings.messageBulkEmailTooManySelectedAtachments),
							ContentMode.HTML,
							620);
					}
				}
			} else {
				StringBuilder fileType = new StringBuilder();
				acceptedFileExtensions.forEach(attachedFileAcceptedExtension -> {
					fileType.append(attachedFileAcceptedExtension);
					fileType.append(", ");
				});
				fileType.replace(fileType.length() - 2, fileType.length() - 1, "");
				String fileNameLabel = String.format(I18nProperties.getString(Strings.messageBulkEmailWrongAttachmentExtension), fileName, fileType);
				VaadinUiUtil
					.showSimplePopupWindow(I18nProperties.getString(Strings.headingBulkEmailWrongFileType), fileNameLabel, ContentMode.HTML, 500);
			}

		}, uploadStateWindow, multipleUpload);
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

	private static byte[] getContent(InputStream inputStream) {
		try {
			return ByteStreams.toByteArray(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected ExternalEmailOptionsWithAttachmentsDto getInternalValue() {
		return super.getInternalValue();
	}
}
