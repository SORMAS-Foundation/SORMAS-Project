/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.events;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import de.symeda.sormas.api.event.EventGroupDto;

import com.vaadin.ui.Label;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class EventGroupDataForm extends AbstractEditForm<EventGroupDto> {

	private static final long serialVersionUID = 1L;

	private static final String EVENT_DATA_HEADING_LOC = "contactDataHeadingLoc";

	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(EVENT_DATA_HEADING_LOC) +
			fluidRowLocs(EventGroupDto.UUID) +
			fluidRowLocs(EventGroupDto.NAME);
	//@formatter:on

	private final Boolean isCreateForm;

	public EventGroupDataForm(boolean create) {
		super(EventGroupDto.class, EventGroupDto.I18N_PREFIX, false, new FieldVisibilityCheckers(), UiFieldAccessCheckers.getNoop());

		isCreateForm = create;

		if (create) {
			hideValidationUntilNextCommit();
		}

		addFields();
	}

	@Override
	protected void addFields() {
		if (isCreateForm == null) {
			return;
		}

		Label eventDataHeadingLabel = new Label(I18nProperties.getString(Strings.headingEventGroupData));
		eventDataHeadingLabel.addStyleName(H3);
		getContent().addComponent(eventDataHeadingLabel, EVENT_DATA_HEADING_LOC);

		addField(EventGroupDto.UUID, TextField.class);
		addFields(EventGroupDto.NAME);

		setReadOnly(true, EventGroupDto.UUID);
		setRequired(true, EventGroupDto.NAME);

		initializeAccessAndAllowedAccesses();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
