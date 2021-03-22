/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.externalsurveillanceservice;

import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;

import java.util.List;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.share.ExternalShareInfoCriteria;
import de.symeda.sormas.api.share.ExternalShareInfoDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.PaginationList;

public class ExternalShareInfoList extends PaginationList<ExternalShareInfoDto> {

	private final ExternalShareInfoCriteria criteria;
	private final Label placeholderLabel;
	private Language userLanguage;

	public ExternalShareInfoList(ExternalShareInfoCriteria criteria, boolean showPlaceholder, String placeholderCaptionTag) {
		super(5);

		this.criteria = criteria;

		this.placeholderLabel = new Label(placeholderCaptionTag != null ? I18nProperties.getCaption(placeholderCaptionTag) : null);
		this.placeholderLabel.setVisible(showPlaceholder);

		userLanguage = FacadeProvider.getUserFacade().getCurrentUser().getLanguage();
	}

	@Override
	public void reload() {
		List<ExternalShareInfoDto> shareInfos = FacadeProvider.getExternalShareInfoFacade().getIndexList(criteria, 0, maxDisplayedEntries * 20);

		setEntries(shareInfos);
		if (shareInfos.isEmpty()) {
			updatePaginationLayout();
			listLayout.addComponent(placeholderLabel);
		} else {
			listLayout.removeComponent(placeholderLabel);
			showPage(1);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		List<ExternalShareInfoDto> displayedEntries = getDisplayedEntries();

		for (ExternalShareInfoDto shareInfo : displayedEntries) {
			ExternalShareInfoListEntry listEntry = new ExternalShareInfoListEntry(shareInfo, userLanguage);
			listLayout.addComponent(listEntry);
		}
	}

	private static class ExternalShareInfoListEntry extends HorizontalLayout {

		private static final long serialVersionUID = 235034377845417079L;

		private static final String SENDER_LOC = "user";
		private static final String TIMESTAMP_LOC = "timestamp";

		public ExternalShareInfoListEntry(ExternalShareInfoDto shareInfo, Language userLanguage) {
			setMargin(false);
			setSpacing(true);
			setWidth(100, Unit.PERCENTAGE);
			addStyleName(CssStyles.SORMAS_LIST_ENTRY);

			CustomLayout infoLayout = new CustomLayout();
			infoLayout.setTemplateContents(buildLayout());
			infoLayout.setWidth(100, Unit.PERCENTAGE);

			Label senderLabel = new Label(shareInfo.getSender().getShortCaption());
			senderLabel.addStyleName(CssStyles.LABEL_BOLD);
			infoLayout.addComponent(senderLabel, SENDER_LOC);

			Label timestampLabel = new Label(DateHelper.formatLocalDateTime(shareInfo.getCreationDate(), userLanguage));
			timestampLabel.addStyleName(CssStyles.LABEL_BOLD);
			infoLayout.addComponent(timestampLabel, TIMESTAMP_LOC);

			addComponent(infoLayout);
			setExpandRatio(infoLayout, 1);
		}

		private String buildLayout() {
			return locCss(null, SENDER_LOC, "span") + " " + I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_sharedAt) + " "
				+ locCss(null, TIMESTAMP_LOC, "span");
		}

	}
}
