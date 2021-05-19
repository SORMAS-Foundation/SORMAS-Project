/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.ui.user;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.ui.Notification;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class UserController {

	public void create() {
		CommitDiscardWrapperComponent<UserEditForm> userCreateComponent = getUserCreateComponent();
		Window window = VaadinUiUtil.showModalPopupWindow(userCreateComponent, I18nProperties.getString(Strings.headingCreateNewUser));
		// user form is too big for typical screens
		window.setWidth(userCreateComponent.getWrappedComponent().getWidth() + 64 + 20, Unit.PIXELS);
		window.setHeight(90, Unit.PERCENTAGE);
	}

	public void edit(UserDto user) {
		Window window = VaadinUiUtil.createPopupWindow();
		CommitDiscardWrapperComponent<UserEditForm> userComponent = getUserEditComponent(user.getUuid(), window::close);
		window.setCaption(I18nProperties.getString(Strings.headingEditUser));
		window.setContent(userComponent);
		// user form is too big for typical screens
		window.setWidth(userComponent.getWrappedComponent().getWidth() + 64 + 20, Unit.PIXELS);
		window.setHeight(90, Unit.PERCENTAGE);
		UI.getCurrent().addWindow(window);
	}

	public void overview() {
		String navigationState = UsersView.VIEW_NAME;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	/**
	 * Update the fragment without causing navigator to change view
	 */
	public void setUriFragmentParameter(String caseUuid) {
		String fragmentParameter;
		if (caseUuid == null || caseUuid.isEmpty()) {
			fragmentParameter = "";
		} else {
			fragmentParameter = caseUuid;
		}

		Page page = SormasUI.get().getPage();
		page.setUriFragment("!" + UsersView.VIEW_NAME + "/" + fragmentParameter, false);
	}

	public CommitDiscardWrapperComponent<UserEditForm> getUserEditComponent(final String userUuid, Runnable closeWindowCallback) {
		UserEditForm userEditForm = new UserEditForm(false);
		UserDto userDto = FacadeProvider.getUserFacade().getByUuid(userUuid);
		userEditForm.setValue(userDto);
		final CommitDiscardWrapperComponent<UserEditForm> editView = new CommitDiscardWrapperComponent<UserEditForm>(
			userEditForm,
			UserProvider.getCurrent().hasUserRight(UserRight.USER_EDIT),
			userEditForm.getFieldGroup());

		// Add reset password button
		Button resetPasswordButton = createResetPasswordButton(userUuid, userDto.getUserEmail(), editView);
		editView.getButtonsPanel().addComponent(resetPasswordButton, 0);

		editView.addDiscardListener(closeWindowCallback::run);
		editView.addCommitListener(() -> {
			if (!userEditForm.getFieldGroup().isModified()) {
				UserDto user = userEditForm.getValue();
				UserDto existingUser = FacadeProvider.getUserFacade().getByUuid(user.getUuid());
				// If user roles have changed, the user might no longer have a district assigned and therefore
				// needs to be removed as surveillance/contact officer from existing cases/contacts
				if (existingUser.getDistrict() != null && user.getDistrict() == null) {
					openRemoveUserAsOfficerPrompt(result -> {
						if (result) {
							saveUser(user);
							FacadeProvider.getUserFacade().removeUserAsSurveillanceAndContactOfficer(user.getUuid());
							closeWindowCallback.run();
						}
					});
				} else {
					saveUser(user);
					closeWindowCallback.run();
				}
			}
		});

		return editView;
	}

	private void openRemoveUserAsOfficerPrompt(Consumer<Boolean> callback) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingSaveUser),
			new Label(I18nProperties.getString(Strings.confirmationRemoveUserAsOfficer)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			callback);
	}

	private void saveUser(UserDto user) {
		FacadeProvider.getUserFacade().saveUser(user);
		refreshView();
	}

	public CommitDiscardWrapperComponent<UserEditForm> getUserCreateComponent() {

		UserEditForm createForm = new UserEditForm(true);
		createForm.setValue(UserDto.build());
		final CommitDiscardWrapperComponent<UserEditForm> editView = new CommitDiscardWrapperComponent<UserEditForm>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.USER_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					UserDto dto = createForm.getValue();
					dto = FacadeProvider.getUserFacade().saveUser(dto);
					refreshView();
					makeInitialPassword(dto.getUuid(), dto.getUserEmail());
				}
			}
		});
		return editView;
	}

	public boolean isLoginUnique(String uuid, String userName) {
		return FacadeProvider.getUserFacade().isLoginUnique(uuid, userName);
	}

	public void makeInitialPassword(String userUuid, String userEmail) {
		if (StringUtils.isBlank(userEmail) || AuthProvider.getProvider().isDefaultProvider()) {
			String newPassword = FacadeProvider.getUserFacade().resetPassword(userUuid);
			showPasswordResetInternalSuccessPopup(newPassword);
		} else {
			showAccountCreatedSuccessful();
		}
	}

	public void makeNewPassword(String userUuid, String userEmail) {
		String newPassword = FacadeProvider.getUserFacade().resetPassword(userUuid);

		if (StringUtils.isBlank(userEmail) || AuthProvider.getProvider().isDefaultProvider()) {
			showPasswordResetInternalSuccessPopup(newPassword);
		} else {
			showPasswordResetExternalSuccessPopup();
		}
	}

	private void showPasswordResetInternalSuccessPopup(String newPassword) {
		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(new Label(I18nProperties.getString(Strings.messageCopyPassword)));
		Label passwordLabel = new Label(newPassword);
		passwordLabel.addStyleName(CssStyles.H2);
		layout.addComponent(passwordLabel);
		Window popupWindow = VaadinUiUtil.showPopupWindow(layout);
		popupWindow.setCaption(I18nProperties.getString(Strings.headingNewPassword));
		layout.setMargin(true);
	}

	private void showAccountCreatedSuccessful() {
		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(new Label(I18nProperties.getString(Strings.messageActivateAccount)));
		Window popupWindow = VaadinUiUtil.showPopupWindow(layout);
		popupWindow.setCaption(I18nProperties.getString(Strings.headingNewAccount));
		popupWindow.setWidth(350, Unit.PIXELS);
		layout.setMargin(true);
	}

	private void showPasswordResetExternalSuccessPopup() {
		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(new Label(I18nProperties.getString(Strings.messagePasswordResetEmailLink)));
		Window popupWindow = VaadinUiUtil.showPopupWindow(layout);
		popupWindow.setCaption(I18nProperties.getString(Strings.headingNewPassword));
		popupWindow.setWidth(450, Unit.PIXELS);
		layout.setMargin(true);
	}

	private void refreshView() {
		View currentView = SormasUI.get().getNavigator().getCurrentView();
		if (currentView instanceof UsersView) {
			// force refresh, because view didn't change
			((UsersView) currentView).enter(null);
		}
	}

	public Button createResetPasswordButton(String userUuid, String userEmail, CommitDiscardWrapperComponent<UserEditForm> editView) {

		return ButtonHelper.createIconButton(Captions.userResetPassword, VaadinIcons.UNLOCK, new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmationComponent resetPasswordComponent = getResetPasswordConfirmationComponent(userUuid, userEmail, editView);
				Window popupWindow = VaadinUiUtil.showPopupWindow(resetPasswordComponent);
				resetPasswordComponent.addDoneListener(() -> popupWindow.close());
				resetPasswordComponent.getCancelButton().addClickListener(new ClickListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						popupWindow.close();
					}
				});
				popupWindow.setCaption(I18nProperties.getString(Strings.headingUpdatePassword));
			}
		}, ValoTheme.BUTTON_LINK);
	}

	public ConfirmationComponent getResetPasswordConfirmationComponent(
		String userUuid,
		String userEmail,
		CommitDiscardWrapperComponent<UserEditForm> editView) {
		ConfirmationComponent resetPasswordConfirmationComponent = new ConfirmationComponent(false) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfirm() {
				onDone();
				editView.discard();
				makeNewPassword(userUuid, userEmail);
			}

			@Override
			protected void onCancel() {
			}
		};
		resetPasswordConfirmationComponent.getConfirmButton().setCaption(I18nProperties.getCaption(Captions.userUpdatePasswordConfirmation));
		resetPasswordConfirmationComponent.getCancelButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));
		resetPasswordConfirmationComponent.setMargin(true);
		return resetPasswordConfirmationComponent;
	}

	public CommitDiscardWrapperComponent<UserSettingsForm> getUserSettingsComponent(Runnable commitOrDiscardCallback) {
		UserSettingsForm form = new UserSettingsForm();
		UserDto user = FacadeProvider.getUserFacade().getByUuid(UserProvider.getCurrent().getUuid());
		form.setValue(user);

		final CommitDiscardWrapperComponent<UserSettingsForm> component = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		component.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				UserDto changedUser = form.getValue();
				FacadeProvider.getUserFacade().saveUser(changedUser);
				I18nProperties.setUserLanguage(changedUser.getLanguage());
				FacadeProvider.getI18nFacade().setUserLanguage(changedUser.getLanguage());
				Page.getCurrent().reload();
				commitOrDiscardCallback.run();
			}
		});
		component.addDiscardListener(commitOrDiscardCallback::run);

		return component;
	}

	public void setFlagIcons(ComboBox cbLanguage) {
		for (Language language : Language.values()) {
			cbLanguage.setItemIcon(language, new ThemeResource("img/flag-icons/" + language.name().toLowerCase() + ".png"));
		}
	}

	public void sync() {
		Window window = VaadinUiUtil.showPopupWindow(new UsersSyncLayout());
		window.setCaption(I18nProperties.getCaption(Captions.syncUsers));
	}

	public void enableAllSelectedItems(Collection<UserDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoUsersSelected),
				I18nProperties.getString(Strings.messageNoUsersSelected),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmEnabling),
				new Label(String.format(I18nProperties.getString(Strings.confirmationEnableUsers), selectedRows.size())),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				confirmed -> {
					if (!confirmed) {
						return;
					}

					List<String> uuids = selectedRows.stream().map(UserDto::getUuid).collect(Collectors.toList());
					FacadeProvider.getUserFacade().enableUsers(uuids);
					callback.run();
					new Notification(
						I18nProperties.getString(Strings.headingUsersEnabled),
						I18nProperties.getString(Strings.messageUsersEnabled),
						Notification.Type.HUMANIZED_MESSAGE,
						false).show(Page.getCurrent());
				});
		}
	}

	public void disableAllSelectedItems(Collection<UserDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoUsersSelected),
				I18nProperties.getString(Strings.messageNoUsersSelected),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmDisabling),
				new Label(String.format(I18nProperties.getString(Strings.confirmationDisableUsers), selectedRows.size())),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				confirmed -> {
					if (!confirmed) {
						return;
					}

					List<String> uuids = selectedRows.stream().map(UserDto::getUuid).collect(Collectors.toList());
					FacadeProvider.getUserFacade().disableUsers(uuids);
					callback.run();
					new Notification(
						I18nProperties.getString(Strings.headingUsersDisabled),
						I18nProperties.getString(Strings.messageUsersDisabled),
						Notification.Type.HUMANIZED_MESSAGE,
						false).show(Page.getCurrent());
				});
		}
	}
}
