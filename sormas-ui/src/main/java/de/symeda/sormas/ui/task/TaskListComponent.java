package de.symeda.sormas.ui.task;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class TaskListComponent extends VerticalLayout {

	private TaskGrid grid;    
    private Button createButton;

	private VerticalLayout gridLayout;
	
	public TaskListComponent() {
		setSizeFull();

        grid = new TaskGrid();

        gridLayout = new VerticalLayout();
        gridLayout.addComponent(createTopBar());
        gridLayout.addComponent(createFilterBar());
        gridLayout.addComponent(grid);

    	gridLayout.setMargin(true);
        styleGridLayout(gridLayout);
        
        addComponent(gridLayout);
	}
	
	public TaskListComponent(TaskContext context, ReferenceDto entityRef) {
		setSizeFull();
		setMargin(true);
		
		grid = new TaskGrid(context, entityRef);
		grid.setHeightMode(HeightMode.ROW);
		
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createTopBarForEntity(context, entityRef));
		gridLayout.addComponent(grid);

    	gridLayout.setMargin(new MarginInfo(true, false, false, false));
        styleGridLayout(gridLayout);
        
        addComponent(gridLayout);
	}
	
	public HorizontalLayout createTopBar() {
    	HorizontalLayout topLayout = new HorizontalLayout();
    	topLayout.setSpacing(true);
    	topLayout.setWidth(100, Unit.PERCENTAGE);
    	topLayout.addStyleName(CssStyles.VSPACE_3);
    	
    	HorizontalLayout buttonFilterLayout = new HorizontalLayout();
    	{
	    	Button statusAll = new Button("all", e -> grid.filterAssignee(null, true));
	        statusAll.setStyleName(ValoTheme.BUTTON_LINK);
	        buttonFilterLayout.addComponent(statusAll);
	        
	    	Button statusPossible = new Button("officer tasks", e -> grid.filterExcludeAssignee(LoginHelper.getCurrentUserAsReference(), true));
	    	statusPossible.setStyleName(ValoTheme.BUTTON_LINK);
	    	buttonFilterLayout.addComponent(statusPossible);
	        
	        Button statusInvestigated = new Button("my tasks", e -> grid.filterAssignee(LoginHelper.getCurrentUserAsReference(), true));
	        statusInvestigated.setStyleName(ValoTheme.BUTTON_LINK);
	        buttonFilterLayout.addComponent(statusInvestigated);
    	}
    	topLayout.addComponent(buttonFilterLayout);
    	
    	createButton = new Button("New task");
        createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        createButton.setIcon(FontAwesome.PLUS_CIRCLE);
        createButton.addClickListener(e -> ControllerProvider.getTaskController().create(TaskContext.GENERAL, null, grid));
        topLayout.addComponent(createButton);
        topLayout.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
        topLayout.setExpandRatio(createButton, 1);

        return topLayout;
    }
	
	public HorizontalLayout createTopBarForEntity(TaskContext context, ReferenceDto entityRef) {
		HorizontalLayout topLayout = new HorizontalLayout();
    	topLayout.setSpacing(true);
    	topLayout.setWidth(100, Unit.PERCENTAGE);
    	topLayout.addStyleName(CssStyles.VSPACE_3);
    	
    	HorizontalLayout buttonFilterLayout = new HorizontalLayout();
    	{
            for (TaskStatus status : TaskStatus.values()) {
    	    	Button statusButton = new Button(status.toString(), e -> grid.filterTaskStatus(status, true));
    	    	statusButton.setStyleName(ValoTheme.BUTTON_LINK);
    	    	buttonFilterLayout.addComponent(statusButton);
            }
    		
    		Button statusAll = new Button("all", e -> grid.filterTaskStatus(null, true));
    		statusAll.setStyleName(ValoTheme.BUTTON_LINK);
    		buttonFilterLayout.addComponent(statusAll);
    	}
    	topLayout.addComponent(buttonFilterLayout);
    	
        createButton = new Button("New task");
        createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        createButton.setIcon(FontAwesome.PLUS_CIRCLE);
        createButton.addClickListener(e -> ControllerProvider.getTaskController().create(context, entityRef, grid));
        topLayout.addComponent(createButton);
        topLayout.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
        topLayout.setExpandRatio(createButton, 1);

        return topLayout;
	}
	
	public HorizontalLayout createFilterBar() {
    	HorizontalLayout filterLayout = new HorizontalLayout();
    	filterLayout.setSpacing(true);
    	filterLayout.setSizeUndefined();
    	filterLayout.addStyleName(CssStyles.VSPACE_3);
    	        
        ComboBox statusFilter = new ComboBox();
        statusFilter.setWidth(200, Unit.PIXELS);
        statusFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(TaskDto.I18N_PREFIX, TaskDto.TASK_STATUS));
        statusFilter.addItems((Object[])TaskStatus.values());
        statusFilter.addValueChangeListener(e->grid.filterTaskStatus((TaskStatus)e.getProperty().getValue(), true));
        statusFilter.setValue(TaskStatus.PENDING);
        filterLayout.addComponent(statusFilter);

        return filterLayout;
    }

    public void reload() {
    	grid.reload();
    }
    
    private void styleGridLayout(VerticalLayout gridLayout) {
    	gridLayout.setSpacing(false);
        gridLayout.setSizeFull();
        gridLayout.setExpandRatio(grid, 1);
        gridLayout.setStyleName("crud-main-layout");
    }

}
