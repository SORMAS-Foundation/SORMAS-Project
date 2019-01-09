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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.task;

import java.util.HashMap;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class TaskGridComponent extends VerticalLayout {

	private TaskGrid grid;    
	private Button createButton;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	private VerticalLayout gridLayout;

	private boolean showArchivedTasks = false;
	private Label viewTitleLabel;
	private String originalViewTitle;

	public TaskGridComponent(Label viewTitleLabel) {
		setSizeFull();
		
		this.viewTitleLabel = viewTitleLabel;
		originalViewTitle = viewTitleLabel.getValue();
		
		grid = new TaskGrid();
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createAssigneeFilterBar());
		gridLayout.addComponent(grid);
		grid.getContainer().addItemSetChangeListener(e -> {
			updateActiveStatusButtonCaption();
		});

		gridLayout.setMargin(true);
		styleGridLayout(gridLayout);

		addComponent(gridLayout);
	}

	public TaskGridComponent(TaskContext context, ReferenceDto entityRef) {
		setSizeFull();
		setMargin(true);

		grid = new TaskGrid(context, entityRef);
		grid.setHeightMode(HeightMode.ROW);

		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBarForEntity(context, entityRef));
		gridLayout.addComponent(grid);
		grid.getContainer().addItemSetChangeListener(e -> {
			updateActiveStatusButtonCaption();
		});

		gridLayout.setMargin(new MarginInfo(true, false, false, false));
		styleGridLayout(gridLayout);

		addComponent(gridLayout);
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();

		ComboBox statusFilter = new ComboBox();
		statusFilter.setWidth(200, Unit.PIXELS);
		statusFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(TaskDto.I18N_PREFIX, TaskDto.TASK_STATUS));
		statusFilter.addItems((Object[])TaskStatus.values());
		statusFilter.addValueChangeListener(e -> {
			grid.filterTaskStatus((TaskStatus)e.getProperty().getValue(), true);
		});
		statusFilter.setValue(TaskStatus.PENDING);
		filterLayout.addComponent(statusFilter);

		return filterLayout;
	}

	public HorizontalLayout createAssigneeFilterBar() {
		HorizontalLayout assigneeFilterLayout = new HorizontalLayout();
		assigneeFilterLayout.setSpacing(true);
		assigneeFilterLayout.setWidth(100, Unit.PERCENTAGE);
		assigneeFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		HorizontalLayout buttonFilterLayout = new HorizontalLayout();
		buttonFilterLayout.setSpacing(true);
		{
			Button allTasks = new Button("All", e -> processAssigneeFilterChange(false, false, e.getButton()));
			initializeStatusButton(allTasks, buttonFilterLayout, "All");
			Button officerTasks = new Button("Officer tasks", e -> processAssigneeFilterChange(true, false, e.getButton()));
			initializeStatusButton(officerTasks, buttonFilterLayout, "Officer tasks");
			Button myTasks = new Button("My tasks", e -> processAssigneeFilterChange(false, true, e.getButton()));
			initializeStatusButton(myTasks, buttonFilterLayout, "My tasks");

			// Default filter for lab users (that don't have any other role) is "My tasks"
			if ((CurrentUser.getCurrent().hasUserRole(UserRole.LAB_USER) || CurrentUser.getCurrent().hasUserRole(UserRole.EXTERNAL_LAB_USER)) && CurrentUser.getCurrent().getUserRoles().size() == 1) {
				processAssigneeFilterChange(false, true, myTasks);
			} else {
				CssStyles.removeStyles(allTasks, CssStyles.BUTTON_FILTER_LIGHT);
				activeStatusButton = allTasks;
			}
		}
		assigneeFilterLayout.addComponent(buttonFilterLayout);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show archived/active cases button
			if (CurrentUser.getCurrent().hasUserRight(UserRight.TASK_VIEW_ARCHIVED)) {
				Button switchArchivedActiveButton = new Button(I18nProperties.getText("showArchivedTasks"));
				switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_LINK);
				switchArchivedActiveButton.addClickListener(e -> {
					showArchivedTasks = !showArchivedTasks;
					if (!showArchivedTasks) {
						viewTitleLabel.setValue(originalViewTitle);
						switchArchivedActiveButton.setCaption(I18nProperties.getText("showArchivedTasks"));
						switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_LINK);
						grid.getTaskCriteria().archived(false);
						grid.reload();
					} else {
						viewTitleLabel.setValue(I18nProperties.getPrefixFragment("View", TasksView.VIEW_NAME.replaceAll("/", ".") + ".archive"));
						switchArchivedActiveButton.setCaption(I18nProperties.getText("showActiveTasks"));
						switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
						grid.getTaskCriteria().archived(true);
						grid.reload();
					}
				});
				actionButtonsLayout.addComponent(switchArchivedActiveButton);
			}
			// Bulk operation dropdown
			if (CurrentUser.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				assigneeFilterLayout.setWidth(100, Unit.PERCENTAGE);

				MenuBar bulkOperationsDropdown = new MenuBar();	
				MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem("Bulk Actions", null);

				Command deleteCommand = selectedItem -> {
					ControllerProvider.getTaskController().deleteAllSelectedItems(grid.getSelectedRows(), new Runnable() {
						public void run() {
							grid.deselectAll();
							grid.reload();
						}
					});
				};
				bulkOperationsItem.addItem("Delete", FontAwesome.TRASH, deleteCommand);
				
				actionButtonsLayout.addComponent(bulkOperationsDropdown);
			}
		}
		assigneeFilterLayout.addComponent(actionButtonsLayout);
		assigneeFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		assigneeFilterLayout.setExpandRatio(actionButtonsLayout, 1);

		return assigneeFilterLayout;
	}

	public HorizontalLayout createFilterBarForEntity(TaskContext context, ReferenceDto entityRef) {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setWidth(100, Unit.PERCENTAGE);
		filterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		HorizontalLayout buttonFilterLayout = new HorizontalLayout();
		buttonFilterLayout.setSpacing(true);
		{
			Button statusAll = new Button("all", e -> processStatusChange(null, e.getButton()));
			initializeStatusButton(statusAll, buttonFilterLayout, "All");
			CssStyles.removeStyles(statusAll, CssStyles.BUTTON_FILTER_LIGHT);
			activeStatusButton = statusAll;

			for (TaskStatus status : TaskStatus.values()) {
				Button statusButton = new Button(status.toString(), e -> processStatusChange(status, e.getButton()));
				initializeStatusButton(statusButton, buttonFilterLayout, status.toString());
			}
		}
		filterLayout.addComponent(buttonFilterLayout);

		// Bulk operation dropdown
		if (CurrentUser.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			filterLayout.setWidth(100, Unit.PERCENTAGE);

			MenuBar bulkOperationsDropdown = new MenuBar();	
			MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem("Bulk Actions", null);

			Command deleteCommand = selectedItem -> {
				ControllerProvider.getTaskController().deleteAllSelectedItems(grid.getSelectedRows(), new Runnable() {
					public void run() {
						grid.deselectAll();
						grid.reload();
					}
				});
			};
			bulkOperationsItem.addItem("Delete", FontAwesome.TRASH, deleteCommand);

			filterLayout.addComponent(bulkOperationsDropdown);
			filterLayout.setComponentAlignment(bulkOperationsDropdown, Alignment.TOP_RIGHT);
			filterLayout.setExpandRatio(bulkOperationsDropdown, 1);
		}

		if (CurrentUser.getCurrent().hasUserRight(UserRight.TASK_CREATE)) {
			createButton = new Button("New task");
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(FontAwesome.PLUS_CIRCLE);
			createButton.addClickListener(e -> ControllerProvider.getTaskController().create(context, entityRef, grid::reload));
			filterLayout.addComponent(createButton);
			filterLayout.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
		}

		return filterLayout;
	}

	private void styleGridLayout(VerticalLayout gridLayout) {
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
	}

	public void updateActiveStatusButtonCaption() {
		if (activeStatusButton != null) {
			activeStatusButton.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getContainer().size())));
		}
	}

	private void processAssigneeFilterChange(boolean officerTasks, boolean myTasks, Button button) {
		if (officerTasks) {
			grid.filterExcludeAssignee(CurrentUser.getCurrent().getUserReference(), true);
		} else if (myTasks) {
			grid.filterAssignee(CurrentUser.getCurrent().getUserReference(), true);
		} else {
			grid.filterAssignee(null, true);
		}

		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
		});
		CssStyles.removeStyles(button, CssStyles.BUTTON_FILTER_LIGHT);
		activeStatusButton = button;	
		updateActiveStatusButtonCaption();
	}

	private void processStatusChange(TaskStatus taskStatus, Button button) {
		grid.filterTaskStatus(taskStatus, true);
		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
		});
		CssStyles.removeStyles(button, CssStyles.BUTTON_FILTER_LIGHT);
		activeStatusButton = button;
		updateActiveStatusButtonCaption();
	}

	private void initializeStatusButton(Button button, HorizontalLayout filterLayout, String caption) {
		CssStyles.style(button, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);
		button.setCaptionAsHtml(true);
		filterLayout.addComponent(button);
		statusButtons.put(button, caption);
	}

	public void reload() {
		grid.reload();
	}

	public TaskGrid getGrid() {
		return grid;
	}

}
