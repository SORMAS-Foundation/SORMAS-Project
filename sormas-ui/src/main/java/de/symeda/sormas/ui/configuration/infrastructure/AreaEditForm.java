package de.symeda.sormas.ui.configuration.infrastructure;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.infrastructure.InfrastructureDto;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;

@SuppressWarnings("serial")
public class AreaEditForm extends AbstractEditForm<AreaDto> {

	private static final String HTML_LAYOUT =
		fluidRowLocs(AreaDto.NAME, AreaDto.EXTERNAL_ID) + fluidRowLocs(InfrastructureDto.DEFAULT_INFRASTRUCTURE);

	public AreaEditForm(boolean create) {
		super(AreaDto.class, AreaDto.I18N_PREFIX);

		setWidth(540, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	@Override
	protected void addFields() {
		addField(AreaDto.NAME, TextField.class);
		addField(AreaDto.EXTERNAL_ID, TextField.class);

		if (FacadeProvider.getFeatureConfigurationFacade()
			.isPropertyValueTrue(FeatureType.CASE_SURVEILANCE, FeatureTypeProperty.HIDE_JURISDICTION_FIELDS)) {
			addField(InfrastructureDto.DEFAULT_INFRASTRUCTURE, CheckBox.class);
		}

		setRequired(true, AreaDto.NAME);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
