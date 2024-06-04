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
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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

import de.symeda.sormas.api.DocumentHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.EmailAttachementDto;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.document.DocumentDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.externalemail.ExternalEmailOptionsWithAttachmentsDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.docgeneration.QuarantineOrderLayout;
import de.symeda.sormas.ui.importer.DocumentMultiFileUpload;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.MultiSelectFiles;

public class ExternalBulkEmailOptionsForm extends AbstractEditForm<ExternalEmailOptionsWithAttachmentsDto> {

	private static final String UPLOAD_LOC = "uploadLoc";
	public static final int MAX_ATTACHMENT_NUMBER = 5;
	public static final String CUSTOM_EMAIL_ATTACHMENT_DOCUMENT = "customEmailAttachmentDocument";

	private static final String HTML_LAYOUT = fluidRowLocs(ExternalEmailOptionsWithAttachmentsDto.TEMPLATE_NAME)
		+ fluidRowLocs(UPLOAD_LOC)
		+ fluidRowLocs(ExternalEmailOptionsWithAttachmentsDto.ATTACHED_DOCUMENTS)
		+ fluidRowLocs(CUSTOM_EMAIL_ATTACHMENT_DOCUMENT);

	private final DocumentWorkflow documentWorkflow;
	private final DocumentRelatedEntityType documentRelatedEntityType;
	public MultiSelectFiles<EmailAttachementDto> attachedDocumentsField;

	protected Upload upload;
	private PopupButton mainButton;

	private RootEntityType rootEntityType;

	private QuarantineOrderLayout attachDocTemplateLayout;

	private final DocumentWorkflow templatesWorkflow;

	protected ExternalBulkEmailOptionsForm(
		DocumentWorkflow documentWorkflow,
		DocumentRelatedEntityType documentRelatedEntityType,
		RootEntityType rootEntityType, DocumentWorkflow templatesWorkflow) {
		super(ExternalEmailOptionsWithAttachmentsDto.class, ExternalEmailOptionsWithAttachmentsDto.I18N_PREFIX, false);
		this.documentWorkflow = documentWorkflow;
		this.documentRelatedEntityType = documentRelatedEntityType;
		this.attachedDocumentsField = new MultiSelectFiles<>();
		this.rootEntityType = rootEntityType;
		this.templatesWorkflow = templatesWorkflow;

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
		}

		if (templatesWorkflow != null) {
			attachDocTemplateLayout = new QuarantineOrderLayout(templatesWorkflow);
			attachDocTemplateLayout.setMargin(false);
			getContent().addComponent(attachDocTemplateLayout, CUSTOM_EMAIL_ATTACHMENT_DOCUMENT);
		}
	}

	@Override
	public ExternalEmailOptionsWithAttachmentsDto getValue() {
		ExternalEmailOptionsWithAttachmentsDto value = super.getValue();
		if (attachDocTemplateLayout != null) {
			value.setQuarantineOrderDocumentOptionsDto(attachDocTemplateLayout.getFieldValues());
		}
		return value;
	}

	private Button buildUploadButton() {
		VerticalLayout uploadLayout = new VerticalLayout();
		uploadLayout.setSpacing(true);
		uploadLayout.setMargin(true);
		uploadLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);

		mainButton =
			ButtonHelper.createIconPopupButton(Captions.documentUploadDocument, VaadinIcons.PLUS_CIRCLE, uploadLayout, ValoTheme.BUTTON_PRIMARY);

		boolean multipleUpload = UiUtil.enabled(FeatureType.DOCUMENTS_MULTI_UPLOAD);
		AtomicBoolean limitInfoPopUpShown = new AtomicBoolean(false);

		UploadStateWindow uploadStateWindow = new UploadStateWindow();
		MultiFileUpload multiFileUpload = new DocumentMultiFileUpload(() -> {
			mainButton.setButtonClickTogglesPopupVisibility(false);
			mainButton.setClosePopupOnOutsideClick(false);
		}, (inputStream, fileName, mimeType, length, filesLeftInQueue) -> {
			int alreadyAttachedCount = attachedDocumentsField.getSelectedItemsWithCaption().size();

			Set<String> acceptedFileExtensions = FacadeProvider.getExternalEmailFacade().getAttachableFileExtensions();
			String fileExtension = DocumentHelper.getFileExtension(fileName);

			if (acceptedFileExtensions.contains(fileExtension)) {
				if (alreadyAttachedCount < MAX_ATTACHMENT_NUMBER) {
					DocumentDto document = DocumentDto.build();
					document.setName(fileName);
					document.setMimeType(mimeType);
					document.setSize(length);
					document.setUploadingUser(FacadeProvider.getUserFacade().getCurrentUser().toReference());
					DocumentRelatedEntityDto documentRelatedEntityDto = new DocumentRelatedEntityDto();
					documentRelatedEntityDto.setRelatedEntityType(documentRelatedEntityType);

					attachedDocumentsField.addSelectedItemWithCaption(new EmailAttachementDto(document, getContent(inputStream)), fileName);
				} else if (!limitInfoPopUpShown.get()) {
					limitInfoPopUpShown.set(true);
					VaadinUiUtil.showSimplePopupWindow(
						I18nProperties.getString(Strings.headingBulkEmailMaxAttachedFiles),
						String.format(
							I18nProperties.getString(Strings.messageBulkEmailTooManySelectedAtachments),
							MAX_ATTACHMENT_NUMBER,
							MAX_ATTACHMENT_NUMBER),
						ContentMode.HTML,
						620);
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
					.showSimplePopupWindow(I18nProperties.getString(Strings.headingBulkEmailWrongFileType), fileNameLabel, ContentMode.HTML, 520);
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
}
