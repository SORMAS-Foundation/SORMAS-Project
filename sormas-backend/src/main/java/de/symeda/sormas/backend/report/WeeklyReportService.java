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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.report.WeeklyReportSummaryDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class WeeklyReportService extends AbstractAdoService<WeeklyReport> {

	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;

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

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@SuppressWarnings("rawtypes")
	public List<WeeklyReportSummaryDto> getWeeklyReportSummariesPerRegion(EpiWeek epiWeek) {
		Query query = em.createNativeQuery(
				"SELECT region_id, MAX(officers) as officers, MAX(omissing) as omissing, MAX(oreport) as oreport, MAX(ozero) as ozero, MAX(informants) as informants, MAX(imissing) as imissing, MAX(ireport) as ireport, MAX(izero) as izero FROM ("
				+ "SELECT region_id COUNT(officers) as officers, SUM(missing) as missing, SUM(report) as report, SUM(zero) as zero, NULL as informants, NULL as imissing, NULL as ireport, NULL as izero "
				+ "FROM ("
				+ "SELECT users.id as officers, users.region_id, "
				+ "CASE WHEN COUNT(wr.id) = 0 THEN 1 ELSE 0 END as omissing, "
				+ "CASE WHEN SUM(wr.totalnumberofcases) > 0 AND COUNT(wr.id) = 1 THEN 1 ELSE 0 END as oreport, "
				+ "CASE WHEN SUM(wr.totalnumberofcases) = 0 AND COUNT(wr.id) = 1 THEN 1 ELSE 0 END as ozero "
				+ "FROM users "
				+ "INNER JOIN users_userroles ON users_userroles.user_id = users.id "
				+ "LEFT JOIN ("
				+ "SELECT * FROM weeklyreport WHERE year = " + epiWeek.getYear() + " AND epiweek = " + epiWeek.getWeek()
				+ ") as wr ON wr.reportinguser_id = users.id "
				+ "WHERE users_userroles.userrole = 'SURVEILLANCE_OFFICER' "
				+ "GROUP BY users.username, users.id"
				+ ") as officer_query"
				+ "GROUP BY region_id"
				+ "UNION ALL"
				+ "SELECT region_id, NULL as officers, NULL as omissing, NULL as oreport, NULL as ozero, COUNT(informants) as informants, SUM(imissing) as imissing, SUM(ireport) as ireport, SUM(izero) as izero "
				+ "FROM ("
				+ "SELECT users.id as informants, iofficer.region_id as region_id, "
				+ "CASE WHEN COUNT(wr.id) = 0 THEN 1 ELSE 0 END as imissing, "
				+ "CASE WHEN SUM(wr.totalnumberofcases) > 0 AND COUNT(wr.id) = 1 THEN 1 ELSE 0 END as ireport, "
				+ "CASE WHEN SUM(wr.totalnumberofcases) = 0 AND COUNT(wr.id) = 1 THEN 1 ELSE 0 END as izero, "
				+ "FROM users "
				+ "INNER JOIN users_userroles ON users_userroles.user_id = users.id "
				+ "LEFT JOIN ("
				+ "SELECT * FROM weeklyreport WHERE year = " + epiWeek.getYear() + " AND epiweek = " + epiWeek.getWeek()
				+ ") as wr ON wr.reportinguser_id = users.id "
				+ "LEFT JOIN ("
				+ "SELECT * FROM users"
				+ ") as iofficer ON users.associatedofficer_id = iofficer.id "
				+ "WHERE users_userroles.userrole IN ('HOSPITAL_INFORMANT', 'COMMUNITY_INFORMANT') "
				+ "GROUP BY iofficer.region_id, users.id"
				+ ") as informant_query "
				+ "GROUP BY region_id"
				+ ") AS t GROUP BY t.region_id");
				
		List results = query.getResultList();

		List<WeeklyReportSummaryDto> summaryDtos = new ArrayList<>();

		for (int i = 0; i < results.size(); i++) {
			Object[] result = (Object[]) results.get(i);
			int officerReports = ((Long) result[3]).intValue();
			int officerMissingReports = ((Long) result[2]).intValue();
			int officerZeroReports= ((Long) result[4]).intValue();
			int informantReports = ((Long) result[7]).intValue();
			int informantMissingReports = ((Long) result[6]).intValue();
			int informantZeroReports = ((Long) result[8]).intValue();
			
			WeeklyReportSummaryDto summaryDto = new WeeklyReportSummaryDto();
			summaryDto.setRegion(RegionFacadeEjb.toReferenceDto(regionService.getById((long) result[0])));
			summaryDto.setOfficers(((Long) result[1]).intValue());
			summaryDto.setInformants(((Long) result[5]).intValue());
			summaryDto.setOfficerMissingReports(officerMissingReports);
			summaryDto.setOfficerReports(officerReports);
			summaryDto.setOfficerZeroReports(officerZeroReports);
			summaryDto.setInformantMissingReports(informantMissingReports);
			summaryDto.setInformantReports(informantReports);
			summaryDto.setInformantZeroReports(informantZeroReports);
			
			summaryDtos.add(summaryDto);
		}

		return summaryDtos;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<WeeklyReportSummaryDto> getWeeklyReportSummariesPerOfficer(Region region, EpiWeek epiWeek) {
		Query query = em.createNativeQuery("SELECT id, district_id, COUNT(inf) as informants, SUM(missing) as missing, SUM(report) as report, SUM(zero) as zero "
				+ "FROM ("
					+ "SELECT users.id, users.district_id, inf, "
					+ "CASE WHEN COUNT(users.id) > COUNT(wr.id) THEN 1 ELSE 0 END as missing, "
					+ "CASE WHEN SUM(wr.totalnumberofcases) > 0 AND COUNT(users.id) = COUNT(wr.id) THEN 1 ELSE 0 END as report, "
					+ "CASE WHEN SUM(wr.totalnumberofcases) = 0 AND COUNT(users.id) = COUNT(wr.id) THEN 1 ELSE 0 END as zero "
					+ "FROM users "
					+ "INNER JOIN users_userroles ON users_userroles.user_id = users.id "
					+ "LEFT JOIN ("
						+ "SELECT * FROM weeklyreport WHERE year = " + epiWeek.getYear() + " AND epiweek = " + epiWeek.getWeek() 
					+ ") as wr ON wr.reportinguser_id = users.id "
					+ "LEFT JOIN ("
						+ "SELECT * FROM users"
					+ ") as inf ON inf.associatedofficer_id = users.id "
					+ "WHERE users_userroles.userrole = 'SURVEILLANCE_OFFICER' "
						+ "AND users.region_id = " + region.getId() + " "
					+ "GROUP BY users.id, inf"
				+ ") as inner_query "
				+ "GROUP BY district_id, id;");

		@SuppressWarnings("rawtypes")
		List results = query.getResultList();

		List<WeeklyReportSummaryDto> summaryDtos = new ArrayList<>();

		for (int i = 0; i < results.size(); i++) {
			Object[] result = (Object[]) results.get(i);
			int reports = ((Long) result[3]).intValue();
			int missingReports = ((Long) result[2]).intValue();
			int zeroReports = ((Long) result[4]).intValue();
			int totalReports = reports + missingReports + zeroReports;
			
			WeeklyReportSummaryDto summaryDto = new WeeklyReportSummaryDto();
			summaryDto.setDistrict(DistrictFacadeEjb.toReferenceDto(districtService.getById((long) result[0])));
//			summaryDto.setFacilities(((Long) result[1]).intValue());
//			summaryDto.setMissingReports(missingReports);
//			summaryDto.setReports(reports);
//			summaryDto.setZeroReports(zeroReports);
//			summaryDto.setMissingReportsPercentage(new BigDecimal(missingReports).multiply(new BigDecimal(100)).divide(new BigDecimal(totalReports), RoundingMode.HALF_UP).intValue());
//			summaryDto.setReportsPercentage(new BigDecimal(reports).multiply(new BigDecimal(100)).divide(new BigDecimal(totalReports), RoundingMode.HALF_UP).intValue());
//			summaryDto.setZeroReportsPercentage(new BigDecimal(zeroReports).multiply(new BigDecimal(100)).divide(new BigDecimal(totalReports), RoundingMode.HALF_UP).intValue());
			
			summaryDtos.add(summaryDto);
		}

		return summaryDtos;
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<WeeklyReport, WeeklyReport> from, User user) {
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
				// Supervisors see all reports from facilities in their region
				if (user.getRegion() != null) {
					filter = cb.or(filter, cb.equal(informant.join(User.HEALTH_FACILITY, JoinType.LEFT).get(Facility.REGION), user.getRegion()));
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

}
