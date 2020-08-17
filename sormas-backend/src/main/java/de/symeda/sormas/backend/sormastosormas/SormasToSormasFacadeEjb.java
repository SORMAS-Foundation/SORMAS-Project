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

import java.sql.Timestamp;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasFacade;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "SormasToSormasFacade")
public class SormasToSormasFacadeEjb implements SormasToSormasFacade {

	@EJB
	private SormasToSormasService service;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private ContactFacadeEjbLocal contactFacade;
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

	private void processCaseData(CaseDataDto caze, PersonDto person) {
		processPerson(person);

		caze.setPerson(person.toReference());
		caze.setReportingUser(userService.getCurrentUser().toReference());

		RegionReferenceDto region = mapRegion(caze.getRegion());
		caze.setRegion(region);

		DistrictReferenceDto district = mapDistrict(caze.getDistrict(), region);
		caze.setDistrict(district);

		CommunityReferenceDto community = null;
		if (caze.getCommunity() != null) {
			community = mapCommunity(caze.getCommunity(), district);
			caze.setCommunity(community);
		}

		if (caze.getHealthFacility() != null) {
			caze.setHealthFacility(
				facilityFacade.getByName(caze.getHealthFacility().getCaption(), district, community, false).stream().findFirst().orElse(null));
		}

		if (caze.getPointOfEntry() != null) {
			caze.setPointOfEntry(
				pointOfEntryFacade.getByName(caze.getPointOfEntry().getCaption(), district, false).stream().findFirst().orElse(null));
		}

		if (caze.getHospitalization() != null) {
			caze.getHospitalization().setUuid(DataHelper.createUuid());
			caze.getHospitalization().getPreviousHospitalizations().forEach(ph -> ph.setUuid(DataHelper.createUuid()));
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

		if (contact.getCommunity() != null) {
			CommunityReferenceDto community = mapCommunity(contact.getCommunity(), district);
			contact.setCommunity(community);
		}

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

		if (address.getCommunity() != null) {
			address.setCommunity(mapCommunity(address.getCommunity(), addressDistrict));
		}
	}

	private CommunityReferenceDto mapCommunity(CommunityReferenceDto community, DistrictReferenceDto district) {
		return communityFacade.getByName(community.getCaption(), district, false).stream().findFirst().orElse(null);
	}

	private DistrictReferenceDto mapDistrict(DistrictReferenceDto district, RegionReferenceDto region) {
		return districtFacade.getByName(district.getCaption(), region, false).stream().findFirst().orElse(null);
	}

	private RegionReferenceDto mapRegion(RegionReferenceDto region) {
		return regionFacade.getByName(region.getCaption(), false).stream().findFirst().orElse(null);
	}

	private void validateCase(CaseDataDto caze) throws ValidationRuntimeException {
		if (caseFacade.exists(caze.getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasCaseExists));
		}

		validateShareInfo(caze.getSormasShareInfo());
	}

	private void validateContact(ContactDto contact) throws ValidationRuntimeException {
		if (contactFacade.exists(contact.getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasContactExists));
		}

		if (contact.getCaze() != null && !contactFacade.exists(contact.getCaze().getUuid())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasContactCaseNotExists));
		}

		validateShareInfo(contact.getSormasShareInfo());
	}

	private void validateShareInfo(SormasToSormasShareInfoDto shareInfo) {
		if (shareInfo == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasShareInfoMissing));
		}

		if (DataHelper.isNullOrEmpty(shareInfo.getSenderHealthDepartment())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasSenderHealthDepartmentMissing));
		}

		if (DataHelper.isNullOrEmpty(shareInfo.getSenderName())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.sormasToSormasSenderNameMissing));
		}
	}

	public SormasToSormasShareInfo fromDto(SormasToSormasShareInfoDto source) {
		if (source == null) {
			return null;
		}

		SormasToSormasShareInfo target = service.getByUuid(source.getUuid());
		if (target == null) {
			target = new SormasToSormasShareInfo();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setSenderHealthDepartment(source.getSenderHealthDepartment());
		target.setSenderName(source.getSenderName());
		target.setSenderEmail(source.getSenderEmail());
		target.setSenderPhoneNumber(source.getSenderPhoneNumber());

		return target;
	}

	public static SormasToSormasShareInfoDto toDto(SormasToSormasShareInfo source) {
		if (source == null) {
			return null;
		}

		SormasToSormasShareInfoDto target = new SormasToSormasShareInfoDto();

		target.setCreationDate(source.getCreationDate());
		target.setChangeDate(source.getChangeDate());
		target.setUuid(source.getUuid());

		target.setSenderHealthDepartment(source.getSenderHealthDepartment());
		target.setSenderName(source.getSenderName());
		target.setSenderEmail(source.getSenderEmail());
		target.setSenderPhoneNumber(source.getSenderPhoneNumber());

		return target;
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasFacadeEjbLocal extends SormasToSormasFacadeEjb {

	}
}
