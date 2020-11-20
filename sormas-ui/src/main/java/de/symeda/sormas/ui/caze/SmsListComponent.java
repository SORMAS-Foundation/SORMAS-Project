package de.symeda.sormas.ui.caze;

import java.util.Arrays;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SmsListComponent extends VerticalLayout {

	private SmsList list;
	private Button sendSmsButton;
	private boolean hasPhoneNumber;

	public SmsListComponent(CaseReferenceDto caseRef) {
		hasPhoneNumber = FacadeProvider.getCaseFacade().countCasesWithMissingMessageType(Arrays.asList(caseRef.getUuid()), MessageType.SMS) == 0;
		createSmsListComponent(new SmsList(caseRef, hasPhoneNumber), e -> {
			final SmsComponent smsComponent = new SmsComponent();
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.sendingSms),
				smsComponent,
				I18nProperties.getString(Strings.send),
				I18nProperties.getString(Strings.cancel),
				640,
				confirmationEvent -> {
					if (confirmationEvent.booleanValue()) {
						FacadeProvider.getCaseFacade().sendMessage(Arrays.asList(caseRef.getUuid()), "", smsComponent.getValue(), MessageType.SMS);
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

		Label smsHeader = new Label(I18nProperties.getString(Strings.sms));
		smsHeader.addStyleName(CssStyles.H3);
		componentHeader.addComponent(smsHeader);

		sendSmsButton = new Button(I18nProperties.getCaption(Captions.sendSMS));
		CssStyles.style(sendSmsButton, ValoTheme.BUTTON_PRIMARY);
		sendSmsButton.addClickListener(clickListener);
		componentHeader.addComponent(sendSmsButton);
		componentHeader.setComponentAlignment(sendSmsButton, Alignment.MIDDLE_RIGHT);
		if (!hasPhoneNumber) {
			sendSmsButton.setEnabled(false);
		}
	}
}
