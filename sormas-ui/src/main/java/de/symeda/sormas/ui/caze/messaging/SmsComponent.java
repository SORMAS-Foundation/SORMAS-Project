package de.symeda.sormas.ui.caze.messaging;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.TextFieldWithMaxLengthWrapper;

public class SmsComponent extends CustomField<String> {

	private long missingPhoneNumbers;

	private TextArea smsTextArea;

	public SmsComponent(long missingPhoneNumbers) {
		this.missingPhoneNumbers = missingPhoneNumbers;
	}

	@Override
	protected Component initContent() {
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing(false);
		mainLayout.setMargin(false);
		mainLayout.setSizeUndefined();
		mainLayout.setWidth(100, Unit.PERCENTAGE);

		if (missingPhoneNumbers > 0) {
			mainLayout.addComponent(
				new Label(
					VaadinIcons.INFO_CIRCLE.getHtml() + " "
						+ String.format(I18nProperties.getCaption(Captions.messagesNumberOfMissingPhoneNumbers), missingPhoneNumbers),
					ContentMode.HTML));
			mainLayout.addComponent(new Label());
		}

		mainLayout.addComponent(new Label(I18nProperties.getString(Strings.messageEnterSms)));

		final TextFieldWithMaxLengthWrapper<TextArea> tTextFieldWithMaxLengthWrapper = new TextFieldWithMaxLengthWrapper<>();

		smsTextArea = new TextArea();
		smsTextArea.setWidth(100, Unit.PERCENTAGE);
		smsTextArea.setRows(4);
		mainLayout.addComponent(tTextFieldWithMaxLengthWrapper.wrap(smsTextArea, Captions.messagesCharacters, false));

		final Label numberOfMessagesLabel = new Label(String.format(I18nProperties.getCaption(Captions.messagesNumberOfMessages), 0));
		numberOfMessagesLabel.addStyleNames(CssStyles.ALIGN_RIGHT, CssStyles.FIELD_EXTRA_INFO, CssStyles.LABEL_ITALIC);
		mainLayout.addComponent(numberOfMessagesLabel);

		mainLayout.setComponentAlignment(numberOfMessagesLabel, Alignment.BOTTOM_RIGHT);

		smsTextArea.addTextChangeListener(e -> setNumberOfMessagesLabel(numberOfMessagesLabel, e.getText().length()));
		smsTextArea.addValueChangeListener(e -> setNumberOfMessagesLabel(numberOfMessagesLabel, smsTextArea.getValue().length()));

		return mainLayout;
	}

	private void setNumberOfMessagesLabel(Label numberOfMessagesLabel, int nrOfCharacters) {
		numberOfMessagesLabel.setValue(String.format(I18nProperties.getCaption(Captions.messagesNumberOfMessages), (1 + nrOfCharacters / 160)));
	}

	@Override
	public Class<? extends String> getType() {
		return String.class;
	}

	@Override
	public String getValue() {
		return smsTextArea.getValue();
	}
}
