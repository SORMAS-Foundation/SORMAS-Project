/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.dashboard.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.dashboard.SampleDashboardCriteria;
import de.symeda.sormas.api.dashboard.sample.SampleShipmentStatus;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDashboardFilterDateType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class SampleDashboardFacadeEjbTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf;
	private TestDataCreator.RDCF rdcf2;
	private CaseDataDto caze;
	private Date reportDate;
	private UserDto user;

	@Override
	public void init() {
		super.init();
		rdcf = creator.createRDCF();
		rdcf2 = creator.createRDCF();
		user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		reportDate = DateHelper.getDateZero(2023, 1, 20);

		caze = creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf, c -> {
			c.setReportDate(reportDate);
			c.setDisease(Disease.CORONAVIRUS);
		});
	}

	@Test
	public void getSampleCountsByResultType() {
		SampleDto indeterminateSample = createSampleByResultType(caze, reportDate, PathogenTestResultType.INDETERMINATE, SampleMaterial.BLOOD);
		createPathogenTestByResultType(indeterminateSample, Disease.CORONAVIRUS, reportDate, PathogenTestResultType.INDETERMINATE);
		createPathogenTestByResultType(
			indeterminateSample,
			Disease.CORONAVIRUS,
			DateHelper.subtractDays(reportDate, 4),
			PathogenTestResultType.INDETERMINATE);

		SampleDto pendingSample =
			createSampleByResultType(caze, DateHelper.addDays(reportDate, 1), PathogenTestResultType.PENDING, SampleMaterial.CRUST);
		createPathogenTestByResultType(pendingSample, Disease.CORONAVIRUS, DateHelper.subtractDays(reportDate, 4), PathogenTestResultType.PENDING);

		SampleDto negativeSample =
			createSampleByResultType(caze, DateHelper.addDays(reportDate, 4), PathogenTestResultType.NEGATIVE, SampleMaterial.CRUST);
		createPathogenTestByResultType(
			negativeSample,
			Disease.CORONAVIRUS,
			DateHelper.subtractDays(reportDate, 4),
			PathogenTestResultType.INDETERMINATE);

		createSampleByResultType(caze, DateHelper.subtractDays(reportDate, 1), PathogenTestResultType.POSITIVE, SampleMaterial.CRUST);

		caze = creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf2, c -> {
			c.setReportDate(DateHelper.subtractDays(reportDate, 5));
			c.setDisease(Disease.CHOLERA);
		});
		SampleDto notDoneOtherRdcfSample =
			createSampleByResultType(caze, DateHelper.subtractDays(reportDate, 4), PathogenTestResultType.NOT_DONE, SampleMaterial.THROAT_SWAB);
		createPathogenTestByResultType(notDoneOtherRdcfSample, Disease.CHOLERA, DateHelper.addDays(reportDate, 1), PathogenTestResultType.NOT_DONE);

		Map<PathogenTestResultType, Long> sampleCountsForRelevantDate = getSampleDashboardFacade().getSampleCountsByResultType(
			new SampleDashboardCriteria().dateBetween(DateHelper.subtractDays(reportDate, 2), DateHelper.addDays(reportDate, 2))
				.sampleDateType(SampleDashboardFilterDateType.MOST_RELEVANT));

		assertEquals(1, sampleCountsForRelevantDate.get(PathogenTestResultType.INDETERMINATE));
		assertNull(sampleCountsForRelevantDate.get(PathogenTestResultType.PENDING));
		assertNull(sampleCountsForRelevantDate.get(PathogenTestResultType.NEGATIVE));
		assertEquals(1, sampleCountsForRelevantDate.get(PathogenTestResultType.POSITIVE));
		assertEquals(1, sampleCountsForRelevantDate.get(PathogenTestResultType.NOT_DONE));

		Map<PathogenTestResultType, Long> sampleCountsForSampleDate = getSampleDashboardFacade().getSampleCountsByResultType(
			new SampleDashboardCriteria().dateBetween(DateHelper.subtractDays(reportDate, 2), DateHelper.addDays(reportDate, 2))
				.sampleDateType(SampleDashboardFilterDateType.SAMPLE_DATE_TIME));

		assertEquals(1, sampleCountsForSampleDate.get(PathogenTestResultType.INDETERMINATE));
		assertEquals(1, sampleCountsForSampleDate.get(PathogenTestResultType.PENDING));
		assertNull(sampleCountsForSampleDate.get(PathogenTestResultType.NEGATIVE));
		assertEquals(1, sampleCountsForSampleDate.get(PathogenTestResultType.POSITIVE));
		assertNull(sampleCountsForSampleDate.get(PathogenTestResultType.NOT_DONE));

		Map<PathogenTestResultType, Long> sampleCountsForAssociatedEntityDate = getSampleDashboardFacade().getSampleCountsByResultType(
			new SampleDashboardCriteria().dateBetween(DateHelper.subtractDays(reportDate, 2), DateHelper.addDays(reportDate, 2))
				.sampleDateType(SampleDashboardFilterDateType.ASSOCIATED_ENTITY_REPORT_DATE));

		assertEquals(1, sampleCountsForAssociatedEntityDate.get(PathogenTestResultType.INDETERMINATE));
		assertEquals(1, sampleCountsForAssociatedEntityDate.get(PathogenTestResultType.PENDING));
		assertEquals(1, sampleCountsForAssociatedEntityDate.get(PathogenTestResultType.NEGATIVE));
		assertEquals(1, sampleCountsForAssociatedEntityDate.get(PathogenTestResultType.POSITIVE));
		assertNull(sampleCountsForAssociatedEntityDate.get(PathogenTestResultType.NOT_DONE));

		Map<PathogenTestResultType, Long> sampleCounts = getSampleDashboardFacade().getSampleCountsByResultType(new SampleDashboardCriteria());

		assertEquals(1, sampleCounts.get(PathogenTestResultType.INDETERMINATE));
		assertEquals(1, sampleCounts.get(PathogenTestResultType.PENDING));
		assertEquals(1, sampleCounts.get(PathogenTestResultType.NEGATIVE));
		assertEquals(1, sampleCounts.get(PathogenTestResultType.POSITIVE));
		assertEquals(1, sampleCounts.get(PathogenTestResultType.NOT_DONE));

		Map<PathogenTestResultType, Long> sampleCountsForRegion =
			getSampleDashboardFacade().getSampleCountsByResultType(new SampleDashboardCriteria().region(rdcf2.region));

		assertNull(sampleCountsForRegion.get(PathogenTestResultType.INDETERMINATE));
		assertNull(sampleCountsForRegion.get(PathogenTestResultType.PENDING));
		assertNull(sampleCountsForRegion.get(PathogenTestResultType.NEGATIVE));
		assertNull(sampleCountsForRegion.get(PathogenTestResultType.POSITIVE));
		assertEquals(1, sampleCountsForRegion.get(PathogenTestResultType.NOT_DONE));

		Map<PathogenTestResultType, Long> sampleCountsForDistrict =
			getSampleDashboardFacade().getSampleCountsByResultType(new SampleDashboardCriteria().district(rdcf2.district));

		assertNull(sampleCountsForDistrict.get(PathogenTestResultType.INDETERMINATE));
		assertNull(sampleCountsForDistrict.get(PathogenTestResultType.PENDING));
		assertNull(sampleCountsForDistrict.get(PathogenTestResultType.NEGATIVE));
		assertNull(sampleCountsForDistrict.get(PathogenTestResultType.POSITIVE));
		assertEquals(1, sampleCountsForDistrict.get(PathogenTestResultType.NOT_DONE));

		Map<PathogenTestResultType, Long> sampleCountsForCovid =
			getSampleDashboardFacade().getSampleCountsByResultType(new SampleDashboardCriteria().disease(Disease.CORONAVIRUS));

		assertEquals(1, sampleCountsForCovid.get(PathogenTestResultType.INDETERMINATE));
		assertEquals(1, sampleCountsForCovid.get(PathogenTestResultType.PENDING));
		assertEquals(1, sampleCountsForCovid.get(PathogenTestResultType.NEGATIVE));
		assertEquals(1, sampleCountsForCovid.get(PathogenTestResultType.POSITIVE));
		assertNull(sampleCountsForCovid.get(PathogenTestResultType.NOT_DONE));

		Map<PathogenTestResultType, Long> sampleCountsForSampleMaterial =
			getSampleDashboardFacade().getSampleCountsByResultType(new SampleDashboardCriteria().sampleMaterial(SampleMaterial.BLOOD));

		assertEquals(1, sampleCountsForSampleMaterial.get(PathogenTestResultType.INDETERMINATE));
		assertNull(sampleCountsForSampleMaterial.get(PathogenTestResultType.PENDING));
		assertNull(sampleCountsForSampleMaterial.get(PathogenTestResultType.NEGATIVE));
		assertNull(sampleCountsForSampleMaterial.get(PathogenTestResultType.POSITIVE));
		assertNull(sampleCountsForSampleMaterial.get(PathogenTestResultType.NOT_DONE));

		Map<PathogenTestResultType, Long> sampleCountsForMultipleFilters = getSampleDashboardFacade().getSampleCountsByResultType(
			new SampleDashboardCriteria().dateBetween(DateHelper.subtractDays(reportDate, 2), DateHelper.addDays(reportDate, 2))
				.sampleDateType(SampleDashboardFilterDateType.ASSOCIATED_ENTITY_REPORT_DATE)
				.sampleMaterial(SampleMaterial.BLOOD)
				.disease(Disease.CORONAVIRUS)
				.district(rdcf.district));

		assertEquals(1, sampleCountsForMultipleFilters.get(PathogenTestResultType.INDETERMINATE));
		assertNull(sampleCountsForMultipleFilters.get(PathogenTestResultType.PENDING));
		assertNull(sampleCountsForMultipleFilters.get(PathogenTestResultType.NEGATIVE));
		assertNull(sampleCountsForMultipleFilters.get(PathogenTestResultType.POSITIVE));
		assertNull(sampleCountsForMultipleFilters.get(PathogenTestResultType.NOT_DONE));
	}

	@Test
	public void testGetSampleCountsWithNoDiseaseFlag() {
		EventDto eventWithNoDisease = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipantWithNoDisease =
			creator.createEventParticipant(eventWithNoDisease.toReference(), creator.createPerson(), user.toReference());
		creator.createSample(
			eventParticipantWithNoDisease.toReference(),
			new Date(),
			new Date(),
			user.toReference(),
			SampleMaterial.CRUST,
			rdcf.facility);
		creator.createSample(
			eventParticipantWithNoDisease.toReference(),
			new Date(),
			new Date(),
			user.toReference(),
			SampleMaterial.CRUST,
			rdcf.facility);

		EventDto eventWithDisease = creator.createEvent(user.toReference(), Disease.CORONAVIRUS);
		EventParticipantDto eventParticipantWithDisease =
			creator.createEventParticipant(eventWithDisease.toReference(), creator.createPerson(), user.toReference());
		creator
			.createSample(eventParticipantWithDisease.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.CRUST, rdcf.facility);

		Map<PathogenTestResultType, Long> counts =
			getSampleDashboardFacade().getSampleCountsByResultType(new SampleDashboardCriteria().withNoDisease(null));
		assertEquals(3, counts.get(PathogenTestResultType.PENDING));

		Map<PathogenTestResultType, Long> countsWithNoDisease =
			getSampleDashboardFacade().getSampleCountsByResultType(new SampleDashboardCriteria().withNoDisease(true));
		assertEquals(2, countsWithNoDisease.get(PathogenTestResultType.PENDING));

		Map<PathogenTestResultType, Long> countsWithDisease =
			getSampleDashboardFacade().getSampleCountsByResultType(new SampleDashboardCriteria().withNoDisease(false));
		assertEquals(1, countsWithDisease.get(PathogenTestResultType.PENDING));

	}

	@Test
	public void testGetSampleCountsByPurpose() {
		creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSamplePurpose(SamplePurpose.INTERNAL);
		});

		creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSamplePurpose(SamplePurpose.EXTERNAL);
		});

		Map<SamplePurpose, Long> sampleCounts = getSampleDashboardFacade().getSampleCountsByPurpose(new SampleDashboardCriteria());

		assertEquals(1, sampleCounts.get(SamplePurpose.INTERNAL));
		assertEquals(1, sampleCounts.get(SamplePurpose.EXTERNAL));
	}

	@Test
	public void testGetSampleCountsBySpecimenCondition() {
		creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSamplePurpose(SamplePurpose.EXTERNAL);
			s.setReceived(true);
			s.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		});

		creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSamplePurpose(SamplePurpose.EXTERNAL);
			s.setReceived(true);
			s.setSpecimenCondition(SpecimenCondition.NOT_ADEQUATE);
		});

		creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSamplePurpose(SamplePurpose.EXTERNAL);
			s.setReceived(true);
		});

		// not received sample should not be counted
		creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSamplePurpose(SamplePurpose.EXTERNAL);
			s.setReceived(false);
			s.setSpecimenCondition(SpecimenCondition.NOT_ADEQUATE);
		});

		creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSamplePurpose(SamplePurpose.INTERNAL);
			s.setSpecimenCondition(SpecimenCondition.NOT_ADEQUATE);
		});

		Map<SpecimenCondition, Long> sampleCounts = getSampleDashboardFacade().getSampleCountsBySpecimenCondition(new SampleDashboardCriteria());

		assertEquals(1, sampleCounts.get(SpecimenCondition.ADEQUATE));
		assertEquals(1, sampleCounts.get(SpecimenCondition.NOT_ADEQUATE));
		assertEquals(1, sampleCounts.get(null));
	}

	@Test
	public void testGetSampleCountsByShipmentStatus() {
		FacilityReferenceDto lab = creator.createFacility("lab", rdcf.region, rdcf.district, null, FacilityType.LABORATORY).toReference();

		// not shipped sample
		creator.createSample(caze.toReference(), user.toReference(), lab, s -> {
			s.setSamplePurpose(SamplePurpose.EXTERNAL);
		});
		// not shipped sample
		creator.createSample(caze.toReference(), user.toReference(), lab, s -> {
			s.setSamplePurpose(SamplePurpose.EXTERNAL);
			s.setShipped(false);
			s.setReceived(false);
		});

		// not shipped sample
		creator.createSample(caze.toReference(), user.toReference(), lab, s -> {
			s.setSamplePurpose(SamplePurpose.EXTERNAL);
			s.setShipped(false);
		});

		// not shipped sample
		creator.createSample(caze.toReference(), user.toReference(), lab, s -> {
			s.setSamplePurpose(SamplePurpose.EXTERNAL);
			s.setReceived(false);
		});

		// shipped sample
		creator.createSample(caze.toReference(), user.toReference(), lab, s -> {
			s.setSamplePurpose(SamplePurpose.EXTERNAL);
			s.setShipped(true);
		});

		// shipped sample
		creator.createSample(caze.toReference(), user.toReference(), lab, s -> {
			s.setSamplePurpose(SamplePurpose.EXTERNAL);
			s.setShipped(true);
			s.setReceived(false);
		});

		// received sample
		creator.createSample(caze.toReference(), user.toReference(), lab, s -> {
			s.setSamplePurpose(SamplePurpose.EXTERNAL);
			s.setShipped(true);
			s.setReceived(true);
		});

		// received sample
		creator.createSample(caze.toReference(), user.toReference(), lab, s -> {
			s.setSamplePurpose(SamplePurpose.EXTERNAL);
			s.setReceived(true);
		});

		// internal sample should not be counted
		creator.createSample(caze.toReference(), user.toReference(), lab, s -> {
			s.setSamplePurpose(SamplePurpose.INTERNAL);
			s.setShipped(true);
		});

		Map<SampleShipmentStatus, Long> sampleCounts = getSampleDashboardFacade().getSampleCountsByShipmentStatus(new SampleDashboardCriteria());

		assertEquals(4, sampleCounts.get(SampleShipmentStatus.NOT_SHIPPED));
		assertEquals(2, sampleCounts.get(SampleShipmentStatus.SHIPPED));
		assertEquals(2, sampleCounts.get(SampleShipmentStatus.RECEIVED));
	}

	@Test
	public void getResultCountByResultType() {
		SampleDto sample1 = createSampleByResultType(caze, reportDate, null, SampleMaterial.BLOOD);
		createPathogenTestByResultType(sample1, Disease.CORONAVIRUS, reportDate, PathogenTestResultType.INDETERMINATE);

		SampleDto sample2 = createSampleByResultType(caze, reportDate, null, SampleMaterial.BLOOD);
		createPathogenTestByResultType(sample2, Disease.CORONAVIRUS, reportDate, PathogenTestResultType.PENDING);
		createPathogenTestByResultType(sample2, Disease.CORONAVIRUS, reportDate, PathogenTestResultType.NEGATIVE);

		SampleDto sample3 = createSampleByResultType(caze, reportDate, null, SampleMaterial.BLOOD);
		createPathogenTestByResultType(sample3, Disease.CORONAVIRUS, reportDate, PathogenTestResultType.POSITIVE);

		Map<PathogenTestResultType, Long> pathogenTestCountsForRelevantDate = getSampleDashboardFacade().getTestResultCountsByResultType(
			new SampleDashboardCriteria().dateBetween(DateHelper.subtractDays(reportDate, 2), DateHelper.addDays(reportDate, 2))
				.sampleDateType(SampleDashboardFilterDateType.MOST_RELEVANT));

		assertEquals(1, pathogenTestCountsForRelevantDate.get(PathogenTestResultType.INDETERMINATE));
		assertEquals(1, pathogenTestCountsForRelevantDate.get(PathogenTestResultType.PENDING));
		assertEquals(1, pathogenTestCountsForRelevantDate.get(PathogenTestResultType.NEGATIVE));
		assertEquals(1, pathogenTestCountsForRelevantDate.get(PathogenTestResultType.POSITIVE));
	}

	public SampleDto createSampleByResultType(
		CaseDataDto caze,
		Date sampleDateTime,
		PathogenTestResultType pathogenTestResultType,
		SampleMaterial sampleMaterial) {
		return creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setSampleDateTime(sampleDateTime);
			if (pathogenTestResultType != null) {
				s.setPathogenTestResult(pathogenTestResultType);
			}
			s.setSampleMaterial(sampleMaterial);
		});
	}

	public PathogenTestDto createPathogenTestByResultType(
		SampleDto sample,
		Disease disease,
		Date reportDate,
		PathogenTestResultType pathogenTestResultType) {
		return creator.createPathogenTest(
			sample.toReference(),
			PathogenTestType.RAPID_TEST,
			disease,
			reportDate,
			rdcf.facility,
			user.toReference(),
			pathogenTestResultType,
			"",
			true,
			null);
	}
}
