/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.backend.sample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityHelper;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.IsSample;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleExportDto;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleJurisdictionFlagsDto;
import de.symeda.sormas.api.sample.SampleListEntryDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SampleSimilarityCriteria;
import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.DtoCopyHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.NotificationService;
import de.symeda.sormas.backend.common.messaging.MessageContents;
import de.symeda.sormas.backend.common.messaging.MessageSubject;
import de.symeda.sormas.backend.common.messaging.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.sample.AdditionalTestFacadeEjb.AdditionalTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareInfoHelper;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SampleFacade")
@RightsAllowed(UserRight._SAMPLE_VIEW)
public class SampleFacadeEjb implements SampleFacade {

	private static final int DELETED_BATCH_SIZE = 1000;

	public static final String CONTACT_CASE_DISTRICT = "contactCaseDistrict";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String DISTRICT_NAME = "districtName";

	private static final int SIMILARITY_DATE_TIME_THRESHOLD = 2;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private SampleService sampleService;
	@EJB
	private AdditionalTestService additionalTestService;
	@EJB
	private AdditionalTestFacadeEjbLocal additionalTestFacade;
	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private EventParticipantFacadeEjbLocal eventParticipantFacade;
	@EJB
	private EventFacadeEjb.EventFacadeEjbLocal eventFacade;
	@EJB
	private NotificationService notificationService;
	@EJB
	private PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private SormasToSormasOriginInfoService originInfoService;

	@Override
	public List<String> getAllActiveUuids() {
		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return sampleService.getAllActiveUuids(user);
	}

	@Override
	public List<SampleDto> getAllActiveSamplesAfter(Date date) {
		return getAllActiveSamplesAfter(date, null, null);
	}

	@Override
	public List<SampleDto> getAllActiveSamplesAfter(Date date, Integer batchSize, String lastSynchronizedUuid) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return toPseudonymizedDtos(sampleService.getAllAfter(date, batchSize, lastSynchronizedUuid));
	}

	@Override
	public List<SampleDto> getByUuids(List<String> uuids) {
		return toPseudonymizedDtos(sampleService.getByUuids(uuids));
	}

	@Override
	public List<SampleDto> getByCaseUuids(List<String> caseUuids) {
		return toPseudonymizedDtos(sampleService.getByCaseUuids(caseUuids));
	}

	@Override
	public List<SampleDto> getByContactUuids(List<String> contactUuids) {
		return toPseudonymizedDtos(sampleService.getByContactUuids(contactUuids));
	}

	@Override
	public List<SampleDto> getSimilarSamples(SampleSimilarityCriteria criteria) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Sample> cq = cb.createQuery(Sample.class);
		final Root<Sample> root = cq.from(Sample.class);
		cq.distinct(true);

		SampleQueryContext sampleQueryContext = new SampleQueryContext(cb, cq, root);

		Predicate filter = sampleService.createUserFilter(sampleQueryContext, criteria.getSampleCriteria());
		filter = CriteriaBuilderHelper.and(cb, filter, sampleService.buildCriteriaFilter(criteria.getSampleCriteria(), sampleQueryContext));

		Predicate similarityFilter = null;
		if (criteria.getLabSampleId() != null) {
			similarityFilter = cb.equal(root.get(Sample.LAB_SAMPLE_ID), criteria.getLabSampleId());
		}

		Date sampleDateTime = criteria.getSampleDateTime();
		SampleMaterial sampleMaterial = criteria.getSampleMaterial();

		if (sampleDateTime != null && sampleMaterial != null) {
			Predicate dateAndMaterialFilter = cb.and(
				cb.between(
					root.get(Sample.SAMPLE_DATE_TIME),
					DateHelper.getStartOfDay(DateHelper.subtractDays(sampleDateTime, SIMILARITY_DATE_TIME_THRESHOLD)),
					DateHelper.getEndOfDay(DateHelper.addDays(sampleDateTime, SIMILARITY_DATE_TIME_THRESHOLD))),
				cb.equal(root.get(Sample.SAMPLE_MATERIAL), sampleMaterial));

			similarityFilter = CriteriaBuilderHelper.or(cb, similarityFilter, dateAndMaterialFilter);
		}

		filter = CriteriaBuilderHelper.and(cb, filter, similarityFilter);

		if (filter != null) {
			cq.where(filter);
		}

		return toPseudonymizedDtos(em.createQuery(cq).getResultList());
	}

	public List<SampleDto> getSamplesByCriteria(SampleCriteria criteria) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Sample> cq = cb.createQuery(Sample.class);
		final Root<Sample> root = cq.from(Sample.class);

		SampleQueryContext sampleQueryContext = new SampleQueryContext(cb, cq, root);

		Predicate filter = sampleService.createUserFilter(sampleQueryContext, criteria);
		filter = CriteriaBuilderHelper.and(cb, filter, sampleService.buildCriteriaFilter(criteria, sampleQueryContext));

		if (filter != null) {
			cq.where(filter);
		}

		List<Sample> samples = em.createQuery(cq).getResultList();

		return toPseudonymizedDtos(samples);
	}

	@Override
	public boolean exists(String uuid) {
		return sampleService.exists(uuid);
	}

	@Override
	public List<SampleDto> getByEventParticipantUuids(List<String> eventParticipantUuids) {
		return toPseudonymizedDtos(sampleService.getByEventParticipantUuids(eventParticipantUuids));
	}

	@Override
	public List<SampleDto> getByLabSampleId(String labSampleId) {

		if (labSampleId == null) {
			return new ArrayList<>();
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Sample> cq = cb.createQuery(Sample.class);
		final Root<Sample> sampleRoot = cq.from(Sample.class);
		final SampleQueryContext sampleQueryContext = new SampleQueryContext(cb, cq, sampleRoot);

		Predicate filter = CriteriaBuilderHelper.and(
			cb,
			sampleService.createUserFilter(sampleQueryContext, null),
			sampleService.createDefaultFilter(cb, sampleRoot),
			cb.equal(sampleRoot.get(Sample.LAB_SAMPLE_ID), labSampleId));
		cq.where(filter);

		return em.createQuery(cq).getResultList().stream().distinct().map(SampleFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	@RightsAllowed({
		UserRight._CONTACT_VIEW,
		UserRight._CASE_VIEW,
		UserRight._EVENTPARTICIPANT_VIEW })
	public Date getEarliestPositiveSampleDate(String contactUuid) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		final Root<Sample> sampleRoot = cq.from(Sample.class);

		cq.where(cb.equal(sampleRoot.get(Sample.PATHOGEN_TEST_RESULT), PathogenTestResultType.POSITIVE));
		cq.orderBy(cb.asc(sampleRoot.get(Sample.SAMPLE_DATE_TIME)));
		cq.select(sampleRoot.get(Sample.SAMPLE_DATE_TIME));

		try {
			return em.createQuery(cq).setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<DiseaseVariant> getAssociatedDiseaseVariants(String sampleUuid) {

		return sampleService.getAssociatedDiseaseVariants(sampleUuid);
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return sampleService.getDeletedUuidsSince(user, since);
	}

	@Override
	public List<String> getObsoleteUuidsSince(Date since) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return sampleService.getObsoleteUuidsSince(since);
	}

	@Override
	public SampleDto getSampleByUuid(String uuid) {
		Sample sample = sampleService.getByUuid(uuid);
		return convertToDto(sample, createPseudonymizer(sample));
	}

	@Override
	@RightsAllowed({
		UserRight._SAMPLE_CREATE,
		UserRight._SAMPLE_EDIT })
	public SampleDto saveSample(@Valid SampleDto dto) {
		return saveSample(dto, true, true, true);
	}

	@RightsAllowed({
		UserRight._SAMPLE_CREATE,
		UserRight._SAMPLE_EDIT })
	public SampleDto saveSample(@Valid SampleDto dto, boolean handleChanges, boolean checkChangeDate, boolean internal) {

		Sample existingSample = sampleService.getByUuid(dto.getUuid());
		FacadeHelper.checkCreateAndEditRights(existingSample, userService, UserRight.SAMPLE_CREATE, UserRight.SAMPLE_EDIT);

		if (internal && existingSample != null && !sampleService.isEditAllowed(existingSample)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorSampleNotEditable));
		}

		validate(dto, true);

		SampleDto existingSampleDto = toDto(existingSample);

		restorePseudonymizedDto(dto, existingSample, existingSampleDto);

		Sample sample = fillOrBuildEntity(dto, existingSample, checkChangeDate);

		// Set defaults for testing requests
		if (sample.getPathogenTestingRequested() == null) {
			sample.setPathogenTestingRequested(false);
		}
		if (sample.getAdditionalTestingRequested() == null) {
			sample.setAdditionalTestingRequested(false);
		}

		sampleService.ensurePersisted(sample);

		if (handleChanges) {
			onSampleChanged(existingSampleDto, sample, internal);
		}

		return toDto(sample);
	}

	@Override
	public SampleReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(sampleService.getByUuid(uuid));
	}

	@Override
	public List<SampleIndexDto> getIndexList(SampleCriteria sampleCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		return sampleService.getIndexList(sampleCriteria, first, max, sortProperties);
	}

	@Override
	public List<SampleListEntryDto> getEntriesList(SampleCriteria sampleCriteria, Integer first, Integer max) {
		return sampleService.getEntriesList(sampleCriteria, first, max);
	}

	public Page<SampleIndexDto> getIndexPage(SampleCriteria sampleCriteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<SampleIndexDto> sampleIndexList = getIndexList(sampleCriteria, offset, size, sortProperties);
		long totalElementCount = count(sampleCriteria);
		return new Page<>(sampleIndexList, offset, size, totalElementCount);
	}

	public List<SampleDto> getPositiveOrLatest(SampleCriteria criteria, Function<Sample, AbstractDomainObject> associatedObjectFn) {

		Collection<Sample> entities = sampleService.findBy(criteria, userService.getCurrentUser(), AbstractDomainObject.CREATION_DATE, false)
			.stream()
			.collect(Collectors.toMap(s -> associatedObjectFn.apply(s).getUuid(), s -> s, (s1, s2) -> {

				// keep the positive one
				if (s1.getPathogenTestResult() == PathogenTestResultType.POSITIVE) {
					return s1;
				} else if (s2.getPathogenTestResult() == PathogenTestResultType.POSITIVE) {
					return s2;
				}

				// ordered by creation date by default, so always keep the first one
				return s1;
			}))
			.values();

		return toPseudonymizedDtos(new ArrayList<>(entities));
	}

	@Override
	public void validate(@Valid SampleDto sample, boolean checkAssociatedEntities) throws ValidationRuntimeException {

		if (sample.getAssociatedCase() == null && sample.getAssociatedContact() == null && sample.getAssociatedEventParticipant() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validCaseContactOrEventParticipant));
		}
		if (sample.getSampleDateTime() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_DATE_TIME)));
		}
		if (sample.getReportDateTime() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.REPORT_DATE_TIME)));
		}
		if (sample.getSampleMaterial() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_MATERIAL)));
		}
		if (sample.getSamplePurpose() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_PURPOSE)));
		}
		if (sample.getSamplePurpose() == SamplePurpose.EXTERNAL && sample.getLab() == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.LAB)));
		}

		if (sample.getSampleMaterial() == SampleMaterial.OTHER && StringUtils.isEmpty(sample.getSampleMaterialText())) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.required,
					I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SAMPLE_MATERIAL_TEXT)));
		}

		if (checkAssociatedEntities) {
			validateSampleAssociatedEntities(sample);
		}

	}

	private void validateSampleAssociatedEntities(SampleDto sample) {
		if (sample.getAssociatedCase() != null && !caseService.exists(sample.getAssociatedCase().getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noCaseWithUuid));
		}
		if (sample.getAssociatedContact() != null && !contactService.exists(sample.getAssociatedContact().getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noContactWithUuid));
		}
		if (sample.getAssociatedEventParticipant() != null && !eventParticipantService.exists(sample.getAssociatedEventParticipant().getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noEventParticipantWithUuid));
		}
		if (sample.getLab() != null && !facilityService.exists(sample.getLab().getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noLaboratoryWithUuid));
		}
		if (sample.getReportingUser() != null && !userService.exists(sample.getReportingUser().getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.noReportingUserWithUuid));
		}
	}

	private List<SampleExportDto> getExportList(
		SampleCriteria sampleCriteria,
		CaseCriteria caseCriteria,
		Collection<String> selectedRows,
		int first,
		int max) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SampleExportDto> cq = cb.createQuery(SampleExportDto.class);
		Root<Sample> sampleRoot = cq.from(Sample.class);

		SampleQueryContext sampleQueryContext = new SampleQueryContext(cb, cq, sampleRoot);
		SampleJoins joins = sampleQueryContext.getJoins();

		cq.distinct(true);

		List<Selection<?>> selections = new ArrayList<>();

		Selection<?>[] tmp = new Selection<?>[] {
			sampleRoot.get(AbstractDomainObject.ID),
			sampleRoot.get(AbstractDomainObject.UUID),
			sampleRoot.get(Sample.LAB_SAMPLE_ID),
			sampleRoot.get(Sample.REPORT_DATE_TIME),
			joins.getCaze().get(Case.EPID_NUMBER),
			joins.getCasePerson().get(Person.FIRST_NAME),
			joins.getCasePerson().get(Person.LAST_NAME),
			joins.getContactPerson().get(Person.FIRST_NAME),
			joins.getContactPerson().get(Person.LAST_NAME),
			joins.getEventParticipantPerson().get(Person.FIRST_NAME),
			joins.getEventParticipantPerson().get(Person.LAST_NAME),
			joins.getCaze().get(Case.DISEASE),
			joins.getCaze().get(Case.DISEASE_DETAILS),
			joins.getContact().get(Contact.DISEASE),
			joins.getContact().get(Contact.DISEASE_DETAILS),
			joins.getEvent().get(Event.DISEASE),
			joins.getEvent().get(Event.DISEASE_DETAILS),
			sampleRoot.get(Sample.SAMPLE_DATE_TIME),
			sampleRoot.get(Sample.SAMPLE_MATERIAL),
			sampleRoot.get(Sample.SAMPLE_MATERIAL_TEXT),
			sampleRoot.get(Sample.SAMPLE_PURPOSE),
			sampleRoot.get(Sample.SAMPLING_REASON),
			sampleRoot.get(Sample.SAMPLING_REASON_DETAILS),
			sampleRoot.get(Sample.SAMPLE_SOURCE),
			joins.getLab().get(Facility.NAME),
			sampleRoot.get(Sample.LAB_DETAILS),
			sampleRoot.get(Sample.PATHOGEN_TEST_RESULT),
			sampleRoot.get(Sample.PATHOGEN_TESTING_REQUESTED),
			sampleRoot.get(Sample.REQUESTED_PATHOGEN_TESTS_STRING),
			sampleRoot.get(Sample.REQUESTED_OTHER_PATHOGEN_TESTS),
			sampleRoot.get(Sample.ADDITIONAL_TESTING_REQUESTED),
			sampleRoot.get(Sample.REQUESTED_ADDITIONAL_TESTS_STRING),
			sampleRoot.get(Sample.REQUESTED_OTHER_ADDITIONAL_TESTS),
			sampleRoot.get(Sample.SHIPPED),
			sampleRoot.get(Sample.SHIPMENT_DATE),
			sampleRoot.get(Sample.SHIPMENT_DETAILS),
			sampleRoot.get(Sample.RECEIVED),
			sampleRoot.get(Sample.RECEIVED_DATE),
			sampleRoot.get(Sample.SPECIMEN_CONDITION),
			sampleRoot.get(Sample.NO_TEST_POSSIBLE_REASON),
			sampleRoot.get(Sample.COMMENT),
			joins.getReferredSample().get(AbstractDomainObject.UUID),
			joins.getCaze().get(AbstractDomainObject.UUID),
			joins.getContact().get(AbstractDomainObject.UUID),
			joins.getEventParticipant().get(AbstractDomainObject.UUID),
			joins.getCasePerson().get(Person.APPROXIMATE_AGE),
			joins.getCasePerson().get(Person.APPROXIMATE_AGE_TYPE),
			joins.getCasePerson().get(Person.SEX),
			joins.getContactPerson().get(Person.APPROXIMATE_AGE),
			joins.getContactPerson().get(Person.APPROXIMATE_AGE_TYPE),
			joins.getContactPerson().get(Person.SEX),
			joins.getEventParticipantPerson().get(Person.APPROXIMATE_AGE),
			joins.getEventParticipantPerson().get(Person.APPROXIMATE_AGE_TYPE),
			joins.getEventParticipantPerson().get(Person.SEX),
			joins.getCasePersonAddressRegion().get(Region.NAME),
			joins.getCasePersonAddressDistrict().get(District.NAME),
			joins.getCasePersonAddressCommunity().get(Community.NAME),
			joins.getCasePersonAddress().get(Location.CITY),
			joins.getCasePersonAddress().get(Location.STREET),
			joins.getCasePersonAddress().get(Location.HOUSE_NUMBER),
			joins.getCasePersonAddress().get(Location.ADDITIONAL_INFORMATION),
			joins.getContactPersonAddressRegion().get(Region.NAME),
			joins.getContactPersonAddressDistrict().get(District.NAME),
			joins.getContactPersonAddressCommunity().get(Community.NAME),
			joins.getContactPersonAddress().get(Location.CITY),
			joins.getContactPersonAddress().get(Location.STREET),
			joins.getContactPersonAddress().get(Location.HOUSE_NUMBER),
			joins.getContactPersonAddress().get(Location.ADDITIONAL_INFORMATION),
			joins.getEventRegion().get(Region.NAME),
			joins.getEventDistrict().get(District.NAME),
			joins.getEventCommunity().get(Community.NAME),
			joins.getEventLocation().get(Location.CITY),
			joins.getEventLocation().get(Location.STREET),
			joins.getEventLocation().get(Location.HOUSE_NUMBER),
			joins.getEventLocation().get(Location.ADDITIONAL_INFORMATION),
			joins.getCaze().get(Case.REPORT_DATE),
			joins.getCaze().get(Case.CASE_CLASSIFICATION),
			joins.getCaze().get(Case.OUTCOME),
			joins.getCaseRegion().get(Region.NAME),
			joins.getCaseDistrict().get(District.NAME),
			joins.getCaseCommunity().get(Community.NAME),
			joins.getCaseFacility().get(Facility.NAME),
			joins.getCaze().get(Case.HEALTH_FACILITY_DETAILS),
			joins.getContactRegion().get(Region.NAME),
			joins.getContactDistrict().get(District.NAME),
			joins.getContactCommunity().get(Community.NAME),
			joins.getContact().get(Contact.REPORT_DATE_TIME),
			joins.getContact().get(Contact.LAST_CONTACT_DATE),
			joins.getContact().get(Contact.CONTACT_CLASSIFICATION),
			joins.getContact().get(Contact.CONTACT_STATUS),
			joins.getEventParticipantRegion().get(Region.NAME),
			joins.getEventParticipantDistrict().get(District.NAME),
			joins.getLab().get(AbstractDomainObject.UUID),
			joins.getCaseFacility().get(AbstractDomainObject.UUID),
			joins.getCaseResponsibleRegion().get(Region.NAME),
			joins.getCaseResponsibleDistrict().get(District.NAME),
			joins.getCaseResponsibleCommunity().get(Community.NAME) };

		Collections.addAll(selections, tmp);
		selections.addAll(sampleService.getJurisdictionSelections(sampleQueryContext));

		cq.multiselect(selections);

		Predicate filter = sampleService.createUserFilter(sampleQueryContext, sampleCriteria);

		if (sampleCriteria != null) {
			Predicate criteriaFilter = sampleService.buildCriteriaFilter(sampleCriteria, sampleQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
			filter = CriteriaBuilderHelper.andInValues(selectedRows, filter, cb, sampleRoot.get(AbstractDomainObject.UUID));
		} else if (caseCriteria != null) {
			Predicate criteriaFilter = caseService.createCriteriaFilter(caseCriteria, new CaseQueryContext(cb, cq, joins.getCaseJoins()));
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(sampleRoot.get(CoreAdo.DELETED)));
			filter = CriteriaBuilderHelper.andInValues(selectedRows, filter, cb, joins.getCaze().get(AbstractDomainObject.UUID));
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.desc(sampleRoot.get(Sample.REPORT_DATE_TIME)), cb.desc(sampleRoot.get(AbstractDomainObject.ID)));

		List<SampleExportDto> resultList = QueryHelper.getResultList(em, cq, first, max);

		SamplePseudonymizer<SampleExportDto> pseudonymizer = createPseudonymizer(true, resultList);

		for (SampleExportDto exportDto : resultList) {
			Sample sampleFromExportDto = sampleService.getById(exportDto.getId());
			List<PathogenTest> pathogenTests = sampleFromExportDto.getPathogenTests();
			int count = 0;
			for (PathogenTest pathogenTest : pathogenTests) {
				String lab = pathogenTest.getLab() != null
					? FacilityHelper
						.buildFacilityString(pathogenTest.getLab().getUuid(), pathogenTest.getLab().getName(), pathogenTest.getLabDetails())
					: null;
				SampleExportDto.SampleExportPathogenTest sampleExportPathogenTest = new SampleExportDto.SampleExportPathogenTest(
					pathogenTest.getTestType(),
					pathogenTest.getTestTypeText(),
					DiseaseHelper.toString(pathogenTest.getTestedDisease(), pathogenTest.getTestedDiseaseDetails()),
					pathogenTest.getTestDateTime(),
					lab,
					pathogenTest.getTestResult(),
					pathogenTest.getTestResultVerified());

				switch (++count) {
				case 1:
					exportDto.setPathogenTest1(sampleExportPathogenTest);
					break;
				case 2:
					exportDto.setPathogenTest2(sampleExportPathogenTest);
					break;
				case 3:
					exportDto.setPathogenTest3(sampleExportPathogenTest);
					break;
				default:
					exportDto.addOtherPathogenTest(sampleExportPathogenTest);
					break;
				}
			}

			if (Boolean.TRUE.equals(exportDto.getAdditionalTestingRequested())) {
				List<AdditionalTest> additionalTests = additionalTestService.getAllBySample(sampleFromExportDto);
				if (!additionalTests.isEmpty()) {
					exportDto.setAdditionalTest(AdditionalTestFacadeEjb.toDto(additionalTests.get(0)));
				}
				if (additionalTests.size() > 1) {
					exportDto.setOtherAdditionalTestsDetails(I18nProperties.getString(Strings.yes));
				} else {
					exportDto.setOtherAdditionalTestsDetails(I18nProperties.getString(Strings.no));
				}
			} else {
				exportDto.setOtherAdditionalTestsDetails(I18nProperties.getString(Strings.no));
			}

			boolean isInJurisdiction = exportDto.getSampleJurisdictionFlagsDto().getInJurisdiction();
			pseudonymizer.pseudonymizeDto(
				SampleExportDto.class,
				exportDto,
				isInJurisdiction,
				s -> pseudonymizer.pseudonymizeEmbeddedDtoCollection(
					SampleExportDto.SampleExportPathogenTest.class,
					exportDto.getOtherPathogenTests(),
					isInJurisdiction,
					exportDto));
		}

		return resultList;
	}

	@Override
	@RightsAllowed(UserRight._SAMPLE_EXPORT)
	public List<SampleExportDto> getExportList(SampleCriteria criteria, Collection<String> selectedRows, int first, int max) {
		return getExportList(criteria, null, selectedRows, first, max);
	}

	@Override
	@RightsAllowed(UserRight._SAMPLE_EXPORT)
	public List<SampleExportDto> getExportList(CaseCriteria criteria, Collection<String> selectedRows, int first, int max) {
		return getExportList(null, criteria, selectedRows, first, max);
	}

	@Override
	public long count(SampleCriteria sampleCriteria) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<Sample> root = cq.from(Sample.class);

		SampleQueryContext sampleQueryContext = new SampleQueryContext(cb, cq, root);
		Predicate filter = sampleService.createUserFilter(sampleQueryContext, sampleCriteria);
		if (sampleCriteria != null) {
			Predicate criteriaFilter = sampleService.buildCriteriaFilter(sampleCriteria, sampleQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public SampleReferenceDto getReferredFrom(String sampleUuid) {
		return toReferenceDto(sampleService.getReferredFrom(sampleUuid));
	}

	@Override
	@RightsAllowed(UserRight._SAMPLE_DELETE)
	public void delete(String sampleUuid, DeletionDetails deletionDetails) {
		Sample sample = sampleService.getByUuid(sampleUuid);
		sampleService.delete(sample, deletionDetails);

		handleAssociatedEntityChanges(sample, true);
	}

	@Override
	@RightsAllowed(UserRight._SAMPLE_DELETE)
	public List<ProcessedEntity> restore(List<String> uuids) {
		List<ProcessedEntity> processedSamples = new ArrayList<>();
		List<Sample> samplesToBeRestored = sampleService.getByUuids(uuids);

		if (samplesToBeRestored != null) {
			samplesToBeRestored.forEach(sampleToBeRestored -> {
				try {
					restore(sampleToBeRestored.getUuid());
					processedSamples.add(new ProcessedEntity(sampleToBeRestored.getUuid(), ProcessedEntityStatus.SUCCESS));
				} catch (Exception e) {
					processedSamples.add(new ProcessedEntity(sampleToBeRestored.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
					logger.error("The sample with uuid {} could not be restored due to an Exception", sampleToBeRestored.getUuid(), e);
				}
			});
		}
		return processedSamples;
	}

	@Override
	@RightsAllowed(UserRight._SAMPLE_DELETE)
	public void restore(String sampleUuid) {
		Sample sample = sampleService.getByUuid(sampleUuid);
		sampleService.restore(sample);
	}

	@Override
	@RightsAllowed(UserRight._SAMPLE_DELETE)
	public List<ProcessedEntity> delete(List<String> sampleUuids, DeletionDetails deletionDetails) {
		long startTime = DateHelper.startTime();

		List<ProcessedEntity> processedSamples = new ArrayList<>();
		IterableHelper.executeBatched(
			sampleUuids,
			DELETED_BATCH_SIZE,
			batchedSampleUuids -> processedSamples.addAll(sampleService.deleteAll(batchedSampleUuids, deletionDetails)));
		logger.debug("deleteAllSamples(sampleUuids) finished. samplesCount = {}, {}ms", sampleUuids.size(), DateHelper.durationMillies(startTime));

		return processedSamples;
	}

	@RightsAllowed(UserRight._SAMPLE_DELETE)
	public List<String> deleteSamples(List<String> sampleUuids, DeletionDetails deletionDetails) {
		List<String> deletedSampleUuids = new ArrayList<>();
		List<Sample> samplesToBeDeleted = sampleService.getByUuids(sampleUuids);
		if (samplesToBeDeleted != null) {
			samplesToBeDeleted.forEach(sampleToBeDeleted -> {
				if (!sampleToBeDeleted.isDeleted()) {
					sampleService.delete(sampleToBeDeleted, deletionDetails);
					deletedSampleUuids.add(sampleToBeDeleted.getUuid());
				}
			});
		}
		return deletedSampleUuids;
	}

	public Sample fillOrBuildEntity(@NotNull SampleDto source, Sample target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, Sample::new, checkChangeDate);

		target.setAssociatedCase(caseService.getByReferenceDto(source.getAssociatedCase()));
		target.setAssociatedContact(contactService.getByReferenceDto(source.getAssociatedContact()));
		target.setAssociatedEventParticipant(eventParticipantService.getByReferenceDto(source.getAssociatedEventParticipant()));
		target.setLabSampleID(source.getLabSampleID());
		target.setFieldSampleID(source.getFieldSampleID());
		target.setSampleDateTime(source.getSampleDateTime());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleMaterialText(source.getSampleMaterialText());
		target.setSamplePurpose(source.getSamplePurpose());
		target.setLab(facilityService.getByReferenceDto(source.getLab()));
		target.setLabDetails(source.getLabDetails());
		target.setShipmentDate(source.getShipmentDate());
		target.setShipmentDetails(source.getShipmentDetails());
		target.setReceivedDate(source.getReceivedDate());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setNoTestPossibleReason(source.getNoTestPossibleReason());
		target.setComment(source.getComment());
		target.setSampleSource(source.getSampleSource());
		target.setReferredTo(sampleService.getByReferenceDto(source.getReferredTo()));
		target.setShipped(source.isShipped());
		target.setReceived(source.isReceived());
		target.setPathogenTestingRequested(source.getPathogenTestingRequested());
		target.setAdditionalTestingRequested(source.getAdditionalTestingRequested());
		target.setRequestedPathogenTests(source.getRequestedPathogenTests());
		target.setRequestedAdditionalTests(source.getRequestedAdditionalTests());
		target.setPathogenTestResult(source.getPathogenTestResult());
		target.setRequestedOtherPathogenTests(source.getRequestedOtherPathogenTests());
		target.setRequestedOtherAdditionalTests(source.getRequestedOtherAdditionalTests());
		target.setSamplingReason(source.getSamplingReason());
		target.setSamplingReasonDetails(source.getSamplingReasonDetails());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		if (source.getSormasToSormasOriginInfo() != null) {
			target.setSormasToSormasOriginInfo(originInfoService.getByUuid(source.getSormasToSormasOriginInfo().getUuid()));
		}

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	public SampleDto convertToDto(Sample source, SamplePseudonymizer<SampleDto> pseudonymizers) {

		if (source == null) {
			return null;
		}

		return convertToDto(source, pseudonymizers, sampleService.getJurisdictionFlags(source));
	}

	private SampleDto convertToDto(Sample source, SamplePseudonymizer<SampleDto> pseudonymizers, SampleJurisdictionFlagsDto jurisdictionFlags) {

		SampleDto dto = toDto(source);
		pseudonymizeDto(source, dto, pseudonymizers, jurisdictionFlags);

		return dto;
	}

	private List<SampleDto> toPseudonymizedDtos(List<Sample> entities) {

		Map<Long, SampleJurisdictionFlagsDto> jurisdictionFlags = sampleService.getJurisdictionsFlags(entities);
		SamplePseudonymizer<SampleDto> pseudonymizer = createPseudonymizer(false, entities);

		return entities.stream().map(p -> convertToDto(p, pseudonymizer, jurisdictionFlags.get(p.getId()))).collect(Collectors.toList());
	}

	private SamplePseudonymizer<SampleDto> createPseudonymizer(IsSample sample) {
		return createPseudonymizer(false, sample != null ? Collections.singleton(sample) : Collections.emptyList());
	}

	private <T extends IsSample> SamplePseudonymizer<T> createPseudonymizer(boolean withPlaceHolder, Collection<? extends IsSample> samples) {
		return sampleService.createPseudonymizer(withPlaceHolder, samples);
	}

	private void pseudonymizeDto(
		Sample source,
		SampleDto dto,
		SamplePseudonymizer<SampleDto> pseudonymizer,
		SampleJurisdictionFlagsDto jurisdictionFlags) {

		if (dto != null) {
			User currentUser = userService.getCurrentUser();

			pseudonymizer.pseudonymizeDto(SampleDto.class, dto, jurisdictionFlags.getInJurisdiction(), s -> {
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), currentUser, s::setReportingUser, s);
				pseudonymizeAssociatedObjects(
					s.getAssociatedCase(),
					s.getAssociatedContact(),
					s.getAssociatedEventParticipant(),
					pseudonymizer,
					jurisdictionFlags);
			});
		}
	}

	private void restorePseudonymizedDto(SampleDto dto, Sample existingSample, SampleDto existingSampleDto) {

		if (existingSampleDto != null) {
			boolean inJurisdiction = sampleService.getJurisdictionFlags(existingSample).getInJurisdiction();
			User currentUser = userService.getCurrentUser();

			SamplePseudonymizer<SampleDto> pseudonymizer = createPseudonymizer(existingSample);
			pseudonymizer.restoreUser(existingSample.getReportingUser(), currentUser, dto, dto::setReportingUser);
			pseudonymizer.restorePseudonymizedValues(SampleDto.class, dto, existingSampleDto, inJurisdiction);
		}
	}

	private void pseudonymizeAssociatedObjects(
		CaseReferenceDto sampleCase,
		ContactReferenceDto sampleContact,
		EventParticipantReferenceDto sampleEventParticipant,
		SamplePseudonymizer<SampleDto> pseudonymizer,
		SampleJurisdictionFlagsDto jurisdictionFlagsDto) {

		if (sampleCase != null) {
			pseudonymizer.pseudonymizeCaseReference(sampleCase, jurisdictionFlagsDto.getCaseInJurisdiction());
		}

		if (sampleContact != null) {
			pseudonymizer.pseudonymizeAssociatedDto(
				ContactReferenceDto.PersonName.class,
				sampleContact.getContactName(),
				jurisdictionFlagsDto.getContactInJurisdiction());

			if (sampleContact.getCaze() != null) {
				pseudonymizer.pseudonymizeCaseReference(sampleContact.getCaze(), jurisdictionFlagsDto.getContactCaseInJurisdiction());
			}
		}

		if (sampleEventParticipant != null) {
			pseudonymizer.pseudonymizeAssociatedDto(
				EventParticipantReferenceDto.class,
				sampleEventParticipant,
				jurisdictionFlagsDto.getEvenParticipantInJurisdiction());
		}
	}

	public static SampleDto toDto(Sample source) {

		if (source == null) {
			return null;
		}

		SampleDto target = new SampleDto();
		DtoHelper.fillDto(target, source);

		target.setAssociatedCase(CaseFacadeEjb.toReferenceDto(source.getAssociatedCase()));
		target.setAssociatedContact(ContactFacadeEjb.toReferenceDto(source.getAssociatedContact()));
		target.setAssociatedEventParticipant(EventParticipantFacadeEjb.toReferenceDto(source.getAssociatedEventParticipant()));
		target.setLabSampleID(source.getLabSampleID());
		target.setFieldSampleID(source.getFieldSampleID());
		target.setSampleDateTime(source.getSampleDateTime());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleMaterialText(source.getSampleMaterialText());
		target.setSamplePurpose(source.getSamplePurpose());
		target.setLab(FacilityFacadeEjb.toReferenceDto(source.getLab()));
		target.setLabDetails(source.getLabDetails());
		target.setShipmentDate(source.getShipmentDate());
		target.setShipmentDetails(source.getShipmentDetails());
		target.setReceivedDate(source.getReceivedDate());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setNoTestPossibleReason(source.getNoTestPossibleReason());
		target.setComment(source.getComment());
		target.setSampleSource(source.getSampleSource());
		target.setReferredTo(SampleFacadeEjb.toReferenceDto(source.getReferredTo()));
		target.setShipped(source.isShipped());
		target.setReceived(source.isReceived());
		target.setPathogenTestingRequested(source.getPathogenTestingRequested());
		target.setAdditionalTestingRequested(source.getAdditionalTestingRequested());
		target.setRequestedPathogenTests(source.getRequestedPathogenTests());
		target.setRequestedAdditionalTests(source.getRequestedAdditionalTests());
		target.setPathogenTestResult(source.getPathogenTestResult());
		target.setRequestedOtherPathogenTests(source.getRequestedOtherPathogenTests());
		target.setRequestedOtherAdditionalTests(source.getRequestedOtherAdditionalTests());
		target.setSamplingReason(source.getSamplingReason());
		target.setSamplingReasonDetails(source.getSamplingReasonDetails());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		target.setSormasToSormasOriginInfo(SormasToSormasOriginInfoFacadeEjb.toDto(source.getSormasToSormasOriginInfo()));
		target.setOwnershipHandedOver(source.getSormasToSormasShares().stream().anyMatch(ShareInfoHelper::isOwnerShipHandedOver));

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	public static SampleReferenceDto toReferenceDto(Sample entity) {

		if (entity == null) {
			return null;
		}

		return new SampleReferenceDto(
			entity.getUuid(),
			entity.getSampleMaterial(),
			entity.getAssociatedCase() != null ? entity.getAssociatedCase().getUuid() : null,
			entity.getAssociatedContact() != null ? entity.getAssociatedContact().getUuid() : null,
			entity.getAssociatedEventParticipant() != null ? entity.getAssociatedEventParticipant().getUuid() : null);
	}

	private void onSampleChanged(SampleDto existingSample, Sample newSample, boolean syncShares) {
		// Change pathogenTestResultChangeDate if the pathogen test result has changed
		if (existingSample != null
			&& existingSample.getPathogenTestResult() != null
			&& existingSample.getPathogenTestResult() != newSample.getPathogenTestResult()) {
			Date latestPathogenTestDate = pathogenTestFacade.getLatestPathogenTestDate(newSample.getUuid());
			if (latestPathogenTestDate != null) {
				newSample.setPathogenTestResultChangeDate(latestPathogenTestDate);
			}
		}

		handleAssociatedEntityChanges(newSample, syncShares);

		// Send an email to the lab user when a sample has been shipped to their lab
		if (newSample.isShipped()
			&& (existingSample == null || !existingSample.isShipped())
			&& !StringUtils.equals(newSample.getLab().getUuid(), FacilityDto.OTHER_FACILITY_UUID)) {
			try {
				final String messageContent = getSampleShippedNotificationMessage(newSample);
				notificationService.sendNotifications(
					NotificationType.LAB_SAMPLE_SHIPPED,
					MessageSubject.LAB_SAMPLE_SHIPPED,
					() -> userService.getLabUsersOfLab(newSample.getLab())
						.stream()
						.collect(Collectors.toMap(Function.identity(), (e) -> messageContent)));
			} catch (NotificationDeliveryFailedException e) {
				logger.error("EmailDeliveryFailedException when trying to notify supervisors about " + "the shipment of a lab sample.");
			}
		}
	}

	private String getSampleShippedNotificationMessage(Sample newSample) {
		final String messageContent;

		if (newSample.getAssociatedCase() != null) {
			messageContent = String.format(
				I18nProperties.getString(MessageContents.CONTENT_LAB_SAMPLE_SHIPPED_SHORT),
				DataHelper.getShortUuid(newSample.getAssociatedCase().getUuid()));
		} else if (newSample.getAssociatedContact() != null) {
			messageContent = String.format(
				I18nProperties.getString(MessageContents.CONTENT_LAB_SAMPLE_SHIPPED_SHORT_FOR_CONTACT),
				DataHelper.getShortUuid(newSample.getAssociatedContact().getUuid()));
		} else if (newSample.getAssociatedEventParticipant() != null) {
			messageContent = String.format(
				I18nProperties.getString(MessageContents.CONTENT_LAB_SAMPLE_SHIPPED_SHORT_FOR_EVENT_PARTICIPANT),
				DataHelper.getShortUuid(newSample.getAssociatedEventParticipant().getUuid()));
		} else {
			messageContent = null;
		}

		return messageContent;
	}

	private void handleAssociatedEntityChanges(Sample newSample, boolean syncShares) {

		if (newSample.getAssociatedCase() != null) {
			caseFacade.onCaseSampleChanged(newSample.getAssociatedCase(), syncShares);
		}

		if (newSample.getAssociatedContact() != null && userService.hasRight(UserRight.CONTACT_EDIT)) {
			contactService.updateFollowUpDetails(newSample.getAssociatedContact(), false);
			contactFacade.onContactChanged(contactFacade.toDto(newSample.getAssociatedContact()), newSample.getAssociatedContact(), syncShares);
		}

		EventParticipant associatedEventParticipant = newSample.getAssociatedEventParticipant();
		if (associatedEventParticipant != null && userService.hasRight(UserRight.EVENTPARTICIPANT_EDIT)) {
			eventParticipantFacade.onEventParticipantChanged(
				eventFacade.toDto(associatedEventParticipant.getEvent()),
				eventParticipantFacade.toDto(associatedEventParticipant),
				associatedEventParticipant,
				syncShares);
		}
	}

	@Override
	public boolean isDeleted(String sampleUuid) {
		return sampleService.isDeleted(sampleUuid);
	}

	@Override
	public Boolean isEditAllowed(String uuid) {
		Sample sample = sampleService.getByUuid(uuid);
		return sampleService.isEditAllowed(sample);
	}

	@RightsAllowed({
		UserRight._SAMPLE_CREATE,
		UserRight._CASE_CREATE })
	public void cloneSampleForCase(Sample sample, Case caze) {
		SampleDto newSample = SampleDto.build(sample.getReportingUser().toReference(), caze.toReference());
		DtoCopyHelper.copyDtoValues(newSample, SampleFacadeEjb.toDto(sample), true);
		newSample.setAssociatedCase(caze.toReference());
		newSample.setAssociatedContact(null);
		newSample.setAssociatedEventParticipant(null);
		saveSample(newSample, false, true, true);

		for (PathogenTest pathogenTest : sample.getPathogenTests()) {
			PathogenTestDto newPathogenTest = PathogenTestDto.build(newSample.toReference(), pathogenTest.getLabUser().toReference());
			DtoCopyHelper.copyDtoValues(newPathogenTest, PathogenTestFacadeEjbLocal.toDto(pathogenTest), true);
			pathogenTestFacade.savePathogenTest(newPathogenTest);
		}

		for (AdditionalTest additionalTest : sample.getAdditionalTests()) {
			AdditionalTestDto newAdditionalTest = AdditionalTestDto.build(newSample.toReference());
			DtoCopyHelper.copyDtoValues(newAdditionalTest, AdditionalTestFacadeEjbLocal.toDto(additionalTest), true);
			additionalTestFacade.saveAdditionalTest(newAdditionalTest);
		}
	}

	@LocalBean
	@Stateless
	public static class SampleFacadeEjbLocal extends SampleFacadeEjb {

	}
}
