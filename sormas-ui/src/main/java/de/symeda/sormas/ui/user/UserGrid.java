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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.user;

import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CollectionToStringConverter;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.UuidRenderer;
import elemental.json.JsonValue;

@SuppressWarnings("serial")
public class UserGrid extends FilteredGrid<UserDto, UserCriteria> {

	private static final long serialVersionUID = -1L;

	private static final String EDIT_BTN_ID = "edit";

	public static final String USER_ROLES_COLUMN = Captions.columnUserRoles;
	public static final String ACTIVE_COLUMN = Captions.columnUserActive;

	@SuppressWarnings("unchecked")
	public UserGrid() {
		super(UserDto.class);
		setSizeFull();

		setSelectionMode(SelectionMode.NONE);

		DataProvider<UserDto, UserCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
				query -> FacadeProvider.getUserFacade().getIndexList(
						query.getFilter().orElse(null), query.getOffset(), query.getLimit(), 
						query.getSortOrders().stream().map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList())).stream(),
				query -> {
					return (int) FacadeProvider.getUserFacade().count(query.getFilter().orElse(null));
				});
		setDataProvider(dataProvider);

		Column<UserDto, String> editColumn = addColumn(entry -> VaadinIcons.EDIT.getHtml(), new HtmlRenderer());
		editColumn.setId(EDIT_BTN_ID);
		editColumn.setWidth(60);

		Column<UserDto, String> userRolesColumn = addColumn(entry -> new CollectionToStringConverter().convertToPresentation(entry.getUserRoles(), String.class, null));
		userRolesColumn.setId(USER_ROLES_COLUMN);
		userRolesColumn.setSortable(false);
		
		Column<UserDto, String> userActiveColumn = addColumn(entry -> String.valueOf(entry.isActive()), new ActiveRenderer());
		userActiveColumn.setId(ACTIVE_COLUMN);
		userActiveColumn.setSortProperty(UserDto.ACTIVE);

		setColumns(EDIT_BTN_ID, UserDto.UUID, ACTIVE_COLUMN, USER_ROLES_COLUMN, UserDto.USER_NAME, 
				UserDto.NAME, UserDto.USER_EMAIL, UserDto.ADDRESS, UserDto.DISTRICT);

		((Column<UserDto, String>) getColumn(UserDto.UUID)).setRenderer(new UuidRenderer());

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(
					UserDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}

		addItemClickListener(e ->  {
			if ((e.getColumn() != null && EDIT_BTN_ID.equals(e.getColumn().getId()))
					|| e.getMouseEventDetails().isDoubleClick()) {
				ControllerProvider.getUserController().edit(e.getItem());
			}
		});
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	public static class ActiveRenderer extends HtmlRenderer {
		
		@Override
        public JsonValue encode(String value) {
        	String iconValue = VaadinIcons.CHECK_SQUARE_O.getHtml();
        	if(!Boolean.parseBoolean(value)) {
        		iconValue = VaadinIcons.THIN_SQUARE.getHtml();
        	}
            return super.encode(iconValue);
        }

	}

}


