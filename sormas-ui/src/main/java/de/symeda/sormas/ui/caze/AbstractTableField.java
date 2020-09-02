/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.caze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.exception.CloneFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Container.ItemSetChangeEvent;
import com.vaadin.v7.data.Container.ItemSetChangeListener;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * TODO replace table with grid?
 * TODO whole component seems to need improvement (e.g. should use setInternalValue instead of setValue)
 * Does probably not make sense, because of future update to Vaadin 8
 *
 * @author Martin Wahnschaffe
 */
@SuppressWarnings({
	"serial",
	"rawtypes" })
public abstract class AbstractTableField<E> extends CustomField<Collection> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static final String EDIT_COLUMN_ID = "editColumn";

	protected static final String SCROLLBAR_FIX_CSS = "scrollbarFix";

	private int maxTablePageLength = 10;

	public int getMaxTablePageLength() {
		return maxTablePageLength;
	}

	public void setMaxTablePageLength(int maxTablePageLength) {
		this.maxTablePageLength = maxTablePageLength;
	}

	private VerticalLayout layout;
	private Label captionLabel;
	private Button addButton;
	private Table table;

	private Property<Collection<E>> dataSource;
	private BeanItemContainer<E> container;

	public AbstractTableField() {
		getContent();
	}

	/**
	 * If there are different rows in the table
	 * and do not need to be scrolled vertically (<= 10 entries)
	 * (and expandRatio is used?), A horizontal scroll bar appears.
	 * This can be corrected in CSS as follows:
	 * 
	 * <pre>
	 * .v-table-scrollbarFix .v-table-body-wrapper,
	 * .v-table-scrollbarFix .v-table-body-wrapper:focus {
	 * 		overflow-y: hidden;
	 * }
	 * </pre>
	 */
	protected void applyScrollbarFix() {

		if (table.getPageLength() == 0) {
			table.addStyleName(SCROLLBAR_FIX_CSS);
		} else {
			table.removeStyleName(SCROLLBAR_FIX_CSS);
		}
	}

	protected void applyTablePageLength() {

		if (getTable().size() <= getMaxTablePageLength()) {
			table.setPageLength(0);
		} else {
			table.setPageLength(getMaxTablePageLength());
		}
		applyScrollbarFix();
	}

	@Override
	protected Component initContent() {

		this.addStyleName(CssStyles.CAPTION_HIDDEN);
		this.addStyleName(CssStyles.VSPACE_2);

		layout = new VerticalLayout();
		layout.setSpacing(false);

		HorizontalLayout headerLayout = new HorizontalLayout();
		{
			headerLayout.setWidth(100, Unit.PERCENTAGE);

			captionLabel = new Label(getCaption());
			captionLabel.setSizeUndefined();
			headerLayout.addComponent(captionLabel);
			headerLayout.setComponentAlignment(captionLabel, Alignment.BOTTOM_LEFT);
			headerLayout.setExpandRatio(captionLabel, 0);

			addButton = createAddButton();
			headerLayout.addComponent(addButton);
			headerLayout.setComponentAlignment(addButton, Alignment.BOTTOM_RIGHT);
			headerLayout.setExpandRatio(addButton, 1);
		}
		layout.addComponent(headerLayout);

		table = createTable();
		table.addItemSetChangeListener(new ItemSetChangeListener() {

			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				applyTablePageLength();
			}
		});
		layout.addComponent(table);

		return layout;
	}

	@Override
	public void setCaption(String caption) {

		super.setCaption(caption);
		captionLabel.setValue(caption);
	}

	@Override
	public Class<Collection> getType() {
		return Collection.class;
	}

	public abstract Class<E> getEntryType();

	protected Table createTable() {

		final Table table = new Table();

		table.setEditable(false);
		table.setSelectable(false);
		table.setSizeFull();

		createEditColumn(table);

		return table;
	}

	protected void createEditColumn(Table table) {

		ColumnGenerator editColumnGenerator = new ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				return generateEditCell(itemId);
			}
		};
		table.addGeneratedColumn(EDIT_COLUMN_ID, editColumnGenerator);
		table.setColumnWidth(EDIT_COLUMN_ID, 20);
		table.setColumnHeader(EDIT_COLUMN_ID, "");

		table.addItemClickListener(new ItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick() || EDIT_COLUMN_ID.equals(event.getPropertyId())) {
					final E entry = (E) event.getItemId();
					if (entry != null) {
						editEntry(entry, false, result -> onEntryChanged(result));
					}
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected Object generateEditCell(Object itemId) {

		return ButtonHelper.createIconButtonWithCaption(itemId + "-edit", null, VaadinIcons.EDIT, e -> {
			editEntry((E) itemId, false, this::onEntryChanged);
		}, ValoTheme.BUTTON_BORDERLESS);
	}

	protected Button createAddButton() {
		return ButtonHelper.createButton(Captions.actionNewEntry, event -> addEntry(), ValoTheme.BUTTON_LINK);
	}

	/**
	 * @see #createEntry()
	 * @see #editEntry(Object)
	 */
	protected void addEntry() {

		final E entry = createEntry();

		editEntry(entry, true, new Consumer<E>() {

			@Override
			public void accept(E result) {
				table.addItem(result);
				fireValueChange(false);
			}
		});
	}

	protected abstract void editEntry(E entry, boolean create, Consumer<E> commitCallback);

	/**
	 * Override in order to do custom sort
	 */
	protected void onEntryChanged(E entry) {

		getTable().refreshRowCache();
		fireValueChange(false);
	}

	/**
	 * Set visible columns and their behavior. If entries are to be deleted, the "delete" column must be visible.
	 */
	protected abstract void updateColumns();

	protected E createEntry() {

		// TODO good way to do it like this?
		try {
			return getEntryType().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Copy of an entry. All editing is then done within this copy. At commit, this entry replaces the old one.
	 *
	 * @param sourceEntry
	 *            original
	 * @return
	 */
	protected E cloneEntry(E sourceEntry) {

		if (sourceEntry == null) {
			return null;
		}

		E clone = ObjectUtils.clone(sourceEntry);
		if (clone == null) {
			throw new CloneFailedException("Entry is not Cloneable");
		}
		return clone;
	}

	/**
	 * Delete entry (e.g., called by the user).
	 */
	public void removeEntry(E entry) {

		// gewünschten Eintrag löschen
		getTable().removeItem(entry);

		fireValueChange(false);
	}

	/**
	 * Check whether the entry is empty, so that such entries can be automatically removed from the list.
	 */
	protected abstract boolean isEmpty(E entry);

	/**
	 * Check whether the entry has changed in the past. Required for Field.isModified ().
	 */
	protected abstract boolean isModified(E oldEntry, E newEntry);

	/**
	 * Specifies the possibility to change the entry before it is commited.
	 *
	 * @return May the entry be commited?
	 */
	protected boolean preEntryCommit(E entry) {
		// don't commit empty entries
		return !isEmpty(entry);
	}

	/**
	 * @return Unchanged original entry from the data source
	 */
	protected E getUnmodifiedEntry(E entry) {
		Collection<E> oldEntries = dataSource.getValue();
		for (E oldEntry : oldEntries) {
			if (oldEntry.equals(entry)) {
				return oldEntry;
			}
		}
		return null;
	}

	/**
	 * Property serves as a source for the items in the table.
	 * A copy is created of all entries in the edit, so that the source data is not overwritten until the commit.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setPropertyDataSource(Property newDataSource) {

		if (newDataSource == dataSource || (newDataSource != null && newDataSource.equals(dataSource))) {
			return;
		}

		dataSource = newDataSource;

		Collection<E> entries = dataSource.getValue();
		if (entries == null) {
			throw new IllegalArgumentException("dataSource cannot be null");
		}

		Collection<E> clonedEntries;
		if (entries instanceof List) {
			clonedEntries = new ArrayList<>(entries.size());
		} else if (entries instanceof Set) {
			clonedEntries = new HashSet<>(entries.size());
		} else {
			throw new IllegalArgumentException("dataSource value must be List or Set: " + entries.getClass());
		}

		// Make a copy of all entries so that they can be freely edited
		// important: all fields must be placed on writeThrough!
		for (E entry : entries) {
			clonedEntries.add(cloneEntry(entry));
		}

		container = new BeanItemContainer<>(getEntryType(), clonedEntries);

		getContent();
		Table tbl = getTable();
		if (tbl.getContainerDataSource() != null) {
			// keep the visible Columns
			Object[] visibleColumns = tbl.getVisibleColumns();
			tbl.setContainerDataSource(container, Arrays.asList(visibleColumns));
		} else {
			tbl.setContainerDataSource(container);
		}
		applyTablePageLength();

		updateColumns();

		fireValueChange(false);

		// not set, we manage our own dataSource
		// super.setPropertyDataSource (newDataSource);
	}

	@Override
	public Property<Collection<E>> getPropertyDataSource() {
		return dataSource;
	}

	@Override
	public boolean isModified() {

		if (dataSource == null) {
			return false;
		}

		ArrayList<E> oldEntries = new ArrayList<>(dataSource.getValue());
		ArrayList<E> newEntries = new ArrayList<>(getValue());

		// go through "new entries": there is in each case an equal old?!
		Iterator<E> iterator = newEntries.iterator();
		int newEntriesCount = 0;
		while (iterator.hasNext()) {
			E newEntry = iterator.next();
			// ignore empty entries
			if (isEmpty(newEntry)) {
				continue;
			}
			// search for entry
			int oldIndex = oldEntries.indexOf(newEntry);
			if (oldIndex == -1) {
				// no entry found -> new -> modified
				return true;
			}
			E oldEntry = oldEntries.get(oldIndex);
			if (isModified(oldEntry, newEntry)) {
				// entry modified -> modified
				return true;
			}
			newEntriesCount++;
		}

		// less entries than before? -> modified
		if (newEntriesCount < oldEntries.size()) {
			return true;
		}

		return false;
	}

	/**
	 * Auxiliary method for comparing two objects. Uses equals for comparing.
	 */
	public static boolean isModifiedObject(Object oldObject, Object newObject) {

		if (oldObject == newObject) {
			return false;
		}
		if (oldObject == null || newObject == null) {
			return true;
		}
		if (!oldObject.equals(newObject)) {
			return true;
		}
		return false;
	}

	/**
	 * Auxiliary method for comparing two collections. Used for equals () to compare.
	 */
	public static boolean isModifiedCollection(Collection<?> oldCollection, Collection<?> newCollection) {

		if (oldCollection.size() != newCollection.size()) {
			return true;
		}

		// iterate through all entries in order
		Iterator<?> oldIterator = oldCollection.iterator();
		Iterator<?> newIterator = newCollection.iterator();

		while (newIterator.hasNext()) {
			Object oldObject = oldIterator.next();
			Object newObject = newIterator.next();

			// Eintrag anders?
			if (isModifiedObject(oldObject, newObject)) {
				return true;
			}
		}

		boolean hasNext = !oldIterator.hasNext();
		assert hasNext; // iteration should also be finished

		return false;
	}

	@Override
	public void commit() throws SourceException, InvalidValueException {

		// write new entries to data source
		if (dataSource != null && !dataSource.isReadOnly()) {
			if ((isInvalidCommitted() || isValid())) {

				// creata a copy
				Collection<E> entries = getValue();
				Collection<E> entriesCopy;
				if (dataSource.getValue() instanceof List) {
					entriesCopy = new ArrayList<>(entries);
				} else {
					// Set
					entriesCopy = new LinkedHashSet<>(entries);
				}

				// remove unwanted entries
				for (E entry : entries) {
					if (!preEntryCommit(entry)) {
						entriesCopy.remove(entry);
					}
				}

				// done
				dataSource.setValue(entriesCopy);

				fireValueChange(false);

			} else {
				/* An invalid value and we don't allow them, throw the exception */
				validate();
			}
		}

		// super.commit();
	}

	@Override
	public void discard() throws SourceException {

		// just reread the data source
		Property<Collection<E>> resetDataSource = dataSource;
		dataSource = null;
		setPropertyDataSource(resetDataSource);

		// super.discard();
	}

	@Override
	public Collection<E> getValue() {

		BeanItemContainer<E> container = getContainer();
		if (container == null) {
			return null;
		}

		// return all entries from the container
		Collection<E> entries = container.getItemIds();

		return entries;
	}

	@Override
	public boolean isEmpty() {
		Collection<E> entries = getValue();
		for (E entry : entries) {
			if (!isEmpty(entry)) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setValue(Collection newFieldValue, boolean repaintIsNotNeeded, boolean ignoreReadOnly)
		throws com.vaadin.v7.data.Property.ReadOnlyException, ConversionException, InvalidValueException {

		BeanItemContainer<E> container = getContainer();
		if (container == null) {
			return;
		}

		container.removeAllItems();
		container.addAll(newFieldValue);
		table.refreshRowCache();

		fireValueChange(repaintIsNotNeeded);
	}

	/**
	 * @since Vaadin 7.4
	 *        Workaround, because in AbstractField.clear () calls setValue (null).
	 */
	public void clear() {
		BeanItemContainer<E> container = getContainer();
		if (container != null) {
			container.removeAllItems();
		}
	}

	protected VerticalLayout getLayout() {
		return layout;
	}

	protected Table getTable() {
		return table;
	}

	protected Button getAddButton() {
		return addButton;
	}

	protected BeanItemContainer<E> getContainer() {
		return container;
	}

	protected void setLayoutSpacing(boolean value) {
		getLayout().setSpacing(value);
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		getAddButton().setVisible(!readOnly);
		if (readOnly) {
			getTable().removeContainerProperty(EDIT_COLUMN_ID);
		}
	}
}
