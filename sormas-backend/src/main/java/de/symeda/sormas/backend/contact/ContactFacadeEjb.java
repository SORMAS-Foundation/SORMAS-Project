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
package de.symeda.sormas.backend.contact;

import static de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import static de.symeda.sormas.backend.visit.VisitLogic.getVisitResult;
import static java.time.temporal.ChronoUnit.DAYS;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactExportDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactFollowUpDto;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.DashboardContactDto;
import de.symeda.sormas.api.contact.DashboardQuarantineDataDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.MapContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.EpiDataHelper;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.followup.FollowUpDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.visit.VisitResultDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.api.visit.VisitSummaryExportDetailsDto;
import de.symeda.sormas.api.visit.VisitSummaryExportDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseJurisdictionChecker;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourseFacadeEjb;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.TaskCreationException;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb.EpiDataFacadeEjbLocal;
import de.symeda.sormas.backend.event.ContactEventSummaryDetails;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.exposure.Exposure;
import de.symeda.sormas.backend.externaljournal.ExternalJournalService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasShareInfo;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DateHelper8;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.visit.Visit;
import de.symeda.sormas.backend.visit.VisitService;

@Stateless(name = "ContactFacade")
public class ContactFacadeEjb implements ContactFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private ContactService contactService;
	@EJB
	private ContactListCriteriaBuilder listCriteriaBuilder;
	@EJB
	private CaseService caseService;
	@EJB
	private PersonService personService;
	@EJB
	private UserService userService;
	@EJB
	private VisitService visitService;
	@EJB
	private TaskService taskService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private ExternalJournalService externalJournalService;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private ContactJurisdictionChecker contactJurisdictionChecker;
	@EJB
	private CaseJurisdictionChecker caseJurisdictionChecker;
	@EJB
	private EpiDataFacadeEjbLocal epiDataFacade;
	@EJB
	private ClinicalCourseFacadeEjb.ClinicalCourseFacadeEjbLocal clinicalCourseFacade;
	@EJB
	private SormasToSormasOriginInfoFacadeEjbLocal originInfoFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private EventService eventService;

	@Override
	public List<String> getAllActiveUuids() {

		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return contactService.getAllActiveUuids(user);
	}

	@Override
	public List<ContactDto> getAllActiveContactsAfter(Date date) {

		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return contactService.getAllActiveContactsAfter(date).stream().map(c -> convertToDto(c, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<ContactDto> getByUuids(List<String> uuids) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return contactService.getByUuids(uuids).stream().map(c -> convertToDto(c, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {

		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return contactService.getDeletedUuidsSince(user, since);
	}

	@Override
	public ContactDto getContactByUuid(String uuid) {
		return convertToDto(contactService.getByUuid(uuid), Pseudonymizer.getDefault(userService::hasRight));
	}

	@Override
	public Boolean isValidContactUuid(String uuid) {
		return contactService.exists(uuid);
	}

	@Override
	public ContactReferenceDto getReferenceByUuid(String uuid) {
		return convertToReferenceDto(contactService.getByUuid(uuid));
	}

	@Override
	public ContactDto saveContact(ContactDto dto) {
		return saveContact(dto, true);
	}

	@Override
	public ContactDto saveContact(ContactDto dto, boolean handleChanges) {
		final Contact existingContact = dto.getUuid() != null ? contactService.getByUuid(dto.getUuid()) : null;
		final ContactDto existingContactDto = toDto(existingContact);
		restorePseudonymizedDto(dto, existingContact, existingContactDto);

		validate(dto);

		// taking this out because it may lead to server problems
		// case disease can change over time and there is currently no mechanism that would delete all related contacts
		// in this case the best solution is to only keep this hidden from the UI and still allow it in the backend
		//		if (!DiseaseHelper.hasContactFollowUp(entity.getCaze().getDisease(), entity.getCaze().getPlagueType())) {
		//			throw new UnsupportedOperationException("Contact creation is not allowed for diseases that don't have contact follow-up.");
		//		}

		Contact entity = fromDto(dto);
		contactService.ensurePersisted(entity);

		if (existingContact == null && featureConfigurationFacade.isTaskGenerationFeatureEnabled(TaskType.CONTACT_INVESTIGATION)) {
			createInvestigationTask(entity);

		}
		if (existingContact != null) {
			handleExternalJournalPerson(existingContactDto, dto);
		}

		if (handleChanges) {
			updateContactVisitAssociations(existingContactDto, entity);

			contactService.updateFollowUpUntilAndStatus(entity);
			contactService.udpateContactStatus(entity);

			if (entity.getCaze() != null) {
				caseFacade.onCaseChanged(CaseFacadeEjbLocal.toDto(entity.getCaze()), entity.getCaze());
			}
		}

		return toDto(entity);
	}

	// 5 second delay added before notifying of update so that current transaction can complete and new data can be retrieved from DB
	private void handleExternalJournalPerson(ContactDto existingContact, ContactDto updatedContact) {
		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		Runnable notify = () -> externalJournalService.notifyExternalJournalFollowUpUntilUpdate(updatedContact, existingContact.getFollowUpUntil());
		executorService.schedule(notify, 5, TimeUnit.SECONDS);
	}

	private void createInvestigationTask(Contact entity) {
		LocalDate now = LocalDate.now();
		LocalDate reportDate = DateHelper8.toLocalDate(entity.getReportDateTime());
		if (DAYS.between(reportDate, now) <= 30) {
			try {
				User assignee = taskService.getTaskAssignee(entity);
				LocalDateTime fromDateTime = LocalDate.now().atStartOfDay();
				LocalDateTime toDateTime = fromDateTime.plusDays(1);
				Task task = createContactTask(TaskType.CONTACT_INVESTIGATION, fromDateTime, toDateTime, entity, assignee);
				taskService.ensurePersisted(task);
			} catch (TaskCreationException e) {
				logger.warn(e.getMessage());
			}
		}
	}

	private void updateContactVisitAssociations(ContactDto existingContact, Contact contact) {

		if (existingContact != null
			&& existingContact.getReportDateTime() == contact.getReportDateTime()
			&& existingContact.getLastContactDate() == contact.getLastContactDate()
			&& existingContact.getFollowUpUntil() == contact.getFollowUpUntil()
			&& existingContact.getDisease() == contact.getDisease()) {
			return;
		}

		if (existingContact != null) {
			for (Visit visit : contact.getVisits()) {
				visit.getContacts().remove(contact);
			}
		}

		Date contactStartDate = ContactLogic.getStartDate(contact.getLastContactDate(), contact.getReportDateTime());
		for (Visit visit : visitService.getAllRelevantVisits(
			contact.getPerson(),
			contact.getDisease(),
			contactStartDate,
			contact.getFollowUpUntil() != null ? contact.getFollowUpUntil() : contactStartDate)) {
			contact.getVisits().add(visit); // Necessary for further logic during the contact save process
			visit.getContacts().add(contact);
		}
	}

	@Override
	public Long countContactsForMap(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, List<MapCaseDto> mapCaseDtos) {
		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);
		List<String> caseUuids = mapCaseDtos.stream().map(MapCaseDto::getUuid).collect(Collectors.toList());

		return contactService.countContactsForMap(region, district, disease, caseUuids);
	}

	@Override
	public List<MapContactDto> getContactsForMap(
		RegionReferenceDto regionRef,
		DistrictReferenceDto districtRef,
		Disease disease,
		List<MapCaseDto> mapCaseDtos) {

		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);
		List<String> caseUuids = mapCaseDtos.stream().map(MapCaseDto::getUuid).collect(Collectors.toList());

		return contactService.getContactsForMap(region, district, disease, caseUuids);
	}

	@Override
	public void deleteContact(String contactUuid) {

		if (!userService.hasRight(UserRight.CONTACT_DELETE)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + " is not allowed to delete contacts.");
		}

		Contact contact = contactService.getByUuid(contactUuid);
		contactService.delete(contact);

		if (contact.getCaze() != null) {
			caseFacade.onCaseChanged(CaseFacadeEjbLocal.toDto(contact.getCaze()), contact.getCaze());
		}
	}

	@Override
	public List<ContactExportDto> getExportList(ContactCriteria contactCriteria, int first, int max, Language userLanguage) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ContactExportDto> cq = cb.createQuery(ContactExportDto.class);
		Root<Contact> contact = cq.from(Contact.class);

		ContactJoins joins = new ContactJoins(contact);

		// Events count subquery
		Subquery<Long> eventCountSq = cq.subquery(Long.class);
		Root<EventParticipant> eventCountRoot = eventCountSq.from(EventParticipant.class);
		Join<EventParticipant, Event> eventJoin = eventCountRoot.join(EventParticipant.EVENT, JoinType.INNER);
		Join<Person, Contact> contactJoin = eventCountRoot.join(EventParticipant.PERSON, JoinType.INNER).join(Person.CONTACTS, JoinType.INNER);

		eventCountSq.where(
			cb.and(
				cb.equal(contactJoin.get(Contact.UUID), contact.get(Contact.UUID)),
				cb.isFalse(eventJoin.get(Event.DELETED)),
				cb.isFalse(eventJoin.get(Event.ARCHIVED)),
				cb.isFalse(eventCountRoot.get(EventParticipant.DELETED))));
		eventCountSq.select(cb.countDistinct(eventJoin.get(Event.ID)));

		cq.multiselect(
			Stream.concat(
				Stream.of(
					contact.get(Contact.ID),
					joins.getPerson().get(Person.ID),
					contact.get(Contact.UUID),
					joins.getCaze().get(Case.UUID),
					joins.getCaze().get(Case.CASE_CLASSIFICATION),
					contact.get(Contact.DISEASE),
					contact.get(Contact.DISEASE_DETAILS),
					contact.get(Contact.CONTACT_CLASSIFICATION),
					contact.get(Contact.LAST_CONTACT_DATE),
					joins.getPerson().get(Person.FIRST_NAME),
					joins.getPerson().get(Person.LAST_NAME),
					joins.getPerson().get(Person.SEX),
					joins.getPerson().get(Person.BIRTHDATE_DD),
					joins.getPerson().get(Person.BIRTHDATE_MM),
					joins.getPerson().get(Person.BIRTHDATE_YYYY),
					joins.getPerson().get(Person.APPROXIMATE_AGE),
					joins.getPerson().get(Person.APPROXIMATE_AGE_TYPE),
					contact.get(Contact.REPORT_DATE_TIME),
					contact.get(Contact.CONTACT_IDENTIFICATION_SOURCE),
					contact.get(Contact.CONTACT_IDENTIFICATION_SOURCE_DETAILS),
					contact.get(Contact.TRACING_APP),
					contact.get(Contact.TRACING_APP_DETAILS),
					contact.get(Contact.CONTACT_PROXIMITY),
					contact.get(Contact.CONTACT_STATUS),
					contact.get(Contact.FOLLOW_UP_STATUS),
					contact.get(Contact.FOLLOW_UP_UNTIL),
					contact.get(Contact.QUARANTINE),
					contact.get(Contact.QUARANTINE_TYPE_DETAILS),
					contact.get(Contact.QUARANTINE_FROM),
					contact.get(Contact.QUARANTINE_TO),
					contact.get(Contact.QUARANTINE_HELP_NEEDED),
					contact.get(Contact.QUARANTINE_ORDERED_VERBALLY),
					contact.get(Contact.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT),
					contact.get(Contact.QUARANTINE_ORDERED_VERBALLY_DATE),
					contact.get(Contact.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE),
					contact.get(Contact.QUARANTINE_EXTENDED),
					contact.get(Contact.QUARANTINE_REDUCED),
					contact.get(Contact.QUARANTINE_OFFICIAL_ORDER_SENT),
					contact.get(Contact.QUARANTINE_OFFICIAL_ORDER_SENT_DATE),
					joins.getPerson().get(Person.PRESENT_CONDITION),
					joins.getPerson().get(Person.DEATH_DATE),
					joins.getAddressRegion().get(Region.NAME),
					joins.getAddressDistrict().get(District.NAME),
					joins.getAddressCommunity().get(Community.NAME),
					joins.getAddress().get(Location.CITY),
					joins.getAddress().get(Location.STREET),
					joins.getAddress().get(Location.HOUSE_NUMBER),
					joins.getAddress().get(Location.ADDITIONAL_INFORMATION),
					joins.getAddress().get(Location.POSTAL_CODE),
					joins.getAddressFacility().get(Facility.NAME),
					joins.getAddressFacility().get(Facility.UUID),
					joins.getAddress().get(Location.FACILITY_DETAILS),
					joins.getPerson().get(Person.PHONE),
					joins.getPerson().get(Person.PHONE_OWNER),
					joins.getPerson().get(Person.EMAIL_ADDRESS),
					joins.getPerson().get(Person.OCCUPATION_TYPE),
					joins.getPerson().get(Person.OCCUPATION_DETAILS),
					joins.getRegion().get(Region.NAME),
					joins.getDistrict().get(District.NAME),
					joins.getCommunity().get(Community.NAME),
					joins.getEpiData().get(EpiData.ID),
					joins.getEpiData().get(EpiData.CONTACT_WITH_SOURCE_CASE_KNOWN),
					contact.get(Contact.RETURNING_TRAVELER),
					eventCountSq,
					contact.get(Contact.EXTERNAL_ID)),
				listCriteriaBuilder.getJurisdictionSelections(joins)).collect(Collectors.toList()));

		cq.distinct(true);

		Predicate filter = listCriteriaBuilder.buildContactFilter(contactCriteria, cb, contact, cq, joins);

		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.desc(contact.get(Contact.REPORT_DATE_TIME)), cb.desc(contact.get(Contact.ID)));

		List<ContactExportDto> exportContacts = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		List<String> resultContactsUuids = exportContacts.stream().map(ContactExportDto::getUuid).collect(Collectors.toList());

		if (!exportContacts.isEmpty()) {
			List<Long> exportContactIds = exportContacts.stream().map(e -> e.getId()).collect(Collectors.toList());

			CriteriaQuery<VisitSummaryExportDetails> visitsCq = cb.createQuery(VisitSummaryExportDetails.class);
			Root<Contact> visitsCqRoot = visitsCq.from(Contact.class);
			ContactJoins visitContactJoins = new ContactJoins(visitsCqRoot);

			visitsCq.where(
				ContactService.and(cb, contact.get(AbstractDomainObject.ID).in(exportContactIds), cb.isNotEmpty(visitsCqRoot.get(Contact.VISITS))));
			visitsCq.multiselect(
				Stream
					.concat(
						Stream.of(
							visitsCqRoot.get(AbstractDomainObject.ID),
							visitContactJoins.getVisits().get(Visit.VISIT_DATE_TIME),
							visitContactJoins.getVisits().get(Visit.VISIT_STATUS),
							visitContactJoins.getVisitSymptoms()),
						listCriteriaBuilder.getJurisdictionSelections(visitContactJoins))
					.collect(Collectors.toList()));

			List<VisitSummaryExportDetails> visitSummaries = em.createQuery(visitsCq).getResultList();

			CriteriaQuery<Exposure> exposuresCq = cb.createQuery(Exposure.class);
			Root<Exposure> exposuresRoot = exposuresCq.from(Exposure.class);
			Join<Exposure, EpiData> exposuresEpiDataJoin = exposuresRoot.join(Exposure.EPI_DATA, JoinType.LEFT);
			Expression<String> epiDataIdsExpr = exposuresEpiDataJoin.get(EpiData.ID);
			Predicate exposuresPredicate = cb.and(
				epiDataIdsExpr.in(exportContacts.stream().map(ContactExportDto::getEpiDataId).collect(Collectors.toList())),
				cb.or(
					cb.equal(exposuresRoot.get(Exposure.EXPOSURE_TYPE), ExposureType.TRAVEL),
					cb.equal(exposuresRoot.get(Exposure.EXPOSURE_TYPE), ExposureType.BURIAL)));
			exposuresCq.where(exposuresPredicate);
			exposuresCq.orderBy(cb.asc(exposuresEpiDataJoin.get(EpiData.ID)));
			List<Exposure> exposureList = em.createQuery(exposuresCq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
			Map<Long, List<Exposure>> exposures = exposureList.stream().collect(Collectors.groupingBy(e -> e.getEpiData().getId()));

			// Load latest events info
			// Adding a second query here is not perfect, but selecting the last event with a criteria query
			// doesn't seem to be possible and using a native query is not an option because of user filters
			List<ContactEventSummaryDetails> eventSummaries = eventService.getEventSummaryDetailsByContacts(resultContactsUuids);

			// Adding a second query here is not perfect, but selecting the last cooperative visit with a criteria query
			// doesn't seem to be possible and using a native query is not an option because of user filters
			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
			for (ContactExportDto exportContact : exportContacts) {
				boolean inJurisdiction = contactJurisdictionChecker.isInJurisdictionOrOwned(exportContact.getJurisdiction());

				List<VisitSummaryExportDetails> visits =
					visitSummaries.stream().filter(v -> v.getContactId() == exportContact.getId()).collect(Collectors.toList());

				VisitSummaryExportDetails lastCooperativeVisit = visits.stream()
					.filter(v -> v.getVisitStatus() == VisitStatus.COOPERATIVE)
					.max(Comparator.comparing(VisitSummaryExportDetails::getVisitDateTime))
					.orElse(null);

				exportContact.setNumberOfVisits(visits.size());
				if (lastCooperativeVisit != null) {
					SymptomsDto symptoms = SymptomsFacadeEjb.toDto(lastCooperativeVisit.getSymptoms());
					pseudonymizer.pseudonymizeDto(SymptomsDto.class, symptoms, inJurisdiction, null);

					exportContact.setLastCooperativeVisitDate(lastCooperativeVisit.getVisitDateTime());
					exportContact.setLastCooperativeVisitSymptoms(SymptomsHelper.buildSymptomsHumanString(symptoms, true, userLanguage));
					exportContact.setLastCooperativeVisitSymptomatic(symptoms.getSymptomatic() ? YesNoUnknown.YES : YesNoUnknown.NO);
				}

				if (exposures != null) {
					Optional.ofNullable(exposures.get(exportContact.getEpiDataId())).ifPresent(contactExposures -> {
						StringBuilder travelHistoryBuilder = new StringBuilder();
						if (contactExposures.stream().anyMatch(e -> ExposureType.BURIAL.equals(e.getExposureType()))) {
							exportContact.setBurialAttended(true);
						}
						contactExposures.stream().filter(e -> ExposureType.TRAVEL.equals(e.getExposureType())).forEach(exposure -> {
							travelHistoryBuilder.append(
								EpiDataHelper.buildDetailedTravelString(
									exposure.getLocation().toString(),
									exposure.getDescription(),
									exposure.getStartDate(),
									exposure.getEndDate(),
									userLanguage))
								.append(", ");
						});
						if (travelHistoryBuilder.length() > 0) {
							exportContact.setTraveled(true);
							travelHistoryBuilder.delete(travelHistoryBuilder.lastIndexOf(", "), travelHistoryBuilder.length());
						}
						exportContact.setTravelHistory(travelHistoryBuilder.toString());
					});
				}

				if (eventSummaries != null && exportContact.getEventCount() != 0) {
					eventSummaries.stream()
						.filter(v -> v.getContactUuid().equals(exportContact.getUuid()))
						.max(Comparator.comparing(ContactEventSummaryDetails::getEventDate))
						.ifPresent(eventSummary -> {
							exportContact.setLatestEventId(eventSummary.getEventUuid());
							exportContact.setLatestEventTitle(eventSummary.getEventTitle());
						});
				}

				pseudonymizer.pseudonymizeDto(ContactExportDto.class, exportContact, inJurisdiction, null);
			}
		}

		return exportContacts;

	}

	@Override
	public List<VisitSummaryExportDto> getVisitSummaryExportList(ContactCriteria contactCriteria, int first, int max, Language userLanguage) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<VisitSummaryExportDto> cq = cb.createQuery(VisitSummaryExportDto.class);
		final Root<Contact> contactRoot = cq.from(Contact.class);
		final ContactJoins contactJoins = new ContactJoins(contactRoot);
		final Join<Contact, Person> contactPerson = contactJoins.getPerson();

		cq.multiselect(
			contactRoot.get(Contact.UUID),
			contactRoot.get(Contact.ID),
			contactPerson.get(Person.FIRST_NAME),
			contactPerson.get(Person.LAST_NAME),
			cb.<Date> selectCase()
				.when(cb.isNotNull(contactRoot.get(Contact.LAST_CONTACT_DATE)), contactRoot.get(Contact.LAST_CONTACT_DATE))
				.otherwise(contactRoot.get(Contact.REPORT_DATE_TIME)),
			contactRoot.get(Contact.FOLLOW_UP_UNTIL));

		cq.where(
			AbstractAdoService.and(
				cb,
				listCriteriaBuilder.buildContactFilter(contactCriteria, cb, contactRoot, cq, contactJoins),
				cb.isNotEmpty(contactRoot.get(Contact.VISITS))));
		cq.orderBy(cb.asc(contactRoot.get(Contact.REPORT_DATE_TIME)));

		List<VisitSummaryExportDto> visitSummaries = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();

		if (!visitSummaries.isEmpty()) {
			List<String> visitSummaryUuids = visitSummaries.stream().map(e -> e.getUuid()).collect(Collectors.toList());

			CriteriaQuery<VisitSummaryExportDetails> visitsCq = cb.createQuery(VisitSummaryExportDetails.class);
			Root<Contact> visitsCqRoot = visitsCq.from(Contact.class);
			ContactJoins joins = new ContactJoins(visitsCqRoot);

			visitsCq.where(
				ContactService
					.and(cb, contactRoot.get(AbstractDomainObject.UUID).in(visitSummaryUuids), cb.isNotEmpty(visitsCqRoot.get(Contact.VISITS))));
			visitsCq.multiselect(
				Stream
					.concat(
						Stream.of(
							visitsCqRoot.get(AbstractDomainObject.ID),
							joins.getVisits().get(Visit.VISIT_DATE_TIME),
							joins.getVisits().get(Visit.VISIT_STATUS),
							joins.getVisitSymptoms()),
						listCriteriaBuilder.getJurisdictionSelections(joins))
					.collect(Collectors.toList()));
			visitsCq.orderBy(cb.asc(joins.getVisits().get(Visit.VISIT_DATE_TIME)));

			List<VisitSummaryExportDetails> visitSummaryDetails = em.createQuery(visitsCq).getResultList();

			Map<Long, VisitSummaryExportDto> visitSummaryMap =
				visitSummaries.stream().collect(Collectors.toMap(VisitSummaryExportDto::getContactId, Function.identity()));

			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
			visitSummaryDetails.forEach(v -> {
				SymptomsDto symptoms = SymptomsFacadeEjb.toDto(v.getSymptoms());
				pseudonymizer
					.pseudonymizeDto(SymptomsDto.class, symptoms, contactJurisdictionChecker.isInJurisdictionOrOwned(v.getJurisdiction()), null);

				visitSummaryMap.get(v.getContactId())
					.getVisitDetails()
					.add(
						new VisitSummaryExportDetailsDto(
							v.getVisitDateTime(),
							v.getVisitStatus(),
							SymptomsHelper.buildSymptomsHumanString(symptoms, true, userLanguage)));
			});
		}

		return visitSummaries;
	}

	@Override
	public long countMaximumFollowUpDays(ContactCriteria contactCriteria) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<Contact> contactRoot = cq.from(Contact.class);
		final ContactJoins joins = new ContactJoins(contactRoot);
		joins.getVisits();

		Predicate filter = listCriteriaBuilder.buildContactFilter(contactCriteria, cb, contactRoot, cq, joins);
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(contactRoot.get(AbstractDomainObject.ID));
		List<Long> contactIds = em.createQuery(cq).getResultList();

		if (!contactIds.isEmpty()) {
			return contactIds.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.entrySet()
				.stream()
				.max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
				.get()
				.getValue();
		} else {
			return 0L;
		}
	}

	@Override
	public long count(ContactCriteria contactCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Contact> root = cq.from(Contact.class);
		ContactJoins joins = new ContactJoins(root);

		Predicate filter = listCriteriaBuilder.buildContactFilter(contactCriteria, cb, root, cq, joins);

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<ContactFollowUpDto> getContactFollowUpList(
		ContactCriteria contactCriteria,
		Date referenceDate,
		int interval,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		Date end = DateHelper.getEndOfDay(referenceDate);
		Date start = DateHelper.getStartOfDay(DateHelper.subtractDays(end, interval));

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ContactFollowUpDto> cq = cb.createQuery(ContactFollowUpDto.class);
		Root<Contact> contact = cq.from(Contact.class);

		ContactJoins joins = new ContactJoins(contact);

		cq.multiselect(
			Stream
				.concat(
					Stream.of(
						contact.get(Contact.UUID),
						joins.getPerson().get(Person.FIRST_NAME),
						joins.getPerson().get(Person.LAST_NAME),
						joins.getContactOfficer().get(User.UUID),
						joins.getContactOfficer().get(User.FIRST_NAME),
						joins.getContactOfficer().get(User.LAST_NAME),
						contact.get(Contact.LAST_CONTACT_DATE),
						contact.get(Contact.REPORT_DATE_TIME),
						contact.get(Contact.FOLLOW_UP_UNTIL),
						joins.getPerson().get(Person.SYMPTOM_JOURNAL_STATUS),
						contact.get(Contact.DISEASE)),
					listCriteriaBuilder.getJurisdictionSelections(joins))
				.collect(Collectors.toList()));

		// Only use user filter if no restricting case is specified
		Predicate filter = listCriteriaBuilder.buildContactFilter(contactCriteria, cb, contact, cq, joins);

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case FollowUpDto.UUID:
				case ContactFollowUpDto.LAST_CONTACT_DATE:
				case FollowUpDto.REPORT_DATE:
					expression = contact.get(Contact.REPORT_DATE_TIME);
					break;
				case FollowUpDto.FOLLOW_UP_UNTIL:
					expression = contact.get(sortProperty.propertyName);
					break;
				case ContactFollowUpDto.SYMPTOM_JOURNAL_STATUS:
					expression = joins.getPerson().get(Person.SYMPTOM_JOURNAL_STATUS);
					break;
				case FollowUpDto.FIRST_NAME:
					expression = joins.getPerson().get(Person.FIRST_NAME);
					break;
				case FollowUpDto.LAST_NAME:
					expression = joins.getPerson().get(Person.LAST_NAME);
					break;
				case ContactFollowUpDto.CONTACT_OFFICER:
					expression = joins.getContactOfficer().get(User.FIRST_NAME);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = joins.getContactOfficer().get(User.LAST_NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(contact.get(Contact.CHANGE_DATE)));
		}

		List<ContactFollowUpDto> resultList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();

		if (!resultList.isEmpty()) {

			List<String> contactUuids = resultList.stream().map(d -> d.getUuid()).collect(Collectors.toList());

			CriteriaQuery<Object[]> visitsCq = cb.createQuery(Object[].class);
			Root<Contact> visitsCqRoot = visitsCq.from(Contact.class);
			Join<Contact, Visit> visitsJoin = visitsCqRoot.join(Contact.VISITS, JoinType.LEFT);
			Join<Visit, Symptoms> visitSymptomsJoin = visitsJoin.join(Visit.SYMPTOMS, JoinType.LEFT);

			visitsCq.where(
				AbstractAdoService.and(
					cb,
					contact.get(AbstractDomainObject.UUID).in(contactUuids),
					cb.isNotEmpty(visitsCqRoot.get(Contact.VISITS)),
					cb.between(visitsJoin.get(Visit.VISIT_DATE_TIME), start, end)));
			visitsCq.multiselect(
				visitsCqRoot.get(Contact.UUID),
				visitsJoin.get(Visit.VISIT_DATE_TIME),
				visitsJoin.get(Visit.VISIT_STATUS),
				visitsJoin.get(Visit.ORIGIN),
				visitSymptomsJoin.get(Symptoms.SYMPTOMATIC));

			visitsCq.orderBy(cb.asc(visitsJoin.get(Visit.VISIT_DATE_TIME)), cb.asc(visitsJoin.get(Visit.CREATION_DATE)));

			List<Object[]> visits = em.createQuery(visitsCq).getResultList();
			Map<String, ContactFollowUpDto> resultMap =
				resultList.stream().collect(Collectors.toMap(ContactFollowUpDto::getUuid, Function.identity()));

			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));

			resultMap.values().stream().forEach(contactFollowUpDto -> {
				contactFollowUpDto.initVisitSize(interval + 1);

				boolean isInJurisdiction = contactJurisdictionChecker.isInJurisdictionOrOwned(contactFollowUpDto.getJurisdiction());
				pseudonymizer.pseudonymizeDto(ContactFollowUpDto.class, contactFollowUpDto, isInJurisdiction, null);
			});
			visits.stream().forEach(v -> {
				int day = DateHelper.getDaysBetween(start, (Date) v[1]);
				VisitResultDto result = getVisitResult((VisitStatus) v[2], (VisitOrigin) v[3], (boolean) v[4]);
				resultMap.get(v[0]).getVisitResults()[day - 1] = result;
			});
		}

		return resultList;
	}

	@Override
	public List<ContactIndexDto> getIndexList(ContactCriteria contactCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaQuery<ContactIndexDto> query = listCriteriaBuilder.buildIndexCriteria(contactCriteria, sortProperties);

		final List<ContactIndexDto> dtos;
		if (first != null && max != null) {
			dtos = em.createQuery(query).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			dtos = em.createQuery(query).getResultList();
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(
			ContactIndexDto.class,
			dtos,
			c -> contactJurisdictionChecker.isInJurisdictionOrOwned(c.getJurisdiction()),
			(c, isInJurisdiction) -> {
				if (c.getCaze() != null) {
					pseudonymizer.pseudonymizeDto(
						CaseReferenceDto.class,
						c.getCaze(),
						caseJurisdictionChecker.isInJurisdictionOrOwned(c.getCaseJurisdiction()),
						null);
				}
			});

		return dtos;
	}

	@Override
	public List<ContactIndexDetailedDto> getIndexDetailedList(
		ContactCriteria contactCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		CriteriaQuery<ContactIndexDetailedDto> query = listCriteriaBuilder.buildIndexDetailedCriteria(contactCriteria, sortProperties);

		List<ContactIndexDetailedDto> dtos;
		if (first != null && max != null) {
			dtos = em.createQuery(query).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			dtos = em.createQuery(query).getResultList();
		}

		// Load latest events info
		// Adding a second query here is not perfect, but selecting the last event with a criteria query
		// doesn't seem to be possible and using a native query is not an option because of user filters
		List<ContactEventSummaryDetails> eventSummaries =
			eventService.getEventSummaryDetailsByContacts(dtos.stream().map(ContactIndexDetailedDto::getUuid).collect(Collectors.toList()));
		for (ContactIndexDetailedDto contact : dtos) {
			if (contact.getEventCount() == 0) {
				continue;
			}

			eventSummaries.stream()
				.filter(v -> v.getContactUuid().equals(contact.getUuid()))
				.max(Comparator.comparing(ContactEventSummaryDetails::getEventDate))
				.ifPresent(eventSummary -> {
					contact.setLatestEventId(eventSummary.getEventUuid());
					contact.setLatestEventTitle(eventSummary.getEventTitle());
				});
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		User currentUser = userService.getCurrentUser();
		pseudonymizer.pseudonymizeDtoCollection(
			ContactIndexDetailedDto.class,
			dtos,
			c -> contactJurisdictionChecker.isInJurisdictionOrOwned(c.getJurisdiction()),
			(c, isInJurisdiction) -> {
				pseudonymizer.pseudonymizeUser(userService.getByUuid(c.getReportingUser().getUuid()), currentUser, c::setReportingUser);
				if (c.getCaze() != null) {
					pseudonymizer.pseudonymizeDto(
						CaseReferenceDto.class,
						c.getCaze(),
						caseJurisdictionChecker.isInJurisdictionOrOwned(c.getCaseJurisdiction()),
						null);
				}
			});

		return dtos;
	}

	@Override
	public int[] getContactCountsByCasesForDashboard(List<Long> contactIds) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Contact> contact = cq.from(Contact.class);
		Join<Contact, Case> caseJoin = contact.join(Contact.CAZE);

		cq.where(contact.get(Contact.ID).in(contactIds));
		cq.select(caseJoin.get(Case.ID));
		cq.distinct(true);

		List<Long> caseIds = em.createQuery(cq).getResultList();

		if (caseIds.isEmpty()) {
			return new int[3];
		} else {
			int[] counts = new int[3];
			CriteriaQuery<Long> cq2 = cb.createQuery(Long.class);
			Root<Contact> contact2 = cq2.from(Contact.class);
			cq2.groupBy(contact2.get(Contact.CAZE));

			cq2.where(contact2.get(Contact.CAZE).in(caseIds));
			cq2.select(cb.count(contact2.get(Contact.ID)));

			List<Long> caseContactCounts = em.createQuery(cq2).getResultList();

			counts[0] = caseContactCounts.stream().min((l1, l2) -> l1.compareTo(l2)).orElse(0L).intValue();
			counts[1] = caseContactCounts.stream().max((l1, l2) -> l1.compareTo(l2)).orElse(0L).intValue();
			counts[2] = caseContactCounts.stream().reduce(0L, (a, b) -> a + b).intValue() / caseIds.size();

			return counts;
		}
	}

	@Override
	public int getNonSourceCaseCountForDashboard(List<String> caseUuids) {

		if (CollectionUtils.isEmpty(caseUuids)) {
			// Avoid empty IN clause
			return 0;
		}

		Query query = em.createNativeQuery(
			String.format(
				"SELECT DISTINCT count(case1_.id) FROM contact AS contact0_ LEFT OUTER JOIN cases AS case1_ ON (contact0_.%s_id = case1_.id) WHERE case1_.%s IN (%s)",
				Contact.RESULTING_CASE.toLowerCase(),
				Case.UUID,
				QueryHelper.concatStrings(caseUuids)));

		BigInteger count = (BigInteger) query.getSingleResult();
		return count.intValue();
	}

	public Contact fromDto(@NotNull ContactDto source) {

		Contact target = contactService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Contact();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}

		DtoHelper.validateDto(source, target);

		target.setCaze(caseService.getByReferenceDto(source.getCaze()));
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());

		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		if (source.getReportDateTime() != null) {
			target.setReportDateTime(source.getReportDateTime());
		} else { // make sure we do have a report date
			target.setReportDateTime(new Date());
		}

		// use only date, not time
		target.setMultiDayContact(source.isMultiDayContact());
		if(source.isMultiDayContact()) {
			target.setFirstContactDate(source.getFirstContactDate() != null ?
				DateHelper8.toDate(DateHelper8.toLocalDate(source.getFirstContactDate())) :
				null);
		} else {
			target.setFirstContactDate(null);
		}

		target.setLastContactDate(
			source.getLastContactDate() != null ? DateHelper8.toDate(DateHelper8.toLocalDate(source.getLastContactDate())) : null);

		target.setContactIdentificationSource(source.getContactIdentificationSource());
		target.setContactIdentificationSourceDetails(source.getContactIdentificationSourceDetails());
		target.setTracingApp(source.getTracingApp());
		target.setTracingAppDetails(source.getTracingAppDetails());
		target.setContactProximity(source.getContactProximity());
		target.setContactClassification(source.getContactClassification());
		target.setContactStatus(source.getContactStatus());
		target.setFollowUpStatus(source.getFollowUpStatus());
		target.setFollowUpComment(source.getFollowUpComment());
		target.setFollowUpUntil(source.getFollowUpUntil());
		target.setOverwriteFollowUpUntil(source.isOverwriteFollowUpUntil());
		target.setContactOfficer(userService.getByReferenceDto(source.getContactOfficer()));
		target.setDescription(source.getDescription());
		target.setRelationToCase(source.getRelationToCase());
		target.setRelationDescription(source.getRelationDescription());
		target.setResultingCase(caseService.getByReferenceDto(source.getResultingCase()));

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
		target.setExternalID(source.getExternalID());

		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));

		target.setHighPriority(source.isHighPriority());
		target.setImmunosuppressiveTherapyBasicDisease(source.getImmunosuppressiveTherapyBasicDisease());
		target.setImmunosuppressiveTherapyBasicDiseaseDetails(source.getImmunosuppressiveTherapyBasicDiseaseDetails());
		target.setCareForPeopleOver60(source.getCareForPeopleOver60());

		target.setQuarantine(source.getQuarantine());
		target.setQuarantineTypeDetails(source.getQuarantineTypeDetails());
		target.setQuarantineFrom(source.getQuarantineFrom());
		target.setQuarantineTo(source.getQuarantineTo());

		target.setCaseIdExternalSystem(source.getCaseIdExternalSystem());
		target.setCaseOrEventInformation(source.getCaseOrEventInformation());

		target.setContactProximityDetails(source.getContactProximityDetails());
		target.setContactCategory(source.getContactCategory());

		target.setQuarantineHelpNeeded(source.getQuarantineHelpNeeded());
		target.setQuarantineOrderedVerbally(source.isQuarantineOrderedVerbally());
		target.setQuarantineOrderedOfficialDocument(source.isQuarantineOrderedOfficialDocument());
		target.setQuarantineOrderedVerballyDate(source.getQuarantineOrderedVerballyDate());
		target.setQuarantineOrderedOfficialDocumentDate(source.getQuarantineOrderedOfficialDocumentDate());
		target.setQuarantineHomePossible(source.getQuarantineHomePossible());
		target.setQuarantineHomePossibleComment(source.getQuarantineHomePossibleComment());
		target.setQuarantineHomeSupplyEnsured(source.getQuarantineHomeSupplyEnsured());
		target.setQuarantineHomeSupplyEnsuredComment(source.getQuarantineHomeSupplyEnsuredComment());
		target.setQuarantineExtended(source.isQuarantineExtended());
		target.setQuarantineReduced(source.isQuarantineReduced());
		target.setQuarantineOfficialOrderSent(source.isQuarantineOfficialOrderSent());
		target.setQuarantineOfficialOrderSentDate(source.getQuarantineOfficialOrderSentDate());
		target.setAdditionalDetails(source.getAdditionalDetails());

		target.setEpiData(epiDataFacade.fromDto(source.getEpiData()));
		target.setHealthConditions(clinicalCourseFacade.fromHealthConditionsDto(source.getHealthConditions()));
		target.setReturningTraveler(source.getReturningTraveler());
		target.setEndOfQuarantineReason(source.getEndOfQuarantineReason());
		target.setEndOfQuarantineReasonDetails(source.getEndOfQuarantineReasonDetails());

		if (source.getSormasToSormasOriginInfo() != null) {
			target.setSormasToSormasOriginInfo(originInfoFacade.toDto(source.getSormasToSormasOriginInfo()));
		}

		return target;
	}

	@Override
	public boolean isDeleted(String contactUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Contact> from = cq.from(Contact.class);

		cq.where(cb.and(cb.isTrue(from.get(Contact.DELETED)), cb.equal(from.get(AbstractDomainObject.UUID), contactUuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	@Override
	public List<DashboardContactDto> getContactsForDashboard(
		RegionReferenceDto regionRef,
		DistrictReferenceDto districtRef,
		Disease disease,
		Date from,
		Date to) {

		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);
		User user = userService.getCurrentUser();

		return contactService.getContactsForDashboard(region, district, disease, from, to, user);
	}

	@Override
	public List<DashboardQuarantineDataDto> getQuarantineDataForDashBoard(
		RegionReferenceDto regionRef,
		DistrictReferenceDto districtRef,
		Disease disease,
		Date from,
		Date to) {

		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);
		User user = userService.getCurrentUser();

		List<DashboardQuarantineDataDto> dashboardContactsInQuarantine =
			contactService.getQuarantineDataForDashBoard(region, district, disease, from, to, user);

		return dashboardContactsInQuarantine;
	}

	@Override
	public Map<ContactStatus, Long> getNewContactCountPerStatus(ContactCriteria contactCriteria) {

		User user = userService.getCurrentUser();
		return contactService.getNewContactCountPerStatus(contactCriteria, user);
	}

	@Override
	public Map<ContactClassification, Long> getNewContactCountPerClassification(ContactCriteria contactCriteria) {

		User user = userService.getCurrentUser();
		return contactService.getNewContactCountPerClassification(contactCriteria, user);
	}

	@Override
	public Map<FollowUpStatus, Long> getNewContactCountPerFollowUpStatus(ContactCriteria contactCriteria) {

		User user = userService.getCurrentUser();
		return contactService.getNewContactCountPerFollowUpStatus(contactCriteria, user);
	}

	@Override
	public int getFollowUpUntilCount(ContactCriteria contactCriteria) {

		User user = userService.getCurrentUser();
		return contactService.getFollowUpUntilCount(contactCriteria, user);
	}

	public ContactDto convertToDto(Contact source, Pseudonymizer pseudonymizer) {

		ContactDto dto = toDto(source);
		pseudonymizeDto(source, dto, pseudonymizer);

		return dto;
	}

	private void pseudonymizeDto(Contact source, ContactDto dto, Pseudonymizer pseudonymizer) {
		if (dto != null) {
			boolean isInJurisdiction = contactJurisdictionChecker.isInJurisdictionOrOwned(source);
			User currentUser = userService.getCurrentUser();

			pseudonymizer.pseudonymizeDto(ContactDto.class, dto, isInJurisdiction, (c) -> {
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), currentUser, dto::setReportingUser);

				if (c.getCaze() != null) {
					Boolean isCaseInJurisdiction = caseJurisdictionChecker.isInJurisdictionOrOwned(source.getCaze());
					pseudonymizer.pseudonymizeDto(CaseReferenceDto.class, c.getCaze(), isCaseInJurisdiction, null);
				}

				pseudonymizer.pseudonymizeDto(
					EpiDataDto.class,
					dto.getEpiData(),
					isInJurisdiction,
					e -> pseudonymizer
						.pseudonymizeDtoCollection(ExposureDto.class, e.getExposures(), exp -> isInJurisdiction, (exp, expInJurisdiction) -> {
							pseudonymizer.pseudonymizeDto(LocationDto.class, exp.getLocation(), expInJurisdiction, null);
						}));
			});
		}
	}

	private void restorePseudonymizedDto(ContactDto dto, Contact existingContact, ContactDto existingContactDto) {
		if (existingContactDto != null) {
			boolean isInJurisdiction = contactJurisdictionChecker.isInJurisdictionOrOwned(existingContact);
			User currentUser = userService.getCurrentUser();
			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);

			pseudonymizer.restoreUser(existingContact.getReportingUser(), currentUser, dto, dto::setReportingUser);
			pseudonymizer.restorePseudonymizedValues(ContactDto.class, dto, existingContactDto, isInJurisdiction);
			pseudonymizer.restorePseudonymizedValues(EpiDataDto.class, dto.getEpiData(), existingContactDto.getEpiData(), isInJurisdiction);
		}
	}

	private ContactReferenceDto convertToReferenceDto(Contact source) {

		ContactReferenceDto dto = toReferenceDto(source);

		if (source != null && dto != null) {
			boolean isInJurisdiction = contactJurisdictionChecker.isInJurisdictionOrOwned(source);
			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);

			pseudonymizer.pseudonymizeDto(ContactReferenceDto.class, dto, isInJurisdiction, (c) -> {
				if (source.getCaze() != null) {
					Boolean isCaseInJurisdiction = caseJurisdictionChecker.isInJurisdictionOrOwned(source.getCaze());
					pseudonymizer.pseudonymizeDto(ContactReferenceDto.PersonName.class, c.getCaseName(), isCaseInJurisdiction, null);
				}

				pseudonymizer.pseudonymizeDto(ContactReferenceDto.PersonName.class, c.getContactName(), isInJurisdiction, null);
			});
		}

		return dto;
	}

	public static ContactReferenceDto toReferenceDto(Contact source) {

		if (source == null) {
			return null;
		}

		return source.toReference();
	}

	public static ContactDto toDto(Contact source) {

		if (source == null) {
			return null;
		}
		ContactDto target = new ContactDto();
		DtoHelper.fillDto(target, source);

		target.setCaze(CaseFacadeEjb.toReferenceDto(source.getCaze()));
		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));

		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setReportDateTime(source.getReportDateTime());

		target.setMultiDayContact(source.isMultiDayContact());
		target.setFirstContactDate(source.getFirstContactDate());
		target.setLastContactDate(source.getLastContactDate());
		target.setContactIdentificationSource(source.getContactIdentificationSource());
		target.setContactIdentificationSourceDetails(source.getContactIdentificationSourceDetails());
		target.setTracingApp(source.getTracingApp());
		target.setTracingAppDetails(source.getTracingAppDetails());
		target.setContactProximity(source.getContactProximity());
		target.setContactClassification(source.getContactClassification());
		target.setContactStatus(source.getContactStatus());
		target.setFollowUpStatus(source.getFollowUpStatus());
		target.setFollowUpComment(source.getFollowUpComment());
		target.setFollowUpUntil(source.getFollowUpUntil());
		target.setOverwriteFollowUpUntil(source.isOverwriteFollowUpUntil());
		target.setContactOfficer(UserFacadeEjb.toReferenceDto(source.getContactOfficer()));
		target.setDescription(source.getDescription());
		target.setRelationToCase(source.getRelationToCase());
		target.setRelationDescription(source.getRelationDescription());
		target.setResultingCase(CaseFacadeEjb.toReferenceDto(source.getResultingCase()));

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
		target.setExternalID(source.getExternalID());

		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));

		target.setHighPriority(source.isHighPriority());
		target.setImmunosuppressiveTherapyBasicDisease(source.getImmunosuppressiveTherapyBasicDisease());
		target.setImmunosuppressiveTherapyBasicDiseaseDetails(source.getImmunosuppressiveTherapyBasicDiseaseDetails());
		target.setCareForPeopleOver60(source.getCareForPeopleOver60());

		target.setQuarantine(source.getQuarantine());
		target.setQuarantineTypeDetails(source.getquarantineTypeDetails());
		target.setQuarantineFrom(source.getQuarantineFrom());
		target.setQuarantineTo(source.getQuarantineTo());

		target.setCaseIdExternalSystem(source.getCaseIdExternalSystem());
		target.setCaseOrEventInformation(source.getCaseOrEventInformation());

		target.setContactProximityDetails(source.getContactProximityDetails());
		target.setContactCategory(source.getContactCategory());

		target.setQuarantineHelpNeeded(source.getQuarantineHelpNeeded());
		target.setQuarantineOrderedVerbally(source.isQuarantineOrderedVerbally());
		target.setQuarantineOrderedOfficialDocument(source.isQuarantineOrderedOfficialDocument());
		target.setQuarantineOrderedVerballyDate(source.getQuarantineOrderedVerballyDate());
		target.setQuarantineOrderedOfficialDocumentDate(source.getQuarantineOrderedOfficialDocumentDate());
		target.setQuarantineHomePossible(source.getQuarantineHomePossible());
		target.setQuarantineHomePossibleComment(source.getQuarantineHomePossibleComment());
		target.setQuarantineHomeSupplyEnsured(source.getQuarantineHomeSupplyEnsured());
		target.setQuarantineHomeSupplyEnsuredComment(source.getQuarantineHomeSupplyEnsuredComment());
		target.setQuarantineExtended(source.isQuarantineExtended());
		target.setQuarantineReduced(source.isQuarantineReduced());
		target.setQuarantineOfficialOrderSent(source.isQuarantineOfficialOrderSent());
		target.setQuarantineOfficialOrderSentDate(source.getQuarantineOfficialOrderSentDate());
		target.setAdditionalDetails(source.getAdditionalDetails());

		target.setEpiData(EpiDataFacadeEjb.toDto(source.getEpiData()));
		target.setHealthConditions(ClinicalCourseFacadeEjb.toHealthConditionsDto(source.getHealthConditions()));
		target.setReturningTraveler(source.getReturningTraveler());
		target.setEndOfQuarantineReason(source.getEndOfQuarantineReason());
		target.setEndOfQuarantineReasonDetails(source.getEndOfQuarantineReasonDetails());

		target.setSormasToSormasOriginInfo(SormasToSormasOriginInfoFacadeEjb.toDto(source.getSormasToSormasOriginInfo()));
		target.setOwnershipHandedOver(source.getSormasToSormasShares().stream().anyMatch(SormasToSormasShareInfo::isOwnershipHandedOver));

		return target;
	}

	@RolesAllowed(UserRole._SYSTEM)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void generateContactFollowUpTasks() {

		// get all contacts that are followed up
		LocalDateTime fromDateTime = LocalDate.now().atStartOfDay();
		LocalDateTime toDateTime = fromDateTime.plusDays(1);
		List<Contact> contacts = contactService.getFollowUpBetween(DateHelper8.toDate(fromDateTime), DateHelper8.toDate(toDateTime));

		for (Contact contact : contacts) {
			// Only generate tasks for contacts that are under follow-up
			if (!(contact.getFollowUpStatus().equals(FollowUpStatus.FOLLOW_UP) || contact.getFollowUpStatus().equals(FollowUpStatus.LOST))) {
				continue;
			}

			User assignee;
			try {
				assignee = taskService.getTaskAssignee(contact);
			} catch (TaskCreationException e) {
				logger.warn(e.getMessage());
				continue;
			}

			// find already existing tasks
			TaskCriteria pendingUserTaskCriteria = new TaskCriteria().contact(contact.toReference())
				.taskType(TaskType.CONTACT_FOLLOW_UP)
				.assigneeUser(assignee.toReference())
				.taskStatus(TaskStatus.PENDING);
			List<Task> pendingUserTasks = taskService.findBy(pendingUserTaskCriteria, true);

			if (!pendingUserTasks.isEmpty()) {
				// the user still has a pending task for this contact
				continue;
			}

			TaskCriteria dayTaskCriteria = new TaskCriteria().contact(contact.toReference())
				.taskType(TaskType.CONTACT_FOLLOW_UP)
				.dueDateBetween(DateHelper8.toDate(fromDateTime), DateHelper8.toDate(toDateTime));
			List<Task> dayTasks = taskService.findBy(dayTaskCriteria, true);

			if (!dayTasks.isEmpty()) {
				// there is already a task for the exact day
				continue;
			}

			// none found -> create the task
			Task task = createContactTask(TaskType.CONTACT_FOLLOW_UP, fromDateTime, toDateTime, contact, assignee);
			taskService.ensurePersisted(task);
		}
	}

	private Task createContactTask(TaskType taskType, LocalDateTime fromDateTime, LocalDateTime toDateTime, Contact contact, User assignee) {
		Task task = taskService.buildTask(null);
		task.setTaskContext(TaskContext.CONTACT);
		task.setContact(contact);
		task.setTaskType(taskType);
		task.setSuggestedStart(DateHelper8.toDate(fromDateTime));
		task.setDueDate(DateHelper8.toDate(toDateTime.minusMinutes(1)));
		task.setAssigneeUser(assignee);

		if (contact.isHighPriority()) {
			task.setPriority(TaskPriority.HIGH);
		}
		return task;
	}

	@Override
	public void validate(ContactDto contact) throws ValidationRuntimeException {

		// Check whether any required field that does not have a not null constraint in the database is empty
		if (contact.getReportDateTime() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validReportDateTime));
		}
		if (contact.getReportingUser() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validReportingUser));
		}
		if (contact.getDisease() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validDisease));
		}
		if (contact.getPerson() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validPerson));
		}
		if (contact.isOverwriteFollowUpUntil() && contact.getFollowUpUntil() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.emptyOverwrittenFollowUpUntilDate));
		}
		if (contact.getCaze() == null && (contact.getRegion() == null || contact.getDistrict() == null)) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.contactWithoutInfrastructureData));
		}
	}

	@Override
	public List<SimilarContactDto> getMatchingContacts(ContactSimilarityCriteria criteria) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<SimilarContactDto> cq = cb.createQuery(SimilarContactDto.class);
		final Root<Contact> contactRoot = cq.from(Contact.class);

		ContactJoins joins = new ContactJoins(contactRoot);

		cq.multiselect(
			Stream
				.concat(
					Stream.of(
						joins.getPerson().get(Person.FIRST_NAME),
						joins.getPerson().get(Person.LAST_NAME),
						contactRoot.get(Contact.UUID),
						joins.getCaze().get(Case.UUID),
						joins.getCasePerson().get(Person.FIRST_NAME),
						joins.getCasePerson().get(Person.LAST_NAME),
						contactRoot.get(Contact.CASE_ID_EXTERNAL_SYSTEM),
						contactRoot.get(Contact.LAST_CONTACT_DATE),
						contactRoot.get(Contact.CONTACT_PROXIMITY),
						contactRoot.get(Contact.CONTACT_CLASSIFICATION),
						contactRoot.get(Contact.CONTACT_STATUS),
						contactRoot.get(Contact.FOLLOW_UP_STATUS)),
					listCriteriaBuilder.getJurisdictionSelections(joins))
				.collect(Collectors.toList()));

		final Predicate defaultFilter = contactService.createDefaultFilter(cb, contactRoot);
		final Predicate userFilter = contactService.createUserFilter(cb, cq, contactRoot);

		final PersonReferenceDto person = criteria.getPerson();
		final Predicate samePersonFilter = person != null ? cb.equal(joins.getPerson().get(Person.UUID), person.getUuid()) : null;

		final Disease disease = criteria.getDisease();
		final Predicate diseaseFilter = disease != null ? cb.equal(contactRoot.get(Contact.DISEASE), disease) : null;

		final CaseReferenceDto caze = criteria.getCaze();
		final Predicate cazeFilter = caze != null ? cb.equal(joins.getCaze().get(Case.UUID), caze.getUuid()) : null;

		final Date reportDate = criteria.getReportDate();
		final Date lastContactDate = criteria.getLastContactDate();
		final Predicate recentContactsFilter = AbstractAdoService.and(
			cb,
			contactService.recentDateFilter(cb, reportDate, contactRoot.get(Contact.REPORT_DATE_TIME), 30),
			contactService.recentDateFilter(cb, lastContactDate, contactRoot.get(Contact.LAST_CONTACT_DATE), 30));

		cq.where(AbstractAdoService.and(cb, defaultFilter, userFilter, samePersonFilter, diseaseFilter, cazeFilter, recentContactsFilter));

		List<SimilarContactDto> contacts = em.createQuery(cq).getResultList();

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		pseudonymizer.pseudonymizeDtoCollection(
			SimilarContactDto.class,
			contacts,
			c -> contactJurisdictionChecker.isInJurisdictionOrOwned(c.getJurisdiction()),
			(c, isInJurisdiction) -> {
				CaseReferenceDto contactCase = c.getCaze();
				if (contactCase != null) {
					pseudonymizer.pseudonymizeDto(
						CaseReferenceDto.class,
						contactCase,
						caseJurisdictionChecker.isInJurisdictionOrOwned(c.getCaseJurisdiction()),
						null);
				}
			});

		return contacts;
	}

	@Override
	public boolean exists(String uuid) {
		return this.contactService.exists(uuid);
	}

	@LocalBean
	@Stateless
	public static class ContactFacadeEjbLocal extends ContactFacadeEjb {

	}

	@Override
	public boolean isContactEditAllowed(String contactUuid) {
		Contact contact = contactService.getByUuid(contactUuid);

		return contactService.isContactEditAllowed(contact);
	}
}
