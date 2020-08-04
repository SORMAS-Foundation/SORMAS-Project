package de.symeda.sormas.api.campaign.diagram;

import java.util.List;

import de.symeda.sormas.api.EntityDto;

public class CampaignDiagramDefinitionDto extends EntityDto {

	private String diagramId;
	private DiagramType diagramType;
	private List<CampaignDiagramSeries> campaignDiagramSeriesList;

	public String getDiagramId() {
		return diagramId;
	}

	public void setDiagramId(String diagramId) {
		this.diagramId = diagramId;
	}

	public DiagramType getDiagramType() {
		return diagramType;
	}

	public void setDiagramType(DiagramType diagramType) {
		this.diagramType = diagramType;
	}

	public List<CampaignDiagramSeries> getCampaignDiagramSeriesList() {
		return campaignDiagramSeriesList;
	}

	public void setCampaignDiagramSeriesList(List<CampaignDiagramSeries> campaignDiagramSeriesList) {
		this.campaignDiagramSeriesList = campaignDiagramSeriesList;
	}
}
