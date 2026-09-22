/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.ui.events.sevenoneseven;

import java.util.function.Consumer;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Table;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.ui.caze.AbstractTableField;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Table of child entries of a 7-1-7 assessment that are edited in a popup form.
 */
@SuppressWarnings("serial")
public abstract class AbstractEvent717EntriesField<E extends EntityDto> extends AbstractTableField<E> {

	protected final boolean isEditAllowed;

	protected AbstractEvent717EntriesField(boolean isEditAllowed) {
		super(UiFieldAccessCheckers.getNoop(), isEditAllowed);
		this.isEditAllowed = isEditAllowed;
	}

	/**
	 * @return The form to edit the given entry in the popup.
	 */
	protected abstract AbstractEditForm<E> createEditForm(E entry, boolean create);

	/**
	 * @return The caption of the popup window and of the delete confirmation.
	 */
	protected abstract String getEntryCaption();

	/**
	 * @return The property the table is sorted by after changes, or null to keep the insertion order.
	 */
	protected abstract Object getSortPropertyId();

	@Override
	protected void editEntry(E entry, boolean create, Consumer<E> commitCallback) {

		if (create && entry.getUuid() == null) {
			entry.setUuid(DataHelper.createUuid());
		}

		AbstractEditForm<E> editForm = createEditForm(entry, create);
		editForm.setValue(entry);

		final CommitDiscardWrapperComponent<AbstractEditForm<E>> editView =
			new CommitDiscardWrapperComponent<>(editForm, isEditAllowed, editForm.getFieldGroup());
		editView.getCommitButton().setCaption(I18nProperties.getString(Strings.done));

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, getEntryCaption());

		if (isEditAllowed) {
			editView.addCommitListener(() -> {
				if (!editForm.getFieldGroup().isModified()) {
					commitCallback.accept(editForm.getValue());
				}
			});

			if (!create) {
				editView.addDeleteListener(() -> {
					popupWindow.close();
					removeEntry(entry);
				}, getEntryCaption());
			}
		} else {
			editView.getCommitButton().setVisible(false);
			editView.getDiscardButton().setVisible(false);
		}
	}

	@Override
	protected void addEntry() {

		final E entry = createEntry();
		editEntry(entry, true, result -> {
			getTable().addItem(result);
			sortEntries();
			fireValueChange(false);
		});
	}

	@Override
	protected void onEntryChanged(E entry) {
		sortEntries();
		super.onEntryChanged(entry);
	}

	@Override
	protected Component initContent() {

		Component content = super.initContent();
		// style the caption above the table like the sub headings of other forms
		getCaptionLabel().addStyleName(CssStyles.H4);
		return content;
	}

	@Override
	protected boolean isEmpty(E entry) {
		// entries have required fields, no empty entries possible
		return false;
	}

	@Override
	public void setPropertyDataSource(Property newDataSource) {
		super.setPropertyDataSource(newDataSource);
		getAddButton().setVisible(isEditAllowed);
		sortEntries();
	}

	protected void sortEntries() {

		if (getSortPropertyId() != null && getContainer() != null) {
			getContainer().sort(new Object[] {
				getSortPropertyId() }, new boolean[] {
					true });
		}
	}

	protected void setColumnHeaders(String i18nPrefix) {

		Table table = getTable();
		for (Object columnId : table.getVisibleColumns()) {
			if (columnId.equals(ACTION_COLUMN_ID)) {
				table.setColumnHeader(columnId, "&nbsp");
			} else {
				table.setColumnHeader(columnId, I18nProperties.getPrefixCaption(i18nPrefix, (String) columnId));
			}
		}
	}
}
