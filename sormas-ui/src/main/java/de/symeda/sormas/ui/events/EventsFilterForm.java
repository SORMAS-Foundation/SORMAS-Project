package de.symeda.sormas.ui.events;

import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Date;
import java.util.stream.Stream;

import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class EventsFilterForm extends AbstractFilterForm<EventCriteria> {

	private static final long serialVersionUID = -1166745065032487009L;

	protected EventsFilterForm() {
		super(EventCriteria.class, EventIndexDto.I18N_PREFIX);
	}

	private static final String WEEK_AND_DATE_FILTER = "moreFilters";

	private static final String MORE_FILTERS_HTML_LAYOUT = loc(WEEK_AND_DATE_FILTER);

	@Override
	protected String[] getMainFilterLocators() {

		return new String[] {
			EventIndexDto.DISEASE,
			EventCriteria.REPORTING_USER_ROLE,
			EventCriteria.SURVEILLANCE_OFFICER,
			EventCriteria.FREE_TEXT };
	}

	@Override
	protected void addFields() {

		addField(FieldConfiguration.pixelSized(EventIndexDto.DISEASE, 140));
		addField(FieldConfiguration.withCaptionAndPixelSized(EventCriteria.REPORTING_USER_ROLE, I18nProperties.getString(Strings.reportedBy), 140));
		ComboBox officerField = addField(FieldConfiguration.pixelSized(EventCriteria.SURVEILLANCE_OFFICER, 140));
		RegionReferenceDto userRegion = UserProvider.getCurrent().getUser().getRegion();
		if (userRegion != null) {
			officerField.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(userRegion, UserRole.SURVEILLANCE_OFFICER));
		}
		TextField searchField = addField(
			FieldConfiguration.withCaptionAndPixelSized(EventCriteria.FREE_TEXT, I18nProperties.getString(Strings.promptEventsSearchField), 200));
		searchField.setNullRepresentation("");
	}

	@Override
	public void addMoreFilters(CustomLayout moreFiltersContainer) {
		moreFiltersContainer.addComponent(buildWeekAndDateFilter(), WEEK_AND_DATE_FILTER);
	}

	private HorizontalLayout buildWeekAndDateFilter() {

		Button applyButton = ButtonHelper.createButton(Captions.actionApplyDateFilter, null);

		EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(applyButton, false, false, null);

		weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventEpiWeekFrom));
		weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventEpiWeekTo));
		weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventDateFrom));
		weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventDateTo));

		applyButton.addClickListener(e -> {
			EventCriteria criteria = getValue();

			DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
			Date fromDate, toDate;
			if (dateFilterOption == DateFilterOption.DATE) {
				fromDate = DateHelper.getStartOfDay(weekAndDateFilter.getDateFromFilter().getValue());
				toDate = DateHelper.getEndOfDay(weekAndDateFilter.getDateToFilter().getValue());
			} else {
				fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue());
				toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekAndDateFilter.getWeekToFilter().getValue());
			}
			weekAndDateFilter.setVisible(false);

			if ((fromDate != null && toDate != null) || (fromDate == null && toDate == null)) {
				applyButton.removeStyleName(ValoTheme.BUTTON_PRIMARY);
				criteria.eventDateBetween(fromDate, toDate, dateFilterOption);

				fireValueChange(true);
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
		});

		HorizontalLayout dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();

		dateFilterRowLayout.addComponent(weekAndDateFilter);
		dateFilterRowLayout.addComponent(applyButton);

		return dateFilterRowLayout;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Stream<Field> streamFieldsForEmptyCheck(CustomLayout layout) {

		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		@SuppressWarnings("unchecked")
		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<NewCaseDateType>) dateFilterLayout.getComponent(0);

		return super.streamFieldsForEmptyCheck(layout).filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter());
	}

	@Override
	protected void applyDependenciesOnNewValue(EventCriteria criteria) {

		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter;
		weekAndDateFilter = (EpiWeekAndDateFilterComponent<DateFilterOption>) dateFilterLayout.getComponent(0);

		weekAndDateFilter.getDateFilterOptionFilter().setValue(criteria.getDateFilterOption());
		Date sampleDateFrom = criteria.getEventDateFrom();
		Date sampleDateTo = criteria.getEventDateTo();

		if (DateFilterOption.EPI_WEEK.equals(criteria.getDateFilterOption())) {
			weekAndDateFilter.getWeekFromFilter().setValue(sampleDateFrom == null ? null : DateHelper.getEpiWeek(sampleDateFrom));
			weekAndDateFilter.getWeekToFilter().setValue(sampleDateTo == null ? null : DateHelper.getEpiWeek(sampleDateTo));
		} else {
			weekAndDateFilter.getDateFromFilter().setValue(sampleDateFrom);
			weekAndDateFilter.getDateToFilter().setValue(sampleDateTo);
		}
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML_LAYOUT;
	}
}
