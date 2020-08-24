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
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sormastosormas.HealthDepartmentServerAccessData;
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

	private static final List<HealthDepartmentServerAccessData> MOCK_HEALTH_DEPARTMENTS = new ArrayList<>(2);
	static {
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

		PersonDto personDto = getPersonDto(caze.getPerson(), pseudonymizer);
		CaseDataDto cazeDto = getCazeDto(caze, pseudonymizer);
		cazeDto.setSormasToSormasSource(createSormasToSormasSource(currentUser));

		sendEntityToSormas(new SormasToSormasCaseDto(personDto, cazeDto), SormasToSormasApiConstants.SAVE_SHARED_CASE_ENDPOINT, options);

		saveCaseShareInfo(cazeDto.toReference(), options.getHealthDepartment().getId(), currentUser.toReference());
	}

	@Override
	public void shareContact(String uuid, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		Contact contact = contactService.getByUuid(uuid);

		Pseudonymizer pseudonymizer = createPseudonymizer(options);

		PersonDto personDto = getPersonDto(contact.getPerson(), pseudonymizer);
		ContactDto contactDto = getContactDto(contact, pseudonymizer);
		contactDto.setSormasToSormasSource(createSormasToSormasSource(currentUser));

		sendEntityToSormas(new SormasToSormasContactDto(personDto, contactDto), SormasToSormasApiConstants.SAVE_SHARED_CONTACT_ENDPOINT, options);

		saveContactShareInfo(contact.toReference(), options.getHealthDepartment().getId(), currentUser.toReference());
	}

	@Override
	public List<HealthDepartmentServerAccessData> getAvailableHealthDepartments() {
		return MOCK_HEALTH_DEPARTMENTS;
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

		return resultList.stream().map(this::toDto).collect(Collectors.toList());
	}

	private PersonDto getPersonDto(Person person, Pseudonymizer pseudonymizer) {
		PersonDto personDto = personFacade.convertToDto(person, pseudonymizer, true);
		personDto.setFirstName(I18nProperties.getCaption(Captions.inaccessibleValue));
		personDto.setLastName(I18nProperties.getCaption(Captions.inaccessibleValue));

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

	private void saveCaseShareInfo(CaseReferenceDto caseReference, String healthDepartmentId, UserReferenceDto sender) {
		saveShareInfo(SormasToSormasShareInfoDto.build(caseReference, healthDepartmentId, sender));
	}

	private void saveContactShareInfo(ContactReferenceDto contactReference, String healthDepartment, UserReferenceDto sender) {
		saveShareInfo(SormasToSormasShareInfoDto.build(contactReference, healthDepartment, sender));
	}

	private void saveShareInfo(SormasToSormasShareInfoDto source) {
		SormasToSormasShareInfo target = new SormasToSormasShareInfo();

		target.setUuid(source.getUuid());
		target.setCreationDate(new Timestamp(new Date().getTime()));
		target.setCaze(caseService.getByReferenceDto(source.getCaze()));
		target.setContact(contactService.getByReferenceDto(source.getContact()));
		target.setHealthDepartment(source.getHealthDepartmentId());
		target.setSender(userService.getByReferenceDto(source.getSender()));

		sormasToSormasShareInfoService.ensurePersisted(target);
	}

	private void processCaseData(CaseDataDto caze, PersonDto person) {
		processPerson(person);

		caze.setPerson(person.toReference());
		caze.setReportingUser(userService.getCurrentUser().toReference());

		RegionReferenceDto region = mapRegion(caze.getRegion());
		caze.setRegion(region);

		DistrictReferenceDto district = mapDistrict(caze.getDistrict(), region);
		caze.setDistrict(district);

		CommunityReferenceDto community = mapCommunity(caze.getCommunity(), district);
		caze.setCommunity(community);

		caze.setHealthFacility(mapHealthFacility(caze.getHealthFacility(), district, community, caze.getFacilityType()));

		if (caze.getPointOfEntry() != null) {
			caze.setPointOfEntry(
				pointOfEntryFacade.getByName(caze.getPointOfEntry().getCaption(), district, false).stream().findFirst().orElse(null));
		}

		// init uuids
		caze.getSormasToSormasSource().setUuid(DataHelper.createUuid());

		if (caze.getHospitalization() != null) {
			caze.getHospitalization().setUuid(DataHelper.createUuid());
			caze.getHospitalization().getPreviousHospitalizations().forEach(ph -> {
				ph.setUuid(DataHelper.createUuid());

				RegionReferenceDto phRegion = mapRegion(ph.getRegion());
				ph.setRegion(phRegion);

				DistrictReferenceDto phDistrict = mapDistrict(ph.getDistrict(), phRegion);
				ph.setDistrict(phDistrict);

				CommunityReferenceDto phCommunity = mapCommunity(ph.getCommunity(), phDistrict);
				ph.setCommunity(phCommunity);

				ph.setHealthFacility(mapHealthFacility(ph.getHealthFacility(), phDistrict, phCommunity, FacilityType.HOSPITAL));
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
				caze.getClinicalCourse().getHealthConditions().setUuid(DataHelper.createUuid());
			}
		}

		if (caze.getMaternalHistory() != null) {
			caze.getMaternalHistory().setUuid(DataHelper.createUuid());

			RegionReferenceDto rasExposureRegion = null;
			if (caze.getMaternalHistory().getRashExposureRegion() != null) {
				rasExposureRegion = mapRegion(caze.getMaternalHistory().getRashExposureRegion());
				caze.getMaternalHistory().setRashExposureRegion(rasExposureRegion);
			}

			DistrictReferenceDto rasExposureDistrict = null;
			if (caze.getMaternalHistory().getRashExposureDistrict() != null) {
				rasExposureDistrict = mapDistrict(caze.getMaternalHistory().getRashExposureDistrict(), rasExposureRegion);
				caze.getMaternalHistory().setRashExposureDistrict(rasExposureDistrict);
			}

			if (caze.getMaternalHistory().getRashExposureCommunity() != null) {
				CommunityReferenceDto rashExposureCommunity = mapCommunity(caze.getMaternalHistory().getRashExposureCommunity(), rasExposureDistrict);
				caze.getMaternalHistory().setRashExposureCommunity(rashExposureCommunity);
			}
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

	private void processContactData(ContactDto contact, PersonDto person) {
		processPerson(person);

		contact.setPerson(person.toReference());
		contact.setReportingUser(userService.getCurrentUser().toReference());

		RegionReferenceDto region = mapRegion(contact.getRegion());
		contact.setRegion(region);

		DistrictReferenceDto district = mapDistrict(contact.getDistrict(), region);
		contact.setDistrict(district);

		CommunityReferenceDto community = mapCommunity(contact.getCommunity(), district);
		contact.setCommunity(community);

		// init uuids
		contact.getSormasToSormasSource().setUuid(DataHelper.createUuid());

		if (contact.getEpiData() != null) {
			processEpiData(contact.getEpiData());
		}
	}

	private void processPerson(PersonDto person) {
		person.setUuid(DataHelper.createUuid());

		LocationDto address = person.getAddress();
		address.setUuid(DataHelper.createUuid());

		RegionReferenceDto addressRegion = null;
		if (address.getRegion() != null) {
			addressRegion = mapRegion(address.getRegion());
			address.setRegion(addressRegion);
		}

		DistrictReferenceDto addressDistrict = null;
		if (address.getDistrict() != null) {
			addressDistrict = mapDistrict(address.getDistrict(), addressRegion);
			address.setDistrict(addressDistrict);
		}

		address.setCommunity(mapCommunity(address.getCommunity(), addressDistrict));
	}

	private FacilityReferenceDto mapHealthFacility(
		FacilityReferenceDto facility,
		DistrictReferenceDto district,
		CommunityReferenceDto community,
		FacilityType facilityType) {
		if (facility == null) {
			return null;
		}

		return facilityFacade.getByNameAndType(facility.getCaption(), district, community, facilityType, false).stream().findFirst().orElse(null);
	}

	private CommunityReferenceDto mapCommunity(CommunityReferenceDto community, DistrictReferenceDto district) {
		if (community == null) {
			return null;
		}

		return communityFacade.getByName(community.getCaption(), district, false).stream().findFirst().orElse(null);
	}

	private DistrictReferenceDto mapDistrict(DistrictReferenceDto district, RegionReferenceDto region) {
		if (district == null) {
			return null;
		}

		return districtFacade.getByName(district.getCaption(), region, false).stream().findFirst().orElse(null);
	}

	private RegionReferenceDto mapRegion(RegionReferenceDto region) {
		if (region == null) {
			return null;
		}

		return regionFacade.getByName(region.getCaption(), false).stream().findFirst().orElse(null);
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

	private void validateSource(SormasToSormasSourceDto shareInfo) {
		if (shareInfo == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasShareInfoMissing));
		}

		if (DataHelper.isNullOrEmpty(shareInfo.getHealthDepartment())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasSenderHealthDepartmentMissing));
		}

		if (DataHelper.isNullOrEmpty(shareInfo.getSenderName())) {
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
			"Health Dep One",
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
			MOCK_HEALTH_DEPARTMENTS.stream().filter(hd -> hd.getId().equals(options.getHealthDepartment().getId())).findFirst().orElse(null);

		Response response;
		try {
			response = ClientBuilder.newBuilder()
				.build()
				.target(target.getUrl() + endpoint)
				.request()
				.header("Authorization", "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes())))
				.post(Entity.entity(mapper.writeValueAsString(entity), MediaType.APPLICATION_JSON_TYPE));
		} catch (JsonProcessingException e) {
			LOGGER.error("Unable to send data sormas", e);
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasSend));
		} catch (ResponseProcessingException e) {
			LOGGER.error("Unable to process sormas response", e);
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasResult));
		} catch (ProcessingException e) {
			LOGGER.error("Unable to send data sormas", e);

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

	public SormasToSormasSource fromDto(SormasToSormasSourceDto source) {
		if (source == null) {
			return null;
		}

		SormasToSormasSource target = sormasToSormasSourceService.getByUuid(source.getUuid());
		if (target == null) {
			target = new SormasToSormasSource();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setHealthDepartment(source.getHealthDepartment());
		target.setSenderName(source.getSenderName());
		target.setSenderEmail(source.getSenderEmail());
		target.setSenderPhoneNumber(source.getSenderPhoneNumber());

		return target;
	}

	public static SormasToSormasSourceDto toDto(SormasToSormasSource source) {
		if (source == null) {
			return null;
		}

		SormasToSormasSourceDto target = new SormasToSormasSourceDto();

		target.setCreationDate(source.getCreationDate());
		target.setChangeDate(source.getChangeDate());
		target.setUuid(source.getUuid());

		target.setHealthDepartment(source.getHealthDepartment());
		target.setSenderName(source.getSenderName());
		target.setSenderEmail(source.getSenderEmail());
		target.setSenderPhoneNumber(source.getSenderPhoneNumber());

		return target;
	}

	public SormasToSormasShareInfoDto toDto(SormasToSormasShareInfo source) {
		SormasToSormasShareInfoDto target = new SormasToSormasShareInfoDto();

		if (source.getCaze() != null) {
			target.setCaze(source.getCaze().toReference());
		}

		if (source.getContact() != null) {
			target.setContact(source.getContact().toReference());
		}

		target.setHealthDepartmentId(source.getHealthDepartment());
		target.setSender(source.getSender().toReference());

		return target;
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasFacadeEjbLocal extends SormasToSormasFacadeEjb {

	}
}
