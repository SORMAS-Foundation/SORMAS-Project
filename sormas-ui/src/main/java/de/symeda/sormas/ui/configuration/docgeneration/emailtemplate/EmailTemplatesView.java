/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.configuration.docgeneration.emailtemplate;

import static de.symeda.sormas.api.docgeneneration.DocumentWorkflow.CASE_EMAIL;
import static de.symeda.sormas.api.docgeneneration.DocumentWorkflow.CONTACT_EMAIL;
import static de.symeda.sormas.api.docgeneneration.DocumentWorkflow.EVENT_PARTICIPANT_EMAIL;
import static de.symeda.sormas.api.docgeneneration.DocumentWorkflow.TRAVEL_ENTRY_EMAIL;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.configuration.docgeneration.DocumentTemplateSection;

public class EmailTemplatesView extends AbstractConfigurationView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/emailTemplates";


	public EmailTemplatesView() {
		super(VIEW_NAME);

		VerticalLayout gridLayout = new VerticalLayout(
			new DocumentTemplateSection(CASE_EMAIL),
			new DocumentTemplateSection(CONTACT_EMAIL),
			new DocumentTemplateSection(EVENT_PARTICIPANT_EMAIL),
			new DocumentTemplateSection(TRAVEL_ENTRY_EMAIL));

		gridLayout.setWidth(100, Unit.PERCENTAGE);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(true);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);
	}
}
