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

package de.symeda.sormas.backend.caze.surveillancereport;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportCriteria;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportFacade;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportReferenceDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
import de.symeda.sormas.backend.externalmessage.ExternalMessageFacadeEjb.ExternalMessageFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.entities.caze.SormasToSormasCaseFacadeEjb.SormasToSormasCaseFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareInfoHelper;
import de.symeda.sormas.backend.specialcaseaccess.SpecialCaseAccessService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SurveillanceReportFacade")
@RightsAllowed(UserRight._CASE_VIEW)
public class SurveillanceReportFacadeEjb
	extends
	AbstractBaseEjb<SurveillanceReport, SurveillanceReportDto, SurveillanceReportDto, SurveillanceReportReferenceDto, SurveillanceReportService, SurveillanceReportCriteria>
	implements SurveillanceReportFacade {

	private final Logger logger = LoggerFactory.getLogger(SurveillanceReportFacadeEjb.class);

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private CaseService caseService;
	@EJB
	private ExternalMessageFacadeEjbLocal externalMessageFacade;
	@EJB
	private SormasToSormasOriginInfoService originInfoService;
	@EJB
	private SormasToSormasFacadeEjbLocal sormasToSormasFacade;
	@Resource
	private ManagedScheduledExecutorService executorService;
	@EJB
	private SormasToSormasCaseFacadeEjbLocal sormasToSormasCaseFacade;
	@EJB
	private SpecialCaseAccessService specialCaseAccessService;

	public SurveillanceReportFacadeEjb() {
		super();
	}

	@Inject
	public SurveillanceReportFacadeEjb(SurveillanceReportService service) {
		super(SurveillanceReport.class, SurveillanceReportDto.class, service);
	}

	public static SurveillanceReportReferenceDto toReferenceDto(SurveillanceReport entity) {
		if (entity == null) {
			return null;
		}
		return new SurveillanceReportReferenceDto(entity.getUuid());
	}

	@Override
	@RightsAllowed(UserRight._CASE_EDIT)
	public SurveillanceReportDto save(@Valid @NotNull SurveillanceReportDto dto) {
		return saveSurveillanceReport(dto, true, true);
	}

	@Override
	public long count(SurveillanceReportCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);

		Root<SurveillanceReport> root = cq.from(SurveillanceReport.class);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<SurveillanceReportDto> getIndexList(
		SurveillanceReportCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SurveillanceReport> cq = cb.createQuery(SurveillanceReport.class);
		Root<SurveillanceReport> root = cq.from(SurveillanceReport.class);

		Predicate filter = service.buildCriteriaFilter(criteria, cb, root);
		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.desc(root.get(SurveillanceReport.CREATION_DATE)));

		return toPseudonymizedDtos(QueryHelper.getResultList(em, cq, first, max));
	}

	@Override
	public void validate(@Valid SurveillanceReportDto dto) throws ValidationRuntimeException {

	}

	@Override
	protected SurveillanceReport fillOrBuildEntity(SurveillanceReportDto source, SurveillanceReport target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, service.getByUuid(source.getUuid()), SurveillanceReport::new, checkChangeDate);

		target.setReportingType(source.getReportingType());
		target.setExternalId(source.getExternalId());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setReportDate(source.getReportDate());
		target.setDateOfDiagnosis(source.getDateOfDiagnosis());
		target.setFacilityRegion(regionService.getByReferenceDto(source.getFacilityRegion()));
		target.setFacilityDistrict(districtService.getByReferenceDto(source.getFacilityDistrict()));
		target.setFacilityType(source.getFacilityType());
		target.setFacility(facilityService.getByReferenceDto(source.getFacility()));
		target.setFacilityDetails(source.getFacilityDetails());
		target.setNotificationDetails(source.getNotificationDetails());
		target.setCaze(caseService.getByReferenceDto(source.getCaze()));

		if (source.getSormasToSormasOriginInfo() != null) {
			target.setSormasToSormasOriginInfo(originInfoService.getByUuid(source.getSormasToSormasOriginInfo().getUuid()));
		}

		return target;
	}

	@Override
	protected SurveillanceReportReferenceDto toRefDto(SurveillanceReport surveillanceReport) {
		return toReferenceDto(surveillanceReport);
	}

	@Override
	protected Pseudonymizer<SurveillanceReportDto> createPseudonymizer(List<SurveillanceReport> surveillanceReports) {
		List<String> withSpecialAccess = specialCaseAccessService.getSurveillanceReportUuidsWithSpecialAccess(surveillanceReports);
		return Pseudonymizer.getDefault(userService, r -> withSpecialAccess.contains(r.getUuid()));
	}

	@Override
	protected void pseudonymizeDto(
		SurveillanceReport source,
		SurveillanceReportDto dto,
		Pseudonymizer<SurveillanceReportDto> pseudonymizer,
		boolean inJurisdiction) {
		User currentUser = userService.getCurrentUser();

		pseudonymizer.pseudonymizeDto(
			SurveillanceReportDto.class,
			dto,
			inJurisdiction,
			reportDto -> pseudonymizer.pseudonymizeUser(source.getReportingUser(), currentUser, reportDto::setReportingUser, reportDto));
	}

	@Override
	protected void restorePseudonymizedDto(
		SurveillanceReportDto dto,
		SurveillanceReportDto existingDto,
		SurveillanceReport existingReport,
		Pseudonymizer<SurveillanceReportDto> pseudonymizer) {
		if (existingDto != null) {
			boolean inJurisdiction = service.inJurisdictionOrOwned(existingReport);
			User currentUser = userService.getCurrentUser();

			pseudonymizer.restoreUser(existingReport.getReportingUser(), currentUser, dto, dto::setReportingUser);
			pseudonymizer.restorePseudonymizedValues(SurveillanceReportDto.class, dto, existingDto, inJurisdiction);
		}
	}

	@Override
	@RightsAllowed({
		UserRight._CASE_EDIT,
		UserRight._SYSTEM })
	public void delete(String surveillanceReportUuid) {
		SurveillanceReport report = service.getByUuid(surveillanceReportUuid);

		ExternalMessageDto associatedMessage = externalMessageFacade.getForSurveillanceReport(toRefDto(report));
		if (associatedMessage != null) {
			associatedMessage.setSurveillanceReport(null);
			externalMessageFacade.save(associatedMessage);
		}
		service.deletePermanent(report);
	}

	public SurveillanceReportDto toDto(SurveillanceReport source) {
		if (source == null) {
			return null;
		}

		SurveillanceReportDto target = new SurveillanceReportDto();
		DtoHelper.fillDto(target, source);

		target.setReportingType(source.getReportingType());
		target.setExternalId(source.getExternalId());
		target.setReportingUser(source.getReportingUser() == null ? null : source.getReportingUser().toReference());
		target.setReportDate(source.getReportDate());
		target.setDateOfDiagnosis(source.getDateOfDiagnosis());
		target.setFacilityRegion(RegionFacadeEjb.toReferenceDto(source.getFacilityRegion()));
		target.setFacilityDistrict(DistrictFacadeEjb.toReferenceDto(source.getFacilityDistrict()));
		target.setFacilityType(source.getFacilityType());
		target.setFacility(FacilityFacadeEjb.toReferenceDto(source.getFacility()));
		target.setFacilityDetails(source.getFacilityDetails());
		target.setNotificationDetails(source.getNotificationDetails());
		target.setCaze(CaseFacadeEjb.toReferenceDto(source.getCaze()));

		target.setSormasToSormasOriginInfo(SormasToSormasOriginInfoFacadeEjb.toDto(source.getSormasToSormasOriginInfo()));
		target.setOwnershipHandedOver(source.getSormasToSormasShares().stream().anyMatch(ShareInfoHelper::isOwnerShipHandedOver));

		return target;

	}

	@RightsAllowed(UserRight._CASE_EDIT)
	public SurveillanceReportDto saveSurveillanceReport(SurveillanceReportDto dto, boolean checkChangeDate, boolean internal) {

		SurveillanceReport existingReport = service.getByUuid(dto.getUuid());
		SurveillanceReportDto existingReportDto = toDto(existingReport);

		if (internal && existingReport != null && !service.isEditAllowed(existingReport)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorSurveillanceReportNotEditable));
		}

		Pseudonymizer<SurveillanceReportDto> pseudonymizer = createPseudonymizer(existingReport);
		restorePseudonymizedDto(dto, existingReportDto, existingReport, pseudonymizer);

		validate(dto);

		SurveillanceReport report = fillOrBuildEntity(dto, existingReport, checkChangeDate);

		service.ensurePersisted(report);

		onReportChanged(report, internal);

		return toPseudonymizedDto(report);
	}

	@Override
	public List<SurveillanceReportDto> getByCaseUuids(List<String> caseUuids) {
		return toPseudonymizedDtos(service.getByCaseUuids(caseUuids));
	}

	@RightsAllowed(UserRight._CASE_EDIT)
	public void onReportChanged(SurveillanceReport report, boolean syncShares) {
		if (syncShares && sormasToSormasFacade.isFeatureConfigured()) {
			syncSharesAsync(report);
		}
	}

	private void syncSharesAsync(SurveillanceReport report) {
		executorService.schedule(() -> {
			try {
				sormasToSormasCaseFacade.syncShares(new ShareTreeCriteria(report.getCaze().getUuid()));
			} catch (Exception e) {
				logger.error("Failed to sync shares of SurveillanceReport", e);
			}
		}, 5, TimeUnit.SECONDS);
	}

	@LocalBean
	@Stateless
	public static class SurveillanceReportFacadeEjbLocal extends SurveillanceReportFacadeEjb {

		public SurveillanceReportFacadeEjbLocal() {
			super();
		}

		@Inject
		public SurveillanceReportFacadeEjbLocal(SurveillanceReportService service) {
			super(service);
		}
	}
}
