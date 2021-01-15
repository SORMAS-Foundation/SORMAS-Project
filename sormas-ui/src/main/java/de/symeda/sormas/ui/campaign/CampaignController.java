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

import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.campaign.campaigndata.CampaignDataView;
import de.symeda.sormas.ui.campaign.campaigndata.CampaignFormDataEditForm;
import de.symeda.sormas.ui.campaign.campaigndata.CampaignFormDataView;
import de.symeda.sormas.ui.campaign.campaigns.CampaignEditForm;
import de.symeda.sormas.ui.campaign.campaigns.CampaignView;
import de.symeda.sormas.ui.campaign.campaigns.CampaignsView;
import de.symeda.sormas.ui.utils.ButtonHelper;
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

			if (UserProvider.getCurrent().hasUserRight(UserRight.CAMPAIGN_DELETE)) {
				campaignComponent.addDeleteListener(() -> {
					FacadeProvider.getCampaignFacade().deleteCampaign(campaign.getUuid());
					campaignComponent.discard();
					SormasUI.refreshView();
				}, I18nProperties.getString(Strings.entityCampaign));
			}

			// Initialize 'Archive' button
			if (UserProvider.getCurrent().hasUserRight(UserRight.CAMPAIGN_ARCHIVE)) {
				boolean archived = FacadeProvider.getCampaignFacade().isArchived(campaign.getUuid());
				Button archiveCampaignButton = ButtonHelper.createButton(archived ? Captions.actionDearchive : Captions.actionArchive, e -> {
					campaignComponent.commit();
					archiveOrDearchiveCampaign(campaign.getUuid(), !archived);
				}, ValoTheme.BUTTON_LINK);

				campaignComponent.getButtonsPanel().addComponentAsFirst(archiveCampaignButton);
				campaignComponent.getButtonsPanel().setComponentAlignment(archiveCampaignButton, Alignment.BOTTOM_LEFT);
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

	public void createCampaignDataForm(CampaignReferenceDto campaign, CampaignFormMetaReferenceDto campaignForm) {
		Window window = VaadinUiUtil.createPopupWindow();

		CommitDiscardWrapperComponent<CampaignFormDataEditForm> component =
			getCampaignFormDataComponent(null, campaign, campaignForm, false, false, () -> {
				window.close();
				SormasUI.refreshView();
				Notification
					.show(String.format(I18nProperties.getString(Strings.messageCampaignFormSaved), campaignForm.toString()), Type.TRAY_NOTIFICATION);
			}, window::close);

		window.setCaption(String.format(I18nProperties.getString(Strings.headingCreateCampaignDataForm), campaignForm.toString()));
		window.setContent(component);
		UI.getCurrent().addWindow(window);
	}

	private void archiveOrDearchiveCampaign(String campaignUuid, boolean archive) {

		if (archive) {
			Label contentLabel = new Label(
				String.format(
					I18nProperties.getString(Strings.confirmationArchiveCampaign),
					I18nProperties.getString(Strings.entityCampaign).toLowerCase(),
					I18nProperties.getString(Strings.entityCampaign).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingArchiveCampaign),
				contentLabel,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				e -> {
					if (e) {
						FacadeProvider.getCampaignFacade().archiveOrDearchiveCampaign(campaignUuid, true);
						SormasUI.refreshView();
					}
				});
		} else {
			Label contentLabel = new Label(
				String.format(
					I18nProperties.getString(Strings.confirmationDearchiveCampaign),
					I18nProperties.getString(Strings.entityCampaign).toLowerCase(),
					I18nProperties.getString(Strings.entityCampaign).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingDearchiveCampaign),
				contentLabel,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				e -> {
					if (e) {
						FacadeProvider.getCampaignFacade().archiveOrDearchiveCampaign(campaignUuid, false);
						SormasUI.refreshView();
					}
				});
		}
	}

	public CommitDiscardWrapperComponent<CampaignEditForm> getCampaignComponent(CampaignDto campaignDto, Runnable callback) {

		CampaignEditForm campaignEditForm = new CampaignEditForm(campaignDto);
		if (campaignDto == null) {
			campaignDto = CampaignDto.build();
			campaignDto.setCreatingUser(UserProvider.getCurrent().getUserReference());
		}
		campaignEditForm.setValue(campaignDto);

		final CommitDiscardWrapperComponent<CampaignEditForm> campaignComponent =
			new CommitDiscardWrapperComponent<CampaignEditForm>(campaignEditForm, UserProvider.getCurrent().hasUserRight(UserRight.CAMPAIGN_EDIT), campaignEditForm.getFieldGroup()) {

				@Override
				public void discard() {
					super.discard();
					campaignEditForm.discard();
				}
			};

		if (UserProvider.getCurrent().hasUserRight(UserRight.CAMPAIGN_DELETE)) {
			CampaignDto finalCampaignDto = campaignDto;
			campaignComponent.addDeleteListener(() -> {
				FacadeProvider.getCampaignFacade().deleteCampaign(finalCampaignDto.getUuid());
				UI.getCurrent().getNavigator().navigateTo(CampaignsView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityCampaign));
		}

		// Initialize 'Archive' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.CAMPAIGN_ARCHIVE)) {
			final String campaignUuid = campaignDto.getUuid();
			boolean archived = FacadeProvider.getCampaignFacade().isArchived(campaignUuid);
			Button archiveCampaignButton = ButtonHelper.createButton(archived ? Captions.actionDearchive : Captions.actionArchive, e -> {
				campaignComponent.commit();
				archiveOrDearchiveCampaign(campaignUuid, !archived);
			}, ValoTheme.BUTTON_LINK);

			campaignComponent.getButtonsPanel().addComponentAsFirst(archiveCampaignButton);
			campaignComponent.getButtonsPanel().setComponentAlignment(archiveCampaignButton, Alignment.BOTTOM_LEFT);
		}

		campaignComponent.addCommitListener(() -> {
			if (!campaignEditForm.getFieldGroup().isModified()) {
				CampaignDto dto = campaignEditForm.getValue();
				FacadeProvider.getCampaignFacade().saveCampaign(dto);
				SormasUI.refreshView();
				callback.run();
			}
		});

		return campaignComponent;
	}

	public CommitDiscardWrapperComponent<CampaignFormDataEditForm> getCampaignFormDataComponent(
		CampaignFormDataDto campaignFormData,
		CampaignReferenceDto campaign,
		CampaignFormMetaReferenceDto campaignForm,
		boolean revertFormOnDiscard,
		boolean showDeleteButton,
		Runnable commitCallback,
		Runnable discardCallback) {

		CampaignFormDataEditForm form = new CampaignFormDataEditForm(campaignFormData == null);
		if (campaignFormData == null) {

			final UserDto currentUser = UserProvider.getCurrent().getUser();
			campaignFormData =
				CampaignFormDataDto.build(campaign, campaignForm, currentUser.getRegion(), currentUser.getDistrict(), currentUser.getCommunity());
			campaignFormData.setCreatingUser(UserProvider.getCurrent().getUserReference());
		}
		form.setValue(campaignFormData);

		final CommitDiscardWrapperComponent<CampaignFormDataEditForm> component = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());

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
					UI.getCurrent().getNavigator().navigateTo(CampaignDataView.VIEW_NAME);
				}
			}
		});

		component.addDiscardListener(() -> UI.getCurrent().getNavigator().navigateTo(CampaignDataView.VIEW_NAME));

		if (revertFormOnDiscard) {
			component.addDiscardListener(form::resetFormValues);
		}

		if (discardCallback != null) {
			component.addDiscardListener(discardCallback::run);
		}

		if (showDeleteButton && UserProvider.getCurrent().hasUserRight(UserRight.CAMPAIGN_DELETE)) {
			String campaignFormDataUuid = campaignFormData.getUuid();

			component.addDeleteListener(() -> {
				FacadeProvider.getCampaignFormDataFacade().deleteCampaignFormData(campaignFormDataUuid);
				UI.getCurrent().getNavigator().navigateTo(CampaignFormDataView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityCampaignDataForm));
		}

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
