package de.symeda.sormas.backend.patch.customizablefield;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.exposure.ExposureDto;

/**
 * To be able to use the same naming convention as for non-customizable fields.
 */
public enum CustomizableFieldContextPatchMapping {

	CASE(CustomizableFieldContext.CASE, CaseDataDto.I18N_PREFIX),
	EPIDATA(CustomizableFieldContext.EPIDATA, EpiDataDto.I18N_PREFIX),
	EXPOSURE(CustomizableFieldContext.EXPOSURE, ExposureDto.I18N_PREFIX);

	@NotNull
	private final CustomizableFieldContext customizableFieldContext;
	@NotNull
	private final String patchName;

	public static final Map<String, CustomizableFieldContext> I18N_DICTIONARY = Arrays.stream(CustomizableFieldContextPatchMapping.values())
		.collect(
			Collectors.toMap(CustomizableFieldContextPatchMapping::getPatchName, CustomizableFieldContextPatchMapping::getCustomizableFieldContext));

	CustomizableFieldContextPatchMapping(CustomizableFieldContext customizableFieldContext, String patchName) {
		this.customizableFieldContext = customizableFieldContext;
		this.patchName = patchName;
	}

	public CustomizableFieldContext getCustomizableFieldContext() {
		return customizableFieldContext;
	}

	public String getPatchName() {
		return patchName;
	}

	public static Optional<CustomizableFieldContext> fromI18nName(@NotNull String i18nName) {
		return Arrays.stream(CustomizableFieldContextPatchMapping.values())
			.filter(mapping -> mapping.patchName.equals(i18nName))
			.findAny()
			.map(CustomizableFieldContextPatchMapping::getCustomizableFieldContext);
	}
}
