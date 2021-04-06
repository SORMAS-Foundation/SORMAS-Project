/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.login;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class ChangeDefaultUserPasswordWindow extends Window {

	public ChangeDefaultUserPasswordWindow(Runnable onContinue, UserDto currentUser) {
		setCaption(" " + I18nProperties.getString(Strings.headingSecurityAlert));
		setIcon(VaadinIcons.WARNING);
		setWidth(40, Unit.PERCENTAGE);
		final VerticalLayout content = new VerticalLayout();
		content.setMargin(true);
		content.setWidthFull();

		Label introductionLabel = new Label(I18nProperties.getString(Strings.DefaultPassword_ownUserIntroduction));
		introductionLabel.setWidthFull();
		introductionLabel.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		content.addComponent(introductionLabel);

		Label actionLabel = new Label(I18nProperties.getString(Strings.DefaultPassword_ownUserAction));
		actionLabel.setWidthFull();
		actionLabel.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		content.addComponent(actionLabel);

		Label newPasswordLabel = new Label();
		newPasswordLabel.setWidthFull();
		newPasswordLabel.setVisible(false);
		newPasswordLabel.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		newPasswordLabel.setStyleName(CssStyles.H2, true);
		content.addComponent(newPasswordLabel);

		Label newPasswordSetHintsLabel = new Label(I18nProperties.getString(Strings.DefaultPassword_ownUserNewPasswordSetHints));
		newPasswordSetHintsLabel.setWidthFull();
		newPasswordSetHintsLabel.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		newPasswordSetHintsLabel.setStyleName(ValoTheme.LABEL_SUCCESS, true);
		newPasswordSetHintsLabel.setVisible(false);
		content.addComponent(newPasswordSetHintsLabel);

		Button continueButton = ButtonHelper.createButton(Captions.actionRemindMeLater, (Button.ClickListener) clickEvent -> {
			close();
			onContinue.run();
		}, ValoTheme.BUTTON_PRIMARY);

		Button generateNewPasswordButton = ButtonHelper.createButton(Captions.actionGenerateNewPassword, (Button.ClickListener) clickEvent -> {
			newPasswordLabel.setVisible(true);
			newPasswordSetHintsLabel.setVisible(true);
			continueButton.setCaption(I18nProperties.getCaption(Captions.actionContinue));
			clickEvent.getButton().setVisible(false);
			newPasswordLabel.setValue(
				String.format(
					I18nProperties.getString(Strings.DefaultPassword_newPasswordPlaceholder),
					FacadeProvider.getUserFacade().resetPassword(currentUser.getUuid())));
			center();

		});
		content.addComponent(generateNewPasswordButton);
		content.addComponent(continueButton);

		setModal(true);
		setResizable(false);
		setClosable(false);
		setContent(content);
		setDraggable(false);

	}
}
