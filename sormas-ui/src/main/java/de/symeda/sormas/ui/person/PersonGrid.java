package de.symeda.sormas.ui.person;

import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.ui.login.LoginHelper;

@SuppressWarnings("serial")
public class PersonGrid extends Grid {

	public PersonGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.NONE);

        BeanItemContainer<PersonIndexDto> container = new BeanItemContainer<PersonIndexDto>(PersonIndexDto.class);
        setContainerDataSource(container);
        setColumns(PersonIndexDto.FIRST_NAME, PersonIndexDto.LAST_NAME, 
        		PersonIndexDto.APPROXIMATE_AGE, PersonIndexDto.SEX,
        		PersonIndexDto.PRESENT_CONDITION
        		);

        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
        			PersonIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        reload();
	}
	
    @SuppressWarnings("unchecked")
	private BeanItemContainer<PersonIndexDto> getContainer() {
        return (BeanItemContainer<PersonIndexDto>) super.getContainerDataSource();
    }
    
    public void setFirstNameFilter(String firstName) {
		getContainer().removeContainerFilters(PersonIndexDto.FIRST_NAME);
		if (firstName != null) {
	    	Filter filter = new SimpleStringFilter(PersonIndexDto.FIRST_NAME, firstName, true, false);  
	        getContainer().addContainerFilter(filter);
		}
	}

    public void setLastNameFilter(String lastName) {
		getContainer().removeContainerFilters(PersonIndexDto.LAST_NAME);
		if (lastName != null) {
			Filter filter = new SimpleStringFilter(PersonIndexDto.LAST_NAME, lastName, true, false);  
	        getContainer().addContainerFilter(filter);
		}
	}

    public void reload() {
    	List<PersonIndexDto> entries = FacadeProvider.getPersonFacade().getIndexList(LoginHelper.getCurrentUserAsReference());
        getContainer().removeAllItems();
        getContainer().addAll(entries);    	
    }

    public void refresh(PersonIndexDto entry) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<PersonIndexDto> item = getContainer().getItem(entry);
        if (item != null) {
            // Updated product
            @SuppressWarnings("rawtypes")
			MethodProperty p = (MethodProperty) item.getItemProperty(PersonIndexDto.UUID);
            p.fireValueChange();
        } else {
            // New product
            getContainer().addBean(entry);
        }
    }

    public void remove(PersonIndexDto entry) {
        getContainer().removeItem(entry);
    }
}


