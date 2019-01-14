/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.caze;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.utils.DateFilter;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class CaseGrid extends Grid {
	
	public static final String DISEASE_SHORT = "diseaseShort";
	public static final String NUMBER_OF_PENDING_TASKS = "numberOfPendingTasks";
	
	private CaseCriteria caseCriteria = new CaseCriteria();
	private boolean reloadEnabled = false;
	
	public CaseGrid() {
        setSizeFull();
        
        caseCriteria.archived(false);

        if (CurrentUser.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
        	setSelectionMode(SelectionMode.MULTI);
        } else {
        	setSelectionMode(SelectionMode.NONE);
        }
        
        BeanItemContainer<CaseIndexDto> container = new BeanItemContainer<CaseIndexDto>(CaseIndexDto.class);
        GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
        setContainerDataSource(generatedContainer);
        
        // TODO move to index dto
        generatedContainer.addGeneratedProperty(NUMBER_OF_PENDING_TASKS, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				CaseIndexDto caseDto = (CaseIndexDto)itemId;
				return String.format(I18nProperties.getPrefixCaption(CaseIndexDto.I18N_PREFIX, NUMBER_OF_PENDING_TASKS + "Format"), 
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
				return DiseaseHelper.toString(caseDto.getDisease(), caseDto.getDiseaseDetails());
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
        });
        
        setColumns(CaseIndexDto.UUID, CaseIndexDto.EPID_NUMBER, DISEASE_SHORT, 
        		CaseIndexDto.CASE_CLASSIFICATION, CaseIndexDto.OUTCOME, CaseIndexDto.INVESTIGATION_STATUS, 
        		CaseIndexDto.PERSON_FIRST_NAME, CaseIndexDto.PERSON_LAST_NAME, 
        		CaseIndexDto.DISTRICT_NAME, CaseIndexDto.HEALTH_FACILITY_NAME,
        		CaseIndexDto.REPORT_DATE, CaseIndexDto.CREATION_DATE, NUMBER_OF_PENDING_TASKS);

        getColumn(CaseIndexDto.UUID).setRenderer(new UuidRenderer());
        getColumn(CaseIndexDto.REPORT_DATE).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat()));
        
        if (CurrentUser.getCurrent().hasUserRight(UserRight.CASE_IMPORT)) {
            getColumn(CaseIndexDto.CREATION_DATE).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat()));
        } else {
        	removeColumn(CaseIndexDto.CREATION_DATE);
        }
 
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixCaption(
        			CaseIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        addItemClickListener(e ->  {
        	if (e.getPropertyId() != null && (e.getPropertyId().equals(CaseIndexDto.UUID) || e.isDoubleClick())) {
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
		caseCriteria.regionEquals(region);
		reload();
	}

    public void setDistrictFilter(DistrictReferenceDto district) {
		caseCriteria.districtEquals(district);
		reload();
	}

    public void setHealthFacilityFilter(FacilityReferenceDto facility) {
		caseCriteria.healthFacility(facility);
		reload();
	}
    
    public void setSurveillanceOfficerFilter(UserReferenceDto surveillanceOfficer) {
    	caseCriteria.surveillanceOfficer(surveillanceOfficer);
    	reload();
	}
    
    public void setReportedByFilter(UserRole reportingUserRole) {
    	caseCriteria.reportingUserHasRole(reportingUserRole);
    	reload();
    }

	public void setClassificationFilter(CaseClassification classficiation) {
    	caseCriteria.caseClassification(classficiation);
    	reload();
    }

	public void setInvestigationFilter(InvestigationStatus status) {
    	caseCriteria.investigationStatus(status);
    	reload();
    }
	
	public void setPresentConditionFilter(PresentCondition presentCondition) {
    	caseCriteria.presentCondition(presentCondition);
    	reload();
    }
	
	public void setNoGeoCoordinatesFilter(boolean showOnlyCasesWithoutGPSCoords) {
		caseCriteria.mustHaveNoGeoCoordinatesEquals(showOnlyCasesWithoutGPSCoords);
		reload();
	}
	
	public void setDateFilter(Date fromDate, Date toDate, NewCaseDateType newCaseDateType) {
		caseCriteria.newCaseDateBetween(fromDate, toDate, newCaseDateType);
		reload();
	}
	
	public void filterByText(String text) {
		getContainer().removeContainerFilters(CaseIndexDto.UUID);
		getContainer().removeContainerFilters(CaseIndexDto.PERSON_FIRST_NAME);
		getContainer().removeContainerFilters(CaseIndexDto.PERSON_LAST_NAME);
    	getContainer().removeContainerFilters(CaseIndexDto.EPID_NUMBER);
    	getContainer().removeContainerFilters(CaseIndexDto.REPORT_DATE);
    	getContainer().removeContainerFilters(CaseIndexDto.CREATION_DATE);

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
    		orFilters.add(new DateFilter(CaseIndexDto.CREATION_DATE, text));
            getContainer().addContainerFilter(new Or(orFilters.stream().toArray(Filter[]::new)));
		}
	}
	
	public CaseCriteria getFilterCriteria() {
		return caseCriteria;
	}
	
    @SuppressWarnings("unchecked")
	public BeanItemContainer<CaseIndexDto> getContainer() {
    	GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
        return (BeanItemContainer<CaseIndexDto>) container.getWrappedContainer();
    }
    
    public void reload() {
    	if (reloadEnabled) {
	    	List<CaseIndexDto> cases = FacadeProvider.getCaseFacade().getIndexList(
	    			CurrentUser.getCurrent().getUuid(), 
	    			caseCriteria);
	
	    	getContainer().removeAllItems();
	        getContainer().addAll(cases);
    	}
    }

	public boolean isReloadEnabled() {
		return reloadEnabled;
	}

	public void setReloadEnabled(boolean reloadEnabled) {
		this.reloadEnabled = reloadEnabled;
	}
   
}