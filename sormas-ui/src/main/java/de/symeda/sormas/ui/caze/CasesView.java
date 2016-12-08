package de.symeda.sormas.ui.caze;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseStatus;
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

	private CaseGrid grid;    
    private Button newCase;

	private VerticalLayout gridLayout;

    public CasesView() {
        setSizeFull();
        addStyleName("crud-view");

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
    	topLayout.setWidth(100, Unit.PERCENTAGE);
    	topLayout.addStyleName(CssStyles.VSPACE3);
    	
    	Label header = new Label("Cases");
    	header.setSizeUndefined();
    	CssStyles.style(header, CssStyles.H2, CssStyles.NO_MARGIN);
    	topLayout.addComponent(header);
    	
    	Button statusAll = new Button("all", e -> grid.removeAllStatusFilter());
        statusAll.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusAll);
        
    	Button statusPossible = new Button("possible", e -> grid.setStatusFilter(CaseStatus.POSSIBLE));
    	statusPossible.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusPossible);
        
        Button statusInvestigated = new Button("investigated", e -> grid.setStatusFilter(CaseStatus.INVESTIGATED));
        statusInvestigated.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusInvestigated);
        
        newCase = new Button("New case");
        newCase.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newCase.setIcon(FontAwesome.PLUS_CIRCLE);
        newCase.addClickListener(e -> ControllerProvider.getCaseController().create());
        topLayout.addComponent(newCase);

        topLayout.setExpandRatio(statusInvestigated, 1);
        return topLayout;
    }

	public HorizontalLayout createFilterBar() {
    	HorizontalLayout filterLayout = new HorizontalLayout();
    	filterLayout.setSpacing(true);
    	filterLayout.setSizeUndefined();
    	filterLayout.addStyleName(CssStyles.VSPACE3);

        ComboBox diseaseFilter = new ComboBox();
        diseaseFilter.setWidth(200, Unit.PIXELS);
        diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
        diseaseFilter.addItems((Object[])Disease.values());
        diseaseFilter.addValueChangeListener(e->grid.setDiseaseFilter(((Disease)e.getProperty().getValue())));
        filterLayout.addComponent(diseaseFilter);

        ComboBox districtFilter = new ComboBox();
        districtFilter.setWidth(200, Unit.PIXELS);
        districtFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT));
        UserDto user = LoginHelper.getCurrentUser();
        districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(user.getRegion().getUuid()));
        districtFilter.addValueChangeListener(e->grid.setDistrictFilter(((ReferenceDto)e.getProperty().getValue())));
        filterLayout.addComponent(districtFilter);

        ComboBox officerFilter = new ComboBox();
        officerFilter.setWidth(240, Unit.PIXELS);
        officerFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.SURVEILLANCE_OFFICER));
        officerFilter.addItems(FacadeProvider.getUserFacade().getAssignableUsers(user, UserRole.SURVEILLANCE_OFFICER));
        officerFilter.addValueChangeListener(e->grid.setSurveillanceOfficerFilter(((UserReferenceDto)e.getProperty().getValue())));
        filterLayout.addComponent(officerFilter);

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
