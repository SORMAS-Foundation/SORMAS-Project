package de.symeda.sormas.backend.patch;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

import com.google.common.base.Suppliers;

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
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
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
	public static final String OPENING_PARENTHESIS = "(";
	public static final String CLOSING_PARENTHESIS = ")";
	public static final String PIPE = "|";

	// might be more subtle: person.toto but also *.uuid (or uuid). includes approach ?
	// TODO: must be twofold: enforced default fields : technical: uuid, user ... + custom config by admin
	private Set<String> forbiddenFields = Set.of("Person.birthdate");

	private Set<String> allowedPrefixes = Set.of("Person.", "CaseData");

	@Inject
	private ValueMapperRegistry valueMapperRegistry;

	@Inject
	private FieldCustomMapperRegistry fieldCustomMapperRegistry;

	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;

	@Override
	public DataPatchResponse patch(CaseDataPatchRequest request) {
		logger.info("patch: [{}]", request);

		CaseDataDto caseData = getCaseDataDto(request);

		Supplier<PersonDto> person = Suppliers.memoize(() -> getPersonDto(caseData));

		Map<Tuple<String, DataPatchFailureCause>, Object> actualDictionary = computeActualDictionary(request);

		// TODO: refactor this to create smaller units
		// TODO: duplicate field patching mechanism
		List<SinglePatchResult> results = actualDictionary.entrySet().stream().map(entry -> {
			Tuple<String, DataPatchFailureCause> tuple = entry.getKey();
			String fullFieldName = tuple.getFirst();
			SinglePatchResult singlePatchResult = new SinglePatchResult().setFieldName(fullFieldName);

			Object target = findAppropriateTarget(fullFieldName, caseData, person);

			try {
				DataPatchFailureCause fieldFailureCause = tuple.getSecond();
				if (fieldFailureCause != null) {
					return singlePatchResult.setFailure(new DataPatchFailure().setDataPatchFailureCause(fieldFailureCause));
				}

				Optional<FieldCustomMapper> mapper = fieldCustomMapperRegistry.getMapper(fullFieldName);

				Object untypedTargetValue = entry.getValue();
				if (mapper.isPresent()) {
					Optional<DataPatchFailure> dataPatchFailureOpt = mapper.orElseThrow()
						.map(
							new FieldPatchRequest().setFieldName(fullFieldName)
								.setReplacementType(request.getReplacementStrategy())
								.setTarget(target)
								.setValue(untypedTargetValue));

					if (dataPatchFailureOpt.isPresent()) {
						return singlePatchResult.setFailure(dataPatchFailureOpt.get());
					}

					// TODO: taint the DTO to mark it as dirty
					return singlePatchResult.setValue(untypedTargetValue);
				}

				String relativeFieldName = fullFieldName.substring(fullFieldName.indexOf('.') + 1);
				Optional<Class<?>> nestedPropertyType = PropertyAccessor.getNestedPropertyType(target, relativeFieldName);

				if (nestedPropertyType.isEmpty()) {
					logger.info("Missing field: [{}] on target: [{}]", relativeFieldName, target);
					return singlePatchResult.setFailure(new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.FIELD_DOES_NOT_EXIST));
				}
				Class<?> targetType = nestedPropertyType.orElseThrow();

				// TODO: handle targetType being a list. TO-Check with business: add / replace 

				Object typedValue = valueMapperRegistry.map(untypedTargetValue, targetType);

				if (request.getReplacementStrategy() == DataReplacementStrategy.IF_NOT_ALREADY_PRESENT) {
					Optional<Object> nestedPropertyValue = PropertyAccessor.getNestedProperty(target, relativeFieldName);

					if (nestedPropertyValue.isPresent()) {
						Object currentValue = nestedPropertyValue.orElseThrow();

						if (!currentValue.equals(typedValue)) {
							return singlePatchResult.setFailure(
								new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.FORBIDDEN_VALUE_OVERRIDE)
									.setExistingFieldValue(currentValue)
									.setProvidedFieldValue(typedValue));
						}
					}
				}

				// TODO: taint the DTO to mark it as dirty
				Optional<Exception> exception = PropertyAccessor.setNestedProperty(target, relativeFieldName, typedValue);
				if (exception.isPresent()) {
					return singlePatchResult.setFailure(
						new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.TECHNICAL)
							.setDescription(exception.orElseThrow().getMessage()));
				} else {
					return singlePatchResult.setValue(typedValue);
				}
			} catch (RuntimeException e) {
				logger.error("Failure during patch operation", e);
				return singlePatchResult
					.setFailure(new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.TECHNICAL).setDescription(e.getMessage()));
			}

		}).collect(Collectors.toList());

		Map<String, Object> patchedValuesDictionary = results.stream()
			.filter(singlePatchResult -> singlePatchResult.getValue() != null)
			.collect(Collectors.toMap(SinglePatchResult::getFieldName, SinglePatchResult::getValue));

		Map<String, DataPatchFailure> failuresDictionary = results.stream()
			.filter(singlePatchResult -> singlePatchResult.getFailure() != null)
			.collect(Collectors.toMap(SinglePatchResult::getFieldName, SinglePatchResult::getFailure));

		DataPatchResponse dataPatchResponse = new DataPatchResponse().setPatchDictionary(patchedValuesDictionary).setFailures(failuresDictionary);

		logger.info("patch results: [{}]", results);

		// TODO: not necessarly both to be saved
		// TODO:
		if (logger.isErrorEnabled()) {
			logger.error("CaseData: \n{}", ObjectMapperProvider.writeValueAsStringFailSafe(caseData));
			System.out.println(ObjectMapperProvider.writeValueAsStringFailSafe(caseData));
		}
		caseFacade.save(caseData);

		if (logger.isErrorEnabled()) {
			logger.error("Person: \n{}", ObjectMapperProvider.writeValueAsStringFailSafe(person.get()));
		}
		personFacade.save(person.get());

		return dataPatchResponse;

		/*
		 * Implementation steps:
		 * - lazily produce list of allowed fields to avoid.
		 * - OK: Iterate over patch dictionary
		 * - OK: Filter out empty values.
		 * - OK: Check for forbidden fields
		 * - OK: Check for FieldCustomMapper to use custom mapping strategy
		 * - OK: Check if field exists.
		 * - Go to the appropriate (sub) field
		 * - TODO: if appropriate: multiple patching into same field strategy!!
		 * <p>
		 * WARN: Root will be either: (breaks trivial check if exists approach).
		 * - CaseData
		 * - Person
		 */
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

				DataPatchFailureCause dataPatchFailureCause = checkIfPathIsInvalid(path);

				if (dataPatchFailureCause != null) {
					return Stream.of(buildMapTupleEntryFrom(entry, dataPatchFailureCause));
				}

				if (isNotMultipleFieldFormat(path)) {
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

		String restPath = path.substring(openingParenthesisIndex + 1, closeParen);

		return Arrays.stream(restPath.split("\\|")).map(suffix -> Map.entry(new Tuple<>(prefix + suffix, null), entry.getValue()));
	}

	private boolean isNotMultipleFieldFormat(String path) {
		return !(path.contains(OPENING_PARENTHESIS) || path.contains(CLOSING_PARENTHESIS) || path.contains(PIPE));
	}

	// TODO: could be extracted into another class
	@Nullable
	private DataPatchFailureCause checkIfPathIsInvalid(String path) {
		DataPatchFailureCause dataPatchFailureCause = null;

		if (!startsWithAllowedPrefix(path)) {
			dataPatchFailureCause = DataPatchFailureCause.UNSUPPORTED_PREFIX;
		} else if (fieldIsForbidden(path)) {
			dataPatchFailureCause = DataPatchFailureCause.FORBIDDEN_FIELD;
		} else if (fieldIsInvalidMultiField(path)) {
			dataPatchFailureCause = DataPatchFailureCause.INVALID_MULTIPLE_FIELDS_FORMAT;
		}
		return dataPatchFailureCause;
	}

	private boolean fieldIsInvalidMultiField(String path) {
		if (isNotMultipleFieldFormat(path)) {
			return false;
		}

		long openCount = path.chars().filter(c -> c == '(').count();
		long closeCount = path.chars().filter(c -> c == ')').count();
		int openIndex = path.indexOf('(');
		int closeIndex = path.lastIndexOf(')');

		if (openCount != 1 || closeCount != 1) {
			logger.debug("Path must contain exactly one pair of parentheses: [" + path + "]");
			return false;
		}

		if (openIndex > closeIndex) {
			logger.debug("Closing parenthesis appears before opening parenthesis: [" + path + "]");
			return false;
		}

		if (closeIndex != path.length() - 1) {
			logger.debug("Closing parenthesis must be at the end of the path: [" + path + "]");
			return false;
		}

		String alternatives = path.substring(openIndex + 1, closeIndex);

		if (alternatives.isBlank()) {
			logger.debug("Empty parentheses — nothing between '(' and ')': [" + path + "]");
			return false;
		}

		String[] parts = alternatives.split("\\|");
		for (String part : parts) {
			if (part.isBlank()) {
				logger.debug("Empty alternative found — consecutive or leading/trailing pipes: [" + path + "]");
				return false;
			}
		}

		return true;
	}

	private Map.Entry<Tuple<String, DataPatchFailureCause>, Object> buildMapTupleEntryFrom(
		Map.Entry<String, Object> entry,
		@Nullable DataPatchFailureCause dataPatchFailureCause) {
		return Map.entry(new Tuple<>(entry.getKey(), dataPatchFailureCause), entry.getValue());
	}

	private Map.Entry<Tuple<String, DataPatchFailureCause>, Object> buildMapTupleEntryFrom(Map.Entry<String, Object> entry) {
		return Map.entry(new Tuple<>(entry.getKey(), null), entry.getValue());
	}

	private boolean startsWithAllowedPrefix(String path) {
		return allowedPrefixes.stream().anyMatch(path::startsWith);
	}

	private boolean fieldIsForbidden(String path) {
		return forbiddenFields.contains(path);
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

	public void setForbiddenFields(Set<String> forbiddenFields) {
		this.forbiddenFields = forbiddenFields;
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
