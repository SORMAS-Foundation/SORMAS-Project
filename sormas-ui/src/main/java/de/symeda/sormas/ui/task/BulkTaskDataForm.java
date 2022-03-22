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

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_4;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class BulkTaskDataForm extends AbstractEditForm<TaskBulkEditData> {

	public static final String ASSIGNEE_CHECKBOX = "assigneeCheckbox";
	public static final String PRORITY_CHECKBOX = "prorityCheckbox";
	public static final String STATUS_CHECKBOX = "statusCheckbox";
	private static final long serialVersionUID = 1L;
	private static final String HTML_LAYOUT = fluidRowLocsCss(VSPACE_4, ASSIGNEE_CHECKBOX)
		+ fluidRowLocs(TaskBulkEditData.TASK_ASSIGNEE)
		+ fluidRowLocsCss(VSPACE_4, PRORITY_CHECKBOX)
		+ fluidRowLocs(TaskBulkEditData.TASK_PRIORITY)
		+ fluidRowLocsCss(VSPACE_4, STATUS_CHECKBOX)
		+ fluidRowLocs(TaskBulkEditData.TASK_STATUS);

	private final DistrictReferenceDto district;

	private boolean initialized = false;

	private CheckBox assigneeCheckbox;
	private CheckBox priorityCheckbox;
	private CheckBox taskStatusCheckbox;
	private Collection<? extends TaskIndexDto> selectedTasks;

	public BulkTaskDataForm(DistrictReferenceDto district, Collection<? extends TaskIndexDto> selectedTasks) {
		super(TaskBulkEditData.class, TaskDto.I18N_PREFIX);
		this.district = district;
		this.selectedTasks = selectedTasks;
		setWidth(680, Unit.PIXELS);
		hideValidationUntilNextCommit();
		initialized = true;
		addFields();
	}

	@Override
	protected void addFields() {

		if (!initialized) {
			return;
		}

		taskStatusCheckbox = new CheckBox(I18nProperties.getCaption(Captions.bulkTaskStatus));
		getContent().addComponent(taskStatusCheckbox, STATUS_CHECKBOX);
		NullableOptionGroup status = addField(TaskBulkEditData.TASK_STATUS, NullableOptionGroup.class);
		status.setEnabled(false);
		FieldHelper.setRequiredWhen(getFieldGroup(), taskStatusCheckbox, Arrays.asList(TaskBulkEditData.TASK_STATUS), Arrays.asList(true));
		taskStatusCheckbox.addValueChangeListener(e -> {
			status.setEnabled((boolean) e.getProperty().getValue());
		});

		priorityCheckbox = new CheckBox(I18nProperties.getCaption(Captions.bulkTaskPriority));
		getContent().addComponent(priorityCheckbox, PRORITY_CHECKBOX);
		NullableOptionGroup priority = addField(TaskBulkEditData.TASK_PRIORITY, NullableOptionGroup.class);
		priority.setEnabled(false);
		FieldHelper.setRequiredWhen(getFieldGroup(), priorityCheckbox, Arrays.asList(TaskBulkEditData.TASK_PRIORITY), Arrays.asList(true));
		priorityCheckbox.addValueChangeListener(e -> {
			priority.setEnabled((boolean) e.getProperty().getValue());
		});

		assigneeCheckbox = new CheckBox(I18nProperties.getCaption(Captions.bulkTaskAssignee));
		getContent().addComponent(assigneeCheckbox, ASSIGNEE_CHECKBOX);
		ComboBox assignee = addField(TaskBulkEditData.TASK_ASSIGNEE, ComboBox.class);
		assignee.setEnabled(false);
		FieldHelper
			.addSoftRequiredStyleWhen(getFieldGroup(), assigneeCheckbox, Arrays.asList(TaskBulkEditData.TASK_ASSIGNEE), Arrays.asList(true), null);

		List<UserReferenceDto> users = getUsers();
		Map<String, Long> userTaskCounts =
			FacadeProvider.getTaskFacade().getPendingTaskCountPerUser(users.stream().map(ReferenceDto::getUuid).collect(Collectors.toList()));
		for (UserReferenceDto user : users) {
			assignee.addItem(user);
			Long userTaskCount = userTaskCounts.get(user.getUuid());
			assignee.setItemCaption(user, user.getCaption() + " (" + (userTaskCount != null ? userTaskCount.toString() : "0") + ")");
		}

		assigneeCheckbox.addValueChangeListener(e -> {
			boolean changeAssignee = (boolean) e.getProperty().getValue();
			assignee.setEnabled(changeAssignee);
			assignee.setRequired(changeAssignee);
		});
	}

	private List<UserReferenceDto> getUsers() {
		final List<UserReferenceDto> users = new ArrayList<>();
		UserDto userDto = UserProvider.getCurrent().getUser();

		if (district != null) {
			List<Disease> selectedDiseases = this.selectedTasks.stream().map(c -> c.getDisease()).collect(Collectors.toList());
			List<UserReferenceDto> assignableUsers = null;
			if (selectedDiseases.size() == 1) {
				Disease selectedDisease = selectedDiseases.get(0);
				assignableUsers = FacadeProvider.getUserFacade().getUserRefsByDistrict(district, selectedDisease);
			} else {
				assignableUsers = FacadeProvider.getUserFacade().getUserRefsByDistrict(district, true);
			}
			users.addAll(assignableUsers);
		} else {
			users.addAll(FacadeProvider.getUserFacade().getAllUserRefs(false));
		}

		// Allow users to assign tasks to users of the next higher jurisdiction level, when the higher jurisdiction contains the users jurisdiction
		// For facility users, this checks where the facility is located and considers the district & community of the faciliy the "higher level"
		// For national users, there is no higher level
		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.ASSIGN_TASKS_TO_HIGHER_LEVEL)
			&& UserRole.getJurisdictionLevel(userDto.getUserRoles()) != JurisdictionLevel.NATION) {

			List<UserReferenceDto> superordinateUsers = FacadeProvider.getUserFacade().getUsersWithSuperiorJurisdiction(userDto);
			if (superordinateUsers != null) {
				users.addAll(superordinateUsers);
			}
		}

		return users;
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	public CheckBox getAssigneeCheckbox() {
		return assigneeCheckbox;
	}

	public CheckBox getPriorityCheckbox() {
		return priorityCheckbox;
	}

	public CheckBox getTaskStatusCheckbox() {
		return taskStatusCheckbox;
	}

}
