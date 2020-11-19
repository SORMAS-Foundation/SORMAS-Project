package de.symeda.sormas.ui.configuration.infrastructure;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.CountryDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class CountryEditForm extends AbstractEditForm<CountryDto> {

	private static final long serialVersionUID = 5397257740478821166L;

	//@formatter:off
    private static final String HTML_LAYOUT =
            fluidRowLocs(CountryDto.ISO_CODE, CountryDto.DEFAULT_NAME) +
            fluidRowLocs(CountryDto.EXTERNAL_ID, CountryDto.UNO_CODE);
    //@formatter:on

	private final Boolean create;

	public CountryEditForm(boolean create) {

		super(
			CountryDto.class,
			CountryDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withFeatureTypes(FacadeProvider.getFeatureConfigurationFacade().getActiveServerFeatureTypes()),
			UiFieldAccessCheckers.getNoop());
		this.create = create;

		setWidth(540, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}

		addFields();
	}

	@Override
	protected void addFields() {
		if (create == null) {
			return;
		}

		addField(CountryDto.DEFAULT_NAME, TextField.class);
		addField(CountryDto.ISO_CODE, TextField.class).setMaxLength(3);
		addField(CountryDto.EXTERNAL_ID, TextField.class);
		addField(CountryDto.UNO_CODE, TextField.class).setMaxLength(3);

		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(true, CountryDto.DEFAULT_NAME, CountryDto.ISO_CODE);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
