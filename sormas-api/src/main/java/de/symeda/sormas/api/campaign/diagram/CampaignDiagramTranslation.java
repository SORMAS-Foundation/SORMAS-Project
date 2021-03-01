package de.symeda.sormas.api.campaign.diagram;

import java.io.Serializable;
import java.util.Objects;

public class CampaignDiagramTranslation implements Serializable {

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

	/**
	 * Needed. Otherwise hibernate will persist whenever loading,
	 * because hibernate types creates new instances that aren't equal.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CampaignDiagramTranslation that = (CampaignDiagramTranslation) o;
		return Objects.equals(elementId, that.elementId) && Objects.equals(caption, that.caption);
	}

	@Override
	public int hashCode() {
		return Objects.hash(elementId, caption);
	}
}
