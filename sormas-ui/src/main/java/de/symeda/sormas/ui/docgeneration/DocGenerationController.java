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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.EventDocumentFacade;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DocGenerationController {

	public DocGenerationController() {
	}

	public void showQuarantineOrderDocumentDialog(ReferenceDto referenceDto, DocumentWorkflow workflow, SampleCriteria sampleCriteria) {
		showDialog(new QuarantineOrderLayout(workflow, sampleCriteria, (templateFile, sample, pathogenTest, extraProperties) -> {
			QuarantineOrderFacade quarantineOrderFacade = FacadeProvider.getQuarantineOrderFacade();

			return new ByteArrayInputStream(
				quarantineOrderFacade.getGeneratedDocument(templateFile, workflow, referenceDto, sample, pathogenTest, extraProperties));
		}, (templateFile) -> getDocumentFileName(referenceDto, templateFile)));
	}

	public void showQuarantineOrderDocumentDialog(List<ReferenceDto> referenceDtos, DocumentWorkflow workflow) {
		String filename = DownloadUtil.createFileNameWithCurrentDate(ExportEntityName.DOCUMENTS, ".zip");
		showDialog(new QuarantineOrderLayout(workflow, null, (templateFile, sample, pathogenTest, extraProperties) -> {
			QuarantineOrderFacade quarantineOrderFacade = FacadeProvider.getQuarantineOrderFacade();

			Map<ReferenceDto, byte[]> generatedDocumentContents =
				quarantineOrderFacade.getGeneratedDocuments(templateFile, workflow, referenceDtos, extraProperties);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (ZipOutputStream zos = new ZipOutputStream(baos)) {

				for (Map.Entry<ReferenceDto, byte[]> referenceDocumentContent : generatedDocumentContents.entrySet()) {
					ZipEntry entry = new ZipEntry(getDocumentFileName(referenceDocumentContent.getKey(), templateFile));
					zos.putNextEntry(entry);

					zos.write(referenceDocumentContent.getValue());
					zos.closeEntry();
				}

				zos.finish();
				zos.flush();

				return new ByteArrayInputStream(baos.toByteArray());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}, (templateFile) -> filename));
	}

	public void showEventDocumentDialog(EventReferenceDto eventReferenceDto) {
		showDialog(
			new EventDocumentLayout((templateFileName) -> getDocumentFileName(eventReferenceDto, templateFileName), (templateFile, properties) -> {
				EventDocumentFacade eventDocumentFacade = FacadeProvider.getEventDocumentFacade();

				return new ByteArrayInputStream(
					eventDocumentFacade.getGeneratedDocument(templateFile, eventReferenceDto, properties).getBytes(StandardCharsets.UTF_8));
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
