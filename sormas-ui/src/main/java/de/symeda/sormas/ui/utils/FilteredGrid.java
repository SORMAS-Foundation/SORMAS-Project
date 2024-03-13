package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializableSupplier;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;
import de.symeda.sormas.ui.UiUtil;

public class FilteredGrid<T, C extends BaseCriteria> extends Grid<T> {

	public static final String ACTION_BTN_ID = "action";
	public static final String DELETE_REASON_COLUMN = "deleteReasonCumulated";

	private static final long serialVersionUID = 8116377533153377424L;

	/**
	 * For lazy loading: Defines how many entries are loaded into the grid when new data needs to be loaded for the visible range.
	 */
	private static final int LAZY_BATCH_SIZE = 100;

	private C criteria;
	private boolean inEagerMode;
	private int dataSize;

	public FilteredGrid(Class<T> beanType) {
		super(beanType);
		getDataCommunicator().setMinPushSize(LAZY_BATCH_SIZE);
	}

	public C getCriteria() {
		return criteria;
	}

	public void setCriteria(C criteria) {
		setCriteria(criteria, false);
	}

	public void setCriteria(C criteria, boolean ignoreDataProvider) {
		this.criteria = criteria;
		if (!ignoreDataProvider && !inEagerMode) {
			getFilteredDataProvider().setFilter(criteria);
		}
	}

	public boolean isInEagerMode() {
		return inEagerMode;
	}

	public void setInEagerMode(boolean inEagerMode) {
		this.inEagerMode = inEagerMode;
	}

	/**
	 * @return Amount of total data with the given filter criteria.
	 */
	public int getDataSize() {
		return dataSize;
	}

	public void setDataSize(int dataSize) {

		this.dataSize = dataSize;
		fireEvent(new DataSizeChangeEvent(this, this.dataSize));
	}

	@SuppressWarnings("unchecked")
	public ConfigurableFilterDataProvider<T, Void, C> getFilteredDataProvider() {
		return (ConfigurableFilterDataProvider<T, Void, C>) super.getDataProvider();
	}

	/**
	 * get fired when {@link #getDataSize()} is updated.
	 */
	public Registration addDataSizeChangeListener(DataSizeChangeListener listener) {
		return addListener(DataSizeChangeEvent.class, listener, DataSizeChangeListener.class.getMethods()[0]);
	}

	/**
	 * <ul>
	 * <li>selectionMode = {@link SelectionMode#NONE}</li>
	 * </ul>
	 * 
	 * @see DataProvider#fromFilteringCallbacks
	 */
	public void setLazyDataProvider(CriteriaFetchCallback<T, C> fetchCallback, CriteriaCountCallback<C> countCallback) {

		setLazyDataProvider(fetchCallback, countCallback, SelectionMode.NONE);
	}

	/**
	 * @see DataProvider#fromFilteringCallbacks
	 */
	public void setLazyDataProvider(CriteriaFetchCallback<T, C> fetchCallback, CriteriaCountCallback<C> countCallback, SelectionMode selectionMode) {

		setDataProvider(
			query -> fetchCallback
				.fetchData(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> countCallback.countData(query.getFilter().orElse(null)).intValue());
		setSelectionMode(selectionMode);
	}

	/**
	 * <ul>
	 * <li>selectionMode = {@link SelectionMode#MULTI}</li>
	 * </ul>
	 * 
	 * @see DataProvider#fromStream
	 */
	public void setEagerDataProvider(CriteriaFetchCallback<T, C> fetchCallback) {

		setEagerDataProvider(fetchCallback, SelectionMode.MULTI);
	}

	/**
	 * @see DataProvider#fromStream
	 */
	public void setEagerDataProvider(CriteriaFetchCallback<T, C> fetchCallback, SelectionMode selectionMode) {

		setDataProvider(fetchCallback.fetchData(getCriteria(), null, null, null).stream());
		setSelectionMode(selectionMode);
	}

	/**
	 * @see DataProvider#fromFilteringCallbacks
	 */
	public void setDataProvider(FetchCallback<T, C> fetchCallback, CountCallback<T, C> countCallback) {

		CallbackDataProvider<T, C> dataProvider = DataProvider.fromFilteringCallbacks(fetchCallback, q -> {

			// Every time when the count query is executed, then notify to get the cached dataSize updated
			int size = countCallback.count(q);
			setDataSize(size);
			return size;
		});

		setDataProvider(dataProvider);
	}

	/**
	 * @see DataProvider#fromStream
	 */
	public void setDataProvider(Stream<T> items) {

		ListDataProvider<T> dataProvider = DataProvider.fromStream(items);

		// Every the in-memory data is updated/filtered, then notify to get the cached dataSize updated
		dataProvider.addDataProviderListener(e -> {

			int size = e.getSource().size(new Query<>());
			setDataSize(size);
		});
		setDataProvider(dataProvider);
	}

	/**
	 * @deprecated Use one of the other methods to create and set a {@link DataProvider} to get {@link #getDataSize()} updated.
	 */
	@Override
	@Deprecated
	@SuppressWarnings("unchecked")
	public void setDataProvider(DataProvider<T, ?> dataProvider) {

		if (!inEagerMode && !(dataProvider instanceof ConfigurableFilterDataProvider)) {
			dataProvider = (ConfigurableFilterDataProvider<T, Void, C>) dataProvider.withConfigurableFilter();
		}
		super.setDataProvider(dataProvider);
	}

	/**
	 * @deprecated Use one of the other methods to create and set a {@link DataProvider}.
	 */
	@Override
	@Deprecated
	public void setDataProvider(FetchItemsCallback<T> fetchItems, SerializableSupplier<Integer> sizeCallback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setColumns(String... columnIds) {
		super.setColumns(columnIds);
		getColumns().forEach(tColumn -> tColumn.setMaximumWidth(300));

		Arrays.asList(columnIds).forEach(columnId -> {
			if (!columnId.equals(ACTION_BTN_ID)) {
				Column<?, ?> column = getColumn(columnId);
				if (column.getRenderer() == null || TextRenderer.class.isAssignableFrom(column.getRenderer().getClass())) {
					column.setRenderer(new CaptionRenderer());
				}
			}
		});
	}

	/**
	 * Use this method before calling any bulkaction, to prevent illegal access to pseudonymized entries
	 */
	public void bulkActionHandler(Consumer<Set> callback) {
		if (PseudonymizableIndexDto.class.isAssignableFrom(getBeanType())
			&& getSelectedItems().stream().anyMatch(item -> ((PseudonymizableIndexDto) item).isPseudonymized())) {

			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getCaption(Captions.bulkActions),
				new Label(I18nProperties.getString(Strings.pseudonymizedEntitiesSelectedWarning)),
				I18nProperties.getCaption(Captions.actionDeselectAndContinue),
				I18nProperties.getCaption(Captions.actionCancel),
				640,
				proceed -> {
					if (proceed) {
						getSelectedItems().stream().filter(item -> ((PseudonymizableIndexDto) item).isPseudonymized()).forEach(this::deselect);
						callback.accept(getSelectedItems());
					}
				});
		} else {
			callback.accept(getSelectedItems());
		}
	}

	/**
	 * Use this method before calling any bulkaction, to prevent illegal access to pseudonymized entries
	 *
	 * @param allowAdminOverride
	 *            allow admins to perform this action even on pseudonymized entries
	 */
	public void bulkActionHandler(Consumer<Set> callback, boolean allowAdminOverride) {
		if (UiUtil.permitted(allowAdminOverride, UserRight.PERFORM_BULK_OPERATIONS_PSEUDONYM)) {
			callback.accept(getSelectedItems());
		} else {
			bulkActionHandler(callback);
		}

	}

	/**
	 * Add's a column to the left hand side of the grid complete with an edit-logo
	 *
	 * @param handler
	 *            ItemClickListener
	 */
	protected void addEditColumn(Consumer<T> handler) {
		addActionColumnConfiguration(handler, true);
	}

	private void addActionColumnConfiguration(Consumer<T> handler, boolean isEditAction) {
		List<Column<T, ?>> columnsList = new ArrayList<>(getColumns());

		Column<T, String> editColumn = addColumn(entry -> isEditAction ? VaadinIcons.EDIT.getHtml() : VaadinIcons.EYE.getHtml(), new HtmlRenderer());
		editColumn.setId(ACTION_BTN_ID);
		editColumn.setCaption(isEditAction ? I18nProperties.getCaption(Captions.edit) : I18nProperties.getCaption(Captions.view));
		editColumn.setSortable(false);
		editColumn.setWidth(20);

		// the edit column should always be on the left for consistency and to prevent sidescrolling
		columnsList.add(0, editColumn);
		setColumnOrder(columnsList.toArray(new Column[columnsList.size()]));

		addItemClickListener(new ShowDetailsListener<>(ACTION_BTN_ID, e -> handler.accept(e)));
	}

	protected void addViewColumn(Consumer<T> handler) {
		addActionColumnConfiguration(handler, false);
	}

	protected void removeColumnIfExists(String columnId) {
		if (getColumn(columnId) != null) {
			removeColumn(columnId);
		}
	}

	public interface CriteriaFetchCallback<T, C> {

		List<T> fetchData(C criteria, Integer first, Integer max, List<SortProperty> sortProperties);
	}

	public interface CriteriaCountCallback<C> {

		Long countData(C criteria);
	}
}
