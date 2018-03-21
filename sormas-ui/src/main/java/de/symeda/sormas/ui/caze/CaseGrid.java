package de.symeda.sormas.ui.caze;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
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
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.DateFilter;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class CaseGrid extends Grid {
	
	public static final String DISEASE_SHORT = "diseaseShort";
	public static final String NUMBER_OF_PENDING_TASKS = "numberOfPendingTasks";
	
	private CaseCriteria caseCriteria = new CaseCriteria();
	
	public CaseGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.NONE);

        BeanItemContainer<CaseIndexDto> container = new BeanItemContainer<CaseIndexDto>(CaseIndexDto.class);
        GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
        setContainerDataSource(generatedContainer);
        
        // TODO move to index dto
        generatedContainer.addGeneratedProperty(NUMBER_OF_PENDING_TASKS, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				CaseIndexDto caseDto = (CaseIndexDto)itemId;
				return String.format(I18nProperties.getPrefixFieldCaption(CaseIndexDto.I18N_PREFIX, NUMBER_OF_PENDING_TASKS + "Format"), 
						FacadeProvider.getTaskFacade().getPendingTaskCountByCase(caseDto.toReference()));
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
        
        generatedContainer.addGeneratedProperty(DISEASE_SHORT, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				CaseIndexDto caseDto = (CaseIndexDto) itemId;
				return caseDto.getDisease() != Disease.OTHER 
						? (caseDto.getDisease() != null ? caseDto.getDisease().toShortString() : "")
						: DataHelper.toStringNullable(caseDto.getDiseaseDetails());
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
        });
        
        setColumns(CaseIndexDto.UUID, CaseIndexDto.EPID_NUMBER, DISEASE_SHORT, 
        		CaseIndexDto.CASE_CLASSIFICATION, CaseIndexDto.OUTCOME, CaseIndexDto.INVESTIGATION_STATUS, 
        		CaseIndexDto.PERSON_FIRST_NAME, CaseIndexDto.PERSON_LAST_NAME, 
        		CaseIndexDto.DISTRICT_NAME, CaseIndexDto.REPORT_DATE, NUMBER_OF_PENDING_TASKS);

        getColumn(CaseIndexDto.UUID).setRenderer(new UuidRenderer());
        getColumn(CaseIndexDto.REPORT_DATE).setRenderer(new DateRenderer(DateHelper.getDateTimeFormat()));
 
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
        			CaseIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        addItemClickListener(e ->  {
        	if (e.getPropertyId().equals(CaseIndexDto.UUID) || e.isDoubleClick()) {
        		ControllerProvider.getCaseController().navigateToCase(((CaseIndexDto) e.getItemId()).getUuid());
        	}
        });
	}
	
	public void setOutcomeFilter(CaseOutcome outcome) {
		if (outcome == null) {
			this.getColumn(CaseIndexDto.OUTCOME).setHidden(false);
		} else if (this.getColumn(CaseIndexDto.OUTCOME) != null) {
			this.getColumn(CaseIndexDto.OUTCOME).setHidden(true);
		}
		caseCriteria.outcomeEquals(outcome);
		reload();
	}
    
    public void setDiseaseFilter(Disease disease) {
		caseCriteria.diseaseEquals(disease);
		reload();
	}

    public void setRegionFilter(RegionReferenceDto region) {
		getContainer().removeContainerFilters(CaseIndexDto.REGION_UUID);
		if (region != null) {
	    	Equal filter = new Equal(CaseIndexDto.REGION_UUID, region.getUuid());  
	        getContainer().addContainerFilter(filter);
		}
	}

    public void setDistrictFilter(DistrictReferenceDto district) {
		getContainer().removeContainerFilters(CaseIndexDto.DISTRICT_UUID);
		if (district != null) {
	    	Equal filter = new Equal(CaseIndexDto.DISTRICT_UUID, district.getUuid());  
	        getContainer().addContainerFilter(filter);
		}
	}

    public void setHealthFacilityFilter(FacilityReferenceDto facility) {
		getContainer().removeContainerFilters(CaseIndexDto.HEALTH_FACILITY_UUID);
		if (facility != null) {
	    	Equal filter = new Equal(CaseIndexDto.HEALTH_FACILITY_UUID, facility.getUuid());  
	        getContainer().addContainerFilter(filter);
		}
	}
    
    public void setSurveillanceOfficerFilter(UserReferenceDto surveillanceOfficer) {
		getContainer().removeContainerFilters(CaseIndexDto.SURVEILLANCE_OFFICER_UUID);
		if (surveillanceOfficer != null) {
	    	Equal filter = new Equal(CaseIndexDto.SURVEILLANCE_OFFICER_UUID, surveillanceOfficer.getUuid());  
	        getContainer().addContainerFilter(filter);
		}
	}
    
    public void setReportedByFilter(UserRole reportingUserRole) {
    	caseCriteria.reportingUserHasRole(reportingUserRole);
    	reload();
    }

	public void setClassificationFilter(CaseClassification classficiation) {
		getContainer().removeContainerFilters(CaseIndexDto.CASE_CLASSIFICATION);
    	if (classficiation != null) {
	    	Equal filter = new Equal(CaseIndexDto.CASE_CLASSIFICATION, classficiation);  
	        getContainer().addContainerFilter(filter);
    	}
    }

	public void setInvestigationFilter(InvestigationStatus status) {
		getContainer().removeContainerFilters(CaseIndexDto.INVESTIGATION_STATUS);
    	if (status != null) {
	    	Equal filter = new Equal(CaseIndexDto.INVESTIGATION_STATUS, status);  
	        getContainer().addContainerFilter(filter);
    	}
    }
	
	public void setPresentConditionFilter(PresentCondition presentCondition) {
		getContainer().removeContainerFilters(CaseIndexDto.PRESENT_CONDITION);
    	if (presentCondition != null) {
	    	Equal filter = new Equal(CaseIndexDto.PRESENT_CONDITION, presentCondition);  
	        getContainer().addContainerFilter(filter);
    	}
    }
	
	public void setDateFilter(Date fromDate, Date toDate) {
		caseCriteria.newCaseDateBetween(fromDate, toDate);
		reload();
	}
	
	public void filterByText(String text) {
		getContainer().removeContainerFilters(CaseIndexDto.UUID);
		getContainer().removeContainerFilters(CaseIndexDto.PERSON_FIRST_NAME);
		getContainer().removeContainerFilters(CaseIndexDto.PERSON_LAST_NAME);
    	getContainer().removeContainerFilters(CaseIndexDto.EPID_NUMBER);
    	getContainer().removeContainerFilters(CaseIndexDto.REPORT_DATE);

    	if (text != null && !text.isEmpty()) {
    		List<Filter> orFilters = new ArrayList<Filter>();
    		String[] words = text.split("\\s+");
    		for (String word : words) {
    			orFilters.add(new SimpleStringFilter(CaseIndexDto.UUID, word, true, false));
    			orFilters.add(new SimpleStringFilter(CaseIndexDto.PERSON_FIRST_NAME, word, true, false));
    			orFilters.add(new SimpleStringFilter(CaseIndexDto.PERSON_LAST_NAME, word, true, false));
    			orFilters.add(new SimpleStringFilter(CaseIndexDto.EPID_NUMBER, word, true, false));
    		}
    		orFilters.add(new DateFilter(CaseIndexDto.REPORT_DATE, text));
            getContainer().addContainerFilter(new Or(orFilters.stream().toArray(Filter[]::new)));
		}
	}
	
    @SuppressWarnings("unchecked")
	public BeanItemContainer<CaseIndexDto> getContainer() {
    	GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
        return (BeanItemContainer<CaseIndexDto>) container.getWrappedContainer();
    }
    
    public void reload() {
    	
    	List<CaseIndexDto> cases = FacadeProvider.getCaseFacade().getIndexList(
    			LoginHelper.getCurrentUser().getUuid(), 
    			caseCriteria);

    	getContainer().removeAllItems();
        getContainer().addAll(cases);
    }
   
}