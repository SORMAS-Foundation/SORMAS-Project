package de.symeda.sormas.ui.task;

import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Date;
import java.util.stream.Stream;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskDateType;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.UserRightFieldVisibilityChecker;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class TaskGridFilterForm extends AbstractFilterForm<TaskCriteria> {

	private static final long serialVersionUID = -8661345403078183133L;

	private static final String WEEK_AND_DATE_FILTER = "weekAndDateFilter";

	private static final String MORE_FILTERS_HTML =
		filterLocs(TaskCriteria.ASSIGNEE_USER_LIKE, TaskCriteria.CREATOR_USER_LIKE, TaskCriteria.ASSIGNED_BY_USER_LIKE) + loc(WEEK_AND_DATE_FILTER);

	protected TaskGridFilterForm() {
		super(
			TaskCriteria.class,
			TaskIndexDto.I18N_PREFIX,
			FieldVisibilityCheckers.withCheckers(new UserRightFieldVisibilityChecker(UserProvider.getCurrent()::hasUserRight)),
			JurisdictionFieldConfig.of(TaskIndexDto.REGION, TaskIndexDto.DISTRICT, null));
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			TaskIndexDto.TASK_CONTEXT,
			TaskIndexDto.TASK_STATUS,
			TaskIndexDto.TASK_TYPE,
			TaskIndexDto.REGION,
			TaskIndexDto.DISTRICT,
			TaskCriteria.FREE_TEXT };
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML;
	}

	@Override
	protected void addFields() {
		final ComboBox contextField = addField(FieldConfiguration.pixelSized(TaskIndexDto.TASK_CONTEXT, 140));
		addField(FieldConfiguration.pixelSized(TaskIndexDto.TASK_STATUS, 140));
		final ComboBox typeField = addField(FieldConfiguration.pixelSized(TaskIndexDto.TASK_TYPE, 140));

		contextField.addValueChangeListener(e -> {
			TaskContext taskContext = (TaskContext) e.getProperty().getValue();
			FieldHelper.updateEnumData(typeField, TaskType.getTaskTypes(taskContext));
		});

		final UserDto user = currentUserDto();
		if (user.getDistrict() == null) {
			if (user.getRegion() == null) {
				final ComboBox regionField = addField(FieldConfiguration.pixelSized(TaskIndexDto.REGION, 200));
				regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());

				final ComboBox districtField = addDistrictField();
				districtField.setEnabled(false);

				regionField.addValueChangeListener(e -> {
					RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
					boolean hasRegion = null != region;
					districtField.setEnabled(hasRegion);
					if (hasRegion) {
						FieldHelper.updateItems(districtField, FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
					} else {
						districtField.setValue(null);
					}
				});
			} else {
				FieldHelper.updateItems(addDistrictField(), FacadeProvider.getDistrictFacade().getAllActiveByRegion(user.getRegion().getUuid()));
			}
		}

		addField(FieldConfiguration.withCaptionAndPixelSized(TaskCriteria.FREE_TEXT, I18nProperties.getString(Strings.promptTaskSearchField), 200));
	}

	private ComboBox addDistrictField() {
		final ComboBox districtField = addField(FieldConfiguration.pixelSized(TaskIndexDto.DISTRICT, 200));
		districtField.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));
		return districtField;
	}

	@Override
	public void addMoreFilters(CustomLayout moreFiltersContainer) {

		TextField assigneeUserLikeField = addField(moreFiltersContainer, FieldConfiguration.pixelSized(TaskCriteria.ASSIGNEE_USER_LIKE, 200));
		assigneeUserLikeField.setInputPrompt(I18nProperties.getPrefixCaption(propertyI18nPrefix, TaskIndexDto.ASSIGNEE_USER));
		assigneeUserLikeField.setNullRepresentation("");

		TextField creatorUserLikeField = addField(moreFiltersContainer, FieldConfiguration.pixelSized(TaskCriteria.CREATOR_USER_LIKE, 200));
		creatorUserLikeField.setInputPrompt(I18nProperties.getPrefixCaption(propertyI18nPrefix, TaskIndexDto.CREATOR_USER));
		creatorUserLikeField.setNullRepresentation("");

		TextField assignedByUserLikeField = addField(moreFiltersContainer, FieldConfiguration.pixelSized(TaskCriteria.ASSIGNED_BY_USER_LIKE, 200));
		assignedByUserLikeField.setInputPrompt(I18nProperties.getPrefixCaption(propertyI18nPrefix, TaskIndexDto.ASSIGNED_BY_USER));
		assignedByUserLikeField.setNullRepresentation("");

		moreFiltersContainer.addComponent(buildWeekAndDateFilter(), WEEK_AND_DATE_FILTER);
	}

	private HorizontalLayout buildWeekAndDateFilter() {

		EpiWeekAndDateFilterComponent<TaskDateType> weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(
			false,
			false,
			null,
			TaskDateType.values(),
			I18nProperties.getString(Strings.promptTaskDateType),
			TaskDateType.DUE_DATE,
			this);
		weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptTaskEpiWeekFrom));
		weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptTaskEpiWeekTo));
		weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptTaskDateFrom));
		weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptTaskDateTo));

		addApplyHandler(e -> onApplyClick(weekAndDateFilter));

		HorizontalLayout dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();

		dateFilterRowLayout.addComponent(weekAndDateFilter);

		return dateFilterRowLayout;
	}

	private void onApplyClick(EpiWeekAndDateFilterComponent<TaskDateType> weekAndDateFilter) {
		TaskCriteria criteria = getValue();

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
			TaskDateType taskDateType = (TaskDateType) weekAndDateFilter.getDateTypeSelector().getValue();
			if (taskDateType == TaskDateType.DUE_DATE) {
				criteria.dueDateBetween(fromDate, toDate);
				criteria.startDateBetween(null, null);
			} else {
				criteria.startDateBetween(fromDate, toDate);
				criteria.dueDateBetween(null, null);
			}
			criteria.dateFilterOption(dateFilterOption);
		} else {
			weekAndDateFilter.setNotificationsForMissingFilters();
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(TaskCriteria newValue) {
		// Date/Epi week filter
		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		@SuppressWarnings("unchecked")
		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<NewCaseDateType>) dateFilterLayout.getComponent(0);

		TaskDateType taskDateType = newValue.getStartDateFrom() != null
			? TaskDateType.SUGGESTED_START_DATE
			: newValue.getDueDateFrom() != null ? TaskDateType.DUE_DATE : null;
		weekAndDateFilter.getDateTypeSelector().setValue(taskDateType);
		weekAndDateFilter.getDateFilterOptionFilter().setValue(newValue.getDateFilterOption());
		Date dateFrom = taskDateType == TaskDateType.SUGGESTED_START_DATE
			? newValue.getStartDateFrom()
			: taskDateType == TaskDateType.DUE_DATE ? newValue.getDueDateFrom() : null;
		Date dateTo = taskDateType == TaskDateType.SUGGESTED_START_DATE
			? newValue.getStartDateTo()
			: taskDateType == TaskDateType.DUE_DATE ? newValue.getDueDateTo() : null;

		if (DateFilterOption.EPI_WEEK.equals(newValue.getDateFilterOption())) {
			weekAndDateFilter.getWeekFromFilter().setValue(dateFrom == null ? null : DateHelper.getEpiWeek(dateFrom));
			weekAndDateFilter.getWeekToFilter().setValue(dateTo == null ? null : DateHelper.getEpiWeek(dateTo));
		} else {
			weekAndDateFilter.getDateFromFilter().setValue(dateFrom);
			weekAndDateFilter.getDateToFilter().setValue(dateTo);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Stream<Field> streamFieldsForEmptyCheck(CustomLayout layout) {
		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		@SuppressWarnings("unchecked")
		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<NewCaseDateType>) dateFilterLayout.getComponent(0);

		return super.streamFieldsForEmptyCheck(layout).filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter());
	}
}
