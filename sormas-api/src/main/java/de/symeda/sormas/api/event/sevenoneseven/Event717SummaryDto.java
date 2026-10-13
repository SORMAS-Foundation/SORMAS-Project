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

import java.io.Serializable;
import java.util.EnumMap;

import de.symeda.sormas.api.audit.AuditedClass;

/**
 * 7-1-7 performance of a set of assessed events, like the summary reports of the "Assess 7-1-7 results" sheet of the 7-1-7 data
 * consolidation spreadsheet.
 */
@AuditedClass
public class Event717SummaryDto implements Serializable {

	private static final long serialVersionUID = 3312894507741263114L;

	private static final Event717OutcomeCountsDto NONE = new Event717OutcomeCountsDto(0, 0, 0, 0, 0, 0);

	private final int assessedEvents;
	private final EnumMap<Event717Interval, Event717OutcomeCountsDto> intervals = new EnumMap<>(Event717Interval.class);
	/**
	 * The whole 7-1-7 target, based on the overall status of each assessment.
	 */
	private Event717OutcomeCountsDto allTargets = NONE;
	private final EnumMap<Event717EarlyResponseAction, Event717OutcomeCountsDto> earlyResponseActions =
		new EnumMap<>(Event717EarlyResponseAction.class);

	public Event717SummaryDto(int assessedEvents) {
		this.assessedEvents = assessedEvents;
	}

	public int getAssessedEvents() {
		return assessedEvents;
	}

	public Event717OutcomeCountsDto getInterval(Event717Interval interval) {
		return intervals.getOrDefault(interval, NONE);
	}

	public void setInterval(Event717Interval interval, Event717OutcomeCountsDto counts) {
		intervals.put(interval, counts);
	}

	public Event717OutcomeCountsDto getAllTargets() {
		return allTargets;
	}

	public void setAllTargets(Event717OutcomeCountsDto allTargets) {
		this.allTargets = allTargets;
	}

	public Event717OutcomeCountsDto getEarlyResponseAction(Event717EarlyResponseAction action) {
		return earlyResponseActions.getOrDefault(action, NONE);
	}

	public void setEarlyResponseAction(Event717EarlyResponseAction action, Event717OutcomeCountsDto counts) {
		earlyResponseActions.put(action, counts);
	}
}
