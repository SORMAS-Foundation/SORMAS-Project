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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.form.CampaignForm;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

@Entity(name = "campaignFormData")
@Audited
public class CampaignFormData extends CoreAdo {

	public static final String TABLE_NAME = "campaignFormData";

	public static final String ARCHIVED = "archived";

	private String formData;

	private Campaign campaign;
	private CampaignForm campaignForm;
	private Region region;
	private District district;
	private Community community;

	@Lob
	public String getFormData() {
		return formData;
	}

	public void setFormData(String formData) {
		this.formData = formData;
	}

	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public CampaignForm getCampaignForm() {
		return campaignForm;
	}

	public void setCampaignForm(CampaignForm campaignForm) {
		this.campaignForm = campaignForm;
	}

	@ManyToOne(cascade = {})
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@ManyToOne(cascade = {})
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@ManyToOne(cascade = {})
	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}
}
