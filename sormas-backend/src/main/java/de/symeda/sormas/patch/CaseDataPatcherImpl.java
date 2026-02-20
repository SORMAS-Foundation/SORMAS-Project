package de.symeda.sormas.patch;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

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
import de.symeda.sormas.api.patch.DataReplacementType;
import de.symeda.sormas.api.patch.EmptyValueBehavior;
import de.symeda.sormas.api.patch.mapping.FieldCustomMapper;
import de.symeda.sormas.api.patch.mapping.FieldPatchRequest;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.patch.mapping.FieldCustomMapperRegistry;
import de.symeda.sormas.patch.mapping.ValueMapperRegistry;

@Stateless
public class CaseDataPatcherImpl implements CaseDataPatcher {

	public static final String PERSON_FIELD_NAME_PREFIX = "Person.";
	private final Logger logger = LoggerFactory.getLogger(getClass());

	// might be more subtle: person.toto but also *.uuid (or uuid). includes approach ?
	// TODO: must be twofold: enforced default fields : technical: uuid, user ... + custom config by admin
	private Set<String> forbiddenFields = Set.of();

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
		logger.debug("patch: [{}]", request);

		CaseDataDto caseData = getCaseDataDto(request);

		// TODO: only fetch person when needed.
		Supplier<PersonDto> person = Suppliers.memoize(() -> getPersonDto(caseData));

		Map<String, Object> actualDictionary = computeActualDictionary(request);

		List<SinglePatchResult> results = actualDictionary.entrySet().stream().map(entry -> {
			String fieldName = entry.getKey();
			SinglePatchResult singlePatchResult = new SinglePatchResult().setFieldName(fieldName);

			Object target = findAppropriateTarget(fieldName, caseData, person);

			try { // TODO: patch the same field twice ?

				if (forbiddenFields.contains(fieldName)) {
					return singlePatchResult.setFailure(new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.FORBIDDEN_FIELD));
				}

				Optional<FieldCustomMapper> mapper = fieldCustomMapperRegistry.getMapper(fieldName);

				Object untypedTargetValue = entry.getValue();
				if (mapper.isPresent()) {
					Optional<DataPatchFailure> dataPatchFailureOpt = mapper.orElseThrow()
						.map(
							new FieldPatchRequest().setFieldName(fieldName)
								.setReplacementType(request.getReplacementType())
								.setTarget(target)
								.setValue(untypedTargetValue));

					if (dataPatchFailureOpt.isPresent()) {
						return singlePatchResult.setFailure(dataPatchFailureOpt.get());
					}

					// TODO: taint the DTO to mark it as dirty
					return singlePatchResult.setValue(untypedTargetValue);
				}

				Optional<Class<?>> nestedPropertyType = PropertyAccessor.getNestedPropertyType(target, fieldName);

				if (nestedPropertyType.isEmpty()) {
					return singlePatchResult.setFailure(new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.FIELD_DOES_NOT_EXIST));
				}
				Class<?> targetType = nestedPropertyType.orElseThrow();

				// TODO: handle targetType being a list. TO-Check with business: add / replace 

				Object typedValue = valueMapperRegistry.map(untypedTargetValue, targetType);

				if (request.getReplacementType() == DataReplacementType.IF_NOT_ALREADY_PRESENT) {
					Optional<Object> nestedPropertyValue = PropertyAccessor.getNestedProperty(target, fieldName);

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
				Optional<Exception> exception = PropertyAccessor.setNestedProperty(target, fieldName, typedValue);
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

		logger.debug("patch results: [{}]", results);

		// TODO: not necessarly both to be saved
		// TODO: 
		caseFacade.save(caseData);
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

	private @NotNull Map<String, Object> computeActualDictionary(CaseDataPatchRequest request) {
		Predicate<Map.Entry<String, Object>> filterPredicate = buildAdequateDictionaryValuePredicate(request);

		return request.getPatchDictionary()
			.entrySet()
			.stream()
			.filter(filterPredicate)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
