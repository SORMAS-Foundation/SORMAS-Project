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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717AssessmentDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckCategory;
import de.symeda.sormas.api.event.sevenoneseven.Event717BottleneckDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717CorrectiveActionDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717EarlyResponseAction;
import de.symeda.sormas.api.event.sevenoneseven.Event717EnablerDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717ExportDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

/**
 * Visibility of 7-1-7 assessments by jurisdiction and hiding of their free texts.
 */
public class Event717AssessmentFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private static final String NARRATIVE = "Index case John Doe fell ill";
	private static final String BOTTLENECK_DESCRIPTION = "Specimen of John Doe was delayed";
	private static final String PROPOSED_ACTION = "Call Dr. Smith";

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user2;
	private EventDto event1;
	private EventDto event2;

	@Override
	public void init() {
		super.init();

		createFeatureConfiguration(FeatureType.EVENT_717_ASSESSMENT, true);

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1");
		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2");
		UserRoleReferenceDto role = createRole("Event717DistrictUser", true, true);
		UserDto user1 = creator.createUser(rdcf1, "Surv", "Off1", role);
		user2 = creator.createUser(rdcf2, "Surv", "Off2", role);

		event1 = creator.createEvent(user1.toReference(), Disease.EVD, rdcf1);
		event2 = creator.createEvent(user2.toReference(), Disease.EVD, rdcf2);
		getEvent717AssessmentFacade().save(buildAssessment(event1));
		getEvent717AssessmentFacade().save(buildAssessment(event2));
	}

	private UserRoleReferenceDto createRole(String caption, boolean seeSensitiveData, boolean view717) {

		UserRight[] rights = {
			UserRight.EVENT_VIEW,
			UserRight.EVENT_EDIT,
			UserRight.EVENT_EXPORT,
			view717 ? UserRight.EVENT_717_ASSESSMENT_VIEW : UserRight.EVENT_VIEW,
			view717 ? UserRight.EVENT_717_ASSESSMENT_EDIT : UserRight.EVENT_VIEW,
			seeSensitiveData ? UserRight.SEE_SENSITIVE_DATA_IN_JURISDICTION : UserRight.EVENT_VIEW };
		return creator.createUserRoleWithRequiredRights(caption, JurisdictionLevel.DISTRICT, rights);
	}

	private static Date daysAgo(int days) {
		return DateUtils.addDays(new Date(), -days);
	}

	private static Event717AssessmentDto buildAssessment(EventDto event) {

		Event717AssessmentDto assessment = Event717AssessmentDto.build(event.toReference());
		assessment.setDateOfEmergence(daysAgo(20));
		assessment.setEmergenceNarrative(NARRATIVE);
		assessment.setDateOfDetection(daysAgo(15));
		assessment.setDateOfNotification(daysAgo(14));
		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			assessment.setEarlyResponseActionDate(action, daysAgo(12));
		}
		assessment.setInvestigationNarrative(NARRATIVE);
		assessment.setGeneralNotes(NARRATIVE);
		assessment.setReportCompletedByName("Jane Doe");

		Event717BottleneckDto bottleneck = Event717BottleneckDto.build(Event717Interval.DETECTION);
		bottleneck.setCategory(Event717BottleneckCategory.LAB_DELAYED_SPECIMEN_TRANSPORTATION);
		bottleneck.setDescription(BOTTLENECK_DESCRIPTION);
		assessment.getBottlenecks().add(bottleneck);

		Event717EnablerDto enabler = Event717EnablerDto.build(Event717Interval.NOTIFICATION);
		enabler.setDescription(NARRATIVE);
		assessment.getEnablers().add(enabler);

		Event717CorrectiveActionDto correctiveAction = Event717CorrectiveActionDto.build();
		correctiveAction.setProposedAction(PROPOSED_ACTION);
		correctiveAction.setResponsibleAuthority("Dr. Smith");
		correctiveAction.setBottleneck(bottleneck.toReference());
		assessment.getCorrectiveActions().add(correctiveAction);

		return assessment;
	}

	@Test
	public void testListOnlyContainsEventsInJurisdiction() {

		loginWith(user2);

		assertEquals(1, getEvent717AssessmentFacade().count(new EventCriteria()));
		assertEquals(event2.getUuid(), getEvent717AssessmentFacade().getIndexList(new EventCriteria(), null, null, null).get(0).getEventUuid());
		assertEquals(1, getEvent717AssessmentFacade().getSummary(new EventCriteria()).getAssessedEvents());

		List<Event717ExportDto> export = getEvent717AssessmentFacade().getExportList(new EventCriteria(), null, null);
		assertEquals(1, export.size());
		assertEquals(event2.getUuid(), export.get(0).getEventUuid());
		assertEquals(NARRATIVE, export.get(0).getGeneralNotes());

		// the list matches the events the user can see in the event directory
		assertEquals(getEventFacade().count(new EventCriteria()), getEvent717AssessmentFacade().count(new EventCriteria()));
	}

	@Test
	public void testAssessmentInJurisdictionIsNotPseudonymized() {

		loginWith(user2);

		Event717AssessmentDto assessment = getEvent717AssessmentFacade().getByEventUuid(event2.getUuid());
		assertFalse(assessment.isPseudonymized());
		assertTrue(assessment.isInJurisdiction());
		assertEquals(NARRATIVE, assessment.getEmergenceNarrative());
		assertEquals(BOTTLENECK_DESCRIPTION, assessment.getBottlenecks().get(0).getDescription());
		assertEquals(PROPOSED_ACTION, assessment.getCorrectiveActions().get(0).getProposedAction());
	}

	@Test
	public void testAssessmentOutsideJurisdictionIsPseudonymized() {

		loginWith(user2);

		Event717AssessmentDto assessment = getEvent717AssessmentFacade().getByEventUuid(event1.getUuid());
		assertTrue(assessment.isPseudonymized());
		assertFalse(assessment.isInJurisdiction());

		// free texts are hidden
		assertTrue(StringUtils.isEmpty(assessment.getEmergenceNarrative()));
		assertTrue(StringUtils.isEmpty(assessment.getInvestigationNarrative()));
		assertTrue(StringUtils.isEmpty(assessment.getGeneralNotes()));
		assertTrue(StringUtils.isEmpty(assessment.getReportCompletedByName()));
		Event717BottleneckDto bottleneck = assessment.getBottlenecks().get(0);
		assertTrue(StringUtils.isEmpty(bottleneck.getDescription()));
		assertTrue(StringUtils.isEmpty(assessment.getEnablers().get(0).getDescription()));
		Event717CorrectiveActionDto correctiveAction = assessment.getCorrectiveActions().get(0);
		assertTrue(StringUtils.isEmpty(correctiveAction.getProposedAction()));
		assertTrue(StringUtils.isEmpty(correctiveAction.getResponsibleAuthority()));
		// the caption of the bottleneck reference is its description, which must not leak
		assertFalse(correctiveAction.getBottleneck().getCaption().contains("John Doe"));

		// dates, categories and the timeliness stay visible
		assertNotNull(assessment.getDateOfEmergence());
		assertEquals(Event717BottleneckCategory.LAB_DELAYED_SPECIMEN_TRANSPORTATION, bottleneck.getCategory());
		assertEquals(Integer.valueOf(5), getEvent717AssessmentFacade().getTimelinessByEventUuid(event1.getUuid()).getDetection().getDays());

		// and the assessment can not be edited
		assertNotEquals(EditPermissionType.ALLOWED, getEvent717AssessmentFacade().getEditPermissionType(event1.getUuid()));
		assertThrows(AccessDeniedException.class, () -> getEvent717AssessmentFacade().save(assessment));
	}

	@Test
	public void testHiddenValuesAreKeptOnSave() {

		// a user in the jurisdiction who may not see sensitive data
		UserDto user = creator.createUser(rdcf2, "Surv", "NoSensitive", createRole("Event717NoSensitiveData", false, true));
		loginWith(user);

		Event717AssessmentDto assessment = getEvent717AssessmentFacade().getByEventUuid(event2.getUuid());
		assertTrue(assessment.isPseudonymized());
		assertTrue(StringUtils.isEmpty(assessment.getEmergenceNarrative()));

		Date outbreakEndDate = daysAgo(1);
		assessment.setOutbreakEndDate(outbreakEndDate);
		getEvent717AssessmentFacade().save(assessment);

		loginWith(nationalAdmin);
		Event717AssessmentDto saved = getEvent717AssessmentFacade().getByEventUuid(event2.getUuid());
		assertEquals(outbreakEndDate, saved.getOutbreakEndDate());
		assertEquals(NARRATIVE, saved.getEmergenceNarrative());
		assertEquals(NARRATIVE, saved.getInvestigationNarrative());
		assertEquals(NARRATIVE, saved.getGeneralNotes());
		assertEquals("Jane Doe", saved.getReportCompletedByName());
		assertEquals(BOTTLENECK_DESCRIPTION, saved.getBottlenecks().get(0).getDescription());
		assertEquals(NARRATIVE, saved.getEnablers().get(0).getDescription());
		assertEquals(PROPOSED_ACTION, saved.getCorrectiveActions().get(0).getProposedAction());
		assertEquals("Dr. Smith", saved.getCorrectiveActions().get(0).getResponsibleAuthority());
	}

	@Test
	public void testExportHidesNotesWithoutSensitiveDataRight() {

		UserDto user = creator.createUser(rdcf2, "Surv", "NoSensitive", createRole("Event717NoSensitiveData", false, true));
		loginWith(user);

		List<Event717ExportDto> export = getEvent717AssessmentFacade().getExportList(new EventCriteria(), null, null);
		assertEquals(1, export.size());
		assertTrue(export.get(0).isPseudonymized());
		assertTrue(StringUtils.isEmpty(export.get(0).getGeneralNotes()));
		// the timeliness is not sensitive
		assertEquals("5", export.get(0).getDetectionDays());
	}

	@Test
	public void testExportRequires717ViewRight() {

		UserDto user = creator.createUser(rdcf2, "Surv", "No717", createRole("EventExportWithout717", true, false));
		loginWith(user);

		assertThrows(AccessDeniedException.class, () -> getEvent717AssessmentFacade().getExportList(new EventCriteria(), null, null));
	}
}
