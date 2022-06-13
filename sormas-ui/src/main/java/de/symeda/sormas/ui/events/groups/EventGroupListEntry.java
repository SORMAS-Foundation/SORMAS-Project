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

import de.symeda.sormas.api.event.EventGroupIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.HtmlHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ClickableLabel;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

public class EventGroupListEntry extends SideComponentField {

	private final EventGroupIndexDto eventGroup;
	private Button editButton;
	private Button unlinkEventButton;
	private Button listEventsButton;

	public EventGroupListEntry(EventGroupIndexDto eventGroup) {
		this.eventGroup = eventGroup;

		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setWidth(100, Unit.PERCENTAGE);
		topLayout.setMargin(false);
		topLayout.setSpacing(false);
		addComponentToField(topLayout);

		VerticalLayout topLeftLayout = new VerticalLayout();
		{
			topLeftLayout.setMargin(false);
			topLeftLayout.setSpacing(false);

			Label eventTitleLabel = new Label(DataHelper.toStringNullable(eventGroup.getName()));
			CssStyles.style(eventTitleLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
			eventTitleLabel.setWidth(100, Unit.PERCENTAGE);

			topLeftLayout.addComponent(eventTitleLabel);

			String hyperlinkedUuid = HtmlHelper.buildHyperlinkTitle(eventGroup.getUuid(), DataHelper.getShortUuid(eventGroup.getUuid()));
			ClickableLabel uuidValue = new ClickableLabel(hyperlinkedUuid, ContentMode.HTML);
			uuidValue.addLayoutClickListener(event -> {
				ControllerProvider.getEventGroupController().navigateToData(eventGroup.getUuid());
			});

			Label uuidLabel = new Label(I18nProperties.getCaption(Captions.EventGroup_uuid) + ":", ContentMode.HTML);
			CssStyles.style(uuidLabel, CssStyles.HSPACE_RIGHT_4);

			HorizontalLayout labelLayout = new HorizontalLayout();
			labelLayout.setSpacing(false);
			labelLayout.addComponent(uuidLabel);
			labelLayout.addComponent(uuidValue);

			Label eventCountLabel = new Label(I18nProperties.getCaption(Captions.EventGroup_eventCount) + ": " + eventGroup.getEventCount());

			topLeftLayout.addComponent(labelLayout);
			topLeftLayout.addComponent(eventCountLabel);
		}
		topLayout.addComponent(topLeftLayout);
	}

	public void addEditListener(int rowIndex, Button.ClickListener editClickListener) {
		if (editButton == null) {
			editButton = ButtonHelper.createIconButtonWithCaption(
				"add-event-" + rowIndex,
				null,
				VaadinIcons.PENCIL,
				null,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);
			editButton.setDescription(I18nProperties.getCaption(Captions.eventEditEventGroup));

			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(editButton, 0);
		}

		editButton.addClickListener(editClickListener);
	}

	public void addUnlinkEventListener(int rowIndex, Button.ClickListener unlinkEventClickListener) {
		if (unlinkEventButton == null) {
			unlinkEventButton = ButtonHelper.createIconButtonWithCaption(
				"unlink-event-" + rowIndex,
				null,
				VaadinIcons.UNLINK,
				null,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);
			unlinkEventButton.setDescription(I18nProperties.getCaption(Captions.eventUnlinkEventGroup));

			addComponent(unlinkEventButton);
			setComponentAlignment(unlinkEventButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(unlinkEventButton, 0);
		}

		unlinkEventButton.addClickListener(unlinkEventClickListener);
	}

	public void addListEventsListener(int rowIndex, Button.ClickListener listEventsClickListener) {
		if (listEventsButton == null) {
			listEventsButton = ButtonHelper.createIconButtonWithCaption(
				"list-events-" + rowIndex,
				null,
				VaadinIcons.LIST_UL,
				null,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);
			listEventsButton.setDescription(I18nProperties.getCaption(Captions.eventGroupListEvents));

			addComponent(listEventsButton);
			setComponentAlignment(listEventsButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(listEventsButton, 0);
		}

		listEventsButton.addClickListener(listEventsClickListener);
	}

	public EventGroupIndexDto getEventGroup() {
		return eventGroup;
	}
}
