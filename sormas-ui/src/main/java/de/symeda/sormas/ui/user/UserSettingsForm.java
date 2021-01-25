package de.symeda.sormas.ui.user;

import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.v7.data.validator.EmailValidator;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;

import javax.ejb.EJB;

public class UserSettingsForm extends AbstractEditForm<UserDto> {

	private static final long serialVersionUID = -928337100277917699L;

	private static final String HTML_LAYOUT = loc(UserDto.LANGUAGE) + loc(UserDto.USER_EMAIL);

	private TextField emailTf;

	public UserSettingsForm() {
		super(UserDto.class, UserDto.I18N_PREFIX);

		setWidth(480, Unit.PIXELS);
	}

	@Override
	protected void addFields() {
		ComboBox cbLanguage = addField(UserDto.LANGUAGE, ComboBox.class);
		CssStyles.style(cbLanguage, CssStyles.COMBO_BOX_WITH_FLAG_ICON);
		ControllerProvider.getUserController().setFlagIcons(cbLanguage);

		ComboBox cbExportFormat = addField(UserDto.EXPORT_FORMAT, ComboBox.class);
		CssStyles.style(cbExportFormat, CssStyles.COMBO_BOX_WITH_FLAG_ICON);
		ControllerProvider.getUserController().setExportFormatIcons(cbExportFormat);

		String authenticationProvider = FacadeProvider.getConfigFacade().getAuthenticationProvider();
		if (AuthProvider.KEYCLOAK.equals(authenticationProvider)) {
			emailTf = addField(UserDto.USER_EMAIL, TextField.class);
			emailTf.setCaption(I18nProperties.getCaption(Captions.User_userEmail));
			emailTf.addValidator(new EmailValidator(I18nProperties.getValidationError(Validations.validEmailAddress, emailTf.getCaption())));
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	public void setValue(UserDto userDto) {
		super.setValue(userDto);
		if (emailTf != null) {
			emailTf.setValue(userDto.getUserEmail());
		}
	}
}
