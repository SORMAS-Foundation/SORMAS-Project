package de.symeda.sormas.backend.campaign.form;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(name = "campaignforms")
@Audited
public class CampaignForm extends AbstractDomainObject {

	private static final long serialVersionUID = -5200626281564146919L;

	public static final String TABLE_NAME = "campaignforms";

	private String formId;
	private String languageCode;
	private String campaignFormElements;
	private List<CampaignFormElement> campaignFormElementsList;
	private String campaignFormTranslations;
	private List<CampaignFormTranslations> campaignFormTranslationsList;

	@Column
	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	@Column
	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	public String getCampaignFormElements() {
		return campaignFormElements;
	}

	public void setCampaignFormElements(String campaignFormElements) {
		this.campaignFormElements = campaignFormElements;
		campaignFormElementsList = null;
	}

	@Transient
	public List<CampaignFormElement> getCampaignFormElementsList() {
		if (campaignFormElementsList == null) {
			if (StringUtils.isBlank(campaignFormElements)) {
				campaignFormElementsList = new ArrayList<>();
			} else {
				try {
					ObjectMapper mapper = new ObjectMapper();
					campaignFormElementsList = Arrays.asList(mapper.readValue(campaignFormElements, CampaignFormElement[].class));
				} catch (IOException e) {
					throw new RuntimeException("Content of campaignFormElements could not be parsed to List<CampaignFormElement> - ID: " + getId());
				}
			}
		}
		return campaignFormElementsList;
	}

	public void setCampaignFormElementsList(List<CampaignFormElement> campaignFormElementsList) {
		this.campaignFormElementsList = campaignFormElementsList;

		if (this.campaignFormElementsList == null) {
			campaignFormElements = null;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			campaignFormElements = mapper.writeValueAsString(campaignFormElementsList);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Content of campaignFormElementsList could not be parsed to JSON String - ID: " + getId());
		}
	}

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	public String getCampaignFormTranslations() {
		return campaignFormTranslations;
	}

	public void setCampaignFormTranslations(String campaignFormTranslations) {
		this.campaignFormTranslations = campaignFormTranslations;
		campaignFormTranslationsList = null;
	}

	@Transient
	public List<CampaignFormTranslations> getCampaignFormTranslationsList() {
		if (campaignFormTranslationsList == null) {
			if (StringUtils.isBlank(campaignFormTranslations)) {
				campaignFormTranslationsList = new ArrayList<>();
			} else {
				try {
					ObjectMapper mapper = new ObjectMapper();
					campaignFormTranslationsList = Arrays.asList(mapper.readValue(campaignFormTranslations, CampaignFormTranslations[].class));
				} catch (IOException e) {
					throw new RuntimeException(
						"Content of campaignFormTranslations could not be parsed to List<CampaignFormTranslations> - ID: " + getId());
				}
			}
		}
		return campaignFormTranslationsList;
	}

	public void setCampaignFormTranslationsList(List<CampaignFormTranslations> campaignFormTranslationsList) {
		this.campaignFormTranslationsList = campaignFormTranslationsList;

		if (this.campaignFormTranslationsList == null) {
			campaignFormTranslations = null;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			campaignFormTranslations = mapper.writeValueAsString(campaignFormTranslationsList);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Content of campaignFormTranslationsList could not be parsed to JSON String - ID: " + getId());
		}
	}

	@Override
	public String toString() {
		return formId;
	}

}
