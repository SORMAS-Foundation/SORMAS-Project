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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.JsonNode;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.api.vaccination.VaccinationFacade;
import de.symeda.sormas.api.vaccination.VaccinationListCriteria;
import de.symeda.sormas.api.vaccination.VaccinationListEntryDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourseFacadeEjb;
import de.symeda.sormas.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.backend.clinicalcourse.HealthConditionsMapper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
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
import de.symeda.sormas.backend.util.PatchHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless(name = "VaccinationFacade")
@RolesAllowed(UserRight._IMMUNIZATION_VIEW)
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
	@EJB
	private HealthConditionsMapper healthConditionsMapper;

	@RolesAllowed({UserRight._IMMUNIZATION_CREATE, UserRight._IMMUNIZATION_EDIT})
	public VaccinationDto save(@Valid VaccinationDto dto) {

		Vaccination existingVaccination = dto.getUuid() != null ? vaccinationService.getByUuid(dto.getUuid()) : null;
		VaccinationDto existingDto = toDto(existingVaccination);
		Date oldVaccinationDate = existingVaccination != null ? existingVaccination.getVaccinationDate() : null;

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		restorePseudonymizedDto(dto, existingDto, existingVaccination, pseudonymizer);

		validate(dto, false);

		existingVaccination = fillOrBuildEntity(dto, existingVaccination, true);
		vaccinationService.ensurePersisted(existingVaccination);

		updateVaccinationStatuses(
			existingVaccination.getVaccinationDate(),
			oldVaccinationDate,
			existingVaccination.getImmunization().getPerson().getId(),
			existingVaccination.getImmunization().getDisease());

		return convertToDto(existingVaccination, pseudonymizer);
	}

	@Override
	@RolesAllowed({
		UserRight._IMMUNIZATION_CREATE })
	public VaccinationDto createWithImmunization(
		VaccinationDto dto,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		PersonReferenceDto person,
		Disease disease) {

		if (dto.getImmunization() != null) {
			throw new IllegalArgumentException("VaccinationDto already has an immunization assigned");
		}

		if (dto.getUuid() != null && vaccinationService.getByUuid(dto.getUuid()) != null) {
			throw new IllegalArgumentException("VaccinationDto already has a UUID");
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
			immunizationDto.setImmunizationStatus(ImmunizationStatus.ACQUIRED);
			immunizationDto.setImmunizationManagementStatus(ImmunizationManagementStatus.COMPLETED);
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

	@RolesAllowed(UserRight._IMMUNIZATION_EDIT)
	private boolean addImmunizationToVaccination(Vaccination vaccination, String personUuid, Disease disease) {

		List<Immunization> immunizations = immunizationService.getByPersonAndDisease(personUuid, disease, true);

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

		List<Immunization> immunizations = immunizationService.getByPersonAndDisease(personUuid, disease, false);
		return immunizations.stream().flatMap(i -> i.getVaccinations().stream()).map(v -> toDto(v)).collect(Collectors.toList());
	}

	@Override
	public List<VaccinationListEntryDto> getEntriesList(
		VaccinationListCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {
		List<Vaccination> vaccinationsList = vaccinationService.getVaccinationsByCriteria(criteria, first, max, sortProperties);
		List<VaccinationListEntryDto> entriesList =
			vaccinationsList.stream().map((v) -> toVaccinationListEntryDto(v, true, "")).collect(Collectors.toList());
		return entriesList;
	}

	private VaccinationListEntryDto toVaccinationListEntryDto(Vaccination vaccination, boolean relevant, String message) {
		VaccinationListEntryDto dto = new VaccinationListEntryDto();
		dto.setUuid(vaccination.getUuid());
		dto.setDisease(vaccination.getImmunization().getDisease());
		dto.setVaccinationDate(vaccination.getVaccinationDate());
		dto.setVaccineName(vaccination.getVaccineName());
		dto.setOtherVaccineName(vaccination.getOtherVaccineName());

		dto.setRelevant(relevant);
		dto.setNonRelevantMessage(message);
		return dto;
	}

	@Override
	public List<VaccinationListEntryDto> getEntriesListWithRelevance(
		CaseReferenceDto caseReferenceDto,
		VaccinationListCriteria criteria,
		Integer first,
		Integer max) {
		List<Vaccination> vaccinationsList = vaccinationService.getVaccinationsByCriteria(criteria, first, max, null);
		Case caze = caseService.getByReferenceDto(caseReferenceDto);

		return vaccinationsList.stream()
			.map(
				v -> toVaccinationListEntryDto(
					v,
					vaccinationService.isVaccinationRelevant(caze, v),
					I18nProperties.getString(
						v.getVaccinationDate() != null
							? Strings.messageVaccinationNotRelevantForCase
							: Strings.messageVaccinationNoDateNotRelevantForCase)))
			.collect(Collectors.toList());
	}

	@Override
	public List<VaccinationListEntryDto> getEntriesListWithRelevance(
		ContactReferenceDto contactReferenceDto,
		VaccinationListCriteria criteria,
		Integer first,
		Integer max) {
		List<Vaccination> vaccinationsList = vaccinationService.getVaccinationsByCriteria(criteria, first, max, null);
		Contact contact = contactService.getByReferenceDto(contactReferenceDto);

		return vaccinationsList.stream()
			.map(
				v -> toVaccinationListEntryDto(
					v,
					vaccinationService.isVaccinationRelevant(contact, v),
					I18nProperties.getString(
						v.getVaccinationDate() != null
							? Strings.messageVaccinationNotRelevantForContact
							: Strings.messageVaccinationNoDateNotRelevantForContact)))
			.collect(Collectors.toList());
	}

	@Override
	public List<VaccinationListEntryDto> getEntriesListWithRelevance(
		EventParticipantReferenceDto eventParticipantReferenceDto,
		VaccinationListCriteria criteria,
		Integer first,
		Integer max) {
		List<Vaccination> vaccinationsList = vaccinationService.getVaccinationsByCriteria(criteria, first, max, null);
		EventParticipant eventParticipant = eventParticipantService.getByReferenceDto(eventParticipantReferenceDto);

		return vaccinationsList.stream()
			.map(
				v -> toVaccinationListEntryDto(
					v,
					vaccinationService.isVaccinationRelevant(eventParticipant.getEvent(), v),
					I18nProperties.getString(
						v.getVaccinationDate() != null
							? Strings.messageVaccinationNotRelevantForEventParticipant
							: Strings.messageVaccinationNoDateNotRelevantForEventParticipant)))
			.collect(Collectors.toList());
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

	@RolesAllowed(UserRight._IMMUNIZATION_EDIT)
	public void updateVaccinationStatuses(Date newVaccinationDate, Date oldVaccinationDate, Long personId, Disease disease) {

		if (oldVaccinationDate == null || newVaccinationDate != oldVaccinationDate) {
			caseService.updateVaccinationStatuses(personId, disease, newVaccinationDate);
			contactService.updateVaccinationStatuses(personId, disease, newVaccinationDate);
			eventParticipantService.updateVaccinationStatuses(personId, disease, newVaccinationDate);
		}
	}

	@RolesAllowed(UserRight._CASE_EDIT)
	public void updateVaccinationStatuses(Case caze) {
		List<Immunization> casePersonImmunizations = immunizationService.getByPersonAndDisease(caze.getPerson().getUuid(), caze.getDisease(), true);

		boolean hasValidVaccinations = casePersonImmunizations.stream()
			.anyMatch(
				immunization -> immunization.getVaccinations()
					.stream()
					.anyMatch(vaccination -> vaccinationService.isVaccinationRelevant(caze, vaccination)));

		if (hasValidVaccinations) {
			caze.setVaccinationStatus(VaccinationStatus.VACCINATED);
		}
	}

	@RolesAllowed(UserRight._CONTACT_EDIT)
	public void updateVaccinationStatuses(Contact contact) {
		List<Immunization> contactPersonImmunizations =
			immunizationService.getByPersonAndDisease(contact.getPerson().getUuid(), contact.getDisease(), true);

		boolean hasValidVaccinations = contactPersonImmunizations.stream()
			.anyMatch(
				immunization -> immunization.getVaccinations()
					.stream()
					.anyMatch(vaccination -> vaccinationService.isVaccinationRelevant(contact, vaccination)));

		if (hasValidVaccinations) {
			contact.setVaccinationStatus(VaccinationStatus.VACCINATED);
		}
	}

	@RolesAllowed(UserRight._EVENTPARTICIPANT_EDIT)
	public void updateVaccinationStatuses(EventParticipant eventParticipant) {
		if (eventParticipant.getEvent().getDisease() == null) {
			return;
		}
		List<Immunization> eventParticipantImmunizations =
			immunizationService.getByPersonAndDisease(eventParticipant.getPerson().getUuid(), eventParticipant.getEvent().getDisease(), true);
		Event event = eventParticipant.getEvent();

		boolean hasValidVaccinations = eventParticipantImmunizations.stream()
			.anyMatch(
				immunization -> immunization.getVaccinations()
					.stream()
					.anyMatch(vaccination -> vaccinationService.isVaccinationRelevant(event, vaccination)));

		if (hasValidVaccinations) {
			eventParticipant.setVaccinationStatus(VaccinationStatus.VACCINATED);
		}
	}

	@Override
	@RolesAllowed(UserRight._IMMUNIZATION_DELETE)
	public void deleteWithImmunization(String uuid) {
		Vaccination vaccination = vaccinationService.getByUuid(uuid);
		Immunization immunization = vaccination.getImmunization();
		immunization.getVaccinations().remove(vaccination);
		immunizationService.ensurePersisted(immunization);

		if (immunization.getVaccinations().isEmpty()) {
			immunizationService.delete(immunization);
		}
	}

	@Override
	public VaccinationDto getByUuid(String uuid) {
		return toDto(vaccinationService.getByUuid(uuid));
	}

    @RolesAllowed(UserRight._IMMUNIZATION_EDIT)
	public VaccinationDto postUpdate(String uuid, JsonNode vaccinationDtoJson) {
		VaccinationDto existingVaccinationDto = toDto(vaccinationService.getByUuid(uuid));
		PatchHelper.postUpdate(vaccinationDtoJson, existingVaccinationDto);
		return this.save(existingVaccinationDto);
	}

	@Override
	public Map<String, String> getLastVaccinationType() {
		return vaccinationService.getLastVaccinationType();
	}

	private Vaccination fillOrBuildEntity(@NotNull VaccinationDto source, Vaccination target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, Vaccination::new, checkChangeDate);

		target.setImmunization(immunizationService.getByReferenceDto(source.getImmunization()));
		target.setHealthConditions(healthConditionsMapper.fromDto(source.getHealthConditions(), checkChangeDate));
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

	public VaccinationDto toDto(Vaccination entity) {
		if (entity == null) {
			return null;
		}
		VaccinationDto dto = new VaccinationDto();
		DtoHelper.fillDto(dto, entity);

		dto.setImmunization(ImmunizationFacadeEjb.toReferenceDto(entity.getImmunization()));
		dto.setHealthConditions(healthConditionsMapper.toDto(entity.getHealthConditions()));
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

    @RolesAllowed(UserRight._IMMUNIZATION_EDIT)
	public void copyExistingVaccinationsToNewImmunization(ImmunizationDto immunizationDto, Immunization newImmunization) {
		List<Vaccination> vaccinationEntities = new ArrayList<>();
		for (VaccinationDto vaccinationDto : immunizationDto.getVaccinations()) {
			Vaccination vaccination = new Vaccination();
			vaccination.setUuid(DataHelper.createUuid());
			vaccination = fillOrBuildEntity(vaccinationDto, vaccination, false);

			HealthConditions healthConditions = new HealthConditions();
			healthConditions.setUuid(DataHelper.createUuid());
			healthConditions = healthConditionsMapper.fillOrBuildEntity(vaccinationDto.getHealthConditions(), healthConditions, false);
			vaccination.setHealthConditions(healthConditions);

			vaccination.setImmunization(newImmunization);
			vaccinationEntities.add(vaccination);
		}
		newImmunization.getVaccinations().clear();
		newImmunization.getVaccinations().addAll(vaccinationEntities);
	}

	public Map<String, VaccinationDto> getLatestByPersons(List<PersonReferenceDto> persons, Disease disease) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);

		return vaccinationService
			.getVaccinationsByCriteria(new VaccinationListCriteria.Builder(persons).withDisease(disease).build(), null, null, null)
			.stream()
			.collect(Collectors.toMap(v -> v.getImmunization().getPerson().getUuid(), v -> convertToDto(v, pseudonymizer), (v1, v2) -> {
				Date v1Date = v1.getVaccinationDate() != null ? v1.getVaccinationDate() : v1.getReportDate();
				Date v2Date = v2.getVaccinationDate() != null ? v2.getVaccinationDate() : v2.getReportDate();

				return v1Date.after(v2Date) ? v1 : v2;
			}));
	}

	@LocalBean
	@Stateless
	public static class VaccinationFacadeEjbLocal extends VaccinationFacadeEjb {

	}
}
