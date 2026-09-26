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

import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

import de.symeda.sormas.api.utils.DateHelper;

/**
 * Calculates the 7-1-7 timeliness of an assessment. Timeliness is the difference in calendar days between two milestone
 * dates, compared to the fixed target of the interval (see {@link Event717Interval#getTargetDays()}).
 */
public final class Event717TimelinessCalculator {

	/**
	 * Display value for a timeliness of 0 days, as recommended by the 7-1-7 assessment tool.
	 */
	public static final String LESS_THAN_ONE_DAY = "<1";

	/**
	 * Display value for a not applicable early response action, as used by the 7-1-7 assessment tool.
	 */
	public static final String NOT_APPLICABLE = "NA";

	private Event717TimelinessCalculator() {
		// Hide Utility Class Constructor
	}

	/**
	 * @return true if any applicable early response action has no date yet, or if all actions are marked as not applicable.
	 */
	public static boolean isEarlyResponseIncomplete(Event717AssessmentDto assessment) {

		boolean anyApplicable = false;
		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			if (!assessment.isEarlyResponseActionNotApplicable(action)) {
				anyApplicable = true;
				if (assessment.getEarlyResponseActionDate(action) == null) {
					return true;
				}
			}
		}
		return !anyApplicable;
	}

	/**
	 * @return The date of the last applicable early response action, or null if the early response is incomplete.
	 */
	public static Date calculateEarlyResponseCompletionDate(Event717AssessmentDto assessment) {

		if (isEarlyResponseIncomplete(assessment)) {
			return null;
		}

		return Stream.of(Event717EarlyResponseAction.values())
			.filter(action -> !assessment.isEarlyResponseActionNotApplicable(action))
			.map(assessment::getEarlyResponseActionDate)
			.filter(Objects::nonNull)
			.max(Date::compareTo)
			.orElse(null);
	}

	public static Event717IntervalResultDto calculateInterval(Event717Interval interval, Date start, Date end) {

		if (start == null || end == null) {
			return new Event717IntervalResultDto(interval, null, Event717TimelinessStatus.MISSING);
		}

		int days = DateHelper.getFullDaysBetween(start, end);
		Event717TimelinessStatus status;
		if (days < 0) {
			status = Event717TimelinessStatus.DATA_ERROR;
		} else if (days <= interval.getTargetDays()) {
			status = Event717TimelinessStatus.WITHIN_TARGET;
		} else {
			status = Event717TimelinessStatus.OVER_TARGET;
		}
		return new Event717IntervalResultDto(interval, days, status);
	}

	public static Event717TimelinessDto calculate(Event717AssessmentDto assessment) {

		Event717TimelinessDto timeliness = new Event717TimelinessDto();
		timeliness.setEventUuid(assessment.getEvent() != null ? assessment.getEvent().getUuid() : null);

		timeliness.setDetection(calculateInterval(Event717Interval.DETECTION, assessment.getDateOfEmergence(), assessment.getDateOfDetection()));
		timeliness
			.setNotification(calculateInterval(Event717Interval.NOTIFICATION, assessment.getDateOfDetection(), assessment.getDateOfNotification()));

		boolean incomplete = isEarlyResponseIncomplete(assessment);
		Date completionDate = incomplete ? null : calculateEarlyResponseCompletionDate(assessment);
		timeliness.setEarlyResponseIncomplete(incomplete);
		timeliness.setEarlyResponseCompletionDate(completionDate);
		if (incomplete) {
			timeliness.setResponse(new Event717IntervalResultDto(Event717Interval.RESPONSE, null, Event717TimelinessStatus.INCOMPLETE));
		} else {
			timeliness.setResponse(calculateInterval(Event717Interval.RESPONSE, assessment.getDateOfNotification(), completionDate));
		}

		Boolean allTargetsMet = Boolean.TRUE;
		for (Event717Interval interval : Event717Interval.values()) {
			Boolean targetMet = timeliness.getResult(interval).getTargetMet();
			if (targetMet == null) {
				allTargetsMet = null;
				break;
			} else if (!targetMet) {
				allTargetsMet = Boolean.FALSE;
			}
		}
		timeliness.setAllTargetsMet(allTargetsMet);

		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			timeliness.setEarlyResponseActionDays(action, calculateEarlyResponseActionDays(assessment, action));
		}
		timeliness.setOverallStatus(calculateOverallStatus(timeliness));

		return timeliness;
	}

	/**
	 * @return The number of days from the notification to the early response action, or null if the action is not applicable
	 *         or one of the dates is missing. Negative values are kept to reveal data errors.
	 */
	public static Integer calculateEarlyResponseActionDays(Event717AssessmentDto assessment, Event717EarlyResponseAction action) {

		Date actionDate = assessment.getEarlyResponseActionDate(action);
		if (assessment.isEarlyResponseActionNotApplicable(action) || actionDate == null || assessment.getDateOfNotification() == null) {
			return null;
		}
		return DateHelper.getFullDaysBetween(assessment.getDateOfNotification(), actionDate);
	}

	/**
	 * Compares the days from the notification to an applicable early response action with the target of the response interval, like
	 * the 7-1-7 data consolidation spreadsheet does. The response itself is only evaluated on the last action.
	 */
	public static Event717TimelinessStatus calculateEarlyResponseActionStatus(Integer days) {

		if (days == null) {
			return Event717TimelinessStatus.MISSING;
		} else if (days < 0) {
			return Event717TimelinessStatus.DATA_ERROR;
		} else if (days <= Event717Interval.RESPONSE.getTargetDays()) {
			return Event717TimelinessStatus.WITHIN_TARGET;
		} else {
			return Event717TimelinessStatus.OVER_TARGET;
		}
	}

	/**
	 * Summarizes the timeliness in a single status: {@link Event717TimelinessStatus#DATA_ERROR} if any interval or early response
	 * action has a negative duration, otherwise {@link Event717TimelinessStatus#OVER_TARGET} if any target was not met, otherwise
	 * {@link Event717TimelinessStatus#WITHIN_TARGET} if all targets were met, otherwise {@link Event717TimelinessStatus#INCOMPLETE}.
	 */
	public static Event717TimelinessStatus calculateOverallStatus(Event717TimelinessDto timeliness) {

		boolean dataError = Stream.of(Event717Interval.values())
			.anyMatch(interval -> timeliness.getResult(interval).getStatus() == Event717TimelinessStatus.DATA_ERROR)
			|| Stream.of(Event717EarlyResponseAction.values()).map(timeliness::getEarlyResponseActionDays).anyMatch(days -> days != null && days < 0);
		if (dataError) {
			return Event717TimelinessStatus.DATA_ERROR;
		}
		if (Stream.of(Event717Interval.values())
			.anyMatch(interval -> timeliness.getResult(interval).getStatus() == Event717TimelinessStatus.OVER_TARGET)) {
			return Event717TimelinessStatus.OVER_TARGET;
		}
		if (Stream.of(Event717Interval.values()).allMatch(interval -> timeliness.getResult(interval).getStatus() == Event717TimelinessStatus.WITHIN_TARGET)) {
			return Event717TimelinessStatus.WITHIN_TARGET;
		}
		return Event717TimelinessStatus.INCOMPLETE;
	}

	/**
	 * @return The timeliness for display: empty for null, {@link #LESS_THAN_ONE_DAY} for 0, otherwise the number of days.
	 */
	public static String formatDays(Integer days) {

		if (days == null) {
			return "";
		}
		return days == 0 ? LESS_THAN_ONE_DAY : String.valueOf(days);
	}

	/**
	 * @return {@link #NOT_APPLICABLE} for a not applicable early response action, otherwise the {@link #formatDays(Integer) formatted}
	 *         days.
	 */
	public static String formatActionDays(Integer days, boolean notApplicable) {
		return notApplicable ? NOT_APPLICABLE : formatDays(days);
	}
}
