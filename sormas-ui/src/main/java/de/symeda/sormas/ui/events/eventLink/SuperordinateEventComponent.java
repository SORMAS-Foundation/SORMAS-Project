/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.events.eventLink;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventHelper;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.ui.AbstractInfoLayout;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

import javax.validation.constraints.NotNull;

public class SuperordinateEventComponent extends VerticalLayout {

	private final EventDto subordinateEvent;
	private final Runnable discardChangesCallback;

	public SuperordinateEventComponent(@NotNull final SormasUI ui, EventDto subordinateEvent, Runnable discardChangesCallback) {
		this.subordinateEvent = subordinateEvent;
		this.discardChangesCallback = discardChangesCallback;
		initialize(ui);
	}

	private void initialize(@NotNull final SormasUI ui) {
		setWidthFull();
		setMargin(false);
		setSpacing(false);

		Label lblHeading = new Label(I18nProperties.getCaption(Captions.eventSuperordinateEvent));
		lblHeading.addStyleName(CssStyles.H3);
		addComponent(lblHeading);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setMargin(false);
		buttonLayout.setSpacing(true);
		buttonLayout.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(buttonLayout, CssStyles.VSPACE_TOP_3);

		if (subordinateEvent.getSuperordinateEvent() != null) {
			addComponent(
				new SuperordinateEventInfoLayout(FacadeProvider.getEventFacade().getEventByUuid(subordinateEvent.getSuperordinateEvent().getUuid())));

			if (ui.getUserProvider().hasUserRight(UserRight.EVENT_EDIT)) {
				Button btnUnlinkEvent = ButtonHelper.createIconButtonWithCaption(
					"unlinkSuperordinateEvent",
					I18nProperties.getCaption(Captions.eventUnlinkEvent),
					VaadinIcons.UNLINK,
					e -> createEventWithConfirmationWindow(
						() -> ControllerProvider.getEventController()
							.removeSuperordinateEvent(
								subordinateEvent,
								true,
								I18nProperties.getString(Strings.messageEventSuperordinateEventUnlinked))),
					ValoTheme.BUTTON_PRIMARY);
				btnUnlinkEvent.setWidthFull();
				buttonLayout.addComponent(btnUnlinkEvent);

				Button btnOpenEvent = ButtonHelper.createIconButtonWithCaption(
					"openSuperordinateEvent",
					I18nProperties.getCaption(Captions.eventOpenSuperordinateEvent),
					VaadinIcons.EYE,
					e -> ControllerProvider.getEventController().navigateToData(subordinateEvent.getSuperordinateEvent().getUuid()),
					ValoTheme.BUTTON_PRIMARY);
				btnOpenEvent.setWidthFull();
				buttonLayout.addComponent(btnOpenEvent);
			}
		} else {
			addComponent(new Label(I18nProperties.getString(Strings.infoNoSuperordinateEvent)));

			if (ui.getUserProvider().hasAllUserRights(UserRight.EVENT_CREATE, UserRight.EVENT_EDIT)) {
				Button btnLinkEvent = ButtonHelper.createIconButtonWithCaption(
					"linkSuperordinateEvent",
					I18nProperties.getCaption(Captions.linkEvent),
					VaadinIcons.PLUS_CIRCLE,
					thisEvent -> {
						long events = FacadeProvider.getEventFacade().count(new EventCriteria());
						if (events > 0) {
							createEventWithConfirmationWindow(
								() -> ControllerProvider.getEventController().selectOrCreateSuperordinateEvent(ui, subordinateEvent.toReference()));
						} else {
							createEventWithConfirmationWindow(
								() -> ControllerProvider.getEventController().createSuperordinateEvent(ui, subordinateEvent.toReference()));
						}
					},
					ValoTheme.BUTTON_PRIMARY);
				buttonLayout.addComponent(btnLinkEvent);
			}
		}

		addComponent(buttonLayout);
	}

	private void createEventWithConfirmationWindow(Runnable callback) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.unsavedChanges_discard),
			new Label(I18nProperties.getString(Strings.confirmationSuperordinateEventDiscardUnsavedChanges)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			480,
			confirmed -> {
				if (confirmed) {
					discardChangesCallback.run();
					callback.run();
				}
			});
	}

	private class SuperordinateEventInfoLayout extends AbstractInfoLayout<EventDto> {

		private static final long serialVersionUID = 695237049227590809L;

		private final EventDto superordinateEvent;

		public SuperordinateEventInfoLayout(EventDto superordinateEvent) {
			super(EventDto.class, UiFieldAccessCheckers.getDefault(superordinateEvent.isPseudonymized()));
			this.superordinateEvent = superordinateEvent;
			setSpacing(true);
			setMargin(false);
			setWidthFull();
			initialize();
		}

		private void initialize() {
			VerticalLayout leftColumnLayout = new VerticalLayout();
			leftColumnLayout.setMargin(false);
			leftColumnLayout.setSpacing(true);
			{
				final Label uuidLabel = addDescLabel(
					leftColumnLayout,
					EventDto.UUID,
					DataHelper.getShortUuid(superordinateEvent.getUuid()),
					I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.UUID));
				uuidLabel.setDescription(subordinateEvent.getUuid());

				addDescLabel(
					leftColumnLayout,
					EventDto.EVENT_TITLE,
					superordinateEvent.getEventTitle(),
					I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.EVENT_TITLE));

				addDescLabel(
					leftColumnLayout,
					EventDto.START_DATE,
					EventHelper.buildEventDateString(superordinateEvent.getStartDate(), superordinateEvent.getEndDate()),
					I18nProperties.getCaption(Captions.singleDayEventDate));
			}
			addComponent(leftColumnLayout);

			VerticalLayout rightColumnLayout = new VerticalLayout();
			rightColumnLayout.setMargin(false);
			rightColumnLayout.setSpacing(true);
			{
				addDescLabel(
					rightColumnLayout,
					EventDto.REPORT_DATE_TIME,
					DateFormatHelper.formatDate(superordinateEvent.getReportDateTime()),
					I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.REPORT_DATE_TIME));

				addDescLabel(
					rightColumnLayout,
					EventDto.EVENT_STATUS,
					superordinateEvent.getEventStatus().toString(),
					I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.EVENT_STATUS));
			}
			addComponent(rightColumnLayout);
		}
	}

}
