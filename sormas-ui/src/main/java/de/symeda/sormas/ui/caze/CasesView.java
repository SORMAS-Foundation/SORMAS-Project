package de.symeda.sormas.ui.caze;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

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
	private Button importButton;
    private Button createButton;

	private VerticalLayout gridLayout;

    public CasesView() {
    	super(VIEW_NAME);
    	
    	if (LoginHelper.hasUserRight(UserRight.CASE_IMPORT)) {
    		importButton = new Button("Import");
    		importButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
    		importButton.setIcon(FontAwesome.UPLOAD);
    		importButton.addClickListener(e -> {
    			Window popupWindow = VaadinUiUtil.showPopupWindow(new CaseImportLayout());
    			popupWindow.setCaption("Import cases");
    			popupWindow.addCloseListener(c -> {
    				grid.reload();
    			});
    		});
    		addHeaderComponent(importButton);
    	}
    	
    	if (LoginHelper.hasUserRight(UserRight.CASE_CREATE)) {
	        createButton = new Button("New case");
	        createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
	        createButton.setIcon(FontAwesome.PLUS_CIRCLE);
	        createButton.addClickListener(e -> ControllerProvider.getCaseController().create());
	        addHeaderComponent(createButton);
    	}

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

    	ComboBox outcomeFilter = new ComboBox();
    	outcomeFilter.setWidth(140, Unit.PIXELS);
    	outcomeFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.OUTCOME));
    	outcomeFilter.addItems((Object[]) CaseOutcome.values());
    	outcomeFilter.addValueChangeListener(e -> grid.setOutcomeFilter(((CaseOutcome) e.getProperty().getValue())));
    	filterLayout.addComponent(outcomeFilter);
    	outcomeFilter.setValue(CaseOutcome.NO_OUTCOME);
    	
        ComboBox diseaseFilter = new ComboBox();
        diseaseFilter.setWidth(140, Unit.PIXELS);
        diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
        diseaseFilter.addItems((Object[])Disease.values());
        diseaseFilter.addValueChangeListener(e->grid.setDiseaseFilter(((Disease)e.getProperty().getValue())));
        filterLayout.addComponent(diseaseFilter);
        
        ComboBox classificationFilter = new ComboBox();
        classificationFilter.setWidth(140, Unit.PIXELS);
        classificationFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_CLASSIFICATION));
        classificationFilter.addItems((Object[])CaseClassification.values());
        classificationFilter.addValueChangeListener(e->grid.setClassificationFilter(((CaseClassification)e.getProperty().getValue())));
        filterLayout.addComponent(classificationFilter);        

        ComboBox presentConditionFilter = new ComboBox();
        presentConditionFilter.setWidth(140, Unit.PIXELS);
        presentConditionFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.PRESENT_CONDITION));
        presentConditionFilter.addItems((Object[])PresentCondition.values());
        presentConditionFilter.addValueChangeListener(e->grid.setPresentConditionFilter(((PresentCondition)e.getProperty().getValue())));
        filterLayout.addComponent(presentConditionFilter);        

        UserDto user = LoginHelper.getCurrentUser();

        ComboBox regionFilter = new ComboBox();
        if (user.getRegion() == null) {
            regionFilter.setWidth(140, Unit.PIXELS);
            regionFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REGION));
            regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
            regionFilter.addValueChangeListener(e -> {
            	RegionReferenceDto region = (RegionReferenceDto)e.getProperty().getValue();
            	grid.setRegionFilter(region);
            });
            filterLayout.addComponent(regionFilter);
        }

        ComboBox districtFilter = new ComboBox();
        districtFilter.setWidth(140, Unit.PIXELS);
        districtFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT));
        districtFilter.setDescription("Select a district in the state");
        districtFilter.addValueChangeListener(e->grid.setDistrictFilter(((DistrictReferenceDto)e.getProperty().getValue())));

        if (user.getRegion() != null) {
            districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(user.getRegion().getUuid()));
            districtFilter.setEnabled(true);
        } else {
            regionFilter.addValueChangeListener(e -> {
            	RegionReferenceDto region = (RegionReferenceDto)e.getProperty().getValue();
            	districtFilter.removeAllItems();
            	if (region != null) {
            		districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(region.getUuid()));
                	districtFilter.setEnabled(true);
            	} else {
                	districtFilter.setEnabled(false);
            	}
            });
            districtFilter.setEnabled(false);
        }
        filterLayout.addComponent(districtFilter);

        ComboBox facilityFilter = new ComboBox();
        facilityFilter.setWidth(140, Unit.PIXELS);
        facilityFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY));
        facilityFilter.setDescription("Select a facility in the LGA");
        facilityFilter.addValueChangeListener(e->grid.setHealthFacilityFilter(((FacilityReferenceDto)e.getProperty().getValue())));
        facilityFilter.setEnabled(false);
        filterLayout.addComponent(facilityFilter);

        districtFilter.addValueChangeListener(e-> {
        	facilityFilter.removeAllItems();
        	DistrictReferenceDto district = (DistrictReferenceDto)e.getProperty().getValue();
        	if (district != null) {
        		facilityFilter.addItems(FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict(district, true));
        		facilityFilter.setEnabled(true);
        	} else {
        		facilityFilter.setEnabled(false);
        	}
        });
        
        ComboBox officerFilter = new ComboBox();
        officerFilter.setWidth(140, Unit.PIXELS);
        officerFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.SURVEILLANCE_OFFICER));
        if (user.getRegion() != null) {
        	officerFilter.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(user.getRegion(), UserRole.SURVEILLANCE_OFFICER));
        }
        officerFilter.addValueChangeListener(e->grid.setSurveillanceOfficerFilter(((UserReferenceDto)e.getProperty().getValue())));
        filterLayout.addComponent(officerFilter);
        
        ComboBox reportedByFilter = new ComboBox();
        reportedByFilter.setWidth(140, Unit.PIXELS);
        reportedByFilter.setInputPrompt("Reported By");
        reportedByFilter.addItems((Object[]) UserRole.values());
        reportedByFilter.addValueChangeListener(e -> {
        	grid.setReportedByFilter((UserRole) e.getProperty().getValue());
        });
        filterLayout.addComponent(reportedByFilter);
        
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
}
