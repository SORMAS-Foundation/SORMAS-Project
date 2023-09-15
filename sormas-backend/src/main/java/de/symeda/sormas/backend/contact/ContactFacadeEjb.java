/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import static de.symeda.sormas.backend.visit.VisitLogic.getVisitResult;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.isNull;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.CoreAndPersonDto;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.contact.ContactBulkEditData;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactExportDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactFollowUpDto;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactJurisdictionFlagsDto;
import de.symeda.sormas.api.contact.ContactListEntryDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.MapContactDto;
import de.symeda.sormas.api.contact.MergeContactIndexDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.dashboard.DashboardContactDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.EpiDataHelper;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.followup.FollowUpDto;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.followup.FollowUpPeriodDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasRuntimeException;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.DtoCopyHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.UtilDate;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitResultDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.api.visit.VisitSummaryExportDetailsDto;
import de.symeda.sormas.api.visit.VisitSummaryExportDto;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.clinicalcourse.HealthConditionsMapper;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.TaskCreationException;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.document.Document;
import de.symeda.sormas.backend.document.DocumentService;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb.EpiDataFacadeEjbLocal;
import de.symeda.sormas.backend.event.ContactEventSummaryDetails;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.exposure.Exposure;
import de.symeda.sormas.backend.externaljournal.ExternalJournalService;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.immunization.ImmunizationEntityHelper;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.importexport.ExportHelper;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.entities.contact.SormasToSormasContactFacadeEjb.SormasToSormasContactFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareInfoHelper;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserReference;
import de.symeda.sormas.backend.user.UserRoleFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;
import de.symeda.sormas.backend.vaccination.Vaccination;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb;
import de.symeda.sormas.backend.vaccination.VaccinationService;
import de.symeda.sormas.backend.visit.Visit;
import de.symeda.sormas.backend.visit.VisitFacadeEjb;
import de.symeda.sormas.backend.visit.VisitFacadeEjb.VisitFacadeEjbLocal;
import de.symeda.sormas.backend.visit.VisitService;

@Stateless(name = "ContactFacade")
@RightsAllowed(UserRight._CONTACT_VIEW)
public class ContactFacadeEjb
	extends AbstractCoreFacadeEjb<Contact, ContactDto, ContactIndexDto, ContactReferenceDto, ContactService, ContactCriteria>
	implements ContactFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ContactListCriteriaBuilder listCriteriaBuilder;
	@EJB
	private CaseService caseService;
	@EJB
	private PersonService personService;
	@EJB
	private VisitService visitService;
	@EJB
	private VisitFacadeEjbLocal visitFacade;
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
	private EpiDataFacadeEjbLocal epiDataFacade;
	@EJB
	private SormasToSormasOriginInfoService originInfoService;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private EventService eventService;
	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;
	@EJB
	private SampleService sampleService;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;
	@EJB
	private SampleFacadeEjbLocal sampleFacade;
	@EJB
	private DocumentService documentService;
	@EJB
	private SormasToSormasFacadeEjbLocal sormasToSormasFacade;
	@EJB
	private SormasToSormasContactFacadeEjbLocal sormasToSormasContactFacade;
	@EJB
	private VaccinationFacadeEjb.VaccinationFacadeEjbLocal vaccinationFacade;
	@EJB
	private HealthConditionsMapper healthConditionsMapper;
	@EJB
	private VaccinationService vaccinationService;
	@EJB
	private ContactService contactService;
	@EJB
	private UserRoleFacadeEjb.UserRoleFacadeEjbLocal userRoleFacadeEjb;

	@Resource
	private ManagedScheduledExecutorService executorService;

	public ContactFacadeEjb() {
	}

	@Inject
	public ContactFacadeEjb(ContactService service) {
		super(Contact.class, ContactDto.class, service);
	}

	@Override
	public List<String> getAllActiveUuids() {

		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllActiveUuids(user);
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {

		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return service.getDeletedUuidsSince(user, since);
	}

	private ContactDto getContactWithoutPseudonyimizationByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}

	@Override
	@RightsAllowed({
		UserRight._CONTACT_CREATE,
		UserRight._CONTACT_EDIT })
	public ContactDto save(@Valid @NotNull ContactDto dto) {
		return save(dto, true, true);
	}

	@Override
	@RightsAllowed({
		UserRight._CONTACT_CREATE,
		UserRight._CONTACT_EDIT })
	public ContactDto save(@Valid ContactDto dto, boolean handleChanges, boolean handleCaseChanges) {
		return save(dto, handleChanges, handleCaseChanges, true, true);
	}

	@RightsAllowed({
		UserRight._CONTACT_CREATE,
		UserRight._CONTACT_EDIT })
	public CoreAndPersonDto<ContactDto> save(@Valid @NotNull CoreAndPersonDto<ContactDto> coreAndPersonDto) throws ValidationRuntimeException {
		ContactDto contactDto = coreAndPersonDto.getCoreData();
		CoreAndPersonDto savedCoreAndPersonDto = new CoreAndPersonDto();
		if (coreAndPersonDto.getPerson() != null) {
			PersonDto newlyCreatedPersonDto = personFacade.save(coreAndPersonDto.getPerson());
			contactDto.setPerson(newlyCreatedPersonDto.toReference());
			savedCoreAndPersonDto.setPerson(newlyCreatedPersonDto);
		}
		ContactDto savedContactData = save(contactDto, true, true, true, false);
		savedCoreAndPersonDto.setCoreData(savedContactData);
		return savedCoreAndPersonDto;
	}

	@RightsAllowed({
		UserRight._CONTACT_CREATE,
		UserRight._CONTACT_EDIT })
	public ContactDto save(ContactDto dto, boolean handleChanges, boolean handleCaseChanges, boolean checkChangeDate, boolean internal) {
		final Contact existingContact = dto.getUuid() != null ? service.getByUuid(dto.getUuid(), true) : null;
		FacadeHelper.checkCreateAndEditRights(existingContact, userService, UserRight.CONTACT_CREATE, UserRight.CONTACT_EDIT);

		if (internal && existingContact != null && !service.isEditAllowed(existingContact)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorContactNotEditable));
		}

		if (existingContact != null && internal) {
			EditPermissionType editPermission = service.getEditPermissionType(existingContact);
			if (editPermission == EditPermissionType.OUTSIDE_JURISDICTION) {
				throw new AccessDeniedException(I18nProperties.getString(Strings.errorContactNotEditableOutsideJurisdiction));
			} else if (editPermission != EditPermissionType.ALLOWED) {
				throw new AccessDeniedException(I18nProperties.getString(Strings.errorContactNotEditable));
			}
		}

		final ContactDto existingContactDto = toDto(existingContact);

		restorePseudonymizedDto(dto, existingContactDto, existingContact);

		validateUserRights(dto, existingContactDto);
		validate(dto);
		dto.setReportDateTime(DataHelper.removeTime(dto.getReportDateTime()));
		dto.setLastContactDate(DataHelper.removeTime(dto.getLastContactDate()));

		externalJournalService.handleExternalJournalPersonUpdateAsync(dto.getPerson());

		// taking this out because it may lead to server problems
		// case disease can change over time and there is currently no mechanism that would delete all related contacts
		// in this case the best solution is to only keep this hidden from the UI and still allow it in the backend
		//		if (!DiseaseHelper.hasContactFollowUp(entity.getCaze().getDisease(), entity.getCaze().getPlagueType())) {
		//			throw new UnsupportedOperationException("Contact creation is not allowed for diseases that don't have contact follow-up.");
		//		}

		Contact entity = fillOrBuildEntity(dto, existingContact, checkChangeDate);
		service.ensurePersisted(entity);

		if (existingContact == null && featureConfigurationFacade.isTaskGenerationFeatureEnabled(TaskType.CONTACT_INVESTIGATION)) {
			createInvestigationTask(entity);

		}

		if (handleChanges) {
			entity.setCompleteness(calculateCompleteness(entity));

			updateContactVisitAssociations(existingContactDto, entity);

			final boolean convertedToCase =
				(existingContactDto == null || existingContactDto.getResultingCase() == null) && entity.getResultingCase() != null;
			final boolean dropped = entity.getContactStatus() == ContactStatus.DROPPED
				&& (existingContactDto == null || existingContactDto.getContactStatus() != ContactStatus.DROPPED);
			if (dropped || convertedToCase) {
				service.cancelFollowUp(
					entity,
					I18nProperties
						.getString(convertedToCase ? Strings.messageSystemFollowUpCanceled : Strings.messageSystemFollowUpCanceledByDropping));
			} else {
				service.updateFollowUpDetails(
					entity,
					existingContactDto != null && entity.getFollowUpStatus() != existingContactDto.getFollowUpStatus());
			}

			service.udpateContactStatus(entity);

			if (handleCaseChanges && entity.getCaze() != null) {
				caseFacade.onCaseChanged(caseFacade.toDto(entity.getCaze()), entity.getCaze(), internal);
			}

			onContactChanged(existingContactDto, entity, internal);
		}

		return toDto(entity);
	}

	@PermitAll
	public void onContactChanged(ContactDto contact, boolean syncShares) {
		if (syncShares && sormasToSormasFacade.isFeatureConfigured()) {
			syncSharesAsync(new ShareTreeCriteria(contact.getUuid()));
		}
	}

	@PermitAll
	public void onContactChanged(ContactDto existingContact, Contact contact, boolean syncShares) {

		if (existingContact == null) {
			vaccinationFacade.updateVaccinationStatuses(contact);
		}

		onContactChanged(toDto(contact), syncShares);

		// This logic should be consistent with ContactDataForm.onContactChanged
		if (existingContact != null
			&& existingContact.getQuarantineTo() != null
			&& !existingContact.getQuarantineTo().equals(contact.getQuarantineTo())) {
			contact.setPreviousQuarantineTo(existingContact.getQuarantineTo());
		}
	}

	@RightsAllowed(UserRight._CONTACT_EDIT)
	public void syncSharesAsync(ShareTreeCriteria criteria) {
		executorService.schedule(() -> {
			sormasToSormasContactFacade.syncShares(criteria);
		}, 5, TimeUnit.SECONDS);
	}

	private void createInvestigationTask(Contact entity) {
		LocalDate now = LocalDate.now();
		LocalDate reportDate = UtilDate.toLocalDate(entity.getReportDateTime());
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
			&& Objects.equals(existingContact.getReportDateTime(), contact.getReportDateTime())
			&& Objects.equals(existingContact.getLastContactDate(), contact.getLastContactDate())
			&& Objects.equals(existingContact.getFollowUpUntil(), contact.getFollowUpUntil())
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
	@RightsAllowed({
		UserRight._DASHBOARD_CONTACT_VIEW })
	public Long countContactsForMap(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to) {
		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);

		return service.countContactsForMap(region, district, disease, from, to);
	}

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_CONTACT_VIEW })
	public List<MapContactDto> getContactsForMap(
		RegionReferenceDto regionRef,
		DistrictReferenceDto districtRef,
		Disease disease,
		Date from,
		Date to) {

		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);

		return service.getContactsForMap(region, district, disease, from, to);
	}

	@Override
	public Page<ContactIndexDto> getIndexPage(ContactCriteria contactCriteria, Integer offset, Integer size, List<SortProperty> sortProperties) {
		List<ContactIndexDto> contactIndexList = getIndexList(contactCriteria, offset, size, sortProperties);
		long totalElementCount = count(contactCriteria);
		return new Page<>(contactIndexList, offset, size, totalElementCount);
	}

	public Page<ContactIndexDetailedDto> getIndexDetailedPage(
		@NotNull ContactCriteria contactCriteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {
		List<ContactIndexDetailedDto> contactIndexDetailedList = getIndexDetailedList(contactCriteria, offset, size, sortProperties);
		long totalElementCount = count(contactCriteria);
		return new Page<>(contactIndexDetailedList, offset, size, totalElementCount);
	}

	@Override
	@RightsAllowed(UserRight._CONTACT_DELETE)
	public void delete(String contactUuid, DeletionDetails deletionDetails) {
		Contact contact = service.getByUuid(contactUuid);
		deleteContact(contact, deletionDetails);
	}

	@Override
	@RightsAllowed(UserRight._CONTACT_DELETE)
	public List<String> delete(List<String> uuids, DeletionDetails deletionDetails) {
		List<String> deletedContactUuids = new ArrayList<>();
		List<Contact> contactsToBeDeleted = service.getByUuids(uuids);
		if (contactsToBeDeleted != null) {
			contactsToBeDeleted.forEach(contactToBeDeleted -> {
				if (!contactToBeDeleted.isDeleted()) {
					try {
						deleteContact(contactToBeDeleted, deletionDetails);
						deletedContactUuids.add(contactToBeDeleted.getUuid());
					} catch (AccessDeniedException e) {
						logger.error("The contact with uuid {} could not be deleted", contactToBeDeleted.getUuid(), e);
					}
				}
			});
		}
		return deletedContactUuids;
	}

	@Override
	@RightsAllowed(UserRight._CONTACT_DELETE)
	public void restore(String uuid) {
		super.restore(uuid);
	}

	@Override
	@RightsAllowed(UserRight._CONTACT_DELETE)
	public List<String> restore(List<String> uuids) {
		List<String> restoredContactUuids = new ArrayList<>();
		List<Contact> contactsToBeRestored = contactService.getByUuids(uuids);

		if (contactsToBeRestored != null) {
			contactsToBeRestored.forEach(contactToBeRestored -> {
				try {
					restore(contactToBeRestored.getUuid());
					restoredContactUuids.add(contactToBeRestored.getUuid());
				} catch (Exception e) {
					logger.error("The contact with uuid:" + contactToBeRestored.getUuid() + "could not be restored");
				}
			});
		}

		return restoredContactUuids;
	}

	private void deleteContact(Contact contact, DeletionDetails deletionDetails) {

		if (!contactService.inJurisdictionOrOwned(contact)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.messageContactOutsideJurisdictionDeletionDenied));
		}
		externalJournalService.handleExternalJournalPersonUpdateAsync(contact.getPerson().toReference());
		try {
			sormasToSormasFacade.revokePendingShareRequests(contact.getSormasToSormasShares(), true);
		} catch (SormasToSormasException e) {
			throw new SormasToSormasRuntimeException(e);
		}

		service.delete(contact, deletionDetails);
		if (contact.getCaze() != null) {
			caseFacade.onCaseChanged(caseFacade.toDto(contact.getCaze()), contact.getCaze());
		}
	}

	@Override
	@RightsAllowed(UserRight._CONTACT_ARCHIVE)
	public void archive(String entityUuid, Date endOfProcessingDate) {
		super.archive(entityUuid, endOfProcessingDate);
	}

	@Override
	@RightsAllowed(UserRight._CONTACT_ARCHIVE)
	public void archive(List<String> entityUuids) {
		super.archive(entityUuids);
	}

	@Override
	@RightsAllowed(UserRight._CONTACT_ARCHIVE)
	public void dearchive(List<String> entityUuids, String dearchiveReason) {
		super.dearchive(entityUuids, dearchiveReason);
	}

	@Override
	@RightsAllowed(UserRight._CONTACT_EXPORT)
	public List<ContactExportDto> getExportList(
		ContactCriteria contactCriteria,
		Collection<String> selectedRows,
		int first,
		int max,
		ExportConfigurationDto exportConfiguration,
		Language userLanguage) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<ContactExportDto> cq = cb.createQuery(ContactExportDto.class);
		final Root<Contact> contact = cq.from(Contact.class);

		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contact);
		final ContactJoins joins = contactQueryContext.getJoins();

		cq.multiselect(
			contact.get(Contact.ID),
			joins.getPerson().get(Person.ID),
			contact.get(Contact.UUID),
			joins.getCaze().get(Case.UUID),
			joins.getCaze().get(Case.CASE_CLASSIFICATION),
			contact.get(Contact.DISEASE),
			contact.get(Contact.DISEASE_DETAILS),
			contact.get(Contact.CONTACT_CLASSIFICATION),
			contact.get(Contact.MULTI_DAY_CONTACT),
			contact.get(Contact.FIRST_CONTACT_DATE),
			contact.get(Contact.LAST_CONTACT_DATE),
			contact.get(Contact.CREATION_DATE),
			joins.getPerson().get(Person.UUID),
			joins.getPerson().get(Person.FIRST_NAME),
			joins.getPerson().get(Person.LAST_NAME),
			joins.getPerson().get(Person.SALUTATION),
			joins.getPerson().get(Person.OTHER_SALUTATION),
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
			contact.get(Contact.COMPLETENESS),
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
			contact.get(Contact.PROHIBITION_TO_WORK),
			contact.get(Contact.PROHIBITION_TO_WORK_FROM),
			contact.get(Contact.PROHIBITION_TO_WORK_UNTIL),
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
			contactQueryContext.getSubqueryExpression(ContactQueryContext.PERSON_PHONE_SUBQUERY),
			contactQueryContext.getSubqueryExpression(ContactQueryContext.PERSON_PHONE_OWNER_SUBQUERY),
			contactQueryContext.getSubqueryExpression(ContactQueryContext.PERSON_EMAIL_SUBQUERY),
			contactQueryContext.getSubqueryExpression(ContactQueryContext.PERSON_OTHER_CONTACT_DETAILS_SUBQUERY),
			joins.getPerson().get(Person.OCCUPATION_TYPE),
			joins.getPerson().get(Person.OCCUPATION_DETAILS),
			joins.getPerson().get(Person.ARMED_FORCES_RELATION_TYPE),
			joins.getRegion().get(Region.NAME),
			joins.getDistrict().get(District.NAME),
			joins.getCommunity().get(Community.NAME),
			joins.getEpiData().get(EpiData.ID),
			joins.getEpiData().get(EpiData.CONTACT_WITH_SOURCE_CASE_KNOWN),
			contact.get(Contact.RETURNING_TRAVELER),
			contact.get(Contact.VACCINATION_STATUS),
			contact.get(Contact.EXTERNAL_ID),
			contact.get(Contact.EXTERNAL_TOKEN),
			contact.get(Contact.INTERNAL_TOKEN),
			joins.getPerson().get(Person.BIRTH_NAME),
			joins.getPersonBirthCountry().get(Country.ISO_CODE),
			joins.getPersonBirthCountry().get(Country.DEFAULT_NAME),
			joins.getPersonCitizenship().get(Country.ISO_CODE),
			joins.getPersonCitizenship().get(Country.DEFAULT_NAME),
			joins.getReportingDistrict().get(District.NAME),
			joins.getPerson().get(Person.SYMPTOM_JOURNAL_STATUS),
			joins.getReportingUser().get(User.ID),
			joins.getFollowUpStatusChangeUser().get(User.ID),
			contact.get(Contact.PREVIOUS_QUARANTINE_TO),
			contact.get(Contact.QUARANTINE_CHANGE_COMMENT),
			jurisdictionSelector(contactQueryContext));

		cq.distinct(true);

		Predicate filter = listCriteriaBuilder.buildContactFilter(contactCriteria, contactQueryContext);

		filter = CriteriaBuilderHelper.andInValues(selectedRows, filter, cb, contact.get(Contact.UUID));
		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.desc(contact.get(Contact.REPORT_DATE_TIME)), cb.desc(contact.get(Contact.ID)));

		List<ContactExportDto> exportContacts = QueryHelper.getResultList(em, cq, first, max);
		List<String> resultContactsUuids = exportContacts.stream().map(ContactExportDto::getUuid).collect(Collectors.toList());

		if (!exportContacts.isEmpty()) {
			List<Long> exportContactIds = exportContacts.stream().map(e -> e.getId()).collect(Collectors.toList());

			List<VisitSummaryExportDetails> visitSummaries = null;
			if (ExportHelper.shouldExportFields(
				exportConfiguration,
				ContactExportDto.NUMBER_OF_VISITS,
				ContactExportDto.LAST_COOPERATIVE_VISIT_DATE,
				ContactExportDto.LAST_COOPERATIVE_VISIT_SYMPTOMATIC,
				ContactExportDto.LAST_COOPERATIVE_VISIT_SYMPTOMS)) {
				CriteriaQuery<VisitSummaryExportDetails> visitsCq = cb.createQuery(VisitSummaryExportDetails.class);
				Root<Contact> visitsCqRoot = visitsCq.from(Contact.class);
				ContactJoins visitContactJoins = new ContactJoins(visitsCqRoot);

				visitsCq.where(
					CriteriaBuilderHelper
						.and(cb, contact.get(AbstractDomainObject.ID).in(exportContactIds), cb.isNotEmpty(visitsCqRoot.get(Contact.VISITS))));
				visitsCq.multiselect(
					visitsCqRoot.get(AbstractDomainObject.ID),
					visitContactJoins.getVisits().get(Visit.VISIT_DATE_TIME),
					visitContactJoins.getVisits().get(Visit.VISIT_STATUS),
					visitContactJoins.getVisitSymptoms(),
					jurisdictionSelector(new ContactQueryContext(cb, cq, visitsCqRoot)));

				visitSummaries = em.createQuery(visitsCq).getResultList();
			}

			Map<Long, List<Exposure>> exposures = null;
			if (ExportHelper.shouldExportFields(
				exportConfiguration,
				ContactExportDto.TRAVELED,
				ContactExportDto.TRAVEL_HISTORY,
				ContactExportDto.BURIAL_ATTENDED)) {
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
				List<Exposure> exposureList = em.createQuery(exposuresCq).setHint(ModelConstants.READ_ONLY, true).getResultList();
				exposures = exposureList.stream().collect(Collectors.groupingBy(e -> e.getEpiData().getId()));
			}

			Map<Long, List<Immunization>> immunizations = null;
			if (ExportHelper.shouldExportFields(exportConfiguration, ExportHelper.getVaccinationExportProperties())) {
				List<Immunization> immunizationList;
				CriteriaQuery<Immunization> immunizationsCq = cb.createQuery(Immunization.class);
				Root<Immunization> immunizationsCqRoot = immunizationsCq.from(Immunization.class);
				Join<Immunization, Person> personJoin = immunizationsCqRoot.join(Immunization.PERSON, JoinType.LEFT);
				Expression<String> personIdsExpr = personJoin.get(Person.ID);
				immunizationsCq.where(
					CriteriaBuilderHelper.and(
						cb,
						cb.or(
							cb.equal(immunizationsCqRoot.get(Immunization.MEANS_OF_IMMUNIZATION), MeansOfImmunization.VACCINATION),
							cb.equal(immunizationsCqRoot.get(Immunization.MEANS_OF_IMMUNIZATION), MeansOfImmunization.VACCINATION_RECOVERY)),
						personIdsExpr.in(exportContacts.stream().map(ContactExportDto::getPersonId).collect(Collectors.toList()))));
				immunizationList = em.createQuery(immunizationsCq).setHint(ModelConstants.READ_ONLY, true).getResultList();
				immunizations = immunizationList.stream().collect(Collectors.groupingBy(i -> i.getPerson().getId()));
			}

			Map<String, List<ContactEventSummaryDetails>> eventSummaries = null;
			if (ExportHelper.shouldExportFields(
				exportConfiguration,
				ContactExportDto.EVENT_COUNT,
				ContactExportDto.LATEST_EVENT_ID,
				ContactExportDto.LATEST_EVENT_TITLE)) {
				// Load event count and latest events info per contact
				eventSummaries = eventService.getEventSummaryDetailsByContacts(resultContactsUuids)
					.stream()
					.collect(Collectors.groupingBy(ContactEventSummaryDetails::getContactUuid, Collectors.toList()));
			}

			Map<Long, UserReference> contactUsers = getContactUsersForExport(exportContacts, exportConfiguration);

			// Adding a second query here is not perfect, but selecting the last cooperative visit with a criteria query
			// doesn't seem to be possible and using a native query is not an option because of user filters
			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
			for (ContactExportDto exportContact : exportContacts) {
				boolean inJurisdiction = exportContact.getInJurisdiction();

				if (visitSummaries != null) {
					List<VisitSummaryExportDetails> visits =
						visitSummaries.stream().filter(v -> v.getContactId() == exportContact.getId()).collect(Collectors.toList());

					VisitSummaryExportDetails lastCooperativeVisit = visits.stream()
						.filter(v -> v.getVisitStatus() == VisitStatus.COOPERATIVE)
						.max(Comparator.comparing(VisitSummaryExportDetails::getVisitDateTime))
						.orElse(null);

					exportContact.setNumberOfVisits(visits.size());

					if (lastCooperativeVisit != null) {
						SymptomsDto symptoms = SymptomsFacadeEjb.toSymptomsDto(lastCooperativeVisit.getSymptoms());
						pseudonymizer.pseudonymizeDto(SymptomsDto.class, symptoms, inJurisdiction, null);

						exportContact.setLastCooperativeVisitDate(lastCooperativeVisit.getVisitDateTime());
						exportContact.setLastCooperativeVisitSymptoms(SymptomsHelper.buildSymptomsHumanString(symptoms, true, userLanguage));
						exportContact.setLastCooperativeVisitSymptomatic(
							symptoms.getSymptomatic() == null
								? YesNoUnknown.UNKNOWN
								: (symptoms.getSymptomatic() ? YesNoUnknown.YES : YesNoUnknown.NO));
					}
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

				if (immunizations != null) {
					Optional.ofNullable(immunizations.get(exportContact.getPersonId())).ifPresent(contactImmunizations -> {
						List<Immunization> filteredImmunizations =
							contactImmunizations.stream().filter(i -> i.getDisease() == exportContact.getDisease()).collect(Collectors.toList());
						if (filteredImmunizations.size() > 0) {
							filteredImmunizations.sort(Comparator.comparing(i -> ImmunizationEntityHelper.getDateForComparison(i, false)));
							Immunization mostRecentImmunization = filteredImmunizations.get(filteredImmunizations.size() - 1);
							Integer numberOfDoses = mostRecentImmunization.getNumberOfDoses();

							List<Vaccination> relevantSortedVaccinations = vaccinationService.getRelevantSortedVaccinations(
								filteredImmunizations.stream().flatMap(i -> i.getVaccinations().stream()).collect(Collectors.toList()),
								exportContact.getLastContactDate(),
								exportContact.getReportDate());
							Vaccination firstVaccination = null;
							Vaccination lastVaccination = null;

							if (CollectionUtils.isNotEmpty(relevantSortedVaccinations)) {
								firstVaccination = relevantSortedVaccinations.get(0);
								lastVaccination = relevantSortedVaccinations.get(relevantSortedVaccinations.size() - 1);
								exportContact.setFirstVaccinationDate(firstVaccination.getVaccinationDate());
								exportContact.setLastVaccinationDate(lastVaccination.getVaccinationDate());
								exportContact.setVaccineName(lastVaccination.getVaccineName());
								exportContact.setOtherVaccineName(lastVaccination.getOtherVaccineName());
								exportContact.setVaccineManufacturer(lastVaccination.getVaccineManufacturer());
								exportContact.setOtherVaccineManufacturer(lastVaccination.getOtherVaccineManufacturer());
								exportContact.setVaccinationInfoSource(lastVaccination.getVaccinationInfoSource());
								exportContact.setVaccineAtcCode(lastVaccination.getVaccineAtcCode());
								exportContact.setVaccineBatchNumber(lastVaccination.getVaccineBatchNumber());
								exportContact.setVaccineUniiCode(lastVaccination.getVaccineUniiCode());
								exportContact.setVaccineInn(lastVaccination.getVaccineInn());
							}

							exportContact.setNumberOfDoses(
								numberOfDoses != null ? String.valueOf(numberOfDoses) : getNumberOfDosesFromVaccinations(lastVaccination));
						}
					});
				}

				if (eventSummaries != null) {
					List<ContactEventSummaryDetails> contactEvents = eventSummaries.getOrDefault(exportContact.getUuid(), Collections.emptyList());
					exportContact.setEventCount((long) contactEvents.size());
					contactEvents.stream().max(Comparator.comparing(ContactEventSummaryDetails::getEventDate)).ifPresent(eventSummary -> {
						exportContact.setLatestEventId(eventSummary.getEventUuid());
						exportContact.setLatestEventTitle(eventSummary.getEventTitle());
					});
				}

				if (!contactUsers.isEmpty()) {
					if (exportContact.getReportingUserId() != null) {
						UserReference user = contactUsers.get(exportContact.getReportingUserId());

						exportContact.setReportingUserName(user.getName());
						exportContact.setReportingUserRoles(
							user.getUserRoles().stream().map(userRole -> UserRoleFacadeEjb.toReferenceDto(userRole)).collect(Collectors.toSet()));
					}

					if (exportContact.getFollowUpStatusChangeUserId() != null) {
						UserReference user = contactUsers.get(exportContact.getFollowUpStatusChangeUserId());

						exportContact.setFollowUpStatusChangeUserName(user.getName());
						exportContact.setFollowUpStatusChangeUserRoles(
							user.getUserRoles().stream().map(userRole -> UserRoleFacadeEjb.toReferenceDto(userRole)).collect(Collectors.toSet()));
					}
				}

				pseudonymizer.pseudonymizeDto(ContactExportDto.class, exportContact, inJurisdiction, null);
			}
		}

		return exportContacts;
	}

	private Map<Long, UserReference> getContactUsersForExport(List<ContactExportDto> exportContacts, ExportConfigurationDto exportConfiguration) {
		Map<Long, UserReference> contactUsers = Collections.emptyMap();

		if (exportConfiguration == null
			|| exportConfiguration.getProperties().contains(ContactDto.REPORTING_USER)
			|| exportConfiguration.getProperties().contains(ContactDto.FOLLOW_UP_STATUS_CHANGE_USER)) {
			Set<Long> userIds = exportContacts.stream()
				.map((c -> Arrays.asList(c.getReportingUserId(), c.getFollowUpStatusChangeUserId())))
				.flatMap(Collection::stream)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
			contactUsers = userService.getUserReferencesByIds(userIds).stream().collect(Collectors.toMap(UserReference::getId, Function.identity()));
		}

		return contactUsers;
	}

	@Override
	@RightsAllowed(UserRight._VISIT_EXPORT)
	public List<VisitSummaryExportDto> getVisitSummaryExportList(
		ContactCriteria contactCriteria,
		Collection<String> selectedRows,
		int first,
		int max,
		Language userLanguage) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<VisitSummaryExportDto> cq = cb.createQuery(VisitSummaryExportDto.class);
		final Root<Contact> contactRoot = cq.from(Contact.class);
		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contactRoot);
		final ContactJoins contactJoins = contactQueryContext.getJoins();
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

		Predicate filter = CriteriaBuilderHelper
			.and(cb, listCriteriaBuilder.buildContactFilter(contactCriteria, contactQueryContext), cb.isNotEmpty(contactRoot.get(Contact.VISITS)));
		filter = CriteriaBuilderHelper.andInValues(selectedRows, filter, cb, contactRoot.get(Contact.UUID));
		cq.where(filter);
		cq.orderBy(cb.asc(contactRoot.get(Contact.REPORT_DATE_TIME)));

		List<VisitSummaryExportDto> visitSummaries = QueryHelper.getResultList(em, cq, first, max);

		if (!visitSummaries.isEmpty()) {
			List<String> visitSummaryUuids = visitSummaries.stream().map(e -> e.getUuid()).collect(Collectors.toList());

			CriteriaQuery<VisitSummaryExportDetails> visitsCq = cb.createQuery(VisitSummaryExportDetails.class);
			Root<Contact> visitsCqRoot = visitsCq.from(Contact.class);
			ContactJoins joins = new ContactJoins(visitsCqRoot);

			visitsCq.where(
				CriteriaBuilderHelper
					.and(cb, contactRoot.get(AbstractDomainObject.UUID).in(visitSummaryUuids), cb.isNotEmpty(visitsCqRoot.get(Contact.VISITS))));
			visitsCq.multiselect(
				visitsCqRoot.get(AbstractDomainObject.ID),
				joins.getVisits().get(Visit.VISIT_DATE_TIME),
				joins.getVisits().get(Visit.VISIT_STATUS),
				joins.getVisitSymptoms(),
				jurisdictionSelector(new ContactQueryContext(cb, cq, visitsCqRoot)));
			visitsCq.orderBy(cb.asc(joins.getVisits().get(Visit.VISIT_DATE_TIME)));

			List<VisitSummaryExportDetails> visitSummaryDetails = em.createQuery(visitsCq).getResultList();

			Map<Long, VisitSummaryExportDto> visitSummaryMap =
				visitSummaries.stream().collect(Collectors.toMap(VisitSummaryExportDto::getContactId, Function.identity()));

			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
			visitSummaryDetails.forEach(v -> {
				SymptomsDto symptoms = SymptomsFacadeEjb.toSymptomsDto(v.getSymptoms());
				pseudonymizer.pseudonymizeDto(SymptomsDto.class, symptoms, v.getInJurisdiction(), null);

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

		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contactRoot);

		final ContactJoins joins = (ContactJoins) contactQueryContext.getJoins();
		joins.getVisits();

		Predicate filter = listCriteriaBuilder.buildContactFilter(contactCriteria, contactQueryContext);
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

		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, root);

		Predicate filter = listCriteriaBuilder.buildContactFilter(contactCriteria, contactQueryContext);

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(root));
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

		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contact);
		final ContactJoins joins = contactQueryContext.getJoins();

		cq.multiselect(
			contact.get(Contact.UUID),
			contact.get(Contact.CHANGE_DATE),
			joins.getPerson().get(Person.FIRST_NAME),
			joins.getPerson().get(Person.LAST_NAME),
			joins.getContactOfficer().get(User.UUID),
			joins.getContactOfficer().get(User.FIRST_NAME),
			joins.getContactOfficer().get(User.LAST_NAME),
			contact.get(Contact.LAST_CONTACT_DATE),
			contact.get(Contact.REPORT_DATE_TIME),
			contact.get(Contact.FOLLOW_UP_UNTIL),
			joins.getPerson().get(Person.SYMPTOM_JOURNAL_STATUS),
			contact.get(Contact.DISEASE),
			jurisdictionSelector(contactQueryContext));

		// Only use user filter if no restricting case is specified
		Predicate filter = listCriteriaBuilder.buildContactFilter(contactCriteria, contactQueryContext);

		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true);

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case FollowUpDto.UUID:
				case ContactFollowUpDto.LAST_CONTACT_DATE:
				case FollowUpDto.FOLLOW_UP_UNTIL:
					expression = contact.get(sortProperty.propertyName);
					break;
				case FollowUpDto.REPORT_DATE:
					expression = contact.get(Contact.REPORT_DATE_TIME);
					break;
				case FollowUpDto.SYMPTOM_JOURNAL_STATUS:
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

		List<ContactFollowUpDto> resultList = QueryHelper.getResultList(em, cq, first, max);

		if (!resultList.isEmpty()) {

			List<String> contactUuids = resultList.stream().map(d -> d.getUuid()).collect(Collectors.toList());

			CriteriaQuery<Object[]> visitsCq = cb.createQuery(Object[].class);
			Root<Contact> visitsCqRoot = visitsCq.from(Contact.class);
			Join<Contact, Visit> visitsJoin = visitsCqRoot.join(Contact.VISITS, JoinType.LEFT);
			Join<Visit, Symptoms> visitSymptomsJoin = visitsJoin.join(Visit.SYMPTOMS, JoinType.LEFT);

			visitsCq.where(
				CriteriaBuilderHelper.and(
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

			for (ContactFollowUpDto contactFollowUpDto : resultMap.values()) {
				contactFollowUpDto.initVisitSize(interval + 1);

				boolean isInJurisdiction = contactFollowUpDto.getInJurisdiction();
				pseudonymizer.pseudonymizeDto(ContactFollowUpDto.class, contactFollowUpDto, isInJurisdiction, null);
			}

			for (Object[] v : visits) {
				int day = DateHelper.getDaysBetween(start, (Date) v[1]);
				VisitResultDto result = getVisitResult((VisitStatus) v[2], (VisitOrigin) v[3], (Boolean) v[4]);
				resultMap.get(v[0]).getVisitResults()[day - 1] = result;
			}
		}

		return resultList;
	}

	private Expression<Object> jurisdictionSelector(ContactQueryContext qc) {
		return JurisdictionHelper.booleanSelector(qc.getCriteriaBuilder(), service.inJurisdictionOrOwned(qc));
	}

	@Override
	public FollowUpPeriodDto getCalculatedFollowUpUntilDate(ContactDto contactDto, boolean ignoreOverwrite) {
		return ContactLogic.calculateFollowUpUntilDate(
			contactDto,
			ContactLogic.getFollowUpStartDate(contactDto, sampleFacade.getByContactUuids(Collections.singletonList(contactDto.getUuid()))),
			visitFacade.getVisitsByContact(contactDto.toReference()),
			diseaseConfigurationFacade.getFollowUpDuration(contactDto.getDisease()),
			ignoreOverwrite,
			featureConfigurationFacade.isPropertyValueTrue(FeatureType.CONTACT_TRACING, FeatureTypeProperty.ALLOW_FREE_FOLLOW_UP_OVERWRITE));
	}

	@Override
	public List<ContactIndexDto> getIndexList(ContactCriteria contactCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaQuery<Tuple> cqIds = listCriteriaBuilder.buildIndexCriteriaPrefetchIds(contactCriteria, sortProperties);
		List<Long> indexListIds =
			QueryHelper.getResultList(em, cqIds, first, max).stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());

		List<ContactIndexDto> dtos = new ArrayList<>();
		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			CriteriaQuery<ContactIndexDto> query = listCriteriaBuilder.buildIndexCriteria(contactCriteria, sortProperties, batchedIds);
			dtos.addAll(QueryHelper.getResultList(em, query, null, null));
		});

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(ContactIndexDto.class, dtos, ContactIndexDto::getInJurisdiction, (c, isInJurisdiction) -> {
			if (c.getCaze() != null) {
				pseudonymizer.pseudonymizeDto(CaseReferenceDto.class, c.getCaze(), c.getCaseInJurisdiction(), null);
			}
		});
		dtos.forEach(contact -> {
			if (diseaseConfigurationFacade.hasFollowUp(contact.getDisease())) {
				int numberOfMissedVisits =
					FollowUpLogic.getNumberOfRequiredVisitsSoFar(contact.getReportDateTime(), contact.getFollowUpUntil()) - contact.getVisitCount();
				if (numberOfMissedVisits < 0) {
					numberOfMissedVisits = 0;
				}
				contact.setMissedVisitsCount(numberOfMissedVisits);
			}
		});

		return dtos;
	}

	@Override
	public List<ContactListEntryDto> getEntriesList(String personUuid, Integer first, Integer max) {

		Long personId = personFacade.getPersonIdByUuid(personUuid);
		List<ContactListEntryDto> entries = service.getEntriesList(personId, first, max);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(ContactListEntryDto.class, entries, ContactListEntryDto::isInJurisdiction, null);

		return entries;
	}

	@Override
	public List<ContactIndexDetailedDto> getIndexDetailedList(
		ContactCriteria contactCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		CriteriaQuery<Tuple> cqIds = listCriteriaBuilder.buildIndexDetailedCriteriaPrefetchIds(contactCriteria, sortProperties);
		List<Long> indexListIds =
			QueryHelper.getResultList(em, cqIds, first, max).stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());

		List<ContactIndexDetailedDto> dtos = new ArrayList<>();
		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			CriteriaQuery<ContactIndexDetailedDto> query =
				listCriteriaBuilder.buildIndexDetailedCriteria(contactCriteria, sortProperties, batchedIds);
			dtos.addAll(QueryHelper.getResultList(em, query, null, null));
		});

		if (userService.hasRight(UserRight.EVENT_VIEW)) {
			// Load event count and latest events info per contact
			Map<String, List<ContactEventSummaryDetails>> eventSummaries =
				eventService.getEventSummaryDetailsByContacts(dtos.stream().map(ContactIndexDetailedDto::getUuid).collect(Collectors.toList()))
					.stream()
					.collect(Collectors.groupingBy(ContactEventSummaryDetails::getContactUuid, Collectors.toList()));
			for (ContactIndexDetailedDto contact : dtos) {

				List<ContactEventSummaryDetails> contactEvents = eventSummaries.getOrDefault(contact.getUuid(), Collections.emptyList());
				contact.setEventCount((long) contactEvents.size());

				contactEvents.stream().max(Comparator.comparing(ContactEventSummaryDetails::getEventDate)).ifPresent(eventSummary -> {
					contact.setLatestEventId(eventSummary.getEventUuid());
					contact.setLatestEventTitle(eventSummary.getEventTitle());
				});
			}
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		User currentUser = userService.getCurrentUser();
		pseudonymizer.pseudonymizeDtoCollection(ContactIndexDetailedDto.class, dtos, c -> c.getInJurisdiction(), (c, isInJurisdiction) -> {
			pseudonymizer.pseudonymizeUser(userService.getByUuid(c.getReportingUser().getUuid()), currentUser, c::setReportingUser);
			if (c.getCaze() != null) {
				pseudonymizer.pseudonymizeDto(CaseReferenceDto.class, c.getCaze(), c.getCaseInJurisdiction(), null);
			}
		});

		dtos.forEach(contact -> {
			if (diseaseConfigurationFacade.hasFollowUp(contact.getDisease())) {
				int numberOfMissedVisits =
					FollowUpLogic.getNumberOfRequiredVisitsSoFar(contact.getReportDateTime(), contact.getFollowUpUntil()) - contact.getVisitCount();
				if (numberOfMissedVisits < 0) {
					numberOfMissedVisits = 0;
				}
				contact.setMissedVisitsCount(numberOfMissedVisits);
			}
		});

		return dtos;
	}

	@Override
	public int[] getContactCountsByCasesForDashboard(List<Long> contactIds) {

		Set<Long> caseIds = new LinkedHashSet<>();
		IterableHelper.executeBatched(
			contactIds,
			ModelConstants.PARAMETER_LIMIT,
			batchedContactIds -> caseIds.addAll(getCaseIdsFromContacts(batchedContactIds)));

		if (caseIds.isEmpty()) {
			return new int[3];
		} else {
			int[] counts = new int[3];

			List<Long> caseContactCounts = new ArrayList<>();
			IterableHelper.executeBatched(
				new ArrayList<>(caseIds),
				ModelConstants.PARAMETER_LIMIT,
				batchedCaseIds -> caseContactCounts.addAll(countContactsAssignToCases(batchedCaseIds)));

			counts[0] = caseContactCounts.stream().min((l1, l2) -> l1.compareTo(l2)).orElse(0L).intValue();
			counts[1] = caseContactCounts.stream().max((l1, l2) -> l1.compareTo(l2)).orElse(0L).intValue();
			counts[2] = caseContactCounts.stream().reduce(0L, (a, b) -> a + b).intValue() / caseIds.size();

			return counts;
		}
	}

	private List<Long> getCaseIdsFromContacts(List<Long> contactIds) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Contact> contact = cq.from(Contact.class);
		Join<Contact, Case> caseJoin = contact.join(Contact.CAZE);

		cq.where(contact.get(Contact.ID).in(contactIds));
		cq.select(caseJoin.get(Case.ID));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	private List<Long> countContactsAssignToCases(List<Long> caseIds) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Contact> contact = cq.from(Contact.class);
		cq.groupBy(contact.get(Contact.CAZE));

		cq.where(contact.get(Contact.CAZE).get(Case.ID).in(caseIds));
		cq.select(cb.count(contact.get(Contact.ID)));

		return em.createQuery(cq).getResultList();
	}

	@SuppressWarnings("JpaQueryApiInspection")
	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_CONTACT_VIEW })
	public int getNonSourceCaseCountForDashboard(List<String> caseUuids) {

		AtomicInteger totalCount = new AtomicInteger();
		IterableHelper.executeBatched(caseUuids, ModelConstants.PARAMETER_LIMIT, batchedCaseUuids -> {
			Query query = em.createNativeQuery(
				String.format(
					"SELECT DISTINCT count(case1_.id) FROM contact AS contact0_ LEFT OUTER JOIN cases AS case1_ ON (contact0_.%s_id = case1_.id) WHERE case1_.%s IN (:uuidList)",
					Contact.RESULTING_CASE.toLowerCase(),
					Case.UUID));
			query.setParameter("uuidList", batchedCaseUuids);
			totalCount.addAndGet(((BigInteger) query.getSingleResult()).intValue());
		});
		return totalCount.get();
	}

	public Contact fillOrBuildEntity(@NotNull ContactDto source, Contact target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		boolean targetWasNull = isNull(target);

		target = DtoHelper.fillOrBuildEntity(source, target, Contact::build, checkChangeDate);

		if (targetWasNull) {
			FacadeHelper.setUuidIfDtoExists(target.getEpiData(), source.getEpiData());
		}

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
		if (source.isMultiDayContact()) {
			target
				.setFirstContactDate(source.getFirstContactDate() != null ? UtilDate.from(UtilDate.toLocalDate(source.getFirstContactDate())) : null);
		} else {
			target.setFirstContactDate(null);
		}

		target.setLastContactDate(source.getLastContactDate() != null ? UtilDate.from(UtilDate.toLocalDate(source.getLastContactDate())) : null);

		target.setContactIdentificationSource(source.getContactIdentificationSource());
		target.setContactIdentificationSourceDetails(source.getContactIdentificationSourceDetails());
		target.setTracingApp(source.getTracingApp());
		target.setTracingAppDetails(source.getTracingAppDetails());
		target.setContactProximity(source.getContactProximity());
		if (source.getContactClassification() != null) {
			target.setContactClassification(source.getContactClassification());
		}
		if (source.getContactStatus() != null) {
			target.setContactStatus(source.getContactStatus());
		}
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
		target.setExternalToken(source.getExternalToken());
		target.setInternalToken(source.getInternalToken());

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

		target.setEpiData(epiDataFacade.fillOrBuildEntity(source.getEpiData(), target.getEpiData(), checkChangeDate));
		target.setHealthConditions(
			healthConditionsMapper.fillOrBuildEntity(source.getHealthConditions(), target.getHealthConditions(), checkChangeDate));
		target.setReturningTraveler(source.getReturningTraveler());
		target.setEndOfQuarantineReason(source.getEndOfQuarantineReason());
		target.setEndOfQuarantineReasonDetails(source.getEndOfQuarantineReasonDetails());

		target.setProhibitionToWork(source.getProhibitionToWork());
		target.setProhibitionToWorkFrom(source.getProhibitionToWorkFrom());
		target.setProhibitionToWorkUntil(source.getProhibitionToWorkUntil());

		target.setReportingDistrict(districtService.getByReferenceDto(source.getReportingDistrict()));
		target.setVaccinationStatus(source.getVaccinationStatus());

		if (source.getSormasToSormasOriginInfo() != null) {
			target.setSormasToSormasOriginInfo(originInfoService.getByUuid(source.getSormasToSormasOriginInfo().getUuid()));
		}
		target.setPreviousQuarantineTo(source.getPreviousQuarantineTo());
		target.setQuarantineChangeComment(source.getQuarantineChangeComment());

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {
		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return service.getArchivedUuidsSince(since);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@RightsAllowed(UserRight._SYSTEM)
	public void archiveAllArchivableContacts(int daysAfterContactsGetsArchived) {
		archiveAllArchivableContacts(daysAfterContactsGetsArchived, LocalDate.now());
	}

	private void archiveAllArchivableContacts(int daysAfterEventGetsArchived, @NotNull LocalDate referenceDate) {
		LocalDate notChangedSince = referenceDate.minusDays(daysAfterEventGetsArchived);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Contact> from = cq.from(Contact.class);

		Timestamp notChangedTimestamp = Timestamp.valueOf(notChangedSince.atStartOfDay());
		cq.where(
			cb.equal(from.get(Contact.ARCHIVED), false),
			cb.equal(from.get(Contact.DELETED), false),
			cb.not(service.createChangeDateFilter(cb, from, notChangedTimestamp)));
		cq.select(from.get(Contact.UUID)).distinct(true);
		List<String> contactUuids = em.createQuery(cq).getResultList();

		if (!contactUuids.isEmpty()) {
			archive(contactUuids);
		}
	}

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_CONTACT_VIEW })
	public List<DashboardContactDto> getContactsForDashboard(
		RegionReferenceDto regionRef,
		DistrictReferenceDto districtRef,
		Disease disease,
		Date from,
		Date to) {

		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);

		return service.getContactsForDashboard(region, district, disease, from, to);
	}

	@Override
	public List<ContactDto> getByPersonUuids(List<String> personUuids) {
		return toDtos(service.getByPersonUuids(personUuids).stream());
	}

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_CONTACT_VIEW })
	public Map<ContactStatus, Long> getNewContactCountPerStatus(ContactCriteria contactCriteria) {

		User user = userService.getCurrentUser();
		return service.getNewContactCountPerStatus(contactCriteria, user);
	}

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_CONTACT_VIEW })
	public Map<ContactClassification, Long> getNewContactCountPerClassification(ContactCriteria contactCriteria) {
		return service.getNewContactCountPerClassification(contactCriteria);
	}

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_CONTACT_VIEW })
	public Map<FollowUpStatus, Long> getNewContactCountPerFollowUpStatus(ContactCriteria contactCriteria) {
		return service.getNewContactCountPerFollowUpStatus(contactCriteria);
	}

	@Override
	public int getFollowUpUntilCount(ContactCriteria contactCriteria) {

		User user = userService.getCurrentUser();
		return service.getFollowUpUntilCount(contactCriteria, user);
	}

	@Override
	protected List<ContactDto> toPseudonymizedDtos(List<Contact> adoList) {

		Map<Long, ContactJurisdictionFlagsDto> jurisdictionsFlags = service.getJurisdictionsFlags(adoList);
		Pseudonymizer pseudonymizer = createPseudonymizer();
		return adoList.stream().map(p -> toPseudonymizedDto(p, pseudonymizer, jurisdictionsFlags.get(p.getId()))).collect(Collectors.toList());
	}

	@Override
	public ContactDto toPseudonymizedDto(Contact source, Pseudonymizer pseudonymizer) {

		if (source == null) {
			return null;
		}

		ContactJurisdictionFlagsDto jurisdictionFlags = service.getJurisdictionFlags(source);
		return toPseudonymizedDto(source, pseudonymizer, jurisdictionFlags);
	}

	@Deprecated
	@Override
	public ContactDto toPseudonymizedDto(Contact source, Pseudonymizer pseudonymizer, boolean inJurisdiction) {

		throw new UnsupportedOperationException("Use variant with jurisdictionFlags parameter");
	}

	protected ContactDto toPseudonymizedDto(Contact source, Pseudonymizer pseudonymizer, ContactJurisdictionFlagsDto jurisdictionFlags) {

		ContactDto dto = toDto(source);
		pseudonymizeDto(source, dto, pseudonymizer, jurisdictionFlags);
		return dto;
	}

	@Deprecated
	@Override
	protected void pseudonymizeDto(Contact source, ContactDto dto, Pseudonymizer pseudonymizer, boolean inJurisdiction) {

		throw new UnsupportedOperationException("Use variant with jurisdictionFlags parameter");
	}

	protected void pseudonymizeDto(Contact source, ContactDto dto, Pseudonymizer pseudonymizer, ContactJurisdictionFlagsDto jurisdictionFlags) {

		boolean inJurisdiction = jurisdictionFlags.getInJurisdiction();
		if (dto != null) {
			User currentUser = userService.getCurrentUser();

			pseudonymizer.pseudonymizeDto(ContactDto.class, dto, inJurisdiction, (c) -> {
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), currentUser, dto::setReportingUser);

				if (c.getCaze() != null) {
					pseudonymizer.pseudonymizeDto(CaseReferenceDto.class, c.getCaze(), jurisdictionFlags.getCaseInJurisdiction(), null);
				}

				pseudonymizer.pseudonymizeDto(
					EpiDataDto.class,
					dto.getEpiData(),
					inJurisdiction,
					e -> pseudonymizer
						.pseudonymizeDtoCollection(ExposureDto.class, e.getExposures(), exp -> inJurisdiction, (exp, expInJurisdiction) -> {
							pseudonymizer.pseudonymizeDto(LocationDto.class, exp.getLocation(), expInJurisdiction, null);
						}));
			});
		}
	}

	@Override
	protected void restorePseudonymizedDto(ContactDto dto, ContactDto existingContactDto, Contact existingContact, Pseudonymizer pseudonymizer) {

		if (existingContactDto != null) {
			boolean isInJurisdiction = service.inJurisdictionOrOwned(existingContact);
			User currentUser = userService.getCurrentUser();

			String followUpComment = null;
			if (dto.isPseudonymized() || !pseudonymizer.isAccessible(ContactDto.class, ContactDto.FOLLOW_UP_COMMENT, isInJurisdiction)) {
				/**
				 * Usually, pseudonymized values are not edited, so the pseudonymizer can just overwrite them in the dto when restoring.
				 * One exception is the followUpComment, which can be pseudonymized, but still be edited by automatic system messages,
				 * e.g. when cancelling follow up.
				 * This attribute's value is extracted here before pseudonymized values are restored and then added again.
				 */
				followUpComment = dto.getFollowUpComment();
			}

			pseudonymizer.restoreUser(existingContact.getReportingUser(), currentUser, dto, dto::setReportingUser);
			pseudonymizer.restorePseudonymizedValues(ContactDto.class, dto, existingContactDto, isInJurisdiction);
			pseudonymizer.restorePseudonymizedValues(EpiDataDto.class, dto.getEpiData(), existingContactDto.getEpiData(), isInJurisdiction);

			if (followUpComment != null) {
				dto.addToFollowUpComment(followUpComment);
			}
		}
	}

	private ContactReferenceDto convertToReferenceDto(Contact source) {

		ContactReferenceDto dto = toReferenceDto(source);
		if (source != null && dto != null) {
			final ContactJurisdictionFlagsDto contactJurisdictionFlagsDto = service.getJurisdictionFlags(source);
			boolean inJurisdiction = contactJurisdictionFlagsDto.getInJurisdiction();
			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);

			pseudonymizer.pseudonymizeDto(ContactReferenceDto.class, dto, inJurisdiction, (c) -> {
				if (source.getCaze() != null) {
					pseudonymizer.pseudonymizeDto(
						ContactReferenceDto.PersonName.class,
						c.getCaseName(),
						contactJurisdictionFlagsDto.getCaseInJurisdiction(),
						null);
				}

				pseudonymizer.pseudonymizeDto(ContactReferenceDto.PersonName.class, c.getContactName(), inJurisdiction, null);
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

	@RightsAllowed({
		UserRight._CONTACT_VIEW,
		UserRight._EXTERNAL_VISITS })
	public ContactDto toDto(Contact source) {
		return toContactDto(source);
	}

	public static ContactDto toContactDto(Contact source) {

		if (source == null) {
			return null;
		}
		ContactDto target = new ContactDto();
		DtoHelper.fillDto(target, source);

		target.setCaze(CaseFacadeEjb.toReferenceDto(source.getCaze()));
		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		if (source.getCaze() != null) {
			target.setDiseaseVariant(source.getCaze().getDiseaseVariant());
		}
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
		target.setExternalToken(source.getExternalToken());
		target.setInternalToken(source.getInternalToken());

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
		target.setHealthConditions(HealthConditionsMapper.toDto(source.getHealthConditions()));
		target.setReturningTraveler(source.getReturningTraveler());
		target.setEndOfQuarantineReason(source.getEndOfQuarantineReason());
		target.setEndOfQuarantineReasonDetails(source.getEndOfQuarantineReasonDetails());

		target.setProhibitionToWork(source.getProhibitionToWork());
		target.setProhibitionToWorkFrom(source.getProhibitionToWorkFrom());
		target.setProhibitionToWorkUntil(source.getProhibitionToWorkUntil());

		target.setReportingDistrict(DistrictFacadeEjb.toReferenceDto(source.getReportingDistrict()));

		target.setSormasToSormasOriginInfo(SormasToSormasOriginInfoFacadeEjb.toDto(source.getSormasToSormasOriginInfo()));
		target.setOwnershipHandedOver(source.getSormasToSormasShares().stream().anyMatch(ShareInfoHelper::isOwnerShipHandedOver));

		target.setVaccinationStatus(source.getVaccinationStatus());
		target.setFollowUpStatusChangeDate(source.getFollowUpStatusChangeDate());
		if (source.getFollowUpStatusChangeUser() != null) {
			target.setFollowUpStatusChangeUser(source.getFollowUpStatusChangeUser().toReference());
		}
		target.setPreviousQuarantineTo(source.getPreviousQuarantineTo());
		target.setQuarantineChangeComment(source.getQuarantineChangeComment());

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	@Override
	protected ContactReferenceDto toRefDto(Contact contact) {
		return convertToReferenceDto(contact);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@RightsAllowed(UserRight._SYSTEM)
	public void generateContactFollowUpTasks() {

		// get all contacts that are followed up
		LocalDateTime fromDateTime = LocalDate.now().atStartOfDay();
		LocalDateTime toDateTime = fromDateTime.plusDays(1);
		List<Contact> contacts = service.getFollowUpBetween(UtilDate.from(fromDateTime), UtilDate.from(toDateTime));

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
			List<Task> pendingUserTasks = taskService.findByAssigneeContactTypeAndStatuses(
				assignee.toReference(),
				contact.toReference(),
				TaskType.CONTACT_FOLLOW_UP,
				TaskStatus.IN_PROGRESS,
				TaskStatus.PENDING);

			if (!pendingUserTasks.isEmpty()) {
				// the user still has a pending task for this contact
				continue;
			}

			TaskCriteria dayTaskCriteria = new TaskCriteria().contact(contact.toReference())
				.taskType(TaskType.CONTACT_FOLLOW_UP)
				.dueDateBetween(UtilDate.from(fromDateTime), UtilDate.from(toDateTime));
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
		task.setSuggestedStart(UtilDate.from(fromDateTime));
		task.setDueDate(UtilDate.from(toDateTime.minusMinutes(1)));
		task.setAssigneeUser(assignee);

		if (contact.isHighPriority()) {
			task.setPriority(TaskPriority.HIGH);
		}
		return task;
	}

	private void validateUserRights(ContactDto contact, ContactDto existingContact) {
		if (existingContact != null) {
			if (!DataHelper.isSame(contact.getCaze(), existingContact.getCaze())
				&& !(userService.hasRight(UserRight.CONTACT_REASSIGN_CASE) || userService.hasRight(UserRight.EXTERNAL_VISITS))) {
				throw new AccessDeniedException(
					String.format(
						I18nProperties.getString(Strings.errorNoRightsForChangingField),
						I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.CAZE)));

			}
		}
	}

	@Override
	public void validate(@Valid ContactDto contact) throws ValidationRuntimeException {

		// Check whether any required field that does not have a not null constraint in the database is empty
		if (contact.getReportingUser() == null && !contact.isPseudonymized()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validReportingUser));
		}

		if (contact.getReportDateTime() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validReportDateTime));
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
		cq.distinct(true);

		ContactQueryContext contactQueryContext = new ContactQueryContext(cb, cq, contactRoot);
		ContactJoins joins = contactQueryContext.getJoins();

		List<Selection<?>> selections = new ArrayList<>(
			Arrays.asList(
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
				contactRoot.get(Contact.FOLLOW_UP_STATUS)));

		selections.addAll(service.getJurisdictionSelections(contactQueryContext));
		cq.multiselect(selections);

		cq.where(getSimilarityFilters(criteria, cb, contactRoot, contactQueryContext));

		List<SimilarContactDto> contacts = em.createQuery(cq).getResultList();

		Pseudonymizer pseudonymizer = createPseudonymizer();
		pseudonymizer.pseudonymizeDtoCollection(SimilarContactDto.class, contacts, SimilarContactDto::getInJurisdiction, (c, isInJurisdiction) -> {
			CaseReferenceDto contactCase = c.getCaze();
			if (contactCase != null) {
				pseudonymizer.pseudonymizeDto(CaseReferenceDto.class, contactCase, c.getCaseInJurisdiction(), null);
			}
		});

		if (Boolean.TRUE.equals(criteria.getExcludePseudonymized())) {
			contacts = contacts.stream().filter(c -> !c.isPseudonymized()).collect(Collectors.toList());
		}

		return contacts;
	}

	public boolean hasSimilarContacts(ContactSimilarityCriteria criteria) {
		return service.exists((cb, root, cq) -> getSimilarityFilters(criteria, cb, root, new ContactQueryContext(cb, cq, root)));
	}

	private Predicate getSimilarityFilters(
		ContactSimilarityCriteria criteria,
		CriteriaBuilder cb,
		Root<Contact> contactRoot,
		ContactQueryContext contactQueryContext) {
		ContactJoins joins = contactQueryContext.getJoins();

		final Predicate defaultFilter = service.createDefaultFilter(cb, contactRoot);
		final Predicate userFilter = service.createUserFilter(contactQueryContext);

		final PersonReferenceDto person = criteria.getPerson();
		Predicate samePersonFilter = person != null ? cb.equal(joins.getPerson().get(Person.UUID), person.getUuid()) : null;
		if (criteria.getPersons() != null) {
			samePersonFilter =
				joins.getPerson().get(Person.UUID).in(criteria.getPersons().stream().map(PersonReferenceDto::getUuid).collect(Collectors.toList()));
		}

		final Disease disease = criteria.getDisease();
		final Predicate diseaseFilter = disease != null ? cb.equal(contactRoot.get(Contact.DISEASE), disease) : null;

		final CaseReferenceDto caze = criteria.getCaze();
		Predicate cazeFilter = caze != null ? cb.equal(joins.getCaze().get(Case.UUID), caze.getUuid()) : null;

		if (criteria.getResultingCases() != null) {
			cazeFilter = joins.getResultingCase()
				.get(Case.UUID)
				.in(criteria.getResultingCases().stream().map(CaseReferenceDto::getUuid).collect(Collectors.toList()));
		}

		final ContactClassification contactClassification = criteria.getContactClassification();
		final Predicate contactClassificationFilter =
			contactClassification != null ? cb.equal(contactRoot.get(Contact.CONTACT_CLASSIFICATION), contactClassification) : null;

		final Predicate noResulingCaseFilter =
			Boolean.TRUE.equals(criteria.getNoResultingCase()) ? cb.isNull(contactRoot.get(Contact.RESULTING_CASE)) : null;

		final Date reportDate = criteria.getReportDate();
		final Date lastContactDate = criteria.getLastContactDate();
		final Predicate recentContactsFilter = CriteriaBuilderHelper.and(
			cb,
			service.recentDateFilter(cb, reportDate, contactRoot.get(Contact.REPORT_DATE_TIME), 30),
			service.recentDateFilter(cb, lastContactDate, contactRoot.get(Contact.LAST_CONTACT_DATE), 30));

		final Date relevantDate = criteria.getRelevantDate();
		final Predicate relevantDateFilter = CriteriaBuilderHelper.or(
			cb,
			service.recentDateFilter(cb, relevantDate, contactRoot.get(Contact.REPORT_DATE_TIME), 30),
			service.recentDateFilter(cb, relevantDate, contactRoot.get(Contact.LAST_CONTACT_DATE), 30));

		return CriteriaBuilderHelper.and(
			cb,
			defaultFilter,
			userFilter,
			samePersonFilter,
			diseaseFilter,
			cazeFilter,
			contactClassificationFilter,
			noResulingCaseFilter,
			recentContactsFilter,
			relevantDateFilter);
	}

	@Override
	public boolean doesExternalTokenExist(String externalToken, String contactUuid) {
		return service.exists(
			(cb, contactRoot, cq) -> CriteriaBuilderHelper.and(
				cb,
				cb.equal(contactRoot.get(Contact.EXTERNAL_TOKEN), externalToken),
				cb.notEqual(contactRoot.get(Contact.UUID), contactUuid),
				cb.notEqual(contactRoot.get(Contact.DELETED), Boolean.TRUE)));
	}

	@Override
	@RightsAllowed(UserRight._CONTACT_MERGE)
	public void merge(String leadUuid, String otherUuid) {
		ContactDto leadContactDto = getContactWithoutPseudonyimizationByUuid(leadUuid);
		ContactDto otherContactDto = getContactWithoutPseudonyimizationByUuid(otherUuid);

		// 1 Merge Dtos
		// 1.1 Contact
		copyDtoValues(leadContactDto, otherContactDto);
		save(leadContactDto);

		// 1.2 Person - Only merge when the persons have different UUIDs
		if (!DataHelper.equal(leadContactDto.getPerson().getUuid(), otherContactDto.getPerson().getUuid())) {
			PersonDto leadPerson = personFacade.getByUuid(leadContactDto.getPerson().getUuid());
			PersonDto otherPerson = personFacade.getByUuid(otherContactDto.getPerson().getUuid());
			personFacade.mergePerson(leadPerson, otherPerson);
		}

		// 2 Change ContactReference
		Contact leadContact = service.getByUuid(leadContactDto.getUuid());
		Contact otherContact = service.getByUuid(otherContactDto.getUuid());

		// 2.1 Tasks
		List<Task> tasks = taskService.findBy(new TaskCriteria().contact(otherContactDto.toReference()), true);
		for (Task task : tasks) {
			// simply move existing entities to the merge target
			task.setContact(leadContact);
			taskService.ensurePersisted(task);
		}

		// 2.2 Samples
		List<Sample> samples = sampleService.findBy(new SampleCriteria().contact(otherContactDto.toReference()), null);
		for (Sample sample : samples) {
			// simply move existing entities to the merge target
			sample.setAssociatedContact(leadContact);
			sampleService.ensurePersisted(sample);
		}

		// 3 Attach otherContact visits to leadContact
		// (set the person and the disease of the visit, saveVisit does the rest)
		for (VisitDto otherVisit : otherContact.getVisits().stream().map(VisitFacadeEjb::toVisitDto).collect(Collectors.toList())) {
			otherVisit.setPerson(leadContactDto.getPerson());
			otherVisit.setDisease(leadContactDto.getDisease());
			visitFacade.save(otherVisit);
		}

		// 4 Documents
		List<Document> documents = documentService.getRelatedToEntity(DocumentRelatedEntityType.CONTACT, otherContact.getUuid());
		for (Document document : documents) {
			document.setRelatedEntityUuid(leadContact.getUuid());

			documentService.ensurePersisted(document);
		}
	}

	private void copyDtoValues(ContactDto leadContactDto, ContactDto otherContactDto) {

		DtoCopyHelper.copyDtoValues(leadContactDto, otherContactDto, false);

		final String leadAdditionalDetails = leadContactDto.getAdditionalDetails();
		final String leadFollowUpComment = leadContactDto.getFollowUpComment();
		final String otherAdditionalDetails = otherContactDto.getAdditionalDetails();
		final String otherFollowUpComment = otherContactDto.getFollowUpComment();
		leadContactDto.setAdditionalDetails(
			leadAdditionalDetails != null && leadAdditionalDetails.equals(otherAdditionalDetails)
				? leadAdditionalDetails
				: DataHelper.joinStrings(" ", leadAdditionalDetails, otherAdditionalDetails));
		leadContactDto.setFollowUpComment(
			leadFollowUpComment != null && leadFollowUpComment.equals(otherFollowUpComment)
				? leadFollowUpComment
				: DataHelper.joinStrings(" ", leadFollowUpComment, otherFollowUpComment));
	}

	@Override
	@RightsAllowed(UserRight._CONTACT_MERGE)
	public void deleteAsDuplicate(String uuid, String duplicateOfUuid) {
		Contact contact = service.getByUuid(uuid);
		Contact duplicateOfContact = service.getByUuid(duplicateOfUuid);
		contact.setDuplicateOf(duplicateOfContact);
		service.ensurePersisted(contact);

		this.delete(uuid, new DeletionDetails(DeletionReason.DUPLICATE_ENTRIES, null));
	}

	@Override
	public List<MergeContactIndexDto[]> getContactsForDuplicateMerging(ContactCriteria criteria, @Min(1) Integer limit, boolean ignoreRegion) {
		List<MergeContactIndexDto[]> contacts = service.getContactsForDuplicateMerging(criteria, limit, ignoreRegion);

		for (MergeContactIndexDto[] contact : contacts) {
			pseudonymizeContactPairs(contact);
		}

		return contacts;
	}

	public void pseudonymizeContactPairs(MergeContactIndexDto[] contactPair) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));

		Arrays.stream(contactPair).forEach(contact -> {
			Boolean isInJurisdiction = contact.getInJurisdiction();
			pseudonymizer.pseudonymizeDto(MergeContactIndexDto.class, contact, isInJurisdiction, c -> {
				pseudonymizer.pseudonymizeDto(AgeAndBirthDateDto.class, contact.getAgeAndBirthDate(), isInJurisdiction, null);
				if (contact.getCaze() != null) {
					pseudonymizer.pseudonymizeDto(CaseReferenceDto.class, contact.getCaze(), contact.getCaseInJurisdiction(), null);
				}
			});
		});
	}

	@Override
	@RightsAllowed(UserRight._CONTACT_EDIT)
	public void updateCompleteness(String uuid) {
		Contact contact = service.getByUuid(uuid);
		contact.setCompleteness(calculateCompleteness(contact));
		service.ensurePersisted(contact);
	}

	@Override
	@RightsAllowed(UserRight._CONTACT_EDIT)
	public void updateExternalData(@Valid List<ExternalDataDto> externalData) throws ExternalDataUpdateException {
		service.updateExternalData(externalData);
	}

	@Override
	@RightsAllowed(UserRight._CONTACT_EDIT)
	public Integer saveBulkContacts(
		List<String> contactUuidList,
		ContactBulkEditData updatedContactBulkEditData,
		boolean classificationChange,
		boolean contactOfficerChange)
		throws ValidationRuntimeException {

		int changedContacts = 0;
		for (String contactUuid : contactUuidList) {
			Contact contact = service.getByUuid(contactUuid);

			if (service.isEditAllowed(contact)) {
				ContactDto existingContactDto = toDto(contact);
				if (classificationChange) {
					existingContactDto.setContactClassification(updatedContactBulkEditData.getContactClassification());
				}
				// Setting the contact officer is only allowed if all selected contacts are in the same district
				if (contactOfficerChange) {
					existingContactDto.setContactOfficer(updatedContactBulkEditData.getContactOfficer());
				}

				save(existingContactDto);
				changedContacts++;
			}
		}
		return changedContacts;
	}

	@Override
	public long getContactCount(CaseReferenceDto caze) {
		return service.getContactCount(caze);
	}

	@Override
	protected DeletableEntityType getCoreEntityType() {
		return DeletableEntityType.CONTACT;
	}

	private float calculateCompleteness(Contact contact) {

		float completeness = 0f;

		if (contact.getContactClassification() != null && !ContactClassification.UNCONFIRMED.equals(contact.getContactClassification())) {
			completeness += 0.1f;
		}
		if (contact.getLastContactDate() != null) {
			completeness += 0.2f;
		}
		if (contact.getRelationToCase() != null) {
			completeness += 0.15f;
		}
		if (contact.getContactCategory() != null) {
			completeness += 0.1f;
		}
		if (contact.getContactProximity() != null) {
			completeness += 0.1f;
		}
		if (contact.getContactStatus() != null && !ContactStatus.ACTIVE.equals(contact.getContactStatus())) {
			completeness += 0.1f;
		}
		if (sampleService
			.exists((cb, root, cq) -> cb.and(sampleService.createDefaultFilter(cb, root), cb.equal(root.get(Sample.ASSOCIATED_CONTACT), contact)))) {
			completeness += 0.15f;
		}
		if (contact.getPerson().getBirthdateYYYY() != null || contact.getPerson().getApproximateAge() != null) {
			completeness += 0.05f;
		}
		if (contact.getPerson().getSex() != null) {
			completeness += 0.05f;
		}

		return completeness;
	}

	private User getRandomDistrictContactResponsible(District district) {

		return userService.getRandomDistrictUser(district, UserRight.CONTACT_RESPONSIBLE);
	}

	private String getNumberOfDosesFromVaccinations(Vaccination vaccination) {
		return vaccination != null ? vaccination.getVaccineDose() : "";
	}

	public User getRandomRegionContactResponsible(Region region) {

		return userService.getRandomRegionUser(region, UserRight.CONTACT_RESPONSIBLE);
	}

	@LocalBean
	@Stateless
	public static class ContactFacadeEjbLocal extends ContactFacadeEjb {

		public ContactFacadeEjbLocal() {
		}

		@Inject
		public ContactFacadeEjbLocal(ContactService service) {
			super(service);
		}
	}

}
