package de.symeda.sormas.ui.configuration.infrastructure;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.region.AreaDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;

@SuppressWarnings("serial")
public class AreaEditForm extends AbstractEditForm<AreaDto> {

	private static final String HTML_LAYOUT = fluidRowLocs(AreaDto.NAME, AreaDto.EXTERNAL_ID);

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

		setRequired(true, AreaDto.NAME);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
