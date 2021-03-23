package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.ContinentCriteria;
import de.symeda.sormas.api.region.ContinentIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class ContinentsGrid extends FilteredGrid<ContinentIndexDto, ContinentCriteria> {

	public ContinentsGrid(ContinentCriteria criteria) {
		super(ContinentIndexDto.class);

		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(ContinentsView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		setColumns(
			ContinentIndexDto.DISPLAY_NAME,
			ContinentIndexDto.EXTERNAL_ID,
			ContinentIndexDto.DEFAULT_NAME);
		getColumn(ContinentIndexDto.DEFAULT_NAME).setHidden(true);

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editContinent(e.getUuid()));
		}

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(ContinentIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {

		DataProvider<ContinentIndexDto, ContinentCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
				query -> FacadeProvider.getContinentFacade()
						.getIndexList(
								query.getFilter().orElse(null),
								query.getOffset(),
								query.getLimit(),
								query.getSortOrders()
										.stream()
										.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
										.collect(Collectors.toList()))
						.stream(),
				query -> (int) FacadeProvider.getContinentFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {

		ListDataProvider<ContinentIndexDto> dataProvider =
				DataProvider.fromStream(FacadeProvider.getContinentFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}
}
