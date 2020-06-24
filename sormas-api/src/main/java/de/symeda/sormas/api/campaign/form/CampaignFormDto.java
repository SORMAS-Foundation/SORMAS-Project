package de.symeda.sormas.api.campaign.form;

import java.util.List;

import de.symeda.sormas.api.EntityDto;

public class CampaignFormDto extends EntityDto {

	private static final long serialVersionUID = -1163673887940552133L;

	public static final String FORM_ID = "formId";
	public static final String LANGUAGE_CODE = "languageCode";
	public static final String CAMPAIGN_FORM_ELEMENTS = "campaignFormElements";

	private String formId;
	private String languageCode;
	private List<CampaignFormElement> campaignFormElements;
	private List<CampaignFormTranslations> campaignFormTranslations;

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public List<CampaignFormElement> getCampaignFormElements() {
		return campaignFormElements;
	}

	public void setCampaignFormElements(List<CampaignFormElement> campaignFormElements) {
		this.campaignFormElements = campaignFormElements;
	}

	public List<CampaignFormTranslations> getCampaignFormTranslations() {
		return campaignFormTranslations;
	}

	public void setCampaignFormTranslations(List<CampaignFormTranslations> campaignFormTranslations) {
		this.campaignFormTranslations = campaignFormTranslations;
	}
}
