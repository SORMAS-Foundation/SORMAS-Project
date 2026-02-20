package de.symeda.sormas.patch;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.patch.CaseDataPatchRequest;
import de.symeda.sormas.api.patch.CaseDataPatcher;
import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.DataPatchResponse;
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
		PersonDto person = getPersonDto(caseData);

		Predicate<Map.Entry<String, Object>> filterPredicate =
			request.getEmptyValueBehavior() == EmptyValueBehavior.REPLACE ? ignored -> true : buildEmptyValuePredicate();

		Map<String, Object> actualDictionary = request.getPatchDictionary()
			.entrySet()
			.stream()
			.filter(filterPredicate)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

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

					return singlePatchResult.setValue(untypedTargetValue);
				}

				Optional<Class<?>> nestedPropertyType = PropertyAccessor.getNestedPropertyType(target, fieldName);

				if (nestedPropertyType.isEmpty()) {
					return singlePatchResult.setFailure(new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.FIELD_DOES_NOT_EXIST));
				}
				Class<?> targetType = nestedPropertyType.orElseThrow();

				Object typedValue = valueMapperRegistry.map(untypedTargetValue, targetType);

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
		caseFacade.save(caseData);
		personFacade.save(person);

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

	private Object findAppropriateTarget(String fieldName, CaseDataDto caseData, PersonDto person) {

		fieldName = fieldName;

		return null;
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
}
