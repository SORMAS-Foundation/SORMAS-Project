package de.symeda.sormas.ui.utils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.SerializableSupplier;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.BaseCriteria;

public class FilteredGrid<T, C extends BaseCriteria> extends Grid<T> {

	private static final long serialVersionUID = 8116377533153377424L;

	private C criteria;
	private boolean inEagerMode;

	public FilteredGrid(Class<T> beanType) {
		super(beanType);
	}

	public C getCriteria() {
		return criteria;
	}

	public void setCriteria(C criteria) {
		this.criteria = criteria;
		if (!inEagerMode) { 
			getFilteredDataProvider().setFilter(criteria);
		}
	}

	public boolean isInEagerMode() {
		return inEagerMode;
	}

	public void setInEagerMode(boolean inEagerMode) {
		this.inEagerMode = inEagerMode;
	}

	@SuppressWarnings("unchecked")
	public ConfigurableFilterDataProvider<T, Void, C> getFilteredDataProvider() {
		return (ConfigurableFilterDataProvider<T, Void, C>) super.getDataProvider();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setDataProvider(DataProvider<T, ?> dataProvider) {
		if (!inEagerMode && !(dataProvider instanceof ConfigurableFilterDataProvider)) {
			dataProvider = (ConfigurableFilterDataProvider<T, Void, C>) dataProvider.withConfigurableFilter();
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
	
	public T getFirstItem () {
		return getDataProvider().fetch(new Query<>(0, 1, Collections.emptyList(), null, null)).findFirst().orElse(null);
	}
}
