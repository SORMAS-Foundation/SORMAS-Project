package de.symeda.sormas.ui.contact;

import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.HtmlReferenceDtoConverter;
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
        		ContactIndexDto.CAZE_DISEASE, ContactIndexDto.CAZE,
        		ContactIndexDto.CONTACT_OFFICER
        		);

        getColumn(ContactIndexDto.UUID).setRenderer(new UuidRenderer());
        getColumn(ContactIndexDto.CAZE).setRenderer(new HtmlRenderer(), new HtmlReferenceDtoConverter());
        getColumn(ContactIndexDto.LAST_CONTACT_DATE).setRenderer(new DateRenderer(DateHelper.getShortDateFormat()));
        
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

	public void setClassificationFilter(ContactClassification status) {
		getContainer().removeContainerFilters(ContactIndexDto.CONTACT_CLASSIFICATION);
    	if (status != null) {
	    	Equal filter = new Equal(ContactIndexDto.CONTACT_CLASSIFICATION, status);  
	        getContainer().addContainerFilter(filter);
    	}
    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<ContactIndexDto> getContainer() {
        return (BeanItemContainer<ContactIndexDto>) super.getContainerDataSource();
    }
    
    public void reload(CaseReferenceDto caseRef) {
    	UserDto user = LoginHelper.getCurrentUser();
    	List<ContactIndexDto> entries = FacadeProvider.getContactFacade().getIndexListByCase(user.getUuid(), caseRef);

    	getContainer().removeAllItems();
        getContainer().addAll(entries);    	
    }
}


