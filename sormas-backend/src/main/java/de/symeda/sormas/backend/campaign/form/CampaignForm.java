package de.symeda.sormas.backend.campaign.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = "campaignforms")
@Audited
public class CampaignForm extends AbstractDomainObject {

	private static final long serialVersionUID = -5200626281564146919L;

	public static final String TABLE_NAME = "campaignforms";

	public static final String FORM_ID = "formId";
	public static final String LANGUAGE_CODE = "languageCode";
	public static final String CAMPAIGN_FORM_ELEMENTS = "campaignFormElements";

	private String formId;
	private String languageCode;
	private String campaignFormElements;
	private List<CampaignFormElement> campaignFormElementsList;

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
			if (StringUtils.isEmpty(campaignFormElements)) {
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
}
