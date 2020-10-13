package de.symeda.sormas.ui.dashboard.campaigns;

import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;

public class CampaignDashboardDiagramDto {

	private final CampaignDashboardElement campaignDashboardElement;
	private final CampaignDiagramDefinitionDto campaignDiagramDefinitionDto;

	public CampaignDashboardDiagramDto(CampaignDashboardElement campaignDashboardElement, CampaignDiagramDefinitionDto campaignDiagramDefinitionDto) {
		if (campaignDashboardElement == null
			|| campaignDiagramDefinitionDto == null
			|| !campaignDashboardElement.getDiagramId().equals(campaignDiagramDefinitionDto.getDiagramId())) {
			throw new RuntimeException("Could not construct campaign diagram!");
		}
		this.campaignDashboardElement = campaignDashboardElement;
		this.campaignDiagramDefinitionDto = campaignDiagramDefinitionDto;
	}

	public CampaignDashboardElement getCampaignDashboardElement() {
		return campaignDashboardElement;
	}

	public CampaignDiagramDefinitionDto getCampaignDiagramDefinitionDto() {
		return campaignDiagramDefinitionDto;
	}
}
