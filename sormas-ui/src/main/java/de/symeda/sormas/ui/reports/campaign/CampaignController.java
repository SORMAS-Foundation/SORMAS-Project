package de.symeda.sormas.ui.reports.campaign;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.campaign.CampaignDataForm;
import de.symeda.sormas.ui.campaign.CampaignsView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CampaignController {

	public void createOrEdit(String uuid) {
		CommitDiscardWrapperComponent<CampaignDataForm> campaignComponent;
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
				Button archiveCampaignButton = ButtonHelper.createButton(
						archived ? Captions.actionDearchive : Captions.actionArchive,
						e -> {
							campaignComponent.commit();
							archiveOrDearchiveCampaign(campaign.getUuid(), !archived);
						},
						ValoTheme.BUTTON_LINK);

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

	private void archiveOrDearchiveCampaign(String campaignUuid, boolean archive) {
		if (archive) {
			Label contentLabel = new Label(String.format(I18nProperties.getString(Strings.confirmationArchiveCampaign),
					I18nProperties.getString(Strings.entityCampaign).toLowerCase(),
					I18nProperties.getString(Strings.entityCampaign).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(I18nProperties.getString(Strings.headingArchiveCampaign), contentLabel,
					I18nProperties.getString(Strings.yes), I18nProperties.getString(Strings.no), 640, e -> {
						if (e.booleanValue() == true) {
							FacadeProvider.getCampaignFacade().archiveOrDearchiveCampaign(campaignUuid, true);
							SormasUI.refreshView();
						}
					});
		} else {
			Label contentLabel = new Label(String.format(I18nProperties.getString(Strings.confirmationDearchiveCampaign),
					I18nProperties.getString(Strings.entityCampaign).toLowerCase(),
					I18nProperties.getString(Strings.entityCampaign).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(I18nProperties.getString(Strings.headingDearchiveCampaign), contentLabel,
					I18nProperties.getString(Strings.yes), I18nProperties.getString(Strings.no), 640, e -> {
						if (e.booleanValue()) {
							FacadeProvider.getCampaignFacade().archiveOrDearchiveCampaign(campaignUuid, false);
							SormasUI.refreshView();
						}
					});
		}
	}

	public CommitDiscardWrapperComponent<CampaignDataForm> getCampaignComponent(CampaignDto campaignDto,
			Runnable callback) {
		CampaignDataForm campaignForm = new CampaignDataForm(campaignDto == null);

		final CommitDiscardWrapperComponent<CampaignDataForm> view = new CommitDiscardWrapperComponent<CampaignDataForm>(
				campaignForm, campaignForm.getFieldGroup());

		if (campaignDto == null) {
			campaignDto = CampaignDto.build();
			campaignDto.setCreatingUser(UserProvider.getCurrent().getUserReference());
		}
		campaignForm.setValue(campaignDto);

		view.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!campaignForm.getFieldGroup().isModified()) {
					CampaignDto dto = campaignForm.getValue();
					FacadeProvider.getCampaignFacade().saveCampaign(dto);
					callback.run();
				}
			}
		});

		return view;
	}

	public void registerViews(Navigator navigator) {
		navigator.addView(CampaignsView.VIEW_NAME, CampaignsView.class);
	}

	private CampaignDto getCampaign(String uuid) {
		return FacadeProvider.getCampaignFacade().getByUuid(uuid);
	}
}
