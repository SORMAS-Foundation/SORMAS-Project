package de.symeda.sormas.backend.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.report.AggregateReportFacade;
import de.symeda.sormas.api.report.AggregatedCaseCountDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.PointOfEntryService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "AggregateReportFacade")
public class AggregateReportFacadeEjb implements AggregateReportFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private AggregateReportService service;
	@EJB
	private UserService userService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private PointOfEntryService pointOfEntryService;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;
	@EJB
	private UserRoleConfigFacadeEjbLocal userRoleConfigFacade;

	@Override
	public List<AggregateReportDto> getAllAggregateReportsAfter(Date date) {
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllAfter(date, user).stream().map(r -> toDto(r)).collect(Collectors.toList());
	}

	@Override
	public List<AggregateReportDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(r -> toDto(r)).collect(Collectors.toList());
	}

	@Override
	public AggregateReportDto saveAggregateReport(AggregateReportDto dto) {
		AggregateReport report = fromDto(dto);
		service.ensurePersisted(report);
		return toDto(report);
	}

	@Override
	public List<String> getAllUuids() {
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllUuids(user);
	}
	
	@Override
	public List<AggregatedCaseCountDto> getIndexList(AggregateReportCriteria criteria) {
		User user = userService.getCurrentUser();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<AggregateReport> root = cq.from(AggregateReport.class);
		
		Predicate filter = service.createUserFilter(cb, cq, root);
		if (criteria != null) {
			Predicate criteriaFilter = service.createCriteriaFilter(criteria, cb, cq, root);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}
		
		if (filter != null) {
			cq.where(filter);
		}
		
		cq.multiselect(root.get(AggregateReport.DISEASE), cb.sum(root.get(AggregateReport.NEW_CASES)),
				cb.sum(root.get(AggregateReport.LAB_CONFIRMATIONS)), cb.sum(root.get(AggregateReport.DEATHS)));
		cq.groupBy(root.get(AggregateReport.DISEASE));
		
		List<Object[]> resultList = em.createQuery(cq).getResultList();
		Map<Disease, AggregatedCaseCountDto> reportSet = new HashMap<>();
		
		for (Object[] result : resultList) {
			reportSet.put((Disease) result[0], new AggregatedCaseCountDto((Disease) result[0], ((Long) result[1]).intValue(), ((Long) result[2]).intValue(), ((Long) result[3]).intValue()));
		}
		
		for (Disease disease : diseaseConfigurationFacade.getAllDiseases(true, false, false)) {
			if (!reportSet.containsKey(disease)) {
				reportSet.put(disease, new AggregatedCaseCountDto(disease, 0, 0, 0));
			}
		}
		
		List<AggregatedCaseCountDto> reportList = new ArrayList<>(reportSet.values());
		reportList.sort(Comparator.comparing(r -> r.getDisease().toString()));
		return reportList;
	}
	
	@Override
	public List<AggregateReportDto> getList(AggregateReportCriteria criteria) {
		User user = userService.getCurrentUser();
		
		return service.findBy(criteria, user).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	public AggregateReport fromDto(@NotNull AggregateReportDto source) {
		AggregateReport target = service.getByUuid(source.getUuid());
		if (target == null) {
			target = new AggregateReport();
			target.setUuid(source.getUuid());
		}
		DtoHelper.validateDto(source, target);
		
		target.setDisease(source.getDisease());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setPointOfEntry(pointOfEntryService.getByReferenceDto(source.getPointOfEntry()));
		target.setNewCases(source.getNewCases());
		target.setLabConfirmations(source.getLabConfirmations());
		target.setDeaths(source.getDeaths());
		
		return target;
	}
	
	public static AggregateReportDto toDto(AggregateReport source) {
		if (source == null) {
			return null;
		}
		AggregateReportDto target = new AggregateReportDto();
		DtoHelper.fillDto(target, source);
		
		target.setDisease(source.getDisease());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		target.setPointOfEntry(PointOfEntryFacadeEjb.toReferenceDto(source.getPointOfEntry()));
		target.setNewCases(source.getNewCases());
		target.setLabConfirmations(source.getLabConfirmations());
		target.setDeaths(source.getDeaths());

		return target;
	}
	
	@Override
	public void deleteReport(String reportUuid) {
		User user = userService.getCurrentUser();
		if (!userRoleConfigFacade
				.getEffectiveUserRights(user.getUserRoles().toArray(new UserRole[user.getUserRoles().size()]))
				.contains(UserRight.AGGREGATE_REPORT_EDIT)) {
			throw new UnsupportedOperationException("User " + user.getUuid() + " is not allowed to edit aggregate reports.");
		}

		AggregateReport aggregateReport = service.getByUuid(reportUuid);
		service.delete(aggregateReport);
	}

	@Override
	public long countWithCriteria(AggregateReportCriteria criteria) {
		User user = userService.getCurrentUser();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AggregateReport> root = cq.from(AggregateReport.class);

		Predicate filter = service.createUserFilter(cb, cq, root);
		if (criteria != null) {
			Predicate criteriaFilter = service.createCriteriaFilter(criteria, cb, cq, root);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));

		return em.createQuery(cq).getSingleResult();
	}

	@LocalBean
	@Stateless
	public static class AggregateReportFacadeEjbLocal extends AggregateReportFacadeEjb {
	}
}
