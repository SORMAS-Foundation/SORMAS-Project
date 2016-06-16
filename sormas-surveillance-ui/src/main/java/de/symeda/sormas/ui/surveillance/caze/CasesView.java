package de.symeda.sormas.ui.surveillance.caze;

import java.util.Collection;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDto;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.samples.ResetButtonForTextField;
import de.symeda.sormas.ui.surveillance.navigation.CaseNavigation;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link CaseController} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class CasesView extends CssLayout implements View {

	private static final long serialVersionUID = -3533557348144005469L;
	
	public static final String VIEW_NAME = "cases";
    private CaseGrid grid;
    

    private CaseController viewLogic = new CaseController(this);
    private Button newCase;

	private VerticalLayout gridLayout;

	private VerticalLayout caseNavigationLayout;
	private CaseNavigation caseNavigation;


    public CasesView() {
        setSizeFull();
        addStyleName("crud-view");

        grid = new CaseGrid();
        grid.addSelectionListener(e -> viewLogic.rowSelected(grid.getSelectedRow()));
        gridLayout = new VerticalLayout();
        gridLayout.addComponent(createTopBar());
        gridLayout.addComponent(grid);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);
        gridLayout.setSizeFull();
        gridLayout.setExpandRatio(grid, 1);
        gridLayout.setStyleName("crud-main-layout");
        addComponent(gridLayout);
        
        caseNavigation = new CaseNavigation(viewLogic);
		caseNavigationLayout = new VerticalLayout(caseNavigation);
        caseNavigationLayout.setMargin(true);
        caseNavigationLayout.setSpacing(true);
        caseNavigationLayout.setSizeFull();
        addComponent(caseNavigationLayout);

        viewLogic.init();
    }

    public HorizontalLayout createTopBar() {
    	HorizontalLayout topLayout = new HorizontalLayout();
    	topLayout.setSpacing(true);
    	topLayout.setWidth("100%");
    	
    	Button statusAll = new Button("all", e -> grid.removeAllFilter());
        statusAll.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusAll);
        
        //topLayout.addComponent(new Label("<h3>status:</h3>", ContentMode.HTML));
    	
    	Button statusProbable = new Button("probable", e -> grid.setFilter(CaseStatus.PROBABLE));
    	statusProbable.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusProbable);
        
        Button statusInvestigated = new Button("investigated", e -> grid.setFilter(CaseStatus.INVESTIGATED));
        statusInvestigated.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusInvestigated);
        
        ComboBox diseaseFilter = new ComboBox();
        diseaseFilter.addItem(Disease.EBOLA);
        diseaseFilter.addValueChangeListener(e->grid.setFilter(((Disease)e.getProperty().getValue())));
        topLayout.addComponent(diseaseFilter);
    	
        TextField filter = new TextField();
        filter.setStyleName("filter-textfield");
        filter.setInputPrompt("Search case");
        ResetButtonForTextField.extend(filter);
        filter.setImmediate(true);
        filter.addTextChangeListener(e -> grid.setFilter(e.getText()));
        topLayout.addComponent(filter);

        newCase = new Button("New case");
        newCase.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newCase.setIcon(FontAwesome.PLUS_CIRCLE);
        newCase.addClickListener(e -> viewLogic.newCase());
        topLayout.addComponent(newCase);

        topLayout.setComponentAlignment(filter, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(filter, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        viewLogic.enter(event.getParameters());
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

    public void setNewCaseEnabled(boolean enabled) {
        newCase.setEnabled(enabled);
    }

    public void clearSelection() {
        grid.getSelectionModel().reset();
    }

    public void selectRow(CaseDto row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public CaseDto getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void edit(CaseDto caze) {
        if (caze != null) {
        	caseNavigationLayout.setVisible(true);
        	caseNavigation.setInitialSelectedTab();
            gridLayout.setVisible(false);
        } else {
        	caseNavigationLayout.setVisible(false);
        	gridLayout.setVisible(true);
        }
    }

    public void show(Collection<CaseDto> cases) {
        grid.setCases(cases);
    }

    public void refresh(CaseDto product) {
        grid.refresh(product);
        grid.scrollTo(product);
    }

    public void remove(CaseDto caze) {
        grid.remove(caze);
    }

}
