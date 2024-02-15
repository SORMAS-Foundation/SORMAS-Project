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
package de.symeda.sormas.backend.contact;

import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
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
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactJurisdictionFlagsDto;
import de.symeda.sormas.api.contact.ContactListEntryDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.MapContactDto;
import de.symeda.sormas.api.contact.MergeContactIndexDto;
import de.symeda.sormas.api.dashboard.DashboardContactDto;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.caze.CaseUserFilterCriteria;
import de.symeda.sormas.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ChangeDateBuilder;
import de.symeda.sormas.backend.common.ChangeDateFilterBuilder;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.DeletableAdo;
import de.symeda.sormas.backend.common.JurisdictionFlagsService;
import de.symeda.sormas.backend.contact.transformers.ContactListEntryDtoResultTransformer;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.document.DocumentService;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.exposure.ExposureService;
import de.symeda.sormas.backend.externaljournal.ExternalJournalService;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonQueryContext;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserRole;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.ExternalDataUtil;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.visit.Visit;
import de.symeda.sormas.backend.visit.VisitFacadeEjb;
import de.symeda.sormas.backend.visit.VisitService;

@Stateless
@LocalBean
public class ContactService extends AbstractCoreAdoService<Contact, ContactJoins>
	implements JurisdictionFlagsService<Contact, ContactJurisdictionFlagsDto, ContactJoins, ContactQueryContext> {

	private static final Double SECONDS_30_DAYS = Long.valueOf(TimeUnit.DAYS.toSeconds(30L)).doubleValue();

	@EJB
	private CaseService caseService;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;
	@EJB
	private TaskService taskService;
	@EJB
	private SampleService sampleService;
	@EJB
	private EpiDataService epiDataService;
	@EJB
	private SormasToSormasShareInfoService sormasToSormasShareInfoService;
	@EJB
	private ExposureService exposureService;
	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;
	@EJB
	private ExternalJournalService externalJournalService;
	@EJB
	private UserService userService;
	@EJB
	private DocumentService documentService;
	@EJB
	private SormasToSormasShareInfoFacadeEjb.SormasToSormasShareInfoFacadeEjbLocal sormasToSormasShareInfoFacade;
	@EJB
	private VisitService visitService;
	@EJB
	private SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal sormasToSormasFacade;
	@EJB
	private ContactListCriteriaBuilder listCriteriaBuilder;

	public ContactService() {
		super(Contact.class, DeletableEntityType.CONTACT);
	}

	@Override
	protected List<String> referencesToBeFetched() {
		return Arrays.asList(Contact.HEALTH_CONDITIONS, Contact.EPI_DATA);
	}

	public List<Contact> findBy(ContactCriteria contactCriteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());
		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, from);

		Predicate filter = buildCriteriaFilter(contactCriteria, contactQueryContext);

		if (user != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(contactQueryContext, contactCriteria));
		}
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(Contact.CREATION_DATE)));

		return em.createQuery(cq).getResultList();
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createRelevantDataFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Contact> from) {

		Predicate filter = createActiveContactsFilter(cb, from);

		if (getCurrentUser() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilterInternal(cb, cq, from));
		}

		return filter;
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Contact> from, Date date) {
		return createChangeDateFilter(cb, toJoins(from), DateHelper.toTimestampUpper(date), null);
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Contact> from, Timestamp date) {
		return createChangeDateFilter(cb, toJoins(from), DateHelper.toTimestampUpper(date), null);
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Contact> from, Date date, String lastSynchronizedUuid) {
		return createChangeDateFilter(cb, toJoins(from), DateHelper.toTimestampUpper(date), null);
	}

	public Predicate createChangeDateFilter(CriteriaBuilder cb, ContactJoins joins, Timestamp date, String lastSynchronizedUuid) {

		ChangeDateFilterBuilder changeDateFilterBuilder = new ChangeDateFilterBuilder(cb, date, joins.getRoot(), lastSynchronizedUuid);
		return addChangeDates(changeDateFilterBuilder, joins, false).build();
	}

	@Override
	protected ContactJoins toJoins(From<?, Contact> adoPath) {
		return new ContactJoins(adoPath);
	}

	@Override
	protected <T extends ChangeDateBuilder<T>> T addChangeDates(T builder, ContactJoins joins, boolean includeExtendedChangeDateFilters) {

		Join<Contact, HealthConditions> healthCondition = joins.getHealthConditions();
		Join<Contact, EpiData> epiData = joins.getEpiData();

		builder = super.addChangeDates(builder, joins, includeExtendedChangeDateFilters).add(healthCondition)
			.add(joins.getRoot(), Contact.SORMAS_TO_SORMAS_ORIGIN_INFO)
			.add(joins.getRoot(), Contact.SORMAS_TO_SORMAS_SHARES);

		builder = epiDataService.addChangeDates(builder, epiData);

		if (includeExtendedChangeDateFilters) {
			Join<Contact, Sample> contactSampleJoin = joins.getSamples();
			Join<Contact, Visit> contactVisitJoin = joins.getVisits();
			builder.add(contactSampleJoin).add(contactSampleJoin, Sample.PATHOGENTESTS).add(contactVisitJoin);
		}

		return builder;
	}

	public List<String> getAllActiveUuids(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Contact> from = cq.from(getElementClass());
		ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, from);

		Predicate filter = createActiveContactsFilter(cb, from);

		if (user != null) {
			Predicate userFilter = createUserFilter(contactQueryContext, null);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		if (RequestContextHolder.isMobileSync()) {
			Predicate predicate = createLimitedChangeDateFilter(cb, from);
			if (predicate != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, predicate);
			}
		}

		cq.where(filter);
		cq.select(from.get(Contact.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<Contact> getAllByResultingCase(Case caze) {
		return getAllByResultingCase(caze, false);
	}

	public List<Contact> getAllByResultingCase(Case caze, boolean includeDeleted) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());

		Predicate filter = includeDeleted ? null : createDefaultFilter(cb, from);
		filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.RESULTING_CASE), caze));
		cq.where(filter);

		cq.orderBy(cb.desc(from.get(Contact.REPORT_DATE_TIME)));

		return em.createQuery(cq).getResultList();
	}

	public List<String> getAllUuidsByCaseUuids(List<String> uuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Contact> from = cq.from(getElementClass());

		cq.select(from.get(Contact.UUID));
		cq.where(cb.and(createDefaultFilter(cb, from), cb.in(from.join(Contact.CAZE).get(Case.UUID)).value(uuids)));

		return em.createQuery(cq).getResultList();
	}

	public List<Object[]> getSourceCaseClassifications(List<Long> caseIds) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Contact> contactRoot = cq.from(getElementClass());
		Join<Contact, Case> rootCaseJoin = contactRoot.join(Contact.CAZE);
		Join<Contact, Case> resultingCaseJoin = contactRoot.join(Contact.RESULTING_CASE);

		cq.multiselect(resultingCaseJoin.get(Case.ID), rootCaseJoin.get(Case.CASE_CLASSIFICATION));

		Expression<String> caseIdsExpression = resultingCaseJoin.get(Case.ID);
		cq.where(cb.and(createDefaultFilter(cb, contactRoot), caseIdsExpression.in(caseIds), cb.isNotNull(contactRoot.get(Contact.CAZE))));

		return em.createQuery(cq).getResultList();
	}

	public List<Contact> getFollowUpBetween(@NotNull Date fromDate, @NotNull Date toDate) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());

		Predicate filter = createActiveContactsFilter(cb, from);
		filter = cb.and(filter, cb.isNotNull(from.get(Contact.FOLLOW_UP_UNTIL)));
		filter = cb.and(filter, cb.greaterThanOrEqualTo(from.get(Contact.FOLLOW_UP_UNTIL), fromDate));
		filter = cb.and(
			filter,
			cb.or(
				cb.and(cb.isNotNull(from.get(Contact.LAST_CONTACT_DATE)), cb.lessThan(from.get(Contact.LAST_CONTACT_DATE), toDate)),
				cb.lessThan(from.get(Contact.REPORT_DATE_TIME), toDate)));

		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}

	public List<Contact> getByPersonAndDisease(Person person, Disease disease) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());

		Predicate filter = createDefaultFilter(cb, from);
		filter = cb.and(filter, cb.equal(from.get(Contact.PERSON), person));
		filter = cb.and(filter, cb.equal(from.get(Contact.DISEASE), disease));
		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}

	public Set<Contact> getAllRelevantContacts(Person person, Disease disease, Date referenceDate) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(Contact.class);
		Root<Contact> contactRoot = cq.from(Contact.class);

		Predicate filter = CriteriaBuilderHelper
			.and(cb, createDefaultFilter(cb, contactRoot), buildRelevantContactsFilter(person, disease, referenceDate, cb, contactRoot));
		cq.where(filter);

		return new HashSet<>(em.createQuery(cq).getResultList());
	}

	/**
	 * Returns a filter that can be used to retrieve all contacts with the specified
	 * person and disease whose last contact date or report date (depending on
	 * availability) is before the reference date and, if available, whose follow-up
	 * until date is after the reference date, including an offset to allow some
	 * tolerance.
	 */
	private Predicate buildRelevantContactsFilter(Person person, Disease disease, Date referenceDate, CriteriaBuilder cb, Root<Contact> from) {

		Date referenceDateStart = DateHelper.getStartOfDay(referenceDate);
		Date referenceDateEnd = DateHelper.getEndOfDay(referenceDate);

		Predicate filter = CriteriaBuilderHelper.and(cb, cb.equal(from.get(Contact.PERSON), person), cb.equal(from.get(Contact.DISEASE), disease));

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			CriteriaBuilderHelper.or(
				cb,
				CriteriaBuilderHelper.and(
					cb,
					cb.isNull(from.get(Contact.LAST_CONTACT_DATE)),
					cb.lessThanOrEqualTo(
						from.get(Contact.REPORT_DATE_TIME),
						DateHelper.addDays(referenceDateEnd, FollowUpLogic.ALLOWED_DATE_OFFSET))),
				cb.lessThanOrEqualTo(from.get(Contact.LAST_CONTACT_DATE), DateHelper.addDays(referenceDateEnd, FollowUpLogic.ALLOWED_DATE_OFFSET))));

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			CriteriaBuilderHelper.or(
				cb,
				// If the contact does not have a follow-up until date, use the last
				// contact/contact report date as a fallback
				CriteriaBuilderHelper.and(
					cb,
					cb.isNull(from.get(Contact.FOLLOW_UP_UNTIL)),
					CriteriaBuilderHelper.or(
						cb,
						CriteriaBuilderHelper.and(
							cb,
							cb.isNull(from.get(Contact.LAST_CONTACT_DATE)),
							cb.greaterThanOrEqualTo(
								from.get(Contact.REPORT_DATE_TIME),
								DateHelper.subtractDays(referenceDateStart, FollowUpLogic.ALLOWED_DATE_OFFSET))),
						cb.greaterThanOrEqualTo(
							from.get(Contact.LAST_CONTACT_DATE),
							DateHelper.subtractDays(referenceDateStart, FollowUpLogic.ALLOWED_DATE_OFFSET)))),
				cb.greaterThanOrEqualTo(
					from.get(Contact.FOLLOW_UP_UNTIL),
					DateHelper.subtractDays(referenceDateStart, FollowUpLogic.ALLOWED_DATE_OFFSET))));

		return filter;
	}

	public List<String> getArchivedUuidsSince(Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Contact> contact = cq.from(Contact.class);

		ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contact);
		Predicate filter = createUserFilter(contactQueryContext, null);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(contact.get(Contact.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate archivedFilter = cb.equal(contact.get(Contact.ARCHIVED), true);
		if (filter != null) {
			filter = cb.and(filter, archivedFilter);
		} else {
			filter = archivedFilter;
		}

		cq.where(filter);
		cq.select(contact.get(Contact.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<String> getDeletedUuidsSince(User user, Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Contact> contact = cq.from(Contact.class);
		ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contact);

		Predicate filter = createUserFilter(contactQueryContext, null);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(contact.get(Contact.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate deletedFilter = cb.equal(contact.get(Contact.DELETED), true);
		if (filter != null) {
			filter = cb.and(filter, deletedFilter);
		} else {
			filter = deletedFilter;
		}

		cq.where(filter);
		cq.select(contact.get(Contact.UUID));

		return em.createQuery(cq).getResultList();
	}

	@Override
	protected List<Predicate> getAdditionalObsoleteUuidsPredicates(Date since, CriteriaBuilder cb, CriteriaQuery<String> cq, Root<Contact> from) {

		if (featureConfigurationFacade.isFeatureEnabled(FeatureType.LIMITED_SYNCHRONIZATION)
			&& featureConfigurationFacade
				.isPropertyValueTrue(FeatureType.LIMITED_SYNCHRONIZATION, FeatureTypeProperty.EXCLUDE_NO_CASE_CLASSIFIED_CASES)) {

			ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, from);
			return Collections.singletonList(
				caseService.createObsoleteLimitedSyncCasePredicate(cb, contactQueryContext.getJoins().getCaze(), since, getCurrentUser()));
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	protected Predicate getUserFilterForObsoleteUuids(CriteriaBuilder cb, CriteriaQuery<String> cq, Root<Contact> from) {

		return createUserFilter(new ContactQueryContext(cb, cq, from), new ContactCriteria().excludeLimitedSyncRestrictions(true));
	}

	public Long countContactsForMap(Region region, District district, Disease disease, Date from, Date to) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Contact> contact = cq.from(getElementClass());

		ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contact);

		Predicate filter = createMapContactsFilter(contactQueryContext, region, district, disease, from, to);

		if (filter != null) {
			cq.where(filter);
			cq.select(cb.count(contact.get(Contact.UUID)));

			return em.createQuery(cq).getSingleResult();
		}

		return 0L;
	}

	public List<MapContactDto> getContactsForMap(Region region, District district, Disease disease, Date from, Date to) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MapContactDto> cq = cb.createQuery(MapContactDto.class);
		Root<Contact> contact = cq.from(getElementClass());
		ContactQueryContext cqc = new ContactQueryContext(cb, cq, contact);

		ContactJoins joins = cqc.getJoins();
		Join<Contact, Person> person = joins.getPerson();
		Join<Person, Location> contactPersonAddress = joins.getAddress();
		Join<Contact, Case> caze = joins.getCaze();
		Join<Case, Person> casePerson = joins.getCasePerson();
		Join<Case, Symptoms> symptoms = joins.getCaseJoins().getSymptoms();

		Predicate filter = createMapContactsFilter(cqc, region, district, disease, from, to);

		List<MapContactDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
				contact.get(Contact.UUID),
				contact.get(Contact.CONTACT_CLASSIFICATION),
				contact.get(Contact.REPORT_LAT),
				contact.get(Contact.REPORT_LON),
				contactPersonAddress.get(Location.LATITUDE),
				contactPersonAddress.get(Location.LONGITUDE),
				symptoms.get(Symptoms.ONSET_DATE),
				caze.get(Case.REPORT_DATE),
				contact.get(Contact.REPORT_DATE_TIME),
				person.get(Person.FIRST_NAME),
				person.get(Person.LAST_NAME),
				casePerson.get(Person.FIRST_NAME),
				casePerson.get(Person.LAST_NAME));

			result = em.createQuery(cq).getResultList();
			// #1274 Temporarily disabled because it severely impacts the performance of the
			// Dashboard
			// for (MapContactDto mapContactDto : result) {
			// Visit lastVisit =
			// visitService.getLastVisitByContact(getByUuid(mapContactDto.getUuid()),
			// VisitStatus.COOPERATIVE);
			// if (lastVisit != null) {
			// mapContactDto.setLastVisitDateTime(lastVisit.getVisitDateTime());
			// }
			// }
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	private Predicate createMapContactsFilter(ContactQueryContext cqc, Region region, District district, Disease disease, Date from, Date to) {

		From<?, Contact> contactRoot = cqc.getRoot();
		CriteriaBuilder cb = cqc.getCriteriaBuilder();
		ContactJoins joins = cqc.getJoins();

		Predicate filter = createActiveContactsFilter(cb, contactRoot);
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cqc, null));

		// Filter contacts by date; only consider contacts with reportdates within the given timeframe
		Predicate reportDateFilter =
			cb.between(contactRoot.get(Contact.REPORT_DATE_TIME), DateHelper.getStartOfDay(from), DateHelper.getEndOfDay(to));
		filter = CriteriaBuilderHelper.and(cb, filter, reportDateFilter);

		filter =
			CriteriaBuilderHelper.and(cb, filter, getRegionDistrictDiseasePredicate(region, district, disease, cb, contactRoot, joins.getCaze()));

		// Only retrieve contacts that are currently under follow-up
		Predicate followUpFilter = cb.equal(contactRoot.get(Contact.FOLLOW_UP_STATUS), FollowUpStatus.FOLLOW_UP);
		filter = CriteriaBuilderHelper.and(cb, filter, followUpFilter);

		// only retrieve contacts with given coordinates
		Predicate personLatLonNotNull = CriteriaBuilderHelper.and(
			cb,
			cb.isNotNull(joins.getPersonJoins().getAddress().get(Location.LONGITUDE)),
			cb.isNotNull(joins.getPersonJoins().getAddress().get(Location.LATITUDE)));
		Predicate reportLatLonNotNull =
			CriteriaBuilderHelper.and(cb, cb.isNotNull(contactRoot.get(Contact.REPORT_LON)), cb.isNotNull(contactRoot.get(Contact.REPORT_LAT)));
		Predicate latLonProvided = CriteriaBuilderHelper.or(cb, personLatLonNotNull, reportLatLonNotNull);
		filter = CriteriaBuilderHelper.and(cb, filter, latLonProvided);

		return filter;
	}

	public List<DashboardContactDto> getContactsForDashboard(Region region, District district, Disease disease, Date from, Date to) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardContactDto> cq = cb.createQuery(DashboardContactDto.class);
		Root<Contact> contact = cq.from(getElementClass());
		ContactQueryContext cqc = new ContactQueryContext(cb, cq, contact);
		Join<Contact, Case> caze = cqc.getJoins().getCaze();

		Predicate filter = createDefaultFilter(cb, contact);
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cqc, null));

		Predicate dateFilter = buildDateFilter(cb, contact, from, to);
		if (filter != null) {
			filter = cb.and(filter, dateFilter);
		} else {
			filter = dateFilter;
		}

		filter = CriteriaBuilderHelper.and(cb, filter, getRegionDistrictDiseasePredicate(region, district, disease, cb, contact, caze));

		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
				contact.get(AbstractDomainObject.ID),
				contact.get(Contact.REPORT_DATE_TIME),
				contact.get(Contact.CONTACT_STATUS),
				contact.get(Contact.CONTACT_CLASSIFICATION),
				contact.get(Contact.FOLLOW_UP_STATUS),
				contact.get(Contact.FOLLOW_UP_UNTIL),
				contact.get(Contact.DISEASE),
				contact.get(Contact.QUARANTINE_FROM),
				contact.get(Contact.QUARANTINE_TO));

			List<DashboardContactDto> dashboardContacts = em.createQuery(cq).getResultList();

			if (!dashboardContacts.isEmpty()) {
				List<Long> dashboardContactIds = dashboardContacts.stream().map(DashboardContactDto::getId).collect(Collectors.toList());
				List<DashboardVisit> contactVisits = new ArrayList<>();
				IterableHelper.executeBatched(
					dashboardContactIds,
					ModelConstants.PARAMETER_LIMIT,
					batchedVisitIds -> contactVisits.addAll(getContactDashboardVisits(batchedVisitIds, contact)));

				// Add visit information to the DashboardContactDtos
				for (DashboardContactDto dashboardContact : dashboardContacts) {
					List<DashboardVisit> visits =
						contactVisits.stream().filter(v -> v.getContactId() == dashboardContact.getId()).collect(Collectors.toList());

					DashboardVisit lastVisit = visits.stream().max((v1, v2) -> v1.getVisitDateTime().compareTo(v2.getVisitDateTime())).orElse(null);

					if (lastVisit != null) {
						dashboardContact.setLastVisitDateTime(lastVisit.getVisitDateTime());
						dashboardContact.setLastVisitStatus(lastVisit.getVisitStatus());
						dashboardContact.setSymptomatic(lastVisit.isSymptomatic());

						List<VisitStatus> visitStatuses = visits.stream().map(DashboardVisit::getVisitStatus).collect(Collectors.toList());
						Map<VisitStatus, Integer> frequency = new EnumMap<>(VisitStatus.class);
						for (VisitStatus status : VisitStatus.values()) {
							int freq = Collections.frequency(visitStatuses, status);
							frequency.put(status, freq);
						}

						dashboardContact.setVisitStatusMap(frequency);
					}
				}

				return dashboardContacts;
			}
		}

		return Collections.emptyList();
	}

	private List<DashboardVisit> getContactDashboardVisits(List<Long> dashboardContactIds, Root<Contact> contact) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardVisit> visitsCq = cb.createQuery(DashboardVisit.class);
		Root<Contact> visitsCqRoot = visitsCq.from(getElementClass());
		Join<Contact, Visit> visitsJoin = visitsCqRoot.join(Contact.VISITS, JoinType.LEFT);
		Join<Visit, Symptoms> visitSymptomsJoin = visitsJoin.join(Visit.SYMPTOMS, JoinType.LEFT);

		visitsCq
			.where(CriteriaBuilderHelper.and(cb, contact.get(Contact.ID).in(dashboardContactIds), cb.isNotEmpty(visitsCqRoot.get(Contact.VISITS))));
		visitsCq.multiselect(
			visitsCqRoot.get(Visit.ID),
			visitSymptomsJoin.get(Symptoms.SYMPTOMATIC),
			visitsJoin.get(Visit.VISIT_STATUS),
			visitsJoin.get(Visit.VISIT_DATE_TIME));

		return em.createQuery(visitsCq).getResultList();
	}

	public Predicate getRegionDistrictDiseasePredicate(
		Region region,
		District district,
		Disease disease,
		CriteriaBuilder cb,
		From<?, Contact> contact,
		Join<Contact, Case> caze) {

		Predicate filter = null;

		if (region != null) {
			Predicate regionFilter = cb.or(
				cb.equal(contact.get(Contact.REGION), region),
				cb.and(cb.isNull(contact.get(Contact.REGION)), cb.equal(caze.get(Case.REGION), region)));

			filter = CriteriaBuilderHelper.and(cb, filter, regionFilter);
		}

		if (district != null) {
			Predicate districtFilter = cb.or(
				cb.equal(contact.get(Contact.DISTRICT), district),
				cb.and(cb.isNull(contact.get(Contact.DISTRICT)), cb.equal(caze.get(Case.DISTRICT), district)));

			filter = CriteriaBuilderHelper.and(cb, filter, districtFilter);
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(contact.get(Contact.DISEASE), disease);

			filter = CriteriaBuilderHelper.and(cb, filter, diseaseFilter);
		}
		return filter;
	}

	public Map<ContactStatus, Long> getNewContactCountPerStatus(ContactCriteria contactCriteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Contact> contact = cq.from(getElementClass());
		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contact);

		Predicate filter = createUserFilter(contactQueryContext, null);
		Predicate criteriaFilter = buildCriteriaFilter(contactCriteria, contactQueryContext);
		Predicate notDeleted = cb.isFalse(contact.get(Contact.DELETED));
		if (filter != null) {
			filter = cb.and(filter, criteriaFilter, notDeleted);
		} else {
			filter = cb.and(criteriaFilter, notDeleted);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(contact.get(Contact.CONTACT_STATUS));
		cq.multiselect(contact.get(Contact.CONTACT_STATUS), cb.count(contact));
		List<Object[]> results = em.createQuery(cq).getResultList();

		return results.stream().collect(Collectors.toMap(e -> (ContactStatus) e[0], e -> (Long) e[1]));
	}

	public Map<ContactClassification, Long> getNewContactCountPerClassification(ContactCriteria contactCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Contact> contact = cq.from(getElementClass());
		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contact);

		Predicate filter = createUserFilter(contactQueryContext, contactCriteria);
		Predicate criteriaFilter = buildCriteriaFilter(contactCriteria, contactQueryContext);
		Predicate notDeleted = cb.isFalse(contact.get(Contact.DELETED));
		if (filter != null) {
			filter = cb.and(filter, criteriaFilter, notDeleted);
		} else {
			filter = cb.and(criteriaFilter, notDeleted);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(contact.get(Contact.CONTACT_CLASSIFICATION));
		cq.multiselect(contact.get(Contact.CONTACT_CLASSIFICATION), cb.count(contact));
		List<Object[]> results = em.createQuery(cq).getResultList();

		return results.stream().collect(Collectors.toMap(e -> (ContactClassification) e[0], e -> (Long) e[1]));
	}

	public Map<FollowUpStatus, Long> getNewContactCountPerFollowUpStatus(ContactCriteria contactCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Contact> contact = cq.from(getElementClass());
		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contact);

		Predicate filter = createUserFilter(contactQueryContext, null);
		Predicate criteriaFilter = buildCriteriaFilter(contactCriteria, contactQueryContext);
		Predicate notDeleted = cb.isFalse(contact.get(Contact.DELETED));
		if (filter != null) {
			filter = cb.and(filter, criteriaFilter, notDeleted);
		} else {
			filter = cb.and(criteriaFilter, notDeleted);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(contact.get(Contact.FOLLOW_UP_STATUS));
		cq.multiselect(contact.get(Contact.FOLLOW_UP_STATUS), cb.count(contact));
		List<Object[]> results = em.createQuery(cq).getResultList();

		return results.stream().collect(Collectors.toMap(e -> (FollowUpStatus) e[0], e -> (Long) e[1]));
	}

	public int getFollowUpUntilCount(ContactCriteria contactCriteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Contact> contact = cq.from(getElementClass());
		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contact);

		Predicate filter = createUserFilter(contactQueryContext, null);
		Predicate criteriaFilter = buildCriteriaFilter(contactCriteria, contactQueryContext);
		if (filter != null) {
			filter = cb.and(filter, criteriaFilter);
		} else {
			filter = criteriaFilter;
		}

		cq.select(cb.count(contact));

		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getSingleResult().intValue();
	}

	/**
	 * Calculates resultingCase and contact status based on: - existing disease
	 * cases (and classification) of the person - the incubation period - the
	 * contact classification - the follow-up status
	 */
	public void udpateContactStatus(Contact contact) {

		ContactClassification contactClassification = contact.getContactClassification();
		if (contactClassification == null) { // Fall-back
			contactClassification = ContactClassification.UNCONFIRMED;
		}

		switch (contactClassification) {
		case UNCONFIRMED:
			contact.setContactStatus(ContactStatus.ACTIVE);
			break;
		case NO_CONTACT:
			contact.setContactStatus(ContactStatus.DROPPED);
			cancelFollowUp(contact, I18nProperties.getString(Strings.messageSystemFollowUpCanceledByDropping));
			break;
		case CONFIRMED:
			if (contact.getResultingCase() != null) {
				contact.setContactStatus(ContactStatus.CONVERTED);
			} else {
				if (contact.getFollowUpStatus() != null) {
					switch (contact.getFollowUpStatus()) {
					case CANCELED:
					case COMPLETED:
					case LOST:
					case NO_FOLLOW_UP:
						contact.setContactStatus(ContactStatus.DROPPED);
						break;
					case FOLLOW_UP:
						contact.setContactStatus(ContactStatus.ACTIVE);
						break;
					default:
						throw new NoSuchElementException(contact.getFollowUpStatus().toString());
					}
				} else {
					contact.setContactStatus(ContactStatus.ACTIVE);
				}
			}
			break;
		default:
			throw new NoSuchElementException(DataHelper.toStringNullable(contactClassification));
		}
		externalJournalService.handleExternalJournalPersonUpdateAsync(contact.getPerson().toReference());
		ensurePersisted(contact);
	}

	/**
	 * Calculates and sets the follow-up until date and status of the contact. If
	 * the date has been overwritten by a user, only the status changes and
	 * extensions of the follow-up until date based on missed visits are executed.
	 * <ul>
	 * <li>Disease with no follow-up: Leave empty and set follow-up status to "No
	 * follow-up"</li>
	 * <li>Others: Use follow-up duration of the disease. Reference for calculation
	 * is the reporting date (since this is always later than the last contact date
	 * and we can't be sure the last contact date is correct) If the last visit was
	 * not cooperative and happened at the last date of contact tracing, we need to
	 * do an additional visit.</li>
	 * </ul>
	 */
	public void updateFollowUpDetails(Contact contact, boolean followUpStatusChangedByUser) {

		Disease disease = contact.getDisease();
		boolean changeStatus = contact.getFollowUpStatus() != FollowUpStatus.CANCELED && contact.getFollowUpStatus() != FollowUpStatus.LOST;
		boolean statusChangedBySystem = false;

		ContactProximity contactProximity = contact.getContactProximity();
		if (!diseaseConfigurationFacade.hasFollowUp(disease) || (contactProximity != null && !contactProximity.hasFollowUp())) {
			contact.setFollowUpUntil(null);
			contact.setOverwriteFollowUpUntil(false);
			if (changeStatus) {
				contact.setFollowUpStatus(FollowUpStatus.NO_FOLLOW_UP);
				statusChangedBySystem = true;
			}
		} else {
			ContactDto contactDto = ContactFacadeEjb.toContactDto(contact);
			Date currentFollowUpUntil = contact.getFollowUpUntil();

			Date earliestSampleDate = sampleService.getEarliestSampleDate(contact.getSamples());

			Date untilDate =
				ContactLogic
					.calculateFollowUpUntilDate(
						contactDto,
						ContactLogic.getFollowUpStartDate(contact.getLastContactDate(), contact.getReportDateTime(), earliestSampleDate),
						contact.getVisits().stream().map(VisitFacadeEjb::toVisitDto).collect(Collectors.toList()),
						diseaseConfigurationFacade.getFollowUpDuration(contact.getDisease()),
						false,
						featureConfigurationFacade
							.isPropertyValueTrue(FeatureType.CONTACT_TRACING, FeatureTypeProperty.ALLOW_FREE_FOLLOW_UP_OVERWRITE))
					.getFollowUpEndDate();
			contact.setFollowUpUntil(untilDate);
			if (DateHelper.getStartOfDay(currentFollowUpUntil).before(DateHelper.getStartOfDay(untilDate))) {
				contact.setOverwriteFollowUpUntil(false);
			}
			if (changeStatus) {
				Visit lastVisit = contact.getVisits().stream().max(Comparator.comparing(Visit::getVisitDateTime)).orElse(null);
				if (lastVisit != null && !DateHelper.getStartOfDay(lastVisit.getVisitDateTime()).before(DateHelper.getStartOfDay(untilDate))) {
					contact.setFollowUpStatus(FollowUpStatus.COMPLETED);
				} else {
					contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
				}

				if (contact.getFollowUpStatus() != FollowUpStatus.COMPLETED && contact.getContactStatus() == ContactStatus.CONVERTED) {
					// Cancel follow-up if the contact was converted to a case
					contact.setFollowUpStatus(FollowUpStatus.CANCELED);
					addToFollowUpStatusComment(contact, I18nProperties.getString(Strings.messageSystemFollowUpCanceled));
				}
				statusChangedBySystem = true;
			}
		}

		if (followUpStatusChangedByUser) {
			contact.setFollowUpStatusChangeDate(new Date());
			contact.setFollowUpStatusChangeUser(getCurrentUser());
		} else if (statusChangedBySystem) {
			contact.setFollowUpStatusChangeDate(null);
			contact.setFollowUpStatusChangeUser(null);
		}
		externalJournalService.handleExternalJournalPersonUpdateAsync(contact.getPerson().toReference());
		ensurePersisted(contact);
	}

	public void cancelFollowUp(Contact contact, String comment) {
		contact.setFollowUpStatus(FollowUpStatus.CANCELED);
		addToFollowUpStatusComment(contact, comment);
		externalJournalService.handleExternalJournalPersonUpdateAsync(contact.getPerson().toReference());
		ensurePersisted(contact);
	}

	private void addToFollowUpStatusComment(Contact contact, String comment) {
		String finalFollowUpComment = ContactLogic.extendFollowUpStatusComment(contact.getFollowUpComment(), comment);
		if (finalFollowUpComment.length() <= FieldConstraints.CHARACTER_LIMIT_BIG) {
			contact.setFollowUpComment(finalFollowUpComment);
		}
	}

	// Used only for testing; directly retrieve the contacts from the visit instead
	public List<Contact> getAllByVisit(Visit visit) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(Contact.class);
		Root<Visit> visitRoot = cq.from(Visit.class);

		cq.where(cb.equal(visitRoot.get(Visit.ID), visit.getId()));
		cq.select(visitRoot.get(Visit.CONTACTS));

		return em.createQuery(cq).getResultList();
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createUserFilterInternal(CriteriaBuilder cb, CriteriaQuery cq, From<?, Contact> from) {
		return createUserFilter(new ContactQueryContext(cb, cq, from));
	}

	public Predicate createUserFilter(ContactQueryContext queryContext) {
		return createUserFilter(queryContext, null);
	}

	public Predicate createUserFilter(ContactQueryContext contactQueryContext, ContactCriteria contactCriteria) {

		Predicate userFilter;

		CriteriaQuery<?> cq = contactQueryContext.getQuery();
		CriteriaBuilder cb = contactQueryContext.getCriteriaBuilder();

		CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, contactQueryContext.getJoins().getCaseJoins());
		if (contactCriteria == null || contactCriteria.getIncludeContactsFromOtherJurisdictions()) {
			userFilter = caseService.createUserFilter(caseQueryContext);
		} else {
			CaseUserFilterCriteria userFilterCriteria =
				new CaseUserFilterCriteria().excludeLimitedSyncRestrictions(contactCriteria.isExcludeLimitedSyncRestrictions());
			userFilter = caseService.createUserFilter(caseQueryContext, userFilterCriteria);
		}

		Predicate filter;
		if (userFilter != null) {
			filter = CriteriaBuilderHelper.or(cb, createUserFilterWithoutCase(contactQueryContext, contactCriteria), userFilter);
		} else {
			filter = createUserFilterWithoutCase(contactQueryContext, contactCriteria);
		}

		if (RequestContextHolder.isMobileSync()) {
			Predicate limitedChangeDatePredicate = CriteriaBuilderHelper.and(cb, createLimitedChangeDateFilter(cb, contactQueryContext.getRoot()));
			if (limitedChangeDatePredicate != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, limitedChangeDatePredicate);
			}
		}

		return filter;
	}

	public Predicate createUserFilterWithoutCase(ContactQueryContext qc) {
		return createUserFilterWithoutCase(qc, null);
	}

	public Predicate createUserFilterWithoutCase(ContactQueryContext qc, ContactCriteria contactCriteria) {

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}

		CriteriaBuilder cb = qc.getCriteriaBuilder();
		CriteriaQuery<?> cq = qc.getQuery();
		From<?, Contact> contactRoot = qc.getRoot();

		Predicate filter = null;

		final boolean restrictedToAssignedEntities = isRestrictedToAssignedEntities();
		if (restrictedToAssignedEntities) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(contactRoot.get(Contact.CONTACT_OFFICER).get(User.ID), getCurrentUser().getId()));
		}

		// National users can access all contacts in the system
		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if ((jurisdictionLevel == JurisdictionLevel.NATION && !UserRole.isPortHealthUser(currentUser.getUserRoles()))) {
			return CriteriaBuilderHelper.limitedDiseasePredicate(cb, currentUser, contactRoot.get(Contact.DISEASE));
		}

		// whoever created it or is assigned to it is allowed to access it
		if (contactCriteria == null || contactCriteria.getIncludeContactsFromOtherJurisdictions()) {
			filter = cb.equal(contactRoot.get(Contact.REPORTING_USER), currentUser);
			filter = cb.or(filter, cb.equal(contactRoot.get(Contact.CONTACT_OFFICER), currentUser));
		}

		if (!restrictedToAssignedEntities) {
			switch (jurisdictionLevel) {
			case REGION:
				final Region region = currentUser.getRegion();
				if (region != null) {
					filter = CriteriaBuilderHelper.or(cb, filter, cb.equal(contactRoot.get(Contact.REGION), currentUser.getRegion()));
				}
				break;
			case DISTRICT:
				final District district = currentUser.getDistrict();
				if (district != null) {
					filter = CriteriaBuilderHelper.or(cb, filter, cb.equal(contactRoot.get(Contact.DISTRICT), currentUser.getDistrict()));
				}
				break;
			case COMMUNITY:
				final Community community = currentUser.getCommunity();
				if (community != null) {
					filter = CriteriaBuilderHelper.or(cb, filter, cb.equal(contactRoot.get(Contact.COMMUNITY), currentUser.getCommunity()));
				}
				break;
			case LABORATORY:
				final Subquery<Long> sampleSubQuery = cq.subquery(Long.class);
				final Root<Sample> sampleRoot = sampleSubQuery.from(Sample.class);
				final SampleJoins joins = new SampleJoins(sampleRoot);
				final Join<Sample, Contact> contactJoin = joins.getContact();

				sampleSubQuery.where(cb.and(cb.equal(contactJoin, contactRoot), sampleService.createUserFilterWithoutAssociations(cb, joins)));
				sampleSubQuery.select(sampleRoot.get(Sample.ID));
				filter = CriteriaBuilderHelper.or(cb, filter, cb.exists(sampleSubQuery));
				break;
			default:
			}
		}

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			CriteriaBuilderHelper.limitedDiseasePredicate(
				cb,
				currentUser,
				contactRoot.get(Contact.DISEASE),
				cb.or(
					cb.isNull(contactRoot.get(Contact.DISEASE)),
					cb.equal(contactRoot.get(Contact.REPORTING_USER).get(User.ID), currentUser.getId()))));

		if ((contactCriteria == null || !contactCriteria.isExcludeLimitedSyncRestrictions())
			&& featureConfigurationFacade
				.isPropertyValueTrue(FeatureType.LIMITED_SYNCHRONIZATION, FeatureTypeProperty.EXCLUDE_NO_CASE_CLASSIFIED_CASES)
			&& RequestContextHolder.isMobileSync()) {
			final Predicate limitedCaseSyncPredicate = caseService.createLimitedSyncCasePredicate(cb, qc.getJoins().getCaze(), currentUser);
			filter = CriteriaBuilderHelper.and(cb, filter, limitedCaseSyncPredicate);
		}

		return filter;
	}

	public Predicate buildCriteriaFilter(ContactCriteria contactCriteria, ContactQueryContext cqc) {

		final ContactJoins joins = cqc.getJoins();
		final CriteriaBuilder cb = cqc.getCriteriaBuilder();
		final From<?, Contact> from = cqc.getRoot();

		Predicate filter = null;

		PersonQueryContext personQueryContext = new PersonQueryContext(cb, cqc.getQuery(), joins.getPersonJoins());

		if (contactCriteria.getReportingUserRole() != null) {
			Join<Contact, User> reportingUser = joins.getReportingUser();
			Join<User, UserRole> rolesJoin = reportingUser.join(User.USER_ROLES, JoinType.LEFT);
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(rolesJoin.get(UserRole.UUID), contactCriteria.getReportingUserRole().getUuid()));
		}
		if (contactCriteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.DISEASE), contactCriteria.getDisease()));
		}
		if (contactCriteria.getCaze() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getCaze().get(Case.UUID), contactCriteria.getCaze().getUuid()));
		}
		if (contactCriteria.getResultingCase() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getResultingCase().get(Case.UUID), contactCriteria.getResultingCase().getUuid()));
		}
		if (contactCriteria.getRegion() != null) {
			String regionUuid = contactCriteria.getRegion().getUuid();
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				CriteriaBuilderHelper.or(
					cb,
					cb.equal(joins.getRegion().get(Region.UUID), regionUuid),
					cb.equal(joins.getCaseRegion().get(Region.UUID), regionUuid),
					cb.equal(joins.getCaseResponsibleRegion().get(Region.UUID), regionUuid),
					cb.equal(joins.getResultingCaseJoins().getRegion().get(Region.UUID), regionUuid),
					cb.equal(joins.getResultingCaseJoins().getResponsibleRegion().get(Region.UUID), regionUuid)));
		}
		if (contactCriteria.getDistrict() != null) {
			String districtUuid = contactCriteria.getDistrict().getUuid();
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				CriteriaBuilderHelper.or(
					cb,
					cb.equal(joins.getDistrict().get(District.UUID), districtUuid),
					cb.equal(joins.getCaseDistrict().get(District.UUID), districtUuid),
					cb.equal(joins.getCaseResponsibleDistrict().get(District.UUID), districtUuid)));
		}
		if (contactCriteria.getCommunity() != null) {
			String communityUuid = contactCriteria.getCommunity().getUuid();
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				CriteriaBuilderHelper.or(
					cb,
					cb.equal(joins.getCommunity().get(Community.UUID), communityUuid),
					cb.equal(joins.getCaseCommunity().get(Community.UUID), communityUuid),
					cb.equal(joins.getCaseResponsibleCommunity().get(Community.UUID), communityUuid)));
		}
		if (contactCriteria.getContactOfficer() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getContactOfficer().get(User.UUID), contactCriteria.getContactOfficer().getUuid()));
		}
		if (contactCriteria.getContactClassification() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.CONTACT_CLASSIFICATION), contactCriteria.getContactClassification()));
		}
		if (contactCriteria.getContactStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.CONTACT_STATUS), contactCriteria.getContactStatus()));
		}
		if (contactCriteria.getFollowUpStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.FOLLOW_UP_STATUS), contactCriteria.getFollowUpStatus()));
		}
		if (contactCriteria.getReportDateFrom() != null && contactCriteria.getReportDateTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.between(from.get(Contact.REPORT_DATE_TIME), contactCriteria.getReportDateFrom(), contactCriteria.getReportDateTo()));
		} else if (contactCriteria.getReportDateFrom() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.greaterThanOrEqualTo(from.get(Contact.REPORT_DATE_TIME), contactCriteria.getReportDateFrom()));
		} else if (contactCriteria.getReportDateTo() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(from.get(Contact.REPORT_DATE_TIME), contactCriteria.getReportDateTo()));
		}
		if (contactCriteria.getLastContactDateFrom() != null && contactCriteria.getLastContactDateTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.between(from.get(Contact.LAST_CONTACT_DATE), contactCriteria.getLastContactDateFrom(), contactCriteria.getLastContactDateTo()));
		}
		if (contactCriteria.getCreationDateFrom() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.greaterThan(from.get(Contact.CREATION_DATE), DateHelper.getStartOfDay(contactCriteria.getCreationDateFrom())));
		}
		if (contactCriteria.getCreationDateTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.lessThan(from.get(Contact.CREATION_DATE), DateHelper.getEndOfDay(contactCriteria.getCreationDateTo())));
		}
		if (contactCriteria.getFollowUpUntilFrom() != null && contactCriteria.getFollowUpUntilTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.between(from.get(Contact.FOLLOW_UP_UNTIL), contactCriteria.getFollowUpUntilFrom(), contactCriteria.getFollowUpUntilTo()));
		} else if (contactCriteria.getFollowUpUntilFrom() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.greaterThanOrEqualTo(from.get(Contact.FOLLOW_UP_UNTIL), contactCriteria.getFollowUpUntilFrom()));
		} else if (contactCriteria.getFollowUpUntilTo() != null) {
			if (!Boolean.TRUE.equals(contactCriteria.getFollowUpUntilToPrecise())) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.lessThanOrEqualTo(from.get(Contact.FOLLOW_UP_UNTIL), contactCriteria.getFollowUpUntilTo()));
			} else {
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					cb.between(
						from.get(Contact.FOLLOW_UP_UNTIL),
						DateHelper.getStartOfDay(contactCriteria.getFollowUpUntilTo()),
						DateHelper.getEndOfDay(contactCriteria.getFollowUpUntilTo())));
			}
		}
		if (contactCriteria.getSymptomJournalStatus() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getPerson().get(Person.SYMPTOM_JOURNAL_STATUS), contactCriteria.getSymptomJournalStatus()));
		}
		if (contactCriteria.getVaccinationStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.VACCINATION_STATUS), contactCriteria.getVaccinationStatus()));
		}
		if (contactCriteria.getRelationToCase() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.RELATION_TO_CASE), contactCriteria.getRelationToCase()));
		}
		if (contactCriteria.getQuarantineTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.between(
					from.get(Contact.QUARANTINE_TO),
					DateHelper.getStartOfDay(contactCriteria.getQuarantineTo()),
					DateHelper.getEndOfDay(contactCriteria.getQuarantineTo())));
		}
		if (contactCriteria.getQuarantineType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.QUARANTINE), contactCriteria.getQuarantineType()));
		}
		if (Boolean.TRUE.equals(contactCriteria.getOnlyQuarantineHelpNeeded())) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.and(cb.notEqual(from.get(Contact.QUARANTINE_HELP_NEEDED), ""), cb.isNotNull(from.get(Contact.QUARANTINE_HELP_NEEDED))));
		}
		if (Boolean.TRUE.equals(contactCriteria.getQuarantineOrderedVerbally())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(Contact.QUARANTINE_ORDERED_VERBALLY)));
		}
		if (Boolean.TRUE.equals(contactCriteria.getQuarantineOrderedOfficialDocument())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(Contact.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT)));
		}
		if (Boolean.TRUE.equals(contactCriteria.getQuarantineNotOrdered())) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.and(
					cb.isFalse(from.get(Contact.QUARANTINE_ORDERED_VERBALLY)),
					cb.isFalse(from.get(Contact.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT))));
		}
		if (Boolean.TRUE.equals(contactCriteria.getWithExtendedQuarantine())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(Contact.QUARANTINE_EXTENDED)));
		}
		if (Boolean.TRUE.equals(contactCriteria.getWithReducedQuarantine())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(from.get(Contact.QUARANTINE_REDUCED)));
		}
		if (contactCriteria.getRelevanceStatus() != null) {
			if (contactCriteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.and(cb.or(cb.equal(from.get(Contact.ARCHIVED), false), cb.isNull(from.get(Contact.ARCHIVED)))));
			} else if (contactCriteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.ARCHIVED), true));
			} else if (contactCriteria.getRelevanceStatus() == EntityRelevanceStatus.DELETED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Case.DELETED), true));
			}
		}
		if (contactCriteria.getRelevanceStatus() != EntityRelevanceStatus.DELETED) {
			filter = CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from));
		}

		if (!DataHelper.isNullOrEmpty(contactCriteria.getPersonLike())) {
			Join<Contact, Person> person = joins.getPerson();
			Join<Person, Location> location = joins.getAddress();
			Predicate likeFilters = CriteriaBuilderHelper.buildFreeTextSearchPredicate(
				cb,
				contactCriteria.getPersonLike(),
				textFilter -> cb.or(
					// We should allow short and long versions of the UUID so let's do a LIKE behaving like a "starts with"
					CriteriaBuilderHelper.ilikePrecise(cb, person.get(Person.UUID), textFilter + "%"),
					CriteriaBuilderHelper.unaccentedIlike(cb, person.get(Person.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, person.get(Person.LAST_NAME), textFilter),
					phoneNumberPredicate(cb, personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_PHONE_SUBQUERY), textFilter),
					CriteriaBuilderHelper
						.unaccentedIlike(cb, personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_EMAIL_SUBQUERY), textFilter),
					CriteriaBuilderHelper
						.unaccentedIlike(cb, personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_PRIMARY_OTHER_SUBQUERY), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, location.get(Location.CITY), textFilter),
					CriteriaBuilderHelper.ilike(cb, location.get(Location.POSTAL_CODE), textFilter)));

			filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
		}

		if (!DataHelper.isNullOrEmpty(contactCriteria.getContactOrCaseLike())) {
			Join<Case, Person> casePerson = joins.getCasePerson();
			String[] textFilters = contactCriteria.getContactOrCaseLike().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, from.get(Contact.UUID), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(Contact.EXTERNAL_ID), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(Contact.EXTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(Contact.INTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.ilike(cb, joins.getCaze().get(Case.UUID), textFilter),
					CriteriaBuilderHelper.ilike(cb, joins.getCaze().get(Case.EXTERNAL_ID), textFilter),
					CriteriaBuilderHelper.ilike(cb, joins.getCaze().get(Case.EXTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.ilike(cb, joins.getCaze().get(Case.INTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.ilike(cb, joins.getCaze().get(Case.EPID_NUMBER), textFilter),
					CriteriaBuilderHelper.ilikePrecise(cb, casePerson.get(Person.UUID), textFilter + "%"),
					CriteriaBuilderHelper.unaccentedIlike(cb, casePerson.get(Person.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, casePerson.get(Person.LAST_NAME), textFilter),
					phoneNumberPredicate(cb, cqc.getSubqueryExpression(CaseQueryContext.PERSON_PHONE_SUBQUERY), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}

		if (!DataHelper.isNullOrEmpty(contactCriteria.getReportingUserLike())) {
			String[] textFilters = contactCriteria.getReportingUserLike().split("\\s+");
			for (String s : textFilters) {
				String textFilter = formatForLike(s);
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
						CriteriaBuilderHelper.unaccentedIlike(cb, joins.getReportingUser().get(User.FIRST_NAME), textFilter),
						CriteriaBuilderHelper.unaccentedIlike(cb, joins.getReportingUser().get(User.LAST_NAME), textFilter),
						CriteriaBuilderHelper.unaccentedIlike(cb, joins.getReportingUser().get(User.USER_NAME), textFilter));
					filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
				}
			}
		}

		if (Boolean.TRUE.equals(contactCriteria.getOnlyHighPriorityContacts())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.HIGH_PRIORITY), true));
		}
		if (contactCriteria.getContactCategory() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.CONTACT_CATEGORY), contactCriteria.getContactCategory()));
		}
		if (contactCriteria.getCaseClassification() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getCaze().get(Case.CASE_CLASSIFICATION), contactCriteria.getCaseClassification()));
		}
		if (contactCriteria.getPerson() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getPerson().get(Person.UUID), contactCriteria.getPerson().getUuid()));
		}
		if (contactCriteria.getBirthdateYYYY() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getPerson().get(Person.BIRTHDATE_YYYY), contactCriteria.getBirthdateYYYY()));
		}
		if (contactCriteria.getBirthdateMM() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getPerson().get(Person.BIRTHDATE_MM), contactCriteria.getBirthdateMM()));
		}
		if (contactCriteria.getBirthdateDD() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getPerson().get(Person.BIRTHDATE_DD), contactCriteria.getBirthdateDD()));
		}
		if (contactCriteria.getReturningTraveler() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Contact.RETURNING_TRAVELER), contactCriteria.getReturningTraveler()));
		}
		boolean hasEventLikeCriteria = StringUtils.isNotBlank(contactCriteria.getEventLike());
		boolean hasOnlyContactsSharingEventWithSourceCase = Boolean.TRUE.equals(contactCriteria.getOnlyContactsSharingEventWithSourceCase());
		if (hasEventLikeCriteria || hasOnlyContactsSharingEventWithSourceCase) {
			Join<Person, EventParticipant> eventParticipant = joins.getEventParticipants();
			Join<EventParticipant, Event> event = joins.getEvent();

			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.isFalse(event.get(Event.DELETED)), cb.isFalse(eventParticipant.get(EventParticipant.DELETED)));

			if (hasEventLikeCriteria) {
				String[] textFilters = contactCriteria.getEventLike().trim().split("\\s+");
				for (String textFilter : textFilters) {
					if (DataHelper.isNullOrEmpty(textFilter)) {
						continue;
					}

					Predicate likeFilters = cb.or(
						CriteriaBuilderHelper.unaccentedIlike(cb, event.get(Event.EVENT_DESC), textFilter),
						CriteriaBuilderHelper.unaccentedIlike(cb, event.get(Event.EVENT_TITLE), textFilter),
						CriteriaBuilderHelper.ilike(cb, event.get(Event.UUID), textFilter));
					filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
				}
			}
			if (hasOnlyContactsSharingEventWithSourceCase) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(event.get(Event.UUID), joins.getCaseEvent().get(Event.UUID)));
			}
		}
		if (contactCriteria.getEventUuid() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getEvent().get(Event.UUID), contactCriteria.getEventUuid()));
		}
		if (contactCriteria.getEventParticipant() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getEventParticipants().get(EventParticipant.UUID), contactCriteria.getEventParticipant().getUuid()));
		}
		if (contactCriteria.getOnlyContactsWithSourceCaseInGivenEvent() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(joins.getCaseEvent().get(Event.UUID), contactCriteria.getOnlyContactsWithSourceCaseInGivenEvent().getUuid()));
		}
		if (Boolean.TRUE.equals(contactCriteria.getOnlyContactsFromOtherInstances())) {
			Subquery<Long> sharesSubQuery = cqc.getQuery().subquery(Long.class);
			Root<SormasToSormasShareInfo> sharesRoot = sharesSubQuery.from(SormasToSormasShareInfo.class);
			sharesSubQuery.where(cb.equal(sharesRoot.get(SormasToSormasShareInfo.CONTACT), from));
			sharesSubQuery.select(sharesRoot.get(SormasToSormasShareInfo.ID));

			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.or(cb.exists(sharesSubQuery), cb.isNotNull(from.get(Contact.SORMAS_TO_SORMAS_ORIGIN_INFO))));
		}
		if (contactCriteria.getDiseaseVariant() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getCaze().get(Case.DISEASE_VARIANT), contactCriteria.getDiseaseVariant()));
		}
		if (contactCriteria.getWithOwnership() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, createOwnershipPredicate(Boolean.TRUE.equals(contactCriteria.getWithOwnership()), from, cb, cqc.getQuery()));
		}

		filter = CriteriaBuilderHelper.andInValues(contactCriteria.getUuids(), filter, cb, from.get(Contact.UUID));

		return filter;
	}

	public List<MergeContactIndexDto[]> getContactsForDuplicateMerging(ContactCriteria criteria, @Min(1) Integer limit, boolean ignoreRegion) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

		Root<Contact> root = cq.from(Contact.class);

		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, root);
		ContactJoins joins = contactQueryContext.getJoins();
		Join<Contact, Person> person = joins.getPerson();

		Root<Contact> root2 = cq.from(Contact.class);
		final ContactQueryContext contactQueryContext2 = new ContactQueryContext(cb, cq, root2);
		Join<Contact, Person> person2 = root2.join(Contact.PERSON, JoinType.LEFT);

		// similarity:
		// * first & last name concatenated with whitespace. Similarity function with default threshold of 0.65D
		// uses postgres pg_trgm: https://www.postgresql.org/docs/9.6/pgtrgm.html
		// * same source case
		// * same disease
		// * same region (optional)
		// * report date within 30 days of each other
		// * same sex or same birth date (when defined)
		// * same birth date (when fully defined)

		Predicate sourceCaseFilter = cb.equal(root.get(Contact.CAZE), root2.get(Contact.CAZE));
		Predicate userFilter = createUserFilter(contactQueryContext);
		Predicate criteriaFilter = criteria != null ? buildCriteriaFilter(criteria, contactQueryContext) : null;
		Expression<String> nameSimilarityExpr = cb.concat(person.get(Person.FIRST_NAME), " ");
		nameSimilarityExpr = cb.concat(nameSimilarityExpr, person.get(Person.LAST_NAME));
		Expression<String> nameSimilarityExpr2 = cb.concat(person2.get(Person.FIRST_NAME), " ");
		nameSimilarityExpr2 = cb.concat(nameSimilarityExpr2, person2.get(Person.LAST_NAME));
		Predicate nameSimilarityFilter =
			cb.gt(cb.function("similarity", double.class, nameSimilarityExpr, nameSimilarityExpr2), configFacade.getNameSimilarityThreshold());
		Predicate diseaseFilter = cb.equal(root.get(Contact.DISEASE), root2.get(Contact.DISEASE));
		Predicate reportDateFilter = cb.lessThanOrEqualTo(
			cb.abs(
				cb.diff(
					cb.function("date_part", Double.class, cb.parameter(String.class, "date_type"), root.get(Contact.REPORT_DATE_TIME)),
					cb.function("date_part", Double.class, cb.parameter(String.class, "date_type"), root2.get(Contact.REPORT_DATE_TIME)))),
			SECONDS_30_DAYS);
		// Sex filter: only when sex is filled in for both cases
		Predicate sexFilter = cb.or(
			cb.or(cb.isNull(person.get(Person.SEX)), cb.isNull(person2.get(Person.SEX))),
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

		Predicate filter = CriteriaBuilderHelper.and(
			cb,
			cb.and(createDefaultFilter(cb, root), createDefaultFilter(cb, root2), sourceCaseFilter),
			userFilter,
			criteriaFilter,
			nameSimilarityFilter,
			diseaseFilter,
			reportDateFilter,
			sexFilter,
			birthDateFilter,
			cb.notEqual(root.get(Contact.ID), root2.get(Contact.ID)));

		if (!ignoreRegion) {
			Predicate regionFilter = cb.or(
				cb.or(cb.isNull(root.get(Contact.REGION)), cb.isNull(root2.get(Contact.REGION))),
				cb.equal(root.get(Contact.REGION), root2.get(Contact.REGION)));

			filter = cb.and(filter, regionFilter);
		}

		if (CollectionUtils.isNotEmpty(criteria.getContactUuidsForMerge())) {
			Set<String> contactUuidsForMerge = criteria.getContactUuidsForMerge();
			filter = cb.and(filter, cb.or(root.get(Contact.UUID).in(contactUuidsForMerge), root2.get(Contact.UUID).in(contactUuidsForMerge)));
		}

		cq.where(filter);
		cq.multiselect(root.get(Contact.ID), root2.get(Contact.ID), root.get(Contact.CREATION_DATE));
		cq.orderBy(cb.desc(root.get(Contact.CREATION_DATE)));

		TypedQuery<Object[]> query = em.createQuery(cq).setParameter("date_type", "epoch");
		if (limit != null) {
			// Double the limit because the query result will contain each pair twice; since these duplicates will
			// be removed, the final result list would only contain limit/2 entries otherwise
			query.setMaxResults(limit * 2);
		}

		List<Object[]> foundIds = query.getResultList();

		List<MergeContactIndexDto[]> resultList = new ArrayList<>();
		Set<AbstractMap.SimpleImmutableEntry<Long, Long>> resultIdsSet = new HashSet<>();

		if (!foundIds.isEmpty()) {
			CriteriaQuery<MergeContactIndexDto> indexContactsCq = cb.createQuery(MergeContactIndexDto.class);
			Root<Contact> indexRoot = indexContactsCq.from(Contact.class);
			selectMergeIndexDtoFields(cb, indexContactsCq, indexRoot);
			indexContactsCq.where(
				indexRoot.get(Contact.ID).in(foundIds.stream().map(a -> Arrays.copyOf(a, 2)).flatMap(Arrays::stream).collect(Collectors.toSet())));
			Map<Long, MergeContactIndexDto> indexContacts =
				em.createQuery(indexContactsCq).getResultStream().collect(Collectors.toMap(c -> c.getId(), Function.identity()));

			for (Object[] idPair : foundIds) {
				// Skip duplicate pairs - duplications always happen in reverse order, i.e. if idPair[0]/idPair[1]
				// is already in the result set in this order, the duplication would be added as idPair[1]/idPair[0]
				if (resultIdsSet.contains(new AbstractMap.SimpleImmutableEntry<>(idPair[1], idPair[0]))) {
					continue;
				}

				try {
					// Cloning is necessary here to allow us to add the same CaseIndexDto to the grid multiple times
					MergeContactIndexDto parent = (MergeContactIndexDto) indexContacts.get(idPair[0]).clone();
					MergeContactIndexDto child = (MergeContactIndexDto) indexContacts.get(idPair[1]).clone();

					if (parent.getCompleteness() == null && child.getCompleteness() == null
						|| parent.getCompleteness() != null
							&& (child.getCompleteness() == null || (parent.getCompleteness() >= child.getCompleteness()))) {
						resultList.add(
							new MergeContactIndexDto[] {
								parent,
								child });
					} else {
						resultList.add(
							new MergeContactIndexDto[] {
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

	private void selectMergeIndexDtoFields(CriteriaBuilder cb, CriteriaQuery<MergeContactIndexDto> cq, Root<Contact> root) {
		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, root);
		cq.multiselect(listCriteriaBuilder.getMergeContactIndexSelections(root, contactQueryContext));
	}

	public Predicate createOwnershipPredicate(boolean withOwnership, From<?, ?> from, CriteriaBuilder cb, CriteriaQuery<?> query) {
		Subquery<Boolean> sharesQuery = query.subquery(Boolean.class);
		Root<SormasToSormasShareInfo> shareInfoFrom = sharesQuery.from(SormasToSormasShareInfo.class);
		sharesQuery.select(shareInfoFrom.get(SormasToSormasShareInfo.ID));

		Subquery<Number> latestRequestDateQuery = query.subquery(Number.class);
		Root<ShareRequestInfo> shareRequestInfoRoot = latestRequestDateQuery.from(ShareRequestInfo.class);
		latestRequestDateQuery.select(cb.max(shareRequestInfoRoot.get(ShareRequestInfo.CREATION_DATE)));
		latestRequestDateQuery.where(
			cb.equal(
				shareRequestInfoRoot.join(ShareRequestInfo.SHARES, JoinType.LEFT).get(SormasToSormasShareInfo.ID),
				shareInfoFrom.get(SormasToSormasShareInfo.ID)));

		Join<Object, Object> requestsJoin = shareInfoFrom.join(SormasToSormasShareInfo.REQUESTS);
		sharesQuery.where(
			cb.equal(shareInfoFrom.get(SormasToSormasShareInfo.CONTACT), from.get(Contact.ID)),
			cb.equal(shareInfoFrom.get(SormasToSormasShareInfo.OWNERSHIP_HANDED_OVER), true),
			cb.equal(
				requestsJoin.on(cb.equal(requestsJoin.get(ShareRequestInfo.CREATION_DATE), latestRequestDateQuery))
					.get(ShareRequestInfo.REQUEST_STATUS),
				ShareRequestStatus.ACCEPTED));

		if (withOwnership) {
			return cb.and(
				cb.or(
					cb.isNull(from.get(Contact.SORMAS_TO_SORMAS_ORIGIN_INFO)),
					cb.equal(
						from.join(Contact.SORMAS_TO_SORMAS_ORIGIN_INFO, JoinType.LEFT).get(SormasToSormasOriginInfo.OWNERSHIP_HANDED_OVER),
						true)),
				cb.not(cb.exists(sharesQuery)));
		} else {
			return cb.or(
				cb.equal(from.join(Contact.SORMAS_TO_SORMAS_ORIGIN_INFO, JoinType.LEFT).get(SormasToSormasOriginInfo.OWNERSHIP_HANDED_OVER), false),
				cb.exists(sharesQuery));
		}
	}

	@Override
	public void delete(Contact contact, DeletionDetails deletionDetails) {
		// Delete all tasks associated with this contact
		List<Task> tasks = taskService.findBy(new TaskCriteria().contact(new ContactReferenceDto(contact.getUuid())), true);
		for (Task task : tasks) {
			taskService.deletePermanent(task);
		}

		// Delete all samples only associated with this contact
		contact.getSamples()
			.stream()
			.filter(sample -> sample.getAssociatedCase() == null && sample.getAssociatedEventParticipant() == null)
			.forEach(sample -> sampleService.delete(sample, deletionDetails));

		// Remove this contact from all exposures that its referenced in
		exposureService.removeContactFromExposures(contact.getId());

		// Notify external journal if necessary
		externalJournalService.handleExternalJournalPersonUpdateAsync(contact.getPerson().toReference());
		deleteContactLinks(contact);

		super.delete(contact, deletionDetails);
	}

	@Override
	public void restore(Contact contact) {
		// restore all samples only associated with this contact
		contact.getSamples().stream().forEach(sample -> sampleService.restore(sample));

		super.restore(contact);
	}

	@Override
	public void deletePermanent(Contact contact) {

		// Delete all tasks associated with this case
		Optional.ofNullable(contact.getTasks()).ifPresent(tl -> tl.forEach(t -> taskService.deletePermanent(t)));

		// Delete all samples that are only associated with this contact
		contact.getSamples()
			.stream()
			.filter(sample -> sample.getAssociatedCase() == null && sample.getAssociatedEventParticipant() == null)
			.forEach(sample -> sampleService.deletePermanent(sample));

		// Remove the contact that will be deleted from contact_visits
		em.createNativeQuery("delete from contacts_visits cv where cv.contact_id = ?1").setParameter(1, contact.getId()).executeUpdate();

		// Delete all visits that are not associated with any contact or case
		em.createNativeQuery("delete from visit v where v.caze_id is null and not exists(select from contacts_visits cv where cv.visit_id = v.id)")
			.executeUpdate();

		// Delete documents related to this contact
		documentService.getRelatedToEntity(DocumentRelatedEntityType.CONTACT, contact.getUuid()).forEach(d -> documentService.markAsDeleted(d));

		// Remove the contact from any S2S share info referencing it
		sormasToSormasShareInfoService.getByAssociatedEntity(SormasToSormasShareInfo.CONTACT, contact.getUuid()).forEach(s -> {
			s.setContact(null);
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

		deleteContactFromDuplicateOf(contact);

		deleteContactLinks(contact);

		super.deletePermanent(contact);
	}

	private void deleteContactLinks(Contact contact) {
		// Remove the contact from any sample that is also connected to other entities
		contact.getSamples().stream().filter(s -> s.getAssociatedCase() != null || s.getAssociatedEventParticipant() != null).forEach(s -> {
			s.setAssociatedContact(null);
			sampleService.ensurePersisted(s);
		});

		// Remove the contactToCase from all exposures
		exposureService.removeContactFromExposures(contact.getId());
	}

	private void deleteContactFromDuplicateOf(Contact contact) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Contact> cu = cb.createCriteriaUpdate(Contact.class);
		Root<Contact> root = cu.from(Contact.class);

		cu.where(cb.equal(root.get(Contact.DUPLICATE_OF).get(Contact.ID), contact.getId()));
		cu.set(Contact.DUPLICATE_OF, null);

		em.createQuery(cu).executeUpdate();
	}

	/**
	 * Creates a filter that excludes all contacts that are either
	 * {@link DeletableAdo#isDeleted()} or associated with cases that are
	 * {@link Case#isArchived()}.
	 */
	public Predicate createActiveContactsFilter(CriteriaBuilder cb, From<?, Contact> root) {

		return cb.and(cb.isFalse(root.get(Contact.ARCHIVED)), cb.isFalse(root.get(Contact.DELETED)));
	}

	public Predicate createActiveContactsFilter(ContactQueryContext contactQueryContext) {

		From<?, Contact> root = contactQueryContext.getRoot();
		ContactJoins joins = contactQueryContext.getJoins();
		CriteriaBuilder cb = contactQueryContext.getCriteriaBuilder();
		return cb
			.and(cb.or(cb.isNull(root.get(Contact.CAZE)), cb.isFalse(joins.getCaze().get(Case.ARCHIVED))), cb.isFalse(root.get(Contact.DELETED)));
	}

	/**
	 * Creates a default filter that should be used as the basis of queries that do
	 * not use {@link ContactCriteria}. This essentially removes
	 * {@link DeletableAdo#isDeleted()} contacts from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, Contact> root) {
		return cb.isFalse(root.get(Contact.DELETED));
	}

	private Predicate buildDateFilter(CriteriaBuilder cb, Root<Contact> contact, Date from, Date to) {

		return cb.and(
			cb.or(
				cb.and(cb.isNotNull(contact.get(Contact.FOLLOW_UP_UNTIL)), cb.greaterThanOrEqualTo(contact.get(Contact.FOLLOW_UP_UNTIL), from)),
				cb.or(
					cb.and(
						cb.isNotNull(contact.get(Contact.LAST_CONTACT_DATE)),
						cb.greaterThanOrEqualTo(contact.get(Contact.LAST_CONTACT_DATE), from)),
					cb.greaterThanOrEqualTo(contact.get(Contact.REPORT_DATE_TIME), from))),
			cb.or(
				cb.and(cb.isNotNull(contact.get(Contact.LAST_CONTACT_DATE)), cb.lessThanOrEqualTo(contact.get(Contact.LAST_CONTACT_DATE), to)),
				cb.lessThanOrEqualTo(contact.get(Contact.REPORT_DATE_TIME), to)));
	}

	@Override
	public ContactJurisdictionFlagsDto getJurisdictionFlags(Contact entity) {

		return getJurisdictionsFlags(Collections.singletonList(entity)).get(entity.getId());
	}

	@Override
	public Map<Long, ContactJurisdictionFlagsDto> getJurisdictionsFlags(List<Contact> entities) {

		return getSelectionAttributes(
			entities,
			(cb, cq, from) -> getJurisdictionSelections(new ContactQueryContext(cb, cq, from)),
			e -> new ContactJurisdictionFlagsDto(e));
	}

	@Override
	public Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Contact> from) {
		return inJurisdictionOrOwned(new ContactQueryContext(cb, query, from));
	}

	@Override
	public Predicate inJurisdictionOrOwned(ContactQueryContext contactQueryContext) {
		return inJurisdictionOrOwned(contactQueryContext, userService.getCurrentUser());
	}

	public Predicate inJurisdictionOrOwned(ContactQueryContext contactQueryContext, User user) {
		return ContactJurisdictionPredicateValidator.of(contactQueryContext, user).inJurisdictionOrOwned();
	}

	@Override
	public EditPermissionType getEditPermissionType(Contact contact) {

		if (!inJurisdictionOrOwned(contact)) {
			return EditPermissionType.OUTSIDE_JURISDICTION;
		}

		if (sormasToSormasShareInfoService.isContactOwnershipHandedOver(contact)
			|| (contact.getSormasToSormasOriginInfo() != null && !contact.getSormasToSormasOriginInfo().isOwnershipHandedOver())) {
			return EditPermissionType.WITHOUT_OWNERSHIP;
		}

		return super.getEditPermissionType(contact);
	}

	@Override
	public List<Selection<?>> getJurisdictionSelections(ContactQueryContext qc) {

		final CriteriaBuilder cb = qc.getCriteriaBuilder();
		final ContactJoins joins = qc.getJoins();
		return Arrays.asList(
			JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(qc, userService.getCurrentUser())),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(
					cb.isNotNull(joins.getCaze()),
					caseService.inJurisdictionOrOwned(new CaseQueryContext(cb, qc.getQuery(), joins.getCaseJoins())))));
	}

	public List<Contact> getByPersonUuids(List<String> personUuids) {

		List<Contact> contacts = new LinkedList<>();
		IterableHelper.executeBatched(
			personUuids,
			ModelConstants.PARAMETER_LIMIT,
			batchedPersonUuids -> contacts.addAll(getContactsByPersonUuids(batchedPersonUuids)));
		return contacts;
	}

	private List<Contact> getContactsByPersonUuids(List<String> personUuids) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(Contact.class);
		Root<Contact> contactRoot = cq.from(Contact.class);
		Join<Contact, Person> personJoin = contactRoot.join(Contact.PERSON, JoinType.LEFT);

		cq.where(personJoin.get(AbstractDomainObject.UUID).in(personUuids));

		return em.createQuery(cq).getResultList();
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateExternalData(List<ExternalDataDto> externalData) throws ExternalDataUpdateException {
		ExternalDataUtil.updateExternalData(externalData, this::getByUuids, this::ensurePersisted);
	}

	/**
	 * Sets the vaccination status of all contacts of the specified person and disease with vaccination date <= contact start date.
	 * Vaccinations without a vaccination date are relevant for all contacts.
	 *
	 * @param personId
	 *            The ID of the contact person
	 * @param disease
	 *            The disease of the cases
	 * @param vaccinationDate
	 *            The vaccination date of the created or updated vaccination
	 */
	public void updateVaccinationStatuses(Long personId, Disease disease, Date vaccinationDate) {

		// Only consider contacts with relevance date at least one day after the vaccination date
		if (vaccinationDate == null) {
			return;
		} else {
			vaccinationDate = DateHelper.getEndOfDay(vaccinationDate);
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Contact> cu = cb.createCriteriaUpdate(Contact.class);
		Root<Contact> root = cu.from(Contact.class);

		cu.set(root.get(Contact.VACCINATION_STATUS), VaccinationStatus.VACCINATED);
		cu.set(root.get(AbstractDomainObject.CHANGE_DATE), new Date());

		Predicate datePredicate = vaccinationDate != null
			? cb.or(
				cb.greaterThan(root.get(Contact.LAST_CONTACT_DATE), vaccinationDate),
				cb.and(cb.isNull(root.get(Contact.LAST_CONTACT_DATE)), cb.greaterThan(root.get(Contact.REPORT_DATE_TIME), vaccinationDate)))
			: null;

		cu.where(
			CriteriaBuilderHelper
				.and(cb, cb.equal(root.get(Contact.PERSON).get(Person.ID), personId), cb.equal(root.get(Contact.DISEASE), disease), datePredicate));

		em.createQuery(cu).executeUpdate();
	}

	public List<ContactListEntryDto> getEntriesList(Long personId, Integer first, Integer max) {
		if (personId == null) {
			return Collections.emptyList();
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		final Root<Contact> contact = cq.from(Contact.class);

		ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contact);

		cq.multiselect(
			contact.get(Contact.UUID),
			contactQueryContext.getJoins().getCaze().get(Case.UUID),
			contact.get(Contact.CONTACT_STATUS),
			contact.get(Contact.DISEASE),
			contact.get(Contact.CONTACT_CLASSIFICATION),
			contact.get(Contact.CONTACT_CATEGORY),
			contact.get(Contact.REPORT_DATE_TIME),
			contact.get(Contact.LAST_CONTACT_DATE),
			JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(contactQueryContext)),
			contact.get(Contact.CHANGE_DATE));

		Predicate filter = cb.equal(contact.get(Contact.PERSON_ID), personId);
		filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(contact.get(Contact.DELETED)));
		cq.where(filter);

		cq.orderBy(cb.desc(contact.get(Contact.CHANGE_DATE)));

		cq.distinct(true);

		return QueryHelper.getResultList(em, cq, new ContactListEntryDtoResultTransformer(), first, max);
	}

	public long getContactCount(CaseReferenceDto caze) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<Contact> contact = cq.from(Contact.class);
		final Join<Contact, Case> caseJoin = contact.join(Contact.CAZE, JoinType.LEFT);

		cq.where(cb.equal(caseJoin.get(AbstractDomainObject.UUID), caze.getUuid()));
		cq.select(cb.count(contact.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getSingleResult();
	}

	@Override
	protected boolean hasLimitedChangeDateFilterImplementation() {
		return true;
	}

	@Override
	protected String getDeleteReferenceField(DeletionReference deletionReference) {
		if (deletionReference == DeletionReference.REPORT) {
			return Contact.REPORT_DATE_TIME;
		}

		return super.getDeleteReferenceField(deletionReference);
	}
}
