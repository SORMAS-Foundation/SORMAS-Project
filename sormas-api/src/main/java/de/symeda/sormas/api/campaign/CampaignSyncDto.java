package de.symeda.sormas.api.campaign;

import java.io.Serializable;
import java.util.List;

import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;

public class CampaignSyncDto implements Serializable {

	private List<CampaignDto> campaigns;
	private List<CampaignFormMetaDto> campaignFormMetas;

	public List<CampaignDto> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<CampaignDto> campaigns) {
		this.campaigns = campaigns;
	}

	public List<CampaignFormMetaDto> getCampaignFormMetas() {
		return campaignFormMetas;
	}

	public void setCampaignFormMetas(List<CampaignFormMetaDto> campaignFormMetas) {
		this.campaignFormMetas = campaignFormMetas;
	}
}
