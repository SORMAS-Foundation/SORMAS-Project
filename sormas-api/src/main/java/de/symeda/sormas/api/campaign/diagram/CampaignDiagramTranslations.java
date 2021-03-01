package de.symeda.sormas.api.campaign.diagram;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class CampaignDiagramTranslations implements Serializable {

	private String languageCode;
	private String diagramCaption;
	private List<CampaignDiagramTranslation> translations;

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getDiagramCaption() {
		return diagramCaption;
	}

	public void setDiagramCaption(String diagramCaption) {
		this.diagramCaption = diagramCaption;
	}

	public List<CampaignDiagramTranslation> getTranslations() {
		return translations;
	}

	public void setTranslations(List<CampaignDiagramTranslation> translations) {
		this.translations = translations;
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
		CampaignDiagramTranslations that = (CampaignDiagramTranslations) o;
		return Objects.equals(languageCode, that.languageCode) && Objects.equals(translations, that.translations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(languageCode, translations);
	}
}
