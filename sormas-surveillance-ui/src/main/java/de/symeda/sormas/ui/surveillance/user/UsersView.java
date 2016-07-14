package de.symeda.sormas.ui.surveillance.user;

import java.util.Collection;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.samples.ResetButtonForTextField;
import de.symeda.sormas.ui.surveillance.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractView;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link UserController} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class UsersView extends AbstractView {

	private static final long serialVersionUID = -3533557348144005469L;
	
	public static final String VIEW_NAME = "users";

	private UserGrid grid;    
    private Button newCase;

	private VerticalLayout gridLayout;

    public UsersView() {
        setSizeFull();
        addStyleName("crud-view");

        grid = new UserGrid();
        grid.addItemClickListener(e -> ControllerProvider.getUserController().edit((UserDto)e.getItemId()));

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
    	
    	Button statusAll = new Button("all", e -> grid.removeAllStatusFilter());
        statusAll.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusAll);
        
//    	Button statusPossible = new Button("possible", e -> grid.setFilter(CaseStatus.POSSIBLE));
//    	statusPossible.setStyleName(ValoTheme.BUTTON_LINK);
//        topLayout.addComponent(statusPossible);
//        
//        Button statusInvestigated = new Button("investigated", e -> grid.setFilter(CaseStatus.INVESTIGATED));
//        statusInvestigated.setStyleName(ValoTheme.BUTTON_LINK);
//        topLayout.addComponent(statusInvestigated);
//        
//        ComboBox diseaseFilter = new ComboBox();
//        diseaseFilter.addItem(Disease.EBOLA);
//        diseaseFilter.addValueChangeListener(e->grid.setFilter(((Disease)e.getProperty().getValue())));
//        topLayout.addComponent(diseaseFilter);
//    	
//        TextField filter = new TextField();
//        filter.setStyleName("filter-textfield");
//        filter.setInputPrompt("Search case");
//        ResetButtonForTextField.extend(filter);
//        filter.setImmediate(true);
//        filter.addTextChangeListener(e -> grid.setFilter(e.getText()));
//        topLayout.addComponent(filter);

        newCase = new Button("New case");
        newCase.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newCase.setIcon(FontAwesome.PLUS_CIRCLE);
        newCase.addClickListener(e -> ControllerProvider.getCaseController().create());
        topLayout.addComponent(newCase);

//        topLayout.setComponentAlignment(filter, Alignment.MIDDLE_LEFT);
//        topLayout.setExpandRatio(filter, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	setData(ControllerProvider.getUserController().getAllSurveillanceOfficers());
    }

    public void clearSelection() {
        grid.getSelectionModel().reset();
    }

    public void setData(Collection<UserDto> users) {
        grid.setUsers(users);
    }

    public void refresh(UserDto product) {
        grid.refresh(product);
        grid.scrollTo(product);
    }

    public void remove(CaseDataDto caze) {
        grid.remove(caze);
    }

}
