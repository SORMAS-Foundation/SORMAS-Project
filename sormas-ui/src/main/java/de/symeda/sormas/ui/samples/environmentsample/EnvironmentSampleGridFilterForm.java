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

package de.symeda.sormas.ui.samples.environmentsample;

import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Date;
import java.util.stream.Stream;

import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleCriteria;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleIndexDto;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class EnvironmentSampleGridFilterForm extends AbstractFilterForm<EnvironmentSampleCriteria> {

	private static final String EPI_WEEK_AND_DATE_FILTER = "weekAndDateFilter";
	private static final String MORE_FILTERS_HTML_LAYOUT = loc(EPI_WEEK_AND_DATE_FILTER)
		+ filterLocs(
			EnvironmentSampleCriteria.GPS_LAT_FROM,
			EnvironmentSampleCriteria.GPS_LAT_TO,
			EnvironmentSampleCriteria.GPS_LON_FROM,
			EnvironmentSampleCriteria.GPS_LON_TO);

	protected EnvironmentSampleGridFilterForm() {
		super(EnvironmentSampleCriteria.class, EnvironmentSampleIndexDto.I18N_PREFIX);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			EnvironmentSampleCriteria.FREE_TEXT,
			EnvironmentSampleCriteria.REGION,
			EnvironmentSampleCriteria.DISTRICT,
			EnvironmentSampleCriteria.LABORATORY,
			EnvironmentSampleCriteria.TESTED_PATHOGEN };
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		UserDto user = currentUserDto();

		TextField freeTextField = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				EnvironmentSampleCriteria.FREE_TEXT,
				I18nProperties.getString(Strings.promptEnvironmentSampleFreetext),
				140));
		freeTextField.setDescription(I18nProperties.getString(Strings.promptEnvironmentSampleFreetext));

		if (user.getRegion() == null) {
			ComboBox regionField = addField(
				FieldConfiguration.withCaptionAndPixelSized(
					EnvironmentSampleCriteria.REGION,
					I18nProperties.getString(Strings.promptEnvironmentSampleRegion),
					140));
			regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		}

		if (user.getDistrict() == null) {
			ComboBox districtField = addField(
				FieldConfiguration.withCaptionAndPixelSized(
					EnvironmentSampleCriteria.DISTRICT,
					I18nProperties.getString(Strings.promptEnvironmentSampleDistrict),
					140));
			districtField.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));
			districtField.setEnabled(false);
		}

		if (user.getJurisdictionLevel() != JurisdictionLevel.LABORATORY) {
			ComboBox laboratory = addField(
				FieldConfiguration.withCaptionAndPixelSized(
					EnvironmentSampleCriteria.LABORATORY,
					I18nProperties.getString(Strings.promptEnvironmentSampleLab),
					140));
			laboratory.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));
		}

		ComboBox testedPathogen = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				EnvironmentSampleCriteria.TESTED_PATHOGEN,
				I18nProperties.getString(Strings.promptEnvironmentSampleTestedPathogen),
				140),
			ComboBox.class);
		testedPathogen.addItems(FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.PATHOGEN, null));
	}

	@Override
	public void addMoreFilters(CustomLayout moreFiltersContainer) {
		moreFiltersContainer.addComponent(buildEpiWeekAndDateFilter(), EPI_WEEK_AND_DATE_FILTER);

		addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(
				EnvironmentSampleCriteria.GPS_LAT_FROM,
				I18nProperties.getString(Strings.promptEnvironmentSampleLatFrom),
				200));
		addField(
			moreFiltersContainer,
			FieldConfiguration
				.withCaptionAndPixelSized(EnvironmentSampleCriteria.GPS_LAT_TO, I18nProperties.getString(Strings.promptEnvironmentSampleLatTo), 200));
		addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(
				EnvironmentSampleCriteria.GPS_LON_FROM,
				I18nProperties.getString(Strings.promptEnvironmentSampleLonFrom),
				200));
		addField(
			moreFiltersContainer,
			FieldConfiguration
				.withCaptionAndPixelSized(EnvironmentSampleCriteria.GPS_LON_TO, I18nProperties.getString(Strings.promptEnvironmentSampleLonTo), 200));
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		switch (propertyId) {
		case EnvironmentSampleCriteria.REGION:
			RegionReferenceDto region = (RegionReferenceDto) event.getProperty().getValue();
			if (region != null) {
				applyRegionFilterDependency(region, EnvironmentSampleCriteria.DISTRICT);
			} else {
				clearAndDisableFields(EnvironmentSampleCriteria.DISTRICT);
			}

			break;
		default:
			break;
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(EnvironmentSampleCriteria criteria) {
		super.applyDependenciesOnNewValue(criteria);

		UserDto user = currentUserDto();

		RegionReferenceDto region = user.getRegion() != null ? user.getRegion() : criteria.getRegion();
		if (region != null && user.getDistrict() == null) {
			applyRegionFilterDependency(region, EnvironmentSampleCriteria.DISTRICT);
		}

		EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<DateFilterOption>) getMoreFiltersContainer().getComponent(EPI_WEEK_AND_DATE_FILTER);

		weekAndDateFilter.getDateFilterOptionFilter().setValue(criteria.getDateFilterOption());
		Date sampleDateFrom = criteria.getReportDateFrom();
		Date sampleDateTo = criteria.getReportDateTo();

		if (DateFilterOption.EPI_WEEK.equals(criteria.getDateFilterOption())) {
			weekAndDateFilter.getWeekFromFilter().setValue(sampleDateFrom == null ? null : DateHelper.getEpiWeek(sampleDateFrom));
			weekAndDateFilter.getWeekToFilter().setValue(sampleDateTo == null ? null : DateHelper.getEpiWeek(sampleDateTo));
		} else {
			weekAndDateFilter.getDateFromFilter().setValue(sampleDateFrom);
			weekAndDateFilter.getDateToFilter().setValue(sampleDateTo);
		}
	}

	@Override
	protected Stream<Field> streamFieldsForEmptyCheck(CustomLayout layout) {

		@SuppressWarnings("unchecked")
		EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<DateFilterOption>) getMoreFiltersContainer().getComponent(EPI_WEEK_AND_DATE_FILTER);

		return super.streamFieldsForEmptyCheck(layout).filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter());
	}

	private EpiWeekAndDateFilterComponent<DateFilterOption> buildEpiWeekAndDateFilter() {
		EpiWeekAndDateFilterComponent<DateFilterOption> epiWeekAndDateFilter = new EpiWeekAndDateFilterComponent<>(false, false, null, this);

		epiWeekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEnvironmentSampleEpiWeekFrom));
		epiWeekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEnvironmentSampleEpiWeekTo));
		epiWeekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEnvironmentSampleDateFrom));
		epiWeekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEnvironmentSampleDateTo));

		addApplyHandler(e -> onApplyClick(epiWeekAndDateFilter));

		return epiWeekAndDateFilter;
	}

	private void onApplyClick(EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter) {
		EnvironmentSampleCriteria criteria = getValue();

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

}
