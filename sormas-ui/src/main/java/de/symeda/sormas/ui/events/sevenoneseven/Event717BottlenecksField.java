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

import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckCategory;
import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.AbstractEditForm;

@SuppressWarnings("serial")
public class Event717BottlenecksField extends AbstractEvent717EntriesField<Event717BottleneckDto> {

	public Event717BottlenecksField(boolean isEditAllowed) {
		super(isEditAllowed);
	}

	@Override
	public Class<Event717BottleneckDto> getEntryType() {
		return Event717BottleneckDto.class;
	}

	@Override
	protected Event717BottleneckDto createEntry() {
		return Event717BottleneckDto.build(null);
	}

	@Override
	protected AbstractEditForm<Event717BottleneckDto> createEditForm(Event717BottleneckDto entry, boolean create) {
		return new Event717BottleneckEditForm(create, isEditAllowed);
	}

	@Override
	protected String getEntryCaption() {
		return I18nProperties.getCaption(Event717BottleneckDto.I18N_PREFIX);
	}

	@Override
	protected Object getSortPropertyId() {
		return Event717BottleneckDto.TIMELINESS_INTERVAL;
	}

	/**
	 * Creates an entry for the given interval and opens its edit dialog.
	 */
	public void createEntryForInterval(Event717Interval interval) {
		addNewEntry(Event717BottleneckDto.build(interval));
	}

	@Override
	protected void updateColumns() {

		Table table = getTable();
		table.addGeneratedColumn(Event717BottleneckDto.CATEGORY, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			Event717BottleneckDto bottleneck = (Event717BottleneckDto) itemId;
			if (bottleneck.getCategory() == Event717BottleneckCategory.OTHER && bottleneck.getOtherCategoryDetails() != null) {
				return bottleneck.getCategory() + ": " + bottleneck.getOtherCategoryDetails();
			}
			return bottleneck.getCategory() != null ? bottleneck.getCategory().toString() : null;
		});

		table.setVisibleColumns(
			ACTION_COLUMN_ID,
			Event717BottleneckDto.TIMELINESS_INTERVAL,
			Event717BottleneckDto.CATEGORY,
			Event717BottleneckDto.DESCRIPTION);
		table.setColumnExpandRatio(ACTION_COLUMN_ID, 0);
		table.setColumnExpandRatio(Event717BottleneckDto.TIMELINESS_INTERVAL, 0);
		table.setColumnExpandRatio(Event717BottleneckDto.CATEGORY, 1);
		table.setColumnExpandRatio(Event717BottleneckDto.DESCRIPTION, 1);

		setColumnHeaders(Event717BottleneckDto.I18N_PREFIX);
	}

	@Override
	protected boolean isModified(Event717BottleneckDto oldEntry, Event717BottleneckDto newEntry) {

		return isModifiedObject(oldEntry.getTimelinessInterval(), newEntry.getTimelinessInterval())
			|| isModifiedObject(oldEntry.getCategory(), newEntry.getCategory())
			|| isModifiedObject(oldEntry.getOtherCategoryDetails(), newEntry.getOtherCategoryDetails())
			|| isModifiedObject(oldEntry.getDescription(), newEntry.getDescription());
	}
}
