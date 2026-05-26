package de.symeda.sormas.backend.patch.customizablefield;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.SinglePatchResult;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.customizablefield.CustomizableFieldValueFacadeEjb;
import de.symeda.sormas.backend.patch.DataPatcherImpl;
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
	private CustomizableFieldValueFacadeEjb.CustomizableFieldValueFacadeEjbLocal facade;

	@Inject
	private CustomizableFieldValuePatchMapperRegistry registry;

	@Inject
	private CustomizableFieldHelper customizableFieldHelper;

	public List<Tuple<SinglePatchResult, ValueMappingResult<CustomizableFieldValueDto>>> patch(Request request) {
		List<DataPatcherImpl.SingleFieldPatchResult> patchingTuples = request.getPatchingTuples();

		if (CollectionUtils.isEmpty(patchingTuples)) {
			return List.of();
		}

		List<HelperClass> tempCollect = patchingTuples.stream()
			.map(
				singleFieldResult -> customizableFieldHelper.from(singleFieldResult.getField())
					.map(context -> new HelperClass().setPatchField(context))
					.orElseGet(
						() -> new HelperClass().setSingleFieldPatchResult(
							new DataPatcherImpl.SingleFieldPatchResult().setField(singleFieldResult.getField())
								.setFailureCause(DataPatchFailureCause.INVALID_CUSTOM_CONTEXT))))
			.collect(Collectors.toList());

		Set<CustomizableFieldContext> contexts =
			tempCollect.stream().map(HelperClass::getPatchField).map(CustomizablePatchField::getContext).collect(Collectors.toSet());

		Map<CustomizableFieldContext, Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto>> customizableByContextDictionary =
			contexts.stream()
				.map(context -> Tuple.of(context, facade.getValuesForEntity(extractEntityId(request.getCaseDataDto(), context), context)))
				.collect(Collectors.toMap(Tuple::getFirst, Tuple::getSecond));

		List<ValueMappingResult<CustomizableFieldValueDto>> results = tempCollect.stream().peek(helperClass -> {
			CustomizablePatchField patchField = helperClass.getPatchField();

			Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto> map = customizableByContextDictionary.get(patchField);

			Optional<Map.Entry<CustomizableFieldMetadataDto, CustomizableFieldValueDto>> singleOpt = map.entrySet()
				.stream()
				.filter(entry -> entry.getKey().getName().equals(patchField.getLeafFieldName()))
				.collect(CollectorUtils.toOptionalSingle());

			if (singleOpt.isEmpty()) {
				logger.error("For context. [{}] the leaf field does not exist: [{}]", patchField.getContext(), patchField.getLeafFieldName());
				helperClass.setFailureCause(DataPatchFailureCause.FIELD_DOES_NOT_EXIST);
			} else {
				helperClass.setCustomizableFieldValue(singleOpt.get().getValue());
				helperClass.setMetadata(singleOpt.get().getKey());
			}
			// TODO: handle errors: 
			// - leafFieldName not found 
			// - other error during mapping.
		})
			.map(
				helperClass -> new CustomizableFieldValuePatchRequest().setValue(helperClass.getSingleFieldPatchResult().getValue())
					.setTargetType(helperClass.getMetadata().getFieldType())
					.setCustomizableFieldValueDto(helperClass.getCustomizableFieldValue()))
			.map(a -> registry.map(a))
			.collect(Collectors.toList());

		return results;

//		contextsToLoad.stream().map(a -> facade.getValuesForEntity("", a))
//				.flatMap(a-> a.entrySet().stream()
//						.map(b ->Tuple.of(b.getKey(), b.getValue())))
//				.filter(a -> );
//
//
//
//		return valuesForEntity.entrySet().stream()
//				.filter(a -> patchingTuples.stream().anyMatch(b -> b.))
//				.map(a -> new CustomizableFieldValuePatchRequest())
//				.map(request1 -> Tuple.of(registry.map(request1)))
//				.tempCollect(Collectors.toList());
	}

	public String extractEntityId(CaseDataDto caseDataDto, CustomizableFieldContext context) {
		if (context == CustomizableFieldContext.CASE) {
			return caseDataDto.getUuid();
		} else if (context == CustomizableFieldContext.EPIDATA) {
			return caseDataDto.getEpiData().getUuid();
		} else {
			logger.error("Unsupported for now.");
			return null;
		}
	}

	public boolean match(PatchField patchField, CustomizableFieldMetadataDto metadata) {
		return false;
	}

	public void save(List<CustomizableFieldValueDto> values) {
		values.forEach(facade::save);
	}

	public static final class Request {

		@NotNull
		private CaseDataPatchRequest caseDataPatchRequest;

		@NotNull
		private List<DataPatcherImpl.SingleFieldPatchResult> patchingTuples;

		@NotNull
		private CaseDataDto caseDataDto;

		public CaseDataPatchRequest getCaseDataPatchRequest() {
			return caseDataPatchRequest;
		}

		public Request setCaseDataPatchRequest(CaseDataPatchRequest caseDataPatchRequest) {
			this.caseDataPatchRequest = caseDataPatchRequest;
			return this;
		}

		public List<DataPatcherImpl.SingleFieldPatchResult> getPatchingTuples() {
			return patchingTuples;
		}

		public Request setPatchingTuples(List<DataPatcherImpl.SingleFieldPatchResult> patchingTuples) {
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
		private DataPatcherImpl.SingleFieldPatchResult singleFieldPatchResult;
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

		public DataPatcherImpl.SingleFieldPatchResult getSingleFieldPatchResult() {
			return singleFieldPatchResult;
		}

		public HelperClass setSingleFieldPatchResult(DataPatcherImpl.SingleFieldPatchResult singleFieldPatchResult) {
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
