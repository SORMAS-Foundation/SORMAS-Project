package de.symeda.sormas.ui.surveillance.caze;

import java.util.Collection;
import java.util.Locale;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDto;
import de.symeda.sormas.api.caze.CaseStatus;

public class CaseGrid extends Grid {

	private static final long serialVersionUID = -3413165328323165362L;

	public CaseGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<CaseDto> container = new BeanItemContainer<CaseDto>(CaseDto.class);
        setContainerDataSource(container);
        setColumnOrder(CaseDto.UUID, CaseDto.CASE_STATUS, CaseDto.DISEASE);
        getColumn(CaseDto.UUID).setConverter(new Converter<String, String>() {
			
        	private static final long serialVersionUID = -5274112323551652930L;

			@Override
			public String convertToModel(String value, Class<? extends String> targetType, Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
				throw new UnsupportedOperationException();
			}

			@Override
			public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
				return value;//CaseHelper.getShortUuid(value);
			}

			@Override
			public Class<String> getModelType() {
				return String.class;
			}

			@Override
			public Class<String> getPresentationType() {
				return String.class;
			}
        });
    }
    
    /**
     * Filter the grid based on a search string that is searched for in the
     * product name, availability and category columns.
     *
     * @param filterString
     *            string to look for
     */
    public void setFilter(String filterString) {
    	removeAllFilter();
        if (filterString.length() > 0) {
            SimpleStringFilter nameFilter = new SimpleStringFilter(
            		CaseDto.PERSON, filterString, true, false);
            SimpleStringFilter descFilter = new SimpleStringFilter(
            		CaseDto.DESCRIPTION, filterString, true, false);
            SimpleStringFilter statusFilter = new SimpleStringFilter(
            		CaseDto.CASE_STATUS, filterString, true, false);
            getContainer().addContainerFilter(
                    new Or(nameFilter, descFilter, statusFilter));
        }

    }
    
    public void setFilter(CaseStatus statusToFilter) {
    	removeAllFilter();
    	Equal filter = new Equal(CaseDto.CASE_STATUS, statusToFilter);  
        getContainer().addContainerFilter(filter);
    }
    
    public void setFilter(Disease disease) {
    	removeAllFilter();
    	Equal filter = new Equal(CaseDto.DISEASE, disease);  
        getContainer().addContainerFilter(filter);
	}
    
    public void removeAllFilter() {
    	getContainer().removeAllContainerFilters();
    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<CaseDto> getContainer() {
        return (BeanItemContainer<CaseDto>) super.getContainerDataSource();
    }

    @Override
    public CaseDto getSelectedRow() throws IllegalStateException {
        return (CaseDto) super.getSelectedRow();
    }

    public void setCases(Collection<CaseDto> cases) {
        getContainer().removeAllItems();
        getContainer().addAll(cases);
    }

    public void refresh(CaseDto caze) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<CaseDto> item = getContainer().getItem(caze);
        if (item != null) {
            // Updated product
            @SuppressWarnings("rawtypes")
			MethodProperty p = (MethodProperty) item.getItemProperty(CaseDto.UUID);
            p.fireValueChange();
        } else {
            // New product
            getContainer().addBean(caze);
        }
    }

    public void remove(CaseDto caze) {
        getContainer().removeItem(caze);
    }

	
}
