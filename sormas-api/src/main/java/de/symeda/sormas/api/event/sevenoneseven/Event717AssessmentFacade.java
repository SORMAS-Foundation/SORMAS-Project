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
package de.symeda.sormas.api.event.sevenoneseven;

import java.util.List;
import java.util.Set;

import javax.ejb.Remote;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface Event717AssessmentFacade {

	/**
	 * @return The 7-1-7 assessment of the given event, or null if none exists yet.
	 */
	Event717AssessmentDto getByEventUuid(String eventUuid);

	Event717AssessmentDto getByUuid(String uuid);

	boolean existsForEvent(String eventUuid);

	Event717AssessmentDto save(@Valid @NotNull Event717AssessmentDto dto);

	void deleteByEventUuid(String eventUuid);

	/**
	 * @return The timeliness of the saved 7-1-7 assessment of the given event, or null if none exists yet.
	 */
	Event717TimelinessDto getTimelinessByEventUuid(String eventUuid);

	/**
	 * Calculates the timeliness for the given (possibly unsaved) assessment.
	 */
	Event717TimelinessDto calculateTimeliness(Event717AssessmentDto dto);

	void validate(Event717AssessmentDto dto) throws ValidationRuntimeException;

	EditPermissionType getEditPermissionType(String eventUuid);

	/**
	 * @return The event statuses for which a new assessment can be created, as configured by
	 *         {@link Event717EventStatusesProvider#CONFIG_KEY}. Existing assessments are not affected by this setting.
	 */
	Set<EventStatus> getApplicableEventStatuses();

	/**
	 * @return Whether the 7-1-7 assessment is available for the event: it already has an assessment, whatever its current status, or a
	 *         new one can be created because its current status is one of the {@link #getApplicableEventStatuses() applicable
	 *         statuses}.
	 */
	boolean isAvailableForEvent(String eventUuid);

	/**
	 * @return The event statuses recorded on the existing assessments when they were created, in the order of the enum.
	 */
	List<EventStatus> getAssessedEventStatuses();

	/**
	 * @return The stored timeliness of the 7-1-7 assessments of the events matching the criteria; events without an assessment are
	 *         not included.
	 */
	List<Event717IndexDto> getIndexList(EventCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(EventCriteria criteria);

	List<Event717ExportDto> getExportList(EventCriteria criteria, Integer first, Integer max);

	/**
	 * @return The 7-1-7 performance of the assessed events matching the criteria. Events that are currently dropped are never
	 *         counted.
	 */
	Event717SummaryDto getSummary(EventCriteria criteria);
}
