/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.common.DeletionDetails;
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
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.DtoCopyHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.fieldaccess.checkers.AnnotationBasedFieldAccessChecker.SpecialAccessCheck;
import de.symeda.sormas.api.vaccination.VaccinationCriteria;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.api.vaccination.VaccinationFacade;
import de.symeda.sormas.api.vaccination.VaccinationListEntryDto;
import de.symeda.sormas.api.vaccination.VaccinationReferenceDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourseFacadeEjb;
import de.symeda.sormas.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.backend.clinicalcourse.HealthConditionsMapper;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
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
import de.symeda.sormas.backend.specialcaseaccess.SpecialCaseAccessService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.PatchHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "VaccinationFacade")
@RightsAllowed(UserRight._IMMUNIZATION_VIEW)
public class VaccinationFacadeEjb
	extends AbstractBaseEjb<Vaccination, VaccinationDto, VaccinationListEntryDto, VaccinationReferenceDto, VaccinationService, VaccinationCriteria>
	implements VaccinationFacade {

	@EJB
	private ImmunizationFacadeEjbLocal immunizationFacade;
	@EJB
	private ImmunizationService immunizationService;
	@EJB
	private ClinicalCourseFacadeEjb.ClinicalCourseFacadeEjbLocal clinicalCourseFacade;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private VaccinationService vaccinationService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private HealthConditionsMapper healthConditionsMapper;
	@EJB
	private SpecialCaseAccessService specialCaseAccessService;

	public VaccinationFacadeEjb() {
	}

	@Inject
	protected VaccinationFacadeEjb(VaccinationService service) {
		super(Vaccination.class, VaccinationDto.class, service);
	}

	@RightsAllowed({
		UserRight._IMMUNIZATION_CREATE,
		UserRight._IMMUNIZATION_EDIT })
	public VaccinationDto save(VaccinationDto dto) {

		Vaccination existingVaccination = dto.getUuid() != null ? service.getByUuid(dto.getUuid()) : null;
		VaccinationDto existingDto = toDto(existingVaccination);

		Pseudonymizer<VaccinationDto> pseudonymizer = createPseudonymizer(existingVaccination);
		restorePseudonymizedDto(dto, existingDto, existingVaccination, pseudonymizer);

		validate(dto);

		Vaccination vaccination = fillOrBuildEntity(dto, existingVaccination, true);
		service.ensurePersisted(vaccination);

		updateVaccinationStatuses(
			vaccination,
			existingDto,
			vaccination.getImmunization().getPerson().getId(),
			vaccination.getImmunization().getDisease());

		immunizationFacade.onImmunizationChanged(vaccination.getImmunization(), true);

		return toPseudonymizedDto(vaccination, pseudonymizer);
	}

	@Override
	@RightsAllowed({
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

		if (dto.getUuid() != null && service.getByUuid(dto.getUuid()) != null) {
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

		service.ensurePersisted(vaccination);

		updateVaccinationStatuses(vaccination, null, vaccination.getImmunization().getPerson().getId(), disease);

		if (immunizationFound) {
			immunizationFacade.onImmunizationChanged(vaccination.getImmunization(), true);
		}

		return toPseudonymizedDto(vaccination);
	}

	@RightsAllowed(UserRight._IMMUNIZATION_EDIT)
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
		return toDtos(immunizationService.getByPersonAndDisease(personUuid, disease, false).stream().flatMap(i -> i.getVaccinations().stream()));
	}

	@Override
	public List<VaccinationDto> getVaccinationsByCriteria(
		VaccinationCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {
		return toDtos(service.getVaccinationsByCriteria(criteria, first, max, sortProperties).stream());
	}

	@Override
	public List<VaccinationListEntryDto> getEntriesList(VaccinationCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		List<Vaccination> vaccinationsList = service.getVaccinationsByCriteria(criteria, first, max, sortProperties);
		return vaccinationsList.stream().map(v -> toVaccinationListEntryDto(v, true, "")).collect(Collectors.toList());
	}

	@Override
	public void validate(VaccinationDto dto) throws ValidationRuntimeException {
		validate(dto, false);
	}

	private VaccinationListEntryDto toVaccinationListEntryDto(Vaccination vaccination, boolean relevant, String message) {
		VaccinationListEntryDto dto = new VaccinationListEntryDto(vaccination.getUuid());
		dto.setDisease(vaccination.getImmunization().getDisease());
		dto.setVaccinationDate(vaccination.getVaccinationDate());
		dto.setVaccineName(vaccination.getVaccineName());
		dto.setOtherVaccineName(vaccination.getOtherVaccineName());

		dto.setRelevant(relevant);
		dto.setNonRelevantMessage(message);
		return dto;
	}

	@Override
	public boolean isVaccinationRelevant(CaseDataDto cazeDto, VaccinationDto vaccinationDto) {

		Case caze = caseService.getByUuid(cazeDto.getUuid());
		Vaccination vaccination = service.getByUuid(vaccinationDto.getUuid());
		return service.isVaccinationRelevant(caze, vaccination);
	}

	@Override
	public long count(VaccinationCriteria criteria) {
		return service.count((cb, root) -> service.buildCriteriaFilter(criteria, cb, root));
	}

	@Override
	public List<VaccinationListEntryDto> getIndexList(VaccinationCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		List<Vaccination> vaccinationsList = service.getVaccinationsByCriteria(criteria, first, max, sortProperties);
		return vaccinationsList.stream().map(v -> toVaccinationListEntryDto(v, true, "")).collect(Collectors.toList());
	}

	@Override
	public List<VaccinationDto> getRelevantVaccinationsForCase(CaseDataDto cazeDto) {

		Case caze = caseService.getByUuid(cazeDto.getUuid());
		List<Vaccination> vaccinations = service.getRelevantVaccinationsForCase(caze);
		return toPseudonymizedDtos(vaccinations);
	}

	@Override
	public List<VaccinationListEntryDto> getEntriesListWithRelevance(
		CaseReferenceDto caseReferenceDto,
		VaccinationCriteria criteria,
		Integer first,
		Integer max) {

		List<Vaccination> vaccinationsList = service.getVaccinationsByCriteria(criteria, first, max, null);
		Case caze = caseService.getByReferenceDto(caseReferenceDto);

		return vaccinationsList.stream()
			.map(
				v -> toVaccinationListEntryDto(
					v,
					service.isVaccinationRelevant(caze, v),
					I18nProperties.getString(
						v.getVaccinationDate() != null
							? Strings.messageVaccinationNotRelevantForCase
							: Strings.messageVaccinationNoDateNotRelevantForCase)))
			.collect(Collectors.toList());
	}

	@Override
	public List<VaccinationListEntryDto> getEntriesListWithRelevance(
		ContactReferenceDto contactReferenceDto,
		VaccinationCriteria criteria,
		Integer first,
		Integer max) {
		List<Vaccination> vaccinationsList = service.getVaccinationsByCriteria(criteria, first, max, null);
		Contact contact = contactService.getByReferenceDto(contactReferenceDto);

		return vaccinationsList.stream()
			.map(
				v -> toVaccinationListEntryDto(
					v,
					service.isVaccinationRelevant(contact, v),
					I18nProperties.getString(
						v.getVaccinationDate() != null
							? Strings.messageVaccinationNotRelevantForContact
							: Strings.messageVaccinationNoDateNotRelevantForContact)))
			.collect(Collectors.toList());
	}

	@Override
	public List<VaccinationListEntryDto> getEntriesListWithRelevance(
		EventParticipantReferenceDto eventParticipantReferenceDto,
		VaccinationCriteria criteria,
		Integer first,
		Integer max) {
		List<Vaccination> vaccinationsList = service.getVaccinationsByCriteria(criteria, first, max, null);
		EventParticipant eventParticipant = eventParticipantService.getByReferenceDto(eventParticipantReferenceDto);

		return vaccinationsList.stream()
			.map(
				v -> toVaccinationListEntryDto(
					v,
					service.isVaccinationRelevant(eventParticipant.getEvent(), v),
					I18nProperties.getString(
						v.getVaccinationDate() != null
							? Strings.messageVaccinationNotRelevantForEventParticipant
							: Strings.messageVaccinationNoDateNotRelevantForEventParticipant)))
			.collect(Collectors.toList());
	}

	@Override
	protected Pseudonymizer<VaccinationDto> createPseudonymizer(List<Vaccination> vaccinations) {
		List<String> specialAccessUuids = specialCaseAccessService.getVaccinationUuidsWithSpecialAccess(vaccinations);

		SpecialAccessCheck<VaccinationDto> specialAccessCheck = t -> specialAccessUuids.contains(t.getUuid());

		return Pseudonymizer.getDefault(userService, specialAccessCheck);
	}

	@Override
	protected void pseudonymizeDto(Vaccination source, VaccinationDto dto, Pseudonymizer<VaccinationDto> pseudonymizer, boolean inJurisdiction) {

		if (dto != null) {
			pseudonymizer.pseudonymizeDto(VaccinationDto.class, dto, inJurisdiction, c -> {

				User currentUser = userService.getCurrentUser();
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), currentUser, dto::setReportingUser, dto);
			});
		}
	}

	@Override
	protected void restorePseudonymizedDto(
		VaccinationDto dto,
		VaccinationDto existingDto,
		Vaccination vaccination,
		Pseudonymizer<VaccinationDto> pseudonymizer) {

		if (existingDto != null) {
			final boolean inJurisdiction = service.inJurisdictionOrOwned(vaccination);
			final User currentUser = userService.getCurrentUser();
			pseudonymizer.restoreUser(vaccination.getReportingUser(), currentUser, dto, dto::setReportingUser);
			pseudonymizer.restorePseudonymizedValues(VaccinationDto.class, dto, existingDto, inJurisdiction);
		}
	}

	@Override
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

	@RightsAllowed(UserRight._IMMUNIZATION_EDIT)
	public void updateVaccinationStatuses(Vaccination newVaccination, VaccinationDto oldRelevantVaccination, Long personId, Disease disease) {

		Date newRelevantVaccineDate = service.getRelevantVaccineDate(newVaccination);
		Date oldRelevantVaccineDate = oldRelevantVaccination != null ? service.getRelevantVaccineDate(oldRelevantVaccination) : null;

		if (newRelevantVaccineDate != oldRelevantVaccineDate) {
			caseService.updateVaccinationStatuses(personId, disease, newVaccination);
			contactService.updateVaccinationStatuses(personId, disease, newRelevantVaccineDate);
			eventParticipantService.updateVaccinationStatuses(personId, disease, newRelevantVaccineDate);
		}
	}

	@RightsAllowed({
		UserRight._CASE_EDIT,
		UserRight._CASE_CREATE })
	public void updateVaccinationStatuses(Case caze) {

		List<Immunization> casePersonImmunizations = immunizationService.getByPersonAndDisease(caze.getPerson().getUuid(), caze.getDisease(), true);

		boolean hasValidVaccinations = casePersonImmunizations.stream()
			.anyMatch(
				immunization -> immunization.getVaccinations().stream().anyMatch(vaccination -> service.isVaccinationRelevant(caze, vaccination)));

		if (hasValidVaccinations) {
			caze.setVaccinationStatus(VaccinationStatus.VACCINATED);
		}
	}

	@RightsAllowed({
		UserRight._CONTACT_EDIT,
		UserRight._CONTACT_CREATE })
	public void updateVaccinationStatuses(Contact contact) {
		List<Immunization> contactPersonImmunizations =
			immunizationService.getByPersonAndDisease(contact.getPerson().getUuid(), contact.getDisease(), true);

		boolean hasValidVaccinations = contactPersonImmunizations.stream()
			.anyMatch(
				immunization -> immunization.getVaccinations().stream().anyMatch(vaccination -> service.isVaccinationRelevant(contact, vaccination)));

		if (hasValidVaccinations) {
			contact.setVaccinationStatus(VaccinationStatus.VACCINATED);
		}
	}

	@RightsAllowed({
		UserRight._EVENTPARTICIPANT_CREATE,
		UserRight._EVENTPARTICIPANT_EDIT })
	public void updateVaccinationStatuses(EventParticipant eventParticipant) {
		if (eventParticipant.getEvent().getDisease() == null) {
			return;
		}
		List<Immunization> eventParticipantImmunizations =
			immunizationService.getByPersonAndDisease(eventParticipant.getPerson().getUuid(), eventParticipant.getEvent().getDisease(), true);
		Event event = eventParticipant.getEvent();

		boolean hasValidVaccinations = eventParticipantImmunizations.stream()
			.anyMatch(
				immunization -> immunization.getVaccinations().stream().anyMatch(vaccination -> service.isVaccinationRelevant(event, vaccination)));

		if (hasValidVaccinations) {
			eventParticipant.setVaccinationStatus(VaccinationStatus.VACCINATED);
		}
	}

	@Override
	@RightsAllowed(UserRight._IMMUNIZATION_DELETE)
	public void deleteWithImmunization(String uuid, DeletionDetails deletionDetails) {
		Vaccination vaccination = service.getByUuid(uuid);

		if (!vaccinationService.inJurisdictionOrOwned(vaccination)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.messageVaccinationOutsideJurisdictionDeletionDenied));
		}

		Immunization immunization = vaccination.getImmunization();
		immunization.getVaccinations().remove(vaccination);
		immunizationService.incrementChangeDate(immunization);
		immunizationService.ensurePersisted(immunization);

		if (immunization.getVaccinations().isEmpty()) {
			immunizationService.delete(immunization, deletionDetails);
		}
	}

	@RightsAllowed(UserRight._IMMUNIZATION_EDIT)
	public VaccinationDto postUpdate(String uuid, JsonNode vaccinationDtoJson) {
		VaccinationDto existingVaccinationDto = toDto(service.getByUuid(uuid));
		PatchHelper.postUpdate(vaccinationDtoJson, existingVaccinationDto);
		return this.save(existingVaccinationDto);
	}

	@Override
	public Map<String, String> getLastVaccinationType() {
		return service.getLastVaccinationType();
	}

	public Vaccination fillOrBuildEntity(@NotNull VaccinationDto source, Vaccination target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, Vaccination::new, checkChangeDate);

		target.setImmunization(immunizationService.getByReferenceDto(source.getImmunization()));
		target.setHealthConditions(
			healthConditionsMapper.fillOrBuildEntity(source.getHealthConditions(), target.getHealthConditions(), checkChangeDate));
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

	@Override
	public VaccinationDto toDto(Vaccination entity) {
		return toVaccinationDto(entity);
	}

	public static VaccinationDto toVaccinationDto(Vaccination entity) {
		if (entity == null) {
			return null;
		}
		VaccinationDto dto = new VaccinationDto();
		DtoHelper.fillDto(dto, entity);

		dto.setImmunization(ImmunizationFacadeEjb.toReferenceDto(entity.getImmunization()));
		dto.setHealthConditions(HealthConditionsMapper.toDto(entity.getHealthConditions()));
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

	@Override
	protected VaccinationReferenceDto toRefDto(Vaccination entity) {
		if (entity == null) {
			return null;
		}
		return new VaccinationReferenceDto(entity.getUuid(), entity.toString());
	}

	@RightsAllowed(UserRight._IMMUNIZATION_EDIT)
	public void copyOrMergeVaccinations(ImmunizationDto immunizationDto, Immunization newImmunization, List<VaccinationDto> leadPersonVaccinations) {
		List<Vaccination> vaccinationEntities = new ArrayList<>();
		List<VaccinationDto> followPersonVaccinationWithoutDuplicates = getMergedVaccination(immunizationDto.getVaccinations());

		for (VaccinationDto vaccinationDto : followPersonVaccinationWithoutDuplicates) {
			List<VaccinationDto> duplicateLeadVaccinations = leadPersonVaccinations != null
				? leadPersonVaccinations.stream().filter(v -> isDuplicateOf(vaccinationDto, v)).collect(Collectors.toList())
				: new ArrayList<>();
			duplicateLeadVaccinations.sort(Comparator.comparing(EntityDto::getChangeDate).reversed());
			if (duplicateLeadVaccinations.isEmpty()) {
				Vaccination vaccination = fillOrBuildEntity(vaccinationDto, null, false);
				vaccination.setUuid(DataHelper.createUuid());

				HealthConditions healthConditions = healthConditionsMapper.fillOrBuildEntity(vaccinationDto.getHealthConditions(), null, false);
				healthConditions.setUuid(DataHelper.createUuid());
				vaccination.setHealthConditions(healthConditions);

				vaccination.setImmunization(newImmunization);
				vaccinationEntities.add(vaccination);
			} else {
				VaccinationDto duplicateVaccination = duplicateLeadVaccinations.get(0);
				VaccinationDto updatedVaccination = DtoCopyHelper.copyDtoValues(duplicateVaccination, vaccinationDto, false);
				save(updatedVaccination);
			}
		}
		newImmunization.getVaccinations().clear();
		newImmunization.getVaccinations().addAll(vaccinationEntities);
	}

	/**
	 * This method will return the list with vaccination for a person and merge all duplicates.
	 * The lead vaccine will be considered the latest vaccine changed.
	 * !! The list of merged vaccines is not persisted in DB
	 */
	private List<VaccinationDto> getMergedVaccination(List<VaccinationDto> vaccinationDtos) {
		if (vaccinationDtos == null || vaccinationDtos.isEmpty()) {
			return new ArrayList<>();
		}
		List<VaccinationDto> vaccinationsWithoutDuplicates = new ArrayList<>();
		for (VaccinationDto vaccinationDto : vaccinationDtos) {
			int duplicateIndex = 0;
			VaccinationDto duplicateVaccine = null;
			for (int i = 0; i < vaccinationsWithoutDuplicates.size(); i++) {
				if (isDuplicateOf(vaccinationDto, vaccinationsWithoutDuplicates.get(i))) {
					duplicateVaccine = vaccinationsWithoutDuplicates.get(i);
					duplicateIndex = i;
					break;
				}
			}

			if (duplicateVaccine == null) {
				vaccinationsWithoutDuplicates.add(vaccinationDto);
			} else {
				VaccinationDto targetVaccine;
				VaccinationDto sourceVaccine;
				if (duplicateVaccine.getChangeDate().after(vaccinationDto.getChangeDate())) {
					targetVaccine = duplicateVaccine;
					sourceVaccine = vaccinationDto;
				} else {
					targetVaccine = vaccinationDto;
					sourceVaccine = duplicateVaccine;
				}

				VaccinationDto updatedVaccination = DtoCopyHelper.copyDtoValues(targetVaccine, sourceVaccine, false);
				vaccinationsWithoutDuplicates.set(duplicateIndex, updatedVaccination);
			}
		}
		return vaccinationsWithoutDuplicates;
	}

	private boolean isDuplicateOf(VaccinationDto vaccination1, VaccinationDto vaccination2) {

		return !vaccination1.getUuid().equals(vaccination2.getUuid())
			&& vaccination1.getVaccineName() != null
			&& vaccination2.getVaccineName() != null
			&& vaccination1.getVaccineName() != Vaccine.UNKNOWN
			&& vaccination2.getVaccineName() != Vaccine.UNKNOWN
			&& vaccination1.getVaccinationDate() != null
			&& vaccination2.getVaccinationDate() != null
			&& DateHelper.isSameDay(vaccination1.getVaccinationDate(), vaccination2.getVaccinationDate())
			&& vaccination1.getVaccineName() == vaccination2.getVaccineName()
			&& (vaccination1.getVaccineName() != Vaccine.OTHER
				|| StringUtils.equals(vaccination1.getOtherVaccineName(), vaccination2.getOtherVaccineName()));
	}

	public Map<String, VaccinationDto> getLatestByPersons(List<PersonReferenceDto> persons, Disease disease) {
		List<Vaccination> vaccinations =
			service.getVaccinationsByCriteria(new VaccinationCriteria.Builder(persons).withDisease(disease).build(), null, null, null);

		Pseudonymizer<VaccinationDto> pseudonymizer = createPseudonymizer(vaccinations);

		return vaccinations.stream()
			.collect(Collectors.toMap(v -> v.getImmunization().getPerson().getUuid(), v -> toPseudonymizedDto(v, pseudonymizer), (v1, v2) -> {
				Date v1Date = v1.getVaccinationDate() != null ? v1.getVaccinationDate() : v1.getReportDate();
				Date v2Date = v2.getVaccinationDate() != null ? v2.getVaccinationDate() : v2.getReportDate();

				return v1Date.after(v2Date) ? v1 : v2;
			}));
	}

	@LocalBean
	@Stateless
	public static class VaccinationFacadeEjbLocal extends VaccinationFacadeEjb {

		public VaccinationFacadeEjbLocal() {
		}

		@Inject
		protected VaccinationFacadeEjbLocal(VaccinationService service) {
			super(service);
		}
	}
}
