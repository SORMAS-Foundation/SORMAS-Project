package de.symeda.sormas.patch;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.patch.CaseDataPatchRequest;
import de.symeda.sormas.api.patch.CaseDataPatcher;
import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.DataPatchResponse;
import de.symeda.sormas.api.patch.EmptyValueBehavior;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.patch.mapping.FieldCustomMapperRegistry;
import de.symeda.sormas.patch.mapping.ValueMapperRegistry;

@Stateless
public class CaseDataPatcherImpl implements CaseDataPatcher {

	// might be more subtle: person.toto but also *.uuid (or uuid). includes approach ?
	// TODO: must be twofold: enforced default fields : technical: uuid, user ... + custom config by admin
	private Set<String> forbiddenFields;

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

		String caseUuid = request.getCaseUuid();
		CaseDataDto caseData = caseFacade.getCaseDataByUuid(caseUuid);

		if (caseData == null) {
			throw new IllegalStateException(String.format("No case found for uuid: [%s]", caseUuid));
		}

		// TODO: only fetch person when needed.
		String personUuid = caseData.getPerson().getUuid();
		PersonDto person = personFacade.getByUuid(personUuid);

		if (person == null) {
			throw new IllegalStateException(String.format("No person found for uuid: [%s]", personUuid));
		}

		Predicate<Map.Entry<String, Object>> filterPredicate =
			request.getEmptyValueBehavior() == EmptyValueBehavior.REPLACE ? ignored -> true : buildEmptyValuePredicate();

		Map<String, Object> actualDictionary = request.getPatchDictionary()
			.entrySet()
			.stream()
			.filter(filterPredicate)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		List<SinglePatchResult> results = actualDictionary.entrySet().stream().map(entry -> {
			return new SinglePatchResult().setFieldName(entry.getKey());
		}).collect(Collectors.toList());

		Map<String, Object> patchedValuesDictionary = results.stream()
			.filter(singlePatchResult -> singlePatchResult.getValue() != null)
			.collect(Collectors.toMap(SinglePatchResult::getFieldName, SinglePatchResult::getValue));

		Map<String, DataPatchFailure> failuresDictionary = results.stream()
			.filter(singlePatchResult -> singlePatchResult.getFailure() != null)
			.collect(Collectors.toMap(SinglePatchResult::getFieldName, SinglePatchResult::getFailure));

		return new DataPatchResponse().setPatchDictionary(patchedValuesDictionary).setFailures(failuresDictionary);

		/*
		 * Implementation steps:
		 * - lazily produce list of allowed fields to avoid.
		 * - Iterate over patch dictionary
		 * - Filter out empty values.
		 * - Check if field exists.
		 * - Check for forbidden fields
		 * - Check for FieldCustomMapper to use custom mapping strategy
		 * - Go to the appropriate (sub) field
		 * - TODO: if appropriate: multiple patching into same field strategy!!
		 * <p>
		 * WARN: Root will be either: (breaks trivial check if exists approach).
		 * - CaseData
		 * - Person
		 */
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
