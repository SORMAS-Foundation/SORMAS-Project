package de.symeda.sormas.ui.caze;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CustomField;

public class SmsComponent extends CustomField<String> {

	private TextArea smsTextArea;

	@Override
	protected Component initContent() {
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing(false);
		mainLayout.setSizeUndefined();
		mainLayout.setWidth(100, Unit.PERCENTAGE);

		smsTextArea = new TextArea();
		smsTextArea.setWidth(100, Unit.PERCENTAGE);
		smsTextArea.setRows(4);
		mainLayout.addComponent(smsTextArea);

		final HorizontalLayout charactersLayout = new HorizontalLayout();
		charactersLayout.setSpacing(false);
		charactersLayout.addComponent(new Label("Characters: "));
		final Label characterLeftLabel = new Label("0");
		charactersLayout.addComponent(characterLeftLabel);
		charactersLayout.addComponent(new Label(" / 160 => Nr. of messages: "));
		final Label nrOfMessagesLabel = new Label("1");
		charactersLayout.addComponent(nrOfMessagesLabel);

		mainLayout.addComponent(charactersLayout);
		mainLayout.setComponentAlignment(charactersLayout, Alignment.BOTTOM_RIGHT);

		smsTextArea.addValueChangeListener(e -> {
			final int nrOfCharacters = e.getValue().length();
			characterLeftLabel.setValue(String.valueOf(nrOfCharacters));
			nrOfMessagesLabel.setValue(String.valueOf(1 + nrOfCharacters / 160));
		});

		return mainLayout;
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
