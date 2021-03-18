package de.symeda.sormas.ui.campaign.campaignstatistics;

import de.symeda.sormas.ui.campaign.AbstractCampaignView;
import de.symeda.sormas.ui.campaign.components.CampaignSelector;

public class CampaignStatisticsView extends AbstractCampaignView {

	private final CampaignSelector campaignLayout;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/campaignstatistics";

	public CampaignStatisticsView() {
		super(VIEW_NAME);

		campaignLayout = new CampaignSelector();
		addHeaderComponent(campaignLayout);
	}
}
