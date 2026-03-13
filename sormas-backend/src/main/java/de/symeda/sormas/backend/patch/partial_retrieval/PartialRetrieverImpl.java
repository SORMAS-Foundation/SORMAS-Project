package de.symeda.sormas.backend.patch.partial_retrieval;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.patch.partial_retrieval.DisplayableFieldInfo;
import de.symeda.sormas.api.patch.partial_retrieval.DisplayablePartialRetrievalResponse;
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
import de.symeda.sormas.backend.patch.PathFailureCause;
import de.symeda.sormas.backend.patch.PropertyAccessFailure;
import de.symeda.sormas.backend.patch.PropertyAccessor;
import de.symeda.sormas.backend.patch.alias.PathAliasHelper;

@ApplicationScoped
public class PartialRetrieverImpl implements PartialRetriever {

	private final static Logger logger = LoggerFactory.getLogger(PartialRetrieverImpl.class);

	@Inject
	private BusinessDtoFacade businessDtoFacade;

	@Inject
	private PatchFieldHelper patchFieldHelper;

	@Inject
	private PathAliasHelper pathAliasHelper;

	@Inject
	private TypeToDisplayRegistry typeToDisplayRegistry;

	@EJB
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@Override
	public PartialRetrievalResponse retrievePartial(PartialRetrievalRequest request) {

		CaseDataDto caseData = businessDtoFacade.getCaseDataDto(request.getCaseUuid());

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

		List<Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>>> results = fieldsToRetrieve.stream().flatMap(originalFieldName -> {

			PathFailureCause pathFailureCause = patchFieldHelper.checkIfPathIsInvalid(originalFieldName);

			Tuple<String, PathFailureCause> unAliasedTuple = patchFieldHelper.resolveAlias(originalFieldName);

			PartialRetrievalFailureCause failureCause = Optional.ofNullable(pathFailureCause)
				.map(PathFailureCause::getRelatedRetrieveFailureCause)
				.or(() -> Optional.ofNullable(unAliasedTuple.getSecond()).map(PathFailureCause::getRelatedRetrieveFailureCause))
				.orElse(null);

			if (failureCause != null) {
				return Stream.of(Tuple.of(originalFieldName, new Tuple<>((FieldInfo) null, failureCause)));
			}

			String pathWithoutAlias = unAliasedTuple.getFirst();
			String physicalPathName = pathWithoutAlias.substring(pathWithoutAlias.indexOf('.') + 1);

			// TODO: handle multiple path format.
//			if (!patchFieldHelper.isMultipleFieldFormat(physicalPathName)) {

			String aliasPath = pathAliasHelper.toAliasPath(pathWithoutAlias);
			Optional<EntityDto> adequateBean = getAdequateBean(pathWithoutAlias, caseData);

			if (adequateBean.isEmpty()) {
				return Stream.of(Tuple.of(originalFieldName, new Tuple<>((FieldInfo) null, PartialRetrievalFailureCause.ENTITY_COULD_NOT_BE_FOUND)));
			}

			Tuple<Tuple<Class<?>, Object>, PropertyAccessFailure> propertyType = PropertyAccessor
				.getPropertyTypeAndValue(adequateBean.orElseThrow(), physicalPathName, getFieldVisibilityCheckers(caseData.getDisease()));

			PropertyAccessFailure propertyAccessFailure = propertyType.getSecond();
			if (propertyAccessFailure != null) {
				return Stream.of(Tuple.of(originalFieldName, new Tuple<>((FieldInfo) null, propertyAccessFailure.getRelatedRetrieveFailureCause())));
			}

			Tuple<Class<?>, Object> fieldInfo = propertyType.getFirst();

			// Some fields are translated only by there "physical-path" from root level
			// example: Person.firstName has translation key "firstName"
			// example: CaseData.disease has translation key "firstName"
			String translatedFieldName = Optional.ofNullable(I18nProperties.getCaption(aliasPath, null))
				.or(() -> Optional.ofNullable(I18nProperties.getCaption(physicalPathName, null)))
				.orElseGet(() -> I18nProperties.getDescription(aliasPath, aliasPath));

			return Stream.of(
				Tuple.of(
					originalFieldName,
					new Tuple<>(
						new FieldInfo().setFieldType(fieldInfo.getFirst())
							.setFieldValue(fieldInfo.getSecond())
							.setTranslatedFieldName(translatedFieldName),
						(PartialRetrievalFailureCause) null)));
//			}
//
//			return splitMultipleFieldsPath(physicalPathName).map(a -> );

		}).collect(Collectors.toList());

		Map<String, FieldInfo> successes = results.stream()
			.filter(tuple -> tuple.getSecond().getSecond() == null)
			.collect(Collectors.toMap(Tuple::getFirst, tuple -> tuple.getSecond().getFirst()));

		Map<String, PartialRetrievalFailureCause> failures = results.stream()
			.filter(tuple -> tuple.getSecond().getSecond() != null)
			.collect(Collectors.toMap(Tuple::getFirst, tuple -> tuple.getSecond().getSecond()));

		return new PartialRetrievalResponse().setFailuresDictionary(failures).setFieldInfoDictionary(successes);
	}

	@Override
	public DisplayablePartialRetrievalResponse retrievePartialForDisplay(PartialRetrievalRequest request) {
		PartialRetrievalResponse partialRetrievalResponse = retrievePartial(request);

		// TODO: translate failures.
		return new DisplayablePartialRetrievalResponse().setFieldInfoDictionary(
			partialRetrievalResponse.getFieldInfoDictionary().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
				FieldInfo fieldInfo = entry.getValue();
				return new DisplayableFieldInfo().setTranslatedFieldName(fieldInfo.getTranslatedFieldName())
					.setTranslatedFieldValue(typeToDisplayRegistry.toDisplayValue(fieldInfo.getFieldValue()));
			})))
			.setFailuresDescriptions(
				partialRetrievalResponse.getFailuresDictionary()
					.entrySet()
					.stream()
					.collect(Collectors.toMap(Map.Entry::getKey, entry -> I18nProperties.getEnumCaption(entry.getValue()))));
	}

	private Optional<EntityDto> getAdequateBean(@NotNull String aliasPath, @NotNull CaseDataDto caseData) {

		int i = aliasPath.indexOf(".");

		String prefix = StringUtils.substring(aliasPath, 0, i);

		if (CaseDataDto.I18N_PREFIX.equals(prefix)) {
			return Optional.of(caseData);
		} else {
			List<? extends EntityDto> entityDtos = businessDtoFacade.fetchByI18nName(prefix, caseData);

			int entitiesSize = CollectionUtils.size(entityDtos);

			if (entitiesSize == 0) {
				return Optional.empty();
			}

			if (entitiesSize != 1) {
				logger.warn("Only first element is supported for now: [{}], was: [{}]", aliasPath, entitiesSize);
			}

			return Optional.ofNullable(entityDtos).map(actualEntities -> actualEntities.get(0));
		}
	}

	private FieldVisibilityCheckers getFieldVisibilityCheckers(Disease disease) {
		return FieldVisibilityCheckers.withCountry(configFacade.getCountryLocale())
			.andWithDisease(disease)
			.andWithFeatureType(featureConfigurationFacade.getActiveServerFeatureConfigurations());
	}
}
