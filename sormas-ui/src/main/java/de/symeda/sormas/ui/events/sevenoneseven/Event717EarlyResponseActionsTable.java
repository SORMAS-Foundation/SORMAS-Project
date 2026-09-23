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

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.event.sevenoneseven.Event717AssessmentDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717EarlyResponseAction;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Lists the seven 7-1-7 early response actions. Each action is edited in a dialog. The values are read from and written to
 * the (not displayed) fields of the assessment form, so that committing, discarding and dirty detection are handled by the
 * form's field group.
 */
@SuppressWarnings("serial")
public class Event717EarlyResponseActionsTable extends VerticalLayout {

	private static final String EDIT_COLUMN = "edit";
	private static final String ACTION_COLUMN = "action";
	private static final String DATE_COLUMN = "date";
	private static final String APPLICABLE_COLUMN = "applicable";

	private static final String COLOR_APPLICABLE = "#43A047";
	private static final String COLOR_NOT_APPLICABLE = "#E7503C";
	private static final String NARRATIVE_COLUMN = "narrative";

	/**
	 * The bound fields of one early response action.
	 */
	public static class ActionFields {

		private final DateField date;
		private final CheckBox notApplicable;
		private final TextArea narrative;

		public ActionFields(DateField date, CheckBox notApplicable, TextArea narrative) {
			this.date = date;
			this.notApplicable = notApplicable;
			this.narrative = narrative;
		}

		boolean isNotApplicable() {
			return Boolean.TRUE.equals(notApplicable.getValue());
		}
	}

	private final Map<Event717EarlyResponseAction, ActionFields> actionFields;
	private final boolean isEditAllowed;
	private final Table table;

	public Event717EarlyResponseActionsTable(Map<Event717EarlyResponseAction, ActionFields> actionFields, boolean isEditAllowed) {

		this.actionFields = actionFields;
		this.isEditAllowed = isEditAllowed;

		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);
		addStyleName(CssStyles.VSPACE_3);

		table = new Table();
		table.setWidth(100, Unit.PERCENTAGE);
		table.setSelectable(false);
		table.setSortEnabled(false);

		table.addGeneratedColumn(EDIT_COLUMN, (Table.ColumnGenerator) (source, itemId, columnId) -> createEditButton((Event717EarlyResponseAction) itemId));
		// plain text cells wrap within the column width
		table.addGeneratedColumn(ACTION_COLUMN, (Table.ColumnGenerator) (source, itemId, columnId) -> itemId.toString());
		table.addGeneratedColumn(DATE_COLUMN, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			Date date = actionFields.get(itemId).date.getValue();
			return date != null ? DateFormatHelper.formatDate(date) : null;
		});
		table.addGeneratedColumn(APPLICABLE_COLUMN, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			boolean isApplicable = !actionFields.get(itemId).isNotApplicable();
			return new Label(
				"<span style=\"color:" + (isApplicable ? COLOR_APPLICABLE : COLOR_NOT_APPLICABLE) + ";\">"
					+ (isApplicable ? VaadinIcons.CHECK.getHtml() : VaadinIcons.CLOSE.getHtml()) + "</span>",
				ContentMode.HTML);
		});
		table.addGeneratedColumn(NARRATIVE_COLUMN, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			String narrative = actionFields.get(itemId).narrative.getValue();
			return StringUtils.isBlank(narrative) ? null : narrative;
		});

		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			table.addItem(action);
		}

		table.setVisibleColumns(EDIT_COLUMN, ACTION_COLUMN, DATE_COLUMN, APPLICABLE_COLUMN, NARRATIVE_COLUMN);
		table.setColumnHeader(EDIT_COLUMN, "&nbsp");
		table.setColumnHeader(ACTION_COLUMN, I18nProperties.getCaption(Captions.Action));
		table.setColumnHeader(DATE_COLUMN, I18nProperties.getCaption(Captions.date));
		table.setColumnHeader(APPLICABLE_COLUMN, I18nProperties.getCaption(Captions.Event717Assessment_applicable));
		table.setColumnHeader(NARRATIVE_COLUMN, I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, "narrative"));
		table.setColumnWidth(EDIT_COLUMN, 20);
		table.setColumnWidth(ACTION_COLUMN, 255);
		table.setColumnWidth(DATE_COLUMN, 70);
		table.setColumnWidth(APPLICABLE_COLUMN, 105);
		// the narrative takes the remaining width
		table.setColumnExpandRatio(NARRATIVE_COLUMN, 1);
		table.setColumnAlignment(APPLICABLE_COLUMN, Table.Align.CENTER);
		table.setPageLength(0);

		table.addItemClickListener(event -> {
			if (event.isDoubleClick()) {
				openEditDialog((Event717EarlyResponseAction) event.getItemId());
			}
		});

		// keep the table in sync with the bound fields, e.g. after discarding changes
		Property.ValueChangeListener refreshListener = e -> table.refreshRowCache();
		actionFields.values().forEach(fields -> {
			fields.date.addValueChangeListener(refreshListener);
			fields.notApplicable.addValueChangeListener(refreshListener);
			fields.narrative.addValueChangeListener(refreshListener);
		});

		addComponent(table);
	}

	private Button createEditButton(Event717EarlyResponseAction action) {

		return ButtonHelper.createIconButtonWithCaption(
			action.name() + "-edit",
			null,
			isEditAllowed ? VaadinIcons.EDIT : VaadinIcons.EYE,
			e -> openEditDialog(action),
			ValoTheme.BUTTON_BORDERLESS);
	}

	private void openEditDialog(Event717EarlyResponseAction action) {

		ActionFields fields = actionFields.get(action);

		Event717EarlyResponseActionEditForm editForm = new Event717EarlyResponseActionEditForm(action, isEditAllowed);
		editForm.setValue(new Event717EarlyResponseActionEntry(fields.date.getValue(), fields.isNotApplicable(), fields.narrative.getValue()));

		final CommitDiscardWrapperComponent<Event717EarlyResponseActionEditForm> editView =
			new CommitDiscardWrapperComponent<>(editForm, isEditAllowed, editForm.getFieldGroup());
		editView.getCommitButton().setCaption(I18nProperties.getString(Strings.done));

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getCaption(Captions.Event717EarlyResponseAction));

		if (isEditAllowed) {
			editView.addCommitListener(() -> {
				if (!editForm.getFieldGroup().isModified()) {
					Event717EarlyResponseActionEntry entry = editForm.getValue();
					boolean isNotApplicable = Boolean.TRUE.equals(entry.getNotApplicable());
					fields.notApplicable.setValue(isNotApplicable);
					fields.date.setValue(isNotApplicable ? null : entry.getDate());
					fields.narrative.setValue(StringUtils.isBlank(entry.getNarrative()) ? null : entry.getNarrative());
				}
			});
		} else {
			editView.getCommitButton().setVisible(false);
			editView.getDiscardButton().setVisible(false);
		}
	}
}
