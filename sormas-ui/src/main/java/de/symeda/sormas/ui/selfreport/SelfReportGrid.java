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

import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.selfreport.SelfReportCriteria;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportIndexDto;
import de.symeda.sormas.api.selfreport.SelfReportInvestigationStatus;
import de.symeda.sormas.api.selfreport.SelfReportProcessingStatus;
import de.symeda.sormas.api.selfreport.SelfReportType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class SelfReportGrid extends FilteredGrid<SelfReportIndexDto, SelfReportCriteria> {

	private static final long serialVersionUID = 2818016255621440844L;

	private static final String PROCESS_COLUMN = "process";
	private static final String PLACEHOLDER_SPACE = String.join("", Collections.nCopies(35, "&nbsp"));

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

		initColumns(criteria);

		addItemClickListener(
			new ShowDetailsListener<>(SelfReportIndexDto.UUID, e -> ControllerProvider.getSelfReportController().navigateToSelfReport(e.getUuid())));

		addItemClickListener(
			new ShowDetailsListener<>(SelfReportIndexDto.UUID, e -> ControllerProvider.getSelfReportController().navigateToSelfReport(e.getUuid())));
	}

	protected void initColumns(SelfReportCriteria criteria) {
		Column<SelfReportIndexDto, String> addressColumn = addColumn(
			entry -> Stream.of(entry.getStreet(), entry.getHouseNumber(), entry.getPostalCode(), entry.getCity())
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.joining(", ")));
		addressColumn.setId(SelfReportDto.ADDRESS);

		addComponentColumn(this::buildProcessColumn).setId(PROCESS_COLUMN).setSortable(false);

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
		deleteColumn.setHidden(!criteria.getRelevanceStatus().equals(EntityRelevanceStatus.DELETED));

		setColumns(
			SelfReportIndexDto.UUID,
			SelfReportIndexDto.REPORT_DATE,
			SelfReportIndexDto.TYPE,
			SelfReportIndexDto.DISEASE,
			SelfReportIndexDto.FIRST_NAME,
			SelfReportIndexDto.LAST_NAME,
			SelfReportIndexDto.BIRTH_DATE,
			SelfReportIndexDto.SEX,
			SelfReportIndexDto.DISTRICT,
			SelfReportDto.ADDRESS,
			SelfReportIndexDto.PHONE_NUMBER,
			SelfReportIndexDto.EMAIL,
			SelfReportIndexDto.RESPONSIBLE_USER,
			SelfReportIndexDto.PROCESSING_STATUS,
			PROCESS_COLUMN,
			DELETE_REASON_COLUMN);

		((Column<SelfReportIndexDto, String>) getColumn(SelfReportIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<SelfReportIndexDto, Date>) getColumn(SelfReportIndexDto.REPORT_DATE))
			.setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<SelfReportIndexDto, BirthDateDto>) getColumn(SelfReportIndexDto.BIRTH_DATE)).setRenderer(
			value -> value == null
				? ""
				: de.symeda.sormas.api.utils.DateFormatHelper
					.formatDate(value.getDateOfBirthDD(), value.getDateOfBirthMM(), value.getDateOfBirthYYYY()),
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

	private Component buildProcessColumn(SelfReportIndexDto indexDto) {
		if (UiUtil.permitted(UserRight.SELF_REPORT_PROCESS)
			&& indexDto.getInvestigationStatus() == SelfReportInvestigationStatus.COMPLETED
			&& indexDto.getProcessingStatus() != SelfReportProcessingStatus.PROCESSED
			&& ((indexDto.getType() == SelfReportType.CASE && UiUtil.permitted(UserRight.CASE_CREATE, UserRight.CASE_EDIT))
				|| (indexDto.getType() == SelfReportType.CONTACT && UiUtil.permitted(UserRight.CONTACT_CREATE, UserRight.CONTACT_EDIT)))) {
			// build process button
			return ButtonHelper.createButton(Captions.selfReportProcess, e -> {
				ControllerProvider.getSelfReportController().processSelfReport(indexDto.getUuid());
			}, ValoTheme.BUTTON_PRIMARY);
		} else {
			// build placeholder necessary to circumvent a vaadin scaling issue (see #7681)
			Label placeholder = new Label(PLACEHOLDER_SPACE);
			placeholder.setContentMode(ContentMode.HTML);
			return placeholder;
		}
	}

}
