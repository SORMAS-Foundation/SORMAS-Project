package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

	private final Map<CampaignReferenceDto, List<CampaignDashboardDiagramDto>> campaignDiagramDefinitionsMap = new HashMap<>();

	private final Map<CampaignDashboardDiagramDto, List<CampaignDiagramDataDto>> campaignFormDataMap = new HashMap<>();
	private final Map<CampaignDashboardDiagramDto, Map<CampaignDashboardTotalsReference, Double>> campaignFormTotalsMap = new HashMap<>();

	public void refreshDashboardData() {

		if (campaign != null) {
			FacadeProvider.getCampaignFacade().validate(campaign);
			createCampaignDashboardDiagramDefinitionsMap();
		}
	}

	protected void refreshDiagramsData(String tabId, String subTabId) {
		campaignFormDataMap.clear();
		campaignFormTotalsMap.clear();

		campaignDiagramDefinitionsMap.get(campaign).forEach(campaignDashboardDiagramDto -> {
			final CampaignDashboardElement campaignDashboardElement = campaignDashboardDiagramDto.getCampaignDashboardElement();
			if (campaignDashboardElement.getTabId().equals(tabId) && (subTabId == null || campaignDashboardElement.getSubTabId().equals(subTabId))) {
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
			}
		});
	}

	private void createCampaignDashboardDiagramDefinitionsMap() {
		if (!campaignDiagramDefinitionsMap.containsKey(campaign)) {
			final List<CampaignDashboardDiagramDto> campaignDashboardDiagramDtos = new ArrayList<>();
			final List<CampaignDashboardElement> campaignDashboardElements =
				FacadeProvider.getCampaignFacade().getCampaignDashboardElements(campaign.getUuid());
			final List<CampaignDiagramDefinitionDto> campaignDiagramDefinitions = FacadeProvider.getCampaignDiagramDefinitionFacade().getAll();

			campaignDashboardElements.stream()
				.sorted(Comparator.comparingInt(CampaignDashboardElement::getOrder))
				.forEach(campaignDashboardElement -> {
					final Optional<CampaignDiagramDefinitionDto> first = campaignDiagramDefinitions.stream()
						.filter(
							campaignDiagramDefinitionDto -> campaignDiagramDefinitionDto.getDiagramId()
								.equals(campaignDashboardElement.getDiagramId()))
						.findFirst();
					if (first.isPresent()) {
						CampaignDiagramDefinitionDto campaignDiagramDefinitionDto = first.get();
						campaignDashboardDiagramDtos.add(new CampaignDashboardDiagramDto(campaignDashboardElement, campaignDiagramDefinitionDto));
					}
				});

			campaignDiagramDefinitionsMap.put(campaign, campaignDashboardDiagramDtos);
		}
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

	public List<String> getTabIds() {
		return campaignDiagramDefinitionsMap.get(campaign)
			.stream()
			.map(cdd -> cdd.getCampaignDashboardElement().getTabId())
			.distinct()
			.collect(Collectors.toList());
	}

	public List<String> getSubTabIds(String tabId) {
		return campaignDiagramDefinitionsMap.get(campaign)
			.stream()
			.filter(cdd -> cdd.getCampaignDashboardElement().getTabId().equals(tabId))
			.map(cdd -> cdd.getCampaignDashboardElement().getSubTabId())
			.distinct()
			.collect(Collectors.toList());
	}
}
