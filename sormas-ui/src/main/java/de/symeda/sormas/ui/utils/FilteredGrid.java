package de.symeda.sormas.ui.utils;

import com.vaadin.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.SerializableSupplier;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.BaseCriteria;

public class FilteredGrid<T, C extends BaseCriteria> extends Grid<T> {

	private static final long serialVersionUID = 8116377533153377424L;

	private C criteria;
	
    public FilteredGrid(Class<T> beanType) {
        super(beanType);
    }
	
	public C getCriteria() {
		return criteria;
	}
	
	public void setCriteria(C criteria) {
		this.criteria = criteria;
		getFilteredDataProvider().setFilter(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public ConfigurableFilterDataProvider<T, Void, C> getFilteredDataProvider() {
		return (ConfigurableFilterDataProvider<T, Void, C>)super.getDataProvider();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setDataProvider(DataProvider<T, ?> dataProvider) {
		if (!(dataProvider instanceof ConfigurableFilterDataProvider)) {
			dataProvider = (ConfigurableFilterDataProvider<T, Void, C>)dataProvider.withConfigurableFilter();
		}
		super.setDataProvider(dataProvider);
	}
	
	@Override
	public void setDataProvider(FetchItemsCallback<T> fetchItems, SerializableSupplier<Integer> sizeCallback) {
		throw new UnsupportedOperationException();
	}
	
	public int getItemCount() {
		return getDataProvider().size(new Query<>());
	}
}
