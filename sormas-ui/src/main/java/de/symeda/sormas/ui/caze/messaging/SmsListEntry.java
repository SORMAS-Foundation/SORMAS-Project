package de.symeda.sormas.ui.caze.messaging;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

public class SmsListEntry extends HorizontalLayout {

	public SmsListEntry(ManualMessageLogDto manualMessageLogDto) {

		setMargin(false);
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setMargin(false);
		mainLayout.setSpacing(false);
		addComponent(mainLayout);
		setExpandRatio(mainLayout, 1);

		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setWidth(100, Unit.PERCENTAGE);
		topLayout.setMargin(false);
		topLayout.setSpacing(false);
		mainLayout.addComponent(topLayout);

		VerticalLayout topLeftLayout = new VerticalLayout();
		{
			topLeftLayout.setMargin(false);
			topLeftLayout.setSpacing(false);
			final UserReferenceDto sendingUser = manualMessageLogDto.getSendingUser();
			final Label sendingInfo = new Label(
				I18nProperties.getCaption(Captions.messagesSentBy) + ": " + sendingUser.getFirstName() + " " + sendingUser.getLastName() + " "
					+ DateFormatHelper.formatLocalDateTime(manualMessageLogDto.getSentDate()));
			topLeftLayout.addComponent(sendingInfo);
		}
		topLayout.addComponent(topLeftLayout);
	}
}
