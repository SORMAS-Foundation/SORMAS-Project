package de.symeda.sormas.api.campaign.form;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import de.symeda.sormas.api.campaign.data.translation.TranslationElement;

public class CampaignFormTranslations implements Serializable {

	private static final long serialVersionUID = 8326680921734712660L;

	private String languageCode;
	private List<TranslationElement> translations;

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
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
		CampaignFormTranslations that = (CampaignFormTranslations) o;
		return Objects.equals(languageCode, that.languageCode) && Objects.equals(translations, that.translations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(languageCode, translations);
	}
}
