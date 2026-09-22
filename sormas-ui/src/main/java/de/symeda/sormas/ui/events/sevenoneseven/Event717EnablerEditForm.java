/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.ui.events.sevenoneseven;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;


import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.event.sevenoneseven.Event717AssessmentDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717EnablerDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class Event717EnablerEditForm extends AbstractEditForm<Event717EnablerDto> {

	private static final long serialVersionUID = -2860612736315389754L;

	private static final String HTML_LAYOUT = fluidRowLocs(Event717EnablerDto.TIMELINESS_INTERVAL) + fluidRowLocs(Event717EnablerDto.DESCRIPTION);

	public Event717EnablerEditForm(boolean create, boolean isEditAllowed) {
		super(Event717EnablerDto.class, Event717EnablerDto.I18N_PREFIX, false, null, null, isEditAllowed);

		setWidth(540, Unit.PIXELS);
		addFields();

		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {

		// the interval is determined by the section the entry belongs to
		NullableOptionGroup interval = addField(Event717EnablerDto.TIMELINESS_INTERVAL, NullableOptionGroup.class);
		interval.setEnabled(false);

		TextArea description = addField(Event717EnablerDto.DESCRIPTION, TextArea.class);
		description.setRows(4);

		setRequired(true, Event717EnablerDto.TIMELINESS_INTERVAL, Event717EnablerDto.DESCRIPTION);
	}
}
