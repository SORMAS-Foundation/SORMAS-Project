package de.symeda.sormas.ui.immunization.components.form;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class ImmunizationDataForm extends AbstractEditForm<ImmunizationDto> {

	private final String immunizationUuid;

	public ImmunizationDataForm(String immunizationUuid, boolean isPseudonymized) {
		super(
			ImmunizationDto.class,
			ImmunizationDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			createFieldAccessCheckers(isPseudonymized, true));
		this.immunizationUuid = immunizationUuid;
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return null;
	}

	@Override
	protected void addFields() {

	}

	private static UiFieldAccessCheckers createFieldAccessCheckers(boolean isPseudonymized, boolean withPersonalAndSensitive) {
		if (withPersonalAndSensitive) {
			return UiFieldAccessCheckers.getDefault(isPseudonymized);
		}

		return UiFieldAccessCheckers.getNoop();
	}
}
