package de.symeda.sormas.backend.patch.customizablefield.mappers;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldValuePatchRequest;
import de.symeda.sormas.backend.patch.mapping.impl.valuemapper.EnumPatchMapper;

@ApplicationScoped
public class YesNoUnknownCustomizableFieldValuePatchMapper implements CustomizableFieldValuePatchMapper {

	private static final Set<CustomizableFieldType> SUPPORTED_TYPES = Set.of(CustomizableFieldType.YES_NO_UNKNOWN);

	@Inject
	private EnumPatchMapper enumPatchMapper;

	@Override
	public ValueMappingResult<CustomizableFieldValueDto> map(CustomizableFieldValuePatchRequest request) {
		ValueMappingResult<YesNoUnknown> result = enumPatchMapper.map(request.getValue(), YesNoUnknown.class);

		CustomizableFieldValueDto dto = request.getCustomizableFieldValueDto();
		dto.setValueAsYesNoUnknown(result.getData());

		return CustomizableFieldValuePatchMapper.buildMappingResultFrom(result, dto);
	}

	@Override
	public Set<CustomizableFieldType> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}
}
