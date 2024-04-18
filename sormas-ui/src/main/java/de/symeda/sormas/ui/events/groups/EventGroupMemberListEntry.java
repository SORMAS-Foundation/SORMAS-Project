/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.ui.events.groups;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.HtmlHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ClickableLabel;
import de.symeda.sormas.ui.utils.CssStyles;

public class EventGroupMemberListEntry extends HorizontalLayout {

	private final EventIndexDto event;
	private Button navigateButton;
	private Button unlinkEventButton;

	public EventGroupMemberListEntry(EventIndexDto event) {
		this.event = event;

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

			Label eventTitleLabel = new Label(DataHelper.toStringNullable(event.getEventTitle()));
			CssStyles.style(eventTitleLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			eventTitleLabel.setWidth(100, Unit.PERCENTAGE);

			topLeftLayout.addComponent(eventTitleLabel);

			String hyperlinkedUuid = HtmlHelper.buildHyperlinkTitle(event.getUuid(), DataHelper.getShortUuid(event.getUuid()));
			ClickableLabel uuidValue = new ClickableLabel(hyperlinkedUuid, ContentMode.HTML);
			uuidValue.addLayoutClickListener(e -> {
				ControllerProvider.getEventController().navigateToData(event.getUuid());
			});

			Label uuidLabel = new Label(I18nProperties.getCaption(Captions.Event_uuid) + ":");
			CssStyles.style(uuidLabel, CssStyles.HSPACE_RIGHT_4);

			HorizontalLayout labelLayout = new HorizontalLayout();
			labelLayout.setSpacing(false);
			labelLayout.addComponent(uuidLabel);
			labelLayout.addComponent(uuidValue);
			topLeftLayout.addComponent(labelLayout);

			Label locationLabel = new Label(DataHelper.toStringNullable(event.getEventLocation()));
			locationLabel.setWidth(100, Unit.PERCENTAGE);
			topLeftLayout.addComponent(locationLabel);

			Language userLanguage = I18nProperties.getUserLanguage();
			String eventDate;
			if (event.getEndDate() == null) {
				eventDate = DateHelper.formatLocalDate(event.getStartDate(), userLanguage);
			} else {
				eventDate = String.format(
					"%s - %s",
					DateHelper.formatLocalDate(event.getStartDate(), userLanguage),
					DateHelper.formatLocalDate(event.getEndDate(), userLanguage));
			}
			Label eventDateLabel = new Label(I18nProperties.getCaption(Captions.singleDayEventDate) + ": " + eventDate);

			topLeftLayout.addComponent(eventDateLabel);
		}
		topLayout.addComponent(topLeftLayout);
	}

	public void addNavigateToEventButton(int rowIndex, boolean isEditable, Button.ClickListener editClickListener) {
		if (navigateButton == null) {
			navigateButton = ButtonHelper.createIconButtonWithCaption(
				"add-event-" + rowIndex,
				null,
				isEditable ? VaadinIcons.PENCIL : VaadinIcons.EYE,
				null,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);
			navigateButton.setDescription(I18nProperties.getCaption(Captions.eventEditEvent));

			addComponent(navigateButton);
			setComponentAlignment(navigateButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(navigateButton, 0);
		}

		navigateButton.addClickListener(editClickListener);
	}

	public void addUnlinkEventButton(int rowIndex, Button.ClickListener unlinkEventClickListener) {
		if (unlinkEventButton == null) {
			unlinkEventButton = ButtonHelper.createIconButtonWithCaption(
				"unlink-event-" + rowIndex,
				null,
				VaadinIcons.UNLINK,
				null,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);
			unlinkEventButton.setDescription(I18nProperties.getCaption(Captions.eventUnlinkEvent));

			addComponent(unlinkEventButton);
			setComponentAlignment(unlinkEventButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(unlinkEventButton, 0);
		}

		unlinkEventButton.addClickListener(unlinkEventClickListener);
	}

	public EventIndexDto getEvent() {
		return event;
	}
}
