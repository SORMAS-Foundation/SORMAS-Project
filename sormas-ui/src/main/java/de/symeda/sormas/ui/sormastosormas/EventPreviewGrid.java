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

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasEventPreview;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasPersonPreview;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.LocationHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class EventPreviewGrid extends BasePreviewGrid<SormasToSormasEventPreview> {

	private static final long serialVersionUID = -5660374853463456219L;

	public EventPreviewGrid(List<SormasToSormasEventPreview> event, boolean isPendingRequest) {
		super(
			SormasToSormasEventPreview.class,
			Captions.Event,
			uuid -> FacadeProvider.getEventFacade().exists(uuid),
			uuid -> ControllerProvider.getEventController().navigateToData(uuid),
			isPendingRequest);

		setItems(event);
	}

	@Override
	protected void buildGrid() {

		Language userLanguage = I18nProperties.getUserLanguage();

		removeColumn(SormasToSormasEventPreview.DISEASE);
		addComponentColumn(casePreview -> {
			String diseaseText;
			if (casePreview.getDisease() == null) {
				diseaseText = "";
			} else {
				diseaseText = casePreview.getDisease().toString();
				if (!StringUtils.isEmpty(casePreview.getDiseaseDetails())) {
					diseaseText += " - " + casePreview.getDiseaseDetails();
				}
			}
			return new Label(diseaseText);
		}).setId(SormasToSormasEventPreview.DISEASE);

		removeColumn(SormasToSormasEventPreview.EVENT_LOCATION);
		addComponentColumn(previewData -> new Label(LocationHelper.buildLocationString(previewData.getEventLocation())))
			.setId(SormasToSormasEventPreview.EVENT_LOCATION);

		setColumns(
			SormasToSormasEventPreview.UUID,
			SormasToSormasEventPreview.REPORT_DATE_TIME,
			SormasToSormasEventPreview.EVENT_TITLE,
			SormasToSormasEventPreview.EVENT_DESC,
			SormasToSormasEventPreview.DISEASE,
			SormasToSormasEventPreview.EVENT_LOCATION);

		((Column<SormasToSormasEventPreview, String>) getColumn(SormasToSormasEventPreview.UUID)).setRenderer(new UuidRenderer());
		((Column<SormasToSormasEventPreview, Date>) getColumn(SormasToSormasEventPreview.REPORT_DATE_TIME))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(
				I18nProperties.findPrefixCaption(column.getId(), SormasToSormasEventPreview.I18N_PREFIX, SormasToSormasPersonPreview.I18N_PREFIX));
		}
	}
}
