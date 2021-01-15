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

package de.symeda.sormas.api.campaign.data;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

import java.io.Serializable;

public class CampaignFormDataCriteria extends BaseCriteria implements Serializable {

	public static final String CAMPAIGN = "campaign";
	public static final String CAMPAIGN_FORM = "campaignForm";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";

	private static final long serialVersionUID = 8124072093160133408L;

	private CampaignReferenceDto campaign;
	private CampaignFormReferenceDto campaignForm;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;

	public CampaignReferenceDto getCampaign() {
		return campaign;
	}

	public void setCampaign(CampaignReferenceDto campaign) {
		this.campaign = campaign;
	}

	public CampaignFormDataCriteria campaign(CampaignReferenceDto campaign) {
		this.campaign = campaign;
		return this;
	}

	public CampaignFormReferenceDto getCampaignForm() {
		return campaignForm;
	}

	public void setCampaignForm(CampaignFormReferenceDto campaignForm) {
		this.campaignForm = campaignForm;
	}

	public CampaignFormDataCriteria campaignForm(CampaignFormReferenceDto campaignForm) {
		this.campaignForm = campaignForm;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public CampaignFormDataCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public CampaignFormDataCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public CampaignFormDataCriteria community(CommunityReferenceDto community) {
		this.community = community;
		return this;
	}

}
