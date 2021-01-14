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
import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.CASE_SYNC_ENDPOINT;
import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.CONTACT_ENDPOINT;
import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.CONTACT_SYNC_ENDPOINT;
import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.RESOURCE_PATH;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildCaseValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildContactValidationGroupName;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
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

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasErrorResponse;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasFacade;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.ValidationErrors;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.StartupShutdownService;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.sample.AdditionalTestFacadeEjb;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.databuilder.CaseShareData;
import de.symeda.sormas.backend.sormastosormas.databuilder.CaseShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.databuilder.ContactShareData;
import de.symeda.sormas.backend.sormastosormas.databuilder.ContactShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.datapersister.ProcessedCaseDataPersister;
import de.symeda.sormas.backend.sormastosormas.datapersister.ProcessedContactDataPersister;
import de.symeda.sormas.backend.sormastosormas.dataprocessor.SharedCaseProcessor;
import de.symeda.sormas.backend.sormastosormas.dataprocessor.SharedContactProcessor;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "SormasToSormasFacade")
public class SormasToSormasFacadeEjb implements SormasToSormasFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(SormasToSormasFacadeEjb.class);

	public static final String SAVE_SHARED_CASE_ENDPOINT = RESOURCE_PATH + CASE_ENDPOINT;

	private static final String SAVE_SHARED_CONTACT_ENDPOINT = RESOURCE_PATH + CONTACT_ENDPOINT;

	public static final String UPDATE_SHARED_CASE_ENDPOINT = RESOURCE_PATH + CASE_SYNC_ENDPOINT;

	public static final String UPDATE_SHARED_CONTACT_ENDPOINT = RESOURCE_PATH + CONTACT_SYNC_ENDPOINT;

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private SormasToSormasOriginInfoService originInfoService;
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
	@EJB
	private SharedCaseProcessor sharedCaseProcessor;
	@EJB
	private ProcessedCaseDataPersister caseDataPersister;
	@EJB
	private SharedContactProcessor sharedContactProcessor;
	@EJB
	private ProcessedContactDataPersister contactDataPersister;
	@EJB
	private CaseShareDataBuilder caseShareDataBuilder;
	@EJB
	private ContactShareDataBuilder contactShareDataBuilder;
	@EJB
	private SormasToSormasOriginInfoFacadeEjbLocal originInfoFacadeEjb;

	private final ObjectMapper objectMapper;

	public SormasToSormasFacadeEjb() {
		objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	}

	@Override
	public void saveSharedCases(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {
		SormasToSormasCaseDto[] sharedCases = decryptSharedData(encryptedData, SormasToSormasCaseDto[].class);

		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		List<ProcessedCaseData> casesToSave = new ArrayList<>(sharedCases.length);

		for (SormasToSormasCaseDto sharedCase : sharedCases) {
			try {
				CaseDataDto caze = sharedCase.getCaze();

				ValidationErrors caseErrors = validateSharedCase(caze);
				if (caseErrors.hasError()) {
					validationErrors.put(buildCaseValidationGroupName(caze), caseErrors);
				} else {
					ProcessedCaseData processedCaseData = sharedCaseProcessor.processSharedData(sharedCase);

					processedCaseData.getCaze().setSormasToSormasOriginInfo(processedCaseData.getOriginInfo());

					casesToSave.add(processedCaseData);
				}
			} catch (SormasToSormasValidationException validationException) {
				validationErrors.putAll(validationException.getErrors());
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (ProcessedCaseData caseData : casesToSave) {
			caseDataPersister.persistSharedData(caseData);
		}
	}

	@Override
	public void saveSharedContacts(SormasToSormasEncryptedDataDto sharedData) throws SormasToSormasException, SormasToSormasValidationException {
		SormasToSormasContactDto[] sharedContacts = decryptSharedData(sharedData, SormasToSormasContactDto[].class);

		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		List<ProcessedContactData> contactsToSave = new ArrayList<>(sharedContacts.length);

		for (SormasToSormasContactDto sharedContact : sharedContacts) {
			try {
				ContactDto contact = sharedContact.getContact();
				ValidationErrors contactErrors = validateSharedContact(contact);
				if (contactErrors.hasError()) {
					validationErrors.put(buildContactValidationGroupName(contact), contactErrors);
				} else {
					ProcessedContactData processedContactData = sharedContactProcessor.processSharedData(sharedContact);
					processedContactData.getContact().setSormasToSormasOriginInfo(processedContactData.getOriginInfo());
					contactsToSave.add(processedContactData);
				}
			} catch (SormasToSormasValidationException validationException) {
				validationErrors.putAll(validationException.getErrors());
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (ProcessedContactData contactData : contactsToSave) {
			contactDataPersister.persistSharedData(contactData);
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
			CaseShareData shareData = caseShareDataBuilder.buildShareData(caze, currentUser, options);

			entitiesToSend.add(shareData.getCaseShareData());
			contactsToSend.addAll(shareData.getAssociatedContacts());
			samplesToSend.addAll(shareData.getSamples());
		}

		sendEntitiesToSormas(
			entitiesToSend,
			options,
			(host, authToken, encryptedData) -> sormasToSormasRestClient.post(host, SAVE_SHARED_CASE_ENDPOINT, authToken, encryptedData));

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
			ContactShareData shareData = contactShareDataBuilder.buildShareData(contact, currentUser, options);

			entitiesToSend.add(shareData.getContactShareData());
			samplesToSend.addAll(shareData.getSamples());
		}

		sendEntitiesToSormas(
			entitiesToSend,
			options,
			(host, authToken, encryptedData) -> sormasToSormasRestClient.post(host, SAVE_SHARED_CONTACT_ENDPOINT, authToken, encryptedData));

		contactsToSend.forEach(contact -> saveNewShareInfo(currentUser.toReference(), options, i -> i.setContact(contact)));
		samplesToSend.forEach(sample -> saveNewShareInfo(currentUser.toReference(), options, i -> i.setSample(sample)));
	}

	@Override
	@Transactional
	public void returnCase(String caseUuid, SormasToSormasOptionsDto options) throws SormasToSormasException {
		options.setHandOverOwnership(true);
		User currentUser = userService.getCurrentUser();

		Case caze = caseService.getByUuid(caseUuid);
		validateCasesBeforeSend(Collections.singletonList(caze));

		CaseShareData shareData = caseShareDataBuilder.buildShareData(caze, currentUser, options);

		sendEntitiesToSormas(
			Collections.singletonList(shareData.getCaseShareData()),
			options,
			(host, authToken, encryptedData) -> sormasToSormasRestClient.put(host, SAVE_SHARED_CASE_ENDPOINT, authToken, encryptedData));

		caze.getSormasToSormasOriginInfo().setOwnershipHandedOver(false);
		originInfoService.persist(caze.getSormasToSormasOriginInfo());

		shareData.getAssociatedContacts().forEach(c -> {
			if (c.getSormasToSormasOriginInfo() == null) {
				saveNewShareInfo(currentUser.toReference(), options, i -> i.setContact(c));
			}
		});

		shareData.getSamples().forEach(s -> {
			if (s.getSormasToSormasOriginInfo() == null) {
				saveNewShareInfo(currentUser.toReference(), options, i -> i.setSample(s));
			}
		});
	}

	@Override
	public void returnContact(String contactUuid, SormasToSormasOptionsDto options) throws SormasToSormasException {
		options.setHandOverOwnership(true);
		User currentUser = userService.getCurrentUser();

		Contact contact = contactService.getByUuid(contactUuid);
		validateContactsBeforeSend(Collections.singletonList(contact));

		ContactShareData shareData = contactShareDataBuilder.buildShareData(contact, currentUser, options);

		sendEntitiesToSormas(
			Collections.singletonList(shareData.getContactShareData()),
			options,
			(host, authToken, encryptedData) -> sormasToSormasRestClient.put(host, SAVE_SHARED_CONTACT_ENDPOINT, authToken, encryptedData));

		contact.getSormasToSormasOriginInfo().setOwnershipHandedOver(false);
		originInfoService.persist(contact.getSormasToSormasOriginInfo());

		shareData.getSamples().forEach(s -> {
			if (s.getSormasToSormasOriginInfo() == null) {
				saveNewShareInfo(currentUser.toReference(), options, i -> i.setSample(s));
			}
		});
	}

	@Override
	public void saveReturnedCase(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {
		SormasToSormasCaseDto[] sharedCases = decryptSharedData(encryptedData, SormasToSormasCaseDto[].class);

		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		List<ProcessedCaseData> casesToSave = new ArrayList<>(sharedCases.length);

		for (SormasToSormasCaseDto sharedCase : sharedCases) {
			try {
				CaseDataDto caze = sharedCase.getCaze();

				ValidationErrors caseErrors = validateExistingCase(caze);
				if (caseErrors.hasError()) {
					validationErrors.put(buildCaseValidationGroupName(caze), caseErrors);
				} else {
					ProcessedCaseData processedCaseData = sharedCaseProcessor.processSharedData(sharedCase);
					casesToSave.add(processedCaseData);
				}
			} catch (SormasToSormasValidationException validationException) {
				validationErrors.putAll(validationException.getErrors());
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (ProcessedCaseData caseData : casesToSave) {
			caseDataPersister.persistReturnedData(caseData, caseData.getOriginInfo());
		}
	}

	@Override
	public void saveReturnedContact(SormasToSormasEncryptedDataDto sharedData) throws SormasToSormasException, SormasToSormasValidationException {
		SormasToSormasContactDto[] sharedContacts = decryptSharedData(sharedData, SormasToSormasContactDto[].class);

		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		List<ProcessedContactData> contactsToSave = new ArrayList<>(sharedContacts.length);

		for (SormasToSormasContactDto sharedContact : sharedContacts) {
			try {
				ContactDto contact = sharedContact.getContact();
				ValidationErrors contactErrors = validateExistingContact(contact);
				if (contactErrors.hasError()) {
					validationErrors.put(buildContactValidationGroupName(contact), contactErrors);
				} else {
					ProcessedContactData processedContactData = sharedContactProcessor.processSharedData(sharedContact);
					processedContactData.getContact().setSormasToSormasOriginInfo(processedContactData.getOriginInfo());
					contactsToSave.add(processedContactData);
				}
			} catch (SormasToSormasValidationException validationException) {
				validationErrors.putAll(validationException.getErrors());
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (ProcessedContactData contactData : contactsToSave) {
			contactDataPersister.persistReturnedData(contactData, contactData.getOriginInfo());
		}
	}

	@Override
	public void syncCase(String caseUuid, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();

		Case caze = caseService.getByUuid(caseUuid);
		validateCasesBeforeSend(Collections.singletonList(caze));

		CaseShareData shareData = caseShareDataBuilder.buildShareData(caze, currentUser, options);

		sendEntitiesToSormas(
			Collections.singletonList(shareData.getCaseShareData()),
			options,
			(host, authToken, encryptedData) -> sormasToSormasRestClient.post(host, UPDATE_SHARED_CASE_ENDPOINT, authToken, encryptedData));

		SormasToSormasShareInfo shareInfo = shareInfoService.getByCaseAndOrganization(caze.getUuid(), options.getOrganization().getUuid());
		updateShareInfoOptions(shareInfo, options);

		shareData.getAssociatedContacts().forEach(c -> {
			SormasToSormasShareInfo contactShareInfo = shareInfoService.getByContactAndOrganization(c.getUuid(), options.getOrganization().getUuid());
			if (contactShareInfo == null) {
				saveNewShareInfo(currentUser.toReference(), options, i -> i.setContact(c));
			} else {
				updateShareInfoOptions(contactShareInfo, options);
			}
		});

		shareData.getSamples().forEach(s -> {
			SormasToSormasShareInfo sampleShareInfo = shareInfoService.getBySampleAndOrganization(s.getUuid(), options.getOrganization().getUuid());
			if (sampleShareInfo == null) {
				saveNewShareInfo(currentUser.toReference(), options, i -> i.setSample(s));
			} else {
				updateShareInfoOptions(sampleShareInfo, options);
			}
		});
	}

	@Override
	public void saveSyncedCases(SormasToSormasEncryptedDataDto sharedData) throws SormasToSormasException, SormasToSormasValidationException {
		SormasToSormasCaseDto[] sharedCases = decryptSharedData(sharedData, SormasToSormasCaseDto[].class);

		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		List<ProcessedCaseData> casesToSave = new ArrayList<>(sharedCases.length);

		for (SormasToSormasCaseDto sharedCase : sharedCases) {
			try {
				CaseDataDto caze = sharedCase.getCaze();

				ValidationErrors caseErrors = validateExistingCase(caze);
				if (caseErrors.hasError()) {
					validationErrors.put(buildCaseValidationGroupName(caze), caseErrors);
				} else {
					ProcessedCaseData processedCaseData = sharedCaseProcessor.processSharedData(sharedCase);
					casesToSave.add(processedCaseData);
				}
			} catch (SormasToSormasValidationException validationException) {
				validationErrors.putAll(validationException.getErrors());
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (ProcessedCaseData caseData : casesToSave) {
			caseDataPersister.persistSyncData(caseData);
		}
	}

	@Override
	public void syncContact(String contactUuid, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		Contact contact = contactService.getByUuid(contactUuid);

		validateContactsBeforeSend(Collections.singletonList(contact));

		List<SormasToSormasContactDto> entitiesToSend = new ArrayList<>();

		ContactShareData shareData = contactShareDataBuilder.buildShareData(contact, currentUser, options);

		entitiesToSend.add(shareData.getContactShareData());

		sendEntitiesToSormas(
			entitiesToSend,
			options,
			(host, authToken, encryptedData) -> sormasToSormasRestClient.post(host, UPDATE_SHARED_CONTACT_ENDPOINT, authToken, encryptedData));

		SormasToSormasShareInfo shareInfo = shareInfoService.getByContactAndOrganization(contact.getUuid(), options.getOrganization().getUuid());
		updateShareInfoOptions(shareInfo, options);

		shareData.getSamples().forEach(sample -> {
			SormasToSormasShareInfo sampleShareInfo =
				shareInfoService.getBySampleAndOrganization(sample.getUuid(), options.getOrganization().getUuid());
			if (sampleShareInfo == null) {
				saveNewShareInfo(currentUser.toReference(), options, i -> i.setSample(sample));
			} else {
				updateShareInfoOptions(sampleShareInfo, options);
			}
		});
	}

	@Override
	public void saveSyncedContacts(SormasToSormasEncryptedDataDto sharedData) throws SormasToSormasException, SormasToSormasValidationException {
		SormasToSormasContactDto[] sharedContacts = decryptSharedData(sharedData, SormasToSormasContactDto[].class);

		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		List<ProcessedContactData> contactsToSave = new ArrayList<>(sharedContacts.length);

		for (SormasToSormasContactDto sharedContact : sharedContacts) {
			try {
				ContactDto caze = sharedContact.getContact();

				ValidationErrors contactErrors = validateExistingContact(caze);
				if (contactErrors.hasError()) {
					validationErrors.put(buildContactValidationGroupName(caze), contactErrors);
				} else {
					ProcessedContactData processedContactData = sharedContactProcessor.processSharedData(sharedContact);
					contactsToSave.add(processedContactData);
				}
			} catch (SormasToSormasValidationException validationException) {
				validationErrors.putAll(validationException.getErrors());
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (ProcessedContactData contactData : contactsToSave) {
			contactDataPersister.persistSyncData(contactData);
		}
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

		Predicate filter = shareInfoService.buildCriteriaFilter(criteria, cb, root);
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
		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		for (Case caze : cases) {
			if (!caseService.isCaseEditAllowed(caze)) {
				validationErrors.put(
					buildCaseValidationGroupName(caze),
					ValidationErrors
						.create(I18nProperties.getCaption(Captions.CaseData), I18nProperties.getString(Strings.errorSormasToSormasNotEditable)));
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasShare), validationErrors);
		}
	}

	private void validateContactsBeforeSend(List<Contact> contacts) throws SormasToSormasException {
		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		for (Contact contact : contacts) {
			if (!contactService.isContactEditAllowed(contact)) {
				validationErrors.put(
					buildCaseValidationGroupName(contact),
					ValidationErrors
						.create(I18nProperties.getCaption(Captions.Contact), I18nProperties.getString(Strings.errorSormasToSormasNotEditable)));
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasShare), validationErrors);
		}
	}

	private void saveNewShareInfo(UserReferenceDto sender, SormasToSormasOptionsDto options, Consumer<SormasToSormasShareInfo> setAssociatedObject) {
		SormasToSormasShareInfo shareInfo = new SormasToSormasShareInfo();

		shareInfo.setUuid(DataHelper.createUuid());
		shareInfo.setCreationDate(new Timestamp(new Date().getTime()));
		shareInfo.setOrganizationId(options.getOrganization().getUuid());
		shareInfo.setSender(userService.getByReferenceDto(sender));

		addOptionsToShareInfo(options, shareInfo);

		setAssociatedObject.accept(shareInfo);

		shareInfoService.ensurePersisted(shareInfo);
	}

	private void updateShareInfoOptions(SormasToSormasShareInfo shareInfo, SormasToSormasOptionsDto options) {
		addOptionsToShareInfo(options, shareInfo);

		shareInfoService.ensurePersisted(shareInfo);
	}

	private void addOptionsToShareInfo(SormasToSormasOptionsDto options, SormasToSormasShareInfo shareInfo) {
		shareInfo.setOwnershipHandedOver(options.isHandOverOwnership());
		shareInfo.setWithAssociatedContacts(options.isWithAssociatedContacts());
		shareInfo.setWithSamples(options.isWithSamples());
		shareInfo.setPseudonymizedPersonalData(options.isPseudonymizePersonalData());
		shareInfo.setPseudonymizedSensitiveData(options.isPseudonymizeSensitiveData());
		shareInfo.setComment(options.getComment());
	}

	private void sendEntitiesToSormas(List<?> entities, SormasToSormasOptionsDto options, RestCall restCall) throws SormasToSormasException {

		OrganizationServerAccessData serverAccessData = serverAccessDataService.getServerAccessData()
			.orElseThrow(() -> new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
		OrganizationServerAccessData targetServerAccessData = getOrganizationServerAccessData(options.getOrganization().getUuid())
			.orElseThrow(() -> new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));

		String userCredentials = StartupShutdownService.SORMAS_TO_SORMAS_USER_NAME + ":" + targetServerAccessData.getRestUserPassword();

		Response response;
		try {
			byte[] encryptedEntities = encryptionService.encrypt(objectMapper.writeValueAsBytes(entities), targetServerAccessData.getId());
			response = restCall.call(
				targetServerAccessData.getHostName(),
				"Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8),
				new SormasToSormasEncryptedDataDto(serverAccessData.getId(), encryptedEntities));
		} catch (JsonProcessingException e) {
			LOGGER.error("Unable to send data sormas", e);
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasSend));
		} catch (ResponseProcessingException e) {
			LOGGER.error("Unable to process sormas response", e);
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasResult));
		} catch (ProcessingException e) {
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
			Map<String, ValidationErrors> errors = null;

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

	private Optional<OrganizationServerAccessData> getOrganizationServerAccessData(String id) {
		return serverAccessDataService.getServerListItemById(id);
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
		target.setWithAssociatedContacts(source.isWithAssociatedContacts());
		target.setWithSamples(source.isWithSamples());
		target.setPseudonymizedPersonalData(source.isPseudonymizedPersonalData());
		target.setPseudonymizedSensitiveData(source.isPseudonymizedSensitiveData());
		target.setComment(source.getComment());

		return target;
	}

	private ValidationErrors validateSharedCase(CaseDataDto caze) throws ValidationRuntimeException {
		ValidationErrors errors = new ValidationErrors();
		if (caseFacade.exists(caze.getUuid())) {
			errors.add(I18nProperties.getCaption(Captions.CaseData), I18nProperties.getValidationError(Validations.sormasToSormasCaseExists));
		}

		return errors;
	}

	private ValidationErrors validateExistingCase(CaseDataDto caze) throws ValidationRuntimeException {
		ValidationErrors errors = new ValidationErrors();
		if (!caseFacade.exists(caze.getUuid())) {
			errors
				.add(I18nProperties.getCaption(Captions.CaseData), I18nProperties.getValidationError(Validations.sormasToSormasReturnCaseNotExists));
		}

		return errors;
	}

	private ValidationErrors validateSharedContact(ContactDto contact) throws ValidationRuntimeException {
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

	private ValidationErrors validateExistingContact(ContactDto contact) throws ValidationRuntimeException {
		ValidationErrors errors = new ValidationErrors();

		if (!contactFacade.exists(contact.getUuid())) {
			errors.add(
				I18nProperties.getCaption(Captions.Contact),
				I18nProperties.getValidationError(Validations.sormasToSormasReturnContactNotExists));
		}

		return errors;
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasFacadeEjbLocal extends SormasToSormasFacadeEjb {

	}

	private interface RestCall {

		Response call(String host, String authToken, SormasToSormasEncryptedDataDto encryptedData) throws JsonProcessingException;
	}
}
