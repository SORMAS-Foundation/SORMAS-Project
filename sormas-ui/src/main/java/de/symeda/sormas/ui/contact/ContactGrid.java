package de.symeda.sormas.ui.contact;

import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class ContactGrid extends Grid {

	public ContactGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.NONE);

        BeanItemContainer<ContactIndexDto> container = new BeanItemContainer<ContactIndexDto>(ContactIndexDto.class);
        setContainerDataSource(container);
        setColumns(ContactIndexDto.UUID, ContactIndexDto.PERSON, ContactIndexDto.CONTACT_PROXIMITY, 
        		ContactIndexDto.LAST_CONTACT_DATE, 
        		ContactIndexDto.CAZE_DISEASE, ContactIndexDto.CAZE, ContactIndexDto.CAZE_PERSON,
        		ContactIndexDto.CONTACT_OFFICER
        		);

        getColumn(ContactIndexDto.UUID).setRenderer(new UuidRenderer());
        
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getFieldCaption(
        			ContactIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        addItemClickListener(e -> ControllerProvider.getContactController().editData(
        		((ContactIndexDto)e.getItemId()).getUuid()));
        
        reload();
	}
	

    public void setDiseaseFilter(Disease disease) {
		getContainer().removeContainerFilters(ContactIndexDto.CAZE_DISEASE);
		if (disease != null) {
	    	Equal filter = new Equal(ContactIndexDto.CAZE_DISEASE, disease);  
	        getContainer().addContainerFilter(filter);
		}
	}

    public void setDistrictFilter(ReferenceDto district) {
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

	public void setStatusFilter(ContactStatus status) {
    	removeAllStatusFilter();
    	if (status != null) {
	    	Equal filter = new Equal(ContactIndexDto.CONTACT_STATUS, status);  
	        getContainer().addContainerFilter(filter);
    	}
    }
    
    public void removeAllStatusFilter() {
    	reload();
    	getContainer().removeContainerFilters(ContactIndexDto.CONTACT_STATUS);
    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<ContactIndexDto> getContainer() {
        return (BeanItemContainer<ContactIndexDto>) super.getContainerDataSource();
    }
    
    public void reload() {
    	List<ContactIndexDto> entries = ControllerProvider.getContactController().getIndexList();
        getContainer().removeAllItems();
        getContainer().addAll(entries);    	
    }

    public void refresh(ContactIndexDto entry) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<ContactIndexDto> item = getContainer().getItem(entry);
        if (item != null) {
            // Updated product
            @SuppressWarnings("rawtypes")
			MethodProperty p = (MethodProperty) item.getItemProperty(ContactIndexDto.UUID);
            p.fireValueChange();
        } else {
            // New product
            getContainer().addBean(entry);
        }
    }

    public void remove(ContactIndexDto caze) {
        getContainer().removeItem(caze);
    }
}


