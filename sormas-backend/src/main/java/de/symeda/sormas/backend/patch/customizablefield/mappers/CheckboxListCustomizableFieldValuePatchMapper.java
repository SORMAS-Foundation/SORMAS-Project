package de.symeda.sormas.backend.patch.customizablefield.mappers;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldValuePatchRequest;

@ApplicationScoped
public class CheckboxListCustomizableFieldValuePatchMapper implements CustomizableFieldValuePatchMapper {

	private static final Set<CustomizableFieldType> SUPPORTED_TYPES = Set.of(CustomizableFieldType.CHECKBOX_LIST);

	@Override
	public ValueMappingResult<CustomizableFieldValueDto> map(CustomizableFieldValuePatchRequest request) {
		String rawValue = request.getValue().toString();

		Set<String> values = Arrays.stream(rawValue.split(",")).map(String::trim).filter(str -> !str.isEmpty()).collect(Collectors.toSet());

		CustomizableFieldValueDto dto = request.getCustomizableFieldValueDto();
		dto.setValueAsStringSet(values);

		return ValueMappingResult.withData(dto);
	}

	@Override
	public Set<CustomizableFieldType> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}
}
