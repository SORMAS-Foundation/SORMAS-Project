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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;

@AuditedClass
public class CampaignStatisticsDto implements Serializable, Cloneable {

	public static final String I18N_PREFIX = "CampaignStatistics";

	public static final String CAMPAIGN = "campaign";
	public static final String FORM = "form";
	public static final String AREA = "area";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String FORM_COUNT = "formCount";
	@AuditInclude
	private final CampaignStatisticsGroupingDto campaignStatisticsGroupingDto;
	private long formCount;
	@AuditInclude
	private List<CampaignFormDataEntry> statisticsData;

	public CampaignStatisticsDto(CampaignStatisticsGroupingDto campaignStatisticsGroupingDto, long formCount) {
		this.campaignStatisticsGroupingDto = campaignStatisticsGroupingDto;
		this.formCount = formCount;

		this.statisticsData = new ArrayList<>();
	}

	public String getCampaign() {
		return campaignStatisticsGroupingDto.getCampaign();
	}

	public String getForm() {
		return campaignStatisticsGroupingDto.getForm();
	}

	public String getArea() {
		return campaignStatisticsGroupingDto.getArea();
	}

	public String getRegion() {
		return campaignStatisticsGroupingDto.getRegion();
	}

	public String getDistrict() {
		return campaignStatisticsGroupingDto.getDistrict();
	}

	public String getCommunity() {
		return campaignStatisticsGroupingDto.getCommunity();
	}

	public long getFormCount() {
		return formCount;
	}

	public void setFormCount(long formCount) {
		this.formCount = formCount;
	}

	public List<CampaignFormDataEntry> getStatisticsData() {
		return statisticsData;
	}

	public void addStatisticsData(CampaignFormDataEntry value) {
		this.statisticsData.add(value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(campaignStatisticsGroupingDto, formCount);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CampaignStatisticsDto)) {
			return false;
		}
		CampaignStatisticsDto campaignStatisticsDto = (CampaignStatisticsDto) o;
		return this.campaignStatisticsGroupingDto.equals(campaignStatisticsDto.campaignStatisticsGroupingDto)
			&& this.formCount == campaignStatisticsDto.formCount;
	}
}
