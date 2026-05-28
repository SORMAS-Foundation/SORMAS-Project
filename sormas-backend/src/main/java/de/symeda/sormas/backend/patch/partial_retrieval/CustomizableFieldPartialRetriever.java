package de.symeda.sormas.backend.patch.partial_retrieval;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.patch.partial_retrieval.FieldInfo;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetrievalFailureCause;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.customizablefield.CustomizableFieldMetadataFacadeEjb;
import de.symeda.sormas.backend.customizablefield.CustomizableFieldValueFacadeEjb;
import de.symeda.sormas.backend.patch.PathFailureCause;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldContextPatchMapping;
import de.symeda.sormas.backend.util.CollectorUtils;

/**
 * Handles retrieval of current values for customizable fields, keeping that logic separate from
 * the main {@link PartialRetrieverImpl} flow.
 * <p>
 * Only {@link CustomizableFieldContext#CASE} and {@link CustomizableFieldContext#EPIDATA} are
 * supported — Exposure is excluded because partial retrieval only works for singular fields.
 * In other words in the UI only the current value will be displayed (there is not curreexisating value)
 */
@ApplicationScoped
public class CustomizableFieldPartialRetriever {

	private final static Logger logger = LoggerFactory.getLogger(CustomizableFieldPartialRetriever.class);

	@EJB
	private CustomizableFieldValueFacadeEjb.CustomizableFieldValueFacadeEjbLocal valueFacade;

	@EJB
	private CustomizableFieldMetadataFacadeEjb.CustomizableFieldMetadataFacadeEjbLocal metaDataFacade;

	public List<Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>>> retrieve(
		List<Tuple<String, PathFailureCause>> customizableFieldTuples,
		CaseDataDto caseData) {

		if (CollectionUtils.isEmpty(customizableFieldTuples)) {
			return List.of();
		}

		List<ParsedCustomizableField> parsedFields = customizableFieldTuples.stream().map(this::parse).collect(Collectors.toList());

		Set<CustomizableFieldContext> validContexts =
			parsedFields.stream().filter(ParsedCustomizableField::isValid).map(ParsedCustomizableField::getContext).collect(Collectors.toSet());

		Map<CustomizableFieldContext, List<CustomizableFieldMetadataDto>> metadataByContext = new HashMap<>();
		Map<CustomizableFieldContext, Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto>> valuesByContext = new HashMap<>();

		for (CustomizableFieldContext context : validContexts) {
			String entityUuid = getEntityUuid(context, caseData);
			metadataByContext.put(context, metaDataFacade.getActiveFieldsForContext(context));
			valuesByContext.put(context, valueFacade.getValuesForEntity(entityUuid, context));
		}

		return parsedFields.stream().map(parsedField -> resolve(parsedField, metadataByContext, valuesByContext)).collect(Collectors.toList());
	}

	private ParsedCustomizableField parse(Tuple<String, PathFailureCause> tuple) {
		String originalPath = tuple.getFirst();
		PathFailureCause pathFailureCause = tuple.getSecond();

		ParsedCustomizableField result = new ParsedCustomizableField(originalPath);

		if (pathFailureCause != null) {
			result.setFailureCause(pathFailureCause.getRelatedRetrieveFailureCause());
			return result;
		}

		String[] parts = originalPath.split("\\.");
		if (parts.length != 3) {
			result.setFailureCause(PartialRetrievalFailureCause.INVALID_PATH_FORMAT);
			return result;
		}

		CustomizableFieldContext context = CustomizableFieldContextPatchMapping.I18N_DICTIONARY.get(parts[1]);

		if (context == null) {
			result.setFailureCause(PartialRetrievalFailureCause.UNSUPPORTED_PREFIX);
			return result;
		}

		if (context == CustomizableFieldContext.EXPOSURE) {
			logger.debug("Exposure customizable field retrieval is not supported: [{}]", originalPath);
			result.setFailureCause(PartialRetrievalFailureCause.UNSUPPORTED_PREFIX);
			return result;
		}

		result.setContext(context);
		result.setLeafFieldName(parts[2]);
		return result;
	}

	private Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>> resolve(
		ParsedCustomizableField parsed,
		Map<CustomizableFieldContext, List<CustomizableFieldMetadataDto>> metadataByContext,
		Map<CustomizableFieldContext, Map<CustomizableFieldMetadataDto, CustomizableFieldValueDto>> valuesByContext) {

		String path = parsed.getOriginalPath();

		if (!parsed.isValid()) {
			return Tuple.of(path, Tuple.of((FieldInfo) null, parsed.getFailureCause()));
		}

		CustomizableFieldContext context = parsed.getContext();
		String leafFieldName = parsed.getLeafFieldName();

		List<CustomizableFieldMetadataDto> activeMetadata = metadataByContext.get(context);
		Optional<CustomizableFieldMetadataDto> matchedMetadata =
			activeMetadata.stream().filter(m -> m.getName().equals(leafFieldName)).collect(CollectorUtils.toOptionalSingle());

		if (matchedMetadata.isEmpty()) {
			logger.warn("No active customizable field metadata found for context [{}] and name [{}]", context, leafFieldName);
			return Tuple.of(path, Tuple.of((FieldInfo) null, PartialRetrievalFailureCause.FIELD_DOES_NOT_EXIST));
		}

		CustomizableFieldMetadataDto metadataDto = matchedMetadata.get();
		CustomizableFieldValueDto valueDto = valuesByContext.get(context).get(metadataDto);
		String rawValue = valueDto != null ? valueDto.getValue() : null;

		return Tuple.of(
			path,
			Tuple.of(new FieldInfo().setFieldType(String.class).setFieldValue(rawValue).setTranslatedFieldName(metadataDto.getName()), null));
	}

	private String getEntityUuid(CustomizableFieldContext context, CaseDataDto caseData) {
		switch (context) {
		case CASE:
			return caseData.getUuid();
		case EPIDATA:
			return caseData.getEpiData().getUuid();
		default:
			throw new IllegalArgumentException("Unsupported context for partial retrieval: " + context);
		}
	}

	private static final class ParsedCustomizableField {

		private final String originalPath;
		private CustomizableFieldContext context;
		private String leafFieldName;
		private PartialRetrievalFailureCause failureCause;

		ParsedCustomizableField(String originalPath) {
			this.originalPath = originalPath;
		}

		boolean isValid() {
			return failureCause == null;
		}

		String getOriginalPath() {
			return originalPath;
		}

		CustomizableFieldContext getContext() {
			return context;
		}

		void setContext(CustomizableFieldContext context) {
			this.context = context;
		}

		String getLeafFieldName() {
			return leafFieldName;
		}

		void setLeafFieldName(String leafFieldName) {
			this.leafFieldName = leafFieldName;
		}

		PartialRetrievalFailureCause getFailureCause() {
			return failureCause;
		}

		void setFailureCause(PartialRetrievalFailureCause failureCause) {
			this.failureCause = failureCause;
		}
	}
}
