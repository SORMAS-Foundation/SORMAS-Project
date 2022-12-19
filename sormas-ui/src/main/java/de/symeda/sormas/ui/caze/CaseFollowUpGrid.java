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
package de.symeda.sormas.ui.caze;

import static de.symeda.sormas.ui.utils.FollowUpUtils.getVisitResultCssStyle;
import static de.symeda.sormas.ui.utils.FollowUpUtils.getVisitResultDescription;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.navigator.View;
import com.vaadin.ui.DescriptionGenerator;
import com.vaadin.ui.Label;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFollowUpDto;
import de.symeda.sormas.api.followup.FollowUpDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitResultDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class CaseFollowUpGrid extends FilteredGrid<CaseFollowUpDto, CaseCriteria> {

	private final List<Date> dates = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public <V extends View> CaseFollowUpGrid(CaseCriteria criteria, Class<V> viewClass) {

		super(CaseFollowUpDto.class);
		setSizeFull();

		setColumns(
			FollowUpDto.UUID,
			FollowUpDto.FIRST_NAME,
			FollowUpDto.LAST_NAME,
			FollowUpDto.REPORT_DATE,
			FollowUpDto.FOLLOW_UP_UNTIL,
			FollowUpDto.SYMPTOM_JOURNAL_STATUS);

		setVisitColumns(criteria);

		((Column<CaseFollowUpDto, String>) getColumn(CaseFollowUpDto.UUID)).setRenderer(new UuidRenderer());
		((Column<CaseFollowUpDto, Date>) getColumn(CaseFollowUpDto.REPORT_DATE)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<CaseFollowUpDto, Date>) getColumn(CaseFollowUpDto.FOLLOW_UP_UNTIL)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(
				I18nProperties.findPrefixCaptionWithDefault(column.getId(), column.getCaption(), CaseDataDto.I18N_PREFIX, FollowUpDto.I18N_PREFIX));
		}

		addItemClickListener(e -> {
			if ((e.getColumn() != null && CaseFollowUpDto.UUID.equals(e.getColumn().getId())) || e.getMouseEventDetails().isDoubleClick()) {
				ControllerProvider.getCaseController().navigateToCase(e.getItem().getUuid());
			}
		});
	}

	public void setVisitColumns(CaseCriteria criteria) {
		Date referenceDate = criteria.getFollowUpVisitsTo();
		int interval = criteria.getFollowUpVisitsInterval();

		setDataProvider(referenceDate, interval - 1);
		setCriteria(criteria);
		dates.forEach(date -> removeColumn(DateFormatHelper.formatDate(date)));

		setDates(referenceDate, interval);

		for (int i = 0; i < interval; i++) {
			String columnId = DateFormatHelper.formatDate(dates.get(i));
			addComponentColumn(followUpDto -> new Label("")).setId(columnId);

			final int index = i;
			getColumn(columnId).setCaption(columnId).setSortable(false).setStyleGenerator((StyleGenerator<CaseFollowUpDto>) item -> {
				final VisitResultDto visitResult = item.getVisitResults()[index];
				final Date date = dates.get(index);
				return getVisitResultCssStyle(visitResult, date, item.getReportDate(), item.getFollowUpUntil());
			}).setDescriptionGenerator((DescriptionGenerator<CaseFollowUpDto>) item -> {
				final VisitResultDto visitResult = item.getVisitResults()[index];
				final Date date = dates.get(index);
				return getVisitResultDescription(visitResult, date, item.getReportDate(), item.getFollowUpUntil());
			});
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	public void setDataProvider(Date referenceDate, int interval) {

		setLazyDataProvider(
			(criteria, first, max, sortProperties) -> FacadeProvider.getCaseFacade()
				.getCaseFollowUpList(criteria, referenceDate, interval, first, max, sortProperties),
			FacadeProvider.getCaseFacade()::count);
	}

	private void setDates(Date referenceDate, int interval) {

		dates.clear();
		for (int i = 0; i < interval; i++) {
			dates.add(DateHelper.subtractDays(referenceDate, interval - i - 1));
		}
	}
}
