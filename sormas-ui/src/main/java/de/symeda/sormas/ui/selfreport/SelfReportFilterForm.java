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

import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.selfreport.SelfReportCriteria;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class SelfReportFilterForm extends AbstractFilterForm<SelfReportCriteria> {

	private static final long serialVersionUID = -8445740290879441416L;

	private static final String EPI_WEEK_AND_DATE_FILTER = "weekAndDateFilter";

	private static final String MORE_FILTERS_HTML_LAYOUT = loc(EPI_WEEK_AND_DATE_FILTER);

	protected SelfReportFilterForm() {
		super(SelfReportCriteria.class, SelfReportDto.I18N_PREFIX, null);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			SelfReportCriteria.FREE_TEXT,
			SelfReportCriteria.TYPE,
			SelfReportCriteria.DISEASE,
			SelfReportCriteria.DISEASE_VARIANT,
			SelfReportCriteria.REPORT_DATE };
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML_LAYOUT;
	}

	@Override
	protected void addFields() {

		addField(
			FieldConfiguration
				.withCaptionAndPixelSized(SelfReportCriteria.FREE_TEXT, I18nProperties.getString(Strings.promptSelfReportFreeTextSearch), 300),
			TextField.class);

		addField(FieldConfiguration.pixelSized(SelfReportCriteria.TYPE, 140));

		addField(FieldConfiguration.pixelSized(SelfReportCriteria.DISEASE, 140));

		addField(FieldConfiguration.pixelSized(SelfReportCriteria.DISEASE_VARIANT, 140), ComboBox.class);
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		switch (propertyId) {
		case SelfReportCriteria.DISEASE:
			ComboBox diseaseVariantField = getField(SelfReportCriteria.DISEASE_VARIANT);
			Disease disease = (Disease) event.getProperty().getValue();

			if (disease == null) {
				FieldHelper.updateItems(diseaseVariantField, Collections.emptyList());
				FieldHelper.setEnabled(false, diseaseVariantField);
			} else {
				List<DiseaseVariant> diseaseVariants =
					FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
				FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
				FieldHelper.setEnabled(CollectionUtils.isNotEmpty(diseaseVariants), diseaseVariantField);
			}

			break;
		default:
			break;
		}
	}

	@Override
	public void addMoreFilters(CustomLayout moreFiltersContainer) {
		moreFiltersContainer.addComponent(buildEpiWeekAndDateFilter(), EPI_WEEK_AND_DATE_FILTER);

	}

	@Override
	protected void applyDependenciesOnNewValue(SelfReportCriteria criteria) {
		super.applyDependenciesOnNewValue(criteria);

		EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<DateFilterOption>) getMoreFiltersContainer().getComponent(EPI_WEEK_AND_DATE_FILTER);

		weekAndDateFilter.getDateFilterOptionFilter().setValue(criteria.getDateFilterOption());
		Date reportDateFrom = criteria.getReportDateFrom();
		Date reportDateTo = criteria.getReportDateTo();

		if (DateFilterOption.EPI_WEEK.equals(criteria.getDateFilterOption())) {
			weekAndDateFilter.getWeekFromFilter().setValue(reportDateFrom == null ? null : DateHelper.getEpiWeek(reportDateFrom));
			weekAndDateFilter.getWeekToFilter().setValue(reportDateTo == null ? null : DateHelper.getEpiWeek(reportDateTo));
		} else {
			weekAndDateFilter.getDateFromFilter().setValue(reportDateFrom);
			weekAndDateFilter.getDateToFilter().setValue(reportDateTo);
		}

		ComboBox diseaseField = getField(SelfReportCriteria.DISEASE);
		ComboBox diseaseVariantField = getField(SelfReportCriteria.DISEASE_VARIANT);
		Disease disease = (Disease) diseaseField.getValue();
		if (disease == null) {
			FieldHelper.updateItems(diseaseVariantField, Collections.emptyList());
			FieldHelper.setEnabled(false, diseaseVariantField);
		} else {
			List<DiseaseVariant> diseaseVariants =
				FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
			FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
			FieldHelper.setEnabled(CollectionUtils.isNotEmpty(diseaseVariants), diseaseVariantField);
		}
	}

	private EpiWeekAndDateFilterComponent buildEpiWeekAndDateFilter() {
		EpiWeekAndDateFilterComponent<DateFilterOption> epiWeekAndDateFilter = new EpiWeekAndDateFilterComponent<>(false, false, null, this);

		epiWeekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptSelfReportEpiWeekFrom));
		epiWeekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptSelfReportEpiWeekTo));
		epiWeekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptSelfReportDateFrom));
		epiWeekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptSelfReportDateTo));

		addApplyHandler(e -> onApplyClick(epiWeekAndDateFilter));

		return epiWeekAndDateFilter;
	}

	private void onApplyClick(EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter) {
		SelfReportCriteria criteria = getValue();

		DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
		final Date fromDate;
		final Date toDate;
		if (dateFilterOption == DateFilterOption.DATE) {
			Date dateFrom = weekAndDateFilter.getDateFromFilter().getValue();
			fromDate = dateFrom != null ? DateHelper.getStartOfDay(dateFrom) : null;
			Date dateTo = weekAndDateFilter.getDateToFilter().getValue();
			toDate = dateFrom != null ? DateHelper.getEndOfDay(dateTo) : null;
		} else {
			fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue());
			toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekAndDateFilter.getWeekToFilter().getValue());
		}

		if ((fromDate != null && toDate != null) || (fromDate == null && toDate == null)) {
			criteria.reportDateBetween(fromDate, toDate, dateFilterOption);
		} else {
			weekAndDateFilter.setNotificationsForMissingFilters();
		}
	}

	@Override
	protected Stream<Field> streamFieldsForEmptyCheck(CustomLayout layout) {

		@SuppressWarnings("unchecked")
		EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<DateFilterOption>) getMoreFiltersContainer().getComponent(EPI_WEEK_AND_DATE_FILTER);

		return super.streamFieldsForEmptyCheck(layout).filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter());
	}

}
