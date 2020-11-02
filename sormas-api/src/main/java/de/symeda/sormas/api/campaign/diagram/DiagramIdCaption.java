package de.symeda.sormas.api.campaign.diagram;

import java.io.Serializable;

public class DiagramIdCaption implements Serializable {

	private String diagramId;
	private String diagramCaption;

	public DiagramIdCaption(String diagramId, String diagramCaption) {
		this.diagramId = diagramId;
		this.diagramCaption = diagramCaption;
	}

	public String getDiagramId() {
		return diagramId;
	}

	public void setDiagramId(String diagramId) {
		this.diagramId = diagramId;
	}

	public String getDiagramCaption() {
		return diagramCaption;
	}

	public void setDiagramCaption(String diagramCaption) {
		this.diagramCaption = diagramCaption;
	}

	@Override
	public String toString() {
		return this.diagramCaption;
	}
}
