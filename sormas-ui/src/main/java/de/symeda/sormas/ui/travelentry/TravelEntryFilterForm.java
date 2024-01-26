package de.symeda.sormas.ui.travelentry;

import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Date;
import java.util.stream.Stream;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class TravelEntryFilterForm extends AbstractFilterForm<TravelEntryCriteria> {

	private static final String CHECKBOX_STYLE = CssStyles.CHECKBOX_FILTER_INLINE + " " + CssStyles.VSPACE_3;
	private static final String WEEK_AND_DATE_FILTER = "weekAndDateFilter";

	private static final String MORE_FILTERS_HTML_LAYOUT = loc(WEEK_AND_DATE_FILTER);

	protected TravelEntryFilterForm() {
		super(
			TravelEntryCriteria.class,
			TravelEntryDto.I18N_PREFIX,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			null);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			TravelEntryCriteria.NAME_UUID_EXTERNAL_ID_LIKE,
			TravelEntryCriteria.ONLY_RECOVERED_ENTRIES,
			TravelEntryCriteria.ONLY_VACCINATED_ENTRIES,
			TravelEntryCriteria.ONLY_ENTRIES_TESTED_NEGATIVE,
			TravelEntryCriteria.ONLY_ENTRIES_CONVERTED_TO_CASE };
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		final TextField searchField = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				TravelEntryCriteria.NAME_UUID_EXTERNAL_ID_LIKE,
				I18nProperties.getString(Strings.promptTravelEntrySearchField),
				200));
		searchField.setNullRepresentation("");

		addField(
			FieldConfiguration.withCaptionAndStyle(
				TravelEntryCriteria.ONLY_RECOVERED_ENTRIES,
				I18nProperties.getCaption(Captions.travelEntryOnlyRecoveredEntries),
				null,
				CHECKBOX_STYLE),
			CheckBox.class);

		addField(
			FieldConfiguration.withCaptionAndStyle(
				TravelEntryCriteria.ONLY_VACCINATED_ENTRIES,
				I18nProperties.getCaption(Captions.travelEntryOnlyVaccinatedEntries),
				null,
				CHECKBOX_STYLE),
			CheckBox.class);

		addField(
			FieldConfiguration.withCaptionAndStyle(
				TravelEntryCriteria.ONLY_ENTRIES_TESTED_NEGATIVE,
				I18nProperties.getCaption(Captions.travelEntryOnlyEntriesTestedNegative),
				null,
				CHECKBOX_STYLE),
			CheckBox.class);

		addField(
			FieldConfiguration.withCaptionAndStyle(
				TravelEntryCriteria.ONLY_ENTRIES_CONVERTED_TO_CASE,
				I18nProperties.getCaption(Captions.travelEntryOnlyEntriesConvertedToCase),
				null,
				CHECKBOX_STYLE),
			CheckBox.class);
	}

	@Override
	public void addMoreFilters(CustomLayout moreFiltersContainer) {
		moreFiltersContainer.addComponent(buildWeekAndDateFilter(), WEEK_AND_DATE_FILTER);
	}

	private HorizontalLayout buildWeekAndDateFilter() {

		EpiWeekAndDateFilterComponent weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(false, false, null, this);

		weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptTravelEntryEpiWeekFrom));
		weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptTravelEntryEpiWeekTo));
		weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptTravelEntryDateFrom));
		weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptTravelEntryDateTo));
		addApplyHandler(e -> onApplyClick(weekAndDateFilter));

		HorizontalLayout dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();

		dateFilterRowLayout.addComponent(weekAndDateFilter);

		return dateFilterRowLayout;
	}

	private void onApplyClick(EpiWeekAndDateFilterComponent<?> weekAndDateFilter) {
		TravelEntryCriteria criteria = getValue();

		DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();

		Date fromDate, toDate;
		if (dateFilterOption == DateFilterOption.DATE) {
			Date dateFrom = weekAndDateFilter.getDateFromFilter().getValue();
			fromDate = dateFrom != null ? DateHelper.getStartOfDay(dateFrom) : null;
			Date dateTo = weekAndDateFilter.getDateToFilter().getValue();
			toDate = dateTo != null ? DateHelper.getEndOfDay(dateTo) : null;
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
	protected void applyDependenciesOnNewValue(TravelEntryCriteria criteria) {

		final HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		final EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter;
		weekAndDateFilter = (EpiWeekAndDateFilterComponent<DateFilterOption>) dateFilterLayout.getComponent(0);

		final DateFilterOption dateFilterOption = criteria.getDateFilterOption();
		final Date dateFrom = criteria.getReportDateFrom();
		final Date dateTo = criteria.getReportDateTo();
		weekAndDateFilter.getDateFilterOptionFilter().setValue(dateFilterOption);

		if (DateFilterOption.EPI_WEEK.equals(dateFilterOption)) {
			weekAndDateFilter.getWeekFromFilter().setValue(dateFrom == null ? null : DateHelper.getEpiWeek(dateFrom));
			weekAndDateFilter.getWeekToFilter().setValue(dateTo == null ? null : DateHelper.getEpiWeek(dateTo));
		} else {
			weekAndDateFilter.getDateFromFilter().setValue(dateFrom);
			weekAndDateFilter.getDateToFilter().setValue(dateTo);
		}
	}

	@Override
	protected Stream<Field> streamFieldsForEmptyCheck(CustomLayout layout) {
		final HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		final EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter;
		weekAndDateFilter = (EpiWeekAndDateFilterComponent<DateFilterOption>) dateFilterLayout.getComponent(0);

		return super.streamFieldsForEmptyCheck(layout).filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter());
	}
}
