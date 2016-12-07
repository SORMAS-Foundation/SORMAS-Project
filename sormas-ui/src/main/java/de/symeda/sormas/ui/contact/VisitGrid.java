package de.symeda.sormas.ui.contact;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.ui.ControllerProvider;

@SuppressWarnings("serial")
public class VisitGrid extends Grid {

	public static final String SYMPTOMS_SYMPTOMATIC = VisitDto.SYMPTOMS + "." + SymptomsDto.SYMPTOMATIC;
	public static final String SYMPTOMS_TEMPERATURE = VisitDto.SYMPTOMS + "." + SymptomsDto.TEMPERATURE;
	
	public VisitGrid() {
		setSizeFull();

        setSelectionMode(SelectionMode.NONE);

        BeanItemContainer<VisitDto> container = new BeanItemContainer<VisitDto>(VisitDto.class);
        container.addNestedContainerProperty(SYMPTOMS_SYMPTOMATIC);
        container.addNestedContainerProperty(SYMPTOMS_TEMPERATURE);
        setContainerDataSource(container);
        setColumns(VisitDto.VISIT_DATE_TIME, VisitDto.VISIT_STATUS, VisitDto.VISIT_REMARKS, 
        		SYMPTOMS_SYMPTOMATIC, SYMPTOMS_TEMPERATURE
        		);

        getColumn(VisitDto.VISIT_DATE_TIME).setRenderer(new DateRenderer(DateHelper.getShortTimeDateFormat()));
        
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
        			VisitDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        addItemClickListener(e -> {
        	VisitDto indexDto = (VisitDto)e.getItemId();
        	ControllerProvider.getContactController().editVisit(indexDto.getUuid());
        });
	}

    @SuppressWarnings("unchecked")
	private BeanItemContainer<VisitDto> getContainer() {
        return (BeanItemContainer<VisitDto>) super.getContainerDataSource();
    }
    
    public void reload(ContactReferenceDto contact) {
    	List<VisitDto> entries = FacadeProvider.getVisitFacade().getAllByContact(contact);
    	
    	getContainer().removeAllItems();
        getContainer().addAll(entries);    	
    }
    
    public void reload(PersonReferenceDto person) {
    	List<VisitDto> entries = FacadeProvider.getVisitFacade().getAllByPerson(person);

    	getContainer().removeAllItems();
        getContainer().addAll(entries);    	
    }

}


