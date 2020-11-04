package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramCriteria;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.region.AreaReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class CampaignDashboardDataProvider {

	private CampaignReferenceDto campaign;
	private AreaReferenceDto area;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;

	private final Map<CampaignDashboardDiagramDto, List<CampaignDiagramDataDto>> campaignFormDataMap = new HashMap<>();
	private final Map<CampaignDashboardDiagramDto, Map<CampaignDashboardTotalsReference, Double>> campaignFormTotalsMap = new HashMap<>();

	public void refreshData() {
		campaignFormDataMap.clear();
		campaignFormTotalsMap.clear();

		FacadeProvider.getCampaignFacade().validate(campaign);

		final List<CampaignDashboardElement> campaignDashboardElements =
			FacadeProvider.getCampaignFacade().getCampaignDashboardElements(campaign != null ? campaign.getUuid() : null);
		final List<CampaignDiagramDefinitionDto> campaignDiagramDefinitions = FacadeProvider.getCampaignDiagramDefinitionFacade().getAll();

		final List<CampaignDashboardDiagramDto> campaignDashboardDiagramDtos = new ArrayList<>();

		campaignDashboardElements.stream().sorted(Comparator.comparingInt(CampaignDashboardElement::getOrder)).forEach(campaignDashboardElement -> {
			final Optional<CampaignDiagramDefinitionDto> first = campaignDiagramDefinitions.stream()
				.filter(campaignDiagramDefinitionDto -> campaignDiagramDefinitionDto.getDiagramId().equals(campaignDashboardElement.getDiagramId()))
				.findFirst();
			if (first.isPresent()) {
				CampaignDiagramDefinitionDto campaignDiagramDefinitionDto = first.get();
				campaignDashboardDiagramDtos.add(new CampaignDashboardDiagramDto(campaignDashboardElement, campaignDiagramDefinitionDto));
			}
		});

		campaignDashboardDiagramDtos.forEach(campaignDashboardDiagramDto -> {
			List<CampaignDiagramDataDto> diagramData = FacadeProvider.getCampaignFormDataFacade()
				.getDiagramData(
					campaignDashboardDiagramDto.getCampaignDiagramDefinitionDto().getCampaignDiagramSeries(),
					new CampaignDiagramCriteria(campaign, area, region, district));
			campaignFormDataMap.put(campaignDashboardDiagramDto, diagramData);

			if (campaignDashboardDiagramDto.getCampaignDiagramDefinitionDto().getCampaignSeriesTotal() != null) {
				List<CampaignDiagramDataDto> percentageDiagramData = FacadeProvider.getCampaignFormDataFacade()
					.getDiagramData(
						campaignDashboardDiagramDto.getCampaignDiagramDefinitionDto().getCampaignSeriesTotal(),
						new CampaignDiagramCriteria(campaign, area, region, district));

				Map<CampaignDashboardTotalsReference, Double> percentageMap = new HashMap<>();
				for (CampaignDiagramDataDto data : percentageDiagramData) {
					CampaignDashboardTotalsReference totals = new CampaignDashboardTotalsReference(data.getGroupingKey(), data.getStack());
					Double value = percentageMap.getOrDefault(totals, 0D);
					value += data.getValueSum().doubleValue();
					percentageMap.put(totals, value);
				}
				campaignFormTotalsMap.put(campaignDashboardDiagramDto, percentageMap);
			}
		});
	}

	public CampaignReferenceDto getLastStartedCampaign() {
		return FacadeProvider.getCampaignFacade().getLastStartedCampaign();
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

	public Map<CampaignDashboardDiagramDto, List<CampaignDiagramDataDto>> getCampaignFormDataMap() {
		return campaignFormDataMap;
	}

	public Map<CampaignDashboardDiagramDto, Map<CampaignDashboardTotalsReference, Double>> getCampaignFormTotalsMap() {
		return campaignFormTotalsMap;
	}
}
