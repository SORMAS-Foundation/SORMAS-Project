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

import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
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

	private final transient CaseNotifierSideViewController controller = ControllerProvider.getCaseNotifierSideViewController();

	/**
	 * Creates a new notifier side view component for the given case.
	 *
	 */
	/**
	 * Creates a new notifier side view component for the given case.
	 *
	 * @param cazeRef
	 *            the case data reference for which the notifier side view is displayed
	 */
	public CaseNotifierSideViewComponent(CaseReferenceDto cazeRef, Consumer<Runnable> actionCallback) {
		super(I18nProperties.getString(Strings.headingCaseNotifiedBy), actionCallback);

		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(cazeRef.getUuid());

		final SurveillanceReportDto oldestDoctorDeclarationReport = controller.getOldestDoctorDeclarationReport(cazeRef);
		final SurveillanceReportDto phoneNotificationReport = controller.getNewestPhoneNotificationReport(cazeRef);

		final boolean hasNotifier = caze.getNotifier() != null;
		final boolean hasPhoneNotification = phoneNotificationReport != null;
		final boolean hasNoNotificationReports = oldestDoctorDeclarationReport == null && !hasPhoneNotification;

		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		if (hasNoNotificationReports || (!hasNotifier && !hasPhoneNotification)) { // if we do not have any relevant notifications or no notifier add the create button
			addComponent(new Label(I18nProperties.getCaption(Captions.Notification_noNotification)));
			// Create a new notification button
			Button newNotificationButton = ButtonHelper.createIconButton(Captions.Notification_createNotification, VaadinIcons.PHONE, e -> {
				actionCallback.accept(() -> controller.createPhoneNotification(cazeRef, () -> {
					// Refresh the view by navigating back to the same case
					ControllerProvider.getCaseController().navigateToCase(cazeRef.getUuid());
				}));
			}, ValoTheme.BUTTON_PRIMARY);
			addCreateButton(newNotificationButton);

		} else { // we have either a phone notification or a doctor declaration report
			if (hasPhoneNotification) { // in case we have a phone notification add the component and a button to allow the user to edit it

				// Add the component
				final var component = controller.getNotifierComponent(caze, phoneNotificationReport);
				addComponent(component);

				// Show edit button when only PHONE_NOTIFICATION exists
				Button editNotifierButton = ButtonHelper.createIconButton(Captions.edit, VaadinIcons.EDIT, e -> {
					controller.editPhoneNotification(caze, () -> {
						// Refresh the view by navigating back to the same case
						ControllerProvider.getCaseController().navigateToCase(caze.getUuid());
					}, true); // true = allow editing
				}, ValoTheme.BUTTON_PRIMARY);

				addCreateButton(editNotifierButton);
			} else {
				if (oldestDoctorDeclarationReport != null) { // in case we have a doctor declaration the component and a button to display the external message (no edit allowed in this case)

					// Add the component
					final var component = controller.getNotifierComponent(caze, oldestDoctorDeclarationReport);
					addComponent(component);

					// Show notification button for DOCTOR reports
					Button notificationButton = ButtonHelper.createIconButton(Captions.Notifier_notification, VaadinIcons.BOOK, e -> {
						final var oldestReport = controller.getOldestDoctorDeclarationReport(caze);
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
				}
			}
		}
	}

}
