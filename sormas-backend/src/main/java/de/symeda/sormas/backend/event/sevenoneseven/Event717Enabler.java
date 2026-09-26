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
package de.symeda.sormas.backend.event.sevenoneseven;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;
import de.symeda.sormas.backend.common.AbstractDomainObject;

/**
 * Factor that enabled timely action in one 7-1-7 interval.
 */
@Entity(name = Event717Enabler.TABLE_NAME)
public class Event717Enabler extends AbstractDomainObject {

	private static final long serialVersionUID = 4718370962305518463L;

	public static final String TABLE_NAME = "event717enabler";

	public static final String ASSESSMENT = "assessment";
	public static final String TIMELINESS_INTERVAL = "timelinessInterval";
	public static final String DESCRIPTION = "description";

	private Event717Assessment assessment;
	private Event717Interval timelinessInterval;
	private String description;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	public Event717Assessment getAssessment() {
		return assessment;
	}

	public void setAssessment(Event717Assessment assessment) {
		this.assessment = assessment;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public Event717Interval getTimelinessInterval() {
		return timelinessInterval;
	}

	public void setTimelinessInterval(Event717Interval timelinessInterval) {
		this.timelinessInterval = timelinessInterval;
	}

	@Column(columnDefinition = "text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
