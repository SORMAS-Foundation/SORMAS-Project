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

package de.symeda.sormas.api.campaign.data;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import io.swagger.v3.oas.annotations.media.Schema;

public class CampaignFormDataDto extends EntityDto {

	private static final long serialVersionUID = -8087195060395038093L;

	public static final String I18N_PREFIX = "CampaignFormData";

	public static final String CAMPAIGN = "campaign";
	public static final String CAMPAIGN_FORM_META = "campaignFormMeta";
	public static final String FORM_DATE = "formDate";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String CREATING_USER = "creatingUser";

	@Valid
	private List<CampaignFormDataEntry> formValues;
	@Schema(description = "Corresponding campaign")
	private CampaignReferenceDto campaign;
	@Valid
	@Schema(description = "Corresponding meta information")
	private CampaignFormMetaReferenceDto campaignFormMeta;
	@Schema(description = "Date and time when the campaign form was created")
	private Date formDate;
	@Schema(description = "Corresponding region")
	private RegionReferenceDto region;
	@Schema(description = "Corresponding district")
	private DistrictReferenceDto district;
	@Schema(description = "Corresponding community")
	private CommunityReferenceDto community;
	@Schema(description = "Reference to the user who created the form")
	private UserReferenceDto creatingUser;

	public static CampaignFormDataDto build(
		CampaignReferenceDto campaign,
		CampaignFormMetaReferenceDto campaignFormMeta,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		CommunityReferenceDto community) {
		CampaignFormDataDto campaignFormData = new CampaignFormDataDto();
		campaignFormData.setUuid(DataHelper.createUuid());
		campaignFormData.setCampaign(campaign);
		campaignFormData.setCampaignFormMeta(campaignFormMeta);
		campaignFormData.setRegion(region);
		campaignFormData.setDistrict(district);
		campaignFormData.setCommunity(community);
		campaignFormData.setFormDate(new Date());
		return campaignFormData;
	}

	public static CampaignFormDataDto build() {
		CampaignFormDataDto campaignFormData = new CampaignFormDataDto();
		campaignFormData.setUuid(DataHelper.createUuid());
		return campaignFormData;
	}

	public List<CampaignFormDataEntry> getFormValues() {
		return formValues;
	}

	public void setFormValues(List<CampaignFormDataEntry> formValues) {
		this.formValues = formValues;
	}

	@ImportIgnore
	public CampaignFormMetaReferenceDto getCampaignFormMeta() {
		return campaignFormMeta;
	}

	public void setCampaignFormMeta(CampaignFormMetaReferenceDto campaignFormMeta) {
		this.campaignFormMeta = campaignFormMeta;
	}

	@ImportIgnore
	public CampaignReferenceDto getCampaign() {
		return campaign;
	}

	public void setCampaign(CampaignReferenceDto campaign) {
		this.campaign = campaign;
	}

	public Date getFormDate() {
		return formDate;
	}

	public void setFormDate(Date formDate) {
		this.formDate = formDate;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public UserReferenceDto getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(UserReferenceDto creatingUser) {
		this.creatingUser = creatingUser;
	}

}
