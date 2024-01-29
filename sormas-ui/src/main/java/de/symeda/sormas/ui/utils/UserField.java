package de.symeda.sormas.ui.utils;

import static de.symeda.sormas.ui.utils.CssStyles.LABEL_BOLD;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.v7.shared.ui.textfield.AbstractTextFieldState;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.v7.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class UserField extends CustomField<UserReferenceDto> {

	private ComboBoxWithPlaceholder userCombo;
	private boolean readOnly;
	private boolean enabled;
	private List<UserReferenceDto> items = new ArrayList<>();

	public UserField() {
	}

	@Override
	protected Component initContent() {
		HorizontalLayout userLayout = new HorizontalLayout();

		userCombo = ComboBoxHelper.createComboBoxV7();
		userCombo.addItems(items);

		userCombo.setNewItemsAllowed(true);

		userCombo.setImmediate(true);
		if (getValue() != null) {
			userCombo.addItem(getValue());
		}
		userCombo.setNullSelectionAllowed(true);
		userCombo.setValue(getValue());
		userCombo.setEnabled(enabled);
		userCombo.setReadOnly(readOnly);
		userLayout.addComponent(userCombo);
		final Button userContactButton = createUserContactButton();
		userLayout.addComponent(userContactButton);
		userLayout.setWidthFull();
		userLayout.setExpandRatio(userCombo, 1);
		userLayout.setSpacing(false);

		userCombo.setWidthFull();

		userCombo.addValueChangeListener(valueChangeEvent -> {
			super.setValue((UserReferenceDto) userCombo.getValue());
		});

		return userLayout;
	}

	public void addItems(List<UserReferenceDto> items) {
		this.items.addAll(items);
		if (userCombo == null) {
			userCombo = ComboBoxHelper.createComboBoxV7();
		}
		userCombo.addItems(this.items);
	}

	public void removeAllItems() {
		items.clear();
		if (userCombo != null) {
			userCombo.removeAllItems();
		}
	}

	protected Button createUserContactButton() {
		boolean isReadOnly = userCombo.isReadOnly();
		Button userContactButton = ButtonHelper.createIconButtonWithCaption("userContact", "", VaadinIcons.EYE, e -> {
			triggerUserContactPopUpWindow();
		},
			ValoTheme.BUTTON_ICON_ONLY,
			ValoTheme.BUTTON_BORDERLESS,
			ValoTheme.BUTTON_LARGE,
			isReadOnly ? CssStyles.HSPACE_LEFT_6 : CssStyles.HSPACE_LEFT_NONE,
			isReadOnly ? CssStyles.VSPACE_TOP_6 : CssStyles.VSPACE_NONE);
		return userContactButton;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		if (userCombo != null) {
			userCombo.setReadOnly(readOnly);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (userCombo != null) {
			userCombo.setEnabled(enabled);
		}
	}

	@Override
	public void setValue(UserReferenceDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);
		if (userCombo != null) {
			userCombo.setValue(newFieldValue);
		}
	}

	protected void triggerUserContactPopUpWindow() {
		UserDto userDto = null;
		if (getValue() != null) {
			userDto = FacadeProvider.getUserFacade().getByUuid(getValue().getUuid());
		}

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setMargin(false);
		if (userDto != null) {
			if (userCombo.isReadOnly()) {
				HorizontalLayout reportingUserLayout = new HorizontalLayout();
				Label reportingUserLabel = new Label(I18nProperties.getString(Strings.reportingUser));
				reportingUserLabel.addStyleName(LABEL_BOLD);
				reportingUserLayout.addComponent(reportingUserLabel);

				Label reportingUser = new Label(getValue().getCaption());
				reportingUserLayout.addComponent(reportingUser);

				verticalLayout.addComponent(reportingUserLayout);
			}

			HorizontalLayout telephoneNumberLayout = new HorizontalLayout();
			Label telephoneLabel = new Label(I18nProperties.getString(Strings.promptTelephoneNumber));
			telephoneLabel.addStyleName(LABEL_BOLD);
			telephoneNumberLayout.addComponent(telephoneLabel);

			final String phone = userDto.getPhone();
			if (StringUtils.isBlank(phone)) {
				Label telephoneNumber = new Label();
				telephoneNumber.setValue(I18nProperties.getString(Strings.notSpecified));
				telephoneNumberLayout.addComponent(telephoneNumber);
			} else {
				Link telephoneNumber = new Link(phone, new ExternalResource("tel:" + phone));
				telephoneNumberLayout.addComponent(telephoneNumber);
			}

			verticalLayout.addComponent(telephoneNumberLayout);

			HorizontalLayout emailLayout = new HorizontalLayout();
			Label emailLabel = new Label(I18nProperties.getString(Strings.promptEmail));
			emailLabel.addStyleName(LABEL_BOLD);
			emailLayout.addComponent(emailLabel);
			final String userEmail = userDto.getUserEmail();
			if (StringUtils.isBlank(userEmail)) {
				Label email = new Label();
				email.setValue(I18nProperties.getString(Strings.notSpecified));
				emailLayout.addComponent(email);
			} else {
				Link email = new Link(userEmail, new ExternalResource("mailto:" + userEmail));
				emailLayout.addComponent(email);
			}

			verticalLayout.addComponent(emailLayout);
		} else {
			HorizontalLayout labelLayout = new HorizontalLayout();
			Label noUserMessageLabel = new Label();
			final String noUSerMessage = I18nProperties.getString(Strings.messageNoUserSelected);
			noUserMessageLabel.setValue(noUSerMessage);
			labelLayout.addComponent(noUserMessageLabel);
			verticalLayout.addComponent(labelLayout);
		}

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingContactInformation),
			verticalLayout,
			I18nProperties.getString(Strings.close),
			null,
			640,
			confirmed -> {
				return true;
			});
	}

	@Override
	public Class<? extends UserReferenceDto> getType() {
		return UserReferenceDto.class;
	}
}
