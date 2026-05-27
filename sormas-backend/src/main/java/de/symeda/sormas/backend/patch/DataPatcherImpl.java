package de.symeda.sormas.backend.patch;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.externalmessage.survey.PatchDictionary;
import de.symeda.sormas.api.externalmessage.survey.PatchField;
import de.symeda.sormas.api.patch.*;
import de.symeda.sormas.api.patch.mapping.FieldCustomMapper;
import de.symeda.sormas.api.patch.mapping.FieldPatchRequest;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.json.ObjectMapperProvider;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldDataPatcher;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldSinglePatchingResult;
import de.symeda.sormas.backend.patch.mapping.FieldCustomMapperRegistry;
import de.symeda.sormas.backend.patch.mapping.PatchEqualityCheckersRegistry;
import de.symeda.sormas.backend.patch.mapping.ValueMapperRegistry;
import de.symeda.sormas.backend.util.CollectorUtils;

@ApplicationScoped
public class DataPatcherImpl implements DataPatcher {

	private final static Logger logger = LoggerFactory.getLogger(DataPatcherImpl.class);

	@Inject
	private PatchFieldHelper patchFieldHelper;

	@Inject
	private ValueMapperRegistry valueMapperRegistry;

	@Inject
	private FieldCustomMapperRegistry fieldCustomMapperRegistry;

	@Inject
	private PatchEqualityCheckersRegistry patchEqualityCheckersRegistry;

	@Inject
	private BusinessDtoFacade businessDtoFacade;

	@EJB
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@Inject
	private CustomizableFieldDataPatcher customizableFieldDataPatcher;

	public DataPatcherImpl() {
	}

	public DataPatcherImpl(
		PatchFieldHelper patchFieldHelper,
		ValueMapperRegistry valueMapperRegistry,
		FieldCustomMapperRegistry fieldCustomMapperRegistry,
		PatchEqualityCheckersRegistry patchEqualityCheckersRegistry,
		BusinessDtoFacade businessDtoFacade,
		FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade,
		ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade) {

		this.patchFieldHelper = patchFieldHelper;
		this.valueMapperRegistry = valueMapperRegistry;
		this.fieldCustomMapperRegistry = fieldCustomMapperRegistry;
		this.patchEqualityCheckersRegistry = patchEqualityCheckersRegistry;
		this.businessDtoFacade = businessDtoFacade;
		this.featureConfigurationFacade = featureConfigurationFacade;
		this.configFacade = configFacade;
	}

	@Override
	public DataPatchResponse patch(CaseDataPatchRequest request) {
		logger.debug("patch: [{}]", request);

		CaseDataDto caseData = getCaseDataDto(request);

		Disease disease = caseData.getDisease();

		Map<Tuple<String, Integer>, AttachedEntityWrapper> entityCache = new HashMap<>();
		entityCache.put(Tuple.firstOnly(CaseDataDto.I18N_PREFIX), AttachedEntityWrapper.attached(caseData));

		List<SingleFieldPatchResult> patchingTuples = computePatchingTuples(request);

		logger.trace("Computed patchingTuples: [{}]", patchingTuples);

		Predicate<SingleFieldPatchResult> customizableFieldsPredicate =
			patchResult -> patchResult.getField().getField().contains(PatchFieldHelper.CUSTOM_PREFIX);

		List<PlainSinglePatchResult> plainResults =
			patchingTuples.stream().filter(Predicate.not(customizableFieldsPredicate)).map(singleFieldPatchResult -> {
				PatchField patchField = singleFieldPatchResult.field;
				PlainSinglePatchResult singlePatchResult = new PlainSinglePatchResult().setField(patchField);
				try {
					Supplier<AttachedEntityWrapper> target = () -> findAppropriateTarget(patchField, caseData, entityCache);

					return produceSinglePatchResult(request, singleFieldPatchResult, disease, target);
				} catch (DataPatchingException e) {
					DataPatchFailureCause failureCause = e.getFailureCause();
					logger.error(
						"DataPatching-specific failure during patch operation for request: [{}], [{}], of type [{}]",
						request,
						singleFieldPatchResult,
						failureCause,
						e);
					return singlePatchResult.setFailure(new DataPatchFailure().setDataPatchFailureCause(failureCause));
				} catch (RuntimeException e) {
					logger.error("Failure during patch operation for request: [{}], [{}]", request, singleFieldPatchResult, e);
					return singlePatchResult.setFailure(new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.TECHNICAL));
				}

			}).collect(Collectors.toList());

		List<CustomizableFieldSinglePatchingResult> customizableResults =
			customizableFieldDataPatcher.patch(new CustomizableFieldDataPatcher.Request());

		List<SinglePatchResult> aggregatedResults = Stream.concat(plainResults.stream(), customizableResults.stream()).collect(Collectors.toList());

		if (logger.isDebugEnabled()) {
			logger.debug("Aggregated patchResults: [{}]", aggregatedResults.stream().map(SinglePatchResult::getField).collect(Collectors.toList()));
		}

		Map<PatchField, Object> validPatchDictionary = buildDictionaryFor(aggregatedResults, SinglePatchResult::getValue, true);
		DataPatchResponse response = new DataPatchResponse().setApplied(false)
			.setFailures((LinkedHashMap<PatchField, DataPatchFailure>) buildDictionaryFor(aggregatedResults, SinglePatchResult::getFailure, false))
			.setValidPatchDictionary(new PatchDictionary().setNonTypedPatchDictionary(validPatchDictionary));

		if (validPatchDictionary.isEmpty() || (!request.isPatchedInCaseOfFailures() && response.hasFailures())) {
			logger.info(
				"No patch was applied as contained failures AND request doesn't allow patch in case of failures: request: [{}], response: [{}]",
				request,
				response);
			return response;
		}

		saveDTOsIfAppropriate(entityCache);

		saveCustomizableFieldsIfAppropriate(customizableResults);

		logger.debug("dataPatchResponse: [{}]", response);

		return response.setApplied(true);
	}

	private void saveCustomizableFieldsIfAppropriate(List<CustomizableFieldSinglePatchingResult> customizableResults) {
		List<CustomizableFieldValueDto> toSave = customizableResults.stream()
			.filter(result -> result.getFailure() == null)
			.map(CustomizableFieldSinglePatchingResult::getCustomizableFieldValue)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());

		if (toSave.isEmpty()) {
			logger.info("No customizable field values were modified / created, nothing to save");
			return;
		}

		customizableFieldDataPatcher.save(toSave);
	}

	@NotNull
	private PlainSinglePatchResult produceSinglePatchResult(
		CaseDataPatchRequest request,
		SingleFieldPatchResult singleFieldPatchResult,
		Disease disease,
		Supplier<AttachedEntityWrapper> target) {

		return invalidFieldResult(singleFieldPatchResult).or(() -> fieldMappingResult(singleFieldPatchResult, disease, request, target))
			.orElseGet(() -> valueMappingResult(singleFieldPatchResult, disease, request, target));
	}

	private void saveDTOsIfAppropriate(Map<Tuple<String, Integer>, AttachedEntityWrapper> entityCache) {
		List<EntityDto> toSave = entityCache.values().stream().map(AttachedEntityWrapper::getEntityDto).collect(Collectors.toList());

		if (toSave.isEmpty()) {
			logger.warn("Nothing to save in entity cache");
			return;
		}

		toSave.forEach(entity -> {
			logger.info("{} was modified, will be saved. Enable debug to see fully patched object", entity.getClass().getSimpleName());
			if (logger.isDebugEnabled()) {
				logger.debug("{}: \n{}", entity.getClass().getSimpleName(), ObjectMapperProvider.writeValueAsStringFailSafe(entity));
			}
		});

		businessDtoFacade.save(toSave);
	}

	private @NotNull <R> Map<PatchField, R> buildDictionaryFor(
		List<SinglePatchResult> results,
		Function<SinglePatchResult, R> fct,
		boolean valueContext) {
		return results.stream()
			// edge case were target value is null: this is allowed, which makes both fields null.
			.filter(
				singlePatchResult -> fct.apply(singlePatchResult) != null
					|| (valueContext && singlePatchResult.getFailure() == null && singlePatchResult.getValue() == null))
			.collect(CollectorUtils.toOrderedNullSafeMap(SinglePatchResult::getField, fct));
	}

	private @NotNull PlainSinglePatchResult valueMappingResult(
		SingleFieldPatchResult singleFieldPatchResult,
		Disease disease,
		CaseDataPatchRequest request,
		Supplier<AttachedEntityWrapper> targetOpt) {

		PatchField patchField = singleFieldPatchResult.field;
		String fullFieldName = patchField.getField();

		PlainSinglePatchResult singlePatchResult = new PlainSinglePatchResult().setField(patchField);

		AttachedEntityWrapper attachedEntityWrapper = targetOpt.get();
		Object target = attachedEntityWrapper.getEntityDto();
		String relativeFieldName = fullFieldName.substring(fullFieldName.indexOf('.') + 1);
		Tuple<Class<?>, PropertyAccessFailure> nestedPropertyTypeTuple =
			PropertyAccessor.getNestedPropertyType(target, relativeFieldName, getFieldVisibilityCheckers(disease));
		Object untypedTargetValue = singleFieldPatchResult.value;

		PropertyAccessFailure propertyAccessFailure = nestedPropertyTypeTuple.getSecond();
		if (propertyAccessFailure != null) {
			logger.info("Missing field: [{}] on target: [{}]", relativeFieldName, target);
			return singlePatchResult.setFailure(buildFailure(propertyAccessFailure.getRelatedPatchFailureCause(), untypedTargetValue));
		}
		Class<?> targetType = nestedPropertyTypeTuple.getFirst();

		ValueMappingResult<?> result = valueMapperRegistry.map(
			new ValuePatchRequest().setValue(untypedTargetValue)
				.setTargetType(targetType)
				.setInputLanguages(request.getInputLanguages())
				.setAllowFallbackValues(request.isAllowFallbackValues()));

		DataPatchFailureCause dataPatchFailureCause = result.getDataPatchFailureCause();
		if (dataPatchFailureCause != null) {
			return singlePatchResult.setFailure(buildFailure(dataPatchFailureCause, untypedTargetValue));
		}

		Object typedValue = result.getData();

		if (!attachedEntityWrapper.isAttached() && !StringUtils.contains(relativeFieldName, ".")) {
			logger.debug(
				"Entity was not yet attached and relative field name: [{}] is not for sub-Objects, therefore overwrite is allowed and ignored for this target only: [{}]",
				relativeFieldName,
				target.getClass());
		} else if (request.getReplacementStrategy() == DataReplacementStrategy.IF_NOT_ALREADY_PRESENT) {
			Optional<Object> nestedPropertyValue = PropertyAccessor.getNestedProperty(target, relativeFieldName);

			if (nestedPropertyValue.isPresent()) {
				Object currentValue = nestedPropertyValue.orElseThrow();

				if (!patchEqualityCheckersRegistry.areEqual(currentValue, typedValue)) {
					return singlePatchResult.setFailure(
						new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.FORBIDDEN_VALUE_OVERRIDE)
							.setExistingFieldValue(currentValue)
							.setProvidedFieldValue(untypedTargetValue));
				}
			}
		}

		Optional<Exception> exception = PropertyAccessor.setNestedProperty(target, relativeFieldName, typedValue);
		if (exception.isPresent()) {
			Exception e = exception.orElseThrow();
			logger.error("Setting nested property failed for: field [{}] on [{}] with value: [{}]", relativeFieldName, target, typedValue, e);
			return singlePatchResult.setFailure(
				new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.TECHNICAL).setProvidedFieldValue(untypedTargetValue));
		} else {
			return singlePatchResult.setValue(untypedTargetValue);
		}
	}

	private DataPatchFailure buildFailure(DataPatchFailureCause fieldDoesNotExist, Object untypedTargetValue) {
		return new DataPatchFailure().setDataPatchFailureCause(fieldDoesNotExist).setProvidedFieldValue(untypedTargetValue);
	}

	private @NotNull Optional<PlainSinglePatchResult> invalidFieldResult(SingleFieldPatchResult singleFieldPatchResult) {
		return Optional.ofNullable(singleFieldPatchResult.failureCause)
			.map(invalidFieldFailureCause -> buildFailureFor(singleFieldPatchResult, invalidFieldFailureCause));
	}

	private Optional<PlainSinglePatchResult> fieldMappingResult(
		SingleFieldPatchResult singleFieldPatchResult,
		Disease disease,
		CaseDataPatchRequest request,
		Supplier<AttachedEntityWrapper> target) {

		PatchField patchField = singleFieldPatchResult.field;
		String fullFieldName = patchField.getField();

		Optional<FieldCustomMapper> mapper = fieldCustomMapperRegistry.getMapper(fullFieldName, disease);

		Object untypedTargetValue = singleFieldPatchResult.value;
		if (mapper.isPresent()) {
			PlainSinglePatchResult singlePatchResult = new PlainSinglePatchResult().setField(patchField);

			Optional<DataPatchFailure> dataPatchFailureOpt = mapper.orElseThrow()
				.map(
					new FieldPatchRequest().setFieldName(fullFieldName)
						.setReplacementType(request.getReplacementStrategy())
						.setOrigin(request.getOrigin())
						.setTarget(target.get().getEntityDto())
						.setValue(untypedTargetValue));

			return dataPatchFailureOpt.map(singlePatchResult::setFailure).or(() -> Optional.of(singlePatchResult.setValue(untypedTargetValue)));
		}

		return Optional.empty();
	}

	private PlainSinglePatchResult buildFailureFor(SingleFieldPatchResult singleFieldPatchResult, DataPatchFailureCause fieldFailureCause) {
		return new PlainSinglePatchResult().setField(singleFieldPatchResult.field)
			.setFailure(buildFailure(fieldFailureCause, singleFieldPatchResult.value));
	}

	private FieldVisibilityCheckers getFieldVisibilityCheckers(Disease disease) {
		return FieldVisibilityCheckers.withCountry(configFacade.getCountryLocale())
			.andWithDisease(disease)
			.andWithFeatureType(featureConfigurationFacade.getActiveServerFeatureConfigurations());
	}

	private List<SingleFieldPatchResult> computePatchingTuples(CaseDataPatchRequest request) {
		Predicate<Map.Entry<PatchField, Object>> filterPredicate = buildAdequateDictionaryValuePredicate(request);

		return request.getPatchDictionary()
			.getDictionary()
			.entrySet()
			.stream()
			.filter(entry -> StringUtils.isNotBlank(entry.getKey().getField()))
			.filter(filterPredicate)
			.flatMap(originalEntry -> {
				PatchField patchField = originalEntry.getKey();
				String path = patchField.getField();

				PathFailureCause pathFailureCause = patchFieldHelper.checkIfPathIsInvalid(path);

				Tuple<String, PathFailureCause> unAliasedTuple = patchFieldHelper.resolveAlias(path);
				Map.Entry<PatchField, Object> entry = toMapEntry(patchField.setField(unAliasedTuple.getFirst()), originalEntry.getValue());

				DataPatchFailureCause dataPatchFailureCause = Optional.ofNullable(pathFailureCause)
					.map(PathFailureCause::getRelatedPatchFailureCause)
					.or(() -> Optional.ofNullable(unAliasedTuple.getSecond()).map(PathFailureCause::getRelatedPatchFailureCause))
					.orElse(null);

				if (dataPatchFailureCause != null) {
					return Stream.of(new SingleFieldPatchResult(entry.getKey(), dataPatchFailureCause, entry.getValue()));
				}

				if (!patchFieldHelper.isMultipleFieldFormat(path)) {
					return Stream.of(new SingleFieldPatchResult(entry.getKey(), null, entry.getValue()));
				}

				return splitMultipleFieldsPath(entry);
			})
			.collect(Collectors.toList());
	}

	private AbstractMap.@NotNull SimpleEntry<PatchField, Object> toMapEntry(PatchField first, Object value) {
		return new AbstractMap.SimpleEntry<>(first, value);
	}

	@NotNull
	private Stream<SingleFieldPatchResult> splitMultipleFieldsPath(Map.Entry<PatchField, Object> entry) {
		PatchField patchField = entry.getKey();
		return patchFieldHelper.splitMultipleFieldsPath(patchField.getField())
			.map(field -> new PatchField().setField(field).setGroupIndex(patchField.getGroupIndex()))
			.map(singlePath -> new SingleFieldPatchResult(singlePath, null, entry.getValue()));
	}

	private @NotNull Predicate<Map.Entry<PatchField, Object>> buildAdequateDictionaryValuePredicate(CaseDataPatchRequest request) {
		return request.getEmptyValueBehavior() == EmptyValueBehavior.REPLACE ? ignored -> true : buildEmptyValuePredicate();
	}

	private @NotNull CaseDataDto getCaseDataDto(CaseDataPatchRequest request) {
		String caseUuid = request.getCaseUuid();
		CaseDataDto caseData = businessDtoFacade.getCaseDataDtoNullable(caseUuid);

		if (caseData == null) {
			throw new IllegalStateException(String.format("No case found for uuid: [%s]", caseUuid));
		}

		return caseData;
	}

	private AttachedEntityWrapper findAppropriateTarget(
		PatchField patchField,
		CaseDataDto caseData,
		Map<Tuple<String, Integer>, AttachedEntityWrapper> entityCache) {

		String resolvedPath = patchField.getField();
		String prefix = extractPrefix(resolvedPath);

		Tuple<String, Integer> key = Tuple.of(prefix, patchField.getGroupIndex());

		if (entityCache.containsKey(key)) {
			return entityCache.get(key);
		}

		Optional<AttachedEntityWrapper> fetched = businessDtoFacade.tryFetchByI18nNameForCreateUpdate(prefix, caseData);
		if (fetched.isPresent()) {
			AttachedEntityWrapper appropriateEntityWrapper = fetched.get();

			if (patchField.getGroupIndex() != null && appropriateEntityWrapper.isAttached()) {
				throw new DataPatchingException(
					String.format(
						"The field [%s] is already attached, this means no new entities can be added as group: only a single instance is valid for the Case-'data-Tree'",
						prefix),
					DataPatchFailureCause.FORBIDDEN_MULTI_GROUP_FIELD);
			}

			entityCache.put(key, appropriateEntityWrapper);
			return appropriateEntityWrapper;
		}

		logger.error("Fallbacked to entity for resolved path: [{}]. This should not occur as CaseData is already in entityCache", resolvedPath);
		return AttachedEntityWrapper.attached(caseData);
	}

	private String extractPrefix(String fieldName) {
		int dotIndex = fieldName.indexOf('.');
		return dotIndex == -1 ? fieldName : fieldName.substring(0, dotIndex);
	}

	private Predicate<Map.Entry<PatchField, Object>> buildEmptyValuePredicate() {

		return stringObjectEntry -> {
			Object value = stringObjectEntry.getValue();

			if (value == null) {
				return false;
			}

			if (value instanceof String) {
				return !((String) value).trim().isEmpty();
			}

			return true;
		};
	}

	/**
	 * 
	 */
	// TODO: Extract & Rename
	public static final class SingleFieldPatchResult {

		private PatchField field;
		private DataPatchFailureCause failureCause;
		private Object value;

		SingleFieldPatchResult(PatchField fieldPath, DataPatchFailureCause cause, Object value) {
			this.field = fieldPath;
			this.failureCause = cause;
			this.value = value;
		}

		public SingleFieldPatchResult() {
		}

		public SingleFieldPatchResult setField(PatchField field) {
			this.field = field;
			return this;
		}

		public SingleFieldPatchResult setFailureCause(DataPatchFailureCause failureCause) {
			this.failureCause = failureCause;
			return this;
		}

		public SingleFieldPatchResult setValue(Object value) {
			this.value = value;
			return this;
		}

		public PatchField getField() {
			return field;
		}

		public DataPatchFailureCause getFailureCause() {
			return failureCause;
		}

		public Object getValue() {
			return value;
		}
	}

}
