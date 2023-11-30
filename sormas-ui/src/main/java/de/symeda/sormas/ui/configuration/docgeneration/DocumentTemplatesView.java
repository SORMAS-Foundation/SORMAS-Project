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

import static de.symeda.sormas.api.docgeneneration.DocumentWorkflow.EVENT_HANDOUT;
import static de.symeda.sormas.api.docgeneneration.DocumentWorkflow.QUARANTINE_ORDER_CASE;
import static de.symeda.sormas.api.docgeneneration.DocumentWorkflow.QUARANTINE_ORDER_CONTACT;
import static de.symeda.sormas.api.docgeneneration.DocumentWorkflow.QUARANTINE_ORDER_EVENT_PARTICIPANT;
import static de.symeda.sormas.api.docgeneneration.DocumentWorkflow.QUARANTINE_ORDER_TRAVEL_ENTRY;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.configuration.AbstractConfigurationView;

public class DocumentTemplatesView extends AbstractConfigurationView {

	private static final long serialVersionUID = -4759099406008618416L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/documentTemplates";

	private VerticalLayout gridLayout;

	public DocumentTemplatesView() {

		super(VIEW_NAME);
		gridLayout = new VerticalLayout(
			new DocumentTemplateSection(QUARANTINE_ORDER_CASE),
			new DocumentTemplateSection(QUARANTINE_ORDER_CONTACT),
			new DocumentTemplateSection(QUARANTINE_ORDER_EVENT_PARTICIPANT),
			new DocumentTemplateSection(QUARANTINE_ORDER_TRAVEL_ENTRY),
			new DocumentTemplateSection(EVENT_HANDOUT));

		gridLayout.setWidth(100, Unit.PERCENTAGE);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(true);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);
	}
}
