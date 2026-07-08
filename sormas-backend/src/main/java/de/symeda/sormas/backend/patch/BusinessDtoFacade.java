package de.symeda.sormas.backend.patch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
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
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.Tuple;
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

	private final Map<String, Function<CaseDataDto, AttachedEntityWrapper>> dtoRetrieverByI18nDictionaryCreateUpdate = new HashMap<>();

	/**
	 * Some {@link EntityDto} must be attached to a "parent" to be saved.
	 */
	private final Map<Class<? extends EntityDto>, LeafAttacher> leafAttacherDictionary = new LinkedHashMap<>();

	/**
	 * Attached directly to case data.
	 * Will be saved in one shot with the case.
	 */
	private final Map<Class<? extends EntityDto>, LeafAttacher> caseDataLeafAttacherDictionary = new LinkedHashMap<>();

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
			caseDataDto -> AttachedEntityWrapper.attached(personFacade.getByUuid(caseDataDto.getPerson().getUuid())));

		registerFetchByI18nCreateUpdate(
			ImmunizationDto.I18N_PREFIX,
			createImmunizationDtoFromCaseFct().andThen(AttachedEntityWrapper::notYetAttached));

		registerFetchByI18nCreateUpdate(
			VaccinationDto.I18N_PREFIX,
			caseDataDto -> AttachedEntityWrapper.notYetAttached(VaccinationDto.build(userFacade.getCurrentUserAsReference())));

		registerFetchByI18nCreateUpdate(
			ExposureDto.I18N_PREFIX,
			caseDataDto -> AttachedEntityWrapper.notYetAttached(ExposureDto.build(ExposureType.UNKNOWN)));

		registerFetchByI18nCreateUpdate(
			ActivityAsCaseDto.I18N_PREFIX,
			caseDataDto -> AttachedEntityWrapper.notYetAttached(ActivityAsCaseDto.build(ActivityAsCaseType.UNKNOWN)));

		registerFetchByI18nCreateUpdate(
			PreviousHospitalizationDto.I18N_PREFIX,
			caze -> AttachedEntityWrapper.notYetAttached(PreviousHospitalizationDto.build(caze)));
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

	private void registerFetchByI18nCreateUpdate(String i18nName, Function<CaseDataDto, AttachedEntityWrapper> fct) {
		dtoRetrieverByI18nDictionaryCreateUpdate.put(i18nName, fct);
	}

	private void registerLeafAttacherOperations() {
		registerLeafAttacher(VaccinationDto.class, (leaf, groupIndex, list) -> {
			ImmunizationDto immunization = list.stream()
				.filter(tuple -> tuple.getSecond() instanceof ImmunizationDto && Objects.equals(tuple.getFirst(), groupIndex))
				.map(tuple -> (ImmunizationDto) tuple.getSecond())
				.findAny()
				.orElseGet(() -> {
					ImmunizationDto newImm = (ImmunizationDto) createImmunizationDtoFromCaseFct().apply(requireCaseData(list));
					list.add(Tuple.of(groupIndex, newImm));
					return newImm;
				});

			if (immunization.getMeansOfImmunization() == null) {
				immunization.setMeansOfImmunization(MeansOfImmunization.VACCINATION);
			}
			immunization.getVaccinations().add((VaccinationDto) leaf);
		});

		// directly linked to the case
		registerCaseDataLeafAttacher(ExposureDto.class, (leaf, groupIndex, list) -> {
			requireCaseData(list).getEpiData().getExposures().add((ExposureDto) leaf);
		});
		registerCaseDataLeafAttacher(ActivityAsCaseDto.class, (leaf, groupIndex, list) -> {
			requireCaseData(list).getEpiData().getActivitiesAsCase().add((ActivityAsCaseDto) leaf);
		});
		registerCaseDataLeafAttacher(PreviousHospitalizationDto.class, (leaf, groupIndex, list) -> {
			requireCaseData(list).getHospitalization().getPreviousHospitalizations().add((PreviousHospitalizationDto) leaf);
		});
	}

	private <T extends EntityDto> void registerLeafAttacher(Class<T> leafClass, LeafAttacher attacher) {
		leafAttacherDictionary.put(leafClass, attacher);
	}

	private <T extends EntityDto> void registerCaseDataLeafAttacher(Class<T> leafClass, LeafAttacher attacher) {
		caseDataLeafAttacherDictionary.put(leafClass, attacher);
	}

	private CaseDataDto requireCaseData(List<Tuple<Integer, EntityDto>> dtosInProgress) {
		return dtosInProgress.stream()
			.map(Tuple::getSecond)
			.filter(CaseDataDto.class::isInstance)
			.map(CaseDataDto.class::cast)
			.findAny()
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
	 * 
	 * @param i18nName
	 *            I18N translation key
	 * @param caseDataDto
	 *            root/reference entity
	 * @return "un-attached" DTO that will be thrown away if not saved.
	 */
	public Optional<AttachedEntityWrapper> tryFetchByI18nNameForCreateUpdate(@NotNull String i18nName, CaseDataDto caseDataDto) {
		return Optional.ofNullable(dtoRetrieverByI18nDictionaryCreateUpdate.get(i18nName)).map(fct -> fct.apply(caseDataDto));
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
	private <T extends EntityDto> T saveDirectEntity(@NotNull EntityDto entityDto) {
		Class<? extends EntityDto> entityDtoClass = entityDto.getClass();

		return Optional.ofNullable((Function<T, T>) directDtoSaveDictionary.get(entityDtoClass))
			.orElseThrow(() -> new IllegalStateException(String.format("No save function defined for: [%s]", entityDtoClass)))
			.apply((T) entityDto);
	}

	public void save(@NotNull List<Tuple<Integer, EntityDto>> entityDtosByKey) {
		Predicate<Tuple<Integer, EntityDto>> allButCaseDataDto = tuple -> tuple.getSecond() instanceof CaseDataDto;
		Optional<EntityDto> caseDataOpt = entityDtosByKey.stream().filter(allButCaseDataDto).map(Tuple::getSecond).findAny();

		List<Tuple<Integer, EntityDto>> dtosInProgress = new ArrayList<>(entityDtosByKey);

		// must be attached to case data before being stored, otherwise those entities are lost.
		caseDataLeafAttacherDictionary.forEach((leafClass, attacher) -> {
			List<Tuple<Integer, EntityDto>> leaves =
				dtosInProgress.stream().filter(t -> leafClass.isInstance(t.getSecond())).collect(Collectors.toList());

			leaves.forEach(leafTuple -> {
				dtosInProgress.remove(leafTuple);
				attacher.attachLeaf(leafTuple.getSecond(), leafTuple.getFirst(), dtosInProgress);
			});
		});

		// case data must be stored up-front because "logically-attached" entities might update it again: immunization etc. 
		caseDataOpt.ifPresent(this::saveDirectEntity);

		leafAttacherDictionary.forEach((leafClass, attacher) -> {
			List<Tuple<Integer, EntityDto>> leaves =
				dtosInProgress.stream().filter(t -> leafClass.isInstance(t.getSecond())).collect(Collectors.toList());

			leaves.forEach(leafTuple -> {
				dtosInProgress.remove(leafTuple);
				attacher.attachLeaf(leafTuple.getSecond(), leafTuple.getFirst(), dtosInProgress);
			});
		});

		dtosInProgress.stream().filter(Predicate.not(allButCaseDataDto)).map(Tuple::getSecond).forEach(this::saveDirectEntity);
	}

	@FunctionalInterface
	private interface LeafAttacher {

		void attachLeaf(EntityDto leaf, Integer groupIndex, List<Tuple<Integer, EntityDto>> dtosInProgress);
	}

}
