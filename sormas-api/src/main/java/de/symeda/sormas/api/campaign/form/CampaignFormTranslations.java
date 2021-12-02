package de.symeda.sormas.api.campaign.form;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_SMALL;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.campaign.data.translation.TranslationElement;
import de.symeda.sormas.api.i18n.Validations;

public class CampaignFormTranslations implements Serializable {

	private static final long serialVersionUID = 8326680921734712660L;

	@Size(max = CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String languageCode;
	@Valid
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
