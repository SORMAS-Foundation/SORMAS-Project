package de.symeda.sormas.backend.patch;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;

/**
 * Meant as single entry point for usages were multiple Business DTOs must be fetched or saved.
 */
@ApplicationScoped
public class BusinessDtoFacade {

	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;

	@EJB
	private ImmunizationFacadeEjb.ImmunizationFacadeEjbLocal immunizationFacade;

	private final Map<Class<? extends EntityDto>, Function<? extends EntityDto, ? extends EntityDto>> dtoSaveDictionary = new HashMap<>();

	private final Map<Class<? extends EntityDto>, Function<CaseDataDto, ? extends EntityDto>> dtoRetrieverDictionary = new HashMap<>();

	private final Map<String, Function<CaseDataDto, List<? extends EntityDto>>> dtoRetrieverByI18nDictionary = new HashMap<>();

	@PostConstruct
	private void init() {
		registerSaveOperations();
		registerFetchOperations();
		registerFetchByI18nOperations();
	}

	private void registerSaveOperations() {
		registerSave(CaseDataDto.class, caseDataDto -> caseFacade.save(caseDataDto));
		registerSave(PersonDto.class, personDto -> personFacade.save(personDto));
		registerSave(ImmunizationDto.class, immunizationDto -> immunizationFacade.save(immunizationDto));
	}

	private <T extends EntityDto> void registerSave(Class<T> dtoClass, Function<T, T> consumer) {
		dtoSaveDictionary.put(dtoClass, consumer);
	}

	private void registerFetchOperations() {
		registerFetch(PersonDto.class, caseDataDto -> personFacade.getByUuid(caseDataDto.getPerson().getUuid()));
	}

	private <T extends EntityDto> void registerFetch(Class<T> dtoClass, Function<CaseDataDto, T> fct) {
		dtoRetrieverDictionary.put(dtoClass, fct);
	}

	private void registerFetchByI18nOperations() {
		registerFetchByI18n(
			PersonDto.I18N_PREFIX,
			caseDataDto -> Collections.singletonList(personFacade.getByUuid(caseDataDto.getPerson().getUuid())));
		registerFetchByI18n(
			ImmunizationDto.I18N_PREFIX,
			caseDataDto -> immunizationFacade.getByPersonUuids(Collections.singletonList(caseDataDto.getPerson().getUuid())));

		registerFetchByI18n(
			VaccinationDto.I18N_PREFIX,
			caseDataDto -> immunizationFacade.getByPersonUuids(Collections.singletonList(caseDataDto.getPerson().getUuid()))
				.stream()
				.flatMap(immunization -> immunization.getVaccinations().stream())
				.collect(Collectors.toList()));

	}

	private void registerFetchByI18n(String i18nName, Function<CaseDataDto, List<? extends EntityDto>> fct) {
		dtoRetrieverByI18nDictionary.put(i18nName, fct);
	}

	@Nullable
	public CaseDataDto getCaseDataDtoNullable(String caseUuid) {
		return caseFacade.getByUuid(caseUuid);
	}

	@NotNull
	public CaseDataDto getCaseDataDto(String caseUuid) {
		return Optional.ofNullable(getCaseDataDtoNullable(caseUuid))
			.orElseThrow(() -> new IllegalStateException(String.format("No CaseDataDto found for [%s]", caseUuid)));
	}

	/**
	 * Meant for creational purposes.
	 * 
	 * @param entityClass
	 *            target class
	 * @param caseDataDto
	 *            linked case
	 * @return DTO if found.
	 * @param <T>
	 *            types
	 */
	@Nullable
	public <T extends EntityDto> T fetch(@NotNull Class<T> entityClass, CaseDataDto caseDataDto) {
		return Optional.ofNullable((Function<CaseDataDto, T>) dtoRetrieverDictionary.get(entityClass))
			.orElseThrow(() -> new IllegalStateException(String.format("No fetch function defined for: [%s]", entityClass)))
			.apply(caseDataDto);
	}

	/**
	 * Meant for display purposes.
	 * 
	 * @param i18nName
	 *            DtoPrefix per example {@link PersonDto#I18N_PREFIX}.
	 * @param caseDataDto
	 *            linked case.
	 * @return entity dto if found.
	 */
	@Nullable
	public List<? extends EntityDto> fetchByI18nName(@NotNull String i18nName, CaseDataDto caseDataDto) {
		return Optional.ofNullable(dtoRetrieverByI18nDictionary.get(i18nName))
			.orElseThrow(() -> new IllegalStateException(String.format("No fetch function defined for: [%s]", i18nName)))
			.apply(caseDataDto);
	}

	/**
	 * For displaying purposes what purposes can be retrieved.
	 * 
	 * @return prefixes that can be fetched through their I18n Prefix.
	 */
	public Set<String> fetchablePrefixes() {
		return dtoRetrieverByI18nDictionary.keySet();
	}

	/**
	 * Single entry for saving a business DTO.
	 * 
	 * @param entityDto
	 *            business DTO
	 * @return saved DTO.
	 * @param <T>
	 *            type
	 */
	public <T extends EntityDto> T save(@NotNull EntityDto entityDto) {
		Class<? extends EntityDto> entityDtoClass = entityDto.getClass();

		return Optional.ofNullable((Function<T, T>) dtoSaveDictionary.get(entityDtoClass))
			.orElseThrow(() -> new IllegalStateException(String.format("No save function defined for: [%s]", entityDtoClass)))
			.apply((T) entityDto);
	}
}
