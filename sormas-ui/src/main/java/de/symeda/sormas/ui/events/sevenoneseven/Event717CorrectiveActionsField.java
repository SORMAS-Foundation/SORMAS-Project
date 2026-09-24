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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.v7.ui.Table;

import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckReferenceDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717CorrectiveActionDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateFormatHelper;

@SuppressWarnings("serial")
public class Event717CorrectiveActionsField extends AbstractEvent717EntriesField<Event717CorrectiveActionDto> {

	private final transient Supplier<Collection<Event717BottleneckDto>> bottlenecksSupplier;

	/**
	 * @param bottlenecksSupplier
	 *            Provides the bottlenecks currently entered in the assessment, including unsaved ones
	 */
	public Event717CorrectiveActionsField(
		Supplier<Collection<Event717BottleneckDto>> bottlenecksSupplier,
		boolean isEditAllowed,
		boolean isPseudonymized) {
		super(isEditAllowed, isPseudonymized);
		this.bottlenecksSupplier = bottlenecksSupplier;
	}

	@Override
	public Class<Event717CorrectiveActionDto> getEntryType() {
		return Event717CorrectiveActionDto.class;
	}

	@Override
	protected Event717CorrectiveActionDto createEntry() {
		return Event717CorrectiveActionDto.build();
	}

	@Override
	protected AbstractEditForm<Event717CorrectiveActionDto> createEditForm(Event717CorrectiveActionDto entry, boolean create) {
		return new Event717CorrectiveActionEditForm(getBottleneckReferences(), create, fieldAccessCheckers, isEditAllowed);
	}

	@Override
	protected String getEntryCaption() {
		return I18nProperties.getCaption(Event717CorrectiveActionDto.I18N_PREFIX);
	}

	@Override
	protected Object getSortPropertyId() {
		return null;
	}

	private List<Event717BottleneckReferenceDto> getBottleneckReferences() {

		Collection<Event717BottleneckDto> bottlenecks = bottlenecksSupplier.get();
		if (bottlenecks == null) {
			return Collections.emptyList();
		}
		return bottlenecks.stream().map(this::toReference).collect(Collectors.toList());
	}

	private Event717BottleneckReferenceDto toReference(Event717BottleneckDto bottleneck) {

		// hidden descriptions are replaced by the category, like the server does for the stored references
		String details = StringUtils.isEmpty(bottleneck.getDescription()) && isPseudonymized
			? (bottleneck.getCategory() != null ? bottleneck.getCategory().toString() : "")
			: bottleneck.getDescription();
		return new Event717BottleneckReferenceDto(bottleneck.getUuid(), bottleneck.getTimelinessInterval() + ": " + details);
	}

	/**
	 * Removes references to bottlenecks that have been removed from the assessment.
	 */
	public void removeInvalidBottleneckReferences() {

		Collection<Event717CorrectiveActionDto> actions = getValue();
		if (actions == null) {
			return;
		}

		Set<String> bottleneckUuids = getBottleneckReferences().stream().map(Event717BottleneckReferenceDto::getUuid).collect(Collectors.toSet());
		boolean changed = false;
		for (Event717CorrectiveActionDto action : actions) {
			if (action.getBottleneck() != null && !bottleneckUuids.contains(action.getBottleneck().getUuid())) {
				action.setBottleneck(null);
				changed = true;
			}
		}
		if (changed) {
			getTable().refreshRowCache();
			fireValueChange(false);
		} else {
			// captions of bottlenecks may have changed
			getTable().refreshRowCache();
		}
	}

	@Override
	protected void updateColumns() {

		Table table = getTable();
		table.addGeneratedColumn(Event717CorrectiveActionDto.BOTTLENECK, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			Event717BottleneckReferenceDto bottleneck = ((Event717CorrectiveActionDto) itemId).getBottleneck();
			if (bottleneck == null) {
				return null;
			}
			return getBottleneckReferences().stream()
				.filter(b -> b.getUuid().equals(bottleneck.getUuid()))
				.map(Event717BottleneckReferenceDto::getCaption)
				.findFirst()
				.orElse(bottleneck.getCaption());
		});
		if (isPseudonymized) {
			table.addGeneratedColumn(
				Event717CorrectiveActionDto.PROPOSED_ACTION,
				(Table.ColumnGenerator) (source, itemId, columnId) -> Event717FieldAccess
					.displayValue(((Event717CorrectiveActionDto) itemId).getProposedAction(), true));
			table.addGeneratedColumn(
				Event717CorrectiveActionDto.RESPONSIBLE_AUTHORITY,
				(Table.ColumnGenerator) (source, itemId, columnId) -> Event717FieldAccess
					.displayValue(((Event717CorrectiveActionDto) itemId).getResponsibleAuthority(), true));
		}
		table.addGeneratedColumn(Event717CorrectiveActionDto.TARGET_END_DATE, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			Event717CorrectiveActionDto action = (Event717CorrectiveActionDto) itemId;
			return action.getTargetEndDate() != null ? DateFormatHelper.formatDate(action.getTargetEndDate()) : null;
		});

		table.setVisibleColumns(
			ACTION_COLUMN_ID,
			Event717CorrectiveActionDto.PROPOSED_ACTION,
			Event717CorrectiveActionDto.BOTTLENECK,
			Event717CorrectiveActionDto.PRIORITIZATION,
			Event717CorrectiveActionDto.RESPONSIBLE_AUTHORITY,
			Event717CorrectiveActionDto.TARGET_END_DATE,
			Event717CorrectiveActionDto.PROGRESS_STATUS);
		table.setColumnExpandRatio(ACTION_COLUMN_ID, 0);
		table.setColumnExpandRatio(Event717CorrectiveActionDto.PROPOSED_ACTION, 1);
		table.setColumnExpandRatio(Event717CorrectiveActionDto.BOTTLENECK, 1);

		setColumnHeaders(Event717CorrectiveActionDto.I18N_PREFIX);
	}

	@Override
	protected boolean isModified(Event717CorrectiveActionDto oldEntry, Event717CorrectiveActionDto newEntry) {

		return isModifiedObject(oldEntry.getProposedAction(), newEntry.getProposedAction())
			|| isModifiedObject(oldEntry.getBottleneck(), newEntry.getBottleneck())
			|| isModifiedObject(oldEntry.getPrioritization(), newEntry.getPrioritization())
			|| isModifiedObject(oldEntry.getResponsibleAuthority(), newEntry.getResponsibleAuthority())
			|| isModifiedObject(oldEntry.getTargetStartDate(), newEntry.getTargetStartDate())
			|| isModifiedObject(oldEntry.getTargetEndDate(), newEntry.getTargetEndDate())
			|| isModifiedObject(oldEntry.getPlanningFundingOpportunities(), newEntry.getPlanningFundingOpportunities())
			|| isModifiedObject(oldEntry.getProgressStatus(), newEntry.getProgressStatus())
			|| isModifiedObject(oldEntry.getNextSteps(), newEntry.getNextSteps());
	}
}
