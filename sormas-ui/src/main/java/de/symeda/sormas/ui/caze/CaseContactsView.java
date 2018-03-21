package de.symeda.sormas.ui.caze;

import java.util.Date;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.contact.ContactGrid;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.DownloadUtil;

public class CaseContactsView extends AbstractCaseView {

	private static final long serialVersionUID = -1L;
	
	public static final String VIEW_NAME = "cases/contacts";

	private ContactGrid grid;    
	private ComboBox classificationFilter;
	private ComboBox districtFilter;
	private ComboBox officerFilter;
    private Button newButton;
	private VerticalLayout gridLayout;

    public CaseContactsView() {
    	super(VIEW_NAME);
        setSizeFull();

        grid = new ContactGrid();

        gridLayout = new VerticalLayout();
        gridLayout.addComponent(createTopBar());
        gridLayout.addComponent(createFilterBar());
        gridLayout.addComponent(grid);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(false);
        gridLayout.setSizeFull();
        gridLayout.setExpandRatio(grid, 1);
        
        setSubComponent(gridLayout);
    }

	public HorizontalLayout createTopBar() {
    	HorizontalLayout topLayout = new HorizontalLayout();
    	topLayout.setSpacing(true);
    	topLayout.setWidth("100%");
    	
    	Button statusAll = new Button("all", e -> grid.setStatusFilter(null));
        statusAll.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusAll);
        
        for (ContactStatus status : ContactStatus.values()) {
	    	Button statusButton = new Button(status.toString(), e -> {
	    		grid.setStatusFilter(status);
	    		grid.reload();
	    	});
	    	statusButton.setStyleName(ValoTheme.BUTTON_LINK);
	        topLayout.addComponent(statusButton);
        }
        topLayout.setExpandRatio(topLayout.getComponent(topLayout.getComponentCount()-1), 1);

        if (LoginHelper.hasUserRight(UserRight.CONTACT_EXPORT)) {
			Button exportButton = new Button("Export");
			exportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			exportButton.setIcon(FontAwesome.DOWNLOAD);
			
			StreamResource streamResource = DownloadUtil.createGridExportStreamResource(grid, "sormas_contacts", "sormas_contacts_" + DateHelper.formatDateForExport(new Date()) + ".csv", "text/csv");
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);
			
			topLayout.addComponent(exportButton);
			topLayout.setComponentAlignment(exportButton, Alignment.MIDDLE_RIGHT);
			topLayout.setExpandRatio(exportButton, 1);
		}
        
        if (LoginHelper.hasUserRight(UserRight.CONTACT_CREATE)) {
	        newButton = new Button("New contact");
	        newButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
	        newButton.setIcon(FontAwesome.PLUS_CIRCLE);
	        newButton.addClickListener(e -> ControllerProvider.getContactController().create(this.getCaseRef()));
	        topLayout.addComponent(newButton);
	        topLayout.setComponentAlignment(newButton, Alignment.MIDDLE_RIGHT);
        }
        
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

	public HorizontalLayout createFilterBar() {
    	HorizontalLayout topLayout = new HorizontalLayout();
    	topLayout.setSpacing(true);
    	topLayout.setWidth(100, Unit.PERCENTAGE);
    	
    	classificationFilter = new ComboBox();
    	classificationFilter.setWidth(240, Unit.PIXELS);
    	classificationFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_CLASSIFICATION));
    	classificationFilter.addValueChangeListener(e -> {
        	ContactClassification classification = (ContactClassification) e.getProperty().getValue();
        	grid.setClassificationFilter(classification);
        });
        topLayout.addComponent(classificationFilter);
    	
        districtFilter = new ComboBox();
        districtFilter.setWidth(240, Unit.PIXELS);
        districtFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_DISTRICT_UUID));
        districtFilter.addValueChangeListener(e -> {
        	DistrictReferenceDto district = (DistrictReferenceDto) e.getProperty().getValue();
        	grid.setDistrictFilter(district != null ? district.getUuid() : null);
        });
        topLayout.addComponent(districtFilter);

        officerFilter = new ComboBox();
        officerFilter.setWidth(240, Unit.PIXELS);
        officerFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_OFFICER_UUID));
        officerFilter.addValueChangeListener(e -> {
        	UserReferenceDto officer = (UserReferenceDto) e.getProperty().getValue();
        	grid.setContactOfficerFilter(officer != null ? officer.getUuid() : null);
        });
        topLayout.addComponent(officerFilter);

        topLayout.setExpandRatio(officerFilter, 1);
        return topLayout;
    }
	
	private void update() {
    	grid.setCaseFilter(getCaseRef());

    	CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());

    	classificationFilter.removeAllItems();
    	classificationFilter.addItems((Object[]) ContactClassification.values());
    	
    	districtFilter.removeAllItems();
        districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(caseDto.getRegion().getUuid()));

        officerFilter.removeAllItems();
    	officerFilter.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(caseDto.getRegion(), UserRole.CONTACT_OFFICER));
	}

    @Override
    public void enter(ViewChangeEvent event) {
    	super.enter(event);
    	update();
    }

}
