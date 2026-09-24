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

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.event.sevenoneseven.Event717AssessmentDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717EarlyResponseAction;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.MultilineLabel;

public class Event717EarlyResponseActionEditForm extends AbstractEditForm<Event717EarlyResponseActionEntry> {

	private static final long serialVersionUID = -4031682765541021539L;

	private static final String LOC_ACTION_TITLE = "actionTitle";

	private static final String HTML_LAYOUT = loc(LOC_ACTION_TITLE)
		+ fluidRowLocs(Event717EarlyResponseActionEntry.DATE, Event717EarlyResponseActionEntry.NOT_APPLICABLE)
		+ fluidRowLocs(Event717EarlyResponseActionEntry.NARRATIVE);

	private final Event717EarlyResponseAction action;
	private final boolean isEditAllowed;

	public Event717EarlyResponseActionEditForm(
		Event717EarlyResponseAction action,
		UiFieldAccessCheckers<Event717EarlyResponseActionEntry> fieldAccessCheckers,
		boolean isEditAllowed) {
		super(Event717EarlyResponseActionEntry.class, Event717AssessmentDto.I18N_PREFIX, false, null, fieldAccessCheckers, isEditAllowed);
		this.action = action;
		this.isEditAllowed = isEditAllowed;

		setWidth(540, Unit.PIXELS);
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {

		// the full title of the action, it is too long for the caption of the dialog
		MultilineLabel actionTitle = new MultilineLabel(action.toString());
		CssStyles.style(actionTitle, CssStyles.LABEL_BOLD, CssStyles.LABEL_LARGE_ALT, CssStyles.VSPACE_3);
		getContent().addComponent(actionTitle, LOC_ACTION_TITLE);

		DateField date = addField(Event717EarlyResponseActionEntry.DATE, DateField.class);
		date.setCaption(I18nProperties.getCaption(Captions.date));

		CheckBox notApplicable = addField(Event717EarlyResponseActionEntry.NOT_APPLICABLE, CheckBox.class);
		notApplicable.setCaption(
			I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, Event717AssessmentDto.INVESTIGATION_NOT_APPLICABLE));
		CssStyles.style(notApplicable, CssStyles.FORCE_CAPTION_CHECKBOX);

		TextArea narrative = addField(Event717EarlyResponseActionEntry.NARRATIVE, TextArea.class);
		narrative.setCaption(I18nProperties.getPrefixCaption(Event717AssessmentDto.I18N_PREFIX, "narrative"));
		narrative.setNullRepresentation("");
		narrative.setRows(4);

		notApplicable.addValueChangeListener(e -> {
			boolean isNotApplicable = Boolean.TRUE.equals(notApplicable.getValue());
			if (isNotApplicable) {
				date.setValue(null);
			}
			date.setEnabled(isEditAllowed && !isNotApplicable);
		});

		initializeAccessAndAllowedAccesses();
	}

	@Override
	public void setValue(Event717EarlyResponseActionEntry newFieldValue) {
		super.setValue(newFieldValue);
		getField(Event717EarlyResponseActionEntry.DATE).setEnabled(isEditAllowed && !Boolean.TRUE.equals(newFieldValue.getNotApplicable()));
	}
}
