package de.symeda.sormas.ui.caze;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class CaseGrid extends Grid {
	
	public static final String NUMBER_OF_PENDING_TASKS = "numberOfPendingTasks";
	public static final String DISEASE_SHORT = "diseaseShort";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	
	public CaseGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.NONE);

        BeanItemContainer<CaseDataDto> container = new BeanItemContainer<CaseDataDto>(CaseDataDto.class);
        GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
        setContainerDataSource(generatedContainer);
        
        generatedContainer.addGeneratedProperty(NUMBER_OF_PENDING_TASKS, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				CaseDataDto caseDataDto = (CaseDataDto)itemId;
				return String.format(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, NUMBER_OF_PENDING_TASKS + "Format"), 
						FacadeProvider.getTaskFacade().getPendingTaskCountByCase(caseDataDto));
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
        
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
        
        setColumns(CaseDataDto.UUID, DISEASE_SHORT, 
        		CaseDataDto.CASE_CLASSIFICATION, CaseDataDto.INVESTIGATION_STATUS, FIRST_NAME, LAST_NAME, 
        		CaseDataDto.DISTRICT, CaseDataDto.REPORT_DATE, NUMBER_OF_PENDING_TASKS);

        getColumn(CaseDataDto.UUID).setRenderer(new UuidRenderer());
        getColumn(CaseDataDto.REPORT_DATE).setRenderer(new DateRenderer(DateHelper.getDateTimeFormat()));
 
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
        			CaseDataDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        addItemClickListener(e -> ControllerProvider.getCaseController().navigateToData(
        		((CaseDataDto)e.getItemId()).getUuid()));
        
        reload();
	}
	
    /**
     * Filter the grid based on a search string that is searched for in the
     * product name, availability and category columns.
     *
     * @param filterString
     *            string to look for
     */
    public void setFilter(String filterString) {
    	getContainer().removeContainerFilters(CaseDataDto.PERSON);
        if (filterString.length() > 0) {
            SimpleStringFilter nameFilter = new SimpleStringFilter(CaseDataDto.PERSON, filterString, true, false);
            getContainer().addContainerFilter(
//            new Or(nameFilter, descFilter, statusFilter));
            new Or(nameFilter));
        }

    }
    
    public void setDiseaseFilter(Disease disease) {
		getContainer().removeContainerFilters(CaseDataDto.DISEASE);
		if (disease != null) {
	    	Equal filter = new Equal(CaseDataDto.DISEASE, disease);  
	        getContainer().addContainerFilter(filter);
		}
	}

    public void setDistrictFilter(DistrictReferenceDto district) {
		getContainer().removeContainerFilters(CaseDataDto.DISTRICT);
		if (district != null) {
	    	Equal filter = new Equal(CaseDataDto.DISTRICT, district);  
	        getContainer().addContainerFilter(filter);
		}
	}
    
    public void setSurveillanceOfficerFilter(UserReferenceDto surveillanceOfficer) {
		getContainer().removeContainerFilters(CaseDataDto.SURVEILLANCE_OFFICER);
		if (surveillanceOfficer != null) {
	    	Equal filter = new Equal(CaseDataDto.SURVEILLANCE_OFFICER, surveillanceOfficer);  
	        getContainer().addContainerFilter(filter);
		}
	}

	public void setClassificationFilter(CaseClassification classficiation) {
		getContainer().removeContainerFilters(CaseDataDto.CASE_CLASSIFICATION);
    	if (classficiation != null) {
	    	Equal filter = new Equal(CaseDataDto.CASE_CLASSIFICATION, classficiation);  
	        getContainer().addContainerFilter(filter);
    	}
    }

	public void setInvestigationFilter(InvestigationStatus status) {
		getContainer().removeContainerFilters(CaseDataDto.INVESTIGATION_STATUS);
    	if (status != null) {
	    	Equal filter = new Equal(CaseDataDto.INVESTIGATION_STATUS, status);  
	        getContainer().addContainerFilter(filter);
    	}
    }
	
	public void filterByText(String text) {
		getContainer().removeContainerFilters(CaseDataDto.UUID);
		getContainer().removeContainerFilters(CaseDataDto.PERSON);
		if(text != null && !text.isEmpty()) {
			Or textFilter = new Or(new SimpleStringFilter(CaseDataDto.UUID, text, true, false), new SimpleStringFilter(CaseDataDto.PERSON, text, true, false));
			getContainer().addContainerFilter(textFilter);
		}
	}
	
    @SuppressWarnings("unchecked")
	private BeanItemContainer<CaseDataDto> getContainer() {
    	GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
        return (BeanItemContainer<CaseDataDto>) container.getWrappedContainer();
    }
    
    public void reload() {
    	List<CaseDataDto> cases = ControllerProvider.getCaseController().getCaseIndexList();
        getContainer().removeAllItems();
        getContainer().addAll(cases);    	
    }

    public void refresh(CaseDataDto caze) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<CaseDataDto> item = getContainer().getItem(caze);
        if (item != null) {
            // Updated product
            @SuppressWarnings("rawtypes")
			MethodProperty p = (MethodProperty) item.getItemProperty(CaseDataDto.UUID);
            p.fireValueChange();
        } else {
            // New product
            getContainer().addBean(caze);
        }
    }

    public void remove(CaseDataDto caze) {
        getContainer().removeItem(caze);
    }
}


