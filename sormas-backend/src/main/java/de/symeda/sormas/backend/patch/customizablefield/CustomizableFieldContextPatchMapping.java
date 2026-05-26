package de.symeda.sormas.backend.patch.customizablefield;

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

	private final CustomizableFieldContext customizableFieldContext;
	private final String patchName;

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
}
