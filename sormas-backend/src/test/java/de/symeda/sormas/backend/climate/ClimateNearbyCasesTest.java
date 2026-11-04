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

package de.symeda.sormas.backend.climate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.climate.ClimateCaseFacade;
import de.symeda.sormas.api.climate.ClimateNearbyCasesDto;
import de.symeda.sormas.api.climate.ClimateNearbyCasesWeekDto;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateCaseFacadeEjb.ClimateCaseFacadeEjbLocal;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;

/**
 * Cases near an environment: by home address, else report point, within the radius; without a point, by community.
 */
class ClimateNearbyCasesTest extends AbstractBeanTest {

	private static final double SITE_LATITUDE = 6.80;
	private static final double SITE_LONGITUDE = 3.50;

	private final LocalDate day = LocalDate.now().minusDays(3);
	private TestDataCreator.RDCF rdcf;
	private UserDto user;
	private ClimateCaseFacade facade;
	private EnvironmentDto environment;

	@BeforeEach
	void setUpClimate() {
		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF();
		user = loginWith(creator.createNationalUser());
		facade = getBean(ClimateCaseFacadeEjbLocal.class);
		environment = creator.createEnvironment("Well A", EnvironmentMedia.WATER, user.toReference(), rdcf, e -> {
			e.getLocation().setLatitude(SITE_LATITUDE);
			e.getLocation().setLongitude(SITE_LONGITUDE);
			e.getLocation().setCommunity(rdcf.community);
		});
	}

	@Test
	void testDistance() {
		// one hundredth of a degree of latitude is about 1.11 km
		assertEquals(1.112, ClimateCaseFacadeEjb.distanceKm(6.80, 3.50, 6.81, 3.50), 0.001);
		assertEquals(0.0, ClimateCaseFacadeEjb.distanceKm(6.80, 3.50, 6.80, 3.50), 1e-9);
		assertFalse(ClimateCaseFacadeEjb.isUsablePoint(0.0, 0.0));
		assertFalse(ClimateCaseFacadeEjb.isUsablePoint(91.0, 3.0));
		assertTrue(ClimateCaseFacadeEjb.isUsablePoint(6.8, 3.5));
	}

	@Test
	void testRadiusPointsAndCommunity() {

		Community otherCommunity = creator.createCommunity("Other community", getDistrictService().getByUuid(rdcf.district.getUuid()));

		String home = createCase(Disease.DENGUE, CaseClassification.SUSPECT, 6.81, SITE_LONGITUDE, null, null, rdcf.community).getUuid();
		String farHome = createCase(Disease.DENGUE, CaseClassification.SUSPECT, 6.83, SITE_LONGITUDE, null, null, rdcf.community).getUuid();
		String reported = createCase(Disease.DENGUE, CaseClassification.PROBABLE, null, null, 6.805, 3.505, rdcf.community).getUuid();
		// the home address wins over a report point near the site
		createCase(Disease.DENGUE, CaseClassification.SUSPECT, 6.90, SITE_LONGITUDE, SITE_LATITUDE, SITE_LONGITUDE, rdcf.community);
		String sameCommunity = createCase(Disease.DENGUE, CaseClassification.SUSPECT, null, null, null, null, rdcf.community).getUuid();
		createCase(Disease.DENGUE, CaseClassification.SUSPECT, null, null, null, null, CommunityFacadeEjb.toReferenceDto(otherCommunity));
		createCase(Disease.DENGUE, CaseClassification.NO_CASE, SITE_LATITUDE, SITE_LONGITUDE, null, null, rdcf.community);
		String cholera = createCase(Disease.CHOLERA, CaseClassification.SUSPECT, SITE_LATITUDE, SITE_LONGITUDE, null, null, rdcf.community).getUuid();

		ClimateNearbyCasesDto dengue = facade.getNearbyCases(environment.toReference(), Disease.DENGUE, 2, day.minusDays(10), day.plusDays(1));
		assertFalse(dengue.isByCommunityOnly());
		ClimateNearbyCasesWeekDto week = weekOf(dengue);
		assertEquals(new HashSet<>(Arrays.asList(home, reported)), new HashSet<>(week.getCaseUuids()));
		assertEquals(Collections.singletonList(sameCommunity), week.getCommunityCaseUuids());
		assertEquals(1, dengue.getCommunityTotal());

		ClimateNearbyCasesWeekDto withinFive = weekOf(facade.getNearbyCases(environment.toReference(), Disease.DENGUE, 5, day, day));
		assertEquals(new HashSet<>(Arrays.asList(home, farHome, reported)), new HashSet<>(withinFive.getCaseUuids()));

		ClimateNearbyCasesWeekDto allDiseases = weekOf(facade.getNearbyCases(environment.toReference(), null, 2, day, day));
		assertEquals(new HashSet<>(Arrays.asList(home, reported, cholera)), new HashSet<>(allDiseases.getCaseUuids()));
	}

	@Test
	void testEnvironmentWithoutUsableGpsCountsByCommunity() {

		String home = createCase(Disease.DENGUE, CaseClassification.SUSPECT, 6.81, SITE_LONGITUDE, null, null, rdcf.community).getUuid();
		String noPoint = createCase(Disease.DENGUE, CaseClassification.SUSPECT, null, null, null, null, rdcf.community).getUuid();
		executeInTransaction(em -> {
			Environment entity = getEnvironmentService().getByUuid(environment.getUuid());
			entity.getLocation().setLatitude(0.0);
			entity.getLocation().setLongitude(0.0);
		});

		ClimateNearbyCasesDto nearby = facade.getNearbyCases(environment.toReference(), Disease.DENGUE, 2, day, day);
		assertTrue(nearby.isByCommunityOnly());
		ClimateNearbyCasesWeekDto week = weekOf(nearby);
		assertEquals(0, week.getCount());
		assertEquals(new HashSet<>(Arrays.asList(home, noPoint)), new HashSet<>(week.getCommunityCaseUuids()));
	}

	private ClimateNearbyCasesWeekDto weekOf(ClimateNearbyCasesDto nearby) {
		List<ClimateNearbyCasesWeekDto> weeks = nearby.getWeeks()
			.stream()
			.filter(week -> week.getWeek().equals(ClimateStatistics.epiWeekOf(day)))
			.collect(Collectors.toList());
		assertEquals(1, weeks.size());
		return weeks.get(0);
	}

	/**
	 * Creates a case with onset on {@link #day}, its person's home address point and the case's report point (either may
	 * be empty), with the home address in the given community.
	 */
	private CaseDataDto createCase(
		Disease disease,
		CaseClassification classification,
		Double homeLatitude,
		Double homeLongitude,
		Double reportLatitude,
		Double reportLongitude,
		CommunityReferenceDto community) {

		PersonDto person = creator.createPerson("Case", "Person", p -> {
			p.getAddress().setLatitude(homeLatitude);
			p.getAddress().setLongitude(homeLongitude);
			p.getAddress().setRegion(rdcf.region);
			p.getAddress().setDistrict(rdcf.district);
			p.getAddress().setCommunity(community);
		});
		Date date = ClimateCalendar.toDate(day);
		return creator.createCase(user.toReference(), person.toReference(), rdcf, c -> {
			c.setDisease(disease);
			c.setCaseClassification(classification);
			c.setReportDate(date);
			c.getSymptoms().setOnsetDate(date);
			c.setReportLat(reportLatitude);
			c.setReportLon(reportLongitude);
			// SORMAS gives a new case without a report point its facility's point; an accuracy keeps the point empty
			c.setReportLatLonAccuracy(reportLatitude == null ? 0f : null);
		});
	}

}
