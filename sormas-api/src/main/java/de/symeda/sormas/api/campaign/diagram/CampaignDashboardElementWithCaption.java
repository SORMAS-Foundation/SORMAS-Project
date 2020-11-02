package de.symeda.sormas.api.campaign.diagram;

public class CampaignDashboardElementWithCaption extends CampaignDashboardElement {

	private String diagramCaption;

	public CampaignDashboardElementWithCaption() {
	}

	public CampaignDashboardElementWithCaption(
		String diagramId,
		String diagramCaption,
		String tabId,
		Integer order,
		Integer width,
		Integer height) {
		super(diagramId, tabId, order, width, height);
		this.diagramCaption = diagramCaption;
	}

	public String getDiagramCaption() {
		return diagramCaption;
	}

	public void setDiagramCaption(String diagramCaption) {
		this.diagramCaption = diagramCaption;
	}
}
