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
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class ContactGrid extends Grid {

	public static final String NUMBER_OF_VISITS = "numberOfVisits";
	public static final String NUMBER_OF_PENDING_TASKS = "numberOfPendingTasks";
	public static final String DISEASE_SHORT = "diseaseShort";
	//public static final String ASSOCIATED_CASE = "associatedCase";
	
	public ContactGrid() {
		setSizeFull();

        setSelectionMode(SelectionMode.NONE);

        BeanItemContainer<ContactIndexDto> container = new BeanItemContainer<ContactIndexDto>(ContactIndexDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
        setContainerDataSource(generatedContainer);

        generatedContainer.addGeneratedProperty(NUMBER_OF_VISITS, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				ContactIndexDto indexDto = (ContactIndexDto)itemId;
				return String.format(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, "numberOfVisitsFormat"),
						indexDto.getNumberOfCooperativeVisits(), indexDto.getNumberOfMissedVisits());
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
						FacadeProvider.getTaskFacade().getPendingTaskCountByContact(contactIndexDto));
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
				CaseDataDto contactCase = FacadeProvider.getCaseFacade().getCaseDataByUuid(contactIndexDto.getCaze().getUuid());
				return contactCase.getDisease() != Disease.OTHER 
					? contactCase.getDisease().toShortString()
					: DataHelper.toStringNullable(contactCase.getDiseaseDetails());
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
        });
        
//        generatedContainer.addGeneratedProperty(ASSOCIATED_CASE, new PropertyValueGenerator<String>() {
//			@Override
//			public String getValue(Item item, Object itemId, Object propertyId) {
//				ContactIndexDto contactIndexDto = (ContactIndexDto) itemId;
//				PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(contactIndexDto.getPerson().getUuid());
//				String caseId = findAssociatedCaseId(personDto, contactIndexDto);
//				if(caseId != null) {
//					return caseId;
//				} else {
//					return "";
//				}
//			}
//			@Override
//			public Class<String> getType() {
//				return String.class;
//			}
//		});

        setColumns(ContactIndexDto.UUID, DISEASE_SHORT, ContactIndexDto.CONTACT_CLASSIFICATION, 
        		ContactIndexDto.PERSON, ContactIndexDto.CONTACT_PROXIMITY,
        		ContactIndexDto.FOLLOW_UP_STATUS, NUMBER_OF_VISITS, NUMBER_OF_PENDING_TASKS);
        getColumn(ContactIndexDto.CONTACT_PROXIMITY).setWidth(200);
        getColumn(ContactIndexDto.UUID).setRenderer(new UuidRenderer());
        //getColumn(ContactIndexDto.CAZE).setRenderer(new HtmlRenderer(), new HtmlReferenceDtoConverter());
        //getColumn(ContactIndexDto.LAST_CONTACT_DATE).setRenderer(new DateRenderer(DateHelper.getDateFormat()));
        //getColumn(ContactIndexDto.FOLLOW_UP_UNTIL).setRenderer(new DateRenderer(DateHelper.getDateFormat()));
		//getColumn(ASSOCIATED_CASE).setRenderer(new CaseUuidRenderer(true));
        
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
        			ContactIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        addItemClickListener(e -> {
	       	ContactIndexDto contactIndexDto = (ContactIndexDto)e.getItemId();
//	       	if(ASSOCIATED_CASE.equals(e.getPropertyId())) {
//	       		PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(contactIndexDto.getPerson().getUuid());
//				CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(findAssociatedCaseId(personDto, contactIndexDto));
//				if(caseDto != null) {
//					ControllerProvider.getCaseController().navigateToData(findAssociatedCaseId(personDto, contactIndexDto));
//				} else {
//					ControllerProvider.getCaseController().create(personDto, contactIndexDto.getCazeDisease(), FacadeProvider.getContactFacade().getContactByUuid(contactIndexDto.getUuid()));
//				}
//	       	} else {
//	       		if (ContactIndexDto.CAZE.equals(e.getPropertyId())) {
//	        		ControllerProvider.getCaseController().navigateToData(contactIndexDto.getCaze().getUuid());
//	        	} else {
	        		ControllerProvider.getContactController().editData(contactIndexDto.getUuid());
//	        	}
//	       	}
		});	
	}
	

    public void setDiseaseFilter(Disease disease) {
		getContainer().removeContainerFilters(ContactIndexDto.CAZE_DISEASE);
		if (disease != null) {
	    	Equal filter = new Equal(ContactIndexDto.CAZE_DISEASE, disease);  
	        getContainer().addContainerFilter(filter);
		}
	}

    public void setRegionFilter(RegionReferenceDto region) {
		getContainer().removeContainerFilters(ContactIndexDto.CAZE_REGION);
		if (region != null) {
	    	Equal filter = new Equal(ContactIndexDto.CAZE_REGION, region);  
	        getContainer().addContainerFilter(filter);
		}
	}
    
    public void setDistrictFilter(DistrictReferenceDto district) {
		getContainer().removeContainerFilters(ContactIndexDto.CAZE_DISTRICT);
		if (district != null) {
	    	Equal filter = new Equal(ContactIndexDto.CAZE_DISTRICT, district);  
	        getContainer().addContainerFilter(filter);
		}
	}
    
    public void setHealthFacilityFilter(FacilityReferenceDto facility) {
		getContainer().removeContainerFilters(ContactIndexDto.CAZE_HEALTH_FACILITY);
		if (facility != null) {
	    	Equal filter = new Equal(ContactIndexDto.CAZE_HEALTH_FACILITY, facility);  
	        getContainer().addContainerFilter(filter);
		}
	}
    
    public void setContactOfficerFilter(UserReferenceDto contactOfficer) {
		getContainer().removeContainerFilters(ContactIndexDto.CONTACT_OFFICER);
		if (contactOfficer != null) {
	    	Equal filter = new Equal(ContactIndexDto.CONTACT_OFFICER, contactOfficer);  
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
		getContainer().removeContainerFilters(ContactIndexDto.CAZE_PERSON);
    	getContainer().removeContainerFilters(ContactIndexDto.CAZE);

    	if (text != null && !text.isEmpty()) {
    		List<Filter> orFilters = new ArrayList<Filter>();
    		String[] words = text.split("\\s+");
    		for (String word : words) {
    			orFilters.add(new SimpleStringFilter(ContactIndexDto.UUID, word, true, false));
    			orFilters.add(new SimpleStringFilter(ContactIndexDto.PERSON, word, true, false));
    			orFilters.add(new SimpleStringFilter(ContactIndexDto.CAZE_PERSON, word, true, false));
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
    	List<ContactIndexDto> entries = FacadeProvider.getContactFacade().getIndexList(LoginHelper.getCurrentUserAsReference().getUuid());
   
    	getContainer().removeAllItems();
        getContainer().addAll(entries);  
    }
    
    public void reload(CaseReferenceDto caseRef) {
    	List<ContactIndexDto> entries = FacadeProvider.getContactFacade().getIndexListByCase(caseRef);

    	getContainer().removeAllItems();
        getContainer().addAll(entries);    	
    }
    
//    private String findAssociatedCaseId(PersonDto personDto, ContactIndexDto contactIndexDto) {
//		if(personDto == null || contactIndexDto == null) {
//			return null;
//		}
//		
//		UserDto user = LoginHelper.getCurrentUser();
//		CaseDataDto caze = FacadeProvider.getCaseFacade().getByPersonAndDisease(personDto.getUuid(), contactIndexDto.getCazeDisease(), user.getUuid());
//		if(caze != null) {
//			return caze.getUuid();
//		} else {
//			return null;
//		}
//	}
}


