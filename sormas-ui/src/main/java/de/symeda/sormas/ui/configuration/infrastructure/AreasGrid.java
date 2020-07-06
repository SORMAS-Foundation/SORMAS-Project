package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.AreaCriteria;
import de.symeda.sormas.api.region.AreaDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public class AreasGrid extends FilteredGrid<AreaDto, AreaCriteria> {

	public AreasGrid(AreaCriteria criteria) {
		super(AreaDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(AreasView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		setColumns(AreaDto.NAME, AreaDto.EXTERNAL_ID);

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editArea(e.getUuid()));
		}

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(AreaDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {
		DataProvider<AreaDto, AreaCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getAreaFacade()
				.getIndexList(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> {
				return (int) FacadeProvider.getAreaFacade().count(query.getFilter().orElse(null));
			});
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {
		ListDataProvider<AreaDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getAreaFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}

}
