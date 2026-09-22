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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
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
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FutureDateValidator;
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
	private static final String NOT_APPLICABLE_COLUMN = "notApplicable";
	private static final String NARRATIVE_COLUMN = "narrative";

	private static final int NARRATIVE_PREVIEW_LENGTH = 60;

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
		table.addGeneratedColumn(ACTION_COLUMN, (Table.ColumnGenerator) (source, itemId, columnId) -> itemId.toString());
		table.addGeneratedColumn(DATE_COLUMN, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			Date date = actionFields.get(itemId).date.getValue();
			return date != null ? DateFormatHelper.formatDate(date) : null;
		});
		table.addGeneratedColumn(NOT_APPLICABLE_COLUMN, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			if (actionFields.get(itemId).isNotApplicable()) {
				return new Label(VaadinIcons.CHECK.getHtml(), ContentMode.HTML);
			}
			return null;
		});
		table.addGeneratedColumn(NARRATIVE_COLUMN, (Table.ColumnGenerator) (source, itemId, columnId) -> {
			String narrative = actionFields.get(itemId).narrative.getValue();
			if (StringUtils.isBlank(narrative)) {
				return null;
			}
			Label label = new Label(StringUtils.abbreviate(narrative.split("\\R", 2)[0], NARRATIVE_PREVIEW_LENGTH));
			label.setDescription(narrative);
			return label;
		});

		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			table.addItem(action);
		}

		table.setVisibleColumns(EDIT_COLUMN, ACTION_COLUMN, DATE_COLUMN, NOT_APPLICABLE_COLUMN, NARRATIVE_COLUMN);
		table.setColumnHeader(EDIT_COLUMN, "&nbsp");
		table.setColumnHeader(ACTION_COLUMN, I18nProperties.getCaption(Captions.Action));
		table.setColumnHeader(DATE_COLUMN, I18nProperties.getCaption(Captions.date));
		table.setColumnHeader(
			NOT_APPLICABLE_COLUMN,
			I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, Event717AssessmentDto.INVESTIGATION_NOT_APPLICABLE));
		table.setColumnHeader(NARRATIVE_COLUMN, I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, "narrative"));
		table.setColumnWidth(EDIT_COLUMN, 20);
		table.setColumnExpandRatio(ACTION_COLUMN, 2);
		table.setColumnExpandRatio(NARRATIVE_COLUMN, 1);
		table.setColumnAlignment(NOT_APPLICABLE_COLUMN, Table.Align.CENTER);
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

		DateField date = new DateField(I18nProperties.getCaption(Captions.date));
		date.setDateFormat(DateFormatHelper.getDateFormatPattern());
		date.setLenient(true);
		date.setValue(fields.date.getValue());
		date.addValidator(new FutureDateValidator(date, 0, date.getCaption()));

		CheckBox notApplicable = new CheckBox(
			I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, Event717AssessmentDto.INVESTIGATION_NOT_APPLICABLE));
		notApplicable.setValue(fields.isNotApplicable());
		CssStyles.style(notApplicable, CssStyles.FORCE_CAPTION_CHECKBOX);

		TextArea narrative = new TextArea(I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, "narrative"));
		narrative.setWidth(100, Unit.PERCENTAGE);
		narrative.setRows(5);
		narrative.setNullRepresentation("");
		narrative.setValue(fields.narrative.getValue());

		date.setEnabled(isEditAllowed && !notApplicable.getValue());
		notApplicable.setEnabled(isEditAllowed);
		narrative.setEnabled(isEditAllowed);
		notApplicable.addValueChangeListener(e -> {
			boolean isNotApplicable = Boolean.TRUE.equals(notApplicable.getValue());
			if (isNotApplicable) {
				date.setValue(null);
			}
			date.setEnabled(isEditAllowed && !isNotApplicable);
		});

		HorizontalLayout dateRow = new HorizontalLayout(date, notApplicable);
		dateRow.setComponentAlignment(notApplicable, Alignment.BOTTOM_LEFT);

		VerticalLayout content = new VerticalLayout(dateRow, narrative);
		content.setWidth(540, Unit.PIXELS);

		Window window = VaadinUiUtil.showPopupWindow(content, action.toString());

		HorizontalLayout buttons = new HorizontalLayout();
		Button cancel = ButtonHelper.createButton(Captions.actionCancel, e -> window.close());
		buttons.addComponent(cancel);
		if (isEditAllowed) {
			Button done = ButtonHelper.createButton(Captions.actionDone, e -> {
				if (!date.isValid()) {
					date.setValidationVisible(true);
					return;
				}
				boolean isNotApplicable = Boolean.TRUE.equals(notApplicable.getValue());
				fields.notApplicable.setValue(isNotApplicable);
				fields.date.setValue(isNotApplicable ? null : date.getValue());
				fields.narrative.setValue(StringUtils.isBlank(narrative.getValue()) ? null : narrative.getValue());
				window.close();
			}, ValoTheme.BUTTON_PRIMARY);
			buttons.addComponent(done);
		} else {
			cancel.setCaption(I18nProperties.getCaption(Captions.actionClose));
		}
		content.addComponent(buttons);
		content.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
	}
}
