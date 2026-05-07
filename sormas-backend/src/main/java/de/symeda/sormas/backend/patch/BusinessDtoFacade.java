package de.symeda.sormas.backend.patch;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.activityascase.ActivityAsCaseType;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
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

	private final Map<Class<? extends EntityDto>, Function<CaseDataDto, ? extends EntityDto>> dtoRetrieverDictionary = new HashMap<>();

	private final Map<String, Function<CaseDataDto, List<? extends EntityDto>>> dtoRetrieverByI18nDictionaryRead = new HashMap<>();

	private final Map<String, Function<CaseDataDto, EntityDto>> dtoRetrieverByI18nDictionaryCreateUpdate = new HashMap<>();

	/**
	 * Some {@link EntityDto} must be attached to a "parent" to be saved.
	 */
	private final Map<Class<? extends EntityDto>, LeafAttacher> leafAttacherRegistry = new LinkedHashMap<>();

	@PostConstruct
	private void init() {
		registerDirectSaveOperations();
		registerFetchOperations();

		registerFetchByI18nOperationsRead();

		registerFetchByI18nOperationsCreateUpdate();

		registerLeafAttacherOperations();
	}


	private void registerDirectSaveOperations() {
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

		registerFetchByI18nRead(ExposureDto.I18N_PREFIX, caseDataDto -> caseDataDto.getEpiData().getExposures());

		registerFetchByI18nRead(ActivityAsCaseDto.I18N_PREFIX, caseDataDto -> caseDataDto.getEpiData().getActivitiesAsCase());

		registerFetchByI18nRead(
			PreviousHospitalizationDto.I18N_PREFIX,
			caseDataDto -> caseDataDto.getHospitalization().getPreviousHospitalizations());
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

		registerFetchByI18nCreateUpdate(ExposureDto.I18N_PREFIX, caseDataDto -> ExposureDto.build(ExposureType.UNKNOWN));

		registerFetchByI18nCreateUpdate(ActivityAsCaseDto.I18N_PREFIX, caseDataDto -> ActivityAsCaseDto.build(ActivityAsCaseType.UNKNOWN));

		registerFetchByI18nCreateUpdate(PreviousHospitalizationDto.I18N_PREFIX, PreviousHospitalizationDto::build);
	}

	private Function<CaseDataDto, EntityDto> createImmunizationDtoFromCaseFct() {
		return caseDataDto -> {
			ImmunizationDto immunization = ImmunizationDto.build(caseDataDto.getPerson());

			immunization.setRelatedCase(caseDataDto.toReference());
			immunization.setPerson(caseDataDto.getPerson());
			immunization.setDisease(caseDataDto.getDisease());
			immunization.setResponsibleRegion(caseDataDto.getResponsibleRegion());
			immunization.setResponsibleDistrict(caseDataDto.getResponsibleDistrict());
			immunization.setReportingUser(userFacade.getCurrentUserAsReference());

			return immunization;
		};
	}

	private void registerFetchByI18nRead(String i18nName, Function<CaseDataDto, List<? extends EntityDto>> fct) {
		dtoRetrieverByI18nDictionaryRead.put(i18nName, fct);
	}

	private void registerFetchByI18nCreateUpdate(String i18nName, Function<CaseDataDto, EntityDto> fct) {
		dtoRetrieverByI18nDictionaryCreateUpdate.put(i18nName, fct);
	}

	private void registerLeafAttacherOperations() {
		registerLeafAttacher(VaccinationDto.class, (leaf, list) -> {
			ImmunizationDto immunization = fetchType(list, ImmunizationDto.class)
				.orElseGet(() -> (ImmunizationDto) createImmunizationDtoFromCaseFct().apply(requireCaseData(list)));
			immunization.getVaccinations().add((VaccinationDto) leaf);
			return immunization;
		});
		registerLeafAttacher(ExposureDto.class, (leaf, list) -> {
			CaseDataDto caseData = requireCaseData(list);
			caseData.getEpiData().getExposures().add((ExposureDto) leaf);
			return caseData;
		});
		registerLeafAttacher(ActivityAsCaseDto.class, (leaf, list) -> {
			CaseDataDto caseData = requireCaseData(list);
			caseData.getEpiData().getActivitiesAsCase().add((ActivityAsCaseDto) leaf);
			return caseData;
		});
		registerLeafAttacher(PreviousHospitalizationDto.class, (leaf, list) -> {
			CaseDataDto caseData = requireCaseData(list);
			caseData.getHospitalization().getPreviousHospitalizations().add((PreviousHospitalizationDto) leaf);
			return caseData;
		});
	}

	private <T extends EntityDto> void registerLeafAttacher(Class<T> leafClass, LeafAttacher attacher) {
		leafAttacherRegistry.put(leafClass, attacher);
	}

	private CaseDataDto requireCaseData(List<EntityDto> dtosInProgress) {
		return fetchType(dtosInProgress, CaseDataDto.class)
			.orElseThrow(
				() -> new IllegalStateException(
					String.format("When saving child leaf entities the caseData must be present, but was not: [%s]", dtosInProgress)));
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
	public Optional<EntityDto> tryFetchByI18nNameForCreateUpdate(@NotNull String i18nName, CaseDataDto caseDataDto) {
		return Optional.ofNullable(dtoRetrieverByI18nDictionaryCreateUpdate.get(i18nName))
			.map(fct -> fct.apply(caseDataDto));
	}

	/**
	 * @return I18n prefixes registered for display retrieval.
	 */
	public Set<String> fetchablePrefixes() {
		return dtoRetrieverByI18nDictionaryRead.keySet();
	}

	/**
	 * @return I18n prefixes registered for create/update retrieval.
	 */
	public Set<String> createUpdatePrefixes() {
		return Collections.unmodifiableSet(dtoRetrieverByI18nDictionaryCreateUpdate.keySet());
	}

	/**
	 * @return DTO classes that have a direct save function registered.
	 */
	public Set<Class<? extends EntityDto>> savableDtoClasses() {
		return Collections.unmodifiableSet(directDtoSaveDictionary.keySet());
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
	private  <T extends EntityDto> T saveDirectEntity(@NotNull EntityDto entityDto) {
		Class<? extends EntityDto> entityDtoClass = entityDto.getClass();

		return Optional.ofNullable((Function<T, T>) directDtoSaveDictionary.get(entityDtoClass))
				.orElseThrow(() -> new IllegalStateException(String.format("No save function defined for: [%s]", entityDtoClass)))
				.apply((T) entityDto);
	}

	public void save(@NotNull List<EntityDto> entityDtos) {
		ArrayList<EntityDto> dtosToSave = new ArrayList<>(entityDtos);

		leafAttacherRegistry.forEach((leafClass, attacher) ->
			dtosToSave.stream().filter(leafClass::isInstance).findAny().ifPresent(leaf -> {
				EntityDto parent = attacher.attachAndReturnParent(leaf, dtosToSave);
				dtosToSave.remove(leaf);
				if (!dtosToSave.contains(parent)) {
					dtosToSave.add(parent);
				}
			}));

		dtosToSave.forEach(this::saveDirectEntity);
	}

	private static @NotNull <T> Optional<T> fetchType(List<EntityDto> entityDtos, Class<T> targetClass) {
		return entityDtos.stream().filter(targetClass::isInstance).map(targetClass::cast).findAny();
	}


	@FunctionalInterface
	private interface LeafAttacher {
		EntityDto attachAndReturnParent(EntityDto leaf, List<EntityDto> dtosInProgress);
	}

}
