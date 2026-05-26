package de.symeda.sormas.backend.patch.customizablefield.mappers;

import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldSetter;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldValuePatchRequest;
import de.symeda.sormas.backend.patch.mapping.impl.valuemapper.PrimitivePatchMapper;

@ApplicationScoped
public class PrivimitiveCustomizableFieldValuePatchMapper implements CustomizableFieldValuePatchMapper {

	private static final Map<CustomizableFieldType, Class<?>> CUSTOM_TYPE_STORAGE_TYPE_DICTIONARY = Map.of(
		CustomizableFieldType.TEXT,
		String.class,

		CustomizableFieldType.TEXTAREA,
		String.class,

		CustomizableFieldType.NUMBER,
		String.class,

		CustomizableFieldType.DECIMAL,
		String.class,

		CustomizableFieldType.COMBOBOX,
		String.class,

		CustomizableFieldType.CHECKBOX,
		Boolean.class,

		CustomizableFieldType.RADIO_BUTTON_LIST,
		String.class);

	private static final Set<CustomizableFieldType> SUPPORTED_TYPES = CUSTOM_TYPE_STORAGE_TYPE_DICTIONARY.keySet();

	@Inject
	private PrimitivePatchMapper primitivePatchMapper;

	@Override
	public ValueMappingResult<CustomizableFieldValueDto> map(CustomizableFieldValuePatchRequest request) {

		CustomizableFieldType targetType = request.getTargetType();

		Class<?> targetClass = CUSTOM_TYPE_STORAGE_TYPE_DICTIONARY.get(targetType);

		ValueMappingResult<?> result = primitivePatchMapper.map(request.getValue(), targetClass);

		Tuple<Class<?>, CustomizableFieldSetter<?>> tuple;
		if (targetClass == Boolean.class) {
			tuple = CustomizableFieldValuePatchMapper.buildTuple(Boolean.class, CustomizableFieldValueDto::setValueAsBoolean);
		} else {
			tuple = CustomizableFieldValuePatchMapper.buildTuple(String.class, CustomizableFieldValueDto::setValue);
		}

		CustomizableFieldSetter<?> second = tuple.getSecond();

		CustomizableFieldValueDto customizableFieldValueDto = request.getCustomizableFieldValueDto();
		Object data = result.getData();
		second.accept(customizableFieldValueDto, data);

		return CustomizableFieldValuePatchMapper.buildMappingResultFrom(result, customizableFieldValueDto);
	}

	@Override
	public Set<CustomizableFieldType> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}
}
