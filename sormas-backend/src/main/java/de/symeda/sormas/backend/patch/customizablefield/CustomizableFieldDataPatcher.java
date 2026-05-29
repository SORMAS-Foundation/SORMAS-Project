package de.symeda.sormas.backend.patch.customizablefield;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.PlainSinglePatchResult;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.customizablefield.CustomizableFieldMetadataFacadeEjb;
import de.symeda.sormas.backend.customizablefield.CustomizableFieldValueFacadeEjb;
import de.symeda.sormas.backend.patch.customizablefield.mappers.CustomizableFieldValuePatchMapperRegistry;
import de.symeda.sormas.backend.util.CollectorUtils;

/**
 * Wrapper arround {@link de.symeda.sormas.api.customizablefield.CustomizableFieldValueFacade} to be able to patch customizable field
 * values.
 */
@ApplicationScoped
public class CustomizableFieldDataPatcher {

	private final static Logger logger = LoggerFactory.getLogger(CustomizableFieldDataPatcher.class);

	@EJB
	private CustomizableFieldValueFacadeEjb.CustomizableFieldValueFacadeEjbLocal valueFacade;

	@EJB
	private CustomizableFieldMetadataFacadeEjb.CustomizableFieldMetadataFacadeEjbLocal metaDataFacade;

	@Inject
	private CustomizableFieldValuePatchMapperRegistry registry;

	@Inject
	private CustomizableFieldHelper customizableFieldHelper;

	public List<CustomizableFieldSinglePatchingResult> patch(CustomizableFieldDataPatchRequest request) {
		logger.debug("patch: [{}]", request);
		List<PlainSinglePatchResult> patchingTuples = request.getPatchingTuples();

		if (CollectionUtils.isEmpty(patchingTuples)) {
			logger.info("No customizable fields patching tuples provided");
			return List.of();
		}

		logger.info("Customizable patch will be attempted, enable debug to see request and response");

		List<CustomizableFieldPatchWrapper> initialWrappers = patchingTuples.stream()
			.map(
				singleFieldResult -> customizableFieldHelper.from(singleFieldResult.getField())
					.map(patchField -> new CustomizableFieldPatchWrapper().setPatchField(patchField).setPlainSinglePatchResult(singleFieldResult))
					.orElseGet(() -> buildInvalidContextErrorWrapper(singleFieldResult)))
			.collect(Collectors.toList());

		Set<CustomizableFieldContext> contexts = getContextsToFetch(initialWrappers);

		Map<CustomizableFieldContext, Map<CustomizableFieldMetadataDto, Function<CustomizablePatchField, CustomizableFieldValueDto>>> customizableByContextDictionary =
			buildDictionary(request, contexts);

		List<CustomizableFieldSinglePatchingResult> results = initialWrappers.stream()
			.peek(customizableFieldPatchWrapper -> enrichWrapper(customizableFieldPatchWrapper, customizableByContextDictionary))
			.map(customizableFieldPatchWrapper -> {
				try {
					return buildPatchResult(customizableFieldPatchWrapper);
				} catch (RuntimeException e) {
					logger.error("Unhandled patching for wrapper: [{}]", customizableFieldPatchWrapper);

					PlainSinglePatchResult plainSinglePatchResult = customizableFieldPatchWrapper.getPlainSinglePatchResult();
					return new CustomizableFieldSinglePatchingResult().setField(plainSinglePatchResult.getField())
						.setFailure(
							new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.TECHNICAL)
								.setProvidedFieldValue(plainSinglePatchResult.getValue()));
				}
			})
			.collect(Collectors.toList());

		logger.trace("Results: [{}]", results);

		return results;
	}

	private CustomizableFieldSinglePatchingResult buildPatchResult(CustomizableFieldPatchWrapper customizableFieldPatchWrapper) {
		PlainSinglePatchResult singleFieldPatchResult = customizableFieldPatchWrapper.getPlainSinglePatchResult();

		DataPatchFailure patchFailure = singleFieldPatchResult.getFailure();
		DataPatchFailureCause preFailureCause =
			patchFailure != null ? patchFailure.getDataPatchFailureCause() : customizableFieldPatchWrapper.getFailureCause();

		if (preFailureCause != null) {
			return new CustomizableFieldSinglePatchingResult().setField(singleFieldPatchResult.getField())
				.setFailure(
					new DataPatchFailure().setDataPatchFailureCause(preFailureCause)
						.setProvidedFieldValue(
							Optional.ofNullable(patchFailure)
								.map(DataPatchFailure::getProvidedFieldValue)
								.orElseGet(singleFieldPatchResult::getValue)));
		}

		ValueMappingResult<CustomizableFieldValueDto> mapResult = registry.map(
			new CustomizableFieldValuePatchRequest().setValue(singleFieldPatchResult.getValue())
				.setTargetType(customizableFieldPatchWrapper.getMetadata().getFieldType())
				.setCustomizableFieldValueDto(customizableFieldPatchWrapper.getCustomizableFieldValue()));

		DataPatchFailure failure = null;
		if (mapResult.getDataPatchFailureCause() != null) {
			failure = new DataPatchFailure().setDataPatchFailureCause(mapResult.getDataPatchFailureCause())
				.setProvidedFieldValue(singleFieldPatchResult.getValue());
		}

		return new CustomizableFieldSinglePatchingResult().setField(singleFieldPatchResult.getField())
			.setFailure(failure)
			.setValue(singleFieldPatchResult.getValue())
			.setCustomizableFieldValue(mapResult.getData());
	}

	private static void enrichWrapper(
		CustomizableFieldPatchWrapper customizableFieldPatchWrapper,
		Map<CustomizableFieldContext, Map<CustomizableFieldMetadataDto, Function<CustomizablePatchField, CustomizableFieldValueDto>>> customizableByContextDictionary) {
		CustomizablePatchField patchField = customizableFieldPatchWrapper.getPatchField();
		if (patchField == null) {
			logger.warn("PatchField was null, this should not occur: [{}]", customizableFieldPatchWrapper);
			return;
		}

		Map<CustomizableFieldMetadataDto, Function<CustomizablePatchField, CustomizableFieldValueDto>> dictionary =
			customizableByContextDictionary.get(patchField.getContext());

		Optional<Map.Entry<CustomizableFieldMetadataDto, Function<CustomizablePatchField, CustomizableFieldValueDto>>> singleOpt =
			dictionary.entrySet()
				.stream()
				.filter(entry -> entry.getKey().getName().equals(patchField.getLeafFieldName()))
				.collect(CollectorUtils.toOptionalSingle());

		if (singleOpt.isEmpty()) {
			logger.error("For context. [{}] the leaf field does not exist: [{}]", patchField.getContext(), patchField.getLeafFieldName());
			customizableFieldPatchWrapper.setFailureCause(DataPatchFailureCause.FIELD_DOES_NOT_EXIST);
		} else {
			Function<CustomizablePatchField, CustomizableFieldValueDto> value = singleOpt.get().getValue();
			customizableFieldPatchWrapper.setCustomizableFieldValue(value.apply(patchField));
			customizableFieldPatchWrapper.setMetadata(singleOpt.get().getKey());
		}
	}

	private static @NotNull Set<CustomizableFieldContext> getContextsToFetch(List<CustomizableFieldPatchWrapper> initialWrappers) {
		return initialWrappers.stream()
			.map(CustomizableFieldPatchWrapper::getPatchField)
			.filter(Objects::nonNull)
			.map(CustomizablePatchField::getContext)
			.collect(Collectors.toSet());
	}

	private static @NotNull CustomizableFieldPatchWrapper buildInvalidContextErrorWrapper(PlainSinglePatchResult singleFieldResult) {
		return new CustomizableFieldPatchWrapper().setPlainSinglePatchResult(
			new PlainSinglePatchResult().setField(singleFieldResult.getField())
				.setFailure(
					new DataPatchFailure().setDataPatchFailureCause(DataPatchFailureCause.INVALID_CUSTOM_CONTEXT)
						.setProvidedFieldValue(singleFieldResult.getValue())));
	}

	private Map<CustomizableFieldContext, Map<CustomizableFieldMetadataDto, Function<CustomizablePatchField, CustomizableFieldValueDto>>> buildDictionary(
		CustomizableFieldDataPatchRequest request,
		Set<CustomizableFieldContext> contexts) {

		Map<CustomizableFieldContext, Map<CustomizableFieldMetadataDto, Function<CustomizablePatchField, CustomizableFieldValueDto>>> customizableByContextDictionary =
			contexts.stream().map(context -> {
				List<CustomizableFieldMetadataDto> activeFieldsForContext = metaDataFacade.getActiveFieldsForContext(context);

				Map<CustomizableContextIndexKey, String> entityUuidDictionary = request.getEntityUuidDictionary();

				Map<String, Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto>> entityValuesCache = new HashMap<>();

				Map<CustomizableFieldMetadataDto, Function<CustomizablePatchField, CustomizableFieldValueDto>> valueProviderDictionary =
					activeFieldsForContext.stream().collect(Collectors.toMap(Function.identity(), metaData -> (customizablePatchField) -> {

						String entityUuid = entityUuidDictionary.get(
							new CustomizableContextIndexKey().setContext(customizablePatchField.getContext())
								.setGroupIndex(customizablePatchField.getGroupIndex()));

						Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> alreadyExistingValuesForEntity =
							entityValuesCache.computeIfAbsent(entityUuid, uuid -> valueFacade.getValuesForEntity(uuid, context));

						CustomizableFieldValueDto alreadyStoredValue = alreadyExistingValuesForEntity.get(metaData);

						if (alreadyStoredValue != null) {
							return alreadyStoredValue;
						}

						CustomizableFieldValueDto newValueDto = new CustomizableFieldValueDto();

						newValueDto.setContextClass(context);
						newValueDto.setCustomizableFieldMetadataUuid(metaData.getUuid());
						newValueDto.setEntityUuid(entityUuid);

						return newValueDto;

					}));

				return Tuple.of(context, valueProviderDictionary);
			}).collect(Collectors.toMap(Tuple::getFirst, Tuple::getSecond));
		return customizableByContextDictionary;
	}

	public void save(List<CustomizableFieldValueDto> values) {
		values.forEach(dto -> {
			logger.info("Saving CustomizableFieldValueDto [{}]", dto);
			valueFacade.save(dto);
		});
	}

	/**
	 * Helper class to ease processing for customizable fields.
	 */
	public static final class CustomizableFieldPatchWrapper {

		private CustomizablePatchField patchField;
		private PlainSinglePatchResult singleFieldPatchResult;
		private DataPatchFailureCause failureCause;

		private CustomizableFieldMetadataDto metadata;
		private CustomizableFieldValueDto customizableFieldValue;

		public CustomizablePatchField getPatchField() {
			return patchField;
		}

		public CustomizableFieldPatchWrapper setPatchField(CustomizablePatchField patchField) {
			this.patchField = patchField;
			return this;
		}

		public PlainSinglePatchResult getPlainSinglePatchResult() {
			return singleFieldPatchResult;
		}

		public CustomizableFieldPatchWrapper setPlainSinglePatchResult(PlainSinglePatchResult singleFieldPatchResult) {
			this.singleFieldPatchResult = singleFieldPatchResult;
			return this;
		}

		public CustomizableFieldValueDto getCustomizableFieldValue() {
			return customizableFieldValue;
		}

		public CustomizableFieldPatchWrapper setCustomizableFieldValue(CustomizableFieldValueDto customizableFieldValue) {
			this.customizableFieldValue = customizableFieldValue;
			return this;
		}

		public DataPatchFailureCause getFailureCause() {
			return failureCause;
		}

		public CustomizableFieldPatchWrapper setFailureCause(DataPatchFailureCause failureCause) {
			this.failureCause = failureCause;
			return this;
		}

		public CustomizableFieldMetadataDto getMetadata() {
			return metadata;
		}

		public CustomizableFieldPatchWrapper setMetadata(CustomizableFieldMetadataDto metadata) {
			this.metadata = metadata;
			return this;
		}

		@Override
		public String toString() {
			return "CustomizableFieldPatchWrapper{" + "patchField=" + patchField + ", singleFieldPatchResult=" + singleFieldPatchResult
				+ ", failureCause=" + failureCause + ", metadata=" + metadata + ", customizableFieldValue=" + customizableFieldValue + '}';
		}
	}
}
