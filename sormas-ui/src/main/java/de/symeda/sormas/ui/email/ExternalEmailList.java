/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.email;

import static de.symeda.sormas.ui.utils.CssStyles.LABEL_BOLD;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogCriteria;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogIndexDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.PaginationList;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

public class ExternalEmailList extends PaginationList<ManualMessageLogIndexDto> {

	private static final int MAX_DISPLAYED_ENTRIES = 5;

	private ManualMessageLogCriteria criteria;
	private Label placeHolderLabel;

	public ExternalEmailList(ManualMessageLogCriteria criteria, String placeholderKey) {
		super(MAX_DISPLAYED_ENTRIES);

		this.criteria = criteria;
		this.placeHolderLabel = new Label(I18nProperties.getString(placeholderKey));
	}

	@Override
	public void reload() {
		List<ManualMessageLogIndexDto> emails = FacadeProvider.getManualMessageLogFacade().getIndexList(criteria);

		setEntries(emails);
		if (!emails.isEmpty()) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			listLayout.addComponent(placeHolderLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		for (ManualMessageLogIndexDto messageLog : getDisplayedEntries()) {
			ListEntry listEntry = new ListEntry(messageLog);

			listEntry.addViewButton("info-email" + messageLog.getUuid(), (Button.ClickListener) event -> {
				showEmailDetailsPopup(messageLog);
			}, VaadinIcons.INFO_CIRCLE);
			listEntry.setEnabled(false);

			listLayout.addComponent(listEntry);
		}
	}

	private static class ListEntry extends SideComponentField {

		public ListEntry(ManualMessageLogIndexDto messageLog) {
			VerticalLayout layout = new VerticalLayout();
			layout.setWidth(100, Unit.PERCENTAGE);
			layout.setMargin(false);
			layout.setSpacing(false);

			layout.addComponent(new Label(DateFormatHelper.formatLocalDateTime(messageLog.getSentDate())));
			layout.addComponent(buildTemplateInfo(messageLog));
			layout.addComponent(buildSendingUserInfo(messageLog.getSendingUser()));

			addComponentToField(layout);
		}

		@NotNull
		private static HorizontalLayout buildTemplateInfo(ManualMessageLogIndexDto messageLog) {
			Label caption = new Label(I18nProperties.getCaption(Captions.externalEmailUsedTemplate));
			return new HorizontalLayout(caption, new Label(messageLog.getUsedTemplate()));
		}

		@NotNull
		private static HorizontalLayout buildSendingUserInfo(UserReferenceDto sendingUser) {
			Label captionLabel = new Label(I18nProperties.getCaption(Captions.externalEmailSentBy) + ": ");
			captionLabel.addStyleNames(CssStyles.LABEL_BOLD);

			final Label sendingUserLabel;
			if (sendingUser != null) {
				sendingUserLabel = new Label(sendingUser.getShortCaption());
			} else {
				sendingUserLabel = new Label(I18nProperties.getCaption(Captions.inaccessibleValue));
				sendingUserLabel.addStyleNames(CssStyles.INACCESSIBLE_LABEL);
			}

			return new HorizontalLayout(captionLabel, sendingUserLabel);
		}
	}

	private static void showEmailDetailsPopup(ManualMessageLogIndexDto messageLog) {
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setMargin(false);
		verticalLayout.setSpacing(false);

		Label sentToCaptionLabel = new Label(I18nProperties.getCaption(Captions.externalEmailSentTo));
		sentToCaptionLabel.addStyleName(LABEL_BOLD);
		verticalLayout.addComponent(new HorizontalLayout(sentToCaptionLabel, new Label(messageLog.getEmailAddress())));

		Label attachmentsCaptionLabel = new Label(I18nProperties.getCaption(Captions.externalEmailAttachedDocuments));
		sentToCaptionLabel.addStyleName(LABEL_BOLD);
		verticalLayout.addComponent(new HorizontalLayout(attachmentsCaptionLabel, new Label(String.join(", ", messageLog.getAttachedDocuments()))));

		VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingExteranlEmailDetails),
				verticalLayout,
				I18nProperties.getString(Strings.close),
				null,
				640,
				confirmed -> true);
	}
}
