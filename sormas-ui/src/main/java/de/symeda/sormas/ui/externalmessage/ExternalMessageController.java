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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.naming.NamingException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
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

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.surveillancereport.ReportingType;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageIndexDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageResult;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.flow.ProcessingResultStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.externalmessage.labmessage.LabMessageProcessingFlow;
import de.symeda.sormas.ui.externalmessage.labmessage.LabMessageSlider;
import de.symeda.sormas.ui.externalmessage.labmessage.RelatedLabMessageHandler;
import de.symeda.sormas.ui.externalmessage.physiciansreport.PhysiciansReportProcessingFlow;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DeleteRestoreHandlers;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ExternalMessageController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@NotNull
	private static ExternalMessageProcessingFacade getExternalMessageProcessingFacade() {
		return new ExternalMessageProcessingFacade(
			FacadeProvider.getExternalMessageFacade(),
			FacadeProvider.getConfigFacade(),
			FacadeProvider.getFeatureConfigurationFacade(),
			FacadeProvider.getCaseFacade(),
			FacadeProvider.getContactFacade(),
			FacadeProvider.getEventFacade(),
			FacadeProvider.getEventParticipantFacade(),
			FacadeProvider.getSampleFacade(),
			FacadeProvider.getPathogenTestFacade(),
			FacadeProvider.getFacilityFacade(),
			FacadeProvider.getCustomizableEnumFacade(),
			FacadeProvider.getCountryFacade(),
			FacadeProvider.getSurveillanceReportFacade()) {

			@Override
			public boolean hasAllUserRights(UserRight... userRights) {
				return UserProvider.getCurrent().hasAllUserRights(userRights);
			}
		};
	}

	public void showLabMessagesSlider(List<ExternalMessageDto> labMessages) {
		new LabMessageSlider(labMessages);
	}

	public void showExternalMessage(String messageUuid, boolean withActions, Runnable onFormActionPerformed) {

		ExternalMessageDto newDto = FacadeProvider.getExternalMessageFacade().getByUuid(messageUuid);
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

	public void processLabMessage(String labMessageUuid) {
		ExternalMessageDto labMessage = FacadeProvider.getExternalMessageFacade().getByUuid(labMessageUuid);
		ExternalMessageProcessingFacade processingFacade = getExternalMessageProcessingFacade();
		ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, processingFacade);
		RelatedLabMessageHandler relatedLabMessageHandler = new RelatedLabMessageHandler(UserProvider.getCurrent().getUser(), mapper);
		LabMessageProcessingFlow flow = new LabMessageProcessingFlow(labMessage, mapper, processingFacade, relatedLabMessageHandler);

		flow.run().handle((BiFunction<? super ProcessingResult<ExternalMessageProcessingResult>, Throwable, Void>) (result, exception) -> {
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
				SormasUI.get().getNavigator().navigateTo(ExternalMessagesView.VIEW_NAME);
			}

			return null;
		});
	}

	public void processPhysiciansReport(String uuid) {
		ExternalMessageDto physicianReport = FacadeProvider.getExternalMessageFacade().getByUuid(uuid);
		ExternalMessageProcessingFacade processingFacade = getExternalMessageProcessingFacade();
		ExternalMessageMapper mapper = new ExternalMessageMapper(physicianReport, processingFacade);
		PhysiciansReportProcessingFlow flow = new PhysiciansReportProcessingFlow(mapper, processingFacade);

		flow.run(physicianReport).handle((BiFunction<? super ProcessingResult<CaseDataDto>, Throwable, Void>) (result, exception) -> {
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
				CaseReferenceDto caseReferenceDto = result.getData().toReference();
				SurveillanceReportDto surveillanceReport = createSurveillanceReport(physicianReport, caseReferenceDto);
				FacadeProvider.getSurveillanceReportFacade().save(surveillanceReport);
				markExternalMessageAsProcessed(physicianReport, surveillanceReport);
				SormasUI.get().getNavigator().navigateTo(ExternalMessagesView.VIEW_NAME);
			}

			return null;
		});
	}
	public void markExternalMessageAsProcessed(ExternalMessageDto externalMessage, SurveillanceReportDto surveillanceReport) {
		externalMessage.setSurveillanceReport(surveillanceReport.toReference());
		externalMessage.setStatus(ExternalMessageStatus.PROCESSED);
		FacadeProvider.getExternalMessageFacade().save(externalMessage);
	}

	protected SurveillanceReportDto createSurveillanceReport(ExternalMessageDto externalMessage, CaseReferenceDto caze) {
		SurveillanceReportDto surveillanceReport = SurveillanceReportDto.build(caze, FacadeProvider.getUserFacade().getCurrentUserAsReference());
		setSurvReportFacility(surveillanceReport, externalMessage, caze);
		surveillanceReport.setReportDate(externalMessage.getMessageDateTime());
		surveillanceReport.setExternalId(externalMessage.getReportMessageId());
		setSurvReportingType(surveillanceReport, externalMessage);
		return surveillanceReport;
	}

	private void setSurvReportFacility(SurveillanceReportDto surveillanceReport, ExternalMessageDto externalMessage, CaseReferenceDto caze) {
		FacilityReferenceDto reporterReference = getExternalMessageProcessingFacade().getFacilityReference(externalMessage.getReporterExternalIds());
		FacilityDto reporter;
		if (reporterReference != null) {
			reporter = FacadeProvider.getFacilityFacade().getByUuid(reporterReference.getUuid());
			surveillanceReport.setFacility(reporterReference);
			if (FacilityDto.OTHER_FACILITY_UUID.equals(reporter.getUuid())) {
				surveillanceReport.setFacilityDetails(I18nProperties.getCaption(Captions.unknown));
			}
			surveillanceReport.setFacilityDistrict(reporter.getDistrict());
			surveillanceReport.setFacilityRegion(reporter.getRegion());
			surveillanceReport.setFacilityType(reporter.getType());
		} else {
			reporter = FacadeProvider.getFacilityFacade().getByUuid(FacilityDto.OTHER_FACILITY_UUID);
			surveillanceReport.setFacility(reporter != null ? reporter.toReference() : null);
			String reporterName = externalMessage.getReporterName();
			if (StringUtils.isNotBlank(reporterName)) {
				surveillanceReport.setFacilityDetails(reporterName);
			} else {
				surveillanceReport.setFacilityDetails(I18nProperties.getCaption(Captions.unknown));
			}
			DataHelper.Pair<RegionReferenceDto, DistrictReferenceDto> regionAndDistrict =
				FacadeProvider.getCaseFacade().getRegionAndDistrictRefsOf(caze);
			surveillanceReport.setFacilityRegion(regionAndDistrict.getElement0());
			surveillanceReport.setFacilityDistrict(regionAndDistrict.getElement1());
			if (ExternalMessageType.LAB_MESSAGE.equals(externalMessage.getType())) {
				surveillanceReport.setFacilityType(FacilityType.LABORATORY);
			} else if (ExternalMessageType.PHYSICIANS_REPORT.equals(externalMessage.getType())) {
				surveillanceReport.setFacilityType(FacilityType.HOSPITAL);
			}
		}
	}

	protected void setSurvReportingType(SurveillanceReportDto surveillanceReport, ExternalMessageDto externalMessage) {
		if (ExternalMessageType.LAB_MESSAGE.equals(externalMessage.getType())) {
			surveillanceReport.setReportingType(ReportingType.LABORATORY);
		} else if (ExternalMessageType.PHYSICIANS_REPORT.equals(externalMessage.getType())) {
			surveillanceReport.setReportingType(ReportingType.DOCTOR);
		} else {
			throw new UnsupportedOperationException(
				String.format("There is no reporting type defined for this type of external message: %s", externalMessage.getType()));
		}
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

	public void deleteAllSelectedItems(
		Collection<ExternalMessageIndexDto> selectedRows,
		ExternalMessageGrid externalMessageGrid,
		Runnable noEntriesRemainingCallback) {

		ControllerProvider.getPermanentDeleteController()
			.deleteAllSelectedItems(
				selectedRows,
				DeleteRestoreHandlers.forExternalMessage(),
				isEligibleForDeletion(selectedRows),
				bulkOperationCallback(externalMessageGrid, noEntriesRemainingCallback, null));

	}

	public boolean isEligibleForDeletion(Collection<ExternalMessageIndexDto> selectedRows) {
		return !selectedRows.stream().anyMatch(m -> m.getStatus() == ExternalMessageStatus.PROCESSED);
	}

	private HorizontalLayout getExternalMessageButtonsPanel(ExternalMessageDto externalMessage, Runnable callback) {
		HorizontalLayout buttonsPanel = new HorizontalLayout();
		buttonsPanel.setMargin(false);
		buttonsPanel.setSpacing(true);

		if (UserProvider.getCurrent().hasUserRight(UserRight.EXTERNAL_MESSAGE_DELETE)) {
			Button deleteButton = ButtonHelper.createButton(
				Captions.actionDelete,
				I18nProperties.getCaption(Captions.actionDelete),
				e -> VaadinUiUtil.showDeleteConfirmationWindow(
					String.format(I18nProperties.getString(Strings.confirmationDeleteEntity), I18nProperties.getCaption(Captions.ExternalMessage)),
					() -> {
						if (FacadeProvider.getExternalMessageFacade().isProcessed(externalMessage.getUuid())) {
							showAlreadyProcessedPopup(null, false);
						} else {
							FacadeProvider.getExternalMessageFacade().delete(externalMessage.getUuid());
							callback.run();
						}
					}),
				ValoTheme.BUTTON_DANGER,
				CssStyles.BUTTON_BORDER_NEUTRAL);

			buttonsPanel.addComponent(deleteButton);
		}

		if (!FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
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
		}

		if (FacadeProvider.getSormasToSormasFacade().isSharingExternalMessagesEnabledForUser()) {
			Button shareButton = ButtonHelper.createIconButton(
				Captions.sormasToSormasSendLabMessage,
				VaadinIcons.SHARE,
				e -> ControllerProvider.getSormasToSormasController().shareExternalMessage(externalMessage, callback));

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

	private Consumer<List<ExternalMessageIndexDto>> bulkOperationCallback(
		ExternalMessageGrid externalMessageGrid,
		Runnable noEntriesRemainingCallback,
		Window popupWindow) {
		return remainingExternalMessages -> {
			if (popupWindow != null) {
				popupWindow.close();
			}

			externalMessageGrid.reload();
			if (CollectionUtils.isNotEmpty(remainingExternalMessages)) {
				externalMessageGrid.asMultiSelect().selectItems(remainingExternalMessages.toArray(new ExternalMessageIndexDto[0]));
			} else {
				noEntriesRemainingCallback.run();
			}
		};
	}
}
