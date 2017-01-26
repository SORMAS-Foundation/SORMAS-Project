package de.symeda.sormas.ui.contact;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.HtmlReferenceDtoConverter;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class ContactGrid extends Grid {

	public static final String NUMBER_OF_VISITS = "numberOfVisits";
	public static final String NUMBER_OF_PENDING_TASKS = "numberOfPendingTasks";
	
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

        setColumns(ContactIndexDto.UUID, ContactIndexDto.PERSON, ContactIndexDto.CONTACT_PROXIMITY,
        		ContactIndexDto.LAST_CONTACT_DATE, ContactIndexDto.CONTACT_CLASSIFICATION, 
        		ContactIndexDto.FOLLOW_UP_STATUS, ContactIndexDto.FOLLOW_UP_UNTIL, NUMBER_OF_VISITS,
        		ContactIndexDto.CAZE_DISEASE, ContactIndexDto.CAZE,
        		ContactIndexDto.CONTACT_OFFICER, NUMBER_OF_PENDING_TASKS
        		);
        getColumn(ContactIndexDto.CONTACT_PROXIMITY).setWidth(200);
        getColumn(ContactIndexDto.UUID).setRenderer(new UuidRenderer());
        getColumn(ContactIndexDto.CAZE).setRenderer(new HtmlRenderer(), new HtmlReferenceDtoConverter());
        getColumn(ContactIndexDto.LAST_CONTACT_DATE).setRenderer(new DateRenderer(DateHelper.getShortDateFormat()));
        getColumn(ContactIndexDto.FOLLOW_UP_UNTIL).setRenderer(new DateRenderer(DateHelper.getShortDateFormat()));
        
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
        			ContactIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        addItemClickListener(e -> {
        	ContactIndexDto indexDto = (ContactIndexDto)e.getItemId();
        	if (ContactIndexDto.CAZE.equals(e.getPropertyId())) {
        		ControllerProvider.getCaseController().navigateToData(indexDto.getCaze().getUuid());
        	} else {
        		ControllerProvider.getContactController().editData(indexDto.getUuid());
        	}
        });
	}
	

    public void setDiseaseFilter(Disease disease) {
		getContainer().removeContainerFilters(ContactIndexDto.CAZE_DISEASE);
		if (disease != null) {
	    	Equal filter = new Equal(ContactIndexDto.CAZE_DISEASE, disease);  
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

    @SuppressWarnings("unchecked")
	private BeanItemContainer<ContactIndexDto> getContainer() {
    	GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
        return (BeanItemContainer<ContactIndexDto>) container.getWrappedContainer();
    }
    
    public void reload(CaseReferenceDto caseRef) {
    	List<ContactIndexDto> entries = FacadeProvider.getContactFacade().getIndexListByCase(caseRef);

    	getContainer().removeAllItems();
        getContainer().addAll(entries);    	
    }
}


