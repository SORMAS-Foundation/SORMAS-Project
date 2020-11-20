package de.symeda.sormas.ui.caze;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.messaging.ManualMessageLogDto;
import de.symeda.sormas.ui.utils.CssStyles;

public class SmsListEntry extends HorizontalLayout {

	private final ManualMessageLogDto manualMessageLogDto;

	public SmsListEntry(ManualMessageLogDto manualMessageLogDto) {
		this.manualMessageLogDto = manualMessageLogDto;

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
			Label sendingInfo = new Label(
				I18nProperties.getCaption(Captions.sentBy) + ": " + manualMessageLogDto.getSendingUser().getCaption() + " "
					+ manualMessageLogDto.getSentDate());
			topLeftLayout.addComponent(sendingInfo);
		}
		topLayout.addComponent(topLeftLayout);
	}
}
