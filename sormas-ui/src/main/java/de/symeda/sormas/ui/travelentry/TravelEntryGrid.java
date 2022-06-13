package de.symeda.sormas.ui.travelentry;

import java.util.Date;
import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.campaign.campaigns.CampaignsView;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class TravelEntryGrid extends FilteredGrid<TravelEntryIndexDto, TravelEntryCriteria> {

	public TravelEntryGrid(TravelEntryCriteria criteria) {
		super(TravelEntryIndexDto.class);
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

		setCriteria(criteria);
		initColumns();
		addItemClickListener(
			new ShowDetailsListener<>(
				TravelEntryIndexDto.UUID,
				e -> ControllerProvider.getTravelEntryController().navigateToTravelEntry(e.getUuid())));
	}

	private void initColumns() {
		setColumns(
			TravelEntryIndexDto.UUID,
			TravelEntryIndexDto.EXTERNAL_ID,
			TravelEntryIndexDto.PERSON_FIRST_NAME,
			TravelEntryIndexDto.PERSON_LAST_NAME,
			TravelEntryIndexDto.HOME_DISTRICT_NAME,
			TravelEntryIndexDto.POINT_OF_ENTRY_NAME,
			TravelEntryIndexDto.RECOVERED,
			TravelEntryIndexDto.VACCINATED,
			TravelEntryIndexDto.TESTED_NEGATIVE,
			TravelEntryIndexDto.QUARANTINE_TO);

		((Column<TravelEntryIndexDto, String>) getColumn(TravelEntryIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<TravelEntryIndexDto, Boolean>) getColumn(TravelEntryIndexDto.RECOVERED)).setRenderer(new BooleanRenderer());
		((Column<TravelEntryIndexDto, Boolean>) getColumn(TravelEntryIndexDto.VACCINATED)).setRenderer(new BooleanRenderer());
		((Column<TravelEntryIndexDto, Boolean>) getColumn(TravelEntryIndexDto.TESTED_NEGATIVE)).setRenderer(new BooleanRenderer());
		((Column<TravelEntryIndexDto, Date>) getColumn(TravelEntryIndexDto.QUARANTINE_TO))
			.setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));

		for (Column<TravelEntryIndexDto, ?> column : getColumns()) {
			column.setCaption(
				I18nProperties
					.findPrefixCaptionWithDefault(column.getId(), column.getCaption(), TravelEntryIndexDto.I18N_PREFIX, PersonDto.I18N_PREFIX));
			column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(getBeanType(), column.getId()));
		}
	}

	public void setLazyDataProvider() {
		DataProvider<TravelEntryIndexDto, TravelEntryCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getTravelEntryFacade()
				.getIndexList(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> (int) FacadeProvider.getTravelEntryFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}


	public void setEagerDataProvider() {
		ListDataProvider<TravelEntryIndexDto> dataProvider =
				DataProvider.fromStream(FacadeProvider.getTravelEntryFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}

	public void reload() {
		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		ViewConfiguration viewConfiguration = ViewModelProviders.of(TravelEntriesView.class).get(ViewConfiguration.class);
		if (viewConfiguration.isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}
}
