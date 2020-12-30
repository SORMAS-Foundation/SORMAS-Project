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

import static de.symeda.sormas.api.utils.HtmlHelper.cleanHtml;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.action.ActionPriority;
import de.symeda.sormas.api.action.ActionStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.HtmlHelper;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

@SuppressWarnings("serial")
public class ActionListEntry extends HorizontalLayout {

	private final ActionDto action;
	private Button editButton;

	public ActionListEntry(ActionDto action) {

		this.action = action;

		setMargin(false);
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);

		VerticalLayout withContentLayout = new VerticalLayout();
		withContentLayout.setMargin(false);
		withContentLayout.setSpacing(false);
		withContentLayout.setWidth(100, Unit.PERCENTAGE);
		addComponent(withContentLayout);
		setExpandRatio(withContentLayout, 1);

		Label title = new Label(MoreObjects.firstNonNull(Strings.emptyToNull(action.getTitle()), "-"));
		title.addStyleName(CssStyles.H3);
		withContentLayout.addComponent(title);

		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setMargin(false);
		topLayout.setSpacing(false);
		topLayout.setWidth(100, Unit.PERCENTAGE);
		withContentLayout.addComponent(topLayout);

		VerticalLayout descReplyLayout = new VerticalLayout();
		descReplyLayout.setMargin(false);
		descReplyLayout.setSpacing(false);
		descReplyLayout.setWidth(100, Unit.PERCENTAGE);
		descReplyLayout.addStyleName(CssStyles.RICH_TEXT_CONTENT_CONTAINER);
		withContentLayout.addComponents(descReplyLayout);

		Label description = new Label(cleanHtml(action.getDescription(), HtmlHelper.EVENTACTION_WHITELIST), ContentMode.HTML);
		description.setWidth(100, Unit.PERCENTAGE);
		descReplyLayout.addComponent(description);
		if (!Strings.isNullOrEmpty(action.getReply())) {
			Label replyLabel = new Label(cleanHtml(action.getReply(), HtmlHelper.EVENTACTION_WHITELIST), ContentMode.HTML);
			replyLabel.setWidth(100, Unit.PERCENTAGE);
			replyLabel.addStyleName(CssStyles.REPLY);
			descReplyLayout.addComponent(replyLabel);
		}

		// TOP LEFT
		VerticalLayout topLeftLayout = new VerticalLayout();

		topLeftLayout.setMargin(false);
		topLeftLayout.setSpacing(false);

		Label dateLabel =
			new Label(I18nProperties.getPrefixCaption(ActionDto.I18N_PREFIX, ActionDto.DATE) + ": " + DateFormatHelper.formatDate(action.getDate()));
		topLeftLayout.addComponent(dateLabel);

		Label creatorLabel = new Label(
			String.format(
				I18nProperties.getCaption(Captions.actionCreatingLabel),
				DateFormatHelper.formatDate(action.getCreationDate()),
				action.getCreatorUser().getCaption()));
		creatorLabel.addStyleName(CssStyles.LABEL_ITALIC);
		topLeftLayout.addComponent(creatorLabel);

		Label replyingUserLabel = null;
		if (action.getReplyingUser() != null) {
			replyingUserLabel = new Label(
				String.format(
					I18nProperties.getCaption(Captions.actionReplyingLabel),
					DateFormatHelper.formatDate(action.getChangeDate()),
					action.getReplyingUser().getCaption()));
			replyingUserLabel.addStyleName(CssStyles.LABEL_ITALIC);
			topLeftLayout.addComponent(replyingUserLabel);
		}

		topLayout.addComponent(topLeftLayout);

		// TOP RIGHT
		VerticalLayout topRightLayout = new VerticalLayout();

		topRightLayout.addStyleName(CssStyles.ALIGN_RIGHT);
		topRightLayout.setMargin(false);
		topRightLayout.setSpacing(false);

		HorizontalLayout statusContainer = new HorizontalLayout();
		Label statusLabel = new Label(
			I18nProperties.getPrefixCaption(ActionDto.I18N_PREFIX, ActionDto.ACTION_STATUS) + ": "
				+ DataHelper.toStringNullable(action.getActionStatus()));
		CssStyles.style(statusLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		statusContainer.addComponent(statusLabel);
		Label statusChangeLabel = null;
		if (action.getStatusChangeDate() != null) {
			statusChangeLabel = new Label(
				String.format(I18nProperties.getCaption(Captions.actionStatusChangeDate), DateFormatHelper.formatDate(action.getStatusChangeDate())));
			statusChangeLabel.addStyleName(CssStyles.LABEL_ITALIC);
			statusContainer.addComponent(statusChangeLabel);
		}

		topRightLayout.addComponent(statusContainer);

		Label priorityLabel = new Label(
			DataHelper.toStringNullable(I18nProperties.getPrefixCaption(ActionDto.I18N_PREFIX, ActionDto.PRIORITY) + ": " + action.getPriority()));
		if (ActionPriority.HIGH == action.getPriority()) {
			priorityLabel.addStyleName(CssStyles.LABEL_IMPORTANT);
		} else if (ActionPriority.NORMAL == action.getPriority()) {
			priorityLabel.addStyleName(CssStyles.LABEL_NEUTRAL);
		}
		topRightLayout.addComponent(priorityLabel);

		topLayout.addComponent(topRightLayout);
		topLayout.setComponentAlignment(topRightLayout, Alignment.TOP_RIGHT);

		String statusStyle = action.getActionStatus() == ActionStatus.DONE ? CssStyles.LABEL_DONE : null;
		if (statusStyle != null) {
			statusLabel.addStyleName(statusStyle);
			dateLabel.addStyleName(statusStyle);
			if (statusChangeLabel != null) {
				statusChangeLabel.addStyleName(statusStyle);
			}
			creatorLabel.addStyleName(statusStyle);
			if (replyingUserLabel != null) {
				replyingUserLabel.addStyleName(statusStyle);
			}
			priorityLabel.addStyleName(statusStyle);
		}
	}

	public void addEditListener(int rowIndex, Button.ClickListener editClickListener) {
		if (editButton == null) {
			editButton = ButtonHelper.createIconButtonWithCaption(
				"edit-action-" + rowIndex,
				null,
				VaadinIcons.PENCIL,
				null,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT,
				CssStyles.LABEL_VERTICAL_ALIGN_TOP);

			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(editButton, 0);
		}

		editButton.addClickListener(editClickListener);
	}

	public ActionDto getAction() {
		return action;
	}
}
