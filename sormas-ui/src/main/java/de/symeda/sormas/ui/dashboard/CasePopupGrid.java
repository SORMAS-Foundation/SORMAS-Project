package de.symeda.sormas.ui.dashboard;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class CasePopupGrid extends Grid {

	public static final String DISEASE_SHORT = "diseaseShort";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	
	private final FacilityDto facility;
	private final MapComponent mapComponent;
	
	public CasePopupGrid(Window window, FacilityDto facility, MapComponent mapComponent) {
		this.facility = facility;
		this.mapComponent = mapComponent;
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
				return caseDataDto.getPerson().getFirstName();
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
				return caseDataDto.getPerson().getLastName();
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
        });
        
        setColumns(CaseDataDto.UUID, DISEASE_SHORT, CaseDataDto.CASE_CLASSIFICATION, FIRST_NAME,
        		LAST_NAME, CaseDataDto.REPORT_DATE);
        
        getColumn(CaseDataDto.UUID).setRenderer(new UuidRenderer());
        
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
        			CaseDataDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        addItemClickListener(e -> {
        	window.close();
        	ControllerProvider.getCaseController().navigateToData(
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
		List<CaseDataDto> cases = mapComponent.getCasesForFacility(facility);
        getContainer().addAll(cases);
        this.setHeightByRows(cases.size());
    }
	
}
