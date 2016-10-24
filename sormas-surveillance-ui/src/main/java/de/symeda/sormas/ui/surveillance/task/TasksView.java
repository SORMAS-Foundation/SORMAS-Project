package de.symeda.sormas.ui.surveillance.task;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.surveillance.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

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
    	topLayout.setWidth(100, Unit.PERCENTAGE);
    	
    	Label header = new Label("Tasks");
    	header.setSizeUndefined();
    	CssStyles.style(header, CssStyles.H2, CssStyles.NO_MARGIN);
    	topLayout.addComponent(header);
    	
    	HorizontalLayout buttonFilterLayout = new HorizontalLayout();
    	{
	    	Button statusAll = new Button("all", e -> grid.filterAssignee(null));
	        statusAll.setStyleName(ValoTheme.BUTTON_LINK);
	        buttonFilterLayout.addComponent(statusAll);
	        
	    	Button statusPossible = new Button("officer tasks", e -> grid.filterExcludeAssignee(LoginHelper.getCurrentUserAsReference()));
	    	statusPossible.setStyleName(ValoTheme.BUTTON_LINK);
	    	buttonFilterLayout.addComponent(statusPossible);
	        
	        Button statusInvestigated = new Button("my tasks", e -> grid.filterAssignee(LoginHelper.getCurrentUserAsReference()));
	        statusInvestigated.setStyleName(ValoTheme.BUTTON_LINK);
	        buttonFilterLayout.addComponent(statusInvestigated);
    	}
    	topLayout.addComponent(buttonFilterLayout);
        
        ComboBox statusFilter = new ComboBox();
        statusFilter.addItem("all");
        statusFilter.setNullSelectionItemId("all");
        statusFilter.addItems((Object[])TaskStatus.values());
        statusFilter.addValueChangeListener(e->grid.filterTaskStatus((TaskStatus)e.getProperty().getValue()));
        statusFilter.setValue(TaskStatus.PENDING);
        topLayout.addComponent(statusFilter);
    	
        newButton = new Button("New task");
        newButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newButton.setIcon(FontAwesome.PLUS_CIRCLE);
        newButton.addClickListener(e -> ControllerProvider.getTaskController().create());
        topLayout.addComponent(newButton);

        topLayout.setComponentAlignment(statusFilter, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(statusFilter, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	grid.reload();
    }
}
