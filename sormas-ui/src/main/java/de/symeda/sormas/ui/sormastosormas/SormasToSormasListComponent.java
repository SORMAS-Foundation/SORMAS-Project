/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.sormastosormas;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sormastosormas.ServerAccessDataReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.PaginationList;

public class SormasToSormasListComponent extends VerticalLayout {

	private static final long serialVersionUID = -7189942121987530912L;

	private SormasToSormasList sormasToSormasList;

	public SormasToSormasListComponent(CaseDataDto caze, boolean canShare) {
		CaseReferenceDto caseRef = caze.toReference();

		sormasToSormasList = new SormasToSormasList(
			new SormasToSormasShareInfoCriteria().caze(caseRef),
			caze.getSormasToSormasOriginInfo() == null,
			Captions.sormasToSormasCaseNotShared);

		initLayout(
			caze.getSormasToSormasOriginInfo(),
			sormasToSormasList,
			canShare ? e -> ControllerProvider.getSormasToSormasController().shareCaseToSormas(caseRef, this) : null);
	}

	public SormasToSormasListComponent(ContactDto contact, boolean canShare) {
		ContactReferenceDto contactRef = contact.toReference();

		sormasToSormasList = new SormasToSormasList(
			new SormasToSormasShareInfoCriteria().contact(contactRef),
			contact.getSormasToSormasOriginInfo() == null,
			Captions.sormasToSormasCaseNotShared);

		initLayout(
			contact.getSormasToSormasOriginInfo(),
			sormasToSormasList,
			canShare ? e -> ControllerProvider.getSormasToSormasController().shareContactToSormas(contactRef, this) : null);
	}

	private void initLayout(
		SormasToSormasOriginInfoDto originInfo,
		SormasToSormasList sormasToSormasList,
		Button.ClickListener shareButtonClickListener) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		if (originInfo != null) {
			addComponent(buildSormasOriginInfo(originInfo));
		}

		addComponent(sormasToSormasList);
		sormasToSormasList.reload();

		Label header = new Label(I18nProperties.getCaption(Captions.sormasToSormasListTitle));
		header.addStyleName(CssStyles.H3);
		componentHeader.addComponent(header);

		if (shareButtonClickListener != null) {
			Button shareButtonButton =
				ButtonHelper.createIconButton(Captions.sormasToSormasShare, VaadinIcons.SHARE, shareButtonClickListener, ValoTheme.BUTTON_PRIMARY);

			componentHeader.addComponent(shareButtonButton);
			componentHeader.setComponentAlignment(shareButtonButton, Alignment.MIDDLE_RIGHT);
		}
	}

	public void reloadList() {
		sormasToSormasList.reload();
	}

	private static class SormasToSormasList extends PaginationList<SormasToSormasShareInfoDto> {

		private static final long serialVersionUID = -4659924105492791566L;

		private final SormasToSormasShareInfoCriteria criteria;
		private final Label placeholderLabel;

		public SormasToSormasList(SormasToSormasShareInfoCriteria criteria, boolean showPlaceholder, String placeholderCaptionTag) {
			super(5);

			this.criteria = criteria;

			this.placeholderLabel = new Label(placeholderCaptionTag != null ? I18nProperties.getCaption(placeholderCaptionTag) : null);
			this.placeholderLabel.setVisible(showPlaceholder);
		}

		@Override
		public void reload() {
			List<SormasToSormasShareInfoDto> shareInfos =
				FacadeProvider.getSormasToSormasFacade().getShareInfoIndexList(criteria, 0, maxDisplayedEntries * 20);

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
			List<SormasToSormasShareInfoDto> displayedEntries = getDisplayedEntries();

			for (SormasToSormasShareInfoDto shareInfo : displayedEntries) {
				SormasToSormasShareListEntry listEntry = new SormasToSormasShareListEntry(shareInfo);
				listLayout.addComponent(listEntry);
			}
		}
	}

	private static class SormasToSormasShareListEntry extends HorizontalLayout {

		private static final long serialVersionUID = 7462585357530141263L;

		public SormasToSormasShareListEntry(SormasToSormasShareInfoDto shareInfo) {
			setMargin(false);
			setSpacing(true);
			setWidth(100, Unit.PERCENTAGE);
			addStyleName(CssStyles.SORMAS_LIST_ENTRY);

			VerticalLayout layout = new VerticalLayout();
			layout.setWidth(100, Unit.PERCENTAGE);
			layout.setMargin(false);
			layout.setSpacing(false);
			addComponent(layout);
			setExpandRatio(layout, 1);

			Label targetLabel = new Label(I18nProperties.getCaption(Captions.sormasToSormasSharedWith) + " " + shareInfo.getTarget());
			targetLabel.addStyleName(CssStyles.LABEL_BOLD);
			layout.addComponent(targetLabel);

			Label senderLabel = new Label(I18nProperties.getCaption(Captions.sormasToSormasSharedBy) + ": " + shareInfo.getSender().getCaption());
			layout.addComponent(senderLabel);

			Label shareDateLabel = new Label(
				I18nProperties.getCaption(Captions.sormasToSormasSharedDate) + ": " + DateFormatHelper.formatDate(shareInfo.getCreationDate()));
			layout.addComponent(shareDateLabel);

			if (!DataHelper.isNullOrEmpty(shareInfo.getComment())) {
				layout.addComponent(new Label(shareInfo.getComment()));
			}
		}
	}

	private VerticalLayout buildSormasOriginInfo(SormasToSormasOriginInfoDto originInfo) {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.setSpacing(false);
		layout.setStyleName(CssStyles.VSPACE_3);

		ServerAccessDataReferenceDto serverAccessDataRef =
			FacadeProvider.getSormasToSormasFacade().getOrganizationRef(originInfo.getOrganizationId());

		Label senderOrganizationLabel = new Label(I18nProperties.getCaption(Captions.sormasToSormasSentFrom) + " " + serverAccessDataRef);
		senderOrganizationLabel.addStyleName(CssStyles.LABEL_BOLD);
		layout.addComponent(senderOrganizationLabel);
		layout.addComponent(new Label(I18nProperties.getCaption(Captions.sormasToSormasSharedBy) + ": " + originInfo.getSenderName()));

		if (originInfo.getSenderEmail() != null) {
			layout.addComponent(new Label(originInfo.getSenderEmail()));
		}

		if (originInfo.getSenderPhoneNumber() != null) {
			layout.addComponent(new Label(originInfo.getSenderPhoneNumber()));
		}

		Label shareDateLabel = new Label(
			I18nProperties.getCaption(Captions.sormasToSormasSharedDate) + ": " + DateFormatHelper.formatDate(originInfo.getCreationDate()));
		layout.addComponent(shareDateLabel);

		if (!DataHelper.isNullOrEmpty(originInfo.getComment())) {
			layout.addComponent(new Label(originInfo.getComment()));
		}

		return layout;
	}
}
