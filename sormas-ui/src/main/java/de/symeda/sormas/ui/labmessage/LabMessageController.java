/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.ui.labmessage;

import de.symeda.sormas.api.sample.SampleReferenceDto;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.labmessage.ExternalMessageResult;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageIndexDto;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.labmessage.processing.flow.ProcessingResultStatus;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

import static de.symeda.sormas.ui.labmessage.processing.LabMessageProcessingUIHelper.showAlreadyProcessedPopup;

public class LabMessageController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final RelatedLabMessageHandler relatedLabMessageHandler;

	public LabMessageController() {
		relatedLabMessageHandler = new RelatedLabMessageHandler();
	}

	public void showLabMessage(String labMessageUuid, boolean withActions, Runnable onFormActionPerformed) {

		LabMessageDto newDto = FacadeProvider.getLabMessageFacade().getByUuid(labMessageUuid);
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);

		Window window = VaadinUiUtil.showPopupWindow(layout, I18nProperties.getString(Strings.headingShowLabMessage));

		LabMessageForm form = new LabMessageForm();
		form.setWidth(550, Sizeable.Unit.PIXELS);
		layout.addComponent(form);

		if (withActions && newDto.getStatus().isProcessable()) {
			layout.addStyleName("lab-message-processable");
			layout.addComponent(getLabMessageButtonsPanel(newDto, () -> {
				window.close();
				onFormActionPerformed.run();
			}));
		} else {
			layout.addStyleName("lab-message-not-processable");
		}

		form.setValue(newDto);
	}

	public void showLabMessagesSlider(List<LabMessageDto> labMessages) {
		new LabMessageSlider(labMessages);
	}

	public void processLabMessage(String labMessageUuid) {
		LabMessageDto labMessage = FacadeProvider.getLabMessageFacade().getByUuid(labMessageUuid);
		LabMessageProcessingFlow flow = new LabMessageProcessingFlow();

		flow.run(labMessage, relatedLabMessageHandler).thenAccept(result -> {
			ProcessingResultStatus status = result.getStatus();

			if (status == ProcessingResultStatus.CANCELED_WITH_CORRECTIONS) {
				showCorrectionsSavedPopup();
			} else if (status == ProcessingResultStatus.DONE) {
				markLabMessageAsProcessed(labMessage, result.getData().getSample().toReference());
				SormasUI.get().getNavigator().navigateTo(LabMessagesView.VIEW_NAME);
			}
		});
	}

	public void markLabMessageAsProcessed(LabMessageDto labMessage, SampleReferenceDto sample) {
		labMessage.setSample(sample);
		labMessage.setStatus(LabMessageStatus.PROCESSED);
		FacadeProvider.getLabMessageFacade().save(labMessage);
	}

	public void assignAllSelectedItems(Collection<LabMessageIndexDto> selectedRows, Runnable callback) {
		if (selectedRows.isEmpty()) {
			new Notification(
				I18nProperties.getString(Strings.headingNoLabMessagesSelected),
				I18nProperties.getString(Strings.messageNoLabMessagesSelected),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			bulkEditAssignee(selectedRows, callback);
		}
	}

	public void deleteAllSelectedItems(Collection<LabMessageIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.isEmpty()) {
			new Notification(
				I18nProperties.getString(Strings.headingNoLabMessagesSelected),
				I18nProperties.getString(Strings.messageNoLabMessagesSelected),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else if (selectedRows.stream().anyMatch(m -> m.getStatus() == LabMessageStatus.PROCESSED)) {
			new Notification(
				I18nProperties.getString(Strings.headingLabMessagesDeleteProcessed),
				I18nProperties.getString(Strings.messageLabMessagesDeleteProcessed),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(
				String.format(I18nProperties.getString(Strings.confirmationDeleteLabMessages), selectedRows.size()),
				() -> {
					FacadeProvider.getLabMessageFacade()
						.deleteLabMessages(selectedRows.stream().map(LabMessageIndexDto::getUuid).collect(Collectors.toList()));
					callback.run();
					new Notification(
						I18nProperties.getString(Strings.headingLabMessagesDeleted),
						I18nProperties.getString(Strings.messageLabMessagesDeleted),
						Notification.Type.HUMANIZED_MESSAGE,
						false).show(Page.getCurrent());
				});
		}
	}

	private HorizontalLayout getLabMessageButtonsPanel(LabMessageDto labMessage, Runnable callback) {
		HorizontalLayout buttonsPanel = new HorizontalLayout();
		buttonsPanel.setMargin(false);
		buttonsPanel.setSpacing(true);

		Button deleteButton = ButtonHelper.createButton(
			Captions.actionDelete,
			I18nProperties.getCaption(Captions.actionDelete),
			(e) -> VaadinUiUtil.showDeleteConfirmationWindow(
				String.format(I18nProperties.getString(Strings.confirmationDeleteEntity), I18nProperties.getCaption(Captions.LabMessage)),
				() -> {
					if (FacadeProvider.getLabMessageFacade().isProcessed(labMessage.getUuid())) {
						showAlreadyProcessedPopup(null, false);
					} else {
						FacadeProvider.getLabMessageFacade().deleteLabMessage(labMessage.getUuid());
						callback.run();
					}
				}),
			ValoTheme.BUTTON_DANGER,
			CssStyles.BUTTON_BORDER_NEUTRAL);

		buttonsPanel.addComponent(deleteButton);

		Button unclearButton = ButtonHelper.createButton(
			Captions.actionUnclearLabMessage,
			I18nProperties.getCaption(Captions.actionUnclearLabMessage),
			(e) -> VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmUnclearLabMessage),
				new Label(I18nProperties.getString(Strings.confirmationUnclearLabMessage)),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				(confirmed) -> {
					if (confirmed) {
						if (FacadeProvider.getLabMessageFacade().isProcessed(labMessage.getUuid())) {
							showAlreadyProcessedPopup(null, false);
						} else {
							labMessage.setStatus(LabMessageStatus.UNCLEAR);
							FacadeProvider.getLabMessageFacade().save(labMessage);
							callback.run();
						}
					}
				}));

		buttonsPanel.addComponent(unclearButton);

		Button forwardButton = ButtonHelper.createButton(
			Captions.actionManualForwardLabMessage,
			I18nProperties.getCaption(Captions.actionManualForwardLabMessage),
			(e) -> VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmManuallyForwardedLabMessage),
				new Label(I18nProperties.getString(Strings.confirmationManuallyForwardedLabMessage)),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				(confirmed) -> {
					if (confirmed) {
						if (FacadeProvider.getLabMessageFacade().isProcessed(labMessage.getUuid())) {
							showAlreadyProcessedPopup(null, false);
						} else {
							labMessage.setStatus(LabMessageStatus.FORWARDED);
							FacadeProvider.getLabMessageFacade().save(labMessage);
							callback.run();
						}
					}
				}));

		buttonsPanel.addComponent(forwardButton);

		if (FacadeProvider.getSormasToSormasFacade().isSharingLabMessagesEnabledForUser()) {
			Button shareButton = ButtonHelper.createIconButton(
				Captions.sormasToSormasSendLabMessage,
				VaadinIcons.SHARE,
				(e) -> ControllerProvider.getSormasToSormasController().shareLabMessage(labMessage, callback));

			buttonsPanel.addComponent(shareButton);
		}

		return buttonsPanel;
	}

	public Optional<byte[]> convertToPDF(String labMessageUuid) {

		LabMessageDto labMessageDto = FacadeProvider.getLabMessageFacade().getByUuid(labMessageUuid);

		try {
			ExternalMessageResult<byte[]> result = FacadeProvider.getExternalLabResultsFacade().convertToPDF(labMessageDto);

			if (result.isSuccess()) {
				return Optional.of(result.getValue());
			} else {
				new Notification(
					I18nProperties.getString(Strings.headingLabMessageDownload),
					result.getError(),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			}

		} catch (NamingException e) {
			new Notification(
				I18nProperties.getString(Strings.headingLabMessageDownload),
				I18nProperties.getString(Strings.messageExternalLabResultsAdapterNotFound),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			logger.error(e.getMessage());
		}
		return Optional.empty();
	}

	private void showCorrectionsSavedPopup() {
		VerticalLayout warningLayout = VaadinUiUtil.createWarningLayout();
		Window popupWindow = VaadinUiUtil.showPopupWindow(warningLayout);
		Label infoLabel = new Label(I18nProperties.getValidationError(Validations.labMessageCorrectionsMade));
		CssStyles.style(infoLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_WHITE_SPACE_NORMAL);
		warningLayout.addComponent(infoLabel);
		popupWindow.addCloseListener(e -> popupWindow.close());
		popupWindow.setWidth(400, Sizeable.Unit.PIXELS);
	}

	public void editAssignee(String labMessageUuid) {

		EditAssigneeComponentContainer components = new EditAssigneeComponentContainer();

		// get fresh data
		LabMessageDto labMessageDto = FacadeProvider.getLabMessageFacade().getByUuid(labMessageUuid);

		if (labMessageDto.getAssignee() != null) {
			components.getAssigneeComboBox().setValue(labMessageDto.getAssignee());
		}

		components.getAssignMeButton()
			.addClickListener(e -> saveAssignee(labMessageDto, UserProvider.getCurrent().getUserReference(), components.getWindow()));
		components.getWrapperComponent()
			.addCommitListener(
				() -> saveAssignee(labMessageDto, (UserReferenceDto) components.getAssigneeComboBox().getValue(), components.getWindow()));

		UI.getCurrent().addWindow(components.getWindow());
	}

	private void bulkEditAssignee(Collection<LabMessageIndexDto> selectedRows, Runnable callback) {

		EditAssigneeComponentContainer components = new EditAssigneeComponentContainer();

		components.getAssignMeButton().addClickListener(e -> {
			FacadeProvider.getLabMessageFacade()
				.bulkAssignLabMessages(
					selectedRows.stream().map(LabMessageIndexDto::getUuid).collect(Collectors.toList()),
					UserProvider.getCurrent().getUserReference());
			components.getWindow().close();
			Notification.show(I18nProperties.getString(Strings.messageLabMessagesAssigned), Notification.Type.HUMANIZED_MESSAGE);
			callback.run();
		});

		components.getWrapperComponent().addCommitListener(() -> {
			FacadeProvider.getLabMessageFacade()
				.bulkAssignLabMessages(
					selectedRows.stream().map(LabMessageIndexDto::getUuid).collect(Collectors.toList()),
					(UserReferenceDto) components.getAssigneeComboBox().getValue());
			components.getWindow().close();
			Notification.show(I18nProperties.getString(Strings.messageLabMessagesAssigned), Notification.Type.HUMANIZED_MESSAGE);
			callback.run();
		});

		UI.getCurrent().addWindow(components.getWindow());
	}

	private void saveAssignee(LabMessageDto labMessageDto, UserReferenceDto assignee, Window popupWindow) {
		labMessageDto.setAssignee(assignee);
		FacadeProvider.getLabMessageFacade().save(labMessageDto);
		popupWindow.close();
		SormasUI.refreshView();
	}
}
