package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.PointOfEntryCriteria;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class PointsOfEntryGrid extends FilteredGrid<PointOfEntryDto, PointOfEntryCriteria> {

	private static final long serialVersionUID = -650914769265620769L;

	public PointsOfEntryGrid(PointOfEntryCriteria criteria) {
		super(PointOfEntryDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(PointsOfEntryView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		setColumns(
			PointOfEntryDto.NAME,
			PointOfEntryDto.POINT_OF_ENTRY_TYPE,
			PointOfEntryDto.REGION,
			PointOfEntryDto.DISTRICT,
			PointOfEntryDto.LATITUDE,
			PointOfEntryDto.LONGITUDE,
			PointOfEntryDto.EXTERNAL_ID,
			PointOfEntryDto.ACTIVE);

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editPointOfEntry(e.getUuid()));
		}

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}

		getColumn(PointOfEntryDto.ACTIVE).setRenderer(new BooleanRenderer());
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {

		DataProvider<PointOfEntryDto, PointOfEntryCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getPointOfEntryFacade()
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
				return (int) FacadeProvider.getPointOfEntryFacade().count(query.getFilter().orElse(null));
			});
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {

		ListDataProvider<PointOfEntryDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getPointOfEntryFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}
}
