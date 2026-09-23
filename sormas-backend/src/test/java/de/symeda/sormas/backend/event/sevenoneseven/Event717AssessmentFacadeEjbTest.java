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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717AssessmentDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckCategory;
import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717CorrectiveActionDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717CorrectiveActionPriority;
import de.symeda.sormas.api.event.sevenoneseven.Event717EarlyResponseAction;
import de.symeda.sormas.api.event.sevenoneseven.Event717EnablerDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.OutdatedEntityException;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.util.DtoHelper;

public class Event717AssessmentFacadeEjbTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf;
	private EventDto event;

	@Override
	public void init() {
		super.init();

		createFeatureConfiguration(FeatureType.EVENT_717_ASSESSMENT, true);
		rdcf = creator.createRDCF();
		event = creator.createEvent(nationalAdmin.toReference());
	}

	private static Date daysAgo(int days) {
		return DateUtils.addDays(new Date(), -days);
	}

	private Event717AssessmentDto buildAssessment(EventDto event) {

		Event717AssessmentDto assessment = Event717AssessmentDto.build(event.toReference());
		assessment.setDateOfEmergence(daysAgo(20));
		assessment.setEmergenceNarrative("Index case symptom onset");
		assessment.setDateOfDetection(daysAgo(15));
		assessment.setDateOfNotification(daysAgo(14));
		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			assessment.setEarlyResponseActionDate(action, daysAgo(12));
		}
		assessment.setReportCompletedByName("Jane Doe");
		return assessment;
	}

	private static Event717BottleneckDto bottleneck(Event717Interval interval) {

		Event717BottleneckDto entry = Event717BottleneckDto.build(interval);
		entry.setDescription("Delayed specimen transport");
		entry.setCategory(Event717BottleneckCategory.LAB_DELAYED_SPECIMEN_TRANSPORTATION);
		return entry;
	}

	private static Event717EnablerDto enabler(Event717Interval interval) {

		Event717EnablerDto entry = Event717EnablerDto.build(interval);
		entry.setDescription("Well trained rapid response team");
		return entry;
	}

	private static Event717CorrectiveActionDto correctiveAction(Event717BottleneckDto bottleneck) {

		Event717CorrectiveActionDto action = Event717CorrectiveActionDto.build();
		action.setProposedAction("Establish courier contract");
		action.setBottleneck(bottleneck != null ? bottleneck.toReference() : null);
		action.setPrioritization(Event717CorrectiveActionPriority.IMMEDIATE);
		return action;
	}

	@Test
	public void testFeatureDisabled() {

		createFeatureConfiguration(FeatureType.EVENT_717_ASSESSMENT, false);

		Event717AssessmentDto assessment = buildAssessment(event);
		assertThrows(AccessDeniedException.class, () -> getEvent717AssessmentFacade().getByEventUuid(event.getUuid()));
		assertThrows(AccessDeniedException.class, () -> getEvent717AssessmentFacade().save(assessment));
		assertThrows(AccessDeniedException.class, () -> getEvent717AssessmentFacade().calculateTimeliness(assessment));
	}

	@Test
	public void testSaveAndGet() {

		assertNull(getEvent717AssessmentFacade().getByEventUuid(event.getUuid()));
		assertFalse(getEvent717AssessmentFacade().existsForEvent(event.getUuid()));

		Event717AssessmentDto assessment = buildAssessment(event);
		assessment.setLabConfirmationNarrative("PCR confirmed");
		Event717AssessmentDto saved = getEvent717AssessmentFacade().save(assessment);

		assertTrue(getEvent717AssessmentFacade().existsForEvent(event.getUuid()));
		Event717AssessmentDto loaded = getEvent717AssessmentFacade().getByEventUuid(event.getUuid());
		assertEquals(saved.getUuid(), loaded.getUuid());
		assertEquals(event.getUuid(), loaded.getEvent().getUuid());
		assertEquals(assessment.getDateOfEmergence(), loaded.getDateOfEmergence());
		assertEquals("Index case symptom onset", loaded.getEmergenceNarrative());
		assertEquals("PCR confirmed", loaded.getLabConfirmationNarrative());
		assertEquals("Jane Doe", loaded.getReportCompletedByName());
		assertEquals(loaded.getUuid(), getEvent717AssessmentFacade().getByUuid(saved.getUuid()).getUuid());

		EventDto otherEvent = creator.createEvent(nationalAdmin.toReference());
		assertNull(getEvent717AssessmentFacade().getByEventUuid(otherEvent.getUuid()));
	}

	@Test
	public void testOnlyOneAssessmentPerEvent() {

		getEvent717AssessmentFacade().save(buildAssessment(event));
		assertThrows(ValidationRuntimeException.class, () -> getEvent717AssessmentFacade().save(buildAssessment(event)));
	}

	@Test
	public void testAssessmentCanNotBeMovedToOtherEvent() {

		Event717AssessmentDto saved = getEvent717AssessmentFacade().save(buildAssessment(event));
		EventDto otherEvent = creator.createEvent(nationalAdmin.toReference());
		saved.setEvent(otherEvent.toReference());

		assertThrows(ValidationRuntimeException.class, () -> getEvent717AssessmentFacade().save(saved));
	}

	@Test
	public void testEarlyResponseCompletionDateIsCalculated() {

		Event717AssessmentDto assessment = buildAssessment(event);
		assessment.setCoordinationDate(daysAgo(10));
		assessment.setLabConfirmationDate(null);
		assessment.setLabConfirmationNotApplicable(true);
		// client value is ignored
		assessment.setEarlyResponseCompletionDate(daysAgo(1));

		Event717AssessmentDto saved = getEvent717AssessmentFacade().save(assessment);
		assertEquals(assessment.getCoordinationDate(), saved.getEarlyResponseCompletionDate());

		saved.setCoordinationDate(null);
		saved = getEvent717AssessmentFacade().save(saved);
		assertNull(saved.getEarlyResponseCompletionDate());

		Event717TimelinessDto timeliness = getEvent717AssessmentFacade().getTimelinessByEventUuid(event.getUuid());
		assertTrue(timeliness.isEarlyResponseIncomplete());
		assertEquals(Event717TimelinessStatus.INCOMPLETE, timeliness.getResponse().getStatus());
	}

	@Test
	public void testTimeliness() {

		assertNull(getEvent717AssessmentFacade().getTimelinessByEventUuid(event.getUuid()));

		Event717AssessmentDto assessment = buildAssessment(event);
		getEvent717AssessmentFacade().save(assessment);

		Event717TimelinessDto timeliness = getEvent717AssessmentFacade().getTimelinessByEventUuid(event.getUuid());
		assertEquals(event.getUuid(), timeliness.getEventUuid());
		assertEquals(Integer.valueOf(5), timeliness.getDetection().getDays());
		assertEquals(Event717TimelinessStatus.MET, timeliness.getDetection().getStatus());
		assertEquals(Integer.valueOf(1), timeliness.getNotification().getDays());
		assertEquals(Integer.valueOf(2), timeliness.getResponse().getDays());
		assertTrue(timeliness.getAllTargetsMet());

		// unsaved changes can be evaluated without saving
		assessment.setDateOfEmergence(daysAgo(30));
		assessment.setDateOfNotification(daysAgo(16));
		timeliness = getEvent717AssessmentFacade().calculateTimeliness(assessment);
		assertEquals(Event717TimelinessStatus.NOT_MET, timeliness.getDetection().getStatus());
		assertEquals(Event717TimelinessStatus.DATA_ERROR, timeliness.getNotification().getStatus());
		assertNull(timeliness.getAllTargetsMet());
	}

	@Test
	public void testDateValidation() {

		Event717AssessmentDto futureDate = buildAssessment(event);
		futureDate.setDateOfDetection(DateUtils.addDays(new Date(), 2));
		assertThrows(ValidationRuntimeException.class, () -> getEvent717AssessmentFacade().save(futureDate));

		Event717AssessmentDto notApplicableWithDate = buildAssessment(event);
		notApplicableWithDate.setRiskCommunicationNotApplicable(true);
		assertThrows(ValidationRuntimeException.class, () -> getEvent717AssessmentFacade().save(notApplicableWithDate));

		// chronology is not enforced, negative intervals are reported as data errors instead
		Event717AssessmentDto negativeInterval = buildAssessment(event);
		negativeInterval.setDateOfDetection(daysAgo(25));
		getEvent717AssessmentFacade().save(negativeInterval);
	}

	@Test
	public void testSaveBottlenecksEnablersAndCorrectiveActions() {

		Event717AssessmentDto assessment = buildAssessment(event);
		Event717BottleneckDto bottleneck = bottleneck(Event717Interval.DETECTION);
		Event717EnablerDto enabler = enabler(Event717Interval.RESPONSE);
		assessment.getBottlenecks().add(bottleneck);
		assessment.getEnablers().add(enabler);
		Event717CorrectiveActionDto action = correctiveAction(bottleneck);
		assessment.getCorrectiveActions().add(action);

		Event717AssessmentDto saved = getEvent717AssessmentFacade().save(assessment);

		assertEquals(1, saved.getBottlenecks().size());
		assertEquals(Event717BottleneckCategory.LAB_DELAYED_SPECIMEN_TRANSPORTATION, saved.getBottlenecks().get(0).getCategory());
		assertEquals(1, saved.getEnablers().size());
		assertEquals(enabler.getUuid(), saved.getEnablers().get(0).getUuid());
		assertEquals(Event717Interval.RESPONSE, saved.getEnablers().get(0).getTimelinessInterval());
		assertEquals(1, saved.getCorrectiveActions().size());
		assertEquals(bottleneck.getUuid(), saved.getCorrectiveActions().get(0).getBottleneck().getUuid());
		assertNotNull(getEvent717AssessmentService().getByUuid(saved.getUuid()).getChangeDateOfEmbeddedLists());
	}

	@Test
	public void testRemoveChildren() {

		Event717AssessmentDto assessment = buildAssessment(event);
		Event717BottleneckDto bottleneck1 = bottleneck(Event717Interval.DETECTION);
		Event717BottleneckDto bottleneck2 = bottleneck(Event717Interval.NOTIFICATION);
		assessment.getBottlenecks().add(bottleneck1);
		assessment.getBottlenecks().add(bottleneck2);
		assessment.getCorrectiveActions().add(correctiveAction(bottleneck1));
		Event717AssessmentDto saved = getEvent717AssessmentFacade().save(assessment);

		saved.getBottlenecks().removeIf(e -> e.getUuid().equals(bottleneck2.getUuid()));
		saved = getEvent717AssessmentFacade().save(saved);
		assertEquals(1, saved.getBottlenecks().size());
		assertEquals(1, getEvent717AssessmentService().getByUuid(saved.getUuid()).getBottlenecks().size());

		// removing a bottleneck that is still referenced by a corrective action is rejected
		Event717AssessmentDto referenced = saved;
		referenced.getBottlenecks().clear();
		assertThrows(ValidationRuntimeException.class, () -> getEvent717AssessmentFacade().save(referenced));
	}

	@Test
	public void testMaxThreeEntriesPerIntervalAndType() {

		Event717AssessmentDto assessment = buildAssessment(event);
		for (int i = 0; i < 3; i++) {
			assessment.getBottlenecks().add(bottleneck(Event717Interval.DETECTION));
			assessment.getEnablers().add(enabler(Event717Interval.DETECTION));
			assessment.getBottlenecks().add(bottleneck(Event717Interval.RESPONSE));
		}
		Event717AssessmentDto saved = getEvent717AssessmentFacade().save(assessment);
		assertEquals(6, saved.getBottlenecks().size());
		assertEquals(3, saved.getEnablers().size());

		Event717AssessmentDto tooManyBottlenecks = getEvent717AssessmentFacade().getByEventUuid(event.getUuid());
		tooManyBottlenecks.getBottlenecks().add(bottleneck(Event717Interval.DETECTION));
		assertThrows(ValidationRuntimeException.class, () -> getEvent717AssessmentFacade().save(tooManyBottlenecks));

		Event717AssessmentDto tooManyEnablers = getEvent717AssessmentFacade().getByEventUuid(event.getUuid());
		tooManyEnablers.getEnablers().add(enabler(Event717Interval.DETECTION));
		assertThrows(ValidationRuntimeException.class, () -> getEvent717AssessmentFacade().save(tooManyEnablers));
	}

	@Test
	public void testBottleneckCategoryValidation() {

		Event717AssessmentDto missingCategory = buildAssessment(event);
		Event717BottleneckDto withoutCategory = bottleneck(Event717Interval.DETECTION);
		withoutCategory.setCategory(null);
		missingCategory.getBottlenecks().add(withoutCategory);
		assertThrows(ValidationRuntimeException.class, () -> getEvent717AssessmentFacade().save(missingCategory));

		Event717AssessmentDto otherWithoutDetails = buildAssessment(event);
		Event717BottleneckDto other = bottleneck(Event717Interval.DETECTION);
		other.setCategory(Event717BottleneckCategory.OTHER);
		otherWithoutDetails.getBottlenecks().add(other);
		assertThrows(ValidationRuntimeException.class, () -> getEvent717AssessmentFacade().save(otherWithoutDetails));

		Event717AssessmentDto missingDescription = buildAssessment(event);
		Event717EnablerDto withoutDescription = enabler(Event717Interval.NOTIFICATION);
		withoutDescription.setDescription(" ");
		missingDescription.getEnablers().add(withoutDescription);
		assertThrows(ValidationRuntimeException.class, () -> getEvent717AssessmentFacade().save(missingDescription));

		other.setOtherCategoryDetails("Strike of lab staff");
		Event717AssessmentDto saved = getEvent717AssessmentFacade().save(otherWithoutDetails);
		assertEquals("Strike of lab staff", saved.getBottlenecks().get(0).getOtherCategoryDetails());
	}

	@Test
	public void testCorrectiveActionValidation() {

		// the addressed bottleneck must be part of the same assessment
		Event717AssessmentDto unknownBottleneck = buildAssessment(event);
		unknownBottleneck.getCorrectiveActions().add(correctiveAction(bottleneck(Event717Interval.DETECTION)));
		assertThrows(ValidationRuntimeException.class, () -> getEvent717AssessmentFacade().save(unknownBottleneck));

		Event717AssessmentDto missingProposedAction = buildAssessment(event);
		Event717CorrectiveActionDto withoutText = correctiveAction(null);
		withoutText.setProposedAction(null);
		missingProposedAction.getCorrectiveActions().add(withoutText);
		assertThrows(ValidationRuntimeException.class, () -> getEvent717AssessmentFacade().save(missingProposedAction));

		Event717AssessmentDto endBeforeStart = buildAssessment(event);
		Event717CorrectiveActionDto action = correctiveAction(null);
		action.setTargetStartDate(DateUtils.addDays(new Date(), 10));
		action.setTargetEndDate(DateUtils.addDays(new Date(), 5));
		endBeforeStart.getCorrectiveActions().add(action);
		assertThrows(ValidationRuntimeException.class, () -> getEvent717AssessmentFacade().save(endBeforeStart));

		// target dates may be in the future
		action.setTargetEndDate(DateUtils.addDays(new Date(), 30));
		getEvent717AssessmentFacade().save(endBeforeStart);
	}

	@Test
	public void testChildOfOtherAssessmentIsNotReassigned() {

		Event717AssessmentDto first = buildAssessment(event);
		Event717BottleneckDto bottleneck = bottleneck(Event717Interval.DETECTION);
		first.getBottlenecks().add(bottleneck);
		getEvent717AssessmentFacade().save(first);

		EventDto otherEvent = creator.createEvent(nationalAdmin.toReference());
		Event717AssessmentDto second = buildAssessment(otherEvent);
		second.getBottlenecks().add(bottleneck);
		assertThrows(Exception.class, () -> getEvent717AssessmentFacade().save(second));

		Event717AssessmentDto firstReloaded = getEvent717AssessmentFacade().getByEventUuid(event.getUuid());
		assertEquals(1, firstReloaded.getBottlenecks().size());
		assertEquals(bottleneck.getUuid(), firstReloaded.getBottlenecks().get(0).getUuid());
	}

	@Test
	public void testDeleteByEventUuid() {

		Event717AssessmentDto assessment = buildAssessment(event);
		Event717BottleneckDto bottleneck = bottleneck(Event717Interval.DETECTION);
		assessment.getBottlenecks().add(bottleneck);
		assessment.getEnablers().add(enabler(Event717Interval.RESPONSE));
		assessment.getCorrectiveActions().add(correctiveAction(bottleneck));
		getEvent717AssessmentFacade().save(assessment);

		getEvent717AssessmentFacade().deleteByEventUuid(event.getUuid());

		assertFalse(getEvent717AssessmentFacade().existsForEvent(event.getUuid()));
		assertEquals(0, getEvent717AssessmentService().count());
	}

	@Test
	public void testEventPermanentDeletionRemovesAssessment() {

		Event717AssessmentDto assessment = buildAssessment(event);
		Event717BottleneckDto bottleneck = bottleneck(Event717Interval.DETECTION);
		assessment.getBottlenecks().add(bottleneck);
		assessment.getEnablers().add(enabler(Event717Interval.RESPONSE));
		assessment.getCorrectiveActions().add(correctiveAction(bottleneck));
		getEvent717AssessmentFacade().save(assessment);

		getEventService().deletePermanent(mergeToEntityManager(getEventService().getByUuid(event.getUuid())));

		assertEquals(0, getEvent717AssessmentService().count());
	}

	@Test
	public void testNotEditableForDeletedOrArchivedEvent() {

		Event717AssessmentDto assessment = getEvent717AssessmentFacade().save(buildAssessment(event));
		assertEquals(EditPermissionType.ALLOWED, getEvent717AssessmentFacade().getEditPermissionType(event.getUuid()));

		createFeatureConfiguration(FeatureType.EDIT_ARCHIVED_ENTITIES, false);
		getEventFacade().archive(Collections.singletonList(event.getUuid()));
		assertNotEquals(EditPermissionType.ALLOWED, getEvent717AssessmentFacade().getEditPermissionType(event.getUuid()));
		assertThrows(AccessDeniedException.class, () -> getEvent717AssessmentFacade().save(assessment));

		getEventFacade().dearchive(Collections.singletonList(event.getUuid()), null);
		getEventFacade().delete(event.getUuid(), new DeletionDetails());
		assertEquals(EditPermissionType.REFUSED, getEvent717AssessmentFacade().getEditPermissionType(event.getUuid()));
		assertThrows(AccessDeniedException.class, () -> getEvent717AssessmentFacade().save(assessment));
		// the assessment is kept for soft deleted events
		assertNotNull(getEvent717AssessmentFacade().getByEventUuid(event.getUuid()));
	}

	@Test
	public void testOutdatedEntity() throws InterruptedException {

		Event717AssessmentDto saved = getEvent717AssessmentFacade().save(buildAssessment(event));
		// simulate a delay between the two changes that is longer than the change date tolerance
		Thread.sleep(DtoHelper.CHANGE_DATE_TOLERANCE_MS + 1);
		saved.setGeneralNotes("first change");
		getEvent717AssessmentFacade().save(saved);

		saved.setGeneralNotes("second change");
		assertThrows(OutdatedEntityException.class, () -> getEvent717AssessmentFacade().save(saved));
	}
}
