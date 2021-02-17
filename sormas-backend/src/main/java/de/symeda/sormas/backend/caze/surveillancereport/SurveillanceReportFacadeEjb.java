/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.caze.surveillancereport;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportCriteria;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportFacade;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseJurisdictionChecker;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless(name = "SurveillanceReportFacade")
public class SurveillanceReportFacadeEjb implements SurveillanceReportFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	@EJB
	private SurveillanceReportService service;
	@EJB
	private UserService userService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private CaseService caseService;
	@EJB
	private CaseJurisdictionChecker caseJurisdictionChecker;

	@Override
	public SurveillanceReportDto saveSurveillanceReport(SurveillanceReportDto dto) {
		return saveSurveillanceReport(dto, true);
	}

	public SurveillanceReportDto saveSurveillanceReport(SurveillanceReportDto dto, boolean checkChangeDate) {
		SurveillanceReport existingReport = service.getByUuid(dto.getUuid());
		SurveillanceReportDto existingReportDto = toDto(existingReport);

		restorePseudonymizedDto(dto, existingReport, existingReportDto);

		SurveillanceReport report = fromDto(dto, checkChangeDate);

		service.ensurePersisted(report);

		return toDto(report);
	}

	@Override
	public void deleteSurveillanceReport(String surveillanceReportUuid) {
		service.delete(service.getByUuid(surveillanceReportUuid));
	}

	@Override
	public List<SurveillanceReportDto> getIndexList(SurveillanceReportCriteria criteria, Integer first, Integer max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SurveillanceReport> cq = cb.createQuery(SurveillanceReport.class);
		Root<SurveillanceReport> root = cq.from(SurveillanceReport.class);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.desc(root.get(SurveillanceReport.CREATION_DATE)));

		List<SurveillanceReport> resultList;
		if (first != null && max != null) {
			resultList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			resultList = em.createQuery(cq).getResultList();
		}

		List<SurveillanceReportDto> reports = resultList.stream().map(SurveillanceReportFacadeEjb::toDto).collect(Collectors.toList());

		User currentUser = userService.getCurrentUser();
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(SurveillanceReportDto.class, reports, reportDto -> {
			Optional<SurveillanceReport> report = resultList.stream().filter(r -> r.getUuid().equals(r.getUuid())).findFirst();
			return report.isPresent() ? caseJurisdictionChecker.isInJurisdictionOrOwned(report.get().getCaze()) : false;
		}, (reportDto, inJurisdiction) -> {
			Optional<SurveillanceReport> report = resultList.stream().filter(r -> r.getUuid().equals(r.getUuid())).findFirst();
			report.ifPresent(
				surveillanceReport -> pseudonymizer.pseudonymizeUser(surveillanceReport.getCreatingUser(), currentUser, reportDto::setCreatingUser));
		});

		return reports;
	}

	private void restorePseudonymizedDto(SurveillanceReportDto dto, SurveillanceReport existingReport, SurveillanceReportDto existingDto) {
		if (existingDto != null) {
			boolean inJurisdiction = caseJurisdictionChecker.isInJurisdictionOrOwned(existingReport.getCaze());
			User currentUser = userService.getCurrentUser();

			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);

			pseudonymizer.restoreUser(existingReport.getCreatingUser(), currentUser, dto, dto::setCreatingUser);
			pseudonymizer.restorePseudonymizedValues(SurveillanceReportDto.class, dto, existingDto, inJurisdiction);
		}
	}

	public static SurveillanceReportDto toDto(SurveillanceReport source) {
		if (source == null) {
			return null;
		}

		SurveillanceReportDto target = new SurveillanceReportDto();
		DtoHelper.fillDto(target, source);

		target.setReportingType(source.getReportingType());
		target.setCreatingUser(source.getCreatingUser().toReference());
		target.setReportDate(source.getReportDate());
		target.setDateOfDiagnosis(source.getDateOfDiagnosis());
		target.setFacilityRegion(RegionFacadeEjb.toReferenceDto(source.getFacilityRegion()));
		target.setFacilityDistrict(DistrictFacadeEjb.toReferenceDto(source.getFacilityDistrict()));
		target.setFacilityType(source.getFacilityType());
		target.setFacility(FacilityFacadeEjb.toReferenceDto(source.getFacility()));
		target.setFacilityDetails(source.getFacilityDetails());
		target.setNotificationDetails(source.getNotificationDetails());
		target.setCaze(CaseFacadeEjb.toReferenceDto(source.getCaze()));

		return target;

	}

	public SurveillanceReport fromDto(@NotNull SurveillanceReportDto source, boolean checkChangeDate) {
		SurveillanceReport target =
			DtoHelper.fillOrBuildEntity(source, service.getByUuid(source.getUuid()), SurveillanceReport::new, checkChangeDate);

		target.setReportingType(source.getReportingType());
		target.setCreatingUser(userService.getByReferenceDto(source.getCreatingUser()));
		target.setReportDate(source.getReportDate());
		target.setDateOfDiagnosis(source.getDateOfDiagnosis());
		target.setFacilityRegion(regionService.getByReferenceDto(source.getFacilityRegion()));
		target.setFacilityDistrict(districtService.getByReferenceDto(source.getFacilityDistrict()));
		target.setFacilityType(source.getFacilityType());
		target.setFacility(facilityService.getByReferenceDto(source.getFacility()));
		target.setFacilityDetails(source.getFacilityDetails());
		target.setNotificationDetails(source.getNotificationDetails());
		target.setCaze(caseService.getByReferenceDto(source.getCaze()));

		return target;

	}

	@LocalBean
	@Stateless
	public static class SurveillanceReportFacadeEjbLocal extends SurveillanceReportFacadeEjb {

	}
}
