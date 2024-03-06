/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.task;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.TaskStatusValidator;
import de.symeda.sormas.ui.utils.components.MultiSelect;

@SuppressWarnings("deprecation")
public class TaskEditForm extends AbstractEditForm<TaskDto> {

	private static final long serialVersionUID = 1L;

	private static final String SAVE_INFO = "saveInfo";
	private static final String ASSIGNEE_MISSING_INFO = "assigneeMissingInfo";
	private static final String OBSERVER_MISSING_INFO = "observerMissingInfo";

	//@formatter:off
	private static final String HTML_LAYOUT = 
			fluidRow(
					loc(TaskDto.TASK_CONTEXT), 
					locs(TaskDto.CAZE, TaskDto.EVENT, TaskDto.CONTACT, TaskDto.TRAVEL_ENTRY, TaskDto.ENVIRONMENT)) +
			fluidRowLocs(TaskDto.TASK_TYPE) +
			fluidRowLocs(TaskDto.SUGGESTED_START, TaskDto.DUE_DATE) +
			fluidRowLocs(TaskDto.ASSIGNEE_USER, TaskDto.PRIORITY) +
			fluidRowLocs(ASSIGNEE_MISSING_INFO) +
			fluidRowLocs(TaskDto.CREATOR_COMMENT) +
			fluidRowLocs(TaskDto.ASSIGNEE_REPLY) +
			fluidRowLocs(TaskDto.TASK_STATUS) +
			fluidRowLocs(TaskDto.OBSERVER_USERS) +
			fluidRowLocs(OBSERVER_MISSING_INFO) +
			fluidRowLocs(SAVE_INFO) + fluidRowLocs(TaskDto.ASSIGNED_BY_USER);
	//@formatter:on

	private UserRight editOrCreateUserRight;
	private boolean editedFromTaskGrid;
	private Disease disease;
	private List<UserReferenceDto> availableUsers;

	public TaskEditForm(boolean create, boolean editedFromTaskGrid, Disease disease) {

		super(TaskDto.class, TaskDto.I18N_PREFIX, false, FieldVisibilityCheckers.withDisease(disease));

		this.editedFromTaskGrid = editedFromTaskGrid;
		this.editOrCreateUserRight = create ? UserRight.TASK_CREATE : UserRight.TASK_EDIT;
		this.disease = disease;
		this.availableUsers = new ArrayList<>();

		addValueChangeListener(e -> {
			updateByTaskContext();
			updateByCreatingAndAssignee();
		});

		setWidth(680, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}

		addFields();
	}

	@Override
	protected void addFields() {

		addField(TaskDto.CAZE, ComboBox.class);
		addField(TaskDto.EVENT, ComboBox.class);
		addField(TaskDto.CONTACT, ComboBox.class);
		addField(TaskDto.TRAVEL_ENTRY, ComboBox.class);
		addField(TaskDto.ENVIRONMENT, ComboBox.class);
		DateTimeField startDate = addDateField(TaskDto.SUGGESTED_START, DateTimeField.class, -1);
		DateTimeField dueDate = addDateField(TaskDto.DUE_DATE, DateTimeField.class, -1);
		dueDate.setImmediate(true);
		addField(TaskDto.PRIORITY, ComboBox.class);
		NullableOptionGroup taskStatus = addField(TaskDto.TASK_STATUS, NullableOptionGroup.class);
		NullableOptionGroup taskContext = addField(TaskDto.TASK_CONTEXT, NullableOptionGroup.class);
		taskContext.setImmediate(true);
		taskContext.addValueChangeListener(event -> updateByTaskContext());

		ComboBox taskTypeField = addField(TaskDto.TASK_TYPE, ComboBox.class);
		taskTypeField.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		taskTypeField.setImmediate(true);
		taskTypeField.addValueChangeListener(e -> {
			TaskType taskType = (TaskType) e.getProperty().getValue();
			if (taskType != null) {
				setRequired(taskType.isCreatorCommentRequired(), TaskDto.CREATOR_COMMENT);
			}
		});

		ComboBox assigneeUser = addField(TaskDto.ASSIGNEE_USER, ComboBox.class);
		assigneeUser.addValueChangeListener(e -> {
			updateObserversList();
			updateByCreatingAndAssignee();
			checkIfUserEmailOrPhoneIsProvided(
				(UserReferenceDto) e.getProperty().getValue(),
				Strings.infoAssigneeMissingEmail,
				Strings.infoAssigneeMissingEmailOrPhoneNumber,
				ASSIGNEE_MISSING_INFO);
		});
		assigneeUser.setImmediate(true);

		TextArea creatorComment = addField(TaskDto.CREATOR_COMMENT, TextArea.class);
		creatorComment.setRows(2);
		creatorComment.setImmediate(true);
		addField(TaskDto.ASSIGNEE_REPLY, TextArea.class).setRows(4);

		MultiSelect<UserReferenceDto> observerUsers = addField(TaskDto.OBSERVER_USERS, MultiSelect.class);
		observerUsers.addValueChangeListener(e -> {
			Collection<UserReferenceDto> userReferences = (Collection<UserReferenceDto>) e.getProperty().getValue();
			for (UserReferenceDto userReference : userReferences) {
				checkIfUserEmailOrPhoneIsProvided(
					userReference,
					Strings.infoObserverMissingEmail,
					Strings.infoObserverMissingEmailOrPhoneNumber,
					OBSERVER_MISSING_INFO);
			}
		});
		observerUsers.setImmediate(true);
		CssStyles.style(observerUsers, CssStyles.OPTIONGROUP_MAX_HEIGHT_150);

		ComboBox assignedBy = addField(TaskDto.ASSIGNED_BY_USER, ComboBox.class);
		assignedBy.setReadOnly(true);

		setRequired(true, TaskDto.TASK_CONTEXT, TaskDto.TASK_TYPE, TaskDto.ASSIGNEE_USER, TaskDto.DUE_DATE, TaskDto.TASK_STATUS);
		setReadOnly(true, TaskDto.TASK_CONTEXT, TaskDto.CAZE, TaskDto.CONTACT, TaskDto.EVENT, TaskDto.TRAVEL_ENTRY, TaskDto.ENVIRONMENT);

		addValueChangeListener(e -> {
			TaskDto taskDto = getValue();

			if (taskDto.getTaskType() == TaskType.CASE_INVESTIGATION && taskDto.getCaze() != null) {
				taskStatus.addValidator(
					new TaskStatusValidator(
						taskDto.getCaze().getUuid(),
						I18nProperties.getValidationError(Validations.investigationStatusUnclassifiedCase)));

				if (!editedFromTaskGrid) {
					final HorizontalLayout saveInfoLayout = new HorizontalLayout(
						new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoSaveOfTask), ContentMode.HTML));
					saveInfoLayout.setSpacing(true);
					saveInfoLayout.setMargin(new MarginInfo(true, false, true, false));
					getContent().addComponent(saveInfoLayout, SAVE_INFO);
				}
			}

			final UserDto userDto = UiUtil.getUser();
			availableUsers = new ArrayList<>();
			if (taskDto.getCaze() != null) {
				availableUsers.addAll(FacadeProvider.getUserFacade().getUsersHavingCaseInJurisdiction(taskDto.getCaze()));
			} else if (taskDto.getContact() != null) {
				availableUsers.addAll(FacadeProvider.getUserFacade().getUsersHavingContactInJurisdiction(taskDto.getContact()));
			} else if (taskDto.getEvent() != null) {
				availableUsers.addAll(FacadeProvider.getUserFacade().getUsersHavingEventInJurisdiction(taskDto.getEvent()));
			} else if (taskDto.getTravelEntry() != null) {
				availableUsers.addAll(FacadeProvider.getUserFacade().getUsersHavingTravelEntryInJurisdiction(taskDto.getTravelEntry()));
			} else if (taskDto.getEnvironment() != null) {
				availableUsers.addAll(FacadeProvider.getUserFacade().getUsersHavingEnvironmentInJurisdiction(taskDto.getEnvironment()));
			} else {
				availableUsers.addAll(FacadeProvider.getUserFacade().getAllUserRefs(false));
			}

			// Allow users to assign tasks to users of the next higher jurisdiction level, when the higher jurisdiction contains the users jurisdiction
			// For facility users, this checks where the facility is located and considers the district & community of the faciliy the "higher level"
			// For national users, there is no higher level
			if (UiUtil.enabled(FeatureType.ASSIGN_TASKS_TO_HIGHER_LEVEL)
				&& UserProvider.getCurrent().getJurisdictionLevel() != JurisdictionLevel.NATION) {

				List<UserReferenceDto> superordinateUsers = FacadeProvider.getUserFacade().getUsersWithSuperiorJurisdiction(userDto);
				if (superordinateUsers != null) {
					availableUsers.addAll(superordinateUsers);
				}
			}

			// Validation
			DateComparisonValidator.addStartEndValidators(startDate, dueDate);
			DateComparisonValidator.dateFieldDependencyValidationVisibility(startDate, dueDate);

			Map<String, Long> userTaskCounts = FacadeProvider.getTaskFacade()
				.getPendingTaskCountPerUser(availableUsers.stream().map(ReferenceDto::getUuid).collect(Collectors.toList()));
			for (UserReferenceDto user : availableUsers) {
				assigneeUser.addItem(user);
				Long userTaskCount = userTaskCounts.get(user.getUuid());
				assigneeUser.setItemCaption(user, user.getCaption() + " (" + (userTaskCount != null ? userTaskCount.toString() : "0") + ")");

				if (!user.equals(assigneeUser.getValue())) {
					// If a user has been assigned to the task, do not make it available for observers field
					observerUsers.addItem(user);
					observerUsers.setItemCaption(user, user.getCaption());
				}
			}
		});
	}

	private void updateObserversList() {
		ComboBox assigneeField = getField(TaskDto.ASSIGNEE_USER);
		MultiSelect<UserReferenceDto> observersField = getField(TaskDto.OBSERVER_USERS);

		Collection<UserReferenceDto> selectedObservers = (Collection<UserReferenceDto>) observersField.getValue();
		if (selectedObservers == null) {
			selectedObservers = Collections.emptyList();
		} else {
			// Let's ensure that the collection won't be touched by the following "observersField.removeAllItems()"
			selectedObservers = new ArrayList<>(selectedObservers);
		}

		// Let's ensure that every available users is a choice into the observers field (except the assignee user)
		observersField.removeAllItems();
		for (UserReferenceDto user : availableUsers) {
			if (user.equals(assigneeField.getValue())) {
				// If a user has been assigned to a task, do not make it available for observers field
				continue;
			}

			observersField.addItem(user);
			observersField.setItemCaption(user, user.getCaption());
		}

		// As we removed everything from observers field, let's apply again its value
		Set<UserReferenceDto> filteredObservers =
			selectedObservers.stream().filter(userReferenceDto -> !userReferenceDto.equals(assigneeField.getValue())).collect(Collectors.toSet());
		observersField.setValue(filteredObservers, true);
	}

	private void checkIfUserEmailOrPhoneIsProvided(
		UserReferenceDto assigneeRef,
		String missingEmailLabel,
		String missingEmailOrPhoneLabel,
		String location) {

		if (assigneeRef == null || UiUtil.disabled(FeatureType.TASK_NOTIFICATIONS)) {
			return;
		}

		UserDto user = FacadeProvider.getUserFacade().getByUuid(assigneeRef.getUuid());
		boolean hasEmail = !StringUtils.isEmpty(user.getUserEmail());
		boolean hasPhoneNumber = !StringUtils.isEmpty(user.getPhone());

		boolean isSmsServiceSetUp = FacadeProvider.getConfigFacade().isSmsServiceSetUp();

		if (isSmsServiceSetUp && !hasEmail && !hasPhoneNumber) {
			getContent().addComponent(getMissingInfoComponent(I18nProperties.getString(missingEmailOrPhoneLabel)), location);
		} else if (!isSmsServiceSetUp && !hasEmail) {
			getContent().addComponent(getMissingInfoComponent(I18nProperties.getString(missingEmailLabel)), location);
		} else {
			getContent().removeComponent(location);
		}
	}

	private HorizontalLayout getMissingInfoComponent(String caption) {
		Label assigneeMissingInfoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + caption, ContentMode.HTML);
		assigneeMissingInfoLabel.setWidthFull();

		final HorizontalLayout assigneeMissingInfo = new HorizontalLayout(assigneeMissingInfoLabel);
		assigneeMissingInfo.setSpacing(true);
		assigneeMissingInfo.setMargin(new MarginInfo(false, false, true, false));
		assigneeMissingInfo.setWidthFull();

		return assigneeMissingInfo;
	}

	private void updateByCreatingAndAssignee() {

		TaskDto value = getValue();
		if (value != null) {
			boolean creating = value.getCreationDate() == null;

			UserDto user = UiUtil.getUser();
			JurisdictionLevel jurisdictionLevel = UiUtil.getJurisdictionLevel();
			boolean creator = value.getCreatorUser() != null && user.getUuid().equals(value.getCreatorUser().getUuid());
			boolean nationalOrAdmin = jurisdictionLevel == null || jurisdictionLevel == JurisdictionLevel.NATION;
			boolean regional = jurisdictionLevel == JurisdictionLevel.REGION;
			boolean assignee = user.equals(getFieldGroup().getField(TaskDto.ASSIGNEE_USER).getValue());
			boolean freeEditingAllowed = FacadeProvider.getFeatureConfigurationFacade()
				.isPropertyValueTrue(FeatureType.TASK_MANAGEMENT, FeatureTypeProperty.ALLOW_FREE_EDITING);

			setVisible(freeEditingAllowed || !creating || assignee || nationalOrAdmin, TaskDto.ASSIGNEE_REPLY, TaskDto.TASK_STATUS);

			if (UiUtil.permitted(editOrCreateUserRight)) {
				setReadOnly(!(freeEditingAllowed || assignee || creator || nationalOrAdmin), TaskDto.TASK_STATUS);
				setReadOnly(!(freeEditingAllowed || assignee || nationalOrAdmin), TaskDto.ASSIGNEE_REPLY);
				setReadOnly(
					!(freeEditingAllowed || creator || nationalOrAdmin),
					TaskDto.TASK_TYPE,
					TaskDto.PRIORITY,
					TaskDto.SUGGESTED_START,
					TaskDto.DUE_DATE,
					TaskDto.ASSIGNEE_USER,
					TaskDto.CREATOR_COMMENT,
					TaskDto.OBSERVER_USERS);
				setReadOnly(
					!(freeEditingAllowed || creator || regional || nationalOrAdmin),
					TaskDto.PRIORITY,
					TaskDto.SUGGESTED_START,
					TaskDto.DUE_DATE,
					TaskDto.ASSIGNEE_USER,
					TaskDto.CREATOR_COMMENT);
			}
		}
	}

	private void updateByTaskContext() {
		TaskContext taskContext = (TaskContext) getFieldGroup().getField(TaskDto.TASK_CONTEXT).getValue();

		// Task types depending on task context
		ComboBox taskType = (ComboBox) getFieldGroup().getField(TaskDto.TASK_TYPE);
		FieldHelper.updateItems(taskType, TaskType.getTaskTypes(taskContext), FieldVisibilityCheckers.withDisease(disease), TaskType.class);

		// context reference depending on task context
		ComboBox caseField = (ComboBox) getFieldGroup().getField(TaskDto.CAZE);
		ComboBox eventField = (ComboBox) getFieldGroup().getField(TaskDto.EVENT);
		ComboBox contactField = (ComboBox) getFieldGroup().getField(TaskDto.CONTACT);
		ComboBox travelEntryField = (ComboBox) getFieldGroup().getField(TaskDto.TRAVEL_ENTRY);
		ComboBox environmentField = (ComboBox) getFieldGroup().getField(TaskDto.ENVIRONMENT);
		if (taskContext != null) {
			switch (taskContext) {
			case CASE:
				FieldHelper.setFirstVisibleClearOthers(caseField, eventField, contactField, travelEntryField, environmentField);
				FieldHelper.setFirstRequired(caseField, eventField, contactField, travelEntryField, environmentField);
				break;
			case EVENT:
				FieldHelper.setFirstVisibleClearOthers(eventField, caseField, contactField, travelEntryField, environmentField);
				FieldHelper.setFirstRequired(eventField, caseField, contactField, travelEntryField, environmentField);
				break;
			case CONTACT:
				FieldHelper.setFirstVisibleClearOthers(contactField, caseField, eventField, travelEntryField, environmentField);
				FieldHelper.setFirstRequired(contactField, caseField, eventField, travelEntryField, environmentField);
				break;
			case TRAVEL_ENTRY:
				FieldHelper.setFirstVisibleClearOthers(travelEntryField, contactField, caseField, eventField, environmentField);
				FieldHelper.setFirstRequired(travelEntryField, contactField, caseField, eventField, environmentField);
				break;
			case ENVIRONMENT:
				FieldHelper.setFirstVisibleClearOthers(environmentField, travelEntryField, contactField, caseField, eventField);
				FieldHelper.setFirstRequired(environmentField, travelEntryField, contactField, caseField, eventField);
				break;
			case GENERAL:
				FieldHelper.setFirstVisibleClearOthers(null, caseField, contactField, eventField);
				FieldHelper.setFirstRequired(null, caseField, contactField, eventField);
				break;
			}
		} else {
			FieldHelper.setFirstVisibleClearOthers(null, caseField, eventField, contactField, travelEntryField, environmentField);
			FieldHelper.setFirstRequired(null, caseField, eventField, contactField, travelEntryField, environmentField);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
