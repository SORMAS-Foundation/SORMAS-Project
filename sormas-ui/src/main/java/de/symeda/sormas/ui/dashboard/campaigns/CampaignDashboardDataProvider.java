package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.region.AreaReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class CampaignDashboardDataProvider {

	private CampaignReferenceDto campaign;
	private AreaReferenceDto area;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;

	private Map<CampaignDiagramDefinitionDto, List<CampaignFormDataDto>> campaignFormDataMap;

	public void refreshData() {

		List<CampaignDiagramDefinitionDto> campaignDiagramDefinitions = FacadeProvider.getCampaignDiagramDefinitionFacade().getAll();
		campaignDiagramDefinitions.forEach(campaignDiagramDefinitionDto -> {
			final List<CampaignFormDataDto> campaignFormDataList = new ArrayList<>();
			campaignDiagramDefinitionDto.getCampaignDiagramSeriesList()
				.forEach(
					campaignDiagramSeries -> campaignFormDataList
						.add(FacadeProvider.getCampaignFormDataFacade().getCampaignFormDataByUuid(campaignDiagramSeries.getFormId())));
			campaignFormDataMap.put(campaignDiagramDefinitionDto, campaignFormDataList);
		});

	}

	public CampaignReferenceDto getCampaign() {
		return campaign;
	}

	public void setCampaign(CampaignReferenceDto campaign) {
		this.campaign = campaign;
	}

	public AreaReferenceDto getArea() {
		return area;
	}

	public void setArea(AreaReferenceDto area) {
		this.area = area;
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

	public Map<CampaignDiagramDefinitionDto, List<CampaignFormDataDto>> getCampaignFormDataMap() {
		return campaignFormDataMap;
	}
}
