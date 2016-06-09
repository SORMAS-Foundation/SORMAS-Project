package de.symeda.sormas.ui.surveillance.caze;

import java.util.Collection;
import java.util.Locale;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.caze.CaseDto;
import de.symeda.sormas.ui.utils.CaseHelper;

public class CaseGrid extends Grid {

    public CaseGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<CaseDto> container = new BeanItemContainer<CaseDto>(CaseDto.class);
        setContainerDataSource(container);
        setColumnOrder(CaseDto.UUID, CaseDto.CASE_STATUS, CaseDto.DESCRIPTION);
        getColumn(CaseDto.UUID).setConverter(new Converter<String, String>() {

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
        getContainer().removeAllContainerFilters();
        if (filterString.length() > 0) {
            SimpleStringFilter nameFilter = new SimpleStringFilter(
                    "productName", filterString, true, false);
            SimpleStringFilter availabilityFilter = new SimpleStringFilter(
                    "availability", filterString, true, false);
            SimpleStringFilter categoryFilter = new SimpleStringFilter(
                    "category", filterString, true, false);
            getContainer().addContainerFilter(
                    new Or(nameFilter, availabilityFilter, categoryFilter));
        }

    }

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
