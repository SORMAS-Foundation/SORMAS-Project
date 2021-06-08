/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.sormastosormas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasPersonPreview;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class ContactsPreviewGrid extends Grid<SormasToSormasContactPreview> {

	private static final long serialVersionUID = -1209058174602542785L;

	public ContactsPreviewGrid(List<SormasToSormasContactPreview> contacts) {
		super(SormasToSormasContactPreview.class);
		setItems(contacts);

		buildGrid();
	}

	private void buildGrid() {

		setSizeFull();
		setSelectionMode(Grid.SelectionMode.SINGLE);
		setHeightMode(HeightMode.ROW);

		Language userLanguage = I18nProperties.getUserLanguage();

		removeColumn(SormasToSormasContactPreview.DISEASE);
		addComponentColumn(eventPreview -> {
			String diseaseText = eventPreview.getDisease().toString();
			if (!StringUtils.isEmpty(eventPreview.getDiseaseDetails())) {
				diseaseText += " - " + eventPreview.getDiseaseDetails();
			}

			return new Label(diseaseText);
		}).setId(SormasToSormasContactPreview.DISEASE);

		List<String> columnConfig = new ArrayList<>();
		columnConfig.addAll(
			Arrays.asList(
				SormasToSormasContactPreview.UUID,
				SormasToSormasContactPreview.REPORT_DATE_TIME,
				SormasToSormasContactPreview.DISEASE,
				SormasToSormasContactPreview.CONTACT_STATUS,
				SormasToSormasContactPreview.CONTACT_CLASSIFICATION,
				SormasToSormasContactPreview.CONTACT_CATEGORY));
		columnConfig.addAll(PreviewGridHelper.createPersonColumns(this, SormasToSormasContactPreview::getPerson));
		columnConfig.addAll(
			Arrays.asList(
				SormasToSormasContactPreview.REGION,
				SormasToSormasContactPreview.DISTRICT,
				SormasToSormasContactPreview.COMMUNITY,
				SormasToSormasContactPreview.LAST_CONTACT_DATE));

		setColumns(columnConfig);

		((Column<SormasToSormasContactPreview, String>) getColumn(SormasToSormasContactPreview.UUID)).setRenderer(new UuidRenderer());
		((Column<SormasToSormasContactPreview, Date>) getColumn(SormasToSormasContactPreview.REPORT_DATE_TIME))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<SormasToSormasContactPreview, Date>) getColumn(SormasToSormasContactPreview.LAST_CONTACT_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(
				I18nProperties.findPrefixCaption(column.getId(), SormasToSormasContactPreview.I18N_PREFIX, SormasToSormasPersonPreview.I18N_PREFIX));
		}
	}

	@Override
	public void setItems(Collection<SormasToSormasContactPreview> items) {
		super.setItems(items);
		setHeightByRows(items.size() > 0 ? (Math.min(items.size(), 10)) : 1);
	}
}
