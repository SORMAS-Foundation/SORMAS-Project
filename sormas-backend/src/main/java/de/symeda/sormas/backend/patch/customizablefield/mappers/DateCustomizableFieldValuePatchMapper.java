package de.symeda.sormas.backend.patch.customizablefield.mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import de.symeda.sormas.backend.patch.mapping.impl.valuemapper.DatePatchMapper;

@ApplicationScoped
public class DateCustomizableFieldValuePatchMapper implements CustomizableFieldValuePatchMapper {

	private static final Map<CustomizableFieldType, Tuple<Class<?>, CustomizableFieldSetter<?>>> DICTIONARY = Map.of(
		CustomizableFieldType.DATE,
		CustomizableFieldValuePatchMapper.buildTuple(LocalDate.class, CustomizableFieldValueDto::setValueAsDate),

		CustomizableFieldType.DATE_TIME,
		CustomizableFieldValuePatchMapper.buildTuple(LocalDateTime.class, CustomizableFieldValueDto::setValueAsDateTime));

	public static final Set<CustomizableFieldType> SUPPORTED_TYPES = DICTIONARY.keySet();

	@Inject
	private DatePatchMapper datePatchMapper;

	@Override
	public ValueMappingResult<CustomizableFieldValueDto> map(CustomizableFieldValuePatchRequest request) {
		Tuple<Class<?>, CustomizableFieldSetter<?>> tuple = DICTIONARY.get(request.getTargetType());
		ValueMappingResult<?> result = datePatchMapper.map(request.getValue(), tuple.getFirst());

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
