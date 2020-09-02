package de.symeda.sormas.ui.user;

import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;

public class UserSettingsForm extends AbstractEditForm<UserDto> {

	private static final long serialVersionUID = -928337100277917699L;

	private static final String HTML_LAYOUT = loc(UserDto.LANGUAGE);

	public UserSettingsForm() {
		super(UserDto.class, UserDto.I18N_PREFIX);

		setWidth(480, Unit.PIXELS);
	}

	@Override
	protected void addFields() {
		ComboBox cbLanguage = addField(UserDto.LANGUAGE, ComboBox.class);
		CssStyles.style(cbLanguage, CssStyles.COMBO_BOX_WITH_FLAG_ICON);
		ControllerProvider.getUserController().setFlagIcons(cbLanguage);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
