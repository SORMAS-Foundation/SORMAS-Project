package de.symeda.sormas.ui.samples;

import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Date;
import java.util.stream.Stream;

import com.vaadin.server.Page;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class SampleGridFilterForm extends AbstractFilterForm<SampleCriteria> {

	private static final long serialVersionUID = 829016959284536683L;

	protected SampleGridFilterForm() {
		super(SampleCriteria.class, SampleIndexDto.I18N_PREFIX);
	}

	private static final String WEEK_AND_DATE_FILTER = "moreFilters";

	private static final String MORE_FILTERS_HTML_LAYOUT = loc(WEEK_AND_DATE_FILTER);

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			SampleCriteria.PATHOGEN_TEST_RESULT,
			SampleCriteria.SPECIMEN_CONDITION,
			SampleCriteria.CASE_CLASSIFICATION,
			SampleCriteria.DISEASE,
			SampleCriteria.REGION,
			SampleCriteria.DISTRICT,
			SampleCriteria.LAB,
			SampleCriteria.CASE_CODE_ID_LIKE };
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML_LAYOUT;
	}

	@Override
	protected void addFields() {

		addField(
			FieldConfiguration.withCaptionAndPixelSized(
				SampleCriteria.PATHOGEN_TEST_RESULT,
				I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT),
				140));
		addField(
			FieldConfiguration.withCaptionAndPixelSized(
				SampleCriteria.SPECIMEN_CONDITION,
				I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SPECIMEN_CONDITION),
				140));
		addField(
			FieldConfiguration.withCaptionAndPixelSized(
				SampleCriteria.CASE_CLASSIFICATION,
				I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_CLASSIFICATION),
				140));
		addField(
			FieldConfiguration.withCaptionAndPixelSized(
				SampleCriteria.DISEASE,
				I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE),
				140));

		UserDto user = currentUserDto();
		if (user.getRegion() == null) {
			ComboBox regionField = addField(
				FieldConfiguration.withCaptionAndPixelSized(
					SampleCriteria.REGION,
					I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REGION),
					140));
			regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		}

		addField(
			FieldConfiguration.withCaptionAndPixelSized(
				SampleCriteria.DISTRICT,
				I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT),
				140));

		ComboBox labField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(SampleCriteria.LAB, I18nProperties.getPrefixCaption(SampleIndexDto.I18N_PREFIX, SampleIndexDto.LAB), 140));
		labField.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));

		TextField searchField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(SampleCriteria.CASE_CODE_ID_LIKE, I18nProperties.getString(Strings.promptSamplesSearchField), 200));
		searchField.setNullRepresentation("");

	}

	@Override
	public void addMoreFilters(CustomLayout moreFiltersContainer) {

		moreFiltersContainer.addComponent(buildWeekAndDateFilter(), WEEK_AND_DATE_FILTER);
	}

	private HorizontalLayout buildWeekAndDateFilter() {

		EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(false, false, null, this);

		weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptSampleEpiWeekFrom));
		weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptSampleEpiWeekTo));
		weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptSampleDateFrom));
		weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptSampleDateTo));

		addApplyHandler(e -> onApplyClick(weekAndDateFilter));

		HorizontalLayout dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();

		dateFilterRowLayout.addComponent(weekAndDateFilter);

		return dateFilterRowLayout;
	}

	private void onApplyClick(EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter) {
		SampleCriteria criteria = getValue();

		DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
		Date fromDate, toDate;
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
			if (dateFilterOption == DateFilterOption.DATE) {
				Notification notification = new Notification(
					I18nProperties.getString(Strings.headingMissingDateFilter),
					I18nProperties.getString(Strings.messageMissingDateFilter),
					Notification.Type.WARNING_MESSAGE,
					false);
				notification.setDelayMsec(-1);
				notification.show(Page.getCurrent());
			} else {
				Notification notification = new Notification(
					I18nProperties.getString(Strings.headingMissingEpiWeekFilter),
					I18nProperties.getString(Strings.messageMissingEpiWeekFilter),
					Notification.Type.WARNING_MESSAGE,
					false);
				notification.setDelayMsec(-1);
				notification.show(Page.getCurrent());
			}
		}
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		switch (propertyId) {
		case SampleCriteria.REGION: {
			RegionReferenceDto region = (RegionReferenceDto) event.getProperty().getValue();
			if (region == null) {
				clearAndDisableFields(SampleCriteria.DISTRICT);
			} else {
				enableFields(SampleCriteria.DISTRICT);
				applyRegionFilterDependency(region, SampleCriteria.DISTRICT);
			}

			break;
		}
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(SampleCriteria criteria) {

		UserDto user = currentUserDto();

		ComboBox districtField = (ComboBox) getField(SampleCriteria.DISTRICT);
		if (user.getRegion() != null) {
			districtField.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(user.getRegion().getUuid()));
			districtField.setEnabled(true);
		} else {
			RegionReferenceDto region = criteria.getRegion();
			if (region != null) {
				districtField.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
				districtField.setEnabled(true);
			} else {
				districtField.setEnabled(false);
			}
		}

		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter;
		weekAndDateFilter = (EpiWeekAndDateFilterComponent<DateFilterOption>) dateFilterLayout.getComponent(0);

		weekAndDateFilter.getDateFilterOptionFilter().setValue(criteria.getDateFilterOption());
		Date sampleDateFrom = criteria.getSampleReportDateFrom();
		Date sampleDateTo = criteria.getSampleReportDateTo();

		if (DateFilterOption.EPI_WEEK.equals(criteria.getDateFilterOption())) {
			weekAndDateFilter.getWeekFromFilter().setValue(sampleDateFrom == null ? null : DateHelper.getEpiWeek(sampleDateFrom));
			weekAndDateFilter.getWeekToFilter().setValue(sampleDateTo == null ? null : DateHelper.getEpiWeek(sampleDateTo));
		} else {
			weekAndDateFilter.getDateFromFilter().setValue(sampleDateFrom);
			weekAndDateFilter.getDateToFilter().setValue(sampleDateTo);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Stream<Field> streamFieldsForEmptyCheck(CustomLayout layout) {

		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		@SuppressWarnings("unchecked")
		EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<DateFilterOption>) dateFilterLayout.getComponent(0);

		return super.streamFieldsForEmptyCheck(layout).filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter());
	}

	public TextField getSearchField() {
		return (TextField) getField(SampleCriteria.CASE_CODE_ID_LIKE);
	}
}
