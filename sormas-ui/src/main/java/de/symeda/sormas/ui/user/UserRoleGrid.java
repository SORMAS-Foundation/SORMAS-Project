package de.symeda.sormas.ui.user;

import java.util.stream.Collectors;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.shared.data.sort.SortDirection;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRoleCriteria;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.utils.FilteredGrid;

public class UserRoleGrid extends FilteredGrid<UserRoleDto, UserRoleCriteria> {

	public UserRoleGrid() {
		super(UserRoleDto.class);
		setSizeFull();
		setLazyDataProvider();

		setColumns(UserRoleDto.CAPTION, UserRoleDto.JURISDICTION_LEVEL, UserRoleDto.DESCRIPTION);

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(UserRoleDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}
	}

	public void setLazyDataProvider() {
		DataProvider<UserRoleDto, UserRoleCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getUserRoleFacade()
				.getIndexList(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> (int) FacadeProvider.getUserRoleFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

}
