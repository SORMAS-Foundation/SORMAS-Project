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

import de.symeda.sormas.api.audit.AuditedClass;

/**
 * Timeliness of one 7-1-7 interval compared to its target.
 */
@AuditedClass
public class Event717IntervalResultDto implements Serializable {

	private static final long serialVersionUID = 4263592361536468914L;

	private Event717Interval interval;
	/**
	 * Difference in calendar days between the start and end milestone of the interval; null if it can not be calculated.
	 */
	private Integer days;
	private Event717TimelinessStatus status;
	/**
	 * Whether the target was met; null if the status is neither MET nor NOT_MET.
	 */
	private Boolean targetMet;

	public Event717IntervalResultDto() {
	}

	public Event717IntervalResultDto(Event717Interval interval, Integer days, Event717TimelinessStatus status) {
		this.interval = interval;
		this.days = days;
		this.status = status;
		this.targetMet = status == Event717TimelinessStatus.MET ? Boolean.TRUE : status == Event717TimelinessStatus.NOT_MET ? Boolean.FALSE : null;
	}

	public Event717Interval getInterval() {
		return interval;
	}

	public void setInterval(Event717Interval interval) {
		this.interval = interval;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Event717TimelinessStatus getStatus() {
		return status;
	}

	public void setStatus(Event717TimelinessStatus status) {
		this.status = status;
	}

	public Boolean getTargetMet() {
		return targetMet;
	}

	public void setTargetMet(Boolean targetMet) {
		this.targetMet = targetMet;
	}
}
