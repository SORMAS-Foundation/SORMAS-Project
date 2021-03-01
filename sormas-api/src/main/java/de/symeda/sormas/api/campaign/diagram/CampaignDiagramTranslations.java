package de.symeda.sormas.api.campaign.diagram;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import de.symeda.sormas.api.campaign.data.translation.TranslationElement;

public class CampaignDiagramTranslations implements Serializable {

	private String languageCode;
	private String diagramCaption;
	private List<TranslationElement> seriesNames;
	private List<TranslationElement> translations;

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

	public List<TranslationElement> getSeriesNames() {
		return seriesNames;
	}

	public void setSeriesNames(List<TranslationElement> seriesNames) {
		this.seriesNames = seriesNames;
	}

	public List<TranslationElement> getTranslations() {
		return translations;
	}

	public void setTranslations(List<TranslationElement> translations) {
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
