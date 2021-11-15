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

package de.symeda.sormas.backend.vaccination;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.api.vaccination.VaccinationFacade;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourseFacadeEjb;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.immunization.ImmunizationEntityHelper;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb.ImmunizationFacadeEjbLocal;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless(name = "VaccinationFacade")
public class VaccinationFacadeEjb implements VaccinationFacade {

	@EJB
	private UserService userService;
	@EJB
	private ImmunizationFacadeEjbLocal immunizationFacade;
	@EJB
	private ImmunizationService immunizationService;
	@EJB
	private ClinicalCourseFacadeEjb.ClinicalCourseFacadeEjbLocal clinicalCourseFacade;
	@EJB
	private VaccinationService vaccinationService;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventParticipantService eventParticipantService;

	public VaccinationDto save(@Valid VaccinationDto dto) {

		Vaccination existingVaccination = dto.getUuid() != null ? vaccinationService.getByUuid(dto.getUuid()) : null;
		VaccinationDto existingDto = toDto(existingVaccination);
		Date currentVaccinationDate = existingVaccination != null ? existingVaccination.getVaccinationDate() : null;

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		restorePseudonymizedDto(dto, existingDto, existingVaccination, pseudonymizer);

		validate(dto, false);

		existingVaccination = fillOrBuildEntity(dto, existingVaccination, true);
		vaccinationService.ensurePersisted(existingVaccination);

		updateVaccinationStatuses(
			existingVaccination.getVaccinationDate(),
			currentVaccinationDate,
			existingVaccination.getImmunization().getPerson().getId(),
			existingVaccination.getImmunization().getDisease());

		return convertToDto(existingVaccination, pseudonymizer);
	}

	@Override
	public VaccinationDto create(
		VaccinationDto dto,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		PersonReferenceDto person,
		Disease disease) {

		if (dto.getUuid() != null && vaccinationService.getByUuid(dto.getUuid()) != null) {
			throw new IllegalArgumentException("DTO already has a UUID");
		}

		validate(dto, true);

		Vaccination vaccination = null;
		vaccination = fillOrBuildEntity(dto, vaccination, true);
		boolean immunizationFound = addImmunizationToVaccination(vaccination, person.getUuid(), disease);

		if (!immunizationFound) {
			ImmunizationDto immunizationDto = ImmunizationDto.build(person);
			immunizationDto.setDisease(disease);
			immunizationDto.setResponsibleRegion(region);
			immunizationDto.setResponsibleDistrict(district);
			immunizationDto.setReportingUser(userService.getCurrentUser().toReference());
			immunizationDto.setMeansOfImmunization(MeansOfImmunization.VACCINATION);
			immunizationFacade.save(immunizationDto);

			Immunization immunization = immunizationService.getByUuid(immunizationDto.getUuid());
			vaccination.setImmunization(immunization);
		}

		if (vaccination.getImmunization() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validImmunization));
		}

		vaccinationService.ensurePersisted(vaccination);

		updateVaccinationStatuses(vaccination.getVaccinationDate(), null, vaccination.getImmunization().getPerson().getId(), disease);

		return convertToDto(vaccination, Pseudonymizer.getDefault(userService::hasRight));
	}

	private boolean addImmunizationToVaccination(Vaccination vaccination, String personUuid, Disease disease) {

		List<Immunization> immunizations = immunizationService.getByPersonAndDisease(personUuid, disease);

		if (immunizations.isEmpty()) {
			return false;
		}

		if (immunizations.size() == 1) {
			vaccination.setImmunization(immunizations.get(0));
			return true;
		}

		// Case 1: If the vaccination date is empty, add the vaccination to the latest immunization
		if (vaccination.getVaccinationDate() == null) {
			immunizations.sort(Comparator.comparing(i -> ImmunizationEntityHelper.getDateForComparison(i, true)));
			vaccination.setImmunization(immunizations.get(immunizations.size() - 1));
			return true;
		}

		// Case 2: Search for an immunization with start date < vaccination date < end date
		Optional<Immunization> immunization = immunizations.stream()
			.filter(
				i -> i.getStartDate() != null
					&& i.getEndDate() != null
					&& DateHelper.isBetween(vaccination.getVaccinationDate(), i.getStartDate(), i.getEndDate()))
			.findFirst();
		if (immunization.isPresent()) {
			vaccination.setImmunization(immunization.get());
			return true;
		}

		// Case 3: Search for the immunization with the nearest end or start date to the vaccination date
		immunization = immunizations.stream().filter(i -> i.getEndDate() != null || i.getStartDate() != null).min((i1, i2) -> {
			Integer i1Interval =
				Math.abs(DateHelper.getDaysBetween(i1.getEndDate() != null ? i1.getEndDate() : i1.getStartDate(), vaccination.getVaccinationDate()));
			Integer i2Interval =
				Math.abs(DateHelper.getDaysBetween(i2.getEndDate() != null ? i2.getEndDate() : i2.getStartDate(), vaccination.getVaccinationDate()));
			return i1Interval.compareTo(i2Interval);
		});
		if (immunization.isPresent()) {
			vaccination.setImmunization(immunization.get());
			return true;
		}

		// Case 4: Use the immunization with the nearest report date to the vaccination date
		immunization = immunizations.stream().min((i1, i2) -> {
			Integer i1Interval = Math.abs(DateHelper.getDaysBetween(i1.getReportDate(), vaccination.getVaccinationDate()));
			Integer i2Interval = Math.abs(DateHelper.getDaysBetween(i2.getReportDate(), vaccination.getVaccinationDate()));
			return i1Interval.compareTo(i2Interval);
		});
		if (immunization.isPresent()) {
			vaccination.setImmunization(immunization.get());
			return true;
		}

		return false;
	}

	@Override
	public List<VaccinationDto> getAllVaccinations(String personUuid, Disease disease) {

		List<Immunization> immunizations = immunizationService.getByPersonAndDisease(personUuid, disease);
		return immunizations.stream().flatMap(i -> i.getVaccinations().stream()).map(VaccinationFacadeEjb::toDto).collect(Collectors.toList());
	}

	public VaccinationDto convertToDto(Vaccination source, Pseudonymizer pseudonymizer) {

		VaccinationDto dto = toDto(source);

		pseudonymizeDto(source, dto, pseudonymizer);

		return dto;
	}

	private void pseudonymizeDto(Vaccination source, VaccinationDto dto, Pseudonymizer pseudonymizer) {

		if (dto != null) {
			boolean inJurisdiction = immunizationService.inJurisdictionOrOwned(source.getImmunization());
			pseudonymizer.pseudonymizeDto(VaccinationDto.class, dto, inJurisdiction, c -> {

				User currentUser = userService.getCurrentUser();
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), currentUser, dto::setReportingUser);
			});
		}
	}

	private void restorePseudonymizedDto(VaccinationDto dto, VaccinationDto existingDto, Vaccination vaccination, Pseudonymizer pseudonymizer) {

		if (existingDto != null) {
			final boolean inJurisdiction = immunizationService.inJurisdictionOrOwned(vaccination.getImmunization());
			final User currentUser = userService.getCurrentUser();
			pseudonymizer.restoreUser(vaccination.getReportingUser(), currentUser, dto, dto::setReportingUser);
			pseudonymizer.restorePseudonymizedValues(VaccinationDto.class, dto, existingDto, inJurisdiction);
		}
	}

	public void validate(VaccinationDto vaccinationDto, boolean allowEmptyImmunization) {

		if (!allowEmptyImmunization && vaccinationDto.getImmunization() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validImmunization));
		}
		if (vaccinationDto.getHealthConditions() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validHealthConditions));
		}
		if (vaccinationDto.getReportDate() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validReportDateTime));
		}
	}

	protected void updateVaccinationStatuses(Date newVaccinationDate, Date currentVaccinationDate, Long personId, Disease disease) {

		if (currentVaccinationDate == null || newVaccinationDate != currentVaccinationDate) {
			caseService.updateVaccinationStatuses(personId, disease, newVaccinationDate);
			contactService.updateVaccinationStatuses(personId, disease, newVaccinationDate);
			eventParticipantService.updateVaccinationStatuses(personId, disease, newVaccinationDate);
		}
	}

	@Override
	public Map<String, String> getLastVaccinationType() {
		return vaccinationService.getLastVaccinationType();
	}

	private Vaccination fillOrBuildEntity(@NotNull VaccinationDto source, Vaccination target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, Vaccination::new, checkChangeDate);

		target.setImmunization(immunizationService.getByReferenceDto(source.getImmunization()));
		target.setHealthConditions(clinicalCourseFacade.fromHealthConditionsDto(source.getHealthConditions(), checkChangeDate));
		target.setReportDate(source.getReportDate());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setVaccinationDate(source.getVaccinationDate());
		target.setVaccineName(source.getVaccineName());
		target.setOtherVaccineName(source.getOtherVaccineName());
		target.setVaccineManufacturer(source.getVaccineManufacturer());
		target.setOtherVaccineManufacturer(source.getOtherVaccineManufacturer());
		target.setVaccineType(source.getVaccineType());
		target.setVaccineDose(source.getVaccineDose());
		target.setVaccineInn(source.getVaccineInn());
		target.setVaccineBatchNumber(source.getVaccineBatchNumber());
		target.setVaccineUniiCode(source.getVaccineUniiCode());
		target.setVaccineAtcCode(source.getVaccineAtcCode());
		target.setVaccinationInfoSource(source.getVaccinationInfoSource());
		target.setPregnant(source.getPregnant());
		target.setTrimester(source.getTrimester());

		return target;
	}

	public static VaccinationDto toDto(Vaccination entity) {
		if (entity == null) {
			return null;
		}
		VaccinationDto dto = new VaccinationDto();
		DtoHelper.fillDto(dto, entity);

		dto.setImmunization(ImmunizationFacadeEjb.toReferenceDto(entity.getImmunization()));
		dto.setHealthConditions(ClinicalCourseFacadeEjb.toHealthConditionsDto(entity.getHealthConditions()));
		dto.setReportDate(entity.getReportDate());
		dto.setReportingUser(UserFacadeEjb.toReferenceDto(entity.getReportingUser()));
		dto.setVaccinationDate(entity.getVaccinationDate());
		dto.setVaccineName(entity.getVaccineName());
		dto.setOtherVaccineName(entity.getOtherVaccineName());
		dto.setVaccineManufacturer(entity.getVaccineManufacturer());
		dto.setOtherVaccineManufacturer(entity.getOtherVaccineManufacturer());
		dto.setVaccineType(entity.getVaccineType());
		dto.setVaccineDose(entity.getVaccineDose());
		dto.setVaccineInn(entity.getVaccineInn());
		dto.setVaccineBatchNumber(entity.getVaccineBatchNumber());
		dto.setVaccineUniiCode(entity.getVaccineUniiCode());
		dto.setVaccineAtcCode(entity.getVaccineAtcCode());
		dto.setVaccinationInfoSource(entity.getVaccinationInfoSource());
		dto.setPregnant(entity.getPregnant());
		dto.setTrimester(entity.getTrimester());

		return dto;
	}

	public Vaccination fromDto(@NotNull VaccinationDto source, boolean checkChangeDate) {
		return fillOrBuildEntity(source, vaccinationService.getByUuid(source.getUuid()), checkChangeDate);
	}

	@LocalBean
	@Stateless
	public static class VaccinationFacadeEjbLocal extends VaccinationFacadeEjb {

	}
}
