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

import javax.ejb.Remote;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.EditPermissionType;
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
}
