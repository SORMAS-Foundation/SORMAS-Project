/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.backend.campaign.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.campaign.data.CampaignFormDataReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormValue;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.form.CampaignForm;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(name = "campaignFormData")
@Audited
public class CampaignFormData extends AbstractDomainObject {

	public static final String TABLE_NAME = "campaignFormData";

	public static final String CAMPAIGN = "campaign";
	public static final String CAMPAIGN_FORM = "campaignForm";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String ARCHIVED = "archived";

	private static final long serialVersionUID = -8021065433714419288L;

	private List<CampaignFormValue> formValuesList;
	private String formValues;
	private Campaign campaign;
	private CampaignForm campaignForm;
	private Region region;
	private District district;
	private Community community;
	private boolean archived;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	public String getFormValues() {
		return formValues;
	}

	public void setFormValues(String formValues) {
		this.formValues = formValues;
	}

	@Transient
	public List<CampaignFormValue> getFormValuesList() {
		if (formValuesList == null) {
			if (StringUtils.isBlank(formValues)) {
				formValuesList = new ArrayList<>();
			} else {
				try {
					ObjectMapper mapper = new ObjectMapper();
					formValuesList = Arrays.asList(mapper.readValue(formValues, CampaignFormValue[].class));
				} catch (IOException e) {
					throw new RuntimeException("Content of formValues could not be parsed to List<CampaignFormValue> - ID: " + getId());
				}
			}
		}
		return formValuesList;
	}

	public void setFormValuesList(List<CampaignFormValue> formValuesList) {
		this.formValuesList = formValuesList;

		if (this.formValuesList == null) {
			formValues = null;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			formValues = mapper.writeValueAsString(formValuesList);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Content of formValuesList could not be parsed to JSON String - ID: " + getId());
		}
	}

	@ManyToOne()
	@JoinColumn(nullable = false)
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	@ManyToOne()
	@JoinColumn(nullable = false)
	public CampaignForm getCampaignForm() {
		return campaignForm;
	}

	public void setCampaignForm(CampaignForm campaignForm) {
		this.campaignForm = campaignForm;
	}

	@ManyToOne()
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@ManyToOne()
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@ManyToOne()
	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	@Column
	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public CampaignFormDataReferenceDto toReference() {
		return new CampaignFormDataReferenceDto(getUuid());
	}
}
