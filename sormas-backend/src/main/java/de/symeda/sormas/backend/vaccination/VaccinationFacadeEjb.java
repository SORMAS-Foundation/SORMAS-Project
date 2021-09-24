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

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.api.vaccination.VaccinationFacade;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourseFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationService;
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
	private ImmunizationService immunizationService;
	@EJB
	private ClinicalCourseFacadeEjb.ClinicalCourseFacadeEjbLocal clinicalCourseFacade;
	@EJB
	private VaccinationService vaccinationService;

	public VaccinationDto save(@Valid VaccinationDto dto) {

		Vaccination existingVaccination = dto.getUuid() != null ? vaccinationService.getByUuid(dto.getUuid()) : null;
		VaccinationDto existingDto = toDto(existingVaccination);

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		restorePseudonymizedDto(dto, existingDto, existingVaccination, pseudonymizer);

		validate(dto);

		existingVaccination = fillOrBuildEntity(dto, existingVaccination, true);
		vaccinationService.ensurePersisted(existingVaccination);

		return convertToDto(existingVaccination, pseudonymizer);
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

	public void validate(VaccinationDto vaccinationDto) {

		if (vaccinationDto.getImmunization() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validImmunization));
		}
		if (vaccinationDto.getHealthConditions() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validHealthConditions));
		}
		if (vaccinationDto.getReportDate() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validReportDateTime));
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
