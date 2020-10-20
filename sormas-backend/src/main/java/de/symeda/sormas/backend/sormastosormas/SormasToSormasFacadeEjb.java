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
import java.util.List;
import java.util.Optional;
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
import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasErrorResponse;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasFacade;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.user.UserReferenceDto;
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

	private final ObjectMapper objectMapper;

	public SormasToSormasFacadeEjb() {
		objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	}

	@Override
	@Transactional
	public void saveSharedCase(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException {
		SormasToSormasCaseDto shareCaseDto = decryptSharedData(encryptedData, SormasToSormasCaseDto.class);
		PersonDto person = shareCaseDto.getPerson();
		CaseDataDto caze = shareCaseDto.getCaze();
		List<SormasToSormasCaseDto.AssociatedContactDto> associatedContacts = shareCaseDto.getAssociatedContacts();
		SormasToSormasOriginInfoDto originInfo = shareCaseDto.getOriginInfo();

		validateCase(caze);

		processOriginInfo(originInfo);
		processCaseData(caze, person, associatedContacts, originInfo);

		personFacade.savePerson(person);
		CaseDataDto savedCase = caseFacade.saveCase(caze);

		if (associatedContacts != null) {
			associatedContacts.forEach(associatedContact -> {
				personFacade.savePerson(associatedContact.getPerson());

				ContactDto contact = associatedContact.getContact();
				// set the persisted origin info to avoid outdated entity issue
				contact.setSormasToSormasOriginInfo(savedCase.getSormasToSormasOriginInfo());

				contactFacade.saveContact(contact);
			});
		}
	}

	@Override
	@Transactional
	public void saveSharedContact(SormasToSormasEncryptedDataDto sharedData) throws SormasToSormasException {
		SormasToSormasContactDto sharedContact = decryptSharedData(sharedData, SormasToSormasContactDto.class);

		PersonDto person = sharedContact.getPerson();
		ContactDto contact = sharedContact.getContact();

		validateContact(contact);

		processContactData(contact, person);

		personFacade.savePerson(person);
		contactFacade.saveContact(contact);
	}

	@Override
	public void shareCase(String uuid, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		Case caze = caseService.getByUuid(uuid);

		Pseudonymizer pseudonymizer = createPseudonymizer(options);

		PersonDto personDto = getPersonDto(caze.getPerson(), pseudonymizer, options);
		CaseDataDto cazeDto = getCazeDto(caze, pseudonymizer);
		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfo(currentUser, options);

		SormasToSormasCaseDto entityToSend = new SormasToSormasCaseDto(personDto, cazeDto, originInfo);
		List<Contact> associatedContacts = Collections.emptyList();
		if (options.isWithAssociatedContacts()) {
			associatedContacts = contactService.findBy(new ContactCriteria().caze(caze.toReference()), userService.getCurrentUser());
			entityToSend.setAssociatedContacts(getAssociatedContactDtos(associatedContacts, pseudonymizer, options));
		}

		sendEntityToSormas(entityToSend, SAVE_SHARED_CASE_ENDPOINT, options);

		saveNewShareInfo(currentUser.toReference(), options, caze, null);
		associatedContacts.forEach((contact) -> saveNewShareInfo(currentUser.toReference(), options, null, contact));
	}

	@Override
	public void shareContact(String uuid, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		Contact contact = contactService.getByUuid(uuid);

		Pseudonymizer pseudonymizer = createPseudonymizer(options);

		PersonDto personDto = getPersonDto(contact.getPerson(), pseudonymizer, options);
		ContactDto contactDto = getContactDto(contact, pseudonymizer);
		contactDto.setSormasToSormasOriginInfo(createSormasToSormasOriginInfo(currentUser, options));

		sendEntityToSormas(new SormasToSormasContactDto(personDto, contactDto), SAVE_SHARED_CONTACT_ENDPOINT, options);

		saveNewShareInfo(currentUser.toReference(), options, null, contact);
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
		return !serverAccessDataService.getOrganizationList().isEmpty();
	}

	@Override
	public ServerAccessDataReferenceDto getOrganizationRef(String id) {
		return getOrganizationServerAccessData(id).map(OrganizationServerAccessData::toReference).orElseGet(null);
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

	private void processCaseData(
		CaseDataDto caze,
		PersonDto person,
		List<SormasToSormasCaseDto.AssociatedContactDto> associatedContacts,
		SormasToSormasOriginInfoDto originInfo) {
		processPerson(person);

		caze.setPerson(person.toReference());
		caze.setReportingUser(userService.getCurrentUser().toReference());

		InfrastructureData infrastructure = loadLocalInfrastructure(
			caze.getRegion(),
			caze.getDistrict(),
			caze.getCommunity(),
			caze.getFacilityType(),
			caze.getHealthFacility(),
			caze.getPointOfEntry());

		caze.setRegion(infrastructure.region);
		caze.setDistrict(infrastructure.district);
		caze.setCommunity(infrastructure.community);
		caze.setHealthFacility(infrastructure.facility);
		caze.setPointOfEntry(infrastructure.pointOfEntry);

		processEmbeddedObjects(caze);

		caze.setSormasToSormasOriginInfo(originInfo);

		if (associatedContacts != null) {
			associatedContacts.forEach(associatedContact -> {
				ContactDto contact = associatedContact.getContact();
				processContactData(contact, associatedContact.getPerson());

				contact.setSormasToSormasOriginInfo(originInfo);
			});
		}
	}

	private void processEmbeddedObjects(CaseDataDto caze) {
		if (caze.getHospitalization() != null) {
			caze.getHospitalization().setUuid(DataHelper.createUuid());
			caze.getHospitalization().getPreviousHospitalizations().forEach(ph -> {
				ph.setUuid(DataHelper.createUuid());

				InfrastructureData phInfrastructure =
					loadLocalInfrastructure(ph.getRegion(), ph.getDistrict(), ph.getCommunity(), FacilityType.HOSPITAL, ph.getHealthFacility(), null);

				ph.setRegion(phInfrastructure.region);
				ph.setDistrict(phInfrastructure.district);
				ph.setCommunity(phInfrastructure.community);
				ph.setHealthFacility(phInfrastructure.facility);
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

			InfrastructureData rashExposureInfrastructure = loadLocalInfrastructure(
				maternalHistory.getRashExposureRegion(),
				maternalHistory.getRashExposureDistrict(),
				maternalHistory.getRashExposureCommunity(),
				null,
				null,
				null);

			maternalHistory.setRashExposureRegion(rashExposureInfrastructure.region);
			maternalHistory.setRashExposureDistrict(rashExposureInfrastructure.district);
			maternalHistory.setRashExposureCommunity(rashExposureInfrastructure.community);
		}

		if (caze.getPortHealthInfo() != null) {
			caze.getPortHealthInfo().setUuid(DataHelper.createUuid());
		}
	}

	private void processEpiData(EpiDataDto epiData) {
		epiData.setUuid(DataHelper.createUuid());
		epiData.getExposures().forEach(e -> {
			e.setUuid(DataHelper.createUuid());
			e.getLocation().setUuid(DataHelper.createUuid());
		});
	}

	private void processHealthConditions(HealthConditionsDto healthConditions) {
		healthConditions.setUuid(DataHelper.createUuid());
	}

	private void processContactData(ContactDto contact, PersonDto person) {
		processPerson(person);

		contact.setPerson(person.toReference());
		contact.setReportingUser(userService.getCurrentUser().toReference());

		InfrastructureData infrastructure =
			loadLocalInfrastructure(contact.getRegion(), contact.getDistrict(), contact.getCommunity(), null, null, null);

		contact.setRegion(infrastructure.region);
		contact.setDistrict(infrastructure.district);
		contact.setCommunity(infrastructure.community);

		// init uuids
		if (contact.getEpiData() != null) {
			processEpiData(contact.getEpiData());
		}

		if (contact.getHealthConditions() != null) {
			processHealthConditions(contact.getHealthConditions());
		}
	}

	private void processPerson(PersonDto person) {
		person.setUuid(DataHelper.createUuid());

		LocationDto address = person.getAddress();
		address.setUuid(DataHelper.createUuid());

		InfrastructureData infrastructure =
			loadLocalInfrastructure(address.getRegion(), address.getDistrict(), address.getCommunity(), null, null, null);

		address.setRegion(infrastructure.region);
		address.setDistrict(infrastructure.district);
		address.setCommunity(infrastructure.community);
	}

	private InfrastructureData loadLocalInfrastructure(
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

		if (unmatchedFields.size() > 0) {
			throw new ValidationRuntimeException(
				String.format(I18nProperties.getString(Strings.errorSormasToSormasInfrastructure), String.join(",", unmatchedFields)));
		}

		return infrastructureData;
	}

	private void validateCase(CaseDataDto caze) throws ValidationRuntimeException {
		if (caseFacade.exists(caze.getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasCaseExists));
		}
	}

	private void validateContact(ContactDto contact) throws ValidationRuntimeException {
		if (contactFacade.exists(contact.getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasContactExists));
		}

		if (contact.getCaze() != null && !contactFacade.exists(contact.getCaze().getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasContactCaseNotExists));
		}

		processOriginInfo(contact.getSormasToSormasOriginInfo());
	}

	private void processOriginInfo(SormasToSormasOriginInfoDto originInfo) {
		if (originInfo == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasShareInfoMissing));
		}

		if (originInfo.getOrganizationId() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasOrganizationIdMissing));
		}

		if (DataHelper.isNullOrEmpty(originInfo.getSenderName())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasSenderNameMissing));
		}

		originInfo.setUuid(DataHelper.createUuid());
		originInfo.setChangeDate(new Date());
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

	private void saveNewShareInfo(UserReferenceDto sender, SormasToSormasOptionsDto options, Case caze, Contact contact) {
		SormasToSormasShareInfo shareInfo = new SormasToSormasShareInfo();

		shareInfo.setUuid(DataHelper.createUuid());
		shareInfo.setCreationDate(new Timestamp(new Date().getTime()));
		shareInfo.setOrganizationId(options.getOrganization().getUuid());
		shareInfo.setOwnershipHandedOver(options.isHandOverOwnership());
		shareInfo.setSender(userService.getByReferenceDto(sender));
		shareInfo.setComment(options.getComment());

		shareInfo.setCaze(caze);
		shareInfo.setContact(contact);

		sormasToSormasShareInfoService.ensurePersisted(shareInfo);
	}

	private void sendEntityToSormas(Object entity, String endpoint, SormasToSormasOptionsDto options) throws SormasToSormasException {

		OrganizationServerAccessData serverAccessData = serverAccessDataService.getServerAccessData()
			.orElseThrow(() -> new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
		OrganizationServerAccessData targetServerAccessData = getOrganizationServerAccessData(options.getOrganization().getUuid())
			.orElseThrow(() -> new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));

		String userCredentials = StartupShutdownService.SORMAS_TO_SORMAS_USER_NAME + ":" + targetServerAccessData.getRestUserPassword();

		Response response;
		try {
			byte[] encryptedEntity = encryptionService.encrypt(objectMapper.writeValueAsBytes(entity), targetServerAccessData.getId());
			response = sormasToSormasRestClient.post(
				targetServerAccessData.getHostName(),
				endpoint,
				"Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8),
				new SormasToSormasEncryptedDataDto(serverAccessData.getId(), encryptedEntity));
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
			try {
				SormasToSormasErrorResponse errorResponse = objectMapper.readValue(errorMessage, SormasToSormasErrorResponse.class);
				errorMessage = errorResponse.getMessage();
			} catch (IOException e) {
				// do nothing, keep the unparsed response as error message
			}

			if (statusCode != HttpStatus.SC_BAD_REQUEST) {
				// don't log validation errors, will be displayed on the UI
				LOGGER.error("Share case failed: {}; {}", statusCode, errorMessage);
			}

			throw new SormasToSormasException(errorMessage);
		}
	}

	private <T> T decryptSharedData(SormasToSormasEncryptedDataDto encryptedData, Class<T> dataType) throws SormasToSormasException {
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
}
