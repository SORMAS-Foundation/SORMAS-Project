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

import static de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
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
import de.symeda.sormas.api.sormastosormas.HealthDepartmentServerAccessData;
import de.symeda.sormas.api.sormastosormas.HealthDepartmentServerReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasErrorResponse;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasFacade;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSourceDto;
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

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	@EJB
	private SormasToSormasShareInfoService sormasToSormasShareInfoService;
	@EJB
	private SormasToSormasSourceService sormasToSormasSourceService;
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
	private SormasToSormasClient sormasToSormasClient;

	private static final List<HealthDepartmentServerAccessData> MOCK_HEALTH_DEPARTMENTS = new ArrayList<>(2);
	static {
		MOCK_HEALTH_DEPARTMENTS
			.add(new HealthDepartmentServerAccessData("healthDepMain", "Gesundheitsamt Hamburg", "http://localhost:8080/sormas-rest"));
		MOCK_HEALTH_DEPARTMENTS
			.add(new HealthDepartmentServerAccessData("healtsDep1", "Gesundheitsamt Charlottenburg (A)", "http://localhost:8080/sormas-rest"));
		MOCK_HEALTH_DEPARTMENTS
			.add(new HealthDepartmentServerAccessData("healtsDep2", "Gesundheitsamt Friedrichshain (B)", "http://localhost:8080/sormas-rest"));
	}

	@Override
	@Transactional
	public void saveSharedCase(SormasToSormasCaseDto shareCaseDto) {
		PersonDto person = shareCaseDto.getPerson();
		CaseDataDto caze = shareCaseDto.getCaze();

		validateCase(caze);

		processCaseData(caze, person);

		personFacade.savePerson(person);
		caseFacade.saveCase(caze);
	}

	@Override
	@Transactional
	public void saveSharedContact(SormasToSormasContactDto sharedContact) {
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
		cazeDto.setSormasToSormasSource(createSormasToSormasSource(currentUser));

		sendEntityToSormas(new SormasToSormasCaseDto(personDto, cazeDto), SormasToSormasApiConstants.SAVE_SHARED_CASE_ENDPOINT, options);

		saveCaseShareInfo(cazeDto.toReference(), options.getHealthDepartment().toReferenceDto(), currentUser.toReference());
	}

	@Override
	public void shareContact(String uuid, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		Contact contact = contactService.getByUuid(uuid);

		Pseudonymizer pseudonymizer = createPseudonymizer(options);

		PersonDto personDto = getPersonDto(contact.getPerson(), pseudonymizer, options);
		ContactDto contactDto = getContactDto(contact, pseudonymizer);
		contactDto.setSormasToSormasSource(createSormasToSormasSource(currentUser));

		sendEntityToSormas(new SormasToSormasContactDto(personDto, contactDto), SormasToSormasApiConstants.SAVE_SHARED_CONTACT_ENDPOINT, options);

		saveContactShareInfo(contact.toReference(), options.getHealthDepartment().toReferenceDto(), currentUser.toReference());
	}

	@Override
	public List<HealthDepartmentServerAccessData> getAvailableHealthDepartments() {
		return new ArrayList<>(MOCK_HEALTH_DEPARTMENTS.subList(1, MOCK_HEALTH_DEPARTMENTS.size()));
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

	private void saveCaseShareInfo(CaseReferenceDto caseReference, HealthDepartmentServerReferenceDto healthDepartment, UserReferenceDto sender) {
		saveShareInfo(SormasToSormasShareInfoDto.build(caseReference, healthDepartment, sender));
	}

	private void saveContactShareInfo(
		ContactReferenceDto contactReference,
		HealthDepartmentServerReferenceDto healthDepartment,
		UserReferenceDto sender) {
		saveShareInfo(SormasToSormasShareInfoDto.build(contactReference, healthDepartment, sender));
	}

	private void saveShareInfo(SormasToSormasShareInfoDto source) {
		SormasToSormasShareInfo target = new SormasToSormasShareInfo();

		target.setUuid(source.getUuid());
		target.setCreationDate(new Timestamp(new Date().getTime()));
		target.setCaze(caseService.getByReferenceDto(source.getCaze()));
		target.setContact(contactService.getByReferenceDto(source.getContact()));
		target.setHealthDepartment(source.getHealthDepartment().getUuid());
		target.setSender(userService.getByReferenceDto(source.getSender()));

		sormasToSormasShareInfoService.ensurePersisted(target);
	}

	private void processCaseData(CaseDataDto caze, PersonDto person) {
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

		// init uuids
		caze.getSormasToSormasSource().setUuid(DataHelper.createUuid());

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
		contact.getSormasToSormasSource().setUuid(DataHelper.createUuid());

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

		validateSource(caze.getSormasToSormasSource());
	}

	private void validateContact(ContactDto contact) throws ValidationRuntimeException {
		if (contactFacade.exists(contact.getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasContactExists));
		}

		if (contact.getCaze() != null && !contactFacade.exists(contact.getCaze().getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasContactCaseNotExists));
		}

		validateSource(contact.getSormasToSormasSource());
	}

	private void validateSource(SormasToSormasSourceDto source) {
		if (source == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasShareInfoMissing));
		}

		if (source.getHealthDepartment() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasSenderHealthDepartmentMissing));
		}

		if (DataHelper.isNullOrEmpty(source.getSenderName())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasSenderNameMissing));
		}
	}

	private Pseudonymizer createPseudonymizer(SormasToSormasOptionsDto options) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefaultNoCheckers(false);

		if (options.isPseudonymizePersonalData()) {
			pseudonymizer.addFieldAccessChecker(PersonalDataFieldAccessChecker.forcedNoAccess());
		}
		if (options.isPseudonymizeSensitiveData()) {
			pseudonymizer.addFieldAccessChecker(SensitiveDataFieldAccessChecker.forcedNoAccess());
		}

		return pseudonymizer;
	}

	private SormasToSormasSourceDto createSormasToSormasSource(User currentUser) {
		return new SormasToSormasSourceDto(
			new HealthDepartmentServerReferenceDto("healthDepMain", "Gesundheitsamt Hamburg"),
			String.format("%s %s", currentUser.getFirstName(), currentUser),
			currentUser.getUserEmail(),
			currentUser.getPhone());
	}

	private void sendEntityToSormas(Object entity, String endpoint, SormasToSormasOptionsDto options) throws SormasToSormasException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

		String userCredentials = StartupShutdownService.SORMAS_TO_SORMAS_USER_NAME + ":" + "sormas2SORMAS";

		HealthDepartmentServerAccessData target =
			getHealthDepartmentServerAccessData(options.getHealthDepartment().getId()).filter(ad -> !StringUtils.isEmpty(ad.getUrl()))
				.orElseThrow(() -> new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));

		Response response;
		try {
			response =
				sormasToSormasClient.post(target.getUrl() + endpoint, new String(Base64.getEncoder().encode(userCredentials.getBytes())), entity);
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
		if (statusCode < 200 || statusCode >= 400) {
			String errorMessage = response.readEntity(String.class);
			try {
				SormasToSormasErrorResponse errorResponse = mapper.readValue(errorMessage, SormasToSormasErrorResponse.class);
				errorMessage = errorResponse.getMessage();
			} catch (IOException e) {
				// do nothing, keep the unparsed response as error message
			}

			LOGGER.error("Share case failed: {}; {}", statusCode, errorMessage);
			throw new SormasToSormasException(errorMessage);
		}
	}

	private static Optional<HealthDepartmentServerAccessData> getHealthDepartmentServerAccessData(String id) {
		return MOCK_HEALTH_DEPARTMENTS.stream().filter(hd -> hd.getId().equals(id)).findFirst();
	}

	public static SormasToSormasSourceDto toSormasTsoSormasSourceDto(SormasToSormasSource source) {
		if (source == null) {
			return null;
		}

		SormasToSormasSourceDto target = new SormasToSormasSourceDto();

		DtoHelper.fillDto(target, source);

		HealthDepartmentServerAccessData serverAccessData = getHealthDepartmentServerAccessData(source.getHealthDepartment())
			.orElseGet(() -> new HealthDepartmentServerAccessData(source.getHealthDepartment(), source.getHealthDepartment(), null));
		target.setHealthDepartment(new HealthDepartmentServerReferenceDto(serverAccessData.getId(), serverAccessData.getName()));

		target.setSenderName(source.getSenderName());
		target.setSenderEmail(source.getSenderEmail());
		target.setSenderPhoneNumber(source.getSenderPhoneNumber());

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

		HealthDepartmentServerAccessData serverAccessData = getHealthDepartmentServerAccessData(source.getHealthDepartment())
			.orElseGet(() -> new HealthDepartmentServerAccessData(source.getHealthDepartment(), source.getHealthDepartment(), null));
		target.setHealthDepartment(new HealthDepartmentServerReferenceDto(serverAccessData.getId(), serverAccessData.getName()));

		target.setSender(source.getSender().toReference());

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
