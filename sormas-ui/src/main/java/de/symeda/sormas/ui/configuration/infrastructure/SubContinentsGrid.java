package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.SubContinentCriteria;
import de.symeda.sormas.api.region.SubContinentIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class SubContinentsGrid extends FilteredGrid<SubContinentIndexDto, SubContinentCriteria> {

	public SubContinentsGrid(SubContinentCriteria criteria) {
		super(SubContinentIndexDto.class);

		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(SubContinentsView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		setColumns(
			SubContinentIndexDto.DISPLAY_NAME,
			SubContinentIndexDto.CONTINENT,
			SubContinentIndexDto.EXTERNAL_ID,
			SubContinentIndexDto.DEFAULT_NAME);
		getColumn(SubContinentIndexDto.DEFAULT_NAME).setHidden(true);

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editSubContinent(e.getUuid()));
		}

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(SubContinentIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {

		DataProvider<SubContinentIndexDto, SubContinentCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getSubContinentFacade()
				.getIndexList(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> (int) FacadeProvider.getSubContinentFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {

		ListDataProvider<SubContinentIndexDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getSubContinentFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}
}
