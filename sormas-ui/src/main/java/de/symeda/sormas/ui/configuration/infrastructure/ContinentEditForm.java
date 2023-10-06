package de.symeda.sormas.ui.configuration.infrastructure;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.infrastructure.InfrastructureDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class ContinentEditForm extends AbstractEditForm<ContinentDto> {

	//@formatter:off
    private static final String HTML_LAYOUT =
            fluidRowLocs(ContinentDto.EXTERNAL_ID, ContinentDto.DEFAULT_NAME) +
			fluidRowLocs(InfrastructureDto.DEFAULT_INFRASTRUCTURE);
    //@formatter:on

	private final Boolean create;

	public ContinentEditForm(boolean create) {

		super(
			ContinentDto.class,
			ContinentDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withFeatureTypes(FacadeProvider.getFeatureConfigurationFacade().getActiveServerFeatureConfigurations()),
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

		addField(ContinentDto.DEFAULT_NAME, TextField.class);
		addField(ContinentDto.EXTERNAL_ID, TextField.class);

		if (FacadeProvider.getFeatureConfigurationFacade()
			.isPropertyValueTrue(FeatureType.CASE_SURVEILANCE, FeatureTypeProperty.HIDE_JURISDICTION_FIELDS)) {
			addField(InfrastructureDto.DEFAULT_INFRASTRUCTURE, CheckBox.class);
		}

		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(true, ContinentDto.DEFAULT_NAME);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
