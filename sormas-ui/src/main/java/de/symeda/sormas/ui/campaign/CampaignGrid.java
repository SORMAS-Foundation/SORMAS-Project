package de.symeda.sormas.ui.campaign;

import java.util.Date;
import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.campaign.CampaignCriteria;
import de.symeda.sormas.api.campaign.CampaignIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class CampaignGrid extends FilteredGrid<CampaignIndexDto, CampaignCriteria> {

	private static final long serialVersionUID = -7922340233873282326L;

	@SuppressWarnings("unchecked")
	public CampaignGrid(CampaignCriteria criteria) {

		super(CampaignIndexDto.class);

		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(CampaignsView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode()) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		setColumns(CampaignIndexDto.UUID, CampaignIndexDto.NAME, CampaignIndexDto.START_DATE, CampaignIndexDto.END_DATE);
		Language userLanguage = I18nProperties.getUserLanguage();
		((Column<CampaignIndexDto, String>) getColumn(CampaignIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<CampaignIndexDto, Date>) getColumn(CampaignIndexDto.START_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(userLanguage)));
		((Column<CampaignIndexDto, Date>) getColumn(CampaignIndexDto.END_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(userLanguage)));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(CampaignIndexDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}

		addItemClickListener(
			new ShowDetailsListener<>(CampaignIndexDto.UUID, e -> ControllerProvider.getCampaignController().createOrEdit(e.getUuid())));
	}

	public void setLazyDataProvider() {

		DataProvider<CampaignIndexDto, CampaignCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getCampaignFacade()
				.getIndexList(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> (int) FacadeProvider.getCampaignFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {

		ListDataProvider<CampaignIndexDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getCampaignFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}

	public void reload() {
		getDataProvider().refreshAll();
	}
}
