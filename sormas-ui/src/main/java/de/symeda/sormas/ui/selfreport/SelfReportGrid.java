/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.selfreport;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.selfreport.SelfReportCriteria;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class SelfReportGrid extends FilteredGrid<SelfReportIndexDto, SelfReportCriteria> {

	private static final long serialVersionUID = 2818016255621440844L;

	public SelfReportGrid(SelfReportCriteria criteria, ViewConfiguration viewConfiguration) {
		super(SelfReportIndexDto.class);

		setSizeFull();

		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode()) {
			setCriteria(criteria);
			setEagerDataProvider(FacadeProvider.getSelfReportFacade()::getIndexList);
		} else {
			setLazyDataProvider(FacadeProvider.getSelfReportFacade()::getIndexList, FacadeProvider.getSelfReportFacade()::count);
			setCriteria(criteria);
		}

		initColumns();
	}

	protected void initColumns() {
		Column<SelfReportIndexDto, String> addressColumn = addColumn(
			entry -> Stream.of(entry.getStreet(), entry.getHouseNumber(), entry.getPostalCode(), entry.getCity())
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.joining(", ")));
		addressColumn.setId(SelfReportDto.ADDRESS);

		Column<SelfReportIndexDto, String> deleteColumn = addColumn(entry -> {
			if (entry.getDeletionReason() != null) {
				return entry.getDeletionReason() + (entry.getOtherDeletionReason() != null ? ": " + entry.getOtherDeletionReason() : "");
			} else {
				return "-";
			}
		});
		deleteColumn.setId(DELETE_REASON_COLUMN);
		deleteColumn.setSortable(false);
		deleteColumn.setCaption(I18nProperties.getCaption(Captions.deletionReason));

		setColumns(
			SelfReportIndexDto.UUID,
			SelfReportIndexDto.REPORT_DATE,
			SelfReportIndexDto.TYPE,
			SelfReportIndexDto.DISEASE,
			SelfReportIndexDto.FIRST_NAME,
			SelfReportIndexDto.LAST_NAME,
			SelfReportIndexDto.AGE_AND_BIRTH_DATE,
			SelfReportIndexDto.SEX,
			SelfReportIndexDto.DISTRICT,
			SelfReportDto.ADDRESS,
			SelfReportIndexDto.PHONE_NUMBER,
			SelfReportIndexDto.EMAIL,
			SelfReportIndexDto.RESPONSIBLE_USER,
			SelfReportIndexDto.PROCESSING_STATUS,
			DELETE_REASON_COLUMN);

		((Column<SelfReportIndexDto, String>) getColumn(SelfReportIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<SelfReportIndexDto, Date>) getColumn(SelfReportIndexDto.REPORT_DATE))
			.setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<SelfReportIndexDto, AgeAndBirthDateDto>) getColumn(SelfReportIndexDto.AGE_AND_BIRTH_DATE)).setRenderer(
			value -> value == null
				? ""
				: PersonHelper.getAgeAndBirthdateString(
					value.getAge(),
					value.getAgeType(),
					value.getDateOfBirthDD(),
					value.getDateOfBirthMM(),
					value.getDateOfBirthYYYY()),
			new TextRenderer());

		if (!UiUtil.permitted(UserRight.SELF_REPORT_DELETE)) {
			removeColumn(DELETE_REASON_COLUMN);
		}

		for (Column<SelfReportIndexDto, ?> column : getColumns()) {
			column.setCaption(I18nProperties.findPrefixCaptionWithDefault(column.getId(), column.getCaption(), SelfReportDto.I18N_PREFIX));
			column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(SelfReportIndexDto.class, column.getId()));
		}

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			getColumn(SelfReportIndexDto.DISTRICT).setHidden(true);
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}
}
