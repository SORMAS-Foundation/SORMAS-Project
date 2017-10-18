package de.symeda.sormas.ui.caze;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link CaseController} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class CasesView extends AbstractView {

	private static final long serialVersionUID = -3533557348144005469L;
	
	public static final String VIEW_NAME = "cases";
	
	public static final String SEARCH_FIELD = "searchField";

	private CaseGrid grid;    
    private Button createButton;

	private VerticalLayout gridLayout;

    public CasesView() {
    	super(VIEW_NAME);
    	
        createButton = new Button("New case");
        createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        createButton.setIcon(FontAwesome.PLUS_CIRCLE);
        createButton.addClickListener(e -> ControllerProvider.getCaseController().create());
        addHeaderComponent(createButton);

        grid = new CaseGrid();

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
    	topLayout.setSizeUndefined();
    	topLayout.addStyleName(CssStyles.VSPACE_3);
    	
    	Button statusAll = new Button("all", e -> grid.setInvestigationFilter(null));
        statusAll.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusAll);
        
        for (InvestigationStatus status : InvestigationStatus.values()) {
	    	Button statusButton = new Button(status.toString(), e -> grid.setInvestigationFilter(status));
	    	statusButton.setStyleName(ValoTheme.BUTTON_LINK);
	        topLayout.addComponent(statusButton);
        }

        return topLayout;
    }

	public HorizontalLayout createFilterBar() {
    	HorizontalLayout filterLayout = new HorizontalLayout();
    	filterLayout.setSpacing(true);
    	filterLayout.setSizeUndefined();
    	filterLayout.addStyleName(CssStyles.VSPACE_3);

        ComboBox diseaseFilter = new ComboBox();
        diseaseFilter.setWidth(200, Unit.PIXELS);
        diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
        diseaseFilter.addItems((Object[])Disease.values());
        diseaseFilter.addValueChangeListener(e->grid.setDiseaseFilter(((Disease)e.getProperty().getValue())));
        filterLayout.addComponent(diseaseFilter);
        
        ComboBox classificationFilter = new ComboBox();
        classificationFilter.setWidth(200, Unit.PIXELS);
        classificationFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_CLASSIFICATION));
        classificationFilter.addItems((Object[])CaseClassification.values());
        classificationFilter.addValueChangeListener(e->grid.setClassificationFilter(((CaseClassification)e.getProperty().getValue())));
        filterLayout.addComponent(classificationFilter);        

        UserDto user = LoginHelper.getCurrentUser();
        if(user.getRegion() != null) {
	        ComboBox districtFilter = new ComboBox();
	        districtFilter.setWidth(200, Unit.PIXELS);
	        districtFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT));
	        districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(user.getRegion().getUuid()));
	        districtFilter.addValueChangeListener(e->grid.setDistrictFilter(((DistrictReferenceDto)e.getProperty().getValue())));
	        filterLayout.addComponent(districtFilter);
        }

        ComboBox officerFilter = new ComboBox();
        officerFilter.setWidth(240, Unit.PIXELS);
        officerFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.SURVEILLANCE_OFFICER));
        officerFilter.addItems(FacadeProvider.getUserFacade().getAssignableUsers(user, UserRole.SURVEILLANCE_OFFICER));
        officerFilter.addValueChangeListener(e->grid.setSurveillanceOfficerFilter(((UserReferenceDto)e.getProperty().getValue())));
        filterLayout.addComponent(officerFilter);
        
        TextField searchField = new TextField();
		searchField.setWidth(200, Unit.PIXELS);
		searchField.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, SEARCH_FIELD));
		searchField.addTextChangeListener(e->grid.filterByText(e.getText()));
		filterLayout.addComponent(searchField);

        return filterLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	grid.reload();
    }

    public void clearSelection() {
        grid.getSelectionModel().reset();
    }

    public void refresh(CaseDataDto product) {
        grid.refresh(product);
        grid.scrollTo(product);
    }

    public void remove(CaseDataDto caze) {
        grid.remove(caze);
    }

}
