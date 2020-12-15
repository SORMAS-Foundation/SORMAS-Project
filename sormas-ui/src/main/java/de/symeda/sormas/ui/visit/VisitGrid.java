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
package de.symeda.sormas.ui.visit;

import java.util.Date;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.api.visit.VisitIndexDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;

@SuppressWarnings("serial")
public class VisitGrid extends FilteredGrid<VisitIndexDto, VisitCriteria> {

	private static final String EDIT_BTN_ID = "edit";

	@SuppressWarnings("unchecked")
	public VisitGrid(VisitCriteria criteria) {
		super(VisitIndexDto.class);
		setSizeFull();

		setInEagerMode(true);
		setCriteria(criteria);
		setEagerDataProvider();

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setSelectionMode(SelectionMode.MULTI);
		} else {
			setSelectionMode(SelectionMode.NONE);
		}

		addEditColumn(
			e -> ControllerProvider.getVisitController().editVisit(e.getUuid(), getCriteria().getContact(), getCriteria().getCaze(), r -> reload()));

		setColumns(
			EDIT_BTN_ID,
			VisitIndexDto.VISIT_DATE_TIME,
			VisitIndexDto.VISIT_STATUS,
			VisitIndexDto.VISIT_REMARKS,
			VisitIndexDto.DISEASE,
			VisitIndexDto.SYMPTOMATIC,
			VisitIndexDto.TEMPERATURE,
			VisitIndexDto.ORIGIN);

		((Column<VisitIndexDto, Date>) getColumn(VisitIndexDto.VISIT_DATE_TIME))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(I18nProperties.getUserLanguage())));
		((Column<VisitIndexDto, String>) getColumn(VisitIndexDto.SYMPTOMATIC)).setRenderer(new BooleanRenderer());

		for (Column<VisitIndexDto, ?> column : getColumns()) {
			final String columnId = column.getId();
			final String i18nPrefix = columnId.equals(VisitIndexDto.SYMPTOMATIC) || columnId.equals(VisitIndexDto.TEMPERATURE)
				? SymptomsDto.I18N_PREFIX
				: VisitIndexDto.I18N_PREFIX;
			column.setCaption(I18nProperties.getPrefixCaption(i18nPrefix, columnId, column.getCaption()));
			column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(getBeanType(), columnId));
		}
	}

	public void setEagerDataProvider() {
		ListDataProvider<VisitIndexDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getVisitFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
	}

	public void reload() {
		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		//getDataProvider().refreshAll(); // does not work for eager data providers
		setEagerDataProvider();
	}
}
