package de.symeda.sormas.ui.campaign.campaigns;

import static com.vaadin.ui.Notification.Type.TRAY_NOTIFICATION;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

public class CampaignView extends AbstractDetailView<CampaignReferenceDto> {

	public static final String VIEW_NAME = CampaignsView.ROOT_VIEW_NAME + "/data";

	private CommitDiscardWrapperComponent<CampaignEditForm> editComponent;

	public CampaignView() {
		super(VIEW_NAME);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {

		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	protected CampaignReferenceDto getReferenceByUuid(String uuid) {
		final CampaignReferenceDto reference;
		if (FacadeProvider.getCampaignFacade().exists(uuid)) {
			reference = FacadeProvider.getCampaignFacade().getReferenceByUuid(uuid);
		} else {
			reference = null;
		}
		return reference;
	}

	@Override
	protected String getRootViewName() {
		return CampaignsView.ROOT_VIEW_NAME;
	}

	@Override
	protected void initView(String params) {
		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);

		CampaignDto campaignDto = FacadeProvider.getCampaignFacade().getByUuid(getReference().getUuid());
		editComponent = ControllerProvider.getCampaignController().getCampaignComponent(campaignDto, () -> {
			Notification.show(String.format(I18nProperties.getString(Strings.messageCampaignSaved), campaignDto.getName()), TRAY_NOTIFICATION);
		});
		editComponent.setMargin(false);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.setHeightUndefined();
		editComponent.addStyleName(CssStyles.ROOT_COMPONENT);
		editComponent.setWidth(100, Unit.PERCENTAGE);

		container.addComponent(editComponent);

		getViewTitleLabel().setValue(campaignDto.getName());
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		if (!findReferenceByParams(params)) {
			return;
		}
		menu.removeAllViews();
		menu.addView(CampaignsView.VIEW_NAME, I18nProperties.getCaption(Captions.campaignAllCampaigns));
		menu.addView(CampaignView.VIEW_NAME, I18nProperties.getCaption(Captions.Campaign), params);
	}
}
