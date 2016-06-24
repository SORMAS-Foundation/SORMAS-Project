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
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseStatus;

public class CaseGrid extends Grid {

	private static final long serialVersionUID = -3413165328323165362L;

	public CaseGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<CaseDataDto> container = new BeanItemContainer<CaseDataDto>(CaseDataDto.class);
        setContainerDataSource(container);
        setColumnOrder(CaseDataDto.UUID, CaseDataDto.CASE_STATUS, CaseDataDto.DISEASE);
        getColumn(CaseDataDto.UUID).setConverter(new Converter<String, String>() {
			
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
    	getContainer().removeContainerFilters(CaseDataDto.PERSON);
    	getContainer().removeContainerFilters(CaseDataDto.DESCRIPTION);
//    	getContainer().removeContainerFilters(CaseDataDto.CASE_STATUS);
        if (filterString.length() > 0) {
            SimpleStringFilter nameFilter = new SimpleStringFilter(
            		CaseDataDto.PERSON, filterString, true, false);
            SimpleStringFilter descFilter = new SimpleStringFilter(
            		CaseDataDto.DESCRIPTION, filterString, true, false);
//            SimpleStringFilter statusFilter = new SimpleStringFilter(
//            		CaseDataDto.CASE_STATUS, filterString, true, false);
            getContainer().addContainerFilter(
//            new Or(nameFilter, descFilter, statusFilter));
            new Or(nameFilter, descFilter));
        }

    }
    
    public void setFilter(CaseStatus statusToFilter) {
    	removeAllStatusFilter();
    	Equal filter = new Equal(CaseDataDto.CASE_STATUS, statusToFilter);  
        getContainer().addContainerFilter(filter);
    }
    
    public void setFilter(Disease disease) {
    	getContainer().removeContainerFilters(CaseDataDto.DISEASE);
    	Equal filter = new Equal(CaseDataDto.DISEASE, disease);  
        getContainer().addContainerFilter(filter);
	}
    
    public void removeAllStatusFilter() {
    	getContainer().removeContainerFilters(CaseDataDto.CASE_STATUS);
    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<CaseDataDto> getContainer() {
        return (BeanItemContainer<CaseDataDto>) super.getContainerDataSource();
    }

    @Override
    public CaseDataDto getSelectedRow() throws IllegalStateException {
        return (CaseDataDto) super.getSelectedRow();
    }

    public void setCases(Collection<CaseDataDto> cases) {
        getContainer().removeAllItems();
        getContainer().addAll(cases);
    }

    public void refresh(CaseDataDto caze) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<CaseDataDto> item = getContainer().getItem(caze);
        if (item != null) {
            // Updated product
            @SuppressWarnings("rawtypes")
			MethodProperty p = (MethodProperty) item.getItemProperty(CaseDataDto.UUID);
            p.fireValueChange();
        } else {
            // New product
            getContainer().addBean(caze);
        }
    }

    public void remove(CaseDataDto caze) {
        getContainer().removeItem(caze);
    }

	
}
