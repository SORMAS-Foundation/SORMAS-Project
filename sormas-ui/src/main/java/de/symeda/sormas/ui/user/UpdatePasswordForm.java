package de.symeda.sormas.ui.user;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.PasswordField;

import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class UpdatePasswordForm extends AbstractEditForm<UserDto> {

	private static final long serialVersionUID = 1L;
	private static final String OLD_PASSWORD = "oldPassword";
	private static final String NEW_PASSWORD = "newPassword";
	private static final String CONFIRM_PASSWORD = "updateConfirmPassword";

	//@formater:off
	private static final String HTML_LAYOUT = loc(OLD_PASSWORD)
		+ fluidRowLocs(UserDto.PASSWORD)
		+ loc(NEW_PASSWORD)
		+ fluidRowLocs(UserDto.NEW_PASSWORD)
		+ loc(CONFIRM_PASSWORD)
		+ fluidRowLocs(UserDto.CONFIRM_PASSWORD);

	//@formatter:off
	PasswordField currentPassword;
	PasswordField newPassword;
	PasswordField confirmPassword;
	public UpdatePasswordForm(){
		super(UserDto.class, UserDto.I18N_PREFIX);

		setWidth(480, Unit.PIXELS);
	}
	public void showNotification(Notification notification) {
		// keep the notification visible a little while after moving the
		// mouse, or until clicked
		notification.setDelayMsec(2000);
		notification.show(Page.getCurrent());
	}
	@Override
	protected void addFields() {
		currentPassword = addField(UserDto.PASSWORD, PasswordField.class);
		newPassword = addField(UserDto.NEW_PASSWORD, PasswordField.class);
		confirmPassword = addField(UserDto.CONFIRM_PASSWORD, PasswordField.class);
		setRequired(true,UserDto.PASSWORD,UserDto.NEW_PASSWORD,UserDto.CONFIRM_PASSWORD);
	}



	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
