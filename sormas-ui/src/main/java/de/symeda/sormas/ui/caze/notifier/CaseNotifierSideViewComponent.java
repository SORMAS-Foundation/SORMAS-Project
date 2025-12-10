/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.caze.notifier;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

/**
 * UI component displaying the notifier side view for a case.
 * Shows details about who has been notified regarding the case.
 */
@SuppressWarnings("serial")
public class CaseNotifierSideViewComponent extends SideComponent {

	/**
	 * Creates a new notifier side view component for the given case.
	 *
	 * @param caze the case data for which the notifier side view is displayed
	 */
	public CaseNotifierSideViewComponent(CaseDataDto caze) {

		super(I18nProperties.getString(Strings.headingCaseNotifiedBy));
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		if (caze.getNotifier() != null) {
			final var component = ControllerProvider.getCaseNotifierSideViewController().getNotifierComponent(caze);
			addComponent(component);

			final boolean hasReport = ControllerProvider.getCaseNotifierSideViewController().getOldestReport(caze) != null;

			if (hasReport) {
				Button notificationButton = ButtonHelper.createIconButton(Captions.Notifier_notification, VaadinIcons.BOOK, e -> {
					final var oldestReport = ControllerProvider.getCaseNotifierSideViewController().getOldestReport(caze);
					if (oldestReport == null) {
						return;
					}
					final var externalMessage = FacadeProvider.getExternalMessageFacade().getForSurveillanceReport(oldestReport.toReference());
					if (externalMessage == null) {
						return;
					}
					ControllerProvider.getExternalMessageController().showExternalMessage(externalMessage.getUuid(), false, null);
				}, ValoTheme.BUTTON_PRIMARY);

				addCreateButton(notificationButton);
			} else {
				Button editNotifierButton = ButtonHelper.createIconButton(Captions.edit, VaadinIcons.EDIT, e -> {
					ControllerProvider.getCaseNotifierSideViewController().editNotifier(caze, () -> {
						// Refresh the view by navigating back to the same case
						ControllerProvider.getCaseController().navigateToCase(caze.getUuid());
					}, true); // true = allow editing
				}, ValoTheme.BUTTON_PRIMARY);

				addCreateButton(editNotifierButton);
			}
		} else {
			addComponent(new Label(I18nProperties.getCaption(Captions.Notification_noNotification)));
			Button newNotificationButton = ButtonHelper.createIconButton(Captions.Notification_createNotification, VaadinIcons.PHONE, e -> {
				ControllerProvider.getCaseNotifierSideViewController().createNotifier(caze, () -> {
					// Refresh the view by navigating back to the same case
					ControllerProvider.getCaseController().navigateToCase(caze.getUuid());
				});
			}, ValoTheme.BUTTON_PRIMARY);
			addCreateButton(newNotificationButton);
		}
	}

}
