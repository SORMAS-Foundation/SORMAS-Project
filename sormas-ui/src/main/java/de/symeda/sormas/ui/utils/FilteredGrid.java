package de.symeda.sormas.ui.utils;

import java.util.function.Consumer;

import com.vaadin.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializableSupplier;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.BaseCriteria;

public class FilteredGrid<T, C extends BaseCriteria> extends Grid<T> {

	public static final String EDIT_BTN_ID = "edit";

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

	protected void addEditColumn(Consumer<ItemClick<T>> handler) {
		Column<T, String> editColumn = addColumn(entry -> VaadinIcons.EDIT.getHtml(), new HtmlRenderer());
		editColumn.setId(EDIT_BTN_ID);
		editColumn.setSortable(false);
		editColumn.setWidth(20);

		addItemClickListener(e -> {
			if (e.getColumn() != null && (EDIT_BTN_ID.equals(e.getColumn().getId()) || e.getMouseEventDetails().isDoubleClick())) {
				handler.accept(e);
			}
		});
	}
}
