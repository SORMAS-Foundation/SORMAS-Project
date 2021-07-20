package de.symeda.sormas.ui.immunization.components;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class ImmunizationDataForm extends AbstractEditForm<ImmunizationDto> {

	public ImmunizationDataForm() {
		super(
			ImmunizationDto.class,
			ImmunizationDto.I18N_PREFIX,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()));
		setWidth(720, Unit.PIXELS);
		hideValidationUntilNextCommit();
	}

	@Override
	protected String createHtmlLayout() {
		return null;
	}

	@Override
	protected void addFields() {

	}
}
