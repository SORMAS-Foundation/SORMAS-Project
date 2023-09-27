/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.backend.caze;

import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.RequestContextHolder;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseListEntryDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.caze.CaseMergeIndexDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.CaseReferenceDefinition;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.caze.PreviousCaseDto;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseReferenceDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.therapy.TherapyReferenceDto;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;
import de.symeda.sormas.api.utils.criteria.ExternalShareDateType;
import de.symeda.sormas.backend.ExtendedPostgreSQL94Dialect;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportFacadeEjb;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportService;
import de.symeda.sormas.backend.caze.transformers.CaseListEntryDtoResultTransformer;
import de.symeda.sormas.backend.caze.transformers.CaseSelectionDtoResultTransformer;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisitService;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ChangeDateBuilder;
import de.symeda.sormas.backend.common.ChangeDateFilterBuilder;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.DeletableAdo;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactJoins;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb;
import de.symeda.sormas.backend.document.DocumentService;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.externaljournal.ExternalJournalService;
import de.symeda.sormas.backend.externalmessage.ExternalMessageService;
import de.symeda.sormas.backend.externalsurveillancetool.ExternalSurveillanceToolGatewayFacadeEjb;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonQueryContext;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.share.ExternalShareInfo;
import de.symeda.sormas.backend.share.ExternalShareInfoCountAndLatestDate;
import de.symeda.sormas.backend.share.ExternalShareInfoService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoFacadeEjb.SormasToSormasShareInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.therapy.Prescription;
import de.symeda.sormas.backend.therapy.PrescriptionService;
import de.symeda.sormas.backend.therapy.Treatment;
import de.symeda.sormas.backend.therapy.TreatmentService;
import de.symeda.sormas.backend.travelentry.services.TravelEntryService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserRole;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.ExternalDataUtil;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.vaccination.Vaccination;
import de.symeda.sormas.backend.vaccination.VaccinationService;
import de.symeda.sormas.backend.visit.Visit;
import de.symeda.sormas.backend.visit.VisitFacadeEjb;

@Stateless
@LocalBean
public class CaseService extends AbstractCoreAdoService<Case, CaseJoins> {

	private static final Double SECONDS_30_DAYS = Long.valueOf(TimeUnit.DAYS.toSeconds(30L)).doubleValue();

	@EJB
	private CaseListCriteriaBuilder listQueryBuilder;
	@EJB
	private ContactService contactService;
	@EJB
	private SampleService sampleService;
	@EJB
	private EpiDataService epiDataService;
	@EJB
	private UserService userService;
	@EJB
	private TaskService taskService;
	@EJB
	private ClinicalVisitService clinicalVisitService;
	@EJB
	private TreatmentService treatmentService;
	@EJB
	private PrescriptionService prescriptionService;
	@EJB
	private TravelEntryService travelEntryService;
	@EJB
	private ImmunizationService immunizationService;
	@EJB
	private DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private SormasToSormasFacadeEjbLocal sormasToSormasFacade;
	@EJB
	private SormasToSormasShareInfoFacadeEjbLocal sormasToSormasShareInfoFacade;
	@EJB
	private SormasToSormasShareInfoService sormasToSormasShareInfoService;
	@EJB
	private ExternalShareInfoService externalShareInfoService;
	@EJB
	private ExternalJournalService externalJournalService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private SurveillanceReportService surveillanceReportService;
	@EJB
	private DocumentService documentService;
	@EJB
	private ExternalSurveillanceToolGatewayFacadeEjb.ExternalSurveillanceToolGatewayFacadeEjbLocal externalSurveillanceToolGatewayFacade;
	@EJB
	private SurveillanceReportFacadeEjb.SurveillanceReportFacadeEjbLocal surveillanceReportFacade;
	@EJB
	private VaccinationService vaccinationService;
	@EJB
	private ExternalMessageService externalMessageService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;

	public CaseService() {
		super(Case.class, DeletableEntityType.CASE);
	}

	/**
	 * Returns all cases that match the specified {@code caseCriteria} and that the current user has access to.
	 * This should be the preferred method of retrieving cases from the database if there is no special logic required
	 * that can not be part of the {@link CaseCriteria}.
	 */
	public List<Case> findBy(CaseCriteria caseCriteria, boolean ignoreUserFilter) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(getElementClass());
		Root<Case> from = cq.from(getElementClass());
		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, from);

		Predicate filter = createCriteriaFilter(caseCriteria, caseQueryContext);
		if (!ignoreUserFilter) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(caseQueryContext));
		}

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(Case.CREATION_DATE)));

		return em.createQuery(cq).getResultList();
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createRelevantDataFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Case> from) {

		Predicate filter = createActiveCasesFilter(cb, from);

		if (getCurrentUser() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilterInternal(cb, cq, from));
		}

		return filter;
	}

	@Override
	protected List<String> referencesToBeFetched() {
		return Arrays.asList(
			Case.SYMPTOMS,
			Case.THERAPY,
			Case.CLINICAL_COURSE,
			Case.HEALTH_CONDITIONS,
			Case.HOSPITALIZATION,
			Case.EPI_DATA,
			Case.PORT_HEALTH_INFO,
			Case.MATERNAL_HISTORY);
	}

	public List<String> getAllActiveUuids() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> from = cq.from(getElementClass());

		Predicate filter = createActiveCasesFilter(cb, from);

		if (getCurrentUser() != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		if (RequestContextHolder.isMobileSync()) {
			Predicate predicate = createLimitedChangeDateFilter(cb, from);
			if (predicate != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, predicate);
			}
		}

		cq.where(filter);
		cq.select(from.get(Case.UUID));

		return em.createQuery(cq).getResultList();
	}

	public Long countCasesForMap(Region region, District district, Disease disease, Date from, Date to, NewCaseDateType dateType) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Case> caze = cq.from(getElementClass());

		CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);

		Predicate filter = createMapCasesFilter(caseQueryContext, region, district, disease, from, to, dateType);

		if (filter != null) {
			cq.where(filter);
			cq.select(cb.count(caze.get(Case.ID)));

			return em.createQuery(cq).getSingleResult();
		}

		return 0L;
	}

	public List<MapCaseDto> getCasesForMap(Region region, District district, Disease disease, Date from, Date to, NewCaseDateType dateType) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MapCaseDto> cq = cb.createQuery(MapCaseDto.class);
		Root<Case> caze = cq.from(getElementClass());

		CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);
		CaseJoins joins = caseQueryContext.getJoins();

		Predicate filter = createMapCasesFilter(caseQueryContext, region, district, disease, from, to, dateType);

		List<MapCaseDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
				caze.get(Case.UUID),
				caze.get(Case.REPORT_DATE),
				caze.get(Case.CASE_CLASSIFICATION),
				caze.get(Case.DISEASE),
				joins.getPerson().get(Person.UUID),
				joins.getPerson().get(Person.FIRST_NAME),
				joins.getPerson().get(Person.LAST_NAME),
				joins.getFacility().get(Facility.UUID),
				joins.getFacility().get(Facility.LATITUDE),
				joins.getFacility().get(Facility.LONGITUDE),
				caze.get(Case.REPORT_LAT),
				caze.get(Case.REPORT_LON),
				joins.getPersonAddress().get(Location.LATITUDE),
				joins.getPersonAddress().get(Location.LONGITUDE),
				JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(caseQueryContext)));

			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	private Predicate createMapCasesFilter(
		CaseQueryContext caseQueryContext,
		Region region,
		District district,
		Disease disease,
		Date from,
		Date to,
		NewCaseDateType dateType) {

		final CriteriaBuilder cb = caseQueryContext.getCriteriaBuilder();
		final From<?, Case> root = caseQueryContext.getRoot();
		final CaseJoins joins = caseQueryContext.getJoins();

		Predicate filter = createActiveCasesFilter(cb, root);

		// Userfilter
		filter =
			CriteriaBuilderHelper.and(cb, filter, createUserFilter(caseQueryContext, new CaseUserFilterCriteria().excludeCasesFromContacts(true)));

		// Filter by date. The relevancefilter uses a special algorithm that should reflect the current situation.
		if (dateType == null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createCaseRelevanceFilter(caseQueryContext, from, to));
		} else {
			filter = CriteriaBuilderHelper
				.and(cb, filter, createNewCaseFilter(caseQueryContext, DateHelper.getStartOfDay(from), DateHelper.getEndOfDay(to), dateType));
		}

		// only show cases which actually have GPS coordinates provided
		Predicate personLatLonNotNull = CriteriaBuilderHelper
			.and(cb, cb.isNotNull(joins.getPersonAddress().get(Location.LONGITUDE)), cb.isNotNull(joins.getPersonAddress().get(Location.LATITUDE)));
		Predicate reportLatLonNotNull =
			CriteriaBuilderHelper.and(cb, cb.isNotNull(root.get(Case.REPORT_LON)), cb.isNotNull(root.get(Case.REPORT_LAT)));
		Predicate facilityLatLonNotNull = CriteriaBuilderHelper
			.and(cb, cb.isNotNull(joins.getFacility().get(Facility.LONGITUDE)), cb.isNotNull(joins.getFacility().get(Facility.LATITUDE)));
		Predicate latLonProvided = CriteriaBuilderHelper.or(cb, personLatLonNotNull, reportLatLonNotNull, facilityLatLonNotNull);
		filter = CriteriaBuilderHelper.and(cb, filter, latLonProvided);

		if (region != null) {
			Predicate regionFilter = cb.equal(root.get(Case.REGION), region);
			if (filter != null) {
				filter = cb.and(filter, regionFilter);
			} else {
				filter = regionFilter;
			}
		}

		if (district != null) {
			Predicate districtFilter = cb.equal(root.get(Case.DISTRICT), district);
			if (filter != null) {
				filter = cb.and(filter, districtFilter);
			} else {
				filter = districtFilter;
			}
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(root.get(Case.DISEASE), disease);
			if (filter != null) {
				filter = cb.and(filter, diseaseFilter);
			} else {
				filter = diseaseFilter;
			}
		}

		return filter;
	}

	public String getHighestEpidNumber(String epidNumberPrefix, String caseUuid, Disease caseDisease) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);

		Predicate filter = cb.and(cb.equal(caze.get(Case.DELETED), false), cb.equal(caze.get(Case.DISEASE), caseDisease));
		if (!DataHelper.isNullOrEmpty(caseUuid)) {
			filter = cb.and(filter, cb.notEqual(caze.get(Case.UUID), caseUuid));
		}
		filter = cb.and(filter, cb.like(caze.get(Case.EPID_NUMBER), epidNumberPrefix + "%"));
		cq.where(filter);

		ParameterExpression<String> regexPattern = cb.parameter(String.class);
		ParameterExpression<String> regexReplacement = cb.parameter(String.class);
		ParameterExpression<String> regexFlags = cb.parameter(String.class);
		Expression<String> epidNumberSuffixClean = cb.function(
			"regexp_replace",
			String.class,
			cb.substring(caze.get(Case.EPID_NUMBER), epidNumberPrefix.length() + 1),
			regexPattern,
			regexReplacement,
			regexFlags);
		cq.orderBy(cb.desc(cb.concat("0", epidNumberSuffixClean).as(Integer.class)));
		cq.select(caze.get(Case.EPID_NUMBER));
		TypedQuery<String> query = em.createQuery(cq);
		query.setParameter(regexPattern, "\\D"); // Non-digits
		query.setParameter(regexReplacement, ""); // Replace all non-digits with empty string
		query.setParameter(regexFlags, "g"); // Global search

		return QueryHelper.getFirstResult(query);
	}

	public String getUuidByUuidEpidNumberOrExternalId(String searchTerm, CaseCriteria caseCriteria) {

		if (StringUtils.isEmpty(searchTerm)) {
			return null;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> root = cq.from(Case.class);

		Predicate filter = null;
		if (caseCriteria != null) {
			final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, root);
			filter = createCriteriaFilter(caseCriteria, caseQueryContext);
			// Userfilter
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(caseQueryContext, new CaseUserFilterCriteria()));
		}

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			cb.or(
				cb.equal(cb.lower(root.get(Case.UUID)), searchTerm.toLowerCase()),
				cb.equal(cb.lower(root.get(Case.EPID_NUMBER)), searchTerm.toLowerCase()),
				cb.equal(cb.lower(root.get(Case.EXTERNAL_TOKEN)), searchTerm.toLowerCase()),
				cb.equal(cb.lower(root.get(Case.INTERNAL_TOKEN)), searchTerm.toLowerCase()),
				cb.equal(cb.lower(root.get(Case.EXTERNAL_ID)), searchTerm.toLowerCase())));

		cq.where(filter);
		cq.orderBy(cb.desc(root.get(Case.REPORT_DATE)));
		cq.select(root.get(Case.UUID));

		return QueryHelper.getFirstResult(em, cq);
	}

	public List<String> getArchivedUuidsSince(Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);

		Predicate filter = createUserFilter(cb, cq, caze);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(caze.get(Case.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate archivedFilter = cb.equal(caze.get(Case.ARCHIVED), true);
		if (filter != null) {
			filter = cb.and(filter, archivedFilter);
		} else {
			filter = archivedFilter;
		}

		cq.where(filter);
		cq.select(caze.get(Case.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<String> getDeletedUuidsSince(Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Case> caze = cq.from(Case.class);

		Predicate filter = createUserFilter(cb, cq, caze);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(caze.get(Case.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate deletedFilter = cb.equal(caze.get(Case.DELETED), true);
		if (filter != null) {
			filter = cb.and(filter, deletedFilter);
		} else {
			filter = deletedFilter;
		}

		cq.where(filter);
		cq.select(caze.get(Case.UUID));

		return em.createQuery(cq).getResultList();
	}

	@Override
	protected List<Predicate> getAdditionalObsoleteUuidsPredicates(Date since, CriteriaBuilder cb, CriteriaQuery<String> cq, Root<Case> from) {

		if (featureConfigurationFacade.isFeatureEnabled(FeatureType.LIMITED_SYNCHRONIZATION)
			&& featureConfigurationFacade
				.isPropertyValueTrue(FeatureType.LIMITED_SYNCHRONIZATION, FeatureTypeProperty.EXCLUDE_NO_CASE_CLASSIFIED_CASES)) {
			return Collections.singletonList(createObsoleteLimitedSyncCasePredicate(cb, from, since, getCurrentUser()));
		} else {
			return Collections.emptyList();
		}
	}

	public Predicate createLimitedSyncCasePredicate(CriteriaBuilder cb, Join<?, Case> join, User currentUser) {

		return cb.or(
			cb.isNull(join.get(Case.UUID)),
			cb.not(
				cb.and(
					cb.equal(join.get(Case.CASE_CLASSIFICATION), CaseClassification.NO_CASE),
					cb.or(
						cb.notEqual(join.get(Case.REPORTING_USER), currentUser),
						cb.and(cb.equal(join.get(Case.REPORTING_USER), currentUser), cb.isNull(join.get(Case.CREATION_VERSION)))))));
	}

	public Predicate createObsoleteLimitedSyncCasePredicate(CriteriaBuilder cb, From<?, Case> root, Date since, User currentUser) {

		return cb.and(
			cb.equal(root.get(Case.CASE_CLASSIFICATION), CaseClassification.NO_CASE),
			cb.greaterThanOrEqualTo(root.get(Case.CLASSIFICATION_DATE), since),
			cb.or(
				cb.notEqual(root.get(Case.REPORTING_USER), currentUser),
				cb.and(cb.equal(root.get(Case.REPORTING_USER), currentUser), cb.isNull(root.get(Case.CREATION_VERSION)))));
	}

	@Override
	protected Predicate getUserFilterForObsoleteUuids(CriteriaBuilder cb, CriteriaQuery<String> cq, Root<Case> from) {

		return createUserFilter(new CaseQueryContext(cb, cq, from), new CaseUserFilterCriteria().excludeLimitedSyncRestrictions(true));
	}

	/**
	 * Creates a filter that checks whether the case is considered "relevant" in the time frame specified by {@code fromDate} and
	 * {@code toDate}, i.e. either the {@link Symptoms#getOnsetDate()} or {@link Case#getReportDate()} OR the {@link Case#getOutcomeDate()}
	 * are
	 * within the time frame. Also excludes cases with classification=not a case
	 */
	public Predicate createCaseRelevanceFilter(CaseQueryContext caseQueryContext, Date fromDate, Date toDate) {
		CriteriaBuilder cb = caseQueryContext.getCriteriaBuilder();
		From<?, Case> from = caseQueryContext.getRoot();
		Predicate classificationFilter = cb.notEqual(from.get(Case.CASE_CLASSIFICATION), CaseClassification.NO_CASE);
		Predicate dateFromFilter = null;
		Predicate dateToFilter = null;
		if (fromDate != null) {
			dateFromFilter = cb.or(cb.isNull(from.get(Case.OUTCOME_DATE)), cb.greaterThanOrEqualTo(from.get(Case.OUTCOME_DATE), fromDate));
		}
		if (toDate != null) {
			Join<Case, Symptoms> symptoms = caseQueryContext.getJoins().getSymptoms();
			dateToFilter = cb.or(
				cb.lessThanOrEqualTo(symptoms.get(Symptoms.ONSET_DATE), toDate),
				cb.and(cb.isNull(symptoms.get(Symptoms.ONSET_DATE)), cb.lessThanOrEqualTo(from.get(Case.REPORT_DATE), toDate)));
		}

		if (dateFromFilter != null && dateToFilter != null) {
			return cb.and(classificationFilter, dateFromFilter, dateToFilter);
		} else if (dateFromFilter != null) {
			return cb.and(classificationFilter, dateFromFilter);
		} else if (dateToFilter != null) {
			return cb.and(classificationFilter, dateToFilter);
		} else {
			return classificationFilter;
		}
	}

	/**
	 * @return Does the criteria lead to any join on a OneToMany or ManyToMany relation,
	 *         which would lead to the same case being joined multiple times?
	 */
	public boolean hasAnyToManyJoin(CaseCriteria caseCriteria) {
		return Boolean.TRUE.equals(caseCriteria.getOnlyCasesWithEvents())
			|| !DataHelper.isNullOrEmpty(caseCriteria.getEventLike())
			|| Boolean.TRUE.equals(caseCriteria.getOnlyContactsFromOtherInstances());
	}

	public <T extends AbstractDomainObject> Predicate createCriteriaFilter(CaseCriteria caseCriteria, CaseQueryContext caseQueryContext) {

		final From<?, Case> from = caseQueryContext.getRoot();
		final CriteriaBuilder cb = caseQueryContext.getCriteriaBuilder();
		final CriteriaQuery<?> cq = caseQueryContext.getQuery();
		final CaseJoins joins = caseQueryContext.getJoins();

		PersonQueryContext personQueryContext = new PersonQueryContext(cb, cq, joins.getPersonJoins());

		Predicate filter = null;
		if (caseCriteria.getReportingUserRole() != null) {
			Join<User, UserRole> rolesJoin = joins.getReportingUser().join(User.USER_ROLES, JoinType.LEFT);
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(rolesJoin.get(UserRole.UUID), caseCriteria.getReportingUserRole().getUuid()));
		}
		if (caseCriteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.DISEASE), caseCriteria.getDisease()));
		}
		if (caseCriteria.getDiseaseVariant() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.DISEASE_VARIANT), caseCriteria.getDiseaseVariant()));
		}
		if (caseCriteria.getOutcome() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.OUTCOME), caseCriteria.getOutcome()));
		}
		if (caseCriteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				CaseCriteriaHelper.createRegionCriteriaFilter(cb, joins, caseCriteria.getRegion(), caseCriteria.getJurisdictionType()));
		}
		if (caseCriteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				CaseCriteriaHelper.createDistrictCriteriaFilter(cb, joins, caseCriteria.getDistrict(), caseCriteria.getJurisdictionType()));
		}
		if (caseCriteria.getCommunity() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				CaseCriteriaHelper.createCommunityCriteriaFilter(cb, joins, caseCriteria.getCommunity(), caseCriteria.getJurisdictionType()));
		}
		if (caseCriteria.getFollowUpStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.FOLLOW_UP_STATUS), caseCriteria.getFollowUpStatus()));
		}
		if (caseCriteria.getFollowUpUntilFrom() != null && caseCriteria.getFollowUpUntilTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.between(from.get(Case.FOLLOW_UP_UNTIL), caseCriteria.getFollowUpUntilFrom(), caseCriteria.getFollowUpUntilTo()));
		} else if (caseCriteria.getFollowUpUntilFrom() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(from.get(Case.FOLLOW_UP_UNTIL), caseCriteria.getFollowUpUntilFrom()));
		} else if (caseCriteria.getFollowUpUntilTo() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(from.get(Case.FOLLOW_UP_UNTIL), caseCriteria.getFollowUpUntilTo()));
		}
		if (caseCriteria.getSymptomJournalStatus() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getPerson().get(Person.SYMPTOM_JOURNAL_STATUS), caseCriteria.getSymptomJournalStatus()));
		}
		if (caseCriteria.getVaccinationStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.VACCINATION_STATUS), caseCriteria.getVaccinationStatus()));
		}
		if (caseCriteria.getReinfectionStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.REINFECTION_STATUS), caseCriteria.getReinfectionStatus()));
		}
		if (caseCriteria.getReportDateTo() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(from.get(Case.REPORT_DATE), caseCriteria.getReportDateTo()));
		}
		if (caseCriteria.getCaseOrigin() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.CASE_ORIGIN), caseCriteria.getCaseOrigin()));
		}
		if (caseCriteria.getHealthFacility() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getFacility().get(Facility.UUID), caseCriteria.getHealthFacility().getUuid()));
		}
		if (caseCriteria.getFacilityTypeGroup() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, from.get(Case.FACILITY_TYPE).in(FacilityType.getTypes(caseCriteria.getFacilityTypeGroup())));
		}
		if (caseCriteria.getFacilityType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.FACILITY_TYPE), caseCriteria.getFacilityType()));
		}
		if (caseCriteria.getPointOfEntry() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getPointOfEntry().get(PointOfEntry.UUID), caseCriteria.getPointOfEntry().getUuid()));
		}
		if (caseCriteria.getSurveillanceOfficer() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getSurveillanceOfficer().get(User.UUID), caseCriteria.getSurveillanceOfficer().getUuid()));
		}
		if (caseCriteria.getCaseClassification() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.CASE_CLASSIFICATION), caseCriteria.getCaseClassification()));
		}
		if (caseCriteria.getInvestigationStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.INVESTIGATION_STATUS), caseCriteria.getInvestigationStatus()));
		}
		if (caseCriteria.getPresentCondition() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getPerson().get(Person.PRESENT_CONDITION), caseCriteria.getPresentCondition()));
		}
		if (caseCriteria.getNewCaseDateFrom() != null && caseCriteria.getNewCaseDateTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				createNewCaseFilter(
					caseQueryContext,
					DateHelper.getStartOfDay(caseCriteria.getNewCaseDateFrom()),
					DateHelper.getEndOfDay(caseCriteria.getNewCaseDateTo()),
					caseCriteria.getNewCaseDateType()));
		}
		if (caseCriteria.getCreationDateFrom() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.greaterThan(from.get(Case.CREATION_DATE), DateHelper.getStartOfDay(caseCriteria.getCreationDateFrom())));
		}
		if (caseCriteria.getCreationDateTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.lessThan(from.get(Case.CREATION_DATE), DateHelper.getEndOfDay(caseCriteria.getCreationDateTo())));
		}
		if (caseCriteria.getQuarantineType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.QUARANTINE), caseCriteria.getQuarantineType()));
		}
		if (caseCriteria.getQuarantineTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.between(
					from.get(Case.QUARANTINE_TO),
					DateHelper.getStartOfDay(caseCriteria.getQuarantineTo()),
					DateHelper.getEndOfDay(caseCriteria.getQuarantineTo())));
		}
		if (caseCriteria.getPerson() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getPerson().get(Person.UUID), caseCriteria.getPerson().getUuid()));
		}
		if (caseCriteria.getMustHaveNoGeoCoordinates() != null && caseCriteria.getMustHaveNoGeoCoordinates() == true) {
			Join<Person, Location> personAddress = joins.getPersonAddress();
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.and(
					cb.or(cb.isNull(from.get(Case.REPORT_LAT)), cb.isNull(from.get(Case.REPORT_LON))),
					cb.or(cb.isNull(personAddress.get(Location.LATITUDE)), cb.isNull(personAddress.get(Location.LONGITUDE)))));
		}
		if (caseCriteria.getMustBePortHealthCaseWithoutFacility() != null && caseCriteria.getMustBePortHealthCaseWithoutFacility() == true) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.and(cb.equal(from.get(Case.CASE_ORIGIN), CaseOrigin.POINT_OF_ENTRY), cb.isNull(joins.getFacility())));
		}
		if (caseCriteria.getMustHaveCaseManagementData() != null && caseCriteria.getMustHaveCaseManagementData() == true) {
			Subquery<Prescription> prescriptionSubquery = cq.subquery(Prescription.class);
			Root<Prescription> prescriptionRoot = prescriptionSubquery.from(Prescription.class);
			prescriptionSubquery.select(prescriptionRoot).where(cb.equal(prescriptionRoot.get(Prescription.THERAPY), from.get(Case.THERAPY)));
			Subquery<Treatment> treatmentSubquery = cq.subquery(Treatment.class);
			Root<Treatment> treatmentRoot = treatmentSubquery.from(Treatment.class);
			treatmentSubquery.select(treatmentRoot).where(cb.equal(treatmentRoot.get(Treatment.THERAPY), from.get(Case.THERAPY)));
			Subquery<ClinicalVisit> clinicalVisitSubquery = cq.subquery(ClinicalVisit.class);
			Root<ClinicalVisit> clinicalVisitRoot = clinicalVisitSubquery.from(ClinicalVisit.class);
			clinicalVisitSubquery.select(clinicalVisitRoot)
				.where(cb.equal(clinicalVisitRoot.get(ClinicalVisit.CLINICAL_COURSE), from.get(Case.CLINICAL_COURSE)));
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.or(cb.exists(prescriptionSubquery), cb.exists(treatmentSubquery), cb.exists(clinicalVisitSubquery)));
		}
		if (Boolean.TRUE.equals(caseCriteria.getWithoutResponsibleOfficer())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNull(from.get(Case.SURVEILLANCE_OFFICER)));
		}
		if (Boolean.TRUE.equals(caseCriteria.getWithExtendedQuarantine())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(Case.QUARANTINE_EXTENDED)));
		}
		if (Boolean.TRUE.equals(caseCriteria.getWithReducedQuarantine())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(Case.QUARANTINE_REDUCED)));
		}
		if (Boolean.TRUE.equals(caseCriteria.getOnlyQuarantineHelpNeeded())) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.and(cb.notEqual(from.get(Case.QUARANTINE_HELP_NEEDED), ""), cb.isNotNull(from.get(Case.QUARANTINE_HELP_NEEDED))));
		}
		filter = CriteriaBuilderHelper.and(cb, filter, createRelevanceStatusFilter(caseCriteria, caseQueryContext));

		if (!DataHelper.isNullOrEmpty(caseCriteria.getPersonLike())) {
			Predicate likeFilters = CriteriaBuilderHelper.buildFreeTextSearchPredicate(
				cb,
				caseCriteria.getPersonLike(),
				textFilter -> cb.or(
					// We should allow short and long versions of the UUID so let's do a LIKE behaving like a "starts with"
					CriteriaBuilderHelper.ilikePrecise(cb, joins.getPerson().get(Person.UUID), textFilter + "%"),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getPerson().get(Person.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getPerson().get(Person.LAST_NAME), textFilter),
					phoneNumberPredicate(cb, personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_PHONE_SUBQUERY), textFilter),
					CriteriaBuilderHelper
						.unaccentedIlike(cb, personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_EMAIL_SUBQUERY), textFilter),
					CriteriaBuilderHelper
						.unaccentedIlike(cb, personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_PRIMARY_OTHER_SUBQUERY), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getPersonAddress().get(Location.CITY), textFilter),
					CriteriaBuilderHelper.ilike(cb, joins.getPersonAddress().get(Location.POSTAL_CODE), textFilter)));

			filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
		}
		if (!DataHelper.isNullOrEmpty(caseCriteria.getCaseLike())) {
			Predicate likeFilters = CriteriaBuilderHelper.buildFreeTextSearchPredicate(
				cb,
				caseCriteria.getCaseLike(),
				textFilter -> cb.or(
					CriteriaBuilderHelper.ilike(cb, from.get(Case.UUID), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(Case.EPID_NUMBER), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getFacility().get(Facility.NAME), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(Case.EXTERNAL_ID), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(Case.EXTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(Case.HEALTH_FACILITY_DETAILS), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(Case.INTERNAL_TOKEN), textFilter)));

			filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
		}

		boolean hasEventLikeCriteria = caseCriteria.getEventLike() != null && !caseCriteria.getEventLike().trim().isEmpty();
		boolean hasOnlyCasesWithEventsCriteria = Boolean.TRUE.equals(caseCriteria.getOnlyCasesWithEvents());
		if (hasEventLikeCriteria || hasOnlyCasesWithEventsCriteria) {
			Join<Case, EventParticipant> eventParticipant = joins.getEventParticipants();
			Join<EventParticipant, Event> event = joins.getEventParticipantJoins().getEvent();

			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.isFalse(event.get(Event.DELETED)), cb.isFalse(eventParticipant.get(EventParticipant.DELETED)));

			if (hasEventLikeCriteria) {
				String[] textFilters = caseCriteria.getEventLike().trim().split("\\s+");
				for (String textFilter : textFilters) {
					Predicate likeFilters = cb.or(
						CriteriaBuilderHelper.unaccentedIlike(cb, event.get(Event.EVENT_DESC), textFilter),
						CriteriaBuilderHelper.unaccentedIlike(cb, event.get(Event.EVENT_TITLE), textFilter),
						CriteriaBuilderHelper.unaccentedIlike(cb, event.get(Event.INTERNAL_TOKEN), textFilter),
						CriteriaBuilderHelper.ilike(cb, event.get(Event.UUID), textFilter));
					filter = CriteriaBuilderHelper.and(cb, filter, likeFilters, cb.isFalse(eventParticipant.get(EventParticipant.DELETED)));
				}
			}
			if (hasOnlyCasesWithEventsCriteria) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(event.get(Event.ID)));
			}
		}
		if (caseCriteria.getReportingUserLike() != null) {
			String[] textFilters = caseCriteria.getReportingUserLike().split("\\s+");
			for (String textFilter : textFilters) {
				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getReportingUser().get(User.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getReportingUser().get(User.LAST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getReportingUser().get(User.USER_NAME), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (caseCriteria.getBirthdateYYYY() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getPerson().get(Person.BIRTHDATE_YYYY), caseCriteria.getBirthdateYYYY()));
		}
		if (caseCriteria.getBirthdateMM() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getPerson().get(Person.BIRTHDATE_MM), caseCriteria.getBirthdateMM()));
		}
		if (caseCriteria.getBirthdateDD() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getPerson().get(Person.BIRTHDATE_DD), caseCriteria.getBirthdateDD()));
		}
		if (Boolean.TRUE.equals(caseCriteria.getOnlyContactsFromOtherInstances())) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(
					cb.isNotNull(joins.getSormasToSormasShareInfo().get(SormasToSormasShareInfo.CAZE)),
					cb.isNotNull(from.get(Case.SORMAS_TO_SORMAS_ORIGIN_INFO))));
		}
		if (Boolean.TRUE.equals(caseCriteria.getOnlyCasesWithReinfection())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.RE_INFECTION), YesNoUnknown.YES));
		}
		if (Boolean.TRUE.equals(caseCriteria.getOnlyCasesWithDontShareWithExternalSurvTool())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(Case.DONT_SHARE_WITH_REPORTING_TOOL)));
		}
		if (Boolean.TRUE.equals(caseCriteria.getOnlyShowCasesWithFulfilledReferenceDefinition())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.CASE_REFERENCE_DEFINITION), CaseReferenceDefinition.FULFILLED));
		}
		if (caseCriteria.getWithOwnership() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, createOwnershipPredicate(Boolean.TRUE.equals(caseCriteria.getWithOwnership()), from, cb, cq));
		}

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			externalShareInfoService.buildShareCriteriaFilter(
				caseCriteria,
				cq,
				cb,
				from,
				ExternalShareInfo.CAZE,
				(latestShareDate) -> createChangeDateFilter(cq, cb, joins, latestShareDate, true, true)));

		return filter;
	}

	private Predicate createRelevanceStatusFilter(CaseCriteria caseCriteria, CaseQueryContext caseQueryContext) {

		final From<?, Case> from = caseQueryContext.getRoot();
		final CriteriaBuilder cb = caseQueryContext.getCriteriaBuilder();

		Predicate filter = null;

		if (caseCriteria.getRelevanceStatus() != null) {
			if (caseCriteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.or(cb.equal(from.get(Case.ARCHIVED), false), cb.isNull(from.get(Case.ARCHIVED))));
			} else if (caseCriteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.ARCHIVED), true));
			} else if (caseCriteria.getRelevanceStatus() == EntityRelevanceStatus.DELETED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.DELETED), true));
			}
		}
		if (caseCriteria.getRelevanceStatus() != EntityRelevanceStatus.DELETED) {
			filter = CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from));
		}
		return filter;
	}

	public Predicate createOwnershipPredicate(boolean withOwnership, From<?, Case> from, CriteriaBuilder cb, CriteriaQuery<?> cq) {
		Subquery<Boolean> sharesQuery = cq.subquery(Boolean.class);
		Root<SormasToSormasShareInfo> shareInfoFrom = sharesQuery.from(SormasToSormasShareInfo.class);
		sharesQuery.select(shareInfoFrom.get(SormasToSormasShareInfo.ID));

		Subquery<Number> latestRequestDateQuery = cq.subquery(Number.class);
		Root<ShareRequestInfo> shareRequestInfoRoot = latestRequestDateQuery.from(ShareRequestInfo.class);
		latestRequestDateQuery.select(cb.max(shareRequestInfoRoot.get(ShareRequestInfo.CREATION_DATE)));
		latestRequestDateQuery.where(
			cb.equal(
				shareRequestInfoRoot.join(ShareRequestInfo.SHARES, JoinType.LEFT).get(SormasToSormasShareInfo.ID),
				shareInfoFrom.get(SormasToSormasShareInfo.ID)));

		Join<Object, Object> requestsJoin = shareInfoFrom.join(SormasToSormasShareInfo.REQUESTS);
		sharesQuery.where(
			cb.equal(shareInfoFrom.get(SormasToSormasShareInfo.CAZE), from.get(Case.ID)),
			cb.equal(shareInfoFrom.get(SormasToSormasShareInfo.OWNERSHIP_HANDED_OVER), true),
			cb.equal(
				requestsJoin.on(cb.equal(requestsJoin.get(ShareRequestInfo.CREATION_DATE), latestRequestDateQuery))
					.get(ShareRequestInfo.REQUEST_STATUS),
				ShareRequestStatus.ACCEPTED));

		if (withOwnership) {
			return cb.and(
				cb.or(
					cb.isNull(from.get(Case.SORMAS_TO_SORMAS_ORIGIN_INFO)),
					cb.equal(from.join(Case.SORMAS_TO_SORMAS_ORIGIN_INFO, JoinType.LEFT).get(SormasToSormasOriginInfo.OWNERSHIP_HANDED_OVER), true)),
				cb.not(cb.exists(sharesQuery)));
		} else {
			return cb.or(
				cb.equal(from.join(Case.SORMAS_TO_SORMAS_ORIGIN_INFO, JoinType.LEFT).get(SormasToSormasOriginInfo.OWNERSHIP_HANDED_OVER), false),
				cb.exists(sharesQuery));
		}
	}

	/**
	 * Creates a filter that excludes all cases that are either {@link Case#isArchived()} or {@link DeletableAdo#isDeleted()}.
	 */
	public Predicate createActiveCasesFilter(CriteriaBuilder cb, From<?, Case> root) {
		return cb.and(cb.isFalse(root.get(Case.ARCHIVED)), cb.isFalse(root.get(Case.DELETED)));
	}

	/**
	 * Creates a default filter that should be used as the basis of queries that do not use {@link CaseCriteria}.
	 * This essentially removes {@link DeletableAdo#isDeleted()} cases from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, Case> root) {
		return cb.isFalse(root.get(Case.DELETED));
	}

	@Override
	public void deletePermanent(Case caze) {

		// Delete all tasks associated with this case
		Optional.ofNullable(caze.getTasks()).ifPresent(tl -> tl.forEach(t -> taskService.deletePermanent(t)));

		// Delete all samples that are only associated with this case
		caze.getSamples()
			.stream()
			.filter(sample -> sample.getAssociatedContact() == null && sample.getAssociatedEventParticipant() == null)
			.forEach(sample -> sampleService.deletePermanent(sample));

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<Visit> cd = cb.createCriteriaDelete(Visit.class);
		Root<Visit> visitRoot = cd.from(Visit.class);
		Subquery<Long> contactVisitsSubquery = cd.subquery(Long.class);
		initVisitSubqueryForDeletion(cb, visitRoot, contactVisitsSubquery);
		cd.where(cb.and(cb.equal(visitRoot.get(Visit.CAZE).get(Case.ID), caze.getId()), cb.not(cb.exists(contactVisitsSubquery))));
		em.createQuery(cd).executeUpdate();

		CriteriaUpdate<Visit> cu = cb.createCriteriaUpdate(Visit.class);
		Root<Visit> updateRoot = cu.from(Visit.class);
		cu.set(Visit.CAZE, null);
		Subquery<Long> updateVisitsSubquery = cu.subquery(Long.class);
		initVisitSubqueryForDeletion(cb, visitRoot, updateVisitsSubquery);
		cu.where(cb.and(cb.equal(updateRoot.get(Visit.CAZE).get(Case.ID), caze.getId()), cb.exists(updateVisitsSubquery)));
		em.createQuery(cu).executeUpdate();

		// Delete surveillance reports related to this case
		surveillanceReportService.getByCaseUuids(Collections.singletonList(caze.getUuid()))
			.forEach(s -> surveillanceReportFacade.delete(s.getUuid()));

		// Delete documents related to this case
		documentService.getRelatedToEntity(DocumentRelatedEntityType.CASE, caze.getUuid()).forEach(d -> documentService.markAsDeleted(d));

		// Delete clinical management data
		if (caze.getTherapy() != null) {
			TherapyReferenceDto therapy = new TherapyReferenceDto(caze.getTherapy().getUuid());
			List<Treatment> treatments = treatmentService.findBy(new TreatmentCriteria().therapy(therapy));
			treatments.forEach(t -> treatmentService.deletePermanent(t));
			prescriptionService.findBy(new PrescriptionCriteria().therapy(therapy)).forEach(p -> prescriptionService.deletePermanent(p));
		}

		if (caze.getClinicalCourse() != null) {
			ClinicalCourseReferenceDto clinicalCourse = new ClinicalCourseReferenceDto(caze.getClinicalCourse().getUuid());
			List<ClinicalVisit> cvs = clinicalVisitService.findBy(new ClinicalVisitCriteria().clinicalCourse(clinicalCourse));
			cvs.forEach(c -> clinicalVisitService.deletePermanent(c));
		}

		// Remove the case from any S2S share info referencing it
		sormasToSormasShareInfoService.getByAssociatedEntity(SormasToSormasShareInfo.CAZE, caze.getUuid()).forEach(s -> {
			s.setCaze(null);
			if (sormasToSormasShareInfoFacade.hasAnyEntityReference(s)) {
				sormasToSormasShareInfoService.ensurePersisted(s);
			} else {
				try {
					sormasToSormasFacade.revokePendingShareRequests(Collections.singletonList(s), false);
				} catch (SormasToSormasException e) {
					logger.warn("Could not revoke share requests of share info {}", s.getUuid(), e);
				}

				sormasToSormasShareInfoService.deletePermanent(s);
			}
		});

		// Remove the case from any external share info referencing it
		externalShareInfoService.getShareInfoByCase(caze.getUuid()).forEach(e -> {
			externalShareInfoService.deletePermanent(e);
		});

		// Remove the case from all cases in which it has been set as a duplicate
		getCasesSetAsDuplicate(caze.getId()).forEach(c -> {
			c.setDuplicateOf(null);
			ensurePersisted(c);
		});

		deleteCaseLinks(caze);

		super.deletePermanent(caze);
	}

	private void initVisitSubqueryForDeletion(CriteriaBuilder cb, Root<Visit> visitRoot, Subquery<Long> contactVisitsSubquery) {
		Root<Visit> subqueryRoot = contactVisitsSubquery.from(Visit.class);
		Join<Visit, Contact> visitContactJoin = subqueryRoot.join(Visit.CONTACTS, JoinType.INNER);
		contactVisitsSubquery.where(cb.equal(subqueryRoot.get(Visit.ID), visitRoot.get(Visit.ID)));
		contactVisitsSubquery.select(visitContactJoin.get(Visit.ID));
	}

	@Override
	public ProcessedEntity archive(String entityUuid, Date endOfProcessingDate) {
		return archive(Collections.singletonList(entityUuid)).get(0);
	}

	@Override
	public List<ProcessedEntity> archive(List<String> entityUuids) {

		List<ProcessedEntity> updatedInExternalSurveillanceTool = updateArchiveFlagInExternalSurveillanceTool(entityUuids, true);
		List<String> uuidsWithoutFailure = getEntitiesWithoutFailure(entityUuids, updatedInExternalSurveillanceTool).stream()
			.map(AbstractDomainObject::getUuid)
			.collect(Collectors.toList());

		List<ProcessedEntity> resultList =
			updatedInExternalSurveillanceTool.stream().filter(e -> !uuidsWithoutFailure.contains(e.getEntityUuid())).collect(Collectors.toList());

		if (uuidsWithoutFailure.size() > 0) {
			resultList.addAll(super.archive(uuidsWithoutFailure));
		}

		return resultList;
	}

	@Override
	public List<ProcessedEntity> dearchive(List<String> entityUuids, String dearchiveReason) {

		List<ProcessedEntity> updatedInExternalSurveillanceTool = updateArchiveFlagInExternalSurveillanceTool(entityUuids, false);
		List<String> uuidsWithoutFailure = getEntitiesWithoutFailure(entityUuids, updatedInExternalSurveillanceTool).stream()
			.map(AbstractDomainObject::getUuid)
			.collect(Collectors.toList());

		List<ProcessedEntity> resultList =
			updatedInExternalSurveillanceTool.stream().filter(e -> !uuidsWithoutFailure.contains(e.getEntityUuid())).collect(Collectors.toList());

		if (uuidsWithoutFailure.size() > 0) {
			resultList.addAll(super.dearchive(uuidsWithoutFailure, dearchiveReason));
		}

		return resultList;
	}

	private List<ProcessedEntity> updateArchiveFlagInExternalSurveillanceTool(List<String> entityUuids, boolean archived) {
		List<ProcessedEntity> processedEntities = new ArrayList<>();

		List<String> sharedCaseUuids = getEligibleUuidsForSharingWithExternalSurveillanceTool(entityUuids);
		if (!sharedCaseUuids.isEmpty()) {
			processedEntities = externalSurveillanceToolGatewayFacade.sendCasesInternal(sharedCaseUuids, archived);
		}

		return processedEntities;
	}

	private List<String> getEligibleUuidsForSharingWithExternalSurveillanceTool(List<String> entityUuids) {
		List<String> sharedCaseUuids = new ArrayList<>();
		List<String> uuidsAllowedToBeShared = getEntityUuidsAllowedToBeShared(entityUuids);
		if (!uuidsAllowedToBeShared.isEmpty()) {
			sharedCaseUuids = getSharedCaseUuids(uuidsAllowedToBeShared);
		}

		return sharedCaseUuids;
	}

	public List<String> getSharedCaseUuids(List<String> entityUuids) {
		List<Long> caseIds = getCaseIds(entityUuids);
		List<String> sharedCaseUuids = new ArrayList<>();
		List<ExternalShareInfoCountAndLatestDate> caseShareInfos =
			externalShareInfoService.getShareCountAndLatestDate(caseIds, ExternalShareInfo.CAZE);
		caseShareInfos.forEach(shareInfo -> {
			if (shareInfo.getLatestStatus() != ExternalShareStatus.DELETED) {
				sharedCaseUuids.add(shareInfo.getAssociatedObjectUuid());
			}
		});

		return sharedCaseUuids;
	}

	public List<Long> getCaseIds(List<String> entityUuids) {
		List<Long> caseIds = new ArrayList<>();
		entityUuids.forEach(uuid -> caseIds.add(this.getByUuid(uuid).getId()));
		return caseIds;
	}

	public List<String> getEntityUuidsAllowedToBeShared(List<String> entityUuids) {
		List<CaseDataDto> casesAllowedToBeShare =
			caseFacade.getByUuids(entityUuids).stream().filter(c -> !c.isDontShareWithReportingTool()).collect(Collectors.toList());

		return casesAllowedToBeShare.stream().map(CaseDataDto::getUuid).collect(Collectors.toList());
	}

	@Override
	public void delete(Case caze, DeletionDetails deletionDetails) throws ExternalSurveillanceToolRuntimeException {

		// Soft-delete all samples that are only associated with this case
		caze.getSamples()
			.stream()
			.filter(sample -> sample.getAssociatedContact() == null && sample.getAssociatedEventParticipant() == null)
			.forEach(sample -> sampleService.delete(sample, deletionDetails));

		deleteCaseInExternalSurveillanceTool(caze);
		deleteCaseLinks(caze);
		caze.setDeletionReason(deletionDetails.getDeletionReason());
		caze.setOtherDeletionReason(deletionDetails.getOtherDeletionReason());

		// Mark the case as deleted
		super.delete(caze, deletionDetails);
	}

	@Override
	public void restore(Case caze) {
		// un-delete all samples that are only associated with this case
		caze.getSamples().stream().forEach(sample -> sampleService.restore(sample));
		super.restore(caze);
	}

	private void deleteCaseInExternalSurveillanceTool(Case caze) {
		try {
			if (!caze.isDontShareWithReportingTool()) {
				caseFacade.deleteCaseInExternalSurveillanceTool(caze);
			}
		} catch (ExternalSurveillanceToolException e) {
			throw new ExternalSurveillanceToolRuntimeException(e.getMessage(), e.getErrorCode());
		}
	}

	private void deleteCaseLinks(Case caze) {

		// Remove the case as the resulting case and source case from all contacts
		Optional.ofNullable(caze.getContacts()).ifPresent(cl -> cl.forEach(c -> {
			c.setCaze(null);

			// Assign the case jurisdiction to the contact if it does not already have one
			if (c.getDistrict() == null) {
				c.setRegion(caze.getResponsibleRegion());
				c.setDistrict(caze.getResponsibleDistrict());
				c.setCommunity(caze.getResponsibleCommunity());
			}

			externalJournalService.handleExternalJournalPersonUpdateAsync(c.getPerson().toReference());
			contactService.ensurePersisted(c);
		}));

		contactService.getAllByResultingCase(caze, true).forEach(c -> {
			c.setResultingCase(null);
			c.setContactStatus(ContactStatus.DROPPED);
			externalJournalService.handleExternalJournalPersonUpdateAsync(c.getPerson().toReference());
			contactService.ensurePersisted(c);
		});

		// Remove the case from any sample that is also connected to other entities
		caze.getSamples().stream().filter(s -> s.getAssociatedContact() != null || s.getAssociatedEventParticipant() != null).forEach(s -> {
			s.setAssociatedCase(null);
			sampleService.ensurePersisted(s);
		});

		// Remove the case as the resulting case of event participants
		Optional.ofNullable(caze.getEventParticipants()).ifPresent(es -> es.forEach(ep -> {
			ep.setResultingCase(null);
			eventParticipantService.ensurePersisted(ep);
		}));

		// Remove the case as the resulting case of travel entries
		travelEntryService.getAllByResultingCase(caze, true).forEach(t -> {
			t.setResultingCase(null);
			travelEntryService.ensurePersisted(t);
		});

		// Remove the case as the related case of immunizations
		immunizationService.unlinkRelatedCase(caze);
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Case> casePath, Timestamp date) {
		return createChangeDateFilter(cb, toJoins(casePath), date, false);
	}

	/**
	 * @param cb
	 * @param joins
	 * @param date
	 * @param includeExtendedChangeDateFilters
	 *            additional change dates filters for: sample, pathogenTests, patient and location
	 * @return
	 */
	public Predicate createChangeDateFilter(CriteriaBuilder cb, CaseJoins joins, Timestamp date, boolean includeExtendedChangeDateFilters) {

		return addChangeDates(new ChangeDateFilterBuilder(cb, date), joins, includeExtendedChangeDateFilters).build();
	}

	private Predicate createChangeDateFilter(
		CriteriaQuery<?> cq,
		CriteriaBuilder cb,
		CaseJoins joins,
		Expression<? extends Date> dateExpression,
		boolean includeExtendedChangeDateFilters,
		boolean includeRelevantVaccinations) {

		ChangeDateFilterBuilder builder = addChangeDates(new ChangeDateFilterBuilder(cb, dateExpression), joins, includeExtendedChangeDateFilters);

		if (includeRelevantVaccinations) {
			Join<Person, Immunization> immunizationJoin = joins.getPersonJoins().getImmunization();
			Join<Immunization, Vaccination> vaccinationsJoin = immunizationJoin.join(Immunization.VACCINATIONS, JoinType.LEFT);

			builder.add(vaccinationsJoin.on(vaccinationService.getRelevantVaccinationPredicate(joins.getRoot(), cq, cb, vaccinationsJoin)));
			// also consider the immunization of relevant vaccinations
			builder.add(vaccinationsJoin, Vaccination.IMMUNIZATION);

		}

		return builder.build();
	}

	@Override
	protected <T extends ChangeDateBuilder<T>> T addChangeDates(T builder, CaseJoins joins, boolean includeExtendedChangeDateFilters) {
		final From<?, Case> caseFrom = joins.getRoot();
		Join<Case, Hospitalization> hospitalization = joins.getHospitalization();
		Join<Case, ClinicalCourse> clinicalCourse = joins.getClinicalCourse();

		builder = super.addChangeDates(builder, joins, includeExtendedChangeDateFilters).add(caseFrom, Case.SYMPTOMS)
			.add(hospitalization)
			.add(hospitalization, Hospitalization.PREVIOUS_HOSPITALIZATIONS)
			.add(caseFrom, Case.THERAPY)
			.add(clinicalCourse)
			.add(caseFrom, Case.HEALTH_CONDITIONS)
			.add(caseFrom, Case.MATERNAL_HISTORY)
			.add(caseFrom, Case.PORT_HEALTH_INFO)
			.add(caseFrom, Case.SORMAS_TO_SORMAS_ORIGIN_INFO)
			.add(caseFrom, Case.SORMAS_TO_SORMAS_SHARES);

		builder = epiDataService.addChangeDates(builder, joins.getEpiData());

		if (includeExtendedChangeDateFilters) {
			Join<Case, Sample> caseSampleJoin = joins.getSamples();
			Join<Case, Person> casePersonJoin = joins.getPerson();
			Join<Case, Visit> caseVisitJoin = joins.getVisit();
			Join<Case, SurveillanceReport> caseSurveillanceReportJoin = joins.getSurveillanceReportJoin();

			builder = builder.add(caseSampleJoin)
				.add(caseSampleJoin, Sample.PATHOGENTESTS)
				.add(casePersonJoin)
				.add(casePersonJoin, Person.ADDRESS)
				.add(caseVisitJoin)
				.add(caseSurveillanceReportJoin);
		}

		return builder;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createUserFilterInternal(CriteriaBuilder cb, CriteriaQuery cq, From<?, Case> from) {
		return createUserFilter(new CaseQueryContext(cb, cq, from));
	}

	@Override
	protected CaseJoins toJoins(From<?, Case> adoPath) {
		return new CaseJoins(adoPath);
	}

	public Predicate createUserFilter(CaseQueryContext caseQueryContext) {
		return createUserFilter(caseQueryContext, null);
	}

	@SuppressWarnings("rawtypes")
	public Predicate createUserFilter(CaseQueryContext caseQueryContext, CaseUserFilterCriteria userFilterCriteria) {

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}

		final CriteriaQuery<?> cq = caseQueryContext.getQuery();
		final CriteriaBuilder cb = caseQueryContext.getCriteriaBuilder();
		final From<?, Case> casePath = caseQueryContext.getRoot();

		Predicate filterResponsible = null;
		Predicate filter = null;

		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (jurisdictionLevel != JurisdictionLevel.NATION) {
			// whoever created the case or is assigned to it is allowed to access it
			if (userFilterCriteria == null || (userFilterCriteria.getIncludeCasesFromOtherJurisdictions())) {
				filterResponsible = cb.equal(casePath.get(Case.REPORTING_USER).get(User.ID), currentUser.getId());
				filterResponsible = cb.or(filterResponsible, cb.equal(casePath.get(Case.SURVEILLANCE_OFFICER).get(User.ID), currentUser.getId()));
				filterResponsible = cb.or(filterResponsible, cb.equal(casePath.get(Case.CASE_OFFICER).get(User.ID), currentUser.getId()));
			} else {
				// make sure we don't see all cases just because no filter is defined at all
				filterResponsible = cb.disjunction();
			}

			switch (jurisdictionLevel) {
			case REGION:
				final Region region = currentUser.getRegion();
				if (region != null) {
					filter = CriteriaBuilderHelper.or(
						cb,
						filter,
						cb.equal(casePath.get(Case.REGION).get(Region.ID), region.getId()),
						cb.equal(casePath.get(Case.RESPONSIBLE_REGION).get(Region.ID), region.getId()));
				}
				break;
			case DISTRICT:
				final District district = currentUser.getDistrict();
				if (district != null) {
					filter = CriteriaBuilderHelper.or(
						cb,
						filter,
						cb.equal(casePath.get(Case.DISTRICT).get(District.ID), district.getId()),
						cb.equal(casePath.get(Case.RESPONSIBLE_DISTRICT).get(District.ID), district.getId()));
				}
				break;
			case HEALTH_FACILITY:
				final Facility healthFacility = currentUser.getHealthFacility();
				if (healthFacility != null) {
					filter =
						CriteriaBuilderHelper.or(cb, filter, cb.equal(casePath.get(Case.HEALTH_FACILITY).get(Facility.ID), healthFacility.getId()));
				}
				break;
			case COMMUNITY:
				final Community community = currentUser.getCommunity();
				if (community != null) {
					filter = CriteriaBuilderHelper.or(
						cb,
						filter,
						cb.equal(casePath.get(Case.COMMUNITY).get(Community.ID), community.getId()),
						cb.equal(casePath.get(Case.RESPONSIBLE_COMMUNITY).get(Community.ID), community.getId()));
				}
				break;
			case POINT_OF_ENTRY:
				final PointOfEntry pointOfEntry = currentUser.getPointOfEntry();
				if (pointOfEntry != null) {
					filter =
						CriteriaBuilderHelper.or(cb, filter, cb.equal(casePath.get(Case.POINT_OF_ENTRY).get(PointOfEntry.ID), pointOfEntry.getId()));
				}
				break;
			case LABORATORY:
				final Subquery<Long> sampleSubQuery = cq.subquery(Long.class);
				final Root<Sample> sampleRoot = sampleSubQuery.from(Sample.class);
				final SampleJoins joins = new SampleJoins(sampleRoot);
				final Join cazeJoin = joins.getCaze();
				sampleSubQuery.where(cb.and(cb.equal(cazeJoin, casePath), sampleService.createUserFilterWithoutAssociations(cb, joins)));
				sampleSubQuery.select(sampleRoot.get(Sample.ID));
				filter = CriteriaBuilderHelper.or(cb, filter, cb.exists(sampleSubQuery));
				break;
			default:
			}

			// get all cases based on the user's contact association
			if (userFilterCriteria == null
				|| (!userFilterCriteria.isExcludeCasesFromContacts()
					&& Boolean.TRUE.equals(userFilterCriteria.getIncludeCasesFromOtherJurisdictions()))) {
				ContactQueryContext contactQueryContext =
					new ContactQueryContext(cb, cq, new ContactJoins(caseQueryContext.getJoins().getContacts()));
				filter = CriteriaBuilderHelper.or(cb, filter, contactService.createUserFilterWithoutCase(contactQueryContext));
			}

			// users can only be assigned to a task when they have also access to the case
			//Join<Case, Task> tasksJoin = from.join(Case.TASKS, JoinType.LEFT);
			//filter = cb.or(filter, cb.equal(tasksJoin.get(Task.ASSIGNEE_USER), user));

			// all users (without specific restrictions) get access to cases that have been made available to the whole country
			if ((userFilterCriteria == null || userFilterCriteria.getIncludeCasesFromOtherJurisdictions())
				&& !featureConfigurationFacade.isFeatureDisabled(FeatureType.NATIONAL_CASE_SHARING)) {
				filter = CriteriaBuilderHelper.or(cb, filter, cb.isTrue(casePath.get(Case.SHARED_TO_COUNTRY)));
			}
		}

		// only show cases of a specific disease if a limited disease is set
		if (currentUser.getLimitedDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(casePath.get(Case.DISEASE), currentUser.getLimitedDisease()));
		}

		// port health users can only see port health cases
		if (currentUser.getUserRoles().stream().anyMatch(userRole -> userRole.isPortHealthUser())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(casePath.get(Case.CASE_ORIGIN), CaseOrigin.POINT_OF_ENTRY));
		}

		filter = CriteriaBuilderHelper.or(cb, filter, filterResponsible);

		if (RequestContextHolder.isMobileSync()) {
			if ((userFilterCriteria == null || !userFilterCriteria.isExcludeLimitedSyncRestrictions())
				&& featureConfigurationFacade
					.isPropertyValueTrue(FeatureType.LIMITED_SYNCHRONIZATION, FeatureTypeProperty.EXCLUDE_NO_CASE_CLASSIFIED_CASES)) {
				final Predicate limitedCaseSyncPredicate = cb.not(
					cb.and(
						cb.equal(casePath.get(Case.CASE_CLASSIFICATION), CaseClassification.NO_CASE),
						cb.or(
							cb.notEqual(casePath.get(Case.REPORTING_USER), currentUser),
							cb.and(cb.equal(casePath.get(Case.REPORTING_USER), currentUser), cb.isNull(casePath.get(Case.CREATION_VERSION))))));
				filter = CriteriaBuilderHelper.and(cb, filter, limitedCaseSyncPredicate);
			}

			Predicate limitedChangeDatePredicate = CriteriaBuilderHelper.and(cb, createLimitedChangeDateFilter(cb, casePath));
			if (limitedChangeDatePredicate != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, limitedChangeDatePredicate);
			}
		}

		return filter;
	}

	/**
	 * Creates a filter that checks whether the case has "started" within the time frame specified by {@code fromDate} and {@code toDate}.
	 * By default (if {@code dateType} is null), this logic looks at the {@link Symptoms#getOnsetDate()} first or, if this is null,
	 * the {@link Case#getReportDate()}.
	 */
	public Predicate createNewCaseFilter(CaseQueryContext caseQueryContext, Date fromDate, Date toDate, CriteriaDateType dateType) {

		final CriteriaBuilder cb = caseQueryContext.getCriteriaBuilder();
		final From<?, Case> caze = caseQueryContext.getRoot();
		final CriteriaQuery<?> cq = caseQueryContext.getQuery();
		final CaseJoins joins = caseQueryContext.getJoins();

		Join<Case, Symptoms> symptoms = joins.getSymptoms();

		Date toDateEndOfDay = DateHelper.getEndOfDay(toDate);

		Predicate onsetDateFilter = cb.between(symptoms.get(Symptoms.ONSET_DATE), fromDate, toDateEndOfDay);
		Predicate reportDateFilter = cb.between(caze.get(Case.REPORT_DATE), fromDate, toDateEndOfDay);

		Predicate newCaseFilter = null;
		if (dateType == null || dateType == NewCaseDateType.MOST_RELEVANT) {
			newCaseFilter = cb.or(onsetDateFilter, cb.and(cb.isNull(symptoms.get(Symptoms.ONSET_DATE)), reportDateFilter));
		} else if (dateType == NewCaseDateType.ONSET) {
			newCaseFilter = onsetDateFilter;
		} else if (dateType == NewCaseDateType.REPORT) {
			newCaseFilter = reportDateFilter;
		} else if (dateType == ExternalShareDateType.LAST_EXTERNAL_SURVEILLANCE_TOOL_SHARE) {
			newCaseFilter = externalShareInfoService.buildLatestSurvToolShareDateFilter(
				cq,
				cb,
				caze,
				ExternalShareInfo.CAZE,
				(latestShareDate) -> cb.between(latestShareDate, fromDate, toDateEndOfDay));
		}

		return newCaseFilter;
	}

	public Case getRelevantCaseForFollowUp(Person person, Disease disease, Date referenceDate) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(Case.class);
		Root<Case> caseRoot = cq.from(Case.class);

		Predicate filter = CriteriaBuilderHelper
			.and(cb, createDefaultFilter(cb, caseRoot), buildRelevantCasesFilterForFollowUp(person, disease, referenceDate, cb, caseRoot));
		cq.where(filter);

		return em.createQuery(cq).getResultStream().findFirst().orElse(null);
	}

	/**
	 * Returns a filter that can be used to retrieve all cases with the specified
	 * person and disease whose report date is before the reference date and,
	 * if available, whose follow-up until date is after the reference date,
	 * including an offset to allow some tolerance.
	 */
	private Predicate buildRelevantCasesFilterForFollowUp(Person person, Disease disease, Date referenceDate, CriteriaBuilder cb, Root<Case> from) {

		Date referenceDateStart = DateHelper.getStartOfDay(referenceDate);
		Date referenceDateEnd = DateHelper.getEndOfDay(referenceDate);

		Predicate filter = CriteriaBuilderHelper.and(cb, cb.equal(from.get(Case.PERSON), person), cb.equal(from.get(Case.DISEASE), disease));

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			cb.lessThanOrEqualTo(from.get(Case.REPORT_DATE), DateHelper.addDays(referenceDateEnd, FollowUpLogic.ALLOWED_DATE_OFFSET)));

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			CriteriaBuilderHelper.or(
				cb,
				// If the case does not have a follow-up until date, use the case report date as a fallback
				CriteriaBuilderHelper.and(
					cb,
					cb.isNull(from.get(Case.FOLLOW_UP_UNTIL)),
					cb.greaterThanOrEqualTo(
						from.get(Case.REPORT_DATE),
						DateHelper.subtractDays(referenceDateStart, FollowUpLogic.ALLOWED_DATE_OFFSET))),
				cb.greaterThanOrEqualTo(
					from.get(Case.FOLLOW_UP_UNTIL),
					DateHelper.subtractDays(referenceDateStart, FollowUpLogic.ALLOWED_DATE_OFFSET))));

		return filter;
	}

	/**
	 * Calculates and sets the follow-up until date and status of the case. If
	 * the date has been overwritten by a user, only the status changes and
	 * extensions of the follow-up until date based on missed visits are executed.
	 * <ul>
	 * <li>Disease with no follow-up: Leave empty and set follow-up status to "No
	 * follow-up"</li>
	 * <li>Others: Use follow-up duration of the disease. Reference for calculation
	 * is the reporting date If the last visit was not cooperative and happened
	 * at the last date of case tracing, we need to do an additional visit.</li>
	 * </ul>
	 */
	public void updateFollowUpDetails(Case caze, boolean followUpStatusChangedByUser) {

		boolean changeStatus = caze.getFollowUpStatus() != FollowUpStatus.CANCELED && caze.getFollowUpStatus() != FollowUpStatus.LOST;
		boolean statusChangedBySystem = false;

		if (!diseaseConfigurationFacade.hasFollowUp(caze.getDisease())) {
			caze.setFollowUpUntil(null);
			if (changeStatus) {
				caze.setFollowUpStatus(FollowUpStatus.NO_FOLLOW_UP);
				statusChangedBySystem = true;
			}
		} else {
			Date currentFollowUpUntil = caze.getFollowUpUntil();
			Date untilDate = computeFollowUpuntilDate(caze);
			caze.setFollowUpUntil(untilDate);
			if (DateHelper.getStartOfDay(currentFollowUpUntil).before(DateHelper.getStartOfDay(untilDate))) {
				caze.setOverwriteFollowUpUntil(false);
			}
			if (changeStatus) {
				Visit lastVisit = caze.getVisits().stream().max(Comparator.comparing(Visit::getVisitDateTime)).orElse(null);
				if (lastVisit != null && !DateHelper.getStartOfDay(lastVisit.getVisitDateTime()).before(DateHelper.getStartOfDay(untilDate))) {
					caze.setFollowUpStatus(FollowUpStatus.COMPLETED);
				} else {
					caze.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
				}
				statusChangedBySystem = true;
			}
		}

		if (followUpStatusChangedByUser) {
			caze.setFollowUpStatusChangeDate(new Date());
			caze.setFollowUpStatusChangeUser(getCurrentUser());
		} else if (statusChangedBySystem) {
			caze.setFollowUpStatusChangeDate(null);
			caze.setFollowUpStatusChangeUser(null);
		}

		externalJournalService.handleExternalJournalPersonUpdateAsync(caze.getPerson().toReference());
		ensurePersisted(caze);
	}

	private Date computeFollowUpuntilDate(Case caze) {
		return computeFollowUpuntilDate(caze, caze.getSamples());
	}

	public Date computeFollowUpuntilDate(Case caze, Collection<Sample> samples) {
		Date earliestSampleDate = sampleService.getEarliestSampleDate(samples);

		return CaseLogic
			.calculateFollowUpUntilDate(
				CaseFacadeEjb.toCaseDto(caze),
				CaseLogic.getFollowUpStartDate(caze.getSymptoms().getOnsetDate(), caze.getReportDate(), earliestSampleDate),
				caze.getVisits().stream().map(VisitFacadeEjb::toVisitDto).collect(Collectors.toList()),
				diseaseConfigurationFacade.getCaseFollowUpDuration(caze.getDisease()),
				false,
				featureConfigurationFacade.isPropertyValueTrue(FeatureType.CASE_FOLLOWUP, FeatureTypeProperty.ALLOW_FREE_FOLLOW_UP_OVERWRITE))
			.getFollowUpEndDate();
	}

	public EditPermissionType isAddContactAllowed(Case caze) {
		// we allow CaseContactViewEdit independently of ownership or similar

		if (!inJurisdictionOrOwned(caze)) {
			return EditPermissionType.REFUSED;
		}

		return super.getEditPermissionType(caze);
	}

	@Override
	public EditPermissionType getEditPermissionType(Case caze) {

		if (!inJurisdictionOrOwned(caze)) {
			return EditPermissionType.OUTSIDE_JURISDICTION;
		}

		if (sormasToSormasShareInfoService.isCaseOwnershipHandedOver(caze)
			|| (caze.getSormasToSormasOriginInfo() != null && !caze.getSormasToSormasOriginInfo().isOwnershipHandedOver())) {
			return EditPermissionType.WITHOUT_OWNERSHIP;
		}

		return super.getEditPermissionType(caze);

	}

	public boolean inJurisdiction(Case caze, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
		Root<Case> root = cq.from(Case.class);
		cq.multiselect(
			JurisdictionHelper
				.booleanSelector(cb, CaseJurisdictionPredicateValidator.of(new CaseQueryContext(cb, cq, root), user).isRootInJurisdiction()));
		cq.where(cb.equal(root.get(Case.UUID), caze.getUuid()));
		return em.createQuery(cq).getResultList().stream().anyMatch(aBoolean -> aBoolean);
	}

	@Override
	public Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Case> from) {
		return inJurisdictionOrOwned(new CaseQueryContext(cb, query, from));
	}

	public Predicate inJurisdictionOrOwned(CaseQueryContext qc) {
		return inJurisdictionOrOwned(qc, userService.getCurrentUser());
	}

	public Predicate inJurisdictionOrOwned(CaseQueryContext qc, User user) {
		return CaseJurisdictionPredicateValidator.of(qc, user).inJurisdictionOrOwned();
	}

	public List<Case> getByPersonUuids(List<String> personUuids) {

		List<Case> cases = new ArrayList<>();
		IterableHelper.executeBatched(personUuids, ModelConstants.PARAMETER_LIMIT, batchedUuids -> cases.addAll(getCasesByPersonUuids(batchedUuids)));
		return cases;
	}

	private List<Case> getCasesByPersonUuids(List<String> personUuids) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(Case.class);
		Root<Case> caseRoot = cq.from(Case.class);
		Join<Case, Person> personJoin = caseRoot.join(Case.PERSON, JoinType.LEFT);

		cq.where(personJoin.get(AbstractDomainObject.UUID).in(personUuids));

		return em.createQuery(cq).getResultList();
	}

	public List<Case> getByExternalId(String externalId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(Case.class);
		Root<Case> caseRoot = cq.from(Case.class);

		cq.where(cb.equal(caseRoot.get(Case.EXTERNAL_ID), externalId), cb.equal(caseRoot.get(Case.DELETED), Boolean.FALSE));

		return em.createQuery(cq).getResultList();
	}

	public List<CaseSelectionDto> getCaseSelectionList(CaseCriteria caseCriteria) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		final Root<Case> root = cq.from(Case.class);

		CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, root);
		final CaseJoins joins = caseQueryContext.getJoins();

		// This is needed in selection because of the combination of distinct and orderBy clauses - every operator in the orderBy has to be part of the select IF distinct is used
		Expression<Date> latestChangedDateFunction =
			cb.function(ExtendedPostgreSQL94Dialect.GREATEST, Date.class, root.get(Case.CHANGE_DATE), joins.getPerson().get(Person.CHANGE_DATE));

		cq.multiselect(
			root.get(Case.UUID),
			root.get(Case.EPID_NUMBER),
			root.get(Case.EXTERNAL_ID),
			root.get(Case.DISEASE),
			joins.getPerson().get(Person.FIRST_NAME),
			joins.getPerson().get(Person.LAST_NAME),
			joins.getPerson().get(Person.APPROXIMATE_AGE),
			joins.getPerson().get(Person.APPROXIMATE_AGE_TYPE),
			joins.getPerson().get(Person.BIRTHDATE_DD),
			joins.getPerson().get(Person.BIRTHDATE_MM),
			joins.getPerson().get(Person.BIRTHDATE_YYYY),
			joins.getResponsibleDistrict().get(District.NAME),
			joins.getFacility().get(Facility.UUID),
			joins.getFacility().get(Facility.NAME),
			root.get(Case.HEALTH_FACILITY_DETAILS),
			root.get(Case.REPORT_DATE),
			joins.getPerson().get(Person.SEX),
			root.get(Case.CASE_CLASSIFICATION),
			root.get(Case.OUTCOME),
			JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(caseQueryContext)),
			latestChangedDateFunction);
		cq.distinct(true);

		cq.orderBy(cb.desc(latestChangedDateFunction));

		Predicate filter =
			CriteriaBuilderHelper.and(cb, createActiveCasesFilter(cb, root), createUserFilter(caseQueryContext, new CaseUserFilterCriteria()));

		if (caseCriteria != null) {
			if (caseCriteria.getDisease() != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(Case.DISEASE), caseCriteria.getDisease()));
			}
			if (caseCriteria.getSourceCaseInfoLike() != null) {
				String[] textFilters = caseCriteria.getSourceCaseInfoLike().split("\\s+");
				for (String textFilter : textFilters) {
					Predicate likeFilters = cb.or(
						CriteriaBuilderHelper.unaccentedIlike(cb, joins.getPerson().get(Person.FIRST_NAME), textFilter),
						CriteriaBuilderHelper.unaccentedIlike(cb, joins.getPerson().get(Person.LAST_NAME), textFilter),
						CriteriaBuilderHelper.ilike(cb, root.get(Case.UUID), textFilter),
						CriteriaBuilderHelper.ilike(cb, root.get(Case.EPID_NUMBER), textFilter),
						CriteriaBuilderHelper.ilike(cb, root.get(Case.EXTERNAL_ID), textFilter));
					filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
				}
			}
		}

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq)
			.unwrap(org.hibernate.query.Query.class)
			.setResultTransformer(new CaseSelectionDtoResultTransformer())
			.getResultList();
	}

	public List<CaseListEntryDto> getEntriesList(Long personId, Integer first, Integer max) {
		if (personId == null) {
			return Collections.emptyList();
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		final Root<Case> caze = cq.from(Case.class);

		CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);
		Join<Case, Symptoms> symptoms = caseQueryContext.getJoins().getSymptoms();

		cq.multiselect(
			caze.get(Case.UUID),
			caze.get(Case.REPORT_DATE),
			caze.get(Case.DISEASE),
			caze.get(Case.CASE_CLASSIFICATION),
			symptoms.get(Symptoms.ONSET_DATE),
			JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(caseQueryContext)),
			caze.get(Case.CHANGE_DATE));

		Predicate filter = cb.equal(caze.get(Case.PERSON_ID), personId);
		filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(caze.get(Case.DELETED)));
		cq.where(filter);

		cq.orderBy(cb.desc(caze.get(Case.CHANGE_DATE)));

		cq.distinct(true);

		return createQuery(cq, first, max).unwrap(org.hibernate.query.Query.class)
			.setResultTransformer(new CaseListEntryDtoResultTransformer())
			.getResultList();
	}

	public Long getIdByUuid(@NotNull String uuid) {

		if (uuid == null) {
			return null;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		ParameterExpression<String> uuidParam = cb.parameter(String.class, AbstractDomainObject.UUID);
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Case> from = cq.from(Case.class);
		cq.select(from.get(AbstractDomainObject.ID));
		cq.where(cb.equal(from.get(AbstractDomainObject.UUID), uuidParam));

		TypedQuery<Long> q = em.createQuery(cq).setParameter(uuidParam, uuid);

		return q.getResultList().stream().findFirst().orElse(null);
	}

	public List<CaseSelectionDto> getSimilarCases(CaseSimilarityCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> root = cq.from(Case.class);
		CaseQueryContext queryContext = new CaseQueryContext(cb, cq, root);
		CaseJoins joins = queryContext.getJoins();

		cq.multiselect(
			root.get(Case.UUID),
			root.get(Case.EPID_NUMBER),
			root.get(Case.EXTERNAL_ID),
			root.get(Case.DISEASE),
			joins.getPerson().get(Person.FIRST_NAME),
			joins.getPerson().get(Person.LAST_NAME),
			joins.getPerson().get(Person.APPROXIMATE_AGE),
			joins.getPerson().get(Person.APPROXIMATE_AGE_TYPE),
			joins.getPerson().get(Person.BIRTHDATE_DD),
			joins.getPerson().get(Person.BIRTHDATE_MM),
			joins.getPerson().get(Person.BIRTHDATE_YYYY),
			joins.getResponsibleDistrict().get(District.NAME),
			joins.getFacility().get(Facility.UUID),
			joins.getFacility().get(Facility.NAME),
			root.get(Case.HEALTH_FACILITY_DETAILS),
			root.get(Case.REPORT_DATE),
			joins.getPerson().get(Person.SEX),
			root.get(Case.CASE_CLASSIFICATION),
			root.get(Case.OUTCOME),
			JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(new CaseQueryContext(cb, cq, root))),
			root.get(Case.CHANGE_DATE));
		cq.distinct(true);

		Predicate filter = getSimilarityFilters(criteria, cb, root, queryContext);

		cq.where(filter);

		return em.createQuery(cq)
			.unwrap(org.hibernate.query.Query.class)
			.setResultTransformer(new CaseSelectionDtoResultTransformer())
			.getResultList();
	}

	public boolean hasSimilarCases(CaseSimilarityCriteria criteria) {
		return exists((cb, root, cq) -> getSimilarityFilters(criteria, cb, root, new CaseQueryContext(cb, cq, root)));
	}

	private Predicate getSimilarityFilters(CaseSimilarityCriteria criteria, CriteriaBuilder cb, Root<Case> root, CaseQueryContext queryContext) {
		final CaseCriteria caseCriteria = criteria.getCaseCriteria();
		CaseJoins joins = queryContext.getJoins();

		Predicate userFilter = createUserFilter(queryContext);

		// In case you wonder: At this point in time the **person** duplicate check has already happen.
		// Here, we really just check if there is a similar case to the current one, therefore it is allowed to just
		// check if a case exists which references the same person to make sure that we are really talking about
		// the same case.
		Predicate personSimilarityFilter =
			criteria.getPersonUuid() != null ? cb.equal(joins.getPerson().get(Person.UUID), criteria.getPersonUuid()) : null;

		if (criteria.getPersonUuids() != null) {
			personSimilarityFilter = joins.getPerson().get(Person.UUID).in(criteria.getPersonUuids());
		}

		Predicate diseaseFilter = caseCriteria.getDisease() != null ? cb.equal(root.get(Case.DISEASE), caseCriteria.getDisease()) : null;

		Predicate regionFilter = null;
		RegionReferenceDto criteriaRegion = caseCriteria.getRegion();
		if (criteriaRegion != null) {
			regionFilter = CriteriaBuilderHelper.or(cb, regionFilter, CaseCriteriaHelper.createRegionCriteriaFilter(cb, joins, criteriaRegion, null));
		}

		Predicate reportDateFilter = criteria.getReportDate() != null
			? cb.between(
				root.get(Case.REPORT_DATE),
				DateHelper.subtractDays(criteria.getReportDate(), 30),
				DateHelper.addDays(criteria.getReportDate(), 30))
			: null;

		Predicate filter = createDefaultFilter(cb, root);
		filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		filter = CriteriaBuilderHelper.and(cb, filter, personSimilarityFilter);
		filter = CriteriaBuilderHelper.and(cb, filter, diseaseFilter);
		filter = CriteriaBuilderHelper.and(cb, filter, regionFilter);
		filter = CriteriaBuilderHelper.and(cb, filter, reportDateFilter);

		return filter;
	}

	/**
	 * Performance: May be slow when there are 10000s of cases with similar report date in the same region.
	 * 
	 * @param limit
	 *            null: no limit
	 */
	public List<CaseMergeIndexDto[]> getCasesForDuplicateMerging(
		CaseCriteria criteria,
		@Min(1) Integer limit,
		boolean showDuplicatesWithDifferentRegion,
		double nameSimilarityThreshold) {

		if (hasAnyToManyJoin(criteria)) {
			// otherwise we would need to introduce a 'distinct' into the query
			throw new IllegalArgumentException("Using a criteria based on a OneToMany or ManyToMany relation is not supported.");
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		final Root<Case> root = cq.from(Case.class);
		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, root);
		final CaseJoins joins = caseQueryContext.getJoins();

		final Root<Case> root2 = cq.from(Case.class);
		final CaseQueryContext caseQueryContext2 = new CaseQueryContext(cb, cq, root2);
		final CaseJoins joins2 = caseQueryContext2.getJoins();

		Join<Case, Person> person = joins.getPerson();
		Join<Case, Person> person2 = joins2.getPerson();
		Join<Case, Symptoms> symptoms = joins.getSymptoms();
		Join<Case, Symptoms> symptoms2 = joins2.getSymptoms();

		// similarity:
		// * first & last name concatenated with whitespace. Similarity function with default threshold of 0.65D
		// uses postgres pg_trgm: https://www.postgresql.org/docs/9.6/pgtrgm.html
		// * same disease
		// * same region (optional)
		// * report date within 30 days of each other
		// * same sex or same birth date (when defined)
		// * same birth date (when fully defined)
		// * onset date within 30 days of each other (when defined)

		Predicate userFilter = createUserFilter(caseQueryContext);
		Predicate criteriaFilter = criteria != null ? createCriteriaFilter(criteria, caseQueryContext) : null;
		Predicate relevanceStatusRoot2Filter = createRelevanceStatusFilter(criteria, caseQueryContext2);
		Expression<String> nameSimilarityExpr = cb.concat(person.get(Person.FIRST_NAME), " ");
		nameSimilarityExpr = cb.concat(nameSimilarityExpr, person.get(Person.LAST_NAME));
		Expression<String> nameSimilarityExpr2 = cb.concat(person2.get(Person.FIRST_NAME), " ");
		nameSimilarityExpr2 = cb.concat(nameSimilarityExpr2, person2.get(Person.LAST_NAME));
		Predicate nameSimilarityFilter =
			cb.gt(cb.function("similarity", double.class, nameSimilarityExpr, nameSimilarityExpr2), nameSimilarityThreshold);
		Predicate diseaseFilter = cb.equal(root.get(Case.DISEASE), root2.get(Case.DISEASE));
		Predicate reportDateFilter = cb.lessThanOrEqualTo(
			cb.abs(
				cb.diff(
					cb.function("date_part", Double.class, cb.parameter(String.class, "date_type"), root.get(Case.REPORT_DATE)),
					cb.function("date_part", Double.class, cb.parameter(String.class, "date_type"), root2.get(Case.REPORT_DATE)))),
			SECONDS_30_DAYS);

		// // todo this should use PersonService.buildSimilarityCriteriaFilter
		// Sex filter: only when sex is filled in for both cases
		Predicate sexFilter = cb.or(
			cb.or(cb.isNull(person.get(Person.SEX)), cb.isNull(person2.get(Person.SEX))),
			cb.or(cb.equal(person.get(Person.SEX), Sex.UNKNOWN), cb.equal(person2.get(Person.SEX), Sex.UNKNOWN)),
			cb.equal(person.get(Person.SEX), person2.get(Person.SEX)));
		// Birth date filter: only when birth date is filled in for both cases
		Predicate birthDateFilter = cb.or(
			cb.or(
				cb.isNull(person.get(Person.BIRTHDATE_DD)),
				cb.isNull(person.get(Person.BIRTHDATE_MM)),
				cb.isNull(person.get(Person.BIRTHDATE_YYYY)),
				cb.isNull(person2.get(Person.BIRTHDATE_DD)),
				cb.isNull(person2.get(Person.BIRTHDATE_MM)),
				cb.isNull(person2.get(Person.BIRTHDATE_YYYY))),
			cb.and(
				cb.equal(person.get(Person.BIRTHDATE_DD), person2.get(Person.BIRTHDATE_DD)),
				cb.equal(person.get(Person.BIRTHDATE_MM), person2.get(Person.BIRTHDATE_MM)),
				cb.equal(person.get(Person.BIRTHDATE_YYYY), person2.get(Person.BIRTHDATE_YYYY))));
		// Onset date filter: only when onset date is filled in for both cases
		Predicate onsetDateFilter = cb.or(
			cb.or(cb.isNull(symptoms.get(Symptoms.ONSET_DATE)), cb.isNull(symptoms2.get(Symptoms.ONSET_DATE))),
			cb.lessThanOrEqualTo(
				cb.abs(
					cb.diff(
						cb.function("date_part", Double.class, cb.parameter(String.class, "date_type"), symptoms.get(Symptoms.ONSET_DATE)),
						cb.function("date_part", Double.class, cb.parameter(String.class, "date_type"), symptoms2.get(Symptoms.ONSET_DATE)))),
				SECONDS_30_DAYS));

		Predicate filter = CriteriaBuilderHelper.and(cb, userFilter, criteriaFilter, relevanceStatusRoot2Filter, nameSimilarityFilter, diseaseFilter);

		if (!showDuplicatesWithDifferentRegion) {
			Predicate regionFilter = cb.or(
				cb.equal(root.get(Case.RESPONSIBLE_REGION), root2.get(Case.RESPONSIBLE_REGION)),
				cb.and(cb.isNotNull(root.get(Case.REGION)), cb.equal(root.get(Case.REGION), root2.get(Case.REGION))));
			filter = CriteriaBuilderHelper.and(cb, filter, regionFilter);
		}

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			reportDateFilter,
			sexFilter,
			birthDateFilter,
			onsetDateFilter,
			cb.notEqual(root.get(Case.ID), root2.get(Case.ID)),
			cb.lessThanOrEqualTo(root2.get(Case.CREATION_DATE), DateHelper.getEndOfDay(criteria.getCreationDateTo())));

		if (CollectionUtils.isNotEmpty(criteria.getCaseUuidsForMerge())) {
			Set<String> caseUuidsForMerge = criteria.getCaseUuidsForMerge();
			filter = cb.and(filter, cb.or(root.get(Case.UUID).in(caseUuidsForMerge), root2.get(Case.UUID).in(caseUuidsForMerge)));
		}

		cq.where(filter);
		cq.multiselect(root.get(Case.ID), root2.get(Case.ID), root.get(Case.CREATION_DATE));
		cq.orderBy(cb.desc(root.get(Case.CREATION_DATE)));

		TypedQuery<Object[]> typedQuery = em.createQuery(cq).setParameter("date_type", "epoch");
		if (limit != null) {
			// Double the limit because the query result will contain each pair twice; since these duplicates will
			// be removed, the final result list would only contain limit/2 entries otherwise
			typedQuery.setMaxResults(limit * 2);
		}

		List<Object[]> foundIds = typedQuery.getResultList();
		List<CaseMergeIndexDto[]> resultList = new ArrayList<>();
		Set<AbstractMap.SimpleImmutableEntry<Long, Long>> resultIdsSet = new HashSet<>();

		if (!foundIds.isEmpty()) {
			CriteriaQuery<CaseMergeIndexDto> indexCasesCq = cb.createQuery(CaseMergeIndexDto.class);
			Root<Case> indexRoot = indexCasesCq.from(Case.class);
			selectIndexDtoFields(new CaseQueryContext(cb, indexCasesCq, indexRoot));
			indexCasesCq.where(
				indexRoot.get(Case.ID).in(foundIds.stream().map(a -> Arrays.copyOf(a, 2)).flatMap(Arrays::stream).collect(Collectors.toSet())));
			Map<Long, CaseMergeIndexDto> indexCases =
				em.createQuery(indexCasesCq).getResultStream().collect(Collectors.toMap(c -> c.getId(), Function.identity()));

			for (Object[] idPair : foundIds) {
				// Abort the operation if the limit has been reached
				if (limit != null && resultIdsSet.size() >= limit) {
					break;
				}

				// Skip duplicate pairs - duplications always happen in reverse order, i.e. if idPair[0]/idPair[1]
				// is already in the result set in this order, the duplication would be added as idPair[1]/idPair[0]
				if (resultIdsSet.contains(new AbstractMap.SimpleImmutableEntry<>(idPair[1], idPair[0]))) {
					continue;
				}

				try {
					// Cloning is necessary here to allow us to add the same CaseIndexDto to the grid multiple times
					CaseMergeIndexDto parent = (CaseMergeIndexDto) indexCases.get(idPair[0]).clone();
					CaseMergeIndexDto child = (CaseMergeIndexDto) indexCases.get(idPair[1]).clone();

					if (parent.getCompleteness() == null && child.getCompleteness() == null
						|| parent.getCompleteness() != null
							&& (child.getCompleteness() == null || (parent.getCompleteness() >= child.getCompleteness()))) {
						resultList.add(
							new CaseMergeIndexDto[] {
								parent,
								child });
					} else {
						resultList.add(
							new CaseMergeIndexDto[] {
								child,
								parent });
					}
					resultIdsSet.add(new AbstractMap.SimpleImmutableEntry<>((Long) idPair[0], (Long) idPair[1]));
				} catch (CloneNotSupportedException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return resultList;
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateExternalData(List<ExternalDataDto> externalData) throws ExternalDataUpdateException {
		ExternalDataUtil.updateExternalData(externalData, this::getByUuids, this::ensurePersisted);
	}

	public void updateCompleteness(String caseUuid) {
		updateCompleteness(getByUuid(caseUuid));
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateCompleteness(List<String> caseUuids) {
		List<Case> casesForUpdate = getByUuids(caseUuids);
		casesForUpdate.forEach(this::updateCompleteness);
	}

	public void updateCompleteness(Case caze) {
		float completeness = calculateCompleteness(caze);

		/*
		 * Set the calculated value without updating the changeDate:
		 * 1. Do not trigger sync mechanisms that compare changeDates
		 * 2. Avoid optimistic locking problem with parallel running logic like batch imports
		 * Side effect: No AuditLogEntry is created triggered
		 */
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Case> cu = cb.createCriteriaUpdate(Case.class);
		Root<Case> root = cu.from(Case.class);
		cu.set(root.get(Case.COMPLETENESS), completeness);
		cu.where(cb.equal(root.get(Case.UUID), caze.getUuid()));
		em.createQuery(cu).executeUpdate();
	}

	public void clearCompleteness(Case caze) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Case> cu = cb.createCriteriaUpdate(Case.class);
		Root<Case> root = cu.from(Case.class);
		cu.set(root.get(Case.COMPLETENESS), (Float) null);
		cu.where(cb.equal(root.get(Case.UUID), caze.getUuid()));
		em.createQuery(cu).executeUpdate();
	}

	public PreviousCaseDto getMostRecentPreviousCase(String personUuid, Disease disease, Date startDate) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PreviousCaseDto> cq = cb.createQuery(PreviousCaseDto.class);
		Root<Case> root = cq.from(Case.class);
		Join<Case, Person> personJoin = root.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Symptoms> symptomsJoin = root.join(Case.SYMPTOMS, JoinType.LEFT);

		cq.multiselect(
			root.get(AbstractDomainObject.UUID),
			root.get(Case.REPORT_DATE),
			root.get(Case.EXTERNAL_TOKEN),
			root.get(Case.DISEASE_VARIANT),
			symptomsJoin.get(Symptoms.ONSET_DATE));

		cq.where(
			CriteriaBuilderHelper.and(
				cb,
				cb.equal(personJoin.get(AbstractDomainObject.UUID), personUuid),
				cb.equal(root.get(Case.DISEASE), disease),
				cb.isFalse(root.get(Case.DELETED)),
				cb.or(
					cb.lessThan(symptomsJoin.get(Symptoms.ONSET_DATE), startDate),
					cb.and(cb.isNull(symptomsJoin.get(Symptoms.ONSET_DATE)), cb.lessThan(root.get(Case.REPORT_DATE), startDate)))));
		cq.orderBy(cb.desc(symptomsJoin.get(Symptoms.ONSET_DATE)), cb.desc(root.get(Case.REPORT_DATE)));

		try {
			return em.createQuery(cq).setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public DataHelper.Pair<RegionReferenceDto, DistrictReferenceDto> getRegionAndDistrictRefsOf(CaseReferenceDto caze) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<Case> root = cq.from(Case.class);

		cq.multiselect(root.get(Case.RESPONSIBLE_REGION), root.get(Case.RESPONSIBLE_DISTRICT));
		cq.where(cb.equal(root.get(Case.UUID), caze.getUuid()));

		Tuple singleResult = em.createQuery(cq).getSingleResult();
		Region region = (Region) singleResult.get(0);
		District district = (District) singleResult.get(1);
		return new DataHelper.Pair<>(RegionFacadeEjb.toReferenceDto(region), DistrictFacadeEjb.toReferenceDto(district));
	}

	private List<Case> getCasesSetAsDuplicate(Long caseId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(Case.class);
		Root<Case> root = cq.from(Case.class);

		cq.where(cb.equal(root.get(Case.DUPLICATE_OF).get(Case.ID), caseId));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * Sets the vaccination status of all cases of the specified person and disease with vaccination date <= case start date.
	 * Vaccinations without a vaccination date are relevant for all cases.
	 * 
	 * @param personId
	 *            The ID of the case person
	 * @param disease
	 *            The disease of the cases
	 * @param vaccination
	 *            The created or updated vaccination
	 */
	public void updateVaccinationStatuses(Long personId, Disease disease, Vaccination vaccination) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Case> cu = cb.createCriteriaUpdate(Case.class);
		Root<Case> root = cu.from(Case.class);

		cu.set(root.get(Case.VACCINATION_STATUS), VaccinationStatus.VACCINATED);
		cu.set(root.get(AbstractDomainObject.CHANGE_DATE), new Date());

		Predicate datePredicate = vaccinationService.getRelevantVaccinationPredicate(root, cu, cb, vaccination);

		cu.where(
			CriteriaBuilderHelper
				.and(cb, cb.equal(root.get(Case.PERSON).get(Person.ID), personId), cb.equal(root.get(Case.DISEASE), disease), datePredicate));

		em.createQuery(cu).executeUpdate();
	}

	private float calculateCompleteness(Case caze) {

		float completeness = 0f;

		if (InvestigationStatus.DONE.equals(caze.getInvestigationStatus())) {
			completeness += 0.2f;
		}
		if (!CaseClassification.NOT_CLASSIFIED.equals(caze.getCaseClassification())) {
			completeness += 0.2f;
		}
		if (sampleService
			.exists((cb, root, cq) -> cb.and(sampleService.createDefaultFilter(cb, root), cb.equal(root.get(Sample.ASSOCIATED_CASE), caze)))) {
			completeness += 0.15f;
		}
		if (Boolean.TRUE.equals(caze.getSymptoms().getSymptomatic())) {
			completeness += 0.15f;
		}
		if (contactService.exists((cb, root, cq) -> cb.and(contactService.createDefaultFilter(cb, root), cb.equal(root.get(Contact.CAZE), caze)))) {
			completeness += 0.10f;
		}
		if (!CaseOutcome.NO_OUTCOME.equals(caze.getOutcome())) {
			completeness += 0.05f;
		}
		if (caze.getPerson().getBirthdateYYYY() != null || caze.getPerson().getApproximateAge() != null) {
			completeness += 0.05f;
		}
		if (caze.getPerson().getSex() != null) {
			completeness += 0.05f;
		}
		if (caze.getSymptoms().getOnsetDate() != null) {
			completeness += 0.05f;
		}

		return completeness;
	}

	private void selectIndexDtoFields(CaseQueryContext caseQueryContext) {
		CriteriaQuery cq = caseQueryContext.getQuery();
		cq.multiselect(listQueryBuilder.getCaseIndexSelections(caseQueryContext.getRoot(), caseQueryContext));
	}

	@Override
	protected boolean hasLimitedChangeDateFilterImplementation() {
		return true;
	}

	@Override
	protected String getDeleteReferenceField(DeletionReference deletionReference) {
		if (deletionReference == DeletionReference.REPORT) {
			return Case.REPORT_DATE;
		}

		return super.getDeleteReferenceField(deletionReference);
	}
}
