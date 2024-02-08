package de.symeda.sormas.ui.utils;

import static de.symeda.sormas.ui.utils.CssStyles.INACCESSIBLE_LABEL;
import static de.symeda.sormas.ui.utils.CssStyles.LABEL_BOLD;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class UserField extends CustomField<UserReferenceDto> {

	private ComboBoxWithPlaceholder userCombo;
	private boolean readOnly;
	private boolean enabled;
	private List<UserReferenceDto> items = new ArrayList<>();
	private boolean parentPseudonymizedFlag;
	private Supplier<Boolean> parentPseudonymizedSupplier;

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
		parentPseudonymizedFlag = parentPseudonymizedSupplier.get();
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

		addValueChangeListener(c -> {
			userCombo.setValue(c.getProperty().getValue());
			parentPseudonymizedFlag = parentPseudonymizedSupplier.get();
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
		if (userCombo.isReadOnly()) {
			if (userDto == null) {
				verticalLayout.addComponent(createFieldLayout(I18nProperties.getCaption(Captions.User_userName), null, null, true));
				verticalLayout.addComponent(createFieldLayout(I18nProperties.getCaption(Captions.User_phone), null, LinkType.PHONE, true));
				verticalLayout.addComponent(createFieldLayout(I18nProperties.getCaption(Captions.User_userEmail), null, LinkType.EMAIL, true));
			} else {
				verticalLayout.addComponent(createFieldLayout(I18nProperties.getCaption(Captions.User_userName), userDto.getUserName(), null, false));
				verticalLayout
					.addComponent(createFieldLayout(I18nProperties.getCaption(Captions.User_phone), userDto.getPhone(), LinkType.PHONE, false));
				verticalLayout.addComponent(
					createFieldLayout(I18nProperties.getCaption(Captions.User_userEmail), userDto.getUserEmail(), LinkType.EMAIL, false));
			}
		} else {
			if (userDto == null && !parentPseudonymizedFlag) {
				HorizontalLayout labelLayout = new HorizontalLayout();
				Label noUserMessageLabel = new Label();
				final String noUserMessage;
				noUserMessage = I18nProperties.getString(Strings.messageNoUserSelected);
				noUserMessageLabel.setValue(noUserMessage);
				labelLayout.addComponent(noUserMessageLabel);
				verticalLayout.addComponent(labelLayout);
			} else {
				verticalLayout.addComponent(
					createFieldLayout(
						I18nProperties.getCaption(Captions.User_phone),
						userDto != null ? userDto.getPhone() : null,
						LinkType.PHONE,
						parentPseudonymizedFlag));
				verticalLayout.addComponent(
					createFieldLayout(
						I18nProperties.getCaption(Captions.User_userEmail),
						userDto != null ? userDto.getUserEmail() : null,
						LinkType.EMAIL,
						parentPseudonymizedFlag));
			}
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

	private static HorizontalLayout createFieldLayout(String fieldLabelCaption, String fieldValue, LinkType linkType, boolean inaccessible) {
		HorizontalLayout fieldLayout = new HorizontalLayout();
		Label fieldLabel = new Label(fieldLabelCaption + ":");
		fieldLabel.addStyleName(LABEL_BOLD);
		fieldLayout.addComponent(fieldLabel);

		if (inaccessible) {
			Label fieldValueLabel = new Label(fieldValue != null ? fieldValue : I18nProperties.getCaption(Captions.inaccessibleValue));
			fieldValueLabel.addStyleName(INACCESSIBLE_LABEL);
			fieldLayout.addComponent(fieldValueLabel);
		} else {
			if (fieldValue == null || fieldValue.isEmpty()) {
				Label valueLabel = new Label(I18nProperties.getString(Strings.notSpecified));
				fieldLayout.addComponent(valueLabel);
			} else {
				if (linkType != null) {
					Link fieldLink = new Link(fieldValue, new ExternalResource(linkType.getLinkType() + fieldValue));
					fieldLayout.addComponent(fieldLink);
				} else {
					Label valueLabel = new Label(fieldValue);
					fieldLayout.addComponent(valueLabel);
				}
			}
		}
		return fieldLayout;
	}

	public void setParentPseudonymizedSupplier(Supplier<Boolean> parentPseudonymizedSupplier) {
		this.parentPseudonymizedSupplier = parentPseudonymizedSupplier;
	}

	private enum LinkType {

		PHONE("tel:"),
		EMAIL("mailto:");

		private final String linkType;

		LinkType(String linkType) {
			this.linkType = linkType;
		}

		public String getLinkType() {
			return linkType;
		}
	}

	@Override
	public Class<? extends UserReferenceDto> getType() {
		return UserReferenceDto.class;
	}

}
