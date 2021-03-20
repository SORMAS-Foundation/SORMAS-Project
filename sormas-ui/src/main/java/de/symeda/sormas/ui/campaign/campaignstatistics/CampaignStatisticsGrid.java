package de.symeda.sormas.ui.campaign.campaignstatistics;

import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.shared.data.sort.SortDirection;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.statistics.CampaignStatisticsIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.utils.FilteredGrid;

public class CampaignStatisticsGrid extends FilteredGrid<CampaignStatisticsIndexDto, CampaignFormDataCriteria> {

	public CampaignStatisticsGrid(CampaignFormDataCriteria criteria) {
		super(CampaignStatisticsIndexDto.class);
		setSizeFull();

		setDataProvider();
		setCriteria(criteria);

		addDefaultColumns();
	}

	protected void addDefaultColumns() {
		setColumns(
			CampaignStatisticsIndexDto.CAMPAIGN,
			CampaignStatisticsIndexDto.FORM,
			CampaignStatisticsIndexDto.REGION,
			CampaignStatisticsIndexDto.DISTRICT,
			CampaignStatisticsIndexDto.COMMUNITY);

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(CampaignStatisticsIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	public void setDataProvider() {
		DataProvider<CampaignStatisticsIndexDto, CampaignFormDataCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getCampaignStatisticsFacade()
				.getIndexList(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> (int) FacadeProvider.getCampaignStatisticsFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}
}
