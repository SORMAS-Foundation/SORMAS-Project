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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.report;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.report.WeeklyReportCriteria;
import de.symeda.sormas.api.report.WeeklyReportOfficerSummaryDto;
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
		Predicate filter = cb.equal(from.get(WeeklyReport.HEALTH_FACILITY), facility);
		filter = cb.and(filter, cb.equal(from.get(WeeklyReport.YEAR), epiWeek.getYear()));
		filter = cb.and(filter, cb.equal(from.get(WeeklyReport.EPI_WEEK), epiWeek.getWeek()));

		cq.where(filter);
		return em.createQuery(cq).getSingleResult();
	}

	public List<WeeklyReport> getByFacility(Facility facility, EpiWeek epiWeek) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WeeklyReport> cq = cb.createQuery(getElementClass());
		Root<WeeklyReport> from = cq.from(getElementClass());

		Predicate filter = cb.equal(from.get(WeeklyReport.HEALTH_FACILITY), facility);
		filter = cb.and(filter, cb.equal(from.get(WeeklyReport.YEAR), epiWeek.getYear()));
		filter = cb.and(filter, cb.equal(from.get(WeeklyReport.EPI_WEEK), epiWeek.getWeek()));

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

		List<WeeklyReportOfficerSummaryDto> summaryDtos = new ArrayList<>();

		WeeklyReportCriteria officerReportCriteria = new WeeklyReportCriteria().epiWeek(epiWeek);
		WeeklyReportCriteria informantsReportCriteria = new WeeklyReportCriteria().epiWeek(epiWeek).isOfficer(false);

		List<User> officers = userService.getAllByRegionAndUserRoles(region, UserRole.SURVEILLANCE_OFFICER);

		for (User officer : officers) {
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
			informantsReportCriteria.isZeroReport(false);
			Long informantCaseReports = countByCriteria(informantsReportCriteria, null);
			summaryDto.setInformantCaseReports(informantCaseReports.intValue());

			informantsReportCriteria.isZeroReport(true);
			Long informantZeroReports = countByCriteria(informantsReportCriteria, null);
			summaryDto.setInformantZeroReports(informantZeroReports.intValue());

			summaryDtos.add(summaryDto);
		}

		return summaryDtos;
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<WeeklyReport, WeeklyReport> from,
			User user) {

		if (user == null) {
			return null;
		}

		// National users can access all reports in the system
		if (user.getUserRoles().contains(UserRole.NATIONAL_USER)
				|| user.getUserRoles().contains(UserRole.NATIONAL_OBSERVER)) {
			return null;
		}

		// Whoever created the weekly report is allowed to access it
		Join<WeeklyReport, User> informant = from.join(WeeklyReport.REPORTING_USER, JoinType.LEFT);
		Predicate filter = cb.equal(informant, user);

		// Allow access based on user role
		for (UserRole userRole : user.getUserRoles()) {
			switch (userRole) {
			case SURVEILLANCE_SUPERVISOR:
			case CONTACT_SUPERVISOR:
			case CASE_SUPERVISOR:
			case STATE_OBSERVER:
				// Supervisors see all reports from users in their region
				if (user.getRegion() != null) {
					filter = cb.or(filter, cb.equal(
							from.join(WeeklyReport.REPORTING_USER, JoinType.LEFT).get(User.REGION), user.getRegion()));
				}
				break;
			case SURVEILLANCE_OFFICER:
				// Officers see all reports from their assigned informants
				filter = cb.or(filter, cb.equal(informant.get(User.ASSOCIATED_OFFICER), user));
				break;
			default:
				break;
			}
		}

		return filter;
	}

	public List<WeeklyReport> queryByCriteria(WeeklyReportCriteria criteria, User user, String orderProperty,
			boolean asc) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WeeklyReport> cq = cb.createQuery(WeeklyReport.class);
		Root<WeeklyReport> from = cq.from(WeeklyReport.class);

		if (orderProperty != null) {
			cq.orderBy(asc ? cb.asc(from.get(orderProperty)) : cb.desc(from.get(orderProperty)));
		}

		Predicate filter = createUserFilter(cb, cq, from, user);
		filter = and(cb, filter, buildCriteriaFilter(criteria, cb, from));
		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}

	public Long countByCriteria(WeeklyReportCriteria criteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WeeklyReport> from = cq.from(WeeklyReport.class);

		Predicate filter = createUserFilter(cb, cq, from, user);
		filter = and(cb, filter, buildCriteriaFilter(criteria, cb, from));
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(from));

		return em.createQuery(cq).getSingleResult();
	}

	public Predicate buildCriteriaFilter(WeeklyReportCriteria criteria, CriteriaBuilder cb, Root<WeeklyReport> from) {
		Predicate filter = null;
		if (criteria.getEpiWeek() != null) {
			filter = and(cb, filter, cb.equal(from.get(WeeklyReport.YEAR), criteria.getEpiWeek().getYear()));
			filter = and(cb, filter, cb.equal(from.get(WeeklyReport.EPI_WEEK), criteria.getEpiWeek().getWeek()));
		}
		if (criteria.getReportingUser() != null) {
			filter = and(cb, filter, cb.equal(from.join(WeeklyReport.REPORTING_USER, JoinType.LEFT)
					.get(User.UUID), criteria.getReportingUser().getUuid()));
		}
		if (criteria.getReportingUserRegion() != null) {
			filter = and(cb, filter, cb.equal(from.join(WeeklyReport.REPORTING_USER, JoinType.LEFT)
					.join(User.REGION, JoinType.LEFT).get(Region.UUID), criteria.getReportingUserRegion().getUuid()));
		}
		if (criteria.getAssignedOfficer() != null) {
			filter = and(cb, filter, cb.equal(from.join(WeeklyReport.ASSIGNED_OFFICER, JoinType.LEFT).get(User.UUID),
					criteria.getAssignedOfficer().getUuid()));
		}
		if (criteria.getIsOfficer() != null) {
			filter = and(cb, filter, criteria.getIsOfficer() ? cb.isNull(from.get(WeeklyReport.ASSIGNED_OFFICER))
					: cb.isNotNull(from.get(WeeklyReport.ASSIGNED_OFFICER)));
		}
		if (criteria.getIsZeroReport() != null) {
			filter = and(cb, filter,
					criteria.getIsZeroReport() ? cb.equal(from.get(WeeklyReport.TOTAL_NUMBER_OF_CASES), 0)
							: cb.notEqual(from.get(WeeklyReport.TOTAL_NUMBER_OF_CASES), 0));
		}
		return filter;
	}
}
