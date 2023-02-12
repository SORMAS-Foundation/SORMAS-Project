package de.symeda.sormas.ui.user;

import java.util.Set;
import java.util.stream.Collectors;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.HtmlRenderer;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.user.UserGrid.ActiveRenderer;
import de.symeda.sormas.ui.utils.CollectionValueProvider;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.ViewConfiguration;
import elemental.json.JsonValue;

public class UserExportGrid extends FilteredGrid<UserDto, UserCriteria> {

	private static final long serialVersionUID = -1L;

	@SuppressWarnings("unchecked")
	public UserExportGrid() {
		super(UserDto.class);
		setSizeFull();

		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(getCriteria());
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(getCriteria());
		}

		setColumns(UserDto.RCODE, UserDto.PCODE, UserDto.DCODE, UserDto.FIRST_NAME, UserDto.LAST_NAME,
				UserDto.USER_EMAIL, UserDto.PHONE, UserDto.USER_POSITION, UserDto.USER_ORGANISATION, UserDto.USER_NAME,
				UserDto.FORM_ACCESS, UserDto.USER_ROLES, UserDto.COMMUNITY_NOS);

		((Column<UserDto, Set<UserRole>>) getColumn(UserDto.USER_ROLES))
				.setRenderer(new CollectionValueProvider<Set<UserRole>>(), new HtmlRenderer());
		((Column<UserDto, Set<UserRole>>) getColumn(UserDto.USER_ROLES)).setSortable(false);


//		((Column<UserDto, Set<UserRole>>) getColumn(UserDto.COMMUNITY))
//				.setRenderer(new CollectionValueProvider<Set<UserRole>>(), new HtmlRenderer());
//		((Column<UserDto, Set<UserRole>>) getColumn(UserDto.COMMUNITY)).setSortable(false);


		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(UserDto.I18N_PREFIX, column.getId().toString(),
					column.getCaption()));
		}
	}

	public void setLazyDataProvider() {

		System.out.println("sdafasdfasddfgsdfhsdfg");
		DataProvider<UserDto, UserCriteria> dataProvider = DataProvider
				.fromFilteringCallbacks(query -> FacadeProvider.getUserFacade()
						.getIndexList(query.getFilter().orElse(null), query.getOffset(), query.getLimit(),
								query.getSortOrders().stream()
										.map(sortOrder -> new SortProperty(sortOrder.getSorted(),
												sortOrder.getDirection() == SortDirection.ASCENDING))
										.collect(Collectors.toList()))
						.stream(), query -> {
							return (int) FacadeProvider.getUserFacade().count(query.getFilter().orElse(null));
						});
		System.out.println("sdafasdfasdfgasdgvasdfgsdfhsdfg " + dataProvider);
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {
		ListDataProvider<UserDto> dataProvider = null;

		if (UserProvider.getCurrent().hasUserRole(UserRole.ADMIN_SUPERVISOR)) {
			dataProvider = DataProvider
					.fromStream(FacadeProvider.getUserFacade().getIndexList(getCriteria(), null, null, null).stream()
							.filter(e -> e.getDistrict().equals(UserProvider.getCurrent().getUser().getDistrict())));
		} else if (UserProvider.getCurrent().hasUserRole(UserRole.ADMIN_SUPERVISOR)) {
			dataProvider = DataProvider.fromStream(
					FacadeProvider.getUserFacade().getIndexList(getCriteria(), null, null, null).stream().filter(null));
		} else {
			dataProvider = DataProvider
					.fromStream(FacadeProvider.getUserFacade().getIndexList(getCriteria(), null, null, null).stream()
							.filter(e -> e.getDistrict().equals(UserProvider.getCurrent().getUser().getDistrict())));
		}
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}

	public void reload() {

		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		ViewConfiguration viewConfiguration = ViewModelProviders.of(UsersView.class).get(ViewConfiguration.class);
		if (viewConfiguration.isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}

	public static class ActiveRenderer extends HtmlRenderer {

		@Override
		public JsonValue encode(String value) {
			String iconValue = VaadinIcons.CHECK_SQUARE_O.getHtml();
			if (!Boolean.parseBoolean(value)) {
				iconValue = VaadinIcons.THIN_SQUARE.getHtml();
			}
			return super.encode(iconValue);
		}
	}

}
