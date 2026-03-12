package de.symeda.sormas.backend.patch.partial_retrieval;

import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.patch.partial_retrieval.FieldInfo;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetrievalFailureCause;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetrievalRequest;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetrievalResponse;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetriever;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.patch.BusinessDtoFacade;
import de.symeda.sormas.backend.patch.PatchFieldHelper;

@ApplicationScoped
public class PartialRetrieverImpl implements PartialRetriever {

	@Inject
	private BusinessDtoFacade businessDtoFacade;

	@Inject
	private PatchFieldHelper patchFieldHelper;

	@EJB
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@Override
	public PartialRetrievalResponse retrievePartial(PartialRetrievalRequest request) {

		CaseDataDto caseData = businessDtoFacade.getCaseDataDtoNullable(request.getCaseUuid());
		Disease disease = caseData.getDisease();

		/*
		 * Implementation steps:
		 * - Iterate over fields
		 * - Validate if allowed
		 * - Un-alias to get physical for Reflection
		 * - Alias to get Field ID for I18N
		 * - Get type
		 * - Get value
		 */

		Set<String> fieldsToRetrieve = request.getFieldsToRetrieve();

		Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>> targetType = Tuple.of(null, new Tuple<>(null, null));
//		 fieldsToRetrieve.stream()
//				.flatMap(originalFieldName -> {
//
//					PathFailureCause pathFailureCause = patchFieldHelper.checkIfPathIsInvalid(originalFieldName);
//
//					Tuple<String, PathFailureCause> unAliasedTuple = patchFieldHelper.resolveAlias(originalFieldName);
//
//
//						PartialRetrievalFailureCause failureCause = Optional.ofNullable(pathFailureCause)
//								.map(PathFailureCause::getRelatedRetrieveFailureCause)
//								.or(() -> Optional.ofNullable(unAliasedTuple.getSecond()).map(PathFailureCause::getRelatedRetrieveFailureCause))
//								.orElse(null);
//
//						if (failureCause != null) {
//							return Stream.of(Tuple.of(originalFieldName, new Tuple<>(null, failureCause) ));
//						}
//
//					String physicalPathName = unAliasedTuple.getFirst();
//
////					if (!patchFieldHelper.isMultipleFieldFormat(physicalPathName)) {
////							return Stream.of(PropertyAccessor.getNestedPropertyAndType());
////						}
//
//						return splitMultipleFieldsPath(physicalPathName);
//
//
//				}).collect(Collectors.toList());

		return null;
	}

//	@NotNull
//	private Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>> splitMultipleFieldsPath(String path) {
//		int openingParenthesisIndex = path.indexOf("(");
//		String prefix = path.substring(0, openingParenthesisIndex);
//
//		int closeParen = path.indexOf(')');
//
//		String restPath = path.substring(openingParenthesisIndex + 1, closeParen);
//
//		return Arrays.stream(restPath.split("\\|")).map(suffix -> Tuple.of(prefix + suffix, Tuple.of(null));
//	}

	private FieldVisibilityCheckers getFieldVisibilityCheckers(Disease disease) {
		return FieldVisibilityCheckers.withCountry(configFacade.getCountryLocale())
			.andWithDisease(disease)
			.andWithFeatureType(featureConfigurationFacade.getActiveServerFeatureConfigurations());
	}
}
