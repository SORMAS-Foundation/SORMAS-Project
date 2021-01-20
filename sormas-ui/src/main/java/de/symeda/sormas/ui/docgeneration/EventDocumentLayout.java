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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.EventDocumentFacade;
import de.symeda.sormas.api.event.EventReferenceDto;

public class EventDocumentLayout extends AbstractDocgenerationLayout {

	private final EventReferenceDto eventReferenceDto;

	public EventDocumentLayout(EventReferenceDto eventReferenceDto) {
		// TODO: i18n
		super("Ereignis-Formular");
		this.eventReferenceDto = eventReferenceDto;
		init();
	}

	@Override
	protected List<String> getAvailableTemplates() {
		return FacadeProvider.getEventDocumentFacade().getAvailableTemplates();
	}

	@Override
	protected String generateFilename(String templateFile) {
		String uuid = eventReferenceDto.getUuid();
		return uuid.substring(0, Math.min(5, uuid.length())) + "_" + templateFile;
	}

	@Override
	protected DocumentVariables getDocumentVariables(String templateFile) throws IOException {
		return FacadeProvider.getEventDocumentFacade().getDocumentVariables(templateFile);
	}

	@Override
	protected StreamResource createStreamResource(String templateFile, String filename) {
		return new StreamResource((StreamResource.StreamSource) () -> {
			EventDocumentFacade eventDocumentFacade = FacadeProvider.getEventDocumentFacade();
			try {
				return new ByteArrayInputStream(
					eventDocumentFacade.getGeneratedDocument(templateFile, eventReferenceDto, readAdditionalVariables()).getBytes());
			} catch (IOException | IllegalArgumentException e) {
				new Notification("Document generation failed", e.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
				return null;
			}
		}, filename);
	}

	@Override
	protected String getWindowCaption() {
		// TODO: I18N
		return "Ereignis-Formular erstellen";
	}
}
