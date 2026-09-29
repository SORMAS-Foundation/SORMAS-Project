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

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckCategory;
import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;
import de.symeda.sormas.backend.common.AbstractDomainObject;

/**
 * Factor that prevented timely action in one 7-1-7 interval.
 */
@Entity(name = Event717Bottleneck.TABLE_NAME)
public class Event717Bottleneck extends AbstractDomainObject {

	private static final long serialVersionUID = 2297561203862046915L;

	public static final String TABLE_NAME = "event717bottleneck";

	public static final String ASSESSMENT = "assessment";
	public static final String TIMELINESS_INTERVAL = "timelinessInterval";
	public static final String DESCRIPTION = "description";
	public static final String CATEGORY = "category";
	public static final String OTHER_CATEGORY_DETAILS = "otherCategoryDetails";

	private Event717Assessment assessment;
	private Event717Interval timelinessInterval;
	private String description;
	private Event717BottleneckCategory category;
	private String otherCategoryDetails;

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

	@Enumerated(EnumType.STRING)
	public Event717BottleneckCategory getCategory() {
		return category;
	}

	public void setCategory(Event717BottleneckCategory category) {
		this.category = category;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getOtherCategoryDetails() {
		return otherCategoryDetails;
	}

	public void setOtherCategoryDetails(String otherCategoryDetails) {
		this.otherCategoryDetails = otherCategoryDetails;
	}
}
