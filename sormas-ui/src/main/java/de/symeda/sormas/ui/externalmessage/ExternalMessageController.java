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
package de.symeda.sormas.ui.externalmessage;

import static de.symeda.sormas.ui.externalmessage.processing.ExternalMessageProcessingUIHelper.showAlreadyProcessedPopup;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.naming.NamingException;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
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
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageIndexDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageResult;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.externalmessage.labmessage.LabMessageProcessingFlow;
import de.symeda.sormas.ui.externalmessage.labmessage.LabMessageSlider;
import de.symeda.sormas.ui.externalmessage.labmessage.RelatedLabMessageHandler;
import de.symeda.sormas.ui.externalmessage.labmessage.processing.SampleAndPathogenTests;
import de.symeda.sormas.ui.externalmessage.physiciansreport.PhysiciansReportProcessingFlow;
import de.symeda.sormas.ui.externalmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.ui.externalmessage.processing.flow.ProcessingResultStatus;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ExternalMessageController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final RelatedLabMessageHandler relatedLabMessageHandler;

	public ExternalMessageController() {
		relatedLabMessageHandler = new RelatedLabMessageHandler();
	}

	public void showExternalMessage(String labMessageUuid, boolean withActions, Runnable onFormActionPerformed) {

		ExternalMessageDto newDto = FacadeProvider.getExternalMessageFacade().getByUuid(labMessageUuid);
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);

		Window window = VaadinUiUtil.showPopupWindow(layout, I18nProperties.getString(Strings.headingShowExternalMessage));

		ExternalMessageForm form = new ExternalMessageForm();
		form.setWidth(550, Sizeable.Unit.PIXELS);
		layout.addComponent(form);

		if (withActions && newDto.getStatus().isProcessable()) {
			layout.addStyleName("lab-message-processable");
			layout.addComponent(getExternalMessageButtonsPanel(newDto, () -> {
				window.close();
				onFormActionPerformed.run();
			}));
		} else {
			layout.addStyleName("lab-message-not-processable");
		}

		form.setValue(newDto);
	}

	public void showLabMessagesSlider(List<ExternalMessageDto> labMessages) {
		new LabMessageSlider(labMessages);
	}

	public void processLabMessage(String labMessageUuid) {
		ExternalMessageDto labMessage = FacadeProvider.getExternalMessageFacade().getByUuid(labMessageUuid);
		LabMessageProcessingFlow flow = new LabMessageProcessingFlow();

		flow.run(labMessage, relatedLabMessageHandler)
			.handle((BiFunction<? super ProcessingResult<SampleAndPathogenTests>, Throwable, Void>) (result, exception) -> {
				if (exception != null) {
					logger.error("Unexpected exception while processing lab message", exception);

					Notification.show(
						I18nProperties.getString(Strings.errorOccurred, I18nProperties.getString(Strings.errorOccurred)),
						I18nProperties.getString(Strings.errorWasReported),
						Notification.Type.ERROR_MESSAGE);

					return null;
				}

				ProcessingResultStatus status = result.getStatus();

				if (status == ProcessingResultStatus.CANCELED_WITH_CORRECTIONS) {
					showCorrectionsSavedPopup();
				} else if (status == ProcessingResultStatus.DONE) {
					markExternalMessageAsProcessed(labMessage, result.getData().getSample().toReference());
					SormasUI.get().getNavigator().navigateTo(ExternalMessagesView.VIEW_NAME);
				}

				return null;
			});
	}

	public void processPhysiciansReport(String uuid) {
		ExternalMessageDto labMessage = FacadeProvider.getExternalMessageFacade().getByUuid(uuid);
		PhysiciansReportProcessingFlow flow = new PhysiciansReportProcessingFlow();

		flow.run(labMessage).handle((BiFunction<? super ProcessingResult<CaseDataDto>, Throwable, Void>) (result, exception) -> {
			if (exception != null) {
				logger.error("Unexpected exception while processing lab message", exception);

				Notification.show(
					I18nProperties.getString(Strings.errorOccurred, I18nProperties.getString(Strings.errorOccurred)),
					I18nProperties.getString(Strings.errorWasReported),
					Notification.Type.ERROR_MESSAGE);

				return null;
			}

			ProcessingResultStatus status = result.getStatus();

			if (status == ProcessingResultStatus.DONE) {
				markExternalMessageAsProcessed(labMessage, result.getData().toReference());
				SormasUI.get().getNavigator().navigateTo(ExternalMessagesView.VIEW_NAME);
			}

			return null;
		});
	}

	public void markExternalMessageAsProcessed(ExternalMessageDto externalMessage, SampleReferenceDto sample) {
		externalMessage.setSample(sample);
		externalMessage.setStatus(ExternalMessageStatus.PROCESSED);
		FacadeProvider.getExternalMessageFacade().save(externalMessage);
	}

	public void markExternalMessageAsProcessed(ExternalMessageDto externalMessage, CaseReferenceDto caze) {
		externalMessage.setCaze(caze);
		externalMessage.setStatus(ExternalMessageStatus.PROCESSED);
		FacadeProvider.getExternalMessageFacade().save(externalMessage);
	}

	public void assignAllSelectedItems(Collection<ExternalMessageIndexDto> selectedRows, Runnable callback) {
		if (selectedRows.isEmpty()) {
			new Notification(
				I18nProperties.getString(Strings.headingNoExternalMessagesSelected),
				I18nProperties.getString(Strings.messageNoExternalMessagesSelected),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			bulkEditAssignee(selectedRows, callback);
		}
	}

	public void deleteAllSelectedItems(Collection<ExternalMessageIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.isEmpty()) {
			new Notification(
				I18nProperties.getString(Strings.headingNoExternalMessagesSelected),
				I18nProperties.getString(Strings.messageNoExternalMessagesSelected),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else if (selectedRows.stream().anyMatch(m -> m.getStatus() == ExternalMessageStatus.PROCESSED)) {
			new Notification(
				I18nProperties.getString(Strings.headingExternalMessagesDeleteProcessed),
				I18nProperties.getString(Strings.messageExternalMessagesDeleteProcessed),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(
				String.format(I18nProperties.getString(Strings.confirmationDeleteExternalMessages), selectedRows.size()),
				() -> {
					FacadeProvider.getExternalMessageFacade()
						.deleteExternalMessages(selectedRows.stream().map(ExternalMessageIndexDto::getUuid).collect(Collectors.toList()));
					callback.run();
					new Notification(
						I18nProperties.getString(Strings.headingExternalMessagesDeleted),
						I18nProperties.getString(Strings.messageExternalMessagesDeleted),
						Notification.Type.HUMANIZED_MESSAGE,
						false).show(Page.getCurrent());
				});
		}
	}

	private HorizontalLayout getExternalMessageButtonsPanel(ExternalMessageDto externalMessage, Runnable callback) {
		HorizontalLayout buttonsPanel = new HorizontalLayout();
		buttonsPanel.setMargin(false);
		buttonsPanel.setSpacing(true);

		Button deleteButton = ButtonHelper.createButton(
			Captions.actionDelete,
			I18nProperties.getCaption(Captions.actionDelete),
			e -> VaadinUiUtil.showDeleteConfirmationWindow(
				String.format(I18nProperties.getString(Strings.confirmationDeleteEntity), I18nProperties.getCaption(Captions.ExternalMessage)),
				() -> {
					if (FacadeProvider.getExternalMessageFacade().isProcessed(externalMessage.getUuid())) {
						showAlreadyProcessedPopup(null, false);
					} else {
						FacadeProvider.getExternalMessageFacade().deleteExternalMessage(externalMessage.getUuid());
						callback.run();
					}
				}),
			ValoTheme.BUTTON_DANGER,
			CssStyles.BUTTON_BORDER_NEUTRAL);

		buttonsPanel.addComponent(deleteButton);

		Button unclearButton = ButtonHelper.createButton(
			Captions.actionUnclearLabMessage,
			I18nProperties.getCaption(Captions.actionUnclearLabMessage),
			e -> VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmUnclearLabMessage),
				new Label(I18nProperties.getString(Strings.confirmationUnclearExternalMessage)),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				confirmed -> {
					if (BooleanUtils.isTrue(confirmed)) {
						if (FacadeProvider.getExternalMessageFacade().isProcessed(externalMessage.getUuid())) {
							showAlreadyProcessedPopup(null, false);
						} else {
							externalMessage.setStatus(ExternalMessageStatus.UNCLEAR);
							FacadeProvider.getExternalMessageFacade().save(externalMessage);
							callback.run();
						}
					}
				}));

		buttonsPanel.addComponent(unclearButton);

		Button forwardButton = ButtonHelper.createButton(
			Captions.actionManualForwardLabMessage,
			I18nProperties.getCaption(Captions.actionManualForwardLabMessage),
			e -> VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmManuallyForwardedLabMessage),
				new Label(I18nProperties.getString(Strings.confirmationManuallyForwardedExternalMessage)),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				confirmed -> {
					if (BooleanUtils.isTrue(confirmed)) {
						if (FacadeProvider.getExternalMessageFacade().isProcessed(externalMessage.getUuid())) {
							showAlreadyProcessedPopup(null, false);
						} else {
							externalMessage.setStatus(ExternalMessageStatus.FORWARDED);
							FacadeProvider.getExternalMessageFacade().save(externalMessage);
							callback.run();
						}
					}
				}));

		buttonsPanel.addComponent(forwardButton);

		if (FacadeProvider.getSormasToSormasFacade().isSharingExternalMessagesEnabledForUser()) {
			Button shareButton = ButtonHelper.createIconButton(
				Captions.sormasToSormasSendLabMessage,
				VaadinIcons.SHARE,
				e -> ControllerProvider.getSormasToSormasController().shareLabMessage(externalMessage, callback));

			buttonsPanel.addComponent(shareButton);
		}

		return buttonsPanel;
	}

	public Optional<byte[]> convertToPDF(String externalMessageUuid) {

		ExternalMessageDto externalMessageDto = FacadeProvider.getExternalMessageFacade().getByUuid(externalMessageUuid);

		try {
			ExternalMessageResult<byte[]> result = FacadeProvider.getExternalLabResultsFacade().convertToPDF(externalMessageDto);

			if (result.isSuccess()) {
				return Optional.of(result.getValue());
			} else {
				new Notification(
					I18nProperties.getString(Strings.headingExternalMessageDownload),
					result.getError(),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			}

		} catch (NamingException e) {
			new Notification(
				I18nProperties.getString(Strings.headingExternalMessageDownload),
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
		Label infoLabel = new Label(I18nProperties.getValidationError(Validations.externalMessageCorrectionsMade));
		CssStyles.style(infoLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_WHITE_SPACE_NORMAL);
		warningLayout.addComponent(infoLabel);
		popupWindow.addCloseListener(e -> popupWindow.close());
		popupWindow.setWidth(400, Sizeable.Unit.PIXELS);
	}

	public void editAssignee(String labMessageUuid) {

		EditAssigneeComponentContainer components = new EditAssigneeComponentContainer();

		// get fresh data
		ExternalMessageDto externalMessageDto = FacadeProvider.getExternalMessageFacade().getByUuid(labMessageUuid);

		if (externalMessageDto.getAssignee() != null) {
			components.getAssigneeComboBox().setValue(externalMessageDto.getAssignee());
		}

		components.getAssignMeButton()
			.addClickListener(e -> saveAssignee(externalMessageDto, UserProvider.getCurrent().getUserReference(), components.getWindow()));
		components.getWrapperComponent()
			.addCommitListener(
				() -> saveAssignee(externalMessageDto, (UserReferenceDto) components.getAssigneeComboBox().getValue(), components.getWindow()));

		UI.getCurrent().addWindow(components.getWindow());
	}

	private void bulkEditAssignee(Collection<ExternalMessageIndexDto> selectedRows, Runnable callback) {

		EditAssigneeComponentContainer components = new EditAssigneeComponentContainer();

		components.getAssignMeButton().addClickListener(e -> {
			FacadeProvider.getExternalMessageFacade()
				.bulkAssignExternalMessages(
					selectedRows.stream().map(ExternalMessageIndexDto::getUuid).collect(Collectors.toList()),
					UserProvider.getCurrent().getUserReference());
			components.getWindow().close();
			Notification.show(I18nProperties.getString(Strings.messageExternalMessagesAssigned), Notification.Type.HUMANIZED_MESSAGE);
			callback.run();
		});

		components.getWrapperComponent().addCommitListener(() -> {
			FacadeProvider.getExternalMessageFacade()
				.bulkAssignExternalMessages(
					selectedRows.stream().map(ExternalMessageIndexDto::getUuid).collect(Collectors.toList()),
					(UserReferenceDto) components.getAssigneeComboBox().getValue());
			components.getWindow().close();
			Notification.show(I18nProperties.getString(Strings.messageExternalMessagesAssigned), Notification.Type.HUMANIZED_MESSAGE);
			callback.run();
		});

		UI.getCurrent().addWindow(components.getWindow());
	}

	private void saveAssignee(ExternalMessageDto externalMessageDto, UserReferenceDto assignee, Window popupWindow) {
		externalMessageDto.setAssignee(assignee);
		FacadeProvider.getExternalMessageFacade().save(externalMessageDto);
		popupWindow.close();
		SormasUI.refreshView();
	}

	public void registerViews(Navigator navigator) {
		navigator.addView(ExternalMessagesView.VIEW_NAME, ExternalMessagesView.class);
	}
}
