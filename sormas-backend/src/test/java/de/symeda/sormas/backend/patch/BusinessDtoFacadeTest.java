package de.symeda.sormas.backend.patch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb;

class BusinessDtoFacadeTest extends AbstractUnitTest {

	@InjectMocks
	private BusinessDtoFacade victim;

	@Mock
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;
	@Mock
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;
	@Mock
	private ImmunizationFacadeEjb.ImmunizationFacadeEjbLocal immunizationFacade;
	@Mock
	private UserFacadeEjb.UserFacadeEjbLocal userFacade;

	@BeforeEach
	void init() throws ReflectiveOperationException {
		Method initMethod = BusinessDtoFacade.class.getDeclaredMethod("init");
		initMethod.setAccessible(true);
		initMethod.invoke(victim);
	}

	@Test
	void readAndCreateUpdatePrefixes_containSameKeys() {
		assertEquals(victim.fetchablePrefixes(), victim.createUpdatePrefixes());
	}

	@Test
	void allNonRootSavableDtoClasses_arePresentInBothI18nDictionaries() throws ReflectiveOperationException {
		Set<String> readPrefixes = victim.fetchablePrefixes();
		Set<String> createUpdatePrefixes = victim.createUpdatePrefixes();

		for (Class<? extends EntityDto> clazz : victim.savableDtoClasses()) {
			if (CaseDataDto.class.equals(clazz)) {
				continue;
			}
			String i18nPrefix = (String) clazz.getDeclaredField("I18N_PREFIX").get(null);
			assertAll(
				() -> assertTrue(readPrefixes.contains(i18nPrefix), clazz.getSimpleName() + " missing from read dictionary"),
				() -> assertTrue(createUpdatePrefixes.contains(i18nPrefix), clazz.getSimpleName() + " missing from createUpdate dictionary"));
		}
	}

	@Test
	void getCaseDataDtoNullable_returnsNull_whenNotFound() {
		when(caseFacade.getByUuid("unknown")).thenReturn(null);

		assertNull(victim.getCaseDataDtoNullable("unknown"));
	}

	@Test
	void getCaseDataDtoNullable_returnsDto_whenFound() {
		CaseDataDto expected = new CaseDataDto();
		when(caseFacade.getByUuid("uuid-1")).thenReturn(expected);

		assertSame(expected, victim.getCaseDataDtoNullable("uuid-1"));
	}

	@Test
	void getCaseDataDto_throwsIllegalState_whenNotFound() {
		when(caseFacade.getByUuid("unknown")).thenReturn(null);

		assertThrows(IllegalStateException.class, () -> victim.getCaseDataDto("unknown"));
	}

	@Test
	void tryFetchByI18nNameForCreateUpdate_returnsEmpty_forUnknownPrefix() {
		Optional<AttachedEntityWrapper> result = victim.tryFetchByI18nNameForCreateUpdate("UnknownPrefix", new CaseDataDto());

		assertTrue(result.isEmpty());
	}

	@Test
	void tryFetchByI18nNameForCreateUpdate_returnsPersonDto_forPersonPrefix() {
		PersonDto personDto = new PersonDto();
		CaseDataDto caseData = buildCaseDataWithPerson("person-uuid");
		when(personFacade.getByUuid("person-uuid")).thenReturn(personDto);

		Optional<AttachedEntityWrapper> result = victim.tryFetchByI18nNameForCreateUpdate(PersonDto.I18N_PREFIX, caseData);

		assertAll(() -> assertTrue(result.isPresent()), () -> assertSame(personDto, result.get().getEntityDto()));
	}

	@Test
	void tryFetchByI18nNameForCreateUpdate_returnsNewImmunization_forImmunizationPrefix() {
		CaseDataDto caseData = buildCaseDataWithPerson("person-uuid");

		Optional<AttachedEntityWrapper> result = victim.tryFetchByI18nNameForCreateUpdate(ImmunizationDto.I18N_PREFIX, caseData);

		assertTrue(result.isPresent());
		assertTrue(result.get().getEntityDto() instanceof ImmunizationDto);
	}

	// — save(Map) —

	@Test
	void save_caseData_delegatesToCaseFacade() {
		// PREPARE
		CaseDataDto caseData = new CaseDataDto();

		// EXECUTE
		victim.save(Map.of(Tuple.firstOnly(CaseDataDto.I18N_PREFIX), caseData));

		// CHECK
		verify(caseFacade).save(caseData);
	}

	@Test
	void save_personDto_delegatesToPersonFacade() {
		// PREPARE
		PersonDto personDto = new PersonDto();

		// EXECUTE
		victim.save(Map.of(Tuple.firstOnly(PersonDto.I18N_PREFIX), personDto));

		// CHECK
		verify(personFacade).save(personDto);
	}

	@Test
	void save_immunizationDto_delegatesToImmunizationFacade() {
		// PREPARE
		ImmunizationDto immunization = new ImmunizationDto();

		// EXECUTE
		victim.save(Map.of(Tuple.firstOnly(ImmunizationDto.I18N_PREFIX), immunization));

		// CHECK
		verify(immunizationFacade).save(immunization);
	}

	@Test
	void save_vaccinationWithExistingImmunization_attachesVaccinationThenSavesImmunization() {
		// PREPARE
		ImmunizationDto immunization = new ImmunizationDto();
		VaccinationDto vaccination = new VaccinationDto();

		// EXECUTE
		victim.save(Map.of(Tuple.firstOnly(ImmunizationDto.I18N_PREFIX), immunization, Tuple.firstOnly(VaccinationDto.I18N_PREFIX), vaccination));

		// CHECK
		verify(immunizationFacade).save(immunization);
		assertAll(() -> assertEquals(1, immunization.getVaccinations().size()), () -> assertSame(vaccination, immunization.getVaccinations().get(0)));
	}

	@Test
	void save_vaccinationWithoutImmunization_autoCreatesImmunizationAttachesAndSaves() {
		// PREPARE
		CaseDataDto caseData = buildCaseDataWithPerson("person-uuid");
		VaccinationDto vaccination = new VaccinationDto();

		// EXECUTE
		victim.save(Map.of(Tuple.firstOnly(CaseDataDto.I18N_PREFIX), caseData, Tuple.firstOnly(VaccinationDto.I18N_PREFIX), vaccination));

		// CHECK
		ArgumentCaptor<ImmunizationDto> captor = ArgumentCaptor.forClass(ImmunizationDto.class);
		verify(immunizationFacade).save(captor.capture());
		ImmunizationDto savedImmunization = captor.getValue();
		assertAll(
			() -> assertEquals(1, savedImmunization.getVaccinations().size()),
			() -> assertSame(vaccination, savedImmunization.getVaccinations().get(0)));
	}

	@Test
	void save_vaccinationWithoutImmunizationOrCaseData_throwsIllegalState() {
		// PREPARE
		VaccinationDto vaccination = new VaccinationDto();

		// EXECUTE & CHECK
		assertThrows(IllegalStateException.class, () -> victim.save(Map.of(Tuple.firstOnly(VaccinationDto.I18N_PREFIX), vaccination)));
	}

	@Test
	void save_vaccinationWithImmunization_doesNotCallCaseFacadeSave() {
		// PREPARE
		ImmunizationDto immunization = new ImmunizationDto();
		VaccinationDto vaccination = new VaccinationDto();

		// EXECUTE
		victim.save(Map.of(Tuple.firstOnly(ImmunizationDto.I18N_PREFIX), immunization, Tuple.firstOnly(VaccinationDto.I18N_PREFIX), vaccination));

		// CHECK
		verify(caseFacade, never()).save(ArgumentMatchers.<@Valid @NotNull CaseDataDto> any());
	}

	@Test
	void save_groupedVaccinations_eachGroupIndexAttachesToItsOwnImmunization() {
		// PREPARE
		CaseDataDto caseData = buildCaseDataWithPerson("person-uuid");
		ImmunizationDto immunization0 = new ImmunizationDto();
		VaccinationDto vaccination0 = new VaccinationDto();
		VaccinationDto vaccination1 = new VaccinationDto();

		Map<Tuple<String, Integer>, EntityDto> entityDtosByKey = new LinkedHashMap<>();
		entityDtosByKey.put(Tuple.firstOnly(CaseDataDto.I18N_PREFIX), caseData);
		entityDtosByKey.put(Tuple.of(ImmunizationDto.I18N_PREFIX, 0), immunization0);
		entityDtosByKey.put(Tuple.of(VaccinationDto.I18N_PREFIX, 0), vaccination0);
		entityDtosByKey.put(Tuple.of(VaccinationDto.I18N_PREFIX, 1), vaccination1);

		// EXECUTE
		victim.save(entityDtosByKey);

		// CHECK
		ArgumentCaptor<ImmunizationDto> captor = ArgumentCaptor.forClass(ImmunizationDto.class);
		verify(immunizationFacade, times(2)).save(captor.capture());
		ImmunizationDto savedImmunization0 = captor.getAllValues().get(0);
		ImmunizationDto savedImmunization1 = captor.getAllValues().get(1);

		assertAll(
			() -> assertSame(immunization0, savedImmunization0),
			() -> assertEquals(1, savedImmunization0.getVaccinations().size()),
			() -> assertSame(vaccination0, savedImmunization0.getVaccinations().get(0)),

			() -> assertNotSame(immunization0, savedImmunization1),
			() -> assertEquals(1, savedImmunization1.getVaccinations().size()),
			() -> assertSame(vaccination1, savedImmunization1.getVaccinations().get(0)));
	}

	private static CaseDataDto buildCaseDataWithPerson(String personUuid) {
		CaseDataDto caseData = new CaseDataDto();
		caseData.setPerson(new PersonReferenceDto(personUuid));
		return caseData;
	}
}
