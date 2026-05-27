package de.symeda.sormas.backend.patch.customizablefield;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.externalmessage.survey.PatchField;
import de.symeda.sormas.api.patch.CaseDataPatchRequest;
import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.customizablefield.CustomizableFieldMetadataFacadeEjb;
import de.symeda.sormas.backend.customizablefield.CustomizableFieldValueFacadeEjb;
import de.symeda.sormas.backend.patch.SingleFieldPatchResult;
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

	public List<CustomizableFieldSinglePatchingResult> patch(Request request) {
		List<SingleFieldPatchResult> patchingTuples = request.getPatchingTuples();

		if (CollectionUtils.isEmpty(patchingTuples)) {
			return List.of();
		}

		List<HelperClass> tempCollect = patchingTuples.stream()
			.map(
				singleFieldResult -> customizableFieldHelper.from(singleFieldResult.getField())
					.map(context -> new HelperClass().setPatchField(context).setSingleFieldPatchResult(singleFieldResult))
					.orElseGet(
						() -> new HelperClass().setSingleFieldPatchResult(
							new SingleFieldPatchResult().setField(singleFieldResult.getField())
								.setValue(singleFieldResult.getValue())
								.setFailureCause(DataPatchFailureCause.INVALID_CUSTOM_CONTEXT))))
			.collect(Collectors.toList());

		Set<CustomizableFieldContext> contexts = tempCollect.stream()
			.map(HelperClass::getPatchField)
			.filter(Objects::nonNull)
			.map(CustomizablePatchField::getContext)
			.collect(Collectors.toSet());

		Map<CustomizableFieldContext, Map<CustomizableFieldMetadataDto, Supplier<CustomizableFieldValueDto>>> customizableByContextDictionary =
			getDictionary(request, contexts);

		List<CustomizableFieldSinglePatchingResult> results = tempCollect.stream().peek(helperClass -> {
			CustomizablePatchField patchField = helperClass.getPatchField();
			if (patchField == null) {
				return;
			}

			Map<CustomizableFieldMetadataDto, Supplier<CustomizableFieldValueDto>> map = customizableByContextDictionary.get(patchField.getContext());

			Optional<Map.Entry<CustomizableFieldMetadataDto, Supplier<CustomizableFieldValueDto>>> singleOpt = map.entrySet()
				.stream()
				.filter(entry -> entry.getKey().getName().equals(patchField.getLeafFieldName()))
				.collect(CollectorUtils.toOptionalSingle());

			if (singleOpt.isEmpty()) {
				logger.error("For context. [{}] the leaf field does not exist: [{}]", patchField.getContext(), patchField.getLeafFieldName());
				helperClass.setFailureCause(DataPatchFailureCause.FIELD_DOES_NOT_EXIST);
			} else {
				helperClass.setCustomizableFieldValue(singleOpt.get().getValue().get());
				helperClass.setMetadata(singleOpt.get().getKey());
			}
		}).map(helperClass -> {
			SingleFieldPatchResult singleFieldPatchResult = helperClass.getSingleFieldPatchResult();

			DataPatchFailureCause preFailureCause =
				singleFieldPatchResult.getFailureCause() != null ? singleFieldPatchResult.getFailureCause() : helperClass.getFailureCause();

			if (preFailureCause != null) {
				return new CustomizableFieldSinglePatchingResult().setField(singleFieldPatchResult.getField())
					.setFailure(
						new DataPatchFailure().setDataPatchFailureCause(preFailureCause).setProvidedFieldValue(singleFieldPatchResult.getValue()))
					.setValue(singleFieldPatchResult.getValue());
			}

			ValueMappingResult<CustomizableFieldValueDto> mapResult = registry.map(
				new CustomizableFieldValuePatchRequest().setValue(singleFieldPatchResult.getValue())
					.setTargetType(helperClass.getMetadata().getFieldType())
					.setCustomizableFieldValueDto(helperClass.getCustomizableFieldValue()));

			DataPatchFailure failure = null;
			if (mapResult.getDataPatchFailureCause() != null) {
				failure = new DataPatchFailure().setDataPatchFailureCause(mapResult.getDataPatchFailureCause())
					.setProvidedFieldValue(singleFieldPatchResult.getValue());
			}

			return new CustomizableFieldSinglePatchingResult().setField(singleFieldPatchResult.getField())
				.setFailure(failure)
				.setValue(singleFieldPatchResult.getValue())
				.setCustomizableFieldValue(mapResult.getData());
		}).collect(Collectors.toList());

		logger.trace("Results: [{}]", results);

		return results;
	}

	private Map<CustomizableFieldContext, Map<CustomizableFieldMetadataDto, Supplier<CustomizableFieldValueDto>>> getDictionary(
		Request request,
		Set<CustomizableFieldContext> contexts) {

		Map<CustomizableFieldContext, Map<CustomizableFieldMetadataDto, Supplier<CustomizableFieldValueDto>>> customizableByContextDictionary =
			contexts.stream().map(context -> {
				List<CustomizableFieldMetadataDto> activeFieldsForContext = metaDataFacade.getActiveFieldsForContext(context);

				String entityUuid = extractEntityId(request.getCaseDataDto(), context);
				Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> alreadyExistingValuesForEntity =
					valueFacade.getValuesForEntity(entityUuid, context);

				Map<CustomizableFieldMetadataDto, Supplier<CustomizableFieldValueDto>> collect =
					activeFieldsForContext.stream().collect(Collectors.toMap(Function.identity(), metaData -> {

						CustomizableFieldValueDto alreadyStoredValue = alreadyExistingValuesForEntity.get(metaData);

						if (alreadyStoredValue != null) {
							return () -> alreadyStoredValue;
						}

						return () -> {
							CustomizableFieldValueDto newValueDto = new CustomizableFieldValueDto();

							newValueDto.setContextClass(context);
							newValueDto.setCustomizableFieldMetadataUuid(metaData.getUuid());
							newValueDto.setEntityUuid(entityUuid);

							return newValueDto;
						};

					}));

				return Tuple.of(context, collect);
			}).collect(Collectors.toMap(Tuple::getFirst, Tuple::getSecond));
		return customizableByContextDictionary;
	}

	public String extractEntityId(CaseDataDto caseDataDto, CustomizableFieldContext context) {
		// TODO: add PatchField + Entities from request as param.
		if (context == CustomizableFieldContext.CASE) {
			return caseDataDto.getUuid();
		} else if (context == CustomizableFieldContext.EPIDATA) {
			return caseDataDto.getEpiData().getUuid();
		} else {
			logger.error("Context is unsupported for now: [{}]", context);
			return null;
		}
	}

	public boolean match(PatchField patchField, CustomizableFieldMetadataDto metadata) {
		return false;
	}

	public void save(List<CustomizableFieldValueDto> values) {
		values.forEach(valueFacade::save);
	}

	public static final class Request {

		@NotNull
		private CaseDataPatchRequest caseDataPatchRequest;

		@NotNull
		private List<SingleFieldPatchResult> patchingTuples;

		@NotNull
		private CaseDataDto caseDataDto;

		public CaseDataPatchRequest getCaseDataPatchRequest() {
			return caseDataPatchRequest;
		}

		public Request setCaseDataPatchRequest(CaseDataPatchRequest caseDataPatchRequest) {
			this.caseDataPatchRequest = caseDataPatchRequest;
			return this;
		}

		public List<SingleFieldPatchResult> getPatchingTuples() {
			return patchingTuples;
		}

		public Request setPatchingTuples(List<SingleFieldPatchResult> patchingTuples) {
			this.patchingTuples = patchingTuples;
			return this;
		}

		public CaseDataDto getCaseDataDto() {
			return caseDataDto;
		}

		public Request setCaseDataDto(CaseDataDto caseDataDto) {
			this.caseDataDto = caseDataDto;
			return this;
		}
	}

	public static final class HelperClass {

		private CustomizablePatchField patchField;
		private SingleFieldPatchResult singleFieldPatchResult;
		private DataPatchFailureCause failureCause;

		private CustomizableFieldMetadataDto metadata;
		private CustomizableFieldValueDto customizableFieldValue;

		public CustomizablePatchField getPatchField() {
			return patchField;
		}

		public HelperClass setPatchField(CustomizablePatchField patchField) {
			this.patchField = patchField;
			return this;
		}

		public SingleFieldPatchResult getSingleFieldPatchResult() {
			return singleFieldPatchResult;
		}

		public HelperClass setSingleFieldPatchResult(SingleFieldPatchResult singleFieldPatchResult) {
			this.singleFieldPatchResult = singleFieldPatchResult;
			return this;
		}

		public CustomizableFieldValueDto getCustomizableFieldValue() {
			return customizableFieldValue;
		}

		public HelperClass setCustomizableFieldValue(CustomizableFieldValueDto customizableFieldValue) {
			this.customizableFieldValue = customizableFieldValue;
			return this;
		}

		public DataPatchFailureCause getFailureCause() {
			return failureCause;
		}

		public HelperClass setFailureCause(DataPatchFailureCause failureCause) {
			this.failureCause = failureCause;
			return this;
		}

		public CustomizableFieldMetadataDto getMetadata() {
			return metadata;
		}

		public HelperClass setMetadata(CustomizableFieldMetadataDto metadata) {
			this.metadata = metadata;
			return this;
		}
	}
}
