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

package de.symeda.sormas.ui.configuration.docgeneration;

import java.util.Map;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.Upload;

import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.importer.DocumentTemplateReceiver;
import de.symeda.sormas.ui.importer.ImportLayoutComponent;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/*
 * Layout for Uploading Templates
 */
public class DocumentTemplateUploadLayout extends VerticalLayout {

	protected Upload upload;
	private ImportLayoutComponent importGuideComponent;
	private final DocumentWorkflow documentWorkflow;

	private static final Map<DocumentWorkflow, DocumentTemplateInfoData> templateInfoData = Map.ofEntries(
		Map.entry(
			DocumentWorkflow.QUARANTINE_ORDER_CASE,
			DocumentTemplateInfoData.forDocumentTemplate(Captions.DocumentTemplate_exampleTemplateCases, "ExampleDocumentTemplateCases.docx")),
		Map.entry(
			DocumentWorkflow.QUARANTINE_ORDER_CONTACT,
			DocumentTemplateInfoData.forDocumentTemplate(Captions.DocumentTemplate_exampleTemplateContacts, "ExampleDocumentTemplateContacts.docx")),
		Map.entry(
			DocumentWorkflow.QUARANTINE_ORDER_EVENT_PARTICIPANT,
			DocumentTemplateInfoData
				.forDocumentTemplate(Captions.DocumentTemplate_exampleTemplateEventParticipants, "ExampleDocumentTemplateEventParticipant.docx")),
		Map.entry(
			DocumentWorkflow.QUARANTINE_ORDER_TRAVEL_ENTRY,
			DocumentTemplateInfoData
				.forDocumentTemplate(Captions.DocumentTemplate_exampleTemplateTravelEntries, "ExampleDocumentTemplateTravelEntry.docx")),
		Map.entry(
			DocumentWorkflow.EVENT_HANDOUT,
			DocumentTemplateInfoData
				.forDocumentTemplate(Captions.DocumentTemplate_exampleTemplateEventHandout, "ExampleDocumentTemplateEventHandout.html")),
		Map.entry(
			DocumentWorkflow.CASE_EMAIL,
			DocumentTemplateInfoData.forEmailTemplate(Captions.DocumentTemplate_exampleTemplateCaseEmail, "ExampleDocumentTemplateCaseEmail.txt")),
		Map.entry(
			DocumentWorkflow.CONTACT_EMAIL,
			DocumentTemplateInfoData
				.forEmailTemplate(Captions.DocumentTemplate_exampleTemplateContactEmail, "ExampleDocumentTemplateContactEmail.txt")),
		Map.entry(
			DocumentWorkflow.EVENT_PARTICIPANT_EMAIL,
			DocumentTemplateInfoData.forEmailTemplate(
				Captions.DocumentTemplate_exampleTemplateEventParticipantEmail,
				"ExampleDocumentTemplateEventParticipantEmail.txt")),
		Map.entry(
			DocumentWorkflow.TRAVEL_ENTRY_EMAIL,
			DocumentTemplateInfoData
				.forEmailTemplate(Captions.DocumentTemplate_exampleTemplateTravelEntryEmail, "ExampleDocumentTemplateTravelEntryEmail.txt")));

	public DocumentTemplateUploadLayout(DocumentWorkflow documentWorkflow) {
		super();
		this.documentWorkflow = documentWorkflow;
		addDownloadResourcesComponent();
		addUploadResourceComponent();
	}

	protected void addDownloadResourcesComponent() {
		importGuideComponent = new ImportLayoutComponent(
			1,
			I18nProperties.getString(Strings.headingDownloadDocumentTemplateGuide),
			I18nProperties.getString(Strings.infoDownloadDocumentTemplateImportGuide),
			VaadinIcons.FILE_PRESENTATION,
			I18nProperties.getCaption(Captions.DocumentTemplate_documentTemplateGuide));

		Button button = importGuideComponent.getButton();
		addFileDownloader(button, new ClassResource("/" + templateInfoData.get(documentWorkflow).guideFileName));

		addExampleTemplatesQuarantineOrder();

		addDownloadResource(Captions.importDownloadDataDictionary, VaadinIcons.FILE_TABLE, new ClassResource("/doc/SORMAS_Data_Dictionary.xlsx"));

		addComponent(importGuideComponent);
	}

	private void addExampleTemplatesQuarantineOrder() {
		DocumentTemplateInfoData templateInfo = DocumentTemplateUploadLayout.templateInfoData.get(documentWorkflow);
		addDownloadResource(templateInfo.captionKey, VaadinIcons.FILE_TEXT, new ClassResource("/" + templateInfo.fileName));
	}

	private void addUploadResourceComponent() {
		String headline = I18nProperties.getCaption(Captions.DocumentTemplate_uploadTemplate);
		String infoText = String.format(I18nProperties.getString(Strings.infoUploadDocumentTemplate), documentWorkflow.getFileExtension());

		ImportLayoutComponent uploadTemplateComponent = new ImportLayoutComponent(2, headline, infoText, null, null);
		addComponent(uploadTemplateComponent);

		DocumentTemplateReceiver receiver = new DocumentTemplateReceiver(documentWorkflow);
		upload = new Upload("", receiver);
		upload.setButtonCaption(I18nProperties.getCaption(Captions.DocumentTemplate_buttonUploadTemplate));
		CssStyles.style(upload, CssStyles.VSPACE_2);
		upload.addStartedListener(receiver);
		upload.addSucceededListener(receiver);
		addComponent(upload);
	}

	private void addDownloadResource(String caption, VaadinIcons icon, ClassResource resource) {
		Button exampleTemplateWordButton = ButtonHelper.createIconButton(caption, icon, null, ValoTheme.BUTTON_PRIMARY, CssStyles.VSPACE_TOP_3);
		addFileDownloader(exampleTemplateWordButton, resource);
		importGuideComponent.addComponent(exampleTemplateWordButton);
	}

	private void addFileDownloader(Button button, ClassResource importGuideResource) {
		FileDownloader importGuideDownloader = new FileDownloader(importGuideResource);
		importGuideDownloader.extend(button);
	}

	private static final class DocumentTemplateInfoData {

		private final String captionKey;
		private final String fileName;
		private final String guideFileName;
		private final String headingTextKey;
		private final String infoTextKey;

		public static DocumentTemplateInfoData forDocumentTemplate(String caption, String fileName) {
			return new DocumentTemplateInfoData(caption, fileName, "SORMAS_Document_Template_Guide.pdf",
					Strings.headingDownloadDocumentTemplateGuide,
					Strings.infoDownloadDocumentTemplateImportGuide
					);
		}

		public static DocumentTemplateInfoData forEmailTemplate(String caption, String fileName) {
			return new DocumentTemplateInfoData(caption, fileName, "SORMAS_Email_Template_Guide.pdf",
					Strings.headingDownloadEmailTemplateGuide,
					Strings.infoDownloadEmailTemplateImportGuide
			);
		}

		public static DocumentTemplateInfoData of(String caption, String fileName, String guideFileName, String headingTextKey, String infoTextKey) {
			return new DocumentTemplateInfoData(caption, fileName, guideFileName, headingTextKey, infoTextKey);
		}

		private DocumentTemplateInfoData(String captionKey, String fileName, String guideFileName, String headingTextKey, String infoTextKey) {
			this.captionKey = captionKey;
			this.fileName = fileName;
			this.guideFileName = guideFileName;
			this.headingTextKey = headingTextKey;
			this.infoTextKey = infoTextKey;
		}
	}
}
