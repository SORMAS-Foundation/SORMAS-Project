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

package de.symeda.sormas.ui.sormastosormas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.ValidationErrors;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SormasToSormasController {

	public SormasToSormasController() {
	}

	public void shareCaseFromDetailsPage(CaseDataDto caze, SormasToSormasListComponent listComponent) {
		shareToSormasFromDetailPage(
			(options) -> FacadeProvider.getSormasToSormasCaseFacade().shareEntities(Collections.singletonList(caze.getUuid()), options),
			listComponent,
			SormasToSormasOptionsForm.forCase(getCaseExcludedOrganizationIds(caze)));
	}

	public void shareSelectedCases(Collection<? extends CaseIndexDto> selectedRows, Runnable callback) {
		shareHandleShare((options) -> {
			FacadeProvider.getSormasToSormasCaseFacade()
				.shareEntities(selectedRows.stream().map(CaseIndexDto::getUuid).collect(Collectors.toList()), options);
			callback.run();
		}, SormasToSormasOptionsForm.forCase(null), new SormasToSormasOptionsDto());
	}

	public void shareContactFromDetailsPage(ContactDto contact, SormasToSormasListComponent listComponent) {
		shareToSormasFromDetailPage(
			(options) -> FacadeProvider.getSormasToSormasContactFacade().shareEntities(Collections.singletonList(contact.getUuid()), options),
			listComponent,
			SormasToSormasOptionsForm.forContact(getContactExcludedOrganizationIds(contact)));
	}

	public void shareSelectedContacts(Collection<? extends ContactIndexDto> selectedRows, Runnable callback) {
		shareHandleShare((options) -> {
			FacadeProvider.getSormasToSormasContactFacade()
				.shareEntities(selectedRows.stream().map(ContactIndexDto::getUuid).collect(Collectors.toList()), options);
			callback.run();
		}, SormasToSormasOptionsForm.forContact(null), new SormasToSormasOptionsDto());
	}

	public void shareEventFromDetailsPage(EventDto event, SormasToSormasListComponent listComponent) {
		shareToSormasFromDetailPage(
			(options) -> FacadeProvider.getSormasToSormasEventFacade().shareEntities(Collections.singletonList(event.getUuid()), options),
			listComponent,
			SormasToSormasOptionsForm.forEvent(getEventExcludedOrganizationIds(event)));
	}

	public void returnCase(CaseDataDto caze) {
		handleReturn(
			options -> FacadeProvider.getSormasToSormasCaseFacade().returnEntity(caze.getUuid(), options),
			SormasToSormasOptionsForm.forCase(null),
			caze.getSormasToSormasOriginInfo());
	}

	public void syncCase(CaseDataDto caze, SormasToSormasShareInfoDto shareInfo) {
		handleSync(
			options -> FacadeProvider.getSormasToSormasCaseFacade().syncEntity(caze.getUuid(), options),
			SormasToSormasOptionsForm.forCase(null),
			shareInfo);
	}

	public void returnContact(ContactDto contact) {
		handleReturn(
			(options) -> FacadeProvider.getSormasToSormasContactFacade().returnEntity(contact.getUuid(), options),
			SormasToSormasOptionsForm.forContact(null),
			contact.getSormasToSormasOriginInfo());
	}

	public void syncContact(ContactDto contact, SormasToSormasShareInfoDto shareInfo) {
		handleSync(
			options -> FacadeProvider.getSormasToSormasContactFacade().syncEntity(contact.getUuid(), options),
			SormasToSormasOptionsForm.forContact(null),
			shareInfo);
	}

	public void returnEvent(EventDto event) {
		handleReturn(
			(options) -> FacadeProvider.getSormasToSormasEventFacade().returnEntity(event.getUuid(), options),
			SormasToSormasOptionsForm.forEvent(null),
			event.getSormasToSormasOriginInfo());
	}

	public void syncEvent(EventDto event, SormasToSormasShareInfoDto shareInfo) {
		handleSync(
			options -> FacadeProvider.getSormasToSormasEventFacade().syncEntity(event.getUuid(), options),
			SormasToSormasOptionsForm.forEvent(null),
			shareInfo);
	}

	public void shareLabMessage(LabMessageDto labMessage, Runnable callback) {
		shareHandleShare((options) -> {
			FacadeProvider.getSormasToSormasLabMessageFacade().sendLabMessages(Collections.singletonList(labMessage.getUuid()), options);
			callback.run();
		}, new SormasToSormasOptionsForm(false), new SormasToSormasOptionsDto());
	}

	private void shareToSormasFromDetailPage(
		HandleShareWithOptions handleShareWithOptions,
		SormasToSormasListComponent listComponent,
		SormasToSormasOptionsForm optionsForm) {
		shareHandleShare(options -> {
			handleShareWithOptions.handle(options);

			if (options.isHandOverOwnership()) {
				SormasUI.refreshView();
			} else {
				listComponent.reloadList();
			}
		}, optionsForm, new SormasToSormasOptionsDto());
	}

	private void shareHandleShare(
		HandleShareWithOptions handleShareWithOptions,
		SormasToSormasOptionsForm optionsForm,
		SormasToSormasOptionsDto defaultOptions) {
		optionsForm.setValue(defaultOptions);

		CommitDiscardWrapperComponent<SormasToSormasOptionsForm> optionsCommitDiscard =
			new CommitDiscardWrapperComponent<>(optionsForm, optionsForm.getFieldGroup());
		optionsCommitDiscard.getCommitButton().setCaption(I18nProperties.getCaption(Captions.sormasToSormasShare));
		optionsCommitDiscard.setWidth(100, Sizeable.Unit.PERCENTAGE);

		Window optionsPopup = VaadinUiUtil.showPopupWindow(optionsCommitDiscard, I18nProperties.getCaption(Captions.sormasToSormasDialogTitle));

		optionsCommitDiscard.addCommitListener(() -> {
			SormasToSormasOptionsDto options = optionsForm.getValue();

			try {
				handleShareWithOptions.handle(options);
				optionsPopup.close();
			} catch (SormasToSormasException ex) {
				Component messageComponent = buildShareErrorMessage(ex);
				messageComponent.setWidth(100, Sizeable.Unit.PERCENTAGE);
				VaadinUiUtil
					.showPopupWindow(new VerticalLayout(messageComponent), I18nProperties.getCaption(Captions.sormasToSormasErrorDialogTitle));
			}
		});

		optionsCommitDiscard.addDiscardListener(optionsPopup::close);
	}

	private void handleReturn(
		HandleShareWithOptions handleShareWithOptions,
		SormasToSormasOptionsForm optionsForm,
		SormasToSormasOriginInfoDto originInfo) {
		SormasToSormasOptionsDto defaultOptions = new SormasToSormasOptionsDto();
		defaultOptions.setHandOverOwnership(true);
		defaultOptions.setOrganization(new ServerAccessDataReferenceDto(originInfo.getOrganizationId()));

		optionsForm.disableOrganizationAndOwnership();

		shareHandleShare(options -> {
			handleShareWithOptions.handle(options);

			if (options.isHandOverOwnership()) {
				SormasUI.refreshView();
			}
		}, optionsForm, defaultOptions);
	}

	private void handleSync(
		HandleShareWithOptions handleShareWithOptions,
		SormasToSormasOptionsForm optionsForm,
		SormasToSormasShareInfoDto shareInfoDto) {
		SormasToSormasOptionsDto defaultOptions = new SormasToSormasOptionsDto();
		defaultOptions.setOrganization(new ServerAccessDataReferenceDto(shareInfoDto.getTarget().getUuid()));
		defaultOptions.setWithAssociatedContacts(shareInfoDto.isWithAssociatedContacts());
		defaultOptions.setWithSamples(shareInfoDto.isWithSamples());
		defaultOptions.setWithEventParticipants(shareInfoDto.isWithEvenParticipants());
		defaultOptions.setPseudonymizePersonalData(shareInfoDto.isPseudonymizedPersonalData());
		defaultOptions.setPseudonymizeSensitiveData(shareInfoDto.isPseudonymizedSensitiveData());
		defaultOptions.setPseudonymizeSensitiveData(shareInfoDto.isPseudonymizedSensitiveData());

		optionsForm.disableOrganization();

		shareHandleShare(options -> {
			handleShareWithOptions.handle(options);

			if (options.isHandOverOwnership()) {
				SormasUI.refreshView();
			}
		}, optionsForm, defaultOptions);
	}

	private Component buildShareErrorMessage(SormasToSormasException ex) {
		Label errorMessageLabel = new Label(ex.getMessage(), ContentMode.HTML);

		if (ex.getErrors() == null || ex.getErrors().size() == 0) {
			return errorMessageLabel;
		}

		VerticalLayout[] errorLayouts = ex.getErrors().entrySet().stream().map(e -> {
			Label groupLabel = new Label(e.getKey());
			groupLabel.addStyleNames(CssStyles.LABEL_BOLD);

			VerticalLayout groupErrorsLayout = new VerticalLayout(formatGroupErrors(e.getValue()));
			groupErrorsLayout.setMargin(false);
			groupErrorsLayout.setSpacing(false);
			groupErrorsLayout.setStyleName(CssStyles.HSPACE_LEFT_3);

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

	private Component[] formatGroupErrors(ValidationErrors errors) {
		return errors.getErrors().entrySet().stream().map(e -> {
			Label groupLabel = new Label(e.getKey() + ":");
			groupLabel.addStyleName(CssStyles.LABEL_BOLD);
			HorizontalLayout layout = new HorizontalLayout(groupLabel, new Label(String.join(", ", e.getValue())));
			layout.setMargin(false);

			return layout;
		}).toArray(Component[]::new);
	}

	private List<String> getCaseExcludedOrganizationIds(CaseDataDto caze) {
		return getExcludedOrganizationIds(caze.getSormasToSormasOriginInfo(), new SormasToSormasShareInfoCriteria().caze(caze.toReference()));
	}

	private List<String> getContactExcludedOrganizationIds(ContactDto contact) {
		return getExcludedOrganizationIds(
			contact.getSormasToSormasOriginInfo(),
			new SormasToSormasShareInfoCriteria().contact(contact.toReference()));
	}

	private List<String> getEventExcludedOrganizationIds(EventDto event) {
		return getExcludedOrganizationIds(event.getSormasToSormasOriginInfo(), new SormasToSormasShareInfoCriteria().event(event.toReference()));
	}

	private List<String> getExcludedOrganizationIds(SormasToSormasOriginInfoDto originInfo, SormasToSormasShareInfoCriteria criteria) {
		List<String> organizationIds = new ArrayList<>();

		if (originInfo != null) {
			organizationIds.add(originInfo.getOrganizationId());
		}

		List<SormasToSormasShareInfoDto> shares = FacadeProvider.getSormasToSormasFacade().getShareInfoIndexList(criteria, null, null);

		organizationIds.addAll(shares.stream().map(s -> s.getTarget().getUuid()).collect(Collectors.toList()));

		return organizationIds;
	}

	private interface HandleShareWithOptions {

		void handle(SormasToSormasOptionsDto options) throws SormasToSormasException;
	}
}
