/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.configuration.customizableenums;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomizableEnumCriteria;
import de.symeda.sormas.api.customizableenum.CustomizableEnumValueDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumValueIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.FilteredGrid;

public class CustomizableEnumsGrid extends FilteredGrid<CustomizableEnumValueIndexDto, CustomizableEnumCriteria> {

	private static final long serialVersionUID = 8528080770989069026L;

	public CustomizableEnumsGrid(CustomizableEnumCriteria criteria) {

		super(CustomizableEnumValueIndexDto.class);
		setSizeFull();

		setLazyDataProvider(FacadeProvider.getCustomizableEnumFacade()::getIndexList, FacadeProvider.getCustomizableEnumFacade()::count);
		setCriteria(criteria);

		setColumns(
			CustomizableEnumValueIndexDto.DATA_TYPE,
			CustomizableEnumValueIndexDto.VALUE,
			CustomizableEnumValueIndexDto.CAPTION,
			CustomizableEnumValueIndexDto.DISEASES,
			CustomizableEnumValueIndexDto.PROPERTIES);

		Column<CustomizableEnumValueIndexDto, ?> diseaseColumn = getColumn(CustomizableEnumValueIndexDto.DISEASES);
		diseaseColumn.setRenderer(new CustomizableEnumsDiseasesRenderer());
		diseaseColumn.setSortable(false);
		diseaseColumn.setExpandRatio(1);
		Column<CustomizableEnumValueIndexDto, ?> propertiesColumn = getColumn(CustomizableEnumValueIndexDto.PROPERTIES);
		propertiesColumn.setRenderer(new CustomizableEnumsPropertiesRenderer());
		propertiesColumn.setSortable(false);
		propertiesColumn.setWidth(300);

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(CustomizableEnumValueDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}
}
