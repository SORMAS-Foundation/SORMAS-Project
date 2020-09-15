package de.symeda.sormas.api.campaign.form;

import java.io.Serializable;

public class CampaignFormTranslation implements Serializable {

	private static final long serialVersionUID = 2230535709979088957L;

	private String elementId;
	private String caption;

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
}
