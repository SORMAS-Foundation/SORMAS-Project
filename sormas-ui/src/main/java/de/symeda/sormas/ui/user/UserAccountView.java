package de.symeda.sormas.ui.user;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.validator.EmailValidator;
import com.vaadin.v7.data.validator.RegexpValidator;
import com.vaadin.v7.data.validator.StringLengthValidator;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.PasswordField;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserHelper;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.PasswordHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.user.UserEditForm.UserNameValidator;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.InternalPasswordChangeComponent;
import de.symeda.sormas.ui.utils.UserPhoneNumberValidator;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class UserAccountView extends AbstractEditForm<UserDto> {

	private static final long serialVersionUID = -928337100277917699L;

	private static final String PERSON_DATA_HEADING_LOC = "personDataHeadingLoc";
	private static final String ADDRESS_HEADING_LOC = "addressHeadingLoc";
	private static final String USER_DATA_HEADING_LOC = "userDataHeadingLoc";
	private static final String USER_EMAIL_DESC_LOC = "userEmailDescLoc";
	private static final String USER_PHONE_DESC_LOC = "userPhoneDescLoc";
	private static final String PASSWORD_BUTTON = "changePassword";

	private static final String HTML_LAYOUT = loc(PERSON_DATA_HEADING_LOC)
			+ fluidRowLocs(UserDto.FIRST_NAME, UserDto.LAST_NAME) + fluidRowLocs(UserDto.USER_EMAIL, UserDto.PHONE)
			+ fluidRowLocs(USER_EMAIL_DESC_LOC, USER_PHONE_DESC_LOC)
			+ fluidRowLocs(UserDto.USER_POSITION, UserDto.USER_ORGANISATION)

			+ loc(ADDRESS_HEADING_LOC) + fluidRowLocs(UserDto.ADDRESS)

			+ fluidRowLocsCss(VSPACE_TOP_3, UserDto.LANGUAGE, "") // + loc(USER_DATA_HEADING_LOC)
			// + fluidRowLocs(UserDto.USER_NAME)
			+ fluidRowLocs(PASSWORD_BUTTON);

	public UserAccountView() {
		super(UserDto.class, UserDto.I18N_PREFIX, true, new FieldVisibilityCheckers(), UiFieldAccessCheckers.getNoop());
		setWidth(640, Unit.PIXELS);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	class UserNameValidator implements Validator { 

		private static final long serialVersionUID = 1L;

		@Override
		public void validate(Object value) throws InvalidValueException {
			UserDto dto = getValue();
			if (!(value instanceof String
					&& ControllerProvider.getUserController().isLoginUnique(dto.getUuid(), (String) value)))
				throw new InvalidValueException(I18nProperties.getValidationError(Validations.userNameNotUnique));
		}
	}

	@Override
	protected void addFields() {
		Label personDataHeadingLabel = new Label(I18nProperties.getString(Strings.headingPersonData));
		personDataHeadingLabel.addStyleName(H3);
		getContent().addComponent(personDataHeadingLabel, PERSON_DATA_HEADING_LOC);

		Label addressHeadingLabel = new Label(I18nProperties.getString(Strings.address));
		addressHeadingLabel.addStyleName(H3);
		getContent().addComponent(addressHeadingLabel, ADDRESS_HEADING_LOC);

		Label userDataHeadingLabel = new Label(I18nProperties.getString(Strings.headingUserData));
		userDataHeadingLabel.addStyleName(H3);
		getContent().addComponent(userDataHeadingLabel, USER_DATA_HEADING_LOC);

		addField(UserDto.FIRST_NAME, TextField.class);
		addField(UserDto.LAST_NAME, TextField.class);
		addField(UserDto.USER_EMAIL, TextField.class);
		addField(UserDto.USER_POSITION, TextField.class);
		addField(UserDto.USER_ORGANISATION, TextField.class);
		TextField phone = addField(UserDto.PHONE, TextField.class);
		phone.addValidator(
				new UserPhoneNumberValidator(I18nProperties.getValidationError(Validations.phoneNumberValidation)));
		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.AGGREGATE_REPORTING)
				|| FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE)
				|| FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.WEEKLY_REPORTING)
				|| FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE)) {
			addDiseaseField(UserDto.LIMITED_DISEASE, false);
		}

		Label userEmailDesc = new Label(I18nProperties.getString(Strings.infoUserEmail));
		getContent().addComponent(userEmailDesc, USER_EMAIL_DESC_LOC);
		Label userPhoneDesc = new Label(I18nProperties.getString(Strings.infoUserPhoneNumber));
		userPhoneDesc.addStyleName("unwrapLabel");
		getContent().addComponent(userPhoneDesc, USER_PHONE_DESC_LOC);

		ComboBox cbLanguage = addField(UserDto.LANGUAGE, ComboBox.class);
		CssStyles.style(cbLanguage, CssStyles.COMBO_BOX_WITH_FLAG_ICON);
		ControllerProvider.getUserController().setFlagIcons(cbLanguage);

		addField(UserDto.ADDRESS, LocationEditForm.class).setCaption(null);

		addField(UserDto.USER_NAME, TextField.class);

		ComboBox laboratory = addInfrastructureField(UserDto.LABORATORY);
		laboratory.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(false));

		// addValidators(UserDto.USER_NAME, new UserNameValidator());
		// addFieldListeners(UserDto.FIRST_NAME, e -> suggestUserName());
		// addFieldListeners(UserDto.LAST_NAME, e -> suggestUserName());

		// add button for password reset
		Button changePasswordButton = new Button();
		changePasswordButton.setIcon(VaadinIcons.UNLOCK);
		changePasswordButton.setCaption("Change Password");
		InternalPasswordChangeComponent PasswordChangeConfirmationComponent = getPasswordChangeConfirmationComponent(
				this.getField(UserDto.USER_NAME).getValue().toString());

		changePasswordButton.addClickListener(e -> {
			Window popupWindow = VaadinUiUtil.showPopupWindow(PasswordChangeConfirmationComponent);
			PasswordChangeConfirmationComponent.addDoneListener(() -> popupWindow.close());
			PasswordChangeConfirmationComponent.getCancelButton().addClickListener(new ClickListener() {

				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					popupWindow.close();
				}
			});
			popupWindow.setCaption(I18nProperties.getString(Strings.headingUpdatePassword));
		});
		changePasswordButton.addStyleName(ValoTheme.BUTTON_LINK);

		getContent().addComponent(changePasswordButton, PASSWORD_BUTTON);

	}

	private void suggestUserName() {
		TextField fnField = (TextField) getFieldGroup().getField(UserDto.FIRST_NAME);
		TextField lnField = (TextField) getFieldGroup().getField(UserDto.LAST_NAME);
		TextField unField = (TextField) getFieldGroup().getField(UserDto.USER_NAME);
		if (!fnField.isEmpty() && !lnField.isEmpty() && unField.isEmpty() && !unField.isReadOnly()) {
			unField.setValue(UserHelper.getSuggestedUsername(fnField.getValue(), lnField.getValue()));
		}
	}

	public InternalPasswordChangeComponent getPasswordChangeConfirmationComponent(String userName) {
		InternalPasswordChangeComponent PasswordChangeConfirmationComponent = new InternalPasswordChangeComponent(
				false) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfirm() {
				onDone();
				// show window to show form to show password input fields
				showPasswordChangeInternalSuccessPopup(userName);
			}

			@Override
			protected void onCancel() {
			}
		};
		PasswordChangeConfirmationComponent.getConfirmButton()
				.setCaption(I18nProperties.getCaption(Captions.userUpdatePasswordConfirmation));
		PasswordChangeConfirmationComponent.getCancelButton()
				.setCaption(I18nProperties.getCaption(Captions.actionCancel));
		PasswordChangeConfirmationComponent.setMargin(true);
		return PasswordChangeConfirmationComponent;
	}

	private void showPasswordChangeInternalSuccessPopup(String userName) { // close
		VerticalLayout layout = new VerticalLayout();
		
		layout.addComponent(new Label(I18nProperties.getString(Strings.messageChangePassword)));
		PasswordField passField1 = new PasswordField(I18nProperties.getString(Strings.headingNewPassword));
		layout.addComponent(new Label("*Must be at least 8 characters"));
		layout.addComponent(new Label("*Must contain 1 Uppercase and 1 special character "));
		passField1.setSizeFull();
		passField1.addValidator(new RegexpValidator("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
				"Password does not meet requirement"));
		layout.addComponent(passField1);
		
		PasswordField passField2 = new PasswordField("Confirm New Password");
		passField2.setSizeFull();
		layout.addComponent(passField2);
		
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		Button changePassword = new Button("Save"); // VALO
		changePassword.setStyleName(CssStyles.VAADIN_BUTTON);
		changePassword.setStyleName(ValoTheme.BUTTON_PRIMARY);
		changePassword.setStyleName(CssStyles.FLOAT_RIGHT);
		
		buttonLayout.addComponent(changePassword);// cbCommunity

		

		Window popupWindow = VaadinUiUtil.showPopupWindow(layout);
		Button cancel = new Button("Cancel");
		cancel.setStyleName(CssStyles.VAADIN_BUTTON);
		cancel.setStyleName(CssStyles.FLOAT_RIGHT);
		buttonLayout.addComponent(cancel);
		
		changePassword.addClickListener(e -> {
			String newpass1 = passField1.getValue();
			String newpass2 = passField2.getValue();
			
			try {
				passField1.validate();
			} catch (Validator.InvalidValueException ex) {
				passField1.setValidationVisible(true);
			    Notification.show("Invalid value!");
			}
			if (newpass1.equals(newpass2) && passField1.isValid()) { 
				
				 FacadeProvider.getUserFacade()
						.changePassword(this.getField(UserDto.USER_NAME).getValue().toString(), newpass1);
				 popupWindow.close();
				 Notification.show("Password changed Successfully", Notification.Type.TRAY_NOTIFICATION);

			}
			else {
				Notification.show("Password does not match", Notification.Type.HUMANIZED_MESSAGE);
			}
		});
		cancel.addClickListener(e -> {
			popupWindow.close();
		});
		
		buttonLayout.setSpacing(true);
		layout.addComponent(buttonLayout);
		
		layout.setMargin(true);
		layout.setSpacing(false);
	}

}
