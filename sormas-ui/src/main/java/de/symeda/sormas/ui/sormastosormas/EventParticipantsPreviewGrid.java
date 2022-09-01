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
import java.util.Collections;
import java.util.List;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasEventParticipantPreview;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasPersonPreview;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class EventParticipantsPreviewGrid extends BasePreviewGrid<SormasToSormasEventParticipantPreview> {

	private static final long serialVersionUID = -6160313129696297694L;

	public EventParticipantsPreviewGrid(boolean isPendingRequest) {
		super(
			SormasToSormasEventParticipantPreview.class,
			Captions.EventParticipant,
			uuid -> FacadeProvider.getEventParticipantFacade().exists(uuid),
			uuid -> ControllerProvider.getEventParticipantController().navigateToData(uuid),
			isPendingRequest);

		setHeightByRows(1);
	}

	@Override
	protected void buildGrid() {

		List<String> columnConfig = new ArrayList<>();
		columnConfig.addAll(Collections.singletonList(SormasToSormasEventParticipantPreview.UUID));
		columnConfig.addAll(createPersonColumns(SormasToSormasEventParticipantPreview::getPerson));

		setColumns(columnConfig);

		((Column<SormasToSormasEventParticipantPreview, String>) getColumn(SormasToSormasEventParticipantPreview.UUID))
			.setRenderer(new UuidRenderer());

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(
				I18nProperties
					.findPrefixCaption(column.getId(), SormasToSormasEventParticipantPreview.I18N_PREFIX, SormasToSormasPersonPreview.I18N_PREFIX));
		}
	}
}
