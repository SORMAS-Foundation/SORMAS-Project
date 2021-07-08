package de.symeda.sormas.ui.travelentry.components;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class TravelEntryCreateForm extends AbstractEditForm<TravelEntryDto> {

	public TravelEntryCreateForm() {
		super(
			TravelEntryDto.class,
			TravelEntryDto.I18N_PREFIX,
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
