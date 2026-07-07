/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.configuration.customizablefield;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataCriteria;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;

/**
 * Grid for displaying and managing customizable field metadata.
 */
@SuppressWarnings({
	"java:S110", // suppress sonar too many parents warning
	"java:S2160" // suppress missing equals
})
public class CustomizableFieldsGrid extends FilteredGrid<CustomizableFieldMetadataDto, CustomizableFieldMetadataCriteria> {

	private static final long serialVersionUID = 1L;

	private static final String CLONE_COLUMN_ID = "clone";
	private static final String TOGGLE_ACTIVE_COLUMN_ID = "toggleActive";
	private static final String DELETE_COLUMN_ID = "delete";

	public CustomizableFieldsGrid(CustomizableFieldMetadataCriteria criteria) {

		super(CustomizableFieldMetadataDto.class);
		setSizeFull();

		setLazyDataProvider(
			FacadeProvider.getCustomizableFieldMetadataFacade()::getIndexList,
			FacadeProvider.getCustomizableFieldMetadataFacade()::count);
		setCriteria(criteria);

		setColumns(
			CustomizableFieldMetadataDto.NAME,
			CustomizableFieldMetadataDto.CONTEXT_CLASS,
			CustomizableFieldMetadataDto.UI_GROUP,
			CustomizableFieldMetadataDto.FIELD_TYPE,
			CustomizableFieldMetadataDto.ACTIVE,
			CustomizableFieldMetadataDto.READ_ONLY);

		((Column<CustomizableFieldMetadataDto, Boolean>) getColumn(CustomizableFieldMetadataDto.READ_ONLY)).setRenderer(new BooleanRenderer());
		((Column<CustomizableFieldMetadataDto, Boolean>) getColumn(CustomizableFieldMetadataDto.ACTIVE)).setRenderer(new BooleanRenderer());

		addEditColumn(e -> ControllerProvider.getCustomizableFieldsController().editField(e.getUuid()));

		addColumn(e -> VaadinIcons.COPY.getHtml(), new HtmlRenderer()).setId(CLONE_COLUMN_ID)
			.setCaption(I18nProperties.getCaption(Captions.actionClone))
			.setSortable(false)
			.setWidth(40);

		addColumn(e -> e.isActive() ? VaadinIcons.CLOSE.getHtml() : VaadinIcons.CHECK.getHtml(), new HtmlRenderer()).setId(TOGGLE_ACTIVE_COLUMN_ID)
			.setCaption(I18nProperties.getCaption(Captions.actionEnable) + "/" + I18nProperties.getCaption(Captions.actionDisable))
			.setSortable(false)
			.setWidth(55);

		addColumn(e -> VaadinIcons.TRASH.getHtml(), new HtmlRenderer()).setId(DELETE_COLUMN_ID)
			.setCaption(I18nProperties.getCaption(Captions.actionDelete))
			.setSortable(false)
			.setWidth(40);

		addItemClickListener(
			new ShowDetailsListener<>(CLONE_COLUMN_ID, false, e -> ControllerProvider.getCustomizableFieldsController().cloneField(e)));
		addItemClickListener(
			new ShowDetailsListener<>(TOGGLE_ACTIVE_COLUMN_ID, false, e -> ControllerProvider.getCustomizableFieldsController().toggleActive(e)));
		addItemClickListener(
			new ShowDetailsListener<>(
				DELETE_COLUMN_ID,
				false,
				e -> ControllerProvider.getCustomizableFieldsController().deleteField(e.getUuid(), this)));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(CustomizableFieldMetadataDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}
}
