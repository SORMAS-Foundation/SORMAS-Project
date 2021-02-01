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
package de.symeda.sormas.ui.task;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.TaskStatusValidator;

public class TaskEditForm extends AbstractEditForm<TaskDto> {

	private static final long serialVersionUID = 1L;

	private static final String SAVE_INFO = "saveInfo";
	private static final String ASSIGNEE_MISSING_INFO = "assigneeMissingInfo";

	//@formatter:off
	private static final String HTML_LAYOUT = 
			fluidRow(
					loc(TaskDto.TASK_CONTEXT), 
					locs(TaskDto.CAZE, TaskDto.EVENT, TaskDto.CONTACT)) +
			fluidRowLocs(TaskDto.TASK_TYPE) +
			fluidRowLocs(TaskDto.SUGGESTED_START, TaskDto.DUE_DATE) +
			fluidRowLocs(TaskDto.ASSIGNEE_USER, TaskDto.PRIORITY) +
			fluidRowLocs(ASSIGNEE_MISSING_INFO) +
			fluidRowLocs(TaskDto.CREATOR_COMMENT) +
			fluidRowLocs(TaskDto.ASSIGNEE_REPLY) +
			fluidRowLocs(TaskDto.TASK_STATUS) +
			fluidRowLocs(SAVE_INFO);
	//@formatter:on

	private UserRight editOrCreateUserRight;
	private boolean editedFromTaskGrid;

	public TaskEditForm(boolean create, boolean editedFromTaskGrid) {

		super(TaskDto.class, TaskDto.I18N_PREFIX);

		this.editedFromTaskGrid = editedFromTaskGrid;
		this.editOrCreateUserRight = editOrCreateUserRight;

		addValueChangeListener(e -> {
			updateByTaskContext();
			updateByCreatingAndAssignee();
		});

		setWidth(680, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	@Override
	protected void addFields() {

		addField(TaskDto.CAZE, ComboBox.class);
		addField(TaskDto.EVENT, ComboBox.class);
		addField(TaskDto.CONTACT, ComboBox.class);
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
			updateByCreatingAndAssignee();
			checkIfAssigneeEmailOrPhoneIsProvided((UserReferenceDto) e.getProperty().getValue());
		});
		assigneeUser.setImmediate(true);

		TextArea creatorComment = addField(TaskDto.CREATOR_COMMENT, TextArea.class);
		creatorComment.setRows(2);
		creatorComment.setImmediate(true);
		addField(TaskDto.ASSIGNEE_REPLY, TextArea.class).setRows(4);

		setRequired(true, TaskDto.TASK_CONTEXT, TaskDto.TASK_TYPE, TaskDto.ASSIGNEE_USER, TaskDto.DUE_DATE, TaskDto.TASK_STATUS);
		setReadOnly(true, TaskDto.TASK_CONTEXT, TaskDto.CAZE, TaskDto.CONTACT, TaskDto.EVENT);

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

			UserDto userDto = UserProvider.getCurrent().getUser();
			DistrictReferenceDto district = null;
			RegionReferenceDto region = null;
			if (taskDto.getCaze() != null) {
				CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(taskDto.getCaze().getUuid());
				district = caseDto.getDistrict();
				region = caseDto.getRegion();
			} else if (taskDto.getContact() != null) {
				ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(taskDto.getContact().getUuid());
				if (contactDto.getRegion() != null && contactDto.getDistrict() != null) {
					district = contactDto.getDistrict();
					region = contactDto.getRegion();
				} else {
					CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(contactDto.getCaze().getUuid());
					district = caseDto.getDistrict();
					region = caseDto.getRegion();
				}
			} else if (taskDto.getEvent() != null) {
				EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(taskDto.getEvent().getUuid());
				district = eventDto.getEventLocation().getDistrict();
				region = eventDto.getEventLocation().getRegion();
			} else {
				district = userDto.getDistrict();
				region = userDto.getRegion();
			}

			final List<UserReferenceDto> users = new ArrayList<>();
			if (district != null) {
				users.addAll(FacadeProvider.getUserFacade().getUserRefsByDistrict(district, true));
			} else if (region != null) {
				users.addAll(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(region));
			} else {
				// fallback - just show all users
				users.addAll(FacadeProvider.getUserFacade().getAllUserRefs(false));
			}

			// Allow regional users to assign the task to national ones
			if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.ASSIGN_TASKS_TO_HIGHER_LEVEL)
				&& userDto.getDistrict() == null
				&& userDto.getRegion() != null) {
				users.addAll(
					FacadeProvider.getUserFacade()
						.getUsersByRegionAndRoles(null, UserRole.getWithJurisdictionLevels(JurisdictionLevel.NATION).toArray(new UserRole[0])));
			}

			// Validation
			startDate.addValidator(
				new DateComparisonValidator(
					startDate,
					dueDate,
					true,
					false,
					I18nProperties.getValidationError(Validations.beforeDate, startDate.getCaption(), dueDate.getCaption())));
			dueDate.addValidator(
				new DateComparisonValidator(
					dueDate,
					startDate,
					false,
					false,
					I18nProperties.getValidationError(Validations.afterDate, dueDate.getCaption(), startDate.getCaption())));

			Map<String, Long> userTaskCounts =
				FacadeProvider.getTaskFacade().getPendingTaskCountPerUser(users.stream().map(ReferenceDto::getUuid).collect(Collectors.toList()));
			for (UserReferenceDto user : users) {
				assigneeUser.addItem(user);
				Long userTaskCount = userTaskCounts.get(user.getUuid());
				assigneeUser.setItemCaption(user, user.getCaption() + " (" + (userTaskCount != null ? userTaskCount.toString() : "0") + ")");
			}
		});
	}

	private void checkIfAssigneeEmailOrPhoneIsProvided(UserReferenceDto assigneeRef) {

		if (assigneeRef == null || FacadeProvider.getFeatureConfigurationFacade().isFeatureDisabled(FeatureType.TASK_NOTIFICATIONS)) {
			return;
		}

		UserDto user = FacadeProvider.getUserFacade().getByUuid(assigneeRef.getUuid());
		boolean hasEmail = !StringUtils.isEmpty(user.getUserEmail());
		boolean hasPhoneNumber = !StringUtils.isEmpty(user.getPhone());

		boolean isSmsServiceSetUp = FacadeProvider.getConfigFacade().isSmsServiceSetUp();

		if (isSmsServiceSetUp && !hasEmail && !hasPhoneNumber) {
			getContent().addComponent(
				getMissingInfoComponent(I18nProperties.getString(Strings.infoAssigneeMissingEmailOrPhoneNumber)),
				ASSIGNEE_MISSING_INFO);
		} else if (!isSmsServiceSetUp && !hasEmail) {
			getContent().addComponent(getMissingInfoComponent(I18nProperties.getString(Strings.infoAssigneeMissingEmail)), ASSIGNEE_MISSING_INFO);
		} else {
			getContent().removeComponent(ASSIGNEE_MISSING_INFO);
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

			UserDto user = UserProvider.getCurrent().getUser();
			boolean creator = user.equals(value.getCreatorUser());
			boolean supervisor = UserRole.isSupervisor(user.getUserRoles());
			boolean assignee = user.equals(getFieldGroup().getField(TaskDto.ASSIGNEE_USER).getValue());

			setVisible(!creating || assignee, TaskDto.ASSIGNEE_REPLY, TaskDto.TASK_STATUS);
			if (creating && !assignee) {
				discard(TaskDto.ASSIGNEE_REPLY, TaskDto.TASK_STATUS);
			}

			if (UserProvider.getCurrent().hasUserRight(editOrCreateUserRight)) {
				setReadOnly(!(assignee || creator), TaskDto.TASK_STATUS);
				setReadOnly(!assignee, TaskDto.ASSIGNEE_REPLY);
				setReadOnly(
					!creator,
					TaskDto.TASK_TYPE,
					TaskDto.PRIORITY,
					TaskDto.SUGGESTED_START,
					TaskDto.DUE_DATE,
					TaskDto.ASSIGNEE_USER,
					TaskDto.CREATOR_COMMENT);
				setReadOnly(
					!(creator || supervisor),
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
		FieldHelper.updateItems(taskType, TaskType.getTaskTypes(taskContext));

		// context reference depending on task context
		ComboBox caseField = (ComboBox) getFieldGroup().getField(TaskDto.CAZE);
		ComboBox eventField = (ComboBox) getFieldGroup().getField(TaskDto.EVENT);
		ComboBox contactField = (ComboBox) getFieldGroup().getField(TaskDto.CONTACT);
		if (taskContext != null) {
			switch (taskContext) {
			case CASE:
				FieldHelper.setFirstVisibleClearOthers(caseField, eventField, contactField);
				FieldHelper.setFirstRequired(caseField, eventField, contactField);
				break;
			case EVENT:
				FieldHelper.setFirstVisibleClearOthers(eventField, caseField, contactField);
				FieldHelper.setFirstRequired(eventField, caseField, contactField);
				break;
			case CONTACT:
				FieldHelper.setFirstVisibleClearOthers(contactField, caseField, eventField);
				FieldHelper.setFirstRequired(contactField, caseField, eventField);
				break;
			case GENERAL:
				FieldHelper.setFirstVisibleClearOthers(null, caseField, contactField, eventField);
				FieldHelper.setFirstRequired(null, caseField, contactField, eventField);
				break;
			}
		} else {
			FieldHelper.setFirstVisibleClearOthers(null, caseField, eventField, contactField);
			FieldHelper.setFirstRequired(null, caseField, eventField, contactField);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
