package de.symeda.sormas.backend.patch.partial_retrieval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import de.symeda.sormas.api.patch.partial_retrieval.*;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.patch.*;
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

	@Inject
	private SpecificFieldValueRetrieverRegistry specificFieldValueRetrieverRegistry;

	@EJB
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@Override
	public PartialRetrievalResponse retrievePartial(PartialRetrievalRequest request) {
		logger.debug("retrievePartial: [{}]", request);

		CaseDataDto caseData = businessDtoFacade.getCaseDataDto(request.getCaseUuid());

		Map<String, Optional<EntityDto>> beanCache = new HashMap<>();

		List<Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>>> results =
			patchFieldHelper.extractFieldTuples(request.getFieldsToRetrieve(), businessDtoFacade.fetchablePrefixes()).stream().map(tuple -> {

				try {
					return buildTupleImpl(tuple, caseData, beanCache);
				} catch (RuntimeException e) {
					logger.warn("Failure during retrieval for [{}]", tuple, e);
					return Tuple.of(tuple.getFirst(), new Tuple<>((FieldInfo) null, PartialRetrievalFailureCause.TECHNICAL));
				}

			}).collect(Collectors.toList());

		Map<String, FieldInfo> successes = results.stream()
			.filter(tuple -> tuple.getSecond().getSecond() == null)
			.collect(Collectors.toMap(Tuple::getFirst, tuple -> tuple.getSecond().getFirst()));

		Map<String, PartialRetrievalFailureCause> failures = results.stream()
			.filter(tuple -> tuple.getSecond().getSecond() != null)
			.collect(Collectors.toMap(Tuple::getFirst, tuple -> tuple.getSecond().getSecond()));

		PartialRetrievalResponse result = new PartialRetrievalResponse().setFailuresDictionary(failures).setFieldInfoDictionary(successes);

		logger.debug("result: [{}]", result);

		return result;
	}

	private Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>> buildTupleImpl(
		Tuple<String, PathFailureCause> tuple,
		CaseDataDto caseData,
		Map<String, Optional<EntityDto>> beanCache) {
		String originalFieldName = tuple.getFirst();
		PathFailureCause pathFailureCause = tuple.getSecond();

		Tuple<String, PathFailureCause> unAliasedTuple = patchFieldHelper.resolveAlias(originalFieldName);

		PartialRetrievalFailureCause failureCause = Optional.ofNullable(pathFailureCause)
			.map(PathFailureCause::getRelatedRetrieveFailureCause)
			.or(() -> Optional.ofNullable(unAliasedTuple.getSecond()).map(PathFailureCause::getRelatedRetrieveFailureCause))
			.orElse(null);

		if (failureCause != null) {
			return Tuple.of(originalFieldName, new Tuple<>(null, failureCause));
		}

		String pathWithoutAlias = unAliasedTuple.getFirst();
		String physicalPathName = pathWithoutAlias.substring(pathWithoutAlias.indexOf('.') + 1);

		String aliasPath = pathAliasHelper.toAliasPath(pathWithoutAlias);
		Optional<EntityDto> adequateBeanOpt = getAdequateBean(pathWithoutAlias, caseData, beanCache);

		if (adequateBeanOpt.isEmpty()) {
			return Tuple.of(originalFieldName, new Tuple<>(null, PartialRetrievalFailureCause.ENTITY_COULD_NOT_BE_FOUND));
		}

		EntityDto adequateBean = adequateBeanOpt.orElseThrow();
		Optional<FieldInfo> specificFieldInfo = specificFieldValueRetrieverRegistry.getFieldInfo(aliasPath, adequateBean);

		if (specificFieldInfo.isPresent()) {
			return Tuple.of(originalFieldName, new Tuple<>(specificFieldInfo.get(), null));
		}

		@NotNull
		Tuple<Tuple<Class<?>, Object>, PropertyAccessFailure> propertyType =
			PropertyAccessor.getPropertyTypeAndValue(adequateBean, physicalPathName, getFieldVisibilityCheckers(caseData.getDisease()));

		PropertyAccessFailure propertyAccessFailure = propertyType.getSecond();
		if (propertyAccessFailure != null) {
			return Tuple.of(originalFieldName, new Tuple<>(null, propertyAccessFailure.getRelatedRetrieveFailureCause()));
		}

		Tuple<Class<?>, Object> fieldInfo = propertyType.getFirst();

		// Some fields are translated only by there "physical-path" from root level
		// example: Person.firstName has translation key "firstName", CaseData.disease has translation key "Disease"
		// best effort: UI falls-back to 
		String translatedFieldName = Optional.ofNullable(I18nProperties.getCaption(aliasPath, null))
			.or(() -> Optional.ofNullable(I18nProperties.getCaption(physicalPathName, null)))
			.orElseGet(() -> I18nProperties.getDescription(aliasPath, aliasPath));

		return Tuple.of(
			originalFieldName,
			new Tuple<>(
				new FieldInfo().setFieldType(fieldInfo.getFirst()).setFieldValue(fieldInfo.getSecond()).setTranslatedFieldName(translatedFieldName),
				null));
	}

	@Override
	public DisplayablePartialRetrievalResponse retrievePartialForDisplay(PartialRetrievalRequest request) {
		PartialRetrievalResponse partialRetrievalResponse = retrievePartial(request);

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

	private Optional<EntityDto> getAdequateBean(
		@NotNull String pathWithoutAlias,
		@NotNull CaseDataDto caseData,
		@NotNull Map<String, Optional<EntityDto>> beanCache) {

		int i = pathWithoutAlias.indexOf(".");

		String prefix = StringUtils.substring(pathWithoutAlias, 0, i);

		if (CaseDataDto.I18N_PREFIX.equals(prefix)) {
			return Optional.of(caseData);
		} else {
			return beanCache.computeIfAbsent(prefix, prefixCandidate -> {
				List<? extends EntityDto> entityDtos = businessDtoFacade.fetchByI18nNameForDisplay(prefixCandidate, caseData);

				int entitiesSize = CollectionUtils.size(entityDtos);

				if (entitiesSize == 0) {
					return Optional.empty();
				}

				if (entitiesSize != 1) {
					logger.warn("Only first element is supported for now: [{}], was: [{}]", pathWithoutAlias, entitiesSize);
				}

				return Optional.ofNullable(entityDtos).map(actualEntities -> actualEntities.get(0));
			});
		}
	}

	private FieldVisibilityCheckers getFieldVisibilityCheckers(Disease disease) {
		return FieldVisibilityCheckers.withCountry(configFacade.getCountryLocale())
			.andWithDisease(disease)
			.andWithFeatureType(featureConfigurationFacade.getActiveServerFeatureConfigurations());
	}
}
