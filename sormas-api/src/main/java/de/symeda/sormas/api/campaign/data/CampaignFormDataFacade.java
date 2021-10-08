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

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.campaign.diagram.CampaignDiagramCriteria;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramSeries;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface CampaignFormDataFacade {

	CampaignFormDataDto saveCampaignFormData(@Valid CampaignFormDataDto dto);

	List<CampaignFormDataDto> getByUuids(List<String> uuids);

	CampaignFormDataDto getCampaignFormDataByUuid(String campaignFormDataUuid);

	void deleteCampaignFormData(String campaignFormDataUuid);

	boolean isArchived(String campaignFormDataUuid);

	boolean exists(String uuid);

	CampaignFormDataReferenceDto getReferenceByUuid(String uuid);

	List<CampaignFormDataIndexDto> getIndexList(CampaignFormDataCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	CampaignFormDataDto getExistingData(CampaignFormDataCriteria criteria);

	long count(CampaignFormDataCriteria criteria);

	List<CampaignDiagramDataDto> getDiagramData(List<CampaignDiagramSeries> diagramSeries, CampaignDiagramCriteria campaignDiagramCriteria);

	List<CampaignDiagramDataDto> getDiagramDataByAgeGroup(
		CampaignDiagramSeries diagramSeriesTotal,
		CampaignDiagramSeries diagramSeries,
		CampaignDiagramCriteria campaignDiagramCriteria);

	List<String> getAllActiveUuids();

	List<CampaignFormDataDto> getAllActiveAfter(Date date);

	void overwriteCampaignFormData(CampaignFormDataDto existingData, CampaignFormDataDto newData);
}
