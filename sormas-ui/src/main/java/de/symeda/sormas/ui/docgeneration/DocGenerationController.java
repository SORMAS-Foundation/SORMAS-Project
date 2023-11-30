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

package de.symeda.sormas.ui.docgeneration;

import static de.symeda.sormas.ui.docgeneration.DocGenerationHelper.isFileSizeLimitExceeded;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.EventDocumentFacade;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.vaccination.VaccinationCriteria;
import de.symeda.sormas.ui.document.DocumentListComponent;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DocGenerationController {

	public DocGenerationController() {
	}

	public void showQuarantineOrderDocumentDialog(
		RootEntityType rootEntityType,
		ReferenceDto referenceDto,
		DocumentWorkflow workflow,
		SampleCriteria sampleCriteria,
		VaccinationCriteria vaccinationCriteria,
		DocumentListComponent documentListComponent) {
		showDialog(
			new QuarantineOrderLayout(
				workflow,
				sampleCriteria,
				vaccinationCriteria,
				documentListComponent,
				(templateFile, sample, pathogenTest, vaccination, extraProperties, shouldUploadGeneratedDoc) -> {
					QuarantineOrderFacade quarantineOrderFacade = FacadeProvider.getQuarantineOrderFacade();

					return new ByteArrayInputStream(
						quarantineOrderFacade.getGeneratedDocument(
							templateFile,
							workflow,
							rootEntityType,
							referenceDto,
							sample,
							pathogenTest,
							vaccination,
							extraProperties,
							shouldUploadGeneratedDoc));
				},
				(templateFile) -> getDocumentFileName(referenceDto, templateFile)));
	}

	public void showBulkQuarantineOrderDocumentDialog(List<ReferenceDto> referenceDtos, DocumentWorkflow workflow) {
		String filename = DownloadUtil.createFileNameWithCurrentDate(ExportEntityName.DOCUMENTS, ".zip");
		showDialog(
			new QuarantineOrderLayout(
				workflow,
				null,
				null,
				null,
				(templateFile, sample, pathogenTest, vaccination, extraProperties, shouldUploadGeneratedDoc) -> {
					QuarantineOrderFacade quarantineOrderFacade = FacadeProvider.getQuarantineOrderFacade();

					Map<ReferenceDto, byte[]> generatedDocumentContents =
						quarantineOrderFacade.getGeneratedDocuments(templateFile, workflow, referenceDtos, extraProperties, shouldUploadGeneratedDoc);

					return generateZip(templateFile, shouldUploadGeneratedDoc, generatedDocumentContents);

				},
				(templateFile) -> filename));
	}

	public void showBulkEventParticipantQuarantineOrderDocumentDialog(List<EventParticipantReferenceDto> referenceDtos, Disease eventDisease) {
		String filename = DownloadUtil.createFileNameWithCurrentDate(ExportEntityName.DOCUMENTS, ".zip");
		showDialog(
			new QuarantineOrderLayout(
				DocumentWorkflow.QUARANTINE_ORDER_EVENT_PARTICIPANT,
				null,
				null,
				null,
				(templateFile, sample, pathogenTest, vaccination, extraProperties, shouldUploadGeneratedDoc) -> {
					QuarantineOrderFacade quarantineOrderFacade = FacadeProvider.getQuarantineOrderFacade();

					Map<ReferenceDto, byte[]> generatedDocumentContents = quarantineOrderFacade.getGeneratedDocumentsForEventParticipants(
						templateFile,
						referenceDtos,
						eventDisease,
						extraProperties,
						shouldUploadGeneratedDoc);

					return generateZip(templateFile, shouldUploadGeneratedDoc, generatedDocumentContents);

				},
				(templateFile) -> filename));
	}

	private ByteArrayInputStream generateZip(
		String templateFile,
		Boolean shouldUploadGeneratedDoc,
		Map<ReferenceDto, byte[]> generatedDocumentContents) {
		long fileSizeLimitMB = FacadeProvider.getConfigFacade().getDocumentUploadSizeLimitMb();
		List<String> fileSizeLimitExceeded = new ArrayList<>();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try (ZipOutputStream zos = new ZipOutputStream(baos)) {

			for (Map.Entry<ReferenceDto, byte[]> referenceDocumentContent : generatedDocumentContents.entrySet()) {
				ReferenceDto referenceDto = referenceDocumentContent.getKey();
				ZipEntry entry = new ZipEntry(getDocumentFileName(referenceDto, templateFile));
				zos.putNextEntry(entry);

				byte[] document = referenceDocumentContent.getValue();
				zos.write(document);
				zos.closeEntry();
				if (shouldUploadGeneratedDoc && isFileSizeLimitExceeded(document.length, fileSizeLimitMB)) {
					fileSizeLimitExceeded.add(referenceDto.getUuid());
				}
			}

			zos.finish();
			zos.flush();

			return new ByteArrayInputStream(baos.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (shouldUploadGeneratedDoc && fileSizeLimitExceeded.size() > 0) {
				buildDocumentUploadWarningWindow(fileSizeLimitExceeded, fileSizeLimitMB);
			}
		}
	}

	private void buildDocumentUploadWarningWindow(List<String> fileSizeLimitExceededCases, long fileSizeLimitMb) {

		VerticalLayout textAreaLayout = new VerticalLayout();
		Label docsNotUploadedLabel = new Label(I18nProperties.getCaption(Captions.DocumentTemplate_notUploaded));

		TextArea limitExceededCases = new TextArea();
		limitExceededCases.setEnabled(false);
		limitExceededCases.setWidth(906, Sizeable.Unit.PIXELS);
		limitExceededCases.setValue(new ArrayList<>(fileSizeLimitExceededCases).toString());

		textAreaLayout.addComponent(docsNotUploadedLabel);
		textAreaLayout.addComponent(limitExceededCases);

		HorizontalLayout buttonBar = new HorizontalLayout();
		Button okButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.actionOkay));
		buttonBar.addComponents(okButton);

		HorizontalLayout fileTooBig = new HorizontalLayout();
		Label fileTooBigLabel = new Label(String.format(I18nProperties.getCaption(Captions.DocumentTemplate_fileTooBig), fileSizeLimitMb));
		fileTooBig.addComponent(fileTooBigLabel);

		VerticalLayout layout = new VerticalLayout();
		Window window = VaadinUiUtil.showPopupWindow(layout);
		okButton.addClickListener((e) -> window.close());
		layout.addComponent(fileTooBig);
		layout.addComponent(textAreaLayout);
		layout.addComponent(buttonBar);
		layout.setComponentAlignment(buttonBar, Alignment.BOTTOM_CENTER);

		window.setCaption(I18nProperties.getCaption(Captions.DocumentTemplate_documentUploadWarning));
		window.setWidth(1024, Sizeable.Unit.PIXELS);
	}

	public void showEventDocumentDialog(EventReferenceDto eventReferenceDto, DocumentListComponent documentListComponent) {
		showDialog(
			new EventDocumentLayout(
				documentListComponent,
				(templateFileName) -> getDocumentFileName(eventReferenceDto, templateFileName),
				(templateFile, properties, shouldUploadGeneratedDoc) -> {
					EventDocumentFacade eventDocumentFacade = FacadeProvider.getEventDocumentFacade();

					return new ByteArrayInputStream(
						eventDocumentFacade.getGeneratedDocument(templateFile, eventReferenceDto, properties, shouldUploadGeneratedDoc)
							.getBytes(StandardCharsets.UTF_8));
				}));
	}

	public void showEventDocumentDialog(List<EventReferenceDto> referenceDtos) {
		String filename = DownloadUtil.createFileNameWithCurrentDate(ExportEntityName.EVENTS, ".zip");

		showDialog(new EventDocumentLayout(null, (templateFile) -> filename, (templateFile, properties, shouldUploadGeneratedDoc) -> {
			EventDocumentFacade eventDocumentFacade = FacadeProvider.getEventDocumentFacade();

			Map<ReferenceDto, byte[]> generatedDocumentContents =
				eventDocumentFacade.getGeneratedDocuments(templateFile, referenceDtos, properties, shouldUploadGeneratedDoc);

			return generateZip(templateFile, shouldUploadGeneratedDoc, generatedDocumentContents);

		}));
	}

	private void showDialog(AbstractDocgenerationLayout docgenerationLayout) {
		Window window = VaadinUiUtil.showPopupWindow(docgenerationLayout);
		window.setWidth(800, Sizeable.Unit.PIXELS);
		window.setCaption(I18nProperties.getCaption(docgenerationLayout.getWindowCaption()));
	}

	private String getDocumentFileName(ReferenceDto eventReferenceDto, String templateFileName) {
		return DataHelper.getShortUuid(eventReferenceDto) + '-' + templateFileName;
	}
}
