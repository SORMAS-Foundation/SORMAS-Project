package de.symeda.sormas.api.campaign.form;

import java.io.Serializable;
import java.util.List;

public class CampaignFormTranslations implements Serializable {

	private static final long serialVersionUID = 8326680921734712660L;

	private String languageCode;
	private List<CampaignFormTranslation> translations;

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public List<CampaignFormTranslation> getTranslations() {
		return translations;
	}

	public void setTranslations(List<CampaignFormTranslation> translations) {
		this.translations = translations;
	}
}
