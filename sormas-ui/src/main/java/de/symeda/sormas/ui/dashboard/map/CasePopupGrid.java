package de.symeda.sormas.ui.dashboard.map;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class CasePopupGrid extends Grid {

	public static final String DISEASE_SHORT = "diseaseShort";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	
	private final FacilityReferenceDto facility;
	private final DashboardMapComponent dashboardMapComponent;
	
	public CasePopupGrid(Window window, FacilityReferenceDto facility, DashboardMapComponent dashboardMapComponent) {
		this.facility = facility;
		this.dashboardMapComponent = dashboardMapComponent;
		setWidth(960, Unit.PIXELS);
		setHeightUndefined();
		
		setSelectionMode(SelectionMode.NONE);
		
		BeanItemContainer<CaseDataDto> container = new BeanItemContainer<CaseDataDto>(CaseDataDto.class);
        GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
        setContainerDataSource(generatedContainer);
        
        generatedContainer.addGeneratedProperty(DISEASE_SHORT, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				CaseDataDto caseDataDto = (CaseDataDto) itemId;
				String diseaseName = caseDataDto.getDisease().getName();
				return Disease.valueOf(diseaseName).toShortString();
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
        });
        
        generatedContainer.addGeneratedProperty(FIRST_NAME, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				CaseDataDto caseDataDto = (CaseDataDto) itemId;
				PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(caseDataDto.getPerson().getUuid());
				return personDto.getFirstName();
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
        });
        
        generatedContainer.addGeneratedProperty(LAST_NAME, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				CaseDataDto caseDataDto = (CaseDataDto) itemId;
				PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(caseDataDto.getPerson().getUuid());
				return personDto.getLastName();
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
        });
        
        setColumns(CaseDataDto.UUID, DISEASE_SHORT, CaseDataDto.CASE_CLASSIFICATION, FIRST_NAME,
        		LAST_NAME, CaseDataDto.REPORT_DATE, CaseDataDto.HEALTH_FACILITY_DETAILS);
        
        getColumn(CaseDataDto.UUID).setRenderer(new UuidRenderer());
        getColumn(CaseDataDto.REPORT_DATE).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat()));
        
        if (facility == null || !FacilityHelper.isOtherOrNoneHealthFacility(facility.getUuid())) {
        	getColumn(CaseDataDto.HEALTH_FACILITY_DETAILS).setHidden(true);
        }
        
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
        			CaseDataDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        addItemClickListener(e -> {
        	window.close();
        	ControllerProvider.getCaseController().navigateToCase(
        		((CaseDataDto)e.getItemId()).getUuid());
        });
        
        reload();
	}
	
	@SuppressWarnings("unchecked")
	private BeanItemContainer<CaseDataDto> getContainer() {
    	GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
        return (BeanItemContainer<CaseDataDto>) container.getWrappedContainer();
    }
	
	public void reload() {
		getContainer().removeAllItems();
		
		List<CaseDataDto> cases;
		if (facility != null) {
			cases = dashboardMapComponent.getCasesForFacility(facility);
		} else {
			cases = dashboardMapComponent.getCasesWithoutGPSTag();
		}
		
        getContainer().addAll(cases);
        this.setHeightByRows(cases.size());
    }
	
}
