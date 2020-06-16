/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.report;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.report.WeeklyReportCriteria;
import de.symeda.sormas.api.report.WeeklyReportOfficerSummaryDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class WeeklyReportService extends AbstractAdoService<WeeklyReport> {

	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private UserService userService;

	public WeeklyReportService() {
		super(WeeklyReport.class);
	}

	public long getNumberOfWeeklyReportsByFacility(Facility facility, EpiWeek epiWeek) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WeeklyReport> from = cq.from(getElementClass());

		cq.select(cb.count(from));
		Predicate filter = and(
			cb,
			createUserFilter(cb, cq, from),
			cb.equal(from.get(WeeklyReport.HEALTH_FACILITY), facility),
			cb.equal(from.get(WeeklyReport.YEAR), epiWeek.getYear()),
			cb.equal(from.get(WeeklyReport.EPI_WEEK), epiWeek.getWeek()));

		cq.where(filter);

		return em.createQuery(cq).getSingleResult();
	}

	public List<WeeklyReport> getByFacility(Facility facility, EpiWeek epiWeek) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WeeklyReport> cq = cb.createQuery(getElementClass());
		Root<WeeklyReport> from = cq.from(getElementClass());

		Predicate filter = and(
			cb,
			createUserFilter(cb, cq, from),
			cb.equal(from.get(WeeklyReport.HEALTH_FACILITY), facility),
			cb.equal(from.get(WeeklyReport.YEAR), epiWeek.getYear()),
			cb.equal(from.get(WeeklyReport.EPI_WEEK), epiWeek.getWeek()));

		cq.where(filter);
		cq.orderBy(cb.asc(from.get(WeeklyReport.HEALTH_FACILITY)));
		return em.createQuery(cq).getResultList();
	}

	public WeeklyReport getByEpiWeekAndUser(EpiWeek epiWeek, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WeeklyReport> cq = cb.createQuery(getElementClass());
		Root<WeeklyReport> from = cq.from(getElementClass());

		Predicate filter = cb.equal(from.get(WeeklyReport.EPI_WEEK), epiWeek.getWeek());
		filter = cb.and(filter, cb.equal(from.get(WeeklyReport.YEAR), epiWeek.getYear()));
		filter = cb.and(filter, cb.equal(from.get(WeeklyReport.REPORTING_USER), user));

		cq.where(filter);
		try {
			return em.createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<WeeklyReportOfficerSummaryDto> getWeeklyReportSummariesPerOfficer(Region region, EpiWeek epiWeek) {

		WeeklyReportCriteria officerReportCriteria = new WeeklyReportCriteria().epiWeek(epiWeek);
		WeeklyReportCriteria informantsReportCriteria = new WeeklyReportCriteria().epiWeek(epiWeek).officerReport(false);

		Stream<User> officers = userService.getAllByRegionAndUserRoles(region, UserRole.SURVEILLANCE_OFFICER).stream();
		officers = filterWeeklyReportUsers(getCurrentUser(), officers);

		List<WeeklyReportOfficerSummaryDto> summaryDtos = officers.map(officer -> {
			officerReportCriteria.reportingUser(new UserReferenceDto(officer.getUuid()));
			List<WeeklyReport> officerReports = queryByCriteria(officerReportCriteria, null, null, true);

			WeeklyReportOfficerSummaryDto summaryDto = new WeeklyReportOfficerSummaryDto();
			summaryDto.setOfficer(UserFacadeEjb.toReferenceDto(officer));
			summaryDto.setDistrict(DistrictFacadeEjb.toReferenceDto(officer.getDistrict()));

			if (officerReports.size() > 0) {
				WeeklyReport officerReport = officerReports.get(0);
				summaryDto.setOfficerReportDate(officerReport.getReportDateTime());
				summaryDto.setTotalCaseCount(officerReport.getTotalNumberOfCases());
			}

			Long informants = userService.countByAssignedOfficer(officer);
			summaryDto.setInformants(informants.intValue());

			informantsReportCriteria.assignedOfficer(summaryDto.getOfficer());
			informantsReportCriteria.zeroReport(false);
			Long informantCaseReports = countByCriteria(informantsReportCriteria, null);
			summaryDto.setInformantCaseReports(informantCaseReports.intValue());

			informantsReportCriteria.zeroReport(true);
			Long informantZeroReports = countByCriteria(informantsReportCriteria, null);
			summaryDto.setInformantZeroReports(informantZeroReports.intValue());

			return summaryDto;
		}).collect(Collectors.toList());

		return summaryDtos;
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<WeeklyReport, WeeklyReport> from) {

		User currentUser = getCurrentUser();
		// National users can access all reports in the system
		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (currentUser == null
				|| (jurisdictionLevel == JurisdictionLevel.NATION && !currentUser.hasAnyUserRole(UserRole.POE_NATIONAL_USER))
				|| currentUser.hasAnyUserRole(UserRole.REST_USER)) {
			return null;
		}

		// Whoever created the weekly report is allowed to access it
		Join<WeeklyReport, User> informant = from.join(WeeklyReport.REPORTING_USER, JoinType.LEFT);
		Predicate filter = cb.equal(informant, currentUser);

		// Allow access based on user role

		// Supervisors see all reports from users in their region
		if (currentUser.getRegion() != null
			&& jurisdictionLevel == JurisdictionLevel.REGION) {
			filter = cb.or(filter, cb.equal(from.join(WeeklyReport.REPORTING_USER, JoinType.LEFT).get(User.REGION), currentUser.getRegion()));
		}

		// Officers see all reports from their assigned informants
		if (currentUser.hasAnyUserRole(UserRole.SURVEILLANCE_OFFICER)) {
			filter = cb.or(filter, cb.equal(informant.get(User.ASSOCIATED_OFFICER), currentUser));
		}

		return filter;
	}

	/**
	 * Filters users analogous to reportingUsers in ::createUserFilter
	 * 
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	public Stream<User> filterWeeklyReportUsers(User user, Stream<User> usersStream) {

		if (user == null) {
			return usersStream;
		}

		final JurisdictionLevel jurisdictionLevel = user.getJurisdictionLevel();
		// National users can access all reports in the system
		if (jurisdictionLevel == JurisdictionLevel.NATION && !user.hasAnyUserRole(UserRole.POE_NATIONAL_USER)) {
			return usersStream;
		}

		// Whoever created the weekly report is allowed to access it
		java.util.function.Predicate<User> constraints = user::equals;

		// Allow access based on user role

		// Supervisors see all reports from users in their region
		if (user.getRegion() != null && jurisdictionLevel == JurisdictionLevel.REGION) {
			constraints = constraints.or(u -> user.getRegion().equals(u.getRegion()));
		}

		// Officers see all reports from their assigned informants
		if (user.hasAnyUserRole(UserRole.SURVEILLANCE_OFFICER)) {
			constraints = constraints.or(u -> user.equals(u.getAssociatedOfficer()));
		}

		return usersStream.filter(constraints);
	}

	public List<WeeklyReport> queryByCriteria(WeeklyReportCriteria criteria, User user, String orderProperty, boolean asc) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WeeklyReport> cq = cb.createQuery(WeeklyReport.class);
		Root<WeeklyReport> from = cq.from(WeeklyReport.class);

		Optional.ofNullable(orderProperty).map(from::get).map(p -> asc ? cb.asc(p) : cb.desc(p)).ifPresent(cq::orderBy);

		and(cb, Optional.ofNullable(createUserFilter(cb, cq, from)), buildCriteriaFilter(criteria, cb, from)).ifPresent(cq::where);

		return em.createQuery(cq).getResultList();
	}

	public Long countByCriteria(WeeklyReportCriteria criteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WeeklyReport> from = cq.from(WeeklyReport.class);

		and(cb, Optional.ofNullable(createUserFilter(cb, cq, from)), buildCriteriaFilter(criteria, cb, from)).ifPresent(cq::where);

		cq.select(cb.count(from));

		return em.createQuery(cq).getSingleResult();
	}

	public Optional<Predicate> buildCriteriaFilter(WeeklyReportCriteria criteria, CriteriaBuilder cb, Root<WeeklyReport> from) {

		Optional<WeeklyReportCriteria> c = Optional.of(criteria);

		//@formatter:off
		Optional<Predicate> filter = and(cb,
				//EpiWeek
				c.map(WeeklyReportCriteria::getEpiWeek)
				.map(w ->
					and(cb,
						cb.equal(from.get(WeeklyReport.YEAR), w.getYear()),
						cb.equal(from.get(WeeklyReport.EPI_WEEK), w.getWeek()))
				),
				//ReportingUser
				c.map(WeeklyReportCriteria::getReportingUser)
				.map(u ->
					cb.equal(
						from.join(WeeklyReport.REPORTING_USER, JoinType.LEFT).get(User.UUID), 
						u.getUuid())
				),
				//getReportingUserRegion
				c.map(WeeklyReportCriteria::getReportingUserRegion)
				.map(r -> cb.equal(
						from.join(WeeklyReport.REPORTING_USER, JoinType.LEFT).join(User.REGION, JoinType.LEFT).get(Region.UUID), 
						r.getUuid())
				),
				//getAssignedOfficer
				c.map(WeeklyReportCriteria::getAssignedOfficer)
				.map(u -> cb.equal(
						from.join(WeeklyReport.ASSIGNED_OFFICER, JoinType.LEFT).get(User.UUID),
						u.getUuid())
				),
				//getOfficerReport
				c.map(WeeklyReportCriteria::getOfficerReport)
				.map(b -> b ? cb.isNull(from.get(WeeklyReport.ASSIGNED_OFFICER))
						: cb.isNotNull(from.get(WeeklyReport.ASSIGNED_OFFICER))
				),
				//getZeroReport
				c.map(WeeklyReportCriteria::getZeroReport)
				.map(b -> {
					Path<Integer> count = from.get(WeeklyReport.TOTAL_NUMBER_OF_CASES);
					return b ? cb.equal(count, 0) : cb.notEqual(count, 0);
				}
				)
			);
		//@formatter:on

		return filter;
	}
}
