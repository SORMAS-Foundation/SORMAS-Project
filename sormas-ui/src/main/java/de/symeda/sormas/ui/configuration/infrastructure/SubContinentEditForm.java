package de.symeda.sormas.ui.configuration.infrastructure;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.SubContinentDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class SubContinentEditForm extends AbstractEditForm<SubContinentDto> {

	//@formatter:off
    private static final String HTML_LAYOUT =
            fluidRowLocs(SubContinentDto.EXTERNAL_ID, SubContinentDto.DEFAULT_NAME)+
            fluidRowLocs(SubContinentDto.CONTINENT);
    //@formatter:on

	private final Boolean create;

	public SubContinentEditForm(boolean create) {

		super(
			SubContinentDto.class,
			SubContinentDto.I18N_PREFIX,
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

		addField(SubContinentDto.DEFAULT_NAME, TextField.class);
		addField(SubContinentDto.EXTERNAL_ID, TextField.class);
		ComboBox continent = addField(SubContinentDto.CONTINENT, ComboBox.class);

		continent.addItems(FacadeProvider.getContinentFacade().getAllActiveAsReference());

		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(true, SubContinentDto.DEFAULT_NAME, SubContinentDto.CONTINENT);

		if (!create) {
			continent.setEnabled(false);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
