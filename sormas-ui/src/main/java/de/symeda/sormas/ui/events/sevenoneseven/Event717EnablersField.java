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

import com.vaadin.v7.ui.Table;

import de.symeda.sormas.api.event.sevenoneseven.Event717EnablerDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.AbstractEditForm;

@SuppressWarnings("serial")
public class Event717EnablersField extends AbstractEvent717EntriesField<Event717EnablerDto> {

	public Event717EnablersField(boolean isEditAllowed) {
		super(isEditAllowed);
	}

	@Override
	public Class<Event717EnablerDto> getEntryType() {
		return Event717EnablerDto.class;
	}

	@Override
	protected Event717EnablerDto createEntry() {
		return Event717EnablerDto.build(null);
	}

	@Override
	protected AbstractEditForm<Event717EnablerDto> createEditForm(Event717EnablerDto entry, boolean create) {
		return new Event717EnablerEditForm(create, isEditAllowed);
	}

	@Override
	protected String getEntryCaption() {
		return I18nProperties.getCaption(Event717EnablerDto.I18N_PREFIX);
	}

	@Override
	protected Object getSortPropertyId() {
		return Event717EnablerDto.TIMELINESS_INTERVAL;
	}

	/**
	 * Creates an entry for the given interval and opens its edit dialog.
	 */
	public void createEntryForInterval(Event717Interval interval) {
		addNewEntry(Event717EnablerDto.build(interval));
	}

	@Override
	protected void updateColumns() {

		Table table = getTable();
		table.setVisibleColumns(ACTION_COLUMN_ID, Event717EnablerDto.TIMELINESS_INTERVAL, Event717EnablerDto.DESCRIPTION);
		table.setColumnExpandRatio(ACTION_COLUMN_ID, 0);
		table.setColumnExpandRatio(Event717EnablerDto.TIMELINESS_INTERVAL, 0);
		table.setColumnExpandRatio(Event717EnablerDto.DESCRIPTION, 1);

		setColumnHeaders(Event717EnablerDto.I18N_PREFIX);
	}

	@Override
	protected boolean isModified(Event717EnablerDto oldEntry, Event717EnablerDto newEntry) {

		return isModifiedObject(oldEntry.getTimelinessInterval(), newEntry.getTimelinessInterval())
			|| isModifiedObject(oldEntry.getDescription(), newEntry.getDescription());
	}
}
