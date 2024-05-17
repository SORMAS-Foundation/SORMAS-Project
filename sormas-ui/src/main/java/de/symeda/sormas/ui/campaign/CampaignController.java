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

package de.symeda.sormas.ui.campaign;

import static com.vaadin.v7.data.Validator.InvalidValueException;
import static de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria.CAMPAIGN;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.campaign.campaigndata.CampaignDataView;
import de.symeda.sormas.ui.campaign.campaigndata.CampaignFormDataEditForm;
import de.symeda.sormas.ui.campaign.campaigndata.CampaignFormDataView;
import de.symeda.sormas.ui.campaign.campaigns.CampaignEditForm;
import de.symeda.sormas.ui.campaign.campaigns.CampaignView;
import de.symeda.sormas.ui.campaign.campaigns.CampaignsView;
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CampaignController {

	public void createOrEditCampaign(String uuid) {

		CommitDiscardWrapperComponent<CampaignEditForm> campaignComponent;
		String heading;
		if (uuid != null) {
			CampaignDto campaign = getCampaign(uuid);
			campaignComponent = getCampaignComponent(getCampaign(uuid), () -> {
				Notification.show(I18nProperties.getString(Strings.messageCampaignSaved), Type.WARNING_MESSAGE);
				SormasUI.refreshView();
			});

			if (UiUtil.permitted(UserRight.CAMPAIGN_DELETE)) {
				campaignComponent.addDeleteWithReasonOrRestoreListener((deleteDetails) -> {
					FacadeProvider.getCampaignFacade().delete(campaign.getUuid(), deleteDetails);
					campaignComponent.discard();
					SormasUI.refreshView();
				}, null, (deleteDetails) -> {
					FacadeProvider.getCampaignFacade().restore(campaign.getUuid());
					campaignComponent.discard();
					SormasUI.refreshView();
				}, I18nProperties.getString(Strings.entityCampaign), campaign.getUuid(), FacadeProvider.getCampaignFacade());
			}

			// Initialize 'Archive' button
			if (UiUtil.permitted(UserRight.CAMPAIGN_ARCHIVE)) {
				createArchiveButton(campaignComponent, campaign);
			}
			heading = I18nProperties.getString(Strings.headingEditCampaign);
		} else {
			campaignComponent = getCampaignComponent(null, () -> {
				Notification.show(I18nProperties.getString(Strings.messageCampaignCreated), Type.WARNING_MESSAGE);
				SormasUI.refreshView();
			});
			heading = I18nProperties.getString(Strings.headingCreateNewCampaign);
		}
		VaadinUiUtil.showModalPopupWindow(campaignComponent, heading);
	}

	private void createArchiveButton(CommitDiscardWrapperComponent<CampaignEditForm> campaignComponent, CampaignDto campaign) {
		ControllerProvider.getArchiveController()
			.addArchivingButton(campaign, ArchiveHandlers.forCampaign(), campaignComponent, () -> navigateToCampaign(campaign.getUuid()));
	}

	public void createCampaignDataForm(CampaignReferenceDto campaign, CampaignFormMetaReferenceDto campaignForm) {
		Window window = VaadinUiUtil.createPopupWindow();

		CommitDiscardWrapperComponent<CampaignFormDataEditForm> component =
			getCampaignFormDataComponent(null, campaign, campaignForm, false, true, () -> {
				window.close();
				SormasUI.refreshView();
				Notification.show(
					String.format(I18nProperties.getString(Strings.messageCampaignFormSaved), campaignForm.buildCaption()),
					Type.TRAY_NOTIFICATION);
			}, window::close);

		window.setCaption(String.format(I18nProperties.getString(Strings.headingCreateCampaignDataForm), campaignForm.buildCaption()));
		window.setContent(component);
		UI.getCurrent().addWindow(window);
	}

	public CommitDiscardWrapperComponent<CampaignEditForm> getCampaignComponent(CampaignDto campaignDto, Runnable callback) {

		CampaignEditForm campaignEditForm = new CampaignEditForm(campaignDto);
		boolean isCreate = false;
		if (campaignDto == null) {
			isCreate = true;
			campaignDto = CampaignDto.build();
			campaignDto.setCreatingUser(UiUtil.getUserReference());
		}
		campaignEditForm.setValue(campaignDto);

		final CommitDiscardWrapperComponent<CampaignEditForm> campaignComponent =
			new CommitDiscardWrapperComponent<CampaignEditForm>(campaignEditForm, true, campaignEditForm.getFieldGroup()) {

				@Override
				public void discard() {
					super.discard();
					campaignEditForm.discard();
				}
			};

		if (UiUtil.permitted(!isCreate, UserRight.CAMPAIGN_DELETE)) {
			CampaignDto finalCampaignDto = campaignDto;
			campaignComponent.addDeleteWithReasonOrRestoreListener((deleteDetails) -> {
				FacadeProvider.getCampaignFacade().delete(finalCampaignDto.getUuid(), deleteDetails);
				UI.getCurrent().getNavigator().navigateTo(CampaignsView.VIEW_NAME);
			}, null, (deleteDetails) -> {
				FacadeProvider.getCampaignFacade().restore(finalCampaignDto.getUuid());
				campaignComponent.discard();
				SormasUI.refreshView();
			}, I18nProperties.getString(Strings.entityCampaign), finalCampaignDto.getUuid(), FacadeProvider.getCampaignFacade());
		}

		// Initialize 'Archive' button
		if (UiUtil.permitted(!isCreate, UserRight.CAMPAIGN_ARCHIVE)) {
			final CampaignDto campaign = campaignDto;
			createArchiveButton(campaignComponent, campaign);
		}

		campaignComponent.addCommitListener(() -> {
			if (!campaignEditForm.getFieldGroup().isModified()) {
				CampaignDto dto = campaignEditForm.getValue();
				FacadeProvider.getCampaignFacade().save(dto);
				SormasUI.refreshView();
				callback.run();
			}
		});

		if (campaignDto.isDeleted()) {
			campaignComponent.getWrappedComponent().getField(CampaignDto.DELETION_REASON).setVisible(true);
			if (campaignComponent.getWrappedComponent().getField(CampaignDto.DELETION_REASON).getValue() == DeletionReason.OTHER_REASON) {
				campaignComponent.getWrappedComponent().getField(CampaignDto.OTHER_DELETION_REASON).setVisible(true);
			}
		}

		campaignComponent
			.restrictEditableComponentsOnEditView(UserRight.CAMPAIGN_EDIT, null, UserRight.CAMPAIGN_DELETE, UserRight.CAMPAIGN_ARCHIVE, null, true);

		return campaignComponent;
	}

	public CommitDiscardWrapperComponent<CampaignFormDataEditForm> getCampaignFormDataComponent(
		CampaignFormDataDto campaignFormData,
		CampaignReferenceDto campaign,
		CampaignFormMetaReferenceDto campaignForm,
		boolean revertFormOnDiscard,
		boolean isCreate,
		Runnable commitCallback,
		Runnable discardCallback) {

		CampaignFormDataEditForm form = new CampaignFormDataEditForm(campaignFormData == null);
		if (campaignFormData == null) {

			final UserDto currentUser = UiUtil.getUser();
			campaignFormData =
				CampaignFormDataDto.build(campaign, campaignForm, currentUser.getRegion(), currentUser.getDistrict(), currentUser.getCommunity());
			campaignFormData.setCreatingUser(UiUtil.getUserReference());
		}
		form.setValue(campaignFormData);

		final CommitDiscardWrapperComponent<CampaignFormDataEditForm> component =
			new CommitDiscardWrapperComponent<>(form, true, form.getFieldGroup());

		component.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				try {
					form.validate();
				} catch (InvalidValueException e) {
					Notification.show(I18nProperties.getValidationError(Validations.errorsInForm), Type.ERROR_MESSAGE);
					return;
				}

				CampaignFormDataDto formData = form.getValue();
				FacadeProvider.getCampaignFormDataFacade().saveCampaignFormData(formData);
				if (commitCallback != null) {
					commitCallback.run();
					UI.getCurrent().getNavigator().navigateTo(CampaignDataView.VIEW_NAME + "/?" + CAMPAIGN + "=" + campaign.getUuid());
				}
			}
		});

		component.addDiscardListener(
			() -> UI.getCurrent().getNavigator().navigateTo(CampaignDataView.VIEW_NAME + "/?" + CAMPAIGN + "=" + campaign.getUuid()));

		if (revertFormOnDiscard) {
			component.addDiscardListener(form::resetFormValues);
		}

		if (discardCallback != null) {
			component.addDiscardListener(discardCallback::run);
		}

		String campaignFormDataUuid = campaignFormData.getUuid();
		if (UiUtil.permitted(!isCreate, UserRight.CAMPAIGN_DELETE)) {

			component.addDeleteListener(() -> {
				FacadeProvider.getCampaignFormDataFacade().deleteCampaignFormData(campaignFormDataUuid);
				UI.getCurrent().getNavigator().navigateTo(CampaignFormDataView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityCampaignDataForm));
		}

		boolean isInJurisdiction = isCreate || FacadeProvider.getCampaignFormDataFacade().isInJurisdiction(campaignFormDataUuid);
		component.restrictEditableComponentsOnEditView(
			UserRight.CAMPAIGN_FORM_DATA_EDIT,
			null,
			UserRight.CAMPAIGN_FORM_DATA_DELETE,
			UserRight.CAMPAIGN_FORM_DATA_ARCHIVE,
			null,
			isInJurisdiction);

		return component;
	}

	private CampaignDto getCampaign(String uuid) {
		return FacadeProvider.getCampaignFacade().getByUuid(uuid);
	}

	public void navigateToCampaign(String uuid) {
		String navigationState = CampaignView.VIEW_NAME + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateToFormDataView(String uuid) {
		String navigationState = CampaignFormDataView.VIEW_NAME + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateToCampaignData(String campaignUuid) {
		String navigationState = CampaignDataView.VIEW_NAME + "/?" + CampaignFormDataDto.CAMPAIGN + "=" + campaignUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}
}
