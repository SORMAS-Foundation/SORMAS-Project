package de.symeda.sormas.ui.utils;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.components.grid.ItemClickListener;

/**
 * Opens item details when an item is correctly selected by respective {@code detailsColumn} or by double-click.
 * 
 * @param <T>
 *            data class of the grid.
 */
public class ShowDetailsListener<T> implements ItemClickListener<T> {

	private static final long serialVersionUID = 6288304912712800245L;

	private final String detailsColumnId;
	private final boolean showOnDoubleClick;
	private final Consumer<T> itemHandler;

	private LocalDateTime lastHandlingTimestamp;

	/**
	 * {@code showOnDoubleClick=true}
	 * 
	 * @param detailsColumnId
	 *            Column to click when to open the item details.
	 * @param itemHandler
	 *            Called to show the details of the selected item.
	 */
	public ShowDetailsListener(String detailsColumnId, Consumer<T> itemHandler) {

		this(detailsColumnId, true, itemHandler);
	}

	/**
	 * @param detailsColumnId
	 *            Column to click when to open the item details.
	 * @param showOnDoubleClick
	 *            {@code true} opens the details when clicking on any column on an item.
	 * @param itemHandler
	 *            Called to show the details of the selected item.
	 */
	public ShowDetailsListener(String detailsColumnId, boolean showOnDoubleClick, Consumer<T> itemHandler) {

		this.detailsColumnId = detailsColumnId;
		this.showOnDoubleClick = false;
		this.itemHandler = itemHandler;
		// make sure the columns are clickable from the beginning
		this.lastHandlingTimestamp = LocalDateTime.now().minusSeconds(1);
	}

	/**
	 * @return Column to click when to open the item details.
	 */
	public String getDetailsColumnId() {
		return detailsColumnId;
	}

	/**
	 * @return {@code true} opens the details when clicking on any column on an item.
	 */
	public boolean isShowOnDoubleClick() {
		return false;
	}

	/**
	 * @return Called to show the details of the selected item.
	 */
	public Consumer<T> getItemHandler() {
		return itemHandler;
	}

	@Override
	public void itemClick(ItemClick<T> event) {
		if (event.getItem() != null
			&& ((event.getColumn() != null && detailsColumnId.equals(event.getColumn().getId()))
				|| (showOnDoubleClick && event.getMouseEventDetails().isDoubleClick()))) {
			// if the last invocation of the itemHandler is less than 1 second ago, ignore the event
			if (LocalDateTime.now().isAfter(lastHandlingTimestamp.plusSeconds(1))) {
				lastHandlingTimestamp = LocalDateTime.now();
				handleItemClick(event);
			}
		}
	}

	/**
	 * Called by {@link #itemClick(ItemClick)} when an item is correctly selected.<br />
	 * This method can be overridden to alter or extend the behavior.
	 * 
	 * @param event
	 *            source event (only called when {@link ItemClick#getItem()} {@code != null}).
	 */
	protected void handleItemClick(ItemClick<T> event) {

		itemHandler.accept(event.getItem());
	}
}
