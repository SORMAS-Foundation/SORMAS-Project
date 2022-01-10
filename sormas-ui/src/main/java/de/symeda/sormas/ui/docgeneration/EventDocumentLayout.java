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

package de.symeda.sormas.ui.docgeneration;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.document.DocumentListComponent;

public class EventDocumentLayout extends AbstractDocgenerationLayout {

	private final DocumentInputStreamSupplier documentInputStreamSupplier;
	private DocumentListComponent documentListComponent;

	public EventDocumentLayout(
		DocumentListComponent documentListComponent,
		Function<String, String> fileNameFunction,
		DocumentInputStreamSupplier documentInputStreamSupplier) {
		super(I18nProperties.getCaption(Captions.DocumentTemplate_EventHandout), fileNameFunction, isNull(documentListComponent));

		this.documentListComponent = documentListComponent;
		this.documentInputStreamSupplier = documentInputStreamSupplier;

		init();
	}

	@Override
	protected List<String> getAvailableTemplates() {
		return FacadeProvider.getEventDocumentFacade().getAvailableTemplates();
	}

	@Override
	protected DocumentVariables getDocumentVariables(String templateFile) throws DocumentTemplateException {
		return FacadeProvider.getEventDocumentFacade().getDocumentVariables(templateFile);
	}

	@Override
	protected StreamResource createStreamResource(String templateFile, String filename) {
		return new StreamResource((StreamResource.StreamSource) () -> {
			try {
				return documentInputStreamSupplier.get(templateFile, readAdditionalVariables(), checkBoxUploadGeneratedDoc.getValue());
			} catch (Exception e) {
				new Notification(I18nProperties.getString(Strings.errorProcessingTemplate), e.getMessage(), Notification.Type.ERROR_MESSAGE)
					.show(Page.getCurrent());
				return null;
			} finally {
				if (nonNull(documentListComponent)) {
					documentListComponent.reload();
				}
			}
		}, filename);
	}

	@Override
	protected String getWindowCaption() {
		return I18nProperties.getCaption(Captions.DocumentTemplate_EventHandout_create);
	}

	interface DocumentInputStreamSupplier {

		InputStream get(String templateFile, Properties properties, Boolean shouldUploadGeneratedDoc) throws DocumentTemplateException;
	}
}
