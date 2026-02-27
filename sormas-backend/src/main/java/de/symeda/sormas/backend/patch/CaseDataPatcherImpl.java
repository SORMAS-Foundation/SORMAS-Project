package de.symeda.sormas.backend.patch;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.patch.CaseDataPatchRequest;
import de.symeda.sormas.api.patch.CaseDataPatcher;
import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.DataPatchResponse;
import de.symeda.sormas.api.patch.DataReplacementStrategy;
import de.symeda.sormas.api.patch.EmptyValueBehavior;
import de.symeda.sormas.api.patch.mapping.FieldCustomMapper;
import de.symeda.sormas.api.patch.mapping.FieldPatchRequest;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.json.ObjectMapperProvider;
import de.symeda.sormas.backend.patch.mapping.FieldCustomMapperRegistry;
import de.symeda.sormas.backend.patch.mapping.ValueMapperRegistry;
import de.symeda.sormas.backend.person.PersonFacadeEjb;

// TODO: test integration vaccines
@Stateless
@LocalBean
public class CaseDataPatcherImpl implements CaseDataPatcher {

	public static final String PERSON_FIELD_NAME_PREFIX = "Person.";

	private final static Logger logger = LoggerFactory.getLogger(CaseDataPatcherImpl.class);

	@Inject
	private ValueMapperRegistry valueMapperRegistry;

	@Inject
	private FieldCustomMapperRegistry fieldCustomMapperRegistry;

	@Inject
	private PatchFieldHelper patchFieldHelper;

	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;

	@EJB
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	public CaseDataPatcherImpl() {
	}

	public CaseDataPatcherImpl(
		ValueMapperRegistry valueMapperRegistry,
		FieldCustomMapperRegistry fieldCustomMapperRegistry,
		PatchFieldHelper patchFieldHelper,
		CaseFacadeEjb.CaseFacadeEjbLocal caseFacade,
		PersonFacadeEjb.PersonFacadeEjbLocal personFacade,
		FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade,
		ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade) {
		this.valueMapperRegistry = valueMapperRegistry;
		this.fieldCustomMapperRegistry = fieldCustomMapperRegistry;
		this.patchFieldHelper = patchFieldHelper;
		this.caseFacade = caseFacade;
		this.personFacade = personFacade;
		this.featureConfigurationFacade = featureConfigurationFacade;
		this.configFacade = configFacade;
	}

	@Override
	public DataPatchResponse patch(CaseDataPatchRequest request) {
		logger.debug("patch: [{}]", request);

		CaseDataDto caseData = getCaseDataDto(request);

		Disease disease = caseData.getDisease();

		LazySupplier<PersonDto> personSupplier = LazySupplier.of(() -> getPersonDto(caseData));

		List<SinglePatchResult> results = computeActualDictionary(request).entrySet().stream().map(entry -> {
			Tuple<String, DataPatchFailureCause> tuple = entry.getKey();
			String fullFieldName = tuple.getFirst();
			SinglePatchResult singlePatchResult = new SinglePatchResult().setFieldName(fullFieldName);

			Supplier<Object> target = () -> findAppropriateTarget(fullFieldName, caseData, personSupplier);

			try {

				return invalidFieldResult(entry, tuple).or(() -> fieldMappingResult(entry, disease, request, target))
					.orElseGet(() -> valueMappingResult(entry, disease, request, target));
			} catch (RuntimeException e) {
				logger.error("Failure during patch operation", e);
				return singlePatchResult
					.setFailure(new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.TECHNICAL).setDescription(e.getMessage()));
			}

		}).collect(Collectors.toList());

		DataPatchResponse response = new DataPatchResponse().setApplied(false)
			.setFailures(buildDictionaryFor(results, SinglePatchResult::getFailure))
			.setValidPatchDictionary(buildDictionaryFor(results, SinglePatchResult::getValue));
		if (!request.isPatchedInCaseOfFailures() && response.hasFailures()) {
			logger.info(
				"No patch was applied as contained failures AND request doesn't allow patch in case of failures: request: [{}], response: [{}]",
				request,
				response);
			return response;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("CaseData: \n{}", ObjectMapperProvider.writeValueAsStringFailSafe(caseData));
		}
		caseFacade.save(caseData);

		if (logger.isDebugEnabled()) {
			logger.debug("Person: \n{}", ObjectMapperProvider.writeValueAsStringFailSafe(personSupplier.get()));
		}

		if (personSupplier.isLoaded()) {
			logger.info("Person was loaded, therefore will be updated");
			personFacade.save(personSupplier.get());
		}

		logger.debug("dataPatchResponse: [{}]", response);

		return response.setApplied(true);
	}

	private @NotNull <R> Map<String, R> buildDictionaryFor(List<SinglePatchResult> results, Function<SinglePatchResult, R> fct) {
		return results.stream()
			.filter(singlePatchResult -> fct.apply(singlePatchResult) != null)
			.collect(Collectors.toMap(SinglePatchResult::getFieldName, fct));
	}

	private @NotNull SinglePatchResult valueMappingResult(
		Map.Entry<Tuple<String, DataPatchFailureCause>, Object> entry,
		Disease disease,
		CaseDataPatchRequest request,
		Supplier<Object> targetOpt) {

		Tuple<String, DataPatchFailureCause> tuple = entry.getKey();
		String fullFieldName = tuple.getFirst();

		SinglePatchResult singlePatchResult = new SinglePatchResult().setFieldName(fullFieldName);

		Object target = targetOpt.get();
		String relativeFieldName = fullFieldName.substring(fullFieldName.indexOf('.') + 1);
		Optional<Tuple<Class<?>, Boolean>> nestedPropertyType =
			PropertyAccessor.getNestedPropertyType(target, relativeFieldName, getFieldVisibilityCheckers(disease));

		if (nestedPropertyType.isEmpty()) {
			logger.info("Missing field: [{}] on target: [{}]", relativeFieldName, target);
			return singlePatchResult.setFailure(new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.FIELD_DOES_NOT_EXIST));
		}
		Tuple<Class<?>, Boolean> classSetTuple = nestedPropertyType.orElseThrow();
		Class<?> targetType = classSetTuple.getFirst();

		Object untypedTargetValue = entry.getValue();

		if (!Boolean.TRUE.equals(classSetTuple.getSecond())) {
			logger.info("Field: [{}] on object [{}] cannot be patched for disease: [{}]", relativeFieldName, target, disease);
			return singlePatchResult.setFailure(
				new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE)
					.setProvidedFieldValue(untypedTargetValue));
		}

		// TODO: handle targetType being a list. TO-Check with business: add / replace

		ValueMappingResult<?> result = valueMapperRegistry.map(
			new ValuePatchRequest().setValue(untypedTargetValue)
				.setTargetType(targetType)
				.setInputLanguages(request.getInputLanguages())
				.setAllowFallbackValues(request.isAllowFallbackValues()));

		DataPatchFailureCause dataPatchFailureCause = result.getDataPatchFailureCause();
		if (dataPatchFailureCause != null) {
			return singlePatchResult
				.setFailure(new DataPatchFailure().setDataPatchFailureCause(dataPatchFailureCause).setProvidedFieldValue(untypedTargetValue));
		}

		Object typedValue = result.getData();

		if (request.getReplacementStrategy() == DataReplacementStrategy.IF_NOT_ALREADY_PRESENT) {
			Optional<Object> nestedPropertyValue = PropertyAccessor.getNestedProperty(target, relativeFieldName);

			if (nestedPropertyValue.isPresent()) {
				Object currentValue = nestedPropertyValue.orElseThrow();

				if (!currentValue.equals(typedValue)) {
					return singlePatchResult.setFailure(
						new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.FORBIDDEN_VALUE_OVERRIDE)
							.setExistingFieldValue(currentValue)
							.setProvidedFieldValue(untypedTargetValue));
				}
			}
		}

		Optional<Exception> exception = PropertyAccessor.setNestedProperty(target, relativeFieldName, typedValue);
		if (exception.isPresent()) {
			return singlePatchResult.setFailure(
				new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.TECHNICAL)
					.setDescription(exception.orElseThrow().getMessage()));
		} else {
			return singlePatchResult.setValue(untypedTargetValue);
		}
	}

	private @NotNull Optional<SinglePatchResult> invalidFieldResult(
		Map.Entry<Tuple<String, DataPatchFailureCause>, Object> entry,
		Tuple<String, DataPatchFailureCause> tuple) {

		return Optional.ofNullable(tuple.getSecond()).map(invalidFieldFailureCause -> buildFailureFor(entry, tuple.getSecond()));
	}

	public Optional<SinglePatchResult> fieldMappingResult(
		Map.Entry<Tuple<String, DataPatchFailureCause>, Object> entry,
		Disease disease,
		CaseDataPatchRequest request,
		Supplier<Object> target) {

		Tuple<String, DataPatchFailureCause> tuple = entry.getKey();
		String fullFieldName = tuple.getFirst();

		Optional<FieldCustomMapper> mapper = fieldCustomMapperRegistry.getMapper(fullFieldName, disease);

		Object untypedTargetValue = entry.getValue();
		if (mapper.isPresent()) {
			SinglePatchResult singlePatchResult = new SinglePatchResult().setFieldName(fullFieldName);

			Optional<DataPatchFailure> dataPatchFailureOpt = mapper.orElseThrow()
				.map(
					new FieldPatchRequest().setFieldName(fullFieldName)
						.setReplacementType(request.getReplacementStrategy())
						.setOrigin(request.getOrigin())
						.setTarget(target.get())
						.setValue(untypedTargetValue));

			return dataPatchFailureOpt.map(singlePatchResult::setFailure).or(() -> Optional.of(singlePatchResult.setValue(untypedTargetValue)));
		}

		return Optional.empty();
	}

	private SinglePatchResult buildFailureFor(
		Map.Entry<Tuple<String, DataPatchFailureCause>, Object> entry,
		DataPatchFailureCause fieldFailureCause) {

		return new SinglePatchResult().setFieldName(entry.getKey().getFirst())
			.setFailure(new DataPatchFailure().setDataPatchFailureCause(fieldFailureCause).setProvidedFieldValue(entry.getValue()));
	}

	private FieldVisibilityCheckers getFieldVisibilityCheckers(Disease disease) {
		return FieldVisibilityCheckers.withCountry(configFacade.getCountryLocale())
			.andWithDisease(disease)
			.andWithFeatureType(featureConfigurationFacade.getActiveServerFeatureConfigurations());
	}

	private Map<Tuple<String, DataPatchFailureCause>, Object> computeActualDictionary(CaseDataPatchRequest request) {
		Predicate<Map.Entry<String, Object>> filterPredicate = buildAdequateDictionaryValuePredicate(request);

		return request.getPatchDictionary()
			.entrySet()
			.stream()
			.filter(entry -> StringUtils.isNotBlank(entry.getKey()))
			.filter(filterPredicate)
			.flatMap(entry -> {
				String path = entry.getKey();

				DataPatchFailureCause dataPatchFailureCause = patchFieldHelper.checkIfPathIsInvalid(path);

				if (dataPatchFailureCause != null) {
					return Stream.of(buildMapTupleEntryFrom(entry, dataPatchFailureCause));
				}

				if (!patchFieldHelper.isMultipleFieldFormat(path)) {
					return Stream.of(buildMapTupleEntryFrom(entry));
				}

				return splitMultipleFieldsPath(entry);
			})
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@NotNull
	private Stream<Map.Entry<Tuple<String, DataPatchFailureCause>, Object>> splitMultipleFieldsPath(Map.Entry<String, Object> entry) {
		String path = entry.getKey();
		int openingParenthesisIndex = path.indexOf("(");
		String prefix = path.substring(0, openingParenthesisIndex);

		int closeParen = path.indexOf(')');

		logger.error(path);
		String restPath = path.substring(openingParenthesisIndex + 1, closeParen);

		return Arrays.stream(restPath.split("\\|")).map(suffix -> Map.entry(new Tuple<>(prefix + suffix, null), entry.getValue()));
	}

	private Map.Entry<Tuple<String, DataPatchFailureCause>, Object> buildMapTupleEntryFrom(
		Map.Entry<String, Object> entry,
		@Nullable DataPatchFailureCause dataPatchFailureCause) {
		return Map.entry(new Tuple<>(entry.getKey(), dataPatchFailureCause), entry.getValue());
	}

	private Map.Entry<Tuple<String, DataPatchFailureCause>, Object> buildMapTupleEntryFrom(Map.Entry<String, Object> entry) {
		return Map.entry(new Tuple<>(entry.getKey(), null), entry.getValue());
	}

	private @NotNull Predicate<Map.Entry<String, Object>> buildAdequateDictionaryValuePredicate(CaseDataPatchRequest request) {
		return request.getEmptyValueBehavior() == EmptyValueBehavior.REPLACE ? ignored -> true : buildEmptyValuePredicate();
	}

	private @NotNull PersonDto getPersonDto(CaseDataDto caseData) {
		String personUuid = caseData.getPerson().getUuid();
		PersonDto person = personFacade.getByUuid(personUuid);

		if (person == null) {
			throw new IllegalStateException(String.format("No person found for uuid: [%s]", personUuid));
		}
		return person;
	}

	private @NotNull CaseDataDto getCaseDataDto(CaseDataPatchRequest request) {
		String caseUuid = request.getCaseUuid();
		CaseDataDto caseData = caseFacade.getCaseDataByUuid(caseUuid);

		if (caseData == null) {
			throw new IllegalStateException(String.format("No case found for uuid: [%s]", caseUuid));
		}
		return caseData;
	}

	private Object findAppropriateTarget(String fieldName, CaseDataDto caseData, Supplier<PersonDto> person) {
		if (fieldName.startsWith(PERSON_FIELD_NAME_PREFIX)) {
			return person.get();
		}

		return caseData;
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

	public void setValueMapperRegistry(ValueMapperRegistry valueMapperRegistry) {
		this.valueMapperRegistry = valueMapperRegistry;
	}

	public void setFieldCustomMapperRegistry(FieldCustomMapperRegistry fieldCustomMapperRegistry) {
		this.fieldCustomMapperRegistry = fieldCustomMapperRegistry;
	}

	public void setCaseFacade(CaseFacadeEjb.CaseFacadeEjbLocal caseFacade) {
		this.caseFacade = caseFacade;
	}

	public void setPersonFacade(PersonFacadeEjb.PersonFacadeEjbLocal personFacade) {
		this.personFacade = personFacade;
	}
}
