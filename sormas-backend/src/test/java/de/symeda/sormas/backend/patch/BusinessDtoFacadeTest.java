package de.symeda.sormas.backend.patch;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
		Optional<EntityDto> result = victim.tryFetchByI18nNameForCreateUpdate("UnknownPrefix", new CaseDataDto());

		assertTrue(result.isEmpty());
	}

	@Test
	void tryFetchByI18nNameForCreateUpdate_returnsPersonDto_forPersonPrefix() {
		PersonDto personDto = new PersonDto();
		CaseDataDto caseData = buildCaseDataWithPerson("person-uuid");
		when(personFacade.getByUuid("person-uuid")).thenReturn(personDto);

		Optional<EntityDto> result = victim.tryFetchByI18nNameForCreateUpdate(PersonDto.I18N_PREFIX, caseData);

		assertAll(
			() -> assertTrue(result.isPresent()),
			() -> assertSame(personDto, result.get()));
	}

	@Test
	void tryFetchByI18nNameForCreateUpdate_returnsNewImmunization_forImmunizationPrefix() {
		CaseDataDto caseData = buildCaseDataWithPerson("person-uuid");

		Optional<EntityDto> result = victim.tryFetchByI18nNameForCreateUpdate(ImmunizationDto.I18N_PREFIX, caseData);

		assertTrue(result.isPresent());
		assertTrue(result.get() instanceof ImmunizationDto);
	}

	// — save(List) —

	@Test
	void save_list_caseData_delegatesToCaseFacade() {
		CaseDataDto caseData = new CaseDataDto();

		victim.save(List.of(caseData));

		verify(caseFacade).save(caseData);
	}

	@Test
	void save_list_personDto_delegatesToPersonFacade() {
		PersonDto personDto = new PersonDto();

		victim.save(List.of(personDto));

		verify(personFacade).save(personDto);
	}

	@Test
	void save_list_immunizationDto_delegatesToImmunizationFacade() {
		ImmunizationDto immunization = new ImmunizationDto();

		victim.save(List.of(immunization));

		verify(immunizationFacade).save(immunization);
	}

	@Test
	void save_list_vaccinationWithExistingImmunization_attachesVaccinationThenSavesImmunization() {
		ImmunizationDto immunization = new ImmunizationDto();
		VaccinationDto vaccination = new VaccinationDto();

		victim.save(List.of(immunization, vaccination));

		verify(immunizationFacade).save(immunization);
		assertAll(
			() -> assertEquals(1, immunization.getVaccinations().size()),
			() -> assertSame(vaccination, immunization.getVaccinations().get(0)));
	}

	@Test
	void save_list_vaccinationWithoutImmunization_autoCreatesImmunizationAttachesAndSaves() {
		CaseDataDto caseData = buildCaseDataWithPerson("person-uuid");
		VaccinationDto vaccination = new VaccinationDto();

		victim.save(List.of(caseData, vaccination));

		ArgumentCaptor<ImmunizationDto> captor = ArgumentCaptor.forClass(ImmunizationDto.class);
		verify(immunizationFacade).save(captor.capture());
		ImmunizationDto savedImmunization = captor.getValue();
		assertAll(
			() -> assertEquals(1, savedImmunization.getVaccinations().size()),
			() -> assertSame(vaccination, savedImmunization.getVaccinations().get(0)));
	}

	@Test
	void save_list_vaccinationWithoutImmunizationOrCaseData_throwsIllegalState() {
		VaccinationDto vaccination = new VaccinationDto();

		assertThrows(IllegalStateException.class, () -> victim.save(List.of(vaccination)));
	}

	@Test
	void save_list_vaccinationWithImmunization_doesNotCallCaseFacadeSave() {
		ImmunizationDto immunization = new ImmunizationDto();
		VaccinationDto vaccination = new VaccinationDto();

		victim.save(List.of(immunization, vaccination));

		verify(caseFacade, never()).save(ArgumentMatchers.<@Valid @NotNull CaseDataDto>any());
	}

	private static CaseDataDto buildCaseDataWithPerson(String personUuid) {
		CaseDataDto caseData = new CaseDataDto();
		caseData.setPerson(new PersonReferenceDto(personUuid));
		return caseData;
	}
}
