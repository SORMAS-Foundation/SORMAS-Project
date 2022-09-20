/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.campaign.statistics;

import java.io.Serializable;
import java.util.Objects;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;

@AuditedClass
public class CampaignStatisticsGroupingDto implements Serializable, Cloneable {

	@AuditInclude
	private String campaign;
	private String form;
	@AuditInclude
	private String area;
	@AuditInclude
	private String region;
	@AuditInclude
	private String district;
	@AuditInclude
	private String community;

	public CampaignStatisticsGroupingDto(String campaign, String form, String area, String region, String district, String community) {
		this.campaign = campaign;
		this.form = form;
		this.area = area;
		this.region = region;
		this.district = district;
		this.community = community;
	}

	public String getCampaign() {
		return campaign;
	}

	public String getForm() {
		return form;
	}

	public String getArea() {
		return area;
	}

	public String getRegion() {
		return region;
	}

	public String getDistrict() {
		return district;
	}

	public String getCommunity() {
		return community;
	}

	@Override
	public int hashCode() {
		return Objects.hash(campaign, form, area, region, district, community);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CampaignStatisticsGroupingDto)) {
			return false;
		}
		CampaignStatisticsGroupingDto campaignStatisticsGroupingDto = (CampaignStatisticsGroupingDto) o;
		return this.campaign.equals(campaignStatisticsGroupingDto.getCampaign())
			&& this.form.equals(campaignStatisticsGroupingDto.getForm())
			&& this.area.equals(campaignStatisticsGroupingDto.getArea())
			&& this.region.equals(campaignStatisticsGroupingDto.getRegion())
			&& this.district.equals(campaignStatisticsGroupingDto.getDistrict())
			&& this.community.equals(campaignStatisticsGroupingDto.getCommunity());
	}
}
