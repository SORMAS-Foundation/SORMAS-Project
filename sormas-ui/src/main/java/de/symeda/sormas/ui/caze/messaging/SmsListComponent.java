package de.symeda.sormas.ui.caze.messaging;

import java.util.Arrays;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SmsListComponent extends VerticalLayout {

	private SmsList list;
	private Button sendSmsButton;
	private boolean hasPhoneNumber;

	public SmsListComponent(CaseReferenceDto caseRef, PersonReferenceDto personRef) {
		long missingPhoneNumbers = FacadeProvider.getCaseFacade().countCasesWithMissingContactInformation(Arrays.asList(caseRef.getUuid()), MessageType.SMS);
		hasPhoneNumber = missingPhoneNumbers == 0;
		createSmsListComponent(new SmsList(personRef, hasPhoneNumber), e -> {
			final SmsComponent smsComponent = new SmsComponent(missingPhoneNumbers);
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getCaption(Captions.messagesSendingSms),
				smsComponent,
				I18nProperties.getCaption(Captions.actionSend),
				I18nProperties.getCaption(Captions.actionCancel),
				640,
				confirmationEvent -> {
					if (confirmationEvent.booleanValue()) {
						FacadeProvider.getCaseFacade().sendMessage(Arrays.asList(caseRef.getUuid()), "", smsComponent.getValue(), MessageType.SMS);
						Notification.show(null, I18nProperties.getString(Strings.notificationSmsSent), Notification.Type.TRAY_NOTIFICATION);
					}
				});
		});

	}

	private void createSmsListComponent(SmsList smsList, Button.ClickListener clickListener) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		list = smsList;
		addComponent(list);
		list.reload();

		Label smsHeader = new Label(I18nProperties.getCaption(Captions.messagesSms));
		smsHeader.addStyleName(CssStyles.H3);
		componentHeader.addComponent(smsHeader);

		sendSmsButton = new Button(I18nProperties.getCaption(Captions.messagesSendSMS));
		CssStyles.style(sendSmsButton, ValoTheme.BUTTON_PRIMARY);
		sendSmsButton.addClickListener(clickListener);
		componentHeader.addComponent(sendSmsButton);
		componentHeader.setComponentAlignment(sendSmsButton, Alignment.MIDDLE_RIGHT);
		if (!hasPhoneNumber) {
			sendSmsButton.setEnabled(false);
		}
	}
}
