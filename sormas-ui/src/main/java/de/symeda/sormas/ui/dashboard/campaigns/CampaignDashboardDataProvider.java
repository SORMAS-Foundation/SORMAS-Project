package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.symeda.sormas.api.campaign.CampaignJurisdictionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramCriteria;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramSeries;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.AreaReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class CampaignDashboardDataProvider {

	private CampaignReferenceDto campaign;
	private AreaReferenceDto area;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy;

	private final List<CampaignDashboardDiagramDto> campaignDashboardDiagrams = new ArrayList<>();
	private final Map<CampaignDashboardDiagramDto, List<CampaignDiagramDataDto>> campaignFormDataMap = new HashMap<>();
	private final Map<CampaignDashboardDiagramDto, Map<CampaignDashboardTotalsReference, Double>> campaignFormTotalsMap = new HashMap<>();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public CampaignReferenceDto getLastStartedCampaign() {
		return FacadeProvider.getCampaignFacade().getLastStartedCampaign();
	}

	public CampaignReferenceDto getCampaign() {
		return campaign;
	}

	public void setCampaign(CampaignReferenceDto campaign) {
		if (campaign != this.campaign) {
			campaignDashboardDiagrams.clear();
			this.campaign = campaign;
			requestDiagramsData();
		}
	}

	public AreaReferenceDto getArea() {
		return area;
	}

	public void setArea(AreaReferenceDto area) {
		if (this.area != area) {
			this.area = area;
			requestDiagramsData();
		}
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		if (this.region != region) {
			this.region = region;
			requestDiagramsData();
		}
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		if (this.district != district) {
			this.district = district;
			requestDiagramsData();
		}
	}

	public CampaignJurisdictionLevel getCampaignJurisdictionLevelGroupBy() {
		return campaignJurisdictionLevelGroupBy;
	}

	public void setCampaignJurisdictionLevelGroupBy(CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy) {
		if (this.campaignJurisdictionLevelGroupBy != campaignJurisdictionLevelGroupBy) {
			this.campaignJurisdictionLevelGroupBy = campaignJurisdictionLevelGroupBy;
			requestDiagramsData();
		}
	}

	protected void requestDiagramsData() {
		campaignFormDataMap.clear();
		campaignFormTotalsMap.clear();
	}

	public Map<CampaignDashboardDiagramDto, List<CampaignDiagramDataDto>> getCampaignFormDataMap(String tabId, String subTabId) {

		Predicate<Map.Entry<CampaignDashboardDiagramDto, List<CampaignDiagramDataDto>>> tabFilter =
				diagramPair -> diagramPair.getKey().getCampaignDashboardElement().getTabId() == tabId
						&& diagramPair.getKey().getCampaignDashboardElement().getSubTabId() == subTabId;

		boolean alreadyLoaded = campaignFormDataMap.entrySet().stream().anyMatch(tabFilter);

		if (!alreadyLoaded) {
			createDiagramsData(tabId, subTabId);
		}

		Map<CampaignDashboardDiagramDto, List<CampaignDiagramDataDto>> result = campaignFormDataMap.entrySet().stream()
				.filter(tabFilter)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		return result;
	}

	public Map<CampaignDashboardDiagramDto, Map<CampaignDashboardTotalsReference, Double>> getCampaignFormTotalsMap(String tabId, String subTabId) {

		Predicate<Map.Entry<CampaignDashboardDiagramDto, Map<CampaignDashboardTotalsReference, Double>>> tabFilter =
				diagramPair -> diagramPair.getKey().getCampaignDashboardElement().getTabId() == tabId
						&& diagramPair.getKey().getCampaignDashboardElement().getSubTabId() == subTabId;

		boolean alreadyLoaded = campaignFormTotalsMap.entrySet().stream().anyMatch(tabFilter);

		if (!alreadyLoaded) {
			createDiagramsData(tabId, subTabId);
		}

		Map<CampaignDashboardDiagramDto, Map<CampaignDashboardTotalsReference, Double>> result = campaignFormTotalsMap.entrySet().stream()
				.filter(tabFilter)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		return result;
	}

	public List<CampaignDashboardDiagramDto> getCampaignDashboardDiagrams()
	{
		if (campaignDashboardDiagrams.isEmpty()) {
			createCampaignDashboarDiagrams();
		}
		return campaignDashboardDiagrams;
	}

	private void createCampaignDashboarDiagrams() {

		campaignDashboardDiagrams.clear();

		if (campaign != null) {
			FacadeProvider.getCampaignFacade().validate(campaign);

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
							campaignDashboardDiagrams.add(new CampaignDashboardDiagramDto(campaignDashboardElement, campaignDiagramDefinitionDto));
						}
					});
		}
	}

	protected void createDiagramsData(String tabId, String subTabId) {

		getCampaignDashboardDiagrams().forEach(campaignDashboardDiagramDto -> {
			final CampaignDashboardElement campaignDashboardElement = campaignDashboardDiagramDto.getCampaignDashboardElement();
			if (campaignDashboardElement.getTabId().equals(tabId)
					&& (subTabId == null || campaignDashboardElement.getSubTabId().equals(subTabId))) {
				List<CampaignDiagramDataDto> diagramData = FacadeProvider.getCampaignFormDataFacade()
						.getDiagramData(
								campaignDashboardDiagramDto.getCampaignDiagramDefinitionDto().getCampaignDiagramSeries(),
								new CampaignDiagramCriteria(campaign, area, region, district, campaignJurisdictionLevelGroupBy));
				campaignFormDataMap.put(campaignDashboardDiagramDto, diagramData);
				List<CampaignDiagramSeries> campaignSeriesTotal =
						campaignDashboardDiagramDto.getCampaignDiagramDefinitionDto().getCampaignSeriesTotal();

				List<CampaignDiagramDataDto> percentageDiagramData = null;
				if (campaignSeriesTotal != null) {
					Optional populationGroup = campaignSeriesTotal.stream().filter(e -> Objects.nonNull(e.getPopulationGroup())).findFirst();
					Optional formIdOptional = campaignSeriesTotal.stream().filter(e -> Objects.nonNull(e.getFormId())).findFirst();
					{
						if (populationGroup.isPresent()) {
							percentageDiagramData = FacadeProvider.getCampaignFormDataFacade()
									.getDiagramDataByAgeGroup(
											(CampaignDiagramSeries) populationGroup.get(),
											campaignDashboardDiagramDto.getCampaignDiagramDefinitionDto().getCampaignDiagramSeries().get(0),
											new CampaignDiagramCriteria(campaign, area, region, district, campaignJurisdictionLevelGroupBy));
							if (formIdOptional.isPresent()) {
								logger.warn(String.format(I18nProperties.getString(Strings.errorFormIdPopulationAgeGroup)));
							}
						} else {
							percentageDiagramData = FacadeProvider.getCampaignFormDataFacade()
									.getDiagramData(
											campaignDashboardDiagramDto.getCampaignDiagramDefinitionDto().getCampaignSeriesTotal(),
											new CampaignDiagramCriteria(campaign, area, region, district, campaignJurisdictionLevelGroupBy));
						}
						Map<CampaignDashboardTotalsReference, Double> percentageMap = new HashMap<>();
						for (CampaignDiagramDataDto data : percentageDiagramData) {
							CampaignDashboardTotalsReference totals =
									new CampaignDashboardTotalsReference(data.getGroupingKey(), data.getStack());
							Double value = percentageMap.getOrDefault(totals, 0D);
							value += data.getValueSum().doubleValue();
							percentageMap.put(totals, value);
						}
						campaignFormTotalsMap.put(campaignDashboardDiagramDto, percentageMap);
					}
				}
			}
		});
	}

	public List<String> getTabIds() {
		if (campaign != null) {
			return getCampaignDashboardDiagrams()
				.stream()
				.map(cdd -> cdd.getCampaignDashboardElement().getTabId())
				.distinct()
				.collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	public List<String> getSubTabIds(String tabId) {
		if (campaign != null) {
			return getCampaignDashboardDiagrams()
				.stream()
				.filter(cdd -> cdd.getCampaignDashboardElement().getTabId().equals(tabId))
				.map(cdd -> cdd.getCampaignDashboardElement().getSubTabId())
				.distinct()
				.collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}
}
