/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.externalmessage.labmessage;

import static de.symeda.sormas.ui.externalmessage.processing.ExternalMessageProcessingUIHelper.showAlreadyProcessedPopup;
import static de.symeda.sormas.ui.externalmessage.processing.ExternalMessageProcessingUIHelper.showEditSampleWindow;
import static de.symeda.sormas.ui.externalmessage.processing.ExternalMessageProcessingUIHelper.showFormWithLabMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import javax.naming.CannotProceedException;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.labmessage.AbstractRelatedLabMessageHandler;
import de.symeda.sormas.api.externalmessage.processing.labmessage.LabMessageProcessingHelper;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.externalmessage.CorrectionPanel;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.samples.PathogenTestForm;
import de.symeda.sormas.ui.samples.humansample.SampleEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Related lab messages handler implemented with vaadin dialogs/components for handling confirmation and object edit/save steps
 */
public class RelatedLabMessageHandler extends AbstractRelatedLabMessageHandler {

	public RelatedLabMessageHandler(UserDto user, ExternalMessageMapper mapper) {
		super(user, mapper);
	}

	@Override
	protected CompletionStage<Boolean> confirmShortcut(boolean hasRelatedLabMessages) {

		CompletableFuture<Boolean> ret = new CompletableFuture<>();

		String message = hasRelatedLabMessages
			? I18nProperties.getString(Strings.messageRelatedSampleAndLabMessagesFound)
			: I18nProperties.getString(Strings.messageRelatedSampleFound);

		Window window = VaadinUiUtil.showChooseOptionPopup(
			I18nProperties.getCaption(Captions.externalMessageRelatedEntriesFound),
			new Label(message, ContentMode.HTML),
			I18nProperties.getCaption(Captions.actionYes),
			I18nProperties.getCaption(Captions.actionNo),
			null,
			ret::complete);

		window.addCloseListener(e -> ret.complete(false));

		return ret;
	}

	@Override
	protected CompletionStage<Boolean> confirmContinueProcessing(ExternalMessageDto labMessage, SampleReferenceDto sample) {

		CompletableFuture<Boolean> ret = new CompletableFuture<>();

		Window window = VaadinUiUtil.createPopupWindow();
		Label label = new Label(I18nProperties.getString(Strings.confirmExternalMessageCorrectionThrough));
		label.addStyleName(CssStyles.LABEL_WHITE_SPACE_NORMAL);
		CommitDiscardWrapperComponent<Label> confirmComponent = new CommitDiscardWrapperComponent<>(label);
		confirmComponent.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionYes));
		confirmComponent.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionNo));

		confirmComponent.addCommitListener(() -> ret.complete(true));
		confirmComponent.addDiscardListener(() -> ret.complete(false));

		showFormWithLabMessage(labMessage, confirmComponent, window, I18nProperties.getString(Strings.headingLabMessageCorrectionThrough), false);

		return ret;
	}

	@Override
	protected void handleShortcut(ExternalMessageDto labMessage, SampleDto sample, RelatedLabMessageHandlerChain chain) {
		// Currently, related entities are only looked at when there is just one sample report in the lab message
		List<PathogenTestDto> newPathogenTests =
			LabMessageProcessingHelper.buildPathogenTests(sample, 0, labMessage, mapper, UserProvider.getCurrent().getUser());
		showEditSampleWindow(sample, true, newPathogenTests, labMessage, mapper, s -> chain.next(true), chain::cancel);
	}

	@Override
	protected CompletionStage<Boolean> confirmCorrectionFlow() {

		return VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingExternalMessageCorrection),
			new Label(I18nProperties.getString(Strings.confirmationExternalMessageCorrection), ContentMode.HTML),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no));
	}

	@Override
	protected void handlePersonCorrection(
		ExternalMessageDto labMessage,
		PersonDto person,
		PersonDto updatedPerson,
		List<String[]> changedFields,
		RelatedLabMessageHandlerChain chain) {

		CorrectionPanel<PersonDto> personCorrectionPanel = new CorrectionPanel<>(
			() -> new PersonEditForm(true, person.isPseudonymized(), person.isInJurisdiction()),
			person,
			updatedPerson,
			Strings.headingPreviousPersonInformation,
			Strings.headingUpdatedPersonInformation,
			changedFields);

		showCorrectionWindow(labMessage, Strings.headingCorrectPerson, personCorrectionPanel, p -> {
			FacadeProvider.getPersonFacade().save(p);
			Notification.show(I18nProperties.getString(Strings.messagePersonSaved), Notification.Type.TRAY_NOTIFICATION);
		}, chain);
	}

	@Override
	protected void handleSampleCorrection(
		ExternalMessageDto labMessage,
		SampleDto sample,
		SampleDto updatedSample,
		List<String[]> changedFields,
		RelatedLabMessageHandlerChain chain) {

		CorrectionPanel<SampleDto> sampleCorrectionPanel = new CorrectionPanel<>(
			() -> new SampleEditForm(
				sample.isPseudonymized(),
				sample.isInJurisdiction(),
				ControllerProvider.getSampleController().getDiseaseOf(sample)),
			sample,
			updatedSample,
			Strings.headingPreviousSampleInformation,
			Strings.headingUpdatedSampleInformation,
			changedFields);

		showCorrectionWindow(labMessage, Strings.headingCorrectSample, sampleCorrectionPanel, s -> {
			FacadeProvider.getSampleFacade().saveSample(s);
			Notification.show(I18nProperties.getString(Strings.messageSampleSaved), Notification.Type.TRAY_NOTIFICATION);
		}, chain);
	}

	@Override
	protected void handlePathogenTestCorrection(
		ExternalMessageDto labMessage,
		PathogenTestDto pathogenTest,
		PathogenTestDto updatedPathogenTest,
		List<String[]> changedFields,
		RelatedLabMessageHandlerChain chain) {

		SampleDto sample = FacadeProvider.getSampleFacade().getSampleByUuid(pathogenTest.getSample().getUuid());
		int caseSampleCount = ControllerProvider.getSampleController().caseSampleCountOf(sample);

		CorrectionPanel<PathogenTestDto> pathogenTestCorrectionPanel = new CorrectionPanel<>(
			() -> new PathogenTestForm(sample, false, caseSampleCount, sample.isPseudonymized(), sample.isInJurisdiction()),
			pathogenTest,
			updatedPathogenTest,
			Strings.headingPreviousPathogenTestInformation,
			Strings.headingUpdatedPathogenTestInformation,
			changedFields);

		showCorrectionWindow(labMessage, Strings.headingCorrectPathogenTest, pathogenTestCorrectionPanel, t -> {
			FacadeProvider.getPathogenTestFacade().savePathogenTest(t);
			Notification.show(I18nProperties.getString(Strings.messagePathogenTestSavedShort), Notification.Type.TRAY_NOTIFICATION);
		}, chain);
	}

	@Override
	protected void handlePathogenTestCreation(
		ExternalMessageDto labMessage,
		TestReportDto testReport,
		SampleDto sample,
		RelatedLabMessageHandlerChain chain) {

		Window window = VaadinUiUtil.createPopupWindow();

		int caseSampleCount = ControllerProvider.getSampleController().caseSampleCountOf(sample);

		CommitDiscardWrapperComponent<PathogenTestForm> pathogenTestCreateComponent =
			ControllerProvider.getPathogenTestController().getPathogenTestCreateComponent(sample, caseSampleCount, (savedPathogenTest) -> {
				chain.next(true);
				window.close();
			}, true);

		pathogenTestCreateComponent.addDiscardListener(() -> {
			if (Boolean.TRUE.equals(FacadeProvider.getExternalMessageFacade().isProcessed(labMessage.getUuid()))) {
				showAlreadyProcessedPopup(null, false);
				pathogenTestCreateComponent.getCommitButton().setEnabled(false);
				pathogenTestCreateComponent.getDiscardButton().setEnabled(false);
			} else {
				chain.next(true);
				window.close();
			}
		});

		Button cancelButton = LabMessageUiHelper.addCancelAndUpdateLabels(pathogenTestCreateComponent, Captions.actionDiscardAndContinue);
		cancelButton.addClickListener(e -> {
			chain.cancel();
			window.close();
		});

		window.addCloseListener(e -> {
			if (!chain.done()) {
				chain.cancel();
			}
		});

		pathogenTestCreateComponent.getWrappedComponent()
			.setValue(LabMessageProcessingHelper.buildPathogenTest(testReport, mapper, sample, UserProvider.getCurrent().getUser()));
		ControllerProvider.getSampleController().setViaLimsFieldChecked(pathogenTestCreateComponent.getWrappedComponent());

		showFormWithLabMessage(
			labMessage,
			pathogenTestCreateComponent,
			window,
			I18nProperties.getString(Strings.headingCreatePathogenTestResult),
			false);

		pathogenTestCreateComponent.setPrimaryCommitListener(() -> {
			if (Boolean.TRUE.equals(FacadeProvider.getExternalMessageFacade().isProcessed(labMessage.getUuid()))) {
				pathogenTestCreateComponent.getCommitButton().setEnabled(false);
				pathogenTestCreateComponent.getDiscardButton().setEnabled(false);
				showAlreadyProcessedPopup(pathogenTestCreateComponent.getWrappedComponent(), false);
				throw new CannotProceedException("The lab message was processed in the meantime");
			}
		});
	}

	private <T> void showCorrectionWindow(
		ExternalMessageDto labMessage,
		String titleTag,
		CorrectionPanel<T> correctionPanel,
		Consumer<T> save,
		RelatedLabMessageHandlerChain chain) {

		Window window = VaadinUiUtil.createPopupWindow();

		correctionPanel.setCancelListener(e -> {
			chain.cancel();
			window.close();
		});
		correctionPanel.setDiscardListener(() -> {
			if (Boolean.TRUE.equals(FacadeProvider.getExternalMessageFacade().isProcessed(labMessage.getUuid()))) {
				showAlreadyProcessedPopup(null, false);
				correctionPanel.disableContinueButtons();
			} else {
				chain.next(false);
				window.close();
			}
		});
		correctionPanel.setCommitListener(updated -> {
			if (Boolean.TRUE.equals(FacadeProvider.getExternalMessageFacade().isProcessed(labMessage.getUuid()))) {
				showAlreadyProcessedPopup(null, false);
				correctionPanel.disableContinueButtons();
			} else {
				save.accept(updated);
				chain.next(true);
				window.close();
			}
		});
		window.addCloseListener(e -> {
			if (!chain.done()) {
				chain.cancel();
			}
		});

		HorizontalLayout toolbar = new HorizontalLayout(
			ButtonHelper.createIconButton(
				null,
				VaadinIcons.EYE,
				e -> ControllerProvider.getExternalMessageController().showExternalMessage(labMessage.getUuid(), false, null)));
		toolbar.setMargin(new MarginInfo(true, true, false, true));

		VerticalLayout content = new VerticalLayout(toolbar, correctionPanel);
		content.setMargin(false);
		content.setSpacing(false);
		content.setExpandRatio(toolbar, 0);
		content.setExpandRatio(correctionPanel, 1);

		content.setSizeFull();

		window.setContent(content);
		window.setSizeFull();
		window.setCaption(I18nProperties.getString(titleTag));

		UI.getCurrent().addWindow(window);
	}
}
