/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.user;

import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.CollectionValueProvider;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;
import elemental.json.JsonValue;

@SuppressWarnings("serial")
public class UserGrid extends FilteredGrid<UserDto, UserCriteria> {

	private static final long serialVersionUID = -1L;

	@SuppressWarnings("unchecked")
	public UserGrid() {
		super(UserDto.class);
		setSizeFull();

		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(getCriteria());
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(getCriteria());
		}
		
		

		//addEditColumn(e -> ControllerProvider.getUserController().edit(e));
		
		addItemClickListener(new ShowDetailsListener<>(UserDto.UUID, e -> ControllerProvider.getUserController().edit(e)));
		addItemClickListener(new ShowDetailsListener<>(UserDto.ACTIVE, e -> ControllerProvider.getUserController().edit(e)));
		addItemClickListener(new ShowDetailsListener<>(UserDto.USER_ROLES, e -> ControllerProvider.getUserController().edit(e)));
		addItemClickListener(new ShowDetailsListener<>(UserDto.USER_NAME, e -> ControllerProvider.getUserController().edit(e)));
		addItemClickListener(new ShowDetailsListener<>(UserDto.NAME, e -> ControllerProvider.getUserController().edit(e)));
		addItemClickListener(new ShowDetailsListener<>(UserDto.USER_EMAIL, e -> ControllerProvider.getUserController().edit(e)));
		addItemClickListener(new ShowDetailsListener<>(UserDto.ADDRESS, e -> ControllerProvider.getUserController().edit(e)));
		addItemClickListener(new ShowDetailsListener<>(UserDto.AREA, e -> ControllerProvider.getUserController().edit(e)));
		addItemClickListener(new ShowDetailsListener<>(UserDto.REGION, e -> ControllerProvider.getUserController().edit(e)));
		addItemClickListener(new ShowDetailsListener<>(UserDto.DISTRICT, e -> ControllerProvider.getUserController().edit(e)));
		addItemClickListener(new ShowDetailsListener<>(UserDto.HEALTH_FACILITY, e -> ControllerProvider.getUserController().edit(e)));
		
		
		//addItemClickListener(new ShowDetailsListener<>(EDIT_BTN_ID, e -> handler.accept(e)));   //addEditColumn
		

		setColumns(
		//	EDIT_BTN_ID,
			UserDto.UUID,
			UserDto.ACTIVE,
			UserDto.USER_ROLES,
			UserDto.USER_NAME,
			UserDto.NAME,
			UserDto.USER_EMAIL,
			UserDto.ADDRESS,
			UserDto.AREA,
			UserDto.REGION,
			UserDto.DISTRICT,
			UserDto.HEALTH_FACILITY);

		((Column<UserDto, String>) getColumn(UserDto.UUID)).setRenderer(new UuidRenderer());
		((Column<UserDto, Set<UserRole>>) getColumn(UserDto.USER_ROLES))
			.setRenderer(new CollectionValueProvider<Set<UserRole>>(), new HtmlRenderer());
		((Column<UserDto, Set<UserRole>>) getColumn(UserDto.USER_ROLES)).setSortable(false);
		((Column<UserDto, Boolean>) getColumn(UserDto.ACTIVE)).setRenderer(value -> String.valueOf(value), new ActiveRenderer());

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(UserDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}
	}

	public void setLazyDataProvider() {
		System.out.println("sdafasdfasddfgsdfhsdfg");
		DataProvider<UserDto, UserCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getUserFacade()
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
				return (int) FacadeProvider.getUserFacade().count(query.getFilter().orElse(null));
			});
		System.out.println("sdafasdfasdfgasdgvasdfgsdfhsdfg "+dataProvider);
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {
		ListDataProvider<UserDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getUserFacade().getIndexList(getCriteria(), null, null, null).stream());
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
