package de.symeda.sormas.backend.patch;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
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
import de.symeda.sormas.backend.user.UserFacadeEjb;

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

	@EJB
	private UserFacadeEjb.UserFacadeEjbLocal userFacade;

	private final Map<Class<? extends EntityDto>, Function<? extends EntityDto, ? extends EntityDto>> directDtoSaveDictionary = new HashMap<>();
	private final Map<Class<? extends EntityDto>, BiFunction<CaseDataDto, ? extends EntityDto, ? extends EntityDto>> saveFromCaseDictionary = new HashMap<>();

	private final Map<Class<? extends EntityDto>, Function<CaseDataDto, ? extends EntityDto>> dtoRetrieverDictionary = new HashMap<>();

	private final Map<String, Function<CaseDataDto, List<? extends EntityDto>>> dtoRetrieverByI18nDictionaryRead = new HashMap<>();

	private final Map<String, Function<CaseDataDto, EntityDto>> dtoRetrieverByI18nDictionaryCreateUpdate = new HashMap<>();

	@PostConstruct
	private void init() {
		registerSaveOperations();
		registerFetchOperations();

		// TODO: add fetch for "list elements":
		// TODO: probably start fetch from highest-level element and drill down to retrive.
		// Quite unsure if drill always work: Add exception: "missing parent entity".
		// - Exposure
		// - Activity as a case
		// for save: List elements must be added to the parent
		// TODO: issue with saving in order ?
		registerFetchByI18nOperationsRead();

		registerFetchByI18nOperationsCreateUpdate();
	}


	private void registerSaveOperations() {
		registerSave(CaseDataDto.class, caseDataDto -> caseFacade.save(caseDataDto));
		registerSave(PersonDto.class, personDto -> personFacade.save(personDto));
		registerSave(ImmunizationDto.class, immunizationDto -> immunizationFacade.save(immunizationDto));
	}

	private <T extends EntityDto> void registerSave(Class<T> dtoClass, Function<T, T> consumer) {
		directDtoSaveDictionary.put(dtoClass, consumer);
	}

	private void registerFetchOperations() {
		registerFetch(PersonDto.class, caseDataDto -> personFacade.getByUuid(caseDataDto.getPerson().getUuid()));
	}

	private <T extends EntityDto> void registerFetch(Class<T> dtoClass, Function<CaseDataDto, T> fct) {
		dtoRetrieverDictionary.put(dtoClass, fct);
	}

	private void registerFetchByI18nOperationsRead() {
		registerFetchByI18nRead(
			PersonDto.I18N_PREFIX,
			caseDataDto -> Collections.singletonList(personFacade.getByUuid(caseDataDto.getPerson().getUuid())));
		registerFetchByI18nRead(
			ImmunizationDto.I18N_PREFIX,
			caseDataDto -> immunizationFacade.getByPersonUuids(Collections.singletonList(caseDataDto.getPerson().getUuid())));

		registerFetchByI18nRead(
			VaccinationDto.I18N_PREFIX,
			caseDataDto -> immunizationFacade.getByPersonUuids(Collections.singletonList(caseDataDto.getPerson().getUuid()))
				.stream()
				.flatMap(immunization -> immunization.getVaccinations().stream())
				.collect(Collectors.toList()));
	}


	private void registerFetchByI18nOperationsCreateUpdate() {
		registerFetchByI18nCreateUpdate(
				PersonDto.I18N_PREFIX,
				caseDataDto -> personFacade.getByUuid(caseDataDto.getPerson().getUuid()));

		registerFetchByI18nCreateUpdate(
				ImmunizationDto.I18N_PREFIX,
				createImmunizationDtoFromCaseFct());

		registerFetchByI18nCreateUpdate(
				VaccinationDto.I18N_PREFIX,
				caseDataDto -> VaccinationDto.build(userFacade.getCurrentUserAsReference()));
	}

	private static Function<CaseDataDto, EntityDto> createImmunizationDtoFromCaseFct() {
		return caseDataDto -> {
			ImmunizationDto build = ImmunizationDto.build(caseDataDto.getPerson());
			build.setRelatedCase(caseDataDto.toReference());
			build.setPerson(caseDataDto.getPerson());
			return build;
		};
	}

	private void registerFetchByI18nRead(String i18nName, Function<CaseDataDto, List<? extends EntityDto>> fct) {
		dtoRetrieverByI18nDictionaryRead.put(i18nName, fct);
	}

	private void registerFetchByI18nCreateUpdate(String i18nName, Function<CaseDataDto, EntityDto> fct) {
		dtoRetrieverByI18nDictionaryCreateUpdate.put(i18nName, fct);
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
	public List<? extends EntityDto> fetchByI18nNameForDisplay(@NotNull String i18nName, CaseDataDto caseDataDto) {
		return Optional.ofNullable(dtoRetrieverByI18nDictionaryRead.get(i18nName))
			.orElseThrow(() -> new IllegalStateException(String.format("No fetch function defined for: [%s]", i18nName)))
			.apply(caseDataDto);
	}

	/**
	 * Warning: Will retrieve a new instance on every fetch, MUST be cached within a single patch operation.
	 * @param i18nName I18N translation key
	 * @param caseDataDto root/reference entity
	 * @return "un-attached" DTO that will be thrown away if not saved.
	 */
	@Nullable
	public EntityDto fetchByI18nNameForCreateUpdate(@NotNull String i18nName, CaseDataDto caseDataDto) {
		return Optional.ofNullable(dtoRetrieverByI18nDictionaryCreateUpdate.get(i18nName))
				.orElseThrow(() -> new IllegalStateException(String.format("No fetch function defined for: [%s]", i18nName)))
				.apply(caseDataDto);
	}

	public Optional<EntityDto> tryFetchByI18nNameForCreateUpdate(@NotNull String i18nName, CaseDataDto caseDataDto) {
		return Optional.ofNullable(dtoRetrieverByI18nDictionaryCreateUpdate.get(i18nName))
			.map(fct -> fct.apply(caseDataDto));
	}

	/**
	 * For displaying purposes what purposes can be retrieved.
	 * 
	 * @return prefixes that can be fetched through their I18n Prefix.
	 */
	public Set<String> fetchablePrefixes() {
		return dtoRetrieverByI18nDictionaryRead.keySet();
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

		return Optional.ofNullable((Function<T, T>) directDtoSaveDictionary.get(entityDtoClass))
				.orElseThrow(() -> new IllegalStateException(String.format("No save function defined for: [%s]", entityDtoClass)))
				.apply((T) entityDto);
	}

	public void save(@NotNull List<EntityDto> entityDtos) {

        Optional<ImmunizationDto> immunizationDto = fetchType(entityDtos, ImmunizationDto.class);
        Optional<VaccinationDto> vaccinationDto = fetchType(entityDtos, VaccinationDto.class);
        Optional<CaseDataDto> caseDataDtoOpt = fetchType(entityDtos, CaseDataDto.class);

		if (vaccinationDto.isPresent()) {
			ImmunizationDto actualImmunizationDto;
			if (immunizationDto.isPresent()) {
				actualImmunizationDto = immunizationDto.orElseThrow();
			} else {
				actualImmunizationDto = (ImmunizationDto) createImmunizationDtoFromCaseFct().apply(caseDataDtoOpt.orElseThrow());
			}

			actualImmunizationDto.getVaccinations().add(vaccinationDto.orElseThrow());
		}

		// TODO: filter out those that are already done through "case-drilling"
		entityDtos.forEach(this::save);
	}

	private static @NotNull <T> Optional<T> fetchType(List<EntityDto> entityDtos, Class<T> targetClass) {
		return entityDtos.stream().filter(targetClass::isInstance).map(targetClass::cast).findAny();
	}

}
