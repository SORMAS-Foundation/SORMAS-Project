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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Date;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.utils.UtilDate;

public class Event717TimelinessCalculatorTest {

	private static Date date(int day) {
		return date(day, 12);
	}

	private static Date date(int day, int hour) {
		return UtilDate.from(LocalDateTime.of(2026, 3, 1, 0, 0).plusDays(day).withHour(hour));
	}

	private static Event717AssessmentDto assessmentWithAllEarlyResponseDates(int day) {
		Event717AssessmentDto assessment = Event717AssessmentDto.build(new EventReferenceDto("event-uuid"));
		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			assessment.setEarlyResponseActionDate(action, date(day));
		}
		return assessment;
	}

	@Test
	public void testCalculateInterval() {

		Event717IntervalResultDto result = Event717TimelinessCalculator.calculateInterval(Event717Interval.DETECTION, date(0), date(7));
		assertEquals(Integer.valueOf(7), result.getDays());
		assertEquals(Event717TimelinessStatus.WITHIN_TARGET, result.getStatus());
		assertTrue(result.getTargetMet());

		result = Event717TimelinessCalculator.calculateInterval(Event717Interval.DETECTION, date(0), date(8));
		assertEquals(Event717TimelinessStatus.OVER_TARGET, result.getStatus());
		assertFalse(result.getTargetMet());

		result = Event717TimelinessCalculator.calculateInterval(Event717Interval.NOTIFICATION, date(3), date(3));
		assertEquals(Integer.valueOf(0), result.getDays());
		assertEquals(Event717TimelinessStatus.WITHIN_TARGET, result.getStatus());

		result = Event717TimelinessCalculator.calculateInterval(Event717Interval.NOTIFICATION, date(3), date(5));
		assertEquals(Event717TimelinessStatus.OVER_TARGET, result.getStatus());

		result = Event717TimelinessCalculator.calculateInterval(Event717Interval.NOTIFICATION, date(3), date(2));
		assertEquals(Integer.valueOf(-1), result.getDays());
		assertEquals(Event717TimelinessStatus.DATA_ERROR, result.getStatus());
		assertNull(result.getTargetMet());

		result = Event717TimelinessCalculator.calculateInterval(Event717Interval.RESPONSE, null, date(2));
		assertNull(result.getDays());
		assertEquals(Event717TimelinessStatus.MISSING, result.getStatus());
		assertNull(result.getTargetMet());
	}

	@Test
	public void testCalculateIntervalUsesCalendarDays() {

		// 23:00 on day 1 to 01:00 on day 2 is one calendar day although only two hours passed
		Event717IntervalResultDto result = Event717TimelinessCalculator.calculateInterval(Event717Interval.NOTIFICATION, date(1, 23), date(2, 1));
		assertEquals(Integer.valueOf(1), result.getDays());

		result = Event717TimelinessCalculator.calculateInterval(Event717Interval.NOTIFICATION, date(1, 1), date(1, 23));
		assertEquals(Integer.valueOf(0), result.getDays());
	}

	@Test
	public void testEarlyResponseCompletion() {

		Event717AssessmentDto assessment = assessmentWithAllEarlyResponseDates(5);
		assessment.setLabConfirmationDate(date(9));
		assertFalse(Event717TimelinessCalculator.isEarlyResponseIncomplete(assessment));
		assertEquals(date(9), Event717TimelinessCalculator.calculateEarlyResponseCompletionDate(assessment));

		// not applicable actions are ignored, even if they have a date
		assessment.setLabConfirmationNotApplicable(true);
		assertEquals(date(5), Event717TimelinessCalculator.calculateEarlyResponseCompletionDate(assessment));

		// a missing date of an applicable action makes the early response incomplete
		assessment.setCoordinationDate(null);
		assertTrue(Event717TimelinessCalculator.isEarlyResponseIncomplete(assessment));
		assertNull(Event717TimelinessCalculator.calculateEarlyResponseCompletionDate(assessment));

		// all actions not applicable -> incomplete
		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			assessment.setEarlyResponseActionNotApplicable(action, true);
		}
		assertTrue(Event717TimelinessCalculator.isEarlyResponseIncomplete(assessment));
	}

	@Test
	public void testCalculate() {

		Event717AssessmentDto assessment = assessmentWithAllEarlyResponseDates(10);
		assessment.setDateOfEmergence(date(0));
		assessment.setDateOfDetection(date(5));
		assessment.setDateOfNotification(date(6));

		Event717TimelinessDto timeliness = Event717TimelinessCalculator.calculate(assessment);
		assertEquals("event-uuid", timeliness.getEventUuid());
		assertEquals(Integer.valueOf(5), timeliness.getDetection().getDays());
		assertEquals(Integer.valueOf(1), timeliness.getNotification().getDays());
		assertEquals(Integer.valueOf(4), timeliness.getResponse().getDays());
		assertEquals(date(10), timeliness.getEarlyResponseCompletionDate());
		assertFalse(timeliness.isEarlyResponseIncomplete());
		assertTrue(timeliness.getAllTargetsMet());

		assessment.setDateOfNotification(date(8));
		timeliness = Event717TimelinessCalculator.calculate(assessment);
		assertEquals(Event717TimelinessStatus.OVER_TARGET, timeliness.getNotification().getStatus());
		assertFalse(timeliness.getAllTargetsMet());

		assessment.setInvestigationDate(null);
		timeliness = Event717TimelinessCalculator.calculate(assessment);
		assertTrue(timeliness.isEarlyResponseIncomplete());
		assertEquals(Event717TimelinessStatus.INCOMPLETE, timeliness.getResponse().getStatus());
		assertNull(timeliness.getResponse().getDays());
		assertNull(timeliness.getAllTargetsMet());
		// the notification target was still not met
		assertEquals(Event717TimelinessStatus.OVER_TARGET, timeliness.getOverallStatus());
		assertNull(timeliness.getEarlyResponseActionDays(Event717EarlyResponseAction.INVESTIGATION));
		assertEquals(Integer.valueOf(2), timeliness.getEarlyResponseActionDays(Event717EarlyResponseAction.COORDINATION));
	}

	@Test
	public void testEarlyResponseActionDays() {

		Event717AssessmentDto assessment = assessmentWithAllEarlyResponseDates(10);
		assessment.setDateOfNotification(date(6));
		assertEquals(
			Integer.valueOf(4),
			Event717TimelinessCalculator.calculateEarlyResponseActionDays(assessment, Event717EarlyResponseAction.INVESTIGATION));

		// negative durations are kept
		assessment.setInvestigationDate(date(4));
		assertEquals(
			Integer.valueOf(-2),
			Event717TimelinessCalculator.calculateEarlyResponseActionDays(assessment, Event717EarlyResponseAction.INVESTIGATION));

		// not applicable actions have no days, even if they have a date
		assessment.setInvestigationNotApplicable(true);
		assertNull(Event717TimelinessCalculator.calculateEarlyResponseActionDays(assessment, Event717EarlyResponseAction.INVESTIGATION));

		assessment.setLabConfirmationDate(null);
		assertNull(Event717TimelinessCalculator.calculateEarlyResponseActionDays(assessment, Event717EarlyResponseAction.LAB_CONFIRMATION));

		assessment.setDateOfNotification(null);
		assertNull(Event717TimelinessCalculator.calculateEarlyResponseActionDays(assessment, Event717EarlyResponseAction.COORDINATION));
	}

	@Test
	public void testOverallStatus() {

		Event717AssessmentDto assessment = assessmentWithAllEarlyResponseDates(10);
		assessment.setDateOfEmergence(date(0));
		assessment.setDateOfDetection(date(5));
		assessment.setDateOfNotification(date(6));
		assertEquals(Event717TimelinessStatus.WITHIN_TARGET, Event717TimelinessCalculator.calculate(assessment).getOverallStatus());

		// a not met target outweighs missing data
		assessment.setDateOfEmergence(date(-10));
		assessment.setCoordinationDate(null);
		assertEquals(Event717TimelinessStatus.OVER_TARGET, Event717TimelinessCalculator.calculate(assessment).getOverallStatus());

		// a data error outweighs a not met target
		assessment.setDateOfNotification(date(4));
		assertEquals(Event717TimelinessStatus.DATA_ERROR, Event717TimelinessCalculator.calculate(assessment).getOverallStatus());

		// a negative early response action is a data error even if all targets are met
		assessment = assessmentWithAllEarlyResponseDates(10);
		assessment.setDateOfEmergence(date(0));
		assessment.setDateOfDetection(date(5));
		assessment.setDateOfNotification(date(6));
		assessment.setInvestigationDate(date(5));
		Event717TimelinessDto timeliness = Event717TimelinessCalculator.calculate(assessment);
		assertTrue(timeliness.getAllTargetsMet());
		assertEquals(Event717TimelinessStatus.DATA_ERROR, timeliness.getOverallStatus());

		// missing data only -> incomplete
		assessment.setInvestigationDate(date(10));
		assessment.setDateOfEmergence(null);
		assertEquals(Event717TimelinessStatus.INCOMPLETE, Event717TimelinessCalculator.calculate(assessment).getOverallStatus());

		// incomplete early response only -> incomplete
		assessment.setDateOfEmergence(date(0));
		assessment.setLabConfirmationDate(null);
		assertEquals(Event717TimelinessStatus.INCOMPLETE, Event717TimelinessCalculator.calculate(assessment).getOverallStatus());
	}

	@Test
	public void testFormatActionDays() {

		assertEquals(Event717TimelinessCalculator.NOT_APPLICABLE, Event717TimelinessCalculator.formatActionDays(3, true));
		assertEquals(Event717TimelinessCalculator.NOT_APPLICABLE, Event717TimelinessCalculator.formatActionDays(null, true));
		assertEquals("3", Event717TimelinessCalculator.formatActionDays(3, false));
		assertEquals("", Event717TimelinessCalculator.formatActionDays(null, false));
	}

	@Test
	public void testFormatDays() {

		assertEquals("", Event717TimelinessCalculator.formatDays(null));
		assertEquals(Event717TimelinessCalculator.LESS_THAN_ONE_DAY, Event717TimelinessCalculator.formatDays(0));
		assertEquals("3", Event717TimelinessCalculator.formatDays(3));
		assertEquals("-2", Event717TimelinessCalculator.formatDays(-2));
	}

	@Test
	public void testBottleneckCategoriesHaveThemes() {

		for (Event717BottleneckCategory category : Event717BottleneckCategory.values()) {
			assertEquals(category == Event717BottleneckCategory.OTHER, category.getTheme() == null, category.name());
		}
		for (Event717BottleneckTheme theme : Event717BottleneckTheme.values()) {
			assertFalse(Event717BottleneckCategory.getByTheme(theme).isEmpty(), theme.name());
		}
	}
}
