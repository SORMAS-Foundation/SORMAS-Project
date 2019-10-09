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
package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.DistrictCriteria;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.FilteredGrid;

public class DistrictsGrid extends FilteredGrid<DistrictDto, DistrictCriteria> {

	private static final long serialVersionUID = -4437531618828715458L;

	public static final String EDIT_BTN_ID = "edit";
	
	public DistrictsGrid() {
		super(DistrictDto.class);
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);

		DataProvider<DistrictDto, DistrictCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
				query -> FacadeProvider.getDistrictFacade().getIndexList(
						query.getFilter().orElse(null), query.getOffset(), query.getLimit(),
						query.getSortOrders().stream().map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList())).stream(),
				query -> {
					return (int) FacadeProvider.getDistrictFacade().count(query.getFilter().orElse(null));
				});
		setDataProvider(dataProvider);
		
		setColumns(DistrictDto.NAME, DistrictDto.REGION, DistrictDto.EPID_CODE, DistrictDto.GROWTH_RATE);

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {
			Column<DistrictDto, String> editColumn = addColumn(entry -> VaadinIcons.EDIT.getHtml(), new HtmlRenderer());
			editColumn.setId(EDIT_BTN_ID);
			editColumn.setWidth(20);

			addItemClickListener(e -> {
				if (e.getColumn() != null && (EDIT_BTN_ID.equals(e.getColumn().getId()) || e.getMouseEventDetails().isDoubleClick())) {
					ControllerProvider.getInfrastructureController().editDistrict(e.getItem().getUuid());
				}
			});
		}	
		
		for(Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(
					DistrictDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}
	
}
