/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas;

import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.CASE_ENDPOINT;
import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.CONTACT_ENDPOINT;
import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.RESOURCE_PATH;
import static de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasErrorResponse;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasFacade;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.fieldaccess.checkers.PersonalDataFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.checkers.SensitiveDataFieldAccessChecker;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.StartupShutdownService;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.sample.AdditionalTestFacadeEjb;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless(name = "SormasToSormasFacade")
public class SormasToSormasFacadeEjb implements SormasToSormasFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(SormasToSormasFacadeEjb.class);

	public static final String SAVE_SHARED_CASE_ENDPOINT = RESOURCE_PATH + CASE_ENDPOINT;

	private static final String SAVE_SHARED_CONTACT_ENDPOINT = RESOURCE_PATH + CONTACT_ENDPOINT;

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	@EJB
	private SormasToSormasShareInfoService sormasToSormasShareInfoService;
	@EJB
	private SormasToSormasOriginInfoService sormasToSormasOriginInfoService;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private ContactService contactService;
	@EJB
	private UserService userService;
	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private PointOfEntryFacadeEjbLocal pointOfEntryFacade;
	@Inject
	private SormasToSormasRestClient sormasToSormasRestClient;
	@EJB
	private ServerAccessDataService serverAccessDataService;
	@EJB
	protected SormasToSormasEncryptionService encryptionService;
	@EJB
	private SampleService sampleService;
	@EJB
	private SampleFacadeEjb.SampleFacadeEjbLocal sampleFacade;
	@EJB
	private PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private AdditionalTestFacadeEjb.AdditionalTestFacadeEjbLocal additionalTestFacade;

	private final ObjectMapper objectMapper;

	public SormasToSormasFacadeEjb() {
		objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	}

	@Override
	@Transactional
	public void saveSharedCases(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {
		SormasToSormasCaseDto[] sharedCases = decryptSharedData(encryptedData, SormasToSormasCaseDto[].class);

		Map<String, Map<String, List<String>>> validationErrors = new HashMap<>();
		List<ProcessedCaseData> casesToSave = new ArrayList<>(sharedCases.length);

		for (SormasToSormasCaseDto sharedCase : sharedCases) {
			PersonDto person = sharedCase.getPerson();
			CaseDataDto caze = sharedCase.getCaze();
			List<SormasToSormasCaseDto.AssociatedContactDto> associatedContacts = sharedCase.getAssociatedContacts();
			List<SormasToSormasSampleDto> samples = sharedCase.getSamples();

			SormasToSormasOriginInfoDto originInfo = sharedCase.getOriginInfo();

			ValidationErrors caseValidationErrors = new ValidationErrors();

			ValidationErrors caseErrors = validateCase(caze);
			if (caseErrors.hasError()) {
				validationErrors.put(buildCaseValidationGroupName(caze), caseErrors.getErrors());

				continue;
			}

			ValidationErrors originInfoErrorsErrors = processOriginInfo(originInfo, Captions.CaseData);
			caseValidationErrors.addAll(originInfoErrorsErrors);

			ValidationErrors caseDataErrors = processCaseData(caze, person, originInfo);
			caseValidationErrors.addAll(caseDataErrors);

			if (caseValidationErrors.hasError()) {
				validationErrors.put(buildCaseValidationGroupName(caze), caseValidationErrors.getErrors());
			}

			if (associatedContacts != null) {
				Map<String, Map<String, List<String>>> contactValidationErrors = processAssociatedContacts(associatedContacts, originInfo);
				validationErrors.putAll(contactValidationErrors);
			}

			if (samples != null) {
				Map<String, Map<String, List<String>>> sampleErrors = processSamples(samples);
				validationErrors.putAll(sampleErrors);
			}

			casesToSave.add(ProcessedCaseData.create(person, caze, associatedContacts, samples));
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (ProcessedCaseData caseData : casesToSave) {
			handleValidationError(() -> personFacade.savePerson(caseData.person), Captions.Person, buildCaseValidationGroupName(caseData.caze));
			CaseDataDto savedCase =
				handleValidationError(() -> caseFacade.saveCase(caseData.caze), Captions.CaseData, buildCaseValidationGroupName(caseData.caze));

			if (caseData.associatedContacts != null) {
				for (SormasToSormasCaseDto.AssociatedContactDto associatedContact : caseData.associatedContacts) {
					ContactDto contact = associatedContact.getContact();

					handleValidationError(
						() -> personFacade.savePerson(associatedContact.getPerson()),
						Captions.Person,
						buildContactValidationGroupName(contact));

					// set the persisted origin info to avoid outdated entity issue
					contact.setSormasToSormasOriginInfo(savedCase.getSormasToSormasOriginInfo());

					handleValidationError(() -> contactFacade.saveContact(contact), Captions.Contact, buildContactValidationGroupName(contact));
				}
			}

			if (caseData.samples != null) {
				saveSamples(caseData.samples, savedCase.getSormasToSormasOriginInfo());
			}
		}
	}

	@Override
	@Transactional
	public void saveSharedContacts(SormasToSormasEncryptedDataDto sharedData) throws SormasToSormasException, SormasToSormasValidationException {
		SormasToSormasContactDto[] sharedContacts = decryptSharedData(sharedData, SormasToSormasContactDto[].class);

		Map<String, Map<String, List<String>>> validationErrors = new HashMap<>();
		List<ProcessedContactData> contactsToSave = new ArrayList<>(sharedContacts.length);

		for (SormasToSormasContactDto sharedContact : sharedContacts) {
			PersonDto person = sharedContact.getPerson();
			ContactDto contact = sharedContact.getContact();
			List<SormasToSormasSampleDto> samples = sharedContact.getSamples();
			SormasToSormasOriginInfoDto originInfo = sharedContact.getOriginInfo();

			ValidationErrors contactErrors = validateContact(contact);
			if (contactErrors.hasError()) {
				validationErrors.put(buildContactValidationGroupName(contact), contactErrors.getErrors());

				continue;
			}

			ValidationErrors originInfoErrors = processOriginInfo(originInfo, Captions.Contact);
			contactErrors.addAll(originInfoErrors);

			ValidationErrors contactDataErrors = processContactData(contact, person, originInfo);
			contactErrors.addAll(contactDataErrors);

			if (contactErrors.hasError()) {
				validationErrors.put(buildContactValidationGroupName(contact), contactErrors.getErrors());
			}

			if (samples != null) {
				Map<String, Map<String, List<String>>> sampleErrors = processSamples(samples);
				validationErrors.putAll(sampleErrors);
			}

			contactsToSave.add(ProcessedContactData.create(person, contact, samples));
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (ProcessedContactData contactData : contactsToSave) {
			handleValidationError(
				() -> personFacade.savePerson(contactData.person),
				Captions.Person,
				buildContactValidationGroupName(contactData.contact));
			ContactDto savedContact = handleValidationError(
				() -> contactFacade.saveContact(contactData.contact),
				Captions.Contact,
				buildContactValidationGroupName(contactData.contact));

			if (contactData.samples != null) {
				saveSamples(contactData.samples, savedContact.getSormasToSormasOriginInfo());
			}
		}
	}

	@Override
	public void shareCases(List<String> caseUuids, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();

		List<Case> casesToSend = caseService.getByUuids(caseUuids);
		validateCasesBeforeSend(casesToSend);

		List<Contact> contactsToSend = new ArrayList<>();
		List<Sample> samplesToSend = new ArrayList<>();
		List<SormasToSormasCaseDto> entitiesToSend = new ArrayList<>();

		for (Case caze : casesToSend) {
			Pseudonymizer pseudonymizer = createPseudonymizer(options);

			PersonDto personDto = getPersonDto(caze.getPerson(), pseudonymizer, options);
			CaseDataDto cazeDto = getCazeDto(caze, pseudonymizer);

			SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfo(currentUser, options);

			SormasToSormasCaseDto entity = new SormasToSormasCaseDto(personDto, cazeDto, originInfo);
			List<Contact> associatedContacts = Collections.emptyList();
			if (options.isWithAssociatedContacts()) {
				associatedContacts = contactService.findBy(new ContactCriteria().caze(caze.toReference()), userService.getCurrentUser());
				entity.setAssociatedContacts(getAssociatedContactDtos(associatedContacts, pseudonymizer, options));
			}

			final List<Sample> samples = new ArrayList<>();
			if (options.isWithSamples()) {
				final List<Sample> caseSamples = sampleService.findBy(new SampleCriteria().caze(caze.toReference()), currentUser);
				samples.addAll(caseSamples);

				associatedContacts.forEach(associatedContact -> {
					List<Sample> contactSamples = sampleService.findBy(new SampleCriteria().contact(associatedContact.toReference()), currentUser)
						.stream()
						.filter(contactSample -> caseSamples.stream().noneMatch(caseSample -> DataHelper.isSame(caseSample, contactSample)))
						.collect(Collectors.toList());

					samples.addAll(contactSamples);
				});
			}

			entity.setSamples(getSampleDtos(samples, pseudonymizer));

			entitiesToSend.add(entity);
			contactsToSend.addAll(associatedContacts);
			samplesToSend.addAll(samples);
		}

		sendEntityToSormas(entitiesToSend, SAVE_SHARED_CASE_ENDPOINT, options);

		casesToSend.forEach(caze -> saveNewShareInfo(currentUser.toReference(), options, i -> i.setCaze(caze)));
		contactsToSend.forEach((contact) -> saveNewShareInfo(currentUser.toReference(), options, i -> i.setContact(contact)));
		samplesToSend.forEach((sample) -> saveNewShareInfo(currentUser.toReference(), options, i -> i.setSample(sample)));
	}

	@Override
	public void shareContacts(List<String> contactUuids, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		List<Contact> contactsToSend = contactService.getByUuids(contactUuids);
		List<Sample> samplesToSend = new ArrayList<>();

		validateContactsBeforeSend(contactsToSend);

		List<SormasToSormasContactDto> entitiesToSend = new ArrayList<>();

		for (Contact contact : contactsToSend) {
			Pseudonymizer pseudonymizer = createPseudonymizer(options);

			PersonDto personDto = getPersonDto(contact.getPerson(), pseudonymizer, options);
			ContactDto contactDto = getContactDto(contact, pseudonymizer);

			SormasToSormasContactDto entity =
				new SormasToSormasContactDto(personDto, contactDto, createSormasToSormasOriginInfo(currentUser, options));

			List<Sample> samples = Collections.emptyList();
			if (options.isWithSamples()) {
				samples = sampleService.findBy(new SampleCriteria().contact(contact.toReference()), currentUser);

				entity.setSamples(getSampleDtos(samples, pseudonymizer));
			}

			entitiesToSend.add(entity);
			samplesToSend.addAll(samples);
		}

		sendEntityToSormas(entitiesToSend, SAVE_SHARED_CONTACT_ENDPOINT, options);

		contactsToSend.forEach(contact -> saveNewShareInfo(currentUser.toReference(), options, i -> i.setContact(contact)));
		samplesToSend.forEach(sample -> saveNewShareInfo(currentUser.toReference(), options, i -> i.setSample(sample)));
	}

	@Override
	public List<ServerAccessDataReferenceDto> getAvailableOrganizations() {
		return serverAccessDataService.getOrganizationList().stream().map(OrganizationServerAccessData::toReference).collect(Collectors.toList());
	}

	@Override
	public List<SormasToSormasShareInfoDto> getShareInfoIndexList(SormasToSormasShareInfoCriteria criteria, Integer first, Integer max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SormasToSormasShareInfo> cq = cb.createQuery(SormasToSormasShareInfo.class);
		Root<SormasToSormasShareInfo> root = cq.from(SormasToSormasShareInfo.class);

		Predicate filter = sormasToSormasShareInfoService.buildCriteriaFilter(criteria, cb, root);
		if (filter != null) {
			cq.where(filter);
		}

		List<SormasToSormasShareInfo> resultList;
		if (first != null && max != null) {
			resultList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			resultList = em.createQuery(cq).getResultList();
		}

		return resultList.stream().map(this::toSormasToSormasShareInfoDto).collect(Collectors.toList());
	}

	@Override
	public boolean isFeatureEnabled() {
		return userService.hasRight(UserRight.SORMAS_TO_SORMAS_SHARE) && !serverAccessDataService.getOrganizationList().isEmpty();
	}

	@Override
	public ServerAccessDataReferenceDto getOrganizationRef(String id) {
		return getOrganizationServerAccessData(id).map(OrganizationServerAccessData::toReference).orElseGet(null);
	}

	private void validateCasesBeforeSend(List<Case> cases) throws SormasToSormasException {
		Map<String, Map<String, List<String>>> validationErrors = new HashMap<>();
		for (Case caze : cases) {
			if (!caseService.isCaseEditAllowed(caze)) {
				Map<String, List<String>> error = new HashMap<>(1);
				error.put(
					I18nProperties.getCaption(Captions.CaseData),
					Collections.singletonList(I18nProperties.getString(Strings.errorSormasToSormasNotEditable)));

				validationErrors.put(buildCaseValidationGroupName(caze), error);
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasShare), validationErrors);
		}
	}

	private void validateContactsBeforeSend(List<Contact> contacts) throws SormasToSormasException {
		Map<String, Map<String, List<String>>> validationErrors = new HashMap<>();
		for (Contact contact : contacts) {
			if (!contactService.isContactEditAllowed(contact)) {
				Map<String, List<String>> error = new HashMap<>(1);
				error.put(
					I18nProperties.getCaption(Captions.Contact),
					Collections.singletonList(I18nProperties.getString(Strings.errorSormasToSormasNotEditable)));

				validationErrors.put(buildCaseValidationGroupName(contact), error);
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasShare), validationErrors);
		}
	}

	private PersonDto getPersonDto(Person person, Pseudonymizer pseudonymizer, SormasToSormasOptionsDto options) {
		PersonDto personDto = personFacade.convertToDto(person, pseudonymizer, true);

		if (options.isPseudonymizePersonalData() || options.isPseudonymizeSensitiveData()) {
			personDto.setFirstName(I18nProperties.getCaption(Captions.inaccessibleValue));
			personDto.setLastName(I18nProperties.getCaption(Captions.inaccessibleValue));
		}

		return personDto;
	}

	private CaseDataDto getCazeDto(Case caze, Pseudonymizer pseudonymizer) {
		CaseDataDto cazeDto = caseFacade.convertToDto(caze, pseudonymizer);

		cazeDto.setReportingUser(null);
		cazeDto.setClassificationUser(null);
		cazeDto.setSurveillanceOfficer(null);
		cazeDto.setCaseOfficer(null);

		return cazeDto;
	}

	private ContactDto getContactDto(Contact contact, Pseudonymizer pseudonymizer) {
		ContactDto contactDto = contactFacade.convertToDto(contact, pseudonymizer);

		contactDto.setReportingUser(null);
		contactDto.setContactOfficer(null);
		contactDto.setResultingCaseUser(null);

		return contactDto;
	}

	private List<SormasToSormasCaseDto.AssociatedContactDto> getAssociatedContactDtos(
		List<Contact> associatedContacts,
		Pseudonymizer pseudonymizer,
		SormasToSormasOptionsDto options) {
		return associatedContacts.stream().map(contact -> {
			PersonDto personDto = getPersonDto(contact.getPerson(), pseudonymizer, options);
			ContactDto contactDto = getContactDto(contact, pseudonymizer);

			return new SormasToSormasCaseDto.AssociatedContactDto(personDto, contactDto);
		}).collect(Collectors.toList());
	}

	public List<SormasToSormasSampleDto> getSampleDtos(List<Sample> samples, Pseudonymizer pseudonymizer) {
		return samples.stream()
			.map(
				s -> new SormasToSormasSampleDto(
					sampleFacade.convertToDto(s, pseudonymizer),
					s.getPathogenTests().stream().map(t -> pathogenTestFacade.convertToDto(t, pseudonymizer)).collect(Collectors.toList()),
					s.getAdditionalTests().stream().map(t -> additionalTestFacade.convertToDto(t, pseudonymizer)).collect(Collectors.toList())))
			.collect(Collectors.toList());
	}

	private ValidationErrors processCaseData(CaseDataDto caze, PersonDto person, SormasToSormasOriginInfoDto originInfo) {
		ValidationErrors caseValidationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = processPerson(person);
		caseValidationErrors.addAll(personValidationErrors);

		caze.setPerson(person.toReference());
		caze.setReportingUser(userService.getCurrentUser().toReference());

		DataHelper.Pair<InfrastructureData, List<String>> infrastructureAndErrors = loadLocalInfrastructure(
			caze.getRegion(),
			caze.getDistrict(),
			caze.getCommunity(),
			caze.getFacilityType(),
			caze.getHealthFacility(),
			caze.getPointOfEntry());

		handleInfraStructure(infrastructureAndErrors, Captions.CaseData, caseValidationErrors, infrastructureData -> {
			caze.setRegion(infrastructureData.region);
			caze.setDistrict(infrastructureData.district);
			caze.setCommunity(infrastructureData.community);
			caze.setHealthFacility(infrastructureData.facility);
			caze.setPointOfEntry(infrastructureData.pointOfEntry);
		});

		ValidationErrors embeddedObjectErrors = processEmbeddedObjects(caze);
		caseValidationErrors.addAll(embeddedObjectErrors);

		caze.setSormasToSormasOriginInfo(originInfo);

		return caseValidationErrors;
	}

	private Map<String, Map<String, List<String>>> processAssociatedContacts(
		List<SormasToSormasCaseDto.AssociatedContactDto> associatedContacts,
		SormasToSormasOriginInfoDto originInfo) {
		Map<String, Map<String, List<String>>> validationErrors = new HashMap<>();

		associatedContacts.forEach(associatedContact -> {
			ContactDto contact = associatedContact.getContact();

			ValidationErrors contactErrors = processContactData(contact, associatedContact.getPerson(), originInfo);

			if (contactErrors.hasError()) {
				validationErrors.put(buildContactValidationGroupName(contact), contactErrors.getErrors());
			}
		});

		return validationErrors;
	}

	private Map<String, Map<String, List<String>>> processSamples(List<SormasToSormasSampleDto> samples) {
		Map<String, Map<String, List<String>>> validationErrors = new HashMap<>();

		samples.forEach(sormasToSormasSample -> {
			SampleDto sample = sormasToSormasSample.getSample();

			sample.setReportingUser(userService.getCurrentUser().toReference());

			DataHelper.Pair<InfrastructureData, List<String>> infrastructureAndErrors =
				loadLocalInfrastructure(null, null, null, null, sample.getLab(), null);

			ValidationErrors sampleErrors = new ValidationErrors();
			handleInfraStructure(infrastructureAndErrors, Captions.Sample_lab, sampleErrors, (infrastructureData -> {
				sample.setLab(infrastructureData.facility);
			}));
			if (sampleErrors.hasError()) {
				validationErrors.put(buildSampleValidationGroupName(sample), sampleErrors.getErrors());
			}

			sormasToSormasSample.getPathogenTests().forEach(pathogenTest -> {
				pathogenTest.setUuid(DataHelper.createUuid());
				DataHelper.Pair<InfrastructureData, List<String>> ptInfrastructureAndErrors =
					loadLocalInfrastructure(null, null, null, FacilityType.LABORATORY, pathogenTest.getLab(), null);

				ValidationErrors pathogenTestErrors = new ValidationErrors();
				handleInfraStructure(ptInfrastructureAndErrors, Captions.PathogenTest_lab, pathogenTestErrors, (infrastructureData -> {
					pathogenTest.setLab(infrastructureData.facility);
				}));

				if (pathogenTestErrors.hasError()) {
					validationErrors.put(buildPathogenTestValidationGroupName(pathogenTest), pathogenTestErrors.getErrors());
				}
			});

			sormasToSormasSample.getAdditionalTests().forEach(additionalTest -> {
				additionalTest.setUuid(DataHelper.createUuid());
			});

		});

		return validationErrors;
	}

	private ValidationErrors processEmbeddedObjects(CaseDataDto caze) {
		ValidationErrors validationErrors = new ValidationErrors();

		if (caze.getHospitalization() != null) {
			caze.getHospitalization().setUuid(DataHelper.createUuid());
			caze.getHospitalization().getPreviousHospitalizations().forEach(ph -> {
				ph.setUuid(DataHelper.createUuid());

				DataHelper.Pair<InfrastructureData, List<String>> phInfrastructureAndErrors =
					loadLocalInfrastructure(ph.getRegion(), ph.getDistrict(), ph.getCommunity(), FacilityType.HOSPITAL, ph.getHealthFacility(), null);

				handleInfraStructure(
					phInfrastructureAndErrors,
					Captions.CaseHospitalization_previousHospitalizations,
					validationErrors,
					(phInfrastructure) -> {
						ph.setRegion(phInfrastructure.region);
						ph.setDistrict(phInfrastructure.district);
						ph.setCommunity(phInfrastructure.community);
						ph.setHealthFacility(phInfrastructure.facility);
					});
			});
		}

		if (caze.getSymptoms() != null) {
			caze.getSymptoms().setUuid(DataHelper.createUuid());
		}

		if (caze.getEpiData() != null) {
			processEpiData(caze.getEpiData());
		}

		if (caze.getTherapy() != null) {
			caze.getTherapy().setUuid(DataHelper.createUuid());
		}

		if (caze.getClinicalCourse() != null) {
			caze.getClinicalCourse().setUuid(DataHelper.createUuid());

			if (caze.getClinicalCourse().getHealthConditions() != null) {
				processHealthConditions(caze.getClinicalCourse().getHealthConditions());
			}
		}

		MaternalHistoryDto maternalHistory = caze.getMaternalHistory();
		if (maternalHistory != null) {
			maternalHistory.setUuid(DataHelper.createUuid());

			DataHelper.Pair<InfrastructureData, List<String>> rashExposureInfrastructureAndErrors = loadLocalInfrastructure(
				maternalHistory.getRashExposureRegion(),
				maternalHistory.getRashExposureDistrict(),
				maternalHistory.getRashExposureCommunity(),
				null,
				null,
				null);

			handleInfraStructure(
				rashExposureInfrastructureAndErrors,
				Captions.MaternalHistory_rashExposure,
				validationErrors,
				(rashExposureInfrastructure) -> {
					maternalHistory.setRashExposureRegion(rashExposureInfrastructure.region);
					maternalHistory.setRashExposureDistrict(rashExposureInfrastructure.district);
					maternalHistory.setRashExposureCommunity(rashExposureInfrastructure.community);
				});
		}

		if (caze.getPortHealthInfo() != null) {
			caze.getPortHealthInfo().setUuid(DataHelper.createUuid());
		}

		return validationErrors;
	}

	private void processEpiData(EpiDataDto epiData) {
		epiData.setUuid(DataHelper.createUuid());
		epiData.getBurials().forEach(b -> {
			b.setUuid(DataHelper.createUuid());
			b.getBurialAddress().setUuid(DataHelper.createUuid());
		});
		epiData.getTravels().forEach(t -> t.setUuid(DataHelper.createUuid()));
		epiData.getGatherings().forEach(g -> {
			g.setUuid(DataHelper.createUuid());
			g.getGatheringAddress().setUuid(DataHelper.createUuid());
		});
	}

	private void processHealthConditions(HealthConditionsDto healthConditions) {
		healthConditions.setUuid(DataHelper.createUuid());
	}

	private ValidationErrors processContactData(ContactDto contact, PersonDto person, SormasToSormasOriginInfoDto originInfo) {
		ValidationErrors validationErrors = new ValidationErrors();

		processPerson(person);

		contact.setPerson(person.toReference());
		contact.setReportingUser(userService.getCurrentUser().toReference());

		DataHelper.Pair<InfrastructureData, List<String>> infrastructureAndErrors =
			loadLocalInfrastructure(contact.getRegion(), contact.getDistrict(), contact.getCommunity(), null, null, null);

		handleInfraStructure(infrastructureAndErrors, Captions.Contact, validationErrors, (infrastructure -> {
			contact.setRegion(infrastructure.region);
			contact.setDistrict(infrastructure.district);
			contact.setCommunity(infrastructure.community);
		}));

		// init uuids
		if (contact.getEpiData() != null) {
			processEpiData(contact.getEpiData());
		}

		if (contact.getHealthConditions() != null) {
			processHealthConditions(contact.getHealthConditions());
		}

		contact.setSormasToSormasOriginInfo(originInfo);

		return validationErrors;
	}

	private ValidationErrors processPerson(PersonDto person) {
		ValidationErrors validationErrors = new ValidationErrors();

		person.setUuid(DataHelper.createUuid());

		LocationDto address = person.getAddress();
		address.setUuid(DataHelper.createUuid());

		DataHelper.Pair<InfrastructureData, List<String>> infrastructureAndErrors =
			loadLocalInfrastructure(address.getRegion(), address.getDistrict(), address.getCommunity(), null, null, null);

		handleInfraStructure(infrastructureAndErrors, Captions.Person, validationErrors, (infrastructure -> {
			address.setRegion(infrastructure.region);
			address.setDistrict(infrastructure.district);
			address.setCommunity(infrastructure.community);
		}));

		return validationErrors;
	}

	private DataHelper.Pair<InfrastructureData, List<String>> loadLocalInfrastructure(
		RegionReferenceDto region,
		DistrictReferenceDto district,
		CommunityReferenceDto community,
		FacilityType facilityType,
		FacilityReferenceDto facility,
		PointOfEntryReferenceDto pointOfEntry) {

		InfrastructureData infrastructureData = new InfrastructureData();
		List<String> unmatchedFields = new ArrayList<>();

		RegionReferenceDto localRegion = null;
		if (region != null) {
			String regionName = region.getCaption();
			localRegion = regionFacade.getByName(regionName, false).stream().findFirst().orElse(null);

			if (localRegion == null) {
				unmatchedFields.add(I18nProperties.getCaption(Captions.region) + ": " + regionName);
			} else {
				infrastructureData.region = localRegion;
			}
		}

		DistrictReferenceDto localDistrict = null;
		if (district != null) {
			String districtName = district.getCaption();
			localDistrict = districtFacade.getByName(districtName, localRegion, false).stream().findFirst().orElse(null);
			if (localDistrict == null) {
				unmatchedFields.add(I18nProperties.getCaption(Captions.district) + ": " + districtName);
			} else {
				infrastructureData.district = localDistrict;
			}
		}

		CommunityReferenceDto localCommunity = null;
		if (community != null) {
			String communityName = community.getCaption();
			localCommunity = communityFacade.getByName(communityName, localDistrict, false).stream().findFirst().orElse(null);

			if (localCommunity == null) {
				unmatchedFields.add(I18nProperties.getCaption(Captions.community) + ": " + communityName);
			} else {
				infrastructureData.community = localCommunity;
			}
		}

		if (facility != null) {
			String facilityName = facility.getCaption();
			FacilityReferenceDto localFacility =
				facilityFacade.getByNameAndType(facilityName, localDistrict, localCommunity, facilityType, false).stream().findFirst().orElse(null);

			if (localFacility == null) {
				unmatchedFields.add(I18nProperties.getCaption(Captions.facility) + ": " + facilityName);
			} else {
				infrastructureData.facility = localFacility;
			}
		}

		if (pointOfEntry != null) {
			String pointOfEntryName = pointOfEntry.getCaption();
			PointOfEntryReferenceDto localPointOfEntry =
				pointOfEntryFacade.getByName(pointOfEntryName, localDistrict, false).stream().findFirst().orElse(null);

			if (localPointOfEntry == null) {
				unmatchedFields.add(I18nProperties.getCaption(Captions.pointOfEntry) + ": " + pointOfEntryName);
			} else {
				infrastructureData.pointOfEntry = localPointOfEntry;
			}
		}

		return new DataHelper.Pair(infrastructureData, unmatchedFields);
	}

	private void handleInfraStructure(
		DataHelper.Pair<InfrastructureData, List<String>> infrastructureAndErrors,
		String groupNameTag,
		ValidationErrors validationErrors,
		Consumer<InfrastructureData> onNoErrors) {

		List<String> errors = infrastructureAndErrors.getElement1();
		if (errors.size() > 0) {
			validationErrors.add(
				I18nProperties.getCaption(groupNameTag),
				String.format(I18nProperties.getString(Strings.errorSormasToSormasInfrastructure), String.join(",", errors)));

		} else {
			onNoErrors.accept(infrastructureAndErrors.getElement0());
		}
	}

	private <T> T handleValidationError(Supplier<T> saveOperation, String validationGroupCaption, String parentValidationGroup)
		throws SormasToSormasValidationException {
		try {
			return saveOperation.get();
		} catch (ValidationRuntimeException exception) {
			Map<String, List<String>> validationError = new HashMap<>(1);
			validationError.put(I18nProperties.getCaption(validationGroupCaption), Collections.singletonList(exception.getMessage()));

			Map<String, Map<String, List<String>>> parentError = new HashMap<>(1);
			parentError.put(parentValidationGroup, validationError);

			throw new SormasToSormasValidationException(parentError);
		}
	}

	private String buildCaseValidationGroupName(HasUuid caze) {
		return buildValidationGroupName(Captions.CaseData, caze);
	}

	private String buildContactValidationGroupName(ContactDto contact) {
		return buildValidationGroupName(Captions.Contact, contact);
	}

	private String buildSampleValidationGroupName(SampleDto sample) {
		return buildValidationGroupName(Captions.Sample, sample);
	}

	private String buildPathogenTestValidationGroupName(PathogenTestDto pathogenTest) {
		return buildValidationGroupName(Captions.PathogenTest, pathogenTest);
	}

	private String buildValidationGroupName(String captionTag, HasUuid hasUuid) {
		return String.format("%s %s", I18nProperties.getCaption(captionTag), DataHelper.getShortUuid(hasUuid.getUuid()));
	}

	private ValidationErrors validateCase(CaseDataDto caze) throws ValidationRuntimeException {
		ValidationErrors errors = new ValidationErrors();
		if (caseFacade.exists(caze.getUuid())) {
			errors.add(I18nProperties.getCaption(Captions.CaseData), I18nProperties.getValidationError(Validations.sormasToSormasCaseExists));
		}

		return errors;
	}

	private ValidationErrors validateContact(ContactDto contact) throws ValidationRuntimeException {
		ValidationErrors errors = new ValidationErrors();

		if (contactFacade.exists(contact.getUuid())) {
			errors.add(I18nProperties.getCaption(Captions.Contact), I18nProperties.getValidationError(Validations.sormasToSormasContactExists));
		}

		if (contact.getCaze() != null && !contactFacade.exists(contact.getCaze().getUuid())) {
			errors
				.add(I18nProperties.getCaption(Captions.CaseData), I18nProperties.getValidationError(Validations.sormasToSormasContactCaseNotExists));
		}

		return errors;
	}

	private ValidationErrors processOriginInfo(SormasToSormasOriginInfoDto originInfo, String validationGroupCaption) {
		if (originInfo == null) {
			return ValidationErrors.create(
				I18nProperties.getCaption(validationGroupCaption),
				I18nProperties.getValidationError(Validations.sormasToSormasShareInfoMissing));
		}

		ValidationErrors validationErrors = new ValidationErrors();

		if (originInfo.getOrganizationId() == null) {
			validationErrors.add(
				I18nProperties.getCaption(Captions.CaseData_sormasToSormasOriginInfo),
				I18nProperties.getValidationError(Validations.sormasToSormasOrganizationIdMissing));
		}

		if (DataHelper.isNullOrEmpty(originInfo.getSenderName())) {
			validationErrors.add(
				I18nProperties.getCaption(Captions.CaseData_sormasToSormasOriginInfo),
				I18nProperties.getValidationError(Validations.sormasToSormasSenderNameMissing));
		}

		originInfo.setUuid(DataHelper.createUuid());
		originInfo.setChangeDate(new Date());

		return validationErrors;
	}

	private Pseudonymizer createPseudonymizer(SormasToSormasOptionsDto options) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefaultNoCheckers(false);

		if (options.isPseudonymizePersonalData()) {
			pseudonymizer.addFieldAccessChecker(PersonalDataFieldAccessChecker.forcedNoAccess(), PersonalDataFieldAccessChecker.forcedNoAccess());
		}
		if (options.isPseudonymizeSensitiveData()) {
			pseudonymizer.addFieldAccessChecker(SensitiveDataFieldAccessChecker.forcedNoAccess(), SensitiveDataFieldAccessChecker.forcedNoAccess());
		}

		return pseudonymizer;
	}

	private SormasToSormasOriginInfoDto createSormasToSormasOriginInfo(User currentUser, SormasToSormasOptionsDto options)
		throws SormasToSormasException {
		OrganizationServerAccessData serverAccessData = getServerAccessData();

		SormasToSormasOriginInfoDto sormasToSormasOriginInfo = new SormasToSormasOriginInfoDto();
		sormasToSormasOriginInfo.setOrganizationId(serverAccessData.getId());
		sormasToSormasOriginInfo.setSenderName(String.format("%s %s", currentUser.getFirstName(), currentUser.getLastName()));
		sormasToSormasOriginInfo.setSenderEmail(currentUser.getUserEmail());
		sormasToSormasOriginInfo.setSenderPhoneNumber(currentUser.getPhone());
		sormasToSormasOriginInfo.setOwnershipHandedOver(options.isHandOverOwnership());
		sormasToSormasOriginInfo.setComment(options.getComment());

		return sormasToSormasOriginInfo;
	}

	private void saveNewShareInfo(UserReferenceDto sender, SormasToSormasOptionsDto options, Consumer<SormasToSormasShareInfo> setAssociatedObject) {
		SormasToSormasShareInfo shareInfo = new SormasToSormasShareInfo();

		shareInfo.setUuid(DataHelper.createUuid());
		shareInfo.setCreationDate(new Timestamp(new Date().getTime()));
		shareInfo.setOrganizationId(options.getOrganization().getUuid());
		shareInfo.setOwnershipHandedOver(options.isHandOverOwnership());
		shareInfo.setSender(userService.getByReferenceDto(sender));
		shareInfo.setComment(options.getComment());

		setAssociatedObject.accept(shareInfo);

		sormasToSormasShareInfoService.ensurePersisted(shareInfo);
	}

	private void saveSamples(List<SormasToSormasSampleDto> samples, SormasToSormasOriginInfoDto sormasToSormasOriginInfo)
		throws SormasToSormasValidationException {
		for (SormasToSormasSampleDto sormasToSormasSample : samples) {
			SampleDto sample = sormasToSormasSample.getSample();
			sample.setSormasToSormasOriginInfo(sormasToSormasOriginInfo);

			handleValidationError(() -> sampleFacade.saveSample(sample), Captions.Sample, buildSampleValidationGroupName(sample));

			for (PathogenTestDto pathogenTest : sormasToSormasSample.getPathogenTests()) {
				handleValidationError(
					() -> pathogenTestFacade.savePathogenTest(pathogenTest),
					Captions.PathogenTest,
					buildPathogenTestValidationGroupName(pathogenTest));
			}

			for (AdditionalTestDto additionalTest : sormasToSormasSample.getAdditionalTests()) {
				handleValidationError(
					() -> additionalTestFacade.saveAdditionalTest(additionalTest),
					Captions.AdditionalTest,
					buildValidationGroupName(Captions.AdditionalTest, additionalTest));
			}
		}
	}

	private void sendEntityToSormas(List<?> entities, String endpoint, SormasToSormasOptionsDto options) throws SormasToSormasException {

		OrganizationServerAccessData serverAccessData = serverAccessDataService.getServerAccessData()
			.orElseThrow(() -> new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
		OrganizationServerAccessData targetServerAccessData = getOrganizationServerAccessData(options.getOrganization().getUuid())
			.orElseThrow(() -> new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));

		String userCredentials = StartupShutdownService.SORMAS_TO_SORMAS_USER_NAME + ":" + targetServerAccessData.getRestUserPassword();

		Response response;
		try {
			byte[] encryptedEntities = encryptionService.encrypt(objectMapper.writeValueAsBytes(entities), targetServerAccessData.getId());
			response = sormasToSormasRestClient.post(
				targetServerAccessData.getHostName(),
				endpoint,
				"Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8),
				new SormasToSormasEncryptedDataDto(serverAccessData.getId(), encryptedEntities));
		} catch (JsonProcessingException e) {
			LOGGER.error("Unable to send data sormas", e);
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasSend));
		} catch (ResponseProcessingException e) {
			LOGGER.error("Unable to process sormas response", e);
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasResult));
		} catch (NoSuchAlgorithmException | KeyManagementException | ProcessingException e) {
			LOGGER.error("Unable to send data to sormas", e);

			String processingErrorMessage = I18nProperties.getString(Strings.errorSormasToSormasSend);
			if (ConnectException.class.isAssignableFrom(e.getCause().getClass())) {
				processingErrorMessage = I18nProperties.getString(Strings.errorSormasToSormasConnection);
			}

			throw new SormasToSormasException(processingErrorMessage);
		}

		int statusCode = response.getStatus();
		if (statusCode != HttpStatus.SC_NO_CONTENT) {
			String errorMessage = response.readEntity(String.class);
			Map<String, Map<String, List<String>>> errors = null;

			try {
				SormasToSormasErrorResponse errorResponse = objectMapper.readValue(errorMessage, SormasToSormasErrorResponse.class);
				errorMessage = I18nProperties.getString(Strings.errorSormasToSormasShare);
				errors = errorResponse.getErrors();
			} catch (IOException e) {
				// do nothing, keep the unparsed response as error message
			}

			if (statusCode != HttpStatus.SC_BAD_REQUEST) {
				// don't log validation errors, will be displayed on the UI
				LOGGER.error("Share case failed: {}; {}", statusCode, errorMessage);
			}

			throw new SormasToSormasException(errorMessage, errors);
		}
	}

	private <T> T[] decryptSharedData(SormasToSormasEncryptedDataDto encryptedData, Class<T[]> dataType) throws SormasToSormasException {
		try {
			byte[] decryptedData = encryptionService.decrypt(encryptedData.getData(), encryptedData.getOrganizationId());

			return objectMapper.readValue(decryptedData, dataType);
		} catch (IOException e) {
			LOGGER.error("Can't parse shared data", e);
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasDecrypt));
		}
	}

	private OrganizationServerAccessData getServerAccessData() throws SormasToSormasException {
		return serverAccessDataService.getServerAccessData()
			.orElseThrow(() -> new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasCertNotGenerated)));
	}

	private Optional<OrganizationServerAccessData> getOrganizationServerAccessData(String id) {
		return serverAccessDataService.getServerListItemById(id);
	}

	public SormasToSormasOriginInfo fromSormasToSormasOriginInfoDto(SormasToSormasOriginInfoDto source) {
		if (source == null) {
			return null;
		}

		SormasToSormasOriginInfo target = sormasToSormasOriginInfoService.getByUuid(source.getUuid());
		if (target == null) {
			target = new SormasToSormasOriginInfo();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setOrganizationId(source.getOrganizationId());
		target.setSenderName(source.getSenderName());
		target.setSenderEmail(source.getSenderEmail());
		target.setSenderPhoneNumber(source.getSenderPhoneNumber());
		target.setOwnershipHandedOver(source.isOwnershipHandedOver());
		target.setComment(source.getComment());

		return target;
	}

	public static SormasToSormasOriginInfoDto toSormasToSormasOriginInfoDto(SormasToSormasOriginInfo source) {
		if (source == null) {
			return null;
		}

		SormasToSormasOriginInfoDto target = new SormasToSormasOriginInfoDto();

		DtoHelper.fillDto(target, source);

		target.setOrganizationId(source.getOrganizationId());
		target.setSenderName(source.getSenderName());
		target.setSenderEmail(source.getSenderEmail());
		target.setSenderPhoneNumber(source.getSenderPhoneNumber());
		target.setOwnershipHandedOver(source.isOwnershipHandedOver());
		target.setComment(source.getComment());

		return target;
	}

	public SormasToSormasShareInfoDto toSormasToSormasShareInfoDto(SormasToSormasShareInfo source) {
		SormasToSormasShareInfoDto target = new SormasToSormasShareInfoDto();

		DtoHelper.fillDto(target, source);

		if (source.getCaze() != null) {
			target.setCaze(source.getCaze().toReference());
		}

		if (source.getContact() != null) {
			target.setContact(source.getContact().toReference());
		}

		if (source.getSample() != null) {
			target.setSample(source.getSample().toReference());
		}

		OrganizationServerAccessData serverAccessData = getOrganizationServerAccessData(source.getOrganizationId())
			.orElseGet(() -> new OrganizationServerAccessData(source.getOrganizationId(), source.getOrganizationId()));
		target.setTarget(serverAccessData.toReference());

		target.setSender(source.getSender().toReference());
		target.setOwnershipHandedOver(source.isOwnershipHandedOver());
		target.setComment(source.getComment());

		return target;
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasFacadeEjbLocal extends SormasToSormasFacadeEjb {

	}

	private static class InfrastructureData {

		private RegionReferenceDto region;
		private DistrictReferenceDto district;
		private CommunityReferenceDto community;
		private FacilityReferenceDto facility;
		private PointOfEntryReferenceDto pointOfEntry;
	}

	private static class ProcessedCaseData {

		private PersonDto person;
		private CaseDataDto caze;
		private List<SormasToSormasCaseDto.AssociatedContactDto> associatedContacts;
		private List<SormasToSormasSampleDto> samples;

		static ProcessedCaseData create(
			PersonDto person,
			CaseDataDto caze,
			List<SormasToSormasCaseDto.AssociatedContactDto> associatedContacts,
			List<SormasToSormasSampleDto> samples) {
			ProcessedCaseData caseData = new ProcessedCaseData();

			caseData.person = person;
			caseData.caze = caze;
			caseData.associatedContacts = associatedContacts;
			caseData.samples = samples;

			return caseData;
		}
	}

	private static class ProcessedContactData {

		private PersonDto person;
		private ContactDto contact;
		private List<SormasToSormasSampleDto> samples;

		static ProcessedContactData create(PersonDto person, ContactDto contact, List<SormasToSormasSampleDto> samples) {
			ProcessedContactData data = new ProcessedContactData();
			data.person = person;
			data.contact = contact;
			data.samples = samples;

			return data;
		}
	}
}
