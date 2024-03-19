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
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_5;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class BulkTaskDataForm extends AbstractEditForm<TaskDto> {

	public static final String ASSIGNEE_CHECKBOX = "assigneeCheckbox";
	public static final String ASSIGNEE_HINT = "assigneeHint";
	public static final String PRORITY_CHECKBOX = "prorityCheckbox";
	public static final String STATUS_CHECKBOX = "statusCheckbox";
	private static final long serialVersionUID = 1L;
	private static final String HTML_LAYOUT = fluidRowLocsCss(VSPACE_4, ASSIGNEE_CHECKBOX)
		+ fluidRowLocs(ASSIGNEE_HINT)
		+ fluidRowLocs(TaskBulkEditData.ASSIGNEE_USER)
		+ fluidRowLocsCss(VSPACE_4, PRORITY_CHECKBOX)
		+ fluidRowLocs(TaskBulkEditData.PRIORITY)
		+ fluidRowLocsCss(VSPACE_4, STATUS_CHECKBOX)
		+ fluidRowLocs(TaskBulkEditData.TASK_STATUS);

	private boolean initialized;

	private CheckBox assigneeCheckbox;
	private CheckBox priorityCheckbox;
	private CheckBox taskStatusCheckbox;
	private Collection<? extends TaskIndexDto> selectedTasks;
	private List<UserReferenceDto> assignableUsers;
	private JurisdictionLevel lowestCommonJurisdictionLevel;
	private InfrastructureDataReferenceDto commonJurisdictionReference = null;

	public BulkTaskDataForm(Collection<? extends TaskIndexDto> selectedTasks) {
		super(TaskDto.class, TaskDto.I18N_PREFIX);
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
		NullableOptionGroup priority = addField(TaskBulkEditData.PRIORITY, NullableOptionGroup.class);
		priority.setEnabled(false);
		FieldHelper.setRequiredWhen(getFieldGroup(), priorityCheckbox, Arrays.asList(TaskBulkEditData.PRIORITY), Arrays.asList(true));
		priorityCheckbox.addValueChangeListener(e -> priority.setEnabled((boolean) e.getProperty().getValue()));

		assigneeCheckbox = new CheckBox(I18nProperties.getCaption(Captions.bulkTaskAssignee));
		getContent().addComponent(assigneeCheckbox, ASSIGNEE_CHECKBOX);
		ComboBox assignee = addField(TaskBulkEditData.ASSIGNEE_USER, ComboBox.class);
		assignee.setEnabled(false);
		FieldHelper
			.addSoftRequiredStyleWhen(getFieldGroup(), assigneeCheckbox, Arrays.asList(TaskBulkEditData.ASSIGNEE_USER), Arrays.asList(true), null);

		Label assigneeHint = new Label();
		getContent().addComponent(assigneeHint, ASSIGNEE_HINT);
		CssStyles.style(assigneeHint, VSPACE_4, VSPACE_TOP_5);
		assigneeHint.setVisible(false);
		assigneeHint.setWidthFull();
		assigneeHint.setContentMode(ContentMode.HTML);

		assigneeCheckbox.addValueChangeListener(e -> {
			boolean changeAssignee = (boolean) e.getProperty().getValue();
			assignee.setEnabled(changeAssignee);
			assignee.setRequired(changeAssignee);

			if (changeAssignee && assignableUsers == null) {

				List<FacilityReferenceDto> commonFacilities =
					selectedTasks.stream().map(TaskIndexDto::getFacility).distinct().limit(2).collect(Collectors.toList());
				List<PointOfEntryReferenceDto> commonPointsOfEntry =
					selectedTasks.stream().map(TaskIndexDto::getPointOfEntry).distinct().limit(2).collect(Collectors.toList());

				if (selectedTasks.stream().allMatch(t -> t.getFacility() != null)
					&& commonFacilities.size() <= 1
					&& !commonFacilities.get(0).getUuid().equals(FacilityDto.NONE_FACILITY_UUID)
					&& !commonFacilities.get(0).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
					lowestCommonJurisdictionLevel = JurisdictionLevel.HEALTH_FACILITY;
					commonJurisdictionReference = selectedTasks.iterator().next().getFacility();
				} else if (selectedTasks.stream().allMatch(t -> t.getCommunity() != null)
					&& selectedTasks.stream().map(TaskIndexDto::getCommunity).distinct().limit(2).count() <= 1) {
					lowestCommonJurisdictionLevel = JurisdictionLevel.COMMUNITY;
					commonJurisdictionReference = selectedTasks.iterator().next().getCommunity();
				} else if (selectedTasks.stream().allMatch(t -> t.getPointOfEntry() != null)
					&& commonPointsOfEntry.size() <= 1
					&& !commonPointsOfEntry.get(0).isOtherPointOfEntry()) {
					lowestCommonJurisdictionLevel = JurisdictionLevel.POINT_OF_ENTRY;
					commonJurisdictionReference = selectedTasks.iterator().next().getPointOfEntry();
				} else if (selectedTasks.stream().map(TaskIndexDto::getDistrict).distinct().limit(2).count() <= 1) {
					lowestCommonJurisdictionLevel = JurisdictionLevel.DISTRICT;
					commonJurisdictionReference = selectedTasks.iterator().next().getDistrict();
				} else if (selectedTasks.stream().map(TaskIndexDto::getRegion).distinct().limit(2).count() <= 1) {
					lowestCommonJurisdictionLevel = JurisdictionLevel.REGION;
					commonJurisdictionReference = selectedTasks.iterator().next().getRegion();
				} else {
					lowestCommonJurisdictionLevel = JurisdictionLevel.NATION;
				}

				fillAssignableUsers();
				Map<String, Long> userTaskCounts = FacadeProvider.getTaskFacade()
					.getPendingTaskCountPerUser(assignableUsers.stream().map(ReferenceDto::getUuid).collect(Collectors.toList()));
				for (UserReferenceDto user : assignableUsers) {
					assignee.addItem(user);
					Long userTaskCount = userTaskCounts.get(user.getUuid());
					assignee.setItemCaption(user, user.getCaption() + " (" + (userTaskCount != null ? userTaskCount.toString() : "0") + ")");
				}
			}

			if (changeAssignee && lowestCommonJurisdictionLevel.getOrder() < 5 && lowestCommonJurisdictionLevel != JurisdictionLevel.POINT_OF_ENTRY) {
				assigneeHint.setValue(
					VaadinIcons.INFO_CIRCLE.getHtml() + " "
						+ String.format(
							I18nProperties.getString(Strings.infoTasksWithMultipleJurisdictionsSelected),
							lowestCommonJurisdictionLevel.toString()));
				assigneeHint.setVisible(true);
			} else {
				assigneeHint.setValue("");
				assigneeHint.setVisible(false);
			}
		});
	}

	private void fillAssignableUsers() {

		Set<Disease> selectedDiseases = this.selectedTasks.stream().map(TaskIndexDto::getDisease).collect(Collectors.toSet());
		Disease selectedDisease = selectedDiseases.size() == 1 ? selectedDiseases.iterator().next() : null;

		JurisdictionLevel userJurisdictionLevel = UiUtil.getJurisdictionLevel();
		JurisdictionLevel highestAllowedJurisdictionLevel;

		if (userJurisdictionLevel == JurisdictionLevel.NATION || userJurisdictionLevel == JurisdictionLevel.NONE) {
			highestAllowedJurisdictionLevel = JurisdictionLevel.NATION;
		} else if (UiUtil.enabled(FeatureType.ASSIGN_TASKS_TO_HIGHER_LEVEL)
			&& InfrastructureHelper.getSuperordinateJurisdiction(userJurisdictionLevel).getOrder() <= lowestCommonJurisdictionLevel.getOrder()) {
			highestAllowedJurisdictionLevel = InfrastructureHelper.getSuperordinateJurisdiction(userJurisdictionLevel);
		} else {
			highestAllowedJurisdictionLevel = userJurisdictionLevel;
		}

		assignableUsers = FacadeProvider.getUserFacade()
			.getUserRefsByInfrastructure(
				commonJurisdictionReference,
				lowestCommonJurisdictionLevel,
				highestAllowedJurisdictionLevel,
				selectedDisease);
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
