package de.symeda.sormas.ui.surveillance.task;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.ui.surveillance.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractView;

@SuppressWarnings("serial")
public class TasksView extends AbstractView {

	public static final String VIEW_NAME = "tasks";

	private TaskGrid grid;    
    private Button newButton;

	private VerticalLayout gridLayout;

    public TasksView() {
        setSizeFull();
        addStyleName("crud-view");

        grid = new TaskGrid();
//        grid.addItemClickListener(e -> ControllerProvider.getCaseController().editData(
//        		((CaseDataDto)e.getItemId()).getUuid()));

        gridLayout = new VerticalLayout();
        gridLayout.addComponent(createTopBar());
        gridLayout.addComponent(grid);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);
        gridLayout.setSizeFull();
        gridLayout.setExpandRatio(grid, 1);
        gridLayout.setStyleName("crud-main-layout");
        
        addComponent(gridLayout);
    }


	public HorizontalLayout createTopBar() {
    	HorizontalLayout topLayout = new HorizontalLayout();
    	topLayout.setSpacing(true);
    	topLayout.setWidth("100%");
    	
//    	Button statusAll = new Button("all", e -> grid.filterTaskStatus(null));
//        statusAll.setStyleName(ValoTheme.BUTTON_LINK);
//        topLayout.addComponent(statusAll);
//        
//    	Button statusPossible = new Button("possible", e -> grid.filterTaskStatus(Tas.POSSIBLE));
//    	statusPossible.setStyleName(ValoTheme.BUTTON_LINK);
//        topLayout.addComponent(statusPossible);
//        
//        Button statusInvestigated = new Button("investigated", e -> grid.filterTaskStatus(CaseStatus.INVESTIGATED));
//        statusInvestigated.setStyleName(ValoTheme.BUTTON_LINK);
//        topLayout.addComponent(statusInvestigated);
        
        ComboBox assigneeFilter = new ComboBox();
        // TODO add assignees
        assigneeFilter.addValueChangeListener(e->grid.filterAssignee((ReferenceDto)e.getProperty().getValue()));
        topLayout.addComponent(assigneeFilter);
    	
        newButton = new Button("New task");
        newButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newButton.setIcon(FontAwesome.PLUS_CIRCLE);
        newButton.addClickListener(e -> ControllerProvider.getTaskController().create());
        topLayout.addComponent(newButton);

        topLayout.setComponentAlignment(assigneeFilter, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(assigneeFilter, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	grid.reload();
    }

    public void clearSelection() {
        grid.getSelectionModel().reset();
    }

    public void refresh(TaskDto task) {
        grid.refresh(task);
        grid.scrollTo(task);
    }

    public void remove(TaskDto task) {
        grid.remove(task);
    }
}
