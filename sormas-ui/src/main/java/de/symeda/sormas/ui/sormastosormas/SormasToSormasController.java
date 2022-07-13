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

package de.symeda.sormas.ui.sormastosormas;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.shareinfo.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.shareinfo.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasShareRequestIndexDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SormasToSormasController {

	public SormasToSormasController() {
	}

	public void registerViews(Navigator navigator) {
		navigator.addView(ShareRequestsView.VIEW_NAME, ShareRequestsView.class);
	}

	public void shareCaseFromDetailsPage(CaseDataDto caze) {
		List<SormasToSormasShareInfoDto> currentShares = FacadeProvider.getSormasToSormasShareInfoFacade()
			.getIndexList(new SormasToSormasShareInfoCriteria().caze(caze.toReference()), null, null);
		shareToSormasFromDetailPage(
			options -> FacadeProvider.getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options),
			SormasToSormasOptionsForm.forCase(caze, currentShares));
	}

	public void shareSelectedCases(Collection<? extends CaseIndexDto> selectedRows, Runnable callback) {
		handleShareWithOptions(
			options -> FacadeProvider.getSormasToSormasCaseFacade()
				.share(selectedRows.stream().map(CaseIndexDto::getUuid).collect(Collectors.toList()), options),
			callback,
			SormasToSormasOptionsForm.forCase(null, null),
			new SormasToSormasOptionsDto());
	}

	public void shareContactFromDetailsPage(ContactDto contact) {
		if (contact.getCaze() == null) {
			VaadinUiUtil.showSimplePopupWindow(
				I18nProperties.getString(Strings.headingSormasToSormasCantShareContactWithoutCase),
				I18nProperties.getString(Strings.errorSormasToSormasShareContactWithoutCase));
			return;
		}

		List<SormasToSormasShareInfoDto> currentShares = FacadeProvider.getSormasToSormasShareInfoFacade()
			.getIndexList(new SormasToSormasShareInfoCriteria().contact(contact.toReference()), null, null);
		shareToSormasFromDetailPage(
			options -> FacadeProvider.getSormasToSormasContactFacade().share(Collections.singletonList(contact.getUuid()), options),
			SormasToSormasOptionsForm.forContact(contact, currentShares));
	}

	public void shareSelectedContacts(Collection<? extends ContactIndexDto> selectedRows, Runnable callback) {
		handleShareWithOptions(
			options -> FacadeProvider.getSormasToSormasContactFacade()
				.share(selectedRows.stream().map(ContactIndexDto::getUuid).collect(Collectors.toList()), options),
			callback,
			SormasToSormasOptionsForm.forContact(null, null),
			new SormasToSormasOptionsDto());
	}

	public void shareEventFromDetailsPage(EventDto event) {
		List<SormasToSormasShareInfoDto> currentShares = FacadeProvider.getSormasToSormasShareInfoFacade()
			.getIndexList(new SormasToSormasShareInfoCriteria().event(event.toReference()), null, null);

		shareToSormasFromDetailPage(
			options -> FacadeProvider.getSormasToSormasEventFacade().share(Collections.singletonList(event.getUuid()), options),
			SormasToSormasOptionsForm.forEvent(event, currentShares));
	}

	public void shareLabMessage(ExternalMessageDto labMessage, Runnable callback) {
		handleShareWithOptions(
			options -> FacadeProvider.getSormasToSormasLabMessageFacade()
				.sendExternalMessages(Collections.singletonList(labMessage.getUuid()), options),
			callback,
			SormasToSormasOptionsForm.withoutOptions(),
			new SormasToSormasOptionsDto());
	}

	public void rejectShareRequest(SormasToSormasShareRequestIndexDto request, Runnable callback) {

		TextArea commentField = new TextArea(I18nProperties.getCaption(Captions.SormasToSormasOptions_comment));
		commentField.setWidthFull();
		VerticalLayout popupContent =
			new VerticalLayout(new Label(I18nProperties.getString(Strings.confirmationRejectSormasToSormasShareRequest)), commentField);

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingRejectSormasToSormasShareRequest),
			popupContent,
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			confirmed -> {
				if (confirmed) {
					handleSormasToSormasRequest(
						() -> FacadeProvider.getSormasToSormasFacade()
							.rejectRequest(request.getDataType(), request.getUuid(), commentField.getValue()),
						callback);
				}
			});
	}

	public void acceptShareRequest(SormasToSormasShareRequestIndexDto request, Runnable callback) {
		boolean hasErrors = false;

		if (request.getDataType() == ShareRequestDataType.CONTACT) {
			SormasToSormasShareRequestDto shareRequest =
				FacadeProvider.getSormasToSormasShareRequestFacade().getShareRequestByUuid(request.getUuid());

			List<SormasToSormasContactPreview> contacts = shareRequest.getContacts();
			for (int i = 0, contactsSize = contacts.size(); i < contactsSize && !hasErrors; i++) {
				SormasToSormasContactPreview c = contacts.get(i);

				if (FacadeProvider.getCaseFacade().exists(c.getCaze().getUuid())) {
					continue;
				}

				List<SormasToSormasShareRequestDto> caseRequests =
					FacadeProvider.getSormasToSormasShareRequestFacade().getShareRequestsForCase(c.getCaze());
				if (caseRequests.isEmpty()) {
					VaadinUiUtil.showSimplePopupWindow(
						I18nProperties.getString(Strings.headingSormasToSormasCanAcceptContactsWithoutCase),
						String
							.format(I18nProperties.getString(Strings.errorSormasToSormasAcceptContactsWithoutCaseShared), DataHelper.getShortUuid(c)),
						ContentMode.HTML);
					hasErrors = true;
				} else if (caseRequests.stream().noneMatch(r -> r.getStatus() == ShareRequestStatus.ACCEPTED)) {
					List<SormasToSormasShareRequestDto> pendingRequests = caseRequests.stream()
						.filter(r -> r.getStatus() == ShareRequestStatus.PENDING)
						.sorted(Comparator.comparing(SormasToSormasShareRequestDto::getChangeDate))
						.collect(Collectors.toList());
					if (pendingRequests.isEmpty()) {
						VaadinUiUtil.showSimplePopupWindow(
							I18nProperties.getString(Strings.headingSormasToSormasCanAcceptContactsWithoutCase),
							String.format(
								I18nProperties.getString(Strings.errorSormasToSormasAcceptContactsWithoutCaseShared),
								DataHelper.getShortUuid(c)),
							ContentMode.HTML);
					} else {
						VaadinUiUtil.showSimplePopupWindow(
							I18nProperties.getString(Strings.headingSormasToSormasCanAcceptContactsWithoutCase),
							String.format(
								I18nProperties.getString(Strings.errorSormasToSormasAcceptContactsBeforeAcceptSourceCase),
								DataHelper.getShortUuid(c),
								DataHelper.getShortUuid(pendingRequests.get(0))),
							ContentMode.HTML);
					}

					hasErrors = true;
				}
			}
		}

		if (!hasErrors) {
			handleSormasToSormasRequest(
				() -> FacadeProvider.getSormasToSormasFacade().acceptShareRequest(request.getDataType(), request.getUuid()),
				callback);
		}
	}

	public void revokeShare(String shareInfoUuid, Runnable callback) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingRevokeSormasToSormasShareRequest),
			new Label(I18nProperties.getString(Strings.confirmationRevokeSormasToSormasShareRequest)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			confirmed -> {
				if (confirmed) {
					handleSormasToSormasRequest(() -> FacadeProvider.getSormasToSormasFacade().revokeShare(shareInfoUuid), callback);
				}
			});
	}

	private void shareToSormasFromDetailPage(HandleShareWithOptions handleShareWithOptions, SormasToSormasOptionsForm optionsForm) {
		handleShareWithOptions(handleShareWithOptions, SormasUI::refreshView, optionsForm, new SormasToSormasOptionsDto());
	}

	private void handleShareWithOptions(
		HandleShareWithOptions handleShareWithOptions,
		Runnable callback,
		SormasToSormasOptionsForm optionsForm,
		SormasToSormasOptionsDto defaultOptions) {
		optionsForm.setValue(defaultOptions);

		CommitDiscardWrapperComponent<SormasToSormasOptionsForm> optionsCommitDiscard =
			new CommitDiscardWrapperComponent<>(optionsForm, optionsForm.getFieldGroup());
		optionsCommitDiscard.getCommitButton().setCaption(I18nProperties.getCaption(Captions.sormasToSormasShare));
		optionsCommitDiscard.setWidth(100, Sizeable.Unit.PERCENTAGE);

		Window optionsPopup = VaadinUiUtil.showPopupWindow(optionsCommitDiscard, I18nProperties.getCaption(Captions.sormasToSormasShare));
		optionsForm.setWidth(600, Sizeable.Unit.PIXELS);

		optionsForm.getTargetCombo().addValueChangeListener(e -> optionsCommitDiscard.getCommitButton().setEnabled(optionsForm.isTargetValid()));

		optionsCommitDiscard.addCommitListener(() -> {
			SormasToSormasOptionsDto options = optionsForm.getValue();

			handleSormasToSormasRequest(() -> handleShareWithOptions.handle(options), () -> {
				callback.run();
				optionsPopup.close();
			});
		});

		optionsCommitDiscard.addDiscardListener(optionsPopup::close);
	}

	private void handleSormasToSormasRequest(SormasToSormasRequest request, Runnable callback) {
		try {
			request.run();
			callback.run();
		} catch (SormasToSormasException ex) {
			if (ex.isWarning()) {
				VaadinUiUtil.showWarningPopup(ex.getMessage());
				callback.run();
			} else {
				Component messageComponent =
					buildShareErrorMessage(ex.getHumanMessage() + " " + I18nProperties.getString(Strings.reloadPageToSeeChanges), ex.getErrors());
				messageComponent.setWidth(100, Sizeable.Unit.PERCENTAGE);
				VaadinUiUtil.showPopupWindowWithWidth(
					new VerticalLayout(messageComponent),
					I18nProperties.getCaption(Captions.sormasToSormasErrorDialogTitle),
					48);
			}
		} catch (SormasToSormasValidationException ex) {
			Component messageComponent = buildShareErrorMessage(ex.getMessage(), ex.getErrors());
			messageComponent.setWidth(100, Sizeable.Unit.PERCENTAGE);
			VaadinUiUtil.showPopupWindowWithWidth(
				new VerticalLayout(messageComponent),
				I18nProperties.getCaption(Captions.sormasToSormasErrorDialogTitle),
				48);
		}
	}

	private Component buildShareErrorMessage(String message, List<ValidationErrors> errors) {
		Label errorMessageLabel = new Label(message, ContentMode.HTML);
		errorMessageLabel.addStyleName(CssStyles.LABEL_WHITE_SPACE_NORMAL);

		if (errors == null || errors.isEmpty()) {
			return errorMessageLabel;
		}

		VerticalLayout[] errorLayouts = errors.stream().map(e -> {
			Label groupLabel = new Label(e.getGroup().getHumanMessage());
			groupLabel.addStyleNames(CssStyles.LABEL_BOLD);

			VerticalLayout groupErrorsLayout = new VerticalLayout(formatSubGroupErrors(e));
			groupErrorsLayout.setMargin(false);
			groupErrorsLayout.setSpacing(false);
			groupErrorsLayout.setStyleName(CssStyles.HSPACE_LEFT_3);
			groupErrorsLayout.setWidth(92, Sizeable.Unit.PERCENTAGE);

			VerticalLayout layout = new VerticalLayout(groupLabel, groupErrorsLayout);
			layout.setMargin(false);
			layout.setSpacing(false);

			return layout;
		}).toArray(VerticalLayout[]::new);

		VerticalLayout errorsLayout = new VerticalLayout(errorMessageLabel);
		errorsLayout.addComponents(errorLayouts);
		errorsLayout.setMargin(false);
		errorsLayout.setSpacing(false);

		return errorsLayout;
	}

	private Component[] formatSubGroupErrors(ValidationErrors errors) {
		return errors.getSubGroups().stream().map(e -> {
			Label groupLabel = new Label(e.getHumanMessage() + ":");
			groupLabel.addStyleName(CssStyles.LABEL_BOLD);
			Label label = new Label(
				String.join(", ", e.getMessages().stream().map(ValidationErrorMessage::getHumanMessage).collect(Collectors.toList()).toString()));
			HorizontalLayout layout = new HorizontalLayout(groupLabel, label);
			label.addStyleName(CssStyles.LABEL_WHITE_SPACE_NORMAL);
			layout.setMargin(false);

			return layout;
		}).toArray(Component[]::new);
	}

	public void showRequestDetails(SormasToSormasShareRequestIndexDto request) {
		SormasToSormasShareRequestDto shareRequest = FacadeProvider.getSormasToSormasShareRequestFacade().getShareRequestByUuid(request.getUuid());
		ShareRequestLayout shareRequestLayout = new ShareRequestLayout(shareRequest);
		shareRequestLayout.setWidth(900, Sizeable.Unit.PIXELS);
		shareRequestLayout.setMargin(true);

		VaadinUiUtil.showPopupWindow(shareRequestLayout, I18nProperties.getString(Strings.headingShareRequestDetails));
	}

	private interface HandleShareWithOptions {

		void handle(SormasToSormasOptionsDto options) throws SormasToSormasException;
	}

	private interface SormasToSormasRequest {

		void run() throws SormasToSormasException, SormasToSormasValidationException;
	}
}
