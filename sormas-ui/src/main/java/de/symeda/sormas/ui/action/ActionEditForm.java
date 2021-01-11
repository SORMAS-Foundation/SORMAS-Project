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
package de.symeda.sormas.ui.action;

import static de.symeda.sormas.ui.utils.CssStyles.SOFT_REQUIRED;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.RichTextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.action.ActionContext;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class ActionEditForm extends AbstractEditForm<ActionDto> {

	private static final long serialVersionUID = -6759724916847528789L;

	private static final String CREATING_LABEL_LOC = "creatingLabelLoc";
	private static final String LAST_MODIFIED_BY_LABEL_LOC = "lastModifiedByLabelLoc";
	private static final String STATUS_CHANGE_LABEL_LOC = "statusChangeLabelLoc";

	//@formatter:off
	private static final String HTML_LAYOUT =
			fluidRow(
					loc(ActionDto.ACTION_CONTEXT),
					locs(ActionDto.EVENT)) +
			fluidRowLocs(ActionDto.DATE, ActionDto.PRIORITY) +
			loc(CREATING_LABEL_LOC) +
			fluidRowLocs(ActionDto.TITLE) +
			fluidRowLocs(ActionDto.DESCRIPTION) +
			loc(LAST_MODIFIED_BY_LABEL_LOC) +
			fluidRowLocs(ActionDto.REPLY) +
			fluidRowLocs(4, ActionDto.ACTION_STATUS, 8, STATUS_CHANGE_LABEL_LOC);
	//@formatter:on

	public ActionEditForm(boolean create) {

		super(ActionDto.class, ActionDto.I18N_PREFIX);
		addValueChangeListener(e -> {
			updateByActionContext();
			updateByCreating();
			updateLastModifiedByInfo();
			updateStatusChangeInfo();
		});

		setWidth(680, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	@Override
	protected void addFields() {

		addField(ActionDto.EVENT, ComboBox.class);
		DateTimeField date = addDateField(ActionDto.DATE, DateTimeField.class, -1);
		date.setImmediate(true);
		addField(ActionDto.PRIORITY, ComboBox.class);
		addField(ActionDto.ACTION_STATUS, OptionGroup.class);
		NullableOptionGroup actionContext = addField(ActionDto.ACTION_CONTEXT, NullableOptionGroup.class);
		actionContext.setImmediate(true);
		actionContext.addValueChangeListener(event -> updateByActionContext());
		// XXX: set visible when other contexts will be managed
		actionContext.setVisible(false);

		TextField title = addField(ActionDto.TITLE, TextField.class);
		title.addStyleName(SOFT_REQUIRED);
		RichTextArea description = addField(ActionDto.DESCRIPTION, RichTextArea.class);
		description.setNullRepresentation("");
		description.setImmediate(true);
		RichTextArea reply = addField(ActionDto.REPLY, RichTextArea.class);
		reply.setNullRepresentation("");
		reply.setImmediate(true);

		setRequired(true, ActionDto.ACTION_CONTEXT, ActionDto.DATE, ActionDto.ACTION_STATUS);
		setReadOnly(true, ActionDto.ACTION_CONTEXT, ActionDto.EVENT);
	}

	private void updateLastModifiedByInfo() {
		if (getValue().getLastModifiedBy() != null && getValue().getChangeDate() != null) {
			Label replyLabel = new Label(
				String.format(
					I18nProperties.getCaption(Captions.actionLastModifiedByLabel),
					DateFormatHelper.formatDate(getValue().getChangeDate()),
					getValue().getLastModifiedBy().getCaption()));
			replyLabel.addStyleNames(CssStyles.LABEL_ITALIC);
			getContent().addComponent(replyLabel, LAST_MODIFIED_BY_LABEL_LOC);
		}
	}

	private void updateStatusChangeInfo() {
		if (getValue().getStatusChangeDate() != null) {
			Label statusChangeLabel = new Label(
				String.format(
					I18nProperties.getCaption(Captions.actionStatusChangeDate),
					DateFormatHelper.formatDate(getValue().getStatusChangeDate())));
			statusChangeLabel.addStyleNames(CssStyles.LABEL_ITALIC);
			getContent().addComponent(statusChangeLabel, STATUS_CHANGE_LABEL_LOC);
		}
	}

	private void updateCreationInfo() {
		Label creationLabel = new Label(
			String.format(
				I18nProperties.getCaption(Captions.actionCreatingLabel),
				DateFormatHelper.formatDate(getValue().getCreationDate()),
				getValue().getCreatorUser().getCaption()));
		creationLabel.addStyleNames(CssStyles.LABEL_ITALIC);
		getContent().addComponent(creationLabel, CREATING_LABEL_LOC);
	}

	private void updateByCreating() {

		ActionDto value = getValue();
		if (value != null) {
			boolean creating = value.getCreationDate() == null;

			UserDto user = UserProvider.getCurrent().getUser();
			boolean creator = user.equals(value.getCreatorUser());

			setVisible(!creating, ActionDto.REPLY);
			if (creating) {
				discard(ActionDto.REPLY);
			} else {
				updateCreationInfo();
			}

			setReadOnly(!creator, ActionDto.DESCRIPTION, ActionDto.TITLE);
		}
	}

	private void updateByActionContext() {
		Object fieldValueActionContext = getFieldGroup().getField(ActionDto.ACTION_CONTEXT).getValue();
		ActionContext actionContext = fieldValueActionContext == null ? null : (ActionContext) fieldValueActionContext;

		// context reference depending on action context
		// ready for adding new context
		ComboBox eventField = (ComboBox) getFieldGroup().getField(ActionDto.EVENT);
		if (actionContext != null) {
			if (actionContext == ActionContext.EVENT) {
				FieldHelper.setFirstVisibleClearOthers(eventField);
				FieldHelper.setFirstRequired(eventField);
			}
		} else {
			FieldHelper.setFirstVisibleClearOthers(null, eventField);
			FieldHelper.setFirstRequired(null, eventField);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
