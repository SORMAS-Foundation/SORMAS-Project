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
package de.symeda.sormas.backend.sample;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleJurisdictionFlagsDto;
import de.symeda.sormas.api.sample.SampleListEntryDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDeletableAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.DeletableAdo;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactJoins;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantQueryContext;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.labmessage.LabMessageService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.sample.transformers.SampleListEntryDtoResultTransformer;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoFacadeEjb.SormasToSormasShareInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class SampleService extends AbstractDeletableAdoService<Sample> {

	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;
	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;
	@EJB
	private ContactService contactService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private PathogenTestService pathogenTestService;
	@EJB
	private AdditionalTestService additionalTestService;
	@EJB
	private SormasToSormasShareInfoFacadeEjbLocal sormasToSormasShareInfoFacade;
	@EJB
	private SormasToSormasShareInfoService sormasToSormasShareInfoService;
	@EJB
	private LabMessageService labMessageService;

	public SampleService() {
		super(Sample.class);
	}

	public List<Sample> findBy(SampleCriteria criteria, User user, String sortProperty, boolean ascending) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(getElementClass());
		Root<Sample> from = cq.from(getElementClass());
		SampleJoins joins = new SampleJoins(from);

		Predicate filter = buildCriteriaFilter(criteria, cb, joins);

		if (user != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, from));
		}
		if (filter != null) {
			cq.where(filter);
		}

		if (ascending) {
			cq.orderBy(cb.asc(from.get(sortProperty)));
		} else {
			cq.orderBy(cb.desc(from.get(sortProperty)));
		}

		return em.createQuery(cq).getResultList();
	}

	public List<Sample> findBy(SampleCriteria criteria, User user) {
		return findBy(criteria, user, Sample.CREATION_DATE, true);
	}

	public List<SampleIndexDto> getIndexList(SampleCriteria sampleCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<SampleIndexDto> cq = cb.createQuery(SampleIndexDto.class);
		final Root<Sample> sample = cq.from(Sample.class);

		SampleQueryContext sampleQueryContext = new SampleQueryContext(cb, cq, sample);
		SampleJoins joins = sampleQueryContext.getJoins();

		final Join<Sample, Case> caze = joins.getCaze();
		final Join<Case, District> caseDistrict = joins.getCaseDistrict();

		final Join<Sample, Contact> contact = joins.getContact();
		final Join<Contact, District> contactDistrict = joins.getContactDistrict();
		final Join<Case, District> contactCaseDistrict = joins.getContactCaseDistrict();

		final Join<EventParticipant, Event> event = joins.getEvent();
		final Join<Location, District> eventDistrict = joins.getEventDistrict();

		Expression<Object> diseaseSelect = cb.selectCase()
			.when(cb.isNotNull(caze), caze.get(Case.DISEASE))
			.otherwise(cb.selectCase().when(cb.isNotNull(contact), contact.get(Contact.DISEASE)).otherwise(event.get(Event.DISEASE)));
		Expression<Object> diseaseDetailsSelect = cb.selectCase()
			.when(cb.isNotNull(caze), caze.get(Case.DISEASE_DETAILS))
			.otherwise(cb.selectCase().when(cb.isNotNull(contact), contact.get(Contact.DISEASE_DETAILS)).otherwise(event.get(Event.DISEASE_DETAILS)));

		Expression<Object> districtSelect = cb.selectCase()
			.when(cb.isNotNull(caseDistrict), caseDistrict.get(District.NAME))
			.otherwise(
				cb.selectCase()
					.when(cb.isNotNull(contactDistrict), contactDistrict.get(District.NAME))
					.otherwise(
						cb.selectCase()
							.when(cb.isNotNull(contactCaseDistrict), contactCaseDistrict.get(District.NAME))
							.otherwise(eventDistrict.get(District.NAME))));

		cq.distinct(true);

		List<Selection<?>> selections = new ArrayList<>(
			Arrays.asList(
				sample.get(Sample.UUID),
				caze.get(Case.EPID_NUMBER),
				sample.get(Sample.LAB_SAMPLE_ID),
				sample.get(Sample.SAMPLE_DATE_TIME),
				sample.get(Sample.SHIPPED),
				sample.get(Sample.SHIPMENT_DATE),
				sample.get(Sample.RECEIVED),
				sample.get(Sample.RECEIVED_DATE),
				sample.get(Sample.SAMPLE_MATERIAL),
				sample.get(Sample.SAMPLE_PURPOSE),
				sample.get(Sample.SPECIMEN_CONDITION),
				joins.getLab().get(Facility.NAME),
				joins.getReferredSample().get(Sample.UUID),
				sample.get(Sample.SAMPLING_REASON),
				sample.get(Sample.SAMPLING_REASON_DETAILS),
				caze.get(Case.UUID),
				joins.getCasePerson().get(Person.FIRST_NAME),
				joins.getCasePerson().get(Person.LAST_NAME),
				joins.getContact().get(Contact.UUID),
				joins.getContactPerson().get(Person.FIRST_NAME),
				joins.getContactPerson().get(Person.LAST_NAME),
				joins.getEventParticipant().get(EventParticipant.UUID),
				joins.getEventParticipantPerson().get(Person.FIRST_NAME),
				joins.getEventParticipantPerson().get(Person.LAST_NAME),
				diseaseSelect,
				diseaseDetailsSelect,
				sample.get(Sample.PATHOGEN_TEST_RESULT),
				sample.get(Sample.ADDITIONAL_TESTING_REQUESTED),
				cb.isNotEmpty(sample.get(Sample.ADDITIONAL_TESTS)),
				districtSelect,
				joins.getLab().get(Facility.UUID)));

		// Tests count subquery
		Subquery<Long> testCountSq = cq.subquery(Long.class);
		Root<PathogenTest> testCountRoot = testCountSq.from(PathogenTest.class);
		testCountSq.where(cb.equal(testCountRoot.get(PathogenTest.SAMPLE), sample), cb.isFalse(testCountRoot.get(PathogenTest.DELETED)));
		testCountSq.select(cb.countDistinct(testCountRoot.get(PathogenTest.ID)));
		selections.add(testCountSq.getSelection());

		selections.addAll(getJurisdictionSelections(sampleQueryContext));
		cq.multiselect(selections);

		Predicate filter = createUserFilter(cq, cb, joins, sampleCriteria);

		if (sampleCriteria != null) {
			Predicate criteriaFilter = buildCriteriaFilter(sampleCriteria, cb, joins);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case SampleIndexDto.UUID:
				case SampleIndexDto.LAB_SAMPLE_ID:
				case SampleIndexDto.SHIPPED:
				case SampleIndexDto.RECEIVED:
				case SampleIndexDto.REFERRED:
				case SampleIndexDto.SAMPLE_DATE_TIME:
				case SampleIndexDto.SHIPMENT_DATE:
				case SampleIndexDto.RECEIVED_DATE:
				case SampleIndexDto.SAMPLE_MATERIAL:
				case SampleIndexDto.SAMPLE_PURPOSE:
				case SampleIndexDto.PATHOGEN_TEST_RESULT:
				case SampleIndexDto.ADDITIONAL_TESTING_STATUS:
					expression = sample.get(sortProperty.propertyName);
					break;
				case SampleIndexDto.DISEASE:
					expression = diseaseSelect;
					break;
				case SampleIndexDto.EPID_NUMBER:
					expression = caze.get(Case.EPID_NUMBER);
					break;
				case SampleIndexDto.ASSOCIATED_CASE:
					expression = joins.getCasePerson().get(Person.LAST_NAME);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = joins.getCasePerson().get(Person.FIRST_NAME);
					break;
				case SampleIndexDto.ASSOCIATED_CONTACT:
					expression = joins.getContactPerson().get(Person.LAST_NAME);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = joins.getContactPerson().get(Person.FIRST_NAME);
					break;
				case SampleIndexDto.ASSOCIATED_EVENT_PARTICIPANT:
					expression = joins.getEventParticipantPerson().get(Person.LAST_NAME);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = joins.getEventParticipantPerson().get(Person.FIRST_NAME);
					break;
				case SampleIndexDto.DISTRICT:
					expression = districtSelect;
					break;
				case SampleIndexDto.LAB:
					expression = joins.getLab().get(Facility.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(sample.get(Sample.SAMPLE_DATE_TIME)));
		}

		List<SampleIndexDto> samples = QueryHelper.getResultList(em, cq, first, max);

		if (!samples.isEmpty()) {
			CriteriaQuery<Object[]> testCq = cb.createQuery(Object[].class);
			Root<PathogenTest> testRoot = testCq.from(PathogenTest.class);
			Expression<String> sampleIdExpr = testRoot.get(PathogenTest.SAMPLE).get(Sample.UUID);

			Path<Long> testType = testRoot.get(PathogenTest.TEST_TYPE);
			Path<Date> cqValue = testRoot.get(PathogenTest.CQ_VALUE);
			testCq.select(cb.array(testType, cqValue, sampleIdExpr));

			testCq.where(
				cb.isFalse(testRoot.get(PathogenTest.DELETED)),
				sampleIdExpr.in(samples.stream().map(SampleIndexDto::getUuid).collect(Collectors.toList())));
			testCq.orderBy(cb.desc(testRoot.get(PathogenTest.CHANGE_DATE)));

			List<Object[]> testList = em.createQuery(testCq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();

			Map<String, Object[]> tests = testList.stream()
				.filter(distinctByKey(pathogenTest -> pathogenTest[2]))
				.collect(Collectors.toMap(pathogenTest -> pathogenTest[2].toString(), Function.identity()));

			for (SampleIndexDto indexDto : samples) {
				Optional.ofNullable(tests.get(indexDto.getUuid())).ifPresent(test -> {
					indexDto.setTypeOfLastTest((PathogenTestType) test[0]);
					indexDto.setLastTestCqValue((Float) test[1]);
				});
			}
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		Pseudonymizer emptyValuePseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		pseudonymizer
			.pseudonymizeDtoCollection(SampleIndexDto.class, samples, s -> s.getSampleJurisdictionFlagsDto().getInJurisdiction(), (s, ignored) -> {
				final SampleJurisdictionFlagsDto sampleJurisdictionFlagsDto = s.getSampleJurisdictionFlagsDto();
				if (s.getAssociatedCase() != null) {
					emptyValuePseudonymizer
						.pseudonymizeDto(CaseReferenceDto.class, s.getAssociatedCase(), sampleJurisdictionFlagsDto.getCaseInJurisdiction(), null);
				}

				ContactReferenceDto associatedContact = s.getAssociatedContact();
				if (associatedContact != null) {
					emptyValuePseudonymizer.pseudonymizeDto(
						ContactReferenceDto.PersonName.class,
						associatedContact.getContactName(),
						sampleJurisdictionFlagsDto.getContactInJurisdiction(),
						null);

					if (associatedContact.getCaseName() != null) {
						pseudonymizer.pseudonymizeDto(
							ContactReferenceDto.PersonName.class,
							associatedContact.getCaseName(),
							sampleJurisdictionFlagsDto.getContactCaseInJurisdiction(),
							null);
					}
				}

				if (s.getAssociatedEventParticipant() != null) {
					emptyValuePseudonymizer.pseudonymizeDto(
						EventParticipantReferenceDto.class,
						s.getAssociatedEventParticipant(),
						sampleJurisdictionFlagsDto.getEvenParticipantInJurisdiction(),
						null);
				}
			}, true);

		return samples;
	}

	public List<SampleListEntryDto> getEntriesList(SampleCriteria sampleCriteria, Integer first, Integer max) {
		if (sampleCriteria == null) {
			return Collections.emptyList();
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		final Root<Sample> sample = cq.from(Sample.class);

		SampleQueryContext sampleQueryContext = new SampleQueryContext(cb, cq, sample);
		SampleJoins joins = sampleQueryContext.getJoins();

		cq.distinct(true);

		List<Selection<?>> selections = new ArrayList<>(
			Arrays.asList(
				sample.get(Sample.UUID),
				sample.get(Sample.SAMPLE_MATERIAL),
				sample.get(Sample.PATHOGEN_TEST_RESULT),
				sample.get(Sample.SPECIMEN_CONDITION),
				sample.get(Sample.SAMPLE_PURPOSE),
				joins.getReferredSample().get(Sample.UUID),
				sample.get(Sample.RECEIVED),
				sample.get(Sample.RECEIVED_DATE),
				sample.get(Sample.SHIPPED),
				sample.get(Sample.SHIPMENT_DATE),
				sample.get(Sample.SAMPLE_DATE_TIME),
				joins.getLab().get(Facility.NAME),
				joins.getLab().get(Facility.UUID),
				sample.get(Sample.SAMPLING_REASON),
				sample.get(Sample.SAMPLING_REASON_DETAILS),
				sample.get(Sample.ADDITIONAL_TESTING_REQUESTED),
				cb.isNotEmpty(sample.get(Sample.ADDITIONAL_TESTS))));

		// Tests count subquery
		Subquery<Long> testCountSq = cq.subquery(Long.class);
		Root<PathogenTest> testCountRoot = testCountSq.from(PathogenTest.class);
		testCountSq.where(cb.equal(testCountRoot.get(PathogenTest.SAMPLE), sample), cb.isFalse(testCountRoot.get(PathogenTest.DELETED)));
		testCountSq.select(cb.countDistinct(testCountRoot.get(PathogenTest.ID)));
		selections.add(testCountSq.getSelection());

		selections.addAll(getJurisdictionSelections(sampleQueryContext));
		cq.multiselect(selections);

		Predicate filter = CriteriaBuilderHelper.and(cb, createDefaultFilter(cb, sample), createUserFilter(cq, cb, joins, sampleCriteria));
		Predicate criteriaFilter = buildSampleListCriteriaFilter(sampleCriteria, cb, joins);
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);

		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.desc(sample.get(Sample.SAMPLE_DATE_TIME)));

		return createQuery(cq, first, max).unwrap(org.hibernate.query.Query.class)
			.setResultTransformer(new SampleListEntryDtoResultTransformer())
			.getResultList();
	}

	public List<Sample> getAllActiveSamplesAfter(Date date, User user, Integer batchSize, String lastSynchronizedUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(getElementClass());
		Root<Sample> from = cq.from(getElementClass());

		Predicate filter = createActiveSamplesFilter(cb, from);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date, lastSynchronizedUuid);
			filter = CriteriaBuilderHelper.and(cb, filter, dateFilter);
		}

		cq.where(filter);
		cq.distinct(true);

		return getBatchedQueryResults(cb, cq, from, batchSize);
	}

	public List<String> getAllActiveUuids(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Sample> from = cq.from(getElementClass());

		Predicate filter = createActiveSamplesFilter(cb, from);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(Sample.UUID));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * Returns the sample that refers to the sample identified by the sampleUuid.
	 *
	 * @param sampleUuid
	 *            The UUID of the sample to get the referral for.
	 * @return The sample that refers to this sample, or null if none is found.
	 */
	public Sample getReferredFrom(String sampleUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(getElementClass());
		Root<Sample> from = cq.from(getElementClass());
		Join<Sample, Sample> referredJoin = from.join(Sample.REFERRED_TO, JoinType.LEFT);

		cq.where(cb.equal(referredJoin.get(AbstractDomainObject.UUID), sampleUuid));
		return QueryHelper.getSingleResult(em, cq);
	}

	public List<String> getDeletedUuidsSince(User user, Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Sample> sample = cq.from(Sample.class);

		Predicate filter = createUserFilter(cb, cq, sample);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(sample.get(Sample.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate deletedFilter = cb.equal(sample.get(Sample.DELETED), true);
		if (filter != null) {
			filter = cb.and(filter, deletedFilter);
		} else {
			filter = deletedFilter;
		}

		cq.where(filter);
		cq.select(sample.get(Sample.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<Sample> getByCaseUuids(List<String> caseUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(Sample.class);
		Root<Sample> sampleRoot = cq.from(Sample.class);
		Join<Sample, Case> caseJoin = sampleRoot.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);

		Predicate filter = cb.and(createDefaultFilter(cb, sampleRoot), caseJoin.get(AbstractDomainObject.UUID).in(caseUuids));

		cq.where(filter);
		return em.createQuery(cq).getResultList();
	}

	public List<Sample> getByContactUuids(List<String> contactUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(Sample.class);
		Root<Sample> sampleRoot = cq.from(Sample.class);
		Join<Sample, Contact> contactJoin = sampleRoot.join(Sample.ASSOCIATED_CONTACT, JoinType.LEFT);

		Predicate filter = cb.and(createDefaultFilter(cb, sampleRoot), contactJoin.get(AbstractDomainObject.UUID).in(contactUuids));

		cq.where(filter);
		return em.createQuery(cq).getResultList();
	}

	public List<Sample> getByEventParticipantUuids(List<String> eventParticipantUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(Sample.class);
		Root<Sample> sampleRoot = cq.from(Sample.class);
		Join<Sample, EventParticipant> eventParticipantJoin = sampleRoot.join(Sample.ASSOCIATED_EVENT_PARTICIPANT, JoinType.LEFT);

		Predicate filter = cb.and(createDefaultFilter(cb, sampleRoot), eventParticipantJoin.get(AbstractDomainObject.UUID).in(eventParticipantUuids));

		cq.where(filter);
		return em.createQuery(cq).getResultList();
	}

	@SuppressWarnings("unchecked")
	public Map<PathogenTestResultType, Long> getNewTestResultCountByResultType(List<Long> caseIds) {

		// Avoid parameter limit by joining caseIds to a String instead of n parameters 
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder.append("WITH sortedsamples AS (SELECT DISTINCT ON (").append(Sample.ASSOCIATED_CASE).append("_id) ")
					.append(Sample.ASSOCIATED_CASE).append("_id, ").append(Sample.PATHOGEN_TEST_RESULT).append(", ").append(Sample.SAMPLE_DATE_TIME)
					.append(" FROM ").append(Sample.TABLE_NAME).append(" WHERE (").append(Sample.SPECIMEN_CONDITION).append(" IS NULL OR ")
					.append(Sample.SPECIMEN_CONDITION).append(" = '").append(SpecimenCondition.ADEQUATE.name()).append("') AND ").append(Sample.TABLE_NAME)
					.append(".").append(Sample.DELETED).append(" = false ORDER BY ").append(Sample.ASSOCIATED_CASE).append("_id, ")
					.append(Sample.SAMPLE_DATE_TIME).append(" desc) SELECT sortedsamples.").append(Sample.PATHOGEN_TEST_RESULT).append(", COUNT(")
					.append(Sample.ASSOCIATED_CASE).append("_id) FROM sortedsamples JOIN ").append(Case.TABLE_NAME).append(" ON sortedsamples.")
					.append(Sample.ASSOCIATED_CASE).append("_id = ").append(Case.TABLE_NAME).append(".id ")
					.append(" WHERE sortedsamples.").append(Sample.ASSOCIATED_CASE).append("_id IN (:caseIds)")
					.append(" GROUP BY sortedsamples." + Sample.PATHOGEN_TEST_RESULT);
		//@formatter:on

		List<Object[]> results = new LinkedList<>();
		IterableHelper.executeBatched(caseIds, ModelConstants.PARAMETER_LIMIT, batchedCaseIds -> {
			Query query = em.createNativeQuery(queryBuilder.toString());
			query.setParameter("caseIds", batchedCaseIds);
			results.addAll(query.getResultList());
		});

		return results.stream()
			.filter(e -> e[0] != null)
			.collect(Collectors.toMap(e -> PathogenTestResultType.valueOf((String) e[0]), e -> ((BigInteger) e[1]).longValue(), Long::sum));
	}

	@Override
	@SuppressWarnings("rawtypes")
	@Deprecated
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Sample> samplePath) {
		return createUserFilter(cq, cb, new SampleJoins(samplePath), new SampleCriteria());
	}

	@SuppressWarnings("rawtypes")
	public Predicate createUserFilter(CriteriaQuery cq, CriteriaBuilder cb, SampleJoins joins, SampleCriteria criteria) {

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}

		Predicate filter = createUserFilterWithoutAssociations(cb, joins);

		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (jurisdictionLevel == JurisdictionLevel.LABORATORY || jurisdictionLevel == JurisdictionLevel.EXTERNAL_LABORATORY) {
			return filter;
		}

		if (criteria != null && criteria.getSampleAssociationType() != null && criteria.getSampleAssociationType() != SampleAssociationType.ALL) {
			final SampleAssociationType sampleAssociationType = criteria.getSampleAssociationType();
			if (sampleAssociationType == SampleAssociationType.CASE) {
				filter = CriteriaBuilderHelper.or(cb, filter, caseService.createUserFilter(cb, cq, joins.getCaze(), null));
			} else if (sampleAssociationType == SampleAssociationType.CONTACT) {
				filter = CriteriaBuilderHelper.or(cb, filter, contactService.createUserFilterForJoin(cb, cq, joins.getContact()));
			} else if (sampleAssociationType == SampleAssociationType.EVENT_PARTICIPANT) {
				filter = CriteriaBuilderHelper.or(cb, filter, eventParticipantService.createUserFilterForJoin(cb, cq, joins.getEventParticipant()));
			}
		} else if (currentUser.getLimitedDisease() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				CriteriaBuilderHelper.or(
					cb,
					caseService.createUserFilter(cb, cq, joins.getCaze(), null),
					contactService.createUserFilterForJoin(cb, cq, joins.getContact()),
					eventParticipantService.createUserFilterForJoin(cb, cq, joins.getEventParticipant())));
		} else {
			filter = CriteriaBuilderHelper.or(
				cb,
				filter,
				caseService.createUserFilter(cb, cq, joins.getCaze(), null),
				contactService.createUserFilterForJoin(cb, cq, joins.getContact()),
				eventParticipantService.createUserFilterForJoin(cb, cq, joins.getEventParticipant()));
		}

		return filter;
	}

	/**
	 * Creates a user filter that does not take sample associations into account, i.e. their associated cases, contacts, and event
	 * participants. Instead, it filters for samples of the user's laboratory (if present) and removes samples with diseases
	 * that the user can't access if they have a limited disease set. SHOULD GENERALLY NOT BE USED WITHOUT A PROPER USER FILTER!
	 */
	public Predicate createUserFilterWithoutAssociations(CriteriaBuilder cb, SampleJoins joins) {
		Predicate filter = null;

		User currentUser = getCurrentUser();
		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		// Lab users can see samples assigned to their laboratory
		if (jurisdictionLevel == JurisdictionLevel.LABORATORY || jurisdictionLevel == JurisdictionLevel.EXTERNAL_LABORATORY) {
			if (currentUser.getLaboratory() != null) {
				filter = CriteriaBuilderHelper.or(cb, filter, cb.equal(joins.getLab(), currentUser.getLaboratory()));
			}
		}

		// Only show samples of a specific disease if a limited disease is set
		if (currentUser.getLimitedDisease() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(
					cb.and(cb.isNotNull(joins.getEvent()), cb.isNull(joins.getEvent().get(Event.DISEASE))),
					cb.equal(
						cb.selectCase()
							.when(cb.isNotNull(joins.getCaze()), joins.getCaze().get(Case.DISEASE))
							.when(cb.isNotNull(joins.getContact()), joins.getContact().get(Contact.DISEASE))
							.otherwise(joins.getEvent().get(Event.DISEASE)),
						currentUser.getLimitedDisease())));
		}

		return filter;
	}

	public SampleJurisdictionFlagsDto inJurisdictionOrOwned(Sample sample) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SampleJurisdictionFlagsDto> cq = cb.createQuery(SampleJurisdictionFlagsDto.class);
		Root<Sample> root = cq.from(Sample.class);
		cq.multiselect(getJurisdictionSelections(new SampleQueryContext(cb, cq, root)));
		cq.where(cb.equal(root.get(Sample.UUID), sample.getUuid()));
		return em.createQuery(cq).getSingleResult();
	}

	public List<Selection<?>> getJurisdictionSelections(SampleQueryContext qc) {

		CriteriaBuilder cb = qc.getCriteriaBuilder();
		SampleJoins joins = (SampleJoins) qc.getJoins();
		CriteriaQuery cq = qc.getQuery();
		ContactJoins contactJoins = new ContactJoins(joins.getContact());
		return Arrays.asList(
			JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(qc)),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(cb.isNotNull(joins.getCaze()), caseService.inJurisdictionOrOwned(new CaseQueryContext(cb, cq, joins.getCaseJoins())))),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(cb.isNotNull(joins.getContact()), contactService.inJurisdictionOrOwned(new ContactQueryContext(cb, cq, joins.getContact())))),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(
					cb.isNotNull(joins.getContact()),
					cb.isNotNull(contactJoins.getCaze()),
					caseService.inJurisdictionOrOwned(new CaseQueryContext(cb, cq, contactJoins.getCaseJoins())))),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(
					cb.isNotNull(joins.getEventParticipant()),
					eventParticipantService.inJurisdictionOrOwned(new EventParticipantQueryContext(cb, cq, joins.getEventParticipant())))));
	}

	public Predicate inJurisdictionOrOwned(SampleQueryContext qc) {
		final User currentUser = userService.getCurrentUser();
		return SampleJurisdictionPredicateValidator.of(qc, currentUser).inJurisdictionOrOwned();
	}

	public Predicate buildCriteriaFilter(SampleCriteria criteria, CriteriaBuilder cb, SampleJoins joins) {
		final From<?, ?> sample = joins.getRoot();

		Predicate filter = null;
		final SampleAssociationType sampleAssociationType = criteria.getSampleAssociationType();
		if (sampleAssociationType == SampleAssociationType.CASE) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(joins.getCaze()));
		} else if (sampleAssociationType == SampleAssociationType.CONTACT) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(joins.getContact()));
		} else if (sampleAssociationType == SampleAssociationType.EVENT_PARTICIPANT) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(joins.getEventParticipant()));
		}

		if (criteria.getRegion() != null) {
			Expression<Object> regionExpression = cb.selectCase()
				.when(cb.isNotNull(joins.getCaseRegion()), joins.getCaseRegion().get(AbstractDomainObject.UUID))
				.otherwise(
					cb.selectCase()
						.when(cb.isNotNull(joins.getContactRegion()), joins.getContactRegion().get(AbstractDomainObject.UUID))
						.otherwise(
							cb.selectCase()
								.when(cb.isNotNull(joins.getContactCaseRegion()), joins.getContactCaseRegion().get(AbstractDomainObject.UUID))
								.otherwise(joins.getEventRegion().get(AbstractDomainObject.UUID))));
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(
					cb.and(
						cb.isNotNull(joins.getCaze()),
						cb.equal(joins.getCaseResponsibleRegion().get(AbstractDomainObject.UUID), criteria.getRegion().getUuid())),
					cb.equal(regionExpression, criteria.getRegion().getUuid())));
		}
		if (criteria.getDistrict() != null) {
			Expression<Object> districtExpression = cb.selectCase()
				.when(cb.isNotNull(joins.getCaseDistrict()), joins.getCaseDistrict().get(AbstractDomainObject.UUID))
				.otherwise(
					cb.selectCase()
						.when(cb.isNotNull(joins.getContactDistrict()), joins.getContactDistrict().get(AbstractDomainObject.UUID))
						.otherwise(
							cb.selectCase()
								.when(cb.isNotNull(joins.getContactCaseDistrict()), joins.getContactCaseDistrict().get(AbstractDomainObject.UUID))
								.otherwise(joins.getEventDistrict().get(AbstractDomainObject.UUID))));
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(
					cb.and(
						cb.isNotNull(joins.getCaze()),
						cb.equal(joins.getCaseResponsibleDistrict().get(AbstractDomainObject.UUID), criteria.getDistrict().getUuid())),
					cb.equal(districtExpression, criteria.getDistrict().getUuid())));
		}
		if (criteria.getLaboratory() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getLab().get(AbstractDomainObject.UUID), criteria.getLaboratory().getUuid()));
		}
		if (criteria.getShipped() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(sample.get(Sample.SHIPPED), criteria.getShipped()));
		}
		if (criteria.getReceived() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(sample.get(Sample.RECEIVED), criteria.getReceived()));
		}
		if (criteria.getReferred() != null) {
			if (criteria.getReferred().equals(Boolean.TRUE)) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(sample.get(Sample.REFERRED_TO)));
			} else {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.isNull(sample.get(Sample.REFERRED_TO)));
			}
		}
		if (criteria.getPathogenTestResult() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(sample.get(Sample.PATHOGEN_TEST_RESULT), criteria.getPathogenTestResult()));
		}
		if (criteria.getCaseClassification() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getCaze().get(Case.CASE_CLASSIFICATION), criteria.getCaseClassification()));
		}
		if (criteria.getDisease() != null) {
			Expression<Object> diseaseExpression = cb.selectCase()
				.when(cb.isNotNull(joins.getCaze()), joins.getCaze().get(Case.DISEASE))
				.otherwise(
					cb.selectCase()
						.when(cb.isNotNull(joins.getContact()), joins.getContact().get(Contact.DISEASE))
						.otherwise(joins.getEvent().get(Event.DISEASE)));
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(diseaseExpression, criteria.getDisease()));
		}
		if (criteria.getCaze() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getCaze().get(Case.UUID), criteria.getCaze().getUuid()));
		}
		if (criteria.getContact() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getContact().get(Contact.UUID), criteria.getContact().getUuid()));
		}
		if (criteria.getEventParticipant() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getEventParticipant().get(EventParticipant.UUID), criteria.getEventParticipant().getUuid()));
		}
		if (criteria.getSampleReportDateFrom() != null && criteria.getSampleReportDateTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.between(sample.get(Sample.SAMPLE_DATE_TIME), criteria.getSampleReportDateFrom(), criteria.getSampleReportDateTo()));
		} else if (criteria.getSampleReportDateFrom() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.greaterThanOrEqualTo(sample.get(Sample.SAMPLE_DATE_TIME), criteria.getSampleReportDateFrom()));
		} else if (criteria.getSampleReportDateTo() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(sample.get(Sample.SAMPLE_DATE_TIME), criteria.getSampleReportDateTo()));
		}
		if (criteria.getSpecimenCondition() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(sample.get(Sample.SPECIMEN_CONDITION), criteria.getSpecimenCondition()));
		}
		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.or(cb.equal(joins.getCaze().get(Case.ARCHIVED), false), cb.isNull(joins.getCaze().get(Case.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getCaze().get(Case.ARCHIVED), true));
			}
		}
		if (criteria.getDeleted() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(sample.get(Sample.DELETED), criteria.getDeleted()));
		}

		if (criteria.getCaseCodeIdLike() != null) {
			String[] textFilters = criteria.getCaseCodeIdLike().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, joins.getCaze().get(Case.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getCasePerson().get(Person.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getCasePerson().get(Person.LAST_NAME), textFilter),
					CriteriaBuilderHelper.ilike(cb, joins.getCaze().get(Case.EPID_NUMBER), textFilter),
					CriteriaBuilderHelper.ilike(cb, sample.get(Sample.UUID), textFilter),
					CriteriaBuilderHelper.ilike(cb, sample.get(Sample.LAB_SAMPLE_ID), textFilter),
					CriteriaBuilderHelper.ilike(cb, sample.get(Sample.FIELD_SAMPLE_ID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getLab().get(Facility.NAME), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}

		if (criteria.getCaseUuids() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, sample.get(Sample.ASSOCIATED_CASE).get(Case.UUID).in(criteria.getCaseUuids()));
		}

		if (criteria.getContactUuids() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, sample.get(Sample.ASSOCIATED_CONTACT).get(Contact.UUID).in(criteria.getContactUuids()));
		}

		if (criteria.getEventParticipantUuids() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, sample.get(Sample.ASSOCIATED_EVENT_PARTICIPANT).get(EventParticipant.UUID).in(criteria.getEventParticipantUuids()));
		}

		return filter;
	}

	private Predicate buildSampleListCriteriaFilter(SampleCriteria criteria, CriteriaBuilder cb, SampleJoins joins) {
		Predicate filter = null;
		final SampleAssociationType sampleAssociationType = criteria.getSampleAssociationType();
		if (sampleAssociationType == SampleAssociationType.CASE) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(joins.getCaze()));
		} else if (sampleAssociationType == SampleAssociationType.CONTACT) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(joins.getContact()));
		} else if (sampleAssociationType == SampleAssociationType.EVENT_PARTICIPANT) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(joins.getEventParticipant()));
		}

		if (criteria.getCaze() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getCaze().get(Case.UUID), criteria.getCaze().getUuid()));
		}
		if (criteria.getContact() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getContact().get(Contact.UUID), criteria.getContact().getUuid()));
		}
		if (criteria.getEventParticipant() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getEventParticipant().get(EventParticipant.UUID), criteria.getEventParticipant().getUuid()));
		}

		return filter;
	}

	@Override
	public void deletePermanent(Sample sample) {

		// Delete all pathogen tests of this sample
		for (PathogenTest pathogenTest : sample.getPathogenTests()) {
			pathogenTestService.deletePermanent(pathogenTest);
		}

		// Delete all additional tests of this sample
		for (AdditionalTest additionalTest : sample.getAdditionalTests()) {
			additionalTestService.deletePermanent(additionalTest);
		}

		// Remove the case from any S2S share info referencing it
		sormasToSormasShareInfoService.getByAssociatedEntity(SormasToSormasShareInfo.SAMPLE, sample.getUuid()).forEach(s -> {
			s.setSample(null);
			if (sormasToSormasShareInfoFacade.hasAnyEntityReference(s)) {
				sormasToSormasShareInfoService.ensurePersisted(s);
			} else {
				sormasToSormasShareInfoService.deletePermanent(s);
			}
		});

		deleteSampleLinks(sample);

		super.deletePermanent(sample);
	}

	@Override
	public void delete(Sample sample) {

		// Mark all pathogen tests of this sample as deleted
		for (PathogenTest pathogenTest : sample.getPathogenTests()) {
			pathogenTestService.delete(pathogenTest);
		}

		deleteSampleLinks(sample);

		super.delete(sample);
	}

	private void deleteSampleLinks(Sample sample) {

		// Remove the reference from another sample to this sample if existing
		Sample referralSample = getReferredFrom(sample.getUuid());
		if (referralSample != null) {
			referralSample.setReferredTo(null);
			ensurePersisted(referralSample);
		}

		// Remove the reference from all lab messages
		labMessageService.getForSample(new SampleReferenceDto(sample.getUuid())).forEach(labMessage -> {
			labMessage.setSample(null);
			labMessageService.ensurePersisted(labMessage);
		});
	}

	/**
	 * @param sampleUuids
	 *            {@link Sample}s identified by {@code List<String> sampleUuids} to be deleted.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteAll(List<String> sampleUuids) {

		List<Sample> samplesList = getByUuids(sampleUuids);
		List<String> pathogenTestUUIDsList = new ArrayList<>();
		List<String> additionalTestUUIDsList = new ArrayList<>();
		for (Sample sample : samplesList) {
			// Mark all pathogen tests of this sample as deleted
			if (!sample.getPathogenTests().isEmpty()) {
				for (PathogenTest pathogenTest : sample.getPathogenTests()) {
					pathogenTestUUIDsList.add(pathogenTest.getUuid());
				}
			}

			// Delete all additional tests of this sample
			if (!sample.getPathogenTests().isEmpty()) {
				for (AdditionalTest additionalTest : sample.getAdditionalTests()) {
					additionalTestUUIDsList.add(additionalTest.getUuid());
				}
			}
		}

		long startTime;
		if (pathogenTestUUIDsList.size() > 0) {
			startTime = DateHelper.startTime();
			IterableHelper.executeBatched(
				pathogenTestUUIDsList,
				pathogenTestUUIDsList.size(),
				batchedSampleUuids -> pathogenTestService.delete(pathogenTestUUIDsList));
			logger.debug(
				"pathogenTestService.delete(pathogenTestUUIDsList) = {}, {}ms",
				pathogenTestUUIDsList.size(),
				DateHelper.durationMillies(startTime));
		}

		if (additionalTestUUIDsList.size() > 0) {
			startTime = DateHelper.startTime();
			IterableHelper.executeBatched(
				additionalTestUUIDsList,
				additionalTestUUIDsList.size(),
				batchedSampleUuids -> additionalTestService.delete(additionalTestUUIDsList));
			logger.debug(
				"additionalTestService.delete(additionalTestUUIDsList) = {}, {}ms",
				additionalTestUUIDsList.size(),
				DateHelper.durationMillies(startTime));
		}

		for (Sample sample : samplesList) {
			// Remove the reference from another sample to this sample if existing
			Sample referralSample = getReferredFrom(sample.getUuid());
			if (referralSample != null) {
				referralSample.setReferredTo(null);
				ensurePersisted(referralSample);
			}
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Sample> cu = cb.createCriteriaUpdate(Sample.class);
		Root<Sample> root = cu.from(Sample.class);

		cu.set(Sample.CHANGE_DATE, Timestamp.from(Instant.now()));
		cu.set(root.get(Sample.DELETED), true);

		cu.where(root.get(Sample.UUID).in(sampleUuids));

		em.createQuery(cu).executeUpdate();

		Map<String, Case> stringCaseMap = new HashMap<>();
		for (Sample sample : samplesList) {
			final Case associatedCase = sample.getAssociatedCase();
			if (associatedCase != null) {
				stringCaseMap.put(associatedCase.getUuid(), associatedCase);
			}
		}

		for (Map.Entry<String, Case> entry : stringCaseMap.entrySet()) {
			Case associatedCase = entry.getValue();
			caseFacade.onCaseSampleChanged(associatedCase);
		}
	}

	/**
	 * Creates a filter that excludes all samples that are deleted or associated with archived or deleted entities
	 */
	public Predicate createActiveSamplesFilter(CriteriaBuilder cb, From<?, Sample> root) {

		Join<Sample, Case> caze = root.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
		Join<Sample, Contact> contact = root.join(Sample.ASSOCIATED_CONTACT, JoinType.LEFT);
		Join<Contact, Case> contactCase = contact.join(Contact.CAZE, JoinType.LEFT);
		Join<Sample, EventParticipant> eventParticipant = root.join(Sample.ASSOCIATED_EVENT_PARTICIPANT, JoinType.LEFT);
		Join<EventParticipant, Event> event = eventParticipant.join(EventParticipant.EVENT, JoinType.LEFT);
		Predicate pred = cb.or(
			cb.and(cb.isFalse(caze.get(Case.ARCHIVED)), cb.isFalse(caze.get(Case.DELETED))),
			cb.and(
				cb.or(
					cb.isNull(contact.get(Contact.CAZE)),
					cb.and(cb.isFalse(contactCase.get(Case.ARCHIVED)), cb.isFalse(contactCase.get(Case.DELETED)))),
				cb.isFalse(contact.get(Contact.DELETED))),
			cb.and(
				cb.isFalse(event.get(Event.ARCHIVED)),
				cb.isFalse(event.get(Event.DELETED)),
				cb.isFalse(eventParticipant.get(EventParticipant.DELETED))));
		return cb.and(pred, cb.isFalse(root.get(Sample.DELETED)));
	}

	/**
	 * Creates a default filter that should be used as the basis of queries that do not use {@link SampleCriteria}.
	 * This essentially removes {@link DeletableAdo#isDeleted()} samples from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, Root<Sample> root) {
		return cb.isFalse(root.get(Sample.DELETED));
	}

	public Boolean isSampleEditAllowed(Sample sample) {
		if (sample.getSormasToSormasOriginInfo() != null && !sample.getSormasToSormasOriginInfo().isOwnershipHandedOver()) {
			return false;
		}

		return inJurisdictionOrOwned(sample).getInJurisdiction() && !sormasToSormasShareInfoService.isSamlpeOwnershipHandedOver(sample);
	}

	public Date getEarliestSampleDate(Collection<Sample> samples) {
		Date earliestSampleDate = null;
		for (Sample sample : samples) {
			if (!sample.isDeleted()
				&& sample.getPathogenTestResult() == PathogenTestResultType.POSITIVE
				&& (earliestSampleDate == null || sample.getSampleDateTime().before(earliestSampleDate))) {
				earliestSampleDate = sample.getSampleDateTime();
			}
		}
		return earliestSampleDate;
	}

	private <T> java.util.function.Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

}
