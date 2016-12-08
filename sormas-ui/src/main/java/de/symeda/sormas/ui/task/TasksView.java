package de.symeda.sormas.ui.task;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class TasksView extends AbstractView {

	public static final String VIEW_NAME = "tasks";

	private TaskGrid grid;    
    private Button createButton;

	private VerticalLayout gridLayout;

    public TasksView() {
        setSizeFull();
        addStyleName("crud-view");

        grid = new TaskGrid();

        gridLayout = new VerticalLayout();
        gridLayout.addComponent(createTopBar());
        gridLayout.addComponent(createFilterBar());
        gridLayout.addComponent(grid);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(false);
        gridLayout.setSizeFull();
        gridLayout.setExpandRatio(grid, 1);
        gridLayout.setStyleName("crud-main-layout");
        
        addComponent(gridLayout);
    }


	public HorizontalLayout createTopBar() {
    	HorizontalLayout topLayout = new HorizontalLayout();
    	topLayout.setSpacing(true);
    	topLayout.setWidth(100, Unit.PERCENTAGE);
    	topLayout.addStyleName(CssStyles.VSPACE3);
    	
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
    	
        createButton = new Button("New task");
        createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        createButton.setIcon(FontAwesome.PLUS_CIRCLE);
        createButton.addClickListener(e -> ControllerProvider.getTaskController().create());
        topLayout.addComponent(createButton);
        topLayout.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
        topLayout.setExpandRatio(createButton, 1);

        return topLayout;
    }
	
	public HorizontalLayout createFilterBar() {
    	HorizontalLayout filterLayout = new HorizontalLayout();
    	filterLayout.setSpacing(true);
    	filterLayout.setSizeUndefined();
    	filterLayout.addStyleName(CssStyles.VSPACE3);
    	        
        ComboBox statusFilter = new ComboBox();
        statusFilter.setWidth(200, Unit.PIXELS);
        statusFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(TaskDto.I18N_PREFIX, TaskDto.TASK_STATUS));
//        statusFilter.addItem("all");
//        statusFilter.setNullSelectionItemId("all");
        statusFilter.addItems((Object[])TaskStatus.values());
        statusFilter.addValueChangeListener(e->grid.filterTaskStatus((TaskStatus)e.getProperty().getValue()));
        statusFilter.setValue(TaskStatus.PENDING);
        filterLayout.addComponent(statusFilter);

        return filterLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	grid.reload();
    }
}
