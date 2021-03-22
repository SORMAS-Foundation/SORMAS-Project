package de.symeda.sormas.ui.configuration.infrastructure;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.SubcontinentDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class SubcontinentEditForm extends AbstractEditForm<SubcontinentDto> {

	//@formatter:off
    private static final String HTML_LAYOUT =
            fluidRowLocs(SubcontinentDto.EXTERNAL_ID, SubcontinentDto.DEFAULT_NAME)+
            fluidRowLocs(SubcontinentDto.CONTINENT);
    //@formatter:on

	private final Boolean create;

	public SubcontinentEditForm(boolean create) {

		super(
			SubcontinentDto.class,
			SubcontinentDto.I18N_PREFIX,
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

		addField(SubcontinentDto.DEFAULT_NAME, TextField.class);
		addField(SubcontinentDto.EXTERNAL_ID, TextField.class);
		ComboBox continent = addField(SubcontinentDto.CONTINENT, ComboBox.class);

		continent.addItems(FacadeProvider.getContinentFacade().getAllActiveAsReference());

		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(true, SubcontinentDto.DEFAULT_NAME, SubcontinentDto.CONTINENT);

		if (!create) {
			continent.setEnabled(false);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
