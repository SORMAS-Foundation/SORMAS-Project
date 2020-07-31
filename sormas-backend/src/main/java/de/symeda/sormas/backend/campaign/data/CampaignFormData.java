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
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity(name = "campaignFormData")
@Audited
public class CampaignFormData extends AbstractDomainObject {

	public static final String TABLE_NAME = "campaignFormData";

	public static final String FORM_VALUES = "formValues";
	public static final String CAMPAIGN = "campaign";
	public static final String CAMPAIGN_FORM_META = "campaignFormMeta";
	public static final String FORM_DATE = "formDate";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String ARCHIVED = "archived";

	private static final long serialVersionUID = -8021065433714419288L;

	private List<CampaignFormDataEntry> formValuesList;
	private String formValues;
	private Campaign campaign;
	private CampaignFormMeta campaignFormMeta;
	private Date formDate;
	private Region region;
	private District district;
	private Community community;
	private User creatingUser;
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
	public List<CampaignFormDataEntry> getFormValuesList() {
		if (formValuesList == null) {
			if (StringUtils.isBlank(formValues)) {
				formValuesList = new ArrayList<>();
			} else {
				try {
					ObjectMapper mapper = new ObjectMapper();
					formValuesList = Arrays.asList(mapper.readValue(formValues, CampaignFormDataEntry[].class));
				} catch (IOException e) {
					throw new RuntimeException("Content of formValues could not be parsed to List<CampaignFormDataEntry> - ID: " + getId());
				}
			}
		}
		return formValuesList;
	}

	public void setFormValuesList(List<CampaignFormDataEntry> formValuesList) {
		this.formValuesList = formValuesList;

		if (this.formValuesList == null) {
			formValues = null;
			return;
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
	public CampaignFormMeta getCampaignFormMeta() {
		return campaignFormMeta;
	}

	public void setCampaignFormMeta(CampaignFormMeta campaignFormMeta) {
		this.campaignFormMeta = campaignFormMeta;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getFormDate() {
		return formDate;
	}

	public void setFormDate(Date formDate) {
		this.formDate = formDate;
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

	@ManyToOne
	@JoinColumn
	public User getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(User creatingUser) {
		this.creatingUser = creatingUser;
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
