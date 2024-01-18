package de.symeda.sormas.ui.campaign.campaignstatistics;

import java.util.List;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignJurisdictionLevel;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsCriteria;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.FilteredGrid;

public class CampaignStatisticsGrid extends FilteredGrid<CampaignStatisticsDto, CampaignStatisticsCriteria> {

	public CampaignStatisticsGrid(CampaignStatisticsCriteria criteria) {
		super(CampaignStatisticsDto.class);
		setSizeFull();

		setInEagerMode(true);
		setCriteria(criteria);
		setDataProvider();

		addDefaultColumns();
		setColumnsVisibility(criteria.getGroupingLevel());
	}

	protected void addDefaultColumns() {
		setColumns(
			CampaignStatisticsDto.CAMPAIGN,
			CampaignStatisticsDto.FORM,
			CampaignStatisticsDto.AREA,
			CampaignStatisticsDto.REGION,
			CampaignStatisticsDto.DISTRICT,
			CampaignStatisticsDto.COMMUNITY,
			CampaignStatisticsDto.FORM_COUNT);

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(CampaignStatisticsDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	public void reload() {
		setItems(getGridData());
	}

	public void setDataProvider() {

		setDataProvider(getGridData().stream());
		setSelectionMode(SelectionMode.NONE);
	}

	public void setColumnsVisibility(CampaignJurisdictionLevel groupingLevel) {
		setAreaColumnVisible(
			!FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.HIDE_JURISDICTION_FIELDS)
				&& (CampaignJurisdictionLevel.AREA.equals(groupingLevel) || CampaignJurisdictionLevel.REGION.equals(groupingLevel)));
		setRegionColumnVisible(
			CampaignJurisdictionLevel.REGION.equals(groupingLevel)
				|| CampaignJurisdictionLevel.DISTRICT.equals(groupingLevel)
				|| CampaignJurisdictionLevel.COMMUNITY.equals(groupingLevel));
		setDistrictColumnVisible(
			CampaignJurisdictionLevel.DISTRICT.equals(groupingLevel) || CampaignJurisdictionLevel.COMMUNITY.equals(groupingLevel));
		setCommunityColumnVisible(CampaignJurisdictionLevel.COMMUNITY.equals(groupingLevel));
	}

	public void addCustomColumn(String property, String caption) {
		Column<CampaignStatisticsDto, Object> newColumn =
			addColumn(e -> e.getStatisticsData().stream().filter(v -> v.getId().equals(property)).findFirst().orElse(null));
		newColumn.setSortable(false);
		newColumn.setCaption(caption);
		newColumn.setId(property);
	}

	private void setAreaColumnVisible(boolean visible) {
		getColumn(CampaignStatisticsDto.AREA).setHidden(!visible);
	}

	private void setRegionColumnVisible(boolean visible) {
		getColumn(CampaignStatisticsDto.REGION).setHidden(!visible);
	}

	private void setDistrictColumnVisible(boolean visible) {
		getColumn(CampaignStatisticsDto.DISTRICT).setHidden(!visible);
	}

	private void setCommunityColumnVisible(boolean visible) {
		getColumn(CampaignStatisticsDto.COMMUNITY).setHidden(!visible);
	}

	private List<CampaignStatisticsDto> getGridData() {
		return FacadeProvider.getCampaignStatisticsFacade().getCampaignStatistics(getCriteria());
	}
}
