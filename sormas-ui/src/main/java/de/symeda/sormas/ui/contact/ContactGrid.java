package de.symeda.sormas.ui.contact;

import java.util.ArrayList;
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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class ContactGrid extends Grid {

	public static final String NUMBER_OF_VISITS = "numberOfVisits";
	public static final String NUMBER_OF_PENDING_TASKS = "numberOfPendingTasks";
	public static final String DISEASE_SHORT = "diseaseShort";
	
	private final ContactCriteria contactCriteria = new ContactCriteria();
	
	public ContactGrid() {
		setSizeFull();

        setSelectionMode(SelectionMode.NONE);

        BeanItemContainer<ContactIndexDto> container = new BeanItemContainer<ContactIndexDto>(ContactIndexDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
        setContainerDataSource(generatedContainer);

        generatedContainer.addGeneratedProperty(NUMBER_OF_VISITS, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				ContactIndexDto indexDto = (ContactIndexDto) itemId;
				int numberOfVisits = FacadeProvider.getVisitFacade().getNumberOfVisits(indexDto.toReference(), null);
				int numberOfCooperativeVisits = FacadeProvider.getVisitFacade().getNumberOfVisits(indexDto.toReference(), VisitStatus.COOPERATIVE);
				
				return String.format(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, "numberOfVisitsFormat"),
						numberOfCooperativeVisits, numberOfVisits - numberOfCooperativeVisits);
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
        
        generatedContainer.addGeneratedProperty(NUMBER_OF_PENDING_TASKS, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				ContactIndexDto contactIndexDto = (ContactIndexDto)itemId;
				return String.format(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, NUMBER_OF_PENDING_TASKS + "Format"), 
						FacadeProvider.getTaskFacade().getPendingTaskCountByContact(contactIndexDto.toReference()));
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
        
        generatedContainer.addGeneratedProperty(DISEASE_SHORT, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				ContactIndexDto contactIndexDto = (ContactIndexDto) itemId;
				return contactIndexDto.getCaseDisease() != Disease.OTHER 
					? contactIndexDto.getCaseDisease().toShortString()
					: DataHelper.toStringNullable(contactIndexDto.getCaseDiseaseDetails());
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
        });

        setColumns(ContactIndexDto.UUID, DISEASE_SHORT, ContactIndexDto.CONTACT_CLASSIFICATION, 
        		ContactIndexDto.PERSON, ContactIndexDto.CONTACT_PROXIMITY,
        		ContactIndexDto.FOLLOW_UP_STATUS, NUMBER_OF_VISITS, NUMBER_OF_PENDING_TASKS);
        getColumn(ContactIndexDto.CONTACT_PROXIMITY).setWidth(200);
        getColumn(ContactIndexDto.UUID).setRenderer(new UuidRenderer());

        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
        			ContactIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        addItemClickListener(e -> {
	       	ContactIndexDto contactIndexDto = (ContactIndexDto)e.getItemId();
	        ControllerProvider.getContactController().editData(contactIndexDto.getUuid());
		});	
	}
	
	public void setCaseFilter(CaseReferenceDto caseRef) {
		contactCriteria.caseEquals(caseRef);
		reload();
	}

    public void setDiseaseFilter(Disease disease) {
		contactCriteria.caseDieasesEquals(disease);
		reload();
	}

    public void setReportedByFilter(UserRole reportingUserRole) {
    	contactCriteria.reportingUserHasRole(reportingUserRole);
    	reload();
    }
    
    public void setRegionFilter(String regionUuid) {
		getContainer().removeContainerFilters(ContactIndexDto.CASE_REGION_UUID);
		if (regionUuid != null) {
	    	Equal filter = new Equal(ContactIndexDto.CASE_REGION_UUID, regionUuid);  
	        getContainer().addContainerFilter(filter);
		}
	}
    
    public void setDistrictFilter(String districtUuid) {
		getContainer().removeContainerFilters(ContactIndexDto.CASE_DISTRICT_UUID);
		if (districtUuid != null) {
	    	Equal filter = new Equal(ContactIndexDto.CASE_DISTRICT_UUID, districtUuid);  
	        getContainer().addContainerFilter(filter);
		}
	}
    
    public void setHealthFacilityFilter(String facilityUuid) {
		getContainer().removeContainerFilters(ContactIndexDto.CASE_HEALTH_FACILITY_UUID);
		if (facilityUuid != null) {
	    	Equal filter = new Equal(ContactIndexDto.CASE_HEALTH_FACILITY_UUID, facilityUuid);  
	        getContainer().addContainerFilter(filter);
		}
	}
    
    public void setContactOfficerFilter(String contactOfficerUuid) {
		getContainer().removeContainerFilters(ContactIndexDto.CONTACT_OFFICER_UUID);
		if (contactOfficerUuid != null) {
	    	Equal filter = new Equal(ContactIndexDto.CONTACT_OFFICER_UUID, contactOfficerUuid);  
	        getContainer().addContainerFilter(filter);
		}
	}

	public void setClassificationFilter(ContactClassification status) {
		getContainer().removeContainerFilters(ContactIndexDto.CONTACT_CLASSIFICATION);
    	if (status != null) {
	    	Equal filter = new Equal(ContactIndexDto.CONTACT_CLASSIFICATION, status);  
	        getContainer().addContainerFilter(filter);
    	}
    }
	
	public void setFollowUpStatusFilter(FollowUpStatus status) {
		getContainer().removeContainerFilters(ContactIndexDto.FOLLOW_UP_STATUS);
		if(status != null) {
			Equal filter = new Equal(ContactIndexDto.FOLLOW_UP_STATUS, status);
			getContainer().addContainerFilter(filter);
		}
	}

	public void filterByText(String text) {
		getContainer().removeContainerFilters(ContactIndexDto.UUID);
		getContainer().removeContainerFilters(ContactIndexDto.PERSON);
    	getContainer().removeContainerFilters(ContactIndexDto.CAZE);

    	if (text != null && !text.isEmpty()) {
    		List<Filter> orFilters = new ArrayList<Filter>();
    		String[] words = text.split("\\s+");
    		for (String word : words) {
    			orFilters.add(new SimpleStringFilter(ContactIndexDto.UUID, word, true, false));
    			orFilters.add(new SimpleStringFilter(ContactIndexDto.PERSON, word, true, false));
    			orFilters.add(new SimpleStringFilter(ContactIndexDto.CAZE, word, true, false));
    		}
            getContainer().addContainerFilter(new Or(orFilters.stream().toArray(Filter[]::new)));
		}
	}
	
	
    @SuppressWarnings("unchecked")
	private BeanItemContainer<ContactIndexDto> getContainer() {
    	GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
        return (BeanItemContainer<ContactIndexDto>) container.getWrappedContainer();
    }
    
    public void reload() {
    	List<ContactIndexDto> entries = FacadeProvider.getContactFacade().getIndexList(LoginHelper.getCurrentUserAsReference().getUuid(), contactCriteria);
   
    	getContainer().removeAllItems();
        getContainer().addAll(entries);  
    }
}


