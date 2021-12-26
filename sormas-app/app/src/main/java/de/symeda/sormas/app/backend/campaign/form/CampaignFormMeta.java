/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.campaign.form;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;

@Entity(name = CampaignFormMeta.TABLE_NAME)
@DatabaseTable(tableName = CampaignFormMeta.TABLE_NAME)
public class CampaignFormMeta extends PseudonymizableAdo {

	public static final String TABLE_NAME = "campaignformmeta";
	public static final String I18N_PREFIX = "CampaignFormMeta";

	public static final String FORM_ID = "formId";
	public static final String FORM_NAME = "formName";
	public static final String CAMPAIGN_FORM_ELEMENTS = "campaignFormElements";
	public static final String CAMPAIGN_FORM_TRANSLATIONS = "campaignFormTranslations";

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String formId;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String formName;

	@Column(length = 32)
	private String languageCode;

	@Column(name = "campaignFormElements")
	private String campaignFormElementsJson;
	private List<CampaignFormElement> campaignFormElements;

	@Column(name = "campaignFormTranslations")
	private String campaignFormTranslationsJson;
	private List<CampaignFormTranslations> campaignFormTranslations;

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getCampaignFormElementsJson() {
		return campaignFormElementsJson;
	}

	public void setCampaignFormElementsJson(String campaignFormElementsJson) {
		this.campaignFormElementsJson = campaignFormElementsJson;
	}

	@Transient
	public List<CampaignFormElement> getCampaignFormElements() {
		if (campaignFormElements == null) {
			Gson gson = new Gson();
			Type type = new TypeToken<List<CampaignFormElement>>() {
			}.getType();
			campaignFormElements = gson.fromJson(campaignFormElementsJson, type);
			if (campaignFormElements == null) {
				campaignFormElements = new ArrayList<>();
			}
		}
		return campaignFormElements;
	}

	public void setCampaignFormElements(List<CampaignFormElement> campaignFormElements) {
		this.campaignFormElements = campaignFormElements;
		Gson gson = new Gson();
		campaignFormElementsJson = gson.toJson(campaignFormElements);
	}

	public String getCampaignFormTranslationsJson() {
		return campaignFormTranslationsJson;
	}

	public void setCampaignFormTranslationsJson(String campaignFormTranslationsJson) {
		this.campaignFormTranslationsJson = campaignFormTranslationsJson;
		this.campaignFormTranslations = null;
	}

	@Transient
	public List<CampaignFormTranslations> getCampaignFormTranslations() {
		if (campaignFormTranslations == null) {
			Gson gson = new Gson();
			Type type = new TypeToken<List<CampaignFormTranslations>>() {
			}.getType();
			campaignFormTranslations = gson.fromJson(campaignFormTranslationsJson, type);
			if (campaignFormTranslations == null) {
				campaignFormTranslations = new ArrayList<>();
			}
		}
		return campaignFormTranslations;
	}

	public void setCampaignFormTranslations(List<CampaignFormTranslations> campaignFormTranslations) {
		this.campaignFormTranslations = campaignFormTranslations;
		Gson gson = new Gson();
		campaignFormTranslationsJson = gson.toJson(campaignFormTranslations);
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	@Override
	public String toString() {
		return getFormName();
	}
}
