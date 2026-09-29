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
import java.util.Date;
import java.util.EnumMap;

import de.symeda.sormas.api.audit.AuditedClass;

/**
 * Calculated 7-1-7 timeliness of an event (detection, notification and response interval).
 */
@AuditedClass
public class Event717TimelinessDto implements Serializable {

	private static final long serialVersionUID = -2468427541098817253L;

	private String eventUuid;
	private Event717IntervalResultDto detection;
	private Event717IntervalResultDto notification;
	private Event717IntervalResultDto response;
	private Date earlyResponseCompletionDate;
	private boolean earlyResponseIncomplete;
	/**
	 * Whether all three targets were met; null if any interval could not be evaluated.
	 */
	private Boolean allTargetsMet;
	/**
	 * Days from the notification to each early response action; no entry if the action is not applicable or a date is missing.
	 */
	private final EnumMap<Event717EarlyResponseAction, Integer> earlyResponseActionDays = new EnumMap<>(Event717EarlyResponseAction.class);
	private Event717TimelinessStatus overallStatus;

	public Event717IntervalResultDto getResult(Event717Interval interval) {

		switch (interval) {
		case DETECTION:
			return detection;
		case NOTIFICATION:
			return notification;
		case RESPONSE:
			return response;
		default:
			throw new IllegalArgumentException(interval.name());
		}
	}

	public String getEventUuid() {
		return eventUuid;
	}

	public void setEventUuid(String eventUuid) {
		this.eventUuid = eventUuid;
	}

	public Event717IntervalResultDto getDetection() {
		return detection;
	}

	public void setDetection(Event717IntervalResultDto detection) {
		this.detection = detection;
	}

	public Event717IntervalResultDto getNotification() {
		return notification;
	}

	public void setNotification(Event717IntervalResultDto notification) {
		this.notification = notification;
	}

	public Event717IntervalResultDto getResponse() {
		return response;
	}

	public void setResponse(Event717IntervalResultDto response) {
		this.response = response;
	}

	public Date getEarlyResponseCompletionDate() {
		return earlyResponseCompletionDate;
	}

	public void setEarlyResponseCompletionDate(Date earlyResponseCompletionDate) {
		this.earlyResponseCompletionDate = earlyResponseCompletionDate;
	}

	public boolean isEarlyResponseIncomplete() {
		return earlyResponseIncomplete;
	}

	public void setEarlyResponseIncomplete(boolean earlyResponseIncomplete) {
		this.earlyResponseIncomplete = earlyResponseIncomplete;
	}

	public Boolean getAllTargetsMet() {
		return allTargetsMet;
	}

	public void setAllTargetsMet(Boolean allTargetsMet) {
		this.allTargetsMet = allTargetsMet;
	}

	public Integer getEarlyResponseActionDays(Event717EarlyResponseAction action) {
		return earlyResponseActionDays.get(action);
	}

	public void setEarlyResponseActionDays(Event717EarlyResponseAction action, Integer days) {
		if (days == null) {
			earlyResponseActionDays.remove(action);
		} else {
			earlyResponseActionDays.put(action, days);
		}
	}

	public Event717TimelinessStatus getOverallStatus() {
		return overallStatus;
	}

	public void setOverallStatus(Event717TimelinessStatus overallStatus) {
		this.overallStatus = overallStatus;
	}
}
