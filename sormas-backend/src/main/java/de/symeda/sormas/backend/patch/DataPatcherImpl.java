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
import de.symeda.sormas.backend.patch.mapping.EqualityCheckerRegistry;
import de.symeda.sormas.backend.patch.mapping.FieldCustomMapperRegistry;
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
	private EqualityCheckerRegistry equalityCheckerRegistry;

	@Inject
	private BusinessDtoFacade businessDtoFacade;

	@EJB
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	public DataPatcherImpl() {
	}

	public DataPatcherImpl(
		PatchFieldHelper patchFieldHelper,
		ValueMapperRegistry valueMapperRegistry,
		FieldCustomMapperRegistry fieldCustomMapperRegistry,
		EqualityCheckerRegistry equalityCheckerRegistry,
		BusinessDtoFacade businessDtoFacade,
		FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade,
		ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade) {
		this.patchFieldHelper = patchFieldHelper;
		this.valueMapperRegistry = valueMapperRegistry;
		this.fieldCustomMapperRegistry = fieldCustomMapperRegistry;
		this.equalityCheckerRegistry = equalityCheckerRegistry;
		this.businessDtoFacade = businessDtoFacade;
		this.featureConfigurationFacade = featureConfigurationFacade;
		this.configFacade = configFacade;
	}

	@Override
	public DataPatchResponse patch(CaseDataPatchRequest request) {
		logger.debug("patch: [{}]", request);

		CaseDataDto caseData = getCaseDataDto(request);

		Disease disease = caseData.getDisease();

		Map<String, AttachedEntityWrapper> entityCache = new HashMap<>();
		entityCache.put(CaseDataDto.I18N_PREFIX, AttachedEntityWrapper.attached(caseData));

		List<SingleFieldPatchResult> patchingTuples = computePatchingTuples(request);

		List<SinglePatchResult> results = patchingTuples.stream().map(singleFieldPatchResult -> {
			String fullFieldName = singleFieldPatchResult.path;
			SinglePatchResult singlePatchResult = new SinglePatchResult().setFieldName(fullFieldName);

			Supplier<AttachedEntityWrapper> target = () -> findAppropriateTarget(fullFieldName, caseData, entityCache);

			try {
				return produceSinglePatchResult(request, singleFieldPatchResult, disease, target);
			} catch (RuntimeException e) {
				logger.error("Failure during patch operation", e);
				return singlePatchResult
					.setFailure(new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.TECHNICAL).setDescription(e.getMessage()));
			}

		}).collect(Collectors.toList());

		Map<String, Object> validPatchDictionary = buildDictionaryFor(results, SinglePatchResult::getValue, true);
		DataPatchResponse response = new DataPatchResponse().setApplied(false)
			.setFailures(buildDictionaryFor(results, SinglePatchResult::getFailure, false))
			.setValidPatchDictionary(validPatchDictionary);

		if (validPatchDictionary.isEmpty() || (!request.isPatchedInCaseOfFailures() && response.hasFailures())) {
			logger.info(
				"No patch was applied as contained failures AND request doesn't allow patch in case of failures: request: [{}], response: [{}]",
				request,
				response);
			return response;
		}

		saveDTOsIfAppropriate(entityCache);

		logger.debug("dataPatchResponse: [{}]", response);

		return response.setApplied(true);
	}

	@NotNull
	private SinglePatchResult produceSinglePatchResult(
		CaseDataPatchRequest request,
		SingleFieldPatchResult singleFieldPatchResult,
		Disease disease,
		Supplier<AttachedEntityWrapper> target) {

		return invalidFieldResult(singleFieldPatchResult).or(() -> fieldMappingResult(singleFieldPatchResult, disease, request, target))
			.orElseGet(() -> valueMappingResult(singleFieldPatchResult, disease, request, target));
	}

	private void saveDTOsIfAppropriate(Map<String, AttachedEntityWrapper> entityCache) {
		List<EntityDto> toSave = new ArrayList<>(entityCache.values().stream().map(AttachedEntityWrapper::getEntityDto).collect(Collectors.toList()));

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

	private @NotNull <R> Map<String, R> buildDictionaryFor(
		List<SinglePatchResult> results,
		Function<SinglePatchResult, R> fct,
		boolean valueContext) {
		return results.stream()
			// edge case were target value is null: this is allowed, which makes both fields null.
			.filter(
				singlePatchResult -> fct.apply(singlePatchResult) != null
					|| (valueContext && singlePatchResult.getFailure() == null && singlePatchResult.getValue() == null))
			.collect(CollectorUtils.toNullSafeMap(SinglePatchResult::getFieldName, fct));
	}

	private @NotNull SinglePatchResult valueMappingResult(
		SingleFieldPatchResult singleFieldPatchResult,
		Disease disease,
		CaseDataPatchRequest request,
		Supplier<AttachedEntityWrapper> targetOpt) {

		String fullFieldName = singleFieldPatchResult.path;

		SinglePatchResult singlePatchResult = new SinglePatchResult().setFieldName(fullFieldName);

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

				if (!equalityCheckerRegistry.areEqual(currentValue, typedValue)) {
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
			logger.error("Setting nested property failed", e);
			return singlePatchResult.setFailure(
				new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.TECHNICAL)
					.setDescription(e.getMessage())
					.setProvidedFieldValue(untypedTargetValue));
		} else {
			return singlePatchResult.setValue(untypedTargetValue);
		}
	}

	private DataPatchFailure buildFailure(DataPatchFailureCause fieldDoesNotExist, Object untypedTargetValue) {
		return new DataPatchFailure().setDataPatchFailureCause(fieldDoesNotExist).setProvidedFieldValue(untypedTargetValue);
	}

	private @NotNull Optional<SinglePatchResult> invalidFieldResult(SingleFieldPatchResult singleFieldPatchResult) {
		return Optional.ofNullable(singleFieldPatchResult.failureCause)
			.map(invalidFieldFailureCause -> buildFailureFor(singleFieldPatchResult, invalidFieldFailureCause));
	}

	private Optional<SinglePatchResult> fieldMappingResult(
		SingleFieldPatchResult singleFieldPatchResult,
		Disease disease,
		CaseDataPatchRequest request,
		Supplier<AttachedEntityWrapper> target) {

		String fullFieldName = singleFieldPatchResult.path;

		Optional<FieldCustomMapper> mapper = fieldCustomMapperRegistry.getMapper(fullFieldName, disease);

		Object untypedTargetValue = singleFieldPatchResult.value;
		if (mapper.isPresent()) {
			SinglePatchResult singlePatchResult = new SinglePatchResult().setFieldName(fullFieldName);

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

	private SinglePatchResult buildFailureFor(SingleFieldPatchResult singleFieldPatchResult, DataPatchFailureCause fieldFailureCause) {
		return new SinglePatchResult().setFieldName(singleFieldPatchResult.path)
			.setFailure(buildFailure(fieldFailureCause, singleFieldPatchResult.value));
	}

	private FieldVisibilityCheckers getFieldVisibilityCheckers(Disease disease) {
		return FieldVisibilityCheckers.withCountry(configFacade.getCountryLocale())
			.andWithDisease(disease)
			.andWithFeatureType(featureConfigurationFacade.getActiveServerFeatureConfigurations());
	}

	private List<SingleFieldPatchResult> computePatchingTuples(CaseDataPatchRequest request) {
		Predicate<Map.Entry<String, Object>> filterPredicate = buildAdequateDictionaryValuePredicate(request);

		return request.getPatchDictionary()
			.entrySet()
			.stream()
			.filter(entry -> StringUtils.isNotBlank(entry.getKey()))
			.filter(filterPredicate)
			.flatMap(originalEntry -> {
				String path = originalEntry.getKey();

				PathFailureCause pathFailureCause = patchFieldHelper.checkIfPathIsInvalid(path);

				Tuple<String, PathFailureCause> unAliasedTuple = patchFieldHelper.resolveAlias(path);
				Map.Entry<String, Object> entry = toMapEntry(unAliasedTuple.getFirst(), originalEntry.getValue());

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

	private AbstractMap.@NotNull SimpleEntry<String, Object> toMapEntry(String first, Object value) {
		return new AbstractMap.SimpleEntry<>(first, value);
	}

	@NotNull
	private Stream<SingleFieldPatchResult> splitMultipleFieldsPath(Map.Entry<String, Object> entry) {
		String path = entry.getKey();
		int openingParenthesisIndex = path.indexOf("(");
		String prefix = path.substring(0, openingParenthesisIndex);

		int closeParen = path.indexOf(')');

		String restPath = path.substring(openingParenthesisIndex + 1, closeParen);

		return Arrays.stream(restPath.split("\\|")).map(suffix -> new SingleFieldPatchResult(prefix + suffix, null, entry.getValue()));
	}

	private @NotNull Predicate<Map.Entry<String, Object>> buildAdequateDictionaryValuePredicate(CaseDataPatchRequest request) {
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

	private AttachedEntityWrapper findAppropriateTarget(String resolvedPath, CaseDataDto caseData, Map<String, AttachedEntityWrapper> entityCache) {
		String prefix = extractPrefix(resolvedPath);

		if (entityCache.containsKey(prefix)) {
			return entityCache.get(prefix);
		}

		Optional<AttachedEntityWrapper> fetched = businessDtoFacade.tryFetchByI18nNameForCreateUpdate(prefix, caseData);
		if (fetched.isPresent()) {
			entityCache.put(prefix, fetched.get());
			return fetched.get();
		}

		logger.error("Fallbacked to entity for resolved path: [{}]. This should not occur as CaseData is already in entityCache", resolvedPath);
		return AttachedEntityWrapper.attached(caseData);
	}

	private String extractPrefix(String fieldName) {
		int dotIndex = fieldName.indexOf('.');
		return dotIndex == -1 ? fieldName : fieldName.substring(0, dotIndex);
	}

	private Predicate<Map.Entry<String, Object>> buildEmptyValuePredicate() {

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

	private static final class SingleFieldPatchResult {

		final String path;
		final DataPatchFailureCause failureCause;
		final Object value;

		SingleFieldPatchResult(String fieldPath, DataPatchFailureCause cause, Object value) {
			this.path = fieldPath;
			this.failureCause = cause;
			this.value = value;
		}
	}

}
