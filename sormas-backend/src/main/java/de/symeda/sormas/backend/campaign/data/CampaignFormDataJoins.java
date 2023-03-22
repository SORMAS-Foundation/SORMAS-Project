/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.campaign.data;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;

import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;

public class CampaignFormDataJoins extends QueryJoins<CampaignFormData> {

	private Join<CampaignFormData, Region> region;
	private Join<CampaignFormData, District> district;
	private Join<CampaignFormData, Community> community;
	private Join<CampaignFormData, User> creatingUser;

	public CampaignFormDataJoins(From<?, CampaignFormData> root) {
		super(root);
	}

	public Join<CampaignFormData, Region> getRegion() {
		return region;
	}

	public void setRegion(Join<CampaignFormData, Region> region) {
		this.region = region;
	}

	public Join<CampaignFormData, District> getDistrict() {
		return district;
	}

	public void setDistrict(Join<CampaignFormData, District> district) {
		this.district = district;
	}

	public Join<CampaignFormData, Community> getCommunity() {
		return community;
	}

	public void setCommunity(Join<CampaignFormData, Community> community) {
		this.community = community;
	}

	public Join<CampaignFormData, User> getCreatingUser() {
		return creatingUser;
	}

	public void setCreatingUser(Join<CampaignFormData, User> creatingUser) {
		this.creatingUser = creatingUser;
	}
}
